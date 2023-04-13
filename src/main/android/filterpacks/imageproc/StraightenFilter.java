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
public class StraightenFilter extends Filter {
    private static final float DEGREE_TO_RADIAN = 0.017453292f;
    @GenerateFieldPort(hasDefault = true, name = "angle")
    private float mAngle;
    private int mHeight;
    @GenerateFieldPort(hasDefault = true, name = "maxAngle")
    private float mMaxAngle;
    private Program mProgram;
    private int mTarget;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize;
    private int mWidth;

    public StraightenFilter(String name) {
        super(name);
        this.mAngle = 0.0f;
        this.mMaxAngle = 45.0f;
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
            this.mHeight = inputFormat.getHeight();
            updateParameters();
        }
        Frame output = context.getFrameManager().newFrame(inputFormat);
        this.mProgram.process(input, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }

    private void updateParameters() {
        float cosTheta = (float) Math.cos(this.mAngle * DEGREE_TO_RADIAN);
        float sinTheta = (float) Math.sin(this.mAngle * DEGREE_TO_RADIAN);
        float f = this.mMaxAngle;
        if (f <= 0.0f) {
            throw new RuntimeException("Max angle is out of range (0-180).");
        }
        if (f > 90.0f) {
            f = 90.0f;
        }
        this.mMaxAngle = f;
        int i = this.mWidth;
        int i2 = this.mHeight;
        Point p0 = new Point(((-cosTheta) * i) + (i2 * sinTheta), ((-sinTheta) * i) - (i2 * cosTheta));
        int i3 = this.mWidth;
        int i4 = this.mHeight;
        Point p1 = new Point((i3 * cosTheta) + (i4 * sinTheta), (i3 * sinTheta) - (i4 * cosTheta));
        int i5 = this.mWidth;
        int i6 = this.mHeight;
        Point p2 = new Point(((-cosTheta) * i5) - (i6 * sinTheta), ((-sinTheta) * i5) + (i6 * cosTheta));
        int i7 = this.mWidth;
        int i8 = this.mHeight;
        Point p3 = new Point((i7 * cosTheta) - (i8 * sinTheta), (i7 * sinTheta) + (i8 * cosTheta));
        float maxWidth = Math.max(Math.abs(p0.f50x), Math.abs(p1.f50x));
        float maxHeight = Math.max(Math.abs(p0.f51y), Math.abs(p1.f51y));
        float scale = Math.min(this.mWidth / maxWidth, this.mHeight / maxHeight) * 0.5f;
        p0.set(((p0.f50x * scale) / this.mWidth) + 0.5f, ((p0.f51y * scale) / this.mHeight) + 0.5f);
        p1.set(((p1.f50x * scale) / this.mWidth) + 0.5f, ((p1.f51y * scale) / this.mHeight) + 0.5f);
        p2.set(((p2.f50x * scale) / this.mWidth) + 0.5f, ((p2.f51y * scale) / this.mHeight) + 0.5f);
        p3.set(((p3.f50x * scale) / this.mWidth) + 0.5f, ((p3.f51y * scale) / this.mHeight) + 0.5f);
        Quad quad = new Quad(p0, p1, p2, p3);
        ((ShaderProgram) this.mProgram).setSourceRegion(quad);
    }
}
