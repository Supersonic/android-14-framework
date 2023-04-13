package com.android.server.p011pm.verify.domain.proxy;

import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationInfo;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.verify.domain.DomainVerificationCollector;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
/* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxyV1 */
/* loaded from: classes2.dex */
public class DomainVerificationProxyV1 implements DomainVerificationProxy {
    public final DomainVerificationCollector mCollector;
    public final Connection mConnection;
    public final Context mContext;
    public final DomainVerificationManagerInternal mManager;
    public final ComponentName mVerifierComponent;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArrayMap<Integer, Pair<UUID, String>> mRequests = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public int mVerificationToken = 0;

    /* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxyV1$Connection */
    /* loaded from: classes2.dex */
    public interface Connection extends DomainVerificationProxy.BaseConnection {
        AndroidPackage getPackage(String str);
    }

    public DomainVerificationProxyV1(Context context, DomainVerificationManagerInternal domainVerificationManagerInternal, DomainVerificationCollector domainVerificationCollector, Connection connection, ComponentName componentName) {
        this.mContext = context;
        this.mConnection = connection;
        this.mVerifierComponent = componentName;
        this.mManager = domainVerificationManagerInternal;
        this.mCollector = domainVerificationCollector;
    }

    public static void queueLegacyVerifyResult(Context context, Connection connection, int i, int i2, List<String> list, int i3) {
        context.enforceCallingOrSelfPermission("android.permission.INTENT_FILTER_VERIFICATION_AGENT", "Only the intent filter verification agent can verify applications");
        connection.schedule(3, new Response(i3, i, i2, list));
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public void sendBroadcastForPackages(Set<String> set) {
        synchronized (this.mLock) {
            for (int size = this.mRequests.size() - 1; size >= 0; size--) {
                if (set.contains(this.mRequests.valueAt(size).second)) {
                    this.mRequests.removeAt(size);
                }
            }
        }
        this.mConnection.schedule(2, set);
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public boolean runMessage(int i, Object obj) {
        DomainVerificationInfo domainVerificationInfo;
        if (i == 2) {
            Set<String> set = (Set) obj;
            ArrayMap<Integer, Pair<UUID, String>> arrayMap = new ArrayMap<>(set.size());
            synchronized (this.mLock) {
                for (String str : set) {
                    UUID domainVerificationInfoId = this.mManager.getDomainVerificationInfoId(str);
                    if (domainVerificationInfoId != null) {
                        int i2 = this.mVerificationToken;
                        this.mVerificationToken = i2 + 1;
                        arrayMap.put(Integer.valueOf(i2), Pair.create(domainVerificationInfoId, str));
                    }
                }
                this.mRequests.putAll((ArrayMap<? extends Integer, ? extends Pair<UUID, String>>) arrayMap);
            }
            sendBroadcasts(arrayMap);
            return true;
        } else if (i != 3) {
            return false;
        } else {
            Response response = (Response) obj;
            Pair<UUID, String> pair = this.mRequests.get(Integer.valueOf(response.verificationId));
            if (pair == null) {
                return true;
            }
            UUID uuid = (UUID) pair.first;
            String str2 = (String) pair.second;
            try {
                domainVerificationInfo = this.mManager.getDomainVerificationInfo(str2);
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (domainVerificationInfo == null || !Objects.equals(uuid, domainVerificationInfo.getIdentifier()) || this.mConnection.getPackage(str2) == null) {
                return true;
            }
            ArraySet arraySet = new ArraySet(response.failedDomains);
            Set keySet = domainVerificationInfo.getHostToStateMap().keySet();
            ArraySet arraySet2 = new ArraySet(keySet);
            arraySet2.removeAll(arraySet);
            for (int size = arraySet2.size() - 1; size >= 0; size--) {
                String str3 = (String) arraySet2.valueAt(size);
                if (str3.startsWith("*.")) {
                    String substring = str3.substring(2);
                    if (arraySet.contains(substring)) {
                        arraySet.add(str3);
                        arraySet2.removeAt(size);
                        if (!keySet.contains(substring)) {
                            arraySet.remove(substring);
                        }
                    }
                }
            }
            int i3 = response.callingUid;
            if (!arraySet2.isEmpty()) {
                try {
                    if (this.mManager.setDomainVerificationStatusInternal(i3, uuid, arraySet2, 1) != 0) {
                        Slog.e("DomainVerificationProxyV1", "Failure reporting successful domains for " + str2);
                    }
                } catch (Exception e) {
                    Slog.e("DomainVerificationProxyV1", "Failure reporting successful domains for " + str2, e);
                }
            }
            if (!arraySet.isEmpty()) {
                try {
                    if (this.mManager.setDomainVerificationStatusInternal(i3, uuid, arraySet, 6) != 0) {
                        Slog.e("DomainVerificationProxyV1", "Failure reporting failed domains for " + str2);
                    }
                } catch (Exception e2) {
                    Slog.e("DomainVerificationProxyV1", "Failure reporting failed domains for " + str2, e2);
                }
            }
            return true;
        }
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public boolean isCallerVerifier(int i) {
        return this.mConnection.isCallerPackage(i, this.mVerifierComponent.getPackageName());
    }

    public final void sendBroadcasts(ArrayMap<Integer, Pair<UUID, String>> arrayMap) {
        long powerSaveTempWhitelistAppDuration = this.mConnection.getPowerSaveTempWhitelistAppDuration();
        this.mConnection.getDeviceIdleInternal().addPowerSaveTempWhitelistApp(Process.myUid(), this.mVerifierComponent.getPackageName(), powerSaveTempWhitelistAppDuration, 0, true, (int) FrameworkStatsLog.f74x6a63db72, "domain verification agent");
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            int intValue = arrayMap.keyAt(i).intValue();
            String str = (String) arrayMap.valueAt(i).second;
            Intent addFlags = new Intent("android.intent.action.INTENT_FILTER_NEEDS_VERIFICATION").setComponent(this.mVerifierComponent).putExtra("android.content.pm.extra.INTENT_FILTER_VERIFICATION_ID", intValue).putExtra("android.content.pm.extra.INTENT_FILTER_VERIFICATION_URI_SCHEME", "https").putExtra("android.content.pm.extra.INTENT_FILTER_VERIFICATION_HOSTS", buildHostsString(this.mConnection.getPackage(str))).putExtra("android.content.pm.extra.INTENT_FILTER_VERIFICATION_PACKAGE_NAME", str).addFlags(268435456);
            BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
            makeBasic.setTemporaryAppAllowlist(powerSaveTempWhitelistAppDuration, 0, (int) FrameworkStatsLog.f74x6a63db72, "");
            this.mContext.sendBroadcastAsUser(addFlags, UserHandle.SYSTEM, null, makeBasic.toBundle());
        }
    }

    public final String buildHostsString(AndroidPackage androidPackage) {
        ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(androidPackage);
        StringBuilder sb = new StringBuilder();
        int size = collectValidAutoVerifyDomains.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            String valueAt = collectValidAutoVerifyDomains.valueAt(i);
            if (valueAt.startsWith("*.")) {
                valueAt = valueAt.substring(2);
            }
            sb.append(valueAt);
        }
        return sb.toString();
    }

    @Override // com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy
    public ComponentName getComponentName() {
        return this.mVerifierComponent;
    }

    /* renamed from: com.android.server.pm.verify.domain.proxy.DomainVerificationProxyV1$Response */
    /* loaded from: classes2.dex */
    public static class Response {
        public final int callingUid;
        public final List<String> failedDomains;
        public final int verificationCode;
        public final int verificationId;

        public Response(int i, int i2, int i3, List<String> list) {
            this.callingUid = i;
            this.verificationId = i2;
            this.verificationCode = i3;
            this.failedDomains = list == null ? Collections.emptyList() : list;
        }
    }
}
