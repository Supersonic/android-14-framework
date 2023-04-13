package android.filterfw.core;
/* loaded from: classes.dex */
public class SimpleFrameManager extends FrameManager {
    @Override // android.filterfw.core.FrameManager
    public Frame newFrame(FrameFormat format) {
        return createNewFrame(format);
    }

    @Override // android.filterfw.core.FrameManager
    public Frame newBoundFrame(FrameFormat format, int bindingType, long bindingId) {
        switch (format.getTarget()) {
            case 3:
                GLFrame glFrame = new GLFrame(format, this, bindingType, bindingId);
                glFrame.init(getGLEnvironment());
                return glFrame;
            default:
                throw new RuntimeException("Attached frames are not supported for target type: " + FrameFormat.targetToString(format.getTarget()) + "!");
        }
    }

    private Frame createNewFrame(FrameFormat format) {
        switch (format.getTarget()) {
            case 1:
                Frame result = new SimpleFrame(format, this);
                return result;
            case 2:
                Frame result2 = new NativeFrame(format, this);
                return result2;
            case 3:
                GLFrame glFrame = new GLFrame(format, this);
                glFrame.init(getGLEnvironment());
                return glFrame;
            case 4:
                Frame result3 = new VertexFrame(format, this);
                return result3;
            default:
                throw new RuntimeException("Unsupported frame target type: " + FrameFormat.targetToString(format.getTarget()) + "!");
        }
    }

    @Override // android.filterfw.core.FrameManager
    public Frame retainFrame(Frame frame) {
        frame.incRefCount();
        return frame;
    }

    @Override // android.filterfw.core.FrameManager
    public Frame releaseFrame(Frame frame) {
        int refCount = frame.decRefCount();
        if (refCount == 0 && frame.hasNativeAllocation()) {
            frame.releaseNativeAllocation();
            return null;
        } else if (refCount < 0) {
            throw new RuntimeException("Frame reference count dropped below 0!");
        } else {
            return frame;
        }
    }
}
