package com.android.server.usb.descriptors.report;

import com.android.server.usb.descriptors.UsbDescriptorParser;
/* loaded from: classes2.dex */
public final class TextReportCanvas extends ReportCanvas {
    public int mListIndent;
    public final StringBuilder mStringBuilder;

    public TextReportCanvas(UsbDescriptorParser usbDescriptorParser, StringBuilder sb) {
        super(usbDescriptorParser);
        this.mStringBuilder = sb;
    }

    public final void writeListIndent() {
        for (int i = 0; i < this.mListIndent; i++) {
            this.mStringBuilder.append(" ");
        }
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void write(String str) {
        this.mStringBuilder.append(str);
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void openHeader(int i) {
        writeListIndent();
        this.mStringBuilder.append("[");
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void closeHeader(int i) {
        this.mStringBuilder.append("]\n");
    }

    public void openParagraph(boolean z) {
        writeListIndent();
    }

    public void closeParagraph() {
        this.mStringBuilder.append("\n");
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void writeParagraph(String str, boolean z) {
        openParagraph(z);
        if (z) {
            StringBuilder sb = this.mStringBuilder;
            sb.append("*" + str + "*");
        } else {
            this.mStringBuilder.append(str);
        }
        closeParagraph();
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void openList() {
        this.mListIndent += 2;
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void closeList() {
        this.mListIndent -= 2;
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void openListItem() {
        writeListIndent();
        this.mStringBuilder.append("- ");
    }

    @Override // com.android.server.usb.descriptors.report.ReportCanvas
    public void closeListItem() {
        this.mStringBuilder.append("\n");
    }
}
