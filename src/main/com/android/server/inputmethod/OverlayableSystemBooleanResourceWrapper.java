package com.android.server.inputmethod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Slog;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class OverlayableSystemBooleanResourceWrapper implements AutoCloseable {
    public final AtomicReference<Runnable> mCleanerRef;
    public final int mUserId;
    public final AtomicBoolean mValueRef;

    public static OverlayableSystemBooleanResourceWrapper create(final Context context, final int i, Handler handler, final Consumer<OverlayableSystemBooleanResourceWrapper> consumer) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(evaluate(context, i));
        AtomicReference atomicReference = new AtomicReference();
        final OverlayableSystemBooleanResourceWrapper overlayableSystemBooleanResourceWrapper = new OverlayableSystemBooleanResourceWrapper(context.getUserId(), atomicBoolean, atomicReference);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(PackageManagerShellCommandDataLoader.PACKAGE, 0);
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.inputmethod.OverlayableSystemBooleanResourceWrapper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                boolean evaluate = OverlayableSystemBooleanResourceWrapper.evaluate(context, i);
                if (evaluate != atomicBoolean.getAndSet(evaluate)) {
                    consumer.accept(overlayableSystemBooleanResourceWrapper);
                }
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter, null, handler, 4);
        atomicReference.set(new Runnable() { // from class: com.android.server.inputmethod.OverlayableSystemBooleanResourceWrapper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                context.unregisterReceiver(broadcastReceiver);
            }
        });
        atomicBoolean.set(evaluate(context, i));
        return overlayableSystemBooleanResourceWrapper;
    }

    public OverlayableSystemBooleanResourceWrapper(int i, AtomicBoolean atomicBoolean, AtomicReference<Runnable> atomicReference) {
        this.mUserId = i;
        this.mValueRef = atomicBoolean;
        this.mCleanerRef = atomicReference;
    }

    public boolean get() {
        return this.mValueRef.get();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public static boolean evaluate(Context context, int i) {
        try {
            return context.getPackageManager().getResourcesForApplication(PackageManagerShellCommandDataLoader.PACKAGE).getBoolean(i);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("OverlayableSystemBooleanResourceWrapper", "getResourcesForApplication(\"android\") failed", e);
            return false;
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        Runnable andSet = this.mCleanerRef.getAndSet(null);
        if (andSet != null) {
            andSet.run();
        }
    }
}
