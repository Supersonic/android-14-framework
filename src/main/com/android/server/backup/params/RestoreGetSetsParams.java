package com.android.server.backup.params;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IRestoreObserver;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.restore.ActiveRestoreSession;
import com.android.server.backup.transport.TransportConnection;
/* loaded from: classes.dex */
public class RestoreGetSetsParams {
    public final OnTaskFinishedListener listener;
    public final TransportConnection mTransportConnection;
    public final IBackupManagerMonitor monitor;
    public final IRestoreObserver observer;
    public final ActiveRestoreSession session;

    public RestoreGetSetsParams(TransportConnection transportConnection, ActiveRestoreSession activeRestoreSession, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, OnTaskFinishedListener onTaskFinishedListener) {
        this.mTransportConnection = transportConnection;
        this.session = activeRestoreSession;
        this.observer = iRestoreObserver;
        this.monitor = iBackupManagerMonitor;
        this.listener = onTaskFinishedListener;
    }
}
