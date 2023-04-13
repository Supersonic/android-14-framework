package com.android.server.p006am;

import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IActivityTaskManager;
import android.app.IAssistDataReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.IWindowManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* renamed from: com.android.server.am.AssistDataRequester */
/* loaded from: classes.dex */
public class AssistDataRequester extends IAssistDataReceiver.Stub {
    public AppOpsManager mAppOpsManager;
    public AssistDataRequesterCallbacks mCallbacks;
    public Object mCallbacksLock;
    public boolean mCanceled;
    public Context mContext;
    public int mPendingDataCount;
    public int mPendingScreenshotCount;
    public int mRequestScreenshotAppOps;
    public int mRequestStructureAppOps;
    public IWindowManager mWindowManager;
    public final ArrayList<Bundle> mAssistData = new ArrayList<>();
    public final ArrayList<Bitmap> mAssistScreenshot = new ArrayList<>();
    @VisibleForTesting
    public IActivityTaskManager mActivityTaskManager = ActivityTaskManager.getService();

    /* renamed from: com.android.server.am.AssistDataRequester$AssistDataRequesterCallbacks */
    /* loaded from: classes.dex */
    public interface AssistDataRequesterCallbacks {
        @GuardedBy({"mCallbacksLock"})
        boolean canHandleReceivedAssistDataLocked();

        @GuardedBy({"mCallbacksLock"})
        default void onAssistDataReceivedLocked(Bundle bundle, int i, int i2) {
        }

        @GuardedBy({"mCallbacksLock"})
        default void onAssistRequestCompleted() {
        }

        @GuardedBy({"mCallbacksLock"})
        default void onAssistScreenshotReceivedLocked(Bitmap bitmap) {
        }
    }

    public AssistDataRequester(Context context, IWindowManager iWindowManager, AppOpsManager appOpsManager, AssistDataRequesterCallbacks assistDataRequesterCallbacks, Object obj, int i, int i2) {
        this.mCallbacks = assistDataRequesterCallbacks;
        this.mCallbacksLock = obj;
        this.mWindowManager = iWindowManager;
        this.mContext = context;
        this.mAppOpsManager = appOpsManager;
        this.mRequestStructureAppOps = i;
        this.mRequestScreenshotAppOps = i2;
    }

    public void requestAssistData(List<IBinder> list, boolean z, boolean z2, boolean z3, boolean z4, int i, String str, String str2) {
        requestAssistData(list, z, z2, true, z3, z4, false, i, str, str2);
    }

