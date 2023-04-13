package com.android.server.biometrics.sensors.face.aidl;

import android.hardware.biometrics.face.AuthenticationFrame;
import android.hardware.biometrics.face.BaseFrame;
import android.hardware.biometrics.face.Cell;
import android.hardware.biometrics.face.EnrollmentFrame;
import android.hardware.face.FaceAuthenticationFrame;
import android.hardware.face.FaceDataFrame;
import android.hardware.face.FaceEnrollCell;
import android.hardware.face.FaceEnrollFrame;
import android.util.Slog;
/* loaded from: classes.dex */
public final class AidlConversionUtils {
    public static int toFrameworkAcquiredInfo(byte b) {
        switch (b) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
                return 10;
            case 12:
                return 11;
            case 13:
                return 12;
            case 14:
                return 13;
            case 15:
                return 14;
            case 16:
                return 15;
            case 17:
                return 16;
            case 18:
                return 17;
            case 19:
                return 18;
            case 20:
                return 19;
            case 21:
                return 20;
            case 22:
                return 21;
            case 23:
                return 22;
            case 24:
                return 24;
            case 25:
                return 25;
            case 26:
                return 26;
            default:
                return 23;
        }
    }

    public static int toFrameworkEnrollmentStage(int i) {
        switch (i) {
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
            default:
                return 0;
        }
    }

    public static int toFrameworkError(byte b) {
        switch (b) {
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
                return 8;
            case 8:
                return 16;
            default:
                return 17;
        }
    }

    public static FaceAuthenticationFrame toFrameworkAuthenticationFrame(AuthenticationFrame authenticationFrame) {
        return new FaceAuthenticationFrame(toFrameworkBaseFrame(authenticationFrame.data));
    }

    public static FaceEnrollFrame toFrameworkEnrollmentFrame(EnrollmentFrame enrollmentFrame) {
        return new FaceEnrollFrame(toFrameworkCell(enrollmentFrame.cell), toFrameworkEnrollmentStage(enrollmentFrame.stage), toFrameworkBaseFrame(enrollmentFrame.data));
    }

    public static FaceDataFrame toFrameworkBaseFrame(BaseFrame baseFrame) {
        return new FaceDataFrame(toFrameworkAcquiredInfo(baseFrame.acquiredInfo), baseFrame.vendorCode, baseFrame.pan, baseFrame.tilt, baseFrame.distance, baseFrame.isCancellable);
    }

    public static FaceEnrollCell toFrameworkCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        return new FaceEnrollCell(cell.f0x, cell.f1y, cell.f2z);
    }

    public static byte convertFrameworkToAidlFeature(int i) throws IllegalArgumentException {
        if (i != 1) {
            if (i == 2) {
                return (byte) 1;
            }
            Slog.e("AidlConversionUtils", "Unsupported feature : " + i);
            throw new IllegalArgumentException();
        }
        return (byte) 0;
    }

    public static int convertAidlToFrameworkFeature(byte b) throws IllegalArgumentException {
        if (b != 0) {
            if (b == 1) {
                return 2;
            }
            Slog.e("AidlConversionUtils", "Unsupported feature : " + ((int) b));
            throw new IllegalArgumentException();
        }
        return 1;
    }
}
