package android.print.pdf;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
/* loaded from: classes3.dex */
public class PrintedPdfDocument extends PdfDocument {
    private static final int MILS_PER_INCH = 1000;
    private static final int POINTS_IN_INCH = 72;
    private final Rect mContentRect;
    private final int mPageHeight;
    private final int mPageWidth;

    public PrintedPdfDocument(Context context, PrintAttributes attributes) {
        PrintAttributes.MediaSize mediaSize = attributes.getMediaSize();
        int widthMils = (int) ((mediaSize.getWidthMils() / 1000.0f) * 72.0f);
        this.mPageWidth = widthMils;
        int heightMils = (int) ((mediaSize.getHeightMils() / 1000.0f) * 72.0f);
        this.mPageHeight = heightMils;
        PrintAttributes.Margins minMargins = attributes.getMinMargins();
        int marginLeft = (int) ((minMargins.getLeftMils() / 1000.0f) * 72.0f);
        int marginTop = (int) ((minMargins.getTopMils() / 1000.0f) * 72.0f);
        int marginRight = (int) ((minMargins.getRightMils() / 1000.0f) * 72.0f);
        int marginBottom = (int) ((minMargins.getBottomMils() / 1000.0f) * 72.0f);
        this.mContentRect = new Rect(marginLeft, marginTop, widthMils - marginRight, heightMils - marginBottom);
    }

    public PdfDocument.Page startPage(int pageNumber) {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(this.mPageWidth, this.mPageHeight, pageNumber).setContentRect(this.mContentRect).create();
        return startPage(pageInfo);
    }

    public int getPageWidth() {
        return this.mPageWidth;
    }

    public int getPageHeight() {
        return this.mPageHeight;
    }

    public Rect getPageContentRect() {
        return this.mContentRect;
    }
}
