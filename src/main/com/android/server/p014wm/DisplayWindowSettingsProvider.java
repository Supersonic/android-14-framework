package com.android.server.p014wm;

import android.os.Environment;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import android.view.DisplayAddress;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p014wm.DisplayWindowSettings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.wm.DisplayWindowSettingsProvider */
/* loaded from: classes2.dex */
public class DisplayWindowSettingsProvider implements DisplayWindowSettings.SettingsProvider {
    public ReadableSettings mBaseSettings;
    public final WritableSettings mOverrideSettings;

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$ReadableSettingsStorage */
    /* loaded from: classes2.dex */
    public interface ReadableSettingsStorage {
        InputStream openRead() throws IOException;
    }

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$WritableSettingsStorage */
    /* loaded from: classes2.dex */
    public interface WritableSettingsStorage extends ReadableSettingsStorage {
        void finishWrite(OutputStream outputStream, boolean z);

        OutputStream startWrite() throws IOException;
    }

    public DisplayWindowSettingsProvider() {
        this(new AtomicFileStorage(getVendorSettingsFile()), new AtomicFileStorage(getOverrideSettingsFile()));
    }

    @VisibleForTesting
    public DisplayWindowSettingsProvider(ReadableSettingsStorage readableSettingsStorage, WritableSettingsStorage writableSettingsStorage) {
        this.mBaseSettings = new ReadableSettings(readableSettingsStorage);
        this.mOverrideSettings = new WritableSettings(writableSettingsStorage);
    }

    public void setBaseSettingsFilePath(String str) {
        AtomicFile vendorSettingsFile;
        File file = str != null ? new File(str) : null;
        if (file != null && file.exists()) {
            vendorSettingsFile = new AtomicFile(file, "wm-displays");
        } else {
            Slog.w(StartingSurfaceController.TAG, "display settings " + str + " does not exist, using vendor defaults");
            vendorSettingsFile = getVendorSettingsFile();
        }
        setBaseSettingsStorage(new AtomicFileStorage(vendorSettingsFile));
    }

    @VisibleForTesting
    public void setBaseSettingsStorage(ReadableSettingsStorage readableSettingsStorage) {
        this.mBaseSettings = new ReadableSettings(readableSettingsStorage);
    }

    @Override // com.android.server.p014wm.DisplayWindowSettings.SettingsProvider
    public DisplayWindowSettings.SettingsProvider.SettingsEntry getSettings(DisplayInfo displayInfo) {
        DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry = this.mBaseSettings.getSettingsEntry(displayInfo);
        DisplayWindowSettings.SettingsProvider.SettingsEntry orCreateSettingsEntry = this.mOverrideSettings.getOrCreateSettingsEntry(displayInfo);
        if (settingsEntry == null) {
            return new DisplayWindowSettings.SettingsProvider.SettingsEntry(orCreateSettingsEntry);
        }
        DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry2 = new DisplayWindowSettings.SettingsProvider.SettingsEntry(settingsEntry);
        settingsEntry2.updateFrom(orCreateSettingsEntry);
        return settingsEntry2;
    }

    @Override // com.android.server.p014wm.DisplayWindowSettings.SettingsProvider
    public DisplayWindowSettings.SettingsProvider.SettingsEntry getOverrideSettings(DisplayInfo displayInfo) {
        return new DisplayWindowSettings.SettingsProvider.SettingsEntry(this.mOverrideSettings.getOrCreateSettingsEntry(displayInfo));
    }

    @Override // com.android.server.p014wm.DisplayWindowSettings.SettingsProvider
    public void updateOverrideSettings(DisplayInfo displayInfo, DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry) {
        this.mOverrideSettings.updateSettingsEntry(displayInfo, settingsEntry);
    }

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$ReadableSettings */
    /* loaded from: classes2.dex */
    public static class ReadableSettings {
        public int mIdentifierType;
        public final Map<String, DisplayWindowSettings.SettingsProvider.SettingsEntry> mSettings = new HashMap();

