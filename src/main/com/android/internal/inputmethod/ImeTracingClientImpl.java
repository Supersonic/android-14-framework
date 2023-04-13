package com.android.internal.inputmethod;

import android.util.proto.ProtoOutputStream;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodManagerGlobal;
import com.android.internal.inputmethod.ImeTracing;
import java.io.PrintWriter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ImeTracingClientImpl extends ImeTracing {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ImeTracingClientImpl() {
        sEnabled = InputMethodManagerGlobal.isImeTraceEnabled();
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void addToBuffer(ProtoOutputStream proto, int source) {
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerClientDump(String where, InputMethodManager immInstance, byte[] icProto) {
        if (!isEnabled() || !isAvailable()) {
            return;
        }
        synchronized (this.mDumpInProgressLock) {
            if (this.mDumpInProgress) {
                return;
            }
            this.mDumpInProgress = true;
            try {
                ProtoOutputStream proto = new ProtoOutputStream();
                immInstance.dumpDebug(proto, icProto);
                sendToService(proto.getBytes(), 0, where);
            } finally {
                this.mDumpInProgress = false;
            }
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerServiceDump(String where, ImeTracing.ServiceDumper dumper, byte[] icProto) {
        if (!isEnabled() || !isAvailable()) {
            return;
        }
        synchronized (this.mDumpInProgressLock) {
            if (this.mDumpInProgress) {
                return;
            }
            this.mDumpInProgress = true;
            try {
                ProtoOutputStream proto = new ProtoOutputStream();
                dumper.dumpToProto(proto, icProto);
                sendToService(proto.getBytes(), 1, where);
            } finally {
                this.mDumpInProgress = false;
            }
        }
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void triggerManagerServiceDump(String where) {
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void startTrace(PrintWriter pw) {
    }

    @Override // com.android.internal.inputmethod.ImeTracing
    public void stopTrace(PrintWriter pw) {
    }
}
