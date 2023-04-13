package com.android.server.p011pm.dex;

import android.content.pm.SharedLibraryInfo;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.ClassLoaderFactory;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.io.File;
import java.util.List;
/* renamed from: com.android.server.pm.dex.DexoptUtils */
/* loaded from: classes2.dex */
public final class DexoptUtils {
    public static final String SHARED_LIBRARY_LOADER_TYPE = ClassLoaderFactory.getPathClassLoaderName();

    public static String[] getClassLoaderContexts(AndroidPackage androidPackage, List<SharedLibraryInfo> list, boolean[] zArr) {
        String encodeSharedLibraries = list != null ? encodeSharedLibraries(list) : "";
        String encodeClassLoader = encodeClassLoader("", androidPackage.getClassLoaderName(), encodeSharedLibraries);
        if (ArrayUtils.isEmpty(androidPackage.getSplitCodePaths())) {
            return new String[]{encodeClassLoader};
        }
        String[] splitRelativeCodePaths = getSplitRelativeCodePaths(androidPackage);
        String name = new File(androidPackage.getBaseApkPath()).getName();
        int i = 1;
        int length = splitRelativeCodePaths.length + 1;
        String[] strArr = new String[length];
        if (!zArr[0]) {
            encodeClassLoader = null;
        }
        strArr[0] = encodeClassLoader;
        SparseArray<int[]> splitDependencies = androidPackage.getSplitDependencies();
        if (!androidPackage.isIsolatedSplitLoading() || splitDependencies == null || splitDependencies.size() == 0) {
            while (i < length) {
                if (zArr[i]) {
                    strArr[i] = encodeClassLoader(name, androidPackage.getClassLoaderName(), encodeSharedLibraries);
                } else {
                    strArr[i] = null;
                }
                name = encodeClasspath(name, splitRelativeCodePaths[i - 1]);
                i++;
            }
        } else {
            String[] strArr2 = new String[splitRelativeCodePaths.length];
            for (int i2 = 0; i2 < splitRelativeCodePaths.length; i2++) {
                strArr2[i2] = encodeClassLoader(splitRelativeCodePaths[i2], androidPackage.getSplitClassLoaderNames()[i2]);
            }
            String encodeClassLoader2 = encodeClassLoader(name, androidPackage.getClassLoaderName());
            for (int i3 = 1; i3 < splitDependencies.size(); i3++) {
                int keyAt = splitDependencies.keyAt(i3);
                if (zArr[keyAt]) {
                    getParentDependencies(keyAt, strArr2, splitDependencies, strArr, encodeClassLoader2);
                }
            }
            while (i < length) {
                String encodeClassLoader3 = encodeClassLoader("", androidPackage.getSplitClassLoaderNames()[i - 1]);
                if (zArr[i]) {
                    if (strArr[i] != null) {
                        encodeClassLoader3 = encodeClassLoaderChain(encodeClassLoader3, strArr[i]) + encodeSharedLibraries;
                    }
                    strArr[i] = encodeClassLoader3;
                } else {
                    strArr[i] = null;
                }
                i++;
            }
        }
        return strArr;
    }

    public static String getParentDependencies(int i, String[] strArr, SparseArray<int[]> sparseArray, String[] strArr2, String str) {
        if (i == 0) {
            return str;
        }
        String str2 = strArr2[i];
        if (str2 != null) {
            return str2;
        }
        int i2 = sparseArray.get(i)[0];
        String parentDependencies = getParentDependencies(i2, strArr, sparseArray, strArr2, str);
        if (i2 != 0) {
            parentDependencies = encodeClassLoaderChain(strArr[i2 - 1], parentDependencies);
        }
        strArr2[i] = parentDependencies;
        return parentDependencies;
    }

    public static String encodeSharedLibrary(SharedLibraryInfo sharedLibraryInfo) {
        List allCodePaths = sharedLibraryInfo.getAllCodePaths();
        String encodeClassLoader = encodeClassLoader(encodeClasspath((String[]) allCodePaths.toArray(new String[allCodePaths.size()])), SHARED_LIBRARY_LOADER_TYPE);
        if (sharedLibraryInfo.getDependencies() != null) {
            return encodeClassLoader + encodeSharedLibraries(sharedLibraryInfo.getDependencies());
        }
        return encodeClassLoader;
    }

    public static String encodeSharedLibraries(List<SharedLibraryInfo> list) {
        String str = "{";
        boolean z = true;
        for (SharedLibraryInfo sharedLibraryInfo : list) {
            if (!z) {
                str = str + "#";
            }
            str = str + encodeSharedLibrary(sharedLibraryInfo);
            z = false;
        }
        return str + "}";
    }

    public static String encodeClasspath(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            if (sb.length() != 0) {
                sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static String encodeClasspath(String str, String str2) {
        if (str.isEmpty()) {
            return str2;
        }
        return str + XmlUtils.STRING_ARRAY_SEPARATOR + str2;
    }

    public static String encodeClassLoader(String str, String str2) {
        str.getClass();
        if (ClassLoaderFactory.isPathClassLoaderName(str2)) {
            str2 = "PCL";
        } else if (ClassLoaderFactory.isDelegateLastClassLoaderName(str2)) {
            str2 = "DLC";
        } else {
            Slog.wtf("DexoptUtils", "Unsupported classLoaderName: " + str2);
        }
        return str2 + "[" + str + "]";
    }

    public static String encodeClassLoader(String str, String str2, String str3) {
        return encodeClassLoader(str, str2) + str3;
    }

    public static String encodeClassLoaderChain(String str, String str2) {
        if (str.isEmpty()) {
            return str2;
        }
        if (str2.isEmpty()) {
            return str;
        }
        return str + ";" + str2;
    }

    public static String[] getSplitRelativeCodePaths(AndroidPackage androidPackage) {
        String parent = new File(androidPackage.getBaseApkPath()).getParent();
        String[] splitCodePaths = androidPackage.getSplitCodePaths();
        int size = ArrayUtils.size(splitCodePaths);
        String[] strArr = new String[size];
        for (int i = 0; i < size; i++) {
            File file = new File(splitCodePaths[i]);
            strArr[i] = file.getName();
            String parent2 = file.getParent();
            if (!parent2.equals(parent)) {
                Slog.wtf("DexoptUtils", "Split paths have different base paths: " + parent2 + " and " + parent);
            }
        }
        return strArr;
    }
}
