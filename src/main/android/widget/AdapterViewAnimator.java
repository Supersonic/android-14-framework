package android.widget;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.p008os.Handler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes4.dex */
public abstract class AdapterViewAnimator extends AdapterView<Adapter> implements RemoteViewsAdapter.RemoteAdapterConnectionCallback, Advanceable {
    private static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final String TAG = "RemoteViewAnimator";
    static final int TOUCH_MODE_DOWN_IN_CURRENT_VIEW = 1;
    static final int TOUCH_MODE_HANDLED = 2;
    static final int TOUCH_MODE_NONE = 0;
    int mActiveOffset;
    Adapter mAdapter;
    boolean mAnimateFirstTime;
    int mCurrentWindowEnd;
    int mCurrentWindowStart;
    int mCurrentWindowStartUnbounded;
    AdapterView<Adapter>.AdapterDataSetObserver mDataSetObserver;
    boolean mDeferNotifyDataSetChanged;
    boolean mFirstTime;
    ObjectAnimator mInAnimation;
    boolean mLoopViews;
    int mMaxNumActiveViews;
    ObjectAnimator mOutAnimation;
    private Runnable mPendingCheckForTap;
    ArrayList<Integer> mPreviousViews;
    int mReferenceChildHeight;
    int mReferenceChildWidth;
    RemoteViewsAdapter mRemoteViewsAdapter;
    private int mRestoreWhichChild;
    private int mTouchMode;
    HashMap<Integer, ViewAndMetaData> mViewsMap;
    int mWhichChild;

    public AdapterViewAnimator(Context context) {
        this(context, null);
    }

