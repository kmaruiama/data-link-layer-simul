import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Device {
    private final byte[] mac;
    private final int frameSize;
    private Socket socket;
    private ObjectOutputStream out;

    public Device(byte[] mac, int frameSize) {
        this.mac = mac;
        this.frameSize = frameSize;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());

        //thread para ouvir respostas do switch
        new Thread(() -> {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    FrameDTO frame = (FrameDTO) in.readObject();
                    System.out.println("Frame recebido: " + new String(frame.getPayload(), StandardCharsets.UTF_8));
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Conexão encerrada");
            }
        }).start();
    }

    public void send(String message, byte[] destMac) throws IOException {
        List<byte[]> fragments = fragment(message);
        for (byte[] frag : fragments) {
            FrameDTO frame = new FrameDTO();
            frame.setMacSource(mac);
            frame.setMacDestination(destMac);
            frame.setPayload(frag);
            frame.setEtherType(new byte[]{0x08, 0x00}); //ipv4
            frame.setFrameCheckSequence(CalculateCfc.getCRCBytes(FrameOperations.getFrameToSendToCRC(frame))); //chamar cfc aquiiii
            byte[] serialized = FrameOperations.serialize(frame);
            socket.getOutputStream().write(serialized);
        }
    }


    private List<byte[]> fragment(String message) {
        List<byte[]> fragments = new ArrayList<>();
        byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);

        for (int index = 0; index < messageInBytes.length; index += frameSize) {
            int remaining = messageInBytes.length - index;
            int currentFragmentSize = Math.min(frameSize, remaining);
            byte[] fragment = Arrays.copyOfRange(messageInBytes, index, index + currentFragmentSize);
            fragments.add(fragment);
        }

        return fragments;
    }
}
