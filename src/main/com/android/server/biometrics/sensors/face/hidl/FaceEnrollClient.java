package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.face.Face;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.view.Surface;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ClientMonitorCompositeCallback;
import com.android.server.biometrics.sensors.EnrollClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceEnrollClient extends EnrollClient<IBiometricsFace> {
    public final int[] mDisabledFeatures;
    public final int[] mEnrollIgnoreList;
    public final int[] mEnrollIgnoreListVendor;

    public FaceEnrollClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, byte[] bArr, String str, long j, BiometricUtils<Face> biometricUtils, int[] iArr, int i2, Surface surface, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, bArr, str, biometricUtils, i2, i3, false, biometricLogger, biometricContext);
        setRequestId(j);
        this.mDisabledFeatures = Arrays.copyOf(iArr, iArr.length);
        this.mEnrollIgnoreList = getContext().getResources().getIntArray(17236066);
        this.mEnrollIgnoreListVendor = getContext().getResources().getIntArray(17236069);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public ClientMonitorCallback wrapCallbackForStart(ClientMonitorCallback clientMonitorCallback) {
        return new ClientMonitorCompositeCallback(getLogger().getAmbientLightProbe(true), clientMonitorCallback);
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient
    public boolean hasReachedEnrollmentLimit() {
        if (this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).size() >= getContext().getResources().getInteger(17694844)) {
            Slog.w("FaceEnrollClient", "Too many faces registered, user: " + getTargetUserId());
            return true;
        }
        return false;
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void onAcquired(int i, int i2) {
        boolean listContains;
        if (i == 22) {
            listContains = Utils.listContains(this.mEnrollIgnoreListVendor, i2);
        } else {
            listContains = Utils.listContains(this.mEnrollIgnoreList, i);
        }
        onAcquiredInternal(i, i2, !listContains);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        ArrayList<Byte> arrayList = new ArrayList<>();
        for (byte b : this.mHardwareAuthToken) {
            arrayList.add(Byte.valueOf(b));
        }
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        for (int i : this.mDisabledFeatures) {
            arrayList2.add(Integer.valueOf(i));
        }
        try {
            if (getFreshDaemon().enroll(arrayList, this.mTimeoutSec, arrayList2) != 0) {
                onError(2, 0);
                this.mCallback.onClientFinished(this, false);
            }
        } catch (RemoteException e) {
            Slog.e("FaceEnrollClient", "Remote exception when requesting enroll", e);
            onError(2, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        try {
            getFreshDaemon().cancel();
        } catch (RemoteException e) {
            Slog.e("FaceEnrollClient", "Remote exception when requesting cancel", e);
            onError(1, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
