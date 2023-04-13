package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public class UsbACMixerUnit extends UsbACInterface {
    public byte[] mInputIDs;
    public byte mNumInputs;
    public byte mNumOutputs;
    public byte mUnitID;

    public UsbACMixerUnit(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public byte getUnitID() {
        return this.mUnitID;
    }

    public byte getNumInputs() {
        return this.mNumInputs;
    }

    public byte[] getInputIDs() {
        return this.mInputIDs;
    }

    public byte getNumOutputs() {
        return this.mNumOutputs;
    }

    public static int calcControlArraySize(int i, int i2) {
        return ((i * i2) + 7) / 8;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mUnitID = byteStream.getByte();
        int i = byteStream.getByte();
        this.mNumInputs = i;
        this.mInputIDs = new byte[i];
        for (int i2 = 0; i2 < this.mNumInputs; i2++) {
            this.mInputIDs[i2] = byteStream.getByte();
        }
        this.mNumOutputs = byteStream.getByte();
        return this.mLength;
    }
}
