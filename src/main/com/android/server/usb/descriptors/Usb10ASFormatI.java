package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb10ASFormatI extends UsbASFormat {
    public byte mBitResolution;
    public byte mNumChannels;
    public byte mSampleFreqType;
    public int[] mSampleRates;
    public byte mSubframeSize;

    public Usb10ASFormatI(int i, byte b, byte b2, byte b3, int i2) {
        super(i, b, b2, b3, i2);
    }

    public byte getNumChannels() {
        return this.mNumChannels;
    }

    public byte getSubframeSize() {
        return this.mSubframeSize;
    }

    public byte getBitResolution() {
        return this.mBitResolution;
    }

    public byte getSampleFreqType() {
        return this.mSampleFreqType;
    }

    public int[] getSampleRates() {
        return this.mSampleRates;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mNumChannels = byteStream.getByte();
        this.mSubframeSize = byteStream.getByte();
        this.mBitResolution = byteStream.getByte();
        int i = byteStream.getByte();
        this.mSampleFreqType = i;
        if (i == 0) {
            this.mSampleRates = r0;
            int[] iArr = {byteStream.unpackUsbTriple()};
            this.mSampleRates[1] = byteStream.unpackUsbTriple();
        } else {
            this.mSampleRates = new int[i];
            for (int i2 = 0; i2 < this.mSampleFreqType; i2++) {
                this.mSampleRates[i2] = byteStream.unpackUsbTriple();
            }
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbASFormat, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("" + ((int) getNumChannels()) + " Channels.");
        StringBuilder sb = new StringBuilder();
        sb.append("Subframe Size: ");
        sb.append((int) getSubframeSize());
        reportCanvas.writeListItem(sb.toString());
        reportCanvas.writeListItem("Bit Resolution: " + ((int) getBitResolution()));
        byte sampleFreqType = getSampleFreqType();
        int[] sampleRates = getSampleRates();
        reportCanvas.writeListItem("Sample Freq Type: " + ((int) sampleFreqType));
        reportCanvas.openList();
        if (sampleFreqType == 0) {
            reportCanvas.writeListItem("min: " + sampleRates[0]);
            reportCanvas.writeListItem("max: " + sampleRates[1]);
        } else {
            for (int i = 0; i < sampleFreqType; i++) {
                reportCanvas.writeListItem("" + sampleRates[i]);
            }
        }
        reportCanvas.closeList();
        reportCanvas.closeList();
    }
}
