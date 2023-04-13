package com.android.server.p011pm.dex;

import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastPrintWriter;
import com.android.server.p011pm.AbstractStatsBase;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.dex.PackageDynamicCodeLoading */
/* loaded from: classes2.dex */
public class PackageDynamicCodeLoading extends AbstractStatsBase<Void> {
    @VisibleForTesting
    static final int MAX_FILES_PER_OWNER = 100;
    public static final Pattern PACKAGE_LINE_PATTERN = Pattern.compile("([A-Z]):([0-9]+):([^:]*):(.*)");
    public final Object mLock;
    @GuardedBy({"mLock"})
    public Map<String, PackageDynamicCode> mPackageMap;

    public static boolean isValidFileType(int i) {
        return i == 68 || i == 78;
    }

    public PackageDynamicCodeLoading() {
        super("package-dcl.list", "PackageDynamicCodeLoading_DiskWriter", false);
        this.mLock = new Object();
        this.mPackageMap = new HashMap();
    }

    public boolean record(String str, String str2, int i, int i2, String str3) {
        boolean add;
        if (!isValidFileType(i)) {
            throw new IllegalArgumentException("Bad file type: " + i);
        }
        synchronized (this.mLock) {
            PackageDynamicCode packageDynamicCode = this.mPackageMap.get(str);
            if (packageDynamicCode == null) {
                packageDynamicCode = new PackageDynamicCode();
                this.mPackageMap.put(str, packageDynamicCode);
            }
            add = packageDynamicCode.add(str2, (char) i, i2, str3);
        }
        return add;
    }

    public Set<String> getAllPackagesWithDynamicCodeLoading() {
        HashSet hashSet;
        synchronized (this.mLock) {
            hashSet = new HashSet(this.mPackageMap.keySet());
        }
        return hashSet;
    }

    public PackageDynamicCode getPackageDynamicCodeInfo(String str) {
        PackageDynamicCode packageDynamicCode;
        synchronized (this.mLock) {
            PackageDynamicCode packageDynamicCode2 = this.mPackageMap.get(str);
            packageDynamicCode = null;
            if (packageDynamicCode2 != null) {
                packageDynamicCode = new PackageDynamicCode(packageDynamicCode2);
            }
        }
        return packageDynamicCode;
    }

