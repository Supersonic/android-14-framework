package com.android.server.p011pm;

import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.CompilerStats */
/* loaded from: classes2.dex */
public class CompilerStats extends AbstractStatsBase<Void> {
    public final Map<String, PackageStats> packageStats;

    /* renamed from: com.android.server.pm.CompilerStats$PackageStats */
    /* loaded from: classes2.dex */
    public static class PackageStats {
        public final Map<String, Long> compileTimePerCodePath = new ArrayMap(2);
        public final String packageName;

        public PackageStats(String str) {
            this.packageName = str;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public long getCompileTime(String str) {
            String storedPathFromCodePath = getStoredPathFromCodePath(str);
            synchronized (this.compileTimePerCodePath) {
                Long l = this.compileTimePerCodePath.get(storedPathFromCodePath);
                if (l == null) {
                    return 0L;
                }
                return l.longValue();
            }
        }

        public void setCompileTime(String str, long j) {
            String storedPathFromCodePath = getStoredPathFromCodePath(str);
            synchronized (this.compileTimePerCodePath) {
                if (j <= 0) {
                    this.compileTimePerCodePath.remove(storedPathFromCodePath);
                } else {
                    this.compileTimePerCodePath.put(storedPathFromCodePath, Long.valueOf(j));
                }
            }
        }

        public static String getStoredPathFromCodePath(String str) {
            return str.substring(str.lastIndexOf(File.separatorChar) + 1);
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            synchronized (this.compileTimePerCodePath) {
                if (this.compileTimePerCodePath.size() == 0) {
                    indentingPrintWriter.println("(No recorded stats)");
                } else {
                    for (Map.Entry<String, Long> entry : this.compileTimePerCodePath.entrySet()) {
                        indentingPrintWriter.println(" " + entry.getKey() + " - " + entry.getValue());
                    }
                }
            }
        }
    }

    public CompilerStats() {
        super("package-cstats.list", "CompilerStats_DiskWriter", false);
        this.packageStats = new HashMap();
    }

    public PackageStats getPackageStats(String str) {
        PackageStats packageStats;
        synchronized (this.packageStats) {
            packageStats = this.packageStats.get(str);
        }
        return packageStats;
    }

    public PackageStats createPackageStats(String str) {
        PackageStats packageStats;
        synchronized (this.packageStats) {
            packageStats = new PackageStats(str);
            this.packageStats.put(str, packageStats);
        }
        return packageStats;
    }

    public PackageStats getOrCreatePackageStats(String str) {
        synchronized (this.packageStats) {
            PackageStats packageStats = this.packageStats.get(str);
            if (packageStats != null) {
                return packageStats;
            }
            return createPackageStats(str);
        }
    }

    public void write(Writer writer) {
        FastPrintWriter fastPrintWriter = new FastPrintWriter(writer);
        fastPrintWriter.print("PACKAGE_MANAGER__COMPILER_STATS__");
        fastPrintWriter.println(1);
        synchronized (this.packageStats) {
            for (PackageStats packageStats : this.packageStats.values()) {
                synchronized (packageStats.compileTimePerCodePath) {
                    if (!packageStats.compileTimePerCodePath.isEmpty()) {
                        fastPrintWriter.println(packageStats.getPackageName());
                        for (Map.Entry entry : packageStats.compileTimePerCodePath.entrySet()) {
                            fastPrintWriter.println(PackageManagerShellCommandDataLoader.STDIN_PATH + ((String) entry.getKey()) + XmlUtils.STRING_ARRAY_SEPARATOR + entry.getValue());
                        }
                    }
                }
            }
        }
        fastPrintWriter.flush();
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0070, code lost:
        throw new java.lang.IllegalArgumentException("Could not parse data " + r3);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean read(Reader reader) {
        synchronized (this.packageStats) {
            this.packageStats.clear();
            try {
                BufferedReader bufferedReader = new BufferedReader(reader);
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    throw new IllegalArgumentException("No version line found.");
                }
                if (!readLine.startsWith("PACKAGE_MANAGER__COMPILER_STATS__")) {
                    throw new IllegalArgumentException("Invalid version line: " + readLine);
                }
                int parseInt = Integer.parseInt(readLine.substring(33));
                if (parseInt != 1) {
                    throw new IllegalArgumentException("Unexpected version: " + parseInt);
                }
                PackageStats packageStats = new PackageStats("fake package");
                while (true) {
                    String readLine2 = bufferedReader.readLine();
                    if (readLine2 != null) {
                        if (readLine2.startsWith(PackageManagerShellCommandDataLoader.STDIN_PATH)) {
                            int indexOf = readLine2.indexOf(58);
                            if (indexOf == -1 || indexOf == 1) {
                                break;
                            }
                            packageStats.setCompileTime(readLine2.substring(1, indexOf), Long.parseLong(readLine2.substring(indexOf + 1)));
                        } else {
                            packageStats = getOrCreatePackageStats(readLine2);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("PackageManager", "Error parsing compiler stats", e);
                return false;
            }
        }
        return true;
    }

    public void writeNow() {
        writeNow(null);
    }

    public boolean maybeWriteAsync() {
        return maybeWriteAsync(null);
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public void writeInternal(Void r3) {
        FileOutputStream fileOutputStream;
        AtomicFile file = getFile();
        try {
            fileOutputStream = file.startWrite();
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            write(outputStreamWriter);
            outputStreamWriter.flush();
            file.finishWrite(fileOutputStream);
        } catch (IOException e2) {
            e = e2;
            if (fileOutputStream != null) {
                file.failWrite(fileOutputStream);
            }
            Log.e("PackageManager", "Failed to write compiler stats", e);
        }
    }

    public void read() {
        read((CompilerStats) null);
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public void readInternal(Void r4) {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getFile().openRead()));
        } catch (FileNotFoundException unused) {
        } catch (Throwable th) {
            th = th;
        }
        try {
            read((Reader) bufferedReader);
            IoUtils.closeQuietly(bufferedReader);
        } catch (FileNotFoundException unused2) {
            bufferedReader2 = bufferedReader;
            IoUtils.closeQuietly(bufferedReader2);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            IoUtils.closeQuietly(bufferedReader2);
            throw th;
        }
    }
}
