package com.android.server.p014wm;

import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.window.TransitionInfo;
import com.android.internal.util.TraceBuffer;
import com.android.server.p014wm.Transition;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.wm.TransitionTracer */
/* loaded from: classes2.dex */
public class TransitionTracer {
    public final TransitionTraceBuffer mTraceBuffer = new TransitionTraceBuffer();
    public final Object mEnabledLock = new Object();
    public volatile boolean mActiveTracingEnabled = false;

    public void logSentTransition(Transition transition, ArrayList<Transition.ChangeInfo> arrayList, long j, long j2, TransitionInfo transitionInfo) {
        this.mTraceBuffer.pushSentTransition(transition, arrayList, j, j2);
        logTransitionInfo(transition, transitionInfo);
    }

    public void logState(Transition transition) {
        if (this.mActiveTracingEnabled) {
            this.mTraceBuffer.pushTransitionState(transition);
        }
    }

    public final void logTransitionInfo(Transition transition, TransitionInfo transitionInfo) {
        if (this.mActiveTracingEnabled) {
            this.mTraceBuffer.pushTransitionInfo(transition, transitionInfo);
        }
    }

    /* renamed from: com.android.server.wm.TransitionTracer$TransitionTraceBuffer */
    /* loaded from: classes2.dex */
    public class TransitionTraceBuffer {
        public final TraceBuffer mBuffer;
        public final TraceBuffer mStateBuffer;
        public final TraceBuffer mTransitionInfoBuffer;

        public TransitionTraceBuffer() {
            this.mBuffer = new TraceBuffer(15360);
            this.mStateBuffer = new TraceBuffer(5120000);
            this.mTransitionInfoBuffer = new TraceBuffer(5120000);
        }

        public void pushSentTransition(Transition transition, ArrayList<Transition.ChangeInfo> arrayList, long j, long j2) {
            Trace.beginSection("TransitionTraceBuffer#pushSentTransition");
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            long start = protoOutputStream.start(2246267895810L);
            if (TransitionTracer.this.mActiveTracingEnabled) {
                protoOutputStream.write(1120986464257L, transition.getSyncId());
            }
            protoOutputStream.write(1116691496962L, transition.getStartTransaction().getId());
            protoOutputStream.write(1116691496963L, transition.getFinishTransaction().getId());
            protoOutputStream.write(1112396529668L, j);
            protoOutputStream.write(1112396529669L, j2);
            for (int i = 0; i < arrayList.size(); i++) {
                long start2 = protoOutputStream.start(2246267895814L);
                Transition.ChangeInfo changeInfo = arrayList.get(i);
                int transitMode = changeInfo.getTransitMode(changeInfo.mContainer);
                int layerId = changeInfo.mContainer.mSurfaceControl.isValid() ? changeInfo.mContainer.mSurfaceControl.getLayerId() : -1;
                protoOutputStream.write(1120986464257L, transitMode);
                protoOutputStream.write(1120986464258L, layerId);
                if (TransitionTracer.this.mActiveTracingEnabled) {
                    protoOutputStream.write(1120986464259L, System.identityHashCode(changeInfo.mContainer));
                }
                protoOutputStream.end(start2);
            }
            protoOutputStream.end(start);
            this.mBuffer.add(protoOutputStream);
            Trace.endSection();
        }

        public final void pushTransitionState(Transition transition) {
            Trace.beginSection("TransitionTraceBuffer#pushTransitionState");
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            long start = protoOutputStream.start(2246267895811L);
            protoOutputStream.write(1112396529665L, SystemClock.elapsedRealtimeNanos());
            protoOutputStream.write(1120986464258L, transition.getSyncId());
            protoOutputStream.write(1120986464259L, transition.mType);
            protoOutputStream.write(1159641169924L, transition.getState());
            protoOutputStream.write(1120986464261L, transition.getFlags());
            for (int i = 0; i < transition.mChanges.size(); i++) {
                writeChange(protoOutputStream, transition.mChanges.keyAt(i), transition.mChanges.valueAt(i));
            }
            for (int i2 = 0; i2 < transition.mChanges.size(); i2++) {
                writeChange(protoOutputStream, transition.mChanges.keyAt(i2), transition.mChanges.valueAt(i2));
            }
            for (int i3 = 0; i3 < transition.mParticipants.size(); i3++) {
                transition.mParticipants.valueAt(i3).writeIdentifierToProto(protoOutputStream, 2246267895815L);
            }
            protoOutputStream.end(start);
            this.mStateBuffer.add(protoOutputStream);
            Trace.endSection();
        }

        public final void pushTransitionInfo(Transition transition, TransitionInfo transitionInfo) {
            Trace.beginSection("TransitionTraceBuffer#pushTransitionInfo");
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            long start = protoOutputStream.start(2246267895812L);
            protoOutputStream.write(1120986464257L, transition.getSyncId());
            for (int i = 0; i < transitionInfo.getChanges().size(); i++) {
                writeTransitionInfoChange(protoOutputStream, (TransitionInfo.Change) transitionInfo.getChanges().get(i));
            }
            protoOutputStream.end(start);
            this.mTransitionInfoBuffer.add(protoOutputStream);
            Trace.endSection();
        }

