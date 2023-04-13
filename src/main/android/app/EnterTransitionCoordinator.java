package android.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityTransitionCoordinator;
import android.app.EnterTransitionCoordinator;
import android.app.SharedElementCallback;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.ResultReceiver;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.Window;
import com.android.internal.view.OneShotPreDrawListener;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EnterTransitionCoordinator extends ActivityTransitionCoordinator {
    private static final int MIN_ANIMATION_FRAMES = 2;
    private static final String TAG = "EnterTransitionCoordinator";
    private Activity mActivity;
    private boolean mAreViewsReady;
    private ObjectAnimator mBackgroundAnimator;
    private Transition mEnterViewsTransition;
    private boolean mHasStopped;
    private boolean mIsCanceled;
    private final boolean mIsCrossTask;
    private boolean mIsExitTransitionComplete;
    private boolean mIsReadyForTransition;
    private boolean mIsTaskRoot;
    private boolean mIsViewsTransitionStarted;
    private Runnable mOnTransitionComplete;
    private ArrayList<String> mPendingExitNames;
    private Drawable mReplacedBackground;
    private boolean mSharedElementTransitionStarted;
    private Bundle mSharedElementsBundle;
    private OneShotPreDrawListener mViewsReadyListener;
    private boolean mWasOpaque;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EnterTransitionCoordinator(Activity activity, ResultReceiver resultReceiver, ArrayList<String> sharedElementNames, boolean isReturning, boolean isCrossTask) {
        super(activity.getWindow(), sharedElementNames, getListener(activity, isReturning && !isCrossTask), isReturning);
        this.mActivity = activity;
        this.mIsCrossTask = isCrossTask;
        setResultReceiver(resultReceiver);
        prepareEnter();
        Bundle resultReceiverBundle = new Bundle();
        resultReceiverBundle.putParcelable("android:remoteReceiver", this);
        this.mResultReceiver.send(100, resultReceiverBundle);
        final View decorView = getDecor();
        if (decorView != null) {
            final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.app.EnterTransitionCoordinator.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (EnterTransitionCoordinator.this.mIsReadyForTransition) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                            return false;
                        }
                        decorView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                    return false;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCrossTask() {
        return this.mIsCrossTask;
    }

    public void viewInstancesReady(ArrayList<String> accepted, ArrayList<String> localNames, ArrayList<View> localViews) {
        boolean remap = false;
        for (int i = 0; i < localViews.size(); i++) {
            View view = localViews.get(i);
            if (!TextUtils.equals(view.getTransitionName(), localNames.get(i)) || !view.isAttachedToWindow()) {
                remap = true;
                break;
            }
        }
        if (remap) {
            triggerViewsReady(mapNamedElements(accepted, localNames));
        } else {
            triggerViewsReady(mapSharedElements(accepted, localViews));
        }
    }

    public void namedViewsReady(ArrayList<String> accepted, ArrayList<String> localNames) {
        triggerViewsReady(mapNamedElements(accepted, localNames));
    }

    public Transition getEnterViewsTransition() {
        return this.mEnterViewsTransition;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ActivityTransitionCoordinator
    public void viewsReady(ArrayMap<String, View> sharedElements) {
        super.viewsReady(sharedElements);
        this.mIsReadyForTransition = true;
        hideViews(this.mSharedElements);
        Transition viewsTransition = getViewsTransition();
        if (viewsTransition != null && this.mTransitioningViews != null) {
            removeExcludedViews(viewsTransition, this.mTransitioningViews);
            stripOffscreenViews();
            hideViews(this.mTransitioningViews);
        }
        if (this.mIsReturning) {
            sendSharedElementDestination();
        } else {
            moveSharedElementsToOverlay();
        }
        if (this.mSharedElementsBundle != null) {
            onTakeSharedElements();
        }
    }

    private void triggerViewsReady(final ArrayMap<String, View> sharedElements) {
        if (this.mAreViewsReady) {
            return;
        }
        this.mAreViewsReady = true;
        ViewGroup decor = getDecor();
        if (decor == null || (decor.isAttachedToWindow() && (sharedElements.isEmpty() || !sharedElements.valueAt(0).isLayoutRequested()))) {
            viewsReady(sharedElements);
            return;
        }
        this.mViewsReadyListener = OneShotPreDrawListener.add(decor, new Runnable() { // from class: android.app.EnterTransitionCoordinator$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                EnterTransitionCoordinator.this.lambda$triggerViewsReady$0(sharedElements);
            }
        });
        decor.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerViewsReady$0(ArrayMap sharedElements) {
        this.mViewsReadyListener = null;
        viewsReady(sharedElements);
    }

    private ArrayMap<String, View> mapNamedElements(ArrayList<String> accepted, ArrayList<String> localNames) {
        View view;
        ArrayMap<String, View> sharedElements = new ArrayMap<>();
        ViewGroup decorView = getDecor();
        if (decorView != null) {
            decorView.findNamedViews(sharedElements);
        }
        if (accepted != null) {
            for (int i = 0; i < localNames.size(); i++) {
                String localName = localNames.get(i);
                String acceptedName = accepted.get(i);
                if (localName != null && !localName.equals(acceptedName) && (view = sharedElements.get(localName)) != null) {
                    sharedElements.put(acceptedName, view);
                }
            }
        }
        return sharedElements;
    }

    private void sendSharedElementDestination() {
        boolean allReady;
        View decorView = getDecor();
        if (allowOverlappingTransitions() && getEnterViewsTransition() != null) {
            allReady = false;
        } else if (decorView == null) {
            allReady = true;
        } else {
            allReady = !decorView.isLayoutRequested();
            if (allReady) {
                int i = 0;
                while (true) {
                    if (i >= this.mSharedElements.size()) {
                        break;
                    } else if (!this.mSharedElements.get(i).isLayoutRequested()) {
                        i++;
                    } else {
                        allReady = false;
                        break;
                    }
                }
            }
        }
        if (allReady) {
            Bundle state = captureSharedElementState();
            moveSharedElementsToOverlay();
            this.mResultReceiver.send(107, state);
        } else if (decorView != null) {
            OneShotPreDrawListener.add(decorView, new Runnable() { // from class: android.app.EnterTransitionCoordinator$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EnterTransitionCoordinator.this.lambda$sendSharedElementDestination$1();
                }
            });
        }
        if (allowOverlappingTransitions()) {
            startEnterTransitionOnly();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSharedElementDestination$1() {
        if (this.mResultReceiver != null) {
            Bundle state = captureSharedElementState();
            moveSharedElementsToOverlay();
            this.mResultReceiver.send(107, state);
        }
    }

    private static SharedElementCallback getListener(Activity activity, boolean isReturning) {
        return isReturning ? activity.mExitTransitionListener : activity.mEnterTransitionListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.p008os.ResultReceiver
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 103:
                if (!this.mIsCanceled) {
                    this.mSharedElementsBundle = resultData;
                    onTakeSharedElements();
                    return;
                }
                return;
            case 104:
                if (!this.mIsCanceled) {
                    this.mIsExitTransitionComplete = true;
                    if (this.mSharedElementTransitionStarted) {
                        onRemoteExitTransitionComplete();
                        return;
                    }
                    return;
                }
                return;
            case 105:
            case 107:
            default:
                return;
            case 106:
                cancel();
                return;
            case 108:
                if (!this.mIsCanceled && !this.mIsTaskRoot) {
                    this.mPendingExitNames = this.mAllSharedElementNames;
                    return;
                }
                return;
        }
    }

    public boolean isWaitingForRemoteExit() {
        return this.mIsReturning && this.mResultReceiver != null;
    }

    public ArrayList<String> getPendingExitSharedElementNames() {
        return this.mPendingExitNames;
    }

    public void forceViewsToAppear() {
        OneShotPreDrawListener oneShotPreDrawListener;
        if (!this.mIsReturning) {
            return;
        }
        if (!this.mIsReadyForTransition) {
            this.mIsReadyForTransition = true;
            ViewGroup decor = getDecor();
            if (decor != null && (oneShotPreDrawListener = this.mViewsReadyListener) != null) {
                oneShotPreDrawListener.removeListener();
                this.mViewsReadyListener = null;
            }
            showViews(this.mTransitioningViews, true);
            setTransitioningViewsVisiblity(0, true);
            this.mSharedElements.clear();
            this.mAllSharedElementNames.clear();
            this.mTransitioningViews.clear();
            this.mIsReadyForTransition = true;
            viewsTransitionComplete();
            sharedElementTransitionComplete();
        } else {
            if (!this.mSharedElementTransitionStarted) {
                moveSharedElementsFromOverlay();
                this.mSharedElementTransitionStarted = true;
                showViews(this.mSharedElements, true);
                this.mSharedElements.clear();
                sharedElementTransitionComplete();
            }
            if (!this.mIsViewsTransitionStarted) {
                this.mIsViewsTransitionStarted = true;
                showViews(this.mTransitioningViews, true);
                setTransitioningViewsVisiblity(0, true);
                this.mTransitioningViews.clear();
                viewsTransitionComplete();
            }
            cancelPendingTransitions();
        }
        this.mAreViewsReady = true;
        if (this.mResultReceiver != null) {
            this.mResultReceiver.send(106, null);
            this.mResultReceiver = null;
        }
    }

    private void cancel() {
        if (!this.mIsCanceled) {
            this.mIsCanceled = true;
            if (getViewsTransition() == null || this.mIsViewsTransitionStarted) {
                showViews(this.mSharedElements, true);
            } else if (this.mTransitioningViews != null) {
                this.mTransitioningViews.addAll(this.mSharedElements);
            }
            moveSharedElementsFromOverlay();
            this.mSharedElementNames.clear();
            this.mSharedElements.clear();
            this.mAllSharedElementNames.clear();
            startSharedElementTransition(null);
            onRemoteExitTransitionComplete();
        }
    }

    public boolean isReturning() {
        return this.mIsReturning;
    }

    protected void prepareEnter() {
        Drawable background;
        ViewGroup decorView = getDecor();
        Activity activity = this.mActivity;
        if (activity == null || decorView == null) {
            return;
        }
        this.mIsTaskRoot = activity.isTaskRoot();
        if (!isCrossTask()) {
            this.mActivity.overridePendingTransition(0, 0);
        }
        if (!this.mIsReturning) {
            this.mWasOpaque = this.mActivity.convertToTranslucent(null, null);
            Drawable background2 = decorView.getBackground();
            if (background2 == null) {
                background = new ColorDrawable(0);
                this.mReplacedBackground = background;
            } else {
                getWindow().setBackgroundDrawable(null);
                background = background2.mutate();
                background.setAlpha(0);
            }
            getWindow().setBackgroundDrawable(background);
            return;
        }
        this.mActivity = null;
    }

    @Override // android.app.ActivityTransitionCoordinator
    protected Transition getViewsTransition() {
        Window window = getWindow();
        if (window == null) {
            return null;
        }
        if (this.mIsReturning) {
            return window.getReenterTransition();
        }
        return window.getEnterTransition();
    }

    protected Transition getSharedElementTransition() {
        Window window = getWindow();
        if (window == null) {
            return null;
        }
        if (this.mIsReturning) {
            return window.getSharedElementReenterTransition();
        }
        return window.getSharedElementEnterTransition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSharedElementTransition(Bundle sharedElementState) {
        ViewGroup decorView = getDecor();
        if (decorView == null) {
            return;
        }
        ArrayList<String> rejectedNames = new ArrayList<>(this.mAllSharedElementNames);
        rejectedNames.removeAll(this.mSharedElementNames);
        ArrayList<View> rejectedSnapshots = createSnapshots(sharedElementState, rejectedNames);
        if (this.mListener != null) {
            this.mListener.onRejectSharedElements(rejectedSnapshots);
        }
        removeNullViews(rejectedSnapshots);
        startRejectedAnimations(rejectedSnapshots);
        ArrayList<View> sharedElementSnapshots = createSnapshots(sharedElementState, this.mSharedElementNames);
        boolean startEnterTransition = true;
        showViews(this.mSharedElements, true);
        scheduleSetSharedElementEnd(sharedElementSnapshots);
        ArrayList<ActivityTransitionCoordinator.SharedElementOriginalState> originalImageViewState = setSharedElementState(sharedElementState, sharedElementSnapshots);
        requestLayoutForSharedElements();
        if (!allowOverlappingTransitions() || this.mIsReturning) {
            startEnterTransition = false;
        }
        lambda$scheduleGhostVisibilityChange$1(4);
        scheduleGhostVisibilityChange(4);
        pauseInput();
        Transition transition = beginTransition(decorView, startEnterTransition, true);
        scheduleGhostVisibilityChange(0);
        lambda$scheduleGhostVisibilityChange$1(0);
        if (startEnterTransition) {
            startEnterTransition(transition);
        }
        setOriginalSharedElementState(this.mSharedElements, originalImageViewState);
        if (this.mResultReceiver != null) {
            decorView.postOnAnimation(new Runnable() { // from class: android.app.EnterTransitionCoordinator.2
                int mAnimations;

                @Override // java.lang.Runnable
                public void run() {
                    int i = this.mAnimations;
                    this.mAnimations = i + 1;
                    if (i < 2) {
                        View decorView2 = EnterTransitionCoordinator.this.getDecor();
                        if (decorView2 != null) {
                            decorView2.postOnAnimation(this);
                        }
                    } else if (EnterTransitionCoordinator.this.mResultReceiver != null) {
                        EnterTransitionCoordinator.this.mResultReceiver.send(101, null);
                        EnterTransitionCoordinator.this.mResultReceiver = null;
                    }
                }
            });
        }
    }

    private static void removeNullViews(ArrayList<View> views) {
        if (views != null) {
            for (int i = views.size() - 1; i >= 0; i--) {
                if (views.get(i) == null) {
                    views.remove(i);
                }
            }
        }
    }

    private void onTakeSharedElements() {
        if (!this.mIsReadyForTransition || this.mSharedElementsBundle == null) {
            return;
        }
        Bundle sharedElementState = this.mSharedElementsBundle;
        this.mSharedElementsBundle = null;
        SharedElementCallback.OnSharedElementsReadyListener listener = new C01633(sharedElementState);
        if (this.mListener == null) {
            listener.onSharedElementsReady();
        } else {
            this.mListener.onSharedElementsArrived(this.mSharedElementNames, this.mSharedElements, listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.app.EnterTransitionCoordinator$3 */
    /* loaded from: classes.dex */
    public class C01633 implements SharedElementCallback.OnSharedElementsReadyListener {
        final /* synthetic */ Bundle val$sharedElementState;

        C01633(Bundle bundle) {
            this.val$sharedElementState = bundle;
        }

        @Override // android.app.SharedElementCallback.OnSharedElementsReadyListener
        public void onSharedElementsReady() {
            View decorView = EnterTransitionCoordinator.this.getDecor();
            if (decorView != null) {
                final Bundle bundle = this.val$sharedElementState;
                OneShotPreDrawListener.add(decorView, false, new Runnable() { // from class: android.app.EnterTransitionCoordinator$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EnterTransitionCoordinator.C01633.this.lambda$onSharedElementsReady$1(bundle);
                    }
                });
                decorView.invalidate();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSharedElementsReady$1(final Bundle sharedElementState) {
            EnterTransitionCoordinator.this.startTransition(new Runnable() { // from class: android.app.EnterTransitionCoordinator$3$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EnterTransitionCoordinator.C01633.this.lambda$onSharedElementsReady$0(sharedElementState);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSharedElementsReady$0(Bundle sharedElementState) {
            EnterTransitionCoordinator.this.startSharedElementTransition(sharedElementState);
        }
    }

    private void requestLayoutForSharedElements() {
        int numSharedElements = this.mSharedElements.size();
        for (int i = 0; i < numSharedElements; i++) {
            this.mSharedElements.get(i).requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Transition beginTransition(ViewGroup decorView, boolean startEnterTransition, boolean startSharedElementTransition) {
        Transition sharedElementTransition = null;
        if (startSharedElementTransition) {
            if (!this.mSharedElementNames.isEmpty()) {
                sharedElementTransition = configureTransition(getSharedElementTransition(), false);
            }
            if (sharedElementTransition == null) {
                sharedElementTransitionStarted();
                sharedElementTransitionComplete();
            } else {
                sharedElementTransition.addListener(new TransitionListenerAdapter() { // from class: android.app.EnterTransitionCoordinator.4
                    @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionStart(Transition transition) {
                        EnterTransitionCoordinator.this.sharedElementTransitionStarted();
                    }

                    @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionEnd(Transition transition) {
                        transition.removeListener(this);
                        EnterTransitionCoordinator.this.sharedElementTransitionComplete();
                    }
                });
            }
        }
        Transition viewsTransition = null;
        if (startEnterTransition) {
            this.mIsViewsTransitionStarted = true;
            if (this.mTransitioningViews != null && !this.mTransitioningViews.isEmpty()) {
                viewsTransition = configureTransition(getViewsTransition(), true);
            }
            if (viewsTransition == null) {
                viewsTransitionComplete();
            } else {
                final ArrayList<View> transitioningViews = this.mTransitioningViews;
                viewsTransition.addListener(new ActivityTransitionCoordinator.ContinueTransitionListener() { // from class: android.app.EnterTransitionCoordinator.5
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super();
                    }

                    @Override // android.app.ActivityTransitionCoordinator.ContinueTransitionListener, android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionStart(Transition transition) {
                        EnterTransitionCoordinator.this.mEnterViewsTransition = transition;
                        ArrayList<View> arrayList = transitioningViews;
                        if (arrayList != null) {
                            EnterTransitionCoordinator.this.showViews(arrayList, false);
                        }
                        super.onTransitionStart(transition);
                    }

                    @Override // android.app.ActivityTransitionCoordinator.ContinueTransitionListener, android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionEnd(Transition transition) {
                        EnterTransitionCoordinator.this.mEnterViewsTransition = null;
                        transition.removeListener(this);
                        EnterTransitionCoordinator.this.viewsTransitionComplete();
                        super.onTransitionEnd(transition);
                    }
                });
            }
        }
        Transition transition = mergeTransitions(sharedElementTransition, viewsTransition);
        if (transition != null) {
            transition.addListener(new ActivityTransitionCoordinator.ContinueTransitionListener());
            if (startEnterTransition) {
                setTransitioningViewsVisiblity(4, false);
            }
            TransitionManager.beginDelayedTransition(decorView, transition);
            if (startEnterTransition) {
                setTransitioningViewsVisiblity(0, false);
            }
            decorView.invalidate();
        } else {
            transitionStarted();
        }
        return transition;
    }

    public void runAfterTransitionsComplete(Runnable onTransitionComplete) {
        if (!isTransitionRunning()) {
            onTransitionsComplete();
        } else {
            this.mOnTransitionComplete = onTransitionComplete;
        }
    }

    @Override // android.app.ActivityTransitionCoordinator
    protected void onTransitionsComplete() {
        moveSharedElementsFromOverlay();
        ViewGroup decorView = getDecor();
        if (decorView != null) {
            decorView.sendAccessibilityEvent(2048);
            Window window = getWindow();
            if (window != null && this.mReplacedBackground == decorView.getBackground()) {
                window.setBackgroundDrawable(null);
            }
        }
        Runnable runnable = this.mOnTransitionComplete;
        if (runnable != null) {
            runnable.run();
            this.mOnTransitionComplete = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sharedElementTransitionStarted() {
        this.mSharedElementTransitionStarted = true;
        if (this.mIsExitTransitionComplete) {
            send(104, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startEnterTransition(Transition transition) {
        ViewGroup decorView = getDecor();
        if (!this.mIsReturning && decorView != null) {
            Drawable background = decorView.getBackground();
            if (background != null) {
                Drawable background2 = background.mutate();
                getWindow().setBackgroundDrawable(background2);
                ObjectAnimator ofInt = ObjectAnimator.ofInt(background2, "alpha", 255);
                this.mBackgroundAnimator = ofInt;
                ofInt.setDuration(getFadeDuration());
                this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter() { // from class: android.app.EnterTransitionCoordinator.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        EnterTransitionCoordinator.this.makeOpaque();
                        EnterTransitionCoordinator.this.backgroundAnimatorComplete();
                    }
                });
                this.mBackgroundAnimator.start();
                return;
            } else if (transition != null) {
                transition.addListener(new TransitionListenerAdapter() { // from class: android.app.EnterTransitionCoordinator.7
                    @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionEnd(Transition transition2) {
                        transition2.removeListener(this);
                        EnterTransitionCoordinator.this.makeOpaque();
                    }
                });
                backgroundAnimatorComplete();
                return;
            } else {
                makeOpaque();
                backgroundAnimatorComplete();
                return;
            }
        }
        backgroundAnimatorComplete();
    }

    public void stop() {
        ViewGroup decorView;
        Drawable drawable;
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            objectAnimator.end();
            this.mBackgroundAnimator = null;
        } else if (this.mWasOpaque && (decorView = getDecor()) != null && (drawable = decorView.getBackground()) != null) {
            drawable.setAlpha(255);
        }
        makeOpaque();
        this.mIsCanceled = true;
        this.mResultReceiver = null;
        this.mActivity = null;
        moveSharedElementsFromOverlay();
        if (this.mTransitioningViews != null) {
            showViews(this.mTransitioningViews, true);
            setTransitioningViewsVisiblity(0, true);
        }
        showViews(this.mSharedElements, true);
        clearState();
    }

    public boolean cancelEnter() {
        lambda$scheduleGhostVisibilityChange$1(4);
        this.mHasStopped = true;
        this.mIsCanceled = true;
        clearState();
        return super.cancelPendingTransitions();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ActivityTransitionCoordinator
    public void clearState() {
        this.mSharedElementsBundle = null;
        this.mEnterViewsTransition = null;
        this.mResultReceiver = null;
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mBackgroundAnimator = null;
        }
        Runnable runnable = this.mOnTransitionComplete;
        if (runnable != null) {
            runnable.run();
            this.mOnTransitionComplete = null;
        }
        super.clearState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeOpaque() {
        Activity activity;
        if (!this.mHasStopped && (activity = this.mActivity) != null) {
            if (this.mWasOpaque) {
                activity.convertFromTranslucent();
            }
            this.mActivity = null;
        }
    }

    private boolean allowOverlappingTransitions() {
        return this.mIsReturning ? getWindow().getAllowReturnTransitionOverlap() : getWindow().getAllowEnterTransitionOverlap();
    }

    private void startRejectedAnimations(final ArrayList<View> rejectedSnapshots) {
        final ViewGroup decorView;
        if (rejectedSnapshots != null && !rejectedSnapshots.isEmpty() && (decorView = getDecor()) != null) {
            ViewGroupOverlay overlay = decorView.getOverlay();
            ObjectAnimator animator = null;
            int numRejected = rejectedSnapshots.size();
            for (int i = 0; i < numRejected; i++) {
                View snapshot = rejectedSnapshots.get(i);
                overlay.add(snapshot);
                animator = ObjectAnimator.ofFloat(snapshot, View.ALPHA, 1.0f, 0.0f);
                animator.start();
            }
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.app.EnterTransitionCoordinator.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ViewGroupOverlay overlay2 = decorView.getOverlay();
                    int numRejected2 = rejectedSnapshots.size();
                    for (int i2 = 0; i2 < numRejected2; i2++) {
                        overlay2.remove((View) rejectedSnapshots.get(i2));
                    }
                }
            });
        }
    }

    protected void onRemoteExitTransitionComplete() {
        if (!allowOverlappingTransitions()) {
            startEnterTransitionOnly();
        }
    }

    private void startEnterTransitionOnly() {
        startTransition(new Runnable() { // from class: android.app.EnterTransitionCoordinator.9
            @Override // java.lang.Runnable
            public void run() {
                ViewGroup decorView = EnterTransitionCoordinator.this.getDecor();
                if (decorView != null) {
                    Transition transition = EnterTransitionCoordinator.this.beginTransition(decorView, true, false);
                    EnterTransitionCoordinator.this.startEnterTransition(transition);
                }
            }
        });
    }
}
