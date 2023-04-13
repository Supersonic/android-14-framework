package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SubCardTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<SubCardTemplateData> CREATOR = new Parcelable.Creator<SubCardTemplateData>() { // from class: android.app.smartspace.uitemplatedata.SubCardTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubCardTemplateData createFromParcel(Parcel in) {
            return new SubCardTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubCardTemplateData[] newArray(int size) {
            return new SubCardTemplateData[size];
        }
    };
    private final TapAction mSubCardAction;
    private final Icon mSubCardIcon;
    private final Text mSubCardText;

    SubCardTemplateData(Parcel in) {
        super(in);
        this.mSubCardIcon = (Icon) in.readTypedObject(Icon.CREATOR);
        this.mSubCardText = (Text) in.readTypedObject(Text.CREATOR);
        this.mSubCardAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
    }

    private SubCardTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, Icon subCardIcon, Text subCardText, TapAction subCardAction) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mSubCardIcon = subCardIcon;
        this.mSubCardText = subCardText;
        this.mSubCardAction = subCardAction;
    }

    public Icon getSubCardIcon() {
        return this.mSubCardIcon;
    }

    public Text getSubCardText() {
        return this.mSubCardText;
    }

    public TapAction getSubCardAction() {
        return this.mSubCardAction;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedObject(this.mSubCardIcon, flags);
        out.writeTypedObject(this.mSubCardText, flags);
        out.writeTypedObject(this.mSubCardAction, flags);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof SubCardTemplateData) && super.equals(o)) {
            SubCardTemplateData that = (SubCardTemplateData) o;
            return this.mSubCardIcon.equals(that.mSubCardIcon) && SmartspaceUtils.isEqual(this.mSubCardText, that.mSubCardText) && Objects.equals(this.mSubCardAction, that.mSubCardAction);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mSubCardIcon, this.mSubCardText, this.mSubCardAction);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceSubCardUiTemplateData{mSubCardIcon=" + this.mSubCardIcon + ", mSubCardText=" + this.mSubCardText + ", mSubCardAction=" + this.mSubCardAction + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private TapAction mSubCardAction;
        private final Icon mSubCardIcon;
        private Text mSubCardText;

        public Builder(Icon subCardIcon) {
            super(7);
            this.mSubCardIcon = (Icon) Objects.requireNonNull(subCardIcon);
        }

        public Builder setSubCardText(Text subCardText) {
            this.mSubCardText = subCardText;
            return this;
        }

        public Builder setSubCardAction(TapAction subCardAction) {
            this.mSubCardAction = subCardAction;
            return this;
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public SubCardTemplateData build() {
            return new SubCardTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mSubCardIcon, this.mSubCardText, this.mSubCardAction);
        }
    }
}
