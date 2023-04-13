package android.text.style;

import android.graphics.MaskFilter;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class MaskFilterSpan extends CharacterStyle implements UpdateAppearance {
    private MaskFilter mFilter;

    public MaskFilterSpan(MaskFilter filter) {
        this.mFilter = filter;
    }

    public MaskFilter getMaskFilter() {
        return this.mFilter;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setMaskFilter(this.mFilter);
    }

    public String toString() {
        return "MaskFilterSpan{filter=" + getMaskFilter() + '}';
    }
}
