package com.android.server.p011pm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.config.appcloning.AppCloningDeviceConfigHelper;
import com.android.server.LocalServices;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.p011pm.verify.domain.DomainVerificationUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
/* renamed from: com.android.server.pm.CrossProfileIntentResolverEngine */
/* loaded from: classes2.dex */
public class CrossProfileIntentResolverEngine {
    public AppCloningDeviceConfigHelper mAppCloningDeviceConfigHelper;
    public final Context mContext;
    public final DefaultAppProvider mDefaultAppProvider;
    public final DomainVerificationManagerInternal mDomainVerificationManager;
    public final UserManagerService mUserManager;
    public final UserManagerInternal mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);

    public CrossProfileIntentResolverEngine(UserManagerService userManagerService, DomainVerificationManagerInternal domainVerificationManagerInternal, DefaultAppProvider defaultAppProvider, Context context) {
        this.mUserManager = userManagerService;
        this.mDomainVerificationManager = domainVerificationManagerInternal;
        this.mDefaultAppProvider = defaultAppProvider;
        this.mContext = context;
    }

    public List<CrossProfileDomainInfo> resolveIntent(Computer computer, Intent intent, String str, int i, long j, String str2, boolean z, boolean z2, Function<String, PackageStateInternal> function) {
        return resolveIntentInternal(computer, intent, str, i, i, j, str2, z, z2, function, null);
    }

    public final List<CrossProfileDomainInfo> resolveIntentInternal(Computer computer, Intent intent, String str, int i, int i2, long j, String str2, boolean z, boolean z2, Function<String, PackageStateInternal> function, Set<Integer> set) {
        int i3;
        int i4;
        UserInfo profileParent;
        CrossProfileDomainInfo crossProfileDomainPreferredLpr;
        int i5;
        Set<Integer> set2;
        SparseArray sparseArray;
        CrossProfileResolver chooseCrossProfileResolver;
        boolean z3;
        Set<Integer> set3 = set;
        if (set3 != null) {
            set3.add(Integer.valueOf(i2));
        }
        ArrayList arrayList = new ArrayList();
        List<CrossProfileIntentFilter> matchingCrossProfileIntentFilters = computer.getMatchingCrossProfileIntentFilters(intent, str, i2);
        if (matchingCrossProfileIntentFilters == null) {
            i3 = i;
            i4 = i2;
        } else if (!matchingCrossProfileIntentFilters.isEmpty()) {
            SparseArray sparseArray2 = new SparseArray();
            for (int i6 = 0; i6 < matchingCrossProfileIntentFilters.size(); i6++) {
                CrossProfileIntentFilter crossProfileIntentFilter = matchingCrossProfileIntentFilters.get(i6);
                if (!sparseArray2.contains(crossProfileIntentFilter.mTargetUserId)) {
                    sparseArray2.put(crossProfileIntentFilter.mTargetUserId, new ArrayList());
                }
                ((List) sparseArray2.get(crossProfileIntentFilter.mTargetUserId)).add(crossProfileIntentFilter);
            }
            if (set3 == null) {
                set3 = new HashSet<>();
                set3.add(Integer.valueOf(i2));
            }
            Set<Integer> set4 = set3;
            int i7 = 0;
            while (i7 < sparseArray2.size()) {
                int keyAt = sparseArray2.keyAt(i7);
                if (set4.contains(Integer.valueOf(keyAt)) || (chooseCrossProfileResolver = chooseCrossProfileResolver(computer, i2, keyAt, z2, j)) == null) {
                    i5 = i7;
                    set2 = set4;
                    sparseArray = sparseArray2;
                } else {
                    i5 = i7;
                    set2 = set4;
                    SparseArray sparseArray3 = sparseArray2;
                    List<CrossProfileDomainInfo> resolveIntent = chooseCrossProfileResolver.resolveIntent(computer, intent, str, i2, keyAt, j, str2, (List) sparseArray2.valueAt(i7), z, function);
                    arrayList.addAll(resolveIntent);
                    set2.add(Integer.valueOf(keyAt));
                    int i8 = 0;
                    while (true) {
                        if (i8 >= ((List) sparseArray3.valueAt(i5)).size()) {
                            z3 = false;
                            break;
                        } else if ((((CrossProfileIntentFilter) ((List) sparseArray3.valueAt(i5)).get(i8)).mFlags & 16) != 0) {
                            z3 = true;
                            break;
                        } else {
                            i8++;
                        }
                    }
                    if (z3) {
                        sparseArray = sparseArray3;
                        arrayList.addAll(resolveIntentInternal(computer, intent, str, i, keyAt, j, str2, hasNonNegativePriority(resolveIntent), z2, function, set2));
                    } else {
                        sparseArray = sparseArray3;
                    }
                }
                i7 = i5 + 1;
                set4 = set2;
                sparseArray2 = sparseArray;
            }
            return arrayList;
        } else {
            i3 = i;
            i4 = i2;
        }
        if (i3 == i4 && intent.hasWebURI() && (profileParent = computer.getProfileParent(i4)) != null && (crossProfileDomainPreferredLpr = computer.getCrossProfileDomainPreferredLpr(intent, str, j, i2, profileParent.id)) != null) {
            arrayList.add(crossProfileDomainPreferredLpr);
        }
        return arrayList;
    }

    public final CrossProfileResolver chooseCrossProfileResolver(Computer computer, int i, int i2, boolean z, long j) {
        if (shouldUseNoFilteringResolver(i, i2)) {
            if (this.mAppCloningDeviceConfigHelper == null) {
                this.mAppCloningDeviceConfigHelper = AppCloningDeviceConfigHelper.getInstance(this.mContext);
            }
            if (NoFilteringResolver.isIntentRedirectionAllowed(this.mContext, this.mAppCloningDeviceConfigHelper, z, j)) {
                return new NoFilteringResolver(computer.getComponentResolver(), this.mUserManager);
            }
            return null;
        }
        return new DefaultCrossProfileResolver(computer.getComponentResolver(), this.mUserManager, this.mDomainVerificationManager);
    }

    public boolean canReachTo(Computer computer, Intent intent, String str, int i, int i2) {
        return canReachToInternal(computer, intent, str, i, i2, new HashSet());
    }

    public final boolean canReachToInternal(Computer computer, Intent intent, String str, int i, int i2, Set<Integer> set) {
        if (i == i2) {
            return true;
        }
        set.add(Integer.valueOf(i));
        List<CrossProfileIntentFilter> matchingCrossProfileIntentFilters = computer.getMatchingCrossProfileIntentFilters(intent, str, i);
        if (matchingCrossProfileIntentFilters != null) {
            for (int i3 = 0; i3 < matchingCrossProfileIntentFilters.size(); i3++) {
                CrossProfileIntentFilter crossProfileIntentFilter = matchingCrossProfileIntentFilters.get(i3);
                int i4 = crossProfileIntentFilter.mTargetUserId;
                if (i4 == i2) {
                    return true;
                }
                if (!set.contains(Integer.valueOf(i4)) && (crossProfileIntentFilter.mFlags & 16) != 0) {
                    set.add(Integer.valueOf(crossProfileIntentFilter.mTargetUserId));
                    if (canReachToInternal(computer, intent, str, crossProfileIntentFilter.mTargetUserId, i2, set)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean shouldSkipCurrentProfile(Computer computer, Intent intent, String str, int i) {
        List<CrossProfileIntentFilter> matchingCrossProfileIntentFilters = computer.getMatchingCrossProfileIntentFilters(intent, str, i);
        if (matchingCrossProfileIntentFilters != null) {
            for (int i2 = 0; i2 < matchingCrossProfileIntentFilters.size(); i2++) {
                if ((matchingCrossProfileIntentFilters.get(i2).getFlags() & 2) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public QueryIntentActivitiesResult combineFilterAndCreateQueryActivitiesResponse(Computer computer, Intent intent, String str, String str2, String str3, boolean z, long j, int i, int i2, boolean z2, List<ResolveInfo> list, List<CrossProfileDomainInfo> list2, boolean z3, boolean z4, boolean z5, Function<String, PackageStateInternal> function) {
        List<ResolveInfo> list3;
        if (shouldSkipCurrentProfile(computer, intent, str, i)) {
            return new QueryIntentActivitiesResult(computer.applyPostResolutionFilter(resolveInfoFromCrossProfileDomainInfo(list2), str2, z, i2, z2, i, intent));
        }
        if (str3 == null && intent.hasWebURI()) {
            if (!z4 && ((list.size() <= 1 && list2.isEmpty()) || (list.isEmpty() && !list2.isEmpty()))) {
                list.addAll(resolveInfoFromCrossProfileDomainInfo(list2));
                return new QueryIntentActivitiesResult(computer.applyPostResolutionFilter(list, str2, z, i2, z2, i, intent));
            }
            list3 = filterCandidatesWithDomainPreferredActivitiesLPr(computer, intent, j, list, list2, i, z3, z2, function);
        } else {
            list.addAll(resolveInfoFromCrossProfileDomainInfo(list2));
            list3 = list;
        }
        return new QueryIntentActivitiesResult(z5, z4, list3);
    }

    public final List<ResolveInfo> filterCandidatesWithDomainPreferredActivitiesLPr(Computer computer, Intent intent, long j, List<ResolveInfo> list, List<CrossProfileDomainInfo> list2, int i, boolean z, boolean z2, Function<String, PackageStateInternal> function) {
        return filterCandidatesWithDomainPreferredActivitiesLPrBody(computer, intent, j, list, list2, i, z, (intent.getFlags() & 8) != 0, z2, function);
    }

    public final List<ResolveInfo> filterCandidatesWithDomainPreferredActivitiesLPrBody(Computer computer, Intent intent, long j, List<ResolveInfo> list, List<CrossProfileDomainInfo> list2, int i, boolean z, boolean z2, boolean z3, Function<String, PackageStateInternal> function) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        boolean z4 = true;
        boolean z5 = intent.isWebIntent() && z;
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo = list.get(i2);
            if (z5) {
                if (!resolveInfo.isInstantAppAvailable) {
                    if (computer.isInstantAppInternal(resolveInfo.activityInfo.packageName, i, 1000)) {
                    }
                }
            }
            if (resolveInfo.handleAllWebDataURI) {
                arrayList2.add(resolveInfo);
            } else {
                arrayList3.add(resolveInfo);
            }
        }
        SparseArray<List<CrossProfileDomainInfo>> sparseArray = new SparseArray<>();
        if (list2 != null && !list2.isEmpty()) {
            for (int i3 = 0; i3 < list2.size(); i3++) {
                CrossProfileDomainInfo crossProfileDomainInfo = list2.get(i3);
                if (!sparseArray.contains(crossProfileDomainInfo.mTargetUserId)) {
                    sparseArray.put(crossProfileDomainInfo.mTargetUserId, new ArrayList());
                }
                sparseArray.get(crossProfileDomainInfo.mTargetUserId).add(crossProfileDomainInfo);
            }
        }
        if (!DomainVerificationUtils.isDomainVerificationIntent(intent, j)) {
            arrayList.addAll(arrayList3);
            arrayList.addAll(filterCrossProfileCandidatesWithDomainPreferredActivities(computer, intent, j, sparseArray, i, 0, z3));
        } else {
            Pair<List<ResolveInfo>, Integer> filterToApprovedApp = this.mDomainVerificationManager.filterToApprovedApp(intent, arrayList3, i, function);
            List list3 = (List) filterToApprovedApp.first;
            Integer num = (Integer) filterToApprovedApp.second;
            if (list3.isEmpty()) {
                arrayList.addAll(filterCrossProfileCandidatesWithDomainPreferredActivities(computer, intent, j, sparseArray, i, 0, z3));
            } else {
                arrayList.addAll(list3);
                arrayList.addAll(filterCrossProfileCandidatesWithDomainPreferredActivities(computer, intent, j, sparseArray, i, num.intValue(), z3));
                z4 = false;
            }
        }
        if (z4) {
            if ((j & 131072) != 0) {
                arrayList.addAll(arrayList2);
            } else {
                String defaultBrowser = this.mDefaultAppProvider.getDefaultBrowser(i);
                int size2 = arrayList2.size();
                ResolveInfo resolveInfo2 = null;
                int i4 = 0;
                for (int i5 = 0; i5 < size2; i5++) {
                    ResolveInfo resolveInfo3 = (ResolveInfo) arrayList2.get(i5);
                    int i6 = resolveInfo3.priority;
                    if (i6 > i4) {
                        i4 = i6;
                    }
                    if (resolveInfo3.activityInfo.packageName.equals(defaultBrowser) && (resolveInfo2 == null || resolveInfo2.priority < resolveInfo3.priority)) {
                        if (z2) {
                            Slog.v("PackageManager", "Considering default browser match " + resolveInfo3);
                        }
                        resolveInfo2 = resolveInfo3;
                    }
                }
                if (resolveInfo2 != null && resolveInfo2.priority >= i4 && !TextUtils.isEmpty(defaultBrowser)) {
                    if (z2) {
                        Slog.v("PackageManager", "Default browser match " + resolveInfo2);
                    }
                    arrayList.add(resolveInfo2);
                } else {
                    arrayList.addAll(arrayList2);
                }
            }
            if (arrayList.size() == 0) {
                arrayList.addAll(list);
            }
        }
        return arrayList;
    }

    public final List<ResolveInfo> filterCrossProfileCandidatesWithDomainPreferredActivities(Computer computer, Intent intent, long j, SparseArray<List<CrossProfileDomainInfo>> sparseArray, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < sparseArray.size(); i3++) {
            if (sparseArray.keyAt(i3) == -2) {
                arrayList.addAll(sparseArray.valueAt(i3));
            } else {
                CrossProfileResolver chooseCrossProfileResolver = chooseCrossProfileResolver(computer, i, sparseArray.keyAt(i3), z, j);
                if (chooseCrossProfileResolver != null) {
                    arrayList.addAll(chooseCrossProfileResolver.filterResolveInfoWithDomainPreferredActivity(intent, sparseArray.valueAt(i3), j, i, sparseArray.keyAt(i3), i2));
                } else {
                    arrayList.addAll(sparseArray.valueAt(i3));
                }
            }
        }
        return resolveInfoFromCrossProfileDomainInfo(arrayList);
    }

    public final List<ResolveInfo> resolveInfoFromCrossProfileDomainInfo(List<CrossProfileDomainInfo> list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(list.get(i).mResolveInfo);
        }
        return arrayList;
    }

    public final boolean hasNonNegativePriority(List<CrossProfileDomainInfo> list) {
        return list.size() > 0 && list.get(0).mResolveInfo != null && list.get(0).mResolveInfo.priority >= 0;
    }

    public final boolean shouldUseNoFilteringResolver(int i, int i2) {
        return isNoFilteringPropertyConfiguredForUser(i) || isNoFilteringPropertyConfiguredForUser(i2);
    }

    public final boolean isNoFilteringPropertyConfiguredForUser(int i) {
        UserProperties userProperties;
        return this.mUserManager.isProfile(i) && (userProperties = this.mUserManagerInternal.getUserProperties(i)) != null && userProperties.getCrossProfileIntentResolutionStrategy() == 1;
    }
}
