package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
/* loaded from: classes.dex */
public class FrameStore extends Filter {
    @GenerateFieldPort(name = "key")
    private String mKey;

    public FrameStore(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addInputPort("frame");
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("frame");
        context.storeFrame(this.mKey, input);
    }
}
