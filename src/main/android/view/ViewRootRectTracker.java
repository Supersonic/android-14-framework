package android.view;

import android.graphics.Rect;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ViewRootRectTracker {
    private final Function<View, List<Rect>> mRectCollector;
    private boolean mViewsChanged = false;
    private boolean mRootRectsChanged = false;
    private List<Rect> mRootRects = Collections.emptyList();
    private List<ViewInfo> mViewInfos = new ArrayList();
    private List<Rect> mRects = Collections.emptyList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewRootRectTracker(Function<View, List<Rect>> rectCollector) {
        this.mRectCollector = rectCollector;
    }

    public void updateRectsForView(View view) {
        boolean found = false;
        Iterator<ViewInfo> i = this.mViewInfos.iterator();
        while (true) {
            if (!i.hasNext()) {
                break;
            }
            ViewInfo info = i.next();
            View v = info.getView();
            if (v == null || !v.isAttachedToWindow() || !v.isAggregatedVisible()) {
                this.mViewsChanged = true;
                i.remove();
            } else if (v == view) {
                found = true;
                info.mDirty = true;
                break;
            }
        }
        if (!found && view.isAttachedToWindow()) {
            this.mViewInfos.add(new ViewInfo(view));
            this.mViewsChanged = true;
        }
    }

    public List<Rect> computeChangedRects() {
        if (computeChanges()) {
            return this.mRects;
        }
        return null;
    }

    public boolean computeChanges() {
        boolean changed = this.mRootRectsChanged;
        Iterator<ViewInfo> i = this.mViewInfos.iterator();
        List<Rect> rects = new ArrayList<>(this.mRootRects);
        while (i.hasNext()) {
            ViewInfo info = i.next();
            switch (info.update()) {
                case 0:
                    changed = true;
                    break;
                case 2:
                    this.mViewsChanged = true;
                    i.remove();
                    continue;
            }
            rects.addAll(info.mRects);
        }
        if (changed || this.mViewsChanged) {
            this.mViewsChanged = false;
            this.mRootRectsChanged = false;
            if (!this.mRects.equals(rects)) {
                this.mRects = rects;
                return true;
            }
        }
        return false;
    }

    public List<Rect> getLastComputedRects() {
        return this.mRects;
    }

    public void setRootRects(List<Rect> rects) {
        Preconditions.checkNotNull(rects, "rects must not be null");
        this.mRootRects = rects;
        this.mRootRectsChanged = true;
    }

    public List<Rect> getRootRects() {
        return this.mRootRects;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Rect> getTrackedRectsForView(View v) {
        List<Rect> rects = this.mRectCollector.apply(v);
        return rects == null ? Collections.emptyList() : rects;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ViewInfo {
        public static final int CHANGED = 0;
        public static final int GONE = 2;
        public static final int UNCHANGED = 1;
        boolean mDirty = true;
        List<Rect> mRects = Collections.emptyList();
        private final WeakReference<View> mView;

        ViewInfo(View view) {
            this.mView = new WeakReference<>(view);
        }

        public View getView() {
            return this.mView.get();
        }

        public int update() {
            View view = getView();
            if (view == null || !view.isAttachedToWindow() || !view.isAggregatedVisible()) {
                return 2;
            }
            List<Rect> localRects = ViewRootRectTracker.this.getTrackedRectsForView(view);
            List<Rect> newRects = new ArrayList<>(localRects.size());
            for (Rect src : localRects) {
                Rect mappedRect = new Rect(src);
                ViewParent p = view.getParent();
                if (p != null && p.getChildVisibleRect(view, mappedRect, null)) {
                    newRects.add(mappedRect);
                }
            }
            if (this.mRects.equals(localRects)) {
                return 1;
            }
            this.mRects = newRects;
            return 0;
        }
    }
}
