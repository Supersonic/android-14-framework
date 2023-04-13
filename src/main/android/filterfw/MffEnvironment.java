package android.filterfw;

import android.filterfw.core.CachedFrameManager;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
/* loaded from: classes.dex */
public class MffEnvironment {
    private FilterContext mContext;

    /* JADX INFO: Access modifiers changed from: protected */
    public MffEnvironment(FrameManager frameManager) {
        frameManager = frameManager == null ? new CachedFrameManager() : frameManager;
        FilterContext filterContext = new FilterContext();
        this.mContext = filterContext;
        filterContext.setFrameManager(frameManager);
    }

    public FilterContext getContext() {
        return this.mContext;
    }

    public void setGLEnvironment(GLEnvironment glEnvironment) {
        this.mContext.initGLEnvironment(glEnvironment);
    }

    public void createGLEnvironment() {
        GLEnvironment glEnvironment = new GLEnvironment();
        glEnvironment.initWithNewContext();
        setGLEnvironment(glEnvironment);
    }

    public void activateGLEnvironment() {
        GLEnvironment glEnv = this.mContext.getGLEnvironment();
        if (glEnv != null) {
            this.mContext.getGLEnvironment().activate();
            return;
        }
        throw new NullPointerException("No GLEnvironment in place to activate!");
    }

    public void deactivateGLEnvironment() {
        GLEnvironment glEnv = this.mContext.getGLEnvironment();
        if (glEnv != null) {
            this.mContext.getGLEnvironment().deactivate();
            return;
        }
        throw new NullPointerException("No GLEnvironment in place to deactivate!");
    }
}
