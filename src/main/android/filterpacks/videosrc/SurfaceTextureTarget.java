package android.filterpacks.videosrc;

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
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.graphics.SurfaceTexture;
import android.media.MediaMetrics;
import android.util.Log;
/* loaded from: classes.dex */
public class SurfaceTextureTarget extends Filter {
    private static final String TAG = "SurfaceTextureTarget";
    private final int RENDERMODE_CUSTOMIZE;
    private final int RENDERMODE_FILL_CROP;
    private final int RENDERMODE_FIT;
    private final int RENDERMODE_STRETCH;
    private float mAspectRatio;
    private boolean mLogVerbose;
    private ShaderProgram mProgram;
    private int mRenderMode;
    @GenerateFieldPort(hasDefault = true, name = "renderMode")
    private String mRenderModeString;
    private GLFrame mScreen;
    @GenerateFinalPort(name = "height")
    private int mScreenHeight;
    @GenerateFinalPort(name = "width")
    private int mScreenWidth;
    @GenerateFieldPort(hasDefault = true, name = "sourceQuad")
    private Quad mSourceQuad;
    private int mSurfaceId;
    @GenerateFinalPort(name = "surfaceTexture")
    private SurfaceTexture mSurfaceTexture;
    @GenerateFieldPort(hasDefault = true, name = "targetQuad")
    private Quad mTargetQuad;

