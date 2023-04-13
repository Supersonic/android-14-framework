package android.net.netstats;

import android.annotation.SystemApi;
import android.content.Context;
import android.media.MediaMetrics;
import android.net.NetworkIdentity;
import android.net.NetworkStatsCollection;
import android.net.NetworkStatsHistory;
import android.p008os.Environment;
import android.util.AtomicFile;
import com.android.internal.util.ArtFastDataInput;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import libcore.io.IoUtils;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public class NetworkStatsDataMigrationUtils {
    private static final int BUFFER_SIZE = 8192;
    private static final int FILE_MAGIC = 1095648596;
    public static final String PREFIX_UID = "uid";
    public static final String PREFIX_XT = "xt";
    public static final String PREFIX_UID_TAG = "uid_tag";
    private static final Map<String, String> sPrefixLegacyFileNameMap = Map.of(PREFIX_XT, "netstats_xt.bin", "uid", "netstats_uid.bin", PREFIX_UID_TAG, "netstats_uid.bin");

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Prefix {
    }

    /* loaded from: classes2.dex */
    private static class CollectionVersion {
        static final int VERSION_NETWORK_INIT = 1;
        static final int VERSION_UID_INIT = 1;
        static final int VERSION_UID_WITH_IDENT = 2;
        static final int VERSION_UID_WITH_SET = 4;
        static final int VERSION_UID_WITH_TAG = 3;
        static final int VERSION_UNIFIED_INIT = 16;

        private CollectionVersion() {
        }
    }

    /* loaded from: classes2.dex */
    private static class HistoryVersion {
        static final int VERSION_ADD_ACTIVE = 3;
        static final int VERSION_ADD_PACKETS = 2;
        static final int VERSION_INIT = 1;

        private HistoryVersion() {
        }
    }

    /* loaded from: classes2.dex */
    private static class IdentitySetVersion {
        static final int VERSION_ADD_DEFAULT_NETWORK = 5;
        static final int VERSION_ADD_METERED = 4;
        static final int VERSION_ADD_NETWORK_ID = 3;
        static final int VERSION_ADD_OEM_MANAGED_NETWORK = 6;
        static final int VERSION_ADD_ROAMING = 2;
        static final int VERSION_ADD_SUB_ID = 7;
        static final int VERSION_INIT = 1;

        private IdentitySetVersion() {
        }
    }

    private NetworkStatsDataMigrationUtils() {
    }

    private static File getPlatformSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    private static File getPlatformBaseDir() {
        File baseDir = new File(getPlatformSystemDir(), Context.NETWORK_STATS_SERVICE);
        baseDir.mkdirs();
        return baseDir;
    }

    private static File getLegacyBinFileForPrefix(String prefix) {
        return new File(getPlatformSystemDir(), sPrefixLegacyFileNameMap.get(prefix));
    }

    private static ArrayList<File> getPlatformFileListForPrefix(String prefix) {
        ArrayList<File> list = new ArrayList<>();
        File platformFiles = getPlatformBaseDir();
        if (platformFiles.exists()) {
            String[] files = platformFiles.list();
            if (files == null) {
                return list;
            }
            Arrays.sort(files);
            for (String name : files) {
                if (name.startsWith(prefix + MediaMetrics.SEPARATOR)) {
                    list.add(new File(platformFiles, name));
                }
            }
        }
        return list;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static NetworkStatsCollection readPlatformCollection(String prefix, long bucketDuration) throws IOException {
        char c;
        NetworkStatsCollection.Builder builder = new NetworkStatsCollection.Builder(bucketDuration);
        switch (prefix.hashCode()) {
            case -434894037:
                if (prefix.equals(PREFIX_UID_TAG)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 115792:
                if (prefix.equals("uid")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                File uidFile = getLegacyBinFileForPrefix(prefix);
                if (uidFile.exists()) {
                    readLegacyUid(builder, uidFile, PREFIX_UID_TAG.equals(prefix));
                    break;
                }
                break;
        }
        ArrayList<File> platformFiles = getPlatformFileListForPrefix(prefix);
        Iterator<File> it = platformFiles.iterator();
        while (it.hasNext()) {
            File platformFile = it.next();
            if (platformFile.exists()) {
                readPlatformCollection(builder, platformFile);
            }
        }
        return builder.build();
    }

    private static void readPlatformCollection(NetworkStatsCollection.Builder builder, File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        ArtFastDataInput dataIn = new ArtFastDataInput(is, 8192);
        try {
            readPlatformCollection(builder, dataIn);
        } finally {
            IoUtils.closeQuietly(dataIn);
        }
    }

    public static void readPlatformCollection(NetworkStatsCollection.Builder builder, DataInput in) throws IOException {
        int magic = in.readInt();
        if (magic != FILE_MAGIC) {
            throw new ProtocolException("unexpected magic: " + magic);
        }
        int version = in.readInt();
        switch (version) {
            case 16:
                int identSize = in.readInt();
                for (int i = 0; i < identSize; i++) {
                    Set<NetworkIdentity> ident = readPlatformNetworkIdentitySet(in);
                    int size = in.readInt();
                    for (int j = 0; j < size; j++) {
                        int uid = in.readInt();
                        int set = in.readInt();
                        int tag = in.readInt();
                        NetworkStatsCollection.Key key = new NetworkStatsCollection.Key(ident, uid, set, tag);
                        NetworkStatsHistory history = readPlatformHistory(in);
                        builder.addEntry(key, history);
                    }
                }
                return;
            default:
                throw new ProtocolException("unexpected version: " + version);
        }
    }

    private static long[] readFullLongArray(DataInput in) throws IOException {
        int size = in.readInt();
        if (size < 0) {
            throw new ProtocolException("negative array size");
        }
        long[] values = new long[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = in.readLong();
        }
        return values;
    }

    private static long[] readVarLongArray(DataInput in) throws IOException {
        int size = in.readInt();
        if (size == -1) {
            return null;
        }
        if (size < 0) {
            throw new ProtocolException("negative array size");
        }
        long[] values = new long[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = readVarLong(in);
        }
        return values;
    }

    private static long readVarLong(DataInput in) throws IOException {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            byte b = in.readByte();
            result |= (b & Byte.MAX_VALUE) << shift;
            if ((b & 128) == 0) {
                return result;
            }
        }
        throw new ProtocolException("malformed var long");
    }

    private static String readOptionalString(DataInput in) throws IOException {
        if (in.readByte() != 0) {
            return in.readUTF();
        }
        return null;
    }

    private static NetworkStatsHistory readPlatformHistory(DataInput in) throws IOException {
        long bucketDuration;
        long[] bucketStart;
        long[] rxBytes;
        long[] rxPackets;
        long[] txBytes;
        long[] txPackets;
        long[] operations;
        int bucketCount;
        long[] jArr;
        long[] activeTime = new long[0];
        int version = in.readInt();
        switch (version) {
            case 1:
                bucketDuration = in.readLong();
                bucketStart = readFullLongArray(in);
                rxBytes = readFullLongArray(in);
                rxPackets = new long[bucketStart.length];
                txBytes = readFullLongArray(in);
                txPackets = new long[bucketStart.length];
                operations = new long[bucketStart.length];
                bucketCount = bucketStart.length;
                break;
            case 2:
            case 3:
                bucketDuration = in.readLong();
                bucketStart = readVarLongArray(in);
                if (version >= 3) {
                    jArr = readVarLongArray(in);
                } else {
                    jArr = new long[bucketStart.length];
                }
                activeTime = jArr;
                rxBytes = readVarLongArray(in);
                rxPackets = readVarLongArray(in);
                txBytes = readVarLongArray(in);
                txPackets = readVarLongArray(in);
                operations = readVarLongArray(in);
                bucketCount = bucketStart.length;
                break;
            default:
                throw new ProtocolException("unexpected version: " + version);
        }
        NetworkStatsHistory.Builder historyBuilder = new NetworkStatsHistory.Builder(bucketDuration, bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            NetworkStatsHistory.Entry entry = new NetworkStatsHistory.Entry(bucketStart[i], activeTime[i], rxBytes[i], rxPackets[i], txBytes[i], txPackets[i], operations[i]);
            historyBuilder.addEntry(entry);
        }
        return historyBuilder.build();
    }

    private static Set<NetworkIdentity> readPlatformNetworkIdentitySet(DataInput in) throws IOException {
        String networkId;
        boolean roaming;
        boolean defaultNetwork;
        int oemNetCapabilities;
        int subId;
        int version = in.readInt();
        int size = in.readInt();
        Set<NetworkIdentity> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            boolean metered = true;
            if (version <= 1) {
                in.readInt();
            }
            int type = in.readInt();
            int ratType = in.readInt();
            String subscriberId = readOptionalString(in);
            if (version >= 3) {
                networkId = readOptionalString(in);
            } else {
                networkId = null;
            }
            if (version >= 2) {
                roaming = in.readBoolean();
            } else {
                roaming = false;
            }
            if (version >= 4) {
                metered = in.readBoolean();
            } else if (type != 0) {
                metered = false;
            }
            if (version >= 5) {
                defaultNetwork = in.readBoolean();
            } else {
                defaultNetwork = true;
            }
            if (version >= 6) {
                oemNetCapabilities = in.readInt();
            } else {
                oemNetCapabilities = 0;
            }
            if (version >= 7) {
                subId = in.readInt();
            } else {
                subId = -1;
            }
            int collapsedLegacyType = getCollapsedLegacyType(type);
            NetworkIdentity.Builder builder = new NetworkIdentity.Builder().setType(collapsedLegacyType).setSubscriberId(subscriberId).setWifiNetworkKey(networkId).setRoaming(roaming).setMetered(metered).setDefaultNetwork(defaultNetwork).setOemManaged(oemNetCapabilities).setSubId(subId);
            if (type == 0 && ratType != -1) {
                builder.setRatType(ratType);
            }
            set.add(builder.build());
        }
        return set;
    }

    private static int getCollapsedLegacyType(int networkType) {
        switch (networkType) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
                return 0;
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            default:
                return networkType;
        }
    }

    private static void readLegacyUid(NetworkStatsCollection.Builder builder, File uidFile, boolean onlyTaggedData) throws IOException {
        AtomicFile inputFile = new AtomicFile(uidFile);
        DataInputStream in = new DataInputStream(new BufferedInputStream(inputFile.openRead()));
        try {
            readLegacyUid(builder, in, onlyTaggedData);
        } finally {
            IoUtils.closeQuietly(in);
        }
    }

    public static void readLegacyUid(NetworkStatsCollection.Builder builder, DataInput in, boolean taggedData) throws IOException {
        int set;
        try {
            int magic = in.readInt();
            if (magic != FILE_MAGIC) {
                throw new ProtocolException("unexpected magic: " + magic);
            }
            int version = in.readInt();
            switch (version) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                case 4:
                    int identSize = in.readInt();
                    for (int i = 0; i < identSize; i++) {
                        Set<NetworkIdentity> ident = readPlatformNetworkIdentitySet(in);
                        int size = in.readInt();
                        for (int j = 0; j < size; j++) {
                            int uid = in.readInt();
                            if (version >= 4) {
                                set = in.readInt();
                            } else {
                                set = 0;
                            }
                            int tag = in.readInt();
                            NetworkStatsCollection.Key key = new NetworkStatsCollection.Key(ident, uid, set, tag);
                            NetworkStatsHistory history = readPlatformHistory(in);
                            if ((tag == 0) != taggedData) {
                                builder.addEntry(key, history);
                            }
                        }
                    }
                    break;
                default:
                    throw new ProtocolException("unknown version: " + version);
            }
        } catch (FileNotFoundException | ProtocolException e) {
        }
    }
}
