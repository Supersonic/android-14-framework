package android.media;

import android.media.SubtitleTrack;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParserException;
/* compiled from: TtmlRenderer.java */
/* loaded from: classes2.dex */
class TtmlTrack extends SubtitleTrack implements TtmlNodeListener {
    private static final String TAG = "TtmlTrack";
    private Long mCurrentRunID;
    private final TtmlParser mParser;
    private String mParsingData;
    private final TtmlRenderingWidget mRenderingWidget;
    private TtmlNode mRootNode;
    private final TreeSet<Long> mTimeEvents;
    private final LinkedList<TtmlNode> mTtmlNodes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TtmlTrack(TtmlRenderingWidget renderingWidget, MediaFormat format) {
        super(format);
        this.mParser = new TtmlParser(this);
        this.mTtmlNodes = new LinkedList<>();
        this.mTimeEvents = new TreeSet<>();
        this.mRenderingWidget = renderingWidget;
        this.mParsingData = "";
    }

    @Override // android.media.SubtitleTrack
    public TtmlRenderingWidget getRenderingWidget() {
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
                String str2 = this.mParsingData + str;
                this.mParsingData = str2;
                if (eos) {
                    try {
                        this.mParser.parse(str2, this.mCurrentRunID.longValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e2) {
                        e2.printStackTrace();
                    }
                    finishedRun(runID);
                    this.mParsingData = "";
                    this.mCurrentRunID = null;
                }
            }
        } catch (UnsupportedEncodingException e3) {
            Log.m104w(TAG, "subtitle data is not UTF-8 encoded: " + e3);
        }
    }

    @Override // android.media.TtmlNodeListener
    public void onTtmlNodeParsed(TtmlNode node) {
        this.mTtmlNodes.addLast(node);
        addTimeEvents(node);
    }

    @Override // android.media.TtmlNodeListener
    public void onRootNodeParsed(TtmlNode node) {
        this.mRootNode = node;
        while (true) {
            TtmlCue cue = getNextResult();
            if (cue != null) {
                addCue(cue);
            } else {
                this.mRootNode = null;
                this.mTtmlNodes.clear();
                this.mTimeEvents.clear();
                return;
            }
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
        this.mRenderingWidget.setActiveCues(activeCues);
    }

    public TtmlCue getNextResult() {
        while (this.mTimeEvents.size() >= 2) {
            long start = this.mTimeEvents.pollFirst().longValue();
            long end = this.mTimeEvents.first().longValue();
            List<TtmlNode> activeCues = getActiveNodes(start, end);
            if (!activeCues.isEmpty()) {
                return new TtmlCue(start, end, TtmlUtils.applySpacePolicy(TtmlUtils.extractText(this.mRootNode, start, end), false), TtmlUtils.extractTtmlFragment(this.mRootNode, start, end));
            }
        }
        return null;
    }

    private void addTimeEvents(TtmlNode node) {
        this.mTimeEvents.add(Long.valueOf(node.mStartTimeMs));
        this.mTimeEvents.add(Long.valueOf(node.mEndTimeMs));
        for (int i = 0; i < node.mChildren.size(); i++) {
            addTimeEvents(node.mChildren.get(i));
        }
    }

    private List<TtmlNode> getActiveNodes(long startTimeUs, long endTimeUs) {
        List<TtmlNode> activeNodes = new ArrayList<>();
        for (int i = 0; i < this.mTtmlNodes.size(); i++) {
            TtmlNode node = this.mTtmlNodes.get(i);
            if (node.isActive(startTimeUs, endTimeUs)) {
                activeNodes.add(node);
            }
        }
        return activeNodes;
    }
}
