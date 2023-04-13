package android.service.contentcapture;

import android.annotation.SystemApi;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public interface DataShareCallback {
    void onAccept(Executor executor, DataShareReadAdapter dataShareReadAdapter);

    void onReject();
}
