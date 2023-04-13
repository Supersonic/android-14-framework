package com.android.server.p006am;

import android.app.IApplicationThread;
import android.app.ReceiverInfo;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.CompatibilityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteCallback;
import android.os.RemoteException;
import java.util.List;
import java.util.Objects;
/* renamed from: com.android.server.am.SameProcessApplicationThread */
/* loaded from: classes.dex */
public class SameProcessApplicationThread extends IApplicationThread.Default {
    public final Handler mHandler;
    public final IApplicationThread mWrapped;

    public SameProcessApplicationThread(IApplicationThread iApplicationThread, Handler handler) {
        Objects.requireNonNull(iApplicationThread);
        this.mWrapped = iApplicationThread;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
    }

    public void scheduleReceiver(final Intent intent, final ActivityInfo activityInfo, final CompatibilityInfo compatibilityInfo, final int i, final String str, final Bundle bundle, final boolean z, final boolean z2, final int i2, final int i3, final int i4, final String str2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.SameProcessApplicationThread$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SameProcessApplicationThread.this.lambda$scheduleReceiver$0(intent, activityInfo, compatibilityInfo, i, str, bundle, z, z2, i2, i3, i4, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleReceiver$0(Intent intent, ActivityInfo activityInfo, CompatibilityInfo compatibilityInfo, int i, String str, Bundle bundle, boolean z, boolean z2, int i2, int i3, int i4, String str2) {
        try {
            this.mWrapped.scheduleReceiver(intent, activityInfo, compatibilityInfo, i, str, bundle, z, z2, i2, i3, i4, str2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleRegisteredReceiver(final IIntentReceiver iIntentReceiver, final Intent intent, final int i, final String str, final Bundle bundle, final boolean z, final boolean z2, final boolean z3, final int i2, final int i3, final int i4, final String str2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.SameProcessApplicationThread$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SameProcessApplicationThread.this.lambda$scheduleRegisteredReceiver$1(iIntentReceiver, intent, i, str, bundle, z, z2, z3, i2, i3, i4, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRegisteredReceiver$1(IIntentReceiver iIntentReceiver, Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, boolean z3, int i2, int i3, int i4, String str2) {
        try {
            this.mWrapped.scheduleRegisteredReceiver(iIntentReceiver, intent, i, str, bundle, z, z2, z3, i2, i3, i4, str2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleReceiverList(final List<ReceiverInfo> list) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.SameProcessApplicationThread$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SameProcessApplicationThread.this.lambda$scheduleReceiverList$2(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleReceiverList$2(List list) {
        try {
            this.mWrapped.scheduleReceiverList(list);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void schedulePing(final RemoteCallback remoteCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.SameProcessApplicationThread$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SameProcessApplicationThread.this.lambda$schedulePing$3(remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$schedulePing$3(RemoteCallback remoteCallback) {
        try {
            this.mWrapped.schedulePing(remoteCallback);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
