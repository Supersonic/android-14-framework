package com.android.server.p014wm.utils;

import android.graphics.Matrix;
/* renamed from: com.android.server.wm.utils.CoordinateTransforms */
/* loaded from: classes2.dex */
public class CoordinateTransforms {
    public static void transformPhysicalToLogicalCoordinates(int i, int i2, int i3, Matrix matrix) {
        if (i == 0) {
            matrix.reset();
        } else if (i == 1) {
            matrix.setRotate(270.0f);
            matrix.postTranslate(0.0f, i2);
        } else if (i == 2) {
            matrix.setRotate(180.0f);
            matrix.postTranslate(i2, i3);
        } else if (i == 3) {
            matrix.setRotate(90.0f);
            matrix.postTranslate(i3, 0.0f);
        } else {
            throw new IllegalArgumentException("Unknown rotation: " + i);
        }
    }

    public static void transformLogicalToPhysicalCoordinates(int i, int i2, int i3, Matrix matrix) {
        if (i == 0) {
            matrix.reset();
        } else if (i == 1) {
            matrix.setRotate(90.0f);
            matrix.preTranslate(0.0f, -i2);
        } else if (i == 2) {
            matrix.setRotate(180.0f);
            matrix.preTranslate(-i2, -i3);
        } else if (i == 3) {
            matrix.setRotate(270.0f);
            matrix.preTranslate(-i3, 0.0f);
        } else {
            throw new IllegalArgumentException("Unknown rotation: " + i);
        }
    }

    public static void computeRotationMatrix(int i, int i2, int i3, Matrix matrix) {
        if (i == 0) {
            matrix.reset();
        } else if (i == 1) {
            matrix.setRotate(90.0f);
            matrix.postTranslate(i3, 0.0f);
        } else if (i == 2) {
            matrix.setRotate(180.0f);
            matrix.postTranslate(i2, i3);
        } else if (i != 3) {
        } else {
            matrix.setRotate(270.0f);
            matrix.postTranslate(0.0f, i2);
        }
    }
}
