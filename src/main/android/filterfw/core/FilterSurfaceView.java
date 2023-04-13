package android.filterfw.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/* loaded from: classes.dex */
public class FilterSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static int STATE_ALLOCATED = 0;
    private static int STATE_CREATED = 1;
    private static int STATE_INITIALIZED = 2;
    private int mFormat;
    private GLEnvironment mGLEnv;
    private int mHeight;
    private SurfaceHolder.Callback mListener;
    private int mState;
    private int mSurfaceId;
    private int mWidth;

    public FilterSurfaceView(Context context) {
        super(context);
        this.mState = STATE_ALLOCATED;
        this.mSurfaceId = -1;
        getHolder().addCallback(this);
    }

    public FilterSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = STATE_ALLOCATED;
        this.mSurfaceId = -1;
        getHolder().addCallback(this);
    }

    public synchronized void bindToListener(SurfaceHolder.Callback listener, GLEnvironment glEnv) {
        if (listener == null) {
            throw new NullPointerException("Attempting to bind null filter to SurfaceView!");
        }
        SurfaceHolder.Callback callback = this.mListener;
        if (callback != null && callback != listener) {
            throw new RuntimeException("Attempting to bind filter " + listener + " to SurfaceView with another open filter " + this.mListener + " attached already!");
        }
        this.mListener = listener;
        GLEnvironment gLEnvironment = this.mGLEnv;
        if (gLEnvironment != null && gLEnvironment != glEnv) {
            gLEnvironment.unregisterSurfaceId(this.mSurfaceId);
        }
        this.mGLEnv = glEnv;
        if (this.mState >= STATE_CREATED) {
            registerSurface();
            this.mListener.surfaceCreated(getHolder());
            if (this.mState == STATE_INITIALIZED) {
                this.mListener.surfaceChanged(getHolder(), this.mFormat, this.mWidth, this.mHeight);
            }
        }
    }

    public synchronized void unbind() {
        this.mListener = null;
    }

    public synchronized int getSurfaceId() {
        return this.mSurfaceId;
    }

    public synchronized GLEnvironment getGLEnv() {
        return this.mGLEnv;
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceCreated(SurfaceHolder holder) {
        this.mState = STATE_CREATED;
        if (this.mGLEnv != null) {
            registerSurface();
        }
        SurfaceHolder.Callback callback = this.mListener;
        if (callback != null) {
            callback.surfaceCreated(holder);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mFormat = format;
        this.mWidth = width;
        this.mHeight = height;
        this.mState = STATE_INITIALIZED;
        SurfaceHolder.Callback callback = this.mListener;
        if (callback != null) {
            callback.surfaceChanged(holder, format, width, height);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        this.mState = STATE_ALLOCATED;
        SurfaceHolder.Callback callback = this.mListener;
        if (callback != null) {
            callback.surfaceDestroyed(holder);
        }
        unregisterSurface();
    }

    private void registerSurface() {
        int registerSurface = this.mGLEnv.registerSurface(getHolder().getSurface());
        this.mSurfaceId = registerSurface;
        if (registerSurface < 0) {
            throw new RuntimeException("Could not register Surface: " + getHolder().getSurface() + " in FilterSurfaceView!");
        }
    }

    private void unregisterSurface() {
        int i;
        GLEnvironment gLEnvironment = this.mGLEnv;
        if (gLEnvironment != null && (i = this.mSurfaceId) > 0) {
            gLEnvironment.unregisterSurfaceId(i);
        }
    }
}
