package android.telephony;

import android.annotation.SystemApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Telephony;
import android.telephony.CbGeoUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class SmsCbMessage implements Parcelable {
    public static final Parcelable.Creator<SmsCbMessage> CREATOR = new Parcelable.Creator<SmsCbMessage>() { // from class: android.telephony.SmsCbMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsCbMessage createFromParcel(Parcel in) {
            return new SmsCbMessage(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsCbMessage[] newArray(int size) {
            return new SmsCbMessage[size];
        }
    };
    public static final int GEOGRAPHICAL_SCOPE_CELL_WIDE = 3;
    public static final int GEOGRAPHICAL_SCOPE_CELL_WIDE_IMMEDIATE = 0;
    public static final int GEOGRAPHICAL_SCOPE_LOCATION_AREA_WIDE = 2;
    public static final int GEOGRAPHICAL_SCOPE_PLMN_WIDE = 1;
    public static final String LOG_TAG = "SMSCB";
    public static final int MAXIMUM_WAIT_TIME_NOT_SET = 255;
    public static final int MESSAGE_FORMAT_3GPP = 1;
    public static final int MESSAGE_FORMAT_3GPP2 = 2;
    public static final int MESSAGE_PRIORITY_EMERGENCY = 3;
    public static final int MESSAGE_PRIORITY_INTERACTIVE = 1;
    public static final int MESSAGE_PRIORITY_NORMAL = 0;
    public static final int MESSAGE_PRIORITY_URGENT = 2;
    private final String mBody;
    private final SmsCbCmasInfo mCmasWarningInfo;
    private final int mDataCodingScheme;
    private final SmsCbEtwsInfo mEtwsWarningInfo;
    private final int mGeographicalScope;
    private final List<CbGeoUtils.Geometry> mGeometries;
    private final String mLanguage;
    private final SmsCbLocation mLocation;
    private final int mMaximumWaitTimeSec;
    private final int mMessageFormat;
    private final int mPriority;
    private final long mReceivedTimeMillis;
    private final int mSerialNumber;
    private final int mServiceCategory;
    private final int mSlotIndex;
    private final int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GeographicalScope {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MessageFormat {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MessagePriority {
    }

    public SmsCbMessage(int messageFormat, int geographicalScope, int serialNumber, SmsCbLocation location, int serviceCategory, String language, String body, int priority, SmsCbEtwsInfo etwsWarningInfo, SmsCbCmasInfo cmasWarningInfo, int slotIndex, int subId) {
        this(messageFormat, geographicalScope, serialNumber, location, serviceCategory, language, 0, body, priority, etwsWarningInfo, cmasWarningInfo, 0, null, System.currentTimeMillis(), slotIndex, subId);
    }

    public SmsCbMessage(int messageFormat, int geographicalScope, int serialNumber, SmsCbLocation location, int serviceCategory, String language, int dataCodingScheme, String body, int priority, SmsCbEtwsInfo etwsWarningInfo, SmsCbCmasInfo cmasWarningInfo, int maximumWaitTimeSec, List<CbGeoUtils.Geometry> geometries, long receivedTimeMillis, int slotIndex, int subId) {
        this.mMessageFormat = messageFormat;
        this.mGeographicalScope = geographicalScope;
        this.mSerialNumber = serialNumber;
        this.mLocation = location;
        this.mServiceCategory = serviceCategory;
        this.mLanguage = language;
        this.mDataCodingScheme = dataCodingScheme;
        this.mBody = body;
        this.mPriority = priority;
        this.mEtwsWarningInfo = etwsWarningInfo;
        this.mCmasWarningInfo = cmasWarningInfo;
        this.mReceivedTimeMillis = receivedTimeMillis;
        this.mGeometries = geometries;
        this.mMaximumWaitTimeSec = maximumWaitTimeSec;
        this.mSlotIndex = slotIndex;
        this.mSubId = subId;
    }

    public SmsCbMessage(Parcel in) {
        this.mMessageFormat = in.readInt();
        this.mGeographicalScope = in.readInt();
        this.mSerialNumber = in.readInt();
        this.mLocation = new SmsCbLocation(in);
        this.mServiceCategory = in.readInt();
        this.mLanguage = in.readString();
        this.mDataCodingScheme = in.readInt();
        this.mBody = in.readString();
        this.mPriority = in.readInt();
        int type = in.readInt();
        switch (type) {
            case 67:
                this.mEtwsWarningInfo = null;
                this.mCmasWarningInfo = new SmsCbCmasInfo(in);
                break;
            case 68:
            default:
                this.mEtwsWarningInfo = null;
                this.mCmasWarningInfo = null;
                break;
            case 69:
                this.mEtwsWarningInfo = new SmsCbEtwsInfo(in);
                this.mCmasWarningInfo = null;
                break;
        }
        this.mReceivedTimeMillis = in.readLong();
        String geoStr = in.readString();
        this.mGeometries = geoStr != null ? CbGeoUtils.parseGeometriesFromString(geoStr) : null;
        this.mMaximumWaitTimeSec = in.readInt();
        this.mSlotIndex = in.readInt();
        this.mSubId = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMessageFormat);
        dest.writeInt(this.mGeographicalScope);
        dest.writeInt(this.mSerialNumber);
        this.mLocation.writeToParcel(dest, flags);
        dest.writeInt(this.mServiceCategory);
        dest.writeString(this.mLanguage);
        dest.writeInt(this.mDataCodingScheme);
        dest.writeString(this.mBody);
        dest.writeInt(this.mPriority);
        if (this.mEtwsWarningInfo != null) {
            dest.writeInt(69);
            this.mEtwsWarningInfo.writeToParcel(dest, flags);
        } else if (this.mCmasWarningInfo != null) {
            dest.writeInt(67);
            this.mCmasWarningInfo.writeToParcel(dest, flags);
        } else {
            dest.writeInt(48);
        }
        dest.writeLong(this.mReceivedTimeMillis);
        List<CbGeoUtils.Geometry> list = this.mGeometries;
        dest.writeString(list != null ? CbGeoUtils.encodeGeometriesToString(list) : null);
        dest.writeInt(this.mMaximumWaitTimeSec);
        dest.writeInt(this.mSlotIndex);
        dest.writeInt(this.mSubId);
    }

    public int getGeographicalScope() {
        return this.mGeographicalScope;
    }

    public int getSerialNumber() {
        return this.mSerialNumber;
    }

    public SmsCbLocation getLocation() {
        return this.mLocation;
    }

    public int getServiceCategory() {
        return this.mServiceCategory;
    }

    public String getLanguageCode() {
        return this.mLanguage;
    }

    public int getDataCodingScheme() {
        return this.mDataCodingScheme;
    }

    public String getMessageBody() {
        return this.mBody;
    }

    @SystemApi
    public List<CbGeoUtils.Geometry> getGeometries() {
        List<CbGeoUtils.Geometry> list = this.mGeometries;
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    public int getMaximumWaitingDuration() {
        return this.mMaximumWaitTimeSec;
    }

    public long getReceivedTime() {
        return this.mReceivedTimeMillis;
    }

    public int getSlotIndex() {
        return this.mSlotIndex;
    }

    public int getSubscriptionId() {
        return this.mSubId;
    }

    public int getMessageFormat() {
        return this.mMessageFormat;
    }

    public int getMessagePriority() {
        return this.mPriority;
    }

    public SmsCbEtwsInfo getEtwsWarningInfo() {
        return this.mEtwsWarningInfo;
    }

    public SmsCbCmasInfo getCmasWarningInfo() {
        return this.mCmasWarningInfo;
    }

    public boolean isEmergencyMessage() {
        return this.mPriority == 3;
    }

    public boolean isEtwsMessage() {
        return this.mEtwsWarningInfo != null;
    }

    public boolean isCmasMessage() {
        return this.mCmasWarningInfo != null;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("SmsCbMessage{geographicalScope=").append(this.mGeographicalScope).append(", serialNumber=").append(this.mSerialNumber).append(", location=").append(this.mLocation).append(", serviceCategory=").append(this.mServiceCategory).append(", language=").append(this.mLanguage).append(", body=").append(this.mBody).append(", priority=").append(this.mPriority).append(this.mEtwsWarningInfo != null ? ", " + this.mEtwsWarningInfo.toString() : "").append(this.mCmasWarningInfo != null ? ", " + this.mCmasWarningInfo.toString() : "").append(", maximumWaitingTime=").append(this.mMaximumWaitTimeSec).append(", received time=").append(this.mReceivedTimeMillis).append(", slotIndex = ").append(this.mSlotIndex).append(", geo=");
        List<CbGeoUtils.Geometry> list = this.mGeometries;
        return append.append(list != null ? CbGeoUtils.encodeGeometriesToString(list) : "null").append('}').toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues(16);
        cv.put("slot_index", Integer.valueOf(this.mSlotIndex));
        cv.put("sub_id", Integer.valueOf(this.mSubId));
        cv.put(Telephony.CellBroadcasts.GEOGRAPHICAL_SCOPE, Integer.valueOf(this.mGeographicalScope));
        if (this.mLocation.getPlmn() != null) {
            cv.put("plmn", this.mLocation.getPlmn());
        }
        if (this.mLocation.getLac() != -1) {
            cv.put(Telephony.CellBroadcasts.LAC, Integer.valueOf(this.mLocation.getLac()));
        }
        if (this.mLocation.getCid() != -1) {
            cv.put("cid", Integer.valueOf(this.mLocation.getCid()));
        }
        cv.put("serial_number", Integer.valueOf(getSerialNumber()));
        cv.put(Telephony.CellBroadcasts.SERVICE_CATEGORY, Integer.valueOf(getServiceCategory()));
        cv.put("language", getLanguageCode());
        cv.put(Telephony.CellBroadcasts.DATA_CODING_SCHEME, Integer.valueOf(getDataCodingScheme()));
        cv.put("body", getMessageBody());
        cv.put(Telephony.CellBroadcasts.MESSAGE_FORMAT, Integer.valueOf(getMessageFormat()));
        cv.put("priority", Integer.valueOf(getMessagePriority()));
        SmsCbEtwsInfo etwsInfo = getEtwsWarningInfo();
        if (etwsInfo != null) {
            cv.put(Telephony.CellBroadcasts.ETWS_WARNING_TYPE, Integer.valueOf(etwsInfo.getWarningType()));
            cv.put(Telephony.CellBroadcasts.ETWS_IS_PRIMARY, Boolean.valueOf(etwsInfo.isPrimary()));
        }
        SmsCbCmasInfo cmasInfo = getCmasWarningInfo();
        if (cmasInfo != null) {
            cv.put(Telephony.CellBroadcasts.CMAS_MESSAGE_CLASS, Integer.valueOf(cmasInfo.getMessageClass()));
            cv.put(Telephony.CellBroadcasts.CMAS_CATEGORY, Integer.valueOf(cmasInfo.getCategory()));
            cv.put(Telephony.CellBroadcasts.CMAS_RESPONSE_TYPE, Integer.valueOf(cmasInfo.getResponseType()));
            cv.put(Telephony.CellBroadcasts.CMAS_SEVERITY, Integer.valueOf(cmasInfo.getSeverity()));
            cv.put(Telephony.CellBroadcasts.CMAS_URGENCY, Integer.valueOf(cmasInfo.getUrgency()));
            cv.put(Telephony.CellBroadcasts.CMAS_CERTAINTY, Integer.valueOf(cmasInfo.getCertainty()));
        }
        cv.put(Telephony.CellBroadcasts.RECEIVED_TIME, Long.valueOf(this.mReceivedTimeMillis));
        List<CbGeoUtils.Geometry> list = this.mGeometries;
        if (list != null) {
            cv.put(Telephony.CellBroadcasts.GEOMETRIES, CbGeoUtils.encodeGeometriesToString(list));
        } else {
            cv.put(Telephony.CellBroadcasts.GEOMETRIES, (String) null);
        }
        cv.put(Telephony.CellBroadcasts.MAXIMUM_WAIT_TIME, Integer.valueOf(this.mMaximumWaitTimeSec));
        return cv;
    }

    public static SmsCbMessage createFromCursor(Cursor cursor) {
        String plmn;
        int lac;
        int cid;
        SmsCbEtwsInfo etwsInfo;
        int etwsWarningTypeColumn;
        int cid2;
        SmsCbCmasInfo cmasInfo;
        int cmasCategory;
        int responseType;
        int severity;
        int urgency;
        int certainty;
        int geoScope = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.CellBroadcasts.GEOGRAPHICAL_SCOPE));
        int serialNum = cursor.getInt(cursor.getColumnIndexOrThrow("serial_number"));
        int category = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.CellBroadcasts.SERVICE_CATEGORY));
        String language = cursor.getString(cursor.getColumnIndexOrThrow("language"));
        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        int format = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.CellBroadcasts.MESSAGE_FORMAT));
        int priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));
        int slotIndex = cursor.getInt(cursor.getColumnIndexOrThrow("slot_index"));
        int subId = cursor.getInt(cursor.getColumnIndexOrThrow("sub_id"));
        int plmnColumn = cursor.getColumnIndex("plmn");
        if (plmnColumn != -1 && !cursor.isNull(plmnColumn)) {
            plmn = cursor.getString(plmnColumn);
        } else {
            plmn = null;
        }
        int lacColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.LAC);
        if (lacColumn != -1 && !cursor.isNull(lacColumn)) {
            lac = cursor.getInt(lacColumn);
        } else {
            lac = -1;
        }
        int cidColumn = cursor.getColumnIndex("cid");
        if (cidColumn != -1 && !cursor.isNull(cidColumn)) {
            cid = cursor.getInt(cidColumn);
        } else {
            cid = -1;
        }
        SmsCbLocation location = new SmsCbLocation(plmn, lac, cid);
        int etwsWarningTypeColumn2 = cursor.getColumnIndex(Telephony.CellBroadcasts.ETWS_WARNING_TYPE);
        int etwsIsPrimaryColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.ETWS_IS_PRIMARY);
        if (etwsWarningTypeColumn2 != -1 && !cursor.isNull(etwsWarningTypeColumn2) && etwsIsPrimaryColumn != -1 && !cursor.isNull(etwsIsPrimaryColumn)) {
            int warningType = cursor.getInt(etwsWarningTypeColumn2);
            boolean isPrimary = cursor.getInt(etwsIsPrimaryColumn) != 0;
            SmsCbEtwsInfo etwsInfo2 = new SmsCbEtwsInfo(warningType, false, false, isPrimary, null);
            etwsInfo = etwsInfo2;
        } else {
            etwsInfo = null;
        }
        int cmasMessageClassColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_MESSAGE_CLASS);
        if (cmasMessageClassColumn != -1 && !cursor.isNull(cmasMessageClassColumn)) {
            int messageClass = cursor.getInt(cmasMessageClassColumn);
            int cmasCategoryColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_CATEGORY);
            if (cmasCategoryColumn != -1 && !cursor.isNull(cmasCategoryColumn)) {
                cmasCategory = cursor.getInt(cmasCategoryColumn);
            } else {
                cmasCategory = -1;
            }
            int cmasResponseTypeColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_RESPONSE_TYPE);
            if (cmasResponseTypeColumn != -1 && !cursor.isNull(cmasResponseTypeColumn)) {
                responseType = cursor.getInt(cmasResponseTypeColumn);
            } else {
                responseType = -1;
            }
            int cmasSeverityColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_SEVERITY);
            if (cmasSeverityColumn != -1 && !cursor.isNull(cmasSeverityColumn)) {
                severity = cursor.getInt(cmasSeverityColumn);
            } else {
                severity = -1;
            }
            int cmasUrgencyColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_URGENCY);
            etwsWarningTypeColumn = etwsWarningTypeColumn2;
            if (cmasUrgencyColumn != -1 && !cursor.isNull(cmasUrgencyColumn)) {
                urgency = cursor.getInt(cmasUrgencyColumn);
            } else {
                urgency = -1;
            }
            int cmasCertaintyColumn = cursor.getColumnIndex(Telephony.CellBroadcasts.CMAS_CERTAINTY);
            cid2 = cid;
            if (cmasCertaintyColumn != -1 && !cursor.isNull(cmasCertaintyColumn)) {
                certainty = cursor.getInt(cmasCertaintyColumn);
            } else {
                certainty = -1;
            }
            cmasInfo = new SmsCbCmasInfo(messageClass, cmasCategory, responseType, severity, urgency, certainty);
        } else {
            etwsWarningTypeColumn = etwsWarningTypeColumn2;
            cid2 = cid;
            cmasInfo = null;
        }
        String geoStr = cursor.getString(cursor.getColumnIndex(Telephony.CellBroadcasts.GEOMETRIES));
        List<CbGeoUtils.Geometry> geometries = geoStr != null ? CbGeoUtils.parseGeometriesFromString(geoStr) : null;
        long receivedTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.CellBroadcasts.RECEIVED_TIME));
        int maximumWaitTimeSec = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.CellBroadcasts.MAXIMUM_WAIT_TIME));
        return new SmsCbMessage(format, geoScope, serialNum, location, category, language, 0, body, priority, etwsInfo, cmasInfo, maximumWaitTimeSec, geometries, receivedTimeMillis, slotIndex, subId);
    }

    public boolean needGeoFencingCheck() {
        List<CbGeoUtils.Geometry> list;
        return (this.mMaximumWaitTimeSec <= 0 || (list = this.mGeometries) == null || list.isEmpty()) ? false : true;
    }
}
