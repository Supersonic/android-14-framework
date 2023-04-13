package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class ImageEncoder extends Filter {
    @GenerateFieldPort(name = "stream")
    private OutputStream mOutputStream;
    @GenerateFieldPort(hasDefault = true, name = "quality")
    private int mQuality;

    public ImageEncoder(String name) {
        super(name);
        this.mQuality = 80;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3, 0));
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame input = pullInput(SliceItem.FORMAT_IMAGE);
        Bitmap bitmap = input.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, this.mQuality, this.mOutputStream);
    }
}
