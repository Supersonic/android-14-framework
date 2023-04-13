package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
/* loaded from: classes.dex */
public class RedEyeFilter extends Filter {
    private static final float DEFAULT_RED_INTENSITY = 1.3f;
    private static final float MIN_RADIUS = 10.0f;
    private static final float RADIUS_RATIO = 0.06f;
    private final Canvas mCanvas;
    @GenerateFieldPort(name = "centers")
    private float[] mCenters;
    private int mHeight;
    private final Paint mPaint;
    private Program mProgram;
    private float mRadius;
    private Bitmap mRedEyeBitmap;
    private Frame mRedEyeFrame;
    private final String mRedEyeShader;
    private int mTarget;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize;
    private int mWidth;

    public RedEyeFilter(String name) {
        super(name);
        this.mTileSize = 640;
        this.mCanvas = new Canvas();
        this.mPaint = new Paint();
        this.mWidth = 0;
        this.mHeight = 0;
        this.mTarget = 0;
        this.mRedEyeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n";
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    public void initProgram(FilterContext context, int target) {
        switch (target) {
            case 3:
                ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n");
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                shaderProgram.setHostValue("intensity", Float.valueOf(1.3f));
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter RedEye does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput(SliceItem.FORMAT_IMAGE);
        FrameFormat inputFormat = input.getFormat();
        Frame output = context.getFrameManager().newFrame(inputFormat);
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (inputFormat.getWidth() != this.mWidth || inputFormat.getHeight() != this.mHeight) {
            this.mWidth = inputFormat.getWidth();
            this.mHeight = inputFormat.getHeight();
        }
        createRedEyeFrame(context);
        Frame[] inputs = {input, this.mRedEyeFrame};
        this.mProgram.process(inputs, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
        this.mRedEyeFrame.release();
        this.mRedEyeFrame = null;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateProgramParams();
        }
    }

    private void createRedEyeFrame(FilterContext context) {
        int bitmapWidth = this.mWidth / 2;
        int bitmapHeight = this.mHeight / 2;
        Bitmap redEyeBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(redEyeBitmap);
        this.mPaint.setColor(-1);
        this.mRadius = Math.max((float) MIN_RADIUS, Math.min(bitmapWidth, bitmapHeight) * RADIUS_RATIO);
        int i = 0;
        while (true) {
            float[] fArr = this.mCenters;
            if (i < fArr.length) {
                this.mCanvas.drawCircle(fArr[i] * bitmapWidth, fArr[i + 1] * bitmapHeight, this.mRadius, this.mPaint);
                i += 2;
            } else {
                FrameFormat format = ImageFormat.create(bitmapWidth, bitmapHeight, 3, 3);
                Frame newFrame = context.getFrameManager().newFrame(format);
                this.mRedEyeFrame = newFrame;
                newFrame.setBitmap(redEyeBitmap);
                redEyeBitmap.recycle();
                return;
            }
        }
    }

    private void updateProgramParams() {
        if (this.mCenters.length % 2 == 1) {
            throw new RuntimeException("The size of center array must be even.");
        }
    }
}
