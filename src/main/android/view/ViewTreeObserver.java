package android.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public final class ViewTreeObserver {
    private static boolean sIllegalOnDrawModificationIsFatal;
    private boolean mAlive = true;
    private CopyOnWriteArray<Consumer<List<Rect>>> mGestureExclusionListeners;
    private boolean mInDispatchOnDraw;
    private String mLastDispatchOnPreDrawCanceledReason;
    private CopyOnWriteArray<OnComputeInternalInsetsListener> mOnComputeInternalInsetsListeners;
    private ArrayList<OnDrawListener> mOnDrawListeners;
    private CopyOnWriteArrayList<OnEnterAnimationCompleteListener> mOnEnterAnimationCompleteListeners;
    private ArrayList<Runnable> mOnFrameCommitListeners;
    private CopyOnWriteArrayList<OnGlobalFocusChangeListener> mOnGlobalFocusListeners;
    private CopyOnWriteArray<OnGlobalLayoutListener> mOnGlobalLayoutListeners;
    private CopyOnWriteArray<OnPreDrawListener> mOnPreDrawListeners;
    private CopyOnWriteArray<OnScrollChangedListener> mOnScrollChangedListeners;
    private CopyOnWriteArrayList<OnTouchModeChangeListener> mOnTouchModeChangeListeners;
    private CopyOnWriteArrayList<OnWindowAttachListener> mOnWindowAttachListeners;
    private CopyOnWriteArrayList<OnWindowFocusChangeListener> mOnWindowFocusListeners;
    private CopyOnWriteArray<OnWindowShownListener> mOnWindowShownListeners;
    private CopyOnWriteArrayList<OnWindowVisibilityChangeListener> mOnWindowVisibilityListeners;
    private boolean mWindowShown;

    /* loaded from: classes4.dex */
    public interface OnComputeInternalInsetsListener {
        void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo);
    }

    /* loaded from: classes4.dex */
    public interface OnDrawListener {
        void onDraw();
    }

    /* loaded from: classes4.dex */
    public interface OnEnterAnimationCompleteListener {
        void onEnterAnimationComplete();
    }

    /* loaded from: classes4.dex */
    public interface OnGlobalFocusChangeListener {
        void onGlobalFocusChanged(View view, View view2);
    }

    /* loaded from: classes4.dex */
    public interface OnGlobalLayoutListener {
        void onGlobalLayout();
    }

    /* loaded from: classes4.dex */
    public interface OnPreDrawListener {
        boolean onPreDraw();
    }

    /* loaded from: classes4.dex */
    public interface OnScrollChangedListener {
        void onScrollChanged();
    }

    /* loaded from: classes4.dex */
    public interface OnTouchModeChangeListener {
        void onTouchModeChanged(boolean z);
    }

    /* loaded from: classes4.dex */
    public interface OnWindowAttachListener {
        void onWindowAttached();

        void onWindowDetached();
    }

    /* loaded from: classes4.dex */
    public interface OnWindowFocusChangeListener {
        void onWindowFocusChanged(boolean z);
    }

    /* loaded from: classes4.dex */
    public interface OnWindowShownListener {
        void onWindowShown();
    }

    /* loaded from: classes4.dex */
    public interface OnWindowVisibilityChangeListener {
        void onWindowVisibilityChanged(int i);
    }

    /* loaded from: classes4.dex */
    public static final class InternalInsetsInfo {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;
        int mTouchableInsets;
        public final Rect contentInsets = new Rect();
        public final Rect visibleInsets = new Rect();
        public final Region touchableRegion = new Region();

        public void setTouchableInsets(int val) {
            this.mTouchableInsets = val;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void reset() {
            this.contentInsets.setEmpty();
            this.visibleInsets.setEmpty();
            this.touchableRegion.setEmpty();
            this.mTouchableInsets = 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isEmpty() {
            return this.contentInsets.isEmpty() && this.visibleInsets.isEmpty() && this.touchableRegion.isEmpty() && this.mTouchableInsets == 0;
        }

        public int hashCode() {
            int result = this.contentInsets.hashCode();
            return (((((result * 31) + this.visibleInsets.hashCode()) * 31) + this.touchableRegion.hashCode()) * 31) + this.mTouchableInsets;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            InternalInsetsInfo other = (InternalInsetsInfo) o;
            if (this.mTouchableInsets == other.mTouchableInsets && this.contentInsets.equals(other.contentInsets) && this.visibleInsets.equals(other.visibleInsets) && this.touchableRegion.equals(other.touchableRegion)) {
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void set(InternalInsetsInfo other) {
            this.contentInsets.set(other.contentInsets);
            this.visibleInsets.set(other.visibleInsets);
            this.touchableRegion.set(other.touchableRegion);
            this.mTouchableInsets = other.mTouchableInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewTreeObserver(Context context) {
        sIllegalOnDrawModificationIsFatal = context.getApplicationInfo().targetSdkVersion >= 26;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void merge(ViewTreeObserver observer) {
        CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList = observer.mOnWindowAttachListeners;
        if (copyOnWriteArrayList != null) {
            CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList2 = this.mOnWindowAttachListeners;
            if (copyOnWriteArrayList2 != null) {
                copyOnWriteArrayList2.addAll(copyOnWriteArrayList);
            } else {
                this.mOnWindowAttachListeners = copyOnWriteArrayList;
            }
        }
        CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList3 = observer.mOnWindowFocusListeners;
        if (copyOnWriteArrayList3 != null) {
            CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList4 = this.mOnWindowFocusListeners;
            if (copyOnWriteArrayList4 != null) {
                copyOnWriteArrayList4.addAll(copyOnWriteArrayList3);
            } else {
                this.mOnWindowFocusListeners = copyOnWriteArrayList3;
            }
        }
        CopyOnWriteArrayList<OnWindowVisibilityChangeListener> copyOnWriteArrayList5 = observer.mOnWindowVisibilityListeners;
        if (copyOnWriteArrayList5 != null) {
            CopyOnWriteArrayList<OnWindowVisibilityChangeListener> copyOnWriteArrayList6 = this.mOnWindowVisibilityListeners;
            if (copyOnWriteArrayList6 != null) {
                copyOnWriteArrayList6.addAll(copyOnWriteArrayList5);
            } else {
                this.mOnWindowVisibilityListeners = copyOnWriteArrayList5;
            }
        }
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList7 = observer.mOnGlobalFocusListeners;
        if (copyOnWriteArrayList7 != null) {
            CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList8 = this.mOnGlobalFocusListeners;
            if (copyOnWriteArrayList8 != null) {
                copyOnWriteArrayList8.addAll(copyOnWriteArrayList7);
            } else {
                this.mOnGlobalFocusListeners = copyOnWriteArrayList7;
            }
        }
        CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray = observer.mOnGlobalLayoutListeners;
        if (copyOnWriteArray != null) {
            CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray2 = this.mOnGlobalLayoutListeners;
            if (copyOnWriteArray2 != null) {
                copyOnWriteArray2.addAll(copyOnWriteArray);
            } else {
                this.mOnGlobalLayoutListeners = copyOnWriteArray;
            }
        }
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray3 = observer.mOnPreDrawListeners;
        if (copyOnWriteArray3 != null) {
            CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray4 = this.mOnPreDrawListeners;
            if (copyOnWriteArray4 != null) {
                copyOnWriteArray4.addAll(copyOnWriteArray3);
            } else {
                this.mOnPreDrawListeners = copyOnWriteArray3;
            }
        }
        ArrayList<OnDrawListener> arrayList = observer.mOnDrawListeners;
        if (arrayList != null) {
            ArrayList<OnDrawListener> arrayList2 = this.mOnDrawListeners;
            if (arrayList2 != null) {
                arrayList2.addAll(arrayList);
            } else {
                this.mOnDrawListeners = arrayList;
            }
        }
        if (observer.mOnFrameCommitListeners != null) {
            ArrayList<Runnable> arrayList3 = this.mOnFrameCommitListeners;
            if (arrayList3 != null) {
                arrayList3.addAll(observer.captureFrameCommitCallbacks());
            } else {
                this.mOnFrameCommitListeners = observer.captureFrameCommitCallbacks();
            }
        }
        CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList9 = observer.mOnTouchModeChangeListeners;
        if (copyOnWriteArrayList9 != null) {
            CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList10 = this.mOnTouchModeChangeListeners;
            if (copyOnWriteArrayList10 != null) {
                copyOnWriteArrayList10.addAll(copyOnWriteArrayList9);
            } else {
                this.mOnTouchModeChangeListeners = copyOnWriteArrayList9;
            }
        }
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray5 = observer.mOnComputeInternalInsetsListeners;
        if (copyOnWriteArray5 != null) {
            CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray6 = this.mOnComputeInternalInsetsListeners;
            if (copyOnWriteArray6 != null) {
                copyOnWriteArray6.addAll(copyOnWriteArray5);
            } else {
                this.mOnComputeInternalInsetsListeners = copyOnWriteArray5;
            }
        }
        CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray7 = observer.mOnScrollChangedListeners;
        if (copyOnWriteArray7 != null) {
            CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray8 = this.mOnScrollChangedListeners;
            if (copyOnWriteArray8 != null) {
                copyOnWriteArray8.addAll(copyOnWriteArray7);
            } else {
                this.mOnScrollChangedListeners = copyOnWriteArray7;
            }
        }
        CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray9 = observer.mOnWindowShownListeners;
        if (copyOnWriteArray9 != null) {
            CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray10 = this.mOnWindowShownListeners;
            if (copyOnWriteArray10 != null) {
                copyOnWriteArray10.addAll(copyOnWriteArray9);
            } else {
                this.mOnWindowShownListeners = copyOnWriteArray9;
            }
        }
        CopyOnWriteArray<Consumer<List<Rect>>> copyOnWriteArray11 = observer.mGestureExclusionListeners;
        if (copyOnWriteArray11 != null) {
            CopyOnWriteArray<Consumer<List<Rect>>> copyOnWriteArray12 = this.mGestureExclusionListeners;
            if (copyOnWriteArray12 != null) {
                copyOnWriteArray12.addAll(copyOnWriteArray11);
            } else {
                this.mGestureExclusionListeners = copyOnWriteArray11;
            }
        }
        observer.kill();
    }

    public void addOnWindowAttachListener(OnWindowAttachListener listener) {
        checkIsAlive();
        if (this.mOnWindowAttachListeners == null) {
            this.mOnWindowAttachListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowAttachListeners.add(listener);
    }

    public void removeOnWindowAttachListener(OnWindowAttachListener victim) {
        checkIsAlive();
        CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList = this.mOnWindowAttachListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(victim);
    }

    public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener listener) {
        checkIsAlive();
        if (this.mOnWindowFocusListeners == null) {
            this.mOnWindowFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowFocusListeners.add(listener);
    }

    public void removeOnWindowFocusChangeListener(OnWindowFocusChangeListener victim) {
        checkIsAlive();
        CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList = this.mOnWindowFocusListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(victim);
    }

    public void addOnWindowVisibilityChangeListener(OnWindowVisibilityChangeListener listener) {
        checkIsAlive();
        if (this.mOnWindowVisibilityListeners == null) {
            this.mOnWindowVisibilityListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowVisibilityListeners.add(listener);
    }

    public void removeOnWindowVisibilityChangeListener(OnWindowVisibilityChangeListener victim) {
        checkIsAlive();
        CopyOnWriteArrayList<OnWindowVisibilityChangeListener> copyOnWriteArrayList = this.mOnWindowVisibilityListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(victim);
    }

    public void addOnGlobalFocusChangeListener(OnGlobalFocusChangeListener listener) {
        checkIsAlive();
        if (this.mOnGlobalFocusListeners == null) {
            this.mOnGlobalFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnGlobalFocusListeners.add(listener);
    }

    public void removeOnGlobalFocusChangeListener(OnGlobalFocusChangeListener victim) {
        checkIsAlive();
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList = this.mOnGlobalFocusListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(victim);
    }

    public void addOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        checkIsAlive();
        if (this.mOnGlobalLayoutListeners == null) {
            this.mOnGlobalLayoutListeners = new CopyOnWriteArray<>();
        }
        this.mOnGlobalLayoutListeners.add(listener);
    }

    @Deprecated
    public void removeGlobalOnLayoutListener(OnGlobalLayoutListener victim) {
        removeOnGlobalLayoutListener(victim);
    }

    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener victim) {
        checkIsAlive();
        CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray = this.mOnGlobalLayoutListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(victim);
    }

    public void addOnPreDrawListener(OnPreDrawListener listener) {
        checkIsAlive();
        if (this.mOnPreDrawListeners == null) {
            this.mOnPreDrawListeners = new CopyOnWriteArray<>();
        }
        this.mOnPreDrawListeners.add(listener);
    }

    public void removeOnPreDrawListener(OnPreDrawListener victim) {
        checkIsAlive();
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray = this.mOnPreDrawListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(victim);
    }

    public void addOnWindowShownListener(OnWindowShownListener listener) {
        checkIsAlive();
        if (this.mOnWindowShownListeners == null) {
            this.mOnWindowShownListeners = new CopyOnWriteArray<>();
        }
        this.mOnWindowShownListeners.add(listener);
        if (this.mWindowShown) {
            listener.onWindowShown();
        }
    }

    public void removeOnWindowShownListener(OnWindowShownListener victim) {
        checkIsAlive();
        CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray = this.mOnWindowShownListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(victim);
    }

    public void addOnDrawListener(OnDrawListener listener) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            this.mOnDrawListeners = new ArrayList<>();
        }
        if (this.mInDispatchOnDraw) {
            IllegalStateException ex = new IllegalStateException("Cannot call addOnDrawListener inside of onDraw");
            if (sIllegalOnDrawModificationIsFatal) {
                throw ex;
            }
            Log.m109e("ViewTreeObserver", ex.getMessage(), ex);
        }
        this.mOnDrawListeners.add(listener);
    }

    public void removeOnDrawListener(OnDrawListener victim) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            return;
        }
        if (this.mInDispatchOnDraw) {
            IllegalStateException ex = new IllegalStateException("Cannot call removeOnDrawListener inside of onDraw");
            if (sIllegalOnDrawModificationIsFatal) {
                throw ex;
            }
            Log.m109e("ViewTreeObserver", ex.getMessage(), ex);
        }
        this.mOnDrawListeners.remove(victim);
    }

    public void registerFrameCommitCallback(Runnable callback) {
        checkIsAlive();
        if (this.mOnFrameCommitListeners == null) {
            this.mOnFrameCommitListeners = new ArrayList<>();
        }
        this.mOnFrameCommitListeners.add(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Runnable> captureFrameCommitCallbacks() {
        ArrayList<Runnable> ret = this.mOnFrameCommitListeners;
        this.mOnFrameCommitListeners = null;
        return ret;
    }

    public boolean unregisterFrameCommitCallback(Runnable callback) {
        checkIsAlive();
        ArrayList<Runnable> arrayList = this.mOnFrameCommitListeners;
        if (arrayList == null) {
            return false;
        }
        return arrayList.remove(callback);
    }

    public void addOnScrollChangedListener(OnScrollChangedListener listener) {
        checkIsAlive();
        if (this.mOnScrollChangedListeners == null) {
            this.mOnScrollChangedListeners = new CopyOnWriteArray<>();
        }
        this.mOnScrollChangedListeners.add(listener);
    }

    public void removeOnScrollChangedListener(OnScrollChangedListener victim) {
        checkIsAlive();
        CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray = this.mOnScrollChangedListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(victim);
    }

    public void addOnTouchModeChangeListener(OnTouchModeChangeListener listener) {
        checkIsAlive();
        if (this.mOnTouchModeChangeListeners == null) {
            this.mOnTouchModeChangeListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnTouchModeChangeListeners.add(listener);
    }

    public void removeOnTouchModeChangeListener(OnTouchModeChangeListener victim) {
        checkIsAlive();
        CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList = this.mOnTouchModeChangeListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(victim);
    }

    public void addOnComputeInternalInsetsListener(OnComputeInternalInsetsListener listener) {
        checkIsAlive();
        if (this.mOnComputeInternalInsetsListeners == null) {
            this.mOnComputeInternalInsetsListeners = new CopyOnWriteArray<>();
        }
        this.mOnComputeInternalInsetsListeners.add(listener);
    }

    public void removeOnComputeInternalInsetsListener(OnComputeInternalInsetsListener victim) {
        checkIsAlive();
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray = this.mOnComputeInternalInsetsListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(victim);
    }

    public void addOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener listener) {
        checkIsAlive();
        if (this.mOnEnterAnimationCompleteListeners == null) {
            this.mOnEnterAnimationCompleteListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnEnterAnimationCompleteListeners.add(listener);
    }

    public void removeOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener listener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnEnterAnimationCompleteListener> copyOnWriteArrayList = this.mOnEnterAnimationCompleteListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(listener);
    }

    public void addOnSystemGestureExclusionRectsChangedListener(Consumer<List<Rect>> listener) {
        checkIsAlive();
        if (this.mGestureExclusionListeners == null) {
            this.mGestureExclusionListeners = new CopyOnWriteArray<>();
        }
        this.mGestureExclusionListeners.add(listener);
    }

    public void removeOnSystemGestureExclusionRectsChangedListener(Consumer<List<Rect>> listener) {
        checkIsAlive();
        CopyOnWriteArray<Consumer<List<Rect>>> copyOnWriteArray = this.mGestureExclusionListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(listener);
    }

    private void checkIsAlive() {
        if (!this.mAlive) {
            throw new IllegalStateException("This ViewTreeObserver is not alive, call getViewTreeObserver() again");
        }
    }

    public boolean isAlive() {
        return this.mAlive;
    }

    private void kill() {
        this.mAlive = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnWindowAttachedChange(boolean attached) {
        CopyOnWriteArrayList<OnWindowAttachListener> listeners = this.mOnWindowAttachListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnWindowAttachListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnWindowAttachListener listener = it.next();
                if (attached) {
                    listener.onWindowAttached();
                } else {
                    listener.onWindowDetached();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnWindowFocusChange(boolean hasFocus) {
        CopyOnWriteArrayList<OnWindowFocusChangeListener> listeners = this.mOnWindowFocusListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnWindowFocusChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnWindowFocusChangeListener listener = it.next();
                listener.onWindowFocusChanged(hasFocus);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchOnWindowVisibilityChange(int visibility) {
        CopyOnWriteArrayList<OnWindowVisibilityChangeListener> listeners = this.mOnWindowVisibilityListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnWindowVisibilityChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnWindowVisibilityChangeListener listener = it.next();
                listener.onWindowVisibilityChanged(visibility);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnGlobalFocusChange(View oldFocus, View newFocus) {
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> listeners = this.mOnGlobalFocusListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnGlobalFocusChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnGlobalFocusChangeListener listener = it.next();
                listener.onGlobalFocusChanged(oldFocus, newFocus);
            }
        }
    }

    public final void dispatchOnGlobalLayout() {
        CopyOnWriteArray<OnGlobalLayoutListener> listeners = this.mOnGlobalLayoutListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnGlobalLayoutListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onGlobalLayout();
                }
            } finally {
                listeners.end();
            }
        }
    }

    final boolean hasOnPreDrawListeners() {
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray = this.mOnPreDrawListeners;
        return copyOnWriteArray != null && copyOnWriteArray.size() > 0;
    }

    public final boolean dispatchOnPreDraw() {
        this.mLastDispatchOnPreDrawCanceledReason = null;
        boolean cancelDraw = false;
        CopyOnWriteArray<OnPreDrawListener> listeners = this.mOnPreDrawListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnPreDrawListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    OnPreDrawListener preDrawListener = access.get(i);
                    cancelDraw |= !preDrawListener.onPreDraw();
                    if (cancelDraw) {
                        this.mLastDispatchOnPreDrawCanceledReason = preDrawListener.getClass().getName();
                    }
                }
            } finally {
                listeners.end();
            }
        }
        return cancelDraw;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getLastDispatchOnPreDrawCanceledReason() {
        return this.mLastDispatchOnPreDrawCanceledReason;
    }

    public final void dispatchOnWindowShown() {
        this.mWindowShown = true;
        CopyOnWriteArray<OnWindowShownListener> listeners = this.mOnWindowShownListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnWindowShownListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onWindowShown();
                }
            } finally {
                listeners.end();
            }
        }
    }

    public final void dispatchOnDraw() {
        if (this.mOnDrawListeners != null) {
            this.mInDispatchOnDraw = true;
            ArrayList<OnDrawListener> listeners = this.mOnDrawListeners;
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                listeners.get(i).onDraw();
            }
            this.mInDispatchOnDraw = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnTouchModeChanged(boolean inTouchMode) {
        CopyOnWriteArrayList<OnTouchModeChangeListener> listeners = this.mOnTouchModeChangeListeners;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnTouchModeChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnTouchModeChangeListener listener = it.next();
                listener.onTouchModeChanged(inTouchMode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnScrollChanged() {
        CopyOnWriteArray<OnScrollChangedListener> listeners = this.mOnScrollChangedListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnScrollChangedListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onScrollChanged();
                }
            } finally {
                listeners.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasComputeInternalInsetsListeners() {
        CopyOnWriteArray<OnComputeInternalInsetsListener> listeners = this.mOnComputeInternalInsetsListeners;
        return listeners != null && listeners.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchOnComputeInternalInsets(InternalInsetsInfo inoutInfo) {
        CopyOnWriteArray<OnComputeInternalInsetsListener> listeners = this.mOnComputeInternalInsetsListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<OnComputeInternalInsetsListener> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onComputeInternalInsets(inoutInfo);
                }
            } finally {
                listeners.end();
            }
        }
    }

    public final void dispatchOnEnterAnimationComplete() {
        CopyOnWriteArrayList<OnEnterAnimationCompleteListener> listeners = this.mOnEnterAnimationCompleteListeners;
        if (listeners != null && !listeners.isEmpty()) {
            Iterator<OnEnterAnimationCompleteListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnEnterAnimationCompleteListener listener = it.next();
                listener.onEnterAnimationComplete();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchOnSystemGestureExclusionRectsChanged(List<Rect> rects) {
        CopyOnWriteArray<Consumer<List<Rect>>> listeners = this.mGestureExclusionListeners;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<Consumer<List<Rect>>> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).accept(rects);
                }
            } finally {
                listeners.end();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class CopyOnWriteArray<T> {
        private ArrayList<T> mDataCopy;
        private boolean mStart;
        private ArrayList<T> mData = new ArrayList<>();
        private final Access<T> mAccess = new Access<>();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static class Access<T> {
            private ArrayList<T> mData;
            private int mSize;

            Access() {
            }

            T get(int index) {
                return this.mData.get(index);
            }

            int size() {
                return this.mSize;
            }
        }

        CopyOnWriteArray() {
        }

        private ArrayList<T> getArray() {
            if (this.mStart) {
                if (this.mDataCopy == null) {
                    this.mDataCopy = new ArrayList<>(this.mData);
                }
                return this.mDataCopy;
            }
            return this.mData;
        }

        Access<T> start() {
            if (this.mStart) {
                throw new IllegalStateException("Iteration already started");
            }
            this.mStart = true;
            this.mDataCopy = null;
            ((Access) this.mAccess).mData = this.mData;
            ((Access) this.mAccess).mSize = this.mData.size();
            return this.mAccess;
        }

        void end() {
            if (!this.mStart) {
                throw new IllegalStateException("Iteration not started");
            }
            this.mStart = false;
            ArrayList<T> arrayList = this.mDataCopy;
            if (arrayList != null) {
                this.mData = arrayList;
                ((Access) this.mAccess).mData.clear();
                ((Access) this.mAccess).mSize = 0;
            }
            this.mDataCopy = null;
        }

        int size() {
            return getArray().size();
        }

        void add(T item) {
            getArray().add(item);
        }

        void addAll(CopyOnWriteArray<T> array) {
            getArray().addAll(array.mData);
        }

        void remove(T item) {
            getArray().remove(item);
        }

        void clear() {
            getArray().clear();
        }
    }
}
