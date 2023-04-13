package com.android.server.net.watchlist;

import android.os.FileUtils;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class WatchlistConfig {
    public static final WatchlistConfig sInstance = new WatchlistConfig();
    public volatile CrcShaDigests mDomainDigests;
    public volatile CrcShaDigests mIpDigests;
    public boolean mIsSecureConfig;
    public File mXmlFile;

    /* loaded from: classes2.dex */
    public static class CrcShaDigests {
        public final HarmfulCrcs crc32s;
        public final HarmfulDigests sha256Digests;

        public CrcShaDigests(HarmfulCrcs harmfulCrcs, HarmfulDigests harmfulDigests) {
            this.crc32s = harmfulCrcs;
            this.sha256Digests = harmfulDigests;
        }
    }

    public static WatchlistConfig getInstance() {
        return sInstance;
    }

    public WatchlistConfig() {
        this(new File("/data/misc/network_watchlist/network_watchlist.xml"));
    }

    @VisibleForTesting
    public WatchlistConfig(File file) {
        this.mIsSecureConfig = true;
        this.mXmlFile = file;
        reloadConfig();
    }

    public void reloadConfig() {
        char c;
        if (this.mXmlFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.mXmlFile);
                List<byte[]> arrayList = new ArrayList<>();
                List<byte[]> arrayList2 = new ArrayList<>();
                List<byte[]> arrayList3 = new ArrayList<>();
                List<byte[]> arrayList4 = new ArrayList<>();
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(fileInputStream, StandardCharsets.UTF_8.name());
                newPullParser.nextTag();
                newPullParser.require(2, null, "watchlist-config");
                while (newPullParser.nextTag() == 2) {
                    String name = newPullParser.getName();
                    switch (name.hashCode()) {
                        case -1862636386:
                            if (name.equals("crc32-domain")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -14835926:
                            if (name.equals("sha256-domain")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 835385997:
                            if (name.equals("sha256-ip")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1718657537:
                            if (name.equals("crc32-ip")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    if (c == 0) {
                        parseHashes(newPullParser, name, arrayList);
                    } else if (c == 1) {
                        parseHashes(newPullParser, name, arrayList3);
                    } else if (c == 2) {
                        parseHashes(newPullParser, name, arrayList2);
                    } else if (c == 3) {
                        parseHashes(newPullParser, name, arrayList4);
                    } else {
                        Log.w("WatchlistConfig", "Unknown element: " + newPullParser.getName());
                        XmlUtils.skipCurrentTag(newPullParser);
                    }
                }
                newPullParser.require(3, null, "watchlist-config");
                this.mDomainDigests = new CrcShaDigests(new HarmfulCrcs(arrayList), new HarmfulDigests(arrayList2));
                this.mIpDigests = new CrcShaDigests(new HarmfulCrcs(arrayList3), new HarmfulDigests(arrayList4));
                Log.i("WatchlistConfig", "Reload watchlist done");
                fileInputStream.close();
            } catch (IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException e) {
                Slog.e("WatchlistConfig", "Failed parsing xml", e);
            }
        }
    }

    public final void parseHashes(XmlPullParser xmlPullParser, String str, List<byte[]> list) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, str);
        while (xmlPullParser.nextTag() == 2) {
            xmlPullParser.require(2, null, "hash");
            byte[] hexStringToByteArray = HexDump.hexStringToByteArray(xmlPullParser.nextText());
            xmlPullParser.require(3, null, "hash");
            list.add(hexStringToByteArray);
        }
        xmlPullParser.require(3, null, str);
    }

    public boolean containsDomain(String str) {
        CrcShaDigests crcShaDigests = this.mDomainDigests;
        if (crcShaDigests == null) {
            return false;
        }
        if (crcShaDigests.crc32s.contains(getCrc32(str))) {
            return crcShaDigests.sha256Digests.contains(getSha256(str));
        }
        return false;
    }

    public boolean containsIp(String str) {
        CrcShaDigests crcShaDigests = this.mIpDigests;
        if (crcShaDigests == null) {
            return false;
        }
        if (crcShaDigests.crc32s.contains(getCrc32(str))) {
            return crcShaDigests.sha256Digests.contains(getSha256(str));
        }
        return false;
    }

    public final int getCrc32(String str) {
        CRC32 crc32 = new CRC32();
        crc32.update(str.getBytes());
        return (int) crc32.getValue();
    }

    public final byte[] getSha256(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(str.getBytes());
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException unused) {
            return null;
        }
    }

    public boolean isConfigSecure() {
        return this.mIsSecureConfig;
    }

    public byte[] getWatchlistConfigHash() {
        if (this.mXmlFile.exists()) {
            try {
                return DigestUtils.getSha256Hash(this.mXmlFile);
            } catch (IOException | NoSuchAlgorithmException e) {
                Log.e("WatchlistConfig", "Unable to get watchlist config hash", e);
                return null;
            }
        }
        return null;
    }

    public void setTestMode(InputStream inputStream) throws IOException {
        Log.i("WatchlistConfig", "Setting watchlist testing config");
        FileUtils.copyToFileOrThrow(inputStream, new File("/data/misc/network_watchlist/network_watchlist_for_test.xml"));
        this.mIsSecureConfig = false;
        this.mXmlFile = new File("/data/misc/network_watchlist/network_watchlist_for_test.xml");
        reloadConfig();
    }

    public void removeTestModeConfig() {
        try {
            File file = new File("/data/misc/network_watchlist/network_watchlist_for_test.xml");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception unused) {
            Log.e("WatchlistConfig", "Unable to delete test config");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        byte[] watchlistConfigHash = getWatchlistConfigHash();
        StringBuilder sb = new StringBuilder();
        sb.append("Watchlist config hash: ");
        sb.append(watchlistConfigHash != null ? HexDump.toHexString(watchlistConfigHash) : null);
        printWriter.println(sb.toString());
        printWriter.println("Domain CRC32 digest list:");
        if (this.mDomainDigests != null) {
            this.mDomainDigests.crc32s.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("Domain SHA256 digest list:");
        if (this.mDomainDigests != null) {
            this.mDomainDigests.sha256Digests.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("Ip CRC32 digest list:");
        if (this.mIpDigests != null) {
            this.mIpDigests.crc32s.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("Ip SHA256 digest list:");
        if (this.mIpDigests != null) {
            this.mIpDigests.sha256Digests.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
