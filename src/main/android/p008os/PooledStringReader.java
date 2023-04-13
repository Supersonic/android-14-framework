package android.p008os;
/* renamed from: android.os.PooledStringReader */
/* loaded from: classes3.dex */
public class PooledStringReader {
    private final Parcel mIn;
    private final String[] mPool;

    public PooledStringReader(Parcel in) {
        this.mIn = in;
        int size = in.readInt();
        this.mPool = new String[size];
    }

    public int getStringCount() {
        return this.mPool.length;
    }

    public String readString() {
        int idx = this.mIn.readInt();
        if (idx >= 0) {
            return this.mPool[idx];
        }
        String str = this.mIn.readString();
        this.mPool[(-idx) - 1] = str;
        return str;
    }
}
