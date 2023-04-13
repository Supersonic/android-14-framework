package com.android.server.net.watchlist;

import android.os.Environment;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class WatchlistSettings {
    public static final WatchlistSettings sInstance = new WatchlistSettings();
    public byte[] mPrivacySecretKey;
    public final AtomicFile mXmlFile;

    public static WatchlistSettings getInstance() {
        return sInstance;
    }

    public WatchlistSettings() {
        this(getSystemWatchlistFile());
    }

    public static File getSystemWatchlistFile() {
        return new File(Environment.getDataSystemDirectory(), "watchlist_settings.xml");
    }

    @VisibleForTesting
    public WatchlistSettings(File file) {
        this.mPrivacySecretKey = null;
        this.mXmlFile = new AtomicFile(file, "net-watchlist");
        reloadSettings();
        if (this.mPrivacySecretKey == null) {
            this.mPrivacySecretKey = generatePrivacySecretKey();
            saveSettings();
        }
    }

    public final void reloadSettings() {
        if (this.mXmlFile.exists()) {
            try {
                FileInputStream openRead = this.mXmlFile.openRead();
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                XmlUtils.beginDocument(resolvePullParser, "network-watchlist-settings");
                int depth = resolvePullParser.getDepth();
                while (XmlUtils.nextElementWithin(resolvePullParser, depth)) {
                    if (resolvePullParser.getName().equals("secret-key")) {
                        this.mPrivacySecretKey = parseSecretKey(resolvePullParser);
                    }
                }
                Slog.i("WatchlistSettings", "Reload watchlist settings done");
                if (openRead != null) {
                    openRead.close();
                }
            } catch (IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException e) {
                Slog.e("WatchlistSettings", "Failed parsing xml", e);
            }
        }
    }

    public final byte[] parseSecretKey(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "secret-key");
        byte[] hexStringToByteArray = HexDump.hexStringToByteArray(xmlPullParser.nextText());
        xmlPullParser.require(3, null, "secret-key");
        if (hexStringToByteArray == null || hexStringToByteArray.length != 48) {
            Log.e("WatchlistSettings", "Unable to parse secret key");
            return null;
        }
        return hexStringToByteArray;
    }

    public synchronized byte[] getPrivacySecretKey() {
        byte[] bArr;
        bArr = new byte[48];
        System.arraycopy(this.mPrivacySecretKey, 0, bArr, 0, 48);
        return bArr;
    }

    public final byte[] generatePrivacySecretKey() {
        byte[] bArr = new byte[48];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    public final void saveSettings() {
        try {
            FileOutputStream startWrite = this.mXmlFile.startWrite();
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "network-watchlist-settings");
                resolveSerializer.startTag((String) null, "secret-key");
                resolveSerializer.text(HexDump.toHexString(this.mPrivacySecretKey));
                resolveSerializer.endTag((String) null, "secret-key");
                resolveSerializer.endTag((String) null, "network-watchlist-settings");
                resolveSerializer.endDocument();
                this.mXmlFile.finishWrite(startWrite);
            } catch (IOException e) {
                Log.w("WatchlistSettings", "Failed to write display settings, restoring backup.", e);
                this.mXmlFile.failWrite(startWrite);
            }
        } catch (IOException e2) {
            Log.w("WatchlistSettings", "Failed to write display settings: " + e2);
        }
    }
}
