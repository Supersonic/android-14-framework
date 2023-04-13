package android.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.HandwritingInitiator;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public class HandwritingInitiator {
    private final int mHandwritingSlop;
    private final InputMethodManager mImm;
    private State mState;
    private final HandwritingAreaTracker mHandwritingAreasTracker = new HandwritingAreaTracker();
    public WeakReference<View> mConnectedView = null;
    private int mConnectionCount = 0;
    private View mCachedHoverTarget = null;
    private final long mHandwritingTimeoutInMillis = ViewConfiguration.getLongPressTimeout();

    public HandwritingInitiator(ViewConfiguration viewConfiguration, InputMethodManager inputMethodManager) {
        this.mHandwritingSlop = viewConfiguration.getScaledHandwritingSlop();
        this.mImm = inputMethodManager;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        State state;
        int maskedAction = motionEvent.getActionMasked();
        switch (maskedAction) {
            case 0:
            case 5:
                this.mState = null;
                int actionIndex = motionEvent.getActionIndex();
                int toolType = motionEvent.getToolType(actionIndex);
                if (toolType != 2 && toolType != 4) {
                    return false;
                }
                this.mState = new State(motionEvent);
                return false;
            case 1:
            case 3:
                state = this.mState;
                if (state != null) {
                    state.mShouldInitHandwriting = false;
                }
                return false;
            case 2:
                State state2 = this.mState;
                if (state2 == null) {
                    return false;
                }
                if (!state2.mShouldInitHandwriting || this.mState.mExceedHandwritingSlop) {
                    return this.mState.mHasInitiatedHandwriting;
                }
                long timeElapsed = motionEvent.getEventTime() - this.mState.mStylusDownTimeInMillis;
                if (timeElapsed > this.mHandwritingTimeoutInMillis) {
                    this.mState.mShouldInitHandwriting = false;
                    return this.mState.mHasInitiatedHandwriting;
                }
                int pointerIndex = motionEvent.findPointerIndex(this.mState.mStylusPointerId);
                float x = motionEvent.getX(pointerIndex);
                float y = motionEvent.getY(pointerIndex);
                if (largerThanTouchSlop(x, y, this.mState.mStylusDownX, this.mState.mStylusDownY)) {
                    this.mState.mExceedHandwritingSlop = true;
                    View candidateView = findBestCandidateView(this.mState.mStylusDownX, this.mState.mStylusDownY);
                    if (candidateView != null) {
                        if (candidateView == getConnectedView()) {
                            startHandwriting(candidateView);
                        } else if (candidateView.getHandwritingDelegatorCallback() != null) {
                            String delegatePackageName = candidateView.getAllowedHandwritingDelegatePackageName();
                            if (delegatePackageName == null) {
                                delegatePackageName = candidateView.getContext().getOpPackageName();
                            }
                            this.mImm.prepareStylusHandwritingDelegation(candidateView, delegatePackageName);
                            candidateView.getHandwritingDelegatorCallback().run();
                        } else if (candidateView.getRevealOnFocusHint()) {
                            candidateView.setRevealOnFocusHint(false);
                            candidateView.requestFocus();
                            candidateView.setRevealOnFocusHint(true);
                        } else {
                            candidateView.requestFocus();
                        }
                    }
                }
                return this.mState.mHasInitiatedHandwriting;
            case 4:
            default:
                return false;
            case 6:
                int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                State state3 = this.mState;
                if (state3 == null || pointerId != state3.mStylusPointerId) {
                    return false;
                }
                state = this.mState;
                if (state != null) {
                }
                return false;
        }
    }

    private View getConnectedView() {
        WeakReference<View> weakReference = this.mConnectedView;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    private void clearConnectedView() {
        this.mConnectedView = null;
        this.mConnectionCount = 0;
    }

    public void onInputConnectionCreated(View view) {
        State state;
        if (!view.isAutoHandwritingEnabled()) {
            clearConnectedView();
            return;
        }
        View connectedView = getConnectedView();
        if (connectedView == view) {
            this.mConnectionCount++;
            return;
        }
        this.mConnectedView = new WeakReference<>(view);
        this.mConnectionCount = 1;
        if ((!view.isHandwritingDelegate() || !tryAcceptStylusHandwritingDelegation(view)) && (state = this.mState) != null && state.mShouldInitHandwriting) {
            tryStartHandwriting();
        }
    }

    public void onInputConnectionClosed(View view) {
        View connectedView = getConnectedView();
        if (connectedView == null) {
            return;
        }
        if (connectedView == view) {
            int i = this.mConnectionCount - 1;
            this.mConnectionCount = i;
            if (i == 0) {
                clearConnectedView();
                return;
            }
            return;
        }
        clearConnectedView();
    }

    private void tryStartHandwriting() {
        View connectedView;
        if (!this.mState.mExceedHandwritingSlop || (connectedView = getConnectedView()) == null) {
            return;
        }
        if (!connectedView.isAutoHandwritingEnabled()) {
            clearConnectedView();
            return;
        }
        Rect handwritingArea = getViewHandwritingArea(connectedView);
        if (isInHandwritingArea(handwritingArea, this.mState.mStylusDownX, this.mState.mStylusDownY, connectedView)) {
            startHandwriting(connectedView);
        } else {
            this.mState.mShouldInitHandwriting = false;
        }
    }

    public void startHandwriting(View view) {
        this.mImm.startStylusHandwriting(view);
        this.mState.mHasInitiatedHandwriting = true;
        this.mState.mShouldInitHandwriting = false;
    }

    public boolean tryAcceptStylusHandwritingDelegation(View view) {
        String delegatorPackageName = view.getAllowedHandwritingDelegatorPackageName();
        if (delegatorPackageName == null) {
            delegatorPackageName = view.getContext().getOpPackageName();
        }
        if (this.mImm.acceptStylusHandwritingDelegation(view, delegatorPackageName)) {
            State state = this.mState;
            if (state != null) {
                state.mHasInitiatedHandwriting = true;
                this.mState.mShouldInitHandwriting = false;
            }
            return true;
        }
        return false;
    }

    public void updateHandwritingAreasForView(View view) {
        this.mHandwritingAreasTracker.updateHandwritingAreaForView(view);
    }

    private static boolean shouldTriggerStylusHandwritingForView(View view) {
        if (!view.isAutoHandwritingEnabled()) {
            return false;
        }
        return view.isStylusHandwritingAvailable();
    }

    public PointerIcon onResolvePointerIcon(Context context, MotionEvent event) {
        if (shouldShowHandwritingPointerIcon(event)) {
            return PointerIcon.getSystemIcon(context, 1022);
        }
        return null;
    }

    private boolean shouldShowHandwritingPointerIcon(MotionEvent event) {
        if (event.isStylusPointer() && event.isHoverEvent()) {
            if (event.getActionMasked() == 9 || event.getActionMasked() == 7) {
                float hoverX = event.getX(event.getActionIndex());
                float hoverY = event.getY(event.getActionIndex());
                View view = this.mCachedHoverTarget;
                if (view != null) {
                    Rect handwritingArea = getViewHandwritingArea(view);
                    if (isInHandwritingArea(handwritingArea, hoverX, hoverY, this.mCachedHoverTarget) && shouldTriggerStylusHandwritingForView(this.mCachedHoverTarget)) {
                        return true;
                    }
                }
                View candidateView = findBestCandidateView(hoverX, hoverY);
                if (candidateView != null) {
                    this.mCachedHoverTarget = candidateView;
                    return true;
                }
            }
            this.mCachedHoverTarget = null;
            return false;
        }
        return false;
    }

    private View findBestCandidateView(float x, float y) {
        float minDistance = Float.MAX_VALUE;
        View bestCandidate = null;
        View connectedView = getConnectedView();
        if (connectedView != null) {
            Rect handwritingArea = getViewHandwritingArea(connectedView);
            if (isInHandwritingArea(handwritingArea, x, y, connectedView) && shouldTriggerStylusHandwritingForView(connectedView)) {
                float distance = distance(handwritingArea, x, y);
                if (distance == 0.0f) {
                    return connectedView;
                }
                bestCandidate = connectedView;
                minDistance = distance;
            }
        }
        List<HandwritableViewInfo> handwritableViewInfos = this.mHandwritingAreasTracker.computeViewInfos();
        for (HandwritableViewInfo viewInfo : handwritableViewInfos) {
            View view = viewInfo.getView();
            Rect handwritingArea2 = viewInfo.getHandwritingArea();
            if (isInHandwritingArea(handwritingArea2, x, y, view) && shouldTriggerStylusHandwritingForView(view)) {
                float distance2 = distance(handwritingArea2, x, y);
                if (distance2 == 0.0f) {
                    return view;
                }
                if (distance2 < minDistance) {
                    minDistance = distance2;
                    bestCandidate = view;
                }
            }
        }
        return bestCandidate;
    }

    private static float distance(Rect rect, float x, float y) {
        float xDistance;
        float yDistance;
        if (contains(rect, x, y, 0.0f, 0.0f, 0.0f, 0.0f)) {
            return 0.0f;
        }
        if (x >= rect.left && x < rect.right) {
            xDistance = 0.0f;
        } else if (x < rect.left) {
            xDistance = rect.left - x;
        } else {
            xDistance = x - rect.right;
        }
        if (y >= rect.top && y < rect.bottom) {
            yDistance = 0.0f;
        } else if (y < rect.top) {
            yDistance = rect.top - y;
        } else {
            yDistance = y - rect.bottom;
        }
        return (xDistance * xDistance) + (yDistance * yDistance);
    }

    private static Rect getViewHandwritingArea(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent != null && view.isAttachedToWindow() && view.isAggregatedVisible()) {
            Rect localHandwritingArea = view.getHandwritingArea();
            Rect globalHandwritingArea = new Rect();
            if (localHandwritingArea != null) {
                globalHandwritingArea.set(localHandwritingArea);
            } else {
                globalHandwritingArea.set(0, 0, view.getWidth(), view.getHeight());
            }
            if (viewParent.getChildVisibleRect(view, globalHandwritingArea, null)) {
                return globalHandwritingArea;
            }
        }
        return null;
    }

    private static boolean isInHandwritingArea(Rect handwritingArea, float x, float y, View view) {
        if (handwritingArea == null) {
            return false;
        }
        return contains(handwritingArea, x, y, view.getHandwritingBoundsOffsetLeft(), view.getHandwritingBoundsOffsetTop(), view.getHandwritingBoundsOffsetRight(), view.getHandwritingBoundsOffsetBottom());
    }

    private static boolean contains(Rect rect, float x, float y, float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
        return x >= ((float) rect.left) - offsetLeft && x < ((float) rect.right) + offsetRight && y >= ((float) rect.top) - offsetTop && y < ((float) rect.bottom) + offsetBottom;
    }

    private boolean largerThanTouchSlop(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float f = (dx * dx) + (dy * dy);
        int i = this.mHandwritingSlop;
        return f > ((float) (i * i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class State {
        private boolean mExceedHandwritingSlop;
        private boolean mHasInitiatedHandwriting;
        private boolean mShouldInitHandwriting;
        private final long mStylusDownTimeInMillis;
        private final float mStylusDownX;
        private final float mStylusDownY;
        private final int mStylusPointerId;

        private State(MotionEvent motionEvent) {
            int actionIndex = motionEvent.getActionIndex();
            this.mStylusPointerId = motionEvent.getPointerId(actionIndex);
            this.mStylusDownTimeInMillis = motionEvent.getEventTime();
            this.mStylusDownX = motionEvent.getX(actionIndex);
            this.mStylusDownY = motionEvent.getY(actionIndex);
            this.mShouldInitHandwriting = true;
            this.mHasInitiatedHandwriting = false;
            this.mExceedHandwritingSlop = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isViewActive(View view) {
        return view != null && view.isAttachedToWindow() && view.isAggregatedVisible() && view.isAutoHandwritingEnabled();
    }

    /* loaded from: classes4.dex */
    public static class HandwritingAreaTracker {
        private final List<HandwritableViewInfo> mHandwritableViewInfos = new ArrayList();

        public void updateHandwritingAreaForView(View view) {
            Iterator<HandwritableViewInfo> iterator = this.mHandwritableViewInfos.iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                HandwritableViewInfo handwritableViewInfo = iterator.next();
                View curView = handwritableViewInfo.getView();
                if (!HandwritingInitiator.isViewActive(curView)) {
                    iterator.remove();
                }
                if (curView == view) {
                    found = true;
                    handwritableViewInfo.mIsDirty = true;
                }
            }
            if (!found && HandwritingInitiator.isViewActive(view)) {
                this.mHandwritableViewInfos.add(new HandwritableViewInfo(view));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$computeViewInfos$0(HandwritableViewInfo viewInfo) {
            return !viewInfo.update();
        }

        public List<HandwritableViewInfo> computeViewInfos() {
            this.mHandwritableViewInfos.removeIf(new Predicate() { // from class: android.view.HandwritingInitiator$HandwritingAreaTracker$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return HandwritingInitiator.HandwritingAreaTracker.lambda$computeViewInfos$0((HandwritingInitiator.HandwritableViewInfo) obj);
                }
            });
            return this.mHandwritableViewInfos;
        }
    }

    /* loaded from: classes4.dex */
    public static class HandwritableViewInfo {
        Rect mHandwritingArea = null;
        public boolean mIsDirty = true;
        final WeakReference<View> mViewRef;

        public HandwritableViewInfo(View view) {
            this.mViewRef = new WeakReference<>(view);
        }

        public View getView() {
            return this.mViewRef.get();
        }

        public Rect getHandwritingArea() {
            return this.mHandwritingArea;
        }

        public boolean update() {
            View view = getView();
            if (HandwritingInitiator.isViewActive(view)) {
                if (this.mIsDirty) {
                    Rect handwritingArea = view.getHandwritingArea();
                    if (handwritingArea == null) {
                        return false;
                    }
                    ViewParent parent = view.getParent();
                    if (parent != null) {
                        if (this.mHandwritingArea == null) {
                            this.mHandwritingArea = new Rect();
                        }
                        this.mHandwritingArea.set(handwritingArea);
                        if (!parent.getChildVisibleRect(view, this.mHandwritingArea, null)) {
                            this.mHandwritingArea = null;
                        }
                    }
                    this.mIsDirty = false;
                    return true;
                }
                return true;
            }
            return false;
        }
    }
}
