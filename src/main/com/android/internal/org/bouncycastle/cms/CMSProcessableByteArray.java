package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class CMSProcessableByteArray implements CMSTypedData, CMSReadable {
    private final byte[] bytes;
    private final ASN1ObjectIdentifier type;

    public CMSProcessableByteArray(byte[] bytes) {
        this(CMSObjectIdentifiers.data, bytes);
    }

    public CMSProcessableByteArray(ASN1ObjectIdentifier type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSReadable
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
        zOut.write(this.bytes);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public Object getContent() {
        return Arrays.clone(this.bytes);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
