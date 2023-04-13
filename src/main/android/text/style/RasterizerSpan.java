package android.text.style;

import android.graphics.Rasterizer;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class RasterizerSpan extends CharacterStyle implements UpdateAppearance {
    private Rasterizer mRasterizer;

    public RasterizerSpan(Rasterizer r) {
        this.mRasterizer = r;
    }

    public Rasterizer getRasterizer() {
        return this.mRasterizer;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setRasterizer(this.mRasterizer);
    }
}
