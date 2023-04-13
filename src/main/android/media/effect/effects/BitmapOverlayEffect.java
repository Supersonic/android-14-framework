package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.BitmapOverlayFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;
/* loaded from: classes2.dex */
public class BitmapOverlayEffect extends SingleFilterEffect {
    public BitmapOverlayEffect(EffectContext context, String name) {
        super(context, name, BitmapOverlayFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
