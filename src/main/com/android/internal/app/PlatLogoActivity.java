package com.android.internal.app;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AnalogClock;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.C4057R;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.json.JSONObject;
/* loaded from: classes4.dex */
public class PlatLogoActivity extends Activity {
    private static final String[][] EMOJI_SETS = {new String[]{"🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "\u1f96d", "🍎", "🍏", "🍐", "🍑", "🍒", "🍓", "\u1fad0", "🥝"}, new String[]{"😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"}, new String[]{"😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "\u1fae0", "😉", "😊", "😇", "\u1f970", "😍", "🤩", "😘", "😗", "☺️", "😚", "😙", "\u1f972", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "\u1fae2", "\u1fae3", "🤫", "🤔", "\u1fae1", "🤐", "🤨", "😐", "😑", "😶", "\u1fae5", "😏", "😒", "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷"}, new String[]{"🤩", "😍", "\u1f970", "😘", "\u1f973", "\u1f972", "\u1f979"}, new String[]{"\u1fae0"}, new String[]{"💘", "💝", "💖", "💗", "💓", "💞", "💕", "❣", "💔", "❤", "🧡", "💛", "💚", "💙", "💜", "\u1f90e", "🖤", "\u1f90d"}, new String[]{"👽", "🛸", "✨", "🌟", "💫", "🚀", "\u1fa90", "🌙", "⭐", "🌍"}, new String[]{"🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘"}, new String[]{"🐙", "\u1fab8", "🦑", "🦀", "🦐", "🐡", "\u1f99e", "🐠", "🐟", "🐳", "🐋", "🐬", "\u1fae7", "🌊", "🦈"}, new String[]{"🙈", "🙉", "🙊", "🐵", "🐒"}, new String[]{"♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓"}, new String[]{"🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒", "🕞", "🕓", "🕟", "🕔", "🕠", "🕕", "🕡", "🕖", "🕢", "🕗", "🕣", "🕘", "🕤", "🕙", "🕥", "🕚", "🕦"}, new String[]{"🌺", "🌸", "💮", "🏵️", "🌼", "🌿"}, new String[]{"🐢", "✨", "🌟", "👑"}};
    private static final String S_EGG_UNLOCK_SETTING = "egg_mode_s";
    private static final String TAG = "PlatLogoActivity";
    static final String TOUCH_STATS = "touch.stats";
    private BubblesDrawable mBg;
    private SettableAnalogClock mClock;
    private ImageView mLogo;
    double mPressureMin = 0.0d;
    double mPressureMax = -1.0d;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(0);
        getWindow().setStatusBarColor(0);
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
        FrameLayout layout = new FrameLayout(this);
        this.mClock = new SettableAnalogClock(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dp = dm.density;
        int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        int widgetSize = (int) (minSide * 0.75d);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = 17;
        layout.addView(this.mClock, lp);
        ImageView imageView = new ImageView(this);
        this.mLogo = imageView;
        imageView.setVisibility(8);
        this.mLogo.setImageResource(C4057R.C4058drawable.platlogo);
        layout.addView(this.mLogo, lp);
        BubblesDrawable bubblesDrawable = new BubblesDrawable();
        this.mBg = bubblesDrawable;
        bubblesDrawable.setLevel(0);
        this.mBg.avoid = widgetSize / 2;
        this.mBg.padding = 0.5f * dp;
        this.mBg.minR = 1.0f * dp;
        layout.setBackground(this.mBg);
        layout.setOnLongClickListener(this.mBg);
        setContentView(layout);
    }

