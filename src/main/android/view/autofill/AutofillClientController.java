package android.view.autofill;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.text.TextUtils;
import android.util.Dumpable;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import android.view.autofill.AutofillManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes4.dex */
public final class AutofillClientController implements AutofillManager.AutofillClient, Dumpable {
    public static final String AUTOFILL_RESET_NEEDED = "@android:autofillResetNeeded";
    public static final String AUTO_FILL_AUTH_WHO_PREFIX = "@android:autoFillAuth:";
    public static final String DUMPABLE_NAME = "AutofillManager";
    public static final String LAST_AUTOFILL_ID = "android:lastAutofillId";
    private static final String TAG = "AutofillClientController";
    private final Activity mActivity;
    private boolean mAutoFillIgnoreFirstResumePause;
    private boolean mAutoFillResetNeeded;
    private AutofillManager mAutofillManager;
    private AutofillPopupWindow mAutofillPopupWindow;
    public int mLastAutofillId = View.LAST_APP_AUTOFILL_ID;
    private static final String LOG_TAG = "autofill_client";
    public static final boolean DEBUG = Log.isLoggable(LOG_TAG, 3);

    public AutofillClientController(Activity activity) {
        this.mActivity = activity;
    }

    private AutofillManager getAutofillManager() {
        if (this.mAutofillManager == null) {
            this.mAutofillManager = (AutofillManager) this.mActivity.getSystemService(AutofillManager.class);
        }
        return this.mAutofillManager;
    }

    public void onActivityAttached(Application application) {
        this.mActivity.setAutofillOptions(application.getAutofillOptions());
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        this.mAutoFillResetNeeded = savedInstanceState.getBoolean(AUTOFILL_RESET_NEEDED, false);
        this.mLastAutofillId = savedInstanceState.getInt(LAST_AUTOFILL_ID, View.LAST_APP_AUTOFILL_ID);
        if (this.mAutoFillResetNeeded) {
            getAutofillManager().onCreate(savedInstanceState);
        }
    }

    public void onActivityStarted() {
        if (this.mAutoFillResetNeeded) {
            getAutofillManager().onVisibleForAutofill();
        }
    }

    public void onActivityResumed() {
        View focus;
        enableAutofillCompatibilityIfNeeded();
        if (this.mAutoFillResetNeeded && !this.mAutoFillIgnoreFirstResumePause && (focus = this.mActivity.getCurrentFocus()) != null && focus.canNotifyAutofillEnterExitEvent()) {
            getAutofillManager().notifyViewEntered(focus);
        }
    }

    public void onActivityPerformResume(boolean followedByPause) {
        if (this.mAutoFillResetNeeded) {
            this.mAutoFillIgnoreFirstResumePause = followedByPause;
            if (followedByPause && DEBUG) {
                Slog.m92v(TAG, "autofill will ignore first pause when relaunching " + this);
            }
        }
    }

    public void onActivityPaused() {
        if (this.mAutoFillResetNeeded) {
            if (!this.mAutoFillIgnoreFirstResumePause) {
                if (DEBUG) {
                    Log.m106v(TAG, "autofill notifyViewExited " + this);
                }
                View focus = this.mActivity.getCurrentFocus();
                if (focus != null && focus.canNotifyAutofillEnterExitEvent()) {
                    getAutofillManager().notifyViewExited(focus);
                    return;
                }
                return;
            }
            if (DEBUG) {
                Log.m106v(TAG, "autofill got first pause " + this);
            }
            this.mAutoFillIgnoreFirstResumePause = false;
        }
    }

