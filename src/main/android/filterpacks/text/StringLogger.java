package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.format.ObjectFormat;
import android.util.Log;
/* loaded from: classes.dex */
public class StringLogger extends Filter {
    public StringLogger(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("string", ObjectFormat.fromClass(Object.class, 1));
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame input = pullInput("string");
        String inputString = input.getObjectValue().toString();
        Log.m108i("StringLogger", inputString);
    }
}
