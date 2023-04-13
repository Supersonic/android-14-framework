package com.android.server.p011pm.verify.domain;

import android.content.pm.PackageManager;
import android.content.pm.verify.domain.DomainVerificationState;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.PackageUtils;
import android.util.SparseArray;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.verify.domain.models.DomainVerificationInternalUserState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationPkgState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationStateMap;
import java.util.Arrays;
import java.util.List;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationDebug */
/* loaded from: classes2.dex */
public class DomainVerificationDebug {
    public final DomainVerificationCollector mCollector;

    public DomainVerificationDebug(DomainVerificationCollector domainVerificationCollector) {
        this.mCollector = domainVerificationCollector;
    }

    public void printState(IndentingPrintWriter indentingPrintWriter, String str, Integer num, Computer computer, DomainVerificationStateMap<DomainVerificationPkgState> domainVerificationStateMap) throws PackageManager.NameNotFoundException {
        int i;
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        ArraySet<String> arraySet = new ArraySet<>();
        if (str == null) {
            int size = domainVerificationStateMap.size();
            int i2 = 0;
            while (i2 < size) {
                DomainVerificationPkgState valueAt = domainVerificationStateMap.valueAt(i2);
                PackageStateInternal packageStateInternal = computer.getPackageStateInternal(valueAt.getPackageName());
                if (packageStateInternal == null || packageStateInternal.getPkg() == null) {
                    i = i2;
                } else {
                    i = i2;
                    printState(indentingPrintWriter, valueAt, packageStateInternal.getPkg(), num, arraySet, printState(indentingPrintWriter, valueAt, (AndroidPackage) packageStateInternal.getPkg(), arrayMap, false));
                }
                i2 = i + 1;
            }
            return;
        }
        DomainVerificationPkgState domainVerificationPkgState = domainVerificationStateMap.get(str);
        if (domainVerificationPkgState == null) {
            throw DomainVerificationUtils.throwPackageUnavailable(str);
        }
        PackageStateInternal packageStateInternal2 = computer.getPackageStateInternal(str);
        if (packageStateInternal2 == null || packageStateInternal2.getPkg() == null) {
            throw DomainVerificationUtils.throwPackageUnavailable(str);
        }
        AndroidPackageInternal pkg = packageStateInternal2.getPkg();
        printState(indentingPrintWriter, domainVerificationPkgState, (AndroidPackage) pkg, arrayMap, false);
        printState(indentingPrintWriter, domainVerificationPkgState, pkg, num, arraySet, true);
    }

