package com.android.server.p011pm;

import android.content.pm.IPackageInstallObserver2;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Slog;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.pm.MultiPackageVerifyingSession */
/* loaded from: classes2.dex */
public final class MultiPackageVerifyingSession {
    public final List<VerifyingSession> mChildVerifyingSessions;
    public final IPackageInstallObserver2 mObserver;
    public final UserHandle mUser;
    public final Set<VerifyingSession> mVerificationState;

    public MultiPackageVerifyingSession(VerifyingSession verifyingSession, List<VerifyingSession> list) throws PackageManagerException {
        this.mUser = verifyingSession.getUser();
        if (list.size() == 0) {
            throw PackageManagerException.ofInternalError("No child sessions found!", -21);
        }
        this.mChildVerifyingSessions = list;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).mParentVerifyingSession = this;
        }
        this.mVerificationState = new ArraySet(this.mChildVerifyingSessions.size());
        this.mObserver = verifyingSession.mObserver;
    }

    public void start() {
        Trace.asyncTraceEnd(262144L, "queueVerify", System.identityHashCode(this));
        Trace.traceBegin(262144L, "startVerify");
        for (VerifyingSession verifyingSession : this.mChildVerifyingSessions) {
            verifyingSession.handleStartVerify();
        }
        for (VerifyingSession verifyingSession2 : this.mChildVerifyingSessions) {
            verifyingSession2.handleReturnCode();
        }
        Trace.traceEnd(262144L);
    }

    public void trySendVerificationCompleteNotification(VerifyingSession verifyingSession) {
        int i;
        String str;
        this.mVerificationState.add(verifyingSession);
        if (this.mVerificationState.size() != this.mChildVerifyingSessions.size()) {
            return;
        }
        Iterator<VerifyingSession> it = this.mVerificationState.iterator();
        while (true) {
            i = 1;
            if (!it.hasNext()) {
                str = null;
                break;
            }
            VerifyingSession next = it.next();
            int ret = next.getRet();
            if (ret != 1) {
                str = next.getErrorMessage();
                i = ret;
                break;
            }
        }
        try {
            this.mObserver.onPackageInstalled((String) null, i, str, new Bundle());
        } catch (RemoteException unused) {
            Slog.i("PackageManager", "Observer no longer exists.");
        }
    }

    public String toString() {
        return "MultiPackageVerifyingSession{" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
