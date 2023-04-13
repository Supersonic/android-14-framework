package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.AudioPresentation;
import android.media.MediaCodec;
import java.util.Collections;
import java.util.List;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.MediaEvent */
/* loaded from: classes2.dex */
public class MediaEvent extends FilterEvent {
    private final List<AudioPresentation> mAudioPresentations;
    private final long mDataId;
    private final long mDataLength;
    private final long mDts;
    private final AudioDescriptor mExtraMetaData;
    private final boolean mIsDtsPresent;
    private final boolean mIsPrivateData;
    private final boolean mIsPtsPresent;
    private final boolean mIsSecureMemory;
    private MediaCodec.LinearBlock mLinearBlock;
    private final int mMpuSequenceNumber;
    private long mNativeContext;
    private final long mOffset;
    private final long mPts;
    private final int mScIndexMask;
    private final int mStreamId;
    private boolean mReleased = false;
    private final Object mLock = new Object();

    private native void nativeFinalize();

    private native Long nativeGetAudioHandle();

    private native MediaCodec.LinearBlock nativeGetLinearBlock();

    private MediaEvent(int streamId, boolean isPtsPresent, long pts, boolean isDtsPresent, long dts, long dataLength, long offset, MediaCodec.LinearBlock buffer, boolean isSecureMemory, long dataId, int mpuSequenceNumber, boolean isPrivateData, int scIndexMask, AudioDescriptor extraMetaData, List<AudioPresentation> audioPresentations) {
        this.mStreamId = streamId;
        this.mIsPtsPresent = isPtsPresent;
        this.mPts = pts;
        this.mIsDtsPresent = isDtsPresent;
        this.mDts = dts;
        this.mDataLength = dataLength;
        this.mOffset = offset;
        this.mLinearBlock = buffer;
        this.mIsSecureMemory = isSecureMemory;
        this.mDataId = dataId;
        this.mMpuSequenceNumber = mpuSequenceNumber;
        this.mIsPrivateData = isPrivateData;
        this.mScIndexMask = scIndexMask;
        this.mExtraMetaData = extraMetaData;
        this.mAudioPresentations = audioPresentations;
    }

    public int getStreamId() {
        return this.mStreamId;
    }

    public boolean isPtsPresent() {
        return this.mIsPtsPresent;
    }

    public long getPts() {
        return this.mPts;
    }

    public boolean isDtsPresent() {
        return this.mIsDtsPresent;
    }

    public long getDts() {
        return this.mDts;
    }

    public long getDataLength() {
        return this.mDataLength;
    }

    public long getOffset() {
        return this.mOffset;
    }

    public MediaCodec.LinearBlock getLinearBlock() {
        MediaCodec.LinearBlock linearBlock;
        synchronized (this.mLock) {
            if (this.mLinearBlock == null) {
                this.mLinearBlock = nativeGetLinearBlock();
            }
            linearBlock = this.mLinearBlock;
        }
        return linearBlock;
    }

    public boolean isSecureMemory() {
        return this.mIsSecureMemory;
    }

    public long getAvDataId() {
        return this.mDataId;
    }

    public long getAudioHandle() {
        nativeGetAudioHandle();
        return this.mDataId;
    }

    public int getMpuSequenceNumber() {
        return this.mMpuSequenceNumber;
    }

    public boolean isPrivateData() {
        return this.mIsPrivateData;
    }

    public int getScIndexMask() {
        return this.mScIndexMask;
    }

    public AudioDescriptor getExtraMetaData() {
        return this.mExtraMetaData;
    }

    public List<AudioPresentation> getAudioPresentations() {
        List<AudioPresentation> list = this.mAudioPresentations;
        return list == null ? Collections.emptyList() : list;
    }

    protected void finalize() {
        release();
    }

    public void release() {
        synchronized (this.mLock) {
            if (this.mReleased) {
                return;
            }
            nativeFinalize();
            this.mNativeContext = 0L;
            this.mReleased = true;
        }
    }
}
