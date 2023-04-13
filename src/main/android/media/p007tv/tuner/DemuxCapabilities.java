package android.media.p007tv.tuner;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.DemuxCapabilities */
/* loaded from: classes2.dex */
public class DemuxCapabilities {
    private final int mAudioFilterCount;
    private final int mDemuxCount;
    private final int mFilterCaps;
    private final int[] mFilterCapsList;
    private final int[] mLinkCaps;
    private final int mPcrFilterCount;
    private final int mPesFilterCount;
    private final int mPlaybackCount;
    private final int mRecordCount;
    private final int mSectionFilterCount;
    private final long mSectionFilterLength;
    private final boolean mSupportTimeFilter;
    private final int mTsFilterCount;
    private final int mVideoFilterCount;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.DemuxCapabilities$FilterCapabilities */
    /* loaded from: classes2.dex */
    public @interface FilterCapabilities {
    }

    private DemuxCapabilities(int demuxCount, int recordCount, int playbackCount, int tsFilterCount, int sectionFilterCount, int audioFilterCount, int videoFilterCount, int pesFilterCount, int pcrFilterCount, long sectionFilterLength, int filterCaps, int[] filterCapsList, int[] linkCaps, boolean timeFilter) {
        this.mDemuxCount = demuxCount;
        this.mRecordCount = recordCount;
        this.mPlaybackCount = playbackCount;
        this.mTsFilterCount = tsFilterCount;
        this.mSectionFilterCount = sectionFilterCount;
        this.mAudioFilterCount = audioFilterCount;
        this.mVideoFilterCount = videoFilterCount;
        this.mPesFilterCount = pesFilterCount;
        this.mPcrFilterCount = pcrFilterCount;
        this.mSectionFilterLength = sectionFilterLength;
        this.mFilterCaps = filterCaps;
        this.mFilterCapsList = filterCapsList;
        this.mLinkCaps = linkCaps;
        this.mSupportTimeFilter = timeFilter;
    }

    public int getDemuxCount() {
        return this.mDemuxCount;
    }

    public int getRecordCount() {
        return this.mRecordCount;
    }

    public int getPlaybackCount() {
        return this.mPlaybackCount;
    }

    public int getTsFilterCount() {
        return this.mTsFilterCount;
    }

    public int getSectionFilterCount() {
        return this.mSectionFilterCount;
    }

    public int getAudioFilterCount() {
        return this.mAudioFilterCount;
    }

    public int getVideoFilterCount() {
        return this.mVideoFilterCount;
    }

    public int getPesFilterCount() {
        return this.mPesFilterCount;
    }

    public int getPcrFilterCount() {
        return this.mPcrFilterCount;
    }

    public long getSectionFilterLength() {
        return this.mSectionFilterLength;
    }

    public int getFilterCapabilities() {
        return this.mFilterCaps;
    }

    public int[] getFilterTypeCapabilityList() {
        return this.mFilterCapsList;
    }

    public int[] getLinkCapabilities() {
        return this.mLinkCaps;
    }

    public boolean isTimeFilterSupported() {
        return this.mSupportTimeFilter;
    }
}
