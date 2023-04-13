package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public final class UsbACSelectorUnit extends UsbACInterface {
    public byte mNameIndex;
    public byte mNumPins;
    public byte[] mSourceIDs;
    public byte mUnitID;

    public UsbACSelectorUnit(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mUnitID = byteStream.getByte();
        int i = byteStream.getByte();
        this.mNumPins = i;
        this.mSourceIDs = new byte[i];
        for (int i2 = 0; i2 < this.mNumPins; i2++) {
            this.mSourceIDs[i2] = byteStream.getByte();
        }
        this.mNameIndex = byteStream.getByte();
        return this.mLength;
    }
}
