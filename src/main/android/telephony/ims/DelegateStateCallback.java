package android.telephony.ims;

import android.annotation.SystemApi;
import android.telephony.ims.stub.SipDelegate;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public interface DelegateStateCallback {
    void onConfigurationChanged(SipDelegateConfiguration sipDelegateConfiguration);

    void onCreated(SipDelegate sipDelegate, Set<FeatureTagState> set);

    void onDestroyed(int i);

    void onFeatureTagRegistrationChanged(DelegateRegistrationState delegateRegistrationState);

    @Deprecated
    void onImsConfigurationChanged(SipDelegateImsConfiguration sipDelegateImsConfiguration);
}
