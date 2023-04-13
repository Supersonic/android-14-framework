package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricAuthenticator.Identifier;
import android.os.Build;
import android.os.IBinder;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class InternalCleanupClient<S extends BiometricAuthenticator.Identifier, T> extends HalClientMonitor<T> implements EnumerateConsumer, RemovalConsumer, EnrollmentModifier {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final BiometricUtils<S> mBiometricUtils;
    public BaseClientMonitor mCurrentTask;
    public final ClientMonitorCallback mEnumerateCallback;
    public boolean mFavorHalEnrollments;
    public final boolean mHasEnrollmentsBeforeStarting;
    public final ClientMonitorCallback mRemoveCallback;
    public final ArrayList<UserTemplate> mUnknownHALTemplates;

    public abstract InternalEnumerateClient<T> getEnumerateClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, String str, List<S> list, BiometricUtils<S> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext);

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 7;
    }

    public abstract RemovalClient<S, T> getRemovalClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, int i2, String str, BiometricUtils<S> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map);

    public void onAddUnknownTemplate(int i, BiometricAuthenticator.Identifier identifier) {
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    /* loaded from: classes.dex */
    public static final class UserTemplate {
        public final BiometricAuthenticator.Identifier mIdentifier;
        public final int mUserId;

        public UserTemplate(BiometricAuthenticator.Identifier identifier, int i) {
            this.mIdentifier = identifier;
            this.mUserId = i;
        }
    }

    public InternalCleanupClient(Context context, Supplier<T> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, BiometricUtils<S> biometricUtils, Map<Integer, Long> map) {
        super(context, supplier, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mUnknownHALTemplates = new ArrayList<>();
        this.mFavorHalEnrollments = false;
        this.mEnumerateCallback = new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.InternalCleanupClient.1
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                List<BiometricAuthenticator.Identifier> unknownHALTemplates = ((InternalEnumerateClient) InternalCleanupClient.this.mCurrentTask).getUnknownHALTemplates();
                Slog.d("Biometrics/InternalCleanupClient", "Enumerate onClientFinished: " + baseClientMonitor + ", success: " + z);
                if (!unknownHALTemplates.isEmpty()) {
                    Slog.w("Biometrics/InternalCleanupClient", "Adding " + unknownHALTemplates.size() + " templates for deletion");
                }
                for (BiometricAuthenticator.Identifier identifier : unknownHALTemplates) {
                    InternalCleanupClient.this.mUnknownHALTemplates.add(new UserTemplate(identifier, InternalCleanupClient.this.mCurrentTask.getTargetUserId()));
                }
                if (InternalCleanupClient.this.mUnknownHALTemplates.isEmpty()) {
                    return;
                }
                if (InternalCleanupClient.this.mFavorHalEnrollments && Build.isDebuggable()) {
                    try {
                        Iterator it = InternalCleanupClient.this.mUnknownHALTemplates.iterator();
                        while (it.hasNext()) {
                            UserTemplate userTemplate = (UserTemplate) it.next();
                            Slog.i("Biometrics/InternalCleanupClient", "Adding unknown HAL template: " + userTemplate.mIdentifier.getBiometricId());
                            InternalCleanupClient.this.onAddUnknownTemplate(userTemplate.mUserId, userTemplate.mIdentifier);
                        }
                        return;
                    } finally {
                        InternalCleanupClient internalCleanupClient = InternalCleanupClient.this;
                        internalCleanupClient.mCallback.onClientFinished(internalCleanupClient, z);
                    }
                }
                InternalCleanupClient.this.startCleanupUnknownHalTemplates();
            }
        };
        this.mRemoveCallback = new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.InternalCleanupClient.2
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                Slog.d("Biometrics/InternalCleanupClient", "Remove onClientFinished: " + baseClientMonitor + ", success: " + z);
                if (InternalCleanupClient.this.mUnknownHALTemplates.isEmpty()) {
                    InternalCleanupClient internalCleanupClient = InternalCleanupClient.this;
                    internalCleanupClient.mCallback.onClientFinished(internalCleanupClient, z);
                    return;
                }
                InternalCleanupClient.this.startCleanupUnknownHalTemplates();
            }
        };
        this.mBiometricUtils = biometricUtils;
        this.mAuthenticatorIds = map;
        this.mHasEnrollmentsBeforeStarting = !biometricUtils.getBiometricsForUser(context, i).isEmpty();
    }

    public final void startCleanupUnknownHalTemplates() {
        Slog.d("Biometrics/InternalCleanupClient", "startCleanupUnknownHalTemplates, size: " + this.mUnknownHALTemplates.size());
        UserTemplate userTemplate = this.mUnknownHALTemplates.get(0);
        this.mUnknownHALTemplates.remove(userTemplate);
        this.mCurrentTask = getRemovalClient(getContext(), this.mLazyDaemon, getToken(), userTemplate.mIdentifier.getBiometricId(), userTemplate.mUserId, getContext().getPackageName(), this.mBiometricUtils, getSensorId(), getLogger(), getBiometricContext(), this.mAuthenticatorIds);
        getLogger().logUnknownEnrollmentInHal();
        this.mCurrentTask.start(this.mRemoveCallback);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        List<S> biometricsForUser = this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId());
        this.mCurrentTask = getEnumerateClient(getContext(), this.mLazyDaemon, getToken(), getTargetUserId(), getOwnerString(), biometricsForUser, this.mBiometricUtils, getSensorId(), getLogger(), getBiometricContext());
        Slog.d("Biometrics/InternalCleanupClient", "Starting enumerate: " + this.mCurrentTask + " enrolledList size:" + biometricsForUser.size());
        this.mCurrentTask.start(this.mEnumerateCallback);
    }

    @Override // com.android.server.biometrics.sensors.RemovalConsumer
    public void onRemoved(BiometricAuthenticator.Identifier identifier, int i) {
        BaseClientMonitor baseClientMonitor = this.mCurrentTask;
        if (!(baseClientMonitor instanceof RemovalClient)) {
            Slog.e("Biometrics/InternalCleanupClient", "onRemoved received during client: " + this.mCurrentTask.getClass().getSimpleName());
            return;
        }
        ((RemovalClient) baseClientMonitor).onRemoved(identifier, i);
    }

    @Override // com.android.server.biometrics.sensors.EnrollmentModifier
    public boolean hasEnrollmentStateChanged() {
        return (this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).isEmpty() ^ true) != this.mHasEnrollmentsBeforeStarting;
    }

    @Override // com.android.server.biometrics.sensors.EnrollmentModifier
    public boolean hasEnrollments() {
        return !this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).isEmpty();
    }

    @Override // com.android.server.biometrics.sensors.EnumerateConsumer
    public void onEnumerationResult(BiometricAuthenticator.Identifier identifier, int i) {
        if (!(this.mCurrentTask instanceof InternalEnumerateClient)) {
            Slog.e("Biometrics/InternalCleanupClient", "onEnumerationResult received during client: " + this.mCurrentTask.getClass().getSimpleName());
            return;
        }
        Slog.d("Biometrics/InternalCleanupClient", "onEnumerated, remaining: " + i);
        ((EnumerateConsumer) this.mCurrentTask).onEnumerationResult(identifier, i);
    }

    public void setFavorHalEnrollments() {
        this.mFavorHalEnrollments = true;
    }

    @VisibleForTesting
    public InternalEnumerateClient<T> getCurrentEnumerateClient() {
        return (InternalEnumerateClient) this.mCurrentTask;
    }

    @VisibleForTesting
    public RemovalClient<S, T> getCurrentRemoveClient() {
        return (RemovalClient) this.mCurrentTask;
    }

    @VisibleForTesting
    public ArrayList<UserTemplate> getUnknownHALTemplates() {
        return this.mUnknownHALTemplates;
    }
}
