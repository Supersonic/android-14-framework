package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
/* loaded from: classes.dex */
public class GLTextureSource extends Filter {
    private Frame mFrame;
    @GenerateFieldPort(name = "height")
    private int mHeight;
    @GenerateFieldPort(hasDefault = true, name = "repeatFrame")
    private boolean mRepeatFrame;
    @GenerateFieldPort(name = "texId")
    private int mTexId;
    @GenerateFieldPort(hasDefault = true, name = "timestamp")
    private long mTimestamp;
    @GenerateFieldPort(name = "width")
    private int mWidth;

    public GLTextureSource(String name) {
        super(name);
        this.mRepeatFrame = false;
        this.mTimestamp = -1L;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("frame", ImageFormat.create(3, 3));
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        Frame frame = this.mFrame;
        if (frame != null) {
            frame.release();
            this.mFrame = null;
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mFrame == null) {
            FrameFormat outputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
            Frame newBoundFrame = context.getFrameManager().newBoundFrame(outputFormat, 100, this.mTexId);
            this.mFrame = newBoundFrame;
            newBoundFrame.setTimestamp(this.mTimestamp);
        }
        pushOutput("frame", this.mFrame);
        if (!this.mRepeatFrame) {
            closeOutputPort("frame");
        }
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        Frame frame = this.mFrame;
        if (frame != null) {
            frame.release();
        }
    }
}
