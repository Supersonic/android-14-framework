package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
/* loaded from: classes4.dex */
interface SignatureCreator {
    Signature createSignature(String str) throws NoSuchAlgorithmException, NoSuchProviderException;
}
