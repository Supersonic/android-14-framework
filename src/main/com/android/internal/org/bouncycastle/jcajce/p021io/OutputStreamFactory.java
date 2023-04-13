package com.android.internal.org.bouncycastle.jcajce.p021io;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.Signature;
import javax.crypto.Mac;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.io.OutputStreamFactory */
/* loaded from: classes4.dex */
public class OutputStreamFactory {
    public static OutputStream createStream(Signature signature) {
        return new SignatureUpdatingOutputStream(signature);
    }

    public static OutputStream createStream(MessageDigest digest) {
        return new DigestUpdatingOutputStream(digest);
    }

    public static OutputStream createStream(Mac mac) {
        return new MacUpdatingOutputStream(mac);
    }
}
