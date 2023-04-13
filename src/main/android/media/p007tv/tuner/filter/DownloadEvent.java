package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.DownloadEvent */
/* loaded from: classes2.dex */
public class DownloadEvent extends FilterEvent {
    private final int mDataLength;
    private final int mDownloadId;
    private final int mItemFragmentIndex;
    private final int mItemId;
    private final int mLastItemFragmentIndex;
    private final int mMpuSequenceNumber;

    private DownloadEvent(int itemId, int downloadId, int mpuSequenceNumber, int itemFragmentIndex, int lastItemFragmentIndex, int dataLength) {
        this.mItemId = itemId;
        this.mDownloadId = downloadId;
        this.mMpuSequenceNumber = mpuSequenceNumber;
        this.mItemFragmentIndex = itemFragmentIndex;
        this.mLastItemFragmentIndex = lastItemFragmentIndex;
        this.mDataLength = dataLength;
    }

    public int getItemId() {
        return this.mItemId;
    }

    public int getDownloadId() {
        return this.mDownloadId;
    }

    public int getMpuSequenceNumber() {
        return this.mMpuSequenceNumber;
    }

    public int getItemFragmentIndex() {
        return this.mItemFragmentIndex;
    }

    public int getLastItemFragmentIndex() {
        return this.mLastItemFragmentIndex;
    }

    public int getDataLength() {
        return this.mDataLength;
    }
}
