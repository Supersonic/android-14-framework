package android.util;

import android.p008os.SystemClock;
import java.util.ArrayList;
@Deprecated
/* loaded from: classes3.dex */
public class TimingLogger {
    private boolean mDisabled;
    private String mLabel;
    ArrayList<String> mSplitLabels;
    ArrayList<Long> mSplits;
    private String mTag;

    public TimingLogger(String tag, String label) {
        reset(tag, label);
    }

    public void reset(String tag, String label) {
        this.mTag = tag;
        this.mLabel = label;
        reset();
    }

    public void reset() {
        boolean z = !Log.isLoggable(this.mTag, 2);
        this.mDisabled = z;
        if (z) {
            return;
        }
        ArrayList<Long> arrayList = this.mSplits;
        if (arrayList == null) {
            this.mSplits = new ArrayList<>();
            this.mSplitLabels = new ArrayList<>();
        } else {
            arrayList.clear();
            this.mSplitLabels.clear();
        }
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        if (this.mDisabled) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        this.mSplits.add(Long.valueOf(now));
        this.mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        if (this.mDisabled) {
            return;
        }
        Log.m112d(this.mTag, this.mLabel + ": begin");
        long first = this.mSplits.get(0).longValue();
        long now = first;
        for (int i = 1; i < this.mSplits.size(); i++) {
            now = this.mSplits.get(i).longValue();
            String splitLabel = this.mSplitLabels.get(i);
            long prev = this.mSplits.get(i - 1).longValue();
            Log.m112d(this.mTag, this.mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
        }
        Log.m112d(this.mTag, this.mLabel + ": end, " + (now - first) + " ms");
    }
}
