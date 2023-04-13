package com.android.server.statusbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.SparseArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.io.FileDescriptor;
/* loaded from: classes2.dex */
public class TileRequestTracker {
    @VisibleForTesting
    static final int MAX_NUM_DENIALS = 3;
    public final Context mContext;
    public final BroadcastReceiver mUninstallReceiver;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArrayMap<ComponentName, Integer> mTrackingMap = new SparseArrayMap<>();
    @GuardedBy({"mLock"})
    public final ArraySet<ComponentName> mComponentsToRemove = new ArraySet<>();

    public TileRequestTracker(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.statusbar.TileRequestTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    return;
                }
                String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                if (intent.hasExtra("android.intent.extra.UID")) {
                    int userId = UserHandle.getUserId(intent.getIntExtra("android.intent.extra.UID", -1));
                    synchronized (TileRequestTracker.this.mLock) {
                        TileRequestTracker.this.mComponentsToRemove.clear();
                        int numElementsForKey = TileRequestTracker.this.mTrackingMap.numElementsForKey(userId);
                        int indexOfKey = TileRequestTracker.this.mTrackingMap.indexOfKey(userId);
                        for (int i = 0; i < numElementsForKey; i++) {
                            ComponentName componentName = (ComponentName) TileRequestTracker.this.mTrackingMap.keyAt(indexOfKey, i);
                            if (componentName.getPackageName().equals(encodedSchemeSpecificPart)) {
                                TileRequestTracker.this.mComponentsToRemove.add(componentName);
                            }
                        }
                        int size = TileRequestTracker.this.mComponentsToRemove.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            TileRequestTracker.this.mTrackingMap.delete(userId, (ComponentName) TileRequestTracker.this.mComponentsToRemove.valueAt(i2));
                        }
                    }
                }
            }
        };
        this.mUninstallReceiver = broadcastReceiver;
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
        intentFilter.addDataScheme("package");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, null);
    }

    public boolean shouldBeDenied(int i, ComponentName componentName) {
        boolean z;
        synchronized (this.mLock) {
            z = ((Integer) this.mTrackingMap.getOrDefault(i, componentName, 0)).intValue() >= 3;
        }
        return z;
    }

    public void addDenial(int i, ComponentName componentName) {
        synchronized (this.mLock) {
            this.mTrackingMap.add(i, componentName, Integer.valueOf(((Integer) this.mTrackingMap.getOrDefault(i, componentName, 0)).intValue() + 1));
        }
    }

    public void resetRequests(int i, ComponentName componentName) {
        synchronized (this.mLock) {
            this.mTrackingMap.delete(i, componentName);
        }
    }

    public void dump(FileDescriptor fileDescriptor, final IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        indentingPrintWriter.println("TileRequestTracker:");
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            this.mTrackingMap.forEach(new SparseArrayMap.TriConsumer() { // from class: com.android.server.statusbar.TileRequestTracker$$ExternalSyntheticLambda0
                public final void accept(int i, Object obj, Object obj2) {
                    TileRequestTracker.lambda$dump$0(indentingPrintWriter, i, (ComponentName) obj, (Integer) obj2);
                }
            });
        }
        indentingPrintWriter.decreaseIndent();
    }

    public static /* synthetic */ void lambda$dump$0(IndentingPrintWriter indentingPrintWriter, int i, ComponentName componentName, Integer num) {
        indentingPrintWriter.println("user=" + i + ", " + componentName.toShortString() + ": " + num);
    }
}
