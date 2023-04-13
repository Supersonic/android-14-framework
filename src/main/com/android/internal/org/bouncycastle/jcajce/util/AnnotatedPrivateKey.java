package com.android.internal.org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class AnnotatedPrivateKey implements PrivateKey {
    public static final String LABEL = "label";
    private final Map<String, Object> annotations;
    private final PrivateKey key;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotatedPrivateKey(PrivateKey key, String label) {
        this.key = key;
        this.annotations = Collections.singletonMap("label", label);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotatedPrivateKey(PrivateKey key, Map<String, Object> annotations) {
        this.key = key;
        this.annotations = annotations;
    }

    public PrivateKey getKey() {
        return this.key;
    }

    public Map<String, Object> getAnnotations() {
        return this.annotations;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.key.getAlgorithm();
    }

    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }

    public AnnotatedPrivateKey addAnnotation(String name, Object annotation) {
        Map<String, Object> newAnnotations = new HashMap<>(this.annotations);
        newAnnotations.put(name, annotation);
        return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(newAnnotations));
    }

    public AnnotatedPrivateKey removeAnnotation(String name) {
        Map<String, Object> newAnnotations = new HashMap<>(this.annotations);
        newAnnotations.remove(name);
        return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(newAnnotations));
    }

    @Override // java.security.Key
    public String getFormat() {
        return this.key.getFormat();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return this.key.getEncoded();
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof AnnotatedPrivateKey) {
            return this.key.equals(((AnnotatedPrivateKey) o).key);
        }
        return this.key.equals(o);
    }

    public String toString() {
        if (this.annotations.containsKey("label")) {
            return this.annotations.get("label").toString();
        }
        return this.key.toString();
    }
}
