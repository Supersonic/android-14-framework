package com.android.internal.org.bouncycastle.util.p027io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.internal.org.bouncycastle.util.io.pem.PemObject */
/* loaded from: classes4.dex */
public class PemObject implements PemObjectGenerator {
    private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
    private byte[] content;
    private List headers;
    private String type;

    public PemObject(String type, byte[] content) {
        this(type, EMPTY_LIST, content);
    }

    public PemObject(String type, List headers, byte[] content) {
        this.type = type;
        this.headers = Collections.unmodifiableList(headers);
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public List getHeaders() {
        return this.headers;
    }

    public byte[] getContent() {
        return this.content;
    }

    @Override // com.android.internal.org.bouncycastle.util.p027io.pem.PemObjectGenerator
    public PemObject generate() throws PemGenerationException {
        return this;
    }
}
