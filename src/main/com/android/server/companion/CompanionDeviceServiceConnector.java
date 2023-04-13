package com.android.server.companion;

import android.annotation.SuppressLint;
import android.companion.AssociationInfo;
import android.companion.ICompanionDeviceService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.android.internal.infra.ServiceConnector;
import com.android.server.ServiceThread;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public class CompanionDeviceServiceConnector extends ServiceConnector.Impl<ICompanionDeviceService> {
    public static volatile ServiceThread sServiceThread;
    private final ComponentName mComponentName;
    private boolean mIsPrimary;
    private Listener mListener;
    private final int mUserId;

    /* loaded from: classes.dex */
    public interface Listener {
        void onBindingDied(int i, String str, CompanionDeviceServiceConnector companionDeviceServiceConnector);
    }

    public long getAutoDisconnectTimeoutMs() {
        return -1L;
    }

    public void onServiceConnectionStatusChanged(ICompanionDeviceService iCompanionDeviceService, boolean z) {
    }

    public static CompanionDeviceServiceConnector newInstance(Context context, int i, ComponentName componentName, boolean z, boolean z2) {
        return new CompanionDeviceServiceConnector(context, i, componentName, z ? 268435456 : 65536, z2);
    }

    public CompanionDeviceServiceConnector(Context context, int i, ComponentName componentName, int i2, boolean z) {
        super(context, buildIntent(componentName), i2, i, (Function) null);
        this.mUserId = i;
        this.mComponentName = componentName;
        this.mIsPrimary = z;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void postOnDeviceAppeared(final AssociationInfo associationInfo) {
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.companion.CompanionDeviceServiceConnector$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                ((ICompanionDeviceService) obj).onDeviceAppeared(associationInfo);
            }
        });
    }

    public void postOnDeviceDisappeared(final AssociationInfo associationInfo) {
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.companion.CompanionDeviceServiceConnector$$ExternalSyntheticLambda2
            public final void runNoResult(Object obj) {
                ((ICompanionDeviceService) obj).onDeviceDisappeared(associationInfo);
            }
        });
    }

    public void postUnbind() {
        getJobHandler().postDelayed(new Runnable() { // from class: com.android.server.companion.CompanionDeviceServiceConnector$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CompanionDeviceServiceConnector.this.unbind();
            }
        }, 5000L);
    }

    public boolean isPrimary() {
        return this.mIsPrimary;
    }

    public void binderDied() {
        super.binderDied();
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onBindingDied(this.mUserId, this.mComponentName.getPackageName(), this);
        }
    }

    /* renamed from: binderAsInterface */
    public ICompanionDeviceService m2597binderAsInterface(IBinder iBinder) {
        return ICompanionDeviceService.Stub.asInterface(iBinder);
    }

    public Handler getJobHandler() {
        return getServiceThread().getThreadHandler();
    }

    public static Intent buildIntent(ComponentName componentName) {
        return new Intent("android.companion.CompanionDeviceService").setComponent(componentName);
    }

    public static ServiceThread getServiceThread() {
        if (sServiceThread == null) {
            synchronized (CompanionDeviceManagerService.class) {
                if (sServiceThread == null) {
                    sServiceThread = new ServiceThread("companion-device-service-connector", 0, false);
                    sServiceThread.start();
                }
            }
        }
        return sServiceThread;
    }
}
