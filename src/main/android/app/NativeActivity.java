package android.app;

import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.Looper;
import android.p008os.MessageQueue;
import android.util.AttributeSet;
import android.view.InputQueue;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import dalvik.system.BaseDexClassLoader;
import java.io.File;
/* loaded from: classes.dex */
public class NativeActivity extends Activity implements SurfaceHolder.Callback2, InputQueue.Callback, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String KEY_NATIVE_SAVED_STATE = "android:native_state";
    public static final String META_DATA_FUNC_NAME = "android.app.func_name";
    public static final String META_DATA_LIB_NAME = "android.app.lib_name";
    private InputQueue mCurInputQueue;
    private SurfaceHolder mCurSurfaceHolder;
    private boolean mDestroyed;
    private boolean mDispatchingUnhandledKey;
    private InputMethodManager mIMM;
    int mLastContentHeight;
    int mLastContentWidth;
    int mLastContentX;
    int mLastContentY;
    final int[] mLocation = new int[2];
    private NativeContentView mNativeContentView;
    private long mNativeHandle;

    private native String getDlError();

    private native long loadNativeCode(String str, String str2, MessageQueue messageQueue, String str3, String str4, String str5, int i, AssetManager assetManager, byte[] bArr, ClassLoader classLoader, String str6);

    private native void onConfigurationChangedNative(long j);

    private native void onContentRectChangedNative(long j, int i, int i2, int i3, int i4);

    private native void onInputQueueCreatedNative(long j, long j2);

    private native void onInputQueueDestroyedNative(long j, long j2);

    private native void onLowMemoryNative(long j);

    private native void onPauseNative(long j);

    private native void onResumeNative(long j);

    private native byte[] onSaveInstanceStateNative(long j);

    private native void onStartNative(long j);

    private native void onStopNative(long j);

    private native void onSurfaceChangedNative(long j, Surface surface, int i, int i2, int i3);

    private native void onSurfaceCreatedNative(long j, Surface surface);

    private native void onSurfaceDestroyedNative(long j);

    private native void onSurfaceRedrawNeededNative(long j, Surface surface);

    private native void onWindowFocusChangedNative(long j, boolean z);

    private native void unloadNativeCode(long j);

    /* loaded from: classes.dex */
    static class NativeContentView extends View {
        NativeActivity mActivity;

        public NativeContentView(Context context) {
            super(context);
        }

        public NativeContentView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        String libname;
        String funcname;
        byte[] nativeSavedState;
        this.mIMM = (InputMethodManager) getSystemService(InputMethodManager.class);
        getWindow().takeSurface(this);
        getWindow().takeInputQueue(this);
        getWindow().setFormat(4);
        getWindow().setSoftInputMode(16);
        NativeContentView nativeContentView = new NativeContentView(this);
        this.mNativeContentView = nativeContentView;
        nativeContentView.mActivity = this;
        setContentView(this.mNativeContentView);
        this.mNativeContentView.requestFocus();
        this.mNativeContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getIntent().getComponent(), 128);
            if (ai.metaData == null) {
                libname = "main";
                funcname = "ANativeActivity_onCreate";
            } else {
                String ln = ai.metaData.getString(META_DATA_LIB_NAME);
                String libname2 = ln != null ? ln : "main";
                String ln2 = ai.metaData.getString(META_DATA_FUNC_NAME);
                String funcname2 = ln2 != null ? ln2 : "ANativeActivity_onCreate";
                libname = libname2;
                funcname = funcname2;
            }
            BaseDexClassLoader classLoader = (BaseDexClassLoader) getClassLoader();
            String path = classLoader.findLibrary(libname);
            if (path == null) {
                throw new IllegalArgumentException("Unable to find native library " + libname + " using classloader: " + classLoader.toString());
            }
            if (savedInstanceState == null) {
                nativeSavedState = null;
            } else {
                nativeSavedState = savedInstanceState.getByteArray(KEY_NATIVE_SAVED_STATE);
            }
            long loadNativeCode = loadNativeCode(path, funcname, Looper.myQueue(), getAbsolutePath(getFilesDir()), getAbsolutePath(getObbDir()), getAbsolutePath(getExternalFilesDir(null)), Build.VERSION.SDK_INT, getAssets(), nativeSavedState, classLoader, classLoader.getLdLibraryPath());
            this.mNativeHandle = loadNativeCode;
            if (loadNativeCode == 0) {
                throw new UnsatisfiedLinkError("Unable to load native library \"" + path + "\": " + getDlError());
            }
            super.onCreate(savedInstanceState);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Error getting activity info", e);
        }
    }

    private static String getAbsolutePath(File file) {
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        this.mDestroyed = true;
        if (this.mCurSurfaceHolder != null) {
            onSurfaceDestroyedNative(this.mNativeHandle);
            this.mCurSurfaceHolder = null;
        }
        InputQueue inputQueue = this.mCurInputQueue;
        if (inputQueue != null) {
            onInputQueueDestroyedNative(this.mNativeHandle, inputQueue.getNativePtr());
            this.mCurInputQueue = null;
        }
        unloadNativeCode(this.mNativeHandle);
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        onPauseNative(this.mNativeHandle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        onResumeNative(this.mNativeHandle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        byte[] state = onSaveInstanceStateNative(this.mNativeHandle);
        if (state != null) {
            outState.putByteArray(KEY_NATIVE_SAVED_STATE, state);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        onStartNative(this.mNativeHandle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        onStopNative(this.mNativeHandle);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.mDestroyed) {
            onConfigurationChangedNative(this.mNativeHandle);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        if (!this.mDestroyed) {
            onLowMemoryNative(this.mNativeHandle);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!this.mDestroyed) {
            onWindowFocusChangedNative(this.mNativeHandle, hasFocus);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceCreatedNative(this.mNativeHandle, holder.getSurface());
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceChangedNative(this.mNativeHandle, holder.getSurface(), format, width, height);
        }
    }

    @Override // android.view.SurfaceHolder.Callback2
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceRedrawNeededNative(this.mNativeHandle, holder.getSurface());
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mCurSurfaceHolder = null;
        if (!this.mDestroyed) {
            onSurfaceDestroyedNative(this.mNativeHandle);
        }
    }

    @Override // android.view.InputQueue.Callback
    public void onInputQueueCreated(InputQueue queue) {
        if (!this.mDestroyed) {
            this.mCurInputQueue = queue;
            onInputQueueCreatedNative(this.mNativeHandle, queue.getNativePtr());
        }
    }

    @Override // android.view.InputQueue.Callback
    public void onInputQueueDestroyed(InputQueue queue) {
        if (!this.mDestroyed) {
            onInputQueueDestroyedNative(this.mNativeHandle, queue.getNativePtr());
            this.mCurInputQueue = null;
        }
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        this.mNativeContentView.getLocationInWindow(this.mLocation);
        int w = this.mNativeContentView.getWidth();
        int h = this.mNativeContentView.getHeight();
        int[] iArr = this.mLocation;
        int i = iArr[0];
        if (i != this.mLastContentX || iArr[1] != this.mLastContentY || w != this.mLastContentWidth || h != this.mLastContentHeight) {
            this.mLastContentX = i;
            int i2 = iArr[1];
            this.mLastContentY = i2;
            this.mLastContentWidth = w;
            this.mLastContentHeight = h;
            if (!this.mDestroyed) {
                onContentRectChangedNative(this.mNativeHandle, i, i2, w, h);
            }
        }
    }

    void setWindowFlags(int flags, int mask) {
        getWindow().setFlags(flags, mask);
    }

    void setWindowFormat(int format) {
        getWindow().setFormat(format);
    }

    void showIme(int mode) {
        this.mIMM.showSoftInput(this.mNativeContentView, mode);
    }

    void hideIme(int mode) {
        this.mIMM.hideSoftInputFromWindow(this.mNativeContentView.getWindowToken(), mode);
    }
}
