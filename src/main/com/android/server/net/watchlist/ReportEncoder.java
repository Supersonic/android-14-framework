package com.android.server.net.watchlist;

import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import com.android.server.net.watchlist.WatchlistReportDbHelper;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class ReportEncoder {
    public static byte[] encodeWatchlistReport(WatchlistConfig watchlistConfig, byte[] bArr, List<String> list, WatchlistReportDbHelper.AggregatedResult aggregatedResult) {
        return serializeReport(watchlistConfig, PrivacyUtils.createDpEncodedReportMap(watchlistConfig.isConfigSecure(), bArr, list, aggregatedResult));
    }

    @VisibleForTesting
    public static byte[] serializeReport(WatchlistConfig watchlistConfig, Map<String, Boolean> map) {
        byte[] watchlistConfigHash = watchlistConfig.getWatchlistConfigHash();
        if (watchlistConfigHash == null) {
            Log.e("ReportEncoder", "No watchlist hash");
            return null;
        } else if (watchlistConfigHash.length != 32) {
            Log.e("ReportEncoder", "Unexpected hash length");
            return null;
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ProtoOutputStream protoOutputStream = new ProtoOutputStream(byteArrayOutputStream);
            protoOutputStream.write(1120986464257L, 1);
            protoOutputStream.write(1138166333442L, HexDump.toHexString(watchlistConfigHash));
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                String key = entry.getKey();
                HexDump.hexStringToByteArray(key);
                boolean booleanValue = entry.getValue().booleanValue();
                long start = protoOutputStream.start(2246267895811L);
                protoOutputStream.write(1138166333441L, key);
                protoOutputStream.write(1133871366146L, booleanValue);
                protoOutputStream.end(start);
            }
            protoOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
