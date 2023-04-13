package com.android.server.p011pm.verify.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.verify.domain.DomainOwner;
import android.content.pm.verify.domain.DomainVerificationInfo;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationState;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.content.pm.verify.domain.IDomainVerificationManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.PackageUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.SystemConfig;
import com.android.server.SystemService;
import com.android.server.compat.PlatformCompat;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageStateUtils;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import com.android.server.p011pm.pkg.PackageUserStateUtils;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.p011pm.verify.domain.DomainVerificationShell;
import com.android.server.p011pm.verify.domain.models.DomainVerificationInternalUserState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationPkgState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationStateMap;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxyUnavailable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.xmlpull.v1.XmlPullParserException;
@SuppressLint({"MissingPermission"})
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationService */
/* loaded from: classes2.dex */
public class DomainVerificationService extends SystemService implements DomainVerificationManagerInternal, DomainVerificationShell.Callback {
    @GuardedBy({"mLock"})
    public final DomainVerificationStateMap<DomainVerificationPkgState> mAttachedPkgStates;
    public boolean mCanSendBroadcasts;
    public final DomainVerificationCollector mCollector;
    public DomainVerificationManagerInternal.Connection mConnection;
    public final DomainVerificationDebug mDebug;
    public final DomainVerificationEnforcer mEnforcer;
    public final DomainVerificationLegacySettings mLegacySettings;
    public final Object mLock;
    public final PlatformCompat mPlatformCompat;
    public DomainVerificationProxy mProxy;
    public final DomainVerificationSettings mSettings;
    public final DomainVerificationShell mShell;
    public final IDomainVerificationManager.Stub mStub;
    public final SystemConfig mSystemConfig;

