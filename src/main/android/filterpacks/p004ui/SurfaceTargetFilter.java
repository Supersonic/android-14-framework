package android.filterpacks.p004ui;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.util.Log;
import android.view.Surface;
/* renamed from: android.filterpacks.ui.SurfaceTargetFilter */
/* loaded from: classes.dex */
public class SurfaceTargetFilter extends Filter {
    private static final String TAG = "SurfaceRenderFilter";
    private final int RENDERMODE_FILL_CROP;
    private final int RENDERMODE_FIT;
    private final int RENDERMODE_STRETCH;
    private float mAspectRatio;
    private GLEnvironment mGlEnv;
    private boolean mLogVerbose;
    private ShaderProgram mProgram;
    private int mRenderMode;
    @GenerateFieldPort(hasDefault = true, name = "renderMode")
    private String mRenderModeString;
    private GLFrame mScreen;
    @GenerateFieldPort(name = "oheight")
    private int mScreenHeight;
    @GenerateFieldPort(name = "owidth")
    private int mScreenWidth;
    @GenerateFinalPort(name = "surface")
    private Surface mSurface;
    private int mSurfaceId;

    public SurfaceTargetFilter(String name) {
        super(name);
        this.RENDERMODE_STRETCH = 0;
        this.RENDERMODE_FIT = 1;
        this.RENDERMODE_FILL_CROP = 2;
        this.mRenderMode = 1;
        this.mAspectRatio = 1.0f;
        this.mSurfaceId = -1;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        if (this.mSurface == null) {
            throw new RuntimeException("NULL Surface passed to SurfaceTargetFilter");
        }
        addMaskedInputPort("frame", ImageFormat.create(3));
    }

    public void updateRenderMode() {
        String str = this.mRenderModeString;
        if (str != null) {
            if (str.equals("stretch")) {
                this.mRenderMode = 0;
            } else if (this.mRenderModeString.equals("fit")) {
                this.mRenderMode = 1;
            } else if (this.mRenderModeString.equals("fill_crop")) {
                this.mRenderMode = 2;
            } else {
                throw new RuntimeException("Unknown render mode '" + this.mRenderModeString + "'!");
            }
        }
        updateTargetRect();
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        this.mGlEnv = context.getGLEnvironment();
        ShaderProgram createIdentity = ShaderProgram.createIdentity(context);
        this.mProgram = createIdentity;
        createIdentity.setSourceRect(0.0f, 1.0f, 1.0f, -1.0f);
        this.mProgram.setClearsOutput(true);
        this.mProgram.setClearColor(0.0f, 0.0f, 0.0f);
        MutableFrameFormat screenFormat = ImageFormat.create(this.mScreenWidth, this.mScreenHeight, 3, 3);
        this.mScreen = (GLFrame) context.getFrameManager().newBoundFrame(screenFormat, 101, 0L);
        updateRenderMode();
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        registerSurface();
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame gpuFrame;
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Starting frame processing");
        }
        Frame input = pullInput("frame");
        boolean createdFrame = false;
        float currentAspectRatio = input.getFormat().getWidth() / input.getFormat().getHeight();
        if (currentAspectRatio != this.mAspectRatio) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "New aspect ratio: " + currentAspectRatio + ", previously: " + this.mAspectRatio);
            }
            this.mAspectRatio = currentAspectRatio;
            updateTargetRect();
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Got input format: " + input.getFormat());
        }
        int target = input.getFormat().getTarget();
        if (target != 3) {
            gpuFrame = context.getFrameManager().duplicateFrameToTarget(input, 3);
            createdFrame = true;
        } else {
            gpuFrame = input;
        }
        this.mGlEnv.activateSurfaceWithId(this.mSurfaceId);
        this.mProgram.process(gpuFrame, this.mScreen);
        this.mGlEnv.swapBuffers();
        if (createdFrame) {
            gpuFrame.release();
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        this.mScreen.setViewport(0, 0, this.mScreenWidth, this.mScreenHeight);
        updateTargetRect();
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        unregisterSurface();
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        GLFrame gLFrame = this.mScreen;
        if (gLFrame != null) {
            gLFrame.release();
        }
    }

    private void updateTargetRect() {
        int i;
        ShaderProgram shaderProgram;
        int i2 = this.mScreenWidth;
        if (i2 > 0 && (i = this.mScreenHeight) > 0 && (shaderProgram = this.mProgram) != null) {
            float screenAspectRatio = i2 / i;
            float relativeAspectRatio = screenAspectRatio / this.mAspectRatio;
            switch (this.mRenderMode) {
                case 0:
                    shaderProgram.setTargetRect(0.0f, 0.0f, 1.0f, 1.0f);
                    return;
                case 1:
                    if (relativeAspectRatio > 1.0f) {
                        shaderProgram.setTargetRect(0.5f - (0.5f / relativeAspectRatio), 0.0f, 1.0f / relativeAspectRatio, 1.0f);
                        return;
                    } else {
                        shaderProgram.setTargetRect(0.0f, 0.5f - (relativeAspectRatio * 0.5f), 1.0f, relativeAspectRatio);
                        return;
                    }
                case 2:
                    if (relativeAspectRatio > 1.0f) {
                        shaderProgram.setTargetRect(0.0f, 0.5f - (relativeAspectRatio * 0.5f), 1.0f, relativeAspectRatio);
                        return;
                    } else {
                        shaderProgram.setTargetRect(0.5f - (0.5f / relativeAspectRatio), 0.0f, 1.0f / relativeAspectRatio, 1.0f);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    private void registerSurface() {
        int registerSurface = this.mGlEnv.registerSurface(this.mSurface);
        this.mSurfaceId = registerSurface;
        if (registerSurface < 0) {
            throw new RuntimeException("Could not register Surface: " + this.mSurface);
        }
    }

    private void unregisterSurface() {
        int i = this.mSurfaceId;
        if (i > 0) {
            this.mGlEnv.unregisterSurfaceId(i);
        }
    }
}
