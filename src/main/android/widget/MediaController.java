package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.window.OnBackInvokedCallback;
import com.android.internal.C4057R;
import com.android.internal.policy.PhoneWindow;
import java.util.Formatter;
import java.util.Locale;
/* loaded from: classes4.dex */
public class MediaController extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private final AccessibilityManager mAccessibilityManager;
    private View mAnchor;
    private final View.OnAttachStateChangeListener mAttachStateListener;
    private final OnBackInvokedCallback mBackCallback;
    private boolean mBackCallbackRegistered;
    private final Context mContext;
    private TextView mCurrentTime;
    private View mDecor;
    private WindowManager.LayoutParams mDecorLayoutParams;
    private boolean mDragging;
    private TextView mEndTime;
    private final Runnable mFadeOut;
    private ImageButton mFfwdButton;
    private final View.OnClickListener mFfwdListener;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private boolean mFromXml;
    private final View.OnLayoutChangeListener mLayoutChangeListener;
    private boolean mListenersSet;
    private ImageButton mNextButton;
    private View.OnClickListener mNextListener;
    private ImageButton mPauseButton;
    private CharSequence mPauseDescription;
    private final View.OnClickListener mPauseListener;
    private CharSequence mPlayDescription;
    private MediaPlayerControl mPlayer;
    private ImageButton mPrevButton;
    private View.OnClickListener mPrevListener;
    private ProgressBar mProgress;
    private ImageButton mRewButton;
    private final View.OnClickListener mRewListener;
    private View mRoot;
    private final SeekBar.OnSeekBarChangeListener mSeekListener;
    private final Runnable mShowProgress;
    private boolean mShowing;
    private final View.OnTouchListener mTouchListener;
    private final boolean mUseFastForward;
    private Window mWindow;
    private WindowManager mWindowManager;

    /* loaded from: classes4.dex */
    public interface MediaPlayerControl {
        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();

        int getAudioSessionId();

        int getBufferPercentage();

        int getCurrentPosition();

        int getDuration();

        boolean isPlaying();

        void pause();

        void seekTo(int i);

        void start();
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBackCallback = new OnBackInvokedCallback() { // from class: android.widget.MediaController$$ExternalSyntheticLambda0
            @Override // android.window.OnBackInvokedCallback
            public final void onBackInvoked() {
                MediaController.this.hide();
            }
        };
        this.mAttachStateListener = new View.OnAttachStateChangeListener() { // from class: android.widget.MediaController.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                MediaController.this.registerOnBackInvokedCallback();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                MediaController.this.unregisterOnBackInvokedCallback();
            }
        };
        this.mLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: android.widget.MediaController.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                MediaController.this.updateFloatingWindowLayout();
                if (MediaController.this.mShowing) {
                    MediaController.this.mWindowManager.updateViewLayout(MediaController.this.mDecor, MediaController.this.mDecorLayoutParams);
                }
            }
        };
        this.mTouchListener = new View.OnTouchListener() { // from class: android.widget.MediaController.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0 && MediaController.this.mShowing) {
                    MediaController.this.hide();
                    return false;
                }
                return false;
            }
        };
        this.mFadeOut = new Runnable() { // from class: android.widget.MediaController.4
            @Override // java.lang.Runnable
            public void run() {
                MediaController.this.hide();
            }
        };
        this.mShowProgress = new Runnable() { // from class: android.widget.MediaController.5
            @Override // java.lang.Runnable
            public void run() {
                int pos = MediaController.this.setProgress();
                if (!MediaController.this.mDragging && MediaController.this.mShowing && MediaController.this.mPlayer.isPlaying()) {
                    MediaController mediaController = MediaController.this;
                    mediaController.postDelayed(mediaController.mShowProgress, 1000 - (pos % 1000));
                }
            }
        };
        this.mPauseListener = new View.OnClickListener() { // from class: android.widget.MediaController.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MediaController.this.doPauseResume();
                MediaController.this.show(3000);
            }
        };
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: android.widget.MediaController.7
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar bar) {
                MediaController.this.show(3600000);
                MediaController.this.mDragging = true;
                MediaController mediaController = MediaController.this;
                mediaController.removeCallbacks(mediaController.mShowProgress);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
                if (!fromuser) {
                    return;
                }
                long duration = MediaController.this.mPlayer.getDuration();
                long newposition = (progress * duration) / 1000;
                MediaController.this.mPlayer.seekTo((int) newposition);
                if (MediaController.this.mCurrentTime != null) {
                    MediaController.this.mCurrentTime.setText(MediaController.this.stringForTime((int) newposition));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar bar) {
                MediaController.this.mDragging = false;
                MediaController.this.setProgress();
                MediaController.this.updatePausePlay();
                MediaController.this.show(3000);
                MediaController mediaController = MediaController.this;
                mediaController.post(mediaController.mShowProgress);
            }
        };
        this.mRewListener = new View.OnClickListener() { // from class: android.widget.MediaController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int pos = MediaController.this.mPlayer.getCurrentPosition();
                MediaController.this.mPlayer.seekTo(pos - 5000);
                MediaController.this.setProgress();
                MediaController.this.show(3000);
            }
        };
        this.mFfwdListener = new View.OnClickListener() { // from class: android.widget.MediaController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int pos = MediaController.this.mPlayer.getCurrentPosition();
                MediaController.this.mPlayer.seekTo(pos + 15000);
                MediaController.this.setProgress();
                MediaController.this.show(3000);
            }
        };
        this.mRoot = this;
        this.mContext = context;
        this.mUseFastForward = true;
        this.mFromXml = true;
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        View view = this.mRoot;
        if (view != null) {
            initControllerView(view);
        }
    }

    public MediaController(Context context, boolean useFastForward) {
        super(context);
        this.mBackCallback = new OnBackInvokedCallback() { // from class: android.widget.MediaController$$ExternalSyntheticLambda0
            @Override // android.window.OnBackInvokedCallback
            public final void onBackInvoked() {
                MediaController.this.hide();
            }
        };
        this.mAttachStateListener = new View.OnAttachStateChangeListener() { // from class: android.widget.MediaController.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                MediaController.this.registerOnBackInvokedCallback();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                MediaController.this.unregisterOnBackInvokedCallback();
            }
        };
        this.mLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: android.widget.MediaController.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                MediaController.this.updateFloatingWindowLayout();
                if (MediaController.this.mShowing) {
                    MediaController.this.mWindowManager.updateViewLayout(MediaController.this.mDecor, MediaController.this.mDecorLayoutParams);
                }
            }
        };
        this.mTouchListener = new View.OnTouchListener() { // from class: android.widget.MediaController.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0 && MediaController.this.mShowing) {
                    MediaController.this.hide();
                    return false;
                }
                return false;
            }
        };
        this.mFadeOut = new Runnable() { // from class: android.widget.MediaController.4
            @Override // java.lang.Runnable
            public void run() {
                MediaController.this.hide();
            }
        };
        this.mShowProgress = new Runnable() { // from class: android.widget.MediaController.5
            @Override // java.lang.Runnable
            public void run() {
                int pos = MediaController.this.setProgress();
                if (!MediaController.this.mDragging && MediaController.this.mShowing && MediaController.this.mPlayer.isPlaying()) {
                    MediaController mediaController = MediaController.this;
                    mediaController.postDelayed(mediaController.mShowProgress, 1000 - (pos % 1000));
                }
            }
        };
        this.mPauseListener = new View.OnClickListener() { // from class: android.widget.MediaController.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MediaController.this.doPauseResume();
                MediaController.this.show(3000);
            }
        };
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: android.widget.MediaController.7
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar bar) {
                MediaController.this.show(3600000);
                MediaController.this.mDragging = true;
                MediaController mediaController = MediaController.this;
                mediaController.removeCallbacks(mediaController.mShowProgress);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
                if (!fromuser) {
                    return;
                }
                long duration = MediaController.this.mPlayer.getDuration();
                long newposition = (progress * duration) / 1000;
                MediaController.this.mPlayer.seekTo((int) newposition);
                if (MediaController.this.mCurrentTime != null) {
                    MediaController.this.mCurrentTime.setText(MediaController.this.stringForTime((int) newposition));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar bar) {
                MediaController.this.mDragging = false;
                MediaController.this.setProgress();
                MediaController.this.updatePausePlay();
                MediaController.this.show(3000);
                MediaController mediaController = MediaController.this;
                mediaController.post(mediaController.mShowProgress);
            }
        };
        this.mRewListener = new View.OnClickListener() { // from class: android.widget.MediaController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int pos = MediaController.this.mPlayer.getCurrentPosition();
                MediaController.this.mPlayer.seekTo(pos - 5000);
                MediaController.this.setProgress();
                MediaController.this.show(3000);
            }
        };
        this.mFfwdListener = new View.OnClickListener() { // from class: android.widget.MediaController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int pos = MediaController.this.mPlayer.getCurrentPosition();
                MediaController.this.mPlayer.seekTo(pos + 15000);
                MediaController.this.setProgress();
                MediaController.this.show(3000);
            }
        };
        this.mContext = context;
        this.mUseFastForward = useFastForward;
        initFloatingWindowLayout();
        initFloatingWindow();
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
    }

    public MediaController(Context context) {
        this(context, true);
    }

    private void initFloatingWindow() {
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        PhoneWindow phoneWindow = new PhoneWindow(this.mContext);
        this.mWindow = phoneWindow;
        phoneWindow.setWindowManager(this.mWindowManager, null, null);
        this.mWindow.requestFeature(1);
        View decorView = this.mWindow.getDecorView();
        this.mDecor = decorView;
        decorView.setOnTouchListener(this.mTouchListener);
        this.mDecor.addOnAttachStateChangeListener(this.mAttachStateListener);
        this.mWindow.setContentView(this);
        this.mWindow.setBackgroundDrawableResource(17170445);
        this.mWindow.setVolumeControlStream(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(262144);
        requestFocus();
    }

    private void initFloatingWindowLayout() {
        this.mDecorLayoutParams = new WindowManager.LayoutParams();
        WindowManager.LayoutParams p = this.mDecorLayoutParams;
        p.gravity = 51;
        p.height = -2;
        p.f504x = 0;
        p.format = -3;
        p.type = 1000;
        p.flags |= 8519712;
        p.token = null;
        p.windowAnimations = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFloatingWindowLayout() {
        int[] anchorPos = new int[2];
        this.mAnchor.getLocationOnScreen(anchorPos);
        this.mDecor.measure(View.MeasureSpec.makeMeasureSpec(this.mAnchor.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(this.mAnchor.getHeight(), Integer.MIN_VALUE));
        WindowManager.LayoutParams p = this.mDecorLayoutParams;
        p.width = this.mAnchor.getWidth();
        p.f504x = anchorPos[0] + ((this.mAnchor.getWidth() - p.width) / 2);
        p.f505y = (anchorPos[1] + this.mAnchor.getHeight()) - this.mDecor.getMeasuredHeight();
        p.token = this.mAnchor.getWindowToken();
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        this.mPlayer = player;
        updatePausePlay();
    }

    public void setAnchorView(View view) {
        View view2 = this.mAnchor;
        if (view2 != null) {
            view2.removeOnLayoutChangeListener(this.mLayoutChangeListener);
        }
        this.mAnchor = view;
        if (view != null) {
            view.addOnLayoutChangeListener(this.mLayoutChangeListener);
        }
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(-1, -1);
        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate2 = inflate.inflate(C4057R.layout.media_controller, (ViewGroup) null);
        this.mRoot = inflate2;
        initControllerView(inflate2);
        return this.mRoot;
    }

    private void initControllerView(View v) {
        Resources res = this.mContext.getResources();
        this.mPlayDescription = res.getText(C4057R.string.lockscreen_transport_play_description);
        this.mPauseDescription = res.getText(C4057R.string.lockscreen_transport_pause_description);
        ImageButton imageButton = (ImageButton) v.findViewById(C4057R.C4059id.pause);
        this.mPauseButton = imageButton;
        if (imageButton != null) {
            imageButton.requestFocus();
            this.mPauseButton.setOnClickListener(this.mPauseListener);
        }
        ImageButton imageButton2 = (ImageButton) v.findViewById(C4057R.C4059id.ffwd);
        this.mFfwdButton = imageButton2;
        if (imageButton2 != null) {
            imageButton2.setOnClickListener(this.mFfwdListener);
            if (!this.mFromXml) {
                this.mFfwdButton.setVisibility(this.mUseFastForward ? 0 : 8);
            }
        }
        ImageButton imageButton3 = (ImageButton) v.findViewById(C4057R.C4059id.rew);
        this.mRewButton = imageButton3;
        if (imageButton3 != null) {
            imageButton3.setOnClickListener(this.mRewListener);
            if (!this.mFromXml) {
                this.mRewButton.setVisibility(this.mUseFastForward ? 0 : 8);
            }
        }
        ImageButton imageButton4 = (ImageButton) v.findViewById(C4057R.C4059id.next);
        this.mNextButton = imageButton4;
        if (imageButton4 != null && !this.mFromXml && !this.mListenersSet) {
            imageButton4.setVisibility(8);
        }
        ImageButton imageButton5 = (ImageButton) v.findViewById(C4057R.C4059id.prev);
        this.mPrevButton = imageButton5;
        if (imageButton5 != null && !this.mFromXml && !this.mListenersSet) {
            imageButton5.setVisibility(8);
        }
        ProgressBar progressBar = (ProgressBar) v.findViewById(C4057R.C4059id.mediacontroller_progress);
        this.mProgress = progressBar;
        if (progressBar != null) {
            if (progressBar instanceof SeekBar) {
                SeekBar seeker = (SeekBar) progressBar;
                seeker.setOnSeekBarChangeListener(this.mSeekListener);
            }
            this.mProgress.setMax(1000);
        }
        this.mEndTime = (TextView) v.findViewById(C4057R.C4059id.time);
        this.mCurrentTime = (TextView) v.findViewById(C4057R.C4059id.time_current);
        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(this.mFormatBuilder, Locale.getDefault());
        installPrevNextListeners();
    }

    public void show() {
        show(3000);
    }

    private void disableUnsupportedButtons() {
        try {
            if (this.mPauseButton != null && !this.mPlayer.canPause()) {
                this.mPauseButton.setEnabled(false);
            }
            if (this.mRewButton != null && !this.mPlayer.canSeekBackward()) {
                this.mRewButton.setEnabled(false);
            }
            if (this.mFfwdButton != null && !this.mPlayer.canSeekForward()) {
                this.mFfwdButton.setEnabled(false);
            }
            if (this.mProgress != null && !this.mPlayer.canSeekBackward() && !this.mPlayer.canSeekForward()) {
                this.mProgress.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError e) {
        }
    }

    public void show(int timeout) {
        if (!this.mShowing && this.mAnchor != null) {
            setProgress();
            ImageButton imageButton = this.mPauseButton;
            if (imageButton != null) {
                imageButton.requestFocus();
            }
            disableUnsupportedButtons();
            updateFloatingWindowLayout();
            this.mWindowManager.addView(this.mDecor, this.mDecorLayoutParams);
            this.mShowing = true;
        }
        updatePausePlay();
        post(this.mShowProgress);
        if (timeout != 0 && !this.mAccessibilityManager.isTouchExplorationEnabled()) {
            removeCallbacks(this.mFadeOut);
            postDelayed(this.mFadeOut, timeout);
        }
        registerOnBackInvokedCallback();
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public void hide() {
        if (this.mAnchor != null && this.mShowing) {
            try {
                removeCallbacks(this.mShowProgress);
                this.mWindowManager.removeView(this.mDecor);
            } catch (IllegalArgumentException e) {
                Log.m104w("MediaController", "already removed");
            }
            this.mShowing = false;
            unregisterOnBackInvokedCallback();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        this.mFormatBuilder.setLength(0);
        if (hours > 0) {
            return this.mFormatter.format("%d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)).toString();
        }
        return this.mFormatter.format("%02d:%02d", Integer.valueOf(minutes), Integer.valueOf(seconds)).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int setProgress() {
        MediaPlayerControl mediaPlayerControl = this.mPlayer;
        if (mediaPlayerControl == null || this.mDragging) {
            return 0;
        }
        int position = mediaPlayerControl.getCurrentPosition();
        int duration = this.mPlayer.getDuration();
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            if (duration > 0) {
                long pos = (position * 1000) / duration;
                progressBar.setProgress((int) pos);
            }
            int percent = this.mPlayer.getBufferPercentage();
            this.mProgress.setSecondaryProgress(percent * 10);
        }
        TextView textView = this.mEndTime;
        if (textView != null) {
            textView.setText(stringForTime(duration));
        }
        TextView textView2 = this.mCurrentTime;
        if (textView2 != null) {
            textView2.setText(stringForTime(position));
        }
        return position;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                show(0);
                return true;
            case 1:
                show(3000);
                return true;
            case 2:
            default:
                return true;
            case 3:
                hide();
                return true;
        }
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent ev) {
        show(3000);
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == 0;
        if (keyCode == 79 || keyCode == 85 || keyCode == 62) {
            if (uniqueDown) {
                doPauseResume();
                show(3000);
                ImageButton imageButton = this.mPauseButton;
                if (imageButton != null) {
                    imageButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == 126) {
            if (uniqueDown && !this.mPlayer.isPlaying()) {
                this.mPlayer.start();
                updatePausePlay();
                show(3000);
            }
            return true;
        } else if (keyCode == 86 || keyCode == 127) {
            if (uniqueDown && this.mPlayer.isPlaying()) {
                this.mPlayer.pause();
                updatePausePlay();
                show(3000);
            }
            return true;
        } else if (keyCode == 25 || keyCode == 24 || keyCode == 164 || keyCode == 27) {
            return super.dispatchKeyEvent(event);
        } else {
            if (keyCode == 4 || keyCode == 82) {
                if (uniqueDown) {
                    hide();
                }
                return true;
            }
            show(3000);
            return super.dispatchKeyEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePausePlay() {
        if (this.mRoot == null || this.mPauseButton == null) {
            return;
        }
        if (this.mPlayer.isPlaying()) {
            this.mPauseButton.setImageResource(17301539);
            this.mPauseButton.setContentDescription(this.mPauseDescription);
            return;
        }
        this.mPauseButton.setImageResource(17301540);
        this.mPauseButton.setContentDescription(this.mPlayDescription);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPauseResume() {
        if (this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        } else {
            this.mPlayer.start();
        }
        updatePausePlay();
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        ImageButton imageButton = this.mPauseButton;
        if (imageButton != null) {
            imageButton.setEnabled(enabled);
        }
        ImageButton imageButton2 = this.mFfwdButton;
        if (imageButton2 != null) {
            imageButton2.setEnabled(enabled);
        }
        ImageButton imageButton3 = this.mRewButton;
        if (imageButton3 != null) {
            imageButton3.setEnabled(enabled);
        }
        ImageButton imageButton4 = this.mNextButton;
        boolean z = true;
        if (imageButton4 != null) {
            imageButton4.setEnabled(enabled && this.mNextListener != null);
        }
        ImageButton imageButton5 = this.mPrevButton;
        if (imageButton5 != null) {
            if (!enabled || this.mPrevListener == null) {
                z = false;
            }
            imageButton5.setEnabled(z);
        }
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return MediaController.class.getName();
    }

    private void installPrevNextListeners() {
        ImageButton imageButton = this.mNextButton;
        if (imageButton != null) {
            imageButton.setOnClickListener(this.mNextListener);
            this.mNextButton.setEnabled(this.mNextListener != null);
        }
        ImageButton imageButton2 = this.mPrevButton;
        if (imageButton2 != null) {
            imageButton2.setOnClickListener(this.mPrevListener);
            this.mPrevButton.setEnabled(this.mPrevListener != null);
        }
    }

    public void setPrevNextListeners(View.OnClickListener next, View.OnClickListener prev) {
        this.mNextListener = next;
        this.mPrevListener = prev;
        this.mListenersSet = true;
        if (this.mRoot != null) {
            installPrevNextListeners();
            ImageButton imageButton = this.mNextButton;
            if (imageButton != null && !this.mFromXml) {
                imageButton.setVisibility(0);
            }
            ImageButton imageButton2 = this.mPrevButton;
            if (imageButton2 != null && !this.mFromXml) {
                imageButton2.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterOnBackInvokedCallback() {
        if (!this.mBackCallbackRegistered) {
            return;
        }
        ViewRootImpl viewRootImpl = this.mDecor.getViewRootImpl();
        if (viewRootImpl != null && viewRootImpl.getOnBackInvokedDispatcher().isOnBackInvokedCallbackEnabled()) {
            viewRootImpl.getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(this.mBackCallback);
        }
        this.mBackCallbackRegistered = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerOnBackInvokedCallback() {
        ViewRootImpl viewRootImpl;
        if (!this.mBackCallbackRegistered && (viewRootImpl = this.mDecor.getViewRootImpl()) != null && viewRootImpl.getOnBackInvokedDispatcher().isOnBackInvokedCallbackEnabled()) {
            viewRootImpl.getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, this.mBackCallback);
            this.mBackCallbackRegistered = true;
        }
    }
}
