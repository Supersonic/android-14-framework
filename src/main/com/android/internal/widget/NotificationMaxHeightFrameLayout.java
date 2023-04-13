package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class NotificationMaxHeightFrameLayout extends FrameLayout {
    private final int mNotificationMaxHeight;

    public NotificationMaxHeightFrameLayout(Context context) {
        this(context, null, 0, 0);
    }

    public NotificationMaxHeightFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public NotificationMaxHeightFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationMaxHeightFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mNotificationMaxHeight = getFontScaledHeight(this.mContext, C4057R.dimen.notification_min_height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (View.MeasureSpec.getSize(heightMeasureSpec) > this.mNotificationMaxHeight) {
            int mode = View.MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mNotificationMaxHeight, mode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static int getFontScaledHeight(Context context, int dimenId) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(dimenId);
        float factor = Math.max(1.0f, context.getResources().getDisplayMetrics().scaledDensity / context.getResources().getDisplayMetrics().density);
        return (int) (dimensionPixelSize * factor);
    }
}
