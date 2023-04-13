package com.android.server.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.server.notification.NotificationManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes2.dex */
public class RankingHelper {
    public final Context mContext;
    public final NotificationComparator mPreliminaryComparator;
    public final RankingHandler mRankingHandler;
    public final NotificationSignalExtractor[] mSignalExtractors;
    public final GlobalSortKeyComparator mFinalComparator = new GlobalSortKeyComparator();
    public final ArrayMap<String, NotificationRecord> mProxyByGroupTmp = new ArrayMap<>();

    public RankingHelper(Context context, RankingHandler rankingHandler, RankingConfig rankingConfig, ZenModeHelper zenModeHelper, NotificationUsageStats notificationUsageStats, String[] strArr) {
        this.mContext = context;
        this.mRankingHandler = rankingHandler;
        this.mPreliminaryComparator = new NotificationComparator(context);
        int length = strArr.length;
        this.mSignalExtractors = new NotificationSignalExtractor[length];
        for (int i = 0; i < length; i++) {
            try {
                NotificationSignalExtractor notificationSignalExtractor = (NotificationSignalExtractor) this.mContext.getClassLoader().loadClass(strArr[i]).newInstance();
                notificationSignalExtractor.initialize(this.mContext, notificationUsageStats);
                notificationSignalExtractor.setConfig(rankingConfig);
                notificationSignalExtractor.setZenHelper(zenModeHelper);
                this.mSignalExtractors[i] = notificationSignalExtractor;
            } catch (ClassNotFoundException e) {
                Slog.w("RankingHelper", "Couldn't find extractor " + strArr[i] + ".", e);
            } catch (IllegalAccessException e2) {
                Slog.w("RankingHelper", "Problem accessing extractor " + strArr[i] + ".", e2);
            } catch (InstantiationException e3) {
                Slog.w("RankingHelper", "Couldn't instantiate extractor " + strArr[i] + ".", e3);
            }
        }
    }

    public <T extends NotificationSignalExtractor> T findExtractor(Class<T> cls) {
        int length = this.mSignalExtractors.length;
        for (int i = 0; i < length; i++) {
            T t = (T) this.mSignalExtractors[i];
            if (cls.equals(t.getClass())) {
                return t;
            }
        }
        return null;
    }

    public void extractSignals(NotificationRecord notificationRecord) {
        int length = this.mSignalExtractors.length;
        for (int i = 0; i < length; i++) {
            try {
                RankingReconsideration process = this.mSignalExtractors[i].process(notificationRecord);
                if (process != null) {
                    this.mRankingHandler.requestReconsideration(process);
                }
            } catch (Throwable th) {
                Slog.w("RankingHelper", "NotificationSignalExtractor failed.", th);
            }
        }
    }

    public void sort(ArrayList<NotificationRecord> arrayList) {
        String str;
        int size = arrayList.size();
        for (int i = size - 1; i >= 0; i--) {
            arrayList.get(i).setGlobalSortKey(null);
        }
        Collections.sort(arrayList, this.mPreliminaryComparator);
        synchronized (this.mProxyByGroupTmp) {
            for (int i2 = 0; i2 < size; i2++) {
                try {
                    NotificationRecord notificationRecord = arrayList.get(i2);
                    notificationRecord.setAuthoritativeRank(i2);
                    String groupKey = notificationRecord.getGroupKey();
                    if (this.mProxyByGroupTmp.get(groupKey) == null) {
                        this.mProxyByGroupTmp.put(groupKey, notificationRecord);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            for (int i3 = 0; i3 < size; i3++) {
                NotificationRecord notificationRecord2 = arrayList.get(i3);
                NotificationRecord notificationRecord3 = this.mProxyByGroupTmp.get(notificationRecord2.getGroupKey());
                String sortKey = notificationRecord2.getNotification().getSortKey();
                if (sortKey == null) {
                    str = "nsk";
                } else if (sortKey.equals("")) {
                    str = "esk";
                } else {
                    str = "gsk=" + sortKey;
                }
                boolean isGroupSummary = notificationRecord2.getNotification().isGroupSummary();
                Object[] objArr = new Object[6];
                objArr[0] = Integer.valueOf(notificationRecord2.getCriticality());
                char c = '0';
                objArr[1] = Character.valueOf((!notificationRecord2.isRecentlyIntrusive() || notificationRecord2.getImportance() <= 1) ? '1' : '0');
                objArr[2] = Integer.valueOf(notificationRecord3.getAuthoritativeRank());
                if (!isGroupSummary) {
                    c = '1';
                }
                objArr[3] = Character.valueOf(c);
                objArr[4] = str;
                objArr[5] = Integer.valueOf(notificationRecord2.getAuthoritativeRank());
                notificationRecord2.setGlobalSortKey(TextUtils.formatSimple("crtcl=0x%04x:intrsv=%c:grnk=0x%04x:gsmry=%c:%s:rnk=0x%04x", objArr));
            }
            this.mProxyByGroupTmp.clear();
        }
        Collections.sort(arrayList, this.mFinalComparator);
    }

    public int indexOf(ArrayList<NotificationRecord> arrayList, NotificationRecord notificationRecord) {
        return Collections.binarySearch(arrayList, notificationRecord, this.mFinalComparator);
    }

    public void dump(PrintWriter printWriter, String str, NotificationManagerService.DumpFilter dumpFilter) {
        int length = this.mSignalExtractors.length;
        printWriter.print(str);
        printWriter.print("mSignalExtractors.length = ");
        printWriter.println(length);
        for (int i = 0; i < length; i++) {
            printWriter.print(str);
            printWriter.print("  ");
            printWriter.println(this.mSignalExtractors[i].getClass().getSimpleName());
        }
    }

    public void dump(ProtoOutputStream protoOutputStream, NotificationManagerService.DumpFilter dumpFilter) {
        int length = this.mSignalExtractors.length;
        for (int i = 0; i < length; i++) {
            protoOutputStream.write(2237677961217L, this.mSignalExtractors[i].getClass().getSimpleName());
        }
    }
}
