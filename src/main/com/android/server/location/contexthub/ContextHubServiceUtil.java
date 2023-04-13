package com.android.server.location.contexthub;

import android.content.Context;
import android.hardware.contexthub.ContextHubMessage;
import android.hardware.contexthub.NanoappBinary;
import android.hardware.contexthub.NanoappInfo;
import android.hardware.contexthub.NanoappRpcService;
import android.hardware.contexthub.V1_0.ContextHubMsg;
import android.hardware.contexthub.V1_0.NanoAppBinary;
import android.hardware.contexthub.V1_2.HubAppInfo;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppRpcService;
import android.hardware.location.NanoAppState;
import android.util.Log;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ContextHubServiceUtil {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    public static int toTransactionResult(int i) {
        if (i != 0) {
            if (i != 5) {
                int i2 = 2;
                if (i != 2) {
                    i2 = 3;
                    if (i != 3) {
                        return 1;
                    }
                }
                return i2;
            }
            return 4;
        }
        return 0;
    }

    public static HashMap<Integer, ContextHubInfo> createContextHubInfoMap(List<ContextHubInfo> list) {
        HashMap<Integer, ContextHubInfo> hashMap = new HashMap<>();
        for (ContextHubInfo contextHubInfo : list) {
            hashMap.put(Integer.valueOf(contextHubInfo.getId()), contextHubInfo);
        }
        return hashMap;
    }

    public static void copyToByteArrayList(byte[] bArr, ArrayList<Byte> arrayList) {
        arrayList.clear();
        arrayList.ensureCapacity(bArr.length);
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
    }

    public static byte[] createPrimitiveByteArray(ArrayList<Byte> arrayList) {
        byte[] bArr = new byte[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            bArr[i] = arrayList.get(i).byteValue();
        }
        return bArr;
    }

    public static int[] createPrimitiveIntArray(Collection<Integer> collection) {
        int[] iArr = new int[collection.size()];
        int i = 0;
        for (Integer num : collection) {
            iArr[i] = num.intValue();
            i++;
        }
        return iArr;
    }

    public static NanoAppBinary createHidlNanoAppBinary(android.hardware.location.NanoAppBinary nanoAppBinary) {
        NanoAppBinary nanoAppBinary2 = new NanoAppBinary();
        nanoAppBinary2.appId = nanoAppBinary.getNanoAppId();
        nanoAppBinary2.appVersion = nanoAppBinary.getNanoAppVersion();
        nanoAppBinary2.flags = nanoAppBinary.getFlags();
        nanoAppBinary2.targetChreApiMajorVersion = nanoAppBinary.getTargetChreApiMajorVersion();
        nanoAppBinary2.targetChreApiMinorVersion = nanoAppBinary.getTargetChreApiMinorVersion();
        try {
            copyToByteArrayList(nanoAppBinary.getBinaryNoHeader(), nanoAppBinary2.customBinary);
        } catch (IndexOutOfBoundsException e) {
            Log.w("ContextHubServiceUtil", e.getMessage());
        } catch (NullPointerException unused) {
            Log.w("ContextHubServiceUtil", "NanoApp binary was null");
        }
        return nanoAppBinary2;
    }

    public static NanoappBinary createAidlNanoAppBinary(android.hardware.location.NanoAppBinary nanoAppBinary) {
        NanoappBinary nanoappBinary = new NanoappBinary();
        nanoappBinary.nanoappId = nanoAppBinary.getNanoAppId();
        nanoappBinary.nanoappVersion = nanoAppBinary.getNanoAppVersion();
        nanoappBinary.flags = nanoAppBinary.getFlags();
        nanoappBinary.targetChreApiMajorVersion = nanoAppBinary.getTargetChreApiMajorVersion();
        nanoappBinary.targetChreApiMinorVersion = nanoAppBinary.getTargetChreApiMinorVersion();
        nanoappBinary.customBinary = new byte[0];
        try {
            nanoappBinary.customBinary = nanoAppBinary.getBinaryNoHeader();
        } catch (IndexOutOfBoundsException e) {
            Log.w("ContextHubServiceUtil", e.getMessage());
        } catch (NullPointerException unused) {
            Log.w("ContextHubServiceUtil", "NanoApp binary was null");
        }
        return nanoappBinary;
    }

    public static List<NanoAppState> createNanoAppStateList(List<HubAppInfo> list) {
        ArrayList arrayList = new ArrayList();
        for (HubAppInfo hubAppInfo : list) {
            android.hardware.contexthub.V1_0.HubAppInfo hubAppInfo2 = hubAppInfo.info_1_0;
            arrayList.add(new NanoAppState(hubAppInfo2.appId, hubAppInfo2.version, hubAppInfo2.enabled, hubAppInfo.permissions));
        }
        return arrayList;
    }

    public static List<NanoAppState> createNanoAppStateList(NanoappInfo[] nanoappInfoArr) {
        NanoappRpcService[] nanoappRpcServiceArr;
        ArrayList arrayList = new ArrayList();
        for (NanoappInfo nanoappInfo : nanoappInfoArr) {
            ArrayList arrayList2 = new ArrayList();
            for (NanoappRpcService nanoappRpcService : nanoappInfo.rpcServices) {
                arrayList2.add(new NanoAppRpcService(nanoappRpcService.id, nanoappRpcService.version));
            }
            arrayList.add(new NanoAppState(nanoappInfo.nanoappId, nanoappInfo.nanoappVersion, nanoappInfo.enabled, new ArrayList(Arrays.asList(nanoappInfo.permissions)), arrayList2));
        }
        return arrayList;
    }

    public static ContextHubMsg createHidlContextHubMessage(short s, NanoAppMessage nanoAppMessage) {
        ContextHubMsg contextHubMsg = new ContextHubMsg();
        contextHubMsg.appName = nanoAppMessage.getNanoAppId();
        contextHubMsg.hostEndPoint = s;
        contextHubMsg.msgType = nanoAppMessage.getMessageType();
        copyToByteArrayList(nanoAppMessage.getMessageBody(), contextHubMsg.msg);
        return contextHubMsg;
    }

    public static ContextHubMessage createAidlContextHubMessage(short s, NanoAppMessage nanoAppMessage) {
        ContextHubMessage contextHubMessage = new ContextHubMessage();
        contextHubMessage.nanoappId = nanoAppMessage.getNanoAppId();
        contextHubMessage.hostEndPoint = (char) s;
        contextHubMessage.messageType = nanoAppMessage.getMessageType();
        contextHubMessage.messageBody = nanoAppMessage.getMessageBody();
        contextHubMessage.permissions = new String[0];
        return contextHubMessage;
    }

    public static NanoAppMessage createNanoAppMessage(ContextHubMsg contextHubMsg) {
        return NanoAppMessage.createMessageFromNanoApp(contextHubMsg.appName, contextHubMsg.msgType, createPrimitiveByteArray(contextHubMsg.msg), contextHubMsg.hostEndPoint == -1);
    }

    public static NanoAppMessage createNanoAppMessage(ContextHubMessage contextHubMessage) {
        return NanoAppMessage.createMessageFromNanoApp(contextHubMessage.nanoappId, contextHubMessage.messageType, contextHubMessage.messageBody, contextHubMessage.hostEndPoint == 65535);
    }

    public static void checkPermissions(Context context) {
        context.enforceCallingOrSelfPermission("android.permission.ACCESS_CONTEXT_HUB", "ACCESS_CONTEXT_HUB permission required to use Context Hub");
    }

    public static ArrayList<HubAppInfo> toHubAppInfo_1_2(ArrayList<android.hardware.contexthub.V1_0.HubAppInfo> arrayList) {
        ArrayList<HubAppInfo> arrayList2 = new ArrayList<>();
        Iterator<android.hardware.contexthub.V1_0.HubAppInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            android.hardware.contexthub.V1_0.HubAppInfo next = it.next();
            HubAppInfo hubAppInfo = new HubAppInfo();
            android.hardware.contexthub.V1_0.HubAppInfo hubAppInfo2 = hubAppInfo.info_1_0;
            hubAppInfo2.appId = next.appId;
            hubAppInfo2.version = next.version;
            hubAppInfo2.memUsage = next.memUsage;
            hubAppInfo2.enabled = next.enabled;
            hubAppInfo.permissions = new ArrayList();
            arrayList2.add(hubAppInfo);
        }
        return arrayList2;
    }

    public static int toContextHubEvent(int i) {
        if (i != 1) {
            Log.e("ContextHubServiceUtil", "toContextHubEvent: Unknown event type: " + i);
            return 0;
        }
        return 1;
    }

    public static int toContextHubEventFromAidl(int i) {
        if (i != 1) {
            Log.e("ContextHubServiceUtil", "toContextHubEventFromAidl: Unknown event type: " + i);
            return 0;
        }
        return 1;
    }

    public static String formatDateFromTimestamp(long j) {
        return DATE_FORMATTER.format(Instant.ofEpochMilli(j));
    }
}
