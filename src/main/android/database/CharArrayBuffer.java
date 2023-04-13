package android.database;
/* loaded from: classes.dex */
public final class CharArrayBuffer {
    public char[] data;
    public int sizeCopied;

    public CharArrayBuffer(int size) {
        this.data = new char[size];
    }

    public CharArrayBuffer(char[] buf) {
        this.data = buf;
    }
}
