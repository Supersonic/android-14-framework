package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.widget.CachingIconView;
import com.android.internal.widget.NotificationExpandButton;
import java.util.ArrayList;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class NotificationHeaderView extends RelativeLayout {
    private boolean mAcceptAllTouches;
    private View mAltExpandTarget;
    private Drawable mBackground;
    private boolean mEntireHeaderClickable;
    private NotificationExpandButton mExpandButton;
    private View.OnClickListener mExpandClickListener;
    private boolean mExpandOnlyOnButton;
    private CachingIconView mIcon;
    ViewOutlineProvider mProvider;
    private NotificationTopLineView mTopLineView;
    private HeaderTouchListener mTouchListener;
    private final int mTouchableHeight;

    public NotificationHeaderView(Context context) {
        this(context, null);
    }

    public NotificationHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTouchListener = new HeaderTouchListener();
        this.mProvider = new ViewOutlineProvider() { // from class: android.view.NotificationHeaderView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                if (NotificationHeaderView.this.mBackground != null) {
                    outline.setRect(0, 0, NotificationHeaderView.this.getWidth(), NotificationHeaderView.this.getHeight());
                    outline.setAlpha(1.0f);
                }
            }
        };
        Resources res = getResources();
        this.mTouchableHeight = res.getDimensionPixelSize(C4057R.dimen.notification_header_touchable_height);
        this.mEntireHeaderClickable = res.getBoolean(C4057R.bool.config_notificationHeaderClickableForExpand);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIcon = (CachingIconView) findViewById(16908294);
        this.mTopLineView = (NotificationTopLineView) findViewById(C4057R.C4059id.notification_top_line);
        this.mExpandButton = (NotificationExpandButton) findViewById(C4057R.C4059id.expand_button);
        this.mAltExpandTarget = findViewById(C4057R.C4059id.alternate_expand_target);
        setClipToPadding(false);
    }

    public void setHeaderBackgroundDrawable(Drawable drawable) {
        if (drawable != null) {
            setWillNotDraw(false);
            this.mBackground = drawable;
            drawable.setCallback(this);
            setOutlineProvider(this.mProvider);
        } else {
            setWillNotDraw(true);
            this.mBackground = null;
            setOutlineProvider(null);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
            this.mBackground.draw(canvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mBackground;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.isStateful()) {
            this.mBackground.setState(getDrawableState());
        }
    }

    private void updateTouchListener() {
        if (this.mExpandClickListener == null) {
            setOnTouchListener(null);
            return;
        }
        setOnTouchListener(this.mTouchListener);
        this.mTouchListener.bindTouchRects();
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        this.mExpandClickListener = l;
        this.mExpandButton.setOnClickListener(l);
        this.mAltExpandTarget.setOnClickListener(this.mExpandClickListener);
        updateTouchListener();
    }

    public void setTopLineExtraMarginEnd(int extraMarginEnd) {
        this.mTopLineView.setHeaderTextMarginEnd(extraMarginEnd);
    }

    @RemotableViewMethod
    public void setTopLineExtraMarginEndDp(float extraMarginEndDp) {
        setTopLineExtraMarginEnd((int) (getResources().getDisplayMetrics().density * extraMarginEndDp));
    }

    @RemotableViewMethod
    public void styleTextAsTitle(boolean styleTextAsTitle) {
        int styleResId;
        if (styleTextAsTitle) {
            styleResId = C4057R.C4062style.TextAppearance_DeviceDefault_Notification_Title;
        } else {
            styleResId = C4057R.C4062style.TextAppearance_DeviceDefault_Notification_Info;
        }
        View headerText = findViewById(C4057R.C4059id.header_text);
        if (headerText instanceof TextView) {
            ((TextView) headerText).setTextAppearance(styleResId);
        }
        View appNameText = findViewById(C4057R.C4059id.app_name_text);
        if (appNameText instanceof TextView) {
            ((TextView) appNameText).setTextAppearance(styleResId);
        }
    }

    /* loaded from: classes4.dex */
    public class HeaderTouchListener implements View.OnTouchListener {
        private Rect mAltExpandTargetRect;
        private float mDownX;
        private float mDownY;
        private Rect mExpandButtonRect;
        private final ArrayList<Rect> mTouchRects = new ArrayList<>();
        private int mTouchSlop;
        private boolean mTrackGesture;

        public HeaderTouchListener() {
        }

        public void bindTouchRects() {
            this.mTouchRects.clear();
            addRectAroundView(NotificationHeaderView.this.mIcon);
            this.mExpandButtonRect = addRectAroundView(NotificationHeaderView.this.mExpandButton);
            this.mAltExpandTargetRect = addRectAroundView(NotificationHeaderView.this.mAltExpandTarget);
            addWidthRect();
            this.mTouchSlop = ViewConfiguration.get(NotificationHeaderView.this.getContext()).getScaledTouchSlop();
        }

        private void addWidthRect() {
            Rect r = new Rect();
            r.top = 0;
            r.bottom = NotificationHeaderView.this.mTouchableHeight;
            r.left = 0;
            r.right = NotificationHeaderView.this.getWidth();
            this.mTouchRects.add(r);
        }

        private Rect addRectAroundView(View view) {
            Rect r = getRectAroundView(view);
            this.mTouchRects.add(r);
            return r;
        }

        private Rect getRectAroundView(View view) {
            float size = NotificationHeaderView.this.getResources().getDisplayMetrics().density * 48.0f;
            float width = Math.max(size, view.getWidth());
            float height = Math.max(size, view.getHeight());
            Rect r = new Rect();
            if (view.getVisibility() == 8) {
                view = NotificationHeaderView.this.getFirstChildNotGone();
                r.left = (int) (view.getLeft() - (width / 2.0f));
            } else {
                r.left = (int) (((view.getLeft() + view.getRight()) / 2.0f) - (width / 2.0f));
            }
            r.top = (int) (((view.getTop() + view.getBottom()) / 2.0f) - (height / 2.0f));
            r.bottom = (int) (r.top + height);
            r.right = (int) (r.left + width);
            return r;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getActionMasked() & 255) {
                case 0:
                    this.mTrackGesture = false;
                    if (isInside(x, y)) {
                        this.mDownX = x;
                        this.mDownY = y;
                        this.mTrackGesture = true;
                        return true;
                    }
                    break;
                case 1:
                    if (this.mTrackGesture) {
                        float topLineX = NotificationHeaderView.this.mTopLineView.getX();
                        float topLineY = NotificationHeaderView.this.mTopLineView.getY();
                        if (!NotificationHeaderView.this.mTopLineView.onTouchUp(x - topLineX, y - topLineY, this.mDownX - topLineX, this.mDownY - topLineY)) {
                            NotificationHeaderView.this.mExpandButton.performClick();
                            break;
                        }
                    }
                    break;
                case 2:
                    if (this.mTrackGesture && (Math.abs(this.mDownX - x) > this.mTouchSlop || Math.abs(this.mDownY - y) > this.mTouchSlop)) {
                        this.mTrackGesture = false;
                        break;
                    }
                    break;
            }
            return this.mTrackGesture;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isInside(float x, float y) {
            if (NotificationHeaderView.this.mAcceptAllTouches) {
                return true;
            }
            if (NotificationHeaderView.this.mExpandOnlyOnButton) {
                return this.mExpandButtonRect.contains((int) x, (int) y) || this.mAltExpandTargetRect.contains((int) x, (int) y);
            }
            for (int i = 0; i < this.mTouchRects.size(); i++) {
                Rect r = this.mTouchRects.get(i);
                if (r.contains((int) x, (int) y)) {
                    return true;
                }
            }
            float topLineX = x - NotificationHeaderView.this.mTopLineView.getX();
            float topLineY = y - NotificationHeaderView.this.mTopLineView.getY();
            return NotificationHeaderView.this.mTopLineView.isInTouchRect(topLineX, topLineY);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getFirstChildNotGone() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                return child;
            }
        }
        return this;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isInTouchRect(float x, float y) {
        if (this.mExpandClickListener == null) {
            return false;
        }
        return this.mTouchListener.isInside(x, y);
    }

    @RemotableViewMethod
    public void setAcceptAllTouches(boolean acceptAllTouches) {
        this.mAcceptAllTouches = this.mEntireHeaderClickable || acceptAllTouches;
    }

    @RemotableViewMethod
    public void setExpandOnlyOnButton(boolean expandOnlyOnButton) {
        this.mExpandOnlyOnButton = expandOnlyOnButton;
    }
}
