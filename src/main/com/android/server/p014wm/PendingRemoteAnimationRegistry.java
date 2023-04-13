package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.os.Handler;
import android.os.IBinder;
import android.util.ArrayMap;
import android.view.RemoteAnimationAdapter;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p014wm.PendingRemoteAnimationRegistry;
/* renamed from: com.android.server.wm.PendingRemoteAnimationRegistry */
/* loaded from: classes2.dex */
public class PendingRemoteAnimationRegistry {
    public final ArrayMap<String, Entry> mEntries = new ArrayMap<>();
    public final Handler mHandler;
    public final WindowManagerGlobalLock mLock;

    public PendingRemoteAnimationRegistry(WindowManagerGlobalLock windowManagerGlobalLock, Handler handler) {
        this.mLock = windowManagerGlobalLock;
        this.mHandler = handler;
    }

    public void addPendingAnimation(String str, RemoteAnimationAdapter remoteAnimationAdapter, IBinder iBinder) {
        this.mEntries.put(str, new Entry(str, remoteAnimationAdapter, iBinder));
    }

    public ActivityOptions overrideOptionsIfNeeded(String str, ActivityOptions activityOptions) {
        Entry entry = this.mEntries.get(str);
        if (entry == null) {
            return activityOptions;
        }
        if (activityOptions == null) {
            activityOptions = ActivityOptions.makeRemoteAnimation(entry.adapter);
        } else {
            activityOptions.setRemoteAnimationAdapter(entry.adapter);
        }
        IBinder iBinder = entry.launchCookie;
        if (iBinder != null) {
            activityOptions.setLaunchCookie(iBinder);
        }
        this.mEntries.remove(str);
        return activityOptions;
    }

    /* renamed from: com.android.server.wm.PendingRemoteAnimationRegistry$Entry */
    /* loaded from: classes2.dex */
    public class Entry {
        public final RemoteAnimationAdapter adapter;
        public final IBinder launchCookie;
        public final String packageName;

        public Entry(final String str, RemoteAnimationAdapter remoteAnimationAdapter, IBinder iBinder) {
            this.packageName = str;
            this.adapter = remoteAnimationAdapter;
            this.launchCookie = iBinder;
            PendingRemoteAnimationRegistry.this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.wm.PendingRemoteAnimationRegistry$Entry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PendingRemoteAnimationRegistry.Entry.this.lambda$new$0(str);
                }
            }, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(String str) {
            synchronized (PendingRemoteAnimationRegistry.this.mLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (((Entry) PendingRemoteAnimationRegistry.this.mEntries.get(str)) == this) {
                        PendingRemoteAnimationRegistry.this.mEntries.remove(str);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }
}
