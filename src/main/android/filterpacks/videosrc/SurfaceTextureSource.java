package android.filterpacks.videosrc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.p008os.ConditionVariable;
import android.util.Log;
/* loaded from: classes.dex */
public class SurfaceTextureSource extends Filter {
    @GenerateFieldPort(hasDefault = true, name = "closeOnTimeout")
    private boolean mCloseOnTimeout;
    private boolean mFirstFrame;
    private ShaderProgram mFrameExtractor;
    private float[] mFrameTransform;
    @GenerateFieldPort(name = "height")
    private int mHeight;
    private float[] mMappedCoords;
    private GLFrame mMediaFrame;
    private ConditionVariable mNewFrameAvailable;
    private MutableFrameFormat mOutputFormat;
    private final String mRenderShader;
    @GenerateFinalPort(name = "sourceListener")
    private SurfaceTextureSourceListener mSourceListener;
    private SurfaceTexture mSurfaceTexture;
    @GenerateFieldPort(hasDefault = true, name = "waitForNewFrame")
    private boolean mWaitForNewFrame;
    @GenerateFieldPort(hasDefault = true, name = "waitTimeout")
    private int mWaitTimeout;
    @GenerateFieldPort(name = "width")
    private int mWidth;
    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener;
    private static final float[] mSourceCoords = {0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final String TAG = "SurfaceTextureSource";
    private static final boolean mLogVerbose = Log.isLoggable(TAG, 2);

    /* loaded from: classes.dex */
    public interface SurfaceTextureSourceListener {
        void onSurfaceTextureSourceReady(SurfaceTexture surfaceTexture);
    }

    public SurfaceTextureSource(String name) {
        super(name);
        this.mWaitForNewFrame = true;
        this.mWaitTimeout = 1000;
        this.mCloseOnTimeout = false;
        this.mRenderShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
        this.onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.filterpacks.videosrc.SurfaceTextureSource.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if (SurfaceTextureSource.mLogVerbose) {
                    Log.m106v(SurfaceTextureSource.TAG, "New frame from SurfaceTexture");
                }
                SurfaceTextureSource.this.mNewFrameAvailable.open();
            }
        };
        this.mNewFrameAvailable = new ConditionVariable();
        this.mFrameTransform = new float[16];
        this.mMappedCoords = new float[16];
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("video", ImageFormat.create(3, 3));
    }

    private void createFormats() {
        this.mOutputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
    }

    @Override // android.filterfw.core.Filter
    protected void prepare(FilterContext context) {
        if (mLogVerbose) {
            Log.m106v(TAG, "Preparing SurfaceTextureSource");
        }
        createFormats();
        this.mMediaFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L);
        this.mFrameExtractor = new ShaderProgram(context, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        if (mLogVerbose) {
            Log.m106v(TAG, "Opening SurfaceTextureSource");
        }
        SurfaceTexture surfaceTexture = new SurfaceTexture(this.mMediaFrame.getTextureId());
        this.mSurfaceTexture = surfaceTexture;
        surfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
        this.mSourceListener.onSurfaceTextureSourceReady(this.mSurfaceTexture);
        this.mFirstFrame = true;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        boolean z = mLogVerbose;
        if (z) {
            Log.m106v(TAG, "Processing new frame");
        }
        if (this.mWaitForNewFrame || this.mFirstFrame) {
            int i = this.mWaitTimeout;
            if (i != 0) {
                boolean gotNewFrame = this.mNewFrameAvailable.block(i);
                if (!gotNewFrame) {
                    if (!this.mCloseOnTimeout) {
                        throw new RuntimeException("Timeout waiting for new frame");
                    }
                    if (z) {
                        Log.m106v(TAG, "Timeout waiting for a new frame. Closing.");
                    }
                    closeOutputPort("video");
                    return;
                }
            } else {
                this.mNewFrameAvailable.block();
            }
            this.mNewFrameAvailable.close();
            this.mFirstFrame = false;
        }
        this.mSurfaceTexture.updateTexImage();
        this.mSurfaceTexture.getTransformMatrix(this.mFrameTransform);
        Matrix.multiplyMM(this.mMappedCoords, 0, this.mFrameTransform, 0, mSourceCoords, 0);
        ShaderProgram shaderProgram = this.mFrameExtractor;
        float[] fArr = this.mMappedCoords;
        shaderProgram.setSourceRegion(fArr[0], fArr[1], fArr[4], fArr[5], fArr[8], fArr[9], fArr[12], fArr[13]);
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        this.mFrameExtractor.process(this.mMediaFrame, output);
        output.setTimestamp(this.mSurfaceTexture.getTimestamp());
        pushOutput("video", output);
        output.release();
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (mLogVerbose) {
            Log.m106v(TAG, "SurfaceTextureSource closed");
        }
        this.mSourceListener.onSurfaceTextureSourceReady(null);
        this.mSurfaceTexture.release();
        this.mSurfaceTexture = null;
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        GLFrame gLFrame = this.mMediaFrame;
        if (gLFrame != null) {
            gLFrame.release();
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("width") || name.equals("height")) {
            this.mOutputFormat.setDimensions(this.mWidth, this.mHeight);
        }
    }
}
