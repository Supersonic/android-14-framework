package com.android.internal.org.bouncycastle.crypto.params;
/* loaded from: classes4.dex */
public class DHKeyParameters extends AsymmetricKeyParameter {
    private DHParameters params;

    /* JADX INFO: Access modifiers changed from: protected */
    public DHKeyParameters(boolean isPrivate, DHParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DHKeyParameters) {
            DHKeyParameters dhKey = (DHKeyParameters) obj;
            DHParameters dHParameters = this.params;
            if (dHParameters == null) {
                return dhKey.getParameters() == null;
            }
            return dHParameters.equals(dhKey.getParameters());
        }
        return false;
    }

    public int hashCode() {
        int code = !isPrivate();
        DHParameters dHParameters = this.params;
        if (dHParameters != null) {
            return code ^ dHParameters.hashCode();
        }
        return code;
    }
}