    public void printOwners(IndentingPrintWriter indentingPrintWriter, String str, SparseArray<SparseArray<List<String>>> sparseArray) {
        indentingPrintWriter.println(str + XmlUtils.STRING_ARRAY_SEPARATOR);
        indentingPrintWriter.increaseIndent();
        if (sparseArray.size() == 0) {
            indentingPrintWriter.println("none");
            indentingPrintWriter.decreaseIndent();
            return;
        }
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseArray.keyAt(i);
            SparseArray<List<String>> valueAt = sparseArray.valueAt(i);
            if (valueAt.size() != 0) {
                int size2 = valueAt.size();
                boolean z = false;
                for (int i2 = 0; i2 < size2; i2++) {
                    int keyAt2 = valueAt.keyAt(i2);
                    if (keyAt2 >= -1) {
                        if (!z) {
                            indentingPrintWriter.println("User " + keyAt + XmlUtils.STRING_ARRAY_SEPARATOR);
                            indentingPrintWriter.increaseIndent();
                            z = true;
                        }
                        String approvalLevelToDebugString = DomainVerificationManagerInternal.approvalLevelToDebugString(keyAt2);
                        List<String> valueAt2 = valueAt.valueAt(i2);
                        indentingPrintWriter.println(approvalLevelToDebugString + "[" + keyAt2 + "]:");
                        indentingPrintWriter.increaseIndent();
                        if (valueAt2.size() == 0) {
                            indentingPrintWriter.println("none");
                            indentingPrintWriter.decreaseIndent();
                        } else {
                            int size3 = valueAt2.size();
                            for (int i3 = 0; i3 < size3; i3++) {
                                indentingPrintWriter.println(valueAt2.get(i3));
                            }
                            indentingPrintWriter.decreaseIndent();
                        }
                    }
                }
                if (z) {
                    indentingPrintWriter.decreaseIndent();
                }
            }
        }
        indentingPrintWriter.decreaseIndent();
    }

    public boolean printState(IndentingPrintWriter indentingPrintWriter, DomainVerificationPkgState domainVerificationPkgState, AndroidPackage androidPackage, ArrayMap<String, Integer> arrayMap, boolean z) {
        boolean z2;
        arrayMap.clear();
        arrayMap.putAll((ArrayMap<? extends String, ? extends Integer>) domainVerificationPkgState.getStateMap());
        ArraySet<String> collectValidAutoVerifyDomains = this.mCollector.collectValidAutoVerifyDomains(androidPackage);
        int size = collectValidAutoVerifyDomains.size();
        for (int i = 0; i < size; i++) {
            arrayMap.putIfAbsent(collectValidAutoVerifyDomains.valueAt(i), 0);
        }
        if (arrayMap.isEmpty()) {
            return false;
        }
        if (z) {
            z2 = false;
        } else {
            String arrays = androidPackage.getSigningDetails().getSignatures() == null ? null : Arrays.toString(PackageUtils.computeSignaturesSha256Digests(androidPackage.getSigningDetails().getSignatures(), XmlUtils.STRING_ARRAY_SEPARATOR));
            indentingPrintWriter.println(domainVerificationPkgState.getPackageName() + XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("ID: " + domainVerificationPkgState.getId());
            indentingPrintWriter.println("Signatures: " + arrays);
            indentingPrintWriter.decreaseIndent();
            z2 = true;
        }
        indentingPrintWriter.increaseIndent();
        ArraySet<String> collectInvalidAutoVerifyDomains = this.mCollector.collectInvalidAutoVerifyDomains(androidPackage);
        if (!collectInvalidAutoVerifyDomains.isEmpty()) {
            indentingPrintWriter.println("Invalid autoVerify domains:");
            indentingPrintWriter.increaseIndent();
            int size2 = collectInvalidAutoVerifyDomains.size();
            for (int i2 = 0; i2 < size2; i2++) {
                indentingPrintWriter.println(collectInvalidAutoVerifyDomains.valueAt(i2));
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.println("Domain verification state:");
        indentingPrintWriter.increaseIndent();
        int size3 = arrayMap.size();
        for (int i3 = 0; i3 < size3; i3++) {
            indentingPrintWriter.print(arrayMap.keyAt(i3));
            indentingPrintWriter.print(": ");
            indentingPrintWriter.println(DomainVerificationState.stateToDebugString(arrayMap.valueAt(i3).intValue()));
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        return z2;
    }

    public void printState(IndentingPrintWriter indentingPrintWriter, DomainVerificationPkgState domainVerificationPkgState, AndroidPackage androidPackage, Integer num, ArraySet<String> arraySet, boolean z) {
        if (num == null) {
            return;
        }
        ArraySet<String> collectAllWebDomains = this.mCollector.collectAllWebDomains(androidPackage);
        SparseArray<DomainVerificationInternalUserState> userStates = domainVerificationPkgState.getUserStates();
        if (num.intValue() == -1) {
            int size = userStates.size();
            if (size == 0) {
                printState(indentingPrintWriter, domainVerificationPkgState, num.intValue(), null, arraySet, collectAllWebDomains, z);
                return;
            }
            for (int i = 0; i < size; i++) {
                DomainVerificationInternalUserState valueAt = userStates.valueAt(i);
                printState(indentingPrintWriter, domainVerificationPkgState, valueAt.getUserId(), valueAt, arraySet, collectAllWebDomains, z);
            }
            return;
        }
        printState(indentingPrintWriter, domainVerificationPkgState, num.intValue(), userStates.get(num.intValue()), arraySet, collectAllWebDomains, z);
    }

    public boolean printState(IndentingPrintWriter indentingPrintWriter, DomainVerificationPkgState domainVerificationPkgState, int i, DomainVerificationInternalUserState domainVerificationInternalUserState, ArraySet<String> arraySet, ArraySet<String> arraySet2, boolean z) {
        boolean z2;
        arraySet.clear();
        arraySet.addAll((ArraySet<? extends String>) arraySet2);
        if (domainVerificationInternalUserState != null) {
            arraySet.removeAll((ArraySet<? extends String>) domainVerificationInternalUserState.getEnabledHosts());
        }
        ArraySet<String> enabledHosts = domainVerificationInternalUserState == null ? null : domainVerificationInternalUserState.getEnabledHosts();
        int size = CollectionUtils.size(enabledHosts);
        int size2 = arraySet.size();
        if (size > 0 || size2 > 0) {
            boolean z3 = true;
            if (z) {
                z2 = false;
            } else {
                indentingPrintWriter.println(domainVerificationPkgState.getPackageName() + " " + domainVerificationPkgState.getId() + XmlUtils.STRING_ARRAY_SEPARATOR);
                z2 = true;
            }
            if (domainVerificationInternalUserState != null && !domainVerificationInternalUserState.isLinkHandlingAllowed()) {
                z3 = false;
            }
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("User ");
            indentingPrintWriter.print(i == -1 ? "all" : Integer.valueOf(i));
            indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("Verification link handling allowed: ");
            indentingPrintWriter.println(z3);
            indentingPrintWriter.println("Selection state:");
            indentingPrintWriter.increaseIndent();
            if (size > 0) {
                indentingPrintWriter.println("Enabled:");
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < size; i2++) {
                    indentingPrintWriter.println(enabledHosts.valueAt(i2));
                }
                indentingPrintWriter.decreaseIndent();
            }
            if (size2 > 0) {
                indentingPrintWriter.println("Disabled:");
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < size2; i3++) {
                    indentingPrintWriter.println(arraySet.valueAt(i3));
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.decreaseIndent();
            return z2;
        }
        return false;
    }
}
