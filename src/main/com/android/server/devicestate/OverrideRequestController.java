package com.android.server.devicestate;

import android.os.IBinder;
import android.util.Slog;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class OverrideRequestController {
    public OverrideRequest mBaseStateRequest;
    public final StatusChangeListener mListener;
    public OverrideRequest mRequest;
    public boolean mStickyRequest;
    public boolean mStickyRequestsAllowed;

    /* loaded from: classes.dex */
    public interface StatusChangeListener {
        void onStatusChanged(OverrideRequest overrideRequest, int i, int i2);
    }

    public static String statusToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return "CANCELED";
                }
                throw new IllegalArgumentException("Unknown status: " + i);
            }
            return "ACTIVE";
        }
        return "UNKNOWN";
    }

    public OverrideRequestController(StatusChangeListener statusChangeListener) {
        this.mListener = statusChangeListener;
    }

    public void setStickyRequestsAllowed(boolean z) {
        this.mStickyRequestsAllowed = z;
        if (z) {
            return;
        }
        cancelStickyRequest();
    }

    public void addRequest(OverrideRequest overrideRequest) {
        OverrideRequest overrideRequest2 = this.mRequest;
        this.mRequest = overrideRequest;
        this.mListener.onStatusChanged(overrideRequest, 1, 0);
        if (overrideRequest2 != null) {
            cancelRequestLocked(overrideRequest2);
        }
    }

    public void addBaseStateRequest(OverrideRequest overrideRequest) {
        OverrideRequest overrideRequest2 = this.mBaseStateRequest;
        this.mBaseStateRequest = overrideRequest;
        this.mListener.onStatusChanged(overrideRequest, 1, 0);
        if (overrideRequest2 != null) {
            cancelRequestLocked(overrideRequest2);
        }
    }

    public void cancelRequest(OverrideRequest overrideRequest) {
        if (hasRequest(overrideRequest.getToken(), overrideRequest.getRequestType())) {
            cancelCurrentRequestLocked();
        }
    }

    public void cancelStickyRequest() {
        if (this.mStickyRequest) {
            cancelCurrentRequestLocked();
        }
    }

    public void cancelOverrideRequest() {
        cancelCurrentRequestLocked();
    }

    public void cancelBaseStateOverrideRequest() {
        cancelCurrentBaseStateRequestLocked();
    }

    public boolean hasRequest(IBinder iBinder, int i) {
        if (i == 1) {
            OverrideRequest overrideRequest = this.mBaseStateRequest;
            return overrideRequest != null && iBinder == overrideRequest.getToken();
        }
        OverrideRequest overrideRequest2 = this.mRequest;
        return overrideRequest2 != null && iBinder == overrideRequest2.getToken();
    }

    public void handleProcessDied(int i) {
        OverrideRequest overrideRequest = this.mBaseStateRequest;
        if (overrideRequest != null && overrideRequest.getPid() == i) {
            cancelCurrentBaseStateRequestLocked();
        }
        OverrideRequest overrideRequest2 = this.mRequest;
        if (overrideRequest2 == null || overrideRequest2.getPid() != i) {
            return;
        }
        if (this.mStickyRequestsAllowed) {
            this.mStickyRequest = true;
        } else {
            cancelCurrentRequestLocked();
        }
    }

    public void handleBaseStateChanged(int i) {
        OverrideRequest overrideRequest = this.mBaseStateRequest;
        if (overrideRequest != null && i != overrideRequest.getRequestedState()) {
            cancelBaseStateOverrideRequest();
        }
        OverrideRequest overrideRequest2 = this.mRequest;
        if (overrideRequest2 == null || (overrideRequest2.getFlags() & 1) == 0) {
            return;
        }
        cancelCurrentRequestLocked();
    }

    public void handleNewSupportedStates(int[] iArr, int i) {
        int i2 = i == 3 ? 1 : 0;
        OverrideRequest overrideRequest = this.mBaseStateRequest;
        if (overrideRequest != null && !contains(iArr, overrideRequest.getRequestedState())) {
            cancelCurrentBaseStateRequestLocked(i2);
        }
        OverrideRequest overrideRequest2 = this.mRequest;
        if (overrideRequest2 == null || contains(iArr, overrideRequest2.getRequestedState())) {
            return;
        }
        cancelCurrentRequestLocked(i2);
    }

    public void dumpInternal(PrintWriter printWriter) {
        OverrideRequest overrideRequest = this.mRequest;
        boolean z = overrideRequest != null;
        printWriter.println();
        printWriter.println("Override Request active: " + z);
        if (z) {
            printWriter.println("Request: mPid=" + overrideRequest.getPid() + ", mRequestedState=" + overrideRequest.getRequestedState() + ", mFlags=" + overrideRequest.getFlags() + ", mStatus=" + statusToString(1));
        }
    }

    public final void cancelRequestLocked(OverrideRequest overrideRequest) {
        cancelRequestLocked(overrideRequest, 0);
    }

    public final void cancelRequestLocked(OverrideRequest overrideRequest, int i) {
        this.mListener.onStatusChanged(overrideRequest, 2, i);
    }

    public final void cancelCurrentRequestLocked() {
        cancelCurrentRequestLocked(0);
    }

    public final void cancelCurrentRequestLocked(int i) {
        OverrideRequest overrideRequest = this.mRequest;
        if (overrideRequest == null) {
            Slog.w("OverrideRequestController", "Attempted to cancel a null OverrideRequest");
            return;
        }
        this.mStickyRequest = false;
        cancelRequestLocked(overrideRequest, i);
        this.mRequest = null;
    }

    public final void cancelCurrentBaseStateRequestLocked() {
        cancelCurrentBaseStateRequestLocked(0);
    }

    public final void cancelCurrentBaseStateRequestLocked(int i) {
        OverrideRequest overrideRequest = this.mBaseStateRequest;
        if (overrideRequest == null) {
            Slog.w("OverrideRequestController", "Attempted to cancel a null OverrideRequest");
            return;
        }
        cancelRequestLocked(overrideRequest, i);
        this.mBaseStateRequest = null;
    }

    public static boolean contains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }
}
