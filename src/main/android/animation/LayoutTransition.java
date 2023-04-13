package android.animation;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class LayoutTransition {
    public static final int APPEARING = 2;
    public static final int CHANGE_APPEARING = 0;
    public static final int CHANGE_DISAPPEARING = 1;
    public static final int CHANGING = 4;
    private static TimeInterpolator DECEL_INTERPOLATOR = null;
    public static final int DISAPPEARING = 3;
    private static final int FLAG_APPEARING = 1;
    private static final int FLAG_CHANGE_APPEARING = 4;
    private static final int FLAG_CHANGE_DISAPPEARING = 8;
    private static final int FLAG_CHANGING = 16;
    private static final int FLAG_DISAPPEARING = 2;
    private static ObjectAnimator defaultChange;
    private static ObjectAnimator defaultChangeIn;
    private static ObjectAnimator defaultChangeOut;
    private static ObjectAnimator defaultFadeIn;
    private static ObjectAnimator defaultFadeOut;
    private static TimeInterpolator sAppearingInterpolator;
    private static TimeInterpolator sChangingAppearingInterpolator;
    private static TimeInterpolator sChangingDisappearingInterpolator;
    private static TimeInterpolator sChangingInterpolator;
    private static TimeInterpolator sDisappearingInterpolator;
    private final LinkedHashMap<View, Animator> currentAppearingAnimations;
    private final LinkedHashMap<View, Animator> currentChangingAnimations;
    private final LinkedHashMap<View, Animator> currentDisappearingAnimations;
    private final HashMap<View, View.OnLayoutChangeListener> layoutChangeListenerMap;
    private boolean mAnimateParentHierarchy;
    private Animator mAppearingAnim;
    private long mAppearingDelay;
    private long mAppearingDuration;
    private TimeInterpolator mAppearingInterpolator;
    private Animator mChangingAnim;
    private Animator mChangingAppearingAnim;
    private long mChangingAppearingDelay;
    private long mChangingAppearingDuration;
    private TimeInterpolator mChangingAppearingInterpolator;
    private long mChangingAppearingStagger;
    private long mChangingDelay;
    private Animator mChangingDisappearingAnim;
    private long mChangingDisappearingDelay;
    private long mChangingDisappearingDuration;
    private TimeInterpolator mChangingDisappearingInterpolator;
    private long mChangingDisappearingStagger;
    private long mChangingDuration;
    private TimeInterpolator mChangingInterpolator;
    private long mChangingStagger;
    private Animator mDisappearingAnim;
    private long mDisappearingDelay;
    private long mDisappearingDuration;
    private TimeInterpolator mDisappearingInterpolator;
    private ArrayList<TransitionListener> mListeners;
    private int mTransitionTypes;
    private final HashMap<View, Animator> pendingAnimations;
    private long staggerDelay;
    private static long DEFAULT_DURATION = 300;
    private static TimeInterpolator ACCEL_DECEL_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    /* loaded from: classes.dex */
    public interface TransitionListener {
        void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);

        void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);
    }

    static {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        DECEL_INTERPOLATOR = decelerateInterpolator;
        TimeInterpolator timeInterpolator = ACCEL_DECEL_INTERPOLATOR;
        sAppearingInterpolator = timeInterpolator;
        sDisappearingInterpolator = timeInterpolator;
        sChangingAppearingInterpolator = decelerateInterpolator;
        sChangingDisappearingInterpolator = decelerateInterpolator;
        sChangingInterpolator = decelerateInterpolator;
    }

    public LayoutTransition() {
        this.mDisappearingAnim = null;
        this.mAppearingAnim = null;
        this.mChangingAppearingAnim = null;
        this.mChangingDisappearingAnim = null;
        this.mChangingAnim = null;
        long j = DEFAULT_DURATION;
        this.mChangingAppearingDuration = j;
        this.mChangingDisappearingDuration = j;
        this.mChangingDuration = j;
        this.mAppearingDuration = j;
        this.mDisappearingDuration = j;
        this.mAppearingDelay = j;
        this.mDisappearingDelay = 0L;
        this.mChangingAppearingDelay = 0L;
        this.mChangingDisappearingDelay = j;
        this.mChangingDelay = 0L;
        this.mChangingAppearingStagger = 0L;
        this.mChangingDisappearingStagger = 0L;
        this.mChangingStagger = 0L;
        this.mAppearingInterpolator = sAppearingInterpolator;
        this.mDisappearingInterpolator = sDisappearingInterpolator;
        this.mChangingAppearingInterpolator = sChangingAppearingInterpolator;
        this.mChangingDisappearingInterpolator = sChangingDisappearingInterpolator;
        this.mChangingInterpolator = sChangingInterpolator;
        this.pendingAnimations = new HashMap<>();
        this.currentChangingAnimations = new LinkedHashMap<>();
        this.currentAppearingAnimations = new LinkedHashMap<>();
        this.currentDisappearingAnimations = new LinkedHashMap<>();
        this.layoutChangeListenerMap = new HashMap<>();
        this.mTransitionTypes = 15;
        this.mAnimateParentHierarchy = true;
        if (defaultChangeIn == null) {
            PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt(NavigationBarInflaterView.LEFT, 0, 1);
            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
            PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt(NavigationBarInflaterView.RIGHT, 0, 1);
            PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
            PropertyValuesHolder pvhScrollX = PropertyValuesHolder.ofInt("scrollX", 0, 1);
            PropertyValuesHolder pvhScrollY = PropertyValuesHolder.ofInt("scrollY", 0, 1);
            ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(null, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScrollX, pvhScrollY);
            defaultChangeIn = ofPropertyValuesHolder;
            ofPropertyValuesHolder.setDuration(DEFAULT_DURATION);
            defaultChangeIn.setStartDelay(this.mChangingAppearingDelay);
            defaultChangeIn.setInterpolator(this.mChangingAppearingInterpolator);
            ObjectAnimator mo258clone = defaultChangeIn.mo258clone();
            defaultChangeOut = mo258clone;
            mo258clone.setStartDelay(this.mChangingDisappearingDelay);
            defaultChangeOut.setInterpolator(this.mChangingDisappearingInterpolator);
            ObjectAnimator mo258clone2 = defaultChangeIn.mo258clone();
            defaultChange = mo258clone2;
            mo258clone2.setStartDelay(this.mChangingDelay);
            defaultChange.setInterpolator(this.mChangingInterpolator);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) null, "alpha", 0.0f, 1.0f);
            defaultFadeIn = ofFloat;
            ofFloat.setDuration(DEFAULT_DURATION);
            defaultFadeIn.setStartDelay(this.mAppearingDelay);
            defaultFadeIn.setInterpolator(this.mAppearingInterpolator);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object) null, "alpha", 1.0f, 0.0f);
            defaultFadeOut = ofFloat2;
            ofFloat2.setDuration(DEFAULT_DURATION);
            defaultFadeOut.setStartDelay(this.mDisappearingDelay);
            defaultFadeOut.setInterpolator(this.mDisappearingInterpolator);
        }
        this.mChangingAppearingAnim = defaultChangeIn;
        this.mChangingDisappearingAnim = defaultChangeOut;
        this.mChangingAnim = defaultChange;
        this.mAppearingAnim = defaultFadeIn;
        this.mDisappearingAnim = defaultFadeOut;
    }

    public void setDuration(long duration) {
        this.mChangingAppearingDuration = duration;
        this.mChangingDisappearingDuration = duration;
        this.mChangingDuration = duration;
        this.mAppearingDuration = duration;
        this.mDisappearingDuration = duration;
    }

    public void enableTransitionType(int transitionType) {
        switch (transitionType) {
            case 0:
                this.mTransitionTypes |= 4;
                return;
            case 1:
                this.mTransitionTypes |= 8;
                return;
            case 2:
                this.mTransitionTypes |= 1;
                return;
            case 3:
                this.mTransitionTypes |= 2;
                return;
            case 4:
                this.mTransitionTypes |= 16;
                return;
            default:
                return;
        }
    }

    public void disableTransitionType(int transitionType) {
        switch (transitionType) {
            case 0:
                this.mTransitionTypes &= -5;
                return;
            case 1:
                this.mTransitionTypes &= -9;
                return;
            case 2:
                this.mTransitionTypes &= -2;
                return;
            case 3:
                this.mTransitionTypes &= -3;
                return;
            case 4:
                this.mTransitionTypes &= -17;
                return;
            default:
                return;
        }
    }

    public boolean isTransitionTypeEnabled(int transitionType) {
        switch (transitionType) {
            case 0:
                return (this.mTransitionTypes & 4) == 4;
            case 1:
                return (this.mTransitionTypes & 8) == 8;
            case 2:
                return (this.mTransitionTypes & 1) == 1;
            case 3:
                return (this.mTransitionTypes & 2) == 2;
            case 4:
                return (this.mTransitionTypes & 16) == 16;
            default:
                return false;
        }
    }

    public void setStartDelay(int transitionType, long delay) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingDelay = delay;
                return;
            case 1:
                this.mChangingDisappearingDelay = delay;
                return;
            case 2:
                this.mAppearingDelay = delay;
                return;
            case 3:
                this.mDisappearingDelay = delay;
                return;
            case 4:
                this.mChangingDelay = delay;
                return;
            default:
                return;
        }
    }

    public long getStartDelay(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingDelay;
            case 1:
                return this.mChangingDisappearingDelay;
            case 2:
                return this.mAppearingDelay;
            case 3:
                return this.mDisappearingDelay;
            case 4:
                return this.mChangingDelay;
            default:
                return 0L;
        }
    }

    public void setDuration(int transitionType, long duration) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingDuration = duration;
                return;
            case 1:
                this.mChangingDisappearingDuration = duration;
                return;
            case 2:
                this.mAppearingDuration = duration;
                return;
            case 3:
                this.mDisappearingDuration = duration;
                return;
            case 4:
                this.mChangingDuration = duration;
                return;
            default:
                return;
        }
    }

    public long getDuration(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingDuration;
            case 1:
                return this.mChangingDisappearingDuration;
            case 2:
                return this.mAppearingDuration;
            case 3:
                return this.mDisappearingDuration;
            case 4:
                return this.mChangingDuration;
            default:
                return 0L;
        }
    }

    public void setStagger(int transitionType, long duration) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingStagger = duration;
                return;
            case 1:
                this.mChangingDisappearingStagger = duration;
                return;
            case 2:
            case 3:
            default:
                return;
            case 4:
                this.mChangingStagger = duration;
                return;
        }
    }

    public long getStagger(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingStagger;
            case 1:
                return this.mChangingDisappearingStagger;
            case 2:
            case 3:
            default:
                return 0L;
            case 4:
                return this.mChangingStagger;
        }
    }

    public void setInterpolator(int transitionType, TimeInterpolator interpolator) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingInterpolator = interpolator;
                return;
            case 1:
                this.mChangingDisappearingInterpolator = interpolator;
                return;
            case 2:
                this.mAppearingInterpolator = interpolator;
                return;
            case 3:
                this.mDisappearingInterpolator = interpolator;
                return;
            case 4:
                this.mChangingInterpolator = interpolator;
                return;
            default:
                return;
        }
    }

    public TimeInterpolator getInterpolator(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingInterpolator;
            case 1:
                return this.mChangingDisappearingInterpolator;
            case 2:
                return this.mAppearingInterpolator;
            case 3:
                return this.mDisappearingInterpolator;
            case 4:
                return this.mChangingInterpolator;
            default:
                return null;
        }
    }

    public void setAnimator(int transitionType, Animator animator) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingAnim = animator;
                return;
            case 1:
                this.mChangingDisappearingAnim = animator;
                return;
            case 2:
                this.mAppearingAnim = animator;
                return;
            case 3:
                this.mDisappearingAnim = animator;
                return;
            case 4:
                this.mChangingAnim = animator;
                return;
            default:
                return;
        }
    }

    public Animator getAnimator(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingAnim;
            case 1:
                return this.mChangingDisappearingAnim;
            case 2:
                return this.mAppearingAnim;
            case 3:
                return this.mDisappearingAnim;
            case 4:
                return this.mChangingAnim;
            default:
                return null;
        }
    }

    private void runChangeTransition(ViewGroup parent, View newView, int changeReason) {
        Animator baseAnimator;
        Animator parentAnimator;
        long duration;
        switch (changeReason) {
            case 2:
                Animator baseAnimator2 = this.mChangingAppearingAnim;
                long duration2 = this.mChangingAppearingDuration;
                Animator parentAnimator2 = defaultChangeIn;
                baseAnimator = baseAnimator2;
                parentAnimator = parentAnimator2;
                duration = duration2;
                break;
            case 3:
                Animator baseAnimator3 = this.mChangingDisappearingAnim;
                long duration3 = this.mChangingDisappearingDuration;
                Animator parentAnimator3 = defaultChangeOut;
                baseAnimator = baseAnimator3;
                parentAnimator = parentAnimator3;
                duration = duration3;
                break;
            case 4:
                Animator baseAnimator4 = this.mChangingAnim;
                long duration4 = this.mChangingDuration;
                Animator parentAnimator4 = defaultChange;
                baseAnimator = baseAnimator4;
                parentAnimator = parentAnimator4;
                duration = duration4;
                break;
            default:
                baseAnimator = null;
                parentAnimator = null;
                duration = 0;
                break;
        }
        if (baseAnimator != null) {
            this.staggerDelay = 0L;
            ViewTreeObserver observer = parent.getViewTreeObserver();
            if (!observer.isAlive()) {
                return;
            }
            int numChildren = parent.getChildCount();
            for (int i = 0; i < numChildren; i++) {
                View child = parent.getChildAt(i);
                if (child != newView) {
                    setupChangeAnimation(parent, changeReason, baseAnimator, duration, child);
                }
            }
            if (this.mAnimateParentHierarchy) {
                ViewGroup tempParent = parent;
                while (tempParent != null) {
                    ViewParent parentParent = tempParent.getParent();
                    if (parentParent instanceof ViewGroup) {
                        setupChangeAnimation((ViewGroup) parentParent, changeReason, parentAnimator, duration, tempParent);
                        tempParent = (ViewGroup) parentParent;
                    } else {
                        tempParent = null;
                    }
                }
            }
            CleanupCallback callback = new CleanupCallback(this.layoutChangeListenerMap, parent);
            observer.addOnPreDrawListener(callback);
            parent.addOnAttachStateChangeListener(callback);
        }
    }

    public void setAnimateParentHierarchy(boolean animateParentHierarchy) {
        this.mAnimateParentHierarchy = animateParentHierarchy;
    }

    private void setupChangeAnimation(final ViewGroup parent, final int changeReason, Animator baseAnimator, final long duration, final View child) {
        if (this.layoutChangeListenerMap.get(child) != null) {
            return;
        }
        if (child.getWidth() == 0 && child.getHeight() == 0) {
            return;
        }
        final Animator anim = baseAnimator.mo258clone();
        anim.setTarget(child);
        anim.setupStartValues();
        Animator currentAnimation = this.pendingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
            this.pendingAnimations.remove(child);
        }
        this.pendingAnimations.put(child, anim);
        ValueAnimator pendingAnimRemover = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(duration + 100);
        pendingAnimRemover.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LayoutTransition.this.pendingAnimations.remove(child);
            }
        });
        pendingAnimRemover.start();
        final View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() { // from class: android.animation.LayoutTransition.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                anim.setupEndValues();
                Animator animator = anim;
                if (animator instanceof ValueAnimator) {
                    boolean valuesDiffer = false;
                    ValueAnimator valueAnim = (ValueAnimator) animator;
                    PropertyValuesHolder[] oldValues = valueAnim.getValues();
                    for (PropertyValuesHolder pvh : oldValues) {
                        if (pvh.mKeyframes instanceof KeyframeSet) {
                            KeyframeSet keyframeSet = (KeyframeSet) pvh.mKeyframes;
                            if (keyframeSet.mFirstKeyframe == null || keyframeSet.mLastKeyframe == null || !keyframeSet.mFirstKeyframe.getValue().equals(keyframeSet.mLastKeyframe.getValue())) {
                                valuesDiffer = true;
                            }
                        } else if (!pvh.mKeyframes.getValue(0.0f).equals(pvh.mKeyframes.getValue(1.0f))) {
                            valuesDiffer = true;
                        }
                    }
                    if (!valuesDiffer) {
                        return;
                    }
                }
                long startDelay = 0;
                switch (changeReason) {
                    case 2:
                        startDelay = LayoutTransition.this.mChangingAppearingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.this.staggerDelay += LayoutTransition.this.mChangingAppearingStagger;
                        if (LayoutTransition.this.mChangingAppearingInterpolator != LayoutTransition.sChangingAppearingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingAppearingInterpolator);
                            break;
                        }
                        break;
                    case 3:
                        startDelay = LayoutTransition.this.mChangingDisappearingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.this.staggerDelay += LayoutTransition.this.mChangingDisappearingStagger;
                        if (LayoutTransition.this.mChangingDisappearingInterpolator != LayoutTransition.sChangingDisappearingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingDisappearingInterpolator);
                            break;
                        }
                        break;
                    case 4:
                        startDelay = LayoutTransition.this.mChangingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.this.staggerDelay += LayoutTransition.this.mChangingStagger;
                        if (LayoutTransition.this.mChangingInterpolator != LayoutTransition.sChangingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingInterpolator);
                            break;
                        }
                        break;
                }
                anim.setStartDelay(startDelay);
                anim.setDuration(duration);
                Animator prevAnimation = (Animator) LayoutTransition.this.currentChangingAnimations.get(child);
                if (prevAnimation != null) {
                    prevAnimation.cancel();
                }
                Animator pendingAnimation = (Animator) LayoutTransition.this.pendingAnimations.get(child);
                if (pendingAnimation != null) {
                    LayoutTransition.this.pendingAnimations.remove(child);
                }
                LayoutTransition.this.currentChangingAnimations.put(child, anim);
                parent.requestTransitionStart(LayoutTransition.this);
                child.removeOnLayoutChangeListener(this);
                LayoutTransition.this.layoutChangeListenerMap.remove(child);
            }
        };
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                int i;
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator<TransitionListener> it = listeners.iterator();
                    while (it.hasNext()) {
                        TransitionListener listener2 = it.next();
                        LayoutTransition layoutTransition = LayoutTransition.this;
                        ViewGroup viewGroup = parent;
                        View view = child;
                        int i2 = changeReason;
                        if (i2 == 2) {
                            i = 0;
                        } else {
                            i = i2 == 3 ? 1 : 4;
                        }
                        listener2.startTransition(layoutTransition, viewGroup, view, i);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                child.removeOnLayoutChangeListener(listener);
                LayoutTransition.this.layoutChangeListenerMap.remove(child);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                int i;
                LayoutTransition.this.currentChangingAnimations.remove(child);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator<TransitionListener> it = listeners.iterator();
                    while (it.hasNext()) {
                        TransitionListener listener2 = it.next();
                        LayoutTransition layoutTransition = LayoutTransition.this;
                        ViewGroup viewGroup = parent;
                        View view = child;
                        int i2 = changeReason;
                        if (i2 == 2) {
                            i = 0;
                        } else {
                            i = i2 == 3 ? 1 : 4;
                        }
                        listener2.endTransition(layoutTransition, viewGroup, view, i);
                    }
                }
            }
        });
        child.addOnLayoutChangeListener(listener);
        this.layoutChangeListenerMap.put(child, listener);
    }

    public void startChangingAnimations() {
        LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
        for (Animator anim : currentAnimCopy.values()) {
            if (anim instanceof ObjectAnimator) {
                ((ObjectAnimator) anim).setCurrentPlayTime(0L);
            }
            anim.start();
        }
    }

    public void endChangingAnimations() {
        LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
        for (Animator anim : currentAnimCopy.values()) {
            anim.start();
            anim.end();
        }
        this.currentChangingAnimations.clear();
    }

    public boolean isChangingLayout() {
        return this.currentChangingAnimations.size() > 0;
    }

    public boolean isRunning() {
        return this.currentChangingAnimations.size() > 0 || this.currentAppearingAnimations.size() > 0 || this.currentDisappearingAnimations.size() > 0;
    }

    public void cancel() {
        if (this.currentChangingAnimations.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
            for (Animator anim : currentAnimCopy.values()) {
                anim.cancel();
            }
            this.currentChangingAnimations.clear();
        }
        LinkedHashMap<View, Animator> currentAnimCopy2 = this.currentAppearingAnimations;
        if (currentAnimCopy2.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy3 = (LinkedHashMap) this.currentAppearingAnimations.clone();
            for (Animator anim2 : currentAnimCopy3.values()) {
                anim2.end();
            }
            this.currentAppearingAnimations.clear();
        }
        LinkedHashMap<View, Animator> currentAnimCopy4 = this.currentDisappearingAnimations;
        if (currentAnimCopy4.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy5 = (LinkedHashMap) this.currentDisappearingAnimations.clone();
            for (Animator anim3 : currentAnimCopy5.values()) {
                anim3.end();
            }
            this.currentDisappearingAnimations.clear();
        }
    }

    public void cancel(int transitionType) {
        switch (transitionType) {
            case 0:
            case 1:
            case 4:
                if (this.currentChangingAnimations.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
                    for (Animator anim : currentAnimCopy.values()) {
                        anim.cancel();
                    }
                    this.currentChangingAnimations.clear();
                    return;
                }
                return;
            case 2:
                LinkedHashMap<View, Animator> currentAnimCopy2 = this.currentAppearingAnimations;
                if (currentAnimCopy2.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy3 = (LinkedHashMap) this.currentAppearingAnimations.clone();
                    for (Animator anim2 : currentAnimCopy3.values()) {
                        anim2.end();
                    }
                    this.currentAppearingAnimations.clear();
                    return;
                }
                return;
            case 3:
                if (this.currentDisappearingAnimations.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy4 = (LinkedHashMap) this.currentDisappearingAnimations.clone();
                    for (Animator anim3 : currentAnimCopy4.values()) {
                        anim3.end();
                    }
                    this.currentDisappearingAnimations.clear();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void runAppearingTransition(final ViewGroup parent, final View child) {
        Animator currentAnimation = this.currentDisappearingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        Animator animator = this.mAppearingAnim;
        if (animator == null) {
            if (hasListeners()) {
                ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
                Iterator<TransitionListener> it = listeners.iterator();
                while (it.hasNext()) {
                    TransitionListener listener = it.next();
                    listener.endTransition(this, parent, child, 2);
                }
                return;
            }
            return;
        }
        Animator anim = animator.mo258clone();
        anim.setTarget(child);
        anim.setStartDelay(this.mAppearingDelay);
        anim.setDuration(this.mAppearingDuration);
        TimeInterpolator timeInterpolator = this.mAppearingInterpolator;
        if (timeInterpolator != sAppearingInterpolator) {
            anim.setInterpolator(timeInterpolator);
        }
        if (anim instanceof ObjectAnimator) {
            ((ObjectAnimator) anim).setCurrentPlayTime(0L);
        }
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator anim2) {
                LayoutTransition.this.currentAppearingAnimations.remove(child);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners2 = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator<TransitionListener> it2 = listeners2.iterator();
                    while (it2.hasNext()) {
                        TransitionListener listener2 = it2.next();
                        listener2.endTransition(LayoutTransition.this, parent, child, 2);
                    }
                }
            }
        });
        this.currentAppearingAnimations.put(child, anim);
        anim.start();
    }

    private void runDisappearingTransition(final ViewGroup parent, final View child) {
        Animator currentAnimation = this.currentAppearingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        Animator animator = this.mDisappearingAnim;
        if (animator == null) {
            if (hasListeners()) {
                ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
                Iterator<TransitionListener> it = listeners.iterator();
                while (it.hasNext()) {
                    TransitionListener listener = it.next();
                    listener.endTransition(this, parent, child, 3);
                }
                return;
            }
            return;
        }
        Animator anim = animator.mo258clone();
        anim.setStartDelay(this.mDisappearingDelay);
        anim.setDuration(this.mDisappearingDuration);
        TimeInterpolator timeInterpolator = this.mDisappearingInterpolator;
        if (timeInterpolator != sDisappearingInterpolator) {
            anim.setInterpolator(timeInterpolator);
        }
        anim.setTarget(child);
        final float preAnimAlpha = child.getAlpha();
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator anim2) {
                LayoutTransition.this.currentDisappearingAnimations.remove(child);
                child.setAlpha(preAnimAlpha);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners2 = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator<TransitionListener> it2 = listeners2.iterator();
                    while (it2.hasNext()) {
                        TransitionListener listener2 = it2.next();
                        listener2.endTransition(LayoutTransition.this, parent, child, 3);
                    }
                }
            }
        });
        if (anim instanceof ObjectAnimator) {
            ((ObjectAnimator) anim).setCurrentPlayTime(0L);
        }
        this.currentDisappearingAnimations.put(child, anim);
        anim.start();
    }

    private void addChild(ViewGroup parent, View child, boolean changesLayout) {
        if (parent.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 1) == 1) {
            cancel(3);
        }
        if (changesLayout && (this.mTransitionTypes & 4) == 4) {
            cancel(0);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 1) == 1) {
            ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
            Iterator<TransitionListener> it = listeners.iterator();
            while (it.hasNext()) {
                TransitionListener listener = it.next();
                listener.startTransition(this, parent, child, 2);
            }
        }
        if (changesLayout && (this.mTransitionTypes & 4) == 4) {
            runChangeTransition(parent, child, 2);
        }
        if ((this.mTransitionTypes & 1) == 1) {
            runAppearingTransition(parent, child);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasListeners() {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        return arrayList != null && arrayList.size() > 0;
    }

    public void layoutChange(ViewGroup parent) {
        if (parent.getWindowVisibility() == 0 && (this.mTransitionTypes & 16) == 16 && !isRunning()) {
            runChangeTransition(parent, null, 4);
        }
    }

    public void addChild(ViewGroup parent, View child) {
        addChild(parent, child, true);
    }

    @Deprecated
    public void showChild(ViewGroup parent, View child) {
        addChild(parent, child, true);
    }

    public void showChild(ViewGroup parent, View child, int oldVisibility) {
        addChild(parent, child, oldVisibility == 8);
    }

    private void removeChild(ViewGroup parent, View child, boolean changesLayout) {
        if (parent.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 2) == 2) {
            cancel(2);
        }
        if (changesLayout && (this.mTransitionTypes & 8) == 8) {
            cancel(1);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 2) == 2) {
            ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
            Iterator<TransitionListener> it = listeners.iterator();
            while (it.hasNext()) {
                TransitionListener listener = it.next();
                listener.startTransition(this, parent, child, 3);
            }
        }
        if (changesLayout && (this.mTransitionTypes & 8) == 8) {
            runChangeTransition(parent, child, 3);
        }
        if ((this.mTransitionTypes & 2) == 2) {
            runDisappearingTransition(parent, child);
        }
    }

    public void removeChild(ViewGroup parent, View child) {
        removeChild(parent, child, true);
    }

    @Deprecated
    public void hideChild(ViewGroup parent, View child) {
        removeChild(parent, child, true);
    }

    public void hideChild(ViewGroup parent, View child, int newVisibility) {
        removeChild(parent, child, newVisibility == 8);
    }

    public void addTransitionListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
    }

    public void removeTransitionListener(TransitionListener listener) {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(listener);
    }

    public List<TransitionListener> getTransitionListeners() {
        return this.mListeners;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class CleanupCallback implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
        final Map<View, View.OnLayoutChangeListener> layoutChangeListenerMap;
        final ViewGroup parent;

        CleanupCallback(Map<View, View.OnLayoutChangeListener> listenerMap, ViewGroup parent) {
            this.layoutChangeListenerMap = listenerMap;
            this.parent = parent;
        }

        private void cleanup() {
            this.parent.getViewTreeObserver().removeOnPreDrawListener(this);
            this.parent.removeOnAttachStateChangeListener(this);
            int count = this.layoutChangeListenerMap.size();
            if (count > 0) {
                Collection<View> views = this.layoutChangeListenerMap.keySet();
                for (View view : views) {
                    View.OnLayoutChangeListener listener = this.layoutChangeListenerMap.get(view);
                    view.removeOnLayoutChangeListener(listener);
                }
                this.layoutChangeListenerMap.clear();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            cleanup();
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            cleanup();
            return true;
        }
    }
}
