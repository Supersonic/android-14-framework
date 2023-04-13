package com.android.server.biometrics.sensors.face.aidl;

import android.hardware.common.NativeHandle;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.IOException;
/* loaded from: classes.dex */
public final class AidlNativeHandleUtils {
    public static NativeHandle dup(android.os.NativeHandle nativeHandle) throws IOException {
        if (nativeHandle == null) {
            return null;
        }
        NativeHandle nativeHandle2 = new NativeHandle();
        FileDescriptor[] fileDescriptors = nativeHandle.getFileDescriptors();
        nativeHandle2.ints = (int[]) nativeHandle.getInts().clone();
        nativeHandle2.fds = new ParcelFileDescriptor[fileDescriptors.length];
        for (int i = 0; i < fileDescriptors.length; i++) {
            nativeHandle2.fds[i] = ParcelFileDescriptor.dup(fileDescriptors[i]);
        }
        return nativeHandle2;
    }

    public static void close(NativeHandle nativeHandle) throws IOException {
        ParcelFileDescriptor[] parcelFileDescriptorArr;
        if (nativeHandle != null) {
            for (ParcelFileDescriptor parcelFileDescriptor : nativeHandle.fds) {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            }
        }
    }
}
