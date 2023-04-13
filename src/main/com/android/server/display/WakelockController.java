package com.android.server.display;

import android.hardware.display.DisplayManagerInternal;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class WakelockController {
    public final int mDisplayId;
    public final DisplayManagerInternal.DisplayPowerCallbacks mDisplayPowerCallbacks;
    public boolean mHasProximityDebounced;
    public boolean mIsProximityNegativeAcquired;
    public boolean mIsProximityPositiveAcquired;
    public boolean mOnStateChangedPending;
    public final String mSuspendBlockerIdOnStateChanged;
    public final String mSuspendBlockerIdProxDebounce;
    public final String mSuspendBlockerIdProxNegative;
    public final String mSuspendBlockerIdProxPositive;
    public final String mSuspendBlockerIdUnfinishedBusiness;
    public final String mTag;
    public boolean mUnfinishedBusiness;

    public WakelockController(int i, DisplayManagerInternal.DisplayPowerCallbacks displayPowerCallbacks) {
        this.mDisplayId = i;
        this.mTag = "WakelockController[" + i + "]";
        this.mDisplayPowerCallbacks = displayPowerCallbacks;
        this.mSuspendBlockerIdUnfinishedBusiness = "[" + i + "]unfinished business";
        this.mSuspendBlockerIdOnStateChanged = "[" + i + "]on state changed";
        this.mSuspendBlockerIdProxPositive = "[" + i + "]prox positive";
        this.mSuspendBlockerIdProxNegative = "[" + i + "]prox negative";
        this.mSuspendBlockerIdProxDebounce = "[" + i + "]prox debounce";
    }

    public boolean acquireWakelock(int i) {
        return acquireWakelockInternal(i);
    }

    public boolean releaseWakelock(int i) {
        return releaseWakelockInternal(i);
    }

    public void releaseAll() {
        for (int i = 1; i < 5; i++) {
            releaseWakelockInternal(i);
        }
    }

    public final boolean acquireWakelockInternal(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            return acquireUnfinishedBusinessSuspendBlocker();
                        }
                        throw new RuntimeException("Invalid wakelock attempted to be acquired");
                    }
                    return acquireStateChangedSuspendBlocker();
                }
                return acquireProxDebounceSuspendBlocker();
            }
            return acquireProxNegativeSuspendBlocker();
        }
        return acquireProxPositiveSuspendBlocker();
    }

    public final boolean releaseWakelockInternal(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            return releaseUnfinishedBusinessSuspendBlocker();
                        }
                        throw new RuntimeException("Invalid wakelock attempted to be released");
                    }
                    return releaseStateChangedSuspendBlocker();
                }
                return releaseProxDebounceSuspendBlocker();
            }
            return releaseProxNegativeSuspendBlocker();
        }
        return releaseProxPositiveSuspendBlocker();
    }

    public final boolean acquireProxPositiveSuspendBlocker() {
        if (this.mIsProximityPositiveAcquired) {
            return false;
        }
        this.mDisplayPowerCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxPositive);
        this.mIsProximityPositiveAcquired = true;
        return true;
    }

    public final boolean acquireStateChangedSuspendBlocker() {
        if (this.mOnStateChangedPending) {
            return false;
        }
        this.mDisplayPowerCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdOnStateChanged);
        this.mOnStateChangedPending = true;
        return true;
    }

    public final boolean releaseStateChangedSuspendBlocker() {
        if (this.mOnStateChangedPending) {
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdOnStateChanged);
            this.mOnStateChangedPending = false;
            return true;
        }
        return false;
    }

    public final boolean acquireUnfinishedBusinessSuspendBlocker() {
        if (this.mUnfinishedBusiness) {
            return false;
        }
        this.mDisplayPowerCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
        this.mUnfinishedBusiness = true;
        return true;
    }

    public final boolean releaseUnfinishedBusinessSuspendBlocker() {
        if (this.mUnfinishedBusiness) {
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
            this.mUnfinishedBusiness = false;
            return true;
        }
        return false;
    }

    public final boolean releaseProxPositiveSuspendBlocker() {
        if (this.mIsProximityPositiveAcquired) {
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxPositive);
            this.mIsProximityPositiveAcquired = false;
            return true;
        }
        return false;
    }

    public final boolean acquireProxNegativeSuspendBlocker() {
        if (this.mIsProximityNegativeAcquired) {
            return false;
        }
        this.mDisplayPowerCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxNegative);
        this.mIsProximityNegativeAcquired = true;
        return true;
    }

    public final boolean releaseProxNegativeSuspendBlocker() {
        if (this.mIsProximityNegativeAcquired) {
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxNegative);
            this.mIsProximityNegativeAcquired = false;
            return true;
        }
        return false;
    }

    public final boolean acquireProxDebounceSuspendBlocker() {
        if (this.mHasProximityDebounced) {
            return false;
        }
        this.mDisplayPowerCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxDebounce);
        this.mHasProximityDebounced = true;
        return true;
    }

    public final boolean releaseProxDebounceSuspendBlocker() {
        if (this.mHasProximityDebounced) {
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxDebounce);
            this.mHasProximityDebounced = false;
            return true;
        }
        return false;
    }

    public Runnable getOnProximityPositiveRunnable() {
        return new Runnable() { // from class: com.android.server.display.WakelockController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WakelockController.this.lambda$getOnProximityPositiveRunnable$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOnProximityPositiveRunnable$0() {
        if (this.mIsProximityPositiveAcquired) {
            this.mIsProximityPositiveAcquired = false;
            this.mDisplayPowerCallbacks.onProximityPositive();
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxPositive);
        }
    }

    public Runnable getOnStateChangedRunnable() {
        return new Runnable() { // from class: com.android.server.display.WakelockController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WakelockController.this.lambda$getOnStateChangedRunnable$1();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOnStateChangedRunnable$1() {
        if (this.mOnStateChangedPending) {
            this.mOnStateChangedPending = false;
            this.mDisplayPowerCallbacks.onStateChanged();
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdOnStateChanged);
        }
    }

    public Runnable getOnProximityNegativeRunnable() {
        return new Runnable() { // from class: com.android.server.display.WakelockController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                WakelockController.this.lambda$getOnProximityNegativeRunnable$2();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOnProximityNegativeRunnable$2() {
        if (this.mIsProximityNegativeAcquired) {
            this.mIsProximityNegativeAcquired = false;
            this.mDisplayPowerCallbacks.onProximityNegative();
            this.mDisplayPowerCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxNegative);
        }
    }

    public void dumpLocal(PrintWriter printWriter) {
        printWriter.println("WakelockController State:");
        printWriter.println("  mDisplayId=" + this.mDisplayId);
        printWriter.println("  mUnfinishedBusiness=" + hasUnfinishedBusiness());
        printWriter.println("  mOnStateChangePending=" + isOnStateChangedPending());
        printWriter.println("  mOnProximityPositiveMessages=" + isProximityPositiveAcquired());
        printWriter.println("  mOnProximityNegativeMessages=" + isProximityNegativeAcquired());
    }

    @VisibleForTesting
    public String getSuspendBlockerUnfinishedBusinessId() {
        return this.mSuspendBlockerIdUnfinishedBusiness;
    }

    @VisibleForTesting
    public String getSuspendBlockerOnStateChangedId() {
        return this.mSuspendBlockerIdOnStateChanged;
    }

    @VisibleForTesting
    public String getSuspendBlockerProxPositiveId() {
        return this.mSuspendBlockerIdProxPositive;
    }

    @VisibleForTesting
    public String getSuspendBlockerProxNegativeId() {
        return this.mSuspendBlockerIdProxNegative;
    }

    @VisibleForTesting
    public String getSuspendBlockerProxDebounceId() {
        return this.mSuspendBlockerIdProxDebounce;
    }

    @VisibleForTesting
    public boolean hasUnfinishedBusiness() {
        return this.mUnfinishedBusiness;
    }

    @VisibleForTesting
    public boolean isOnStateChangedPending() {
        return this.mOnStateChangedPending;
    }

    @VisibleForTesting
    public boolean isProximityPositiveAcquired() {
        return this.mIsProximityPositiveAcquired;
    }

    @VisibleForTesting
    public boolean isProximityNegativeAcquired() {
        return this.mIsProximityNegativeAcquired;
    }

    @VisibleForTesting
    public boolean hasProximitySensorDebounced() {
        return this.mHasProximityDebounced;
    }
}
