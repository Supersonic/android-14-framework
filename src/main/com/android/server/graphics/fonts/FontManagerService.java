package com.android.server.graphics.fonts;

import android.annotation.EnforcePermission;
import android.annotation.RequiresPermission;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.fonts.FontUpdateRequest;
import android.graphics.fonts.SystemFonts;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.SharedMemory;
import android.os.ShellCallback;
import android.system.ErrnoException;
import android.text.FontConfig;
import android.util.AndroidException;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.graphics.fonts.IFontManager;
import com.android.internal.security.VerityUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.graphics.fonts.UpdatableFontDir;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.DirectByteBuffer;
import java.nio.NioUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class FontManagerService extends IFontManager.Stub {
    public final Context mContext;
    public String mDebugCertFilePath;
    public final boolean mIsSafeMode;
    @GuardedBy({"mSerializedFontMapLock"})
    public SharedMemory mSerializedFontMap;
    public final Object mSerializedFontMapLock;
    @GuardedBy({"mUpdatableFontDirLock"})
    public UpdatableFontDir mUpdatableFontDir;
    public final Object mUpdatableFontDirLock;

    @EnforcePermission("android.permission.UPDATE_FONTS")
    @RequiresPermission("android.permission.UPDATE_FONTS")
    public FontConfig getFontConfig() {
        super.getFontConfig_enforcePermission();
        return getSystemFontConfig();
    }

    @RequiresPermission("android.permission.UPDATE_FONTS")
    public int updateFontFamily(List<FontUpdateRequest> list, int i) {
        try {
            Preconditions.checkArgumentNonnegative(i);
            Objects.requireNonNull(list);
            getContext().enforceCallingPermission("android.permission.UPDATE_FONTS", "UPDATE_FONTS permission required.");
            try {
                update(i, list);
                closeFileDescriptors(list);
                return 0;
            } catch (SystemFontException e) {
                Slog.e("FontManagerService", "Failed to update font family", e);
                int errorCode = e.getErrorCode();
                closeFileDescriptors(list);
                return errorCode;
            }
        } catch (Throwable th) {
            closeFileDescriptors(list);
            throw th;
        }
    }

    public static void closeFileDescriptors(List<FontUpdateRequest> list) {
        ParcelFileDescriptor fd;
        if (list == null) {
            return;
        }
        for (FontUpdateRequest fontUpdateRequest : list) {
            if (fontUpdateRequest != null && (fd = fontUpdateRequest.getFd()) != null) {
                try {
                    fd.close();
                } catch (IOException e) {
                    Slog.w("FontManagerService", "Failed to close fd", e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SystemFontException extends AndroidException {
        private final int mErrorCode;

        public SystemFontException(int i, String str, Throwable th) {
            super(str, th);
            this.mErrorCode = i;
        }

        public SystemFontException(int i, String str) {
            super(str);
            this.mErrorCode = i;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }
    }

    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public final FontManagerService mService;

        public Lifecycle(Context context, boolean z) {
            super(context);
            this.mService = new FontManagerService(context, z);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            LocalServices.addService(FontManagerInternal.class, new FontManagerInternal() { // from class: com.android.server.graphics.fonts.FontManagerService.Lifecycle.1
                @Override // com.android.server.graphics.fonts.FontManagerInternal
                public SharedMemory getSerializedSystemFontMap() {
                    return Lifecycle.this.mService.getCurrentFontMap();
                }
            });
            publishBinderService("font", this.mService);
        }
    }

    /* loaded from: classes.dex */
    public static class FsverityUtilImpl implements UpdatableFontDir.FsverityUtil {
        public final String[] mDerCertPaths;

        public FsverityUtilImpl(String[] strArr) {
            this.mDerCertPaths = strArr;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r7v2 */
        /* JADX WARN: Type inference failed for: r7v3 */
        /* JADX WARN: Type inference failed for: r7v4 */
        /* JADX WARN: Type inference failed for: r7v5 */
        /* JADX WARN: Type inference failed for: r7v8 */
        /* JADX WARN: Type inference failed for: r7v9 */
        @Override // com.android.server.graphics.fonts.UpdatableFontDir.FsverityUtil
        public boolean isFromTrustedProvider(String str, byte[] bArr) {
            FileInputStream fileInputStream;
            byte[] fsverityDigest = VerityUtils.getFsverityDigest(str);
            if (fsverityDigest == null) {
                Log.w("FontManagerService", "Failed to get fs-verity digest for " + str);
                return false;
            }
            String[] strArr = this.mDerCertPaths;
            int length = strArr.length;
            int i = 0;
            ?? r7 = strArr;
            while (i < length) {
                String str2 = r7[i];
                try {
                    fileInputStream = new FileInputStream(str2);
                } catch (IOException unused) {
                    Log.w("FontManagerService", "Failed to read certificate file: " + str2);
                }
                if (VerityUtils.verifyPkcs7DetachedSignature(bArr, fsverityDigest, fileInputStream)) {
                    fileInputStream.close();
                    r7 = 1;
                    return true;
                }
                fileInputStream.close();
                i++;
                r7 = r7;
            }
            return false;
        }

        @Override // com.android.server.graphics.fonts.UpdatableFontDir.FsverityUtil
        public void setUpFsverity(String str) throws IOException {
            VerityUtils.setUpFsverity(str);
        }

        @Override // com.android.server.graphics.fonts.UpdatableFontDir.FsverityUtil
        public boolean rename(File file, File file2) {
            return file.renameTo(file2);
        }
    }

    public FontManagerService(Context context, boolean z) {
        this.mUpdatableFontDirLock = new Object();
        this.mDebugCertFilePath = null;
        this.mSerializedFontMapLock = new Object();
        this.mSerializedFontMap = null;
        if (z) {
            Slog.i("FontManagerService", "Entering safe mode. Deleting all font updates.");
            UpdatableFontDir.deleteAllFiles(new File("/data/fonts/files"), new File("/data/fonts/config/config.xml"));
        }
        this.mContext = context;
        this.mIsSafeMode = z;
        initialize();
    }

    public final UpdatableFontDir createUpdatableFontDir() {
        if (!this.mIsSafeMode && VerityUtils.isFsVeritySupported()) {
            String[] stringArray = this.mContext.getResources().getStringArray(17236073);
            if (this.mDebugCertFilePath != null && (Build.IS_USERDEBUG || Build.IS_ENG)) {
                String[] strArr = new String[stringArray.length + 1];
                System.arraycopy(stringArray, 0, strArr, 0, stringArray.length);
                strArr[stringArray.length] = this.mDebugCertFilePath;
                stringArray = strArr;
            }
            return new UpdatableFontDir(new File("/data/fonts/files"), new OtfFontFileParser(), new FsverityUtilImpl(stringArray), new File("/data/fonts/config/config.xml"));
        }
        return null;
    }

    public void addDebugCertificate(String str) {
        this.mDebugCertFilePath = str;
    }

    public final void initialize() {
        synchronized (this.mUpdatableFontDirLock) {
            UpdatableFontDir createUpdatableFontDir = createUpdatableFontDir();
            this.mUpdatableFontDir = createUpdatableFontDir;
            if (createUpdatableFontDir == null) {
                setSerializedFontMap(serializeSystemServerFontMap());
                return;
            }
            createUpdatableFontDir.loadFontFileMap();
            updateSerializedFontMap();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public SharedMemory getCurrentFontMap() {
        SharedMemory sharedMemory;
        synchronized (this.mSerializedFontMapLock) {
            sharedMemory = this.mSerializedFontMap;
        }
        return sharedMemory;
    }

    public void update(int i, List<FontUpdateRequest> list) throws SystemFontException {
        synchronized (this.mUpdatableFontDirLock) {
            UpdatableFontDir updatableFontDir = this.mUpdatableFontDir;
            if (updatableFontDir == null) {
                throw new SystemFontException(-7, "The font updater is disabled.");
            }
            if (i != -1 && updatableFontDir.getConfigVersion() != i) {
                throw new SystemFontException(-8, "The base config version is older than current.");
            }
            this.mUpdatableFontDir.update(list);
            updateSerializedFontMap();
        }
    }

    public void clearUpdates() {
        UpdatableFontDir.deleteAllFiles(new File("/data/fonts/files"), new File("/data/fonts/config/config.xml"));
        initialize();
    }

    public void restart() {
        initialize();
    }

    public Map<String, File> getFontFileMap() {
        synchronized (this.mUpdatableFontDirLock) {
            UpdatableFontDir updatableFontDir = this.mUpdatableFontDir;
            if (updatableFontDir == null) {
                return Collections.emptyMap();
            }
            return updatableFontDir.getPostScriptMap();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "FontManagerService", printWriter)) {
            new FontManagerShellCommand(this).dumpAll(new IndentingPrintWriter(printWriter, "  "));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new FontManagerShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public FontConfig getSystemFontConfig() {
        synchronized (this.mUpdatableFontDirLock) {
            UpdatableFontDir updatableFontDir = this.mUpdatableFontDir;
            if (updatableFontDir == null) {
                return SystemFonts.getSystemPreinstalledFontConfig();
            }
            return updatableFontDir.getSystemFontConfig();
        }
    }

    public final void updateSerializedFontMap() {
        SharedMemory serializeFontMap = serializeFontMap(getSystemFontConfig());
        if (serializeFontMap == null) {
            serializeFontMap = serializeSystemServerFontMap();
        }
        setSerializedFontMap(serializeFontMap);
    }

    public static SharedMemory serializeFontMap(FontConfig fontConfig) {
        ArrayMap arrayMap = new ArrayMap();
        try {
            try {
                SharedMemory serializeFontMap = Typeface.serializeFontMap(SystemFonts.buildSystemTypefaces(fontConfig, SystemFonts.buildSystemFallback(fontConfig, arrayMap)));
                for (ByteBuffer byteBuffer : arrayMap.values()) {
                    if (byteBuffer instanceof DirectByteBuffer) {
                        NioUtils.freeDirectBuffer(byteBuffer);
                    }
                }
                return serializeFontMap;
            } catch (ErrnoException | IOException e) {
                Slog.w("FontManagerService", "Failed to serialize updatable font map. Retrying with system image fonts.", e);
                for (ByteBuffer byteBuffer2 : arrayMap.values()) {
                    if (byteBuffer2 instanceof DirectByteBuffer) {
                        NioUtils.freeDirectBuffer(byteBuffer2);
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            for (ByteBuffer byteBuffer3 : arrayMap.values()) {
                if (byteBuffer3 instanceof DirectByteBuffer) {
                    NioUtils.freeDirectBuffer(byteBuffer3);
                }
            }
            throw th;
        }
    }

    public static SharedMemory serializeSystemServerFontMap() {
        try {
            return Typeface.serializeFontMap(Typeface.getSystemFontMap());
        } catch (ErrnoException | IOException e) {
            Slog.e("FontManagerService", "Failed to serialize SystemServer system font map", e);
            return null;
        }
    }

    public final void setSerializedFontMap(SharedMemory sharedMemory) {
        SharedMemory sharedMemory2;
        synchronized (this.mSerializedFontMapLock) {
            sharedMemory2 = this.mSerializedFontMap;
            this.mSerializedFontMap = sharedMemory;
        }
        if (sharedMemory2 != null) {
            sharedMemory2.close();
        }
    }
}
