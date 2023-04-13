package com.android.internal.org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class PrivateKeyAnnotator {
    public static AnnotatedPrivateKey annotate(PrivateKey privKey, String label) {
        return new AnnotatedPrivateKey(privKey, label);
    }

    public static AnnotatedPrivateKey annotate(PrivateKey privKey, Map<String, Object> annotations) {
        Map savedAnnotations = new HashMap(annotations);
        return new AnnotatedPrivateKey(privKey, Collections.unmodifiableMap(savedAnnotations));
    }
}
