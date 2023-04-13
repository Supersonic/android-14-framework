package android.inputmethodservice.navigationbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.navigationbar.KeyButtonRipple;
import android.media.AudioManager;
import android.p008os.Bundle;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public class KeyButtonView extends ImageView implements ButtonInterface {
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO = 3.0f;
    private static final String TAG = KeyButtonView.class.getSimpleName();
    private AudioManager mAudioManager;
    private final Runnable mCheckLongPress;
    private int mCode;
    private float mDarkIntensity;
    private long mDownTime;
    private boolean mGestureAborted;
    private boolean mHasOvalBg;
    boolean mLongClicked;
    private View.OnClickListener mOnClickListener;
    private final Paint mOvalBgPaint;
    private final boolean mPlaySounds;
    private final KeyButtonRipple mRipple;
    private int mTouchDownX;
    private int mTouchDownY;
    private boolean mTracking;

    public KeyButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOvalBgPaint = new Paint(3);
        this.mHasOvalBg = false;
        this.mCheckLongPress = new Runnable() { // from class: android.inputmethodservice.navigationbar.KeyButtonView.1
            @Override // java.lang.Runnable
            public void run() {
                if (KeyButtonView.this.isPressed()) {
                    if (KeyButtonView.this.isLongClickable()) {
                        KeyButtonView.this.performLongClick();
                        KeyButtonView.this.mLongClicked = true;
                        return;
                    }
                    if (KeyButtonView.this.mCode != 0) {
                        KeyButtonView.this.sendEvent(0, 128);
                        KeyButtonView.this.sendAccessibilityEvent(2);
                    }
                    KeyButtonView.this.mLongClicked = true;
                }
            }
        };
        switch (getId()) {
            case C4057R.C4059id.input_method_nav_back /* 16909132 */:
                this.mCode = 4;
                break;
            default:
                this.mCode = 0;
                break;
        }
        this.mPlaySounds = true;
        setClickable(true);
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        KeyButtonRipple keyButtonRipple = new KeyButtonRipple(context, this, C4057R.dimen.input_method_nav_key_button_ripple_max_width);
        this.mRipple = keyButtonRipple;
        setBackground(keyButtonRipple);
        setWillNotDraw(false);
        forceHasOverlappingRendering(false);
    }

    @Override // android.view.View
    public boolean isClickable() {
        return this.mCode != 0 || super.isClickable();
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        super.setOnClickListener(onClickListener);
        this.mOnClickListener = onClickListener;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.mCode != 0) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, (CharSequence) null));
            if (isLongClickable()) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, (CharSequence) null));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != 0) {
            jumpDrawablesToCurrentState();
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (action == 16 && this.mCode != 0) {
            sendEvent(0, 0, SystemClock.uptimeMillis());
            sendEvent(1, this.mTracking ? 512 : 0);
            this.mTracking = false;
            sendAccessibilityEvent(1);
            playSoundEffect(0);
            return true;
        } else if (action == 32 && this.mCode != 0) {
            sendEvent(0, 128);
            sendEvent(1, this.mTracking ? 512 : 0);
            this.mTracking = false;
            sendAccessibilityEvent(2);
            return true;
        } else {
            return super.performAccessibilityActionInternal(action, arguments);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        View.OnClickListener onClickListener;
        int action = ev.getAction();
        if (action == 0) {
            this.mGestureAborted = false;
        }
        if (this.mGestureAborted) {
            setPressed(false);
            return false;
        }
        switch (action) {
            case 0:
                this.mDownTime = SystemClock.uptimeMillis();
                this.mLongClicked = false;
                setPressed(true);
                this.mTouchDownX = (int) ev.getRawX();
                this.mTouchDownY = (int) ev.getRawY();
                if (this.mCode != 0) {
                    sendEvent(0, 0, this.mDownTime);
                } else {
                    performHapticFeedback(1);
                }
                playSoundEffect(0);
                removeCallbacks(this.mCheckLongPress);
                postDelayed(this.mCheckLongPress, ViewConfiguration.getLongPressTimeout());
                break;
            case 1:
                boolean doIt = isPressed() && !this.mLongClicked;
                setPressed(false);
                boolean doHapticFeedback = SystemClock.uptimeMillis() - this.mDownTime > 150;
                if (doHapticFeedback && !this.mLongClicked) {
                    performHapticFeedback(8);
                }
                if (this.mCode != 0) {
                    if (doIt) {
                        sendEvent(1, this.mTracking ? 512 : 0);
                        this.mTracking = false;
                        sendAccessibilityEvent(1);
                    } else {
                        sendEvent(1, 32);
                    }
                } else if (doIt && (onClickListener = this.mOnClickListener) != null) {
                    onClickListener.onClick(this);
                    sendAccessibilityEvent(1);
                }
                removeCallbacks(this.mCheckLongPress);
                break;
            case 2:
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                float slop = getQuickStepTouchSlopPx(getContext());
                if (Math.abs(x - this.mTouchDownX) > slop || Math.abs(y - this.mTouchDownY) > slop) {
                    setPressed(false);
                    removeCallbacks(this.mCheckLongPress);
                    break;
                }
                break;
            case 3:
                setPressed(false);
                if (this.mCode != 0) {
                    sendEvent(1, 32);
                }
                removeCallbacks(this.mCheckLongPress);
                break;
        }
        return true;
    }

    @Override // android.widget.ImageView, android.inputmethodservice.navigationbar.ButtonInterface
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null) {
            return;
        }
        KeyButtonDrawable keyButtonDrawable = (KeyButtonDrawable) drawable;
        keyButtonDrawable.setDarkIntensity(this.mDarkIntensity);
        boolean hasOvalBg = keyButtonDrawable.hasOvalBg();
        this.mHasOvalBg = hasOvalBg;
        if (hasOvalBg) {
            this.mOvalBgPaint.setColor(keyButtonDrawable.getDrawableBackgroundColor());
        }
        this.mRipple.setType(keyButtonDrawable.hasOvalBg() ? KeyButtonRipple.Type.OVAL : KeyButtonRipple.Type.ROUNDED_RECT);
    }

    @Override // android.view.View
    public void playSoundEffect(int soundConstant) {
        if (this.mPlaySounds) {
            this.mAudioManager.playSoundEffect(soundConstant);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEvent(int action, int flags) {
        sendEvent(action, flags, SystemClock.uptimeMillis());
    }

    private void sendEvent(int action, int flags, long when) {
        boolean handled;
        InputConnection ic;
        if (this.mCode == 4) {
        }
        if (this.mContext instanceof InputMethodService) {
            boolean z = true;
            int repeatCount = (flags & 128) != 0 ? 1 : 0;
            KeyEvent ev = new KeyEvent(this.mDownTime, when, action, this.mCode, repeatCount, 0, -1, 0, flags | 2 | 64, 257);
            int displayId = -1;
            if (getDisplay() != null) {
                displayId = getDisplay().getDisplayId();
            }
            if (displayId != -1) {
                ev.setDisplayId(displayId);
            }
            InputMethodService ims = (InputMethodService) this.mContext;
            switch (action) {
                case 0:
                    boolean handled2 = ims.onKeyDown(ev.getKeyCode(), ev);
                    if (!handled2 || ev.getRepeatCount() != 0 || (ev.getFlags() & 1073741824) == 0) {
                        z = false;
                    }
                    this.mTracking = z;
                    handled = handled2;
                    break;
                case 1:
                    handled = ims.onKeyUp(ev.getKeyCode(), ev);
                    break;
                default:
                    handled = false;
                    break;
            }
            if (!handled && (ic = ims.getCurrentInputConnection()) != null) {
                ic.sendKeyEvent(ev);
            }
        }
    }

    @Override // android.inputmethodservice.navigationbar.ButtonInterface
    public void setDarkIntensity(float darkIntensity) {
        this.mDarkIntensity = darkIntensity;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            ((KeyButtonDrawable) drawable).setDarkIntensity(darkIntensity);
            invalidate();
        }
        this.mRipple.setDarkIntensity(darkIntensity);
    }

    @Override // android.inputmethodservice.navigationbar.ButtonInterface
    public void setDelayTouchFeedback(boolean shouldDelay) {
        this.mRipple.setDelayTouchFeedback(shouldDelay);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mHasOvalBg) {
            int d = Math.min(getWidth(), getHeight());
            canvas.drawOval(0.0f, 0.0f, d, d, this.mOvalBgPaint);
        }
        super.draw(canvas);
    }

    private static float getQuickStepTouchSlopPx(Context context) {
        return ViewConfiguration.get(context).getScaledTouchSlop() * 3.0f;
    }
}
