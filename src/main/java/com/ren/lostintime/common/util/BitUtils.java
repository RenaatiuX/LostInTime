package com.ren.lostintime.common.util;

public class BitUtils {

    public static boolean getBit(byte b, int index) {
        if (index < 0 || index > 7) throw new IndexOutOfBoundsException("Byte bit index must be 0-7");
        return ((b >> index) & 1) == 1;
    }

    public static boolean getBit(int i, int index) {
        if (index < 0 || index > 31) throw new IndexOutOfBoundsException("Integer bit index must be 0-31");
        return ((i >> index) & 1) == 1;
    }

    public static byte setBit(byte b, int index, boolean value) {
        if (index < 0 || index > 7) throw new IndexOutOfBoundsException("Byte bit index must be 0-7");
        if (value) {
            return (byte) (b | (1 << index));
        } else {
            return (byte) (b & ~(1 << index));
        }
    }

    public static int setBit(int i, int index, boolean value) {
        if (index < 0 || index > 31) throw new IndexOutOfBoundsException("Integer bit index must be 0-31");
        if (value) {
            return i | (1 << index);
        } else {
            return i & ~(1 << index);
        }
    }
}
