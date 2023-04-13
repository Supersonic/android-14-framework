package android.media;

import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes2.dex */
class ExifInterfaceUtils {
    private static final String TAG = "ExifInterface";

    ExifInterfaceUtils() {
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[8192];
        while (true) {
            int c = in.read(buffer);
            if (c != -1) {
                total += c;
                out.write(buffer, 0, c);
            } else {
                return total;
            }
        }
    }

    public static void copy(InputStream in, OutputStream out, int numBytes) throws IOException {
        int remainder = numBytes;
        byte[] buffer = new byte[8192];
        while (remainder > 0) {
            int bytesToRead = Math.min(remainder, 8192);
            int bytesRead = in.read(buffer, 0, bytesToRead);
            if (bytesRead != bytesToRead) {
                throw new IOException("Failed to copy the given amount of bytes from the inputstream to the output stream.");
            }
            remainder -= bytesRead;
            out.write(buffer, 0, bytesRead);
        }
    }

    public static long[] convertToLongArray(Object inputObj) {
        if (inputObj instanceof int[]) {
            int[] input = (int[]) inputObj;
            long[] result = new long[input.length];
            for (int i = 0; i < input.length; i++) {
                result[i] = input[i];
            }
            return result;
        } else if (inputObj instanceof long[]) {
            return (long[]) inputObj;
        } else {
            return null;
        }
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static boolean startsWith(byte[] cur, byte[] val) {
        if (cur == null || val == null || cur.length < val.length || cur.length == 0 || val.length == 0) {
            return false;
        }
        for (int i = 0; i < val.length; i++) {
            if (cur[i] != val[i]) {
                return false;
            }
        }
        return true;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void closeFileDescriptor(FileDescriptor fd) {
        try {
            Os.close(fd);
        } catch (ErrnoException ex) {
            Log.m109e(TAG, "Error closing fd.", ex);
        }
    }
}
