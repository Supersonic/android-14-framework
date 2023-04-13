package com.android.server.security;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelDuration;
import android.os.RemoteException;
import android.security.attestationverification.AttestationProfile;
import android.security.attestationverification.IAttestationVerificationManagerService;
import android.security.attestationverification.IVerificationResult;
import android.security.attestationverification.VerificationToken;
import android.util.ExceptionUtils;
import android.util.Slog;
import com.android.internal.infra.AndroidFuture;
import com.android.server.SystemService;
/* loaded from: classes2.dex */
public class AttestationVerificationManagerService extends SystemService {
    public final AttestationVerificationPeerDeviceVerifier mPeerDeviceVerifier;
    public final IBinder mService;

    public AttestationVerificationManagerService(Context context) throws Exception {
        super(context);
        this.mService = new IAttestationVerificationManagerService.Stub() { // from class: com.android.server.security.AttestationVerificationManagerService.1
            public void verifyAttestation(AttestationProfile attestationProfile, int i, Bundle bundle, byte[] bArr, AndroidFuture androidFuture) throws RemoteException {
                enforceUsePermission();
                try {
                    Slog.d("AVF", "verifyAttestation");
                    AttestationVerificationManagerService.this.verifyAttestationForAllVerifiers(attestationProfile, i, bundle, bArr, androidFuture);
                } catch (Throwable th) {
                    Slog.e("AVF", "failed to verify attestation", th);
                    throw ExceptionUtils.propagate(th, RemoteException.class);
                }
            }

            public void verifyToken(VerificationToken verificationToken, ParcelDuration parcelDuration, AndroidFuture androidFuture) throws RemoteException {
                enforceUsePermission();
                androidFuture.complete(0);
            }

            public final void enforceUsePermission() {
                AttestationVerificationManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.USE_ATTESTATION_VERIFICATION_SERVICE", null);
            }
        };
        this.mPeerDeviceVerifier = new AttestationVerificationPeerDeviceVerifier(context);
    }

    public final void verifyAttestationForAllVerifiers(AttestationProfile attestationProfile, int i, Bundle bundle, byte[] bArr, AndroidFuture<IVerificationResult> androidFuture) {
        IVerificationResult iVerificationResult = new IVerificationResult();
        iVerificationResult.token = null;
        int attestationProfileId = attestationProfile.getAttestationProfileId();
        if (attestationProfileId == 2) {
            Slog.d("AVF", "Verifying Self Trusted profile.");
            try {
                iVerificationResult.resultCode = AttestationVerificationSelfTrustedVerifierForTesting.getInstance().verifyAttestation(i, bundle, bArr);
            } catch (Throwable unused) {
                iVerificationResult.resultCode = 2;
            }
        } else if (attestationProfileId == 3) {
            Slog.d("AVF", "Verifying Peer Device profile.");
            iVerificationResult.resultCode = this.mPeerDeviceVerifier.verifyAttestation(i, bundle, bArr);
        } else {
            Slog.d("AVF", "No profile found, defaulting.");
            iVerificationResult.resultCode = 0;
        }
        androidFuture.complete(iVerificationResult);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        Slog.d("AVF", "Started");
        publishBinderService("attestation_verification", this.mService);
    }
}
