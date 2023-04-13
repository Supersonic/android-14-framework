package android.app;

import android.content.Context;
import android.p008os.Bundle;
import android.view.View;
@Deprecated
/* loaded from: classes.dex */
public abstract class FragmentContainer {
    public abstract <T extends View> T onFindViewById(int i);

    public abstract boolean onHasView();

    public Fragment instantiate(Context context, String className, Bundle arguments) {
        return Fragment.instantiate(context, className, arguments);
    }
}
