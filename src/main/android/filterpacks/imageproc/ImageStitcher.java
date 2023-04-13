package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
/* loaded from: classes.dex */
public class ImageStitcher extends Filter {
    private int mImageHeight;
    private int mImageWidth;
    private int mInputHeight;
    private int mInputWidth;
    private Frame mOutputFrame;
    @GenerateFieldPort(name = "padSize")
    private int mPadSize;
    private Program mProgram;
    private int mSliceHeight;
    private int mSliceIndex;
    private int mSliceWidth;
    @GenerateFieldPort(name = "xSlices")
    private int mXSlices;
    @GenerateFieldPort(name = "ySlices")
    private int mYSlices;

    public ImageStitcher(String name) {
        super(name);
        this.mSliceIndex = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3, 3));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    private FrameFormat calcOutputFormatForInput(FrameFormat format) {
        MutableFrameFormat outputFormat = format.mutableCopy();
        this.mInputWidth = format.getWidth();
        int height = format.getHeight();
        this.mInputHeight = height;
        int i = this.mInputWidth;
        int i2 = this.mPadSize;
        int i3 = i - (i2 * 2);
        this.mSliceWidth = i3;
        int i4 = height - (i2 * 2);
        this.mSliceHeight = i4;
        int i5 = i3 * this.mXSlices;
        this.mImageWidth = i5;
        int i6 = i4 * this.mYSlices;
        this.mImageHeight = i6;
        outputFormat.setDimensions(i5, i6);
        return outputFormat;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput(SliceItem.FORMAT_IMAGE);
        FrameFormat format = input.getFormat();
        if (this.mSliceIndex == 0) {
            this.mOutputFrame = context.getFrameManager().newFrame(calcOutputFormatForInput(format));
        } else if (format.getWidth() != this.mInputWidth || format.getHeight() != this.mInputHeight) {
            throw new RuntimeException("Image size should not change.");
        }
        if (this.mProgram == null) {
            this.mProgram = ShaderProgram.createIdentity(context);
        }
        int i = this.mPadSize;
        float x0 = i / this.mInputWidth;
        float y0 = i / this.mInputHeight;
        int i2 = this.mSliceIndex;
        int i3 = this.mXSlices;
        int i4 = this.mSliceWidth;
        int outputOffsetX = (i2 % i3) * i4;
        int outputOffsetY = (i2 / i3) * this.mSliceHeight;
        float outputWidth = Math.min(i4, this.mImageWidth - outputOffsetX);
        float outputHeight = Math.min(this.mSliceHeight, this.mImageHeight - outputOffsetY);
        ((ShaderProgram) this.mProgram).setSourceRect(x0, y0, outputWidth / this.mInputWidth, outputHeight / this.mInputHeight);
        int i5 = this.mImageWidth;
        int i6 = this.mImageHeight;
        ((ShaderProgram) this.mProgram).setTargetRect(outputOffsetX / i5, outputOffsetY / i6, outputWidth / i5, outputHeight / i6);
        this.mProgram.process(input, this.mOutputFrame);
        int i7 = this.mSliceIndex + 1;
        this.mSliceIndex = i7;
        if (i7 == this.mXSlices * this.mYSlices) {
            pushOutput(SliceItem.FORMAT_IMAGE, this.mOutputFrame);
            this.mOutputFrame.release();
            this.mSliceIndex = 0;
        }
    }
}
