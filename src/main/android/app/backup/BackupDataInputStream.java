package android.app.backup;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes.dex */
public class BackupDataInputStream extends InputStream {
    int dataSize;
    String key;
    BackupDataInput mData;
    byte[] mOneByte;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BackupDataInputStream(BackupDataInput data) {
        this.mData = data;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        byte[] one = this.mOneByte;
        if (this.mOneByte == null) {
            byte[] bArr = new byte[1];
            this.mOneByte = bArr;
            one = bArr;
        }
        this.mData.readEntityData(one, 0, 1);
        return one[0];
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int size) throws IOException {
        return this.mData.readEntityData(b, offset, size);
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return this.mData.readEntityData(b, 0, b.length);
    }

    public String getKey() {
        return this.key;
    }

    public int size() {
        return this.dataSize;
    }
}
