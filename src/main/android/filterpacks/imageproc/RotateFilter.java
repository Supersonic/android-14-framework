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
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
/* loaded from: classes.dex */
public class RotateFilter extends Filter {
    @GenerateFieldPort(name = "angle")
    private int mAngle;
    private int mHeight;
    private int mOutputHeight;
    private int mOutputWidth;
    private Program mProgram;
    private int mTarget;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize;
    private int mWidth;

    public RotateFilter(String name) {
        super(name);
        this.mTileSize = 640;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mTarget = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    public void initProgram(FilterContext context, int target) {
        switch (target) {
            case 3:
                ShaderProgram shaderProgram = ShaderProgram.createIdentity(context);
                shaderProgram.setMaximumTileSize(this.mTileSize);
                shaderProgram.setClearsOutput(true);
                this.mProgram = shaderProgram;
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter Sharpen does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateParameters();
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput(SliceItem.FORMAT_IMAGE);
        FrameFormat inputFormat = input.getFormat();
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (inputFormat.getWidth() != this.mWidth || inputFormat.getHeight() != this.mHeight) {
            this.mWidth = inputFormat.getWidth();
            int height = inputFormat.getHeight();
            this.mHeight = height;
            this.mOutputWidth = this.mWidth;
            this.mOutputHeight = height;
            updateParameters();
        }
        FrameFormat outputFormat = ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        this.mProgram.process(input, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }

    private void updateParameters() {
        float cosTheta;
        float sinTheta;
        int i = this.mAngle;
        if (i % 90 == 0) {
            if (i % 180 == 0) {
                cosTheta = 0.0f;
                sinTheta = i % 360 == 0 ? 1.0f : -1.0f;
            } else {
                float sinTheta2 = (i + 90) % 360 != 0 ? 1.0f : -1.0f;
                this.mOutputWidth = this.mHeight;
                this.mOutputHeight = this.mWidth;
                cosTheta = sinTheta2;
                sinTheta = 0.0f;
            }
            Point x0 = new Point(((-sinTheta) + cosTheta + 1.0f) * 0.5f, (((-cosTheta) - sinTheta) + 1.0f) * 0.5f);
            Point x1 = new Point((sinTheta + cosTheta + 1.0f) * 0.5f, ((cosTheta - sinTheta) + 1.0f) * 0.5f);
            Point x2 = new Point((((-sinTheta) - cosTheta) + 1.0f) * 0.5f, ((-cosTheta) + sinTheta + 1.0f) * 0.5f);
            Point x3 = new Point(((sinTheta - cosTheta) + 1.0f) * 0.5f, (cosTheta + sinTheta + 1.0f) * 0.5f);
            Quad quad = new Quad(x0, x1, x2, x3);
            ((ShaderProgram) this.mProgram).setTargetRegion(quad);
            return;
        }
        throw new RuntimeException("degree has to be multiply of 90.");
    }
}
