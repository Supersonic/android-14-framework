package com.android.internal.org.bouncycastle.crypto;

import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
/* loaded from: classes4.dex */
public interface StagedAgreement extends BasicAgreement {
    AsymmetricKeyParameter calculateStage(CipherParameters cipherParameters);
}