    public boolean removePackage(String str) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPackageMap.remove(str) != null;
        }
        return z;
    }

    public boolean removeUserPackage(String str, int i) {
        synchronized (this.mLock) {
            PackageDynamicCode packageDynamicCode = this.mPackageMap.get(str);
            if (packageDynamicCode == null) {
                return false;
            }
            if (packageDynamicCode.removeUser(i)) {
                if (packageDynamicCode.mFileUsageMap.isEmpty()) {
                    this.mPackageMap.remove(str);
                }
                return true;
            }
            return false;
        }
    }

    public boolean removeFile(String str, String str2, int i) {
        synchronized (this.mLock) {
            PackageDynamicCode packageDynamicCode = this.mPackageMap.get(str);
            if (packageDynamicCode == null) {
                return false;
            }
            if (packageDynamicCode.removeFile(str2, i)) {
                if (packageDynamicCode.mFileUsageMap.isEmpty()) {
                    this.mPackageMap.remove(str);
                }
                return true;
            }
            return false;
        }
    }

    public void syncData(Map<String, Set<Integer>> map) {
        synchronized (this.mLock) {
            Iterator<Map.Entry<String, PackageDynamicCode>> it = this.mPackageMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, PackageDynamicCode> next = it.next();
                Set<Integer> set = map.get(next.getKey());
                if (set == null) {
                    it.remove();
                } else {
                    PackageDynamicCode value = next.getValue();
                    value.syncData(map, set);
                    if (value.mFileUsageMap.isEmpty()) {
                        it.remove();
                    }
                }
            }
        }
    }

    public void maybeWriteAsync() {
        super.maybeWriteAsync(null);
    }

    public void writeNow() {
        super.writeNow(null);
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public final void writeInternal(Void r2) {
        FileOutputStream fileOutputStream;
        AtomicFile file = getFile();
        try {
            fileOutputStream = file.startWrite();
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            write(fileOutputStream);
            file.finishWrite(fileOutputStream);
        } catch (IOException e2) {
            e = e2;
            file.failWrite(fileOutputStream);
            Slog.e("PackageDynamicCodeLoading", "Failed to write dynamic usage for secondary code files.", e);
        }
    }

    @VisibleForTesting
    public void write(OutputStream outputStream) throws IOException {
        HashMap hashMap;
        synchronized (this.mLock) {
            hashMap = new HashMap(this.mPackageMap.size());
            for (Map.Entry<String, PackageDynamicCode> entry : this.mPackageMap.entrySet()) {
                hashMap.put(entry.getKey(), new PackageDynamicCode(entry.getValue()));
            }
        }
        write(outputStream, hashMap);
    }

    public static void write(OutputStream outputStream, Map<String, PackageDynamicCode> map) throws IOException {
        FastPrintWriter fastPrintWriter = new FastPrintWriter(outputStream);
        fastPrintWriter.println("DCL1");
        for (Map.Entry<String, PackageDynamicCode> entry : map.entrySet()) {
            fastPrintWriter.print("P:");
            fastPrintWriter.println(entry.getKey());
            for (Map.Entry<String, DynamicCodeFile> entry2 : entry.getValue().mFileUsageMap.entrySet()) {
                String key = entry2.getKey();
                DynamicCodeFile value = entry2.getValue();
                fastPrintWriter.print(value.mFileType);
                fastPrintWriter.print(':');
                fastPrintWriter.print(value.mUserId);
                fastPrintWriter.print(':');
                String str = "";
                for (String str2 : value.mLoadingPackages) {
                    fastPrintWriter.print(str);
                    fastPrintWriter.print(str2);
                    str = ",";
                }
                fastPrintWriter.print(':');
                fastPrintWriter.println(escape(key));
            }
        }
        fastPrintWriter.flush();
        if (fastPrintWriter.checkError()) {
            throw new IOException("Writer failed");
        }
    }

    public void read() {
        super.read((PackageDynamicCodeLoading) null);
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public final void readInternal(Void r3) {
        FileInputStream fileInputStream = null;
        try {
            try {
                fileInputStream = getFile().openRead();
                read((InputStream) fileInputStream);
            } catch (FileNotFoundException unused) {
            } catch (IOException e) {
                Slog.w("PackageDynamicCodeLoading", "Failed to parse dynamic usage for secondary code files.", e);
            }
        } finally {
            IoUtils.closeQuietly(fileInputStream);
        }
    }

    @VisibleForTesting
    public void read(InputStream inputStream) throws IOException {
        HashMap hashMap = new HashMap();
        read(inputStream, hashMap);
        synchronized (this.mLock) {
            this.mPackageMap = hashMap;
        }
    }

    public static void read(InputStream inputStream, Map<String, PackageDynamicCode> map) throws IOException {
        String readLine;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String readLine2 = bufferedReader.readLine();
        if (!"DCL1".equals(readLine2)) {
            throw new IOException("Incorrect version line: " + readLine2);
        }
        String readLine3 = bufferedReader.readLine();
        if (readLine3 != null && !readLine3.startsWith("P:")) {
            throw new IOException("Malformed line: " + readLine3);
        }
        while (readLine3 != null) {
            String substring = readLine3.substring(2);
            PackageDynamicCode packageDynamicCode = new PackageDynamicCode();
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null || readLine.startsWith("P:")) {
                    break;
                }
                readFileInfo(readLine, packageDynamicCode);
            }
            if (!packageDynamicCode.mFileUsageMap.isEmpty()) {
                map.put(substring, packageDynamicCode);
            }
            readLine3 = readLine;
        }
    }

    public static void readFileInfo(String str, PackageDynamicCode packageDynamicCode) throws IOException {
        try {
            Matcher matcher = PACKAGE_LINE_PATTERN.matcher(str);
            if (!matcher.matches()) {
                throw new IOException("Malformed line: " + str);
            }
            char charAt = matcher.group(1).charAt(0);
            int parseInt = Integer.parseInt(matcher.group(2));
            String[] split = matcher.group(3).split(",");
            String unescape = unescape(matcher.group(4));
            if (split.length == 0) {
                throw new IOException("Malformed line: " + str);
            } else if (!isValidFileType(charAt)) {
                throw new IOException("Unknown file type: " + str);
            } else {
                packageDynamicCode.mFileUsageMap.put(unescape, new DynamicCodeFile(charAt, parseInt, split));
            }
        } catch (RuntimeException e) {
            throw new IOException("Unable to parse line: " + str, e);
        }
    }

    @VisibleForTesting
    public static String escape(String str) {
        if (str.indexOf(92) == -1 && str.indexOf(10) == -1 && str.indexOf(13) == -1) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length() + 10);
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '\n') {
                sb.append("\\n");
            } else if (charAt == '\r') {
                sb.append("\\r");
            } else if (charAt == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    @VisibleForTesting
    public static String unescape(String str) throws IOException {
        int indexOf = str.indexOf(92);
        if (indexOf == -1) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        int i = 0;
        while (indexOf < str.length() - 1) {
            sb.append((CharSequence) str, i, indexOf);
            char charAt = str.charAt(indexOf + 1);
            if (charAt == '\\') {
                sb.append('\\');
            } else if (charAt == 'n') {
                sb.append('\n');
            } else if (charAt == 'r') {
                sb.append('\r');
            } else {
                throw new IOException("Bad escape in: " + str);
            }
            i = indexOf + 2;
            indexOf = str.indexOf(92, i);
            if (indexOf == -1) {
                sb.append((CharSequence) str, i, str.length());
                return sb.toString();
            }
        }
        throw new IOException("Unexpected \\ in: " + str);
    }

    /* renamed from: com.android.server.pm.dex.PackageDynamicCodeLoading$PackageDynamicCode */
    /* loaded from: classes2.dex */
    public static class PackageDynamicCode {
        public final Map<String, DynamicCodeFile> mFileUsageMap;

        public PackageDynamicCode() {
            this.mFileUsageMap = new HashMap();
        }

        public PackageDynamicCode(PackageDynamicCode packageDynamicCode) {
            this.mFileUsageMap = new HashMap(packageDynamicCode.mFileUsageMap.size());
            for (Map.Entry<String, DynamicCodeFile> entry : packageDynamicCode.mFileUsageMap.entrySet()) {
                this.mFileUsageMap.put(entry.getKey(), new DynamicCodeFile(entry.getValue()));
            }
        }

        public final boolean add(String str, char c, int i, String str2) {
            DynamicCodeFile dynamicCodeFile = this.mFileUsageMap.get(str);
            if (dynamicCodeFile == null) {
                if (this.mFileUsageMap.size() >= 100) {
                    return false;
                }
                this.mFileUsageMap.put(str, new DynamicCodeFile(c, i, new String[]{str2}));
                return true;
            } else if (dynamicCodeFile.mUserId != i) {
                throw new IllegalArgumentException("Cannot change userId for '" + str + "' from " + dynamicCodeFile.mUserId + " to " + i);
            } else {
                return dynamicCodeFile.mLoadingPackages.add(str2);
            }
        }

        public final boolean removeUser(int i) {
            Iterator<DynamicCodeFile> it = this.mFileUsageMap.values().iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (it.next().mUserId == i) {
                    it.remove();
                    z = true;
                }
            }
            return z;
        }

        public final boolean removeFile(String str, int i) {
            DynamicCodeFile dynamicCodeFile = this.mFileUsageMap.get(str);
            if (dynamicCodeFile == null || dynamicCodeFile.mUserId != i) {
                return false;
            }
            this.mFileUsageMap.remove(str);
            return true;
        }

        public final void syncData(Map<String, Set<Integer>> map, Set<Integer> set) {
            Iterator<DynamicCodeFile> it = this.mFileUsageMap.values().iterator();
            while (it.hasNext()) {
                DynamicCodeFile next = it.next();
                int i = next.mUserId;
                if (!set.contains(Integer.valueOf(i))) {
                    it.remove();
                } else {
                    Iterator<String> it2 = next.mLoadingPackages.iterator();
                    while (it2.hasNext()) {
                        Set<Integer> set2 = map.get(it2.next());
                        if (set2 == null || !set2.contains(Integer.valueOf(i))) {
                            it2.remove();
                        }
                    }
                    if (next.mLoadingPackages.isEmpty()) {
                        it.remove();
                    }
                }
            }
        }
    }

    /* renamed from: com.android.server.pm.dex.PackageDynamicCodeLoading$DynamicCodeFile */
    /* loaded from: classes2.dex */
    public static class DynamicCodeFile {
        public final char mFileType;
        public final Set<String> mLoadingPackages;
        public final int mUserId;

        public DynamicCodeFile(char c, int i, String... strArr) {
            this.mFileType = c;
            this.mUserId = i;
            this.mLoadingPackages = new HashSet(Arrays.asList(strArr));
        }

        public DynamicCodeFile(DynamicCodeFile dynamicCodeFile) {
            this.mFileType = dynamicCodeFile.mFileType;
            this.mUserId = dynamicCodeFile.mUserId;
            this.mLoadingPackages = new HashSet(dynamicCodeFile.mLoadingPackages);
        }
    }
}
