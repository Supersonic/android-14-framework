package com.android.server.p006am;

import android.annotation.SuppressLint;
import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.am.ActivityManagerLocal */
/* loaded from: classes.dex */
public interface ActivityManagerLocal {
    @SuppressLint({"RethrowRemoteException"})
    boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, IBinder iBinder, String str, String str2, int i2) throws RemoteException;

    @SuppressLint({"RethrowRemoteException"})
    boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, IBinder iBinder, String str, String str2, Context.BindServiceFlags bindServiceFlags) throws RemoteException;

    @SuppressLint({"RethrowRemoteException"})
    boolean bindSdkSandboxService(Intent intent, ServiceConnection serviceConnection, int i, String str, String str2, int i2) throws RemoteException;

    boolean canAllowWhileInUsePermissionInFgs(int i, int i2, String str);

    boolean canStartForegroundService(int i, int i2, String str);

    void killSdkSandboxClientAppProcess(IBinder iBinder);

    @SuppressLint({"RethrowRemoteException"})
    ComponentName startSdkSandboxService(Intent intent, int i, String str, String str2) throws RemoteException;

    boolean stopSdkSandboxService(Intent intent, int i, String str, String str2);

    void tempAllowWhileInUsePermissionInFgs(int i, long j);
}
