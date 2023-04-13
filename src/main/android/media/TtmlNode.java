package android.media;

import java.util.ArrayList;
import java.util.List;
/* compiled from: TtmlRenderer.java */
/* loaded from: classes2.dex */
class TtmlNode {
    public final String mAttributes;
    public final List<TtmlNode> mChildren = new ArrayList();
    public final long mEndTimeMs;
    public final String mName;
    public final TtmlNode mParent;
    public final long mRunId;
    public final long mStartTimeMs;
    public final String mText;

    public TtmlNode(String name, String attributes, String text, long startTimeMs, long endTimeMs, TtmlNode parent, long runId) {
        this.mName = name;
        this.mAttributes = attributes;
        this.mText = text;
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
        this.mParent = parent;
        this.mRunId = runId;
    }

    public boolean isActive(long startTimeMs, long endTimeMs) {
        return this.mEndTimeMs > startTimeMs && this.mStartTimeMs < endTimeMs;
    }
}
