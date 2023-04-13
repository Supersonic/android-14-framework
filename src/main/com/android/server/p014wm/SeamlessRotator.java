package com.android.server.p014wm;

import android.graphics.Matrix;
import android.graphics.Point;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import com.android.server.p014wm.utils.CoordinateTransforms;
import java.io.PrintWriter;
import java.io.StringWriter;
/* renamed from: com.android.server.wm.SeamlessRotator */
/* loaded from: classes2.dex */
public class SeamlessRotator {
    public final boolean mApplyFixedTransformHint;
    public final int mFixedTransformHint;
    public final float[] mFloat9;
    public final int mNewRotation;
    public final int mOldRotation;
    public final Matrix mTransform;

    public SeamlessRotator(int i, int i2, DisplayInfo displayInfo, boolean z) {
        Matrix matrix = new Matrix();
        this.mTransform = matrix;
        this.mFloat9 = new float[9];
        this.mOldRotation = i;
        this.mNewRotation = i2;
        this.mApplyFixedTransformHint = z;
        this.mFixedTransformHint = i;
        int i3 = displayInfo.rotation;
        boolean z2 = true;
        if (i3 != 1 && i3 != 3) {
            z2 = false;
        }
        int i4 = z2 ? displayInfo.logicalWidth : displayInfo.logicalHeight;
        int i5 = z2 ? displayInfo.logicalHeight : displayInfo.logicalWidth;
        Matrix matrix2 = new Matrix();
        CoordinateTransforms.transformLogicalToPhysicalCoordinates(i, i5, i4, matrix);
        CoordinateTransforms.transformPhysicalToLogicalCoordinates(i2, i5, i4, matrix2);
        matrix.postConcat(matrix2);
    }

    public void unrotate(SurfaceControl.Transaction transaction, WindowContainer windowContainer) {
        applyTransform(transaction, windowContainer.getSurfaceControl());
        Point point = windowContainer.mLastSurfacePosition;
        float[] fArr = {point.x, point.y};
        this.mTransform.mapPoints(fArr);
        transaction.setPosition(windowContainer.getSurfaceControl(), fArr[0], fArr[1]);
        if (this.mApplyFixedTransformHint) {
            transaction.setFixedTransformHint(windowContainer.mSurfaceControl, this.mFixedTransformHint);
        }
    }

    public void applyTransform(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        transaction.setMatrix(surfaceControl, this.mTransform, this.mFloat9);
    }

    public int getOldRotation() {
        return this.mOldRotation;
    }

    public void finish(SurfaceControl.Transaction transaction, WindowContainer windowContainer) {
        SurfaceControl surfaceControl = windowContainer.mSurfaceControl;
        if (surfaceControl == null || !surfaceControl.isValid()) {
            return;
        }
        setIdentityMatrix(transaction, windowContainer.mSurfaceControl);
        SurfaceControl surfaceControl2 = windowContainer.mSurfaceControl;
        Point point = windowContainer.mLastSurfacePosition;
        transaction.setPosition(surfaceControl2, point.x, point.y);
        if (this.mApplyFixedTransformHint) {
            transaction.unsetFixedTransformHint(windowContainer.mSurfaceControl);
        }
    }

    public void setIdentityMatrix(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        transaction.setMatrix(surfaceControl, Matrix.IDENTITY_MATRIX, this.mFloat9);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("{old=");
        printWriter.print(this.mOldRotation);
        printWriter.print(", new=");
        printWriter.print(this.mNewRotation);
        printWriter.print("}");
    }

    public String toString() {
        StringWriter stringWriter = new StringWriter();
        dump(new PrintWriter(stringWriter));
        return "ForcedSeamlessRotator" + stringWriter.toString();
    }
}
