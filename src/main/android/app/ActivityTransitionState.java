package android.app;

import android.app.ActivityTransitionState;
import android.app.ExitTransitionCoordinator;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.ResultReceiver;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.android.internal.view.OneShotPreDrawListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ActivityTransitionState {
    private static final String EXITING_MAPPED_FROM = "android:exitingMappedFrom";
    private static final String EXITING_MAPPED_TO = "android:exitingMappedTo";
    private static final String PENDING_EXIT_SHARED_ELEMENTS = "android:pendingExitSharedElements";
    private ExitTransitionCoordinator mCalledExitCoordinator;
    private ActivityOptions mEnterActivityOptions;
    private EnterTransitionCoordinator mEnterTransitionCoordinator;
    private SparseArray<WeakReference<ExitTransitionCoordinator>> mExitTransitionCoordinators;
    private int mExitTransitionCoordinatorsKey = 1;
    private ArrayList<String> mExitingFrom;
    private ArrayList<String> mExitingTo;
    private ArrayList<View> mExitingToView;
    private boolean mHasExited;
    private boolean mIsEnterPostponed;
    private boolean mIsEnterTriggered;
    private ArrayList<String> mPendingExitNames;
    private ExitTransitionCoordinator mReturnExitCoordinator;

    public int addExitTransitionCoordinator(ExitTransitionCoordinator exitTransitionCoordinator) {
        if (this.mExitTransitionCoordinators == null) {
            this.mExitTransitionCoordinators = new SparseArray<>();
        }
        WeakReference<ExitTransitionCoordinator> ref = new WeakReference<>(exitTransitionCoordinator);
        for (int i = this.mExitTransitionCoordinators.size() - 1; i >= 0; i--) {
            WeakReference<ExitTransitionCoordinator> oldRef = this.mExitTransitionCoordinators.valueAt(i);
            if (oldRef.refersTo(null)) {
                this.mExitTransitionCoordinators.removeAt(i);
            }
        }
        int i2 = this.mExitTransitionCoordinatorsKey;
        this.mExitTransitionCoordinatorsKey = i2 + 1;
        this.mExitTransitionCoordinators.append(i2, ref);
        return i2;
    }

    public void readState(Bundle bundle) {
        if (bundle != null) {
            EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
            if (enterTransitionCoordinator == null || enterTransitionCoordinator.isReturning()) {
                this.mPendingExitNames = bundle.getStringArrayList(PENDING_EXIT_SHARED_ELEMENTS);
            }
            if (this.mEnterTransitionCoordinator == null) {
                this.mExitingFrom = bundle.getStringArrayList(EXITING_MAPPED_FROM);
                this.mExitingTo = bundle.getStringArrayList(EXITING_MAPPED_TO);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getPendingExitNames() {
        EnterTransitionCoordinator enterTransitionCoordinator;
        if (this.mPendingExitNames == null && (enterTransitionCoordinator = this.mEnterTransitionCoordinator) != null && !enterTransitionCoordinator.isReturning()) {
            this.mPendingExitNames = this.mEnterTransitionCoordinator.getPendingExitSharedElementNames();
        }
        return this.mPendingExitNames;
    }

    public void saveState(Bundle bundle) {
        ArrayList<String> pendingExitNames = getPendingExitNames();
        if (pendingExitNames != null) {
            bundle.putStringArrayList(PENDING_EXIT_SHARED_ELEMENTS, pendingExitNames);
        }
        ArrayList<String> arrayList = this.mExitingFrom;
        if (arrayList != null) {
            bundle.putStringArrayList(EXITING_MAPPED_FROM, arrayList);
            bundle.putStringArrayList(EXITING_MAPPED_TO, this.mExitingTo);
        }
    }

    public void setEnterActivityOptions(Activity activity, ActivityOptions options) {
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        window.getDecorView();
        if (window.hasFeature(13) && options != null && this.mEnterActivityOptions == null && this.mEnterTransitionCoordinator == null && options.getAnimationType() == 5) {
            this.mEnterActivityOptions = options;
            this.mIsEnterTriggered = false;
            if (options.isReturning()) {
                restoreExitedViews();
                int result = this.mEnterActivityOptions.getResultCode();
                if (result != 0) {
                    Intent intent = this.mEnterActivityOptions.getResultData();
                    if (intent != null) {
                        intent.setExtrasClassLoader(activity.getClassLoader());
                    }
                    activity.onActivityReenter(result, intent);
                }
            }
        }
    }

    public void enterReady(Activity activity) {
        ActivityOptions activityOptions = this.mEnterActivityOptions;
        if (activityOptions == null || this.mIsEnterTriggered) {
            return;
        }
        this.mIsEnterTriggered = true;
        this.mHasExited = false;
        ArrayList<String> sharedElementNames = activityOptions.getSharedElementNames();
        ResultReceiver resultReceiver = this.mEnterActivityOptions.getResultReceiver();
        boolean isReturning = this.mEnterActivityOptions.isReturning();
        if (isReturning) {
            restoreExitedViews();
            activity.getWindow().getDecorView().setVisibility(0);
        }
        getPendingExitNames();
        this.mEnterTransitionCoordinator = new EnterTransitionCoordinator(activity, resultReceiver, sharedElementNames, this.mEnterActivityOptions.isReturning(), this.mEnterActivityOptions.isCrossTask());
        if (this.mEnterActivityOptions.isCrossTask()) {
            this.mExitingFrom = new ArrayList<>(this.mEnterActivityOptions.getSharedElementNames());
            this.mExitingTo = new ArrayList<>(this.mEnterActivityOptions.getSharedElementNames());
        }
        if (!this.mIsEnterPostponed) {
            startEnter();
        }
    }

    public void postponeEnterTransition() {
        this.mIsEnterPostponed = true;
    }

    public void startPostponedEnterTransition() {
        if (this.mIsEnterPostponed) {
            this.mIsEnterPostponed = false;
            if (this.mEnterTransitionCoordinator != null) {
                startEnter();
            }
        }
    }

    private void startEnter() {
        if (this.mEnterTransitionCoordinator.isReturning()) {
            ArrayList<View> arrayList = this.mExitingToView;
            if (arrayList != null) {
                this.mEnterTransitionCoordinator.viewInstancesReady(this.mExitingFrom, this.mExitingTo, arrayList);
            } else {
                this.mEnterTransitionCoordinator.namedViewsReady(this.mExitingFrom, this.mExitingTo);
            }
        } else {
            this.mEnterTransitionCoordinator.namedViewsReady(null, null);
            this.mPendingExitNames = null;
        }
        this.mExitingFrom = null;
        this.mExitingTo = null;
        this.mExitingToView = null;
        this.mEnterActivityOptions = null;
    }

    public void onStop(Activity activity) {
        restoreExitedViews();
        if (this.mEnterTransitionCoordinator != null) {
            getPendingExitNames();
            this.mEnterTransitionCoordinator.stop();
            this.mEnterTransitionCoordinator = null;
        }
        ExitTransitionCoordinator exitTransitionCoordinator = this.mReturnExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.stop(activity);
            this.mReturnExitCoordinator = null;
        }
    }

    public void onResume(Activity activity) {
        if (this.mEnterTransitionCoordinator == null || activity.isTopOfTask()) {
            restoreExitedViews();
            restoreReenteringViews();
            return;
        }
        activity.mHandler.postDelayed(new RunnableC01261(), 1000L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.app.ActivityTransitionState$1 */
    /* loaded from: classes.dex */
    public class RunnableC01261 implements Runnable {
        RunnableC01261() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ActivityTransitionState.this.mEnterTransitionCoordinator == null || ActivityTransitionState.this.mEnterTransitionCoordinator.isWaitingForRemoteExit()) {
                ActivityTransitionState.this.restoreExitedViews();
                ActivityTransitionState.this.restoreReenteringViews();
            } else if (ActivityTransitionState.this.mEnterTransitionCoordinator.isReturning()) {
                ActivityTransitionState.this.mEnterTransitionCoordinator.runAfterTransitionsComplete(new Runnable() { // from class: android.app.ActivityTransitionState$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityTransitionState.RunnableC01261.this.lambda$run$0();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            ActivityTransitionState.this.getPendingExitNames();
            ActivityTransitionState.this.mEnterTransitionCoordinator = null;
        }
    }

    public void clear() {
        this.mPendingExitNames = null;
        this.mExitingFrom = null;
        this.mExitingTo = null;
        this.mExitingToView = null;
        this.mCalledExitCoordinator = null;
        this.mEnterTransitionCoordinator = null;
        this.mEnterActivityOptions = null;
        this.mExitTransitionCoordinators = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreExitedViews() {
        ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.resetViews();
            this.mCalledExitCoordinator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreReenteringViews() {
        EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
        if (enterTransitionCoordinator != null && enterTransitionCoordinator.isReturning() && !this.mEnterTransitionCoordinator.isCrossTask()) {
            this.mEnterTransitionCoordinator.forceViewsToAppear();
            this.mExitingFrom = null;
            this.mExitingTo = null;
            this.mExitingToView = null;
        }
    }

    public boolean startExitBackTransition(final Activity activity) {
        Transition enterViewsTransition;
        ViewGroup decor;
        boolean delayExitBack;
        ArrayList<String> pendingExitNames = getPendingExitNames();
        if (pendingExitNames == null || this.mCalledExitCoordinator != null) {
            return false;
        }
        if (!this.mHasExited) {
            this.mHasExited = true;
            EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
            if (enterTransitionCoordinator == null) {
                enterViewsTransition = null;
                decor = null;
                delayExitBack = false;
            } else {
                Transition enterViewsTransition2 = enterTransitionCoordinator.getEnterViewsTransition();
                ViewGroup decor2 = this.mEnterTransitionCoordinator.getDecor();
                boolean delayExitBack2 = this.mEnterTransitionCoordinator.cancelEnter();
                this.mEnterTransitionCoordinator = null;
                if (enterViewsTransition2 != null && decor2 != null) {
                    enterViewsTransition2.pause(decor2);
                }
                enterViewsTransition = enterViewsTransition2;
                decor = decor2;
                delayExitBack = delayExitBack2;
            }
            this.mReturnExitCoordinator = new ExitTransitionCoordinator(new ExitTransitionCoordinator.ActivityExitTransitionCallbacks(activity), activity.getWindow(), activity.mEnterTransitionListener, pendingExitNames, null, null, true);
            if (enterViewsTransition != null && decor != null) {
                enterViewsTransition.resume(decor);
            }
            if (delayExitBack && decor != null) {
                OneShotPreDrawListener.add(decor, new Runnable() { // from class: android.app.ActivityTransitionState$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityTransitionState.this.lambda$startExitBackTransition$0(activity);
                    }
                });
            } else {
                this.mReturnExitCoordinator.startExit(activity);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startExitBackTransition$0(Activity activity) {
        ExitTransitionCoordinator exitTransitionCoordinator = this.mReturnExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.startExit(activity);
        }
    }

    public boolean isTransitionRunning() {
        EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
        if (enterTransitionCoordinator == null || !enterTransitionCoordinator.isTransitionRunning()) {
            ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
            if (exitTransitionCoordinator == null || !exitTransitionCoordinator.isTransitionRunning()) {
                ExitTransitionCoordinator exitTransitionCoordinator2 = this.mReturnExitCoordinator;
                return exitTransitionCoordinator2 != null && exitTransitionCoordinator2.isTransitionRunning();
            }
            return true;
        }
        return true;
    }

    public void startExitOutTransition(Activity activity, Bundle options) {
        getPendingExitNames();
        this.mEnterTransitionCoordinator = null;
        if (!activity.getWindow().hasFeature(13) || this.mExitTransitionCoordinators == null) {
            return;
        }
        ActivityOptions activityOptions = new ActivityOptions(options);
        if (activityOptions.getAnimationType() == 5) {
            int key = activityOptions.getExitCoordinatorKey();
            int index = this.mExitTransitionCoordinators.indexOfKey(key);
            if (index >= 0) {
                this.mCalledExitCoordinator = this.mExitTransitionCoordinators.valueAt(index).get();
                this.mExitTransitionCoordinators.removeAt(index);
                ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
                if (exitTransitionCoordinator != null) {
                    this.mExitingFrom = exitTransitionCoordinator.getAcceptedNames();
                    this.mExitingTo = this.mCalledExitCoordinator.getMappedNames();
                    this.mExitingToView = this.mCalledExitCoordinator.copyMappedViews();
                    this.mCalledExitCoordinator.startExit();
                }
            }
        }
    }
}
