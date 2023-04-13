package com.android.server.usb.descriptors.report;

import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.usb.descriptors.UsbDescriptorParser;
/* loaded from: classes2.dex */
public abstract class ReportCanvas {
    public final UsbDescriptorParser mParser;

    public abstract void closeHeader(int i);

    public abstract void closeList();

    public abstract void closeListItem();

    public abstract void openHeader(int i);

    public abstract void openList();

    public abstract void openListItem();

    public abstract void write(String str);

    public abstract void writeParagraph(String str, boolean z);

    public ReportCanvas(UsbDescriptorParser usbDescriptorParser) {
        this.mParser = usbDescriptorParser;
    }

    public UsbDescriptorParser getParser() {
        return this.mParser;
    }

    public void writeHeader(int i, String str) {
        openHeader(i);
        write(str);
        closeHeader(i);
    }

    public void writeListItem(String str) {
        openListItem();
        write(str);
        closeListItem();
    }

    public static String getHexString(byte b) {
        return "0x" + Integer.toHexString(b & 255).toUpperCase();
    }

    public static String getBCDString(int i) {
        return "" + ((i >> 8) & 15) + "." + ((i >> 4) & 15) + (i & 15);
    }

    public static String getHexString(int i) {
        int i2 = i & GnssNative.GNSS_AIDING_TYPE_ALL;
        return "0x" + Integer.toHexString(i2).toUpperCase();
    }
}
