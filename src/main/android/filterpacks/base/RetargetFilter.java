package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
/* loaded from: classes.dex */
public class RetargetFilter extends Filter {
    private MutableFrameFormat mOutputFormat;
    private int mTarget;
    @GenerateFinalPort(hasDefault = false, name = "target")
    private String mTargetString;

    public RetargetFilter(String name) {
        super(name);
        this.mTarget = -1;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        this.mTarget = FrameFormat.readTargetString(this.mTargetString);
        addInputPort("frame");
        addOutputBasedOnInput("frame", "frame");
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat retargeted = inputFormat.mutableCopy();
        retargeted.setTarget(this.mTarget);
        return retargeted;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("frame");
        Frame output = context.getFrameManager().duplicateFrameToTarget(input, this.mTarget);
        pushOutput("frame", output);
        output.release();
    }
}