        public ReadableSettings(ReadableSettingsStorage readableSettingsStorage) {
            loadSettings(readableSettingsStorage);
        }

        public final DisplayWindowSettings.SettingsProvider.SettingsEntry getSettingsEntry(DisplayInfo displayInfo) {
            String identifier = getIdentifier(displayInfo);
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry = this.mSettings.get(identifier);
            if (settingsEntry != null) {
                return settingsEntry;
            }
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry2 = this.mSettings.get(displayInfo.name);
            if (settingsEntry2 != null) {
                this.mSettings.remove(displayInfo.name);
                this.mSettings.put(identifier, settingsEntry2);
                return settingsEntry2;
            }
            return null;
        }

        public final String getIdentifier(DisplayInfo displayInfo) {
            DisplayAddress displayAddress;
            if (this.mIdentifierType == 1 && (displayAddress = displayInfo.address) != null && (displayAddress instanceof DisplayAddress.Physical)) {
                return "port:" + displayInfo.address.getPort();
            }
            return displayInfo.uniqueId;
        }

        public final void loadSettings(ReadableSettingsStorage readableSettingsStorage) {
            FileData readSettings = DisplayWindowSettingsProvider.readSettings(readableSettingsStorage);
            if (readSettings != null) {
                this.mIdentifierType = readSettings.mIdentifierType;
                this.mSettings.putAll(readSettings.mSettings);
            }
        }
    }

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$WritableSettings */
    /* loaded from: classes2.dex */
    public static final class WritableSettings extends ReadableSettings {
        public final WritableSettingsStorage mSettingsStorage;

        public WritableSettings(WritableSettingsStorage writableSettingsStorage) {
            super(writableSettingsStorage);
            this.mSettingsStorage = writableSettingsStorage;
        }

        public DisplayWindowSettings.SettingsProvider.SettingsEntry getOrCreateSettingsEntry(DisplayInfo displayInfo) {
            String identifier = getIdentifier(displayInfo);
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry = this.mSettings.get(identifier);
            if (settingsEntry != null) {
                return settingsEntry;
            }
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry2 = this.mSettings.get(displayInfo.name);
            if (settingsEntry2 != null) {
                this.mSettings.remove(displayInfo.name);
                this.mSettings.put(identifier, settingsEntry2);
                writeSettings();
                return settingsEntry2;
            }
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry3 = new DisplayWindowSettings.SettingsProvider.SettingsEntry();
            this.mSettings.put(identifier, settingsEntry3);
            return settingsEntry3;
        }

        public void updateSettingsEntry(DisplayInfo displayInfo, DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry) {
            if (getOrCreateSettingsEntry(displayInfo).setTo(settingsEntry)) {
                writeSettings();
            }
        }

        public final void writeSettings() {
            FileData fileData = new FileData();
            fileData.mIdentifierType = this.mIdentifierType;
            fileData.mSettings.putAll(this.mSettings);
            DisplayWindowSettingsProvider.writeSettings(this.mSettingsStorage, fileData);
        }
    }

    public static AtomicFile getVendorSettingsFile() {
        File file = new File(Environment.getProductDirectory(), "etc/display_settings.xml");
        if (!file.exists()) {
            file = new File(Environment.getVendorDirectory(), "etc/display_settings.xml");
        }
        return new AtomicFile(file, "wm-displays");
    }

