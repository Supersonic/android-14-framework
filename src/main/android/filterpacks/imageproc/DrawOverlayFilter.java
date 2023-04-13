package android.filterpacks.imageproc;

import android.app.slice.Slice;
import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.format.ObjectFormat;
import android.filterfw.geometry.Quad;
/* loaded from: classes.dex */
public class DrawOverlayFilter extends Filter {
    private ShaderProgram mProgram;

    public DrawOverlayFilter(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        FrameFormat imageFormatMask = ImageFormat.create(3, 3);
        addMaskedInputPort(Slice.SUBTYPE_SOURCE, imageFormatMask);
        addMaskedInputPort("overlay", imageFormatMask);
        addMaskedInputPort("box", ObjectFormat.fromClass(Quad.class, 1));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, Slice.SUBTYPE_SOURCE);
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        this.mProgram = ShaderProgram.createIdentity(context);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame sourceFrame = pullInput(Slice.SUBTYPE_SOURCE);
        Frame overlayFrame = pullInput("overlay");
        Frame boxFrame = pullInput("box");
        Quad box = (Quad) boxFrame.getObjectValue();
        this.mProgram.setTargetRegion(box.translated(1.0f, 1.0f).scaled(2.0f));
        Frame output = env.getFrameManager().newFrame(sourceFrame.getFormat());
        output.setDataFromFrame(sourceFrame);
        this.mProgram.process(overlayFrame, output);
        pushOutput(SliceItem.FORMAT_IMAGE, output);
        output.release();
    }
}
