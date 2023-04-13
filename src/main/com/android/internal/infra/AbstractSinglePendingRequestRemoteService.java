package com.android.internal.infra;

import android.content.ComponentName;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.IInterface;
import android.util.Slog;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.infra.AbstractSinglePendingRequestRemoteService;
import java.io.PrintWriter;
@Deprecated
/* loaded from: classes4.dex */
public abstract class AbstractSinglePendingRequestRemoteService<S extends AbstractSinglePendingRequestRemoteService<S, I>, I extends IInterface> extends AbstractRemoteService<S, I> {
    protected AbstractRemoteService.BasePendingRequest<S, I> mPendingRequest;

    public AbstractSinglePendingRequestRemoteService(Context context, String serviceInterface, ComponentName componentName, int userId, AbstractRemoteService.VultureCallback<S> callback, Handler handler, int bindingFlags, boolean verbose) {
        super(context, serviceInterface, componentName, userId, callback, handler, bindingFlags, verbose);
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    void handlePendingRequests() {
        if (this.mPendingRequest != null) {
            AbstractRemoteService.BasePendingRequest<S, I> pendingRequest = this.mPendingRequest;
            this.mPendingRequest = null;
            handlePendingRequest(pendingRequest);
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    protected void handleOnDestroy() {
        AbstractRemoteService.BasePendingRequest<S, I> basePendingRequest = this.mPendingRequest;
        if (basePendingRequest != null) {
            basePendingRequest.cancel();
            this.mPendingRequest = null;
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    void handleBindFailure() {
        if (this.mPendingRequest != null) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "Sending failure to " + this.mPendingRequest);
            }
            this.mPendingRequest.onFailed();
            this.mPendingRequest = null;
        }
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    public void dump(String prefix, PrintWriter pw) {
        super.dump(prefix, pw);
        pw.append((CharSequence) prefix).append("hasPendingRequest=").append((CharSequence) String.valueOf(this.mPendingRequest != null)).println();
    }

    @Override // com.android.internal.infra.AbstractRemoteService
    void handlePendingRequestWhileUnBound(AbstractRemoteService.BasePendingRequest<S, I> pendingRequest) {
        if (this.mPendingRequest != null) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "handlePendingRequestWhileUnBound(): cancelling " + this.mPendingRequest + " to handle " + pendingRequest);
            }
            this.mPendingRequest.cancel();
        }
        this.mPendingRequest = pendingRequest;
    }
}
