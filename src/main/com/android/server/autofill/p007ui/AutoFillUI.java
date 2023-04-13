package com.android.server.autofill.p007ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveInfo;
import android.service.autofill.ValueFinder;
import android.text.TextUtils;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.autofill.AutofillId;
import android.view.autofill.IAutofillWindowPresenter;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerInternal;
import com.android.server.UiThread;
import com.android.server.autofill.Helper;
import com.android.server.autofill.p007ui.DialogFillUi;
import com.android.server.autofill.p007ui.FillUi;
import com.android.server.autofill.p007ui.SaveUi;
import com.android.server.location.gnss.hal.GnssNative;
import java.io.PrintWriter;
/* renamed from: com.android.server.autofill.ui.AutoFillUI */
/* loaded from: classes.dex */
public final class AutoFillUI {
    public AutoFillUiCallback mCallback;
    public final Context mContext;
    public Runnable mCreateFillUiRunnable;
    public DialogFillUi mFillDialog;
    public FillUi mFillUi;
    public final OverlayControl mOverlayControl;
    public SaveUi mSaveUi;
    public AutoFillUiCallback mSaveUiCallback;
    public final Handler mHandler = UiThread.getHandler();
    public final MetricsLogger mMetricsLogger = new MetricsLogger();
    public final UiModeManagerInternal mUiModeMgr = (UiModeManagerInternal) LocalServices.getService(UiModeManagerInternal.class);

    /* renamed from: com.android.server.autofill.ui.AutoFillUI$AutoFillUiCallback */
    /* loaded from: classes.dex */
    public interface AutoFillUiCallback {
        void authenticate(int i, int i2, IntentSender intentSender, Bundle bundle, int i3);

        void cancelSave();

        void cancelSession();

        void dispatchUnhandledKey(AutofillId autofillId, KeyEvent keyEvent);

        void fill(int i, int i2, Dataset dataset, int i3);

        void onShown(int i);

        void requestFallbackFromFillDialog();

        void requestHideFillUi(AutofillId autofillId);

        void requestShowFillUi(AutofillId autofillId, int i, int i2, IAutofillWindowPresenter iAutofillWindowPresenter);

        void requestShowSoftInput(AutofillId autofillId);

        void save();

        void startIntentSender(IntentSender intentSender, Intent intent);

        void startIntentSenderAndFinishSession(IntentSender intentSender);
    }

    public AutoFillUI(Context context) {
        this.mContext = context;
        this.mOverlayControl = new OverlayControl(context);
    }

    public void setCallback(final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$setCallback$0(autoFillUiCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setCallback$0(AutoFillUiCallback autoFillUiCallback) {
        AutoFillUiCallback autoFillUiCallback2 = this.mCallback;
        if (autoFillUiCallback2 != autoFillUiCallback) {
            if (autoFillUiCallback2 != null) {
                if (isSaveUiShowing()) {
                    hideFillUiUiThread(autoFillUiCallback, true);
                } else {
                    lambda$hideAll$10(this.mCallback);
                }
            }
            this.mCallback = autoFillUiCallback;
        }
    }

    public void clearCallback(final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$clearCallback$1(autoFillUiCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearCallback$1(AutoFillUiCallback autoFillUiCallback) {
        if (this.mCallback == autoFillUiCallback) {
            lambda$hideAll$10(autoFillUiCallback);
            this.mCallback = null;
        }
    }

    public void showError(int i, AutoFillUiCallback autoFillUiCallback) {
        showError(this.mContext.getString(i), autoFillUiCallback);
    }

    public void showError(final CharSequence charSequence, final AutoFillUiCallback autoFillUiCallback) {
        Slog.w("AutofillUI", "showError(): " + ((Object) charSequence));
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$showError$2(autoFillUiCallback, charSequence);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showError$2(AutoFillUiCallback autoFillUiCallback, CharSequence charSequence) {
        if (this.mCallback != autoFillUiCallback) {
            return;
        }
        lambda$hideAll$10(autoFillUiCallback);
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        Toast.makeText(this.mContext, charSequence, 1).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hideFillUi$3(AutoFillUiCallback autoFillUiCallback) {
        hideFillUiUiThread(autoFillUiCallback, true);
    }

    public void hideFillUi(final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$hideFillUi$3(autoFillUiCallback);
            }
        });
    }

    public void hideFillDialog(final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$hideFillDialog$4(autoFillUiCallback);
            }
        });
    }

