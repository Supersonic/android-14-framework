package com.android.server.p006am;

import android.app.IStopUserCallback;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.ProgressReporter;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.am.UserState */
/* loaded from: classes.dex */
public final class UserState {
    public final UserHandle mHandle;
    public final ProgressReporter mUnlockProgress;
    public boolean switching;
    public final ArrayList<IStopUserCallback> mStopCallbacks = new ArrayList<>();
    public final ArrayList<KeyEvictedCallback> mKeyEvictedCallbacks = new ArrayList<>();
    public int state = 0;
    public int lastState = 0;
    public final ArrayMap<String, Long> mProviderLastReportedFg = new ArrayMap<>();

    /* renamed from: com.android.server.am.UserState$KeyEvictedCallback */
    /* loaded from: classes.dex */
    public interface KeyEvictedCallback {
        void keyEvicted(int i);
    }

    public static int stateToProtoEnum(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 3;
                    if (i != 3) {
                        i2 = 4;
                        if (i != 4) {
                            i2 = 5;
                            if (i != 5) {
                                return i;
                            }
                        }
                    }
                }
            }
            return i2;
        }
        return 0;
    }

    public UserState(UserHandle userHandle) {
        this.mHandle = userHandle;
        this.mUnlockProgress = new ProgressReporter(userHandle.getIdentifier());
    }

    public boolean setState(int i, int i2) {
        if (this.state == i) {
            setState(i2);
            return true;
        }
        Slog.w("ActivityManager", "Expected user " + this.mHandle.getIdentifier() + " in state " + stateToString(i) + " but was in state " + stateToString(this.state));
        return false;
    }

    public void setState(int i) {
        if (i == this.state) {
            return;
        }
        int identifier = this.mHandle.getIdentifier();
        if (this.state != 0) {
            Trace.asyncTraceEnd(64L, stateToString(this.state) + " " + identifier, identifier);
        }
        if (i != 5) {
            Trace.asyncTraceBegin(64L, stateToString(i) + " " + identifier, identifier);
        }
        Slog.i("ActivityManager", "User " + identifier + " state changed from " + stateToString(this.state) + " to " + stateToString(i));
        EventLogTags.writeAmUserStateChanged(identifier, i);
        this.lastState = this.state;
        this.state = i;
    }

    public static String stateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? Integer.toString(i) : "SHUTDOWN" : "STOPPING" : "RUNNING_UNLOCKED" : "RUNNING_UNLOCKING" : "RUNNING_LOCKED" : "BOOTING";
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("state=");
        printWriter.print(stateToString(this.state));
        if (this.switching) {
            printWriter.print(" SWITCHING");
        }
        printWriter.println();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1159641169921L, stateToProtoEnum(this.state));
        protoOutputStream.write(1133871366146L, this.switching);
        protoOutputStream.end(start);
    }

    public String toString() {
        return "[UserState: id=" + this.mHandle.getIdentifier() + ", state=" + stateToString(this.state) + ", lastState=" + stateToString(this.lastState) + ", switching=" + this.switching + "]";
    }
}
