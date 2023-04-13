package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public final class UsbInterfaceAssoc extends UsbDescriptor {
    public byte mFirstInterface;
    public byte mFunction;
    public byte mFunctionClass;
    public byte mFunctionProtocol;
    public byte mFunctionSubClass;
    public byte mInterfaceCount;

    public UsbInterfaceAssoc(int i, byte b) {
        super(i, b);
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mFirstInterface = byteStream.getByte();
        this.mInterfaceCount = byteStream.getByte();
        this.mFunctionClass = byteStream.getByte();
        this.mFunctionSubClass = byteStream.getByte();
        this.mFunctionProtocol = byteStream.getByte();
        this.mFunction = byteStream.getByte();
        return this.mLength;
    }
}
