package android.telecom;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Country;
import android.location.CountryDetector;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.android.internal.C4057R;
import java.util.Locale;
/* loaded from: classes3.dex */
public class CallerInfo {
    private static final String TAG = "CallerInfo";
    public static final long USER_TYPE_CURRENT = 0;
    public static final long USER_TYPE_WORK = 1;
    private static final boolean VDBG = Log.VERBOSE;
    public Drawable cachedPhoto;
    public Bitmap cachedPhotoIcon;
    public String cnapName;
    private Uri contactDisplayPhotoUri;
    public boolean contactExists;
    private long contactIdOrZero;
    public Uri contactRefUri;
    public Uri contactRingtoneUri;
    public String geoDescription;
    public boolean isCachedPhotoCurrent;
    public String lookupKey;
    private String name;
    public int namePresentation;
    public boolean needUpdate;
    public String normalizedNumber;
    public String numberLabel;
    public int numberPresentation;
    public int numberType;
    public String phoneLabel;
    private String phoneNumber;
    public int photoResource;
    public ComponentName preferredPhoneAccountComponent;
    public String preferredPhoneAccountId;
    public boolean shouldSendToVoicemail;
    private boolean mIsEmergency = false;
    private boolean mIsVoiceMail = false;
    public long userType = 0;

    public static CallerInfo getCallerInfo(Context context, Uri contactRef, Cursor cursor) {
        boolean z;
        int typeColumnIndex;
        CallerInfo info = new CallerInfo();
        info.photoResource = 0;
        info.phoneLabel = null;
        info.numberType = 0;
        info.numberLabel = null;
        info.cachedPhoto = null;
        info.isCachedPhotoCurrent = false;
        info.contactExists = false;
        info.userType = 0L;
        boolean z2 = VDBG;
        if (z2) {
            Log.m132v(TAG, "getCallerInfo() based on cursor...", new Object[0]);
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("display_name");
                if (columnIndex != -1) {
                    info.name = cursor.getString(columnIndex);
                }
                int columnIndex2 = cursor.getColumnIndex("number");
                if (columnIndex2 != -1) {
                    info.phoneNumber = cursor.getString(columnIndex2);
                }
                int columnIndex3 = cursor.getColumnIndex("normalized_number");
                if (columnIndex3 != -1) {
                    info.normalizedNumber = cursor.getString(columnIndex3);
                }
                int columnIndex4 = cursor.getColumnIndex("label");
                if (columnIndex4 != -1 && (typeColumnIndex = cursor.getColumnIndex("type")) != -1) {
                    info.numberType = cursor.getInt(typeColumnIndex);
                    String string = cursor.getString(columnIndex4);
                    info.numberLabel = string;
                    info.phoneLabel = ContactsContract.CommonDataKinds.Phone.getDisplayLabel(context, info.numberType, string).toString();
                }
                int columnIndex5 = getColumnIndexForPersonId(contactRef, cursor);
                if (columnIndex5 == -1) {
                    Log.m130w(TAG, "Couldn't find contact_id column for " + contactRef, new Object[0]);
                } else {
                    long contactId = cursor.getLong(columnIndex5);
                    if (contactId != 0 && !ContactsContract.Contacts.isEnterpriseContactId(contactId)) {
                        info.contactIdOrZero = contactId;
                        if (z2) {
                            Log.m132v(TAG, "==> got info.contactIdOrZero: " + info.contactIdOrZero, new Object[0]);
                        }
                    }
                    if (ContactsContract.Contacts.isEnterpriseContactId(contactId)) {
                        info.userType = 1L;
                    }
                }
                int columnIndex6 = cursor.getColumnIndex("lookup");
                if (columnIndex6 != -1) {
                    info.lookupKey = cursor.getString(columnIndex6);
                }
                int columnIndex7 = cursor.getColumnIndex("photo_uri");
                if (columnIndex7 != -1 && cursor.getString(columnIndex7) != null) {
                    info.contactDisplayPhotoUri = Uri.parse(cursor.getString(columnIndex7));
                } else {
                    info.contactDisplayPhotoUri = null;
                }
                int columnIndex8 = cursor.getColumnIndex(ContactsContract.DataColumns.PREFERRED_PHONE_ACCOUNT_COMPONENT_NAME);
                if (columnIndex8 != -1 && cursor.getString(columnIndex8) != null) {
                    info.preferredPhoneAccountComponent = ComponentName.unflattenFromString(cursor.getString(columnIndex8));
                }
                int columnIndex9 = cursor.getColumnIndex(ContactsContract.DataColumns.PREFERRED_PHONE_ACCOUNT_ID);
                if (columnIndex9 != -1 && cursor.getString(columnIndex9) != null) {
                    info.preferredPhoneAccountId = cursor.getString(columnIndex9);
                }
                int columnIndex10 = cursor.getColumnIndex("custom_ringtone");
                if (columnIndex10 != -1 && cursor.getString(columnIndex10) != null) {
                    if (TextUtils.isEmpty(cursor.getString(columnIndex10))) {
                        info.contactRingtoneUri = Uri.EMPTY;
                    } else {
                        info.contactRingtoneUri = Uri.parse(cursor.getString(columnIndex10));
                    }
                } else {
                    info.contactRingtoneUri = null;
                }
                int columnIndex11 = cursor.getColumnIndex("send_to_voicemail");
                if (columnIndex11 == -1 || cursor.getInt(columnIndex11) != 1) {
                    z = false;
                } else {
                    z = true;
                }
                info.shouldSendToVoicemail = z;
                info.contactExists = true;
            }
            cursor.close();
        }
        info.needUpdate = false;
        info.name = normalize(info.name);
        info.contactRefUri = contactRef;
        return info;
    }

    public static CallerInfo getCallerInfo(Context context, Uri contactRef) {
        ContentResolver cr = CallerInfoAsyncQuery.getCurrentProfileContentResolver(context);
        if (cr == null) {
            return null;
        }
        try {
            CallerInfo info = getCallerInfo(context, contactRef, cr.query(contactRef, null, null, null, null));
            return info;
        } catch (RuntimeException re) {
            Log.m136e(TAG, (Throwable) re, "Error getting caller info.", new Object[0]);
            return null;
        }
    }

    public static CallerInfo getCallerInfo(Context context, String number) {
        if (VDBG) {
            Log.m132v(TAG, "getCallerInfo() based on number...", new Object[0]);
        }
        int subId = SubscriptionManager.getDefaultSubscriptionId();
        return getCallerInfo(context, number, subId);
    }

    public static CallerInfo getCallerInfo(Context context, String number, int subId) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (tm.isEmergencyNumber(number)) {
            return new CallerInfo().markAsEmergency(context);
        }
        if (PhoneNumberUtils.isVoiceMailNumber(null, subId, number)) {
            return new CallerInfo().markAsVoiceMail(context, subId);
        }
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI, Uri.encode(number));
        CallerInfo info = doSecondaryLookupIfNecessary(context, number, getCallerInfo(context, contactUri));
        if (TextUtils.isEmpty(info.phoneNumber)) {
            info.phoneNumber = number;
        }
        return info;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String number) {
        this.phoneNumber = number;
    }

    public long getContactId() {
        return this.contactIdOrZero;
    }

    public Uri getContactDisplayPhotoUri() {
        return this.contactDisplayPhotoUri;
    }

    public void SetContactDisplayPhotoUri(Uri photoUri) {
        this.contactDisplayPhotoUri = photoUri;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CallerInfo doSecondaryLookupIfNecessary(Context context, String number, CallerInfo previousResult) {
        if (!previousResult.contactExists && PhoneNumberUtils.isUriNumber(number)) {
            String username = PhoneNumberUtils.getUsernameFromUriNumber(number);
            if (PhoneNumberUtils.isGlobalPhoneNumber(username)) {
                return getCallerInfo(context, Uri.withAppendedPath(ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI, Uri.encode(username)));
            }
            return previousResult;
        }
        return previousResult;
    }

    public boolean isEmergencyNumber() {
        return this.mIsEmergency;
    }

    public boolean isVoiceMailNumber() {
        return this.mIsVoiceMail;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallerInfo markAsEmergency(Context context) {
        this.phoneNumber = context.getString(C4057R.string.emergency_call_dialog_number_for_display);
        this.photoResource = C4057R.C4058drawable.picture_emergency;
        this.mIsEmergency = true;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallerInfo markAsVoiceMail(Context context, int subId) {
        this.mIsVoiceMail = true;
        try {
            this.phoneNumber = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subId).getVoiceMailAlphaTag();
        } catch (SecurityException se) {
            Log.m136e(TAG, (Throwable) se, "Cannot access VoiceMail.", new Object[0]);
        }
        return this;
    }

    private static String normalize(String s) {
        if (s == null || s.length() > 0) {
            return s;
        }
        return null;
    }

    private static int getColumnIndexForPersonId(Uri contactRef, Cursor cursor) {
        boolean z = VDBG;
        if (z) {
            Log.m132v(TAG, "- getColumnIndexForPersonId: contactRef URI = '" + contactRef + "'...", new Object[0]);
        }
        String url = contactRef.toString();
        String columnName = null;
        if (url.startsWith("content://com.android.contacts/data/phones")) {
            if (z) {
                Log.m132v(TAG, "'data/phones' URI; using RawContacts.CONTACT_ID", new Object[0]);
            }
            columnName = "contact_id";
        } else if (url.startsWith("content://com.android.contacts/data")) {
            if (z) {
                Log.m132v(TAG, "'data' URI; using Data.CONTACT_ID", new Object[0]);
            }
            columnName = "contact_id";
        } else if (url.startsWith("content://com.android.contacts/phone_lookup")) {
            if (z) {
                Log.m132v(TAG, "'phone_lookup' URI; using PhoneLookup._ID", new Object[0]);
            }
            columnName = "_id";
        } else {
            Log.m130w(TAG, "Unexpected prefix for contactRef '" + url + "'", new Object[0]);
        }
        int columnIndex = columnName != null ? cursor.getColumnIndex(columnName) : -1;
        if (z) {
            Log.m132v(TAG, "==> Using column '" + columnName + "' (columnIndex = " + columnIndex + ") for person_id lookup...", new Object[0]);
        }
        return columnIndex;
    }

    public void updateGeoDescription(Context context, String fallbackNumber) {
        String number = TextUtils.isEmpty(this.phoneNumber) ? fallbackNumber : this.phoneNumber;
        this.geoDescription = getGeoDescription(context, number);
    }

    public static String getGeoDescription(Context context, String number) {
        boolean z = VDBG;
        if (z) {
            Log.m132v(TAG, "getGeoDescription('" + number + "')...", new Object[0]);
        }
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
        Locale locale = context.getResources().getConfiguration().locale;
        String countryIso = getCurrentCountryIso(context, locale);
        Phonenumber.PhoneNumber pn = null;
        if (z) {
            try {
                Log.m132v(TAG, "parsing '" + number + "' for countryIso '" + countryIso + "'...", new Object[0]);
            } catch (NumberParseException e) {
                Log.m130w(TAG, "getGeoDescription: NumberParseException for incoming number '" + Log.pii(number) + "'", new Object[0]);
            }
        }
        pn = util.parse(number, countryIso);
        if (z) {
            Log.m132v(TAG, "- parsed number: " + pn, new Object[0]);
        }
        if (pn != null) {
            String description = geocoder.getDescriptionForNumber(pn, locale);
            if (VDBG) {
                Log.m132v(TAG, "- got description: '" + description + "'", new Object[0]);
            }
            return description;
        }
        return null;
    }

    private static String getCurrentCountryIso(Context context, Locale locale) {
        String countryIso = null;
        CountryDetector detector = (CountryDetector) context.getSystemService(Context.COUNTRY_DETECTOR);
        if (detector != null) {
            Country country = detector.detectCountry();
            if (country == null) {
                Log.m136e(TAG, (Throwable) new Exception(), "CountryDetector.detectCountry() returned null.", new Object[0]);
            } else {
                countryIso = country.getCountryIso();
            }
        }
        if (countryIso == null) {
            String countryIso2 = locale.getCountry();
            Log.m130w(TAG, "No CountryDetector; falling back to countryIso based on locale: " + countryIso2, new Object[0]);
            return countryIso2;
        }
        return countryIso;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getCurrentCountryIso(Context context) {
        return getCurrentCountryIso(context, Locale.getDefault());
    }

    public String toString() {
        return new StringBuilder(128).append(super.toString() + " { ").append("name " + (this.name == null ? "null" : "non-null")).append(", phoneNumber " + (this.phoneNumber != null ? "non-null" : "null")).append(" }").toString();
    }
}
