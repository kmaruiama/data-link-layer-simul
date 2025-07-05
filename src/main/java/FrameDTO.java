import java.util.ArrayList;

public class FrameDTO {
    private byte[] preamble = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
    private byte[] startFrameDelimiter = {(byte) 0xAB};
    private byte[] macDestination = new byte[6];
    private byte[] macSource = new byte[6];
    private byte[] etherType = new byte[2];
    private byte[] payload;
    private byte[] frameCheckSequence = new byte [4];

    public byte[] getPreamble() {
        return preamble;
    }

    public void setPreamble(byte[] preamble) {
        this.preamble = preamble;
    }

    public byte[] getStartFrameDelimiter() {
        return startFrameDelimiter;
    }

    public void setStartFrameDelimiter(byte[] startFrameDelimiter) {
        this.startFrameDelimiter = startFrameDelimiter;
    }

    public byte[] getMacDestination() {
        return macDestination;
    }

    public void setMacDestination(byte[] macDestination) {
        this.macDestination = macDestination;
    }

    public byte[] getMacSource() {
        return macSource;
    }

    public void setMacSource(byte[] macSource) {
        this.macSource = macSource;
    }

    public byte[] getEtherType() {
        return etherType;
    }

    public void setEtherType(byte[] etherType) {
        this.etherType = etherType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getFrameCheckSequence() {
        return frameCheckSequence;
    }

    public void setFrameCheckSequence(byte[] frameCheckSequence) {
        this.frameCheckSequence = frameCheckSequence;
    }
}
