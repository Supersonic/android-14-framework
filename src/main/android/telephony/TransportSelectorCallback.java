package android.telephony;

import java.util.function.Consumer;
/* loaded from: classes3.dex */
public interface TransportSelectorCallback {
    void onCreated(DomainSelector domainSelector);

    void onSelectionTerminated(int i);

    void onWlanSelected(boolean z);

    WwanSelectorCallback onWwanSelected();

    void onWwanSelected(Consumer<WwanSelectorCallback> consumer);
}
