package com.android.server.p006am;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p006am.ActivityManagerService;
import java.util.ArrayList;
/* renamed from: com.android.server.am.ForegroundServiceTypeLoggerModule */
/* loaded from: classes.dex */
public class ForegroundServiceTypeLoggerModule {
    public final SparseArray<UidState> mUids = new SparseArray<>();

    /* renamed from: com.android.server.am.ForegroundServiceTypeLoggerModule$UidState */
    /* loaded from: classes.dex */
    public static class UidState {
        public final SparseArray<FgsApiRecord> mApiClosedCalls;
        public final SparseArray<FgsApiRecord> mApiOpenCalls;
        public final SparseArray<Integer> mOpenWithFgsCount;
        public final SparseArray<Integer> mOpenedWithoutFgsCount;
        public final SparseArray<ArrayMap<ComponentName, ServiceRecord>> mRunningFgs;

        public UidState() {
            this.mApiOpenCalls = new SparseArray<>();
            this.mApiClosedCalls = new SparseArray<>();
            this.mOpenedWithoutFgsCount = new SparseArray<>();
            this.mOpenWithFgsCount = new SparseArray<>();
            this.mRunningFgs = new SparseArray<>();
        }
    }

    public void logForegroundServiceStart(int i, int i2, ServiceRecord serviceRecord) {
        UidState uidState = this.mUids.get(i);
        if (uidState == null) {
            uidState = new UidState();
            this.mUids.put(i, uidState);
        }
        ArrayList<Integer> convertFgsTypeToApiTypes = convertFgsTypeToApiTypes(serviceRecord.foregroundServiceType);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int size = convertFgsTypeToApiTypes.size();
        for (int i3 = 0; i3 < size; i3++) {
            int intValue = convertFgsTypeToApiTypes.get(i3).intValue();
            int indexOfKey = uidState.mRunningFgs.indexOfKey(intValue);
            if (indexOfKey < 0) {
                uidState.mRunningFgs.put(intValue, new ArrayMap<>());
                indexOfKey = uidState.mRunningFgs.indexOfKey(intValue);
            }
            uidState.mRunningFgs.valueAt(indexOfKey).put(serviceRecord.getComponentName(), serviceRecord);
            if (uidState.mApiOpenCalls.contains(intValue)) {
                uidState.mOpenWithFgsCount.put(intValue, uidState.mOpenedWithoutFgsCount.get(intValue));
                uidState.mOpenedWithoutFgsCount.put(intValue, 0);
                arrayList.add(Integer.valueOf(intValue));
                FgsApiRecord fgsApiRecord = uidState.mApiOpenCalls.get(intValue);
                arrayList2.add(Long.valueOf(fgsApiRecord.mTimeStart));
                fgsApiRecord.mIsAssociatedWithFgs = true;
                fgsApiRecord.mAssociatedFgsRecord = serviceRecord;
                uidState.mApiOpenCalls.remove(intValue);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        int[] iArr = new int[arrayList.size()];
        long[] jArr = new long[arrayList.size()];
        int size2 = arrayList.size();
        for (int i4 = 0; i4 < size2; i4++) {
            iArr[i4] = ((Integer) arrayList.get(i4)).intValue();
            jArr[i4] = ((Long) arrayList2.get(i4)).longValue();
        }
        logFgsApiEvent(serviceRecord, 4, 1, iArr, jArr);
    }

    public void logForegroundServiceStop(int i, ServiceRecord serviceRecord) {
        ArrayList<Integer> convertFgsTypeToApiTypes = convertFgsTypeToApiTypes(serviceRecord.foregroundServiceType);
        UidState uidState = this.mUids.get(i);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int size = convertFgsTypeToApiTypes.size();
        for (int i2 = 0; i2 < size; i2++) {
            int intValue = convertFgsTypeToApiTypes.get(i2).intValue();
            FgsApiRecord fgsApiRecord = uidState.mApiClosedCalls.get(intValue);
            if (fgsApiRecord != null && uidState.mOpenWithFgsCount.get(intValue).intValue() == 0) {
                arrayList.add(Integer.valueOf(intValue));
                arrayList2.add(Long.valueOf(fgsApiRecord.mTimeStart));
                uidState.mApiClosedCalls.remove(intValue);
            }
            ArrayMap<ComponentName, ServiceRecord> arrayMap = uidState.mRunningFgs.get(intValue);
            if (arrayMap == null) {
                Log.w("ForegroundServiceTypeLoggerModule", "Could not find appropriate running FGS for FGS stop");
            } else {
                arrayMap.remove(serviceRecord.getComponentName());
                if (arrayMap.size() == 0) {
                    uidState.mRunningFgs.remove(intValue);
                }
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        int[] iArr = new int[arrayList.size()];
        long[] jArr = new long[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            iArr[i3] = ((Integer) arrayList.get(i3)).intValue();
            jArr[i3] = ((Long) arrayList2.get(i3)).longValue();
        }
        logFgsApiEvent(serviceRecord, 4, 2, iArr, jArr);
    }

    public long logForegroundServiceApiEventBegin(int i, int i2, int i3, String str) {
        FgsApiRecord fgsApiRecord = new FgsApiRecord(i2, i3, str, i, System.currentTimeMillis());
        UidState uidState = this.mUids.get(i2);
        if (uidState == null) {
            uidState = new UidState();
            this.mUids.put(i2, uidState);
        }
        if (!hasValidActiveFgs(i2, i)) {
            int indexOfKey = uidState.mOpenedWithoutFgsCount.indexOfKey(i);
            if (indexOfKey < 0) {
                uidState.mOpenedWithoutFgsCount.put(i, 0);
                indexOfKey = uidState.mOpenedWithoutFgsCount.indexOfKey(i);
            }
            if (!uidState.mApiOpenCalls.contains(i) || uidState.mOpenedWithoutFgsCount.valueAt(indexOfKey).intValue() == 0) {
                uidState.mApiOpenCalls.put(i, fgsApiRecord);
            }
            SparseArray<Integer> sparseArray = uidState.mOpenedWithoutFgsCount;
            sparseArray.put(i, Integer.valueOf(sparseArray.get(i).intValue() + 1));
            return fgsApiRecord.mTimeStart;
        }
        int indexOfKey2 = uidState.mOpenWithFgsCount.indexOfKey(i);
        if (indexOfKey2 < 0) {
            uidState.mOpenWithFgsCount.put(i, 0);
            indexOfKey2 = uidState.mOpenWithFgsCount.indexOfKey(i);
        }
        SparseArray<Integer> sparseArray2 = uidState.mOpenWithFgsCount;
        sparseArray2.put(i, Integer.valueOf(sparseArray2.valueAt(indexOfKey2).intValue() + 1));
        ArrayMap<ComponentName, ServiceRecord> arrayMap = uidState.mRunningFgs.get(i);
        int[] iArr = {i};
        long[] jArr = {fgsApiRecord.mTimeStart};
        if (uidState.mOpenWithFgsCount.valueAt(indexOfKey2).intValue() == 1) {
            for (ServiceRecord serviceRecord : arrayMap.values()) {
                logFgsApiEvent(serviceRecord, 4, 1, iArr, jArr);
            }
        }
        return fgsApiRecord.mTimeStart;
    }

    public long logForegroundServiceApiEventEnd(int i, int i2, int i3) {
        UidState uidState = this.mUids.get(i2);
        if (uidState == null) {
            Log.w("ForegroundServiceTypeLoggerModule", "API event end called before start!");
            return -1L;
        }
        if (uidState.mOpenWithFgsCount.contains(i)) {
            if (uidState.mOpenWithFgsCount.get(i).intValue() != 0) {
                SparseArray<Integer> sparseArray = uidState.mOpenWithFgsCount;
                sparseArray.put(i, Integer.valueOf(sparseArray.get(i).intValue() - 1));
            }
            if (!hasValidActiveFgs(i2, i) && uidState.mOpenWithFgsCount.get(i).intValue() == 0) {
                long[] jArr = {System.currentTimeMillis()};
                logFgsApiEventWithNoFgs(i2, 3, new int[]{i}, jArr);
                uidState.mOpenWithFgsCount.remove(i);
                return jArr[0];
            }
        }
        if (!uidState.mOpenedWithoutFgsCount.contains(i)) {
            uidState.mOpenedWithoutFgsCount.put(i, 0);
        }
        if (uidState.mOpenedWithoutFgsCount.get(i).intValue() != 0) {
            SparseArray<Integer> sparseArray2 = uidState.mOpenedWithoutFgsCount;
            sparseArray2.put(i, Integer.valueOf(sparseArray2.get(i).intValue() - 1));
            return System.currentTimeMillis();
        }
        FgsApiRecord fgsApiRecord = new FgsApiRecord(i2, i3, "", i, System.currentTimeMillis());
        uidState.mApiClosedCalls.put(i, fgsApiRecord);
        return fgsApiRecord.mTimeStart;
    }

    public void logForegroundServiceApiStateChanged(int i, int i2, int i3, int i4) {
        UidState uidState = this.mUids.get(i2);
        if (uidState.mRunningFgs.contains(i)) {
            int[] iArr = {i};
            long[] jArr = {System.currentTimeMillis()};
            for (ServiceRecord serviceRecord : uidState.mRunningFgs.get(i).values()) {
                logFgsApiEvent(serviceRecord, 4, i4, iArr, jArr);
            }
        }
    }

    public final ArrayList<Integer> convertFgsTypeToApiTypes(int i) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if ((i & 64) == 64) {
            arrayList.add(1);
        }
        if ((i & 16) == 16) {
            arrayList.add(2);
            arrayList.add(8);
            arrayList.add(9);
        }
        if ((i & 8) == 8) {
            arrayList.add(3);
        }
        if ((i & 2) == 2) {
            arrayList.add(5);
            arrayList.add(4);
        }
        if ((i & 128) == 128) {
            arrayList.add(6);
        }
        if ((i & 4) == 4) {
            arrayList.add(7);
        }
        return arrayList;
    }

    public final boolean hasValidActiveFgs(int i, int i2) {
        UidState uidState = this.mUids.get(i);
        if (uidState != null) {
            return uidState.mRunningFgs.contains(i2);
        }
        return false;
    }

    @VisibleForTesting
    public void logFgsApiEvent(ServiceRecord serviceRecord, int i, int i2, int[] iArr, long[] jArr) {
        ApplicationInfo applicationInfo = serviceRecord.appInfo;
        int i3 = applicationInfo.uid;
        String str = serviceRecord.shortInstanceName;
        boolean z = serviceRecord.mAllowWhileInUsePermissionInFgs;
        int i4 = serviceRecord.mAllowStartForeground;
        int i5 = applicationInfo.targetSdkVersion;
        int i6 = serviceRecord.mRecentCallingUid;
        ActivityManagerService.FgsTempAllowListItem fgsTempAllowListItem = serviceRecord.mInfoTempFgsAllowListReason;
        int i7 = fgsTempAllowListItem != null ? fgsTempAllowListItem.mCallingUid : -1;
        boolean z2 = serviceRecord.mFgsNotificationWasDeferred;
        boolean z3 = serviceRecord.mFgsNotificationShown;
        int i8 = serviceRecord.mStartForegroundCount;
        int hashComponentNameForAtom = ActivityManagerUtils.hashComponentNameForAtom(str);
        boolean z4 = serviceRecord.mFgsHasNotificationPermission;
        int i9 = serviceRecord.foregroundServiceType;
        boolean z5 = serviceRecord.mIsFgsDelegate;
        ForegroundServiceDelegation foregroundServiceDelegation = serviceRecord.mFgsDelegation;
        FrameworkStatsLog.write(60, i3, str, i, z, i4, i5, i6, 0, i7, z2, z3, 0, i8, hashComponentNameForAtom, z4, i9, 0, z5, foregroundServiceDelegation != null ? foregroundServiceDelegation.mOptions.mClientUid : -1, foregroundServiceDelegation != null ? foregroundServiceDelegation.mOptions.mDelegationService : 0, i2, iArr, jArr);
    }

    @VisibleForTesting
    public void logFgsApiEventWithNoFgs(int i, int i2, int[] iArr, long[] jArr) {
        FrameworkStatsLog.write(60, i, null, 4, false, 0, 0, i, 0, 0, false, false, 0, 0, 0, false, 0, 0, false, 0, 0, i2, iArr, jArr);
    }

    /* renamed from: com.android.server.am.ForegroundServiceTypeLoggerModule$FgsApiRecord */
    /* loaded from: classes.dex */
    public static class FgsApiRecord {
        public ServiceRecord mAssociatedFgsRecord;
        public boolean mIsAssociatedWithFgs;
        public final String mPackageName;
        public final int mPid;
        public final long mTimeStart;
        public int mType;
        public final int mUid;

        public FgsApiRecord(int i, int i2, String str, int i3, long j) {
            this.mUid = i;
            this.mPid = i2;
            this.mPackageName = str;
            this.mType = i3;
            this.mTimeStart = j;
        }
    }
}
