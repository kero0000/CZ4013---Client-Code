//import java.nio.charset.StandardCharsets;
//
//public class UnmarshallerCaller {
//
//    /**
//     * Unmarshalls a reply message from a ByteBuffer.
//     *
//     * @param reply The reply message to be unmarshalled.
//     */
//    private static void checkReply(byte[] reply) {
//        if (reply == null) {
//            throw new IllegalArgumentException(
//                    "The provided reply object is null. A valid request object is required for this operation.");
//        }
//    }
//
//    /**
//     * Unmarshalls a reply message from the byte array.
//     *
//     * @param reply The reply message to be unmarshalled.
//     */
//    public static Reply unmarshallReply(byte[] replyBuffer) {
//
//        int index = 0;
//        checkReply(replyBuffer);
//
//        Pair<Integer, Integer> requestIdPair = Unmarshaller.unmarshallInt(replyBuffer, index);
//        index = requestIdPair.getFirst();
//        int requestId = requestIdPair.getSecond();
//        Pair<Integer, Integer> statusPair = Unmarshaller.unmarshallInt(replyBuffer, index);
//        index = statusPair.getFirst();
//        int status = statusPair.getSecond();
//        Pair<Integer, Integer> timePair = Unmarshaller.unmarshallInt(replyBuffer, index);
//        index = timePair.getFirst();
//        int modifiedTime = timePair.getSecond();
//
//        Pair<Integer, String> dataPair = Unmarshaller.unmarshallString(replyBuffer, index);
//        index = dataPair.getFirst();
//        String data = dataPair.getSecond(); // data can be the actual data or error message to be printed or null.
//
//
//    };
//
