package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import java.util.ArrayList;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class MediaNotificationView extends FrameLayout {
    private ArrayList<VisibilityChangeListener> mListeners;

    /* loaded from: classes5.dex */
    public interface VisibilityChangeListener {
        void onAggregatedVisibilityChanged(boolean z);
    }

    public MediaNotificationView(Context context) {
        this(context, null);
    }

    public MediaNotificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MediaNotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (this.mListeners != null) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                this.mListeners.get(i).onAggregatedVisibilityChanged(isVisible);
            }
        }
    }

    public void addVisibilityListener(VisibilityChangeListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeVisibilityListener(VisibilityChangeListener listener) {
        ArrayList<VisibilityChangeListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.remove(listener);
        }
    }
}
