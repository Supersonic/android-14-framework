package com.android.server.graphics.fonts;

import android.graphics.fonts.FontUpdateRequest;
import android.graphics.fonts.SystemFonts;
import android.os.FileUtils;
import android.os.LocaleList;
import android.system.ErrnoException;
import android.system.Os;
import android.text.FontConfig;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Base64;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.graphics.fonts.FontManagerService;
import com.android.server.graphics.fonts.PersistentSystemFontConfig;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class UpdatableFontDir {
    public final AtomicFile mConfigFile;
    public final Function<Map<String, File>, FontConfig> mConfigSupplier;
    public int mConfigVersion;
    public final Supplier<Long> mCurrentTimeSupplier;
    public final File mFilesDir;
    public final ArrayMap<String, FontFileInfo> mFontFileInfoMap;
    public final FsverityUtil mFsverityUtil;
    public long mLastModifiedMillis;
    public final FontFileParser mParser;

    /* loaded from: classes.dex */
    public interface FontFileParser {
        String buildFontFileName(File file) throws IOException;

        String getPostScriptName(File file) throws IOException;

        long getRevision(File file) throws IOException;

        void tryToCreateTypeface(File file) throws Throwable;
    }

    /* loaded from: classes.dex */
    public interface FsverityUtil {
        boolean isFromTrustedProvider(String str, byte[] bArr);

        boolean rename(File file, File file2);

        void setUpFsverity(String str) throws IOException;
    }

    /* loaded from: classes.dex */
    public static final class FontFileInfo {
        public final File mFile;
        public final String mPsName;
        public final long mRevision;

        public FontFileInfo(File file, String str, long j) {
            this.mFile = file;
            this.mPsName = str;
            this.mRevision = j;
        }

        public File getFile() {
            return this.mFile;
        }

        public String getPostScriptName() {
            return this.mPsName;
        }

        public File getRandomizedFontDir() {
            return this.mFile.getParentFile();
        }

        public long getRevision() {
            return this.mRevision;
        }

        public String toString() {
            return "FontFileInfo{mFile=" + this.mFile + ", psName=" + this.mPsName + ", mRevision=" + this.mRevision + '}';
        }
    }

    public UpdatableFontDir(File file, FontFileParser fontFileParser, FsverityUtil fsverityUtil, File file2) {
        this(file, fontFileParser, fsverityUtil, file2, new Supplier() { // from class: com.android.server.graphics.fonts.UpdatableFontDir$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return Long.valueOf(System.currentTimeMillis());
            }
        }, new Function() { // from class: com.android.server.graphics.fonts.UpdatableFontDir$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                FontConfig systemFontConfig;
                systemFontConfig = SystemFonts.getSystemFontConfig((Map) obj, 0L, 0);
                return systemFontConfig;
            }
        });
    }

    public UpdatableFontDir(File file, FontFileParser fontFileParser, FsverityUtil fsverityUtil, File file2, Supplier<Long> supplier, Function<Map<String, File>, FontConfig> function) {
        this.mFontFileInfoMap = new ArrayMap<>();
        this.mFilesDir = file;
        this.mParser = fontFileParser;
        this.mFsverityUtil = fsverityUtil;
        this.mConfigFile = new AtomicFile(file2);
        this.mCurrentTimeSupplier = supplier;
        this.mConfigSupplier = function;
    }

    public void loadFontFileMap() {
        this.mFontFileInfoMap.clear();
        long j = 0;
        this.mLastModifiedMillis = 0L;
        this.mConfigVersion = 1;
        try {
            PersistentSystemFontConfig.Config readPersistentConfig = readPersistentConfig();
            this.mLastModifiedMillis = readPersistentConfig.lastModifiedMillis;
            File[] listFiles = this.mFilesDir.listFiles();
            if (listFiles == null) {
                Slog.e("UpdatableFontDir", "Could not read: " + this.mFilesDir);
                this.mFontFileInfoMap.clear();
                this.mLastModifiedMillis = 0L;
                FileUtils.deleteContents(this.mFilesDir);
                return;
            }
            int length = listFiles.length;
            FontConfig fontConfig = null;
            int i = 0;
            while (i < length) {
                File file = listFiles[i];
                if (!file.getName().startsWith("~~")) {
                    Slog.e("UpdatableFontDir", "Unexpected dir found: " + file);
                    this.mFontFileInfoMap.clear();
                    this.mLastModifiedMillis = j;
                    FileUtils.deleteContents(this.mFilesDir);
                    return;
                }
                if (readPersistentConfig.updatedFontDirs.contains(file.getName())) {
                    File file2 = new File(file, "font.fsv_sig");
                    if (file2.exists()) {
                        try {
                            byte[] readAllBytes = Files.readAllBytes(Paths.get(file2.getAbsolutePath(), new String[0]));
                            File[] listFiles2 = file.listFiles();
                            if (listFiles2 != null && listFiles2.length == 2) {
                                FontFileInfo validateFontFile = validateFontFile(listFiles2[0].equals(file2) ? listFiles2[1] : listFiles2[0], readAllBytes);
                                if (fontConfig == null) {
                                    fontConfig = getSystemFontConfig();
                                }
                                addFileToMapIfSameOrNewer(validateFontFile, fontConfig, true);
                            }
                            Slog.e("UpdatableFontDir", "Unexpected files in dir: " + file);
                            return;
                        } catch (IOException unused) {
                            Slog.e("UpdatableFontDir", "Failed to read signature file.");
                            return;
                        }
                    }
                    Slog.i("UpdatableFontDir", "The signature file is missing.");
                    FileUtils.deleteContentsAndDir(file);
                } else {
                    Slog.i("UpdatableFontDir", "Deleting obsolete dir: " + file);
                    FileUtils.deleteContentsAndDir(file);
                }
                i++;
                j = 0;
            }
        } catch (Throwable th) {
            try {
                Slog.e("UpdatableFontDir", "Failed to load font mappings.", th);
            } finally {
                this.mFontFileInfoMap.clear();
                this.mLastModifiedMillis = 0L;
                FileUtils.deleteContents(this.mFilesDir);
            }
        }
    }

    public void update(List<FontUpdateRequest> list) throws FontManagerService.SystemFontException {
        for (FontUpdateRequest fontUpdateRequest : list) {
            int type = fontUpdateRequest.getType();
            if (type == 0) {
                Objects.requireNonNull(fontUpdateRequest.getFd());
                Objects.requireNonNull(fontUpdateRequest.getSignature());
            } else if (type == 1) {
                Objects.requireNonNull(fontUpdateRequest.getFontFamily());
                Objects.requireNonNull(fontUpdateRequest.getFontFamily().getName());
            }
        }
        ArrayMap<? extends String, ? extends FontFileInfo> arrayMap = new ArrayMap<>(this.mFontFileInfoMap);
        PersistentSystemFontConfig.Config readPersistentConfig = readPersistentConfig();
        HashMap hashMap = new HashMap();
        for (int i = 0; i < readPersistentConfig.fontFamilies.size(); i++) {
            FontUpdateRequest.Family family = readPersistentConfig.fontFamilies.get(i);
            hashMap.put(family.getName(), family);
        }
        long j = this.mLastModifiedMillis;
        try {
            for (FontUpdateRequest fontUpdateRequest2 : list) {
                int type2 = fontUpdateRequest2.getType();
                if (type2 == 0) {
                    installFontFile(fontUpdateRequest2.getFd().getFileDescriptor(), fontUpdateRequest2.getSignature());
                } else if (type2 == 1) {
                    FontUpdateRequest.Family fontFamily = fontUpdateRequest2.getFontFamily();
                    hashMap.put(fontFamily.getName(), fontFamily);
                }
            }
            for (FontUpdateRequest.Family family2 : hashMap.values()) {
                if (resolveFontFilesForNamedFamily(family2) == null) {
                    throw new FontManagerService.SystemFontException(-9, "Required fonts are not available");
                }
            }
            this.mLastModifiedMillis = this.mCurrentTimeSupplier.get().longValue();
            PersistentSystemFontConfig.Config config = new PersistentSystemFontConfig.Config();
            config.lastModifiedMillis = this.mLastModifiedMillis;
            for (FontFileInfo fontFileInfo : this.mFontFileInfoMap.values()) {
                config.updatedFontDirs.add(fontFileInfo.getRandomizedFontDir().getName());
            }
            config.fontFamilies.addAll(hashMap.values());
            writePersistentConfig(config);
            this.mConfigVersion++;
        } catch (Throwable th) {
            this.mFontFileInfoMap.clear();
            this.mFontFileInfoMap.putAll(arrayMap);
            this.mLastModifiedMillis = j;
            throw th;
        }
    }

    public final void installFontFile(FileDescriptor fileDescriptor, byte[] bArr) throws FontManagerService.SystemFontException {
        File randomDir = getRandomDir(this.mFilesDir);
        if (!randomDir.mkdir()) {
            throw new FontManagerService.SystemFontException(-1, "Failed to create font directory.");
        }
        try {
            Os.chmod(randomDir.getAbsolutePath(), 457);
            try {
                File file = new File(randomDir, "font.ttf");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        FileUtils.copy(fileDescriptor, fileOutputStream.getFD());
                        fileOutputStream.close();
                        try {
                            this.mFsverityUtil.setUpFsverity(file.getAbsolutePath());
                            try {
                                String buildFontFileName = this.mParser.buildFontFileName(file);
                                if (buildFontFileName == null) {
                                    throw new FontManagerService.SystemFontException(-4, "Failed to read PostScript name from font file");
                                }
                                File file2 = new File(randomDir, buildFontFileName);
                                if (!this.mFsverityUtil.rename(file, file2)) {
                                    throw new FontManagerService.SystemFontException(-1, "Failed to move verified font file.");
                                }
                                try {
                                    Os.chmod(file2.getAbsolutePath(), FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
                                    File file3 = new File(randomDir, "font.fsv_sig");
                                    try {
                                        FileOutputStream fileOutputStream2 = new FileOutputStream(file3);
                                        try {
                                            fileOutputStream2.write(bArr);
                                            fileOutputStream2.close();
                                            try {
                                                Os.chmod(file3.getAbsolutePath(), FrameworkStatsLog.NON_A11Y_TOOL_SERVICE_WARNING_REPORT);
                                                FontFileInfo validateFontFile = validateFontFile(file2, bArr);
                                                this.mParser.tryToCreateTypeface(validateFontFile.getFile());
                                                if (!addFileToMapIfSameOrNewer(validateFontFile, getSystemFontConfig(), false)) {
                                                    throw new FontManagerService.SystemFontException(-5, "Downgrading font file is forbidden.");
                                                }
                                            } catch (ErrnoException e) {
                                                throw new FontManagerService.SystemFontException(-1, "Failed to change the signature file mode to 600", e);
                                            }
                                        } catch (Throwable th) {
                                            try {
                                                fileOutputStream2.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                            throw th;
                                        }
                                    } catch (IOException e2) {
                                        throw new FontManagerService.SystemFontException(-1, "Failed to write font signature file to storage.", e2);
                                    }
                                } catch (ErrnoException e3) {
                                    throw new FontManagerService.SystemFontException(-1, "Failed to change font file mode to 644", e3);
                                }
                            } catch (IOException e4) {
                                throw new FontManagerService.SystemFontException(-3, "Failed to read PostScript name from font file", e4);
                            }
                        } catch (IOException e5) {
                            throw new FontManagerService.SystemFontException(-2, "Failed to setup fs-verity.", e5);
                        }
                    } catch (Throwable th3) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                } catch (IOException e6) {
                    throw new FontManagerService.SystemFontException(-1, "Failed to write font file to storage.", e6);
                }
            } catch (Throwable th5) {
                FileUtils.deleteContentsAndDir(randomDir);
                throw th5;
            }
        } catch (ErrnoException e7) {
            throw new FontManagerService.SystemFontException(-1, "Failed to change mode to 711", e7);
        }
    }

    public static File getRandomDir(File file) {
        File file2;
        SecureRandom secureRandom = new SecureRandom();
        byte[] bArr = new byte[16];
        do {
            secureRandom.nextBytes(bArr);
            file2 = new File(file, "~~" + Base64.encodeToString(bArr, 10));
        } while (file2.exists());
        return file2;
    }

    public final FontFileInfo lookupFontFileInfo(String str) {
        return this.mFontFileInfoMap.get(str);
    }

    public final void putFontFileInfo(FontFileInfo fontFileInfo) {
        this.mFontFileInfoMap.put(fontFileInfo.getPostScriptName(), fontFileInfo);
    }

    public final boolean addFileToMapIfSameOrNewer(FontFileInfo fontFileInfo, FontConfig fontConfig, boolean z) {
        FontFileInfo lookupFontFileInfo = lookupFontFileInfo(fontFileInfo.getPostScriptName());
        boolean z2 = true;
        if (lookupFontFileInfo != null ? lookupFontFileInfo.getRevision() > fontFileInfo.getRevision() : getPreinstalledFontRevision(fontFileInfo, fontConfig) > fontFileInfo.getRevision()) {
            z2 = false;
        }
        if (z2) {
            if (z && lookupFontFileInfo != null) {
                FileUtils.deleteContentsAndDir(lookupFontFileInfo.getRandomizedFontDir());
            }
            putFontFileInfo(fontFileInfo);
        } else if (z) {
            FileUtils.deleteContentsAndDir(fontFileInfo.getRandomizedFontDir());
        }
        return z2;
    }

    public final long getPreinstalledFontRevision(FontFileInfo fontFileInfo, FontConfig fontConfig) {
        String postScriptName = fontFileInfo.getPostScriptName();
        FontConfig.Font font = null;
        for (int i = 0; i < fontConfig.getFontFamilies().size(); i++) {
            FontConfig.FontFamily fontFamily = (FontConfig.FontFamily) fontConfig.getFontFamilies().get(i);
            int i2 = 0;
            while (true) {
                if (i2 < fontFamily.getFontList().size()) {
                    FontConfig.Font font2 = (FontConfig.Font) fontFamily.getFontList().get(i2);
                    if (font2.getPostScriptName().equals(postScriptName)) {
                        font = font2;
                        break;
                    }
                    i2++;
                }
            }
        }
        for (int i3 = 0; i3 < fontConfig.getNamedFamilyLists().size(); i3++) {
            FontConfig.NamedFamilyList namedFamilyList = (FontConfig.NamedFamilyList) fontConfig.getNamedFamilyLists().get(i3);
            for (int i4 = 0; i4 < namedFamilyList.getFamilies().size(); i4++) {
                FontConfig.FontFamily fontFamily2 = (FontConfig.FontFamily) namedFamilyList.getFamilies().get(i4);
                int i5 = 0;
                while (true) {
                    if (i5 < fontFamily2.getFontList().size()) {
                        FontConfig.Font font3 = (FontConfig.Font) fontFamily2.getFontList().get(i5);
                        if (font3.getPostScriptName().equals(postScriptName)) {
                            font = font3;
                            break;
                        }
                        i5++;
                    }
                }
            }
        }
        if (font == null) {
            return -1L;
        }
        File originalFile = font.getOriginalFile() != null ? font.getOriginalFile() : font.getFile();
        if (originalFile.exists()) {
            long fontRevision = getFontRevision(originalFile);
            if (fontRevision == -1) {
                Slog.w("UpdatableFontDir", "Invalid preinstalled font file");
            }
            return fontRevision;
        }
        return -1L;
    }

    public final FontFileInfo validateFontFile(File file, byte[] bArr) throws FontManagerService.SystemFontException {
        if (!this.mFsverityUtil.isFromTrustedProvider(file.getAbsolutePath(), bArr)) {
            throw new FontManagerService.SystemFontException(-2, "Font validation failed. Fs-verity is not enabled: " + file);
        }
        try {
            String postScriptName = this.mParser.getPostScriptName(file);
            long fontRevision = getFontRevision(file);
            if (fontRevision == -1) {
                throw new FontManagerService.SystemFontException(-3, "Font validation failed. Could not read font revision: " + file);
            }
            return new FontFileInfo(file, postScriptName, fontRevision);
        } catch (IOException unused) {
            throw new FontManagerService.SystemFontException(-4, "Font validation failed. Could not read PostScript name name: " + file);
        }
    }

    public final long getFontRevision(File file) {
        try {
            return this.mParser.getRevision(file);
        } catch (IOException e) {
            Slog.e("UpdatableFontDir", "Failed to read font file", e);
            return -1L;
        }
    }

    public final FontConfig.NamedFamilyList resolveFontFilesForNamedFamily(FontUpdateRequest.Family family) {
        List fonts = family.getFonts();
        ArrayList arrayList = new ArrayList(fonts.size());
        for (int i = 0; i < fonts.size(); i++) {
            FontUpdateRequest.Font font = (FontUpdateRequest.Font) fonts.get(i);
            FontFileInfo fontFileInfo = this.mFontFileInfoMap.get(font.getPostScriptName());
            if (fontFileInfo == null) {
                Slog.e("UpdatableFontDir", "Failed to lookup font file that has " + font.getPostScriptName());
                return null;
            }
            arrayList.add(new FontConfig.Font(fontFileInfo.mFile, (File) null, fontFileInfo.getPostScriptName(), font.getFontStyle(), font.getIndex(), font.getFontVariationSettings(), (String) null));
        }
        return new FontConfig.NamedFamilyList(Collections.singletonList(new FontConfig.FontFamily(arrayList, LocaleList.getEmptyLocaleList(), 0)), family.getName());
    }

    public Map<String, File> getPostScriptMap() {
        ArrayMap arrayMap = new ArrayMap();
        for (int i = 0; i < this.mFontFileInfoMap.size(); i++) {
            FontFileInfo valueAt = this.mFontFileInfoMap.valueAt(i);
            arrayMap.put(valueAt.getPostScriptName(), valueAt.getFile());
        }
        return arrayMap;
    }

    public FontConfig getSystemFontConfig() {
        FontConfig apply = this.mConfigSupplier.apply(getPostScriptMap());
        List<FontUpdateRequest.Family> list = readPersistentConfig().fontFamilies;
        ArrayList arrayList = new ArrayList(apply.getNamedFamilyLists().size() + list.size());
        arrayList.addAll(apply.getNamedFamilyLists());
        for (int i = 0; i < list.size(); i++) {
            FontConfig.NamedFamilyList resolveFontFilesForNamedFamily = resolveFontFilesForNamedFamily(list.get(i));
            if (resolveFontFilesForNamedFamily != null) {
                arrayList.add(resolveFontFilesForNamedFamily);
            }
        }
        return new FontConfig(apply.getFontFamilies(), apply.getAliases(), arrayList, this.mLastModifiedMillis, this.mConfigVersion);
    }

    public final PersistentSystemFontConfig.Config readPersistentConfig() {
        PersistentSystemFontConfig.Config config = new PersistentSystemFontConfig.Config();
        try {
            FileInputStream openRead = this.mConfigFile.openRead();
            PersistentSystemFontConfig.loadFromXml(openRead, config);
            if (openRead != null) {
                openRead.close();
            }
        } catch (IOException | XmlPullParserException unused) {
        }
        return config;
    }

    public final void writePersistentConfig(PersistentSystemFontConfig.Config config) throws FontManagerService.SystemFontException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mConfigFile.startWrite();
            PersistentSystemFontConfig.writeToXml(fileOutputStream, config);
            this.mConfigFile.finishWrite(fileOutputStream);
        } catch (IOException e) {
            if (fileOutputStream != null) {
                this.mConfigFile.failWrite(fileOutputStream);
            }
            throw new FontManagerService.SystemFontException(-6, "Failed to write config XML.", e);
        }
    }

    public int getConfigVersion() {
        return this.mConfigVersion;
    }

    public static void deleteAllFiles(File file, File file2) {
        try {
            new AtomicFile(file2).delete();
        } catch (Throwable unused) {
            Slog.w("UpdatableFontDir", "Failed to delete " + file2);
        }
        try {
            FileUtils.deleteContents(file);
        } catch (Throwable unused2) {
            Slog.w("UpdatableFontDir", "Failed to delete " + file);
        }
    }
}
