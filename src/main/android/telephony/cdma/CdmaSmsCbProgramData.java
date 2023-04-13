package android.telephony.cdma;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class CdmaSmsCbProgramData implements Parcelable {
    public static final int ALERT_OPTION_DEFAULT_ALERT = 1;
    public static final int ALERT_OPTION_HIGH_PRIORITY_ONCE = 10;
    public static final int ALERT_OPTION_HIGH_PRIORITY_REPEAT = 11;
    public static final int ALERT_OPTION_LOW_PRIORITY_ONCE = 6;
    public static final int ALERT_OPTION_LOW_PRIORITY_REPEAT = 7;
    public static final int ALERT_OPTION_MED_PRIORITY_ONCE = 8;
    public static final int ALERT_OPTION_MED_PRIORITY_REPEAT = 9;
    public static final int ALERT_OPTION_NO_ALERT = 0;
    public static final int ALERT_OPTION_VIBRATE_ONCE = 2;
    public static final int ALERT_OPTION_VIBRATE_REPEAT = 3;
    public static final int ALERT_OPTION_VISUAL_ONCE = 4;
    public static final int ALERT_OPTION_VISUAL_REPEAT = 5;
    public static final int CATEGORY_CMAS_CHILD_ABDUCTION_EMERGENCY = 4099;
    public static final int CATEGORY_CMAS_EXTREME_THREAT = 4097;
    public static final int CATEGORY_CMAS_LAST_RESERVED_VALUE = 4351;
    public static final int CATEGORY_CMAS_PRESIDENTIAL_LEVEL_ALERT = 4096;
    public static final int CATEGORY_CMAS_SEVERE_THREAT = 4098;
    public static final int CATEGORY_CMAS_TEST_MESSAGE = 4100;
    public static final Parcelable.Creator<CdmaSmsCbProgramData> CREATOR = new Parcelable.Creator<CdmaSmsCbProgramData>() { // from class: android.telephony.cdma.CdmaSmsCbProgramData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaSmsCbProgramData createFromParcel(Parcel in) {
            return new CdmaSmsCbProgramData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaSmsCbProgramData[] newArray(int size) {
            return new CdmaSmsCbProgramData[size];
        }
    };
    public static final int OPERATION_ADD_CATEGORY = 1;
    public static final int OPERATION_CLEAR_CATEGORIES = 2;
    public static final int OPERATION_DELETE_CATEGORY = 0;
    private final int mAlertOption;
    private final int mCategory;
    private final String mCategoryName;
    private final int mLanguage;
    private final int mMaxMessages;
    private final int mOperation;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Category {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Operation {
    }

    public CdmaSmsCbProgramData(int operation, int category, int language, int maxMessages, int alertOption, String categoryName) {
        this.mOperation = operation;
        this.mCategory = category;
        this.mLanguage = language;
        this.mMaxMessages = maxMessages;
        this.mAlertOption = alertOption;
        this.mCategoryName = categoryName;
    }

    CdmaSmsCbProgramData(Parcel in) {
        this.mOperation = in.readInt();
        this.mCategory = in.readInt();
        this.mLanguage = in.readInt();
        this.mMaxMessages = in.readInt();
        this.mAlertOption = in.readInt();
        this.mCategoryName = in.readString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mOperation);
        dest.writeInt(this.mCategory);
        dest.writeInt(this.mLanguage);
        dest.writeInt(this.mMaxMessages);
        dest.writeInt(this.mAlertOption);
        dest.writeString(this.mCategoryName);
    }

    public int getOperation() {
        return this.mOperation;
    }

    public int getCategory() {
        return this.mCategory;
    }

    public int getLanguage() {
        return this.mLanguage;
    }

    public int getMaxMessages() {
        return this.mMaxMessages;
    }

    public int getAlertOption() {
        return this.mAlertOption;
    }

    public String getCategoryName() {
        return this.mCategoryName;
    }

    public String toString() {
        return "CdmaSmsCbProgramData{operation=" + this.mOperation + ", category=" + this.mCategory + ", language=" + this.mLanguage + ", max messages=" + this.mMaxMessages + ", alert option=" + this.mAlertOption + ", category name=" + this.mCategoryName + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
