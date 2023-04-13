package android.hardware.camera2.impl;

import android.hardware.camera2.CaptureResult;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class FrameNumberTracker {
    private static final String TAG = "FrameNumberTracker";
    private long[] mCompletedFrameNumber = new long[3];
    private final LinkedList<Long>[] mPendingFrameNumbersWithOtherType = new LinkedList[3];
    private final LinkedList<Long>[] mPendingFrameNumbers = new LinkedList[3];
    private final TreeMap<Long, Integer> mFutureErrorMap = new TreeMap<>();
    private final HashMap<Long, List<CaptureResult>> mPartialResults = new HashMap<>();

    public FrameNumberTracker() {
        for (int i = 0; i < 3; i++) {
            this.mCompletedFrameNumber[i] = -1;
            this.mPendingFrameNumbersWithOtherType[i] = new LinkedList<>();
            this.mPendingFrameNumbers[i] = new LinkedList<>();
        }
    }

    private void update() {
        Iterator<Map.Entry<Long, Integer>> iter = this.mFutureErrorMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Integer> pair = iter.next();
            long errorFrameNumber = pair.getKey().longValue();
            int requestType = pair.getValue().intValue();
            Boolean removeError = false;
            if (errorFrameNumber == this.mCompletedFrameNumber[requestType] + 1) {
                removeError = true;
            }
            if (!this.mPendingFrameNumbers[requestType].isEmpty()) {
                if (errorFrameNumber == this.mPendingFrameNumbers[requestType].element().longValue()) {
                    this.mPendingFrameNumbers[requestType].remove();
                    removeError = true;
                }
            } else {
                int i = 1;
                while (true) {
                    if (i >= 3) {
                        break;
                    }
                    int otherType = (requestType + i) % 3;
                    if (this.mPendingFrameNumbersWithOtherType[otherType].isEmpty() || errorFrameNumber != this.mPendingFrameNumbersWithOtherType[otherType].element().longValue()) {
                        i++;
                    } else {
                        this.mPendingFrameNumbersWithOtherType[otherType].remove();
                        removeError = true;
                        break;
                    }
                }
            }
            if (removeError.booleanValue()) {
                this.mCompletedFrameNumber[requestType] = errorFrameNumber;
                this.mPartialResults.remove(Long.valueOf(errorFrameNumber));
                iter.remove();
            }
        }
    }

    public void updateTracker(long frameNumber, boolean isError, int requestType) {
        if (isError) {
            this.mFutureErrorMap.put(Long.valueOf(frameNumber), Integer.valueOf(requestType));
        } else {
            try {
                updateCompletedFrameNumber(frameNumber, requestType);
            } catch (IllegalArgumentException e) {
                Log.m110e(TAG, e.getMessage());
            }
        }
        update();
    }

    public void updateTracker(long frameNumber, CaptureResult result, boolean partial, int requestType) {
        if (!partial) {
            updateTracker(frameNumber, false, requestType);
        } else if (result == null) {
        } else {
            List<CaptureResult> partials = this.mPartialResults.get(Long.valueOf(frameNumber));
            if (partials == null) {
                partials = new ArrayList();
                this.mPartialResults.put(Long.valueOf(frameNumber), partials);
            }
            partials.add(result);
        }
    }

    public List<CaptureResult> popPartialResults(long frameNumber) {
        return this.mPartialResults.remove(Long.valueOf(frameNumber));
    }

    public long getCompletedFrameNumber() {
        return this.mCompletedFrameNumber[0];
    }

    public long getCompletedReprocessFrameNumber() {
        return this.mCompletedFrameNumber[1];
    }

    public long getCompletedZslStillFrameNumber() {
        return this.mCompletedFrameNumber[2];
    }

    private void updateCompletedFrameNumber(long frameNumber, int requestType) throws IllegalArgumentException {
        LinkedList<Long> srcList;
        LinkedList<Long> dstList;
        int index;
        long[] jArr = this.mCompletedFrameNumber;
        if (frameNumber <= jArr[requestType]) {
            throw new IllegalArgumentException("frame number " + frameNumber + " is a repeat");
        }
        int otherType1 = (requestType + 1) % 3;
        int otherType2 = (requestType + 2) % 3;
        long maxOtherFrameNumberSeen = Math.max(jArr[otherType1], jArr[otherType2]);
        if (frameNumber < maxOtherFrameNumberSeen) {
            if (!this.mPendingFrameNumbers[requestType].isEmpty()) {
                Long pendingFrameNumberSameType = this.mPendingFrameNumbers[requestType].element();
                if (frameNumber == pendingFrameNumberSameType.longValue()) {
                    this.mPendingFrameNumbers[requestType].remove();
                } else if (frameNumber < pendingFrameNumberSameType.longValue()) {
                    throw new IllegalArgumentException("frame number " + frameNumber + " is a repeat");
                } else {
                    throw new IllegalArgumentException("frame number " + frameNumber + " comes out of order. Expecting " + pendingFrameNumberSameType);
                }
            } else {
                int index1 = this.mPendingFrameNumbersWithOtherType[otherType1].indexOf(Long.valueOf(frameNumber));
                int index2 = this.mPendingFrameNumbersWithOtherType[otherType2].indexOf(Long.valueOf(frameNumber));
                boolean inSkippedOther1 = index1 != -1;
                boolean inSkippedOther2 = index2 != -1;
                if (!(inSkippedOther1 ^ inSkippedOther2)) {
                    throw new IllegalArgumentException("frame number " + frameNumber + " is a repeat or invalid");
                }
                if (inSkippedOther1) {
                    srcList = this.mPendingFrameNumbersWithOtherType[otherType1];
                    dstList = this.mPendingFrameNumbers[otherType2];
                    index = index1;
                } else {
                    srcList = this.mPendingFrameNumbersWithOtherType[otherType2];
                    dstList = this.mPendingFrameNumbers[otherType1];
                    index = index2;
                }
                for (int i = 0; i < index; i++) {
                    dstList.add(srcList.removeFirst());
                }
                srcList.remove();
            }
        } else {
            long i2 = Math.max(maxOtherFrameNumberSeen, this.mCompletedFrameNumber[requestType]);
            while (true) {
                i2++;
                if (i2 >= frameNumber) {
                    break;
                }
                this.mPendingFrameNumbersWithOtherType[requestType].add(Long.valueOf(i2));
            }
        }
        this.mCompletedFrameNumber[requestType] = frameNumber;
    }
}
