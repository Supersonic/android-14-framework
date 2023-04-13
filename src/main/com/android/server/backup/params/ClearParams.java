package com.android.server.backup.params;

import android.content.pm.PackageInfo;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.transport.TransportConnection;
/* loaded from: classes.dex */
public class ClearParams {
    public OnTaskFinishedListener listener;
    public TransportConnection mTransportConnection;
    public PackageInfo packageInfo;

    public ClearParams(TransportConnection transportConnection, PackageInfo packageInfo, OnTaskFinishedListener onTaskFinishedListener) {
        this.mTransportConnection = transportConnection;
        this.packageInfo = packageInfo;
        this.listener = onTaskFinishedListener;
    }
}