    public void onActivityStopped(Intent intent, boolean changingConfigurations) {
        if (this.mAutoFillResetNeeded) {
            getAutofillManager().onInvisibleForAutofill(!changingConfigurations);
        } else if (intent != null && intent.hasExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN) && intent.hasExtra(AutofillManager.EXTRA_RESTORE_CROSS_ACTIVITY)) {
            restoreAutofillSaveUi(intent);
        }
    }

    public void onActivityDestroyed() {
        if (this.mActivity.isFinishing() && this.mAutoFillResetNeeded) {
            getAutofillManager().onActivityFinishing();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAST_AUTOFILL_ID, this.mLastAutofillId);
        if (this.mAutoFillResetNeeded) {
            outState.putBoolean(AUTOFILL_RESET_NEEDED, true);
            getAutofillManager().onSaveInstanceState(outState);
        }
    }

    public void onActivityFinish(Intent intent) {
        if (intent != null && intent.hasExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN)) {
            restoreAutofillSaveUi(intent);
        }
    }

    public void onActivityBackPressed(Intent intent) {
        if (intent != null && intent.hasExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN)) {
            restoreAutofillSaveUi(intent);
        }
    }

    public void onDispatchActivityResult(int requestCode, int resultCode, Intent data) {
        Intent resultData = resultCode == -1 ? data : null;
        getAutofillManager().onAuthenticationResult(requestCode, resultData, this.mActivity.getCurrentFocus());
    }

    public void onStartActivity(Intent startIntent, Intent cachedIntent) {
        if (cachedIntent != null && cachedIntent.hasExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN) && cachedIntent.hasExtra(AutofillManager.EXTRA_RESTORE_CROSS_ACTIVITY) && TextUtils.equals(this.mActivity.getPackageName(), startIntent.resolveActivity(this.mActivity.getPackageManager()).getPackageName())) {
            IBinder token = cachedIntent.getIBinderExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN);
            cachedIntent.removeExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN);
            cachedIntent.removeExtra(AutofillManager.EXTRA_RESTORE_CROSS_ACTIVITY);
            startIntent.putExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN, token);
            startIntent.putExtra(AutofillManager.EXTRA_RESTORE_CROSS_ACTIVITY, true);
        }
    }

    public void restoreAutofillSaveUi(Intent intent) {
        IBinder token = intent.getIBinderExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN);
        intent.removeExtra(AutofillManager.EXTRA_RESTORE_SESSION_TOKEN);
        intent.removeExtra(AutofillManager.EXTRA_RESTORE_CROSS_ACTIVITY);
        getAutofillManager().onPendingSaveUi(2, token);
    }

    public void enableAutofillCompatibilityIfNeeded() {
        AutofillManager afm;
        if (this.mActivity.isAutofillCompatibilityEnabled() && (afm = (AutofillManager) this.mActivity.getSystemService(AutofillManager.class)) != null) {
            afm.enableCompatibilityMode();
        }
    }

    @Override // android.util.Dumpable
    public String getDumpableName() {
        return DUMPABLE_NAME;
    }

    @Override // android.util.Dumpable
    public void dump(PrintWriter writer, String[] args) {
        AutofillManager afm = getAutofillManager();
        if (afm != null) {
            afm.dump("", writer);
            writer.print("");
            writer.print("Autofill Compat Mode: ");
            writer.println(this.mActivity.isAutofillCompatibilityEnabled());
            return;
        }
        writer.print("");
        writer.println("No AutofillManager");
    }

    public int getNextAutofillId() {
        if (this.mLastAutofillId == 2147483646) {
            this.mLastAutofillId = View.LAST_APP_AUTOFILL_ID;
        }
        int i = this.mLastAutofillId + 1;
        this.mLastAutofillId = i;
        return i;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public AutofillId autofillClientGetNextAutofillId() {
        return new AutofillId(getNextAutofillId());
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean autofillClientIsCompatibilityModeEnabled() {
        return this.mActivity.isAutofillCompatibilityEnabled();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean autofillClientIsVisibleForAutofill() {
        return this.mActivity.isVisibleForAutofill();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public ComponentName autofillClientGetComponentName() {
        return this.mActivity.getComponentName();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public IBinder autofillClientGetActivityToken() {
        return this.mActivity.getActivityToken();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean[] autofillClientGetViewVisibility(AutofillId[] autofillIds) {
        int autofillIdCount = autofillIds.length;
        boolean[] visible = new boolean[autofillIdCount];
        for (int i = 0; i < autofillIdCount; i++) {
            AutofillId autofillId = autofillIds[i];
            View view = autofillClientFindViewByAutofillIdTraversal(autofillId);
            if (view != null) {
                if (!autofillId.isVirtualInt()) {
                    visible[i] = view.isVisibleToUser();
                } else {
                    visible[i] = view.isVisibleToUserForAutofill(autofillId.getVirtualChildIntId());
                }
            }
        }
        if (Helper.sVerbose) {
            Log.m106v(TAG, "autofillClientGetViewVisibility(): " + Arrays.toString(visible));
        }
        return visible;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public View autofillClientFindViewByAccessibilityIdTraversal(int viewId, int windowId) {
        View view;
        ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = roots.get(rootNum).getView();
            if (rootView != null && rootView.getAccessibilityWindowId() == windowId && (view = rootView.findViewByAccessibilityIdTraversal(viewId)) != null) {
                return view;
            }
        }
        return null;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public View autofillClientFindViewByAutofillIdTraversal(AutofillId autofillId) {
        View view;
        ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = roots.get(rootNum).getView();
            if (rootView != null && (view = rootView.findViewByAutofillIdTraversal(autofillId.getViewId())) != null) {
                return view;
            }
        }
        return null;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public View[] autofillClientFindViewsByAutofillIdTraversal(AutofillId[] autofillIds) {
        View[] views = new View[autofillIds.length];
        ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = roots.get(rootNum).getView();
            if (rootView != null) {
                int viewCount = autofillIds.length;
                for (int viewNum = 0; viewNum < viewCount; viewNum++) {
                    if (views[viewNum] == null) {
                        views[viewNum] = rootView.findViewByAutofillIdTraversal(autofillIds[viewNum].getViewId());
                    }
                }
            }
        }
        return views;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean autofillClientIsFillUiShowing() {
        AutofillPopupWindow autofillPopupWindow = this.mAutofillPopupWindow;
        return autofillPopupWindow != null && autofillPopupWindow.isShowing();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean autofillClientRequestHideFillUi() {
        AutofillPopupWindow autofillPopupWindow = this.mAutofillPopupWindow;
        if (autofillPopupWindow == null) {
            return false;
        }
        autofillPopupWindow.dismiss();
        this.mAutofillPopupWindow = null;
        return true;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean autofillClientRequestShowFillUi(View anchor, int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) {
        boolean wasShowing;
        AutofillPopupWindow autofillPopupWindow = this.mAutofillPopupWindow;
        if (autofillPopupWindow == null) {
            wasShowing = false;
            this.mAutofillPopupWindow = new AutofillPopupWindow(presenter);
        } else {
            wasShowing = autofillPopupWindow.isShowing();
        }
        this.mAutofillPopupWindow.update(anchor, 0, 0, width, height, anchorBounds);
        return !wasShowing && this.mAutofillPopupWindow.isShowing();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public void autofillClientDispatchUnhandledKey(View anchor, KeyEvent keyEvent) {
        ViewRootImpl rootImpl = anchor.getViewRootImpl();
        if (rootImpl != null) {
            rootImpl.dispatchKeyFromAutofill(keyEvent);
        }
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public boolean isDisablingEnterExitEventForAutofill() {
        return this.mAutoFillIgnoreFirstResumePause || !this.mActivity.isResumed();
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public void autofillClientResetableStateAvailable() {
        this.mAutoFillResetNeeded = true;
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public void autofillClientRunOnUiThread(Runnable action) {
        this.mActivity.runOnUiThread(action);
    }

    @Override // android.view.autofill.AutofillManager.AutofillClient
    public void autofillClientAuthenticate(int authenticationId, IntentSender intent, Intent fillInIntent, boolean authenticateInline) {
        try {
            this.mActivity.startIntentSenderForResult(intent, AUTO_FILL_AUTH_WHO_PREFIX, authenticationId, fillInIntent, 0, 0, (Bundle) null);
        } catch (IntentSender.SendIntentException e) {
            Log.m109e(TAG, "authenticate() failed for intent:" + intent, e);
        }
    }
}
