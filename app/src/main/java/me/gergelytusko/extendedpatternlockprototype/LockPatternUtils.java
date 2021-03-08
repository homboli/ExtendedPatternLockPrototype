package me.gergelytusko.extendedpatternlockprototype;

import java.util.ArrayList;
import java.util.List;

public class LockPatternUtils {
    private static final String TAG = "LockPatternUtils";

    /**
     * Deserialize a pattern.
     * @param  bytes The pattern serialized with {@link #patternToByteArray}
     * @return The pattern.
     */
    public static List<LockPatternView.Cell> byteArrayToPattern(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        List<LockPatternView.Cell> result = new ArrayList();

        for (int i = 0; i < bytes.length; i++) {
            byte b = (byte) (bytes[i] - '1');
            result.add(LockPatternView.Cell.of(b / 3, b % 3));
        }
        return result;
    }

    /**
     * Serialize a pattern.
     * @param pattern The pattern.
     * @return The pattern in byte array form.
     */
    public static byte[] patternToByteArray(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return new byte[0];
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn() + '1');
        }
        return res;
    }

}