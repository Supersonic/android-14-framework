package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.format.ObjectFormat;
import java.util.Locale;
/* loaded from: classes.dex */
public class ToUpperCase extends Filter {
    private FrameFormat mOutputFormat;

    public ToUpperCase(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        MutableFrameFormat fromClass = ObjectFormat.fromClass(String.class, 1);
        this.mOutputFormat = fromClass;
        addMaskedInputPort("mixedcase", fromClass);
        addOutputPort("uppercase", this.mOutputFormat);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame input = pullInput("mixedcase");
        String inputString = (String) input.getObjectValue();
        Frame output = env.getFrameManager().newFrame(this.mOutputFormat);
        output.setObjectValue(inputString.toUpperCase(Locale.getDefault()));
        pushOutput("uppercase", output);
    }
}
