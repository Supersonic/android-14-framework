package com.android.internal.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.widget.TextView;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public class TooltipPopup {
    private static final String TAG = "TooltipPopup";
    private final View mContentView;
    private final Context mContext;
    private final WindowManager.LayoutParams mLayoutParams;
    private final TextView mMessageView;
    private final int[] mTmpAnchorPos;
    private final int[] mTmpAppPos;
    private final Rect mTmpDisplayFrame;

    public TooltipPopup(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams = layoutParams;
        this.mTmpDisplayFrame = new Rect();
        this.mTmpAnchorPos = new int[2];
        this.mTmpAppPos = new int[2];
        this.mContext = context;
        View inflate = LayoutInflater.from(context).inflate(C4057R.layout.tooltip, (ViewGroup) null);
        this.mContentView = inflate;
        this.mMessageView = (TextView) inflate.findViewById(16908299);
        layoutParams.setTitle(context.getString(C4057R.string.tooltip_popup_title));
        layoutParams.packageName = context.getOpPackageName();
        layoutParams.type = 1005;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = C4057R.C4062style.Animation_Tooltip;
        layoutParams.flags = 24;
    }

    public void show(View anchorView, int anchorX, int anchorY, boolean fromTouch, CharSequence tooltipText) {
        if (isShowing()) {
            hide();
        }
        this.mMessageView.setText(tooltipText);
        computePosition(anchorView, anchorX, anchorY, fromTouch, this.mLayoutParams);
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(this.mContentView, this.mLayoutParams);
    }

    public void hide() {
        if (!isShowing()) {
            return;
        }
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(this.mContentView);
    }

    public View getContentView() {
        return this.mContentView;
    }

    public boolean isShowing() {
        return this.mContentView.getParent() != null;
    }

    private void computePosition(View anchorView, int anchorX, int anchorY, boolean fromTouch, WindowManager.LayoutParams outParams) {
        int offsetX;
        int offsetBelow;
        int offsetExtra;
        outParams.token = anchorView.getApplicationWindowToken();
        int tooltipPreciseAnchorThreshold = this.mContext.getResources().getDimensionPixelOffset(C4057R.dimen.tooltip_precise_anchor_threshold);
        if (anchorView.getWidth() >= tooltipPreciseAnchorThreshold) {
            offsetX = anchorX;
        } else {
            int offsetX2 = anchorView.getWidth();
            offsetX = offsetX2 / 2;
        }
        if (anchorView.getHeight() >= tooltipPreciseAnchorThreshold) {
            int offsetExtra2 = this.mContext.getResources().getDimensionPixelOffset(C4057R.dimen.tooltip_precise_anchor_extra_offset);
            offsetBelow = anchorY + offsetExtra2;
            offsetExtra = anchorY - offsetExtra2;
        } else {
            offsetBelow = anchorView.getHeight();
            offsetExtra = 0;
        }
        outParams.gravity = 49;
        int tooltipOffset = this.mContext.getResources().getDimensionPixelOffset(fromTouch ? C4057R.dimen.tooltip_y_offset_touch : C4057R.dimen.tooltip_y_offset_non_touch);
        View appView = WindowManagerGlobal.getInstance().getWindowView(anchorView.getApplicationWindowToken());
        if (appView == null) {
            Slog.m96e(TAG, "Cannot find app view");
            return;
        }
        appView.getWindowVisibleDisplayFrame(this.mTmpDisplayFrame);
        appView.getLocationOnScreen(this.mTmpAppPos);
        anchorView.getLocationOnScreen(this.mTmpAnchorPos);
        int[] iArr = this.mTmpAnchorPos;
        int i = iArr[0];
        int[] iArr2 = this.mTmpAppPos;
        int i2 = i - iArr2[0];
        iArr[0] = i2;
        iArr[1] = iArr[1] - iArr2[1];
        outParams.f504x = (i2 + offsetX) - (appView.getWidth() / 2);
        int spec = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mContentView.measure(spec, spec);
        int tooltipHeight = this.mContentView.getMeasuredHeight();
        int i3 = this.mTmpAnchorPos[1];
        int yAbove = ((i3 + offsetExtra) - tooltipOffset) - tooltipHeight;
        int yBelow = i3 + offsetBelow + tooltipOffset;
        if (!fromTouch) {
            if (yBelow + tooltipHeight <= this.mTmpDisplayFrame.height()) {
                outParams.f505y = yBelow;
            } else {
                outParams.f505y = yAbove;
            }
        } else if (yAbove >= 0) {
            outParams.f505y = yAbove;
        } else {
            outParams.f505y = yBelow;
        }
    }
}