    public void filterFillUi(final String str, final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$filterFillUi$5(autoFillUiCallback, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filterFillUi$5(AutoFillUiCallback autoFillUiCallback, String str) {
        FillUi fillUi;
        if (autoFillUiCallback == this.mCallback && (fillUi = this.mFillUi) != null) {
            fillUi.setFilterText(str);
        }
    }

    public void showFillUi(final AutofillId autofillId, final FillResponse fillResponse, final String str, String str2, ComponentName componentName, final CharSequence charSequence, final Drawable drawable, final AutoFillUiCallback autoFillUiCallback, int i, boolean z) {
        if (Helper.sDebug) {
            int length = str == null ? 0 : str.length();
            Slog.d("AutofillUI", "showFillUi(): id=" + autofillId + ", filter=" + length + " chars");
        }
        final LogMaker addTaggedData = Helper.newLogMaker(910, componentName, str2, i, z).addTaggedData(911, Integer.valueOf(str == null ? 0 : str.length())).addTaggedData(909, Integer.valueOf(fillResponse.getDatasets() != null ? fillResponse.getDatasets().size() : 0));
        Runnable runnable = new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$showFillUi$6(autoFillUiCallback, fillResponse, autofillId, str, charSequence, drawable, addTaggedData);
            }
        };
        if (isSaveUiShowing()) {
            if (Helper.sDebug) {
                Slog.d("AutofillUI", "postpone fill UI request..");
            }
            this.mCreateFillUiRunnable = runnable;
            return;
        }
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFillUi$6(final AutoFillUiCallback autoFillUiCallback, final FillResponse fillResponse, final AutofillId autofillId, String str, CharSequence charSequence, Drawable drawable, final LogMaker logMaker) {
        if (autoFillUiCallback != this.mCallback) {
            return;
        }
        lambda$hideAll$10(autoFillUiCallback);
        this.mFillUi = new FillUi(this.mContext, fillResponse, autofillId, str, this.mOverlayControl, charSequence, drawable, this.mUiModeMgr.isNightMode(), new FillUi.Callback() { // from class: com.android.server.autofill.ui.AutoFillUI.1
            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void onResponsePicked(FillResponse fillResponse2) {
                logMaker.setType(3);
                AutoFillUI.this.hideFillUiUiThread(autoFillUiCallback, true);
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.authenticate(fillResponse2.getRequestId(), GnssNative.GNSS_AIDING_TYPE_ALL, fillResponse2.getAuthentication(), fillResponse2.getClientState(), 1);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void onShown() {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.onShown(1);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void onDatasetPicked(Dataset dataset) {
                logMaker.setType(4);
                AutoFillUI.this.hideFillUiUiThread(autoFillUiCallback, true);
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.fill(fillResponse.getRequestId(), fillResponse.getDatasets().indexOf(dataset), dataset, 1);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void onCanceled() {
                logMaker.setType(5);
                AutoFillUI.this.hideFillUiUiThread(autoFillUiCallback, true);
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void onDestroy() {
                if (logMaker.getType() == 0) {
                    logMaker.setType(2);
                }
                AutoFillUI.this.mMetricsLogger.write(logMaker);
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void requestShowFillUi(int i, int i2, IAutofillWindowPresenter iAutofillWindowPresenter) {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.requestShowFillUi(autofillId, i, i2, iAutofillWindowPresenter);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void requestHideFillUi() {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.requestHideFillUi(autofillId);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void startIntentSender(IntentSender intentSender) {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.startIntentSenderAndFinishSession(intentSender);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void dispatchUnhandledKey(KeyEvent keyEvent) {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.dispatchUnhandledKey(autofillId, keyEvent);
                }
            }

            @Override // com.android.server.autofill.p007ui.FillUi.Callback
            public void cancelSession() {
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.cancelSession();
                }
            }
        });
    }

    public void showSaveUi(final CharSequence charSequence, final Drawable drawable, final String str, final SaveInfo saveInfo, final ValueFinder valueFinder, final ComponentName componentName, final AutoFillUiCallback autoFillUiCallback, final PendingUi pendingUi, final boolean z, final boolean z2, final boolean z3) {
        if (Helper.sVerbose) {
            Slog.v("AutofillUI", "showSaveUi(update=" + z + ") for " + componentName.toShortString() + ": " + saveInfo);
        }
        final LogMaker addTaggedData = Helper.newLogMaker(916, componentName, str, pendingUi.sessionId, z2).addTaggedData(917, Integer.valueOf((saveInfo.getRequiredIds() == null ? 0 : saveInfo.getRequiredIds().length) + 0 + (saveInfo.getOptionalIds() != null ? saveInfo.getOptionalIds().length : 0)));
        if (z) {
            addTaggedData.addTaggedData(1555, 1);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$showSaveUi$7(autoFillUiCallback, pendingUi, charSequence, drawable, str, componentName, saveInfo, valueFinder, addTaggedData, z, z2, z3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSaveUi$7(final AutoFillUiCallback autoFillUiCallback, final PendingUi pendingUi, CharSequence charSequence, Drawable drawable, String str, ComponentName componentName, SaveInfo saveInfo, ValueFinder valueFinder, final LogMaker logMaker, boolean z, boolean z2, boolean z3) {
        if (autoFillUiCallback != this.mCallback) {
            return;
        }
        lambda$hideAll$10(autoFillUiCallback);
        this.mSaveUiCallback = autoFillUiCallback;
        this.mSaveUi = new SaveUi(this.mContext, pendingUi, charSequence, drawable, str, componentName, saveInfo, valueFinder, this.mOverlayControl, new SaveUi.OnSaveListener() { // from class: com.android.server.autofill.ui.AutoFillUI.2
            @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
            public void onSave() {
                logMaker.setType(4);
                AutoFillUI.this.hideSaveUiUiThread(autoFillUiCallback);
                autoFillUiCallback.save();
                AutoFillUI.this.destroySaveUiUiThread(pendingUi, true);
            }

            @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
            public void onCancel(IntentSender intentSender) {
                logMaker.setType(5);
                AutoFillUI.this.hideSaveUiUiThread(autoFillUiCallback);
                if (intentSender != null) {
                    try {
                        intentSender.sendIntent(AutoFillUI.this.mContext, 0, null, null, null);
                    } catch (IntentSender.SendIntentException e) {
                        Slog.e("AutofillUI", "Error starting negative action listener: " + intentSender, e);
                    }
                }
                autoFillUiCallback.cancelSave();
                AutoFillUI.this.destroySaveUiUiThread(pendingUi, true);
            }

            @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
            public void onDestroy() {
                if (logMaker.getType() == 0) {
                    logMaker.setType(2);
                    autoFillUiCallback.cancelSave();
                }
                AutoFillUI.this.mMetricsLogger.write(logMaker);
            }

            @Override // com.android.server.autofill.p007ui.SaveUi.OnSaveListener
            public void startIntentSender(IntentSender intentSender, Intent intent) {
                autoFillUiCallback.startIntentSender(intentSender, intent);
            }
        }, this.mUiModeMgr.isNightMode(), z, z2, z3);
    }

    public void showFillDialog(final AutofillId autofillId, final FillResponse fillResponse, final String str, final String str2, final ComponentName componentName, final Drawable drawable, final AutoFillUiCallback autoFillUiCallback, int i, boolean z) {
        if (Helper.sVerbose) {
            Slog.v("AutofillUI", "showFillDialog for " + componentName.toShortString() + ": " + fillResponse);
        }
        final LogMaker addTaggedData = Helper.newLogMaker(910, componentName, str2, i, z).addTaggedData(911, Integer.valueOf(str == null ? 0 : str.length())).addTaggedData(909, Integer.valueOf(fillResponse.getDatasets() != null ? fillResponse.getDatasets().size() : 0));
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$showFillDialog$8(autoFillUiCallback, fillResponse, autofillId, str, drawable, str2, componentName, addTaggedData);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFillDialog$8(final AutoFillUiCallback autoFillUiCallback, final FillResponse fillResponse, final AutofillId autofillId, String str, Drawable drawable, String str2, ComponentName componentName, final LogMaker logMaker) {
        if (autoFillUiCallback != this.mCallback) {
            return;
        }
        lambda$hideAll$10(autoFillUiCallback);
        this.mFillDialog = new DialogFillUi(this.mContext, fillResponse, autofillId, str, drawable, str2, componentName, this.mOverlayControl, this.mUiModeMgr.isNightMode(), new DialogFillUi.UiCallback() { // from class: com.android.server.autofill.ui.AutoFillUI.3
            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void onResponsePicked(FillResponse fillResponse2) {
                log(3);
                AutoFillUI.this.lambda$hideFillDialog$4(autoFillUiCallback);
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.authenticate(fillResponse2.getRequestId(), GnssNative.GNSS_AIDING_TYPE_ALL, fillResponse2.getAuthentication(), fillResponse2.getClientState(), 3);
                }
            }

            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void onShown() {
                autoFillUiCallback.onShown(3);
            }

            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void onDatasetPicked(Dataset dataset) {
                log(4);
                AutoFillUI.this.lambda$hideFillDialog$4(autoFillUiCallback);
                if (AutoFillUI.this.mCallback != null) {
                    AutoFillUI.this.mCallback.fill(fillResponse.getRequestId(), fillResponse.getDatasets().indexOf(dataset), dataset, 3);
                }
            }

            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void onDismissed() {
                log(5);
                AutoFillUI.this.lambda$hideFillDialog$4(autoFillUiCallback);
                autoFillUiCallback.requestShowSoftInput(autofillId);
                autoFillUiCallback.requestFallbackFromFillDialog();
            }

            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void onCanceled() {
                log(2);
                AutoFillUI.this.lambda$hideFillDialog$4(autoFillUiCallback);
                autoFillUiCallback.requestShowSoftInput(autofillId);
                autoFillUiCallback.requestFallbackFromFillDialog();
            }

            @Override // com.android.server.autofill.p007ui.DialogFillUi.UiCallback
            public void startIntentSender(IntentSender intentSender) {
                AutoFillUI.this.mCallback.startIntentSenderAndFinishSession(intentSender);
            }

            public final void log(int i) {
                logMaker.setType(i);
                AutoFillUI.this.mMetricsLogger.write(logMaker);
            }
        });
    }

    public void onPendingSaveUi(final int i, final IBinder iBinder) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$onPendingSaveUi$9(i, iBinder);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPendingSaveUi$9(int i, IBinder iBinder) {
        SaveUi saveUi = this.mSaveUi;
        if (saveUi != null) {
            saveUi.onPendingUi(i, iBinder);
            return;
        }
        Slog.w("AutofillUI", "onPendingSaveUi(" + i + "): no save ui");
    }

    public void hideAll(final AutoFillUiCallback autoFillUiCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$hideAll$10(autoFillUiCallback);
            }
        });
    }

    public void destroyAll(final PendingUi pendingUi, final AutoFillUiCallback autoFillUiCallback, final boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ui.AutoFillUI$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                AutoFillUI.this.lambda$destroyAll$11(pendingUi, autoFillUiCallback, z);
            }
        });
    }

    public boolean isSaveUiShowing() {
        SaveUi saveUi = this.mSaveUi;
        if (saveUi == null) {
            return false;
        }
        return saveUi.isShowing();
    }

    public boolean isFillDialogShowing() {
        DialogFillUi dialogFillUi = this.mFillDialog;
        if (dialogFillUi == null) {
            return false;
        }
        return dialogFillUi.isShowing();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("Autofill UI");
        printWriter.print("  ");
        printWriter.print("Night mode: ");
        printWriter.println(this.mUiModeMgr.isNightMode());
        if (this.mFillUi != null) {
            printWriter.print("  ");
            printWriter.println("showsFillUi: true");
            this.mFillUi.dump(printWriter, "    ");
        } else {
            printWriter.print("  ");
            printWriter.println("showsFillUi: false");
        }
        if (this.mSaveUi != null) {
            printWriter.print("  ");
            printWriter.println("showsSaveUi: true");
            this.mSaveUi.dump(printWriter, "    ");
        } else {
            printWriter.print("  ");
            printWriter.println("showsSaveUi: false");
        }
        if (this.mFillDialog != null) {
            printWriter.print("  ");
            printWriter.println("showsFillDialog: true");
            this.mFillDialog.dump(printWriter, "    ");
            return;
        }
        printWriter.print("  ");
        printWriter.println("showsFillDialog: false");
    }

    public final void hideFillUiUiThread(AutoFillUiCallback autoFillUiCallback, boolean z) {
        FillUi fillUi = this.mFillUi;
        if (fillUi != null) {
            if (autoFillUiCallback == null || autoFillUiCallback == this.mCallback) {
                fillUi.destroy(z);
                this.mFillUi = null;
            }
        }
    }

    public final PendingUi hideSaveUiUiThread(AutoFillUiCallback autoFillUiCallback) {
        if (Helper.sVerbose) {
            Slog.v("AutofillUI", "hideSaveUiUiThread(): mSaveUi=" + this.mSaveUi + ", callback=" + autoFillUiCallback + ", mCallback=" + this.mCallback);
        }
        SaveUi saveUi = this.mSaveUi;
        if (saveUi == null || this.mSaveUiCallback != autoFillUiCallback) {
            return null;
        }
        return saveUi.hide();
    }

    /* renamed from: hideFillDialogUiThread */
    public final void lambda$hideFillDialog$4(AutoFillUiCallback autoFillUiCallback) {
        DialogFillUi dialogFillUi = this.mFillDialog;
        if (dialogFillUi != null) {
            if (autoFillUiCallback == null || autoFillUiCallback == this.mCallback) {
                dialogFillUi.destroy();
                this.mFillDialog = null;
            }
        }
    }

    public final void destroySaveUiUiThread(PendingUi pendingUi, boolean z) {
        if (this.mSaveUi == null) {
            if (Helper.sDebug) {
                Slog.d("AutofillUI", "destroySaveUiUiThread(): already destroyed");
                return;
            }
            return;
        }
        if (Helper.sDebug) {
            Slog.d("AutofillUI", "destroySaveUiUiThread(): " + pendingUi);
        }
        this.mSaveUi.destroy();
        this.mSaveUi = null;
        this.mSaveUiCallback = null;
        if (pendingUi != null && z) {
            try {
                if (Helper.sDebug) {
                    Slog.d("AutofillUI", "destroySaveUiUiThread(): notifying client");
                }
                pendingUi.client.setSaveUiState(pendingUi.sessionId, false);
            } catch (RemoteException e) {
                Slog.e("AutofillUI", "Error notifying client to set save UI state to hidden: " + e);
            }
        }
        if (this.mCreateFillUiRunnable != null) {
            if (Helper.sDebug) {
                Slog.d("AutofillUI", "start the pending fill UI request..");
            }
            this.mHandler.post(this.mCreateFillUiRunnable);
            this.mCreateFillUiRunnable = null;
        }
    }

    /* renamed from: destroyAllUiThread */
    public final void lambda$destroyAll$11(PendingUi pendingUi, AutoFillUiCallback autoFillUiCallback, boolean z) {
        hideFillUiUiThread(autoFillUiCallback, z);
        lambda$hideFillDialog$4(autoFillUiCallback);
        destroySaveUiUiThread(pendingUi, z);
    }

    /* renamed from: hideAllUiThread */
    public final void lambda$hideAll$10(AutoFillUiCallback autoFillUiCallback) {
        hideFillUiUiThread(autoFillUiCallback, true);
        lambda$hideFillDialog$4(autoFillUiCallback);
        PendingUi hideSaveUiUiThread = hideSaveUiUiThread(autoFillUiCallback);
        if (hideSaveUiUiThread == null || hideSaveUiUiThread.getState() != 4) {
            return;
        }
        if (Helper.sDebug) {
            Slog.d("AutofillUI", "hideAllUiThread(): destroying Save UI because pending restoration is finished");
        }
        destroySaveUiUiThread(hideSaveUiUiThread, true);
    }
}
