package com.android.internal.inputmethod;

import android.p008os.Build;
import android.p008os.SystemClock;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.inputmethod.ImeTracing;
import com.android.internal.util.TraceBuffer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ImeTracingServerImpl extends ImeTracing {
    private static final int BUFFER_CAPACITY = 4194304;
    private static final long MAGIC_NUMBER_CLIENTS_VALUE = 4990904633913462089L;
    private static final long MAGIC_NUMBER_IMMS_VALUE = 4990904633914117449L;
    private static final long MAGIC_NUMBER_IMS_VALUE = 4990904633914510665L;
    private static final String TRACE_DIRNAME = "/data/misc/wmtrace/";
    private static final String TRACE_FILENAME_CLIENTS = "ime_trace_clients.winscope";
    private static final String TRACE_FILENAME_IMMS = "ime_trace_managerservice.winscope";
    private static final String TRACE_FILENAME_IMS = "ime_trace_service.winscope";
    private final Object mEnabledLock = new Object();
    private final TraceBuffer mBufferClients = new TraceBuffer(4194304);
    private final File mTraceFileClients = new File("/data/misc/wmtrace/ime_trace_clients.winscope");
    private final TraceBuffer mBufferIms = new TraceBuffer(4194304);
    private final File mTraceFileIms = new File("/data/misc/wmtrace/ime_trace_service.winscope");
    private final TraceBuffer mBufferImms = new TraceBuffer(4194304);
    private final File mTraceFileImms = new File("/data/misc/wmtrace/ime_trace_managerservice.winscope");

    @Override // com.android.internal.inputmethod.ImeTracing
    public void addToBuffer(ProtoOutputStream proto, int source) {
        if (isAvailable() && isEnabled()) {
            switch (source) {
                case 0:
                    this.mBufferClients.add(proto);
                    return;
                case 1:
                    this.mBufferIms.add(proto);
                    return;
                case 2:
                    this.mBufferImms.add(proto);
                    return;
                default:
                    Log.m104w("imeTracing", "Request to add to buffer, but source not recognised.");
                    return;
            }
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerClientDump(String where, InputMethodManager immInstance, byte[] icProto) {
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerServiceDump(String where, ImeTracing.ServiceDumper dumper, byte[] icProto) {
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerManagerServiceDump(String where) {
        if (!isEnabled() || !isAvailable()) {
            return;
        }
        synchronized (this.mDumpInProgressLock) {
            if (this.mDumpInProgress) {
                return;
            }
            this.mDumpInProgress = true;
            try {
                sendToService(null, 2, where);
            } finally {
                this.mDumpInProgress = false;
            }
        }
    }

    private void writeTracesToFilesLocked() {
        try {
            long timeOffsetNs = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()) - SystemClock.elapsedRealtimeNanos();
            ProtoOutputStream clientsProto = new ProtoOutputStream();
            clientsProto.write(1125281431553L, MAGIC_NUMBER_CLIENTS_VALUE);
            clientsProto.write(1125281431555L, timeOffsetNs);
            this.mBufferClients.writeTraceToFile(this.mTraceFileClients, clientsProto);
            ProtoOutputStream imsProto = new ProtoOutputStream();
            imsProto.write(1125281431553L, MAGIC_NUMBER_IMS_VALUE);
            imsProto.write(1125281431555L, timeOffsetNs);
            this.mBufferIms.writeTraceToFile(this.mTraceFileIms, imsProto);
            ProtoOutputStream immsProto = new ProtoOutputStream();
            immsProto.write(1125281431553L, MAGIC_NUMBER_IMMS_VALUE);
            immsProto.write(1125281431555L, timeOffsetNs);
            this.mBufferImms.writeTraceToFile(this.mTraceFileImms, immsProto);
            resetBuffers();
        } catch (IOException e) {
            Log.m109e("imeTracing", "Unable to write buffer to file", e);
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void startTrace(PrintWriter pw) {
        if (Build.IS_USER) {
            Log.m104w("imeTracing", "Warn: Tracing is not supported on user builds.");
            return;
        }
        synchronized (this.mEnabledLock) {
            if (isAvailable() && isEnabled()) {
                Log.m104w("imeTracing", "Warn: Tracing is already started.");
                return;
            }
            logAndPrintln(pw, "Starting tracing in /data/misc/wmtrace/: ime_trace_clients.winscope, ime_trace_service.winscope, ime_trace_managerservice.winscope");
            sEnabled = true;
            resetBuffers();
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void stopTrace(PrintWriter pw) {
        if (Build.IS_USER) {
            Log.m104w("imeTracing", "Warn: Tracing is not supported on user builds.");
            return;
        }
        synchronized (this.mEnabledLock) {
            if (isAvailable() && isEnabled()) {
                logAndPrintln(pw, "Stopping tracing and writing traces in /data/misc/wmtrace/: ime_trace_clients.winscope, ime_trace_service.winscope, ime_trace_managerservice.winscope");
                sEnabled = false;
                writeTracesToFilesLocked();
                return;
            }
            Log.m104w("imeTracing", "Warn: Tracing is not available or not started.");
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void saveForBugreport(PrintWriter pw) {
        if (Build.IS_USER) {
            return;
        }
        synchronized (this.mEnabledLock) {
            if (isAvailable() && isEnabled()) {
                sEnabled = false;
                logAndPrintln(pw, "Writing traces in /data/misc/wmtrace/: ime_trace_clients.winscope, ime_trace_service.winscope, ime_trace_managerservice.winscope");
                writeTracesToFilesLocked();
                sEnabled = true;
            }
        }
    }

    private void resetBuffers() {
        this.mBufferClients.resetBuffer();
        this.mBufferIms.resetBuffer();
        this.mBufferImms.resetBuffer();
    }
}
