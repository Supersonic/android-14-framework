package com.android.server.credentials;
/* loaded from: classes.dex */
public class NonCredentialProviderCallerException extends RuntimeException {
    public NonCredentialProviderCallerException(String str) {
        super(str + " is not an existing Credential Provider.");
    }
}
