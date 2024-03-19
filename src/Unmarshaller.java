
import java.nio.charset.StandardCharsets;

public class Unmarshaller {
    static int BOUNDARY_SIZE = 4;

    /**
     * Unmarshalls a string from a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the string is to be retrieved.
     * @return The string from unicode bytes.
     */
    public static Pair<Integer, String> unmarshallString(byte[] buffer, int index) {

        int actualStringLength = 0;
        // To retrieve in Big Endian byte order
        for (int i = 0; i < 4; i++) {
            actualStringLength <<= 8; // Shift left so MSB will be filled first
            actualStringLength |= (buffer[index++] & 0xFF); // Bitwise OR to add the bytes and Bitwise AND with 0xFF to
            // prevent signed extension
        }

        byte[] stringBytes = new byte[actualStringLength];
        // System.arraycopy(buffer, index, stringBytes, 0, actualStringLength);

        for (int i = 0; i < actualStringLength; i++) {
            stringBytes[i] = buffer[index++];
        }

        int paddingSize = calculatePadding(actualStringLength);
        index += paddingSize;
        return new Pair<>(index, new String(stringBytes, StandardCharsets.UTF_8));

    }

    /**
     * Unmarshalls an interger from a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the integer is to be retrieved.
     * @return The integer.
     */
    public static Pair<Integer, Integer> unmarshallInt(byte[] buffer, int index) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value <<= 8;
            value |= (buffer[index++] & 0xFF);
        }
        return new Pair<>(index, value);
    }

    /**
     * Unmarshalls a long interger from a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the long integer is to be retrieved.
     * @return The long integer.
     */
    public static Pair<Integer, Long> unmarshallLong(byte[] buffer, int index) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value <<= 8;
            value |= (buffer[index++] & 0xFF);
        }
        return new Pair<>(index, value);
    }

    /**
     * Unmarshalls a float from a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the float is to be retrieved.
     * @return The float value.
     */
    public static Pair<Integer, Float> unmarshallFloat(byte[] buffer, int index) {
        int intBits = 0;

        for (int i = 0; i < 4; i++) {
            intBits <<= 8;
            intBits |= (buffer[index++] & 0xFF);
        }
        return new Pair<>(index, Float.intBitsToFloat(intBits)); // Return the standard float from IEEE representation
    }

    /**
     * Unmarshalls a short integer from a ByteBuffer.
     *
     * @param buffer The ByteBuffer to which the short integer is to be retrieved.
     * @return The short integer.
     */
    public static Pair<Integer, Short> unmarshallShort(byte[] buffer, int index) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            value <<= 8;
            value |= (buffer[index++] & 0xFF);
        }
        index += 2;
        return new Pair<>(index, value);
    }

    /**
     * Calculates the size of padding for a given data type length..
     *
     * @param length The length of the data type to which the padding are to be
     *               added.
     * @return The size of the padding.
     */
    public static int calculatePadding(int length) {
        return BOUNDARY_SIZE - (length % BOUNDARY_SIZE);
    }

    public static int calculatePadding(long length) {
        return BOUNDARY_SIZE - (int) (length % BOUNDARY_SIZE);
    }

}