    private boolean shouldWriteSettings() {
        return getPackageName().equals("android");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchNextStage(boolean locked) {
        this.mClock.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).withEndAction(new Runnable() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PlatLogoActivity.this.lambda$launchNextStage$0();
            }
        }).start();
        this.mLogo.setAlpha(0.0f);
        this.mLogo.setScaleX(0.5f);
        this.mLogo.setScaleY(0.5f);
        this.mLogo.setVisibility(0);
        this.mLogo.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setInterpolator(new OvershootInterpolator()).start();
        this.mLogo.postDelayed(new Runnable() { // from class: com.android.internal.app.PlatLogoActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PlatLogoActivity.this.lambda$launchNextStage$1();
            }
        }, 500L);
        ContentResolver cr = getContentResolver();
        try {
            if (shouldWriteSettings()) {
                Log.m106v(TAG, "Saving egg unlock=" + locked);
                syncTouchPressure();
                Settings.System.putLong(cr, S_EGG_UNLOCK_SETTING, locked ? 0L : System.currentTimeMillis());
            }
        } catch (RuntimeException e) {
            Log.m109e(TAG, "Can't write settings", e);
        }
        try {
            startActivity(new Intent(Intent.ACTION_MAIN).setFlags(268468224).addCategory("com.android.internal.category.PLATLOGO"));
        } catch (ActivityNotFoundException e2) {
            Log.m110e("com.android.internal.app.PlatLogoActivity", "No more eggs.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchNextStage$0() {
        this.mClock.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchNextStage$1() {
        ObjectAnimator anim = ObjectAnimator.ofInt(this.mBg, "level", 0, 10000);
        anim.setInterpolator(new DecelerateInterpolator(1.0f));
        anim.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void measureTouchPressure(MotionEvent event) {
        float pressure = event.getPressure();
        switch (event.getActionMasked()) {
            case 0:
                if (this.mPressureMax < 0.0d) {
                    double d = pressure;
                    this.mPressureMax = d;
                    this.mPressureMin = d;
                    return;
                }
                return;
            case 1:
            default:
                return;
            case 2:
                if (pressure < this.mPressureMin) {
                    this.mPressureMin = pressure;
                }
                if (pressure > this.mPressureMax) {
                    this.mPressureMax = pressure;
                    return;
                }
                return;
        }
    }

    private void syncTouchPressure() {
        try {
            String touchDataJson = Settings.System.getString(getContentResolver(), TOUCH_STATS);
            JSONObject touchData = new JSONObject(touchDataJson != null ? touchDataJson : "{}");
            if (touchData.has("min")) {
                this.mPressureMin = Math.min(this.mPressureMin, touchData.getDouble("min"));
            }
            if (touchData.has("max")) {
                this.mPressureMax = Math.max(this.mPressureMax, touchData.getDouble("max"));
            }
            if (this.mPressureMax >= 0.0d) {
                touchData.put("min", this.mPressureMin);
                touchData.put("max", this.mPressureMax);
                if (shouldWriteSettings()) {
                    Settings.System.putString(getContentResolver(), TOUCH_STATS, touchData.toString());
                }
            }
        } catch (Exception e) {
            Log.m109e("com.android.internal.app.PlatLogoActivity", "Can't write touch settings", e);
        }
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        syncTouchPressure();
    }

    @Override // android.app.Activity
    public void onStop() {
        syncTouchPressure();
        super.onStop();
    }

    /* loaded from: classes4.dex */
    public class SettableAnalogClock extends AnalogClock {
        private boolean mOverride;
        private int mOverrideHour;
        private int mOverrideMinute;

        public SettableAnalogClock(Context context) {
            super(context);
            this.mOverrideHour = -1;
            this.mOverrideMinute = -1;
            this.mOverride = false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.AnalogClock
        public Instant now() {
            Instant realNow = super.now();
            ZoneId tz = Clock.systemDefaultZone().getZone();
            ZonedDateTime zdTime = realNow.atZone(tz);
            if (this.mOverride) {
                if (this.mOverrideHour < 0) {
                    this.mOverrideHour = zdTime.getHour();
                }
                return Clock.fixed(zdTime.withHour(this.mOverrideHour).withMinute(this.mOverrideMinute).withSecond(0).toInstant(), tz).instant();
            }
            return realNow;
        }

        double toPositiveDegrees(double rad) {
            return ((Math.toDegrees(rad) + 360.0d) - 90.0d) % 360.0d;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            int i;
            switch (ev.getActionMasked()) {
                case 0:
                    this.mOverride = true;
                    break;
                case 1:
                    if (this.mOverrideMinute == 0 && this.mOverrideHour % 12 == 1) {
                        Log.m106v(PlatLogoActivity.TAG, "13:00");
                        performHapticFeedback(0);
                        PlatLogoActivity.this.launchNextStage(false);
                    }
                    return true;
                case 2:
                    break;
                default:
                    return false;
            }
            PlatLogoActivity.this.measureTouchPressure(ev);
            float x = ev.getX();
            float y = ev.getY();
            float cx = getWidth() / 2.0f;
            float cy = getHeight() / 2.0f;
            float angle = (float) toPositiveDegrees(Math.atan2(x - cx, y - cy));
            int minutes = (75 - ((int) (angle / 6.0f))) % 60;
            int minuteDelta = minutes - this.mOverrideMinute;
            if (minuteDelta != 0) {
                if (Math.abs(minuteDelta) > 45 && (i = this.mOverrideHour) >= 0) {
                    int hourDelta = minuteDelta < 0 ? 1 : -1;
                    this.mOverrideHour = ((i + 24) + hourDelta) % 24;
                }
                this.mOverrideMinute = minutes;
                if (minutes == 0) {
                    performHapticFeedback(0);
                    if (getScaleX() == 1.0f) {
                        setScaleX(1.05f);
                        setScaleY(1.05f);
                        animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
                    }
                } else {
                    performHapticFeedback(4);
                }
                onTimeChanged();
                postInvalidate();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class Bubble {
        public int color;

        /* renamed from: r */
        public float f566r;
        public String text = null;

        /* renamed from: x */
        public float f567x;

        /* renamed from: y */
        public float f568y;

        Bubble() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class BubblesDrawable extends Drawable implements View.OnLongClickListener {
        private static final int MAX_BUBBS = 2000;
        private final int[] mColorIds;
        private int[] mColors;
        private int mNumBubbs;
        private int mEmojiSet = -1;
        private final Bubble[] mBubbs = new Bubble[2000];
        private final Paint mPaint = new Paint(1);
        public float avoid = 0.0f;
        public float padding = 0.0f;
        public float minR = 0.0f;

        BubblesDrawable() {
            int[] iArr = {17170519, 17170520, 17170521, 17170506, 17170507, 17170508};
            this.mColorIds = iArr;
            this.mColors = new int[iArr.length];
            int i = 0;
            while (true) {
                int[] iArr2 = this.mColorIds;
                if (i >= iArr2.length) {
                    break;
                }
                this.mColors[i] = PlatLogoActivity.this.getColor(iArr2[i]);
                i++;
            }
            int j = 0;
            while (true) {
                Bubble[] bubbleArr = this.mBubbs;
                if (j < bubbleArr.length) {
                    bubbleArr[j] = new Bubble();
                    j++;
                } else {
                    return;
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (getLevel() == 0) {
                return;
            }
            float f = getLevel() / 10000.0f;
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setTextAlign(Paint.Align.CENTER);
            int drawn = 0;
            for (int j = 0; j < this.mNumBubbs; j++) {
                if (this.mBubbs[j].color != 0 && this.mBubbs[j].f566r != 0.0f) {
                    if (this.mBubbs[j].text != null) {
                        this.mPaint.setTextSize(this.mBubbs[j].f566r * 1.75f);
                        canvas.drawText(this.mBubbs[j].text, this.mBubbs[j].f567x, this.mBubbs[j].f568y + (this.mBubbs[j].f566r * f * 0.6f), this.mPaint);
                    } else {
                        this.mPaint.setColor(this.mBubbs[j].color);
                        canvas.drawCircle(this.mBubbs[j].f567x, this.mBubbs[j].f568y, this.mBubbs[j].f566r * f, this.mPaint);
                    }
                    drawn++;
                }
            }
        }

        public void chooseEmojiSet() {
            this.mEmojiSet = (int) (Math.random() * PlatLogoActivity.EMOJI_SETS.length);
            String[] emojiSet = PlatLogoActivity.EMOJI_SETS[this.mEmojiSet];
            int j = 0;
            while (true) {
                Bubble[] bubbleArr = this.mBubbs;
                if (j < bubbleArr.length) {
                    bubbleArr[j].text = emojiSet[(int) (Math.random() * emojiSet.length)];
                    j++;
                } else {
                    invalidateSelf();
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.graphics.drawable.Drawable
        public boolean onLevelChange(int level) {
            invalidateSelf();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.graphics.drawable.Drawable
        public void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            randomize();
        }

        private void randomize() {
            float w = getBounds().width();
            float h = getBounds().height();
            float maxR = Math.min(w, h) / 3.0f;
            this.mNumBubbs = 0;
            if (this.avoid > 0.0f) {
                this.mBubbs[0].f567x = w / 2.0f;
                this.mBubbs[this.mNumBubbs].f568y = h / 2.0f;
                this.mBubbs[this.mNumBubbs].f566r = this.avoid;
                this.mBubbs[this.mNumBubbs].color = 0;
                this.mNumBubbs++;
            }
            for (int j = 0; j < 2000; j++) {
                int tries = 5;
                while (true) {
                    int tries2 = tries - 1;
                    if (tries > 0) {
                        float x = ((float) Math.random()) * w;
                        float y = ((float) Math.random()) * h;
                        float r = Math.min(Math.min(x, w - x), Math.min(y, h - y));
                        for (int i = 0; i < this.mNumBubbs; i++) {
                            r = (float) Math.min(r, (Math.hypot(x - this.mBubbs[i].f567x, y - this.mBubbs[i].f568y) - this.mBubbs[i].f566r) - this.padding);
                            if (r < this.minR) {
                                break;
                            }
                        }
                        if (r >= this.minR) {
                            float r2 = Math.min(maxR, r);
                            this.mBubbs[this.mNumBubbs].f567x = x;
                            this.mBubbs[this.mNumBubbs].f568y = y;
                            this.mBubbs[this.mNumBubbs].f566r = r2;
                            this.mBubbs[this.mNumBubbs].color = this.mColors[(int) (Math.random() * this.mColors.length)];
                            this.mNumBubbs++;
                            break;
                        }
                        tries = tries2;
                    }
                }
            }
            int j2 = this.mNumBubbs;
            Log.m106v(PlatLogoActivity.TAG, String.format("successfully placed %d bubbles (%d%%)", Integer.valueOf(j2), Integer.valueOf((int) ((this.mNumBubbs * 100.0f) / 2000.0f))));
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (getLevel() == 0) {
                return false;
            }
            chooseEmojiSet();
            return true;
        }
    }
}
