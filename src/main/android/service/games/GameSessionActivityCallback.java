package android.service.games;

import android.annotation.SystemApi;
import android.content.Intent;
@SystemApi
/* loaded from: classes3.dex */
public interface GameSessionActivityCallback {
    void onActivityResult(int i, Intent intent);

    default void onActivityStartFailed(Throwable t) {
    }
}
