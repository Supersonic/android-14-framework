package com.android.server.notification;

import android.app.NotificationHistory;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class NotificationHistoryProtoHelper {
    public static List<String> readStringPool(ProtoInputStream protoInputStream) throws IOException {
        ArrayList arrayList;
        long start = protoInputStream.start(1146756268033L);
        if (protoInputStream.nextField(1120986464257L)) {
            arrayList = new ArrayList(protoInputStream.readInt(1120986464257L));
        } else {
            arrayList = new ArrayList();
        }
        while (protoInputStream.nextField() != -1) {
            if (protoInputStream.getFieldNumber() == 2) {
                arrayList.add(protoInputStream.readString(2237677961218L));
            }
        }
        protoInputStream.end(start);
        return arrayList;
    }

    public static void writeStringPool(ProtoOutputStream protoOutputStream, NotificationHistory notificationHistory) {
        long start = protoOutputStream.start(1146756268033L);
        String[] pooledStringsToWrite = notificationHistory.getPooledStringsToWrite();
        protoOutputStream.write(1120986464257L, pooledStringsToWrite.length);
        for (String str : pooledStringsToWrite) {
            protoOutputStream.write(2237677961218L, str);
        }
        protoOutputStream.end(start);
    }

    public static void readNotification(ProtoInputStream protoInputStream, List<String> list, NotificationHistory notificationHistory, NotificationHistoryFilter notificationHistoryFilter) throws IOException {
        long start = protoInputStream.start(2246267895811L);
        try {
            try {
                NotificationHistory.HistoricalNotification readNotification = readNotification(protoInputStream, list);
                if (notificationHistoryFilter.matchesPackageAndChannelFilter(readNotification) && notificationHistoryFilter.matchesCountFilter(notificationHistory)) {
                    notificationHistory.addNotificationToWrite(readNotification);
                }
            } catch (Exception e) {
                Slog.e("NotifHistoryProto", "Error reading notification", e);
            }
        } finally {
            protoInputStream.end(start);
        }
    }

    public static NotificationHistory.HistoricalNotification readNotification(ProtoInputStream protoInputStream, List<String> list) throws IOException {
        NotificationHistory.HistoricalNotification.Builder builder = new NotificationHistory.HistoricalNotification.Builder();
        String str = null;
        while (true) {
            switch (protoInputStream.nextField()) {
                case -1:
                    return builder.build();
                case 1:
                    str = protoInputStream.readString(1138166333441L);
                    builder.setPackage(str);
                    list.add(str);
                    break;
                case 2:
                    str = list.get(protoInputStream.readInt(1120986464258L) - 1);
                    builder.setPackage(str);
                    break;
                case 3:
                    String readString = protoInputStream.readString(1138166333443L);
                    builder.setChannelName(readString);
                    list.add(readString);
                    break;
                case 4:
                    builder.setChannelName(list.get(protoInputStream.readInt(1120986464260L) - 1));
                    break;
                case 5:
                    String readString2 = protoInputStream.readString(1138166333445L);
                    builder.setChannelId(readString2);
                    list.add(readString2);
                    break;
                case 6:
                    builder.setChannelId(list.get(protoInputStream.readInt(1120986464262L) - 1));
                    break;
                case 7:
                    builder.setUid(protoInputStream.readInt(1120986464263L));
                    break;
                case 8:
                    builder.setUserId(protoInputStream.readInt(1120986464264L));
                    break;
                case 9:
                    builder.setPostedTimeMs(protoInputStream.readLong(1112396529673L));
                    break;
                case 10:
                    builder.setTitle(protoInputStream.readString(1138166333450L));
                    break;
                case 11:
                    builder.setText(protoInputStream.readString(1138166333451L));
                    break;
                case 12:
                    long start = protoInputStream.start(1146756268044L);
                    loadIcon(protoInputStream, builder, str);
                    protoInputStream.end(start);
                    break;
                case 13:
                    String readString3 = protoInputStream.readString(1138166333453L);
                    builder.setConversationId(readString3);
                    list.add(readString3);
                    break;
                case 14:
                    builder.setConversationId(list.get(protoInputStream.readInt(1120986464270L) - 1));
                    break;
            }
        }
    }

    public static void loadIcon(ProtoInputStream protoInputStream, NotificationHistory.HistoricalNotification.Builder builder, String str) throws IOException {
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        byte[] bArr = null;
        String str2 = null;
        String str3 = null;
        int i4 = 0;
        while (true) {
            switch (protoInputStream.nextField()) {
                case -1:
                    if (i == 3) {
                        if (bArr != null) {
                            builder.setIcon(Icon.createWithData(bArr, i2, i3));
                            return;
                        }
                        return;
                    } else if (i != 2) {
                        if (i != 4 || str2 == null) {
                            return;
                        }
                        builder.setIcon(Icon.createWithContentUri(str2));
                        return;
                    } else if (i4 != 0) {
                        if (str3 != null) {
                            str = str3;
                        }
                        builder.setIcon(Icon.createWithResource(str, i4));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    i = protoInputStream.readInt(1159641169921L);
                    break;
                case 2:
                    protoInputStream.readString(1138166333442L);
                    break;
                case 3:
                    i4 = protoInputStream.readInt(1120986464259L);
                    break;
                case 4:
                    str3 = protoInputStream.readString(1138166333444L);
                    break;
                case 5:
                    bArr = protoInputStream.readBytes(1151051235333L);
                    break;
                case 6:
                    i3 = protoInputStream.readInt(1120986464262L);
                    break;
                case 7:
                    i2 = protoInputStream.readInt(1120986464263L);
                    break;
                case 8:
                    str2 = protoInputStream.readString(1138166333448L);
                    break;
            }
        }
    }

    public static void writeIcon(ProtoOutputStream protoOutputStream, NotificationHistory.HistoricalNotification historicalNotification) {
        long start = protoOutputStream.start(1146756268044L);
        protoOutputStream.write(1159641169921L, historicalNotification.getIcon().getType());
        int type = historicalNotification.getIcon().getType();
        if (type == 2) {
            protoOutputStream.write(1120986464259L, historicalNotification.getIcon().getResId());
            if (!historicalNotification.getPackage().equals(historicalNotification.getIcon().getResPackage())) {
                protoOutputStream.write(1138166333444L, historicalNotification.getIcon().getResPackage());
            }
        } else if (type == 3) {
            protoOutputStream.write(1151051235333L, historicalNotification.getIcon().getDataBytes());
            protoOutputStream.write(1120986464262L, historicalNotification.getIcon().getDataLength());
            protoOutputStream.write(1120986464263L, historicalNotification.getIcon().getDataOffset());
        } else if (type == 4) {
            protoOutputStream.write(1138166333448L, historicalNotification.getIcon().getUriString());
        }
        protoOutputStream.end(start);
    }

    public static void writeNotification(ProtoOutputStream protoOutputStream, String[] strArr, NotificationHistory.HistoricalNotification historicalNotification) {
        long start = protoOutputStream.start(2246267895811L);
        int binarySearch = Arrays.binarySearch(strArr, historicalNotification.getPackage());
        if (binarySearch >= 0) {
            protoOutputStream.write(1120986464258L, binarySearch + 1);
        } else {
            Slog.w("NotifHistoryProto", "notification package name (" + historicalNotification.getPackage() + ") not found in string cache");
            protoOutputStream.write(1138166333441L, historicalNotification.getPackage());
        }
        int binarySearch2 = Arrays.binarySearch(strArr, historicalNotification.getChannelName());
        if (binarySearch2 >= 0) {
            protoOutputStream.write(1120986464260L, binarySearch2 + 1);
        } else {
            Slog.w("NotifHistoryProto", "notification channel name (" + historicalNotification.getChannelName() + ") not found in string cache");
            protoOutputStream.write(1138166333443L, historicalNotification.getChannelName());
        }
        int binarySearch3 = Arrays.binarySearch(strArr, historicalNotification.getChannelId());
        if (binarySearch3 >= 0) {
            protoOutputStream.write(1120986464262L, binarySearch3 + 1);
        } else {
            Slog.w("NotifHistoryProto", "notification channel id (" + historicalNotification.getChannelId() + ") not found in string cache");
            protoOutputStream.write(1138166333445L, historicalNotification.getChannelId());
        }
        if (!TextUtils.isEmpty(historicalNotification.getConversationId())) {
            int binarySearch4 = Arrays.binarySearch(strArr, historicalNotification.getConversationId());
            if (binarySearch4 >= 0) {
                protoOutputStream.write(1120986464270L, binarySearch4 + 1);
            } else {
                Slog.w("NotifHistoryProto", "notification conversation id (" + historicalNotification.getConversationId() + ") not found in string cache");
                protoOutputStream.write(1138166333453L, historicalNotification.getConversationId());
            }
        }
        protoOutputStream.write(1120986464263L, historicalNotification.getUid());
        protoOutputStream.write(1120986464264L, historicalNotification.getUserId());
        protoOutputStream.write(1112396529673L, historicalNotification.getPostedTimeMs());
        protoOutputStream.write(1138166333450L, historicalNotification.getTitle());
        protoOutputStream.write(1138166333451L, historicalNotification.getText());
        writeIcon(protoOutputStream, historicalNotification);
        protoOutputStream.end(start);
    }

    public static void read(InputStream inputStream, NotificationHistory notificationHistory, NotificationHistoryFilter notificationHistoryFilter) throws IOException {
        ProtoInputStream protoInputStream = new ProtoInputStream(inputStream);
        List<String> arrayList = new ArrayList<>();
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                break;
            } else if (nextField == 1) {
                arrayList = readStringPool(protoInputStream);
            } else if (nextField == 3) {
                readNotification(protoInputStream, arrayList, notificationHistory, notificationHistoryFilter);
            }
        }
        if (notificationHistoryFilter.isFiltering()) {
            notificationHistory.poolStringsFromNotifications();
        } else {
            notificationHistory.addPooledStrings(arrayList);
        }
    }

    public static void write(OutputStream outputStream, NotificationHistory notificationHistory, int i) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(outputStream);
        protoOutputStream.write(1120986464258L, i);
        writeStringPool(protoOutputStream, notificationHistory);
        List notificationsToWrite = notificationHistory.getNotificationsToWrite();
        int size = notificationsToWrite.size();
        for (int i2 = 0; i2 < size; i2++) {
            writeNotification(protoOutputStream, notificationHistory.getPooledStringsToWrite(), (NotificationHistory.HistoricalNotification) notificationsToWrite.get(i2));
        }
        protoOutputStream.flush();
    }
}
