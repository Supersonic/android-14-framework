package com.android.internal.infra;

import android.content.ComponentName;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.IInterface;
import android.util.Slog;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
@Deprecated
/* loaded from: classes4.dex */
public abstract class AbstractMultiplePendingRequestsRemoteService<S extends AbstractMultiplePendingRequestsRemoteService<S, I>, I extends IInterface> extends AbstractRemoteService<S, I> {
    private final int mInitialCapacity;
    protected List<AbstractRemoteService.BasePendingRequest<S, I>> mPendingRequests;

    public AbstractMultiplePendingRequestsRemoteService(Context context, String serviceInterface, ComponentName componentName, int userId, AbstractRemoteService.VultureCallback<S> callback, Handler handler, int bindingFlags, boolean verbose, int initialCapacity) {
        super(context, serviceInterface, componentName, userId, callback, handler, bindingFlags, verbose);
        this.mInitialCapacity = initialCapacity;
        this.mPendingRequests = new ArrayList(initialCapacity);
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    void handlePendingRequests() {
        synchronized (this.mPendingRequests) {
            int size = this.mPendingRequests.size();
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "Sending " + size + " pending requests");
            }
            for (int i = 0; i < size; i++) {
                handlePendingRequest(this.mPendingRequests.get(i));
            }
            this.mPendingRequests.clear();
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    protected void handleOnDestroy() {
        synchronized (this.mPendingRequests) {
            int size = this.mPendingRequests.size();
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "Canceling " + size + " pending requests");
            }
            for (int i = 0; i < size; i++) {
                this.mPendingRequests.get(i).cancel();
            }
            this.mPendingRequests.clear();
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    final void handleBindFailure() {
        synchronized (this.mPendingRequests) {
            int size = this.mPendingRequests.size();
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "Sending failure to " + size + " pending requests");
            }
            for (int i = 0; i < size; i++) {
                AbstractRemoteService.BasePendingRequest<S, I> request = this.mPendingRequests.get(i);
                request.onFailed();
                request.finish();
            }
            this.mPendingRequests.clear();
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    public void dump(String prefix, PrintWriter pw) {
        int size;
        super.dump(prefix, pw);
        pw.append((CharSequence) prefix).append("initialCapacity=").append((CharSequence) String.valueOf(this.mInitialCapacity)).println();
        synchronized (this.mPendingRequests) {
            size = this.mPendingRequests.size();
        }
        pw.append((CharSequence) prefix).append("pendingRequests=").append((CharSequence) String.valueOf(size)).println();
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    void handlePendingRequestWhileUnBound(AbstractRemoteService.BasePendingRequest<S, I> pendingRequest) {
        synchronized (this.mPendingRequests) {
            this.mPendingRequests.add(pendingRequest);
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "queued " + this.mPendingRequests.size() + " requests; last=" + pendingRequest);
            }
        }
    }
}
