package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb10ASFormatII extends UsbASFormat {
    public int mMaxBitRate;
    public byte mSamFreqType;
    public int[] mSampleRates;
    public int mSamplesPerFrame;

    public Usb10ASFormatII(int i, byte b, byte b2, byte b3, int i2) {
        super(i, b, b2, b3, i2);
    }

    public int getMaxBitRate() {
        return this.mMaxBitRate;
    }

    public byte getSamFreqType() {
        return this.mSamFreqType;
    }

    public int[] getSampleRates() {
        return this.mSampleRates;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mMaxBitRate = byteStream.unpackUsbShort();
        this.mSamplesPerFrame = byteStream.unpackUsbShort();
        byte b = byteStream.getByte();
        this.mSamFreqType = b;
        int i = b;
        if (b == 0) {
            i = 2;
        }
        this.mSampleRates = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mSampleRates[i2] = byteStream.unpackUsbTriple();
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbASFormat, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Max Bit Rate: " + getMaxBitRate());
        reportCanvas.writeListItem("Samples Per Frame: " + getMaxBitRate());
        byte samFreqType = getSamFreqType();
        int[] sampleRates = getSampleRates();
        reportCanvas.writeListItem("Sample Freq Type: " + ((int) samFreqType));
        reportCanvas.openList();
        if (samFreqType == 0) {
            reportCanvas.writeListItem("min: " + sampleRates[0]);
            reportCanvas.writeListItem("max: " + sampleRates[1]);
        } else {
            for (int i = 0; i < samFreqType; i++) {
                reportCanvas.writeListItem("" + sampleRates[i]);
            }
        }
        reportCanvas.closeList();
        reportCanvas.closeList();
    }
}
