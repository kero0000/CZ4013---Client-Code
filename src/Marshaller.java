import java.nio.ByteBuffer;

public class Marshaller {
    static int BOUNDARY_SIZE = 4;

    /**
     * Marshalls a string into a ByteBuffer with padding.
     *
     * @param buffer      The ByteBuffer to which the string and padding are to be
     *                    added.
     * @param stringBytes The bytes of the string to be added to the buffer.
     * @param paddingSize The size of the padding to be added after the string.
     */
    public static void marshallString(ByteBuffer buffer, byte[] stringBytes, int paddingSize) {
        // byte[] stringBytes = str.getBytes(StandardCharsets.UTF_8);
        // int paddingSize = calculatePadding(stringBytes.length, boundary);

        buffer.putInt(stringBytes.length);
        buffer.put(stringBytes);

        // Add padding to boundary for memory management
        for (int i = 0; i < paddingSize; i++) {
            buffer.put((byte) 0);
        }

    }

    /**
     * Marshalls an interger into a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the integer is to be added.
     * @param value  The integer to be added to the buffer.
     */
    public static void marshallInt(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }

    /**
     * Marshalls a long interger into a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the long interger is to be added.
     * @param value  The long integer to be added to the buffer.
     */
    public static void marshallLong(ByteBuffer buffer, Long value) {
        // buffer.putInt(Long.BYTES); // Include the length of the Long value
        buffer.putLong(value);
        // int paddingSize = calculatePadding(Long.BYTES, boundary);
        // for (int i = 0; i < paddingSize; i++) {
        // buffer.put((byte) 0);
        // }
    }

    /**
     * Marshalls a float into a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the float is to be added.
     * @param value  The float to be added to the buffer.
     */
    public static void marshallFloat(ByteBuffer buffer, Float value) {

        int intBits = Float.floatToIntBits(value); // Convert the float to IEEE representation
        buffer.putInt(intBits);
    }

    /**
     * Marshalls a short interger into a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the short interger is to be added.
     * @param value  The short integer to be added to the buffer.
     */
    public static void marshallShort(ByteBuffer buffer, short value) {
        buffer.putShort(value);
    }

    /**
     * Marshalls an interger into a ByteBuffer.
     *
     * @param length The length of the data type to which the padding are to be
     *               added.
     * @return The size of padding.
     */
    public static int calculatePadding(int length) {
        return (BOUNDARY_SIZE - (length % BOUNDARY_SIZE));
    }

}
