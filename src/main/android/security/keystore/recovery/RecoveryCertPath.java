package android.security.keystore.recovery;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.io.ByteArrayInputStream;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class RecoveryCertPath implements Parcelable {
    private static final String CERT_PATH_ENCODING = "PkiPath";
    public static final Parcelable.Creator<RecoveryCertPath> CREATOR = new Parcelable.Creator<RecoveryCertPath>() { // from class: android.security.keystore.recovery.RecoveryCertPath.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecoveryCertPath createFromParcel(Parcel in) {
            return new RecoveryCertPath(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecoveryCertPath[] newArray(int length) {
            return new RecoveryCertPath[length];
        }
    };
    private final byte[] mEncodedCertPath;

    public static RecoveryCertPath createRecoveryCertPath(CertPath certPath) throws CertificateException {
        try {
            return new RecoveryCertPath(encodeCertPath(certPath));
        } catch (CertificateEncodingException e) {
            throw new CertificateException("Failed to encode the given CertPath", e);
        }
    }

    public CertPath getCertPath() throws CertificateException {
        return decodeCertPath(this.mEncodedCertPath);
    }

    private RecoveryCertPath(byte[] encodedCertPath) {
        this.mEncodedCertPath = (byte[]) Objects.requireNonNull(encodedCertPath);
    }

    private RecoveryCertPath(Parcel in) {
        this.mEncodedCertPath = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeByteArray(this.mEncodedCertPath);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static byte[] encodeCertPath(CertPath certPath) throws CertificateEncodingException {
        Objects.requireNonNull(certPath);
        return certPath.getEncoded(CERT_PATH_ENCODING);
    }

    private static CertPath decodeCertPath(byte[] bytes) throws CertificateException {
        Objects.requireNonNull(bytes);
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return certFactory.generateCertPath(new ByteArrayInputStream(bytes), CERT_PATH_ENCODING);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