    public DomainVerificationService(Context context, SystemConfig systemConfig, PlatformCompat platformCompat) {
        super(context);
        this.mAttachedPkgStates = new DomainVerificationStateMap<>();
        this.mLock = new Object();
        this.mStub = new DomainVerificationManagerStub(this);
        this.mProxy = new DomainVerificationProxyUnavailable();
        this.mSystemConfig = systemConfig;
        this.mPlatformCompat = platformCompat;
        DomainVerificationCollector domainVerificationCollector = new DomainVerificationCollector(platformCompat, systemConfig);
        this.mCollector = domainVerificationCollector;
        this.mSettings = new DomainVerificationSettings(domainVerificationCollector);
        this.mEnforcer = new DomainVerificationEnforcer(context);
        this.mDebug = new DomainVerificationDebug(domainVerificationCollector);
        this.mShell = new DomainVerificationShell(this);
        this.mLegacySettings = new DomainVerificationLegacySettings();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("domain_verification", this.mStub);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void setConnection(DomainVerificationManagerInternal.Connection connection) {
        this.mConnection = connection;
        this.mEnforcer.setCallback(connection);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public DomainVerificationProxy getProxy() {
        return this.mProxy;
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        super.onBootPhase(i);
        if (hasRealVerifier()) {
            if (i == 550) {
                this.mCanSendBroadcasts = true;
            } else if (i != 1000) {
            } else {
                verifyPackages(null, false);
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocked(SystemService.TargetUser targetUser) {
        super.onUserUnlocked(targetUser);
        verifyPackages(null, false);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void setProxy(DomainVerificationProxy domainVerificationProxy) {
        this.mProxy = domainVerificationProxy;
    }

    public List<String> queryValidVerificationPackageNames() {
        this.mEnforcer.assertApprovedVerifier(this.mConnection.getCallingUid(), this.mProxy);
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            int size = this.mAttachedPkgStates.size();
            for (int i = 0; i < size; i++) {
                DomainVerificationPkgState valueAt = this.mAttachedPkgStates.valueAt(i);
                if (valueAt.isHasAutoVerifyDomains()) {
                    arrayList.add(valueAt.getPackageName());
                }
            }
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public UUID getDomainVerificationInfoId(String str) {
        synchronized (this.mLock) {
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState != null) {
                return domainVerificationPkgState.getId();
            }
            return null;
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public DomainVerificationInfo getDomainVerificationInfo(String str) throws PackageManager.NameNotFoundException {
        this.mEnforcer.assertApprovedQuerent(this.mConnection.getCallingUid(), this.mProxy);
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            PackageStateInternal packageStateInternal = snapshot.getPackageStateInternal(str);
            AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
            if (pkg == null) {
                throw DomainVerificationUtils.throwPackageUnavailable(str);
            }
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState == null) {
                return null;
            }
            ArrayMap arrayMap = new ArrayMap(domainVerificationPkgState.getStateMap());
            ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(pkg);
            if (collectValidAutoVerifyDomains.isEmpty()) {
                return null;
            }
            int size = collectValidAutoVerifyDomains.size();
            for (int i = 0; i < size; i++) {
                arrayMap.putIfAbsent(collectValidAutoVerifyDomains.valueAt(i), 0);
            }
            int size2 = arrayMap.size();
            for (int i2 = 0; i2 < size2; i2++) {
                arrayMap.setValueAt(i2, Integer.valueOf(DomainVerificationState.convertToInfoState(((Integer) arrayMap.valueAt(i2)).intValue())));
            }
            return new DomainVerificationInfo(domainVerificationPkgState.getId(), str, arrayMap);
        }
    }

    @DomainVerificationManager.Error
    public int setDomainVerificationStatus(UUID uuid, Set<String> set, int i) throws PackageManager.NameNotFoundException {
        if (i < 1024 && i != 1) {
            throw new IllegalArgumentException("Caller is not allowed to set state code " + i);
        }
        return setDomainVerificationStatusInternal(this.mConnection.getCallingUid(), uuid, set, i);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    @DomainVerificationManager.Error
    public int setDomainVerificationStatusInternal(int i, UUID uuid, Set<String> set, int i2) throws PackageManager.NameNotFoundException {
        this.mEnforcer.assertApprovedVerifier(i, this.mProxy);
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            ArrayList arrayList = new ArrayList();
            GetAttachedResult andValidateAttachedLocked = getAndValidateAttachedLocked(uuid, set, true, i, null, snapshot);
            if (andValidateAttachedLocked.isError()) {
                return andValidateAttachedLocked.getErrorCode();
            }
            DomainVerificationPkgState pkgState = andValidateAttachedLocked.getPkgState();
            ArrayMap<String, Integer> stateMap = pkgState.getStateMap();
            for (String str : set) {
                Integer num = stateMap.get(str);
                if (num == null || (num.intValue() != i2 && DomainVerificationState.isModifiable(num.intValue()))) {
                    if (DomainVerificationState.isVerified(i2) && (num == null || !DomainVerificationState.isVerified(num.intValue()))) {
                        arrayList.add(str);
                    }
                    stateMap.put(str, Integer.valueOf(i2));
                }
            }
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                removeUserStatesForDomain(pkgState, (String) arrayList.get(i3));
            }
            this.mConnection.scheduleWriteSettings();
            return 0;
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void setDomainVerificationStatusInternal(String str, int i, ArraySet<String> arraySet) throws PackageManager.NameNotFoundException {
        ArraySet arraySet2;
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        if (i != 0 && i != 1 && i != 2 && i != 3) {
            throw new IllegalArgumentException("State must be one of NO_RESPONSE, SUCCESS, APPROVED, or DENIED");
        }
        int i2 = 0;
        if (str == null) {
            Computer snapshot = this.mConnection.snapshot();
            synchronized (this.mLock) {
                ArraySet<String> arraySet3 = new ArraySet<>();
                int size = this.mAttachedPkgStates.size();
                while (i2 < size) {
                    DomainVerificationPkgState valueAt = this.mAttachedPkgStates.valueAt(i2);
                    PackageStateInternal packageStateInternal = snapshot.getPackageStateInternal(valueAt.getPackageName());
                    if (packageStateInternal != null && packageStateInternal.getPkg() != null) {
                        AndroidPackageInternal pkg = packageStateInternal.getPkg();
                        arraySet3.clear();
                        ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(pkg);
                        if (arraySet == null) {
                            arraySet3.addAll((ArraySet<? extends String>) collectValidAutoVerifyDomains);
                        } else {
                            arraySet3.addAll((ArraySet<? extends String>) arraySet);
                            arraySet3.retainAll(collectValidAutoVerifyDomains);
                        }
                        setDomainVerificationStatusInternal(valueAt, i, arraySet3);
                    }
                    i2++;
                }
            }
        } else {
            Computer snapshot2 = this.mConnection.snapshot();
            synchronized (this.mLock) {
                DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
                if (domainVerificationPkgState == null) {
                    throw DomainVerificationUtils.throwPackageUnavailable(str);
                }
                PackageStateInternal packageStateInternal2 = snapshot2.getPackageStateInternal(str);
                if (packageStateInternal2 == null || packageStateInternal2.getPkg() == null) {
                    throw DomainVerificationUtils.throwPackageUnavailable(str);
                }
                AndroidPackageInternal pkg2 = packageStateInternal2.getPkg();
                if (arraySet == null) {
                    arraySet = this.mCollector.collectValidAutoVerifyDomains(pkg2);
                } else {
                    arraySet.retainAll(this.mCollector.collectValidAutoVerifyDomains(pkg2));
                }
                if (DomainVerificationState.isVerified(i)) {
                    arraySet2 = new ArraySet();
                    ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
                    int size2 = arraySet.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        String valueAt2 = arraySet.valueAt(i3);
                        Integer num = stateMap.get(valueAt2);
                        if (num == null || !DomainVerificationState.isVerified(num.intValue())) {
                            arraySet2.add(valueAt2);
                        }
                    }
                } else {
                    arraySet2 = null;
                }
                setDomainVerificationStatusInternal(domainVerificationPkgState, i, arraySet);
                if (arraySet2 != null) {
                    int size3 = arraySet2.size();
                    while (i2 < size3) {
                        removeUserStatesForDomain(domainVerificationPkgState, (String) arraySet2.valueAt(i2));
                        i2++;
                    }
                }
            }
        }
        this.mConnection.scheduleWriteSettings();
    }

    public final void setDomainVerificationStatusInternal(DomainVerificationPkgState domainVerificationPkgState, int i, ArraySet<String> arraySet) {
        ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
        int size = arraySet.size();
        for (int i2 = 0; i2 < size; i2++) {
            stateMap.put(arraySet.valueAt(i2), Integer.valueOf(i));
        }
    }

    public final void removeUserStatesForDomain(DomainVerificationPkgState domainVerificationPkgState, String str) {
        SparseArray<DomainVerificationInternalUserState> userStates = domainVerificationPkgState.getUserStates();
        synchronized (this.mLock) {
            int size = this.mAttachedPkgStates.size();
            for (int i = 0; i < size; i++) {
                SparseArray<DomainVerificationInternalUserState> userStates2 = this.mAttachedPkgStates.valueAt(i).getUserStates();
                int size2 = userStates2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    DomainVerificationInternalUserState domainVerificationInternalUserState = userStates.get(userStates2.keyAt(i2));
                    if (domainVerificationInternalUserState == null || domainVerificationInternalUserState.isLinkHandlingAllowed()) {
                        userStates2.valueAt(i2).removeHost(str);
                    }
                }
            }
        }
    }

    public void setDomainVerificationLinkHandlingAllowed(String str, boolean z, int i) throws PackageManager.NameNotFoundException {
        if (!this.mEnforcer.assertApprovedUserSelector(this.mConnection.getCallingUid(), this.mConnection.getCallingUserId(), str, i)) {
            throw DomainVerificationUtils.throwPackageUnavailable(str);
        }
        synchronized (this.mLock) {
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState == null) {
                throw DomainVerificationUtils.throwPackageUnavailable(str);
            }
            domainVerificationPkgState.getOrCreateUserState(i).setLinkHandlingAllowed(z);
        }
        this.mConnection.scheduleWriteSettings();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void setDomainVerificationLinkHandlingAllowedInternal(String str, boolean z, int i) throws PackageManager.NameNotFoundException {
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        if (str == null) {
            synchronized (this.mLock) {
                int size = this.mAttachedPkgStates.size();
                for (int i2 = 0; i2 < size; i2++) {
                    DomainVerificationPkgState valueAt = this.mAttachedPkgStates.valueAt(i2);
                    if (i == -1) {
                        for (int i3 : this.mConnection.getAllUserIds()) {
                            valueAt.getOrCreateUserState(i3).setLinkHandlingAllowed(z);
                        }
                    } else {
                        valueAt.getOrCreateUserState(i).setLinkHandlingAllowed(z);
                    }
                }
            }
        } else {
            synchronized (this.mLock) {
                DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
                if (domainVerificationPkgState == null) {
                    throw DomainVerificationUtils.throwPackageUnavailable(str);
                }
                if (i == -1) {
                    for (int i4 : this.mConnection.getAllUserIds()) {
                        domainVerificationPkgState.getOrCreateUserState(i4).setLinkHandlingAllowed(z);
                    }
                } else {
                    domainVerificationPkgState.getOrCreateUserState(i).setLinkHandlingAllowed(z);
                }
            }
        }
        this.mConnection.scheduleWriteSettings();
    }

    @DomainVerificationManager.Error
    public int setDomainVerificationUserSelection(UUID uuid, Set<String> set, boolean z, int i) throws PackageManager.NameNotFoundException {
        int revokeOtherUserSelectionsLocked;
        int callingUid = this.mConnection.getCallingUid();
        if (this.mEnforcer.assertApprovedUserSelector(callingUid, this.mConnection.getCallingUserId(), null, i)) {
            Computer snapshot = this.mConnection.snapshot();
            synchronized (this.mLock) {
                GetAttachedResult andValidateAttachedLocked = getAndValidateAttachedLocked(uuid, set, false, callingUid, Integer.valueOf(i), snapshot);
                if (andValidateAttachedLocked.isError()) {
                    return andValidateAttachedLocked.getErrorCode();
                }
                DomainVerificationInternalUserState orCreateUserState = andValidateAttachedLocked.getPkgState().getOrCreateUserState(i);
                if (!z || (revokeOtherUserSelectionsLocked = revokeOtherUserSelectionsLocked(orCreateUserState, i, set, snapshot)) == 0) {
                    if (z) {
                        orCreateUserState.addHosts(set);
                    } else {
                        orCreateUserState.removeHosts(set);
                    }
                    this.mConnection.scheduleWriteSettings();
                    return 0;
                }
                return revokeOtherUserSelectionsLocked;
            }
        }
        return 1;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void setDomainVerificationUserSelectionInternal(int i, String str, boolean z, ArraySet<String> arraySet) throws PackageManager.NameNotFoundException {
        int[] allUserIds;
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState == null) {
                throw DomainVerificationUtils.throwPackageUnavailable(str);
            }
            PackageStateInternal packageStateInternal = snapshot.getPackageStateInternal(str);
            AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
            if (pkg == null) {
                throw DomainVerificationUtils.throwPackageUnavailable(str);
            }
            if (arraySet == null) {
                arraySet = this.mCollector.collectAllWebDomains(pkg);
            }
            arraySet.retainAll(this.mCollector.collectAllWebDomains(pkg));
            if (i == -1) {
                for (int i2 : this.mConnection.getAllUserIds()) {
                    DomainVerificationInternalUserState orCreateUserState = domainVerificationPkgState.getOrCreateUserState(i2);
                    revokeOtherUserSelectionsLocked(orCreateUserState, i2, arraySet, snapshot);
                    if (z) {
                        orCreateUserState.addHosts((Set<String>) arraySet);
                    } else {
                        orCreateUserState.removeHosts(arraySet);
                    }
                }
            } else {
                DomainVerificationInternalUserState orCreateUserState2 = domainVerificationPkgState.getOrCreateUserState(i);
                revokeOtherUserSelectionsLocked(orCreateUserState2, i, arraySet, snapshot);
                if (z) {
                    orCreateUserState2.addHosts((Set<String>) arraySet);
                } else {
                    orCreateUserState2.removeHosts(arraySet);
                }
            }
        }
        this.mConnection.scheduleWriteSettings();
    }

    @GuardedBy({"mLock"})
    public final int revokeOtherUserSelectionsLocked(DomainVerificationInternalUserState domainVerificationInternalUserState, int i, Set<String> set, Computer computer) {
        DomainVerificationInternalUserState userState;
        ArrayMap arrayMap = new ArrayMap();
        for (String str : set) {
            if (!domainVerificationInternalUserState.getEnabledHosts().contains(str)) {
                Pair<List<String>, Integer> approvedPackagesLocked = getApprovedPackagesLocked(str, i, 1, computer);
                if (((Integer) approvedPackagesLocked.second).intValue() > 3) {
                    return 3;
                }
                arrayMap.put(str, (List) approvedPackagesLocked.first);
            }
        }
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str2 = (String) arrayMap.keyAt(i2);
            List list = (List) arrayMap.valueAt(i2);
            int size2 = list.size();
            for (int i3 = 0; i3 < size2; i3++) {
                DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get((String) list.get(i3));
                if (domainVerificationPkgState != null && (userState = domainVerificationPkgState.getUserState(i)) != null) {
                    userState.removeHost(str2);
                }
            }
        }
        return 0;
    }

    public DomainVerificationUserState getDomainVerificationUserState(String str, int i) throws PackageManager.NameNotFoundException {
        boolean z;
        if (!this.mEnforcer.assertApprovedUserStateQuerent(this.mConnection.getCallingUid(), this.mConnection.getCallingUserId(), str, i)) {
            throw DomainVerificationUtils.throwPackageUnavailable(str);
        }
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            PackageStateInternal packageStateInternal = snapshot.getPackageStateInternal(str);
            AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
            if (pkg == null) {
                throw DomainVerificationUtils.throwPackageUnavailable(str);
            }
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState == null) {
                return null;
            }
            ArraySet<String> collectAllWebDomains = this.mCollector.collectAllWebDomains(pkg);
            int size = collectAllWebDomains.size();
            ArrayMap arrayMap = new ArrayMap(size);
            ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
            DomainVerificationInternalUserState userState = domainVerificationPkgState.getUserState(i);
            Set emptySet = userState == null ? Collections.emptySet() : userState.getEnabledHosts();
            int i2 = 0;
            while (true) {
                z = true;
                int i3 = 1;
                z = true;
                if (i2 >= size) {
                    break;
                }
                String valueAt = collectAllWebDomains.valueAt(i2);
                Integer num = stateMap.get(valueAt);
                if (num != null && DomainVerificationState.isVerified(num.intValue())) {
                    i3 = 2;
                } else if (!emptySet.contains(valueAt)) {
                    i3 = 0;
                }
                arrayMap.put(valueAt, Integer.valueOf(i3));
                i2++;
            }
            if (userState != null && !userState.isLinkHandlingAllowed()) {
                z = false;
            }
            return new DomainVerificationUserState(domainVerificationPkgState.getId(), str, UserHandle.of(i), z, arrayMap);
        }
    }

    public List<DomainOwner> getOwnersForDomain(String str, int i) {
        Objects.requireNonNull(str);
        this.mEnforcer.assertOwnerQuerent(this.mConnection.getCallingUid(), this.mConnection.getCallingUserId(), i);
        SparseArray<List<String>> ownersForDomainInternal = getOwnersForDomainInternal(str, false, i, this.mConnection.snapshot());
        if (ownersForDomainInternal.size() == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        int size = ownersForDomainInternal.size();
        for (int i2 = 0; i2 < size; i2++) {
            boolean z = ownersForDomainInternal.keyAt(i2) <= 3;
            List<String> valueAt = ownersForDomainInternal.valueAt(i2);
            int size2 = valueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                arrayList.add(new DomainOwner(valueAt.get(i3), z));
            }
        }
        return arrayList;
    }

    public final SparseArray<List<String>> getOwnersForDomainInternal(String str, boolean z, final int i, final Computer computer) {
        int i2;
        SparseArray<List<String>> sparseArray = new SparseArray<>();
        synchronized (this.mLock) {
            int size = this.mAttachedPkgStates.size();
            for (int i3 = 0; i3 < size; i3++) {
                String packageName = this.mAttachedPkgStates.valueAt(i3).getPackageName();
                PackageStateInternal packageStateInternal = computer.getPackageStateInternal(packageName);
                if (packageStateInternal != null) {
                    int approvalLevelForDomain = approvalLevelForDomain(packageStateInternal, str, z, i, str);
                    if (z || approvalLevelForDomain > 0) {
                        List<String> list = sparseArray.get(approvalLevelForDomain);
                        if (list == null) {
                            list = new ArrayList<>();
                            sparseArray.put(approvalLevelForDomain, list);
                        }
                        list.add(packageName);
                    }
                }
            }
        }
        int size2 = sparseArray.size();
        if (size2 == 0) {
            return sparseArray;
        }
        for (i2 = 0; i2 < size2; i2++) {
            sparseArray.valueAt(i2).sort(new Comparator() { // from class: com.android.server.pm.verify.domain.DomainVerificationService$$ExternalSyntheticLambda1
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$getOwnersForDomainInternal$0;
                    lambda$getOwnersForDomainInternal$0 = DomainVerificationService.lambda$getOwnersForDomainInternal$0(Computer.this, i, (String) obj, (String) obj2);
                    return lambda$getOwnersForDomainInternal$0;
                }
            });
        }
        return sparseArray;
    }

