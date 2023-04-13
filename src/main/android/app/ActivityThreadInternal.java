package android.app;

import android.content.ComponentCallbacks2;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface ActivityThreadInternal {
    ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean z);

    Application getApplication();

    ContextImpl getSystemContext();

    ContextImpl getSystemUiContextNoCreate();

    boolean isInDensityCompatMode();
}