        public final void writeChange(ProtoOutputStream protoOutputStream, WindowContainer windowContainer, Transition.ChangeInfo changeInfo) {
            Trace.beginSection("TransitionTraceBuffer#writeChange");
            long start = protoOutputStream.start(2246267895814L);
            int transitMode = changeInfo.getTransitMode(windowContainer);
            boolean hasChanged = changeInfo.hasChanged();
            int changeFlags = changeInfo.getChangeFlags(windowContainer);
            int i = changeInfo.mWindowingMode;
            protoOutputStream.write(1120986464258L, transitMode);
            protoOutputStream.write(1133871366147L, hasChanged);
            protoOutputStream.write(1120986464260L, changeFlags);
            protoOutputStream.write(1120986464261L, i);
            windowContainer.writeIdentifierToProto(protoOutputStream, 1146756268033L);
            protoOutputStream.end(start);
            Trace.endSection();
        }

        public final void writeTransitionInfoChange(ProtoOutputStream protoOutputStream, TransitionInfo.Change change) {
            Trace.beginSection("TransitionTraceBuffer#writeTransitionInfoChange");
            long start = protoOutputStream.start(2246267895810L);
            protoOutputStream.write(1120986464257L, change.getLeash().getLayerId());
            protoOutputStream.write(1120986464258L, change.getMode());
            protoOutputStream.end(start);
            Trace.endSection();
        }

        public void writeToFile(File file, ProtoOutputStream protoOutputStream) throws IOException {
            this.mBuffer.writeTraceToFile(file, protoOutputStream);
        }

        public void reset() {
            this.mBuffer.resetBuffer();
        }
    }

    public void startTrace(PrintWriter printWriter) {
        if (Build.IS_USER) {
            LogAndPrintln.m2e(printWriter, "Tracing is not supported on user builds.");
            return;
        }
        Trace.beginSection("TransitionTracer#startTrace");
        LogAndPrintln.m0i(printWriter, "Starting shell transition trace.");
        synchronized (this.mEnabledLock) {
            this.mActiveTracingEnabled = true;
            this.mTraceBuffer.mBuffer.setCapacity(5120000);
            this.mTraceBuffer.reset();
        }
        Trace.endSection();
    }

    public void stopTrace(PrintWriter printWriter) {
        stopTrace(printWriter, new File("/data/misc/wmtrace/transition_trace.winscope"));
    }

    public void stopTrace(PrintWriter printWriter, File file) {
        if (Build.IS_USER) {
            LogAndPrintln.m2e(printWriter, "Tracing is not supported on user builds.");
            return;
        }
        Trace.beginSection("TransitionTracer#stopTrace");
        LogAndPrintln.m0i(printWriter, "Stopping shell transition trace.");
        synchronized (this.mEnabledLock) {
            this.mActiveTracingEnabled = false;
            writeTraceToFileLocked(printWriter, file);
            this.mTraceBuffer.mBuffer.setCapacity(15360);
        }
        Trace.endSection();
    }

    public boolean isActiveTracingEnabled() {
        return this.mActiveTracingEnabled;
    }

    public final void writeTraceToFileLocked(PrintWriter printWriter, File file) {
        Trace.beginSection("TransitionTracer#writeTraceToFileLocked");
        try {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            protoOutputStream.write(1125281431553L, 4990904633914184276L);
            int myPid = Process.myPid();
            LogAndPrintln.m0i(printWriter, "Writing file to " + file.getAbsolutePath() + " from process " + myPid);
            this.mTraceBuffer.writeToFile(file, protoOutputStream);
        } catch (IOException e) {
            LogAndPrintln.m1e(printWriter, "Unable to write buffer to file", e);
        }
        Trace.endSection();
    }

    /* renamed from: com.android.server.wm.TransitionTracer$LogAndPrintln */
    /* loaded from: classes2.dex */
    public static class LogAndPrintln {
        /* renamed from: i */
        public static void m0i(PrintWriter printWriter, String str) {
            Log.i("TransitionTracer", str);
            if (printWriter != null) {
                printWriter.println(str);
                printWriter.flush();
            }
        }

        /* renamed from: e */
        public static void m2e(PrintWriter printWriter, String str) {
            Log.e("TransitionTracer", str);
            if (printWriter != null) {
                printWriter.println("ERROR: " + str);
                printWriter.flush();
            }
        }

        /* renamed from: e */
        public static void m1e(PrintWriter printWriter, String str, Exception exc) {
            Log.e("TransitionTracer", str, exc);
            if (printWriter != null) {
                printWriter.println("ERROR: " + str + " ::\n " + exc);
                printWriter.flush();
            }
        }
    }
}
