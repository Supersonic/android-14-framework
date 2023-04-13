package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.FillLightFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;
/* loaded from: classes2.dex */
public class FillLightEffect extends SingleFilterEffect {
    public FillLightEffect(EffectContext context, String name) {
        super(context, name, FillLightFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
