package com.android.ims.rcs.uce.presence.publish;

import android.content.Context;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.ims.ImsManager;
import com.android.ims.rcs.uce.util.UceUtils;
/* loaded from: classes.dex */
public class PublishUtils {
    private static final String DOMAIN_SEPARATOR = "@";
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PublishUtils";
    private static final String SCHEME_SIP = "sip";
    private static final String SCHEME_TEL = "tel";

    public static Uri getDeviceContactUri(Context context, int subId, DeviceCapabilityInfo deviceCap, boolean isForPresence) {
        boolean preferTelUri = false;
        if (isForPresence) {
            preferTelUri = UceUtils.isTelUriForPidfXmlEnabled(context, subId);
        }
        Uri contactUri = deviceCap.getImsAssociatedUri(preferTelUri);
        if (contactUri != null) {
            Uri convertedUri = preferTelUri ? getConvertedTelUri(context, contactUri) : contactUri;
            Log.d(LOG_TAG, "getDeviceContactUri: returning " + (contactUri.equals(convertedUri) ? "found" : "converted") + " ims associated uri");
            return contactUri;
        }
        TelephonyManager telephonyManager = getTelephonyManager(context, subId);
        if (telephonyManager == null) {
            Log.w(LOG_TAG, "getDeviceContactUri: TelephonyManager is null");
            return null;
        }
        Uri contactUri2 = getContactUriFromIsim(telephonyManager);
        if (contactUri2 != null) {
            Log.d(LOG_TAG, "getDeviceContactUri: impu");
            if (preferTelUri) {
                return getConvertedTelUri(context, contactUri2);
            }
            return contactUri2;
        }
        Log.d(LOG_TAG, "getDeviceContactUri: line number");
        if (preferTelUri) {
            return getConvertedTelUri(context, getContactUriFromLine1Number(telephonyManager));
        }
        return getContactUriFromLine1Number(telephonyManager);
    }

    public static String removeNumbersFromUris(String source) {
        return source.replaceAll("(?:sips?|tel):(\\+?[\\d\\-]+)", "[removed]");
    }

    private static Uri getContactUriFromIsim(TelephonyManager telephonyManager) {
        String domain = telephonyManager.getIsimDomain();
        String[] impus = telephonyManager.getIsimImpu();
        if (TextUtils.isEmpty(domain) || impus == null) {
            String str = LOG_TAG;
            Log.d(str, "getContactUriFromIsim: domain is null=" + TextUtils.isEmpty(domain));
            Log.d(str, "getContactUriFromIsim: impu is null=" + ((impus == null || impus.length == 0) ? ImsManager.TRUE : ImsManager.FALSE));
            return null;
        }
        for (String impu : impus) {
            if (!TextUtils.isEmpty(impu)) {
                Uri impuUri = Uri.parse(impu);
                String scheme = impuUri.getScheme();
                String schemeSpecificPart = impuUri.getSchemeSpecificPart();
                if (SCHEME_SIP.equals(scheme) && !TextUtils.isEmpty(schemeSpecificPart) && schemeSpecificPart.endsWith(domain)) {
                    return impuUri;
                }
            }
        }
        Log.d(LOG_TAG, "getContactUriFromIsim: there is no impu matching the domain");
        return null;
    }

    private static Uri getContactUriFromLine1Number(TelephonyManager telephonyManager) {
        String phoneNumber = formatPhoneNumber(telephonyManager.getLine1Number());
        if (TextUtils.isEmpty(phoneNumber)) {
            Log.w(LOG_TAG, "Cannot get the phone number");
            return null;
        }
        String domain = telephonyManager.getIsimDomain();
        if (!TextUtils.isEmpty(domain)) {
            return Uri.fromParts(SCHEME_SIP, phoneNumber + DOMAIN_SEPARATOR + domain, null);
        }
        return Uri.fromParts(SCHEME_TEL, phoneNumber, null);
    }

    private static String formatPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Log.w(LOG_TAG, "formatPhoneNumber: phone number is empty");
            return null;
        }
        String number = PhoneNumberUtils.stripSeparators(phoneNumber);
        return PhoneNumberUtils.normalizeNumber(number);
    }

    private static TelephonyManager getTelephonyManager(Context context, int subId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.createForSubscriptionId(subId);
    }

    private static Uri getConvertedTelUri(Context context, Uri contactUri) {
        if (contactUri == null) {
            return null;
        }
        if (contactUri.getScheme().equalsIgnoreCase(SCHEME_SIP)) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (manager.getIsimDomain() == null) {
                return contactUri;
            }
            String numbers = contactUri.getSchemeSpecificPart();
            String[] numberParts = numbers.split("[@;:]");
            String number = numberParts[0];
            String simCountryIso = manager.getSimCountryIso();
            if (!TextUtils.isEmpty(simCountryIso)) {
                String simCountryIso2 = simCountryIso.toUpperCase();
                PhoneNumberUtil util = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber phoneNumber = util.parse(number, simCountryIso2);
                    number = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                    String telUri = "tel:" + number;
                    return Uri.parse(telUri);
                } catch (NumberParseException e) {
                    Log.w(LOG_TAG, "formatNumber: could not format " + number + ", error: " + e);
                    return contactUri;
                }
            }
            return contactUri;
        }
        return contactUri;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getCapabilityType(Context context, int subId) {
        boolean isPresenceSupported = UceUtils.isPresenceSupported(context, subId);
        boolean isSipOptionsSupported = UceUtils.isSipOptionsSupported(context, subId);
        if (isPresenceSupported) {
            return 2;
        }
        if (isSipOptionsSupported) {
            return 1;
        }
        return 0;
    }
}
