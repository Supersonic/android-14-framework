package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.telephony.ims.DelegateRegistrationState;
import android.telephony.ims.FeatureTagState;
import android.telephony.ims.SipDelegateConfiguration;
import android.telephony.ims.SipDelegateConnection;
import android.telephony.ims.SipDelegateImsConfiguration;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public interface DelegateConnectionStateCallback {
    void onConfigurationChanged(SipDelegateConfiguration sipDelegateConfiguration);

    void onCreated(SipDelegateConnection sipDelegateConnection);

    void onDestroyed(int i);

    void onFeatureTagStatusChanged(DelegateRegistrationState delegateRegistrationState, Set<FeatureTagState> set);

    @Deprecated
    default void onImsConfigurationChanged(SipDelegateImsConfiguration registeredSipConfig) {
        onConfigurationChanged(registeredSipConfig.toNewConfig());
    }
}
