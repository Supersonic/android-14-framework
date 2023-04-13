package android.security.attestationverification;

import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.ParcelDuration;
import android.p008os.RemoteException;
import android.service.timezone.TimeZoneProviderService;
import android.util.Log;
import com.android.internal.infra.AndroidFuture;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class AttestationVerificationManager {
    private static final Duration MAX_TOKEN_AGE = Duration.ofHours(1);
    public static final String PARAM_CHALLENGE = "localbinding.challenge";
    public static final String PARAM_ID = "localbinding.id";
    public static final String PARAM_PUBLIC_KEY = "localbinding.public_key";
    public static final int PROFILE_APP_DEFINED = 1;
    public static final int PROFILE_PEER_DEVICE = 3;
    public static final int PROFILE_SELF_TRUSTED = 2;
    public static final int PROFILE_UNKNOWN = 0;
    public static final int RESULT_FAILURE = 2;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_UNKNOWN = 0;
    private static final String TAG = "AVF";
    public static final int TYPE_APP_DEFINED = 1;
    public static final int TYPE_CHALLENGE = 3;
    public static final int TYPE_PUBLIC_KEY = 2;
    public static final int TYPE_UNKNOWN = 0;
    private final Context mContext;
    private final IAttestationVerificationManagerService mService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AttestationProfileId {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LocalBindingType {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VerificationResult {
    }

    public void verifyAttestation(AttestationProfile profile, int localBindingType, Bundle requirements, byte[] attestation, final Executor executor, final BiConsumer<Integer, VerificationToken> callback) {
        try {
            AndroidFuture<IVerificationResult> resultCallback = new AndroidFuture<>();
            resultCallback.thenAccept(new Consumer() { // from class: android.security.attestationverification.AttestationVerificationManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AttestationVerificationManager.lambda$verifyAttestation$1(executor, callback, (IVerificationResult) obj);
                }
            });
            this.mService.verifyAttestation(profile, localBindingType, requirements, attestation, resultCallback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$verifyAttestation$1(Executor executor, final BiConsumer callback, final IVerificationResult result) {
        Log.m112d(TAG, "verifyAttestation result: " + result.resultCode + " / " + result.token);
        executor.execute(new Runnable() { // from class: android.security.attestationverification.AttestationVerificationManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                callback.accept(Integer.valueOf(r1.resultCode), result.token);
            }
        });
    }

    public int verifyToken(AttestationProfile profile, int localBindingType, Bundle requirements, VerificationToken token, Duration maximumAge) {
        Duration usedMaximumAge;
        if (maximumAge == null) {
            usedMaximumAge = MAX_TOKEN_AGE;
        } else {
            Duration usedMaximumAge2 = MAX_TOKEN_AGE;
            if (maximumAge.compareTo(usedMaximumAge2) > 0) {
                throw new IllegalArgumentException("maximumAge cannot be greater than " + usedMaximumAge2 + "; was " + maximumAge);
            }
            usedMaximumAge = maximumAge;
        }
        try {
            AndroidFuture<Integer> resultCallback = new AndroidFuture<>();
            resultCallback.orTimeout(5L, TimeUnit.SECONDS);
            this.mService.verifyToken(token, new ParcelDuration(usedMaximumAge), resultCallback);
            return resultCallback.get().intValue();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (Throwable t) {
            throw new RuntimeException("Error verifying token.", t);
        }
    }

    public AttestationVerificationManager(Context context, IAttestationVerificationManagerService service) {
        this.mContext = context;
        this.mService = service;
    }

    public static String localBindingTypeToString(int localBindingType) {
        String text;
        switch (localBindingType) {
            case 0:
                text = "UNKNOWN";
                break;
            case 1:
                text = "APP_DEFINED";
                break;
            case 2:
                text = "PUBLIC_KEY";
                break;
            case 3:
                text = "CHALLENGE";
                break;
            default:
                return Integer.toString(localBindingType);
        }
        return text + NavigationBarInflaterView.KEY_CODE_START + localBindingType + NavigationBarInflaterView.KEY_CODE_END;
    }

    public static String verificationResultCodeToString(int resultCode) {
        String text;
        switch (resultCode) {
            case 0:
                text = "UNKNOWN";
                break;
            case 1:
                text = TimeZoneProviderService.TEST_COMMAND_RESULT_SUCCESS_KEY;
                break;
            case 2:
                text = "FAILURE";
                break;
            default:
                return Integer.toString(resultCode);
        }
        return text + NavigationBarInflaterView.KEY_CODE_START + resultCode + NavigationBarInflaterView.KEY_CODE_END;
    }
}
