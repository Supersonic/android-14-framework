package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public class UsbACAudioControlEndpoint extends UsbACEndpoint {
    public byte mAddress;
    public byte mAttribs;
    public byte mInterval;
    public int mMaxPacketSize;

    public UsbACAudioControlEndpoint(int i, byte b, int i2, byte b2) {
        super(i, b, i2, b2);
    }

    @Override // com.android.server.usb.descriptors.UsbACEndpoint, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        this.mAddress = byteStream.getByte();
        this.mAttribs = byteStream.getByte();
        this.mMaxPacketSize = byteStream.unpackUsbShort();
        this.mInterval = byteStream.getByte();
        return this.mLength;
    }
}
