package android.telecom;

import android.p008os.Bundle;
import java.util.List;
/* loaded from: classes3.dex */
public interface CallEventCallback {
    void onAvailableCallEndpointsChanged(List<CallEndpoint> list);

    void onCallEndpointChanged(CallEndpoint callEndpoint);

    void onCallStreamingFailed(int i);

    void onEvent(String str, Bundle bundle);

    void onMuteStateChanged(boolean z);
}
