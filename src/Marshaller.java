public class Marshaller {
    static int BOUNDARY_SIZE = 4;

    /**
     * Marshalls a string into a ByteBuffer with padding.
     *
     * @param buffer      The byte[] array to which a string and padding are to be
     *                    added.
     * @param index       The index to keep track of the position in the array.
     * @param stringBytes The bytes of the string to be added.
     * @param paddingSize The size of the padding to be added.
     * @return The index.
     */
    public static int marshallString(byte[] buffer, int index, byte[] stringBytes, int paddingSize) {

        // Big Endian byte order
        for (int i = 3; i >= 0; i--) {
            buffer[index++] = (byte) (stringBytes.length >>> (8 * i)); // Unsigned Shift right so the MSB will be read
            // first
        }

        // System.arraycopy(stringBytes, 0, buffer, index, stringBytes.length);

        for (int i = 0; i < stringBytes.length; i++) {
            buffer[index++] = stringBytes[i];
        }

        // Add padding to boundary for memory management
        for (int i = 0; i < paddingSize; i++) {
            buffer[index++] = 0;
        }

        return index;
    }

    /**
     * Marshalls an integer into a ByteBuffer with padding.
     *
     * @param buffer The byte[] array to which an integer is to be
     *               added.
     * @param index  The index to keep track of the position in the array.
     * @param value  The bytes of the integer to be added.
     * @return The index.
     */
    public static int marshallInt(byte[] buffer, int index, int value) {
        for (int i = 3; i >= 0; i--) {
            buffer[index++] = (byte) (value >>> (8 * i));
        }
        return index;
    }

    /**
     * Marshalls a long integer into a ByteBuffer with padding.
     *
     * @param buffer The byte[] array to which a long integer is to be
     *               added.
     * @param index  The index to keep track of the position in the array.
     * @param value  The bytes of the long integer to be added.
     * @return The index.
     */
    public static int marshallLong(byte[] buffer, int index, Long value) {
        for (int i = 7; i >= 0; i--) {
            buffer[index++] = (byte) (value >>> (8 * i));
        }
        return index;

    }

    /**
     * Marshalls a float into a ByteBuffer with padding.
     *
     * @param buffer The byte[] array to which a float is to be
     *               added.
     * @param index  The index to keep track of the position in the array.
     * @param value  The bytes of the float to be added.
     * @return The index.
     */
    public static int marshallFloat(byte[] buffer, int index, Float value) {

        int intBits = Float.floatToIntBits(value); // Convert the float to IEEE representation
        for (int i = 3; i >= 0; i--) {
            buffer[index++] = (byte) (intBits >>> (8 * i));
        }
        return index;
    }

    /**
     * Marshalls a short integer into a ByteBuffer with padding.
     *
     * @param buffer The byte[] array to which a short integer is to be
     *               added.
     * @param index  The index to keep track of the position in the array.
     * @param value  The bytes of the short integer to be added.
     * @return The index.
     */
    public static int marshallShort(byte[] buffer, int index, short value) {
        for (int i = 1; i >= 0; i--) {
            buffer[index++] = (byte) (value >>> (8 * i));
        }
        // Add padding to boundary for memory management
        for (int i = 0; i < 2; i++) {
            buffer[index++] = 0;
        }
        return index;
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

    public static int calculatePadding(long length) {
        return BOUNDARY_SIZE - (int) (length % BOUNDARY_SIZE);
    }

}
