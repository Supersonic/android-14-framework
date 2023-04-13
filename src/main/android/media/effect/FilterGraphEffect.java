package android.media.effect;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.SyncRunner;
import android.filterfw.p003io.GraphIOException;
import android.filterfw.p003io.GraphReader;
import android.filterfw.p003io.TextGraphReader;
/* loaded from: classes2.dex */
public class FilterGraphEffect extends FilterEffect {
    private static final String TAG = "FilterGraphEffect";
    protected FilterGraph mGraph;
    protected String mInputName;
    protected String mOutputName;
    protected GraphRunner mRunner;
    protected Class mSchedulerClass;

    public FilterGraphEffect(EffectContext context, String name, String graphString, String inputName, String outputName, Class scheduler) {
        super(context, name);
        this.mInputName = inputName;
        this.mOutputName = outputName;
        this.mSchedulerClass = scheduler;
        createGraph(graphString);
    }

    private void createGraph(String graphString) {
        GraphReader reader = new TextGraphReader();
        try {
            FilterGraph readGraphString = reader.readGraphString(graphString);
            this.mGraph = readGraphString;
            if (readGraphString == null) {
                throw new RuntimeException("Could not setup effect");
            }
            this.mRunner = new SyncRunner(getFilterContext(), this.mGraph, this.mSchedulerClass);
        } catch (GraphIOException e) {
            throw new RuntimeException("Could not setup effect", e);
        }
    }

    @Override // android.media.effect.Effect
    public void apply(int inputTexId, int width, int height, int outputTexId) {
        beginGLEffect();
        Filter src = this.mGraph.getFilter(this.mInputName);
        if (src != null) {
            src.setInputValue("texId", Integer.valueOf(inputTexId));
            src.setInputValue("width", Integer.valueOf(width));
            src.setInputValue("height", Integer.valueOf(height));
            Filter dest = this.mGraph.getFilter(this.mOutputName);
            if (dest != null) {
                dest.setInputValue("texId", Integer.valueOf(outputTexId));
                try {
                    this.mRunner.run();
                    endGLEffect();
                    return;
                } catch (RuntimeException e) {
                    throw new RuntimeException("Internal error applying effect: ", e);
                }
            }
            throw new RuntimeException("Internal error applying effect");
        }
        throw new RuntimeException("Internal error applying effect");
    }

    @Override // android.media.effect.Effect
    public void setParameter(String parameterKey, Object value) {
    }

    @Override // android.media.effect.Effect
    public void release() {
        this.mGraph.tearDown(getFilterContext());
        this.mGraph = null;
    }
}
