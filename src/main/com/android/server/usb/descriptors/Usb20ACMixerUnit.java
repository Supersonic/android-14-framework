package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public final class Usb20ACMixerUnit extends UsbACMixerUnit implements UsbAudioChannelCluster {
    public int mChanConfig;
    public byte mChanNames;
    public byte[] mControls;
    public byte mControlsMask;
    public byte mNameID;

    public Usb20ACMixerUnit(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    @Override // com.android.server.usb.descriptors.UsbAudioChannelCluster
    public byte getChannelCount() {
        return this.mNumOutputs;
    }

    @Override // com.android.server.usb.descriptors.UsbACMixerUnit, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        this.mChanConfig = byteStream.unpackUsbInt();
        this.mChanNames = byteStream.getByte();
        int calcControlArraySize = UsbACMixerUnit.calcControlArraySize(this.mNumInputs, this.mNumOutputs);
        this.mControls = new byte[calcControlArraySize];
        for (int i = 0; i < calcControlArraySize; i++) {
            this.mControls[i] = byteStream.getByte();
        }
        this.mControlsMask = byteStream.getByte();
        this.mNameID = byteStream.getByte();
        return this.mLength;
    }
}
