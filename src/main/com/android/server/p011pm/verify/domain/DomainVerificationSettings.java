package com.android.server.p011pm.verify.domain;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.verify.domain.DomainVerificationPersistence;
import com.android.server.p011pm.verify.domain.models.DomainVerificationInternalUserState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationPkgState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationStateMap;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationSettings */
/* loaded from: classes2.dex */
public class DomainVerificationSettings {
    public final DomainVerificationCollector mCollector;
    @GuardedBy({"mLock"})
    public final ArrayMap<String, DomainVerificationPkgState> mPendingPkgStates = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, DomainVerificationPkgState> mRestoredPkgStates = new ArrayMap<>();
    public final Object mLock = new Object();

    public DomainVerificationSettings(DomainVerificationCollector domainVerificationCollector) {
        this.mCollector = domainVerificationCollector;
    }

    public void writeSettings(TypedXmlSerializer typedXmlSerializer, DomainVerificationStateMap<DomainVerificationPkgState> domainVerificationStateMap, int i, Function<String, String> function) throws IOException {
        synchronized (this.mLock) {
            DomainVerificationPersistence.writeToXml(typedXmlSerializer, domainVerificationStateMap, this.mPendingPkgStates, this.mRestoredPkgStates, i, function);
        }
    }

    public void readSettings(TypedXmlPullParser typedXmlPullParser, DomainVerificationStateMap<DomainVerificationPkgState> domainVerificationStateMap, Computer computer) throws IOException, XmlPullParserException {
        DomainVerificationPersistence.ReadResult readFromXml = DomainVerificationPersistence.readFromXml(typedXmlPullParser);
        ArrayMap<String, DomainVerificationPkgState> arrayMap = readFromXml.active;
        ArrayMap<String, DomainVerificationPkgState> arrayMap2 = readFromXml.restored;
        synchronized (this.mLock) {
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                DomainVerificationPkgState valueAt = arrayMap.valueAt(i);
                String packageName = valueAt.getPackageName();
                DomainVerificationPkgState domainVerificationPkgState = domainVerificationStateMap.get(packageName);
                if (domainVerificationPkgState != null) {
                    if (!domainVerificationPkgState.getId().equals(valueAt.getId())) {
                        mergePkgState(domainVerificationPkgState, valueAt, computer);
                    }
                } else {
                    this.mPendingPkgStates.put(packageName, valueAt);
                }
            }
            int size2 = arrayMap2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                DomainVerificationPkgState valueAt2 = arrayMap2.valueAt(i2);
                this.mRestoredPkgStates.put(valueAt2.getPackageName(), valueAt2);
            }
        }
    }

    public void restoreSettings(TypedXmlPullParser typedXmlPullParser, DomainVerificationStateMap<DomainVerificationPkgState> domainVerificationStateMap, Computer computer) throws IOException, XmlPullParserException {
        DomainVerificationPersistence.ReadResult readFromXml = DomainVerificationPersistence.readFromXml(typedXmlPullParser);
        ArrayMap<String, DomainVerificationPkgState> arrayMap = readFromXml.restored;
        arrayMap.putAll((ArrayMap<? extends String, ? extends DomainVerificationPkgState>) readFromXml.active);
        synchronized (this.mLock) {
            for (int i = 0; i < arrayMap.size(); i++) {
                DomainVerificationPkgState valueAt = arrayMap.valueAt(i);
                String packageName = valueAt.getPackageName();
                DomainVerificationPkgState domainVerificationPkgState = domainVerificationStateMap.get(packageName);
                if (domainVerificationPkgState == null) {
                    domainVerificationPkgState = this.mPendingPkgStates.get(packageName);
                }
                if (domainVerificationPkgState == null) {
                    domainVerificationPkgState = this.mRestoredPkgStates.get(packageName);
                }
                if (domainVerificationPkgState != null) {
                    mergePkgState(domainVerificationPkgState, valueAt, computer);
                } else {
                    ArrayMap<String, Integer> stateMap = valueAt.getStateMap();
                    for (int size = stateMap.size() - 1; size >= 0; size--) {
                        Integer valueAt2 = stateMap.valueAt(size);
                        if (valueAt2 != null) {
                            int intValue = valueAt2.intValue();
                            if (intValue != 1 && intValue != 5) {
                                stateMap.removeAt(size);
                            }
                            stateMap.setValueAt(size, 5);
                        }
                    }
                    this.mRestoredPkgStates.put(packageName, valueAt);
                }
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void mergePkgState(DomainVerificationPkgState domainVerificationPkgState, DomainVerificationPkgState domainVerificationPkgState2, Computer computer) {
        Integer num;
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(domainVerificationPkgState.getPackageName());
        AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
        Set emptySet = pkg == null ? Collections.emptySet() : this.mCollector.collectValidAutoVerifyDomains(pkg);
        ArrayMap<String, Integer> stateMap = domainVerificationPkgState.getStateMap();
        ArrayMap<String, Integer> stateMap2 = domainVerificationPkgState2.getStateMap();
        int size = stateMap2.size();
        for (int i = 0; i < size; i++) {
            String keyAt = stateMap2.keyAt(i);
            Integer valueAt = stateMap2.valueAt(i);
            if (emptySet.contains(keyAt) && (((num = stateMap.get(keyAt)) == null || num.intValue() == 0) && (valueAt.intValue() == 1 || valueAt.intValue() == 5))) {
                stateMap.put(keyAt, 5);
            }
        }
        SparseArray<DomainVerificationInternalUserState> userStates = domainVerificationPkgState.getUserStates();
        SparseArray<DomainVerificationInternalUserState> userStates2 = domainVerificationPkgState2.getUserStates();
        int size2 = userStates2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            int keyAt2 = userStates2.keyAt(i2);
            DomainVerificationInternalUserState valueAt2 = userStates2.valueAt(i2);
            if (valueAt2 != null) {
                ArraySet<String> enabledHosts = valueAt2.getEnabledHosts();
                DomainVerificationInternalUserState domainVerificationInternalUserState = userStates.get(keyAt2);
                boolean isLinkHandlingAllowed = valueAt2.isLinkHandlingAllowed();
                if (domainVerificationInternalUserState == null) {
                    userStates.put(keyAt2, new DomainVerificationInternalUserState(keyAt2, enabledHosts, isLinkHandlingAllowed));
                } else {
                    domainVerificationInternalUserState.addHosts(enabledHosts).setLinkHandlingAllowed(isLinkHandlingAllowed);
                }
            }
        }
    }

    public void removePackage(String str) {
        synchronized (this.mLock) {
            this.mPendingPkgStates.remove(str);
            this.mRestoredPkgStates.remove(str);
        }
    }

    public void removePackageForUser(String str, int i) {
        synchronized (this.mLock) {
            DomainVerificationPkgState domainVerificationPkgState = this.mPendingPkgStates.get(str);
            if (domainVerificationPkgState != null) {
                domainVerificationPkgState.removeUser(i);
            }
            DomainVerificationPkgState domainVerificationPkgState2 = this.mRestoredPkgStates.get(str);
            if (domainVerificationPkgState2 != null) {
                domainVerificationPkgState2.removeUser(i);
            }
        }
    }

    public void removeUser(int i) {
        synchronized (this.mLock) {
            int size = this.mPendingPkgStates.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mPendingPkgStates.valueAt(i2).removeUser(i);
            }
            int size2 = this.mRestoredPkgStates.size();
            for (int i3 = 0; i3 < size2; i3++) {
                this.mRestoredPkgStates.valueAt(i3).removeUser(i);
            }
        }
    }

    public DomainVerificationPkgState removePendingState(String str) {
        DomainVerificationPkgState remove;
        synchronized (this.mLock) {
            remove = this.mPendingPkgStates.remove(str);
        }
        return remove;
    }

    public DomainVerificationPkgState removeRestoredState(String str) {
        DomainVerificationPkgState remove;
        synchronized (this.mLock) {
            remove = this.mRestoredPkgStates.remove(str);
        }
        return remove;
    }
}
