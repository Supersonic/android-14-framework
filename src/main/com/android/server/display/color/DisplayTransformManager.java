package com.android.server.display.color;

import android.app.ActivityTaskManager;
import android.content.res.Configuration;
import android.opengl.Matrix;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.lang.reflect.Array;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DisplayTransformManager {
    @VisibleForTesting
    static final String PERSISTENT_PROPERTY_COMPOSITION_COLOR_MODE = "persist.sys.sf.color_mode";
    @VisibleForTesting
    static final String PERSISTENT_PROPERTY_DISPLAY_COLOR = "persist.sys.sf.native_mode";
    @VisibleForTesting
    static final String PERSISTENT_PROPERTY_SATURATION = "persist.sys.sf.color_saturation";
    public static final IBinder sFlinger = ServiceManager.getService("SurfaceFlinger");
    @GuardedBy({"mColorMatrix"})
    public final SparseArray<float[]> mColorMatrix = new SparseArray<>(6);
    @GuardedBy({"mColorMatrix"})
    public final float[][] mTempColorMatrix = (float[][]) Array.newInstance(Float.TYPE, 2, 16);
    public final Object mDaltonizerModeLock = new Object();
    @GuardedBy({"mDaltonizerModeLock"})
    public int mDaltonizerMode = -1;

    public boolean needsLinearColorMatrix(int i) {
        return i != 2;
    }

    public float[] getColorMatrix(int i) {
        float[] copyOf;
        synchronized (this.mColorMatrix) {
            float[] fArr = this.mColorMatrix.get(i);
            copyOf = fArr == null ? null : Arrays.copyOf(fArr, fArr.length);
        }
        return copyOf;
    }

    public void setColorMatrix(int i, float[] fArr) {
        if (fArr != null && fArr.length != 16) {
            throw new IllegalArgumentException("Expected length: 16 (4x4 matrix), actual length: " + fArr.length);
        }
        synchronized (this.mColorMatrix) {
            float[] fArr2 = this.mColorMatrix.get(i);
            if (!Arrays.equals(fArr2, fArr)) {
                if (fArr == null) {
                    this.mColorMatrix.remove(i);
                } else if (fArr2 == null) {
                    this.mColorMatrix.put(i, Arrays.copyOf(fArr, fArr.length));
                } else {
                    System.arraycopy(fArr, 0, fArr2, 0, fArr.length);
                }
                applyColorMatrix(computeColorMatrixLocked());
            }
        }
    }

    public void setDaltonizerMode(int i) {
        synchronized (this.mDaltonizerModeLock) {
            if (this.mDaltonizerMode != i) {
                this.mDaltonizerMode = i;
                applyDaltonizerMode(i);
            }
        }
    }

    @GuardedBy({"mColorMatrix"})
    public final float[] computeColorMatrixLocked() {
        int size = this.mColorMatrix.size();
        if (size == 0) {
            return null;
        }
        float[][] fArr = this.mTempColorMatrix;
        int i = 0;
        Matrix.setIdentityM(fArr[0], 0);
        while (i < size) {
            int i2 = i + 1;
            Matrix.multiplyMM(fArr[i2 % 2], 0, fArr[i % 2], 0, this.mColorMatrix.valueAt(i), 0);
            i = i2;
        }
        return fArr[size % 2];
    }

    public static void applyColorMatrix(float[] fArr) {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
        if (fArr != null) {
            obtain.writeInt(1);
            for (int i = 0; i < 16; i++) {
                obtain.writeFloat(fArr[i]);
            }
        } else {
            obtain.writeInt(0);
        }
        try {
            try {
                sFlinger.transact(1015, obtain, null, 0);
            } catch (RemoteException e) {
                Slog.e("DisplayTransformManager", "Failed to set color transform", e);
            }
        } finally {
            obtain.recycle();
        }
    }

    public static void applyDaltonizerMode(int i) {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
        obtain.writeInt(i);
        try {
            try {
                sFlinger.transact(1014, obtain, null, 0);
            } catch (RemoteException e) {
                Slog.e("DisplayTransformManager", "Failed to set Daltonizer mode", e);
            }
        } finally {
            obtain.recycle();
        }
    }

    public boolean needsLinearColorMatrix() {
        return SystemProperties.getInt(PERSISTENT_PROPERTY_DISPLAY_COLOR, 1) != 1;
    }

    public boolean setColorMode(int i, float[] fArr, int i2) {
        if (i == 0) {
            applySaturation(1.0f);
            setDisplayColor(0, i2);
        } else if (i == 1) {
            applySaturation(1.1f);
            setDisplayColor(0, i2);
        } else if (i == 2) {
            applySaturation(1.0f);
            setDisplayColor(1, i2);
        } else if (i == 3) {
            applySaturation(1.0f);
            setDisplayColor(2, i2);
        } else if (i >= 256 && i <= 511) {
            applySaturation(1.0f);
            setDisplayColor(i, i2);
        }
        setColorMatrix(100, fArr);
        updateConfiguration();
        return true;
    }

    public boolean isDeviceColorManaged() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
        try {
            sFlinger.transact(1030, obtain, obtain2, 0);
            return obtain2.readBoolean();
        } catch (RemoteException e) {
            Slog.e("DisplayTransformManager", "Failed to query wide color support", e);
            return false;
        } finally {
            obtain.recycle();
            obtain2.recycle();
        }
    }

    public final void applySaturation(float f) {
        SystemProperties.set(PERSISTENT_PROPERTY_SATURATION, Float.toString(f));
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
        obtain.writeFloat(f);
        try {
            try {
                sFlinger.transact(1022, obtain, null, 0);
            } catch (RemoteException e) {
                Slog.e("DisplayTransformManager", "Failed to set saturation", e);
            }
        } finally {
            obtain.recycle();
        }
    }

    public final void setDisplayColor(int i, int i2) {
        SystemProperties.set(PERSISTENT_PROPERTY_DISPLAY_COLOR, Integer.toString(i));
        if (i2 != -1) {
            SystemProperties.set(PERSISTENT_PROPERTY_COMPOSITION_COLOR_MODE, Integer.toString(i2));
        }
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
        obtain.writeInt(i);
        if (i2 != -1) {
            obtain.writeInt(i2);
        }
        try {
            try {
                sFlinger.transact(1023, obtain, null, 0);
            } catch (RemoteException e) {
                Slog.e("DisplayTransformManager", "Failed to set display color", e);
            }
        } finally {
            obtain.recycle();
        }
    }

    public final void updateConfiguration() {
        try {
            ActivityTaskManager.getService().updateConfiguration((Configuration) null);
        } catch (RemoteException e) {
            Slog.e("DisplayTransformManager", "Could not update configuration", e);
        }
    }
}
