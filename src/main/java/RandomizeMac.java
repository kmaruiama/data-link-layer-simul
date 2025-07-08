import java.util.Random;

public class RandomizeMac {
    public static byte[] randomMac(){
        byte[] macAddress = new byte[6];
        Random random = new Random();
        macAddress[0] = (byte) ((random.nextInt(256) & 0b11111100)); // Mantém como unicast global

        for (int i = 1; i < 6; i++) {
            macAddress[i] = (byte) random.nextInt(256);
        }
        return macAddress;
    }
}