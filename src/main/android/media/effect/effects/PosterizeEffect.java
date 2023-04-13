package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.PosterizeFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;
/* loaded from: classes2.dex */
public class PosterizeEffect extends SingleFilterEffect {
    public PosterizeEffect(EffectContext context, String name) {
        super(context, name, PosterizeFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
