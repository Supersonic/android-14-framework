package android.transition;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class Visibility extends Transition {
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    private int mMode;
    private boolean mSuppressLayout;
    static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String[] sTransitionProperties = {PROPNAME_VISIBILITY, PROPNAME_PARENT};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface VisibilityMode {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class VisibilityInfo {
        ViewGroup endParent;
        int endVisibility;
        boolean fadeIn;
        ViewGroup startParent;
        int startVisibility;
        boolean visibilityChange;

        private VisibilityInfo() {
        }
    }

    public Visibility() {
        this.mMode = 3;
        this.mSuppressLayout = true;
    }

    public Visibility(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMode = 3;
        this.mSuppressLayout = true;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.VisibilityTransition);
        int mode = a.getInt(0, 0);
        a.recycle();
        if (mode != 0) {
            setMode(mode);
        }
    }

    public void setSuppressLayout(boolean suppress) {
        this.mSuppressLayout = suppress;
    }

    public void setMode(int mode) {
        if ((mode & (-4)) != 0) {
            throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
        }
        this.mMode = mode;
    }

    public int getMode() {
        return this.mMode;
    }

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues transitionValues) {
        int visibility = transitionValues.view.getVisibility();
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(visibility));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        int[] loc = new int[2];
        transitionValues.view.getLocationOnScreen(loc);
        transitionValues.values.put(PROPNAME_SCREEN_LOCATION, loc);
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public boolean isVisible(TransitionValues values) {
        if (values == null) {
            return false;
        }
        int visibility = ((Integer) values.values.get(PROPNAME_VISIBILITY)).intValue();
        View parent = (View) values.values.get(PROPNAME_PARENT);
        return visibility == 0 && parent != null;
    }

    private static VisibilityInfo getVisibilityChangeInfo(TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = new VisibilityInfo();
        visInfo.visibilityChange = false;
        visInfo.fadeIn = false;
        if (startValues != null && startValues.values.containsKey(PROPNAME_VISIBILITY)) {
            visInfo.startVisibility = ((Integer) startValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visInfo.startParent = (ViewGroup) startValues.values.get(PROPNAME_PARENT);
        } else {
            visInfo.startVisibility = -1;
            visInfo.startParent = null;
        }
        if (endValues != null && endValues.values.containsKey(PROPNAME_VISIBILITY)) {
            visInfo.endVisibility = ((Integer) endValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visInfo.endParent = (ViewGroup) endValues.values.get(PROPNAME_PARENT);
        } else {
            visInfo.endVisibility = -1;
            visInfo.endParent = null;
        }
        if (startValues != null && endValues != null) {
            if (visInfo.startVisibility == visInfo.endVisibility && visInfo.startParent == visInfo.endParent) {
                return visInfo;
            }
            if (visInfo.startVisibility != visInfo.endVisibility) {
                if (visInfo.startVisibility == 0) {
                    visInfo.fadeIn = false;
                    visInfo.visibilityChange = true;
                } else if (visInfo.endVisibility == 0) {
                    visInfo.fadeIn = true;
                    visInfo.visibilityChange = true;
                }
            } else if (visInfo.startParent != visInfo.endParent) {
                if (visInfo.endParent == null) {
                    visInfo.fadeIn = false;
                    visInfo.visibilityChange = true;
                } else if (visInfo.startParent == null) {
                    visInfo.fadeIn = true;
                    visInfo.visibilityChange = true;
                }
            }
        } else if (startValues == null && visInfo.endVisibility == 0) {
            visInfo.fadeIn = true;
            visInfo.visibilityChange = true;
        } else if (endValues == null && visInfo.startVisibility == 0) {
            visInfo.fadeIn = false;
            visInfo.visibilityChange = true;
        }
        return visInfo;
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = getVisibilityChangeInfo(startValues, endValues);
        if (visInfo.visibilityChange) {
            if (visInfo.startParent != null || visInfo.endParent != null) {
                if (visInfo.fadeIn) {
                    return onAppear(sceneRoot, startValues, visInfo.startVisibility, endValues, visInfo.endVisibility);
                }
                return onDisappear(sceneRoot, startValues, visInfo.startVisibility, endValues, visInfo.endVisibility);
            }
            return null;
        }
        return null;
    }

    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mMode & 1) != 1 || endValues == null) {
            return null;
        }
        if (startValues == null) {
            View endParent = (View) endValues.view.getParent();
            TransitionValues startParentValues = getMatchedTransitionValues(endParent, false);
            TransitionValues endParentValues = getTransitionValues(endParent, false);
            VisibilityInfo parentVisibilityInfo = getVisibilityChangeInfo(startParentValues, endParentValues);
            if (parentVisibilityInfo.visibilityChange) {
                return null;
            }
        }
        return onAppear(sceneRoot, endValues.view, startValues, endValues);
    }

    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        final ViewGroupOverlay overlay;
        if ((this.mMode & 2) == 2 && startValues != null) {
            final View startView = startValues.view;
            View endView = endValues != null ? endValues.view : null;
            View overlayView = null;
            View viewToKeep = null;
            boolean reusingOverlayView = false;
            View savedOverlayView = (View) startView.getTag(C4057R.C4059id.transition_overlay_view_tag);
            if (savedOverlayView != null) {
                overlayView = savedOverlayView;
                reusingOverlayView = true;
            } else {
                boolean needOverlayForStartView = false;
                if (endView == null || endView.getParent() == null) {
                    if (endView != null) {
                        overlayView = endView;
                    } else {
                        needOverlayForStartView = true;
                    }
                } else if (endVisibility == 4) {
                    viewToKeep = endView;
                } else if (startView == endView) {
                    viewToKeep = endView;
                } else {
                    needOverlayForStartView = true;
                }
                if (needOverlayForStartView) {
                    if (startView.getParent() == null) {
                        overlayView = startView;
                    } else if (startView.getParent() instanceof View) {
                        View startParent = (View) startView.getParent();
                        TransitionValues startParentValues = getTransitionValues(startParent, true);
                        TransitionValues endParentValues = getMatchedTransitionValues(startParent, true);
                        VisibilityInfo parentVisibilityInfo = getVisibilityChangeInfo(startParentValues, endParentValues);
                        if (!parentVisibilityInfo.visibilityChange) {
                            overlayView = TransitionUtils.copyViewImage(sceneRoot, startView, startParent);
                        } else {
                            int id = startParent.getId();
                            if (startParent.getParent() == null && id != -1 && sceneRoot.findViewById(id) != null && this.mCanRemoveViews) {
                                overlayView = startView;
                            }
                        }
                    }
                }
            }
            if (overlayView != null) {
                if (!reusingOverlayView) {
                    overlay = sceneRoot.getOverlay();
                    int[] screenLoc = (int[]) startValues.values.get(PROPNAME_SCREEN_LOCATION);
                    int screenX = screenLoc[0];
                    int screenY = screenLoc[1];
                    int[] loc = new int[2];
                    sceneRoot.getLocationOnScreen(loc);
                    overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
                    overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());
                    overlay.add(overlayView);
                } else {
                    overlay = null;
                }
                Animator animator = onDisappear(sceneRoot, overlayView, startValues, endValues);
                if (!reusingOverlayView) {
                    if (animator == null) {
                        overlay.remove(overlayView);
                    } else {
                        startView.setTagInternal(C4057R.C4059id.transition_overlay_view_tag, overlayView);
                        final View finalOverlayView = overlayView;
                        addListener(new TransitionListenerAdapter() { // from class: android.transition.Visibility.1
                            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                            public void onTransitionPause(Transition transition) {
                                overlay.remove(finalOverlayView);
                            }

                            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                            public void onTransitionResume(Transition transition) {
                                if (finalOverlayView.getParent() == null) {
                                    overlay.add(finalOverlayView);
                                } else {
                                    Visibility.this.cancel();
                                }
                            }

                            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                            public void onTransitionEnd(Transition transition) {
                                startView.setTagInternal(C4057R.C4059id.transition_overlay_view_tag, null);
                                overlay.remove(finalOverlayView);
                                transition.removeListener(this);
                            }
                        });
                    }
                }
                return animator;
            } else if (viewToKeep != null) {
                int originalVisibility = viewToKeep.getVisibility();
                viewToKeep.setTransitionVisibility(0);
                Animator animator2 = onDisappear(sceneRoot, viewToKeep, startValues, endValues);
                if (animator2 != null) {
                    DisappearListener disappearListener = new DisappearListener(viewToKeep, endVisibility, this.mSuppressLayout);
                    animator2.addListener(disappearListener);
                    animator2.addPauseListener(disappearListener);
                    addListener(disappearListener);
                } else {
                    viewToKeep.setTransitionVisibility(originalVisibility);
                }
                return animator2;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override // android.transition.Transition
    public boolean isTransitionRequired(TransitionValues startValues, TransitionValues newValues) {
        if (startValues == null && newValues == null) {
            return false;
        }
        if (startValues != null && newValues != null && newValues.values.containsKey(PROPNAME_VISIBILITY) != startValues.values.containsKey(PROPNAME_VISIBILITY)) {
            return false;
        }
        VisibilityInfo changeInfo = getVisibilityChangeInfo(startValues, newValues);
        if (changeInfo.visibilityChange) {
            return changeInfo.startVisibility == 0 || changeInfo.endVisibility == 0;
        }
        return false;
    }

    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class DisappearListener extends TransitionListenerAdapter implements Animator.AnimatorListener, Animator.AnimatorPauseListener {
        boolean mCanceled = false;
        private final int mFinalVisibility;
        private boolean mLayoutSuppressed;
        private final ViewGroup mParent;
        private final boolean mSuppressLayout;
        private final View mView;

        public DisappearListener(View view, int finalVisibility, boolean suppressLayout) {
            this.mView = view;
            this.mFinalVisibility = finalVisibility;
            this.mParent = (ViewGroup) view.getParent();
            this.mSuppressLayout = suppressLayout;
            suppressLayout(true);
        }

        @Override // android.animation.Animator.AnimatorPauseListener
        public void onAnimationPause(Animator animation) {
            if (!this.mCanceled) {
                this.mView.setTransitionVisibility(this.mFinalVisibility);
            }
        }

        @Override // android.animation.Animator.AnimatorPauseListener
        public void onAnimationResume(Animator animation) {
            if (!this.mCanceled) {
                this.mView.setTransitionVisibility(0);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            hideViewWhenNotCanceled();
        }

        @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
            hideViewWhenNotCanceled();
            transition.removeListener(this);
        }

        @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
            suppressLayout(false);
        }

        @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
            suppressLayout(true);
        }

        private void hideViewWhenNotCanceled() {
            if (!this.mCanceled) {
                this.mView.setTransitionVisibility(this.mFinalVisibility);
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            suppressLayout(false);
        }

        private void suppressLayout(boolean suppress) {
            ViewGroup viewGroup;
            if (this.mSuppressLayout && this.mLayoutSuppressed != suppress && (viewGroup = this.mParent) != null) {
                this.mLayoutSuppressed = suppress;
                viewGroup.suppressLayout(suppress);
            }
        }
    }
}
