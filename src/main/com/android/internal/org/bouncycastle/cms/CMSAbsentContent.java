package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class CMSAbsentContent implements CMSTypedData, CMSReadable {
    private final ASN1ObjectIdentifier type;

    public CMSAbsentContent() {
        this(CMSObjectIdentifiers.data);
    }

    public CMSAbsentContent(ASN1ObjectIdentifier type) {
        this.type = type;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSReadable
    public InputStream getInputStream() {
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public Object getContent() {
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
