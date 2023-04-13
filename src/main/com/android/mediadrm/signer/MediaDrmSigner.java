package com.android.mediadrm.signer;

import android.media.DeniedByServerException;
import android.media.MediaDrm;
/* loaded from: classes.dex */
public final class MediaDrmSigner {
    public static final int CERTIFICATE_TYPE_X509 = 1;

    private MediaDrmSigner() {
    }

    /* loaded from: classes.dex */
    public static final class CertificateRequest {
        private final MediaDrm.CertificateRequest mCertRequest;

        CertificateRequest(MediaDrm.CertificateRequest certRequest) {
            this.mCertRequest = certRequest;
        }

        public byte[] getData() {
            return this.mCertRequest.getData();
        }

        public String getDefaultUrl() {
            return this.mCertRequest.getDefaultUrl();
        }
    }

    /* loaded from: classes.dex */
    public static final class Certificate {
        private final MediaDrm.Certificate mCertificate;

        Certificate(MediaDrm.Certificate certificate) {
            this.mCertificate = certificate;
        }

        public byte[] getWrappedPrivateKey() {
            return this.mCertificate.getWrappedPrivateKey();
        }

        public byte[] getContent() {
            return this.mCertificate.getContent();
        }
    }

    public static CertificateRequest getCertificateRequest(MediaDrm drm, int certType, String certAuthority) {
        return new CertificateRequest(drm.getCertificateRequest(certType, certAuthority));
    }

    public static Certificate provideCertificateResponse(MediaDrm drm, byte[] response) throws DeniedByServerException {
        return new Certificate(drm.provideCertificateResponse(response));
    }

    public static byte[] signRSA(MediaDrm drm, byte[] sessionId, String algorithm, byte[] wrappedKey, byte[] message) {
        return drm.signRSA(sessionId, algorithm, wrappedKey, message);
    }
}
