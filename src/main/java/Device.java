import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Dispositivo {
    private int frameSize;
    private byte[] mac;

    Dispositivo(byte[] mac, int frameSize){
        this.frameSize = frameSize;
        this.mac = mac;
    }

    public void updateFrameSize(int framesize){
        this.frameSize = framesize;
    }

    private void send(){
        String aleatorio = "isiwjijsiwjsijw";//aqui da pra depois trocar pra ele ler um txt e ir mandando, enfim
        List<byte[]> fragments = fragment(aleatorio);
        List<FrameDTO> frames = buildFrames(fragments);

    }

    List<FrameDTO> buildFrames(List<byte[]> fragments){
        
    }

    //nao tem a ver "diretamente" com o protocolo, mas quebra a string em varios arrays de bytes de tamanho frameSize
    private List<byte[]> fragment(String message) {
        List<byte[]> fragments = new ArrayList<>();
        byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);

        for (int index = 0; index < messageInBytes.length; index += frameSize) {
            int remaining = messageInBytes.length - index;
            int currentFragmentSize = Math.min(frameSize, remaining);

            byte[] fragment = new byte[currentFragmentSize];
            for (int i = 0; i < currentFragmentSize; i++) {
                fragment[i] = messageInBytes[index + i];
            }

            fragments.add(fragment);
        }

        return fragments;
    }
}
