package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.BrightnessFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;
/* loaded from: classes2.dex */
public class BrightnessEffect extends SingleFilterEffect {
    public BrightnessEffect(EffectContext context, String name) {
        super(context, name, BrightnessFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
