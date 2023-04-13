package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.view.Display;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class DisplayAdapter {
    public static final AtomicInteger NEXT_DISPLAY_MODE_ID = new AtomicInteger(1);
    public final Context mContext;
    public final Handler mHandler;
    public final Listener mListener;
    public final String mName;
    public final DisplayManagerService.SyncRoot mSyncRoot;

    /* loaded from: classes.dex */
    public interface Listener {
        void onDisplayDeviceEvent(DisplayDevice displayDevice, int i);

        void onTraversalRequested();
    }

    public void dumpLocked(PrintWriter printWriter) {
    }

    public void registerLocked() {
    }

    public DisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, Listener listener, String str) {
        this.mSyncRoot = syncRoot;
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mName = str;
    }

    public final DisplayManagerService.SyncRoot getSyncRoot() {
        return this.mSyncRoot;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Handler getHandler() {
        return this.mHandler;
    }

    public final String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendDisplayDeviceEventLocked$0(DisplayDevice displayDevice, int i) {
        this.mListener.onDisplayDeviceEvent(displayDevice, i);
    }

    public final void sendDisplayDeviceEventLocked(final DisplayDevice displayDevice, final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.DisplayAdapter$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DisplayAdapter.this.lambda$sendDisplayDeviceEventLocked$0(displayDevice, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendTraversalRequestLocked$1() {
        this.mListener.onTraversalRequested();
    }

    public final void sendTraversalRequestLocked() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.DisplayAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayAdapter.this.lambda$sendTraversalRequestLocked$1();
            }
        });
    }

    public static Display.Mode createMode(int i, int i2, float f) {
        return createMode(i, i2, f, new float[0], new int[0]);
    }

    public static Display.Mode createMode(int i, int i2, float f, float[] fArr, int[] iArr) {
        return new Display.Mode(NEXT_DISPLAY_MODE_ID.getAndIncrement(), i, i2, f, fArr, iArr);
    }
}
