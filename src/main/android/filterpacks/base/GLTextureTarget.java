package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
/* loaded from: classes.dex */
public class GLTextureTarget extends Filter {
    @GenerateFieldPort(name = "texId")
    private int mTexId;

    public GLTextureTarget(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("frame", ImageFormat.create(3));
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("frame");
        FrameFormat format = ImageFormat.create(input.getFormat().getWidth(), input.getFormat().getHeight(), 3, 3);
        Frame frame = context.getFrameManager().newBoundFrame(format, 100, this.mTexId);
        frame.setDataFromFrame(input);
        frame.release();
    }
}
