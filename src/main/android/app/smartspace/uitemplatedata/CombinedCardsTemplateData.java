package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class CombinedCardsTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<CombinedCardsTemplateData> CREATOR = new Parcelable.Creator<CombinedCardsTemplateData>() { // from class: android.app.smartspace.uitemplatedata.CombinedCardsTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CombinedCardsTemplateData createFromParcel(Parcel in) {
            return new CombinedCardsTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CombinedCardsTemplateData[] newArray(int size) {
            return new CombinedCardsTemplateData[size];
        }
    };
    private final List<BaseTemplateData> mCombinedCardDataList;

    CombinedCardsTemplateData(Parcel in) {
        super(in);
        this.mCombinedCardDataList = in.createTypedArrayList(BaseTemplateData.CREATOR);
    }

    private CombinedCardsTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, List<BaseTemplateData> combinedCardDataList) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mCombinedCardDataList = combinedCardDataList;
    }

    public List<BaseTemplateData> getCombinedCardDataList() {
        return this.mCombinedCardDataList;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedList(this.mCombinedCardDataList);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof CombinedCardsTemplateData) && super.equals(o)) {
            CombinedCardsTemplateData that = (CombinedCardsTemplateData) o;
            return this.mCombinedCardDataList.equals(that.mCombinedCardDataList);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mCombinedCardDataList);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceCombinedCardsUiTemplateData{mCombinedCardDataList=" + this.mCombinedCardDataList + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private final List<BaseTemplateData> mCombinedCardDataList;

        public Builder(List<BaseTemplateData> combinedCardDataList) {
            super(6);
            this.mCombinedCardDataList = (List) Objects.requireNonNull(combinedCardDataList);
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public CombinedCardsTemplateData build() {
            if (this.mCombinedCardDataList == null) {
                throw new IllegalStateException("Please assign a value to all @NonNull args.");
            }
            return new CombinedCardsTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mCombinedCardDataList);
        }
    }
}
