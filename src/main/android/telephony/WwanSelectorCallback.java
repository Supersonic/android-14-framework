package android.telephony;

import android.p008os.CancellationSignal;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public interface WwanSelectorCallback {
    void onDomainSelected(int i, boolean z);

    void onRequestEmergencyNetworkScan(List<Integer> list, int i, CancellationSignal cancellationSignal, Consumer<EmergencyRegResult> consumer);
}
