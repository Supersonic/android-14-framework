package com.android.internal.telephony.cat;
/* loaded from: classes.dex */
public class TextAttribute {
    public TextAlignment align;
    public boolean bold;
    public TextColor color;
    public boolean italic;
    public int length;
    public FontSize size;
    public int start;
    public boolean strikeThrough;
    public boolean underlined;

    public TextAttribute(int i, int i2, TextAlignment textAlignment, FontSize fontSize, boolean z, boolean z2, boolean z3, boolean z4, TextColor textColor) {
        this.start = i;
        this.length = i2;
        this.align = textAlignment;
        this.size = fontSize;
        this.bold = z;
        this.italic = z2;
        this.underlined = z3;
        this.strikeThrough = z4;
        this.color = textColor;
    }
}
