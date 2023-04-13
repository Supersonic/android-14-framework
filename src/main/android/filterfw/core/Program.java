package android.filterfw.core;
/* loaded from: classes.dex */
public abstract class Program {
    public abstract Object getHostValue(String str);

    public abstract void process(Frame[] frameArr, Frame frame);

    public abstract void setHostValue(String str, Object obj);

    public void process(Frame input, Frame output) {
        Frame[] inputs = {input};
        process(inputs, output);
    }

    public void reset() {
    }
}
