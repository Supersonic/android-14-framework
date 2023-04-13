package android.filterpacks.videoproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.opengl.GLES20;
import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.Arrays;
/* loaded from: classes.dex */
public class BackDropperFilter extends Filter {
    private static final float DEFAULT_ACCEPT_STDDEV = 0.85f;
    private static final float DEFAULT_ADAPT_RATE_BG = 0.0f;
    private static final float DEFAULT_ADAPT_RATE_FG = 0.0f;
    private static final String DEFAULT_AUTO_WB_SCALE = "0.25";
    private static final float DEFAULT_EXPOSURE_CHANGE = 1.0f;
    private static final int DEFAULT_HIER_LRG_EXPONENT = 3;
    private static final float DEFAULT_HIER_LRG_SCALE = 0.7f;
    private static final int DEFAULT_HIER_MID_EXPONENT = 2;
    private static final float DEFAULT_HIER_MID_SCALE = 0.6f;
    private static final int DEFAULT_HIER_SML_EXPONENT = 0;
    private static final float DEFAULT_HIER_SML_SCALE = 0.5f;
    private static final float DEFAULT_LEARNING_ADAPT_RATE = 0.2f;
    private static final int DEFAULT_LEARNING_DONE_THRESHOLD = 20;
    private static final int DEFAULT_LEARNING_DURATION = 40;
    private static final int DEFAULT_LEARNING_VERIFY_DURATION = 10;
    private static final float DEFAULT_MASK_BLEND_BG = 0.65f;
    private static final float DEFAULT_MASK_BLEND_FG = 0.95f;
    private static final int DEFAULT_MASK_HEIGHT_EXPONENT = 8;
    private static final float DEFAULT_MASK_VERIFY_RATE = 0.25f;
    private static final int DEFAULT_MASK_WIDTH_EXPONENT = 8;
    private static final float DEFAULT_UV_SCALE_FACTOR = 1.35f;
    private static final float DEFAULT_WHITE_BALANCE_BLUE_CHANGE = 0.0f;
    private static final float DEFAULT_WHITE_BALANCE_RED_CHANGE = 0.0f;
    private static final int DEFAULT_WHITE_BALANCE_TOGGLE = 0;
    private static final float DEFAULT_Y_SCALE_FACTOR = 0.4f;
    private static final String DISTANCE_STORAGE_SCALE = "0.6";
    private static final String MASK_SMOOTH_EXPONENT = "2.0";
    private static final String MIN_VARIANCE = "3.0";
    private static final String RGB_TO_YUV_MATRIX = "0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 ";
    private static final String TAG = "BackDropperFilter";
    private static final String VARIANCE_STORAGE_SCALE = "5.0";
    private static final String mAutomaticWhiteBalance = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float pyramid_depth;\nuniform bool autowb_toggle;\nvarying vec2 v_texcoord;\nvoid main() {\n   vec4 mean_video = texture2D(tex_sampler_0, v_texcoord, pyramid_depth);\n   vec4 mean_bg = texture2D(tex_sampler_1, v_texcoord, pyramid_depth);\n   float green_normalizer = mean_video.g / mean_bg.g;\n   vec4 adjusted_value = vec4(mean_bg.r / mean_video.r * green_normalizer, 1., \n                         mean_bg.b / mean_video.b * green_normalizer, 1.) * auto_wb_scale; \n   gl_FragColor = autowb_toggle ? adjusted_value : vec4(auto_wb_scale);\n}\n";
    private static final String mBgDistanceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n\n  float dist_y = gauss_dist_y(fg.r, mean.r, variance.r);\n  float dist_uv = gauss_dist_uv(fg.gb, mean.gb, variance.gb);\n  gl_FragColor = vec4(0.5*fg.rg, dist_scale*dist_y, dist_scale*dist_uv);\n}\n";
    private static final String mBgMaskShader = "uniform sampler2D tex_sampler_0;\nuniform float accept_variance;\nuniform vec2 yuv_weights;\nuniform float scale_lrg;\nuniform float scale_mid;\nuniform float scale_sml;\nuniform float exp_lrg;\nuniform float exp_mid;\nuniform float exp_sml;\nvarying vec2 v_texcoord;\nbool is_fg(vec2 dist_yc, float accept_variance) {\n  return ( dot(yuv_weights, dist_yc) >= accept_variance );\n}\nvoid main() {\n  vec4 dist_lrg_sc = texture2D(tex_sampler_0, v_texcoord, exp_lrg);\n  vec4 dist_mid_sc = texture2D(tex_sampler_0, v_texcoord, exp_mid);\n  vec4 dist_sml_sc = texture2D(tex_sampler_0, v_texcoord, exp_sml);\n  vec2 dist_lrg = inv_dist_scale * dist_lrg_sc.ba;\n  vec2 dist_mid = inv_dist_scale * dist_mid_sc.ba;\n  vec2 dist_sml = inv_dist_scale * dist_sml_sc.ba;\n  vec2 norm_dist = 0.75 * dist_sml / accept_variance;\n  bool is_fg_lrg = is_fg(dist_lrg, accept_variance * scale_lrg);\n  bool is_fg_mid = is_fg_lrg || is_fg(dist_mid, accept_variance * scale_mid);\n  float is_fg_sml =\n      float(is_fg_mid || is_fg(dist_sml, accept_variance * scale_sml));\n  float alpha = 0.5 * is_fg_sml + 0.3 * float(is_fg_mid) + 0.2 * float(is_fg_lrg);\n  gl_FragColor = vec4(alpha, norm_dist, is_fg_sml);\n}\n";
    private static final String mBgSubtractForceShader = "  vec4 ghost_rgb = (fg_adjusted * 0.7 + vec4(0.3,0.3,0.4,0.))*0.65 + \n                   0.35*bg_rgb;\n  float glow_start = 0.75 * mask_blend_bg; \n  float glow_max   = mask_blend_bg; \n  gl_FragColor = mask.a < glow_start ? bg_rgb : \n                 mask.a < glow_max ? mix(bg_rgb, vec4(0.9,0.9,1.0,1.0), \n                                     (mask.a - glow_start) / (glow_max - glow_start) ) : \n                 mask.a < mask_blend_fg ? mix(vec4(0.9,0.9,1.0,1.0), ghost_rgb, \n                                    (mask.a - glow_max) / (mask_blend_fg - glow_max) ) : \n                 ghost_rgb;\n}\n";
    private static final String mBgSubtractShader = "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n";
    private static final String mMaskVerifyShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float verify_rate;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 lastmask = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  float newmask = mix(lastmask.a, mask.a, verify_rate);\n  gl_FragColor = vec4(0., 0., 0., newmask);\n}\n";
    private static final String mUpdateBgModelMeanShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 new_mean = mix(mean, fg, alpha);\n  gl_FragColor = new_mean;\n}\n";
    private static final String mUpdateBgModelVarianceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_3, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 cur_variance = (fg-mean)*(fg-mean);\n  vec4 new_variance = mix(variance, cur_variance, alpha);\n  new_variance = max(new_variance, vec4(min_variance));\n  gl_FragColor = var_scale * new_variance;\n}\n";
    private final int BACKGROUND_FILL_CROP;
    private final int BACKGROUND_FIT;
    private final int BACKGROUND_STRETCH;
    private ShaderProgram copyShaderProgram;
    private boolean isOpen;
    @GenerateFieldPort(hasDefault = true, name = "acceptStddev")
    private float mAcceptStddev;
    @GenerateFieldPort(hasDefault = true, name = "adaptRateBg")
    private float mAdaptRateBg;
    @GenerateFieldPort(hasDefault = true, name = "adaptRateFg")
    private float mAdaptRateFg;
    @GenerateFieldPort(hasDefault = true, name = "learningAdaptRate")
    private float mAdaptRateLearning;
    private GLFrame mAutoWB;
    @GenerateFieldPort(hasDefault = true, name = "autowbToggle")
    private int mAutoWBToggle;
    private ShaderProgram mAutomaticWhiteBalanceProgram;
    private MutableFrameFormat mAverageFormat;
    @GenerateFieldPort(hasDefault = true, name = "backgroundFitMode")
    private int mBackgroundFitMode;
    private boolean mBackgroundFitModeChanged;
    private ShaderProgram mBgDistProgram;
    private GLFrame mBgInput;
    private ShaderProgram mBgMaskProgram;
    private GLFrame[] mBgMean;
    private ShaderProgram mBgSubtractProgram;
    private ShaderProgram mBgUpdateMeanProgram;
    private ShaderProgram mBgUpdateVarianceProgram;
    private GLFrame[] mBgVariance;
    @GenerateFieldPort(hasDefault = true, name = "chromaScale")
    private float mChromaScale;
    private ShaderProgram mCopyOutProgram;
    private GLFrame mDistance;
    @GenerateFieldPort(hasDefault = true, name = "exposureChange")
    private float mExposureChange;
    private int mFrameCount;
    @GenerateFieldPort(hasDefault = true, name = "hierLrgExp")
    private int mHierarchyLrgExp;
    @GenerateFieldPort(hasDefault = true, name = "hierLrgScale")
    private float mHierarchyLrgScale;
    @GenerateFieldPort(hasDefault = true, name = "hierMidExp")
    private int mHierarchyMidExp;
    @GenerateFieldPort(hasDefault = true, name = "hierMidScale")
    private float mHierarchyMidScale;
    @GenerateFieldPort(hasDefault = true, name = "hierSmlExp")
    private int mHierarchySmlExp;
    @GenerateFieldPort(hasDefault = true, name = "hierSmlScale")
    private float mHierarchySmlScale;
    @GenerateFieldPort(hasDefault = true, name = "learningDoneListener")
    private LearningDoneListener mLearningDoneListener;
    @GenerateFieldPort(hasDefault = true, name = "learningDuration")
    private int mLearningDuration;
    @GenerateFieldPort(hasDefault = true, name = "learningVerifyDuration")
    private int mLearningVerifyDuration;
    private final boolean mLogVerbose;
    @GenerateFieldPort(hasDefault = true, name = "lumScale")
    private float mLumScale;
    private GLFrame mMask;
    private GLFrame mMaskAverage;
    @GenerateFieldPort(hasDefault = true, name = "maskBg")
    private float mMaskBg;
    @GenerateFieldPort(hasDefault = true, name = "maskFg")
    private float mMaskFg;
    private MutableFrameFormat mMaskFormat;
    @GenerateFieldPort(hasDefault = true, name = "maskHeightExp")
    private int mMaskHeightExp;
    private GLFrame[] mMaskVerify;
    private ShaderProgram mMaskVerifyProgram;
    @GenerateFieldPort(hasDefault = true, name = "maskWidthExp")
    private int mMaskWidthExp;
    private MutableFrameFormat mMemoryFormat;
    @GenerateFieldPort(hasDefault = true, name = "mirrorBg")
    private boolean mMirrorBg;
    @GenerateFieldPort(hasDefault = true, name = "orientation")
    private int mOrientation;
    private FrameFormat mOutputFormat;
    private boolean mPingPong;
    @GenerateFinalPort(hasDefault = true, name = "provideDebugOutputs")
    private boolean mProvideDebugOutputs;
    private int mPyramidDepth;
    private float mRelativeAspect;
    private boolean mStartLearning;
    private int mSubsampleLevel;
    @GenerateFieldPort(hasDefault = true, name = "useTheForce")
    private boolean mUseTheForce;
    @GenerateFieldPort(hasDefault = true, name = "maskVerifyRate")
    private float mVerifyRate;
    private GLFrame mVideoInput;
    @GenerateFieldPort(hasDefault = true, name = "whitebalanceblueChange")
    private float mWhiteBalanceBlueChange;
    @GenerateFieldPort(hasDefault = true, name = "whitebalanceredChange")
    private float mWhiteBalanceRedChange;
    private long startTime;
    private static final float[] DEFAULT_BG_FIT_TRANSFORM = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final String[] mInputNames = {"video", "background"};
    private static final String[] mOutputNames = {"video"};
    private static final String[] mDebugOutputNames = {"debug1", "debug2"};
    private static String mSharedUtilShader = "precision mediump float;\nuniform float fg_adapt_rate;\nuniform float bg_adapt_rate;\nconst mat4 coeff_yuv = mat4(0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 );\nconst float dist_scale = 0.6;\nconst float inv_dist_scale = 1. / dist_scale;\nconst float var_scale=5.0;\nconst float inv_var_scale = 1. / var_scale;\nconst float min_variance = inv_var_scale *3.0/ 256.;\nconst float auto_wb_scale = 0.25;\n\nfloat gauss_dist_y(float y, float mean, float variance) {\n  float dist = (y - mean) * (y - mean) / variance;\n  return dist;\n}\nfloat gauss_dist_uv(vec2 uv, vec2 mean, vec2 variance) {\n  vec2 dist = (uv - mean) * (uv - mean) / variance;\n  return dist.r + dist.g;\n}\nfloat local_adapt_rate(float alpha) {\n  return mix(bg_adapt_rate, fg_adapt_rate, alpha);\n}\n\n";

    /* loaded from: classes.dex */
    public interface LearningDoneListener {
        void onLearningDone(BackDropperFilter backDropperFilter);
    }

    public BackDropperFilter(String name) {
        super(name);
        this.BACKGROUND_STRETCH = 0;
        this.BACKGROUND_FIT = 1;
        this.BACKGROUND_FILL_CROP = 2;
        this.mBackgroundFitMode = 2;
        this.mLearningDuration = 40;
        this.mLearningVerifyDuration = 10;
        this.mAcceptStddev = DEFAULT_ACCEPT_STDDEV;
        this.mHierarchyLrgScale = DEFAULT_HIER_LRG_SCALE;
        this.mHierarchyMidScale = 0.6f;
        this.mHierarchySmlScale = 0.5f;
        this.mMaskWidthExp = 8;
        this.mMaskHeightExp = 8;
        this.mHierarchyLrgExp = 3;
        this.mHierarchyMidExp = 2;
        this.mHierarchySmlExp = 0;
        this.mLumScale = 0.4f;
        this.mChromaScale = DEFAULT_UV_SCALE_FACTOR;
        this.mMaskBg = DEFAULT_MASK_BLEND_BG;
        this.mMaskFg = DEFAULT_MASK_BLEND_FG;
        this.mExposureChange = 1.0f;
        this.mWhiteBalanceRedChange = 0.0f;
        this.mWhiteBalanceBlueChange = 0.0f;
        this.mAutoWBToggle = 0;
        this.mAdaptRateLearning = 0.2f;
        this.mAdaptRateBg = 0.0f;
        this.mAdaptRateFg = 0.0f;
        this.mVerifyRate = 0.25f;
        this.mLearningDoneListener = null;
        this.mUseTheForce = false;
        this.mProvideDebugOutputs = false;
        this.mMirrorBg = false;
        this.mOrientation = 0;
        this.startTime = -1L;
        boolean isLoggable = Log.isLoggable(TAG, 2);
        this.mLogVerbose = isLoggable;
        String adjStr = SystemProperties.get("ro.media.effect.bgdropper.adj");
        if (adjStr.length() > 0) {
            try {
                this.mAcceptStddev += Float.parseFloat(adjStr);
                if (isLoggable) {
                    Log.m106v(TAG, "Adjusting accept threshold by " + adjStr + ", now " + this.mAcceptStddev);
                }
            } catch (NumberFormatException e) {
                Log.m110e(TAG, "Badly formatted property ro.media.effect.bgdropper.adj: " + adjStr);
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        FrameFormat imageFormat = ImageFormat.create(3, 0);
        for (String inputName : mInputNames) {
            addMaskedInputPort(inputName, imageFormat);
        }
        for (String outputName : mOutputNames) {
            addOutputBasedOnInput(outputName, "video");
        }
        if (this.mProvideDebugOutputs) {
            for (String outputName2 : mDebugOutputNames) {
                addOutputBasedOnInput(outputName2, "video");
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat format = inputFormat.mutableCopy();
        if (!Arrays.asList(mOutputNames).contains(portName)) {
            format.setDimensions(0, 0);
        }
        return format;
    }

    private boolean createMemoryFormat(FrameFormat inputFormat) {
        if (this.mMemoryFormat != null) {
            return false;
        }
        if (inputFormat.getWidth() == 0 || inputFormat.getHeight() == 0) {
            throw new RuntimeException("Attempting to process input frame with unknown size");
        }
        this.mMaskFormat = inputFormat.mutableCopy();
        int maskWidth = (int) Math.pow(2.0d, this.mMaskWidthExp);
        int maskHeight = (int) Math.pow(2.0d, this.mMaskHeightExp);
        this.mMaskFormat.setDimensions(maskWidth, maskHeight);
        this.mPyramidDepth = Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        this.mMemoryFormat = this.mMaskFormat.mutableCopy();
        int widthExp = Math.max(this.mMaskWidthExp, pyramidLevel(inputFormat.getWidth()));
        int heightExp = Math.max(this.mMaskHeightExp, pyramidLevel(inputFormat.getHeight()));
        this.mPyramidDepth = Math.max(widthExp, heightExp);
        int memWidth = Math.max(maskWidth, (int) Math.pow(2.0d, widthExp));
        int memHeight = Math.max(maskHeight, (int) Math.pow(2.0d, heightExp));
        this.mMemoryFormat.setDimensions(memWidth, memHeight);
        this.mSubsampleLevel = this.mPyramidDepth - Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Mask frames size " + maskWidth + " x " + maskHeight);
            Log.m106v(TAG, "Pyramid levels " + widthExp + " x " + heightExp);
            Log.m106v(TAG, "Memory frames size " + memWidth + " x " + memHeight);
        }
        MutableFrameFormat mutableCopy = inputFormat.mutableCopy();
        this.mAverageFormat = mutableCopy;
        mutableCopy.setDimensions(1, 1);
        return true;
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Preparing BackDropperFilter!");
        }
        this.mBgMean = new GLFrame[2];
        this.mBgVariance = new GLFrame[2];
        this.mMaskVerify = new GLFrame[2];
        this.copyShaderProgram = ShaderProgram.createIdentity(context);
    }

    private void allocateFrames(FrameFormat inputFormat, FilterContext context) {
        if (!createMemoryFormat(inputFormat)) {
            return;
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Allocating BackDropperFilter frames");
        }
        int numBytes = this.mMaskFormat.getSize();
        byte[] initialBgMean = new byte[numBytes];
        byte[] initialBgVariance = new byte[numBytes];
        byte[] initialMaskVerify = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            initialBgMean[i] = Byte.MIN_VALUE;
            initialBgVariance[i] = 10;
            initialMaskVerify[i] = 0;
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.mBgMean[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mBgMean[i2].setData(initialBgMean, 0, numBytes);
            this.mBgVariance[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mBgVariance[i2].setData(initialBgVariance, 0, numBytes);
            this.mMaskVerify[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mMaskVerify[i2].setData(initialMaskVerify, 0, numBytes);
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Done allocating texture for Mean and Variance objects!");
        }
        this.mDistance = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
        this.mMask = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
        this.mAutoWB = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
        this.mVideoInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
        this.mBgInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
        this.mMaskAverage = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
        ShaderProgram shaderProgram = new ShaderProgram(context, mSharedUtilShader + mBgDistanceShader);
        this.mBgDistProgram = shaderProgram;
        shaderProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        ShaderProgram shaderProgram2 = new ShaderProgram(context, mSharedUtilShader + mBgMaskShader);
        this.mBgMaskProgram = shaderProgram2;
        float f = this.mAcceptStddev;
        shaderProgram2.setHostValue("accept_variance", Float.valueOf(f * f));
        float[] yuvWeights = {this.mLumScale, this.mChromaScale};
        this.mBgMaskProgram.setHostValue("yuv_weights", yuvWeights);
        this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
        this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
        this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
        if (this.mUseTheForce) {
            this.mBgSubtractProgram = new ShaderProgram(context, mSharedUtilShader + mBgSubtractShader + mBgSubtractForceShader);
        } else {
            this.mBgSubtractProgram = new ShaderProgram(context, mSharedUtilShader + mBgSubtractShader + "}\n");
        }
        this.mBgSubtractProgram.setHostValue("bg_fit_transform", DEFAULT_BG_FIT_TRANSFORM);
        this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        ShaderProgram shaderProgram3 = new ShaderProgram(context, mSharedUtilShader + mUpdateBgModelMeanShader);
        this.mBgUpdateMeanProgram = shaderProgram3;
        shaderProgram3.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        ShaderProgram shaderProgram4 = new ShaderProgram(context, mSharedUtilShader + mUpdateBgModelVarianceShader);
        this.mBgUpdateVarianceProgram = shaderProgram4;
        shaderProgram4.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        this.mCopyOutProgram = ShaderProgram.createIdentity(context);
        ShaderProgram shaderProgram5 = new ShaderProgram(context, mSharedUtilShader + mAutomaticWhiteBalance);
        this.mAutomaticWhiteBalanceProgram = shaderProgram5;
        shaderProgram5.setHostValue("pyramid_depth", Float.valueOf(this.mPyramidDepth));
        this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
        ShaderProgram shaderProgram6 = new ShaderProgram(context, mSharedUtilShader + mMaskVerifyShader);
        this.mMaskVerifyProgram = shaderProgram6;
        shaderProgram6.setHostValue("verify_rate", Float.valueOf(this.mVerifyRate));
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Shader width set to " + this.mMemoryFormat.getWidth());
        }
        this.mRelativeAspect = 1.0f;
        this.mFrameCount = 0;
        this.mStartLearning = true;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame video = pullInput("video");
        Frame background = pullInput("background");
        allocateFrames(video.getFormat(), context);
        if (this.mStartLearning) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Starting learning");
            }
            this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mFrameCount = 0;
        }
        boolean z = this.mPingPong;
        int inputIndex = !z ? 1 : 0;
        this.mPingPong = !z ? 1 : 0;
        updateBgScaling(video, background, this.mBackgroundFitModeChanged);
        this.mBackgroundFitModeChanged = false;
        this.copyShaderProgram.process(video, this.mVideoInput);
        this.copyShaderProgram.process(background, this.mBgInput);
        this.mVideoInput.generateMipMap();
        this.mVideoInput.setTextureParameter(10241, 9985);
        this.mBgInput.generateMipMap();
        this.mBgInput.setTextureParameter(10241, 9985);
        if (this.mStartLearning) {
            this.copyShaderProgram.process(this.mVideoInput, this.mBgMean[inputIndex]);
            this.mStartLearning = false;
        }
        Frame[] distInputs = {this.mVideoInput, this.mBgMean[inputIndex], this.mBgVariance[inputIndex]};
        this.mBgDistProgram.process(distInputs, this.mDistance);
        this.mDistance.generateMipMap();
        this.mDistance.setTextureParameter(10241, 9985);
        this.mBgMaskProgram.process(this.mDistance, this.mMask);
        this.mMask.generateMipMap();
        this.mMask.setTextureParameter(10241, 9985);
        Frame[] autoWBInputs = {this.mVideoInput, this.mBgInput};
        this.mAutomaticWhiteBalanceProgram.process(autoWBInputs, this.mAutoWB);
        if (this.mFrameCount <= this.mLearningDuration) {
            pushOutput("video", video);
            int i = this.mFrameCount;
            int i2 = this.mLearningDuration;
            int i3 = this.mLearningVerifyDuration;
            if (i == i2 - i3) {
                ShaderProgram shaderProgram = this.copyShaderProgram;
                GLFrame gLFrame = this.mMask;
                GLFrame[] gLFrameArr = this.mMaskVerify;
                int outputIndex = z ? 1 : 0;
                shaderProgram.process(gLFrame, gLFrameArr[outputIndex]);
                this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
                this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
            } else if (i > i2 - i3) {
                GLFrame[] gLFrameArr2 = this.mMaskVerify;
                Frame[] maskVerifyInputs = {gLFrameArr2[inputIndex], this.mMask};
                ShaderProgram shaderProgram2 = this.mMaskVerifyProgram;
                int outputIndex2 = z ? 1 : 0;
                shaderProgram2.process(maskVerifyInputs, gLFrameArr2[outputIndex2]);
                GLFrame[] gLFrameArr3 = this.mMaskVerify;
                int outputIndex3 = z ? 1 : 0;
                gLFrameArr3[outputIndex3].generateMipMap();
                GLFrame[] gLFrameArr4 = this.mMaskVerify;
                int outputIndex4 = z ? 1 : 0;
                gLFrameArr4[outputIndex4].setTextureParameter(10241, 9985);
            }
            if (this.mFrameCount == this.mLearningDuration) {
                ShaderProgram shaderProgram3 = this.copyShaderProgram;
                GLFrame[] gLFrameArr5 = this.mMaskVerify;
                int outputIndex5 = z ? 1 : 0;
                shaderProgram3.process(gLFrameArr5[outputIndex5], this.mMaskAverage);
                ByteBuffer mMaskAverageByteBuffer = this.mMaskAverage.getData();
                byte[] mask_average = mMaskAverageByteBuffer.array();
                int bi = mask_average[3] & 255;
                if (this.mLogVerbose) {
                    Log.m106v(TAG, String.format("Mask_average is %d, threshold is %d", Integer.valueOf(bi), 20));
                }
                if (bi >= 20) {
                    this.mStartLearning = true;
                } else {
                    if (this.mLogVerbose) {
                        Log.m106v(TAG, "Learning done");
                    }
                    LearningDoneListener learningDoneListener = this.mLearningDoneListener;
                    if (learningDoneListener != null) {
                        learningDoneListener.onLearningDone(this);
                    }
                }
            }
        } else {
            Frame output = context.getFrameManager().newFrame(video.getFormat());
            Frame[] subtractInputs = {video, background, this.mMask, this.mAutoWB};
            this.mBgSubtractProgram.process(subtractInputs, output);
            pushOutput("video", output);
            output.release();
        }
        if (this.mFrameCount < this.mLearningDuration - this.mLearningVerifyDuration || this.mAdaptRateBg > 0.0d || this.mAdaptRateFg > 0.0d) {
            GLFrame[] gLFrameArr6 = this.mBgMean;
            Frame[] meanUpdateInputs = {this.mVideoInput, gLFrameArr6[inputIndex], this.mMask};
            ShaderProgram shaderProgram4 = this.mBgUpdateMeanProgram;
            int outputIndex6 = z ? 1 : 0;
            shaderProgram4.process(meanUpdateInputs, gLFrameArr6[outputIndex6]);
            GLFrame[] gLFrameArr7 = this.mBgMean;
            int outputIndex7 = z ? 1 : 0;
            gLFrameArr7[outputIndex7].generateMipMap();
            GLFrame[] gLFrameArr8 = this.mBgMean;
            int outputIndex8 = z ? 1 : 0;
            gLFrameArr8[outputIndex8].setTextureParameter(10241, 9985);
            GLFrame[] gLFrameArr9 = this.mBgVariance;
            Frame[] varianceUpdateInputs = {this.mVideoInput, this.mBgMean[inputIndex], gLFrameArr9[inputIndex], this.mMask};
            ShaderProgram shaderProgram5 = this.mBgUpdateVarianceProgram;
            int outputIndex9 = z ? 1 : 0;
            shaderProgram5.process(varianceUpdateInputs, gLFrameArr9[outputIndex9]);
            GLFrame[] gLFrameArr10 = this.mBgVariance;
            int outputIndex10 = z ? 1 : 0;
            gLFrameArr10[outputIndex10].generateMipMap();
            GLFrame[] gLFrameArr11 = this.mBgVariance;
            int outputIndex11 = z ? 1 : 0;
            gLFrameArr11[outputIndex11].setTextureParameter(10241, 9985);
        }
        if (this.mProvideDebugOutputs) {
            Frame dbg1 = context.getFrameManager().newFrame(video.getFormat());
            this.mCopyOutProgram.process(video, dbg1);
            pushOutput("debug1", dbg1);
            dbg1.release();
            Frame dbg2 = context.getFrameManager().newFrame(this.mMemoryFormat);
            this.mCopyOutProgram.process(this.mMask, dbg2);
            pushOutput("debug2", dbg2);
            dbg2.release();
        }
        int i4 = this.mFrameCount + 1;
        this.mFrameCount = i4;
        if (this.mLogVerbose && i4 % 30 == 0) {
            if (this.startTime == -1) {
                context.getGLEnvironment().activate();
                GLES20.glFinish();
                this.startTime = SystemClock.elapsedRealtime();
                return;
            }
            context.getGLEnvironment().activate();
            GLES20.glFinish();
            long endTime = SystemClock.elapsedRealtime();
            Log.m106v(TAG, "Avg. frame duration: " + String.format("%.2f", Double.valueOf((endTime - this.startTime) / 30.0d)) + " ms. Avg. fps: " + String.format("%.2f", Double.valueOf(1000.0d / ((endTime - this.startTime) / 30.0d))));
            this.startTime = endTime;
        }
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (this.mMemoryFormat == null) {
            return;
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Filter Closing!");
        }
        for (int i = 0; i < 2; i++) {
            this.mBgMean[i].release();
            this.mBgVariance[i].release();
            this.mMaskVerify[i].release();
        }
        this.mDistance.release();
        this.mMask.release();
        this.mAutoWB.release();
        this.mVideoInput.release();
        this.mBgInput.release();
        this.mMaskAverage.release();
        this.mMemoryFormat = null;
    }

    public synchronized void relearn() {
        this.mStartLearning = true;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("backgroundFitMode")) {
            this.mBackgroundFitModeChanged = true;
        } else if (name.equals("acceptStddev")) {
            ShaderProgram shaderProgram = this.mBgMaskProgram;
            float f = this.mAcceptStddev;
            shaderProgram.setHostValue("accept_variance", Float.valueOf(f * f));
        } else if (name.equals("hierLrgScale")) {
            this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        } else if (name.equals("hierMidScale")) {
            this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        } else if (name.equals("hierSmlScale")) {
            this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        } else if (name.equals("hierLrgExp")) {
            this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
        } else if (name.equals("hierMidExp")) {
            this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
        } else if (name.equals("hierSmlExp")) {
            this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
        } else if (name.equals("lumScale") || name.equals("chromaScale")) {
            float[] yuvWeights = {this.mLumScale, this.mChromaScale};
            this.mBgMaskProgram.setHostValue("yuv_weights", yuvWeights);
        } else if (name.equals("maskBg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        } else if (name.equals("maskFg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        } else if (name.equals("exposureChange")) {
            this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        } else if (name.equals("whitebalanceredChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        } else if (name.equals("whitebalanceblueChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        } else if (name.equals("autowbToggle")) {
            this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
        }
    }

    private void updateBgScaling(Frame video, Frame background, boolean fitModeChanged) {
        float foregroundAspect = video.getFormat().getWidth() / video.getFormat().getHeight();
        float backgroundAspect = background.getFormat().getWidth() / background.getFormat().getHeight();
        float currentRelativeAspect = foregroundAspect / backgroundAspect;
        if (currentRelativeAspect != this.mRelativeAspect || fitModeChanged) {
            this.mRelativeAspect = currentRelativeAspect;
            float xMin = 0.0f;
            float xWidth = 1.0f;
            float yMin = 0.0f;
            float yWidth = 1.0f;
            switch (this.mBackgroundFitMode) {
                case 1:
                    if (currentRelativeAspect > 1.0f) {
                        xMin = 0.5f - (currentRelativeAspect * 0.5f);
                        xWidth = currentRelativeAspect * 1.0f;
                        break;
                    } else {
                        yMin = 0.5f - (0.5f / currentRelativeAspect);
                        yWidth = 1.0f / currentRelativeAspect;
                        break;
                    }
                case 2:
                    if (currentRelativeAspect > 1.0f) {
                        yMin = 0.5f - (0.5f / currentRelativeAspect);
                        yWidth = 1.0f / currentRelativeAspect;
                        break;
                    } else {
                        xMin = 0.5f - (currentRelativeAspect * 0.5f);
                        xWidth = this.mRelativeAspect;
                        break;
                    }
            }
            if (this.mMirrorBg) {
                if (this.mLogVerbose) {
                    Log.m106v(TAG, "Mirroring the background!");
                }
                int i = this.mOrientation;
                if (i == 0 || i == 180) {
                    xWidth = -xWidth;
                    xMin = 1.0f - xMin;
                } else {
                    yWidth = -yWidth;
                    yMin = 1.0f - yMin;
                }
            }
            if (this.mLogVerbose) {
                Log.m106v(TAG, "bgTransform: xMin, yMin, xWidth, yWidth : " + xMin + ", " + yMin + ", " + xWidth + ", " + yWidth + ", mRelAspRatio = " + this.mRelativeAspect);
            }
            float[] bgTransform = {xWidth, 0.0f, 0.0f, 0.0f, yWidth, 0.0f, xMin, yMin, 1.0f};
            this.mBgSubtractProgram.setHostValue("bg_fit_transform", bgTransform);
        }
    }

    private int pyramidLevel(int size) {
        return ((int) Math.floor(Math.log10(size) / Math.log10(2.0d))) - 1;
    }
}
