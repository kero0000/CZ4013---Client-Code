import java.nio.charset.StandardCharsets;

public class MarshallerCaller {

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

        byte[] stringBytes = filename.getBytes(StandardCharsets.UTF_8); // Convert to 8-bit unicode
        int estimatedSize = 0;
        int paddingSizeFilename = Marshaller.calculatePadding(stringBytes.length);
        estimatedSize += Integer.BYTES * 2;
        estimatedSize += Integer.BYTES + stringBytes.length;
        estimatedSize += paddingSizeFilename;
        byte[] buffer;
        int index = 0;

        int operation = request.getOperation();

        // Switch for different request types
        switch (operation) {
            case 1:
                estimatedSize += Integer.BYTES * 2;
                buffer = new byte[estimatedSize];

                index = Marshaller.marshallInt(buffer, index, request.getrequestId());
                index = Marshaller.marshallInt(buffer, index, request.getOperation());
                index = Marshaller.marshallString(buffer, index, stringBytes, paddingSizeFilename);
                index = Marshaller.marshallInt(buffer, index, request.getOffset());
                index = Marshaller.marshallInt(buffer, index, request.getBytesToReadFrom());
                return buffer;
            case 5:
                estimatedSize += Integer.BYTES * 2;
                buffer = new byte[estimatedSize];

                index = Marshaller.marshallInt(buffer, index, request.getrequestId());
                index = Marshaller.marshallInt(buffer, index, request.getOperation());
                index = Marshaller.marshallString(buffer, index, stringBytes, paddingSizeFilename);
                index = Marshaller.marshallInt(buffer, index, request.getOffset());
                index = Marshaller.marshallInt(buffer, index, request.getBytesToReadFrom());
                return buffer;

            case 2:
                String bytesToWrite = request.getBytesToWrite();
                byte[] stringBytesToWrite = bytesToWrite.getBytes(StandardCharsets.UTF_8);
                int paddingSizeBytesToWrite = Marshaller.calculatePadding(stringBytesToWrite.length);

                estimatedSize += Integer.BYTES + stringBytesToWrite.length;
                estimatedSize += Integer.BYTES;
                estimatedSize += paddingSizeBytesToWrite;
                buffer = new byte[estimatedSize];

                index = Marshaller.marshallInt(buffer, index, request.getrequestId());
                index = Marshaller.marshallInt(buffer, index, request.getOperation());
                index = Marshaller.marshallString(buffer, index, stringBytes, paddingSizeFilename);
                index = Marshaller.marshallInt(buffer, index, request.getOffset());
                index = Marshaller.marshallString(buffer, index, stringBytesToWrite, paddingSizeBytesToWrite);

                return buffer;
            case 3:
                estimatedSize += Integer.BYTES;
                buffer = new byte[estimatedSize];

                index = Marshaller.marshallInt(buffer, index, request.getrequestId());
                index = Marshaller.marshallInt(buffer, index, request.getOperation());
                index = Marshaller.marshallString(buffer, index, stringBytes, paddingSizeFilename);
                index = Marshaller.marshallInt(buffer, index, request.getInterval());

                return buffer;
            case 4:
            case 6:
                buffer = new byte[estimatedSize];

                index = Marshaller.marshallInt(buffer, index, request.getrequestId());
                index = Marshaller.marshallInt(buffer, index, request.getOperation());
                index = Marshaller.marshallString(buffer, index, stringBytes, paddingSizeFilename);

                return buffer;
            default:
                System.out.println("No such operation");
                break;
        }
        return null;
    }
}
