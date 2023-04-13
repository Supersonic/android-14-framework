package com.android.server.hdmi;
/* loaded from: classes.dex */
public class SetAudioVolumeLevelMessage extends HdmiCecMessage {
    public final int mAudioVolumeLevel;

    public SetAudioVolumeLevelMessage(int i, int i2, byte[] bArr, int i3) {
        super(i, i2, 115, bArr, 0);
        this.mAudioVolumeLevel = i3;
    }

    public static HdmiCecMessage build(int i, int i2, int i3) {
        byte[] bArr = {(byte) (i3 & 255)};
        int validateAddress = validateAddress(i, i2);
        if (validateAddress == 0) {
            return new SetAudioVolumeLevelMessage(i, i2, bArr, i3);
        }
        return new HdmiCecMessage(i, i2, 115, bArr, validateAddress);
    }

    public static HdmiCecMessage build(int i, int i2, byte[] bArr) {
        if (bArr.length == 0) {
            return new HdmiCecMessage(i, i2, 115, bArr, 4);
        }
        byte b = bArr[0];
        int validateAddress = validateAddress(i, i2);
        if (validateAddress == 0) {
            return new SetAudioVolumeLevelMessage(i, i2, bArr, b);
        }
        return new HdmiCecMessage(i, i2, 115, bArr, validateAddress);
    }

    public static int validateAddress(int i, int i2) {
        return HdmiCecMessageValidator.validateAddress(i, i2, 1);
    }

    public int getAudioVolumeLevel() {
        return this.mAudioVolumeLevel;
    }
}
