package android.filterfw.core;

import android.util.Log;
/* loaded from: classes.dex */
public abstract class FilterPort {
    private static final String TAG = "FilterPort";
    protected Filter mFilter;
    protected String mName;
    protected FrameFormat mPortFormat;
    protected boolean mIsBlocking = true;
    protected boolean mIsOpen = false;
    protected boolean mChecksType = false;
    private boolean mLogVerbose = Log.isLoggable(TAG, 2);

    public abstract void clear();

    public abstract boolean filterMustClose();

    public abstract boolean hasFrame();

    public abstract boolean isReady();

    public abstract Frame pullFrame();

    public abstract void pushFrame(Frame frame);

    public abstract void setFrame(Frame frame);

    public FilterPort(Filter filter, String name) {
        this.mName = name;
        this.mFilter = filter;
    }

    public boolean isAttached() {
        return this.mFilter != null;
    }

    public FrameFormat getPortFormat() {
        return this.mPortFormat;
    }

    public void setPortFormat(FrameFormat format) {
        this.mPortFormat = format;
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public String getName() {
        return this.mName;
    }

    public void setBlocking(boolean blocking) {
        this.mIsBlocking = blocking;
    }

    public void setChecksType(boolean checksType) {
        this.mChecksType = checksType;
    }

    public void open() {
        if (!this.mIsOpen && this.mLogVerbose) {
            Log.m106v(TAG, "Opening " + this);
        }
        this.mIsOpen = true;
    }

    public void close() {
        if (this.mIsOpen && this.mLogVerbose) {
            Log.m106v(TAG, "Closing " + this);
        }
        this.mIsOpen = false;
    }

    public boolean isOpen() {
        return this.mIsOpen;
    }

    public boolean isBlocking() {
        return this.mIsBlocking;
    }

    public String toString() {
        return "port '" + this.mName + "' of " + this.mFilter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertPortIsOpen() {
        if (!isOpen()) {
            throw new RuntimeException("Illegal operation on closed " + this + "!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkFrameType(Frame frame, boolean forceCheck) {
        if ((this.mChecksType || forceCheck) && this.mPortFormat != null && !frame.getFormat().isCompatibleWith(this.mPortFormat)) {
            throw new RuntimeException("Frame passed to " + this + " is of incorrect type! Expected " + this.mPortFormat + " but got " + frame.getFormat());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkFrameManager(Frame frame, FilterContext context) {
        if (frame.getFrameManager() != null && frame.getFrameManager() != context.getFrameManager()) {
            throw new RuntimeException("Frame " + frame + " is managed by foreign FrameManager! ");
        }
    }
}
