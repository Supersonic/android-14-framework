package com.android.server.usb.descriptors;

import android.util.Log;
import com.android.server.usb.descriptors.report.ReportCanvas;
import com.android.server.usb.descriptors.report.UsbStrings;
/* loaded from: classes2.dex */
public abstract class UsbDescriptor {
    public int mHierarchyLevel;
    public final int mLength;
    public int mOverUnderRunCount;
    public byte[] mRawData;
    public int mStatus = 0;
    public final byte mType;
    public static byte[] sStringBuffer = new byte[256];
    public static String[] sStatusStrings = {"UNPARSED", "PARSED - OK", "PARSED - UNDERRUN", "PARSED - OVERRUN"};

    public static void logDescriptorName(byte b, int i) {
    }

    public UsbDescriptor(int i, byte b) {
        if (i < 2) {
            throw new IllegalArgumentException();
        }
        this.mLength = i;
        this.mType = b;
    }

    public int getLength() {
        return this.mLength;
    }

    public byte getType() {
        return this.mType;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int i) {
        this.mStatus = i;
    }

    public int getOverUnderRunCount() {
        return this.mOverUnderRunCount;
    }

    public String getStatusString() {
        return sStatusStrings[this.mStatus];
    }

    public void postParse(ByteStream byteStream) {
        int readCount = byteStream.getReadCount();
        int i = this.mLength;
        if (readCount < i) {
            byteStream.advance(i - readCount);
            this.mStatus = 2;
            this.mOverUnderRunCount = this.mLength - readCount;
            Log.w("UsbDescriptor", "UNDERRUN t:0x" + Integer.toHexString(this.mType) + " r: " + readCount + " < l: " + this.mLength);
        } else if (readCount > i) {
            byteStream.reverse(readCount - i);
            this.mStatus = 3;
            this.mOverUnderRunCount = readCount - this.mLength;
            Log.w("UsbDescriptor", "OVERRRUN t:0x" + Integer.toHexString(this.mType) + " r: " + readCount + " > l: " + this.mLength);
        } else {
            this.mStatus = 1;
        }
    }

    public int parseRawDescriptors(ByteStream byteStream) {
        int readCount = this.mLength - byteStream.getReadCount();
        if (readCount > 0) {
            this.mRawData = new byte[readCount];
            for (int i = 0; i < readCount; i++) {
                this.mRawData[i] = byteStream.getByte();
            }
        }
        return this.mLength;
    }

    public final void reportParseStatus(ReportCanvas reportCanvas) {
        int status = getStatus();
        if (status == 0 || status == 2 || status == 3) {
            reportCanvas.writeParagraph("status: " + getStatusString() + " [" + getOverUnderRunCount() + "]", true);
        }
    }

    public void report(ReportCanvas reportCanvas) {
        String str = UsbStrings.getDescriptorName(getType()) + ": " + ReportCanvas.getHexString(getType()) + " Len: " + getLength();
        int i = this.mHierarchyLevel;
        if (i != 0) {
            reportCanvas.writeHeader(i, str);
        } else {
            reportCanvas.writeParagraph(str, false);
        }
        if (getStatus() != 1) {
            reportParseStatus(reportCanvas);
        }
    }
}
