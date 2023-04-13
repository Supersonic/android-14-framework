package android.media;

import android.media.SubtitleTrack;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
/* compiled from: WebVttRenderer.java */
/* loaded from: classes2.dex */
class WebVttTrack extends SubtitleTrack implements WebVttCueListener {
    private static final String TAG = "WebVttTrack";
    private Long mCurrentRunID;
    private final UnstyledTextExtractor mExtractor;
    private final WebVttParser mParser;
    private final Map<String, TextTrackRegion> mRegions;
    private final WebVttRenderingWidget mRenderingWidget;
    private final Vector<Long> mTimestamps;
    private final Tokenizer mTokenizer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebVttTrack(WebVttRenderingWidget renderingWidget, MediaFormat format) {
        super(format);
        this.mParser = new WebVttParser(this);
        UnstyledTextExtractor unstyledTextExtractor = new UnstyledTextExtractor();
        this.mExtractor = unstyledTextExtractor;
        this.mTokenizer = new Tokenizer(unstyledTextExtractor);
        this.mTimestamps = new Vector<>();
        this.mRegions = new HashMap();
        this.mRenderingWidget = renderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public WebVttRenderingWidget getRenderingWidget() {
        return this.mRenderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public void onData(byte[] data, boolean eos, long runID) {
        try {
            String str = new String(data, "UTF-8");
            synchronized (this.mParser) {
                Long l = this.mCurrentRunID;
                if (l != null && runID != l.longValue()) {
                    throw new IllegalStateException("Run #" + this.mCurrentRunID + " in progress.  Cannot process run #" + runID);
                }
                this.mCurrentRunID = Long.valueOf(runID);
                this.mParser.parse(str);
                if (eos) {
                    finishedRun(runID);
                    this.mParser.eos();
                    this.mRegions.clear();
                    this.mCurrentRunID = null;
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.m104w(TAG, "subtitle data is not UTF-8 encoded: " + e);
        }
    }

    @Override // android.media.WebVttCueListener
    public void onCueParsed(TextTrackCue cue) {
        String[] strArr;
        TextTrackCueSpan[][] textTrackCueSpanArr;
        synchronized (this.mParser) {
            if (cue.mRegionId.length() != 0) {
                cue.mRegion = this.mRegions.get(cue.mRegionId);
            }
            if (this.DEBUG) {
                Log.m106v(TAG, "adding cue " + cue);
            }
            this.mTokenizer.reset();
            for (String s : cue.mStrings) {
                this.mTokenizer.tokenize(s);
            }
            cue.mLines = this.mExtractor.getText();
            if (this.DEBUG) {
                Log.m106v(TAG, cue.appendLinesToBuilder(cue.appendStringsToBuilder(new StringBuilder()).append(" simplified to: ")).toString());
            }
            for (TextTrackCueSpan[] line : cue.mLines) {
                for (TextTrackCueSpan span : line) {
                    if (span.mTimestampMs > cue.mStartTimeMs && span.mTimestampMs < cue.mEndTimeMs && !this.mTimestamps.contains(Long.valueOf(span.mTimestampMs))) {
                        this.mTimestamps.add(Long.valueOf(span.mTimestampMs));
                    }
                }
            }
            if (this.mTimestamps.size() > 0) {
                cue.mInnerTimesMs = new long[this.mTimestamps.size()];
                for (int ix = 0; ix < this.mTimestamps.size(); ix++) {
                    cue.mInnerTimesMs[ix] = this.mTimestamps.get(ix).longValue();
                }
                this.mTimestamps.clear();
            } else {
                cue.mInnerTimesMs = null;
            }
            cue.mRunID = this.mCurrentRunID.longValue();
        }
        addCue(cue);
    }

    @Override // android.media.WebVttCueListener
    public void onRegionParsed(TextTrackRegion region) {
        synchronized (this.mParser) {
            this.mRegions.put(region.mId, region);
        }
    }

    @Override // android.media.SubtitleTrack
    public void updateView(Vector<SubtitleTrack.Cue> activeCues) {
        if (!this.mVisible) {
            return;
        }
        if (this.DEBUG && this.mTimeProvider != null) {
            try {
                Log.m112d(TAG, "at " + (this.mTimeProvider.getCurrentTimeUs(false, true) / 1000) + " ms the active cues are:");
            } catch (IllegalStateException e) {
                Log.m112d(TAG, "at (illegal state) the active cues are:");
            }
        }
        WebVttRenderingWidget webVttRenderingWidget = this.mRenderingWidget;
        if (webVttRenderingWidget != null) {
            webVttRenderingWidget.setActiveCues(activeCues);
        }
    }
}
