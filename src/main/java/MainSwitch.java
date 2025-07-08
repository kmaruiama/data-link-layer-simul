import java.io.IOException;

public class MainSwitch {
    public static void main(String[] args) throws IOException{
        int frameSize = 1024;
        Switch networkSwitch = new Switch(frameSize);
        networkSwitch.start(9000);
    }
}
