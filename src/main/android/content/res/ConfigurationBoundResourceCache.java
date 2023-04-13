package android.content.res;

import android.content.res.Resources;
/* loaded from: classes.dex */
public class ConfigurationBoundResourceCache<T> extends ThemedResourceCache<ConstantState<T>> {
    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ Object get(long j, Resources.Theme theme) {
        return super.get(j, theme);
    }

    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ void onConfigurationChange(int i) {
        super.onConfigurationChange(i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ void put(long j, Resources.Theme theme, Object obj) {
        super.put(j, theme, obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ void put(long j, Resources.Theme theme, Object obj, boolean z) {
        super.put(j, theme, obj, z);
    }

    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ boolean shouldInvalidateEntry(Object obj, int i) {
        return shouldInvalidateEntry((ConstantState) ((ConstantState) obj), i);
    }

    public T getInstance(long key, Resources resources, Resources.Theme theme) {
        ConstantState<T> entry = (ConstantState) get(key, theme);
        if (entry != null) {
            return entry.newInstance(resources, theme);
        }
        return null;
    }

    public boolean shouldInvalidateEntry(ConstantState<T> entry, int configChanges) {
        return Configuration.needNewResources(configChanges, entry.getChangingConfigurations());
    }
}
