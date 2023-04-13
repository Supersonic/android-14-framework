package com.android.server.autofill.p007ui;

import android.os.Handler;
import android.util.Slog;
import com.android.internal.view.inline.IInlineContentCallback;
import com.android.internal.view.inline.IInlineContentProvider;
import com.android.server.FgThread;
import com.android.server.autofill.Helper;
/* renamed from: com.android.server.autofill.ui.InlineContentProviderImpl */
/* loaded from: classes.dex */
public final class InlineContentProviderImpl extends IInlineContentProvider.Stub {
    public static final String TAG = InlineContentProviderImpl.class.getSimpleName();
    public final Handler mHandler = FgThread.getHandler();
    public boolean mProvideContentCalled = false;
    public RemoteInlineSuggestionUi mRemoteInlineSuggestionUi;
    public final RemoteInlineSuggestionViewConnector mRemoteInlineSuggestionViewConnector;

    public InlineContentProviderImpl(RemoteInlineSuggestionViewConnector remoteInlineSuggestionViewConnector, RemoteInlineSuggestionUi remoteInlineSuggestionUi) {
        this.mRemoteInlineSuggestionViewConnector = remoteInlineSuggestionViewConnector;
        this.mRemoteInlineSuggestionUi = remoteInlineSuggestionUi;
    }

    public InlineContentProviderImpl copy() {
        return new InlineContentProviderImpl(this.mRemoteInlineSuggestionViewConnector, this.mRemoteInlineSuggestionUi);
    }

    public void provideContent(final int i, final int i2, final IInlineContentCallback iInlineContentCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.InlineContentProviderImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InlineContentProviderImpl.this.lambda$provideContent$0(i, i2, iInlineContentCallback);
            }
        });
    }

    public void requestSurfacePackage() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.InlineContentProviderImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                InlineContentProviderImpl.this.handleGetSurfacePackage();
            }
        });
    }

    public void onSurfacePackageReleased() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.InlineContentProviderImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InlineContentProviderImpl.this.handleOnSurfacePackageReleased();
            }
        });
    }

    /* renamed from: handleProvideContent */
    public final void lambda$provideContent$0(int i, int i2, IInlineContentCallback iInlineContentCallback) {
        if (Helper.sVerbose) {
            Slog.v(TAG, "handleProvideContent");
        }
        if (this.mProvideContentCalled) {
            return;
        }
        this.mProvideContentCalled = true;
        RemoteInlineSuggestionUi remoteInlineSuggestionUi = this.mRemoteInlineSuggestionUi;
        if (remoteInlineSuggestionUi == null || !remoteInlineSuggestionUi.match(i, i2)) {
            this.mRemoteInlineSuggestionUi = new RemoteInlineSuggestionUi(this.mRemoteInlineSuggestionViewConnector, i, i2, this.mHandler);
        }
        this.mRemoteInlineSuggestionUi.setInlineContentCallback(iInlineContentCallback);
        this.mRemoteInlineSuggestionUi.requestSurfacePackage();
    }

    public final void handleGetSurfacePackage() {
        RemoteInlineSuggestionUi remoteInlineSuggestionUi;
        if (Helper.sVerbose) {
            Slog.v(TAG, "handleGetSurfacePackage");
        }
        if (!this.mProvideContentCalled || (remoteInlineSuggestionUi = this.mRemoteInlineSuggestionUi) == null) {
            return;
        }
        remoteInlineSuggestionUi.requestSurfacePackage();
    }

    public final void handleOnSurfacePackageReleased() {
        RemoteInlineSuggestionUi remoteInlineSuggestionUi;
        if (Helper.sVerbose) {
            Slog.v(TAG, "handleOnSurfacePackageReleased");
        }
        if (!this.mProvideContentCalled || (remoteInlineSuggestionUi = this.mRemoteInlineSuggestionUi) == null) {
            return;
        }
        remoteInlineSuggestionUi.surfacePackageReleased();
    }
}