    public AdapterViewAnimator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterViewAnimator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AdapterViewAnimator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mWhichChild = 0;
        this.mRestoreWhichChild = -1;
        this.mAnimateFirstTime = true;
        this.mActiveOffset = 0;
        this.mMaxNumActiveViews = 1;
        this.mViewsMap = new HashMap<>();
        this.mCurrentWindowStart = 0;
        this.mCurrentWindowEnd = -1;
        this.mCurrentWindowStartUnbounded = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mFirstTime = true;
        this.mLoopViews = true;
        this.mReferenceChildWidth = -1;
        this.mReferenceChildHeight = -1;
        this.mTouchMode = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.AdapterViewAnimator, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.AdapterViewAnimator, attrs, a, defStyleAttr, defStyleRes);
        int resource = a.getResourceId(0, 0);
        if (resource > 0) {
            setInAnimation(context, resource);
        } else {
            setInAnimation(getDefaultInAnimation());
        }
        int resource2 = a.getResourceId(1, 0);
        if (resource2 > 0) {
            setOutAnimation(context, resource2);
        } else {
            setOutAnimation(getDefaultOutAnimation());
        }
        boolean flag = a.getBoolean(2, true);
        setAnimateFirstView(flag);
        this.mLoopViews = a.getBoolean(3, false);
        a.recycle();
        initViewAnimator();
    }

    private void initViewAnimator() {
        this.mPreviousViews = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ViewAndMetaData {
        int adapterPosition;
        long itemId;
        int relativeIndex;
        View view;

        ViewAndMetaData(View view, int relativeIndex, int adapterPosition, long itemId) {
            this.view = view;
            this.relativeIndex = relativeIndex;
            this.adapterPosition = adapterPosition;
            this.itemId = itemId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void configureViewAnimator(int numVisibleViews, int activeOffset) {
        this.mMaxNumActiveViews = numVisibleViews;
        this.mActiveOffset = activeOffset;
        this.mPreviousViews.clear();
        this.mViewsMap.clear();
        removeAllViewsInLayout();
        this.mCurrentWindowStart = 0;
        this.mCurrentWindowEnd = -1;
    }

    void transformViewForTransition(int fromIndex, int toIndex, View view, boolean animate) {
        if (fromIndex == -1) {
            this.mInAnimation.setTarget(view);
            this.mInAnimation.start();
        } else if (toIndex == -1) {
            this.mOutAnimation.setTarget(view);
            this.mOutAnimation.start();
        }
    }

    ObjectAnimator getDefaultInAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat((Object) null, "alpha", 0.0f, 1.0f);
        anim.setDuration(200L);
        return anim;
    }

    ObjectAnimator getDefaultOutAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat((Object) null, "alpha", 1.0f, 0.0f);
        anim.setDuration(200L);
        return anim;
    }

    @RemotableViewMethod
    public void setDisplayedChild(int whichChild) {
        setDisplayedChild(whichChild, true);
    }

    private void setDisplayedChild(int whichChild, boolean animate) {
        if (this.mAdapter != null) {
            this.mWhichChild = whichChild;
            if (whichChild >= getWindowSize()) {
                this.mWhichChild = this.mLoopViews ? 0 : getWindowSize() - 1;
            } else if (whichChild < 0) {
                this.mWhichChild = this.mLoopViews ? getWindowSize() - 1 : 0;
            }
            boolean hasFocus = getFocusedChild() != null;
            showOnly(this.mWhichChild, animate);
            if (hasFocus) {
                requestFocus(2);
            }
        }
    }

    void applyTransformForChildAtIndex(View child, int relativeIndex) {
    }

    public int getDisplayedChild() {
        return this.mWhichChild;
    }

    public void showNext() {
        setDisplayedChild(this.mWhichChild + 1);
    }

    public void showPrevious() {
        setDisplayedChild(this.mWhichChild - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int modulo(int pos, int size) {
        if (size > 0) {
            return ((pos % size) + size) % size;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getViewAtRelativeIndex(int relativeIndex) {
        if (relativeIndex >= 0 && relativeIndex <= getNumActiveViews() - 1 && this.mAdapter != null) {
            int i = modulo(this.mCurrentWindowStartUnbounded + relativeIndex, getWindowSize());
            if (this.mViewsMap.get(Integer.valueOf(i)) != null) {
                return this.mViewsMap.get(Integer.valueOf(i)).view;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNumActiveViews() {
        if (this.mAdapter != null) {
            return Math.min(getCount() + 1, this.mMaxNumActiveViews);
        }
        return this.mMaxNumActiveViews;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWindowSize() {
        if (this.mAdapter != null) {
            int adapterCount = getCount();
            if (adapterCount <= getNumActiveViews() && this.mLoopViews) {
                return this.mMaxNumActiveViews * adapterCount;
            }
            return adapterCount;
        }
        return 0;
    }

    private ViewAndMetaData getMetaDataForChild(View child) {
        for (ViewAndMetaData vm : this.mViewsMap.values()) {
            if (vm.view == child) {
                return vm;
            }
        }
        return null;
    }

    ViewGroup.LayoutParams createOrReuseLayoutParams(View v) {
        ViewGroup.LayoutParams currentLp = v.getLayoutParams();
        if (currentLp != null) {
            return currentLp;
        }
        return new ViewGroup.LayoutParams(0, 0);
    }

    void refreshChildren() {
        View updatedChild;
        int adapterCount = this.mAdapter == null ? 0 : getCount();
        for (int i = this.mCurrentWindowStart; i <= this.mCurrentWindowEnd; i++) {
            int index = modulo(i, getWindowSize());
            if (i < adapterCount) {
                updatedChild = this.mAdapter.getView(modulo(i, adapterCount), null, this);
                if (updatedChild.getImportantForAccessibility() == 0) {
                    updatedChild.setImportantForAccessibility(1);
                }
            } else {
                updatedChild = null;
            }
            if (this.mViewsMap.containsKey(Integer.valueOf(index))) {
                FrameLayout fl = (FrameLayout) this.mViewsMap.get(Integer.valueOf(index)).view;
                fl.removeAllViewsInLayout();
                if (updatedChild != null) {
                    fl.addView(updatedChild);
                }
            }
        }
    }

    FrameLayout getFrameForChild() {
        return new FrameLayout(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showOnly(int childIndex, boolean animate) {
        int adapterCount;
        int newWindowStart;
        int newWindowEnd;
        boolean wrap;
        int oldRelativeIndex;
        int newWindowEndUnbounded;
        int adapterCount2;
        int newWindowStartUnbounded;
        int rangeEnd;
        int rangeStart;
        int newWindowEnd2;
        int newRelativeIndex;
        if (this.mAdapter == null || (adapterCount = getCount()) == 0) {
            return;
        }
        for (int i = 0; i < this.mPreviousViews.size(); i++) {
            View viewToRemove = this.mViewsMap.get(this.mPreviousViews.get(i)).view;
            this.mViewsMap.remove(this.mPreviousViews.get(i));
            viewToRemove.clearAnimation();
            if (viewToRemove instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) viewToRemove;
                vg.removeAllViewsInLayout();
            }
            applyTransformForChildAtIndex(viewToRemove, -1);
            removeViewInLayout(viewToRemove);
        }
        this.mPreviousViews.clear();
        int newWindowStartUnbounded2 = childIndex - this.mActiveOffset;
        int newWindowEndUnbounded2 = (getNumActiveViews() + newWindowStartUnbounded2) - 1;
        int newWindowStart2 = Math.max(0, newWindowStartUnbounded2);
        int newWindowEnd3 = Math.min(adapterCount - 1, newWindowEndUnbounded2);
        if (!this.mLoopViews) {
            newWindowStart = newWindowStart2;
            newWindowEnd = newWindowEnd3;
        } else {
            newWindowStart = newWindowStartUnbounded2;
            newWindowEnd = newWindowEndUnbounded2;
        }
        int newWindowStart3 = getWindowSize();
        int rangeStart2 = modulo(newWindowStart, newWindowStart3);
        int rangeEnd2 = modulo(newWindowEnd, getWindowSize());
        if (rangeStart2 <= rangeEnd2) {
            wrap = false;
        } else {
            wrap = true;
        }
        for (Integer index : this.mViewsMap.keySet()) {
            boolean remove = false;
            if (!wrap && (index.intValue() < rangeStart2 || index.intValue() > rangeEnd2)) {
                remove = true;
            } else if (wrap && index.intValue() > rangeEnd2 && index.intValue() < rangeStart2) {
                remove = true;
            }
            if (remove) {
                View previousView = this.mViewsMap.get(index).view;
                int oldRelativeIndex2 = this.mViewsMap.get(index).relativeIndex;
                this.mPreviousViews.add(index);
                transformViewForTransition(oldRelativeIndex2, -1, previousView, animate);
            }
        }
        if (newWindowStart != this.mCurrentWindowStart || newWindowEnd != this.mCurrentWindowEnd || newWindowStartUnbounded2 != this.mCurrentWindowStartUnbounded) {
            int i2 = newWindowStart;
            while (i2 <= newWindowEnd) {
                int index2 = modulo(i2, getWindowSize());
                if (this.mViewsMap.containsKey(Integer.valueOf(index2))) {
                    oldRelativeIndex = this.mViewsMap.get(Integer.valueOf(index2)).relativeIndex;
                } else {
                    oldRelativeIndex = -1;
                }
                int newRelativeIndex2 = i2 - newWindowStartUnbounded2;
                boolean inOldRange = this.mViewsMap.containsKey(Integer.valueOf(index2)) && !this.mPreviousViews.contains(Integer.valueOf(index2));
                if (inOldRange) {
                    View view = this.mViewsMap.get(Integer.valueOf(index2)).view;
                    this.mViewsMap.get(Integer.valueOf(index2)).relativeIndex = newRelativeIndex2;
                    applyTransformForChildAtIndex(view, newRelativeIndex2);
                    transformViewForTransition(oldRelativeIndex, newRelativeIndex2, view, animate);
                    rangeEnd = rangeEnd2;
                    newWindowEnd2 = newWindowEnd;
                    rangeStart = rangeStart2;
                    adapterCount2 = adapterCount;
                    newWindowStartUnbounded = newWindowStartUnbounded2;
                    newWindowEndUnbounded = newWindowEndUnbounded2;
                    newRelativeIndex = -1;
                } else {
                    int adapterPosition = modulo(i2, adapterCount);
                    View newView = this.mAdapter.getView(adapterPosition, null, this);
                    long itemId = this.mAdapter.getItemId(adapterPosition);
                    FrameLayout fl = getFrameForChild();
                    if (newView != null) {
                        fl.addView(newView);
                    }
                    newWindowEndUnbounded = newWindowEndUnbounded2;
                    adapterCount2 = adapterCount;
                    newWindowStartUnbounded = newWindowStartUnbounded2;
                    rangeEnd = rangeEnd2;
                    rangeStart = rangeStart2;
                    newWindowEnd2 = newWindowEnd;
                    this.mViewsMap.put(Integer.valueOf(index2), new ViewAndMetaData(fl, newRelativeIndex2, adapterPosition, itemId));
                    addChild(fl);
                    applyTransformForChildAtIndex(fl, newRelativeIndex2);
                    newRelativeIndex = -1;
                    transformViewForTransition(-1, newRelativeIndex2, fl, animate);
                }
                this.mViewsMap.get(Integer.valueOf(index2)).view.bringToFront();
                i2++;
                newWindowEnd = newWindowEnd2;
                newWindowEndUnbounded2 = newWindowEndUnbounded;
                adapterCount = adapterCount2;
                newWindowStartUnbounded2 = newWindowStartUnbounded;
                rangeEnd2 = rangeEnd;
                rangeStart2 = rangeStart;
            }
            int adapterCount3 = adapterCount;
            this.mCurrentWindowStart = newWindowStart;
            this.mCurrentWindowEnd = newWindowEnd;
            this.mCurrentWindowStartUnbounded = newWindowStartUnbounded2;
            if (this.mRemoteViewsAdapter != null) {
                int adapterStart = modulo(newWindowStart, adapterCount3);
                int adapterEnd = modulo(this.mCurrentWindowEnd, adapterCount3);
                this.mRemoteViewsAdapter.setVisibleRangeHint(adapterStart, adapterEnd);
            }
        }
        requestLayout();
        invalidate();
    }

    private void addChild(View child) {
        addViewInLayout(child, -1, createOrReuseLayoutParams(child));
        if (this.mReferenceChildWidth == -1 || this.mReferenceChildHeight == -1) {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            child.measure(measureSpec, measureSpec);
            this.mReferenceChildWidth = child.getMeasuredWidth();
            this.mReferenceChildHeight = child.getMeasuredHeight();
        }
    }

    void showTapFeedback(View v) {
        v.setPressed(true);
    }

    void hideTapFeedback(View v) {
        v.setPressed(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelHandleClick() {
        View v = getCurrentView();
        if (v != null) {
            hideTapFeedback(v);
        }
        this.mTouchMode = 0;
    }

    /* loaded from: classes4.dex */
    final class CheckForTap implements Runnable {
        CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AdapterViewAnimator.this.mTouchMode == 1) {
                View v = AdapterViewAnimator.this.getCurrentView();
                AdapterViewAnimator.this.showTapFeedback(v);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        boolean handled = false;
        switch (action) {
            case 0:
                View v = getCurrentView();
                if (v != null && isTransformedTouchPointInView(ev.getX(), ev.getY(), v, null)) {
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    this.mTouchMode = 1;
                    postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                    break;
                }
                break;
            case 1:
                if (this.mTouchMode == 1) {
                    final View v2 = getCurrentView();
                    final ViewAndMetaData viewData = getMetaDataForChild(v2);
                    if (v2 != null && isTransformedTouchPointInView(ev.getX(), ev.getY(), v2, null)) {
                        Handler handler = getHandler();
                        if (handler != null) {
                            handler.removeCallbacks(this.mPendingCheckForTap);
                        }
                        showTapFeedback(v2);
                        postDelayed(new Runnable() { // from class: android.widget.AdapterViewAnimator.1
                            @Override // java.lang.Runnable
                            public void run() {
                                AdapterViewAnimator.this.hideTapFeedback(v2);
                                AdapterViewAnimator.this.post(new Runnable() { // from class: android.widget.AdapterViewAnimator.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        if (viewData != null) {
                                            AdapterViewAnimator.this.performItemClick(v2, viewData.adapterPosition, viewData.itemId);
                                        } else {
                                            AdapterViewAnimator.this.performItemClick(v2, 0, 0L);
                                        }
                                    }
                                });
                            }
                        }, ViewConfiguration.getPressedStateDuration());
                        handled = true;
                    }
                }
                this.mTouchMode = 0;
                break;
            case 3:
                View v3 = getCurrentView();
                if (v3 != null) {
                    hideTapFeedback(v3);
                }
                this.mTouchMode = 0;
                break;
        }
        return handled;
    }

    private void measureChildren() {
        int count = getChildCount();
        int childWidth = (getMeasuredWidth() - this.mPaddingLeft) - this.mPaddingRight;
        int childHeight = (getMeasuredHeight() - this.mPaddingTop) - this.mPaddingBottom;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(childHeight, 1073741824));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean haveChildRefSize = (this.mReferenceChildWidth == -1 || this.mReferenceChildHeight == -1) ? false : true;
        if (heightSpecMode == 0) {
            heightSpecSize = haveChildRefSize ? this.mReferenceChildHeight + this.mPaddingTop + this.mPaddingBottom : 0;
        } else if (heightSpecMode == Integer.MIN_VALUE && haveChildRefSize) {
            int height = this.mReferenceChildHeight + this.mPaddingTop + this.mPaddingBottom;
            heightSpecSize = height > heightSpecSize ? heightSpecSize | 16777216 : height;
        }
        if (widthSpecMode == 0) {
            widthSpecSize = haveChildRefSize ? this.mReferenceChildWidth + this.mPaddingLeft + this.mPaddingRight : 0;
        } else if (heightSpecMode == Integer.MIN_VALUE && haveChildRefSize) {
            int width = this.mReferenceChildWidth + this.mPaddingLeft + this.mPaddingRight;
            widthSpecSize = width > widthSpecSize ? widthSpecSize | 16777216 : width;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        measureChildren();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkForAndHandleDataChanged() {
        boolean dataChanged = this.mDataChanged;
        if (dataChanged) {
            post(new Runnable() { // from class: android.widget.AdapterViewAnimator.2
                @Override // java.lang.Runnable
                public void run() {
                    AdapterViewAnimator.this.handleDataChanged();
                    if (AdapterViewAnimator.this.mWhichChild >= AdapterViewAnimator.this.getWindowSize()) {
                        AdapterViewAnimator.this.mWhichChild = 0;
                        AdapterViewAnimator adapterViewAnimator = AdapterViewAnimator.this;
                        adapterViewAnimator.showOnly(adapterViewAnimator.mWhichChild, false);
                    } else if (AdapterViewAnimator.this.mOldItemCount != AdapterViewAnimator.this.getCount()) {
                        AdapterViewAnimator adapterViewAnimator2 = AdapterViewAnimator.this;
                        adapterViewAnimator2.showOnly(adapterViewAnimator2.mWhichChild, false);
                    }
                    AdapterViewAnimator.this.refreshChildren();
                    AdapterViewAnimator.this.requestLayout();
                }
            });
        }
        this.mDataChanged = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        checkForAndHandleDataChanged();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childRight = this.mPaddingLeft + child.getMeasuredWidth();
            int childBottom = this.mPaddingTop + child.getMeasuredHeight();
            child.layout(this.mPaddingLeft, this.mPaddingTop, childRight, childBottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.AdapterViewAnimator.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int whichChild;

        SavedState(Parcelable superState, int whichChild) {
            super(superState);
            this.whichChild = whichChild;
        }

        private SavedState(Parcel in) {
            super(in);
            this.whichChild = in.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.whichChild);
        }

        public String toString() {
            return "AdapterViewAnimator.SavedState{ whichChild = " + this.whichChild + " }";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteViewsAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.saveRemoteViewsCache();
        }
        return new SavedState(superState, this.mWhichChild);
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        int i = ss.whichChild;
        this.mWhichChild = i;
        if (this.mRemoteViewsAdapter != null && this.mAdapter == null) {
            this.mRestoreWhichChild = i;
        } else {
            setDisplayedChild(i, false);
        }
    }

    public View getCurrentView() {
        return getViewAtRelativeIndex(this.mActiveOffset);
    }

    public ObjectAnimator getInAnimation() {
        return this.mInAnimation;
    }

    public void setInAnimation(ObjectAnimator inAnimation) {
        this.mInAnimation = inAnimation;
    }

    public ObjectAnimator getOutAnimation() {
        return this.mOutAnimation;
    }

    public void setOutAnimation(ObjectAnimator outAnimation) {
        this.mOutAnimation = outAnimation;
    }

    public void setInAnimation(Context context, int resourceID) {
        setInAnimation((ObjectAnimator) AnimatorInflater.loadAnimator(context, resourceID));
    }

    public void setOutAnimation(Context context, int resourceID) {
        setOutAnimation((ObjectAnimator) AnimatorInflater.loadAnimator(context, resourceID));
    }

    public void setAnimateFirstView(boolean animate) {
        this.mAnimateFirstTime = animate;
    }

    @Override // android.view.View
    public int getBaseline() {
        return getCurrentView() != null ? getCurrentView().getBaseline() : super.getBaseline();
    }

    @Override // android.widget.AdapterView
    public Adapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(Adapter adapter) {
        AdapterView<Adapter>.AdapterDataSetObserver adapterDataSetObserver;
        Adapter adapter2 = this.mAdapter;
        if (adapter2 != null && (adapterDataSetObserver = this.mDataSetObserver) != null) {
            adapter2.unregisterDataSetObserver(adapterDataSetObserver);
        }
        this.mAdapter = adapter;
        checkFocus();
        if (this.mAdapter != null) {
            AdapterView<Adapter>.AdapterDataSetObserver adapterDataSetObserver2 = new AdapterView.AdapterDataSetObserver();
            this.mDataSetObserver = adapterDataSetObserver2;
            this.mAdapter.registerDataSetObserver(adapterDataSetObserver2);
            this.mItemCount = this.mAdapter.getCount();
        }
        setFocusable(true);
        this.mWhichChild = 0;
        showOnly(0, false);
    }

    @RemotableViewMethod(asyncImpl = "setRemoteViewsAdapterAsync")
    public void setRemoteViewsAdapter(Intent intent) {
        setRemoteViewsAdapter(intent, false);
    }

    public Runnable setRemoteViewsAdapterAsync(Intent intent) {
        return new RemoteViewsAdapter.AsyncRemoteAdapterAction(this, intent);
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void setRemoteViewsAdapter(Intent intent, boolean isAsync) {
        if (this.mRemoteViewsAdapter != null) {
            Intent.FilterComparison fcNew = new Intent.FilterComparison(intent);
            Intent.FilterComparison fcOld = new Intent.FilterComparison(this.mRemoteViewsAdapter.getRemoteViewsServiceIntent());
            if (fcNew.equals(fcOld)) {
                return;
            }
        }
        this.mDeferNotifyDataSetChanged = false;
        RemoteViewsAdapter remoteViewsAdapter = new RemoteViewsAdapter(getContext(), intent, this, isAsync);
        this.mRemoteViewsAdapter = remoteViewsAdapter;
        if (remoteViewsAdapter.isDataReady()) {
            setAdapter(this.mRemoteViewsAdapter);
        }
    }

    public void setRemoteViewsOnClickHandler(RemoteViews.InteractionHandler handler) {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteViewsAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.setRemoteViewsInteractionHandler(handler);
        }
    }

    @Override // android.widget.AdapterView
    public void setSelection(int position) {
        setDisplayedChild(position);
    }

    @Override // android.widget.AdapterView
    public View getSelectedView() {
        return getViewAtRelativeIndex(this.mActiveOffset);
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void deferNotifyDataSetChanged() {
        this.mDeferNotifyDataSetChanged = true;
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public boolean onRemoteAdapterConnected() {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteViewsAdapter;
        if (remoteViewsAdapter != this.mAdapter) {
            setAdapter(remoteViewsAdapter);
            if (this.mDeferNotifyDataSetChanged) {
                this.mRemoteViewsAdapter.notifyDataSetChanged();
                this.mDeferNotifyDataSetChanged = false;
            }
            int i = this.mRestoreWhichChild;
            if (i > -1) {
                setDisplayedChild(i, false);
                this.mRestoreWhichChild = -1;
            }
            return false;
        } else if (remoteViewsAdapter != null) {
            remoteViewsAdapter.superNotifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void onRemoteAdapterDisconnected() {
    }

    @Override // android.widget.Advanceable
    public void advance() {
        showNext();
    }

    @Override // android.widget.Advanceable
    public void fyiWillBeAdvancedByHostKThx() {
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AdapterViewAnimator.class.getName();
    }
}
