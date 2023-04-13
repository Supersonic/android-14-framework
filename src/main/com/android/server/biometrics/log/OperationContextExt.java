package com.android.server.biometrics.log;

import android.hardware.biometrics.common.AuthenticateReason;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.biometrics.common.WakeReason;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
/* loaded from: classes.dex */
public class OperationContextExt {
    public final OperationContext mAidlContext;
    public int mDockState;
    public int mFoldState;
    public boolean mIsDisplayOn;
    public int mOrientation;
    public BiometricContextSessionInfo mSessionInfo;

    @AuthenticateReason.Fingerprint
    public final int getAuthReason(FingerprintAuthenticateOptions fingerprintAuthenticateOptions) {
        return 0;
    }

    @WakeReason
    public final int getWakeReason(FingerprintAuthenticateOptions fingerprintAuthenticateOptions) {
        return 0;
    }

    public OperationContextExt() {
        this(new OperationContext());
    }

    public OperationContextExt(OperationContext operationContext) {
        this.mIsDisplayOn = false;
        this.mDockState = 0;
        this.mOrientation = 0;
        this.mFoldState = 0;
        this.mAidlContext = operationContext;
    }

    public OperationContext toAidlContext() {
        return this.mAidlContext;
    }

    public OperationContext toAidlContext(FaceAuthenticateOptions faceAuthenticateOptions) {
        this.mAidlContext.authenticateReason = AuthenticateReason.faceAuthenticateReason(getAuthReason(faceAuthenticateOptions));
        this.mAidlContext.wakeReason = getWakeReason(faceAuthenticateOptions);
        return this.mAidlContext;
    }

    public OperationContext toAidlContext(FingerprintAuthenticateOptions fingerprintAuthenticateOptions) {
        this.mAidlContext.authenticateReason = AuthenticateReason.fingerprintAuthenticateReason(getAuthReason(fingerprintAuthenticateOptions));
        this.mAidlContext.wakeReason = getWakeReason(fingerprintAuthenticateOptions);
        return this.mAidlContext;
    }

    @AuthenticateReason.Face
    public final int getAuthReason(FaceAuthenticateOptions faceAuthenticateOptions) {
        switch (faceAuthenticateOptions.getAuthenticateReason()) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            default:
                return 0;
        }
    }

    @WakeReason
    public final int getWakeReason(FaceAuthenticateOptions faceAuthenticateOptions) {
        int wakeReason = faceAuthenticateOptions.getWakeReason();
        if (wakeReason != 1) {
            if (wakeReason != 4) {
                if (wakeReason != 10) {
                    if (wakeReason != 6) {
                        if (wakeReason != 7) {
                            switch (wakeReason) {
                                case 15:
                                    return 7;
                                case 16:
                                    return 8;
                                case 17:
                                    return 9;
                                default:
                                    return 0;
                            }
                        }
                        return 4;
                    }
                    return 3;
                }
                return 6;
            }
            return 2;
        }
        return 1;
    }

    public int getId() {
        return this.mAidlContext.id;
    }

    public int getOrderAndIncrement() {
        BiometricContextSessionInfo biometricContextSessionInfo = this.mSessionInfo;
        if (biometricContextSessionInfo != null) {
            return biometricContextSessionInfo.getOrderAndIncrement();
        }
        return -1;
    }

    public byte getReason() {
        return this.mAidlContext.reason;
    }

    public boolean isDisplayOn() {
        return this.mIsDisplayOn;
    }

    public boolean isAod() {
        return this.mAidlContext.isAod;
    }

    public boolean isCrypto() {
        return this.mAidlContext.isCrypto;
    }

    public int getDockState() {
        return this.mDockState;
    }

    public int getFoldState() {
        return this.mFoldState;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public OperationContextExt update(BiometricContext biometricContext) {
        this.mAidlContext.isAod = biometricContext.isAod();
        setFirstSessionId(biometricContext);
        this.mIsDisplayOn = biometricContext.isDisplayOn();
        this.mDockState = biometricContext.getDockedState();
        this.mFoldState = biometricContext.getFoldState();
        this.mOrientation = biometricContext.getCurrentRotation();
        return this;
    }

    public final void setFirstSessionId(BiometricContext biometricContext) {
        BiometricContextSessionInfo keyguardEntrySessionInfo = biometricContext.getKeyguardEntrySessionInfo();
        this.mSessionInfo = keyguardEntrySessionInfo;
        if (keyguardEntrySessionInfo != null) {
            this.mAidlContext.id = keyguardEntrySessionInfo.getId();
            this.mAidlContext.reason = (byte) 2;
            return;
        }
        BiometricContextSessionInfo biometricPromptSessionInfo = biometricContext.getBiometricPromptSessionInfo();
        this.mSessionInfo = biometricPromptSessionInfo;
        if (biometricPromptSessionInfo != null) {
            this.mAidlContext.id = biometricPromptSessionInfo.getId();
            this.mAidlContext.reason = (byte) 1;
            return;
        }
        OperationContext operationContext = this.mAidlContext;
        operationContext.id = 0;
        operationContext.reason = (byte) 0;
    }
}
