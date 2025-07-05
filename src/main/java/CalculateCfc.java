public class CalculateCfc {
    private static final int constant = 0x04C11DB7; //polinômio padrao IEEE 802.3

    public static byte[] getCRCBytes(byte[] data) {
        int crc = calculateCRC32(data);
        return new byte[] {
                (byte) ((crc >>> 24) & 0xFF),
                (byte) ((crc >>> 16) & 0xFF),
                (byte) ((crc >>> 8) & 0xFF),
                (byte) (crc & 0xFF)
        };
    }

    private static int calculateCRC32(byte[] data) {
        int crc = 0xFFFFFFFF;

        for (byte b : data) {
            crc ^= (b & 0xFF) << 24; //xor
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80000000) != 0) {
                    crc = (crc << 1) ^ constant;
                } else {
                    crc <<= 1;
                }
            }
        }
        return ~crc;
    }
}
