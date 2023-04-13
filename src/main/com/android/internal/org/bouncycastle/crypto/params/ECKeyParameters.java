package com.android.internal.org.bouncycastle.crypto.params;
/* loaded from: classes4.dex */
public class ECKeyParameters extends AsymmetricKeyParameter {
    private final ECDomainParameters parameters;

    /* JADX INFO: Access modifiers changed from: protected */
    public ECKeyParameters(boolean isPrivate, ECDomainParameters parameters) {
        super(isPrivate);
        if (parameters == null) {
            throw new NullPointerException("'parameters' cannot be null");
        }
        this.parameters = parameters;
    }

    public ECDomainParameters getParameters() {
        return this.parameters;
    }
}
