package com.android.server.permission.access;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.SystemConfig;
import com.android.server.p011pm.permission.PermissionAllowlist;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.permission.access.appop.PackageAppOpPolicy;
import com.android.server.permission.access.appop.UidAppOpPolicy;
import com.android.server.permission.access.collection.IndexedListSet;
import com.android.server.permission.access.collection.IntSet;
import com.android.server.permission.access.collection.IntSetKt;
import com.android.server.permission.access.permission.UidPermissionPolicy;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* compiled from: AccessPolicy.kt */
/* loaded from: classes2.dex */
public final class AccessPolicy {
    public static final Companion Companion = new Companion(null);
    public static final String LOG_TAG = AccessPolicy.class.getSimpleName();
    public final ArrayMap<String, ArrayMap<String, SchemePolicy>> schemePolicies;

    public AccessPolicy(ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap) {
        this.schemePolicies = arrayMap;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AccessPolicy() {
        this(r0);
        ArrayMap arrayMap = new ArrayMap();
        _init_$lambda$1$addPolicy(arrayMap, new UidPermissionPolicy());
        _init_$lambda$1$addPolicy(arrayMap, new UidAppOpPolicy());
        _init_$lambda$1$addPolicy(arrayMap, new PackageAppOpPolicy());
    }

    public static final SchemePolicy _init_$lambda$1$addPolicy(ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap, SchemePolicy schemePolicy) {
        String subjectScheme = schemePolicy.getSubjectScheme();
        ArrayMap<String, SchemePolicy> arrayMap2 = arrayMap.get(subjectScheme);
        if (arrayMap2 == null) {
            arrayMap2 = new ArrayMap<>();
            arrayMap.put(subjectScheme, arrayMap2);
        }
        return arrayMap2.put(schemePolicy.getObjectScheme(), schemePolicy);
    }

    public final SchemePolicy getSchemePolicy(String str, String str2) {
        ArrayMap<String, SchemePolicy> arrayMap = this.schemePolicies.get(str);
        SchemePolicy schemePolicy = arrayMap != null ? arrayMap.get(str2) : null;
        if (schemePolicy != null) {
            return schemePolicy;
        }
        throw new IllegalStateException(("Scheme policy for " + str + " and " + str2 + " does not exist").toString());
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x0111, code lost:
        r5 = r18.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0116, code lost:
        if (r5 == 1) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0118, code lost:
        if (r5 == 2) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x011b, code lost:
        if (r5 == 3) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x01a5, code lost:
        r0 = r18.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01aa, code lost:
        if (r0 == 1) goto L125;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01ac, code lost:
        if (r0 == 2) goto L124;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x01af, code lost:
        if (r0 == 3) goto L122;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void parseSystemState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState) {
        int next;
        int i;
        int i2;
        int next2;
        int next3;
        int next4;
        int next5;
        int next6;
        int eventType = binaryXmlPullParser.getEventType();
        if (eventType != 0 && eventType != 2) {
            throw new XmlPullParserException("Unexpected event type " + eventType);
        }
        do {
            next = binaryXmlPullParser.next();
            i = 3;
            i2 = 1;
            if (next == 1 || next == 2) {
                break;
            }
        } while (next != 3);
        while (true) {
            int eventType2 = binaryXmlPullParser.getEventType();
            if (eventType2 == i2) {
                return;
            }
            if (eventType2 != 2) {
                if (eventType2 == i) {
                    return;
                }
                throw new XmlPullParserException("Unexpected event type " + eventType2);
            }
            int depth = binaryXmlPullParser.getDepth();
            if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "access")) {
                int eventType3 = binaryXmlPullParser.getEventType();
                if (eventType3 != 0 && eventType3 != 2) {
                    throw new XmlPullParserException("Unexpected event type " + eventType3);
                }
                do {
                    next4 = binaryXmlPullParser.next();
                    if (next4 == i2 || next4 == 2) {
                        break;
                    }
                } while (next4 != i);
                while (true) {
                    int eventType4 = binaryXmlPullParser.getEventType();
                    if (eventType4 == i2) {
                        break;
                    } else if (eventType4 == 2) {
                        int depth2 = binaryXmlPullParser.getDepth();
                        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
                        int size = arrayMap.size();
                        for (int i3 = 0; i3 < size; i3++) {
                            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i3);
                            int size2 = valueAt.size();
                            for (int i4 = 0; i4 < size2; i4++) {
                                valueAt.valueAt(i4).parseSystemState(binaryXmlPullParser, accessState);
                            }
                        }
                        int depth3 = binaryXmlPullParser.getDepth();
                        if (depth3 != depth2) {
                            throw new XmlPullParserException("Unexpected post-block depth " + depth3 + ", expected " + depth2);
                        }
                        while (true) {
                            int eventType5 = binaryXmlPullParser.getEventType();
                            if (eventType5 == 2) {
                                do {
                                    next6 = binaryXmlPullParser.next();
                                    if (next6 != 1 && next6 != 2) {
                                    }
                                } while (next6 != 3);
                            } else if (eventType5 != 3) {
                                throw new XmlPullParserException("Unexpected event type " + eventType5);
                            } else if (binaryXmlPullParser.getDepth() <= depth2) {
                                break;
                            } else {
                                do {
                                    next5 = binaryXmlPullParser.next();
                                    if (next5 != 1 && next5 != 2) {
                                    }
                                } while (next5 != 3);
                            }
                        }
                    } else if (eventType4 != i) {
                        throw new XmlPullParserException("Unexpected event type " + eventType4);
                    }
                    i = 3;
                    i2 = 1;
                }
            } else {
                String str = LOG_TAG;
                String name = binaryXmlPullParser.getName();
                Log.w(str, "Ignoring unknown tag " + name + " when parsing system state");
            }
            int depth4 = binaryXmlPullParser.getDepth();
            if (depth4 != depth) {
                throw new XmlPullParserException("Unexpected post-block depth " + depth4 + ", expected " + depth);
            }
            while (true) {
                int eventType6 = binaryXmlPullParser.getEventType();
                if (eventType6 == 2) {
                    do {
                        next3 = binaryXmlPullParser.next();
                        if (next3 == 1 || next3 == 2) {
                            break;
                        }
                    } while (next3 != 3);
                } else if (eventType6 != 3) {
                    throw new XmlPullParserException("Unexpected event type " + eventType6);
                } else if (binaryXmlPullParser.getDepth() <= depth) {
                    break;
                } else {
                    do {
                        next2 = binaryXmlPullParser.next();
                        if (next2 != 1 && next2 != 2) {
                        }
                    } while (next2 != 3);
                }
            }
            i = 3;
            i2 = 1;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x0117, code lost:
        r5 = r18.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x011c, code lost:
        if (r5 == 1) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x011f, code lost:
        if (r5 == 2) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0122, code lost:
        if (r5 == 3) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01b3, code lost:
        r1 = r18.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x01b8, code lost:
        if (r1 == 1) goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x01bb, code lost:
        if (r1 == 2) goto L126;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x01be, code lost:
        if (r1 == 3) goto L124;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void parseUserState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
        int next;
        int i2;
        int i3;
        int next2;
        int next3;
        int next4;
        int eventType = binaryXmlPullParser.getEventType();
        int i4 = 2;
        if (eventType != 0 && eventType != 2) {
            throw new XmlPullParserException("Unexpected event type " + eventType);
        }
        do {
            next = binaryXmlPullParser.next();
            i2 = 3;
            i3 = 1;
            if (next == 1 || next == 2) {
                break;
            }
        } while (next != 3);
        while (true) {
            int eventType2 = binaryXmlPullParser.getEventType();
            if (eventType2 == i3) {
                return;
            }
            if (eventType2 != i4) {
                if (eventType2 == i2) {
                    return;
                }
                throw new XmlPullParserException("Unexpected event type " + eventType2);
            }
            int depth = binaryXmlPullParser.getDepth();
            if (Intrinsics.areEqual(binaryXmlPullParser.getName(), "access")) {
                int eventType3 = binaryXmlPullParser.getEventType();
                if (eventType3 != 0 && eventType3 != i4) {
                    throw new XmlPullParserException("Unexpected event type " + eventType3);
                }
                do {
                    next3 = binaryXmlPullParser.next();
                    if (next3 == i3 || next3 == i4) {
                        break;
                    }
                } while (next3 != i2);
                while (true) {
                    int eventType4 = binaryXmlPullParser.getEventType();
                    if (eventType4 == i3) {
                        break;
                    } else if (eventType4 == i4) {
                        int depth2 = binaryXmlPullParser.getDepth();
                        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
                        int size = arrayMap.size();
                        for (int i5 = 0; i5 < size; i5++) {
                            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i5);
                            int size2 = valueAt.size();
                            for (int i6 = 0; i6 < size2; i6++) {
                                valueAt.valueAt(i6).parseUserState(binaryXmlPullParser, accessState, i);
                            }
                        }
                        int depth3 = binaryXmlPullParser.getDepth();
                        if (depth3 != depth2) {
                            throw new XmlPullParserException("Unexpected post-block depth " + depth3 + ", expected " + depth2);
                        }
                        while (true) {
                            int eventType5 = binaryXmlPullParser.getEventType();
                            if (eventType5 == 2) {
                                do {
                                    next4 = binaryXmlPullParser.next();
                                    if (next4 != 1 && next4 != 2) {
                                    }
                                } while (next4 != 3);
                            } else if (eventType5 != 3) {
                                throw new XmlPullParserException("Unexpected event type " + eventType5);
                            } else if (binaryXmlPullParser.getDepth() <= depth2) {
                                break;
                            } else {
                                while (true) {
                                    int next5 = binaryXmlPullParser.next();
                                    int i7 = (next5 == 1 || next5 == i7 || next5 == 3) ? 2 : 2;
                                }
                            }
                        }
                    } else if (eventType4 != i2) {
                        throw new XmlPullParserException("Unexpected event type " + eventType4);
                    }
                    i4 = 2;
                    i2 = 3;
                    i3 = 1;
                }
            } else {
                String str = LOG_TAG;
                String name = binaryXmlPullParser.getName();
                Log.w(str, "Ignoring unknown tag " + name + " when parsing user state for user " + i);
            }
            int depth4 = binaryXmlPullParser.getDepth();
            if (depth4 != depth) {
                throw new XmlPullParserException("Unexpected post-block depth " + depth4 + ", expected " + depth);
            }
            while (true) {
                int eventType6 = binaryXmlPullParser.getEventType();
                if (eventType6 == 2) {
                    do {
                        next2 = binaryXmlPullParser.next();
                        if (next2 != 1 && next2 != 2) {
                        }
                    } while (next2 != 3);
                } else if (eventType6 != 3) {
                    throw new XmlPullParserException("Unexpected event type " + eventType6);
                } else if (binaryXmlPullParser.getDepth() <= depth) {
                    break;
                } else {
                    while (true) {
                        int next6 = binaryXmlPullParser.next();
                        int i8 = (next6 == 1 || next6 == i8 || next6 == 3) ? 2 : 2;
                    }
                }
            }
            i4 = 2;
            i2 = 3;
            i3 = 1;
        }
    }

    public final int getDecision(GetStateScope getStateScope, AccessUri accessUri, AccessUri accessUri2) {
        return getSchemePolicy(accessUri, accessUri2).getDecision(getStateScope, accessUri, accessUri2);
    }

    public final void serializeSystemState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState) {
        binaryXmlSerializer.startTag((String) null, "access");
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                valueAt.valueAt(i2).serializeSystemState(binaryXmlSerializer, accessState);
            }
        }
        binaryXmlSerializer.endTag((String) null, "access");
    }

    public final void serializeUserState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState, int i) {
        binaryXmlSerializer.startTag((String) null, "access");
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
            int size2 = valueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                valueAt.valueAt(i3).serializeUserState(binaryXmlSerializer, accessState, i);
            }
        }
        binaryXmlSerializer.endTag((String) null, "access");
    }

    public final void setDecision(MutateStateScope mutateStateScope, AccessUri accessUri, AccessUri accessUri2, int i) {
        getSchemePolicy(accessUri, accessUri2).setDecision(mutateStateScope, accessUri, accessUri2, i);
    }

    public final void initialize(AccessState accessState, IntSet intSet, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, boolean z, Map<String, SystemConfig.PermissionEntry> map3, IndexedListSet<String> indexedListSet, PermissionAllowlist permissionAllowlist, ArrayMap<String, IndexedListSet<String>> arrayMap) {
        SystemState systemState = accessState.getSystemState();
        IntSetKt.plusAssign(systemState.getUserIds(), intSet);
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        for (Map.Entry<String, ? extends PackageState> entry : map.entrySet()) {
            PackageState value = entry.getValue();
            SparseArray<IndexedListSet<String>> appIds = systemState.getAppIds();
            int appId = value.getAppId();
            IndexedListSet<String> indexedListSet2 = appIds.get(appId);
            if (indexedListSet2 == null) {
                indexedListSet2 = new IndexedListSet<>();
                appIds.put(appId, indexedListSet2);
            }
            indexedListSet2.add(value.getPackageName());
        }
        systemState.setKnownPackages(sparseArray);
        systemState.setLeanback(z);
        systemState.setConfigPermissions(map3);
        systemState.setPrivilegedPermissionAllowlistPackages(indexedListSet);
        systemState.setPermissionAllowlist(permissionAllowlist);
        systemState.setImplicitToSourcePermissions(arrayMap);
        SparseArray<UserState> userStates = accessState.getUserStates();
        int size = intSet.getSize();
        for (int i = 0; i < size; i++) {
            userStates.set(intSet.elementAt(i), new UserState());
        }
    }

    public final void onUserAdded(MutateStateScope mutateStateScope, int i) {
        mutateStateScope.getNewState().getSystemState().getUserIds().add(i);
        mutateStateScope.getNewState().getUserStates().set(i, new UserState());
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
            int size2 = valueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                valueAt.valueAt(i3).onUserAdded(mutateStateScope, i);
            }
        }
    }

    public final void onUserRemoved(MutateStateScope mutateStateScope, int i) {
        mutateStateScope.getNewState().getSystemState().getUserIds().remove(i);
        mutateStateScope.getNewState().getUserStates().remove(i);
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
            int size2 = valueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                valueAt.valueAt(i3).onUserRemoved(mutateStateScope, i);
            }
        }
    }

    public final void onStorageVolumeMounted(MutateStateScope mutateStateScope, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, String str, boolean z) {
        IntSet intSet = new IntSet();
        SystemState systemState = mutateStateScope.getNewState().getSystemState();
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        for (Map.Entry<String, ? extends PackageState> entry : map.entrySet()) {
            String key = entry.getKey();
            PackageState value = entry.getValue();
            if (Intrinsics.areEqual(value.getVolumeUuid(), str)) {
                int appId = value.getAppId();
                SparseArray<IndexedListSet<String>> appIds = systemState.getAppIds();
                IndexedListSet<String> indexedListSet = appIds.get(appId);
                if (indexedListSet == null) {
                    intSet.add(appId);
                    indexedListSet = new IndexedListSet<>();
                    appIds.put(appId, indexedListSet);
                }
                indexedListSet.add(key);
            }
        }
        systemState.setKnownPackages(sparseArray);
        int size = intSet.getSize();
        for (int i = 0; i < size; i++) {
            int elementAt = intSet.elementAt(i);
            ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
            int size2 = arrayMap.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
                int size3 = valueAt.size();
                for (int i3 = 0; i3 < size3; i3++) {
                    valueAt.valueAt(i3).onAppIdAdded(mutateStateScope, elementAt);
                }
            }
        }
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap2 = this.schemePolicies;
        int size4 = arrayMap2.size();
        for (int i4 = 0; i4 < size4; i4++) {
            ArrayMap<String, SchemePolicy> valueAt2 = arrayMap2.valueAt(i4);
            int size5 = valueAt2.size();
            for (int i5 = 0; i5 < size5; i5++) {
                valueAt2.valueAt(i5).onStorageVolumeMounted(mutateStateScope, str, z);
            }
        }
    }

    public final void onPackageAdded(MutateStateScope mutateStateScope, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, String str) {
        boolean z;
        PackageState packageState = map.get(str);
        if (packageState == null) {
            throw new IllegalStateException(("Added package " + str + " isn't found in packageStates in onPackageAdded()").toString());
        }
        int appId = packageState.getAppId();
        SystemState systemState = mutateStateScope.getNewState().getSystemState();
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        SparseArray<IndexedListSet<String>> appIds = systemState.getAppIds();
        IndexedListSet<String> indexedListSet = appIds.get(appId);
        if (indexedListSet != null) {
            z = false;
        } else {
            indexedListSet = new IndexedListSet<>();
            appIds.put(appId, indexedListSet);
            z = true;
        }
        indexedListSet.add(str);
        systemState.setKnownPackages(sparseArray);
        if (z) {
            ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i);
                int size2 = valueAt.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    valueAt.valueAt(i2).onAppIdAdded(mutateStateScope, appId);
                }
            }
        }
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap2 = this.schemePolicies;
        int size3 = arrayMap2.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ArrayMap<String, SchemePolicy> valueAt2 = arrayMap2.valueAt(i3);
            int size4 = valueAt2.size();
            for (int i4 = 0; i4 < size4; i4++) {
                valueAt2.valueAt(i4).onPackageAdded(mutateStateScope, packageState);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0084 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void onPackageRemoved(MutateStateScope mutateStateScope, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, String str, int i) {
        int size;
        int i2;
        boolean z = true;
        if (!(!map.containsKey(str))) {
            throw new IllegalStateException(("Removed package " + str + " is still in packageStates in onPackageRemoved()").toString());
        }
        SystemState systemState = mutateStateScope.getNewState().getSystemState();
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        IndexedListSet<String> indexedListSet = systemState.getAppIds().get(i);
        if (indexedListSet != null) {
            indexedListSet.remove(str);
            if (indexedListSet.isEmpty()) {
                systemState.getAppIds().remove(i);
                systemState.setKnownPackages(sparseArray);
                ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
                size = arrayMap.size();
                for (i2 = 0; i2 < size; i2++) {
                    ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
                    int size2 = valueAt.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        valueAt.valueAt(i3).onPackageRemoved(mutateStateScope, str, i);
                    }
                }
                if (z) {
                    return;
                }
                ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap2 = this.schemePolicies;
                int size3 = arrayMap2.size();
                for (int i4 = 0; i4 < size3; i4++) {
                    ArrayMap<String, SchemePolicy> valueAt2 = arrayMap2.valueAt(i4);
                    int size4 = valueAt2.size();
                    for (int i5 = 0; i5 < size4; i5++) {
                        valueAt2.valueAt(i5).onAppIdRemoved(mutateStateScope, i);
                    }
                }
                return;
            }
        }
        z = false;
        systemState.setKnownPackages(sparseArray);
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap3 = this.schemePolicies;
        size = arrayMap3.size();
        while (i2 < size) {
        }
        if (z) {
        }
    }

    public final void onPackageInstalled(MutateStateScope mutateStateScope, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, String str, int i) {
        SystemState systemState = mutateStateScope.getNewState().getSystemState();
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        systemState.setKnownPackages(sparseArray);
        PackageState packageState = map.get(str);
        if (packageState == null) {
            throw new IllegalStateException(("Installed package " + str + " isn't found in packageStates in onPackageInstalled()").toString());
        }
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i2);
            int size2 = valueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                valueAt.valueAt(i3).onPackageInstalled(mutateStateScope, packageState, i);
            }
        }
    }

    public final void onPackageUninstalled(MutateStateScope mutateStateScope, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<String[]> sparseArray, String str, int i, int i2) {
        SystemState systemState = mutateStateScope.getNewState().getSystemState();
        systemState.setPackageStates(map);
        systemState.setDisabledSystemPackageStates(map2);
        systemState.setKnownPackages(sparseArray);
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i3 = 0; i3 < size; i3++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i3);
            int size2 = valueAt.size();
            for (int i4 = 0; i4 < size2; i4++) {
                valueAt.valueAt(i4).onPackageUninstalled(mutateStateScope, str, i, i2);
            }
        }
    }

    public final void onSystemReady(MutateStateScope mutateStateScope) {
        mutateStateScope.getNewState().getSystemState().setSystemReady(true);
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                valueAt.valueAt(i2).onSystemReady(mutateStateScope);
            }
        }
    }

    public final SchemePolicy getSchemePolicy(AccessUri accessUri, AccessUri accessUri2) {
        return getSchemePolicy(accessUri.getScheme(), accessUri2.getScheme());
    }

    public final void onInitialized(MutateStateScope mutateStateScope) {
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                valueAt.valueAt(i2).onInitialized(mutateStateScope);
            }
        }
    }

    public final void onStateMutated(GetStateScope getStateScope) {
        ArrayMap<String, ArrayMap<String, SchemePolicy>> arrayMap = this.schemePolicies;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            ArrayMap<String, SchemePolicy> valueAt = arrayMap.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                valueAt.valueAt(i2).onStateMutated(getStateScope);
            }
        }
    }

    /* compiled from: AccessPolicy.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }
}
