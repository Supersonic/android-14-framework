package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.NativeProgram;
import android.filterfw.core.Program;
import android.filterfw.format.ImageFormat;
/* loaded from: classes.dex */
public class ToRGBAFilter extends Filter {
    private int mInputBPP;
    private FrameFormat mLastFormat;
    private Program mProgram;

    public ToRGBAFilter(String name) {
        super(name);
        this.mLastFormat = null;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        MutableFrameFormat mask = new MutableFrameFormat(2, 2);
        mask.setDimensionCount(2);
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, mask);
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return getConvertedFormat(inputFormat);
    }

    public FrameFormat getConvertedFormat(FrameFormat format) {
        MutableFrameFormat result = format.mutableCopy();
        result.setMetaValue(ImageFormat.COLORSPACE_KEY, 3);
        result.setBytesPerSample(4);
        return result;
    }

    public void createProgram(FilterContext context, FrameFormat format) {
        this.mInputBPP = format.getBytesPerSample();
        FrameFormat frameFormat = this.mLastFormat;
        if (frameFormat == null || frameFormat.getBytesPerSample() != this.mInputBPP) {
            this.mLastFormat = format;
            switch (this.mInputBPP) {
                case 1:
                    this.mProgram = new NativeProgram("filterpack_imageproc", "gray_to_rgba");
                    return;
                case 2:
                default:
                    throw new RuntimeException("Unsupported BytesPerPixel: " + this.mInputBPP + "!");
                case 3:
                    this.mProgram = new NativeProgram("filterpack_imageproc", "rgb_to_rgba");
                    return;
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput(SliceItem.FORMAT_IMAGE);
        createProgram(context, input.getFormat());
        Frame output = context.getFrameManager().newFrame(getConvertedFormat(input.getFormat()));
        this.mProgram.process(input, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }
}
