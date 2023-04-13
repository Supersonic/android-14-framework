package android.util;

import android.p008os.ParcelableException;
import com.android.internal.util.Preconditions;
import java.io.IOException;
/* loaded from: classes3.dex */
public class ExceptionUtils {
    public static RuntimeException wrap(IOException e) {
        throw new ParcelableException(e);
    }

    public static void maybeUnwrapIOException(RuntimeException e) throws IOException {
        if (e instanceof ParcelableException) {
            ((ParcelableException) e).maybeRethrow(IOException.class);
        }
    }

    public static String getCompleteMessage(String msg, Throwable t) {
        StringBuilder builder = new StringBuilder();
        if (msg != null) {
            builder.append(msg).append(": ");
        }
        builder.append(t.getMessage());
        while (true) {
            Throwable cause = t.getCause();
            t = cause;
            if (cause != null) {
                builder.append(": ").append(t.getMessage());
            } else {
                return builder.toString();
            }
        }
    }

    public static String getCompleteMessage(Throwable t) {
        return getCompleteMessage(null, t);
    }

    public static <E extends Throwable> void propagateIfInstanceOf(Throwable t, Class<E> c) throws Throwable {
        if (t != null && c.isInstance(t)) {
            throw c.cast(t);
        }
    }

    public static <E extends Exception> RuntimeException propagate(Throwable t, Class<E> c) throws Exception {
        propagateIfInstanceOf(t, c);
        return propagate(t);
    }

    public static RuntimeException propagate(Throwable t) {
        Preconditions.checkNotNull(t);
        propagateIfInstanceOf(t, Error.class);
        propagateIfInstanceOf(t, RuntimeException.class);
        throw new RuntimeException(t);
    }

    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static Throwable appendCause(Throwable t, Throwable cause) {
        if (cause != null) {
            getRootCause(t).initCause(cause);
        }
        return t;
    }
}
