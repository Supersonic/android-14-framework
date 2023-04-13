package com.android.server.biometrics.sensors;

import android.hardware.biometrics.SensorPropertiesInternal;
import android.util.proto.ProtoOutputStream;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public interface BiometricServiceProvider<T extends SensorPropertiesInternal> {
    boolean containsSensor(int i);

    void dumpInternal(int i, PrintWriter printWriter);

    void dumpProtoMetrics(int i, FileDescriptor fileDescriptor);

    void dumpProtoState(int i, ProtoOutputStream protoOutputStream, boolean z);

    long getAuthenticatorId(int i, int i2);

    int getLockoutModeForUser(int i, int i2);

    T getSensorProperties(int i);

    List<T> getSensorProperties();

    boolean hasEnrollments(int i, int i2);

    boolean isHardwareDetected(int i);
}
