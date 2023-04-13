package com.android.server.p011pm;

import android.os.FileUtils;
import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.PackageUsage */
/* loaded from: classes2.dex */
public class PackageUsage extends AbstractStatsBase<Map<String, PackageSetting>> {
    public boolean mIsHistoricalPackageUsageAvailable;

    public PackageUsage() {
        super("package-usage.list", "PackageUsage_DiskWriter", true);
        this.mIsHistoricalPackageUsageAvailable = true;
    }

    public boolean isHistoricalPackageUsageAvailable() {
        return this.mIsHistoricalPackageUsageAvailable;
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public void writeInternal(Map<String, PackageSetting> map) {
        FileOutputStream fileOutputStream;
        long[] lastPackageUsageTimeInMills;
        AtomicFile file = getFile();
        try {
            fileOutputStream = file.startWrite();
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            FileUtils.setPermissions(file.getBaseFile().getPath(), FrameworkStatsLog.DISPLAY_HBM_STATE_CHANGED, 1000, 1032);
            StringBuilder sb = new StringBuilder();
            sb.append("PACKAGE_USAGE__VERSION_1");
            sb.append('\n');
            bufferedOutputStream.write(sb.toString().getBytes(StandardCharsets.US_ASCII));
            for (PackageSetting packageSetting : map.values()) {
                if (packageSetting != null && packageSetting.getPkgState() != null && packageSetting.getPkgState().getLatestPackageUseTimeInMills() != 0) {
                    sb.setLength(0);
                    sb.append(packageSetting.getPackageName());
                    for (long j : packageSetting.getPkgState().getLastPackageUsageTimeInMills()) {
                        sb.append(' ');
                        sb.append(j);
                    }
                    sb.append('\n');
                    bufferedOutputStream.write(sb.toString().getBytes(StandardCharsets.US_ASCII));
                }
            }
            bufferedOutputStream.flush();
            file.finishWrite(fileOutputStream);
        } catch (IOException e2) {
            e = e2;
            if (fileOutputStream != null) {
                file.failWrite(fileOutputStream);
            }
            Log.e("PackageManager", "Failed to write package usage times", e);
        }
    }

    @Override // com.android.server.p011pm.AbstractStatsBase
    public void readInternal(Map<String, PackageSetting> map) {
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2 = null;
        try {
            try {
                bufferedInputStream = new BufferedInputStream(getFile().openRead());
            } catch (Throwable th) {
                th = th;
            }
        } catch (FileNotFoundException unused) {
        } catch (IOException e) {
            e = e;
        }
        try {
            StringBuilder sb = new StringBuilder();
            String readLine = readLine(bufferedInputStream, sb);
            if (readLine != null) {
                if ("PACKAGE_USAGE__VERSION_1".equals(readLine)) {
                    readVersion1LP(map, bufferedInputStream, sb);
                } else {
                    readVersion0LP(map, bufferedInputStream, sb, readLine);
                }
            }
            IoUtils.closeQuietly(bufferedInputStream);
        } catch (FileNotFoundException unused2) {
            bufferedInputStream2 = bufferedInputStream;
            this.mIsHistoricalPackageUsageAvailable = false;
            IoUtils.closeQuietly(bufferedInputStream2);
        } catch (IOException e2) {
            e = e2;
            bufferedInputStream2 = bufferedInputStream;
            Log.w("PackageManager", "Failed to read package usage times", e);
            IoUtils.closeQuietly(bufferedInputStream2);
        } catch (Throwable th2) {
            th = th2;
            bufferedInputStream2 = bufferedInputStream;
            IoUtils.closeQuietly(bufferedInputStream2);
            throw th;
        }
    }

    public final void readVersion0LP(Map<String, PackageSetting> map, InputStream inputStream, StringBuilder sb, String str) throws IOException {
        while (str != null) {
            String[] split = str.split(" ");
            if (split.length != 2) {
                throw new IOException("Failed to parse " + str + " as package-timestamp pair.");
            }
            PackageSetting packageSetting = map.get(split[0]);
            if (packageSetting != null) {
                long parseAsLong = parseAsLong(split[1]);
                for (int i = 0; i < 8; i++) {
                    packageSetting.getPkgState().setLastPackageUsageTimeInMills(i, parseAsLong);
                }
            }
            str = readLine(inputStream, sb);
        }
    }

    public final void readVersion1LP(Map<String, PackageSetting> map, InputStream inputStream, StringBuilder sb) throws IOException {
        while (true) {
            String readLine = readLine(inputStream, sb);
            if (readLine == null) {
                return;
            }
            String[] split = readLine.split(" ");
            if (split.length != 9) {
                throw new IOException("Failed to parse " + readLine + " as a timestamp array.");
            }
            int i = 0;
            PackageSetting packageSetting = map.get(split[0]);
            if (packageSetting != null) {
                while (i < 8) {
                    int i2 = i + 1;
                    packageSetting.getPkgState().setLastPackageUsageTimeInMills(i, parseAsLong(split[i2]));
                    i = i2;
                }
            }
        }
    }

    public final long parseAsLong(String str) throws IOException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to parse " + str + " as a long.", e);
        }
    }

    public final String readLine(InputStream inputStream, StringBuilder sb) throws IOException {
        return readToken(inputStream, sb, '\n');
    }

    public final String readToken(InputStream inputStream, StringBuilder sb, char c) throws IOException {
        sb.setLength(0);
        while (true) {
            int read = inputStream.read();
            if (read == -1) {
                if (sb.length() == 0) {
                    return null;
                }
                throw new IOException("Unexpected EOF");
            } else if (read == c) {
                return sb.toString();
            } else {
                sb.append((char) read);
            }
        }
    }
}
