package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.crypto.KeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DESedeParameters;
/* loaded from: classes4.dex */
public class DESedeKeyGenerator extends DESKeyGenerator {
    private static final int MAX_IT = 20;

    @Override // com.android.internal.org.bouncycastle.crypto.generators.DESKeyGenerator, com.android.internal.org.bouncycastle.crypto.CipherKeyGenerator
    public void init(KeyGenerationParameters param) {
        this.random = param.getRandom();
        this.strength = (param.getStrength() + 7) / 8;
        if (this.strength == 0 || this.strength == 21) {
            this.strength = 24;
        } else if (this.strength == 14) {
            this.strength = 16;
        } else if (this.strength != 24 && this.strength != 16) {
            throw new IllegalArgumentException("DESede key must be 192 or 128 bits long.");
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.generators.DESKeyGenerator, com.android.internal.org.bouncycastle.crypto.CipherKeyGenerator
    public byte[] generateKey() {
        byte[] newKey = new byte[this.strength];
        int count = 0;
        while (true) {
            this.random.nextBytes(newKey);
            DESedeParameters.setOddParity(newKey);
            count++;
            if (count >= 20 || (!DESedeParameters.isWeakKey(newKey, 0, newKey.length) && DESedeParameters.isRealEDEKey(newKey, 0))) {
                break;
            }
        }
        if (DESedeParameters.isWeakKey(newKey, 0, newKey.length) || !DESedeParameters.isRealEDEKey(newKey, 0)) {
            throw new IllegalStateException("Unable to generate DES-EDE key");
        }
        return newKey;
    }
}
