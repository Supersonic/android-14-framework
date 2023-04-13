package android.media;

import android.media.SubtitleTrack;
/* compiled from: TtmlRenderer.java */
/* loaded from: classes2.dex */
class TtmlCue extends SubtitleTrack.Cue {
    public String mText;
    public String mTtmlFragment;

    public TtmlCue(long startTimeMs, long endTimeMs, String text, String ttmlFragment) {
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
        this.mText = text;
        this.mTtmlFragment = ttmlFragment;
    }
}
