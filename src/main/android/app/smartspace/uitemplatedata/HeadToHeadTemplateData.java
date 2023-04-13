package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class HeadToHeadTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<HeadToHeadTemplateData> CREATOR = new Parcelable.Creator<HeadToHeadTemplateData>() { // from class: android.app.smartspace.uitemplatedata.HeadToHeadTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HeadToHeadTemplateData createFromParcel(Parcel in) {
            return new HeadToHeadTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HeadToHeadTemplateData[] newArray(int size) {
            return new HeadToHeadTemplateData[size];
        }
    };
    private final TapAction mHeadToHeadAction;
    private final Icon mHeadToHeadFirstCompetitorIcon;
    private final Text mHeadToHeadFirstCompetitorText;
    private final Icon mHeadToHeadSecondCompetitorIcon;
    private final Text mHeadToHeadSecondCompetitorText;
    private final Text mHeadToHeadTitle;

    HeadToHeadTemplateData(Parcel in) {
        super(in);
        this.mHeadToHeadTitle = (Text) in.readTypedObject(Text.CREATOR);
        this.mHeadToHeadFirstCompetitorIcon = (Icon) in.readTypedObject(Icon.CREATOR);
        this.mHeadToHeadSecondCompetitorIcon = (Icon) in.readTypedObject(Icon.CREATOR);
        this.mHeadToHeadFirstCompetitorText = (Text) in.readTypedObject(Text.CREATOR);
        this.mHeadToHeadSecondCompetitorText = (Text) in.readTypedObject(Text.CREATOR);
        this.mHeadToHeadAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
    }

    private HeadToHeadTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, Text headToHeadTitle, Icon headToHeadFirstCompetitorIcon, Icon headToHeadSecondCompetitorIcon, Text headToHeadFirstCompetitorText, Text headToHeadSecondCompetitorText, TapAction headToHeadAction) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mHeadToHeadTitle = headToHeadTitle;
        this.mHeadToHeadFirstCompetitorIcon = headToHeadFirstCompetitorIcon;
        this.mHeadToHeadSecondCompetitorIcon = headToHeadSecondCompetitorIcon;
        this.mHeadToHeadFirstCompetitorText = headToHeadFirstCompetitorText;
        this.mHeadToHeadSecondCompetitorText = headToHeadSecondCompetitorText;
        this.mHeadToHeadAction = headToHeadAction;
    }

    public Text getHeadToHeadTitle() {
        return this.mHeadToHeadTitle;
    }

    public Icon getHeadToHeadFirstCompetitorIcon() {
        return this.mHeadToHeadFirstCompetitorIcon;
    }

    public Icon getHeadToHeadSecondCompetitorIcon() {
        return this.mHeadToHeadSecondCompetitorIcon;
    }

    public Text getHeadToHeadFirstCompetitorText() {
        return this.mHeadToHeadFirstCompetitorText;
    }

    public Text getHeadToHeadSecondCompetitorText() {
        return this.mHeadToHeadSecondCompetitorText;
    }

    public TapAction getHeadToHeadAction() {
        return this.mHeadToHeadAction;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedObject(this.mHeadToHeadTitle, flags);
        out.writeTypedObject(this.mHeadToHeadFirstCompetitorIcon, flags);
        out.writeTypedObject(this.mHeadToHeadSecondCompetitorIcon, flags);
        out.writeTypedObject(this.mHeadToHeadFirstCompetitorText, flags);
        out.writeTypedObject(this.mHeadToHeadSecondCompetitorText, flags);
        out.writeTypedObject(this.mHeadToHeadAction, flags);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof HeadToHeadTemplateData) && super.equals(o)) {
            HeadToHeadTemplateData that = (HeadToHeadTemplateData) o;
            return SmartspaceUtils.isEqual(this.mHeadToHeadTitle, that.mHeadToHeadTitle) && Objects.equals(this.mHeadToHeadFirstCompetitorIcon, that.mHeadToHeadFirstCompetitorIcon) && Objects.equals(this.mHeadToHeadSecondCompetitorIcon, that.mHeadToHeadSecondCompetitorIcon) && SmartspaceUtils.isEqual(this.mHeadToHeadFirstCompetitorText, that.mHeadToHeadFirstCompetitorText) && SmartspaceUtils.isEqual(this.mHeadToHeadSecondCompetitorText, that.mHeadToHeadSecondCompetitorText) && Objects.equals(this.mHeadToHeadAction, that.mHeadToHeadAction);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mHeadToHeadTitle, this.mHeadToHeadFirstCompetitorIcon, this.mHeadToHeadSecondCompetitorIcon, this.mHeadToHeadFirstCompetitorText, this.mHeadToHeadSecondCompetitorText, this.mHeadToHeadAction);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceHeadToHeadUiTemplateData{mH2HTitle=" + this.mHeadToHeadTitle + ", mH2HFirstCompetitorIcon=" + this.mHeadToHeadFirstCompetitorIcon + ", mH2HSecondCompetitorIcon=" + this.mHeadToHeadSecondCompetitorIcon + ", mH2HFirstCompetitorText=" + this.mHeadToHeadFirstCompetitorText + ", mH2HSecondCompetitorText=" + this.mHeadToHeadSecondCompetitorText + ", mH2HAction=" + this.mHeadToHeadAction + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private TapAction mHeadToHeadAction;
        private Icon mHeadToHeadFirstCompetitorIcon;
        private Text mHeadToHeadFirstCompetitorText;
        private Icon mHeadToHeadSecondCompetitorIcon;
        private Text mHeadToHeadSecondCompetitorText;
        private Text mHeadToHeadTitle;

        public Builder() {
            super(5);
        }

        public Builder setHeadToHeadTitle(Text headToHeadTitle) {
            this.mHeadToHeadTitle = headToHeadTitle;
            return this;
        }

        public Builder setHeadToHeadFirstCompetitorIcon(Icon headToHeadFirstCompetitorIcon) {
            this.mHeadToHeadFirstCompetitorIcon = headToHeadFirstCompetitorIcon;
            return this;
        }

        public Builder setHeadToHeadSecondCompetitorIcon(Icon headToHeadSecondCompetitorIcon) {
            this.mHeadToHeadSecondCompetitorIcon = headToHeadSecondCompetitorIcon;
            return this;
        }

        public Builder setHeadToHeadFirstCompetitorText(Text headToHeadFirstCompetitorText) {
            this.mHeadToHeadFirstCompetitorText = headToHeadFirstCompetitorText;
            return this;
        }

        public Builder setHeadToHeadSecondCompetitorText(Text headToHeadSecondCompetitorText) {
            this.mHeadToHeadSecondCompetitorText = headToHeadSecondCompetitorText;
            return this;
        }

        public Builder setHeadToHeadAction(TapAction headToHeadAction) {
            this.mHeadToHeadAction = headToHeadAction;
            return this;
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public HeadToHeadTemplateData build() {
            return new HeadToHeadTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mHeadToHeadTitle, this.mHeadToHeadFirstCompetitorIcon, this.mHeadToHeadSecondCompetitorIcon, this.mHeadToHeadFirstCompetitorText, this.mHeadToHeadSecondCompetitorText, this.mHeadToHeadAction);
        }
    }
}
