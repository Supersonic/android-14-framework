package com.android.server.appop;

import android.annotation.RequiresPermission;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.PackageTagsList;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class AppOpsRestrictionsImpl implements AppOpsRestrictions {
    public AppOpsCheckingServiceInterface mAppOpsCheckingServiceInterface;
    public Context mContext;
    public Handler mHandler;
    public final ArrayMap<Object, SparseBooleanArray> mGlobalRestrictions = new ArrayMap<>();
    public final ArrayMap<Object, SparseArray<SparseBooleanArray>> mUserRestrictions = new ArrayMap<>();
    public final ArrayMap<Object, SparseArray<PackageTagsList>> mUserRestrictionExcludedPackageTags = new ArrayMap<>();

    public AppOpsRestrictionsImpl(Context context, Handler handler, AppOpsCheckingServiceInterface appOpsCheckingServiceInterface) {
        this.mContext = context;
        this.mHandler = handler;
        this.mAppOpsCheckingServiceInterface = appOpsCheckingServiceInterface;
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean setGlobalRestriction(Object obj, int i, boolean z) {
        if (z) {
            if (!this.mGlobalRestrictions.containsKey(obj)) {
                this.mGlobalRestrictions.put(obj, new SparseBooleanArray());
            }
            SparseBooleanArray sparseBooleanArray = this.mGlobalRestrictions.get(obj);
            Objects.requireNonNull(sparseBooleanArray);
            boolean z2 = !sparseBooleanArray.get(i);
            sparseBooleanArray.put(i, true);
            return z2;
        }
        SparseBooleanArray sparseBooleanArray2 = this.mGlobalRestrictions.get(obj);
        if (sparseBooleanArray2 == null) {
            return false;
        }
        boolean z3 = sparseBooleanArray2.get(i);
        sparseBooleanArray2.delete(i);
        if (sparseBooleanArray2.size() == 0) {
            this.mGlobalRestrictions.remove(obj);
        }
        return z3;
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean getGlobalRestriction(Object obj, int i) {
        SparseBooleanArray sparseBooleanArray = this.mGlobalRestrictions.get(obj);
        if (sparseBooleanArray == null) {
            return false;
        }
        return sparseBooleanArray.get(i);
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean hasGlobalRestrictions(Object obj) {
        return this.mGlobalRestrictions.containsKey(obj);
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean clearGlobalRestrictions(Object obj) {
        return this.mGlobalRestrictions.remove(obj) != null;
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    @RequiresPermission(anyOf = {"android.permission.MANAGE_USERS", "android.permission.CREATE_USERS"})
    public boolean setUserRestriction(Object obj, int i, int i2, boolean z, PackageTagsList packageTagsList) {
        int[] resolveUserId = resolveUserId(i);
        boolean z2 = false;
        for (int i3 = 0; i3 < resolveUserId.length; i3++) {
            z2 = z2 | putUserRestriction(obj, resolveUserId[i3], i2, z) | putUserRestrictionExclusions(obj, resolveUserId[i3], packageTagsList);
        }
        return z2;
    }

    @RequiresPermission(anyOf = {"android.permission.MANAGE_USERS", "android.permission.CREATE_USERS"})
    public final int[] resolveUserId(int i) {
        if (i == -1) {
            List users = UserManager.get(this.mContext).getUsers();
            int[] iArr = new int[users.size()];
            for (int i2 = 0; i2 < users.size(); i2++) {
                iArr[i2] = ((UserInfo) users.get(i2)).id;
            }
            return iArr;
        }
        return new int[]{i};
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean hasUserRestrictions(Object obj) {
        return this.mUserRestrictions.containsKey(obj);
    }

    public final boolean getUserRestriction(Object obj, int i, int i2) {
        SparseBooleanArray sparseBooleanArray;
        SparseArray<SparseBooleanArray> sparseArray = this.mUserRestrictions.get(obj);
        if (sparseArray == null || (sparseBooleanArray = sparseArray.get(i)) == null) {
            return false;
        }
        return sparseBooleanArray.get(i2);
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean getUserRestriction(Object obj, int i, int i2, String str, String str2, boolean z) {
        if (getUserRestriction(obj, i, i2)) {
            PackageTagsList userRestrictionExclusions = getUserRestrictionExclusions(obj, i);
            if (userRestrictionExclusions == null) {
                return true;
            }
            if (z) {
                return !userRestrictionExclusions.includes(str);
            }
            return !userRestrictionExclusions.contains(str, str2);
        }
        return false;
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean clearUserRestrictions(Object obj) {
        SparseBooleanArray collectAllUserRestrictedCodes = collectAllUserRestrictedCodes(obj);
        boolean z = (this.mUserRestrictions.remove(obj) != null) | false | (this.mUserRestrictionExcludedPackageTags.remove(obj) != null);
        notifyAllUserRestrictions(collectAllUserRestrictedCodes);
        return z;
    }

    public final SparseBooleanArray collectAllUserRestrictedCodes(Object obj) {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        SparseArray<SparseBooleanArray> sparseArray = this.mUserRestrictions.get(obj);
        if (sparseArray == null) {
            return sparseBooleanArray;
        }
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            SparseBooleanArray valueAt = sparseArray.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                sparseBooleanArray.put(valueAt.keyAt(i2), true);
            }
        }
        return sparseBooleanArray;
    }

    public final void notifyAllUserRestrictions(SparseBooleanArray sparseBooleanArray) {
        int size = sparseBooleanArray.size();
        for (int i = 0; i < size; i++) {
            final int keyAt = sparseBooleanArray.keyAt(i);
            this.mHandler.post(new Runnable() { // from class: com.android.server.appop.AppOpsRestrictionsImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AppOpsRestrictionsImpl.this.lambda$notifyAllUserRestrictions$0(keyAt);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyAllUserRestrictions$0(int i) {
        this.mAppOpsCheckingServiceInterface.notifyWatchersOfChange(i, -2);
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public boolean clearUserRestrictions(Object obj, Integer num) {
        SparseArray<SparseBooleanArray> sparseArray = this.mUserRestrictions.get(obj);
        boolean z = false;
        if (sparseArray != null) {
            z = false | sparseArray.contains(num.intValue());
            sparseArray.remove(num.intValue());
            if (sparseArray.size() == 0) {
                this.mUserRestrictions.remove(obj);
            }
        }
        SparseArray<PackageTagsList> sparseArray2 = this.mUserRestrictionExcludedPackageTags.get(obj);
        if (sparseArray2 != null) {
            z |= sparseArray2.contains(num.intValue());
            sparseArray2.remove(num.intValue());
            if (sparseArray2.size() == 0) {
                this.mUserRestrictionExcludedPackageTags.remove(obj);
            }
        }
        return z;
    }

    public final boolean putUserRestriction(Object obj, int i, int i2, boolean z) {
        SparseBooleanArray sparseBooleanArray;
        if (z) {
            if (!this.mUserRestrictions.containsKey(obj)) {
                this.mUserRestrictions.put(obj, new SparseArray<>());
            }
            SparseArray<SparseBooleanArray> sparseArray = this.mUserRestrictions.get(obj);
            Objects.requireNonNull(sparseArray);
            if (!sparseArray.contains(i)) {
                sparseArray.put(i, new SparseBooleanArray());
            }
            SparseBooleanArray sparseBooleanArray2 = sparseArray.get(i);
            boolean z2 = !sparseBooleanArray2.get(i2);
            sparseBooleanArray2.put(i2, z);
            return z2;
        }
        SparseArray<SparseBooleanArray> sparseArray2 = this.mUserRestrictions.get(obj);
        if (sparseArray2 == null || (sparseBooleanArray = sparseArray2.get(i)) == null) {
            return false;
        }
        boolean z3 = sparseBooleanArray.get(i2);
        sparseBooleanArray.delete(i2);
        if (sparseBooleanArray.size() == 0) {
            sparseArray2.remove(i);
        }
        if (sparseArray2.size() == 0) {
            this.mUserRestrictions.remove(obj);
        }
        return z3;
    }

    public PackageTagsList getUserRestrictionExclusions(Object obj, int i) {
        SparseArray<PackageTagsList> sparseArray = this.mUserRestrictionExcludedPackageTags.get(obj);
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    public final boolean putUserRestrictionExclusions(Object obj, int i, PackageTagsList packageTagsList) {
        if ((packageTagsList == null || packageTagsList.isEmpty()) ? false : true) {
            if (!this.mUserRestrictionExcludedPackageTags.containsKey(obj)) {
                this.mUserRestrictionExcludedPackageTags.put(obj, new SparseArray<>());
            }
            SparseArray<PackageTagsList> sparseArray = this.mUserRestrictionExcludedPackageTags.get(obj);
            Objects.requireNonNull(sparseArray);
            sparseArray.put(i, packageTagsList);
            return true;
        }
        SparseArray<PackageTagsList> sparseArray2 = this.mUserRestrictionExcludedPackageTags.get(obj);
        if (sparseArray2 == null) {
            return false;
        }
        boolean z = sparseArray2.get(i) != null;
        sparseArray2.remove(i);
        if (sparseArray2.size() == 0) {
            this.mUserRestrictionExcludedPackageTags.remove(obj);
        }
        return z;
    }

    @Override // com.android.server.appop.AppOpsRestrictions
    public void dumpRestrictions(PrintWriter printWriter, int i, String str, boolean z) {
        String str2;
        boolean z2;
        int i2;
        String str3;
        boolean z3;
        int i3;
        String str4;
        SparseArray<SparseBooleanArray> sparseArray;
        int i4;
        AppOpsRestrictionsImpl appOpsRestrictionsImpl = this;
        PrintWriter printWriter2 = printWriter;
        int size = appOpsRestrictionsImpl.mGlobalRestrictions.size();
        int i5 = 0;
        while (true) {
            str2 = "[";
            z2 = true;
            if (i5 >= size) {
                break;
            }
            Object keyAt = appOpsRestrictionsImpl.mGlobalRestrictions.keyAt(i5);
            SparseBooleanArray valueAt = appOpsRestrictionsImpl.mGlobalRestrictions.valueAt(i5);
            printWriter2.println("  Global restrictions for token " + keyAt + XmlUtils.STRING_ARRAY_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            int size2 = valueAt.size();
            for (int i6 = 0; i6 < size2; i6++) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(AppOpsManager.opToName(valueAt.keyAt(i6)));
            }
            sb.append("]");
            printWriter2.println("      Restricted ops: " + ((Object) sb));
            i5++;
        }
        if (z) {
            int size3 = appOpsRestrictionsImpl.mUserRestrictions.size();
            int i7 = 0;
            while (i7 < size3) {
                Object keyAt2 = appOpsRestrictionsImpl.mUserRestrictions.keyAt(i7);
                SparseArray<SparseBooleanArray> sparseArray2 = appOpsRestrictionsImpl.mUserRestrictions.get(keyAt2);
                SparseArray<PackageTagsList> sparseArray3 = appOpsRestrictionsImpl.mUserRestrictionExcludedPackageTags.get(keyAt2);
                int size4 = sparseArray2 != null ? sparseArray2.size() : 0;
                if (size4 <= 0 || str != null) {
                    i2 = size3;
                    str3 = str2;
                    z3 = false;
                } else {
                    int i8 = 0;
                    z3 = false;
                    boolean z4 = false;
                    while (i8 < size4) {
                        int keyAt3 = sparseArray2.keyAt(i8);
                        int i9 = size3;
                        SparseBooleanArray valueAt2 = sparseArray2.valueAt(i8);
                        if (valueAt2 != null && (i < 0 || valueAt2.get(i))) {
                            sparseArray = sparseArray2;
                            if (z3) {
                                i4 = size4;
                            } else {
                                StringBuilder sb2 = new StringBuilder();
                                i4 = size4;
                                sb2.append("  User restrictions for token ");
                                sb2.append(keyAt2);
                                sb2.append(XmlUtils.STRING_ARRAY_SEPARATOR);
                                printWriter2.println(sb2.toString());
                                z3 = true;
                            }
                            if (!z4) {
                                printWriter2.println("      Restricted ops:");
                                z4 = true;
                            }
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(str2);
                            int size5 = valueAt2.size();
                            str4 = str2;
                            int i10 = 0;
                            while (i10 < size5) {
                                int keyAt4 = valueAt2.keyAt(i10);
                                SparseBooleanArray sparseBooleanArray = valueAt2;
                                int i11 = size5;
                                if (sb3.length() > 1) {
                                    sb3.append(", ");
                                }
                                sb3.append(AppOpsManager.opToName(keyAt4));
                                i10++;
                                valueAt2 = sparseBooleanArray;
                                size5 = i11;
                            }
                            sb3.append("]");
                            printWriter2.print("        ");
                            printWriter2.print("user: ");
                            printWriter2.print(keyAt3);
                            printWriter2.print(" restricted ops: ");
                            printWriter2.println(sb3);
                        } else {
                            str4 = str2;
                            sparseArray = sparseArray2;
                            i4 = size4;
                        }
                        i8++;
                        size3 = i9;
                        sparseArray2 = sparseArray;
                        size4 = i4;
                        str2 = str4;
                    }
                    i2 = size3;
                    str3 = str2;
                }
                int size6 = sparseArray3 != null ? sparseArray3.size() : 0;
                if (size6 > 0 && i < 0) {
                    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter2);
                    indentingPrintWriter.increaseIndent();
                    int i12 = 0;
                    boolean z5 = false;
                    while (i12 < size6) {
                        int keyAt5 = sparseArray3.keyAt(i12);
                        PackageTagsList valueAt3 = sparseArray3.valueAt(i12);
                        if (valueAt3 != null) {
                            if (str != null ? valueAt3.includes(str) : true) {
                                i3 = size6;
                                if (!z3) {
                                    indentingPrintWriter.println("User restrictions for token " + keyAt2 + XmlUtils.STRING_ARRAY_SEPARATOR);
                                    z3 = true;
                                }
                                indentingPrintWriter.increaseIndent();
                                if (!z5) {
                                    indentingPrintWriter.println("Excluded packages:");
                                    z5 = true;
                                }
                                indentingPrintWriter.increaseIndent();
                                indentingPrintWriter.print("user: ");
                                indentingPrintWriter.print(keyAt5);
                                indentingPrintWriter.println(" packages: ");
                                indentingPrintWriter.increaseIndent();
                                valueAt3.dump(indentingPrintWriter);
                                indentingPrintWriter.decreaseIndent();
                                indentingPrintWriter.decreaseIndent();
                                indentingPrintWriter.decreaseIndent();
                                i12++;
                                size6 = i3;
                            }
                        }
                        i3 = size6;
                        i12++;
                        size6 = i3;
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                i7++;
                appOpsRestrictionsImpl = this;
                printWriter2 = printWriter;
                size3 = i2;
                str2 = str3;
                z2 = true;
            }
        }
    }
}
