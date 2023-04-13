package android.filterfw;

import android.content.Context;
import android.filterfw.core.AsyncRunner;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.RoundRobinScheduler;
import android.filterfw.core.SyncRunner;
import android.filterfw.p003io.GraphIOException;
import android.filterfw.p003io.GraphReader;
import android.filterfw.p003io.TextGraphReader;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class GraphEnvironment extends MffEnvironment {
    public static final int MODE_ASYNCHRONOUS = 1;
    public static final int MODE_SYNCHRONOUS = 2;
    private GraphReader mGraphReader;
    private ArrayList<GraphHandle> mGraphs;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GraphHandle {
        private AsyncRunner mAsyncRunner;
        private FilterGraph mGraph;
        private SyncRunner mSyncRunner;

        public GraphHandle(FilterGraph graph) {
            this.mGraph = graph;
        }

        public FilterGraph getGraph() {
            return this.mGraph;
        }

        public AsyncRunner getAsyncRunner(FilterContext environment) {
            if (this.mAsyncRunner == null) {
                AsyncRunner asyncRunner = new AsyncRunner(environment, RoundRobinScheduler.class);
                this.mAsyncRunner = asyncRunner;
                asyncRunner.setGraph(this.mGraph);
            }
            return this.mAsyncRunner;
        }

        public GraphRunner getSyncRunner(FilterContext environment) {
            if (this.mSyncRunner == null) {
                this.mSyncRunner = new SyncRunner(environment, this.mGraph, RoundRobinScheduler.class);
            }
            return this.mSyncRunner;
        }
    }

    public GraphEnvironment() {
        super(null);
        this.mGraphs = new ArrayList<>();
    }

    public GraphEnvironment(FrameManager frameManager, GraphReader reader) {
        super(frameManager);
        this.mGraphs = new ArrayList<>();
        this.mGraphReader = reader;
    }

    public GraphReader getGraphReader() {
        if (this.mGraphReader == null) {
            this.mGraphReader = new TextGraphReader();
        }
        return this.mGraphReader;
    }

    public void addReferences(Object... references) {
        getGraphReader().addReferencesByKeysAndValues(references);
    }

    public int loadGraph(Context context, int resourceId) {
        try {
            FilterGraph graph = getGraphReader().readGraphResource(context, resourceId);
            return addGraph(graph);
        } catch (GraphIOException e) {
            throw new RuntimeException("Could not read graph: " + e.getMessage());
        }
    }

    public int addGraph(FilterGraph graph) {
        GraphHandle graphHandle = new GraphHandle(graph);
        this.mGraphs.add(graphHandle);
        return this.mGraphs.size() - 1;
    }

    public FilterGraph getGraph(int graphId) {
        if (graphId < 0 || graphId >= this.mGraphs.size()) {
            throw new IllegalArgumentException("Invalid graph ID " + graphId + " specified in runGraph()!");
        }
        return this.mGraphs.get(graphId).getGraph();
    }

    public GraphRunner getRunner(int graphId, int executionMode) {
        switch (executionMode) {
            case 1:
                return this.mGraphs.get(graphId).getAsyncRunner(getContext());
            case 2:
                return this.mGraphs.get(graphId).getSyncRunner(getContext());
            default:
                throw new RuntimeException("Invalid execution mode " + executionMode + " specified in getRunner()!");
        }
    }
}
