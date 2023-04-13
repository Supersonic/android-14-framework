package android.util;
/* loaded from: classes3.dex */
public class LogPrinter implements Printer {
    private final int mBuffer;
    private final int mPriority;
    private final String mTag;

    public LogPrinter(int priority, String tag) {
        this.mPriority = priority;
        this.mTag = tag;
        this.mBuffer = 0;
    }

    public LogPrinter(int priority, String tag, int buffer) {
        this.mPriority = priority;
        this.mTag = tag;
        this.mBuffer = buffer;
    }

    @Override // android.util.Printer
    public void println(String x) {
        Log.println_native(this.mBuffer, this.mPriority, this.mTag, x);
    }
}
