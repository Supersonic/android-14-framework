package android.security;

import android.content.Context;
import android.p008os.RemoteException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
/* loaded from: classes3.dex */
public final class FileIntegrityManager {
    private final Context mContext;
    private final IFileIntegrityService mService;

    public FileIntegrityManager(Context context, IFileIntegrityService service) {
        this.mContext = context;
        this.mService = service;
    }

    public boolean isApkVeritySupported() {
        try {
            return this.mService.isApkVeritySupported();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAppSourceCertificateTrusted(X509Certificate certificate) throws CertificateEncodingException {
        try {
            return this.mService.isAppSourceCertificateTrusted(certificate.getEncoded(), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
