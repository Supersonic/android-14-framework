package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import java.util.function.Consumer;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class ObservableTextView extends TextView {
    private Consumer<Integer> mOnVisibilityChangedListener;

    public ObservableTextView(Context context) {
        super(context);
    }

    public ObservableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ObservableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        Consumer<Integer> consumer;
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && (consumer = this.mOnVisibilityChangedListener) != null) {
            consumer.accept(Integer.valueOf(visibility));
        }
    }

    public void setOnVisibilityChangedListener(Consumer<Integer> listener) {
        this.mOnVisibilityChangedListener = listener;
    }
}
