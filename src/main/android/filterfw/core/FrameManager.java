package android.filterfw.core;
/* loaded from: classes.dex */
public abstract class FrameManager {
    private FilterContext mContext;

    public abstract Frame newBoundFrame(FrameFormat frameFormat, int i, long j);

    public abstract Frame newFrame(FrameFormat frameFormat);

    public abstract Frame releaseFrame(Frame frame);

    public abstract Frame retainFrame(Frame frame);

    public Frame duplicateFrame(Frame frame) {
        Frame result = newFrame(frame.getFormat());
        result.setDataFromFrame(frame);
        return result;
    }

    public Frame duplicateFrameToTarget(Frame frame, int newTarget) {
        MutableFrameFormat newFormat = frame.getFormat().mutableCopy();
        newFormat.setTarget(newTarget);
        Frame result = newFrame(newFormat);
        result.setDataFromFrame(frame);
        return result;
    }

    public FilterContext getContext() {
        return this.mContext;
    }

    public GLEnvironment getGLEnvironment() {
        FilterContext filterContext = this.mContext;
        if (filterContext != null) {
            return filterContext.getGLEnvironment();
        }
        return null;
    }

    public void tearDown() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContext(FilterContext context) {
        this.mContext = context;
    }
}
