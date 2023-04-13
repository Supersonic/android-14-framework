package android.filterfw.core;
/* loaded from: classes.dex */
public abstract class Scheduler {
    private FilterGraph mGraph;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void reset();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Filter scheduleNextNode();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Scheduler(FilterGraph graph) {
        this.mGraph = graph;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilterGraph getGraph() {
        return this.mGraph;
    }

    boolean finished() {
        return true;
    }
}
