import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Device {
    private final byte[] mac;
    private final int frameSize;
    private Socket socket;
    private OutputStream out;
    private final Map<Integer, FrameDTO> window = new HashMap<>();
    private int base = 0;
    private int nextSeqNum = 0;
    private int expectedSeqNum = 0;
    private String deviceName;

    private byte[] broadcastMac = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};

    private final Map<String, List<Byte>> receivedBuffers = new HashMap<>();

    private Timer retransmissionTimer;
    private TimerTask retransmissionTask;

    public static final int MAX_FRAME_BUFFER_SIZE = 1518;

    public Device(byte[] mac, int frameSize, String deviceName) {
        this.mac = mac;
        this.frameSize = frameSize;
        this.deviceName = deviceName;
        createDeviceFolders();
    }

    private String macToString(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
            if (i < mac.length - 1) sb.append("-");
        }
        return sb.toString();
    }

    private void createDeviceFolders() {
        File baseDir = new File("devices/" + deviceName);
        File sendDir = new File(baseDir, "send");
        File receivedDir = new File(baseDir, "received");

        if (!sendDir.exists()) {
            sendDir.mkdirs();
            System.out.println("Diretório criado: " + sendDir.getAbsolutePath());
        }
        if (!receivedDir.exists()) {
            receivedDir.mkdirs();
            System.out.println("Diretório criado: " + receivedDir.getAbsolutePath());
        }
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = socket.getOutputStream();

        new Thread(() -> {
            try (InputStream in = socket.getInputStream()) {
                byte[] buffer = new byte[MAX_FRAME_BUFFER_SIZE];
                while (true) {
                    int read = in.read(buffer);
                    if (read == -1) break;
                    byte[] data = Arrays.copyOf(buffer, read);
                    FrameDTO frame = FrameOperations.deserialize(data);
                    if (frame != null) {
                        handleReceivedFrame(frame);
                    } else {
                        System.err.println(deviceName + " - Quadro corrompido ou inválido recebido.");
                    }
                }
            } catch (IOException e) {
                System.out.println(deviceName + " - Conexão encerrada: " + e.getMessage());
            }
        }).start();
    }

    public void send(String message, byte[] destMac) throws IOException {
        List<byte[]> fragments = fragment(message);
        boolean isBroadcast = Arrays.equals(destMac, broadcastMac);

        int janelaTamanho = 4;
        for (byte[] frag : fragments) {
            if (!isBroadcast) {
                while (nextSeqNum >= base + janelaTamanho) {
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }
            }

            FrameDTO frame = new FrameDTO();
            frame.setMacSource(mac);
            frame.setMacDestination(destMac);
            frame.setEtherType(new byte[]{0x08, 0x00});
            frame.setFrameType((byte) 0);

            frame.setPayload(addSeqToPayload(frag, nextSeqNum));
            frame.setFrameCheckSequence(CalculateCfc.getCRCBytes(FrameOperations.getFrameToSendToCRC(frame)));

            if (!isBroadcast) {
                window.put(nextSeqNum, frame);
            }

            byte[] serialized = FrameOperations.serialize(frame);
            out.write(serialized);

            if (!isBroadcast) {
                if (base == nextSeqNum) {
                    startTimerForBase();
                }
            }
            nextSeqNum++;
        }
        if (isBroadcast) {
            base = 0;
            nextSeqNum = 0;
            stopTimerForBase();
        }
    }

    private byte[] addSeqToPayload(byte[] data, int seq) {
        byte[] result = new byte[data.length + 1];
        result[0] = (byte) seq;
        System.arraycopy(data, 0, result, 1, data.length);
        return result;
    }

    private int extractSeq(byte[] payload) {
        return payload[0];
    }

    private byte[] extractData(byte[] payload) {
        return Arrays.copyOfRange(payload, 1, payload.length);
    }

    private void handleReceivedFrame(FrameDTO frame) {
        if (Arrays.equals(frame.getMacDestination(), broadcastMac) || Arrays.equals(frame.getMacDestination(), mac)) {
            byte[] receivedFCS = frame.getFrameCheckSequence();
            byte[] dataForCRC = FrameOperations.getFrameToSendToCRC(frame);
            byte[] calculatedFCS = CalculateCfc.getCRCBytes(dataForCRC);

            if (!Arrays.equals(calculatedFCS, receivedFCS)) {
                System.out.println(deviceName + " - ERRO DE CRC: Quadro recebido corrompido. Descartando.");
                return;
            }

            if (frame.getFrameType() == 0) {
                byte[] payload = frame.getPayload();
                int seq = extractSeq(payload);

                String sourceMacStr = macToString(frame.getMacSource());
                List<Byte> buffer = receivedBuffers.computeIfAbsent(sourceMacStr, k -> new ArrayList<>());

                if (!Arrays.equals(frame.getMacDestination(), broadcastMac)) {
                    if (seq == expectedSeqNum) {
                        byte[] data = extractData(payload);
                        for (byte b : data) {
                            buffer.add(b);
                        }
                        System.out.println(deviceName + " - Recebido de " + sourceMacStr + ": " + new String(data, StandardCharsets.UTF_8) + " (Seq: " + seq + ")");
                        expectedSeqNum++;
                        sendAck(expectedSeqNum, frame.getMacSource());
                        saveReceivedBuffer(sourceMacStr, buffer);
                    } else {
                        System.out.println(deviceName + " - Quadro fora de ordem ou duplicado (Seq: " + seq + ", Esperado: " + expectedSeqNum + "). Reenviando ACK para " + expectedSeqNum);
                        sendAck(expectedSeqNum, frame.getMacSource());
                    }
                } else {
                    byte[] data = extractData(payload);
                    for (byte b : data) {
                        buffer.add(b);
                    }
                    System.out.println(deviceName + " - Recebido broadcast de " + sourceMacStr + ": " + new String(data, StandardCharsets.UTF_8));
                    saveReceivedBuffer(sourceMacStr, buffer);
                }
            } else if (frame.getFrameType() == 1) {
                int ackNum = extractSeq(frame.getPayload());
                System.out.println(deviceName + " - Recebido ACK: " + ackNum + " (Base: " + base + ")");

                if (ackNum > base) {
                    base = ackNum;
                    if (base < nextSeqNum) {
                        startTimerForBase();
                    } else {
                        stopTimerForBase();
                    }
                }
            }
        }
    }


    private void saveReceivedBuffer(String sourceMacStr, List<Byte> buffer) {
        File receivedDir = new File("devices/" + deviceName + "/received");
        if (!receivedDir.exists()) {
            receivedDir.mkdirs();
        }
        File file = new File(receivedDir, "receivedfrommac_" + sourceMacStr + ".txt");

        byte[] bytes = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            bytes[i] = buffer.get(i);
        }

        try {
            Files.write(file.toPath(), bytes);
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo recebido: " + e.getMessage());
        }
    }

    private void sendAck(int ackNum, byte[] destinationMac) {
        try {
            FrameDTO ack = new FrameDTO();
            ack.setMacSource(mac);
            ack.setMacDestination(destinationMac);
            ack.setEtherType(new byte[]{0x08, 0x00});
            ack.setFrameType((byte) 1);
            ack.setPayload(new byte[]{(byte) ackNum});
            ack.setFrameCheckSequence(CalculateCfc.getCRCBytes(FrameOperations.getFrameToSendToCRC(ack)));
            out.write(FrameOperations.serialize(ack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void startTimerForBase() {
        stopTimerForBase();
        retransmissionTimer = new Timer(true);
        retransmissionTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println(deviceName + " - Timer expirou para o base " + base + ". Retransmitindo de " + base + " a " + (nextSeqNum - 1));
                    for (int i = base; i < nextSeqNum; i++) {
                        FrameDTO frameToRetransmit = window.get(i);
                        if (frameToRetransmit != null) {
                            out.write(FrameOperations.serialize(frameToRetransmit));
                        }
                    }
                    startTimerForBase();
                } catch (IOException e) {
                    System.err.println(deviceName + " - Erro durante a retransmissão: " + e.getMessage());
                }
            }
        };
        retransmissionTimer.schedule(retransmissionTask, 1000);
    }

    private synchronized void stopTimerForBase() {
        if (retransmissionTimer != null) {
            retransmissionTimer.cancel();
            retransmissionTimer = null;
        }
        if (retransmissionTask != null) {
            retransmissionTask.cancel();
            retransmissionTask = null;
        }
    }

    private List<byte[]> fragment(String message) {
        List<byte[]> fragments = new ArrayList<>();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int payloadDataLength = frameSize - 1;
        for (int i = 0; i < messageBytes.length; i += payloadDataLength) {
            int len = Math.min(payloadDataLength, messageBytes.length - i);
            fragments.add(Arrays.copyOfRange(messageBytes, i, i + len));
        }
        return fragments;
    }
}