    public SurfaceTextureTarget(String name) {
        super(name);
        this.RENDERMODE_STRETCH = 0;
        this.RENDERMODE_FIT = 1;
        this.RENDERMODE_FILL_CROP = 2;
        this.RENDERMODE_CUSTOMIZE = 3;
        this.mSourceQuad = new Quad(new Point(0.0f, 1.0f), new Point(1.0f, 1.0f), new Point(0.0f, 0.0f), new Point(1.0f, 0.0f));
        this.mTargetQuad = new Quad(new Point(0.0f, 0.0f), new Point(1.0f, 0.0f), new Point(0.0f, 1.0f), new Point(1.0f, 1.0f));
        this.mRenderMode = 1;
        this.mAspectRatio = 1.0f;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.Filter
    public synchronized void setupPorts() {
        if (this.mSurfaceTexture == null) {
            throw new RuntimeException("Null SurfaceTexture passed to SurfaceTextureTarget");
        }
        addMaskedInputPort("frame", ImageFormat.create(3));
    }

    public void updateRenderMode() {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "updateRenderMode. Thread: " + Thread.currentThread());
        }
        String str = this.mRenderModeString;
        if (str != null) {
            if (str.equals("stretch")) {
                this.mRenderMode = 0;
            } else if (this.mRenderModeString.equals("fit")) {
                this.mRenderMode = 1;
            } else if (this.mRenderModeString.equals("fill_crop")) {
                this.mRenderMode = 2;
            } else if (this.mRenderModeString.equals("customize")) {
                this.mRenderMode = 3;
            } else {
                throw new RuntimeException("Unknown render mode '" + this.mRenderModeString + "'!");
            }
        }
        updateTargetRect();
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Prepare. Thread: " + Thread.currentThread());
        }
        ShaderProgram createIdentity = ShaderProgram.createIdentity(context);
        this.mProgram = createIdentity;
        createIdentity.setSourceRect(0.0f, 1.0f, 1.0f, -1.0f);
        this.mProgram.setClearColor(0.0f, 0.0f, 0.0f);
        updateRenderMode();
        MutableFrameFormat screenFormat = new MutableFrameFormat(2, 3);
        screenFormat.setBytesPerSample(4);
        screenFormat.setDimensions(this.mScreenWidth, this.mScreenHeight);
        this.mScreen = (GLFrame) context.getFrameManager().newBoundFrame(screenFormat, 101, 0L);
    }

    @Override // android.filterfw.core.Filter
    public synchronized void open(FilterContext context) {
        if (this.mSurfaceTexture == null) {
            Log.m110e(TAG, "SurfaceTexture is null!!");
            throw new RuntimeException("Could not register SurfaceTexture: " + this.mSurfaceTexture);
        }
        int registerSurfaceTexture = context.getGLEnvironment().registerSurfaceTexture(this.mSurfaceTexture, this.mScreenWidth, this.mScreenHeight);
        this.mSurfaceId = registerSurfaceTexture;
        if (registerSurfaceTexture <= 0) {
            throw new RuntimeException("Could not register SurfaceTexture: " + this.mSurfaceTexture);
        }
    }

    @Override // android.filterfw.core.Filter
    public synchronized void close(FilterContext context) {
        if (this.mSurfaceId > 0) {
            context.getGLEnvironment().unregisterSurfaceId(this.mSurfaceId);
            this.mSurfaceId = -1;
        }
    }

    public synchronized void disconnect(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, MediaMetrics.Value.DISCONNECT);
        }
        if (this.mSurfaceTexture == null) {
            Log.m112d(TAG, "SurfaceTexture is already null. Nothing to disconnect.");
            return;
        }
        this.mSurfaceTexture = null;
        if (this.mSurfaceId > 0) {
            context.getGLEnvironment().unregisterSurfaceId(this.mSurfaceId);
            this.mSurfaceId = -1;
        }
    }

    @Override // android.filterfw.core.Filter
    public synchronized void process(FilterContext context) {
        Frame gpuFrame;
        if (this.mSurfaceId <= 0) {
            return;
        }
        GLEnvironment glEnv = context.getGLEnvironment();
        Frame input = pullInput("frame");
        boolean createdFrame = false;
        float currentAspectRatio = input.getFormat().getWidth() / input.getFormat().getHeight();
        if (currentAspectRatio != this.mAspectRatio) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Process. New aspect ratio: " + currentAspectRatio + ", previously: " + this.mAspectRatio + ". Thread: " + Thread.currentThread());
            }
            this.mAspectRatio = currentAspectRatio;
            updateTargetRect();
        }
        int target = input.getFormat().getTarget();
        if (target != 3) {
            gpuFrame = context.getFrameManager().duplicateFrameToTarget(input, 3);
            createdFrame = true;
        } else {
            gpuFrame = input;
        }
        glEnv.activateSurfaceWithId(this.mSurfaceId);
        this.mProgram.process(gpuFrame, this.mScreen);
        glEnv.setSurfaceTimestamp(input.getTimestamp());
        glEnv.swapBuffers();
        if (createdFrame) {
            gpuFrame.release();
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "FPVU. Thread: " + Thread.currentThread());
        }
        updateRenderMode();
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
        if (this.mLogVerbose) {
            Log.m106v(TAG, "updateTargetRect. Thread: " + Thread.currentThread());
        }
        int i2 = this.mScreenWidth;
        if (i2 > 0 && (i = this.mScreenHeight) > 0 && this.mProgram != null) {
            float screenAspectRatio = i2 / i;
            float relativeAspectRatio = screenAspectRatio / this.mAspectRatio;
            if (this.mLogVerbose) {
                Log.m106v(TAG, "UTR. screen w = " + this.mScreenWidth + " x screen h = " + this.mScreenHeight + " Screen AR: " + screenAspectRatio + ", frame AR: " + this.mAspectRatio + ", relative AR: " + relativeAspectRatio);
            }
            if (relativeAspectRatio == 1.0f && this.mRenderMode != 3) {
                this.mProgram.setTargetRect(0.0f, 0.0f, 1.0f, 1.0f);
                this.mProgram.setClearsOutput(false);
                return;
            }
            switch (this.mRenderMode) {
                case 0:
                    this.mTargetQuad.f52p0.set(0.0f, 0.0f);
                    this.mTargetQuad.f53p1.set(1.0f, 0.0f);
                    this.mTargetQuad.f54p2.set(0.0f, 1.0f);
                    this.mTargetQuad.f55p3.set(1.0f, 1.0f);
                    this.mProgram.setClearsOutput(false);
                    break;
                case 1:
                    if (relativeAspectRatio > 1.0f) {
                        this.mTargetQuad.f52p0.set(0.5f - (0.5f / relativeAspectRatio), 0.0f);
                        this.mTargetQuad.f53p1.set((0.5f / relativeAspectRatio) + 0.5f, 0.0f);
                        this.mTargetQuad.f54p2.set(0.5f - (0.5f / relativeAspectRatio), 1.0f);
                        this.mTargetQuad.f55p3.set((0.5f / relativeAspectRatio) + 0.5f, 1.0f);
                    } else {
                        this.mTargetQuad.f52p0.set(0.0f, 0.5f - (relativeAspectRatio * 0.5f));
                        this.mTargetQuad.f53p1.set(1.0f, 0.5f - (relativeAspectRatio * 0.5f));
                        this.mTargetQuad.f54p2.set(0.0f, (relativeAspectRatio * 0.5f) + 0.5f);
                        this.mTargetQuad.f55p3.set(1.0f, (relativeAspectRatio * 0.5f) + 0.5f);
                    }
                    this.mProgram.setClearsOutput(true);
                    break;
                case 2:
                    if (relativeAspectRatio > 1.0f) {
                        this.mTargetQuad.f52p0.set(0.0f, 0.5f - (relativeAspectRatio * 0.5f));
                        this.mTargetQuad.f53p1.set(1.0f, 0.5f - (relativeAspectRatio * 0.5f));
                        this.mTargetQuad.f54p2.set(0.0f, (relativeAspectRatio * 0.5f) + 0.5f);
                        this.mTargetQuad.f55p3.set(1.0f, (relativeAspectRatio * 0.5f) + 0.5f);
                    } else {
                        this.mTargetQuad.f52p0.set(0.5f - (0.5f / relativeAspectRatio), 0.0f);
                        this.mTargetQuad.f53p1.set((0.5f / relativeAspectRatio) + 0.5f, 0.0f);
                        this.mTargetQuad.f54p2.set(0.5f - (0.5f / relativeAspectRatio), 1.0f);
                        this.mTargetQuad.f55p3.set((0.5f / relativeAspectRatio) + 0.5f, 1.0f);
                    }
                    this.mProgram.setClearsOutput(true);
                    break;
                case 3:
                    this.mProgram.setSourceRegion(this.mSourceQuad);
                    break;
            }
            if (this.mLogVerbose) {
                Log.m106v(TAG, "UTR. quad: " + this.mTargetQuad);
            }
            this.mProgram.setTargetRegion(this.mTargetQuad);
        }
    }
}
