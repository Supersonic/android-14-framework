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
public class ImageSlicer extends Filter {
    private int mInputHeight;
    private int mInputWidth;
    private Frame mOriginalFrame;
    private int mOutputHeight;
    private int mOutputWidth;
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

    public ImageSlicer(String name) {
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

    private void calcOutputFormatForInput(Frame frame) {
        this.mInputWidth = frame.getFormat().getWidth();
        int height = frame.getFormat().getHeight();
        this.mInputHeight = height;
        int i = this.mInputWidth;
        int i2 = this.mXSlices;
        int i3 = ((i + i2) - 1) / i2;
        this.mSliceWidth = i3;
        int i4 = this.mYSlices;
        int i5 = ((height + i4) - 1) / i4;
        this.mSliceHeight = i5;
        int i6 = this.mPadSize;
        this.mOutputWidth = i3 + (i6 * 2);
        this.mOutputHeight = i5 + (i6 * 2);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mSliceIndex == 0) {
            Frame pullInput = pullInput(SliceItem.FORMAT_IMAGE);
            this.mOriginalFrame = pullInput;
            calcOutputFormatForInput(pullInput);
        }
        FrameFormat inputFormat = this.mOriginalFrame.getFormat();
        MutableFrameFormat outputFormat = inputFormat.mutableCopy();
        outputFormat.setDimensions(this.mOutputWidth, this.mOutputHeight);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        if (this.mProgram == null) {
            this.mProgram = ShaderProgram.createIdentity(context);
        }
        int i = this.mSliceIndex;
        int i2 = this.mXSlices;
        int xSliceIndex = i % i2;
        int ySliceIndex = i / i2;
        int i3 = this.mPadSize;
        int i4 = this.mInputWidth;
        float x0 = ((this.mSliceWidth * xSliceIndex) - i3) / i4;
        float f = (this.mSliceHeight * ySliceIndex) - i3;
        int i5 = this.mInputHeight;
        float y0 = f / i5;
        ((ShaderProgram) this.mProgram).setSourceRect(x0, y0, this.mOutputWidth / i4, this.mOutputHeight / i5);
        this.mProgram.process(this.mOriginalFrame, output);
        int i6 = this.mSliceIndex + 1;
        this.mSliceIndex = i6;
        if (i6 == this.mXSlices * this.mYSlices) {
            this.mSliceIndex = 0;
            this.mOriginalFrame.release();
            setWaitsOnInputPort(SliceItem.FORMAT_IMAGE, true);
        } else {
            this.mOriginalFrame.retain();
            setWaitsOnInputPort(SliceItem.FORMAT_IMAGE, false);
        }
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }
}
