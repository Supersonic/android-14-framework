package android.service.autofill;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.view.autofill.Helper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CompositeUserData implements FieldClassificationUserData, Parcelable {
    public static final Parcelable.Creator<CompositeUserData> CREATOR = new Parcelable.Creator<CompositeUserData>() { // from class: android.service.autofill.CompositeUserData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompositeUserData createFromParcel(Parcel parcel) {
            UserData genericUserData = (UserData) parcel.readParcelable(null, UserData.class);
            UserData packageUserData = (UserData) parcel.readParcelable(null, UserData.class);
            return new CompositeUserData(genericUserData, packageUserData);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompositeUserData[] newArray(int size) {
            return new CompositeUserData[size];
        }
    };
    private final String[] mCategories;
    private final UserData mGenericUserData;
    private final UserData mPackageUserData;
    private final String[] mValues;

    public CompositeUserData(UserData genericUserData, UserData packageUserData) {
        this.mGenericUserData = genericUserData;
        this.mPackageUserData = packageUserData;
        String[] packageCategoryIds = packageUserData.getCategoryIds();
        String[] packageValues = packageUserData.getValues();
        ArrayList<String> categoryIds = new ArrayList<>(packageCategoryIds.length);
        ArrayList<String> values = new ArrayList<>(packageValues.length);
        Collections.addAll(categoryIds, packageCategoryIds);
        Collections.addAll(values, packageValues);
        if (genericUserData != null) {
            String[] genericCategoryIds = genericUserData.getCategoryIds();
            String[] genericValues = genericUserData.getValues();
            int size = genericUserData.getCategoryIds().length;
            for (int i = 0; i < size; i++) {
                if (!categoryIds.contains(genericCategoryIds[i])) {
                    categoryIds.add(genericCategoryIds[i]);
                    values.add(genericValues[i]);
                }
            }
        }
        String[] strArr = new String[categoryIds.size()];
        this.mCategories = strArr;
        categoryIds.toArray(strArr);
        String[] strArr2 = new String[values.size()];
        this.mValues = strArr2;
        values.toArray(strArr2);
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String getFieldClassificationAlgorithm() {
        String packageDefaultAlgo = this.mPackageUserData.getFieldClassificationAlgorithm();
        if (packageDefaultAlgo != null) {
            return packageDefaultAlgo;
        }
        UserData userData = this.mGenericUserData;
        if (userData == null) {
            return null;
        }
        return userData.getFieldClassificationAlgorithm();
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public Bundle getDefaultFieldClassificationArgs() {
        Bundle packageDefaultArgs = this.mPackageUserData.getDefaultFieldClassificationArgs();
        if (packageDefaultArgs != null) {
            return packageDefaultArgs;
        }
        UserData userData = this.mGenericUserData;
        if (userData == null) {
            return null;
        }
        return userData.getDefaultFieldClassificationArgs();
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String getFieldClassificationAlgorithmForCategory(String categoryId) {
        Objects.requireNonNull(categoryId);
        ArrayMap<String, String> categoryAlgorithms = getFieldClassificationAlgorithms();
        if (categoryAlgorithms == null || !categoryAlgorithms.containsKey(categoryId)) {
            return null;
        }
        return categoryAlgorithms.get(categoryId);
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public ArrayMap<String, String> getFieldClassificationAlgorithms() {
        ArrayMap<String, String> packageAlgos = this.mPackageUserData.getFieldClassificationAlgorithms();
        UserData userData = this.mGenericUserData;
        ArrayMap<String, String> genericAlgos = userData == null ? null : userData.getFieldClassificationAlgorithms();
        ArrayMap<String, String> categoryAlgorithms = null;
        if (packageAlgos != null || genericAlgos != null) {
            categoryAlgorithms = new ArrayMap<>();
            if (genericAlgos != null) {
                categoryAlgorithms.putAll((ArrayMap<? extends String, ? extends String>) genericAlgos);
            }
            if (packageAlgos != null) {
                categoryAlgorithms.putAll((ArrayMap<? extends String, ? extends String>) packageAlgos);
            }
        }
        return categoryAlgorithms;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public ArrayMap<String, Bundle> getFieldClassificationArgs() {
        ArrayMap<String, Bundle> packageArgs = this.mPackageUserData.getFieldClassificationArgs();
        UserData userData = this.mGenericUserData;
        ArrayMap<String, Bundle> genericArgs = userData == null ? null : userData.getFieldClassificationArgs();
        ArrayMap<String, Bundle> categoryArgs = null;
        if (packageArgs != null || genericArgs != null) {
            categoryArgs = new ArrayMap<>();
            if (genericArgs != null) {
                categoryArgs.putAll((ArrayMap<? extends String, ? extends Bundle>) genericArgs);
            }
            if (packageArgs != null) {
                categoryArgs.putAll((ArrayMap<? extends String, ? extends Bundle>) packageArgs);
            }
        }
        return categoryArgs;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String[] getCategoryIds() {
        return this.mCategories;
    }

    @Override // android.service.autofill.FieldClassificationUserData
    public String[] getValues() {
        return this.mValues;
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder builder = new StringBuilder("genericUserData=").append(this.mGenericUserData).append(", packageUserData=").append(this.mPackageUserData);
            return builder.toString();
        }
        return super.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mGenericUserData, 0);
        parcel.writeParcelable(this.mPackageUserData, 0);
    }
}
