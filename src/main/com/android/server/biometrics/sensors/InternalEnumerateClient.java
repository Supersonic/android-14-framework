package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.os.IBinder;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class InternalEnumerateClient<T> extends HalClientMonitor<T> implements EnumerateConsumer {
    public List<? extends BiometricAuthenticator.Identifier> mEnrolledList;
    public List<BiometricAuthenticator.Identifier> mUnknownHALTemplates;
    public BiometricUtils mUtils;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 6;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public InternalEnumerateClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, String str, List<? extends BiometricAuthenticator.Identifier> list, BiometricUtils biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mUnknownHALTemplates = new ArrayList();
        this.mEnrolledList = list;
        this.mUtils = biometricUtils;
    }

    @Override // com.android.server.biometrics.sensors.EnumerateConsumer
    public void onEnumerationResult(BiometricAuthenticator.Identifier identifier, int i) {
        handleEnumeratedTemplate(identifier);
        if (i == 0) {
            doTemplateCleanup();
            this.mCallback.onClientFinished(this, true);
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    public final void handleEnumeratedTemplate(BiometricAuthenticator.Identifier identifier) {
        if (identifier == null) {
            Slog.d("Biometrics/InternalEnumerateClient", "Null identifier");
            return;
        }
        Slog.v("Biometrics/InternalEnumerateClient", "handleEnumeratedTemplate: " + identifier.getBiometricId());
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= this.mEnrolledList.size()) {
                break;
            } else if (this.mEnrolledList.get(i).getBiometricId() == identifier.getBiometricId()) {
                this.mEnrolledList.remove(i);
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (!z && identifier.getBiometricId() != 0) {
            this.mUnknownHALTemplates.add(identifier);
        }
        Slog.v("Biometrics/InternalEnumerateClient", "Matched: " + z);
    }

    public final void doTemplateCleanup() {
        if (this.mEnrolledList == null) {
            Slog.d("Biometrics/InternalEnumerateClient", "Null enrolledList");
            return;
        }
        for (int i = 0; i < this.mEnrolledList.size(); i++) {
            BiometricAuthenticator.Identifier identifier = this.mEnrolledList.get(i);
            Slog.e("Biometrics/InternalEnumerateClient", "doTemplateCleanup(): Removing dangling template from framework: " + identifier.getBiometricId() + " " + ((Object) identifier.getName()));
            this.mUtils.removeBiometricForUser(getContext(), getTargetUserId(), identifier.getBiometricId());
            getLogger().logUnknownEnrollmentInFramework();
        }
        this.mEnrolledList.clear();
    }

    public List<BiometricAuthenticator.Identifier> getUnknownHALTemplates() {
        return this.mUnknownHALTemplates;
    }
}
