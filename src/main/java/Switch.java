import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Switch {
    private final Map<byte[], Integer> macTable = new HashMap<>();
    private final Map<Integer, Socket> portSockets = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private int nextPort = 1;
    private final int frameSize;

    public Switch(int frameSize) {
        this.frameSize = frameSize;
    }


    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Switch escutando na porta " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            int portNumber = nextPort++;
            portSockets.put(portNumber, socket);

            pool.submit(() -> handleDevice(socket, portNumber));
        }
    }

    private void handleDevice(Socket socket, int portNumber) {
        try {
            InputStream in = socket.getInputStream();
            while (true) {
                byte[] buffer = new byte[frameSize];
                int bytesRead = in.read(buffer);

                if (bytesRead == -1) break;

                byte[] receivedFrame = Arrays.copyOf(buffer, bytesRead);
                FrameDTO frame = FrameOperations.deserialize(receivedFrame);
                receiveFrame(frame, portNumber);
            }
        } catch (IOException e) {
            System.out.println("Conexão encerrada na porta " + portNumber);
        }
    }


    private Integer getPortByMac(byte[] mac) {
        for (Map.Entry<byte[], Integer> entry : macTable.entrySet()) {
            if (Arrays.equals(entry.getKey(), mac)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void putMac(byte[] mac, int port) {
        byte[] keyToRemove = null;
        for (byte[] key : macTable.keySet()) {
            if (Arrays.equals(key, mac)) {
                keyToRemove = key;
                break;
            }
        }
        if (keyToRemove != null) {
            macTable.remove(keyToRemove);
        }
        macTable.put(mac, port);
    }

    public void receiveFrame(FrameDTO frame, int sourcePort) {
        byte[] sourceMac = frame.getMacSource();
        byte[] destMac = frame.getMacDestination();

        putMac(sourceMac, sourcePort);

        Integer destPort = getPortByMac(destMac);
        if (destPort != null && portSockets.containsKey(destPort)) {
            //unicast
            try {
                ObjectOutputStream out = new ObjectOutputStream(portSockets.get(destPort).getOutputStream());
                out.writeObject(frame);
            } catch (IOException e) {
                System.err.println("Erro ao enviar frame para porta " + destPort);
            }
        } else {
            //broadcast
            for (Map.Entry<Integer, Socket> entry : portSockets.entrySet()) {
                if (entry.getKey() != sourcePort) {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(entry.getValue().getOutputStream());
                        out.writeObject(frame);
                    } catch (IOException e) {
                        System.err.println("Erro ao fazer broadcast para porta " + entry.getKey());
                    }
                }
            }
        }
    }
}
