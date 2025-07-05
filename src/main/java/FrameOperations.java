public class FrameOperations {
    public static byte[] serialize(FrameDTO frameDTO){
        //calculamos o tamanho total do frame
        int totalFrameSizeInBytes = frameDTO.getStartFrameDelimiter().length +
                frameDTO.getFrameCheckSequence().length +
                frameDTO.getEtherType().length +
                frameDTO.getMacDestination().length +
                frameDTO.getMacSource().length +
                frameDTO.getPreamble().length +
                frameDTO.getPayload().length;
        //declaramos
        byte[] frame = new byte[totalFrameSizeInBytes];

        int frameIndex = 0;
        //juntamos o conteudo em um unico array
        frameIndex = copyBytes(frameDTO.getPreamble(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getStartFrameDelimiter(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getMacDestination(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getMacSource(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getEtherType(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getPayload(), frame, frameIndex);
        copyBytes(frameDTO.getFrameCheckSequence(), frame, frameIndex);

        return frame;
    }

    private static int copyBytes(byte[] source, byte[] destination, int frameIndex) {
        for(byte sourceByte: source){
            destination[frameIndex] = sourceByte;
            frameIndex++;
        }
        return frameIndex;
    }

    public static FrameDTO deserialize(byte[] frame){
        FrameDTO frameDto = new FrameDTO();

        int index = 0;

        byte[] preamble = new byte[7];
        System.arraycopy(frame, index, preamble, 0, 7);
        frameDto.setPreamble(preamble);
        index += 7;

        byte[] sfd = new byte[1];
        System.arraycopy(frame, index, sfd, 0, 1);
        frameDto.setStartFrameDelimiter(sfd);
        index += 1;

        byte[] macDest = new byte[6];
        System.arraycopy(frame, index, macDest, 0, 6);
        frameDto.setMacDestination(macDest);
        index += 6;

        byte[] macSrc = new byte[6];
        System.arraycopy(frame, index, macSrc, 0, 6);
        frameDto.setMacSource(macSrc);
        index += 6;

        byte[] etherType = new byte[2];
        System.arraycopy(frame, index, etherType, 0, 2);
        frameDto.setEtherType(etherType);
        index += 2;

        int payloadLength = frame.length - index - 4;
        byte[] payload = new byte[payloadLength];
        System.arraycopy(frame, index, payload, 0, payloadLength);
        frameDto.setPayload(payload);
        index += payloadLength;

        byte[] fcs = new byte[4];
        System.arraycopy(frame, index, fcs, 0, 4);
        frameDto.setFrameCheckSequence(fcs);

        return frameDto;
    }

    public static byte[] getFrameToSendToCRC(FrameDTO frameDTO) {
        int totalFrameSizeInBytes = frameDTO.getStartFrameDelimiter().length +
                frameDTO.getFrameCheckSequence().length +
                frameDTO.getEtherType().length +
                frameDTO.getMacDestination().length +
                frameDTO.getMacSource().length +
                frameDTO.getPreamble().length +
                frameDTO.getPayload().length;

        byte[] frame = new byte[totalFrameSizeInBytes];

        int frameIndex = 0;
        frameIndex = copyBytes(frameDTO.getPreamble(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getStartFrameDelimiter(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getMacDestination(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getMacSource(), frame, frameIndex);
        frameIndex = copyBytes(frameDTO.getEtherType(), frame, frameIndex);
        copyBytes(frameDTO.getPayload(), frame, frameIndex);
        return frame;
    }
}
