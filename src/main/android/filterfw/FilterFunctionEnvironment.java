package android.filterfw;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterFunction;
import android.filterfw.core.FrameManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes.dex */
public class FilterFunctionEnvironment extends MffEnvironment {
    public FilterFunctionEnvironment() {
        super(null);
    }

    public FilterFunctionEnvironment(FrameManager frameManager) {
        super(frameManager);
    }

    public FilterFunction createFunction(Class filterClass, Object... parameters) {
        String filterName = "FilterFunction(" + filterClass.getSimpleName() + NavigationBarInflaterView.KEY_CODE_END;
        Filter filter = FilterFactory.sharedFactory().createFilterByClass(filterClass, filterName);
        filter.initWithAssignmentList(parameters);
        return new FilterFunction(getContext(), filter);
    }
}