    public static /* synthetic */ int lambda$getOwnersForDomainInternal$0(Computer computer, int i, String str, String str2) {
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
        PackageStateInternal packageStateInternal2 = computer.getPackageStateInternal(str2);
        long firstInstallTimeMillis = packageStateInternal == null ? -1L : packageStateInternal.getUserStateOrDefault(i).getFirstInstallTimeMillis();
        long firstInstallTimeMillis2 = packageStateInternal2 != null ? packageStateInternal2.getUserStateOrDefault(i).getFirstInstallTimeMillis() : -1L;
        return firstInstallTimeMillis != firstInstallTimeMillis2 ? (int) (firstInstallTimeMillis - firstInstallTimeMillis2) : str.compareToIgnoreCase(str2);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public UUID generateNewId() {
        return UUID.randomUUID();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void migrateState(PackageStateInternal packageStateInternal, PackageStateInternal packageStateInternal2) {
        String packageName = packageStateInternal2.getPackageName();
        synchronized (this.mLock) {
            UUID domainSetId = packageStateInternal.getDomainSetId();
            UUID domainSetId2 = packageStateInternal2.getDomainSetId();
            DomainVerificationPkgState remove = this.mAttachedPkgStates.remove(domainSetId);
            AndroidPackageInternal pkg = packageStateInternal.getPkg();
            AndroidPackageInternal pkg2 = packageStateInternal2.getPkg();
            ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
            SparseArray sparseArray = new SparseArray();
            if (remove != null && pkg != null && pkg2 != null) {
                ArrayMap<String, Integer> stateMap = remove.getStateMap();
                ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(pkg2);
                int size = collectValidAutoVerifyDomains.size();
                for (int i = 0; i < size; i++) {
                    String valueAt = collectValidAutoVerifyDomains.valueAt(i);
                    Integer num = stateMap.get(valueAt);
                    if (num != null) {
                        int intValue = num.intValue();
                        if (DomainVerificationState.shouldMigrate(intValue)) {
                            arrayMap.put(valueAt, Integer.valueOf(intValue));
                        }
                    }
                }
                SparseArray<DomainVerificationInternalUserState> userStates = remove.getUserStates();
                int size2 = userStates.size();
                if (size2 > 0) {
                    ArraySet<String> collectAllWebDomains = this.mCollector.collectAllWebDomains(pkg2);
                    int i2 = 0;
                    while (i2 < size2) {
                        int keyAt = userStates.keyAt(i2);
                        DomainVerificationInternalUserState valueAt2 = userStates.valueAt(i2);
                        SparseArray<DomainVerificationInternalUserState> sparseArray2 = userStates;
                        ArraySet arraySet = new ArraySet((ArraySet) valueAt2.getEnabledHosts());
                        arraySet.retainAll(collectAllWebDomains);
                        sparseArray.put(keyAt, new DomainVerificationInternalUserState(keyAt, arraySet, valueAt2.isLinkHandlingAllowed()));
                        i2++;
                        userStates = sparseArray2;
                    }
                }
                boolean z = size > 0;
                boolean z2 = z && applyImmutableState(packageStateInternal2, arrayMap, collectValidAutoVerifyDomains);
                this.mAttachedPkgStates.put(packageName, domainSetId2, new DomainVerificationPkgState(packageName, domainSetId2, z, arrayMap, sparseArray, null));
                if (z2) {
                    sendBroadcast(packageName);
                    return;
                }
                return;
            }
            Slog.wtf("DomainVerificationService", "Invalid state nullability old state = " + remove + ", old pkgSetting = " + packageStateInternal + ", new pkgSetting = " + packageStateInternal2 + ", old pkg = " + pkg + ", new pkg = " + pkg2, new Exception());
            this.mAttachedPkgStates.put(packageName, domainSetId2, new DomainVerificationPkgState(packageName, domainSetId2, true, arrayMap, sparseArray, null));
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void addPackage(PackageStateInternal packageStateInternal) {
        boolean z;
        DomainVerificationPkgState domainVerificationPkgState;
        UUID domainSetId = packageStateInternal.getDomainSetId();
        String packageName = packageStateInternal.getPackageName();
        DomainVerificationPkgState removePendingState = this.mSettings.removePendingState(packageName);
        ArraySet<String> arraySet = null;
        if (removePendingState != null) {
            z = false;
        } else {
            removePendingState = this.mSettings.removeRestoredState(packageName);
            if (removePendingState != null && !Objects.equals(removePendingState.getBackupSignatureHash(), PackageUtils.computeSignaturesSha256Digest(packageStateInternal.getSigningDetails().getSignatures()))) {
                removePendingState = null;
            }
            z = true;
        }
        AndroidPackageInternal pkg = packageStateInternal.getPkg();
        ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(pkg);
        boolean z2 = !collectValidAutoVerifyDomains.isEmpty();
        boolean z3 = removePendingState != null;
        if (z3) {
            domainVerificationPkgState = new DomainVerificationPkgState(removePendingState, domainSetId, z2);
            domainVerificationPkgState.getStateMap().retainAll(collectValidAutoVerifyDomains);
            ArraySet<String> collectAllWebDomains = this.mCollector.collectAllWebDomains(pkg);
            SparseArray<DomainVerificationInternalUserState> userStates = domainVerificationPkgState.getUserStates();
            int size = userStates.size();
            for (int i = 0; i < size; i++) {
                userStates.valueAt(i).retainHosts(collectAllWebDomains);
            }
        } else {
            domainVerificationPkgState = new DomainVerificationPkgState(packageName, domainSetId, z2);
        }
        if (applyImmutableState(packageStateInternal, domainVerificationPkgState.getStateMap(), collectValidAutoVerifyDomains) && !z3) {
            SparseIntArray userStates2 = this.mLegacySettings.getUserStates(packageName);
            int size2 = userStates2 == null ? 0 : userStates2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                int keyAt = userStates2.keyAt(i2);
                if (userStates2.valueAt(i2) == 2) {
                    if (arraySet == null) {
                        arraySet = this.mCollector.collectAllWebDomains(pkg);
                    }
                    domainVerificationPkgState.getOrCreateUserState(keyAt).addHosts(arraySet);
                }
            }
            IntentFilterVerificationInfo remove = this.mLegacySettings.remove(packageName);
            if (remove != null && remove.getStatus() == 2) {
                ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
                int size3 = collectValidAutoVerifyDomains.size();
                for (int i3 = 0; i3 < size3; i3++) {
                    stateMap.put(collectValidAutoVerifyDomains.valueAt(i3), 4);
                }
            }
        }
        synchronized (this.mLock) {
            this.mAttachedPkgStates.put(packageName, domainSetId, domainVerificationPkgState);
        }
        if (z && z2) {
            sendBroadcast(packageName);
        }
    }

    public final boolean applyImmutableState(PackageStateInternal packageStateInternal, ArrayMap<String, Integer> arrayMap, ArraySet<String> arraySet) {
        if (packageStateInternal.isSystem() && this.mSystemConfig.getLinkedApps().contains(packageStateInternal.getPackageName())) {
            int size = arraySet.size();
            for (int i = 0; i < size; i++) {
                arrayMap.put(arraySet.valueAt(i), 7);
            }
            return false;
        }
        for (int size2 = arrayMap.size() - 1; size2 >= 0; size2--) {
            if (arrayMap.valueAt(size2).intValue() == 7) {
                arrayMap.removeAt(size2);
            }
        }
        return true;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void writeSettings(final Computer computer, TypedXmlSerializer typedXmlSerializer, boolean z, int i) throws IOException {
        synchronized (this.mLock) {
            this.mSettings.writeSettings(typedXmlSerializer, this.mAttachedPkgStates, i, z ? new Function() { // from class: com.android.server.pm.verify.domain.DomainVerificationService$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$writeSettings$1;
                    lambda$writeSettings$1 = DomainVerificationService.lambda$writeSettings$1(Computer.this, (String) obj);
                    return lambda$writeSettings$1;
                }
            } : null);
        }
        this.mLegacySettings.writeSettings(typedXmlSerializer);
    }

    public static /* synthetic */ String lambda$writeSettings$1(Computer computer, String str) {
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
        if (packageStateInternal == null) {
            return null;
        }
        return PackageUtils.computeSignaturesSha256Digest(packageStateInternal.getSigningDetails().getSignatures());
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void readSettings(Computer computer, TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        synchronized (this.mLock) {
            this.mSettings.readSettings(typedXmlPullParser, this.mAttachedPkgStates, computer);
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void readLegacySettings(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        this.mLegacySettings.readSettings(typedXmlPullParser);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void restoreSettings(Computer computer, TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        synchronized (this.mLock) {
            this.mSettings.restoreSettings(typedXmlPullParser, this.mAttachedPkgStates, computer);
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void addLegacySetting(String str, IntentFilterVerificationInfo intentFilterVerificationInfo) {
        this.mLegacySettings.add(str, intentFilterVerificationInfo);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public boolean setLegacyUserState(String str, int i, int i2) {
        if (this.mEnforcer.callerIsLegacyUserSelector(this.mConnection.getCallingUid(), this.mConnection.getCallingUserId(), str, i)) {
            this.mLegacySettings.add(str, i, i2);
            this.mConnection.scheduleWriteSettings();
            return true;
        }
        return false;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public int getLegacyState(String str, int i) {
        if (this.mEnforcer.callerIsLegacyUserQuerent(this.mConnection.getCallingUid(), this.mConnection.getCallingUserId(), str, i)) {
            return this.mLegacySettings.getUserState(str, i);
        }
        return 0;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void clearPackage(String str) {
        synchronized (this.mLock) {
            this.mAttachedPkgStates.remove(str);
            this.mSettings.removePackage(str);
        }
        this.mConnection.scheduleWriteSettings();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void clearPackageForUser(String str, int i) {
        synchronized (this.mLock) {
            DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
            if (domainVerificationPkgState != null) {
                domainVerificationPkgState.removeUser(i);
            }
            this.mSettings.removePackageForUser(str, i);
        }
        this.mConnection.scheduleWriteSettings();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void clearUser(int i) {
        synchronized (this.mLock) {
            int size = this.mAttachedPkgStates.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mAttachedPkgStates.valueAt(i2).removeUser(i);
            }
            this.mSettings.removeUser(i);
        }
        this.mConnection.scheduleWriteSettings();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public boolean runMessage(int i, Object obj) {
        return this.mProxy.runMessage(i, obj);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void printState(IndentingPrintWriter indentingPrintWriter, String str, Integer num) throws PackageManager.NameNotFoundException {
        printState(this.mConnection.snapshot(), indentingPrintWriter, str, num);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public void printState(Computer computer, IndentingPrintWriter indentingPrintWriter, String str, Integer num) throws PackageManager.NameNotFoundException {
        this.mEnforcer.assertApprovedQuerent(this.mConnection.getCallingUid(), this.mProxy);
        synchronized (this.mLock) {
            this.mDebug.printState(indentingPrintWriter, str, num, computer, this.mAttachedPkgStates);
        }
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void printOwnersForPackage(IndentingPrintWriter indentingPrintWriter, String str, Integer num) throws PackageManager.NameNotFoundException {
        this.mEnforcer.assertApprovedQuerent(this.mConnection.getCallingUid(), this.mProxy);
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            if (str == null) {
                int size = this.mAttachedPkgStates.size();
                for (int i = 0; i < size; i++) {
                    try {
                        printOwnersForPackage(indentingPrintWriter, this.mAttachedPkgStates.valueAt(i).getPackageName(), num, snapshot);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            } else {
                printOwnersForPackage(indentingPrintWriter, str, num, snapshot);
            }
        }
    }

    public final void printOwnersForPackage(IndentingPrintWriter indentingPrintWriter, String str, Integer num, Computer computer) throws PackageManager.NameNotFoundException {
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
        AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
        if (pkg == null) {
            throw DomainVerificationUtils.throwPackageUnavailable(str);
        }
        ArraySet<String> collectAllWebDomains = this.mCollector.collectAllWebDomains(pkg);
        int size = collectAllWebDomains.size();
        if (size == 0) {
            return;
        }
        indentingPrintWriter.println(str + XmlUtils.STRING_ARRAY_SEPARATOR);
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < size; i++) {
            printOwnersForDomain(indentingPrintWriter, collectAllWebDomains.valueAt(i), num, computer);
        }
        indentingPrintWriter.decreaseIndent();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void printOwnersForDomains(IndentingPrintWriter indentingPrintWriter, List<String> list, Integer num) {
        this.mEnforcer.assertApprovedQuerent(this.mConnection.getCallingUid(), this.mProxy);
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                printOwnersForDomain(indentingPrintWriter, list.get(i), num, snapshot);
            }
        }
    }

    public final void printOwnersForDomain(IndentingPrintWriter indentingPrintWriter, String str, Integer num, Computer computer) {
        int[] allUserIds;
        SparseArray<SparseArray<List<String>>> sparseArray = new SparseArray<>();
        if (num == null || num.intValue() == -1) {
            for (int i : this.mConnection.getAllUserIds()) {
                sparseArray.put(i, getOwnersForDomainInternal(str, true, i, computer));
            }
        } else {
            sparseArray.put(num.intValue(), getOwnersForDomainInternal(str, true, num.intValue(), computer));
        }
        this.mDebug.printOwners(indentingPrintWriter, str, sparseArray);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public DomainVerificationShell getShell() {
        return this.mShell;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public DomainVerificationCollector getCollector() {
        return this.mCollector;
    }

    public final void sendBroadcast(String str) {
        sendBroadcast(Collections.singleton(str));
    }

    public final void sendBroadcast(Set<String> set) {
        if (this.mCanSendBroadcasts) {
            this.mProxy.sendBroadcastForPackages(set);
        }
    }

    public final boolean hasRealVerifier() {
        return !(this.mProxy instanceof DomainVerificationProxyUnavailable);
    }

    @GuardedBy({"mLock"})
    public final GetAttachedResult getAndValidateAttachedLocked(UUID uuid, Set<String> set, boolean z, int i, Integer num, Computer computer) throws PackageManager.NameNotFoundException {
        ArraySet<String> collectAllWebDomains;
        if (uuid == null) {
            throw new IllegalArgumentException("domainSetId cannot be null");
        }
        DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(uuid);
        if (domainVerificationPkgState == null) {
            return GetAttachedResult.error(1);
        }
        String packageName = domainVerificationPkgState.getPackageName();
        if (num != null && this.mConnection.filterAppAccess(packageName, i, num.intValue())) {
            return GetAttachedResult.error(1);
        }
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(packageName);
        if (packageStateInternal == null || packageStateInternal.getPkg() == null) {
            throw DomainVerificationUtils.throwPackageUnavailable(packageName);
        }
        if (CollectionUtils.isEmpty(set)) {
            throw new IllegalArgumentException("Provided domain set cannot be empty");
        }
        AndroidPackageInternal pkg = packageStateInternal.getPkg();
        if (z) {
            collectAllWebDomains = this.mCollector.collectValidAutoVerifyDomains(pkg);
        } else {
            collectAllWebDomains = this.mCollector.collectAllWebDomains(pkg);
        }
        if (set.retainAll(collectAllWebDomains)) {
            return GetAttachedResult.error(2);
        }
        return GetAttachedResult.success(domainVerificationPkgState);
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void verifyPackages(List<String> list, boolean z) {
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        Set<String> arraySet = new ArraySet<>();
        int i = 0;
        if (list == null) {
            synchronized (this.mLock) {
                int size = this.mAttachedPkgStates.size();
                while (i < size) {
                    addIfShouldBroadcastLocked(arraySet, this.mAttachedPkgStates.valueAt(i), z);
                    i++;
                }
            }
        } else {
            synchronized (this.mLock) {
                int size2 = list.size();
                while (i < size2) {
                    DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(list.get(i));
                    if (domainVerificationPkgState != null) {
                        addIfShouldBroadcastLocked(arraySet, domainVerificationPkgState, z);
                    }
                    i++;
                }
            }
        }
        if (arraySet.isEmpty()) {
            return;
        }
        sendBroadcast(arraySet);
    }

    @GuardedBy({"mLock"})
    public final void addIfShouldBroadcastLocked(Collection<String> collection, DomainVerificationPkgState domainVerificationPkgState, boolean z) {
        if ((z && domainVerificationPkgState.isHasAutoVerifyDomains()) || shouldReBroadcastPackage(domainVerificationPkgState)) {
            collection.add(domainVerificationPkgState.getPackageName());
        }
    }

    public final boolean shouldReBroadcastPackage(DomainVerificationPkgState domainVerificationPkgState) {
        if (domainVerificationPkgState.isHasAutoVerifyDomains()) {
            ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
            int size = stateMap.size();
            for (int i = 0; i < size; i++) {
                if (!DomainVerificationState.isDefault(stateMap.valueAt(i).intValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void clearDomainVerificationState(List<String> list) {
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        Computer snapshot = this.mConnection.snapshot();
        synchronized (this.mLock) {
            int i = 0;
            if (list == null) {
                int size = this.mAttachedPkgStates.size();
                while (i < size) {
                    DomainVerificationPkgState valueAt = this.mAttachedPkgStates.valueAt(i);
                    PackageStateInternal packageStateInternal = snapshot.getPackageStateInternal(valueAt.getPackageName());
                    if (packageStateInternal != null && packageStateInternal.getPkg() != null) {
                        resetDomainState(valueAt.getStateMap(), packageStateInternal);
                    }
                    i++;
                }
            } else {
                int size2 = list.size();
                while (i < size2) {
                    String str = list.get(i);
                    DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(str);
                    PackageStateInternal packageStateInternal2 = snapshot.getPackageStateInternal(str);
                    if (packageStateInternal2 != null && packageStateInternal2.getPkg() != null) {
                        resetDomainState(domainVerificationPkgState.getStateMap(), packageStateInternal2);
                    }
                    i++;
                }
            }
        }
        this.mConnection.scheduleWriteSettings();
    }

    public final void resetDomainState(ArrayMap<String, Integer> arrayMap, PackageStateInternal packageStateInternal) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            Integer valueAt = arrayMap.valueAt(size);
            int intValue = valueAt.intValue();
            if (intValue == 1 || intValue == 5 || valueAt.intValue() >= 1024) {
                arrayMap.removeAt(size);
            }
        }
        applyImmutableState(packageStateInternal, arrayMap, this.mCollector.collectValidAutoVerifyDomains(packageStateInternal.getPkg()));
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationShell.Callback
    public void clearUserStates(List<String> list, int i) {
        this.mEnforcer.assertInternal(this.mConnection.getCallingUid());
        synchronized (this.mLock) {
            int i2 = 0;
            if (list == null) {
                int size = this.mAttachedPkgStates.size();
                while (i2 < size) {
                    DomainVerificationPkgState valueAt = this.mAttachedPkgStates.valueAt(i2);
                    if (i == -1) {
                        valueAt.removeAllUsers();
                    } else {
                        valueAt.removeUser(i);
                    }
                    i2++;
                }
            } else {
                int size2 = list.size();
                while (i2 < size2) {
                    DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(list.get(i2));
                    if (i == -1) {
                        domainVerificationPkgState.removeAllUsers();
                    } else {
                        domainVerificationPkgState.removeUser(i);
                    }
                    i2++;
                }
            }
        }
        this.mConnection.scheduleWriteSettings();
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public Pair<List<ResolveInfo>, Integer> filterToApprovedApp(Intent intent, List<ResolveInfo> list, int i, Function<String, PackageStateInternal> function) {
        String host = intent.getData().getHost();
        ArrayMap<ResolveInfo, Integer> arrayMap = new ArrayMap<>();
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo = list.get(i2);
            if (resolveInfo.isAutoResolutionAllowed()) {
                arrayMap.put(resolveInfo, null);
            }
        }
        int fillMapWithApprovalLevels = fillMapWithApprovalLevels(arrayMap, host, i, function);
        if (fillMapWithApprovalLevels <= 0) {
            return Pair.create(Collections.emptyList(), Integer.valueOf(fillMapWithApprovalLevels));
        }
        for (int size2 = arrayMap.size() - 1; size2 >= 0; size2--) {
            if (arrayMap.valueAt(size2).intValue() != fillMapWithApprovalLevels) {
                arrayMap.removeAt(size2);
            }
        }
        if (fillMapWithApprovalLevels != 1) {
            filterToLastFirstInstalled(arrayMap, function);
        }
        int size3 = arrayMap.size();
        ArrayList arrayList = new ArrayList(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            arrayList.add(arrayMap.keyAt(i3));
        }
        if (fillMapWithApprovalLevels != 1) {
            filterToLastDeclared(arrayList, function);
        }
        return Pair.create(arrayList, Integer.valueOf(fillMapWithApprovalLevels));
    }

    public final int fillMapWithApprovalLevels(ArrayMap<ResolveInfo, Integer> arrayMap, String str, int i, Function<String, PackageStateInternal> function) {
        int size = arrayMap.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (arrayMap.valueAt(i3) == null) {
                String str2 = arrayMap.keyAt(i3).getComponentInfo().packageName;
                PackageStateInternal apply = function.apply(str2);
                if (apply == null) {
                    fillInfoMapForSamePackage(arrayMap, str2, 0);
                } else {
                    int approvalLevelForDomain = approvalLevelForDomain(apply, str, false, i, str);
                    i2 = Math.max(i2, approvalLevelForDomain);
                    fillInfoMapForSamePackage(arrayMap, str2, approvalLevelForDomain);
                }
            }
        }
        return i2;
    }

    public final void fillInfoMapForSamePackage(ArrayMap<ResolveInfo, Integer> arrayMap, String str, int i) {
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (Objects.equals(str, arrayMap.keyAt(i2).getComponentInfo().packageName)) {
                arrayMap.setValueAt(i2, Integer.valueOf(i));
            }
        }
    }

    public final void filterToLastFirstInstalled(ArrayMap<ResolveInfo, Integer> arrayMap, Function<String, PackageStateInternal> function) {
        int size = arrayMap.size();
        String str = null;
        long j = Long.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            String str2 = arrayMap.keyAt(i).getComponentInfo().packageName;
            PackageStateInternal apply = function.apply(str2);
            if (apply != null) {
                long earliestFirstInstallTime = PackageStateUtils.getEarliestFirstInstallTime(apply.getUserStates());
                if (earliestFirstInstallTime > j) {
                    str = str2;
                    j = earliestFirstInstallTime;
                }
            }
        }
        for (int size2 = arrayMap.size() - 1; size2 >= 0; size2--) {
            if (!Objects.equals(str, arrayMap.keyAt(size2).getComponentInfo().packageName)) {
                arrayMap.removeAt(size2);
            }
        }
    }

    public final void filterToLastDeclared(List<ResolveInfo> list, Function<String, PackageStateInternal> function) {
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo resolveInfo = list.get(i);
            String str = resolveInfo.getComponentInfo().packageName;
            PackageStateInternal apply = function.apply(str);
            AndroidPackageInternal pkg = apply == null ? null : apply.getPkg();
            if (pkg != null) {
                int indexOfIntentFilterEntry = indexOfIntentFilterEntry(pkg, resolveInfo);
                int size = list.size();
                while (true) {
                    size--;
                    if (size < i + 1) {
                        break;
                    }
                    ResolveInfo resolveInfo2 = list.get(size);
                    if (Objects.equals(str, resolveInfo2.getComponentInfo().packageName)) {
                        int indexOfIntentFilterEntry2 = indexOfIntentFilterEntry(pkg, resolveInfo2);
                        if (indexOfIntentFilterEntry2 > indexOfIntentFilterEntry) {
                            resolveInfo = resolveInfo2;
                            indexOfIntentFilterEntry = indexOfIntentFilterEntry2;
                        }
                        list.remove(size);
                    }
                }
                list.set(i, resolveInfo);
            }
        }
    }

    public final int indexOfIntentFilterEntry(AndroidPackage androidPackage, ResolveInfo resolveInfo) {
        List<ParsedActivity> activities = androidPackage.getActivities();
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            if (Objects.equals(activities.get(i).getComponentName(), resolveInfo.getComponentInfo().getComponentName())) {
                return i;
            }
        }
        return -1;
    }

    @Override // com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal
    public int approvalLevelForDomain(PackageStateInternal packageStateInternal, Intent intent, long j, int i) {
        packageStateInternal.getPackageName();
        if (DomainVerificationUtils.isDomainVerificationIntent(intent, j)) {
            return approvalLevelForDomain(packageStateInternal, intent.getData().getHost(), false, i, intent);
        }
        return 0;
    }

    public final int approvalLevelForDomain(PackageStateInternal packageStateInternal, String str, boolean z, int i, Object obj) {
        int approvalLevelForDomainInternal = approvalLevelForDomainInternal(packageStateInternal, str, z, i, obj);
        if (z && approvalLevelForDomainInternal == 0) {
            PackageUserStateInternal userStateOrDefault = packageStateInternal.getUserStateOrDefault(i);
            if (!userStateOrDefault.isInstalled()) {
                return -4;
            }
            AndroidPackageInternal pkg = packageStateInternal.getPkg();
            if (pkg != null) {
                if (!PackageUserStateUtils.isPackageEnabled(userStateOrDefault, pkg)) {
                    return -3;
                }
                if (this.mCollector.containsAutoVerifyDomain(packageStateInternal.getPkg(), str)) {
                    return -1;
                }
            }
        }
        return approvalLevelForDomainInternal;
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0056, code lost:
        if (r4 != 4) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int approvalLevelForDomainInternal(PackageStateInternal packageStateInternal, String str, boolean z, int i, Object obj) {
        String packageName = packageStateInternal.getPackageName();
        AndroidPackageInternal pkg = packageStateInternal.getPkg();
        if (pkg == null || !z || this.mCollector.containsWebDomain(pkg, str)) {
            PackageUserStateInternal packageUserStateInternal = packageStateInternal.getUserStates().get(i);
            if (packageUserStateInternal != null && packageUserStateInternal.isInstalled() && PackageUserStateUtils.isPackageEnabled(packageUserStateInternal, pkg) && !packageUserStateInternal.isSuspended()) {
                if (pkg != null && !DomainVerificationUtils.isChangeEnabled(this.mPlatformCompat, pkg, 178111421L)) {
                    int userState = this.mLegacySettings.getUserState(packageName, i);
                    if (userState != 1) {
                        if (userState == 2) {
                            return 2;
                        }
                        if (userState == 3) {
                            return 0;
                        }
                    }
                    return 1;
                }
                synchronized (this.mLock) {
                    DomainVerificationPkgState domainVerificationPkgState = this.mAttachedPkgStates.get(packageName);
                    if (domainVerificationPkgState == null) {
                        return 0;
                    }
                    DomainVerificationInternalUserState userState2 = domainVerificationPkgState.getUserState(i);
                    if (userState2 == null || userState2.isLinkHandlingAllowed()) {
                        if (pkg != null && packageStateInternal.getUserStateOrDefault(i).isInstantApp() && this.mCollector.collectValidAutoVerifyDomains(pkg).contains(str)) {
                            return 5;
                        }
                        ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
                        Integer num = stateMap.get(str);
                        if (num == null || !DomainVerificationState.isVerified(num.intValue())) {
                            int size = stateMap.size();
                            for (int i2 = 0; i2 < size; i2++) {
                                if (DomainVerificationState.isVerified(stateMap.valueAt(i2).intValue())) {
                                    String keyAt = stateMap.keyAt(i2);
                                    if (keyAt.startsWith("*.") && str.endsWith(keyAt.substring(2))) {
                                        return 4;
                                    }
                                }
                            }
                            if (userState2 == null) {
                                return 0;
                            }
                            ArraySet<String> enabledHosts = userState2.getEnabledHosts();
                            if (enabledHosts.contains(str)) {
                                return 3;
                            }
                            int size2 = enabledHosts.size();
                            for (int i3 = 0; i3 < size2; i3++) {
                                String valueAt = enabledHosts.valueAt(i3);
                                if (valueAt.startsWith("*.") && str.endsWith(valueAt.substring(2))) {
                                    return 3;
                                }
                            }
                            return 0;
                        }
                        return 4;
                    }
                    return 0;
                }
            }
            return 0;
        }
        return -2;
    }

    @GuardedBy({"mLock"})
    public final Pair<List<String>, Integer> getApprovedPackagesLocked(String str, int i, int i2, Computer computer) {
        int approvalLevelForDomain;
        boolean z = i2 < 0;
        List emptyList = Collections.emptyList();
        int size = this.mAttachedPkgStates.size();
        List list = emptyList;
        int i3 = i2;
        for (int i4 = 0; i4 < size; i4++) {
            String packageName = this.mAttachedPkgStates.valueAt(i4).getPackageName();
            PackageStateInternal packageStateInternal = computer.getPackageStateInternal(packageName);
            if (packageStateInternal != null && (approvalLevelForDomain = approvalLevelForDomain(packageStateInternal, str, z, i, str)) >= i2) {
                if (approvalLevelForDomain > i3) {
                    list.clear();
                    i3 = approvalLevelForDomain;
                    list = CollectionUtils.add(list, packageName);
                } else if (approvalLevelForDomain == i3) {
                    list = CollectionUtils.add(list, packageName);
                }
            }
        }
        if (list.isEmpty()) {
            return Pair.create(list, 0);
        }
        ArrayList arrayList = new ArrayList();
        int size2 = list.size();
        long j = Long.MIN_VALUE;
        for (int i5 = 0; i5 < size2; i5++) {
            String str2 = (String) list.get(i5);
            PackageStateInternal packageStateInternal2 = computer.getPackageStateInternal(str2);
            if (packageStateInternal2 != null) {
                long firstInstallTimeMillis = packageStateInternal2.getUserStateOrDefault(i).getFirstInstallTimeMillis();
                int i6 = (firstInstallTimeMillis > j ? 1 : (firstInstallTimeMillis == j ? 0 : -1));
                if (i6 > 0) {
                    arrayList.clear();
                    arrayList.add(str2);
                    j = firstInstallTimeMillis;
                } else if (i6 == 0) {
                    arrayList.add(str2);
                }
            }
        }
        return Pair.create(arrayList, Integer.valueOf(i3));
    }

    /* renamed from: com.android.server.pm.verify.domain.DomainVerificationService$GetAttachedResult */
    /* loaded from: classes2.dex */
    public static class GetAttachedResult {
        public int mErrorCode;
        public DomainVerificationPkgState mPkgState;

        public GetAttachedResult(DomainVerificationPkgState domainVerificationPkgState, int i) {
            this.mPkgState = domainVerificationPkgState;
            this.mErrorCode = i;
        }

        public static GetAttachedResult error(@DomainVerificationManager.Error int i) {
            return new GetAttachedResult(null, i);
        }

        public static GetAttachedResult success(DomainVerificationPkgState domainVerificationPkgState) {
            return new GetAttachedResult(domainVerificationPkgState, 0);
        }

        public DomainVerificationPkgState getPkgState() {
            return this.mPkgState;
        }

        public boolean isError() {
            return this.mErrorCode != 0;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }
    }
}
