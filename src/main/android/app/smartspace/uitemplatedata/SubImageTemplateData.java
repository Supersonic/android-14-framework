package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SubImageTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<SubImageTemplateData> CREATOR = new Parcelable.Creator<SubImageTemplateData>() { // from class: android.app.smartspace.uitemplatedata.SubImageTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubImageTemplateData createFromParcel(Parcel in) {
            return new SubImageTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubImageTemplateData[] newArray(int size) {
            return new SubImageTemplateData[size];
        }
    };
    private final TapAction mSubImageAction;
    private final List<Text> mSubImageTexts;
    private final List<Icon> mSubImages;

    SubImageTemplateData(Parcel in) {
        super(in);
        this.mSubImageTexts = in.createTypedArrayList(Text.CREATOR);
        this.mSubImages = in.createTypedArrayList(Icon.CREATOR);
        this.mSubImageAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
    }

    private SubImageTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, List<Text> subImageTexts, List<Icon> subImages, TapAction subImageAction) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mSubImageTexts = subImageTexts;
        this.mSubImages = subImages;
        this.mSubImageAction = subImageAction;
    }

    public List<Text> getSubImageTexts() {
        return this.mSubImageTexts;
    }

    public List<Icon> getSubImages() {
        return this.mSubImages;
    }

    public TapAction getSubImageAction() {
        return this.mSubImageAction;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedList(this.mSubImageTexts);
        out.writeTypedList(this.mSubImages);
        out.writeTypedObject(this.mSubImageAction, flags);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof SubImageTemplateData) && super.equals(o)) {
            SubImageTemplateData that = (SubImageTemplateData) o;
            return Objects.equals(this.mSubImageTexts, that.mSubImageTexts) && Objects.equals(this.mSubImages, that.mSubImages) && Objects.equals(this.mSubImageAction, that.mSubImageAction);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mSubImageTexts, this.mSubImages, this.mSubImageAction);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceSubImageUiTemplateData{mSubImageTexts=" + this.mSubImageTexts + ", mSubImages=" + this.mSubImages + ", mSubImageAction=" + this.mSubImageAction + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private TapAction mSubImageAction;
        private final List<Text> mSubImageTexts;
        private final List<Icon> mSubImages;

        public Builder(List<Text> subImageTexts, List<Icon> subImages) {
            super(2);
            this.mSubImageTexts = (List) Objects.requireNonNull(subImageTexts);
            this.mSubImages = (List) Objects.requireNonNull(subImages);
        }

        public Builder setSubImageAction(TapAction subImageAction) {
            this.mSubImageAction = subImageAction;
            return this;
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public SubImageTemplateData build() {
            return new SubImageTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mSubImageTexts, this.mSubImages, this.mSubImageAction);
        }
    }
}
