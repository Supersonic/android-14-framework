package com.android.server.biometrics.sensors.face;

import android.content.Context;
import android.hardware.face.Face;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.biometrics.sensors.BiometricUtils;
import java.util.List;
/* loaded from: classes.dex */
public class FaceUtils implements BiometricUtils<Face> {
    public static final Object sInstanceLock = new Object();
    public static SparseArray<FaceUtils> sInstances;
    public final String mFileName;
    @GuardedBy({"this"})
    public final SparseArray<FaceUserState> mUserStates = new SparseArray<>();

    public static FaceUtils getInstance(int i) {
        return getInstance(i, null);
    }

    public static FaceUtils getInstance(int i, String str) {
        FaceUtils faceUtils;
        synchronized (sInstanceLock) {
            if (sInstances == null) {
                sInstances = new SparseArray<>();
            }
            if (sInstances.get(i) == null) {
                if (str == null) {
                    str = "settings_face_" + i + ".xml";
                }
                sInstances.put(i, new FaceUtils(str));
            }
            faceUtils = sInstances.get(i);
        }
        return faceUtils;
    }

    public static FaceUtils getLegacyInstance(int i) {
        return getInstance(i, "settings_face.xml");
    }

    public FaceUtils(String str) {
        this.mFileName = str;
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public List<Face> getBiometricsForUser(Context context, int i) {
        return getStateForUser(context, i).getBiometrics();
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void addBiometricForUser(Context context, int i, Face face) {
        getStateForUser(context, i).addBiometric(face);
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void removeBiometricForUser(Context context, int i, int i2) {
        getStateForUser(context, i).removeBiometric(i2);
    }

    public CharSequence getUniqueName(Context context, int i) {
        return getStateForUser(context, i).getUniqueName();
    }

    @Override // com.android.server.biometrics.sensors.BiometricUtils
    public void setInvalidationInProgress(Context context, int i, boolean z) {
        getStateForUser(context, i).setInvalidationInProgress(z);
    }

    public boolean isInvalidationInProgress(Context context, int i) {
        return getStateForUser(context, i).isInvalidationInProgress();
    }

    public final FaceUserState getStateForUser(Context context, int i) {
        FaceUserState faceUserState;
        synchronized (this) {
            faceUserState = this.mUserStates.get(i);
            if (faceUserState == null) {
                faceUserState = new FaceUserState(context, i, this.mFileName);
                this.mUserStates.put(i, faceUserState);
            }
        }
        return faceUserState;
    }
}
