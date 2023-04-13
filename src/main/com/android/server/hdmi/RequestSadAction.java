package com.android.server.hdmi;

import android.util.Slog;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public final class RequestSadAction extends HdmiCecFeatureAction {
    public final RequestSadCallback mCallback;
    public final List<Integer> mCecCodecsToQuery;
    public int mQueriedSadCount;
    public final List<byte[]> mSupportedSads;
    public final int mTargetAddress;
    public int mTimeoutRetry;

    /* loaded from: classes.dex */
    public interface RequestSadCallback {
        void onRequestSadDone(List<byte[]> list);
    }

    public final boolean isValidCodec(byte b) {
        int i;
        return (b & 128) == 0 && (i = (b & 120) >> 3) > 0 && i <= 15;
    }

    public RequestSadAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, RequestSadCallback requestSadCallback) {
        super(hdmiCecLocalDevice);
        ArrayList arrayList = new ArrayList();
        this.mCecCodecsToQuery = arrayList;
        this.mSupportedSads = new ArrayList();
        this.mQueriedSadCount = 0;
        this.mTimeoutRetry = 0;
        this.mTargetAddress = i;
        Objects.requireNonNull(requestSadCallback);
        this.mCallback = requestSadCallback;
        HdmiCecConfig hdmiCecConfig = localDevice().mService.getHdmiCecConfig();
        if (hdmiCecConfig.getIntValue("query_sad_lpcm") == 1) {
            arrayList.add(1);
        }
        if (hdmiCecConfig.getIntValue("query_sad_dd") == 1) {
            arrayList.add(2);
        }
        if (hdmiCecConfig.getIntValue("query_sad_mpeg1") == 1) {
            arrayList.add(3);
        }
        if (hdmiCecConfig.getIntValue("query_sad_mp3") == 1) {
            arrayList.add(4);
        }
        if (hdmiCecConfig.getIntValue("query_sad_mpeg2") == 1) {
            arrayList.add(5);
        }
        if (hdmiCecConfig.getIntValue("query_sad_aac") == 1) {
            arrayList.add(6);
        }
        if (hdmiCecConfig.getIntValue("query_sad_dts") == 1) {
            arrayList.add(7);
        }
        if (hdmiCecConfig.getIntValue("query_sad_atrac") == 1) {
            arrayList.add(8);
        }
        if (hdmiCecConfig.getIntValue("query_sad_onebitaudio") == 1) {
            arrayList.add(9);
        }
        if (hdmiCecConfig.getIntValue("query_sad_ddp") == 1) {
            arrayList.add(10);
        }
        if (hdmiCecConfig.getIntValue("query_sad_dtshd") == 1) {
            arrayList.add(11);
        }
        if (hdmiCecConfig.getIntValue("query_sad_truehd") == 1) {
            arrayList.add(12);
        }
        if (hdmiCecConfig.getIntValue("query_sad_dst") == 1) {
            arrayList.add(13);
        }
        if (hdmiCecConfig.getIntValue("query_sad_wmapro") == 1) {
            arrayList.add(14);
        }
        if (hdmiCecConfig.getIntValue("query_sad_max") == 1) {
            arrayList.add(15);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        querySad();
        return true;
    }

    public final void querySad() {
        if (this.mQueriedSadCount >= this.mCecCodecsToQuery.size()) {
            wrapUpAndFinish();
            return;
        }
        List<Integer> list = this.mCecCodecsToQuery;
        sendCommand(HdmiCecMessageBuilder.buildRequestShortAudioDescriptor(getSourceAddress(), this.mTargetAddress, list.subList(this.mQueriedSadCount, Math.min(list.size(), this.mQueriedSadCount + 4)).stream().mapToInt(new ToIntFunction() { // from class: com.android.server.hdmi.RequestSadAction$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray()));
        this.mState = 1;
        addTimer(1, 2000);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && this.mTargetAddress == hdmiCecMessage.getSource()) {
            if (hdmiCecMessage.getOpcode() == 163) {
                if (hdmiCecMessage.getParams() != null && hdmiCecMessage.getParams().length != 0 && hdmiCecMessage.getParams().length % 3 == 0) {
                    for (int i = 0; i < hdmiCecMessage.getParams().length - 2; i += 3) {
                        if (isValidCodec(hdmiCecMessage.getParams()[i])) {
                            updateResult(new byte[]{hdmiCecMessage.getParams()[i], hdmiCecMessage.getParams()[i + 1], hdmiCecMessage.getParams()[i + 2]});
                        } else {
                            Slog.w("RequestSadAction", "Dropped invalid codec " + ((int) hdmiCecMessage.getParams()[i]) + ".");
                        }
                    }
                    this.mQueriedSadCount += 4;
                    this.mTimeoutRetry = 0;
                    querySad();
                }
                return true;
            } else if (hdmiCecMessage.getOpcode() == 0 && (hdmiCecMessage.getParams()[0] & 255) == 164) {
                if ((hdmiCecMessage.getParams()[1] & 255) == 0) {
                    wrapUpAndFinish();
                    return true;
                } else if ((hdmiCecMessage.getParams()[1] & 255) == 3) {
                    this.mQueriedSadCount += 4;
                    this.mTimeoutRetry = 0;
                    querySad();
                    return true;
                }
            }
        }
        return false;
    }

    public final void updateResult(byte[] bArr) {
        this.mSupportedSads.add(bArr);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        if (this.mState == i && i == 1) {
            int i2 = this.mTimeoutRetry + 1;
            this.mTimeoutRetry = i2;
            if (i2 <= 1) {
                querySad();
            } else {
                wrapUpAndFinish();
            }
        }
    }

    public final void wrapUpAndFinish() {
        this.mCallback.onRequestSadDone(this.mSupportedSads);
        finish();
    }
}
