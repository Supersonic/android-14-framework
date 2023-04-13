package com.android.server.locksettings;

import android.app.admin.PasswordMetrics;
import com.android.internal.widget.LockscreenCredential;
import java.nio.ByteBuffer;
/* loaded from: classes2.dex */
public class VersionedPasswordMetrics {
    public final PasswordMetrics mMetrics;
    public final int mVersion;

    public VersionedPasswordMetrics(int i, PasswordMetrics passwordMetrics) {
        this.mMetrics = passwordMetrics;
        this.mVersion = i;
    }

    public VersionedPasswordMetrics(LockscreenCredential lockscreenCredential) {
        this(1, PasswordMetrics.computeForCredential(lockscreenCredential));
    }

    public PasswordMetrics getMetrics() {
        return this.mMetrics;
    }

    public byte[] serialize() {
        ByteBuffer allocate = ByteBuffer.allocate(44);
        allocate.putInt(this.mVersion);
        allocate.putInt(this.mMetrics.credType);
        allocate.putInt(this.mMetrics.length);
        allocate.putInt(this.mMetrics.letters);
        allocate.putInt(this.mMetrics.upperCase);
        allocate.putInt(this.mMetrics.lowerCase);
        allocate.putInt(this.mMetrics.numeric);
        allocate.putInt(this.mMetrics.symbols);
        allocate.putInt(this.mMetrics.nonLetter);
        allocate.putInt(this.mMetrics.nonNumeric);
        allocate.putInt(this.mMetrics.seqLength);
        return allocate.array();
    }

    public static VersionedPasswordMetrics deserialize(byte[] bArr) {
        ByteBuffer allocate = ByteBuffer.allocate(bArr.length);
        allocate.put(bArr, 0, bArr.length);
        allocate.flip();
        return new VersionedPasswordMetrics(allocate.getInt(), new PasswordMetrics(allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt(), allocate.getInt()));
    }
}
