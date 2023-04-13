package com.android.server.app;

import android.os.FileUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.app.GameManagerService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class GameManagerSettings {
    @VisibleForTesting
    final AtomicFile mSettingsFile;
    public final File mSystemDir;
    public final ArrayMap<String, Integer> mGameModes = new ArrayMap<>();
    public final ArrayMap<String, GameManagerService.GamePackageConfiguration> mConfigOverrides = new ArrayMap<>();

    public GameManagerSettings(File file) {
        File file2 = new File(file, "system");
        this.mSystemDir = file2;
        file2.mkdirs();
        FileUtils.setPermissions(file2.toString(), 509, -1, -1);
        this.mSettingsFile = new AtomicFile(new File(file2, "game-manager-service.xml"));
    }

    public int getGameModeLocked(String str) {
        int intValue;
        if (!this.mGameModes.containsKey(str) || (intValue = this.mGameModes.get(str).intValue()) == 0) {
            return 1;
        }
        return intValue;
    }

    public void setGameModeLocked(String str, int i) {
        this.mGameModes.put(str, Integer.valueOf(i));
    }

    public void removeGame(String str) {
        this.mGameModes.remove(str);
        this.mConfigOverrides.remove(str);
    }

    public GameManagerService.GamePackageConfiguration getConfigOverride(String str) {
        return this.mConfigOverrides.get(str);
    }

    public void setConfigOverride(String str, GameManagerService.GamePackageConfiguration gamePackageConfiguration) {
        this.mConfigOverrides.put(str, gamePackageConfiguration);
    }

    public void removeConfigOverride(String str) {
        this.mConfigOverrides.remove(str);
    }

    public void writePersistentDataLocked() {
        FileOutputStream startWrite;
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = this.mSettingsFile.startWrite();
        } catch (IOException e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "packages");
            ArraySet arraySet = new ArraySet(this.mGameModes.keySet());
            arraySet.addAll(this.mConfigOverrides.keySet());
            Iterator it = arraySet.iterator();
            while (it.hasNext()) {
                String str = (String) it.next();
                resolveSerializer.startTag((String) null, "package");
                resolveSerializer.attribute((String) null, "name", str);
                if (this.mGameModes.containsKey(str)) {
                    resolveSerializer.attributeInt((String) null, "gameMode", this.mGameModes.get(str).intValue());
                }
                writeGameModeConfigTags(resolveSerializer, this.mConfigOverrides.get(str));
                resolveSerializer.endTag((String) null, "package");
            }
            resolveSerializer.endTag((String) null, "packages");
            resolveSerializer.endDocument();
            this.mSettingsFile.finishWrite(startWrite);
            FileUtils.setPermissions(this.mSettingsFile.toString(), FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED, -1, -1);
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = startWrite;
            this.mSettingsFile.failWrite(fileOutputStream);
            Slog.wtf("GameManagerService_GameManagerSettings", "Unable to write game manager service settings, current changes will be lost at reboot", e);
        }
    }

    public final void writeGameModeConfigTags(TypedXmlSerializer typedXmlSerializer, GameManagerService.GamePackageConfiguration gamePackageConfiguration) throws IOException {
        int[] availableGameModes;
        if (gamePackageConfiguration == null) {
            return;
        }
        for (int i : gamePackageConfiguration.getAvailableGameModes()) {
            GameManagerService.GamePackageConfiguration.GameModeConfiguration gameModeConfiguration = gamePackageConfiguration.getGameModeConfiguration(i);
            if (gameModeConfiguration != null) {
                typedXmlSerializer.startTag((String) null, "gameModeConfig");
                typedXmlSerializer.attributeInt((String) null, "gameMode", i);
                typedXmlSerializer.attributeBoolean((String) null, "useAngle", gameModeConfiguration.getUseAngle());
                typedXmlSerializer.attribute((String) null, "fps", gameModeConfiguration.getFpsStr());
                typedXmlSerializer.attributeFloat((String) null, "scaling", gameModeConfiguration.getScaling());
                typedXmlSerializer.attributeInt((String) null, "loadingBoost", gameModeConfiguration.getLoadingBoostDuration());
                typedXmlSerializer.endTag((String) null, "gameModeConfig");
            }
        }
    }

    public boolean readPersistentDataLocked() {
        int next;
        this.mGameModes.clear();
        if (!this.mSettingsFile.exists()) {
            Slog.v("GameManagerService_GameManagerSettings", "Settings file doesn't exist, skip reading");
            return false;
        }
        try {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(this.mSettingsFile.openRead());
            while (true) {
                next = resolvePullParser.next();
                if (next == 2 || next == 1) {
                    break;
                }
            }
            if (next != 2) {
                Slog.wtf("GameManagerService_GameManagerSettings", "No start tag found in game manager settings");
                return false;
            }
            int depth = resolvePullParser.getDepth();
            while (true) {
                int next2 = resolvePullParser.next();
                if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                    break;
                } else if (next2 != 3 && next2 != 4) {
                    String name = resolvePullParser.getName();
                    if (next2 == 2 && "package".equals(name)) {
                        readPackage(resolvePullParser);
                    } else {
                        XmlUtils.skipCurrentTag(resolvePullParser);
                        Slog.w("GameManagerService_GameManagerSettings", "Unknown element under packages tag: " + name + " with type: " + next2);
                    }
                }
            }
            return true;
        } catch (IOException | XmlPullParserException e) {
            Slog.wtf("GameManagerService_GameManagerSettings", "Error reading game manager settings", e);
            return false;
        }
    }

    public final void readPackage(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        if (attributeValue == null) {
            Slog.wtf("GameManagerService_GameManagerSettings", "No package name found in package tag");
            XmlUtils.skipCurrentTag(typedXmlPullParser);
            return;
        }
        try {
            this.mGameModes.put(attributeValue, Integer.valueOf(typedXmlPullParser.getAttributeInt((String) null, "gameMode")));
        } catch (XmlPullParserException unused) {
            Slog.v("GameManagerService_GameManagerSettings", "No game mode selected by user for package" + attributeValue);
        }
        int depth = typedXmlPullParser.getDepth();
        GameManagerService.GamePackageConfiguration gamePackageConfiguration = new GameManagerService.GamePackageConfiguration(attributeValue);
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (next == 2 && "gameModeConfig".equals(name)) {
                    readGameModeConfig(typedXmlPullParser, gamePackageConfiguration);
                } else {
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                    Slog.w("GameManagerService_GameManagerSettings", "Unknown element under package tag: " + name + " with type: " + next);
                }
            }
        }
        if (gamePackageConfiguration.hasActiveGameModeConfig()) {
            this.mConfigOverrides.put(attributeValue, gamePackageConfiguration);
        }
    }

    public final void readGameModeConfig(TypedXmlPullParser typedXmlPullParser, GameManagerService.GamePackageConfiguration gamePackageConfiguration) {
        try {
            GameManagerService.GamePackageConfiguration.GameModeConfiguration orAddDefaultGameModeConfiguration = gamePackageConfiguration.getOrAddDefaultGameModeConfiguration(typedXmlPullParser.getAttributeInt((String) null, "gameMode"));
            try {
                orAddDefaultGameModeConfiguration.setScaling(typedXmlPullParser.getAttributeFloat((String) null, "scaling"));
            } catch (XmlPullParserException e) {
                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "scaling");
                if (attributeValue != null) {
                    Slog.wtf("GameManagerService_GameManagerSettings", "Invalid scaling value in config tag: " + attributeValue, e);
                }
            }
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "fps");
            if (attributeValue2 == null) {
                attributeValue2 = "";
            }
            orAddDefaultGameModeConfiguration.setFpsStr(attributeValue2);
            try {
                orAddDefaultGameModeConfiguration.setUseAngle(typedXmlPullParser.getAttributeBoolean((String) null, "useAngle"));
            } catch (XmlPullParserException e2) {
                String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "useAngle");
                if (attributeValue3 != null) {
                    Slog.wtf("GameManagerService_GameManagerSettings", "Invalid useAngle value in config tag: " + attributeValue3, e2);
                }
            }
            try {
                orAddDefaultGameModeConfiguration.setLoadingBoostDuration(typedXmlPullParser.getAttributeInt((String) null, "loadingBoost"));
            } catch (XmlPullParserException e3) {
                String attributeValue4 = typedXmlPullParser.getAttributeValue((String) null, "loadingBoost");
                if (attributeValue4 != null) {
                    Slog.wtf("GameManagerService_GameManagerSettings", "Invalid loading boost in config tag: " + attributeValue4, e3);
                }
            }
        } catch (XmlPullParserException e4) {
            Slog.wtf("GameManagerService_GameManagerSettings", "Invalid game mode value in config tag: " + typedXmlPullParser.getAttributeValue((String) null, "gameMode"), e4);
        }
    }
}
