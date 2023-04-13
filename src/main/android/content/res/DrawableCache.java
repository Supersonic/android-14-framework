package android.content.res;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DrawableCache extends ThemedResourceCache<Drawable.ConstantState> {
    public Drawable getInstance(long key, Resources resources, Resources.Theme theme) {
        Drawable.ConstantState entry = get(key, theme);
        if (entry != null) {
            return entry.newDrawable(resources, theme);
        }
        return null;
    }

    @Override // android.content.res.ThemedResourceCache
    public boolean shouldInvalidateEntry(Drawable.ConstantState entry, int configChanges) {
        return Configuration.needNewResources(configChanges, entry.getChangingConfigurations());
    }
}
