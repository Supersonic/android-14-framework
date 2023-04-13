package android.net;

import android.os.Looper;
import android.os.Message;
import com.android.internal.annotations.VisibleForTesting;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface NetworkFactoryShim {
    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    Looper getLooper();

    NetworkProvider getProvider();

    int getRequestCount();

    int getSerialNumber();

    @VisibleForTesting
    Message obtainMessage(int i, int i2, int i3, Object obj);

    void reevaluateAllRequests();

    void register(String str);

    void releaseRequestAsUnfulfillableByAnyFactory(NetworkRequest networkRequest);

    void setCapabilityFilter(NetworkCapabilities networkCapabilities);

    void setScoreFilter(int i);

    void setScoreFilter(NetworkScore networkScore);

    void terminate();

    default void registerIgnoringScore(String str) {
        throw new UnsupportedOperationException();
    }
}
