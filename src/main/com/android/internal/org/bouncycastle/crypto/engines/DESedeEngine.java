package com.android.internal.org.bouncycastle.crypto.engines;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
/* loaded from: classes4.dex */
public class DESedeEngine extends DESEngine {
    protected static final int BLOCK_SIZE = 8;
    private boolean forEncryption;
    private int[] workingKey1 = null;
    private int[] workingKey2 = null;
    private int[] workingKey3 = null;

    @Override // com.android.internal.org.bouncycastle.crypto.engines.DESEngine, com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DESede init - " + params.getClass().getName());
        }
        byte[] keyMaster = ((KeyParameter) params).getKey();
        if (keyMaster.length != 24 && keyMaster.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
        }
        this.forEncryption = encrypting;
        byte[] key1 = new byte[8];
        System.arraycopy(keyMaster, 0, key1, 0, key1.length);
        this.workingKey1 = generateWorkingKey(encrypting, key1);
        byte[] key2 = new byte[8];
        System.arraycopy(keyMaster, 8, key2, 0, key2.length);
        this.workingKey2 = generateWorkingKey(!encrypting, key2);
        if (keyMaster.length == 24) {
            byte[] key3 = new byte[8];
            System.arraycopy(keyMaster, 16, key3, 0, key3.length);
            this.workingKey3 = generateWorkingKey(encrypting, key3);
            return;
        }
        this.workingKey3 = this.workingKey1;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.engines.DESEngine, com.android.internal.org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return KeyProperties.KEY_ALGORITHM_3DES;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.engines.DESEngine, com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.engines.DESEngine, com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int[] iArr = this.workingKey1;
        if (iArr == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        byte[] temp = new byte[8];
        if (this.forEncryption) {
            desFunc(iArr, in, inOff, temp, 0);
            desFunc(this.workingKey2, temp, 0, temp, 0);
            desFunc(this.workingKey3, temp, 0, out, outOff);
        } else {
            desFunc(this.workingKey3, in, inOff, temp, 0);
            desFunc(this.workingKey2, temp, 0, temp, 0);
            desFunc(this.workingKey1, temp, 0, out, outOff);
        }
        return 8;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.engines.DESEngine, com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }
}
