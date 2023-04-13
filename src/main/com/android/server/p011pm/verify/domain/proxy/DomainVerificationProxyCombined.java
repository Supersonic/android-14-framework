package com.android.server.p011pm.verify.domain.proxy;

import android.content.ComponentName;
import java.util.Set;
/* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxyCombined */
/* loaded from: classes2.dex */
public class DomainVerificationProxyCombined implements DomainVerificationProxy {
    public final DomainVerificationProxy mProxyV1;
    public final DomainVerificationProxy mProxyV2;

    public DomainVerificationProxyCombined(DomainVerificationProxy domainVerificationProxy, DomainVerificationProxy domainVerificationProxy2) {
        this.mProxyV1 = domainVerificationProxy;
        this.mProxyV2 = domainVerificationProxy2;
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public void sendBroadcastForPackages(Set<String> set) {
        this.mProxyV2.sendBroadcastForPackages(set);
        this.mProxyV1.sendBroadcastForPackages(set);
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public boolean runMessage(int i, Object obj) {
        return this.mProxyV2.runMessage(i, obj) || this.mProxyV1.runMessage(i, obj);
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public boolean isCallerVerifier(int i) {
        return this.mProxyV2.isCallerVerifier(i) || this.mProxyV1.isCallerVerifier(i);
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public ComponentName getComponentName() {
        return this.mProxyV2.getComponentName();
    }
}
