package android.text.style;

import android.text.TextPaint;
import android.view.View;
/* loaded from: classes3.dex */
public abstract class ClickableSpan extends CharacterStyle implements UpdateAppearance {
    private static int sIdCounter = 0;
    private int mId;

    public abstract void onClick(View view);

    public ClickableSpan() {
        int i = sIdCounter;
        sIdCounter = i + 1;
        this.mId = i;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(true);
    }

    public int getId() {
        return this.mId;
    }

    public String toString() {
        return "ClickableSpan{}";
    }
}
