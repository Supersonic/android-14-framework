package android.app.ambientcontext;

import java.util.List;
/* loaded from: classes.dex */
public interface AmbientContextCallback {
    void onEvents(List<AmbientContextEvent> list);

    void onRegistrationComplete(int i);
}
