package com.android.server.p011pm.verify.domain.proxy;

import android.content.ComponentName;
import android.content.Context;
import com.android.server.DeviceIdleInternal;
import com.android.server.p011pm.verify.domain.DomainVerificationCollector;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxyV1;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxyV2;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxy */
/* loaded from: classes2.dex */
public interface DomainVerificationProxy {

    /* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxy$BaseConnection */
    /* loaded from: classes2.dex */
    public interface BaseConnection {
        DeviceIdleInternal getDeviceIdleInternal();

        long getPowerSaveTempWhitelistAppDuration();

        boolean isCallerPackage(int i, String str);

        void schedule(int i, Object obj);
    }

    ComponentName getComponentName();

    boolean isCallerVerifier(int i);

    boolean runMessage(int i, Object obj);

    void sendBroadcastForPackages(Set<String> set);

    static <ConnectionType extends DomainVerificationProxyV1.Connection & DomainVerificationProxyV2.Connection> DomainVerificationProxy makeProxy(ComponentName componentName, ComponentName componentName2, Context context, DomainVerificationManagerInternal domainVerificationManagerInternal, DomainVerificationCollector domainVerificationCollector, ConnectionType connectiontype) {
        ComponentName componentName3 = (componentName2 == null || componentName == null || Objects.equals(componentName2.getPackageName(), componentName.getPackageName())) ? componentName : null;
        DomainVerificationProxyV1 domainVerificationProxyV1 = componentName3 != null ? new DomainVerificationProxyV1(context, domainVerificationManagerInternal, domainVerificationCollector, connectiontype, componentName3) : null;
        DomainVerificationProxyV2 domainVerificationProxyV2 = componentName2 != null ? new DomainVerificationProxyV2(context, connectiontype, componentName2) : null;
        if (domainVerificationProxyV1 == null || domainVerificationProxyV2 == null) {
            return domainVerificationProxyV1 != null ? domainVerificationProxyV1 : domainVerificationProxyV2 != null ? domainVerificationProxyV2 : new DomainVerificationProxyUnavailable();
        }
        return new DomainVerificationProxyCombined(domainVerificationProxyV1, domainVerificationProxyV2);
    }
}
