package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class BERGenerator extends ASN1Generator {
    private boolean _isExplicit;
    private int _tagNo;
    private boolean _tagged;

    /* JADX INFO: Access modifiers changed from: protected */
    public BERGenerator(OutputStream out) {
        super(out);
        this._tagged = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BERGenerator(OutputStream out, int tagNo, boolean isExplicit) {
        super(out);
        this._tagged = false;
        this._tagged = true;
        this._isExplicit = isExplicit;
        this._tagNo = tagNo;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Generator
    public OutputStream getRawOutputStream() {
        return this._out;
    }

    private void writeHdr(int tag) throws IOException {
        this._out.write(tag);
        this._out.write(128);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeBERHeader(int tag) throws IOException {
        if (this._tagged) {
            int tagNum = this._tagNo | 128;
            if (this._isExplicit) {
                writeHdr(tagNum | 32);
                writeHdr(tag);
                return;
            } else if ((tag & 32) != 0) {
                writeHdr(tagNum | 32);
                return;
            } else {
                writeHdr(tagNum);
                return;
            }
        }
        writeHdr(tag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeBEREnd() throws IOException {
        this._out.write(0);
        this._out.write(0);
        if (this._tagged && this._isExplicit) {
            this._out.write(0);
            this._out.write(0);
        }
    }
}
