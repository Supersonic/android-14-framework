package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class CarouselTemplateData extends BaseTemplateData {
    public static final Parcelable.Creator<CarouselTemplateData> CREATOR = new Parcelable.Creator<CarouselTemplateData>() { // from class: android.app.smartspace.uitemplatedata.CarouselTemplateData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CarouselTemplateData createFromParcel(Parcel in) {
            return new CarouselTemplateData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CarouselTemplateData[] newArray(int size) {
            return new CarouselTemplateData[size];
        }
    };
    private final TapAction mCarouselAction;
    private final List<CarouselItem> mCarouselItems;

    CarouselTemplateData(Parcel in) {
        super(in);
        this.mCarouselItems = in.createTypedArrayList(CarouselItem.CREATOR);
        this.mCarouselAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
    }

    private CarouselTemplateData(int templateType, BaseTemplateData.SubItemInfo primaryItem, BaseTemplateData.SubItemInfo subtitleItem, BaseTemplateData.SubItemInfo subtitleSupplementalItem, BaseTemplateData.SubItemInfo supplementalLineItem, BaseTemplateData.SubItemInfo supplementalAlarmItem, int layoutWeight, List<CarouselItem> carouselItems, TapAction carouselAction) {
        super(templateType, primaryItem, subtitleItem, subtitleSupplementalItem, supplementalLineItem, supplementalAlarmItem, layoutWeight);
        this.mCarouselItems = carouselItems;
        this.mCarouselAction = carouselAction;
    }

    public List<CarouselItem> getCarouselItems() {
        return this.mCarouselItems;
    }

    public TapAction getCarouselAction() {
        return this.mCarouselAction;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeTypedList(this.mCarouselItems);
        out.writeTypedObject(this.mCarouselAction, flags);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof CarouselTemplateData) && super.equals(o)) {
            CarouselTemplateData that = (CarouselTemplateData) o;
            return this.mCarouselItems.equals(that.mCarouselItems) && Objects.equals(this.mCarouselAction, that.mCarouselAction);
        }
        return false;
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mCarouselItems, this.mCarouselAction);
    }

    @Override // android.app.smartspace.uitemplatedata.BaseTemplateData
    public String toString() {
        return super.toString() + " + SmartspaceCarouselUiTemplateData{mCarouselItems=" + this.mCarouselItems + ", mCarouselActions=" + this.mCarouselAction + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder extends BaseTemplateData.Builder {
        private TapAction mCarouselAction;
        private final List<CarouselItem> mCarouselItems;

        public Builder(List<CarouselItem> carouselItems) {
            super(4);
            this.mCarouselItems = (List) Objects.requireNonNull(carouselItems);
        }

        public Builder setCarouselAction(TapAction carouselAction) {
            this.mCarouselAction = carouselAction;
            return this;
        }

        @Override // android.app.smartspace.uitemplatedata.BaseTemplateData.Builder
        public CarouselTemplateData build() {
            if (this.mCarouselItems.isEmpty()) {
                throw new IllegalStateException("Carousel data is empty");
            }
            return new CarouselTemplateData(getTemplateType(), getPrimaryItem(), getSubtitleItem(), getSubtitleSupplemtnalItem(), getSupplementalLineItem(), getSupplementalAlarmItem(), getLayoutWeight(), this.mCarouselItems, this.mCarouselAction);
        }
    }

    /* loaded from: classes.dex */
    public static final class CarouselItem implements Parcelable {
        public static final Parcelable.Creator<CarouselItem> CREATOR = new Parcelable.Creator<CarouselItem>() { // from class: android.app.smartspace.uitemplatedata.CarouselTemplateData.CarouselItem.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CarouselItem createFromParcel(Parcel in) {
                return new CarouselItem(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CarouselItem[] newArray(int size) {
                return new CarouselItem[size];
            }
        };
        private final Icon mImage;
        private final Text mLowerText;
        private final TapAction mTapAction;
        private final Text mUpperText;

        CarouselItem(Parcel in) {
            this.mUpperText = (Text) in.readTypedObject(Text.CREATOR);
            this.mImage = (Icon) in.readTypedObject(Icon.CREATOR);
            this.mLowerText = (Text) in.readTypedObject(Text.CREATOR);
            this.mTapAction = (TapAction) in.readTypedObject(TapAction.CREATOR);
        }

        private CarouselItem(Text upperText, Icon image, Text lowerText, TapAction tapAction) {
            this.mUpperText = upperText;
            this.mImage = image;
            this.mLowerText = lowerText;
            this.mTapAction = tapAction;
        }

        public Text getUpperText() {
            return this.mUpperText;
        }

        public Icon getImage() {
            return this.mImage;
        }

        public Text getLowerText() {
            return this.mLowerText;
        }

        public TapAction getTapAction() {
            return this.mTapAction;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeTypedObject(this.mUpperText, flags);
            out.writeTypedObject(this.mImage, flags);
            out.writeTypedObject(this.mLowerText, flags);
            out.writeTypedObject(this.mTapAction, flags);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof CarouselItem) {
                CarouselItem that = (CarouselItem) o;
                return SmartspaceUtils.isEqual(this.mUpperText, that.mUpperText) && Objects.equals(this.mImage, that.mImage) && SmartspaceUtils.isEqual(this.mLowerText, that.mLowerText) && Objects.equals(this.mTapAction, that.mTapAction);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mUpperText, this.mImage, this.mLowerText, this.mTapAction);
        }

        public String toString() {
            return "CarouselItem{mUpperText=" + this.mUpperText + ", mImage=" + this.mImage + ", mLowerText=" + this.mLowerText + ", mTapAction=" + this.mTapAction + '}';
        }

        @SystemApi
        /* loaded from: classes.dex */
        public static final class Builder {
            private Icon mImage;
            private Text mLowerText;
            private TapAction mTapAction;
            private Text mUpperText;

            public Builder setUpperText(Text upperText) {
                this.mUpperText = upperText;
                return this;
            }

            public Builder setImage(Icon image) {
                this.mImage = image;
                return this;
            }

            public Builder setLowerText(Text lowerText) {
                this.mLowerText = lowerText;
                return this;
            }

            public Builder setTapAction(TapAction tapAction) {
                this.mTapAction = tapAction;
                return this;
            }

            public CarouselItem build() {
                if (SmartspaceUtils.isEmpty(this.mUpperText) && this.mImage == null && SmartspaceUtils.isEmpty(this.mLowerText)) {
                    throw new IllegalStateException("Carousel data is empty");
                }
                return new CarouselItem(this.mUpperText, this.mImage, this.mLowerText, this.mTapAction);
            }
        }
    }
}
