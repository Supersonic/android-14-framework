package android.filterfw.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class FilterContext {
    private FrameManager mFrameManager;
    private GLEnvironment mGLEnvironment;
    private HashMap<String, Frame> mStoredFrames = new HashMap<>();
    private Set<FilterGraph> mGraphs = new HashSet();

    /* loaded from: classes.dex */
    public interface OnFrameReceivedListener {
        void onFrameReceived(Filter filter, Frame frame, Object obj);
    }

    public FrameManager getFrameManager() {
        return this.mFrameManager;
    }

    public void setFrameManager(FrameManager manager) {
        if (manager == null) {
            throw new NullPointerException("Attempting to set null FrameManager!");
        }
        if (manager.getContext() != null) {
            throw new IllegalArgumentException("Attempting to set FrameManager which is already bound to another FilterContext!");
        }
        this.mFrameManager = manager;
        manager.setContext(this);
    }

    public GLEnvironment getGLEnvironment() {
        return this.mGLEnvironment;
    }

    public void initGLEnvironment(GLEnvironment environment) {
        if (this.mGLEnvironment == null) {
            this.mGLEnvironment = environment;
            return;
        }
        throw new RuntimeException("Attempting to re-initialize GL Environment for FilterContext!");
    }

    public synchronized void storeFrame(String key, Frame frame) {
        Frame storedFrame = fetchFrame(key);
        if (storedFrame != null) {
            storedFrame.release();
        }
        frame.onFrameStore();
        this.mStoredFrames.put(key, frame.retain());
    }

    public synchronized Frame fetchFrame(String key) {
        Frame frame;
        frame = this.mStoredFrames.get(key);
        if (frame != null) {
            frame.onFrameFetch();
        }
        return frame;
    }

    public synchronized void removeFrame(String key) {
        Frame frame = this.mStoredFrames.get(key);
        if (frame != null) {
            this.mStoredFrames.remove(key);
            frame.release();
        }
    }

    public synchronized void tearDown() {
        for (Frame frame : this.mStoredFrames.values()) {
            frame.release();
        }
        this.mStoredFrames.clear();
        for (FilterGraph graph : this.mGraphs) {
            graph.tearDown(this);
        }
        this.mGraphs.clear();
        FrameManager frameManager = this.mFrameManager;
        if (frameManager != null) {
            frameManager.tearDown();
            this.mFrameManager = null;
        }
        GLEnvironment gLEnvironment = this.mGLEnvironment;
        if (gLEnvironment != null) {
            gLEnvironment.tearDown();
            this.mGLEnvironment = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addGraph(FilterGraph graph) {
        this.mGraphs.add(graph);
    }
}
