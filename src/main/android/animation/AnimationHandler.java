package android.animation;

import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.util.ArrayMap;
import android.view.Choreographer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class AnimationHandler {
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "AnimationHandler";
    private AnimationFrameCallbackProvider mProvider;
    private static boolean sAnimatorPausingEnabled = isPauseBgAnimationsEnabledInSystemProperties();
    private static boolean sOverrideAnimatorPausingSystemProperty = false;
    public static final ThreadLocal<AnimationHandler> sAnimatorHandler = new ThreadLocal<>();
    private final ArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime = new ArrayMap<>();
    private final ArrayList<AnimationFrameCallback> mAnimationCallbacks = new ArrayList<>();
    private final ArrayList<AnimationFrameCallback> mCommitCallbacks = new ArrayList<>();
    private final ArrayList<Animator> mPausedAnimators = new ArrayList<>();
    private final ArrayList<WeakReference<Object>> mAnimatorRequestors = new ArrayList<>();
    private final Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() { // from class: android.animation.AnimationHandler.1
        @Override // android.view.Choreographer.FrameCallback
        public void doFrame(long frameTimeNanos) {
            AnimationHandler animationHandler = AnimationHandler.this;
            animationHandler.doAnimationFrame(animationHandler.getProvider().getFrameTime());
            if (AnimationHandler.this.mAnimationCallbacks.size() > 0) {
                AnimationHandler.this.getProvider().postFrameCallback(this);
            }
        }
    };
    private boolean mListDirty = false;
    private Choreographer.FrameCallback mPauser = new Choreographer.FrameCallback() { // from class: android.animation.AnimationHandler$$ExternalSyntheticLambda0
        @Override // android.view.Choreographer.FrameCallback
        public final void doFrame(long j) {
            AnimationHandler.this.lambda$new$0(j);
        }
    };

    /* loaded from: classes.dex */
    public interface AnimationFrameCallback {
        void commitAnimationFrame(long j);

        boolean doAnimationFrame(long j);
    }

    /* loaded from: classes.dex */
    public interface AnimationFrameCallbackProvider {
        long getFrameDelay();

        long getFrameTime();

        void postCommitCallback(Runnable runnable);

        void postFrameCallback(Choreographer.FrameCallback frameCallback);

        void setFrameDelay(long j);
    }

    public static AnimationHandler getInstance() {
        ThreadLocal<AnimationHandler> threadLocal = sAnimatorHandler;
        if (threadLocal.get() == null) {
            threadLocal.set(new AnimationHandler());
        }
        return threadLocal.get();
    }

    private static boolean isPauseBgAnimationsEnabledInSystemProperties() {
        return sOverrideAnimatorPausingSystemProperty ? sAnimatorPausingEnabled : SystemProperties.getBoolean("framework.pause_bg_animations.enabled", true);
    }

    public static void setAnimatorPausingEnabled(boolean enable) {
        sAnimatorPausingEnabled = enable;
    }

    public static void setOverrideAnimatorPausingSystemProperty(boolean enable) {
        sOverrideAnimatorPausingSystemProperty = enable;
    }

    public static void removeRequestor(Object requestor) {
        getInstance().requestAnimatorsEnabledImpl(false, requestor);
    }

    public static void requestAnimatorsEnabled(boolean enable, Object requestor) {
        getInstance().requestAnimatorsEnabledImpl(enable, requestor);
    }

    private void requestAnimatorsEnabledImpl(boolean enable, Object requestor) {
        boolean wasEmpty = this.mAnimatorRequestors.isEmpty();
        setAnimatorPausingEnabled(isPauseBgAnimationsEnabledInSystemProperties());
        synchronized (this.mAnimatorRequestors) {
            if (enable) {
                WeakReference<Object> weakRef = null;
                for (int i = this.mAnimatorRequestors.size() - 1; i >= 0; i--) {
                    WeakReference<Object> ref = this.mAnimatorRequestors.get(i);
                    Object referent = ref.get();
                    if (referent == requestor) {
                        weakRef = ref;
                    } else if (referent == null) {
                        this.mAnimatorRequestors.remove(i);
                    }
                }
                if (weakRef == null) {
                    WeakReference<Object> weakRef2 = new WeakReference<>(requestor);
                    this.mAnimatorRequestors.add(weakRef2);
                }
            } else {
                for (int i2 = this.mAnimatorRequestors.size() - 1; i2 >= 0; i2--) {
                    Object referent2 = this.mAnimatorRequestors.get(i2).get();
                    if (referent2 == requestor || referent2 == null) {
                        this.mAnimatorRequestors.remove(i2);
                    }
                }
            }
        }
        if (!sAnimatorPausingEnabled) {
            resumeAnimators();
            return;
        }
        boolean isEmpty = this.mAnimatorRequestors.isEmpty();
        if (wasEmpty != isEmpty) {
            if (!isEmpty) {
                resumeAnimators();
            } else {
                Choreographer.getInstance().postFrameCallbackDelayed(this.mPauser, Animator.getBackgroundPauseDelay());
            }
        }
    }

    private void resumeAnimators() {
        Choreographer.getInstance().removeFrameCallback(this.mPauser);
        for (int i = this.mPausedAnimators.size() - 1; i >= 0; i--) {
            this.mPausedAnimators.get(i).resume();
        }
        this.mPausedAnimators.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(long frameTimeNanos) {
        if (this.mAnimatorRequestors.size() > 0) {
            return;
        }
        for (int i = 0; i < this.mAnimationCallbacks.size(); i++) {
            AnimationFrameCallback callback = this.mAnimationCallbacks.get(i);
            if (callback instanceof Animator) {
                Animator animator = (Animator) callback;
                if (animator.getTotalDuration() == -1 && !animator.isPaused()) {
                    this.mPausedAnimators.add(animator);
                    animator.pause();
                }
            }
        }
    }

    public void setProvider(AnimationFrameCallbackProvider provider) {
        if (provider == null) {
            this.mProvider = new MyFrameCallbackProvider();
        } else {
            this.mProvider = provider;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AnimationFrameCallbackProvider getProvider() {
        if (this.mProvider == null) {
            this.mProvider = new MyFrameCallbackProvider();
        }
        return this.mProvider;
    }

    public void addAnimationFrameCallback(AnimationFrameCallback callback, long delay) {
        if (this.mAnimationCallbacks.size() == 0) {
            getProvider().postFrameCallback(this.mFrameCallback);
        }
        if (!this.mAnimationCallbacks.contains(callback)) {
            this.mAnimationCallbacks.add(callback);
        }
        if (delay > 0) {
            this.mDelayedCallbackStartTime.put(callback, Long.valueOf(SystemClock.uptimeMillis() + delay));
        }
    }

    public void addOneShotCommitCallback(AnimationFrameCallback callback) {
        if (!this.mCommitCallbacks.contains(callback)) {
            this.mCommitCallbacks.add(callback);
        }
    }

    public void removeCallback(AnimationFrameCallback callback) {
        this.mCommitCallbacks.remove(callback);
        this.mDelayedCallbackStartTime.remove(callback);
        int id = this.mAnimationCallbacks.indexOf(callback);
        if (id >= 0) {
            this.mAnimationCallbacks.set(id, null);
            this.mListDirty = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAnimationFrame(long frameTime) {
        long currentTime = SystemClock.uptimeMillis();
        int size = this.mAnimationCallbacks.size();
        for (int i = 0; i < size; i++) {
            final AnimationFrameCallback callback = this.mAnimationCallbacks.get(i);
            if (callback != null && isCallbackDue(callback, currentTime)) {
                callback.doAnimationFrame(frameTime);
                if (this.mCommitCallbacks.contains(callback)) {
                    getProvider().postCommitCallback(new Runnable() { // from class: android.animation.AnimationHandler.2
                        @Override // java.lang.Runnable
                        public void run() {
                            AnimationHandler animationHandler = AnimationHandler.this;
                            animationHandler.commitAnimationFrame(callback, animationHandler.getProvider().getFrameTime());
                        }
                    });
                }
            }
        }
        cleanUpList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void commitAnimationFrame(AnimationFrameCallback callback, long frameTime) {
        if (!this.mDelayedCallbackStartTime.containsKey(callback) && this.mCommitCallbacks.contains(callback)) {
            callback.commitAnimationFrame(frameTime);
            this.mCommitCallbacks.remove(callback);
        }
    }

    private boolean isCallbackDue(AnimationFrameCallback callback, long currentTime) {
        Long startTime = this.mDelayedCallbackStartTime.get(callback);
        if (startTime == null) {
            return true;
        }
        if (startTime.longValue() < currentTime) {
            this.mDelayedCallbackStartTime.remove(callback);
            return true;
        }
        return false;
    }

    public static int getAnimationCount() {
        AnimationHandler handler = sAnimatorHandler.get();
        if (handler == null) {
            return 0;
        }
        return handler.getCallbackSize();
    }

    public static void setFrameDelay(long delay) {
        getInstance().getProvider().setFrameDelay(delay);
    }

    public static long getFrameDelay() {
        return getInstance().getProvider().getFrameDelay();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void autoCancelBasedOn(ObjectAnimator objectAnimator) {
        for (int i = this.mAnimationCallbacks.size() - 1; i >= 0; i--) {
            AnimationFrameCallback cb = this.mAnimationCallbacks.get(i);
            if (cb != null && objectAnimator.shouldAutoCancel(cb)) {
                ((Animator) this.mAnimationCallbacks.get(i)).cancel();
            }
        }
    }

    private void cleanUpList() {
        if (this.mListDirty) {
            for (int i = this.mAnimationCallbacks.size() - 1; i >= 0; i--) {
                if (this.mAnimationCallbacks.get(i) == null) {
                    this.mAnimationCallbacks.remove(i);
                }
            }
            this.mListDirty = false;
        }
    }

    private int getCallbackSize() {
        int count = 0;
        int size = this.mAnimationCallbacks.size();
        for (int i = size - 1; i >= 0; i--) {
            if (this.mAnimationCallbacks.get(i) != null) {
                count++;
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyFrameCallbackProvider implements AnimationFrameCallbackProvider {
        final Choreographer mChoreographer;

        private MyFrameCallbackProvider() {
            this.mChoreographer = Choreographer.getInstance();
        }

        @Override // android.animation.AnimationHandler.AnimationFrameCallbackProvider
        public void postFrameCallback(Choreographer.FrameCallback callback) {
            this.mChoreographer.postFrameCallback(callback);
        }

        @Override // android.animation.AnimationHandler.AnimationFrameCallbackProvider
        public void postCommitCallback(Runnable runnable) {
            this.mChoreographer.postCallback(4, runnable, null);
        }

        @Override // android.animation.AnimationHandler.AnimationFrameCallbackProvider
        public long getFrameTime() {
            return this.mChoreographer.getFrameTime();
        }

        @Override // android.animation.AnimationHandler.AnimationFrameCallbackProvider
        public long getFrameDelay() {
            return Choreographer.getFrameDelay();
        }

        @Override // android.animation.AnimationHandler.AnimationFrameCallbackProvider
        public void setFrameDelay(long delay) {
            Choreographer.setFrameDelay(delay);
        }
    }
}
