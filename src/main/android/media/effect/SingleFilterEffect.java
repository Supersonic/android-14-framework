package android.media.effect;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterFunction;
import android.filterfw.core.Frame;
/* loaded from: classes2.dex */
public class SingleFilterEffect extends FilterEffect {
    protected FilterFunction mFunction;
    protected String mInputName;
    protected String mOutputName;

    public SingleFilterEffect(EffectContext context, String name, Class filterClass, String inputName, String outputName, Object... finalParameters) {
        super(context, name);
        this.mInputName = inputName;
        this.mOutputName = outputName;
        String filterName = filterClass.getSimpleName();
        FilterFactory factory = FilterFactory.sharedFactory();
        Filter filter = factory.createFilterByClass(filterClass, filterName);
        filter.initWithAssignmentList(finalParameters);
        this.mFunction = new FilterFunction(getFilterContext(), filter);
    }

    @Override // android.media.effect.Effect
    public void apply(int inputTexId, int width, int height, int outputTexId) {
        beginGLEffect();
        Frame inputFrame = frameFromTexture(inputTexId, width, height);
        Frame outputFrame = frameFromTexture(outputTexId, width, height);
        Frame resultFrame = this.mFunction.executeWithArgList(this.mInputName, inputFrame);
        outputFrame.setDataFromFrame(resultFrame);
        inputFrame.release();
        outputFrame.release();
        resultFrame.release();
        endGLEffect();
    }

    @Override // android.media.effect.Effect
    public void setParameter(String parameterKey, Object value) {
        this.mFunction.setInputValue(parameterKey, value);
    }

    @Override // android.media.effect.Effect
    public void release() {
        this.mFunction.tearDown();
        this.mFunction = null;
    }
}
