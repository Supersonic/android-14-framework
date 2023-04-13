package com.android.server.p011pm;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.util.SparseBooleanArray;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.resolution.ComponentResolverApi;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
/* renamed from: com.android.server.pm.DefaultCrossProfileResolver */
/* loaded from: classes2.dex */
public final class DefaultCrossProfileResolver extends CrossProfileResolver {
    public final DomainVerificationManagerInternal mDomainVerificationManager;

    public DefaultCrossProfileResolver(ComponentResolverApi componentResolverApi, UserManagerService userManagerService, DomainVerificationManagerInternal domainVerificationManagerInternal) {
        super(componentResolverApi, userManagerService);
        this.mDomainVerificationManager = domainVerificationManagerInternal;
    }

    @Override // com.android.server.p011pm.CrossProfileResolver
    public List<CrossProfileDomainInfo> resolveIntent(Computer computer, Intent intent, String str, int i, int i2, long j, String str2, List<CrossProfileIntentFilter> list, boolean z, Function<String, PackageStateInternal> function) {
        ArrayList arrayList = new ArrayList();
        if (str2 != null) {
            return arrayList;
        }
        CrossProfileDomainInfo querySkipCurrentProfileIntents = querySkipCurrentProfileIntents(computer, list, intent, str, j, i, function);
        if (querySkipCurrentProfileIntents != null) {
            arrayList.add(querySkipCurrentProfileIntents);
            return filterIfNotSystemUser(arrayList, i);
        }
        CrossProfileDomainInfo queryCrossProfileIntents = queryCrossProfileIntents(computer, list, intent, str, j, i, z, function);
        if (intent.hasWebURI()) {
            UserInfo profileParent = getProfileParent(i);
            CrossProfileDomainInfo crossProfileDomainPreferredLpr = profileParent != null ? computer.getCrossProfileDomainPreferredLpr(intent, str, j, i, profileParent.id) : null;
            if (crossProfileDomainPreferredLpr != null) {
                queryCrossProfileIntents = crossProfileDomainPreferredLpr;
            }
            if (queryCrossProfileIntents != null) {
                arrayList.add(queryCrossProfileIntents);
            }
        } else if (queryCrossProfileIntents != null) {
            arrayList.add(queryCrossProfileIntents);
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.CrossProfileResolver
    public List<CrossProfileDomainInfo> filterResolveInfoWithDomainPreferredActivity(Intent intent, List<CrossProfileDomainInfo> list, long j, int i, int i2, int i3) {
        ArrayList arrayList = new ArrayList();
        if (list != null && !list.isEmpty()) {
            for (int i4 = 0; i4 < list.size(); i4++) {
                CrossProfileDomainInfo crossProfileDomainInfo = list.get(i4);
                if (crossProfileDomainInfo.mHighestApprovalLevel > i3) {
                    arrayList.add(crossProfileDomainInfo);
                }
            }
        }
        return arrayList;
    }

    public final CrossProfileDomainInfo querySkipCurrentProfileIntents(Computer computer, List<CrossProfileIntentFilter> list, Intent intent, String str, long j, int i, Function<String, PackageStateInternal> function) {
        CrossProfileDomainInfo createForwardingResolveInfo;
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                CrossProfileIntentFilter crossProfileIntentFilter = list.get(i2);
                if ((crossProfileIntentFilter.getFlags() & 2) != 0 && (createForwardingResolveInfo = createForwardingResolveInfo(computer, crossProfileIntentFilter, intent, str, j, i, function)) != null) {
                    return createForwardingResolveInfo;
                }
            }
            return null;
        }
        return null;
    }

    public final CrossProfileDomainInfo queryCrossProfileIntents(Computer computer, List<CrossProfileIntentFilter> list, Intent intent, String str, long j, int i, boolean z, Function<String, PackageStateInternal> function) {
        CrossProfileDomainInfo crossProfileDomainInfo;
        if (list == null) {
            return null;
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = list.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                crossProfileDomainInfo = null;
                break;
            }
            CrossProfileIntentFilter crossProfileIntentFilter = list.get(i2);
            int targetUserId = crossProfileIntentFilter.getTargetUserId();
            boolean z2 = (crossProfileIntentFilter.getFlags() & 2) != 0;
            boolean z3 = (crossProfileIntentFilter.getFlags() & 4) != 0;
            if (!z2 && !sparseBooleanArray.get(targetUserId) && (!z3 || !z)) {
                crossProfileDomainInfo = createForwardingResolveInfo(computer, crossProfileIntentFilter, intent, str, j, i, function);
                if (crossProfileDomainInfo != null) {
                    break;
                }
                sparseBooleanArray.put(targetUserId, true);
            }
            i2++;
        }
        if (crossProfileDomainInfo == null || !isUserEnabled(crossProfileDomainInfo.mResolveInfo.targetUserId) || filterIfNotSystemUser(Collections.singletonList(crossProfileDomainInfo), i).isEmpty()) {
            return null;
        }
        return crossProfileDomainInfo;
    }

    public CrossProfileDomainInfo createForwardingResolveInfo(Computer computer, CrossProfileIntentFilter crossProfileIntentFilter, Intent intent, String str, long j, int i, Function<String, PackageStateInternal> function) {
        ResolveInfo resolveInfo;
        PackageStateInternal apply;
        int targetUserId = crossProfileIntentFilter.getTargetUserId();
        if (isUserEnabled(targetUserId)) {
            List<ResolveInfo> queryActivities = this.mComponentResolver.queryActivities(computer, intent, str, j, targetUserId);
            if (CollectionUtils.isEmpty(queryActivities)) {
                return null;
            }
            int size = queryActivities.size() - 1;
            while (true) {
                if (size < 0) {
                    resolveInfo = null;
                    break;
                } else if ((queryActivities.get(size).activityInfo.applicationInfo.flags & 1073741824) == 0) {
                    resolveInfo = computer.createForwardingResolveInfoUnchecked(crossProfileIntentFilter, i, targetUserId);
                    break;
                } else {
                    size--;
                }
            }
            if (resolveInfo == null) {
                return null;
            }
            int size2 = queryActivities.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size2; i3++) {
                ResolveInfo resolveInfo2 = queryActivities.get(i3);
                if (!resolveInfo2.handleAllWebDataURI && (apply = function.apply(resolveInfo2.activityInfo.packageName)) != null) {
                    i2 = Math.max(i2, this.mDomainVerificationManager.approvalLevelForDomain(apply, intent, j, targetUserId));
                }
            }
            return new CrossProfileDomainInfo(resolveInfo, i2, targetUserId);
        }
        return null;
    }
}
