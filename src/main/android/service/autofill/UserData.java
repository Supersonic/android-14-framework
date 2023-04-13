package android.service.autofill;

import android.app.ActivityThread;
import android.content.ContentResolver;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.autofill.Helper;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class UserData implements FieldClassificationUserData, Parcelable {
    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() { // from class: android.service.autofill.UserData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserData createFromParcel(Parcel parcel) {
            String id = parcel.readString();
            String[] categoryIds = parcel.readStringArray();
            String[] values = parcel.readStringArray();
            String defaultAlgorithm = parcel.readString();
            Bundle defaultArgs = parcel.readBundle();
            ArrayMap<String, String> categoryAlgorithms = new ArrayMap<>();
            parcel.readMap(categoryAlgorithms, String.class.getClassLoader());
            ArrayMap<String, Bundle> categoryArgs = new ArrayMap<>();
            parcel.readMap(categoryArgs, Bundle.class.getClassLoader());
            Builder builder = new Builder(id, values[0], categoryIds[0]).setFieldClassificationAlgorithm(defaultAlgorithm, defaultArgs);
            for (int i = 1; i < categoryIds.length; i++) {
                builder.add(values[i], categoryIds[i]);
            }
            int size = categoryAlgorithms.size();
            if (size > 0) {
                for (int i2 = 0; i2 < size; i2++) {
                    String categoryId = categoryAlgorithms.keyAt(i2);
                    builder.setFieldClassificationAlgorithmForCategory(categoryId, categoryAlgorithms.valueAt(i2), categoryArgs.get(categoryId));
                }
            }
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
    private static final int DEFAULT_MAX_CATEGORY_COUNT = 10;
    private static final int DEFAULT_MAX_FIELD_CLASSIFICATION_IDS_SIZE = 10;
    private static final int DEFAULT_MAX_USER_DATA_SIZE = 50;
    private static final int DEFAULT_MAX_VALUE_LENGTH = 100;
    private static final int DEFAULT_MIN_VALUE_LENGTH = 3;
    private static final String TAG = "UserData";
    private final ArrayMap<String, String> mCategoryAlgorithms;
    private final ArrayMap<String, Bundle> mCategoryArgs;
    private final String[] mCategoryIds;
    private final String mDefaultAlgorithm;
    private final Bundle mDefaultArgs;
    private final String mId;
    private final String[] mValues;

    private UserData(Builder builder) {
        this.mId = builder.mId;
        String[] strArr = new String[builder.mCategoryIds.size()];
        this.mCategoryIds = strArr;
        builder.mCategoryIds.toArray(strArr);
        String[] strArr2 = new String[builder.mValues.size()];
        this.mValues = strArr2;
        builder.mValues.toArray(strArr2);
        builder.mValues.toArray(strArr2);
        this.mDefaultAlgorithm = builder.mDefaultAlgorithm;
        this.mDefaultArgs = builder.mDefaultArgs;
        this.mCategoryAlgorithms = builder.mCategoryAlgorithms;
        this.mCategoryArgs = builder.mCategoryArgs;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String getFieldClassificationAlgorithm() {
        return this.mDefaultAlgorithm;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public Bundle getDefaultFieldClassificationArgs() {
        return this.mDefaultArgs;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String getFieldClassificationAlgorithmForCategory(String categoryId) {
        Objects.requireNonNull(categoryId);
        ArrayMap<String, String> arrayMap = this.mCategoryAlgorithms;
        if (arrayMap == null || !arrayMap.containsKey(categoryId)) {
            return null;
        }
        return this.mCategoryAlgorithms.get(categoryId);
    }

    public String getId() {
        return this.mId;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String[] getCategoryIds() {
        return this.mCategoryIds;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String[] getValues() {
        return this.mValues;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public ArrayMap<String, String> getFieldClassificationAlgorithms() {
        return this.mCategoryAlgorithms;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public ArrayMap<String, Bundle> getFieldClassificationArgs() {
        return this.mCategoryArgs;
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("id: ");
        pw.print(this.mId);
        pw.print(prefix);
        pw.print("Default Algorithm: ");
        pw.print(this.mDefaultAlgorithm);
        pw.print(prefix);
        pw.print("Default Args");
        pw.print(this.mDefaultArgs);
        ArrayMap<String, String> arrayMap = this.mCategoryAlgorithms;
        if (arrayMap != null && arrayMap.size() > 0) {
            pw.print(prefix);
            pw.print("Algorithms per category: ");
            for (int i = 0; i < this.mCategoryAlgorithms.size(); i++) {
                pw.print(prefix);
                pw.print(prefix);
                pw.print(this.mCategoryAlgorithms.keyAt(i));
                pw.print(": ");
                pw.println(Helper.getRedacted(this.mCategoryAlgorithms.valueAt(i)));
                pw.print("args=");
                pw.print(this.mCategoryArgs.get(this.mCategoryAlgorithms.keyAt(i)));
            }
        }
        pw.print(prefix);
        pw.print("Field ids size: ");
        pw.println(this.mCategoryIds.length);
        for (int i2 = 0; i2 < this.mCategoryIds.length; i2++) {
            pw.print(prefix);
            pw.print(prefix);
            pw.print(i2);
            pw.print(": ");
            pw.println(Helper.getRedacted(this.mCategoryIds[i2]));
        }
        pw.print(prefix);
        pw.print("Values size: ");
        pw.println(this.mValues.length);
        for (int i3 = 0; i3 < this.mValues.length; i3++) {
            pw.print(prefix);
            pw.print(prefix);
            pw.print(i3);
            pw.print(": ");
            pw.println(Helper.getRedacted(this.mValues[i3]));
        }
    }

    public static void dumpConstraints(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("maxUserDataSize: ");
        pw.println(getMaxUserDataSize());
        pw.print(prefix);
        pw.print("maxFieldClassificationIdsSize: ");
        pw.println(getMaxFieldClassificationIdsSize());
        pw.print(prefix);
        pw.print("maxCategoryCount: ");
        pw.println(getMaxCategoryCount());
        pw.print(prefix);
        pw.print("minValueLength: ");
        pw.println(getMinValueLength());
        pw.print(prefix);
        pw.print("maxValueLength: ");
        pw.println(getMaxValueLength());
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private ArrayMap<String, String> mCategoryAlgorithms;
        private ArrayMap<String, Bundle> mCategoryArgs;
        private final ArrayList<String> mCategoryIds;
        private String mDefaultAlgorithm;
        private Bundle mDefaultArgs;
        private boolean mDestroyed;
        private final String mId;
        private final ArraySet<String> mUniqueCategoryIds;
        private final ArraySet<String> mUniqueValueCategoryPairs;
        private final ArrayList<String> mValues;

        public Builder(String id, String value, String categoryId) {
            this.mId = checkNotEmpty("id", id);
            checkNotEmpty("categoryId", categoryId);
            checkValidValue(value);
            int maxUserDataSize = UserData.getMaxUserDataSize();
            this.mCategoryIds = new ArrayList<>(maxUserDataSize);
            this.mValues = new ArrayList<>(maxUserDataSize);
            this.mUniqueValueCategoryPairs = new ArraySet<>(maxUserDataSize);
            this.mUniqueCategoryIds = new ArraySet<>(UserData.getMaxCategoryCount());
            addMapping(value, categoryId);
        }

        public Builder setFieldClassificationAlgorithm(String name, Bundle args) {
            throwIfDestroyed();
            this.mDefaultAlgorithm = name;
            this.mDefaultArgs = args;
            return this;
        }

        public Builder setFieldClassificationAlgorithmForCategory(String categoryId, String name, Bundle args) {
            throwIfDestroyed();
            Objects.requireNonNull(categoryId);
            if (this.mCategoryAlgorithms == null) {
                this.mCategoryAlgorithms = new ArrayMap<>(UserData.getMaxCategoryCount());
            }
            if (this.mCategoryArgs == null) {
                this.mCategoryArgs = new ArrayMap<>(UserData.getMaxCategoryCount());
            }
            this.mCategoryAlgorithms.put(categoryId, name);
            this.mCategoryArgs.put(categoryId, args);
            return this;
        }

        public Builder add(String value, String categoryId) {
            throwIfDestroyed();
            checkNotEmpty("categoryId", categoryId);
            checkValidValue(value);
            if (!this.mUniqueCategoryIds.contains(categoryId)) {
                Preconditions.checkState(this.mUniqueCategoryIds.size() < UserData.getMaxCategoryCount(), "already added %d unique category ids", Integer.valueOf(this.mUniqueCategoryIds.size()));
            }
            Preconditions.checkState(this.mValues.size() < UserData.getMaxUserDataSize(), "already added %d elements", Integer.valueOf(this.mValues.size()));
            addMapping(value, categoryId);
            return this;
        }

        private void addMapping(String value, String categoryId) {
            String pair = value + ":" + categoryId;
            if (this.mUniqueValueCategoryPairs.contains(pair)) {
                Log.m104w(UserData.TAG, "Ignoring entry with same value / category");
                return;
            }
            this.mCategoryIds.add(categoryId);
            this.mValues.add(value);
            this.mUniqueCategoryIds.add(categoryId);
            this.mUniqueValueCategoryPairs.add(pair);
        }

        private String checkNotEmpty(String name, String value) {
            Objects.requireNonNull(value);
            Preconditions.checkArgument(!TextUtils.isEmpty(value), "%s cannot be empty", name);
            return value;
        }

        private void checkValidValue(String value) {
            Objects.requireNonNull(value);
            int length = value.length();
            Preconditions.checkArgumentInRange(length, UserData.getMinValueLength(), UserData.getMaxValueLength(), "value length (" + length + NavigationBarInflaterView.KEY_CODE_END);
        }

        public UserData build() {
            throwIfDestroyed();
            this.mDestroyed = true;
            return new UserData(this);
        }

        private void throwIfDestroyed() {
            if (this.mDestroyed) {
                throw new IllegalStateException("Already called #build()");
            }
        }
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder builder = new StringBuilder("UserData: [id=").append(this.mId);
            builder.append(", categoryIds=");
            Helper.appendRedacted(builder, this.mCategoryIds);
            builder.append(", values=");
            Helper.appendRedacted(builder, this.mValues);
            return builder.append(NavigationBarInflaterView.SIZE_MOD_END).toString();
        }
        return super.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mId);
        parcel.writeStringArray(this.mCategoryIds);
        parcel.writeStringArray(this.mValues);
        parcel.writeString(this.mDefaultAlgorithm);
        parcel.writeBundle(this.mDefaultArgs);
        parcel.writeMap(this.mCategoryAlgorithms);
        parcel.writeMap(this.mCategoryArgs);
    }

    public static int getMaxUserDataSize() {
        return getInt(Settings.Secure.AUTOFILL_USER_DATA_MAX_USER_DATA_SIZE, 50);
    }

    public static int getMaxFieldClassificationIdsSize() {
        return getInt(Settings.Secure.AUTOFILL_USER_DATA_MAX_FIELD_CLASSIFICATION_IDS_SIZE, 10);
    }

    public static int getMaxCategoryCount() {
        return getInt(Settings.Secure.AUTOFILL_USER_DATA_MAX_CATEGORY_COUNT, 10);
    }

    public static int getMinValueLength() {
        return getInt(Settings.Secure.AUTOFILL_USER_DATA_MIN_VALUE_LENGTH, 3);
    }

    public static int getMaxValueLength() {
        return getInt(Settings.Secure.AUTOFILL_USER_DATA_MAX_VALUE_LENGTH, 100);
    }

    private static int getInt(String settings, int defaultValue) {
        ContentResolver cr = null;
        ActivityThread at = ActivityThread.currentActivityThread();
        if (at != null) {
            cr = at.getApplication().getContentResolver();
        }
        if (cr == null) {
            Log.m104w(TAG, "Could not read from " + settings + "; hardcoding " + defaultValue);
            return defaultValue;
        }
        return Settings.Secure.getInt(cr, settings, defaultValue);
    }
}
