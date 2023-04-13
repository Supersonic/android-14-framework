package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public class BaseTemplateData implements Parcelable {
    public static final Parcelable.Creator<BaseTemplateData> CREATOR = new Parcelable.Creator<BaseTemplateData>() { // from class: android.app.smartspace.uitemplatedata.BaseTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BaseTemplateData createFromParcel(Parcel in) {
            return new BaseTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BaseTemplateData[] newArray(int size) {
            return new BaseTemplateData[size];
        }
    };
    private final int mLayoutWeight;
    private final SubItemInfo mPrimaryItem;
    private final SubItemInfo mSubtitleItem;
    private final SubItemInfo mSubtitleSupplementalItem;
    private final SubItemInfo mSupplementalAlarmItem;
    private final SubItemInfo mSupplementalLineItem;
    private final int mTemplateType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseTemplateData(Parcel in) {
        this.mTemplateType = in.readInt();
        this.mPrimaryItem = (SubItemInfo) in.readTypedObject(SubItemInfo.CREATOR);
        this.mSubtitleItem = (SubItemInfo) in.readTypedObject(SubItemInfo.CREATOR);
        this.mSubtitleSupplementalItem = (SubItemInfo) in.readTypedObject(SubItemInfo.CREATOR);
        this.mSupplementalLineItem = (SubItemInfo) in.readTypedObject(SubItemInfo.CREATOR);
        this.mSupplementalAlarmItem = (SubItemInfo) in.readTypedObject(SubItemInfo.CREATOR);
        this.mLayoutWeight = in.readInt();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseTemplateData(int templateType, SubItemInfo primaryItem, SubItemInfo subtitleItem, SubItemInfo subtitleSupplementalItem, SubItemInfo supplementalLineItem, SubItemInfo supplementalAlarmItem, int layoutWeight) {
        this.mTemplateType = templateType;
        this.mPrimaryItem = primaryItem;
        this.mSubtitleItem = subtitleItem;
        this.mSubtitleSupplementalItem = subtitleSupplementalItem;
        this.mSupplementalLineItem = supplementalLineItem;
        this.mSupplementalAlarmItem = supplementalAlarmItem;
        this.mLayoutWeight = layoutWeight;
    }

    public int getTemplateType() {
        return this.mTemplateType;
    }

    public SubItemInfo getPrimaryItem() {
        return this.mPrimaryItem;
    }

    public SubItemInfo getSubtitleItem() {
        return this.mSubtitleItem;
    }

    public SubItemInfo getSubtitleSupplementalItem() {
        return this.mSubtitleSupplementalItem;
    }

    public SubItemInfo getSupplementalLineItem() {
        return this.mSupplementalLineItem;
    }

    public SubItemInfo getSupplementalAlarmItem() {
        return this.mSupplementalAlarmItem;
    }

    public int getLayoutWeight() {
        return this.mLayoutWeight;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mTemplateType);
        out.writeTypedObject(this.mPrimaryItem, flags);
        out.writeTypedObject(this.mSubtitleItem, flags);
        out.writeTypedObject(this.mSubtitleSupplementalItem, flags);
        out.writeTypedObject(this.mSupplementalLineItem, flags);
        out.writeTypedObject(this.mSupplementalAlarmItem, flags);
        out.writeInt(this.mLayoutWeight);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseTemplateData) {
            BaseTemplateData that = (BaseTemplateData) o;
            return this.mTemplateType == that.mTemplateType && this.mLayoutWeight == that.mLayoutWeight && Objects.equals(this.mPrimaryItem, that.mPrimaryItem) && Objects.equals(this.mSubtitleItem, that.mSubtitleItem) && Objects.equals(this.mSubtitleSupplementalItem, that.mSubtitleSupplementalItem) && Objects.equals(this.mSupplementalLineItem, that.mSupplementalLineItem) && Objects.equals(this.mSupplementalAlarmItem, that.mSupplementalAlarmItem);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTemplateType), this.mPrimaryItem, this.mSubtitleItem, this.mSubtitleSupplementalItem, this.mSupplementalLineItem, this.mSupplementalAlarmItem, Integer.valueOf(this.mLayoutWeight));
    }

    public String toString() {
        return "BaseTemplateData{mTemplateType=" + this.mTemplateType + ", mPrimaryItem=" + this.mPrimaryItem + ", mSubtitleItem=" + this.mSubtitleItem + ", mSubtitleSupplementalItem=" + this.mSubtitleSupplementalItem + ", mSupplementalLineItem=" + this.mSupplementalLineItem + ", mSupplementalAlarmItem=" + this.mSupplementalAlarmItem + ", mLayoutWeight=" + this.mLayoutWeight + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static class Builder {
        private int mLayoutWeight = 0;
        private SubItemInfo mPrimaryItem;
        private SubItemInfo mSubtitleItem;
        private SubItemInfo mSubtitleSupplementalItem;
        private SubItemInfo mSupplementalAlarmItem;
        private SubItemInfo mSupplementalLineItem;
        private final int mTemplateType;

        public Builder(int templateType) {
            this.mTemplateType = templateType;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getTemplateType() {
            return this.mTemplateType;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubItemInfo getPrimaryItem() {
            return this.mPrimaryItem;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubItemInfo getSubtitleItem() {
            return this.mSubtitleItem;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubItemInfo getSubtitleSupplemtnalItem() {
            return this.mSubtitleSupplementalItem;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubItemInfo getSupplementalLineItem() {
            return this.mSupplementalLineItem;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubItemInfo getSupplementalAlarmItem() {
            return this.mSupplementalAlarmItem;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getLayoutWeight() {
            return this.mLayoutWeight;
        }

        public Builder setPrimaryItem(SubItemInfo primaryItem) {
            this.mPrimaryItem = primaryItem;
            return this;
        }

        public Builder setSubtitleItem(SubItemInfo subtitleItem) {
            this.mSubtitleItem = subtitleItem;
            return this;
        }

        public Builder setSubtitleSupplementalItem(SubItemInfo subtitleSupplementalItem) {
            this.mSubtitleSupplementalItem = subtitleSupplementalItem;
            return this;
        }

        public Builder setSupplementalLineItem(SubItemInfo supplementalLineItem) {
            this.mSupplementalLineItem = supplementalLineItem;
            return this;
        }

        public Builder setSupplementalAlarmItem(SubItemInfo supplementalAlarmItem) {
            this.mSupplementalAlarmItem = supplementalAlarmItem;
            return this;
        }

        public Builder setLayoutWeight(int layoutWeight) {
            this.mLayoutWeight = layoutWeight;
            return this;
        }

        public BaseTemplateData build() {
            return new BaseTemplateData(this.mTemplateType, this.mPrimaryItem, this.mSubtitleItem, this.mSubtitleSupplementalItem, this.mSupplementalLineItem, this.mSupplementalAlarmItem, this.mLayoutWeight);
        }
    }

    /* loaded from: classes.dex */
    public static final class SubItemInfo implements Parcelable {
        public static final Parcelable.Creator<SubItemInfo> CREATOR = new Parcelable.Creator<SubItemInfo>() { // from class: android.app.smartspace.uitemplatedata.BaseTemplateData.SubItemInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SubItemInfo createFromParcel(Parcel in) {
                return new SubItemInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SubItemInfo[] newArray(int size) {
                return new SubItemInfo[size];
            }
        };
        private final Icon mIcon;
        private final SubItemLoggingInfo mLoggingInfo;
        private final TapAction mTapAction;
        private final Text mText;

        SubItemInfo(Parcel in) {
            this.mText = (Text) in.readTypedObject(Text.CREATOR);
            this.mIcon = (Icon) in.readTypedObject(Icon.CREATOR);
            this.mTapAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
            this.mLoggingInfo = (SubItemLoggingInfo) in.readTypedObject(SubItemLoggingInfo.CREATOR);
        }

        private SubItemInfo(Text text, Icon icon, TapAction tapAction, SubItemLoggingInfo loggingInfo) {
            this.mText = text;
            this.mIcon = icon;
            this.mTapAction = tapAction;
            this.mLoggingInfo = loggingInfo;
        }

        public Text getText() {
            return this.mText;
        }

        public Icon getIcon() {
            return this.mIcon;
        }

        public TapAction getTapAction() {
            return this.mTapAction;
        }

        public SubItemLoggingInfo getLoggingInfo() {
            return this.mLoggingInfo;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeTypedObject(this.mText, flags);
            out.writeTypedObject(this.mIcon, flags);
            out.writeTypedObject(this.mTapAction, flags);
            out.writeTypedObject(this.mLoggingInfo, flags);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof SubItemInfo) {
                SubItemInfo that = (SubItemInfo) o;
                return SmartspaceUtils.isEqual(this.mText, that.mText) && Objects.equals(this.mIcon, that.mIcon) && Objects.equals(this.mTapAction, that.mTapAction) && Objects.equals(this.mLoggingInfo, that.mLoggingInfo);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mText, this.mIcon, this.mTapAction, this.mLoggingInfo);
        }

        public String toString() {
            return "SubItemInfo{mText=" + this.mText + ", mIcon=" + this.mIcon + ", mTapAction=" + this.mTapAction + ", mLoggingInfo=" + this.mLoggingInfo + '}';
        }

        @SystemApi
        /* loaded from: classes.dex */
        public static final class Builder {
            private Icon mIcon;
            private SubItemLoggingInfo mLoggingInfo;
            private TapAction mTapAction;
            private Text mText;

            public Builder setText(Text text) {
                this.mText = text;
                return this;
            }

            public Builder setIcon(Icon icon) {
                this.mIcon = icon;
                return this;
            }

            public Builder setTapAction(TapAction tapAction) {
                this.mTapAction = tapAction;
                return this;
            }

            public Builder setLoggingInfo(SubItemLoggingInfo loggingInfo) {
                this.mLoggingInfo = loggingInfo;
                return this;
            }

            public SubItemInfo build() {
                if (SmartspaceUtils.isEmpty(this.mText) && this.mIcon == null && this.mTapAction == null && this.mLoggingInfo == null) {
                    throw new IllegalStateException("SubItem data is empty");
                }
                return new SubItemInfo(this.mText, this.mIcon, this.mTapAction, this.mLoggingInfo);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class SubItemLoggingInfo implements Parcelable {
        public static final Parcelable.Creator<SubItemLoggingInfo> CREATOR = new Parcelable.Creator<SubItemLoggingInfo>() { // from class: android.app.smartspace.uitemplatedata.BaseTemplateData.SubItemLoggingInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SubItemLoggingInfo createFromParcel(Parcel in) {
                return new SubItemLoggingInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SubItemLoggingInfo[] newArray(int size) {
                return new SubItemLoggingInfo[size];
            }
        };
        private final int mFeatureType;
        private final int mInstanceId;
        private final CharSequence mPackageName;

        SubItemLoggingInfo(Parcel in) {
            this.mInstanceId = in.readInt();
            this.mFeatureType = in.readInt();
            this.mPackageName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        }

        private SubItemLoggingInfo(int instanceId, int featureType, CharSequence packageName) {
            this.mInstanceId = instanceId;
            this.mFeatureType = featureType;
            this.mPackageName = packageName;
        }

        public int getInstanceId() {
            return this.mInstanceId;
        }

        public int getFeatureType() {
            return this.mFeatureType;
        }

        public CharSequence getPackageName() {
            return this.mPackageName;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mInstanceId);
            out.writeInt(this.mFeatureType);
            TextUtils.writeToParcel(this.mPackageName, out, flags);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof SubItemLoggingInfo) {
                SubItemLoggingInfo that = (SubItemLoggingInfo) o;
                return this.mInstanceId == that.mInstanceId && this.mFeatureType == that.mFeatureType && SmartspaceUtils.isEqual(this.mPackageName, that.mPackageName);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mInstanceId), Integer.valueOf(this.mFeatureType), this.mPackageName);
        }

        public String toString() {
            return "SubItemLoggingInfo{mInstanceId=" + this.mInstanceId + ", mFeatureType=" + this.mFeatureType + ", mPackageName=" + ((Object) this.mPackageName) + '}';
        }

        @SystemApi
        /* loaded from: classes.dex */
        public static final class Builder {
            private final int mFeatureType;
            private final int mInstanceId;
            private CharSequence mPackageName;

            public Builder(int instanceId, int featureType) {
                this.mInstanceId = instanceId;
                this.mFeatureType = featureType;
            }

            public Builder setPackageName(CharSequence packageName) {
                this.mPackageName = packageName;
                return this;
            }

            public SubItemLoggingInfo build() {
                return new SubItemLoggingInfo(this.mInstanceId, this.mFeatureType, this.mPackageName);
            }
        }
    }
}