    public static AtomicFile getOverrideSettingsFile() {
        return new AtomicFile(new File(Environment.getDataDirectory(), "system/display_settings.xml"), "wm-displays");
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x0070, code lost:
        r9.close();
     */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00fe  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static FileData readSettings(ReadableSettingsStorage readableSettingsStorage) {
        boolean z;
        int next;
        try {
            InputStream openRead = readableSettingsStorage.openRead();
            FileData fileData = new FileData();
            try {
                try {
                    try {
                        try {
                            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                            while (true) {
                                next = resolvePullParser.next();
                                z = true;
                                if (next == 2 || next == 1) {
                                    break;
                                }
                            }
                            if (next != 2) {
                                throw new IllegalStateException("no start tag found");
                            }
                            int depth = resolvePullParser.getDepth();
                            while (true) {
                                int next2 = resolvePullParser.next();
                                if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                                    try {
                                        break;
                                    } catch (IOException unused) {
                                        if (!z) {
                                        }
                                        return fileData;
                                    }
                                } else if (next2 != 3 && next2 != 4) {
                                    String name = resolvePullParser.getName();
                                    if (name.equals("display")) {
                                        readDisplay(resolvePullParser, fileData);
                                    } else if (name.equals("config")) {
                                        readConfig(resolvePullParser, fileData);
                                    } else {
                                        Slog.w(StartingSurfaceController.TAG, "Unknown element under <display-settings>: " + resolvePullParser.getName());
                                        XmlUtils.skipCurrentTag(resolvePullParser);
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            try {
                                openRead.close();
                            } catch (IOException unused2) {
                            }
                            throw th;
                        }
                    } catch (IOException e) {
                        Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e);
                        try {
                            openRead.close();
                        } catch (IOException unused3) {
                            z = false;
                            if (!z) {
                                fileData.mSettings.clear();
                            }
                            return fileData;
                        }
                    } catch (XmlPullParserException e2) {
                        Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e2);
                        openRead.close();
                    }
                } catch (IllegalStateException e3) {
                    Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e3);
                    openRead.close();
                } catch (IndexOutOfBoundsException e4) {
                    Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e4);
                    openRead.close();
                }
            } catch (NullPointerException e5) {
                Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e5);
                openRead.close();
            } catch (NumberFormatException e6) {
                Slog.w(StartingSurfaceController.TAG, "Failed parsing " + e6);
                openRead.close();
            }
        } catch (IOException unused4) {
            Slog.i(StartingSurfaceController.TAG, "No existing display settings, starting empty");
            return null;
        }
    }

    public static int getIntAttribute(TypedXmlPullParser typedXmlPullParser, String str, int i) {
        return typedXmlPullParser.getAttributeInt((String) null, str, i);
    }

    public static Integer getIntegerAttribute(TypedXmlPullParser typedXmlPullParser, String str, Integer num) {
        try {
            return Integer.valueOf(typedXmlPullParser.getAttributeInt((String) null, str));
        } catch (Exception unused) {
            return num;
        }
    }

    public static Boolean getBooleanAttribute(TypedXmlPullParser typedXmlPullParser, String str, Boolean bool) {
        try {
            return Boolean.valueOf(typedXmlPullParser.getAttributeBoolean((String) null, str));
        } catch (Exception unused) {
            return bool;
        }
    }

    public static void readDisplay(TypedXmlPullParser typedXmlPullParser, FileData fileData) throws NumberFormatException, XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        if (attributeValue != null) {
            DisplayWindowSettings.SettingsProvider.SettingsEntry settingsEntry = new DisplayWindowSettings.SettingsProvider.SettingsEntry();
            settingsEntry.mWindowingMode = getIntAttribute(typedXmlPullParser, "windowingMode", 0);
            settingsEntry.mUserRotationMode = getIntegerAttribute(typedXmlPullParser, "userRotationMode", null);
            settingsEntry.mUserRotation = getIntegerAttribute(typedXmlPullParser, "userRotation", null);
            settingsEntry.mForcedWidth = getIntAttribute(typedXmlPullParser, "forcedWidth", 0);
            settingsEntry.mForcedHeight = getIntAttribute(typedXmlPullParser, "forcedHeight", 0);
            settingsEntry.mForcedDensity = getIntAttribute(typedXmlPullParser, "forcedDensity", 0);
            settingsEntry.mForcedScalingMode = getIntegerAttribute(typedXmlPullParser, "forcedScalingMode", null);
            settingsEntry.mRemoveContentMode = getIntAttribute(typedXmlPullParser, "removeContentMode", 0);
            settingsEntry.mShouldShowWithInsecureKeyguard = getBooleanAttribute(typedXmlPullParser, "shouldShowWithInsecureKeyguard", null);
            settingsEntry.mShouldShowSystemDecors = getBooleanAttribute(typedXmlPullParser, "shouldShowSystemDecors", null);
            Boolean booleanAttribute = getBooleanAttribute(typedXmlPullParser, "shouldShowIme", null);
            if (booleanAttribute != null) {
                settingsEntry.mImePolicy = Integer.valueOf(!booleanAttribute.booleanValue());
            } else {
                settingsEntry.mImePolicy = getIntegerAttribute(typedXmlPullParser, "imePolicy", null);
            }
            settingsEntry.mFixedToUserRotation = getIntegerAttribute(typedXmlPullParser, "fixedToUserRotation", null);
            settingsEntry.mIgnoreOrientationRequest = getBooleanAttribute(typedXmlPullParser, "ignoreOrientationRequest", null);
            settingsEntry.mIgnoreDisplayCutout = getBooleanAttribute(typedXmlPullParser, "ignoreDisplayCutout", null);
            settingsEntry.mDontMoveToTop = getBooleanAttribute(typedXmlPullParser, "dontMoveToTop", null);
            fileData.mSettings.put(attributeValue, settingsEntry);
        }
        XmlUtils.skipCurrentTag(typedXmlPullParser);
    }

    public static void readConfig(TypedXmlPullParser typedXmlPullParser, FileData fileData) throws NumberFormatException, XmlPullParserException, IOException {
        fileData.mIdentifierType = getIntAttribute(typedXmlPullParser, "identifier", 0);
        XmlUtils.skipCurrentTag(typedXmlPullParser);
    }

    public static void writeSettings(WritableSettingsStorage writableSettingsStorage, FileData fileData) {
        try {
            OutputStream startWrite = writableSettingsStorage.startWrite();
            try {
                try {
                    TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                    resolveSerializer.startDocument((String) null, Boolean.TRUE);
                    resolveSerializer.startTag((String) null, "display-settings");
                    resolveSerializer.startTag((String) null, "config");
                    resolveSerializer.attributeInt((String) null, "identifier", fileData.mIdentifierType);
                    resolveSerializer.endTag((String) null, "config");
                    for (Map.Entry<String, DisplayWindowSettings.SettingsProvider.SettingsEntry> entry : fileData.mSettings.entrySet()) {
                        String key = entry.getKey();
                        DisplayWindowSettings.SettingsProvider.SettingsEntry value = entry.getValue();
                        if (!value.isEmpty()) {
                            resolveSerializer.startTag((String) null, "display");
                            resolveSerializer.attribute((String) null, "name", key);
                            int i = value.mWindowingMode;
                            if (i != 0) {
                                resolveSerializer.attributeInt((String) null, "windowingMode", i);
                            }
                            Integer num = value.mUserRotationMode;
                            if (num != null) {
                                resolveSerializer.attributeInt((String) null, "userRotationMode", num.intValue());
                            }
                            Integer num2 = value.mUserRotation;
                            if (num2 != null) {
                                resolveSerializer.attributeInt((String) null, "userRotation", num2.intValue());
                            }
                            int i2 = value.mForcedWidth;
                            if (i2 != 0 && value.mForcedHeight != 0) {
                                resolveSerializer.attributeInt((String) null, "forcedWidth", i2);
                                resolveSerializer.attributeInt((String) null, "forcedHeight", value.mForcedHeight);
                            }
                            int i3 = value.mForcedDensity;
                            if (i3 != 0) {
                                resolveSerializer.attributeInt((String) null, "forcedDensity", i3);
                            }
                            Integer num3 = value.mForcedScalingMode;
                            if (num3 != null) {
                                resolveSerializer.attributeInt((String) null, "forcedScalingMode", num3.intValue());
                            }
                            int i4 = value.mRemoveContentMode;
                            if (i4 != 0) {
                                resolveSerializer.attributeInt((String) null, "removeContentMode", i4);
                            }
                            Boolean bool = value.mShouldShowWithInsecureKeyguard;
                            if (bool != null) {
                                resolveSerializer.attributeBoolean((String) null, "shouldShowWithInsecureKeyguard", bool.booleanValue());
                            }
                            Boolean bool2 = value.mShouldShowSystemDecors;
                            if (bool2 != null) {
                                resolveSerializer.attributeBoolean((String) null, "shouldShowSystemDecors", bool2.booleanValue());
                            }
                            Integer num4 = value.mImePolicy;
                            if (num4 != null) {
                                resolveSerializer.attributeInt((String) null, "imePolicy", num4.intValue());
                            }
                            Integer num5 = value.mFixedToUserRotation;
                            if (num5 != null) {
                                resolveSerializer.attributeInt((String) null, "fixedToUserRotation", num5.intValue());
                            }
                            Boolean bool3 = value.mIgnoreOrientationRequest;
                            if (bool3 != null) {
                                resolveSerializer.attributeBoolean((String) null, "ignoreOrientationRequest", bool3.booleanValue());
                            }
                            Boolean bool4 = value.mIgnoreDisplayCutout;
                            if (bool4 != null) {
                                resolveSerializer.attributeBoolean((String) null, "ignoreDisplayCutout", bool4.booleanValue());
                            }
                            Boolean bool5 = value.mDontMoveToTop;
                            if (bool5 != null) {
                                resolveSerializer.attributeBoolean((String) null, "dontMoveToTop", bool5.booleanValue());
                            }
                            resolveSerializer.endTag((String) null, "display");
                        }
                    }
                    resolveSerializer.endTag((String) null, "display-settings");
                    resolveSerializer.endDocument();
                    writableSettingsStorage.finishWrite(startWrite, true);
                } catch (IOException e) {
                    Slog.w(StartingSurfaceController.TAG, "Failed to write display window settings.", e);
                    writableSettingsStorage.finishWrite(startWrite, false);
                }
            } catch (Throwable th) {
                writableSettingsStorage.finishWrite(startWrite, false);
                throw th;
            }
        } catch (IOException e2) {
            Slog.w(StartingSurfaceController.TAG, "Failed to write display settings: " + e2);
        }
    }

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$FileData */
    /* loaded from: classes2.dex */
    public static final class FileData {
        public int mIdentifierType;
        public final Map<String, DisplayWindowSettings.SettingsProvider.SettingsEntry> mSettings;

        public FileData() {
            this.mSettings = new HashMap();
        }

        public String toString() {
            return "FileData{mIdentifierType=" + this.mIdentifierType + ", mSettings=" + this.mSettings + '}';
        }
    }

    /* renamed from: com.android.server.wm.DisplayWindowSettingsProvider$AtomicFileStorage */
    /* loaded from: classes2.dex */
    public static final class AtomicFileStorage implements WritableSettingsStorage {
        public final AtomicFile mAtomicFile;

        public AtomicFileStorage(AtomicFile atomicFile) {
            this.mAtomicFile = atomicFile;
        }

        @Override // com.android.server.p014wm.DisplayWindowSettingsProvider.ReadableSettingsStorage
        public InputStream openRead() throws FileNotFoundException {
            return this.mAtomicFile.openRead();
        }

        @Override // com.android.server.p014wm.DisplayWindowSettingsProvider.WritableSettingsStorage
        public OutputStream startWrite() throws IOException {
            return this.mAtomicFile.startWrite();
        }

        @Override // com.android.server.p014wm.DisplayWindowSettingsProvider.WritableSettingsStorage
        public void finishWrite(OutputStream outputStream, boolean z) {
            if (!(outputStream instanceof FileOutputStream)) {
                throw new IllegalArgumentException("Unexpected OutputStream as argument: " + outputStream);
            }
            FileOutputStream fileOutputStream = (FileOutputStream) outputStream;
            if (z) {
                this.mAtomicFile.finishWrite(fileOutputStream);
            } else {
                this.mAtomicFile.failWrite(fileOutputStream);
            }
        }
    }
}
