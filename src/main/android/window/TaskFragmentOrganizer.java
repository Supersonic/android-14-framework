package android.window;

import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.view.RemoteAnimationDefinition;
import android.window.ITaskFragmentOrganizer;
import android.window.TaskFragmentOrganizer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public class TaskFragmentOrganizer extends WindowOrganizer {
    public static final String KEY_ERROR_CALLBACK_OP_TYPE = "operation_type";
    public static final String KEY_ERROR_CALLBACK_TASK_FRAGMENT_INFO = "task_fragment_info";
    public static final String KEY_ERROR_CALLBACK_THROWABLE = "fragment_throwable";
    public static final int TASK_FRAGMENT_TRANSIT_CHANGE = 6;
    public static final int TASK_FRAGMENT_TRANSIT_CLOSE = 2;
    public static final int TASK_FRAGMENT_TRANSIT_NONE = 0;
    public static final int TASK_FRAGMENT_TRANSIT_OPEN = 1;
    private final Executor mExecutor;
    private final ITaskFragmentOrganizer mInterface;
    private final TaskFragmentOrganizerToken mToken;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface TaskFragmentTransitionType {
    }

    public static Bundle putErrorInfoInBundle(Throwable exception, TaskFragmentInfo info, int opType) {
        Bundle errorBundle = new Bundle();
        errorBundle.putSerializable(KEY_ERROR_CALLBACK_THROWABLE, exception);
        if (info != null) {
            errorBundle.putParcelable(KEY_ERROR_CALLBACK_TASK_FRAGMENT_INFO, info);
        }
        errorBundle.putInt(KEY_ERROR_CALLBACK_OP_TYPE, opType);
        return errorBundle;
    }

    public TaskFragmentOrganizer(Executor executor) {
        BinderC39641 binderC39641 = new BinderC39641();
        this.mInterface = binderC39641;
        this.mToken = new TaskFragmentOrganizerToken(binderC39641);
        this.mExecutor = executor;
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    public void registerOrganizer() {
        try {
            getController().registerOrganizer(this.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterOrganizer() {
        try {
            getController().unregisterOrganizer(this.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerRemoteAnimations(RemoteAnimationDefinition definition) {
        try {
            getController().registerRemoteAnimations(this.mInterface, definition);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterRemoteAnimations() {
        try {
            getController().unregisterRemoteAnimations(this.mInterface);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onTransactionHandled(IBinder transactionToken, WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) {
        wct.setTaskFragmentOrganizer(this.mInterface);
        try {
            getController().onTransactionHandled(transactionToken, wct, transitionType, shouldApplyIndependently);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.window.WindowOrganizer
    public void applyTransaction(WindowContainerTransaction wct) {
        throw new RuntimeException("Not allowed!");
    }

    public void applyTransaction(WindowContainerTransaction wct, int transitionType, boolean shouldApplyIndependently) {
        if (wct.isEmpty()) {
            return;
        }
        wct.setTaskFragmentOrganizer(this.mInterface);
        try {
            getController().applyTransaction(wct, transitionType, shouldApplyIndependently);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onTransactionReady(TaskFragmentTransaction transaction) {
        onTransactionHandled(transaction.getTransactionToken(), new WindowContainerTransaction(), 0, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.window.TaskFragmentOrganizer$1 */
    /* loaded from: classes4.dex */
    public class BinderC39641 extends ITaskFragmentOrganizer.Stub {
        BinderC39641() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTransactionReady$0(TaskFragmentTransaction transaction) {
            TaskFragmentOrganizer.this.onTransactionReady(transaction);
        }

        @Override // android.window.ITaskFragmentOrganizer
        public void onTransactionReady(final TaskFragmentTransaction transaction) {
            TaskFragmentOrganizer.this.mExecutor.execute(new Runnable() { // from class: android.window.TaskFragmentOrganizer$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TaskFragmentOrganizer.BinderC39641.this.lambda$onTransactionReady$0(transaction);
                }
            });
        }
    }

    public TaskFragmentOrganizerToken getOrganizerToken() {
        return this.mToken;
    }

    private ITaskFragmentOrganizerController getController() {
        try {
            return getWindowOrganizerController().getTaskFragmentOrganizerController();
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean isActivityEmbedded(IBinder activityToken) {
        try {
            return getController().isActivityEmbedded(activityToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
