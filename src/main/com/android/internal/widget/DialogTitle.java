package com.android.internal.widget;

import android.C0001R;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;
/* loaded from: classes5.dex */
public class DialogTitle extends TextView {
    public DialogTitle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DialogTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DialogTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogTitle(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lineCount;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null && (lineCount = layout.getLineCount()) > 0) {
            int ellipsisCount = layout.getEllipsisCount(lineCount - 1);
            if (ellipsisCount > 0) {
                setSingleLine(false);
                setMaxLines(2);
                TypedArray a = this.mContext.obtainStyledAttributes(null, C0001R.styleable.TextAppearance, 16842817, 16973892);
                int textSize = a.getDimensionPixelSize(0, 0);
                if (textSize != 0) {
                    setTextSize(0, textSize);
                }
                a.recycle();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }
}
