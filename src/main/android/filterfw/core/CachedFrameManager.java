package android.filterfw.core;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class CachedFrameManager extends SimpleFrameManager {
    private int mStorageCapacity = 25165824;
    private int mStorageSize = 0;
    private int mTimeStamp = 0;
    private SortedMap<Integer, Frame> mAvailableFrames = new TreeMap();

    @Override // android.filterfw.core.SimpleFrameManager, android.filterfw.core.FrameManager
    public Frame newFrame(FrameFormat format) {
        Frame result = findAvailableFrame(format, 0, 0L);
        if (result == null) {
            result = super.newFrame(format);
        }
        result.setTimestamp(-2L);
        return result;
    }

    @Override // android.filterfw.core.SimpleFrameManager, android.filterfw.core.FrameManager
    public Frame newBoundFrame(FrameFormat format, int bindingType, long bindingId) {
        Frame result = findAvailableFrame(format, bindingType, bindingId);
        if (result == null) {
            result = super.newBoundFrame(format, bindingType, bindingId);
        }
        result.setTimestamp(-2L);
        return result;
    }

    @Override // android.filterfw.core.SimpleFrameManager, android.filterfw.core.FrameManager
    public Frame retainFrame(Frame frame) {
        return super.retainFrame(frame);
    }

    @Override // android.filterfw.core.SimpleFrameManager, android.filterfw.core.FrameManager
    public Frame releaseFrame(Frame frame) {
        if (frame.isReusable()) {
            int refCount = frame.decRefCount();
            if (refCount == 0 && frame.hasNativeAllocation()) {
                if (!storeFrame(frame)) {
                    frame.releaseNativeAllocation();
                    return null;
                }
                return null;
            } else if (refCount < 0) {
                throw new RuntimeException("Frame reference count dropped below 0!");
            }
        } else {
            super.releaseFrame(frame);
        }
        return frame;
    }

    public void clearCache() {
        for (Frame frame : this.mAvailableFrames.values()) {
            frame.releaseNativeAllocation();
        }
        this.mAvailableFrames.clear();
    }

    @Override // android.filterfw.core.FrameManager
    public void tearDown() {
        clearCache();
    }

    private boolean storeFrame(Frame frame) {
        synchronized (this.mAvailableFrames) {
            int frameSize = frame.getFormat().getSize();
            if (frameSize > this.mStorageCapacity) {
                return false;
            }
            int newStorageSize = this.mStorageSize + frameSize;
            while (newStorageSize > this.mStorageCapacity) {
                dropOldestFrame();
                newStorageSize = this.mStorageSize + frameSize;
            }
            frame.onFrameStore();
            this.mStorageSize = newStorageSize;
            this.mAvailableFrames.put(Integer.valueOf(this.mTimeStamp), frame);
            this.mTimeStamp++;
            return true;
        }
    }

    private void dropOldestFrame() {
        int oldest = this.mAvailableFrames.firstKey().intValue();
        Frame frame = this.mAvailableFrames.get(Integer.valueOf(oldest));
        this.mStorageSize -= frame.getFormat().getSize();
        frame.releaseNativeAllocation();
        this.mAvailableFrames.remove(Integer.valueOf(oldest));
    }

    private Frame findAvailableFrame(FrameFormat format, int bindingType, long bindingId) {
        synchronized (this.mAvailableFrames) {
            for (Map.Entry<Integer, Frame> entry : this.mAvailableFrames.entrySet()) {
                Frame frame = entry.getValue();
                if (frame.getFormat().isReplaceableBy(format) && bindingType == frame.getBindingType() && (bindingType == 0 || bindingId == frame.getBindingId())) {
                    super.retainFrame(frame);
                    this.mAvailableFrames.remove(entry.getKey());
                    frame.onFrameFetch();
                    frame.reset(format);
                    this.mStorageSize -= format.getSize();
                    return frame;
                }
            }
            return null;
        }
    }
}
