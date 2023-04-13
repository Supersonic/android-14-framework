package android.inputmethodservice;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public class CompactExtractEditLayout extends LinearLayout {
    private View mInputExtractAccessories;
    private View mInputExtractAction;
    private View mInputExtractEditText;
    private boolean mPerformLayoutChanges;

    public CompactExtractEditLayout(Context context) {
        super(context);
    }

    public CompactExtractEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompactExtractEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mInputExtractEditText = findViewById(16908325);
        this.mInputExtractAccessories = findViewById(16908378);
        View findViewById = findViewById(16908377);
        this.mInputExtractAction = findViewById;
        if (this.mInputExtractEditText != null && this.mInputExtractAccessories != null && findViewById != null) {
            this.mPerformLayoutChanges = true;
        }
    }

    private int applyFractionInt(int fraction, int whole) {
        return Math.round(getResources().getFraction(fraction, whole, whole));
    }

    private static void setLayoutHeight(View v, int px) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.height = px;
        v.setLayoutParams(lp);
    }

    private static void setLayoutMarginBottom(View v, int px) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.bottomMargin = px;
        v.setLayoutParams(lp);
    }

    private void applyProportionalLayout(int screenWidthPx, int screenHeightPx) {
        if (getResources().getConfiguration().isScreenRound()) {
            setGravity(80);
        }
        setLayoutHeight(this, applyFractionInt(C4057R.fraction.input_extract_layout_height, screenHeightPx));
        setPadding(applyFractionInt(C4057R.fraction.input_extract_layout_padding_left, screenWidthPx), 0, applyFractionInt(C4057R.fraction.input_extract_layout_padding_right, screenWidthPx), 0);
        setLayoutMarginBottom(this.mInputExtractEditText, applyFractionInt(C4057R.fraction.input_extract_text_margin_bottom, screenHeightPx));
        setLayoutMarginBottom(this.mInputExtractAccessories, applyFractionInt(C4057R.fraction.input_extract_action_margin_bottom, screenHeightPx));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mPerformLayoutChanges) {
            Resources res = getResources();
            Configuration cfg = res.getConfiguration();
            DisplayMetrics dm = res.getDisplayMetrics();
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            if (cfg.isScreenRound() && heightPixels < widthPixels) {
                heightPixels = widthPixels;
            }
            applyProportionalLayout(widthPixels, heightPixels);
        }
    }
}
