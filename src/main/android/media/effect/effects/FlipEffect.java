package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.FlipFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;
/* loaded from: classes2.dex */
public class FlipEffect extends SingleFilterEffect {
    public FlipEffect(EffectContext context, String name) {
        super(context, name, FlipFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
