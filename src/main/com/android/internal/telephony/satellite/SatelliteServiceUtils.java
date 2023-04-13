package com.android.internal.telephony.satellite;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Binder;
import android.telephony.Rlog;
import android.telephony.satellite.PointingInfo;
import android.telephony.satellite.SatelliteCapabilities;
import android.telephony.satellite.SatelliteDatagram;
import android.telephony.satellite.SatelliteManager;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RILUtils;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import java.util.Arrays;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SatelliteServiceUtils {
    public static int fromSatelliteRadioTechnology(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 3;
                    if (i != 3) {
                        loge("Received invalid radio technology: " + i);
                        return -1;
                    }
                }
            }
            return i2;
        }
        return 0;
    }

    public static int fromSatelliteError(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            default:
                loge("Received invalid satellite service error: " + i);
                return 3;
        }
    }

    public static int fromSatelliteModemState(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 3;
                    if (i != 3) {
                        i2 = 4;
                        if (i != 4) {
                            i2 = 5;
                            if (i != 5) {
                                loge("Received invalid modem state: " + i);
                                return -1;
                            }
                        }
                    }
                }
            }
            return i2;
        }
        return 0;
    }

    public static SatelliteCapabilities fromSatelliteCapabilities(android.telephony.satellite.stub.SatelliteCapabilities satelliteCapabilities) {
        if (satelliteCapabilities == null) {
            return null;
        }
        int[] iArr = satelliteCapabilities.supportedRadioTechnologies;
        if (iArr == null) {
            iArr = new int[0];
        }
        return new SatelliteCapabilities((Set) Arrays.stream(iArr).map(new IntUnaryOperator() { // from class: com.android.internal.telephony.satellite.SatelliteServiceUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                return SatelliteServiceUtils.fromSatelliteRadioTechnology(i);
            }
        }).boxed().collect(Collectors.toSet()), satelliteCapabilities.isAlwaysOn, satelliteCapabilities.needsPointingToSatellite, satelliteCapabilities.needsSeparateSimProfile);
    }

    public static PointingInfo fromPointingInfo(android.telephony.satellite.stub.PointingInfo pointingInfo) {
        if (pointingInfo == null) {
            return null;
        }
        return new PointingInfo(pointingInfo.satelliteAzimuth, pointingInfo.satelliteElevation, pointingInfo.antennaAzimuth, pointingInfo.antennaPitch, pointingInfo.antennaRoll);
    }

    public static SatelliteDatagram fromSatelliteDatagram(android.telephony.satellite.stub.SatelliteDatagram satelliteDatagram) {
        if (satelliteDatagram == null) {
            return null;
        }
        byte[] bArr = satelliteDatagram.data;
        if (bArr == null) {
            bArr = new byte[0];
        }
        return new SatelliteDatagram(bArr);
    }

    public static android.telephony.satellite.stub.SatelliteDatagram toSatelliteDatagram(SatelliteDatagram satelliteDatagram) {
        android.telephony.satellite.stub.SatelliteDatagram satelliteDatagram2 = new android.telephony.satellite.stub.SatelliteDatagram();
        satelliteDatagram2.data = satelliteDatagram.getSatelliteDatagram();
        return satelliteDatagram2;
    }

    public static int getSatelliteError(AsyncResult asyncResult, String str) {
        int i;
        int errorCode;
        SatelliteManager.SatelliteException satelliteException = asyncResult.exception;
        if (satelliteException == null) {
            i = 0;
        } else {
            if (satelliteException instanceof CommandException) {
                errorCode = RILUtils.convertToSatelliteError(((CommandException) satelliteException).getCommandError());
                loge(str + " CommandException: " + asyncResult.exception);
            } else if (satelliteException instanceof SatelliteManager.SatelliteException) {
                errorCode = satelliteException.getErrorCode();
                loge(str + " SatelliteException: " + asyncResult.exception);
            } else {
                loge(str + " unknown exception: " + asyncResult.exception);
                i = 1;
            }
            i = errorCode;
        }
        logd(str + " error: " + i);
        return i;
    }

    public static int getValidSatelliteSubId(int i, Context context) {
        boolean isActiveSubId;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                isActiveSubId = SubscriptionManagerService.getInstance().isActiveSubId(i, context.getOpPackageName(), context.getAttributionTag());
            } else {
                isActiveSubId = SubscriptionController.getInstance().isActiveSubId(i, context.getOpPackageName(), context.getAttributionTag());
            }
            if (isActiveSubId) {
                return i;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            logd("getValidSatelliteSubId: use DEFAULT_SUBSCRIPTION_ID for subId=" + i);
            return KeepaliveStatus.INVALID_HANDLE;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static Phone getPhone() {
        return PhoneFactory.getPhone(0);
    }

    private static void logd(String str) {
        Rlog.d("SatelliteServiceUtils", str);
    }

    private static void loge(String str) {
        Rlog.e("SatelliteServiceUtils", str);
    }
}
