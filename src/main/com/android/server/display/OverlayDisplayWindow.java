package com.android.server.display;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.p005os.IInstalld;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.internal.util.DumpUtils;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class OverlayDisplayWindow implements DumpUtils.Dump {
    public final Context mContext;
    public final Display mDefaultDisplay;
    public int mDensityDpi;
    public final DisplayManager mDisplayManager;
    public GestureDetector mGestureDetector;
    public final int mGravity;
    public int mHeight;
    public final Listener mListener;
    public float mLiveTranslationX;
    public float mLiveTranslationY;
    public final String mName;
    public ScaleGestureDetector mScaleGestureDetector;
    public final boolean mSecure;
    public TextureView mTextureView;
    public String mTitle;
    public TextView mTitleTextView;
    public int mWidth;
    public View mWindowContent;
    public final WindowManager mWindowManager;
    public WindowManager.LayoutParams mWindowParams;
    public float mWindowScale;
    public boolean mWindowVisible;
    public int mWindowX;
    public int mWindowY;
    public final float INITIAL_SCALE = 0.5f;
    public final float MIN_SCALE = 0.3f;
    public final float MAX_SCALE = 1.0f;
    public final float WINDOW_ALPHA = 0.8f;
    public final boolean DISABLE_MOVE_AND_RESIZE = false;
    public final DisplayInfo mDefaultDisplayInfo = new DisplayInfo();
    public float mLiveScale = 1.0f;
    public final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.server.display.OverlayDisplayWindow.1
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            if (i == OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                if (OverlayDisplayWindow.this.updateDefaultDisplayInfo()) {
                    OverlayDisplayWindow.this.relayout();
                    OverlayDisplayWindow.this.mListener.onStateChanged(OverlayDisplayWindow.this.mDefaultDisplayInfo.state);
                    return;
                }
                OverlayDisplayWindow.this.dismiss();
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            if (i == OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                OverlayDisplayWindow.this.dismiss();
            }
        }
    };
    public final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.server.display.OverlayDisplayWindow.2
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            OverlayDisplayWindow.this.mListener.onWindowCreated(surfaceTexture, OverlayDisplayWindow.this.mDefaultDisplayInfo.getRefreshRate(), OverlayDisplayWindow.this.mDefaultDisplayInfo.presentationDeadlineNanos, OverlayDisplayWindow.this.mDefaultDisplayInfo.state);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            OverlayDisplayWindow.this.mListener.onWindowDestroyed();
            return true;
        }
    };
    public final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() { // from class: com.android.server.display.OverlayDisplayWindow.3
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            motionEvent.setLocation(motionEvent.getRawX(), motionEvent.getRawY());
            OverlayDisplayWindow.this.mGestureDetector.onTouchEvent(motionEvent);
            OverlayDisplayWindow.this.mScaleGestureDetector.onTouchEvent(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 1 || actionMasked == 3) {
                OverlayDisplayWindow.this.saveWindowParams();
            }
            motionEvent.setLocation(x, y);
            return true;
        }
    };
    public final GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.android.server.display.OverlayDisplayWindow.4
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            OverlayDisplayWindow.this.mLiveTranslationX -= f;
            OverlayDisplayWindow.this.mLiveTranslationY -= f2;
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };
    public final ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.android.server.display.OverlayDisplayWindow.5
        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            OverlayDisplayWindow.this.mLiveScale *= scaleGestureDetector.getScaleFactor();
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };

    /* loaded from: classes.dex */
    public interface Listener {
        void onStateChanged(int i);

        void onWindowCreated(SurfaceTexture surfaceTexture, float f, long j, int i);

        void onWindowDestroyed();
    }

    public OverlayDisplayWindow(Context context, String str, int i, int i2, int i3, int i4, boolean z, Listener listener) {
        ThreadedRenderer.disableVsync();
        this.mContext = context;
        this.mName = str;
        this.mGravity = i4;
        this.mSecure = z;
        this.mListener = listener;
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mDefaultDisplay = context.getDisplay();
        updateDefaultDisplayInfo();
        resize(i, i2, i3, false);
        createWindow();
    }

    public void show() {
        if (this.mWindowVisible) {
            return;
        }
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
        if (!updateDefaultDisplayInfo()) {
            this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            return;
        }
        clearLiveState();
        updateWindowParams();
        this.mWindowManager.addView(this.mWindowContent, this.mWindowParams);
        this.mWindowVisible = true;
    }

    public void dismiss() {
        if (this.mWindowVisible) {
            this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            this.mWindowManager.removeView(this.mWindowContent);
            this.mWindowVisible = false;
        }
    }

    public void resize(int i, int i2, int i3) {
        resize(i, i2, i3, true);
    }

    public final void resize(int i, int i2, int i3, boolean z) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mDensityDpi = i3;
        this.mTitle = this.mContext.getResources().getString(17040137, this.mName, Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mDensityDpi));
        if (this.mSecure) {
            this.mTitle += this.mContext.getResources().getString(17040136);
        }
        if (z) {
            relayout();
        }
    }

    public void relayout() {
        if (this.mWindowVisible) {
            updateWindowParams();
            this.mWindowManager.updateViewLayout(this.mWindowContent, this.mWindowParams);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println("mWindowVisible=" + this.mWindowVisible);
        printWriter.println("mWindowX=" + this.mWindowX);
        printWriter.println("mWindowY=" + this.mWindowY);
        printWriter.println("mWindowScale=" + this.mWindowScale);
        printWriter.println("mWindowParams=" + this.mWindowParams);
        if (this.mTextureView != null) {
            printWriter.println("mTextureView.getScaleX()=" + this.mTextureView.getScaleX());
            printWriter.println("mTextureView.getScaleY()=" + this.mTextureView.getScaleY());
        }
        printWriter.println("mLiveTranslationX=" + this.mLiveTranslationX);
        printWriter.println("mLiveTranslationY=" + this.mLiveTranslationY);
        printWriter.println("mLiveScale=" + this.mLiveScale);
    }

    public final boolean updateDefaultDisplayInfo() {
        if (this.mDefaultDisplay.getDisplayInfo(this.mDefaultDisplayInfo)) {
            return true;
        }
        Slog.w("OverlayDisplayWindow", "Cannot show overlay display because there is no default display upon which to show it.");
        return false;
    }

    public final void createWindow() {
        View inflate = LayoutInflater.from(this.mContext).inflate(17367251, (ViewGroup) null);
        this.mWindowContent = inflate;
        inflate.setOnTouchListener(this.mOnTouchListener);
        TextureView textureView = (TextureView) this.mWindowContent.findViewById(16909335);
        this.mTextureView = textureView;
        textureView.setPivotX(0.0f);
        this.mTextureView.setPivotY(0.0f);
        this.mTextureView.getLayoutParams().width = this.mWidth;
        this.mTextureView.getLayoutParams().height = this.mHeight;
        this.mTextureView.setOpaque(false);
        this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        TextView textView = (TextView) this.mWindowContent.findViewById(16909336);
        this.mTitleTextView = textView;
        textView.setText(this.mTitle);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(2026);
        this.mWindowParams = layoutParams;
        int i = layoutParams.flags | 16778024;
        layoutParams.flags = i;
        if (this.mSecure) {
            layoutParams.flags = i | IInstalld.FLAG_FORCE;
        }
        layoutParams.privateFlags |= 2;
        layoutParams.alpha = 0.8f;
        layoutParams.gravity = 51;
        layoutParams.setTitle(this.mTitle);
        this.mGestureDetector = new GestureDetector(this.mContext, this.mOnGestureListener);
        this.mScaleGestureDetector = new ScaleGestureDetector(this.mContext, this.mOnScaleGestureListener);
        int i2 = this.mGravity;
        this.mWindowX = (i2 & 3) == 3 ? 0 : this.mDefaultDisplayInfo.logicalWidth;
        this.mWindowY = (i2 & 48) != 48 ? this.mDefaultDisplayInfo.logicalHeight : 0;
        this.mWindowScale = 0.5f;
    }

    public final void updateWindowParams() {
        float max = Math.max(0.3f, Math.min(1.0f, Math.min(Math.min(this.mWindowScale * this.mLiveScale, this.mDefaultDisplayInfo.logicalWidth / this.mWidth), this.mDefaultDisplayInfo.logicalHeight / this.mHeight)));
        float f = ((max / this.mWindowScale) - 1.0f) * 0.5f;
        int i = (int) (this.mWidth * max);
        int i2 = (int) (this.mHeight * max);
        int i3 = (int) ((this.mWindowY + this.mLiveTranslationY) - (i2 * f));
        int max2 = Math.max(0, Math.min((int) ((this.mWindowX + this.mLiveTranslationX) - (i * f)), this.mDefaultDisplayInfo.logicalWidth - i));
        int max3 = Math.max(0, Math.min(i3, this.mDefaultDisplayInfo.logicalHeight - i2));
        this.mTextureView.setScaleX(max);
        this.mTextureView.setScaleY(max);
        WindowManager.LayoutParams layoutParams = this.mWindowParams;
        layoutParams.x = max2;
        layoutParams.y = max3;
        layoutParams.width = i;
        layoutParams.height = i2;
    }

    public final void saveWindowParams() {
        WindowManager.LayoutParams layoutParams = this.mWindowParams;
        this.mWindowX = layoutParams.x;
        this.mWindowY = layoutParams.y;
        this.mWindowScale = this.mTextureView.getScaleX();
        clearLiveState();
    }

    public final void clearLiveState() {
        this.mLiveTranslationX = 0.0f;
        this.mLiveTranslationY = 0.0f;
        this.mLiveScale = 1.0f;
    }
}
