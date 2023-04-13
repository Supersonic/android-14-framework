package android.util.apk;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
/* loaded from: classes3.dex */
class VerbatimX509Certificate extends WrappedX509Certificate {
    private final byte[] mEncodedVerbatim;
    private int mHash;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VerbatimX509Certificate(X509Certificate wrapped, byte[] encodedVerbatim) {
        super(wrapped);
        this.mHash = -1;
        this.mEncodedVerbatim = encodedVerbatim;
    }

    @Override // android.util.apk.WrappedX509Certificate, java.security.cert.Certificate
    public byte[] getEncoded() throws CertificateEncodingException {
        return this.mEncodedVerbatim;
    }

    @Override // java.security.cert.Certificate
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VerbatimX509Certificate) {
            try {
                byte[] a = getEncoded();
                byte[] b = ((VerbatimX509Certificate) o).getEncoded();
                return Arrays.equals(a, b);
            } catch (CertificateEncodingException e) {
                return false;
            }
        }
        return false;
    }

    @Override // java.security.cert.Certificate
    public int hashCode() {
        if (this.mHash == -1) {
            try {
                this.mHash = Arrays.hashCode(getEncoded());
            } catch (CertificateEncodingException e) {
                this.mHash = 0;
            }
        }
        return this.mHash;
    }
}
