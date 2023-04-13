package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
/* loaded from: classes.dex */
public class BitmapSource extends Filter {
    @GenerateFieldPort(name = "bitmap")
    private Bitmap mBitmap;
    private Frame mImageFrame;
    @GenerateFieldPort(hasDefault = true, name = "recycleBitmap")
    private boolean mRecycleBitmap;
    @GenerateFieldPort(hasDefault = true, name = "repeatFrame")
    boolean mRepeatFrame;
    private int mTarget;
    @GenerateFieldPort(name = "target")
    String mTargetString;

    public BitmapSource(String name) {
        super(name);
        this.mRecycleBitmap = true;
        this.mRepeatFrame = false;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        FrameFormat outputFormat = ImageFormat.create(3, 0);
        addOutputPort(SliceItem.FORMAT_IMAGE, outputFormat);
    }

    public void loadImage(FilterContext filterContext) {
        this.mTarget = FrameFormat.readTargetString(this.mTargetString);
        FrameFormat outputFormat = ImageFormat.create(this.mBitmap.getWidth(), this.mBitmap.getHeight(), 3, this.mTarget);
        Frame newFrame = filterContext.getFrameManager().newFrame(outputFormat);
        this.mImageFrame = newFrame;
        newFrame.setBitmap(this.mBitmap);
        this.mImageFrame.setTimestamp(-1L);
        if (this.mRecycleBitmap) {
            this.mBitmap.recycle();
        }
        this.mBitmap = null;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        Frame frame;
        if ((name.equals("bitmap") || name.equals("target")) && (frame = this.mImageFrame) != null) {
            frame.release();
            this.mImageFrame = null;
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mImageFrame == null) {
            loadImage(context);
        }
        pushOutput(SliceItem.FORMAT_IMAGE, this.mImageFrame);
        if (!this.mRepeatFrame) {
            closeOutputPort(SliceItem.FORMAT_IMAGE);
        }
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext env) {
        Frame frame = this.mImageFrame;
        if (frame != null) {
            frame.release();
            this.mImageFrame = null;
        }
    }
}
