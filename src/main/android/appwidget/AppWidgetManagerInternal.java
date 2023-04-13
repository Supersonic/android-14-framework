package android.appwidget;

import android.util.ArraySet;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class AppWidgetManagerInternal {
    public abstract void applyResourceOverlaysToWidgets(Set<String> set, int i, boolean z);

    public abstract ArraySet<String> getHostedWidgetPackages(int i);

    public abstract void unlockUser(int i);
}
