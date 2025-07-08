import java.util.Arrays;
import java.util.Random;

public class NoiseSimulator {
    private static final Random random = new Random();

    public static FrameDTO apply(FrameDTO original, double errorChance) {
        if (random.nextDouble() > errorChance) {
            return original;
        }

        FrameDTO corrupted = new FrameDTO();
        corrupted.setPreamble(original.getPreamble());
        corrupted.setStartFrameDelimiter(original.getStartFrameDelimiter());
        corrupted.setMacDestination(original.getMacDestination());
        corrupted.setMacSource(original.getMacSource());
        corrupted.setEtherType(original.getEtherType());
        corrupted.setFrameType(original.getFrameType());

        byte[] payload = Arrays.copyOf(original.getPayload(), original.getPayload().length);
        if (payload.length > 0) {
            int index = random.nextInt(payload.length);
            payload[index] ^= (byte) (1 << random.nextInt(8));
        }
        corrupted.setPayload(payload);

        corrupted.setFrameCheckSequence(original.getFrameCheckSequence());

        return corrupted;
    }
}
