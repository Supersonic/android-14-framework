package android.filterpacks.videosrc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
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
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Matrix;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
/* loaded from: classes.dex */
public class MediaSource extends Filter {
    private static final int NEWFRAME_TIMEOUT = 100;
    private static final int NEWFRAME_TIMEOUT_REPEAT = 10;
    private static final int PREP_TIMEOUT = 100;
    private static final int PREP_TIMEOUT_REPEAT = 100;
    private static final String TAG = "MediaSource";
    private boolean mCompleted;
    @GenerateFieldPort(hasDefault = true, name = "context")
    private Context mContext;
    private ShaderProgram mFrameExtractor;
    private final String mFrameShader;
    private boolean mGotSize;
    private int mHeight;
    private final boolean mLogVerbose;
    @GenerateFieldPort(hasDefault = true, name = "loop")
    private boolean mLooping;
    private GLFrame mMediaFrame;
    private MediaPlayer mMediaPlayer;
    private boolean mNewFrameAvailable;
    @GenerateFieldPort(hasDefault = true, name = "orientation")
    private int mOrientation;
    private boolean mOrientationUpdated;
    private MutableFrameFormat mOutputFormat;
    private boolean mPaused;
    private boolean mPlaying;
    private boolean mPrepared;
    @GenerateFieldPort(hasDefault = true, name = "sourceIsUrl")
    private boolean mSelectedIsUrl;
    @GenerateFieldPort(hasDefault = true, name = "sourceAsset")
    private AssetFileDescriptor mSourceAsset;
    @GenerateFieldPort(hasDefault = true, name = "sourceUrl")
    private String mSourceUrl;
    private SurfaceTexture mSurfaceTexture;
    @GenerateFieldPort(hasDefault = true, name = TextToSpeech.Engine.KEY_PARAM_VOLUME)
    private float mVolume;
    @GenerateFinalPort(hasDefault = true, name = "waitForNewFrame")
    private boolean mWaitForNewFrame;
    private int mWidth;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private SurfaceTexture.OnFrameAvailableListener onMediaFrameAvailableListener;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener;
    private static final float[] mSourceCoords_0 = {1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] mSourceCoords_270 = {0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] mSourceCoords_180 = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final float[] mSourceCoords_90 = {1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    public MediaSource(String name) {
        super(name);
        this.mSourceUrl = "";
        this.mSourceAsset = null;
        this.mContext = null;
        this.mSelectedIsUrl = false;
        this.mWaitForNewFrame = true;
        this.mLooping = true;
        this.mVolume = 0.0f;
        this.mOrientation = 0;
        this.mFrameShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
        this.onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: android.filterpacks.videosrc.MediaSource.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (MediaSource.this.mLogVerbose) {
                    Log.m106v(MediaSource.TAG, "MediaPlayer sent dimensions: " + width + " x " + height);
                }
                if (!MediaSource.this.mGotSize) {
                    if (MediaSource.this.mOrientation == 0 || MediaSource.this.mOrientation == 180) {
                        MediaSource.this.mOutputFormat.setDimensions(width, height);
                    } else {
                        MediaSource.this.mOutputFormat.setDimensions(height, width);
                    }
                    MediaSource.this.mWidth = width;
                    MediaSource.this.mHeight = height;
                } else if (MediaSource.this.mOutputFormat.getWidth() != width || MediaSource.this.mOutputFormat.getHeight() != height) {
                    Log.m110e(MediaSource.TAG, "Multiple video size change events received!");
                }
                synchronized (MediaSource.this) {
                    MediaSource.this.mGotSize = true;
                    MediaSource.this.notify();
                }
            }
        };
        this.onPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: android.filterpacks.videosrc.MediaSource.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                if (MediaSource.this.mLogVerbose) {
                    Log.m106v(MediaSource.TAG, "MediaPlayer is prepared");
                }
                synchronized (MediaSource.this) {
                    MediaSource.this.mPrepared = true;
                    MediaSource.this.notify();
                }
            }
        };
        this.onCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: android.filterpacks.videosrc.MediaSource.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                if (MediaSource.this.mLogVerbose) {
                    Log.m106v(MediaSource.TAG, "MediaPlayer has completed playback");
                }
                synchronized (MediaSource.this) {
                    MediaSource.this.mCompleted = true;
                }
            }
        };
        this.onMediaFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.filterpacks.videosrc.MediaSource.4
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if (MediaSource.this.mLogVerbose) {
                    Log.m106v(MediaSource.TAG, "New frame from media player");
                }
                synchronized (MediaSource.this) {
                    if (MediaSource.this.mLogVerbose) {
                        Log.m106v(MediaSource.TAG, "New frame: notify");
                    }
                    MediaSource.this.mNewFrameAvailable = true;
                    MediaSource.this.notify();
                    if (MediaSource.this.mLogVerbose) {
                        Log.m106v(MediaSource.TAG, "New frame: notify done");
                    }
                }
            }
        };
        this.mNewFrameAvailable = false;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("video", ImageFormat.create(3, 3));
    }

    private void createFormats() {
        this.mOutputFormat = ImageFormat.create(3, 3);
    }

    @Override // android.filterfw.core.Filter
    protected void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Preparing MediaSource");
        }
        ShaderProgram shaderProgram = new ShaderProgram(context, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
        this.mFrameExtractor = shaderProgram;
        shaderProgram.setSourceRect(0.0f, 1.0f, 1.0f, -1.0f);
        createFormats();
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Opening MediaSource");
            if (this.mSelectedIsUrl) {
                Log.m106v(TAG, "Current URL is " + this.mSourceUrl);
            } else {
                Log.m106v(TAG, "Current source is Asset!");
            }
        }
        GLFrame gLFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L);
        this.mMediaFrame = gLFrame;
        this.mSurfaceTexture = new SurfaceTexture(gLFrame.getTextureId());
        if (!setupMediaPlayer(this.mSelectedIsUrl)) {
            throw new RuntimeException("Error setting up MediaPlayer!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Processing new frame");
        }
        if (this.mMediaPlayer == null) {
            throw new NullPointerException("Unexpected null media player!");
        }
        if (this.mCompleted) {
            closeOutputPort("video");
            return;
        }
        if (!this.mPlaying) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Waiting for preparation to complete");
            }
            int waitCount = 0;
            do {
                if (!this.mGotSize || !this.mPrepared) {
                    try {
                        wait(100L);
                    } catch (InterruptedException e) {
                    }
                    if (this.mCompleted) {
                        closeOutputPort("video");
                        return;
                    }
                    waitCount++;
                } else {
                    if (this.mLogVerbose) {
                        Log.m106v(TAG, "Starting playback");
                    }
                    this.mMediaPlayer.start();
                }
            } while (waitCount != 100);
            this.mMediaPlayer.release();
            throw new RuntimeException("MediaPlayer timed out while preparing!");
        }
        if (!this.mPaused || !this.mPlaying) {
            if (this.mWaitForNewFrame) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Waiting for new frame");
                }
                int waitCount2 = 0;
                while (!this.mNewFrameAvailable) {
                    if (waitCount2 == 10) {
                        if (this.mCompleted) {
                            closeOutputPort("video");
                            return;
                        }
                        throw new RuntimeException("Timeout waiting for new frame!");
                    }
                    try {
                        wait(100L);
                    } catch (InterruptedException e2) {
                        if (this.mLogVerbose) {
                            Log.m106v(TAG, "interrupted");
                        }
                    }
                    waitCount2++;
                }
                this.mNewFrameAvailable = false;
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Got new frame");
                }
            }
            this.mSurfaceTexture.updateTexImage();
            this.mOrientationUpdated = true;
        }
        if (this.mOrientationUpdated) {
            float[] surfaceTransform = new float[16];
            this.mSurfaceTexture.getTransformMatrix(surfaceTransform);
            float[] sourceCoords = new float[16];
            switch (this.mOrientation) {
                case 90:
                    Matrix.multiplyMM(sourceCoords, 0, surfaceTransform, 0, mSourceCoords_90, 0);
                    break;
                case 180:
                    Matrix.multiplyMM(sourceCoords, 0, surfaceTransform, 0, mSourceCoords_180, 0);
                    break;
                case 270:
                    Matrix.multiplyMM(sourceCoords, 0, surfaceTransform, 0, mSourceCoords_270, 0);
                    break;
                default:
                    Matrix.multiplyMM(sourceCoords, 0, surfaceTransform, 0, mSourceCoords_0, 0);
                    break;
            }
            if (this.mLogVerbose) {
                Log.m106v(TAG, "OrientationHint = " + this.mOrientation);
                String temp = String.format("SetSourceRegion: %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f", Float.valueOf(sourceCoords[4]), Float.valueOf(sourceCoords[5]), Float.valueOf(sourceCoords[0]), Float.valueOf(sourceCoords[1]), Float.valueOf(sourceCoords[12]), Float.valueOf(sourceCoords[13]), Float.valueOf(sourceCoords[8]), Float.valueOf(sourceCoords[9]));
                Log.m106v(TAG, temp);
            }
            this.mFrameExtractor.setSourceRegion(sourceCoords[4], sourceCoords[5], sourceCoords[0], sourceCoords[1], sourceCoords[12], sourceCoords[13], sourceCoords[8], sourceCoords[9]);
            this.mOrientationUpdated = false;
        }
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        this.mFrameExtractor.process(this.mMediaFrame, output);
        long timestamp = this.mSurfaceTexture.getTimestamp();
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Timestamp: " + (timestamp / 1.0E9d) + " s");
        }
        output.setTimestamp(timestamp);
        pushOutput("video", output);
        output.release();
        this.mPlaying = true;
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
        }
        this.mPrepared = false;
        this.mGotSize = false;
        this.mPlaying = false;
        this.mPaused = false;
        this.mCompleted = false;
        this.mNewFrameAvailable = false;
        this.mMediaPlayer.release();
        this.mMediaPlayer = null;
        this.mSurfaceTexture.release();
        this.mSurfaceTexture = null;
        if (this.mLogVerbose) {
            Log.m106v(TAG, "MediaSource closed");
        }
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
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Parameter update");
        }
        if (name.equals("sourceUrl")) {
            if (isOpen()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Opening new source URL");
                }
                boolean z = this.mSelectedIsUrl;
                if (z) {
                    setupMediaPlayer(z);
                }
            }
        } else if (name.equals("sourceAsset")) {
            if (isOpen()) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Opening new source FD");
                }
                boolean z2 = this.mSelectedIsUrl;
                if (!z2) {
                    setupMediaPlayer(z2);
                }
            }
        } else if (name.equals("loop")) {
            if (isOpen()) {
                this.mMediaPlayer.setLooping(this.mLooping);
            }
        } else if (name.equals("sourceIsUrl")) {
            if (isOpen()) {
                if (this.mSelectedIsUrl) {
                    if (this.mLogVerbose) {
                        Log.m106v(TAG, "Opening new source URL");
                    }
                } else if (this.mLogVerbose) {
                    Log.m106v(TAG, "Opening new source Asset");
                }
                setupMediaPlayer(this.mSelectedIsUrl);
            }
        } else if (name.equals(TextToSpeech.Engine.KEY_PARAM_VOLUME)) {
            if (isOpen()) {
                MediaPlayer mediaPlayer = this.mMediaPlayer;
                float f = this.mVolume;
                mediaPlayer.setVolume(f, f);
            }
        } else if (name.equals("orientation") && this.mGotSize) {
            int i = this.mOrientation;
            if (i == 0 || i == 180) {
                this.mOutputFormat.setDimensions(this.mWidth, this.mHeight);
            } else {
                this.mOutputFormat.setDimensions(this.mHeight, this.mWidth);
            }
            this.mOrientationUpdated = true;
        }
    }

    public synchronized void pauseVideo(boolean pauseState) {
        if (isOpen()) {
            if (pauseState && !this.mPaused) {
                this.mMediaPlayer.pause();
            } else if (!pauseState && this.mPaused) {
                this.mMediaPlayer.start();
            }
        }
        this.mPaused = pauseState;
    }

    private synchronized boolean setupMediaPlayer(boolean useUrl) {
        this.mPrepared = false;
        this.mGotSize = false;
        this.mPlaying = false;
        this.mPaused = false;
        this.mCompleted = false;
        this.mNewFrameAvailable = false;
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Setting up playback.");
        }
        if (this.mMediaPlayer != null) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Resetting existing MediaPlayer.");
            }
            this.mMediaPlayer.reset();
        } else {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Creating new MediaPlayer.");
            }
            this.mMediaPlayer = new MediaPlayer();
        }
        if (this.mMediaPlayer == null) {
            throw new RuntimeException("Unable to create a MediaPlayer!");
        }
        try {
            if (useUrl) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Setting MediaPlayer source to URI " + this.mSourceUrl);
                }
                Context context = this.mContext;
                if (context == null) {
                    this.mMediaPlayer.setDataSource(this.mSourceUrl);
                } else {
                    this.mMediaPlayer.setDataSource(context, Uri.parse(this.mSourceUrl.toString()));
                }
            } else {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Setting MediaPlayer source to asset " + this.mSourceAsset);
                }
                this.mMediaPlayer.setDataSource(this.mSourceAsset.getFileDescriptor(), this.mSourceAsset.getStartOffset(), this.mSourceAsset.getLength());
            }
            this.mMediaPlayer.setLooping(this.mLooping);
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            float f = this.mVolume;
            mediaPlayer.setVolume(f, f);
            Surface surface = new Surface(this.mSurfaceTexture);
            this.mMediaPlayer.setSurface(surface);
            surface.release();
            this.mMediaPlayer.setOnVideoSizeChangedListener(this.onVideoSizeChangedListener);
            this.mMediaPlayer.setOnPreparedListener(this.onPreparedListener);
            this.mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
            this.mSurfaceTexture.setOnFrameAvailableListener(this.onMediaFrameAvailableListener);
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Preparing MediaPlayer.");
            }
            this.mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            if (useUrl) {
                throw new RuntimeException(String.format("Unable to set MediaPlayer to URL %s!", this.mSourceUrl), e);
            }
            throw new RuntimeException(String.format("Unable to set MediaPlayer to asset %s!", this.mSourceAsset), e);
        } catch (IllegalArgumentException e2) {
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            if (useUrl) {
                throw new RuntimeException(String.format("Unable to set MediaPlayer to URL %s!", this.mSourceUrl), e2);
            }
            throw new RuntimeException(String.format("Unable to set MediaPlayer to asset %s!", this.mSourceAsset), e2);
        }
        return true;
    }
}
