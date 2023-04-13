package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.operator.InputDecryptor;
import com.android.internal.org.bouncycastle.operator.MacCalculator;
import com.android.internal.org.bouncycastle.util.p027io.TeeInputStream;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class RecipientOperator {
    private final Object operator;

    public RecipientOperator(InputDecryptor decryptor) {
        this.operator = decryptor;
    }

    public RecipientOperator(MacCalculator macCalculator) {
        this.operator = macCalculator;
    }

    public InputStream getInputStream(InputStream dataIn) {
        Object obj = this.operator;
        if (obj instanceof InputDecryptor) {
            return ((InputDecryptor) obj).getInputStream(dataIn);
        }
        return new TeeInputStream(dataIn, ((MacCalculator) this.operator).getOutputStream());
    }

    public boolean isMacBased() {
        return this.operator instanceof MacCalculator;
    }

    public byte[] getMac() {
        return ((MacCalculator) this.operator).getMac();
    }
}
