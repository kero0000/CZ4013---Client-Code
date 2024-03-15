import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UnmarshallClientCode {

    /**
     * Checks if request message is null.
     *
     * @param request The request message to be marshalled
     */
    private static void checkRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "The provided request object is null. A valid request object is required for this operation.");
        }
    }

    /**
     * Marshalls a request message into a ByteBuffer with padding.
     *
     * @param request The request message to be marshalled
     * @return The marshalled request message in byte array.
     */
    public static byte[] marshallRequest(Request request) {

        checkRequest(request);
        String filename = request.getFilename();

        if (filename == null) {
            throw new IllegalArgumentException("The filename in the request object cannot be null");
        }

        byte[] stringBytes = filename.getBytes(StandardCharsets.UTF_8);
        int estimatedSize = 0;
        int paddingSizeFilename = Marshaller.calculatePadding(stringBytes.length);
        estimatedSize += Integer.BYTES * 2;
        estimatedSize += Integer.BYTES + stringBytes.length;
        estimatedSize += paddingSizeFilename;
        ByteBuffer buffer = null;

        int operation = request.getOperation();

        // Switch for different request types
        switch (operation) {
            case 1:
            case 5:
                estimatedSize += Integer.BYTES * 2;
                buffer = ByteBuffer.allocate(estimatedSize);

                Marshaller.marshallInt(buffer, request.getrequestId());
                Marshaller.marshallInt(buffer, request.getOperation());
                Marshaller.marshallString(buffer, stringBytes, paddingSizeFilename);
                Marshaller.marshallInt(buffer, request.getOffset());
                Marshaller.marshallInt(buffer, request.getBytesToReadFrom());
                return buffer.array();

            case 2:
                String bytesToWrite = request.getBytesToWrite();
                byte[] stringBytesToWrite = bytesToWrite.getBytes(StandardCharsets.UTF_8);
                int paddingSizeBytesToWrite = Marshaller.calculatePadding(stringBytesToWrite.length);

                estimatedSize += Integer.BYTES + stringBytesToWrite.length;
                estimatedSize += Integer.BYTES;
                estimatedSize += paddingSizeBytesToWrite;
                buffer = ByteBuffer.allocate(estimatedSize);

                Marshaller.marshallInt(buffer, request.getrequestId());
                Marshaller.marshallInt(buffer, request.getOperation());
                Marshaller.marshallString(buffer, stringBytes, paddingSizeFilename);
                Marshaller.marshallString(buffer, stringBytesToWrite, paddingSizeBytesToWrite);
                Marshaller.marshallInt(buffer, request.getBytesToReadFrom());

                return buffer.array();
            case 3:
                estimatedSize += Integer.BYTES;
                buffer = ByteBuffer.allocate(estimatedSize);

                Marshaller.marshallInt(buffer, request.getrequestId());
                Marshaller.marshallInt(buffer, request.getOperation());
                Marshaller.marshallString(buffer, stringBytes, paddingSizeFilename);
                Marshaller.marshallInt(buffer, request.getInterval());

                return buffer.array();
            case 4:
            case 6:
                buffer = ByteBuffer.allocate(estimatedSize);

                Marshaller.marshallInt(buffer, request.getrequestId());
                Marshaller.marshallInt(buffer, request.getOperation());
                Marshaller.marshallString(buffer, stringBytes, paddingSizeFilename);

                return buffer.array();
            default:
                System.out.println("No such operation");
                break;
        }

        return buffer.array();
    }
}
