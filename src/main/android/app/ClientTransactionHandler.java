package android.app;

import android.app.ActivityThread;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.ClientTransactionItem;
import android.app.servertransaction.PendingTransactionActions;
import android.app.servertransaction.TransactionExecutor;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Configuration;
import android.p008os.IBinder;
import android.util.MergedConfiguration;
import android.view.SurfaceControl;
import android.window.SplashScreenView;
import com.android.internal.content.ReferrerIntent;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class ClientTransactionHandler {
    private boolean mIsExecutingLocalTransaction;

    public abstract void countLaunchingActivities(int i);

    public abstract Map<IBinder, ClientTransactionItem> getActivitiesToBeDestroyed();

    public abstract Activity getActivity(IBinder iBinder);

    public abstract ActivityThread.ActivityClientRecord getActivityClient(IBinder iBinder);

    public abstract LoadedApk getPackageInfoNoCheck(ApplicationInfo applicationInfo);

    abstract TransactionExecutor getTransactionExecutor();

    public abstract void handleActivityConfigurationChanged(ActivityThread.ActivityClientRecord activityClientRecord, Configuration configuration, int i);

    public abstract void handleAttachSplashScreenView(ActivityThread.ActivityClientRecord activityClientRecord, SplashScreenView.SplashScreenViewParcelable splashScreenViewParcelable, SurfaceControl surfaceControl);

    public abstract void handleConfigurationChanged(Configuration configuration, int i);

    public abstract void handleDestroyActivity(ActivityThread.ActivityClientRecord activityClientRecord, boolean z, int i, boolean z2, String str);

    public abstract Activity handleLaunchActivity(ActivityThread.ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions, int i, Intent intent);

    public abstract void handleNewIntent(ActivityThread.ActivityClientRecord activityClientRecord, List<ReferrerIntent> list);

    public abstract void handlePauseActivity(ActivityThread.ActivityClientRecord activityClientRecord, boolean z, boolean z2, int i, boolean z3, PendingTransactionActions pendingTransactionActions, String str);

    public abstract void handlePictureInPictureRequested(ActivityThread.ActivityClientRecord activityClientRecord);

    public abstract void handlePictureInPictureStateChanged(ActivityThread.ActivityClientRecord activityClientRecord, PictureInPictureUiState pictureInPictureUiState);

    public abstract void handleRelaunchActivity(ActivityThread.ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions);

    public abstract void handleResumeActivity(ActivityThread.ActivityClientRecord activityClientRecord, boolean z, boolean z2, boolean z3, String str);

    public abstract void handleSendResult(ActivityThread.ActivityClientRecord activityClientRecord, List<ResultInfo> list, String str);

    public abstract void handleStartActivity(ActivityThread.ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions, ActivityOptions activityOptions);

    public abstract void handleStopActivity(ActivityThread.ActivityClientRecord activityClientRecord, int i, PendingTransactionActions pendingTransactionActions, boolean z, String str);

    public abstract void handleTopResumedActivityChanged(ActivityThread.ActivityClientRecord activityClientRecord, boolean z, String str);

    public abstract boolean isHandleSplashScreenExit(IBinder iBinder);

    public abstract void performRestartActivity(ActivityThread.ActivityClientRecord activityClientRecord, boolean z);

    public abstract ActivityThread.ActivityClientRecord prepareRelaunchActivity(IBinder iBinder, List<ResultInfo> list, List<ReferrerIntent> list2, int i, MergedConfiguration mergedConfiguration, boolean z);

    public abstract void reportRefresh(ActivityThread.ActivityClientRecord activityClientRecord);

    public abstract void reportRelaunch(ActivityThread.ActivityClientRecord activityClientRecord);

    public abstract void reportStop(PendingTransactionActions pendingTransactionActions);

    abstract void sendMessage(int i, Object obj);

    public abstract void updatePendingActivityConfiguration(IBinder iBinder, Configuration configuration);

    public abstract void updatePendingConfiguration(Configuration configuration);

    public abstract void updateProcessState(int i, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleTransaction(ClientTransaction transaction) {
        transaction.preExecute(this);
        sendMessage(159, transaction);
    }

    public void executeTransaction(ClientTransaction transaction) {
        this.mIsExecutingLocalTransaction = true;
        try {
            transaction.preExecute(this);
            getTransactionExecutor().execute(transaction);
        } finally {
            this.mIsExecutingLocalTransaction = false;
            transaction.recycle();
        }
    }

    public boolean isExecutingLocalTransaction() {
        return this.mIsExecutingLocalTransaction;
    }
}