    public void requestAssistData(List<IBinder> list, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i, String str, String str2) {
        requestData(list, false, z, z2, z3, z4, z5, z6, i, str, str2);
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x00d0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void requestData(List<IBinder> list, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, int i, String str, String str2) {
        boolean z8;
        boolean requestAssistContextExtras;
        Objects.requireNonNull(list);
        Objects.requireNonNull(str);
        if (list.isEmpty()) {
            tryDispatchRequestComplete();
            return;
        }
        boolean z9 = false;
        try {
            z8 = this.mActivityTaskManager.isAssistDataAllowedOnCurrentActivity();
        } catch (RemoteException unused) {
            z8 = false;
        }
        boolean z10 = z5 & z8;
        boolean z11 = z6 & (z2 && z8 && this.mRequestScreenshotAppOps != -1);
        this.mCanceled = false;
        this.mPendingDataCount = 0;
        this.mPendingScreenshotCount = 0;
        this.mAssistData.clear();
        this.mAssistScreenshot.clear();
        if (z2) {
            if (this.mAppOpsManager.noteOpNoThrow(this.mRequestStructureAppOps, i, str, str2, (String) null) == 0 && z10) {
                int size = list.size();
                int i2 = 0;
                while (i2 < size) {
                    IBinder iBinder = list.get(i2);
                    try {
                        MetricsLogger.count(this.mContext, "assist_with_context", 1);
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", i2);
                        bundle.putInt("count", size);
                        if (z) {
                            requestAssistContextExtras = this.mActivityTaskManager.requestAutofillData(this, bundle, iBinder, 0);
                        } else {
                            requestAssistContextExtras = this.mActivityTaskManager.requestAssistContextExtras(z4 ? 1 : 3, this, bundle, iBinder, i2 == 0 && !z7, i2 == 0);
                        }
                        if (requestAssistContextExtras) {
                            this.mPendingDataCount++;
                        } else if (i2 == 0) {
                            if (this.mCallbacks.canHandleReceivedAssistDataLocked()) {
                                dispatchAssistDataReceived(null);
                            } else {
                                this.mAssistData.add(null);
                            }
                        }
                    } catch (RemoteException unused2) {
                    }
                    i2++;
                }
            } else if (this.mCallbacks.canHandleReceivedAssistDataLocked()) {
                dispatchAssistDataReceived(null);
            } else {
                this.mAssistData.add(null);
            }
            if (z3) {
                if (this.mAppOpsManager.noteOpNoThrow(this.mRequestScreenshotAppOps, i, str, str2, (String) null) == 0 && z9) {
                    try {
                        MetricsLogger.count(this.mContext, "assist_with_screen", 1);
                        this.mPendingScreenshotCount++;
                        this.mWindowManager.requestAssistScreenshot(this);
                    } catch (RemoteException unused3) {
                    }
                } else if (this.mCallbacks.canHandleReceivedAssistDataLocked()) {
                    dispatchAssistScreenshotReceived(null);
                } else {
                    this.mAssistScreenshot.add(null);
                }
            }
            tryDispatchRequestComplete();
        }
        z9 = z11;
        if (z3) {
        }
        tryDispatchRequestComplete();
    }

    public void processPendingAssistData() {
        flushPendingAssistData();
        tryDispatchRequestComplete();
    }

    public final void flushPendingAssistData() {
        int size = this.mAssistData.size();
        for (int i = 0; i < size; i++) {
            dispatchAssistDataReceived(this.mAssistData.get(i));
        }
        this.mAssistData.clear();
        int size2 = this.mAssistScreenshot.size();
        for (int i2 = 0; i2 < size2; i2++) {
            dispatchAssistScreenshotReceived(this.mAssistScreenshot.get(i2));
        }
        this.mAssistScreenshot.clear();
    }

    public int getPendingDataCount() {
        return this.mPendingDataCount;
    }

    public int getPendingScreenshotCount() {
        return this.mPendingScreenshotCount;
    }

    public void cancel() {
        this.mCanceled = true;
        this.mPendingDataCount = 0;
        this.mPendingScreenshotCount = 0;
        this.mAssistData.clear();
        this.mAssistScreenshot.clear();
    }

    public void onHandleAssistData(Bundle bundle) {
        synchronized (this.mCallbacksLock) {
            if (this.mCanceled) {
                return;
            }
            this.mPendingDataCount--;
            if (this.mCallbacks.canHandleReceivedAssistDataLocked()) {
                flushPendingAssistData();
                dispatchAssistDataReceived(bundle);
                tryDispatchRequestComplete();
            } else {
                this.mAssistData.add(bundle);
            }
        }
    }

    public void onHandleAssistScreenshot(Bitmap bitmap) {
        synchronized (this.mCallbacksLock) {
            if (this.mCanceled) {
                return;
            }
            this.mPendingScreenshotCount--;
            if (this.mCallbacks.canHandleReceivedAssistDataLocked()) {
                flushPendingAssistData();
                dispatchAssistScreenshotReceived(bitmap);
                tryDispatchRequestComplete();
            } else {
                this.mAssistScreenshot.add(bitmap);
            }
        }
    }

    public final void dispatchAssistDataReceived(Bundle bundle) {
        int i;
        int i2;
        Bundle bundle2 = bundle != null ? bundle.getBundle("receiverExtras") : null;
        if (bundle2 != null) {
            i = bundle2.getInt("index");
            i2 = bundle2.getInt("count");
        } else {
            i = 0;
            i2 = 0;
        }
        this.mCallbacks.onAssistDataReceivedLocked(bundle, i, i2);
    }

    public final void dispatchAssistScreenshotReceived(Bitmap bitmap) {
        this.mCallbacks.onAssistScreenshotReceivedLocked(bitmap);
    }

    public final void tryDispatchRequestComplete() {
        if (this.mPendingDataCount == 0 && this.mPendingScreenshotCount == 0 && this.mAssistData.isEmpty() && this.mAssistScreenshot.isEmpty()) {
            this.mCallbacks.onAssistRequestCompleted();
        }
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("mPendingDataCount=");
        printWriter.println(this.mPendingDataCount);
        printWriter.print(str);
        printWriter.print("mAssistData=");
        printWriter.println(this.mAssistData);
        printWriter.print(str);
        printWriter.print("mPendingScreenshotCount=");
        printWriter.println(this.mPendingScreenshotCount);
        printWriter.print(str);
        printWriter.print("mAssistScreenshot=");
        printWriter.println(this.mAssistScreenshot);
    }
}
