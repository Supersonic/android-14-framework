package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SubListTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<SubListTemplateData> CREATOR = new Parcelable.Creator<SubListTemplateData>() { // from class: android.app.smartspace.uitemplatedata.SubListTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubListTemplateData createFromParcel(Parcel in) {
            return new SubListTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubListTemplateData[] newArray(int size) {
            return new SubListTemplateData[size];
        }
    };
    private final TapAction mSubListAction;
    private final Icon mSubListIcon;
    private final List<Text> mSubListTexts;

    SubListTemplateData(Parcel in) {
        super(in);
        this.mSubListIcon = (Icon) in.readTypedObject(Icon.CREATOR);
        this.mSubListTexts = in.createTypedArrayList(Text.CREATOR);
        this.mSubListAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
    }

    private SubListTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, Icon subListIcon, List<Text> subListTexts, TapAction subListAction) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mSubListIcon = subListIcon;
        this.mSubListTexts = subListTexts;
        this.mSubListAction = subListAction;
    }

    public Icon getSubListIcon() {
        return this.mSubListIcon;
    }

    public List<Text> getSubListTexts() {
        return this.mSubListTexts;
    }

    public TapAction getSubListAction() {
        return this.mSubListAction;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedObject(this.mSubListIcon, flags);
        out.writeTypedList(this.mSubListTexts);
        out.writeTypedObject(this.mSubListAction, flags);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof SubListTemplateData) && super.equals(o)) {
            SubListTemplateData that = (SubListTemplateData) o;
            return Objects.equals(this.mSubListIcon, that.mSubListIcon) && Objects.equals(this.mSubListTexts, that.mSubListTexts) && Objects.equals(this.mSubListAction, that.mSubListAction);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mSubListIcon, this.mSubListTexts, this.mSubListAction);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceSubListUiTemplateData{mSubListIcon=" + this.mSubListIcon + ", mSubListTexts=" + this.mSubListTexts + ", mSubListAction=" + this.mSubListAction + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private TapAction mSubListAction;
        private Icon mSubListIcon;
        private final List<Text> mSubListTexts;

        public Builder(List<Text> subListTexts) {
            super(3);
            this.mSubListTexts = (List) Objects.requireNonNull(subListTexts);
        }

        public Builder setSubListIcon(Icon subListIcon) {
            this.mSubListIcon = subListIcon;
            return this;
        }

        public Builder setSubListAction(TapAction subListAction) {
            this.mSubListAction = subListAction;
            return this;
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public SubListTemplateData build() {
            return new SubListTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mSubListIcon, this.mSubListTexts, this.mSubListAction);
        }
    }
}
