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

    public Device(byte[] mac, int frameSize, String deviceName) {
        this.mac = mac;
        this.frameSize = frameSize;
        this.deviceName = deviceName;
        createDeviceFolder();
    }

    private void createDeviceFolder() {
        File dir = new File("devices/" + deviceName);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Diretório criado: " + dir.getAbsolutePath());
        }
    }

    private void populateFolderWithRandomFiles(String sourceFolder, int numFiles) throws IOException {
        File source = new File(sourceFolder);
        File[] files = source.listFiles();
        if (files == null || files.length == 0) return;

        File target = new File("devices/" + deviceName);
        Random rand = new Random();

        for (int i = 0; i < numFiles; i++) {
            File toCopy = files[rand.nextInt(files.length)];
            File dest = new File(target, "copy_" + i + "_" + toCopy.getName());
            Files.copy(toCopy.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }



    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = socket.getOutputStream();

        new Thread(() -> {
            try (InputStream in = socket.getInputStream()) {
                byte[] buffer = new byte[frameSize];
                while (true) {
                    int read = in.read(buffer);
                    if (read == -1) break;
                    byte[] data = Arrays.copyOf(buffer, read);
                    FrameDTO frame = FrameOperations.deserialize(data);
                    handleReceivedFrame(frame);
                }
            } catch (IOException e) {
                System.out.println("Conexão encerrada");
            }
        }).start();
    }

    public void send(String message, byte[] destMac) throws IOException {
        List<byte[]> fragments = fragment(message);
        for (byte[] frag : fragments) {
            int janelaTamanho = 4;
            while (nextSeqNum >= base + janelaTamanho) {
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            }

            FrameDTO frame = new FrameDTO();
            frame.setMacSource(mac);
            frame.setMacDestination(destMac);
            frame.setPayload(frag);
            frame.setEtherType(new byte[]{0x08, 0x00});
            frame.setFrameCheckSequence(CalculateCfc.getCRCBytes(FrameOperations.getFrameToSendToCRC(frame)));

            frame.setPayload(addSeqToPayload(frag, nextSeqNum));
            window.put(nextSeqNum, frame);

            byte[] serialized = FrameOperations.serialize(frame);
            out.write(serialized);
            int seq = nextSeqNum;
            new Thread(() -> startTimer(seq)).start();
            nextSeqNum++;
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
        byte[] payload = frame.getPayload();
        int seq = extractSeq(payload);

        if (seq == expectedSeqNum) {
            System.out.println("Recebido: " + new String(extractData(payload), StandardCharsets.UTF_8));
            expectedSeqNum++;
            sendAck(expectedSeqNum);
        } else {
            sendAck(expectedSeqNum);
        }
    }

    private void sendAck(int ackNum) {
        try {
            FrameDTO ack = new FrameDTO();
            ack.setMacSource(mac);
            ack.setPayload(new byte[]{(byte) ackNum});
            ack.setEtherType(new byte[]{0x08, 0x00});
            ack.setMacDestination(new byte[]{0, 0, 0, 0, 0, 0});
            ack.setFrameCheckSequence(CalculateCfc.getCRCBytes(FrameOperations.getFrameToSendToCRC(ack)));
            out.write(FrameOperations.serialize(ack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimer(int seqNum) {
        try {
            Thread.sleep(1000);
            if (seqNum >= base) {
                for (int i = seqNum; i < nextSeqNum; i++) {
                    FrameDTO frame = window.get(i);
                    if (frame != null) {
                        out.write(FrameOperations.serialize(frame));
                    }
                }
            }
        } catch (InterruptedException | IOException ignored) {}
    }

    private List<byte[]> fragment(String message) {
        List<byte[]> fragments = new ArrayList<>();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < messageBytes.length; i += frameSize) {
            int len = Math.min(frameSize, messageBytes.length - i);
            fragments.add(Arrays.copyOfRange(messageBytes, i, i + len));
        }
        return fragments;
    }
}