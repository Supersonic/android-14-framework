package com.android.server.display;

import android.content.Context;
import android.graphics.BLASTBufferQueue;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManagerInternal;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import android.window.ScreenCapture;
import com.android.internal.policy.TransitionAnimation;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import libcore.io.Streams;
/* loaded from: classes.dex */
public final class ColorFade {
    public BLASTBufferQueue mBLASTBufferQueue;
    public SurfaceControl mBLASTSurfaceControl;
    public boolean mCreatedResources;
    public int mDisplayHeight;
    public final int mDisplayId;
    public int mDisplayLayerStack;
    public int mDisplayWidth;
    public EGLConfig mEglConfig;
    public EGLContext mEglContext;
    public EGLDisplay mEglDisplay;
    public EGLSurface mEglSurface;
    public int mGammaLoc;
    public boolean mLastWasProtectedContent;
    public boolean mLastWasWideColor;
    public int mMode;
    public int mOpacityLoc;
    public boolean mPrepared;
    public int mProgram;
    public int mProjMatrixLoc;
    public Surface mSurface;
    public float mSurfaceAlpha;
    public SurfaceControl mSurfaceControl;
    public NaturalSurfaceLayout mSurfaceLayout;
    public boolean mSurfaceVisible;
    public int mTexCoordLoc;
    public int mTexMatrixLoc;
    public boolean mTexNamesGenerated;
    public int mTexUnitLoc;
    public int mVertexLoc;
    public final int[] mTexNames = new int[1];
    public final float[] mTexMatrix = new float[16];
    public final float[] mProjMatrix = new float[16];
    public final int[] mGLBuffers = new int[2];
    public final FloatBuffer mVertexBuffer = createNativeFloatBuffer(8);
    public final FloatBuffer mTexCoordBuffer = createNativeFloatBuffer(8);
    public final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
    public final DisplayManagerInternal mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);

    public ColorFade(int i) {
        this.mDisplayId = i;
    }

    public boolean prepare(Context context, int i) {
        this.mMode = i;
        DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
        if (displayInfo == null) {
            return false;
        }
        this.mDisplayLayerStack = displayInfo.layerStack;
        this.mDisplayWidth = displayInfo.getNaturalWidth();
        this.mDisplayHeight = displayInfo.getNaturalHeight();
        boolean z = displayInfo.colorMode == 9;
        this.mPrepared = true;
        ScreenCapture.ScreenshotHardwareBuffer captureScreen = captureScreen();
        if (captureScreen == null) {
            dismiss();
            return false;
        }
        boolean hasProtectedContent = TransitionAnimation.hasProtectedContent(captureScreen.getHardwareBuffer());
        if (!createSurfaceControl(captureScreen.containsSecureLayers())) {
            dismiss();
            return false;
        } else if (this.mMode == 2) {
            return true;
        } else {
            if (!createEglContext(hasProtectedContent) || !createEglSurface(hasProtectedContent, z) || !setScreenshotTextureAndSetViewport(captureScreen)) {
                dismiss();
                return false;
            } else if (attachEglContext()) {
                try {
                    if (!initGLShaders(context) || !initGLBuffers() || checkGlErrors("prepare")) {
                        detachEglContext();
                        dismiss();
                        return false;
                    }
                    detachEglContext();
                    this.mCreatedResources = true;
                    this.mLastWasProtectedContent = hasProtectedContent;
                    this.mLastWasWideColor = z;
                    if (i == 1) {
                        for (int i2 = 0; i2 < 3; i2++) {
                            draw(1.0f);
                        }
                    }
                    return true;
                } finally {
                    detachEglContext();
                }
            } else {
                return false;
            }
        }
    }

    public final String readFile(Context context, int i) {
        try {
            return new String(Streams.readFully(new InputStreamReader(context.getResources().openRawResource(i))));
        } catch (IOException e) {
            Slog.e("ColorFade", "Unrecognized shader " + Integer.toString(i));
            throw new RuntimeException(e);
        }
    }

    public final int loadShader(Context context, int i, int i2) {
        String readFile = readFile(context, i);
        int glCreateShader = GLES20.glCreateShader(i2);
        GLES20.glShaderSource(glCreateShader, readFile);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] == 0) {
            Slog.e("ColorFade", "Could not compile shader " + glCreateShader + ", " + i2 + XmlUtils.STRING_ARRAY_SEPARATOR);
            Slog.e("ColorFade", GLES20.glGetShaderSource(glCreateShader));
            Slog.e("ColorFade", GLES20.glGetShaderInfoLog(glCreateShader));
            GLES20.glDeleteShader(glCreateShader);
            return 0;
        }
        return glCreateShader;
    }

    public final boolean initGLShaders(Context context) {
        int loadShader = loadShader(context, 17825795, 35633);
        int loadShader2 = loadShader(context, 17825794, 35632);
        GLES20.glReleaseShaderCompiler();
        if (loadShader == 0 || loadShader2 == 0) {
            return false;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        this.mProgram = glCreateProgram;
        GLES20.glAttachShader(glCreateProgram, loadShader);
        GLES20.glAttachShader(this.mProgram, loadShader2);
        GLES20.glDeleteShader(loadShader);
        GLES20.glDeleteShader(loadShader2);
        GLES20.glLinkProgram(this.mProgram);
        this.mVertexLoc = GLES20.glGetAttribLocation(this.mProgram, "position");
        this.mTexCoordLoc = GLES20.glGetAttribLocation(this.mProgram, "uv");
        this.mProjMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "proj_matrix");
        this.mTexMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "tex_matrix");
        this.mOpacityLoc = GLES20.glGetUniformLocation(this.mProgram, "opacity");
        this.mGammaLoc = GLES20.glGetUniformLocation(this.mProgram, "gamma");
        this.mTexUnitLoc = GLES20.glGetUniformLocation(this.mProgram, "texUnit");
        GLES20.glUseProgram(this.mProgram);
        GLES20.glUniform1i(this.mTexUnitLoc, 0);
        GLES20.glUseProgram(0);
        return true;
    }

    public final void destroyGLShaders() {
        GLES20.glDeleteProgram(this.mProgram);
        checkGlErrors("glDeleteProgram");
    }

    public final boolean initGLBuffers() {
        setQuad(this.mVertexBuffer, 0.0f, 0.0f, this.mDisplayWidth, this.mDisplayHeight);
        GLES20.glBindTexture(36197, this.mTexNames[0]);
        GLES20.glTexParameteri(36197, 10240, 9728);
        GLES20.glTexParameteri(36197, 10241, 9728);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        GLES20.glBindTexture(36197, 0);
        GLES20.glGenBuffers(2, this.mGLBuffers, 0);
        GLES20.glBindBuffer(34962, this.mGLBuffers[0]);
        GLES20.glBufferData(34962, this.mVertexBuffer.capacity() * 4, this.mVertexBuffer, 35044);
        GLES20.glBindBuffer(34962, this.mGLBuffers[1]);
        GLES20.glBufferData(34962, this.mTexCoordBuffer.capacity() * 4, this.mTexCoordBuffer, 35044);
        GLES20.glBindBuffer(34962, 0);
        return true;
    }

    public final void destroyGLBuffers() {
        GLES20.glDeleteBuffers(2, this.mGLBuffers, 0);
        checkGlErrors("glDeleteBuffers");
    }

    public static void setQuad(FloatBuffer floatBuffer, float f, float f2, float f3, float f4) {
        floatBuffer.put(0, f);
        floatBuffer.put(1, f2);
        floatBuffer.put(2, f);
        float f5 = f4 + f2;
        floatBuffer.put(3, f5);
        float f6 = f + f3;
        floatBuffer.put(4, f6);
        floatBuffer.put(5, f5);
        floatBuffer.put(6, f6);
        floatBuffer.put(7, f2);
    }

    public void dismissResources() {
        if (this.mCreatedResources) {
            attachEglContext();
            try {
                destroyScreenshotTexture();
                destroyGLShaders();
                destroyGLBuffers();
                destroyEglSurface();
                detachEglContext();
                GLES20.glFlush();
                this.mCreatedResources = false;
            } catch (Throwable th) {
                detachEglContext();
                throw th;
            }
        }
    }

    public void dismiss() {
        if (this.mPrepared) {
            dismissResources();
            destroySurface();
            this.mPrepared = false;
        }
    }

    public boolean draw(float f) {
        if (this.mPrepared) {
            if (this.mMode == 2) {
                return showSurface(1.0f - f);
            }
            if (attachEglContext()) {
                try {
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GLES20.glClear(16384);
                    double d = 1.0f - f;
                    double cos = Math.cos(3.141592653589793d * d);
                    drawFaded(((float) (-Math.pow(d, 2.0d))) + 1.0f, 1.0f / ((float) ((((((cos < 0.0d ? -1.0d : 1.0d) * 0.5d) * Math.pow(cos, 2.0d)) + 0.5d) * 0.9d) + 0.1d)));
                    if (checkGlErrors("drawFrame")) {
                        return false;
                    }
                    EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
                    detachEglContext();
                    return showSurface(1.0f);
                } finally {
                    detachEglContext();
                }
            }
            return false;
        }
        return false;
    }

    public final void drawFaded(float f, float f2) {
        GLES20.glUseProgram(this.mProgram);
        GLES20.glUniformMatrix4fv(this.mProjMatrixLoc, 1, false, this.mProjMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mTexMatrixLoc, 1, false, this.mTexMatrix, 0);
        GLES20.glUniform1f(this.mOpacityLoc, f);
        GLES20.glUniform1f(this.mGammaLoc, f2);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.mTexNames[0]);
        GLES20.glBindBuffer(34962, this.mGLBuffers[0]);
        GLES20.glEnableVertexAttribArray(this.mVertexLoc);
        GLES20.glVertexAttribPointer(this.mVertexLoc, 2, 5126, false, 0, 0);
        GLES20.glBindBuffer(34962, this.mGLBuffers[1]);
        GLES20.glEnableVertexAttribArray(this.mTexCoordLoc);
        GLES20.glVertexAttribPointer(this.mTexCoordLoc, 2, 5126, false, 0, 0);
        GLES20.glDrawArrays(6, 0, 4);
        GLES20.glBindTexture(36197, 0);
        GLES20.glBindBuffer(34962, 0);
    }

    public final void ortho(float f, float f2, float f3, float f4, float f5, float f6) {
        float[] fArr = this.mProjMatrix;
        float f7 = f2 - f;
        fArr[0] = 2.0f / f7;
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 0.0f;
        float f8 = f4 - f3;
        fArr[5] = 2.0f / f8;
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = 0.0f;
        fArr[9] = 0.0f;
        float f9 = f6 - f5;
        fArr[10] = (-2.0f) / f9;
        fArr[11] = 0.0f;
        fArr[12] = (-(f2 + f)) / f7;
        fArr[13] = (-(f4 + f3)) / f8;
        fArr[14] = (-(f6 + f5)) / f9;
        fArr[15] = 1.0f;
    }

    public final boolean setScreenshotTextureAndSetViewport(ScreenCapture.ScreenshotHardwareBuffer screenshotHardwareBuffer) {
        if (attachEglContext()) {
            try {
                if (!this.mTexNamesGenerated) {
                    GLES20.glGenTextures(1, this.mTexNames, 0);
                    if (checkGlErrors("glGenTextures")) {
                        return false;
                    }
                    this.mTexNamesGenerated = true;
                }
                SurfaceTexture surfaceTexture = new SurfaceTexture(this.mTexNames[0]);
                Surface surface = new Surface(surfaceTexture);
                surface.attachAndQueueBufferWithColorSpace(screenshotHardwareBuffer.getHardwareBuffer(), screenshotHardwareBuffer.getColorSpace());
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(this.mTexMatrix);
                surface.release();
                surfaceTexture.release();
                this.mTexCoordBuffer.put(0, 0.0f);
                this.mTexCoordBuffer.put(1, 0.0f);
                this.mTexCoordBuffer.put(2, 0.0f);
                this.mTexCoordBuffer.put(3, 1.0f);
                this.mTexCoordBuffer.put(4, 1.0f);
                this.mTexCoordBuffer.put(5, 1.0f);
                this.mTexCoordBuffer.put(6, 1.0f);
                this.mTexCoordBuffer.put(7, 0.0f);
                GLES20.glViewport(0, 0, this.mDisplayWidth, this.mDisplayHeight);
                ortho(0.0f, this.mDisplayWidth, 0.0f, this.mDisplayHeight, -1.0f, 1.0f);
                return true;
            } finally {
                detachEglContext();
            }
        }
        return false;
    }

    public final void destroyScreenshotTexture() {
        if (this.mTexNamesGenerated) {
            this.mTexNamesGenerated = false;
            GLES20.glDeleteTextures(1, this.mTexNames, 0);
            checkGlErrors("glDeleteTextures");
        }
    }

    public final ScreenCapture.ScreenshotHardwareBuffer captureScreen() {
        ScreenCapture.ScreenshotHardwareBuffer systemScreenshot = this.mDisplayManagerInternal.systemScreenshot(this.mDisplayId);
        if (systemScreenshot == null) {
            Slog.e("ColorFade", "Failed to take screenshot. Buffer is null");
            return null;
        }
        return systemScreenshot;
    }

    public final boolean createSurfaceControl(boolean z) {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            this.mTransaction.setSecure(surfaceControl, z).apply();
            return true;
        }
        try {
            SurfaceControl.Builder callsite = new SurfaceControl.Builder().setName("ColorFade").setSecure(z).setCallsite("ColorFade.createSurface");
            if (this.mMode == 2) {
                callsite.setColorLayer();
            } else {
                callsite.setContainerLayer();
            }
            SurfaceControl build = callsite.build();
            this.mSurfaceControl = build;
            this.mTransaction.setLayerStack(build, this.mDisplayLayerStack);
            this.mTransaction.setWindowCrop(this.mSurfaceControl, this.mDisplayWidth, this.mDisplayHeight);
            NaturalSurfaceLayout naturalSurfaceLayout = new NaturalSurfaceLayout(this.mDisplayManagerInternal, this.mDisplayId, this.mSurfaceControl);
            this.mSurfaceLayout = naturalSurfaceLayout;
            naturalSurfaceLayout.onDisplayTransaction(this.mTransaction);
            this.mTransaction.apply();
            if (this.mMode != 2) {
                this.mBLASTSurfaceControl = new SurfaceControl.Builder().setName("ColorFade BLAST").setParent(this.mSurfaceControl).setHidden(false).setSecure(z).setBLASTLayer().build();
                BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("ColorFade", this.mBLASTSurfaceControl, this.mDisplayWidth, this.mDisplayHeight, -3);
                this.mBLASTBufferQueue = bLASTBufferQueue;
                this.mSurface = bLASTBufferQueue.createSurface();
            }
            return true;
        } catch (Surface.OutOfResourcesException e) {
            Slog.e("ColorFade", "Unable to create surface.", e);
            return false;
        }
    }

    public final boolean createEglContext(boolean z) {
        if (this.mEglDisplay == null) {
            EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
            this.mEglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL14.EGL_NO_DISPLAY) {
                logEglError("eglGetDisplay");
                return false;
            }
            int[] iArr = new int[2];
            if (!EGL14.eglInitialize(eglGetDisplay, iArr, 0, iArr, 1)) {
                this.mEglDisplay = null;
                logEglError("eglInitialize");
                return false;
            }
        }
        if (this.mEglConfig == null) {
            int[] iArr2 = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!EGL14.eglChooseConfig(this.mEglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344}, 0, eGLConfigArr, 0, 1, iArr2, 0)) {
                logEglError("eglChooseConfig");
                return false;
            } else if (iArr2[0] <= 0) {
                Slog.e("ColorFade", "no valid config found");
                return false;
            } else {
                this.mEglConfig = eGLConfigArr[0];
            }
        }
        EGLContext eGLContext = this.mEglContext;
        if (eGLContext != null && z != this.mLastWasProtectedContent) {
            EGL14.eglDestroyContext(this.mEglDisplay, eGLContext);
            this.mEglContext = null;
        }
        if (this.mEglContext == null) {
            int[] iArr3 = {12440, 2, 12344, 12344, 12344};
            if (z) {
                iArr3[2] = 12992;
                iArr3[3] = 1;
            }
            EGLContext eglCreateContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, iArr3, 0);
            this.mEglContext = eglCreateContext;
            if (eglCreateContext == null) {
                logEglError("eglCreateContext");
                return false;
            }
        }
        return true;
    }

    public final boolean createEglSurface(boolean z, boolean z2) {
        int i;
        boolean z3 = (z == this.mLastWasProtectedContent && z2 == this.mLastWasWideColor) ? false : true;
        EGLSurface eGLSurface = this.mEglSurface;
        if (eGLSurface != null && z3) {
            EGL14.eglDestroySurface(this.mEglDisplay, eGLSurface);
            this.mEglSurface = null;
        }
        if (this.mEglSurface == null) {
            int[] iArr = {12344, 12344, 12344, 12344, 12344};
            if (z2) {
                iArr[0] = 12445;
                iArr[1] = 13456;
                i = 2;
            } else {
                i = 0;
            }
            if (z) {
                iArr[i] = 12992;
                iArr[i + 1] = 1;
            }
            EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, this.mSurface, iArr, 0);
            this.mEglSurface = eglCreateWindowSurface;
            if (eglCreateWindowSurface == null) {
                logEglError("eglCreateWindowSurface");
                return false;
            }
        }
        return true;
    }

    public final void destroyEglSurface() {
        EGLSurface eGLSurface = this.mEglSurface;
        if (eGLSurface != null) {
            if (!EGL14.eglDestroySurface(this.mEglDisplay, eGLSurface)) {
                logEglError("eglDestroySurface");
            }
            this.mEglSurface = null;
        }
    }

    public final void destroySurface() {
        if (this.mSurfaceControl != null) {
            this.mSurfaceLayout.dispose();
            this.mSurfaceLayout = null;
            this.mTransaction.remove(this.mSurfaceControl).apply();
            Surface surface = this.mSurface;
            if (surface != null) {
                surface.release();
                this.mSurface = null;
            }
            SurfaceControl surfaceControl = this.mBLASTSurfaceControl;
            if (surfaceControl != null) {
                surfaceControl.release();
                this.mBLASTSurfaceControl = null;
                this.mBLASTBufferQueue.destroy();
                this.mBLASTBufferQueue = null;
            }
            this.mSurfaceControl = null;
            this.mSurfaceVisible = false;
            this.mSurfaceAlpha = 0.0f;
        }
    }

    public final boolean showSurface(float f) {
        if (!this.mSurfaceVisible || this.mSurfaceAlpha != f) {
            this.mTransaction.setLayer(this.mSurfaceControl, 1073741825).setAlpha(this.mSurfaceControl, f).show(this.mSurfaceControl).apply();
            this.mSurfaceVisible = true;
            this.mSurfaceAlpha = f;
        }
        return true;
    }

    public final boolean attachEglContext() {
        EGLSurface eGLSurface = this.mEglSurface;
        if (eGLSurface == null) {
            return false;
        }
        if (EGL14.eglMakeCurrent(this.mEglDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
            return true;
        }
        logEglError("eglMakeCurrent");
        return false;
    }

    public final void detachEglContext() {
        EGLDisplay eGLDisplay = this.mEglDisplay;
        if (eGLDisplay != null) {
            EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
            EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT);
        }
    }

    public static FloatBuffer createNativeFloatBuffer(int i) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(i * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        return allocateDirect.asFloatBuffer();
    }

    public static void logEglError(String str) {
        Slog.e("ColorFade", str + " failed: error " + EGL14.eglGetError(), new Throwable());
    }

    public static boolean checkGlErrors(String str) {
        return checkGlErrors(str, true);
    }

    public static boolean checkGlErrors(String str, boolean z) {
        boolean z2 = false;
        while (true) {
            int glGetError = GLES20.glGetError();
            if (glGetError == 0) {
                return z2;
            }
            if (z) {
                Slog.e("ColorFade", str + " failed: error " + glGetError, new Throwable());
            }
            z2 = true;
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Color Fade State:");
        printWriter.println("  mPrepared=" + this.mPrepared);
        printWriter.println("  mMode=" + this.mMode);
        printWriter.println("  mDisplayLayerStack=" + this.mDisplayLayerStack);
        printWriter.println("  mDisplayWidth=" + this.mDisplayWidth);
        printWriter.println("  mDisplayHeight=" + this.mDisplayHeight);
        printWriter.println("  mSurfaceVisible=" + this.mSurfaceVisible);
        printWriter.println("  mSurfaceAlpha=" + this.mSurfaceAlpha);
    }

    /* loaded from: classes.dex */
    public static final class NaturalSurfaceLayout implements DisplayManagerInternal.DisplayTransactionListener {
        public final int mDisplayId;
        public final DisplayManagerInternal mDisplayManagerInternal;
        public SurfaceControl mSurfaceControl;

        public NaturalSurfaceLayout(DisplayManagerInternal displayManagerInternal, int i, SurfaceControl surfaceControl) {
            this.mDisplayManagerInternal = displayManagerInternal;
            this.mDisplayId = i;
            this.mSurfaceControl = surfaceControl;
            displayManagerInternal.registerDisplayTransactionListener(this);
        }

        public void dispose() {
            synchronized (this) {
                this.mSurfaceControl = null;
            }
            this.mDisplayManagerInternal.unregisterDisplayTransactionListener(this);
        }

        public void onDisplayTransaction(SurfaceControl.Transaction transaction) {
            synchronized (this) {
                if (this.mSurfaceControl == null) {
                    return;
                }
                DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
                if (displayInfo == null) {
                    return;
                }
                int i = displayInfo.rotation;
                if (i == 0) {
                    transaction.setPosition(this.mSurfaceControl, 0.0f, 0.0f);
                    transaction.setMatrix(this.mSurfaceControl, 1.0f, 0.0f, 0.0f, 1.0f);
                } else if (i == 1) {
                    transaction.setPosition(this.mSurfaceControl, 0.0f, displayInfo.logicalHeight);
                    transaction.setMatrix(this.mSurfaceControl, 0.0f, -1.0f, 1.0f, 0.0f);
                } else if (i == 2) {
                    transaction.setPosition(this.mSurfaceControl, displayInfo.logicalWidth, displayInfo.logicalHeight);
                    transaction.setMatrix(this.mSurfaceControl, -1.0f, 0.0f, 0.0f, -1.0f);
                } else if (i == 3) {
                    transaction.setPosition(this.mSurfaceControl, displayInfo.logicalWidth, 0.0f);
                    transaction.setMatrix(this.mSurfaceControl, 0.0f, 1.0f, -1.0f, 0.0f);
                }
            }
        }
    }
}
