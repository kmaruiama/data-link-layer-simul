import java.util.Arrays;
import java.util.Random;

public class NoiseSimulator {
    private static final Random random = new Random();

    public static FrameDTO apply(FrameDTO original, double errorChance) {
        if (random.nextDouble() > errorChance) {
            return original;//não altera
        }

        FrameDTO corrupted = new FrameDTO();
        corrupted.setMacDestination(original.getMacDestination());
        corrupted.setMacSource(original.getMacSource());
        corrupted.setEtherType(original.getEtherType());
        corrupted.setPreamble(original.getPreamble());
        corrupted.setStartFrameDelimiter(original.getStartFrameDelimiter());

        byte[] payload = Arrays.copyOf(original.getPayload(), original.getPayload().length);
        int index = random.nextInt(payload.length); //escolhe um bit e muda
        payload[index] ^= 0x01;
        corrupted.setPayload(payload);

        corrupted.setFrameCheckSequence(original.getFrameCheckSequence());

        return corrupted;
    }
}
