package android.content.p001pm;

import android.annotation.SystemApi;
import android.content.res.ResourceId;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* renamed from: android.content.pm.SuspendDialogInfo */
/* loaded from: classes.dex */
public final class SuspendDialogInfo implements Parcelable {
    public static final int BUTTON_ACTION_MORE_DETAILS = 0;
    public static final int BUTTON_ACTION_UNSUSPEND = 1;
    private static final String XML_ATTR_BUTTON_ACTION = "buttonAction";
    private static final String XML_ATTR_BUTTON_TEXT = "buttonText";
    private static final String XML_ATTR_BUTTON_TEXT_RES_ID = "buttonTextResId";
    private static final String XML_ATTR_DIALOG_MESSAGE = "dialogMessage";
    private static final String XML_ATTR_DIALOG_MESSAGE_RES_ID = "dialogMessageResId";
    private static final String XML_ATTR_ICON_RES_ID = "iconResId";
    private static final String XML_ATTR_TITLE = "title";
    private static final String XML_ATTR_TITLE_RES_ID = "titleResId";
    private final String mDialogMessage;
    private final int mDialogMessageResId;
    private final int mIconResId;
    private final int mNeutralButtonAction;
    private final String mNeutralButtonText;
    private final int mNeutralButtonTextResId;
    private final String mTitle;
    private final int mTitleResId;
    private static final String TAG = SuspendDialogInfo.class.getSimpleName();
    public static final Parcelable.Creator<SuspendDialogInfo> CREATOR = new Parcelable.Creator<SuspendDialogInfo>() { // from class: android.content.pm.SuspendDialogInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuspendDialogInfo createFromParcel(Parcel source) {
            return new SuspendDialogInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuspendDialogInfo[] newArray(int size) {
            return new SuspendDialogInfo[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.SuspendDialogInfo$ButtonAction */
    /* loaded from: classes.dex */
    public @interface ButtonAction {
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public int getTitleResId() {
        return this.mTitleResId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int getDialogMessageResId() {
        return this.mDialogMessageResId;
    }

    public String getDialogMessage() {
        return this.mDialogMessage;
    }

    public int getNeutralButtonTextResId() {
        return this.mNeutralButtonTextResId;
    }

    public String getNeutralButtonText() {
        return this.mNeutralButtonText;
    }

    public int getNeutralButtonAction() {
        return this.mNeutralButtonAction;
    }

    public void saveToXml(TypedXmlSerializer out) throws IOException {
        int i = this.mIconResId;
        if (i != 0) {
            out.attributeInt(null, "iconResId", i);
        }
        int i2 = this.mTitleResId;
        if (i2 != 0) {
            out.attributeInt(null, XML_ATTR_TITLE_RES_ID, i2);
        } else {
            XmlUtils.writeStringAttribute(out, "title", this.mTitle);
        }
        int i3 = this.mDialogMessageResId;
        if (i3 != 0) {
            out.attributeInt(null, XML_ATTR_DIALOG_MESSAGE_RES_ID, i3);
        } else {
            XmlUtils.writeStringAttribute(out, XML_ATTR_DIALOG_MESSAGE, this.mDialogMessage);
        }
        int i4 = this.mNeutralButtonTextResId;
        if (i4 != 0) {
            out.attributeInt(null, XML_ATTR_BUTTON_TEXT_RES_ID, i4);
        } else {
            XmlUtils.writeStringAttribute(out, XML_ATTR_BUTTON_TEXT, this.mNeutralButtonText);
        }
        out.attributeInt(null, XML_ATTR_BUTTON_ACTION, this.mNeutralButtonAction);
    }

    public static SuspendDialogInfo restoreFromXml(TypedXmlPullParser in) {
        Builder dialogInfoBuilder = new Builder();
        try {
            int iconId = in.getAttributeInt(null, "iconResId", 0);
            int titleId = in.getAttributeInt(null, XML_ATTR_TITLE_RES_ID, 0);
            String title = XmlUtils.readStringAttribute(in, "title");
            int buttonTextId = in.getAttributeInt(null, XML_ATTR_BUTTON_TEXT_RES_ID, 0);
            String buttonText = XmlUtils.readStringAttribute(in, XML_ATTR_BUTTON_TEXT);
            int buttonAction = in.getAttributeInt(null, XML_ATTR_BUTTON_ACTION, 0);
            int dialogMessageResId = in.getAttributeInt(null, XML_ATTR_DIALOG_MESSAGE_RES_ID, 0);
            String dialogMessage = XmlUtils.readStringAttribute(in, XML_ATTR_DIALOG_MESSAGE);
            if (iconId != 0) {
                dialogInfoBuilder.setIcon(iconId);
            }
            if (titleId != 0) {
                dialogInfoBuilder.setTitle(titleId);
            } else if (title != null) {
                dialogInfoBuilder.setTitle(title);
            }
            if (buttonTextId != 0) {
                dialogInfoBuilder.setNeutralButtonText(buttonTextId);
            } else if (buttonText != null) {
                dialogInfoBuilder.setNeutralButtonText(buttonText);
            }
            if (dialogMessageResId != 0) {
                dialogInfoBuilder.setMessage(dialogMessageResId);
            } else if (dialogMessage != null) {
                dialogInfoBuilder.setMessage(dialogMessage);
            }
            dialogInfoBuilder.setNeutralButtonAction(buttonAction);
        } catch (Exception e) {
            Slog.m95e(TAG, "Exception while parsing from xml. Some fields may default", e);
        }
        return dialogInfoBuilder.build();
    }

    public int hashCode() {
        int hashCode = this.mIconResId;
        return (((((((((((((hashCode * 31) + this.mTitleResId) * 31) + Objects.hashCode(this.mTitle)) * 31) + this.mNeutralButtonTextResId) * 31) + Objects.hashCode(this.mNeutralButtonText)) * 31) + this.mDialogMessageResId) * 31) + Objects.hashCode(this.mDialogMessage)) * 31) + this.mNeutralButtonAction;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SuspendDialogInfo) {
            SuspendDialogInfo otherDialogInfo = (SuspendDialogInfo) obj;
            return this.mIconResId == otherDialogInfo.mIconResId && this.mTitleResId == otherDialogInfo.mTitleResId && Objects.equals(this.mTitle, otherDialogInfo.mTitle) && this.mDialogMessageResId == otherDialogInfo.mDialogMessageResId && Objects.equals(this.mDialogMessage, otherDialogInfo.mDialogMessage) && this.mNeutralButtonTextResId == otherDialogInfo.mNeutralButtonTextResId && Objects.equals(this.mNeutralButtonText, otherDialogInfo.mNeutralButtonText) && this.mNeutralButtonAction == otherDialogInfo.mNeutralButtonAction;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("SuspendDialogInfo: {");
        if (this.mIconResId != 0) {
            builder.append("mIconId = 0x");
            builder.append(Integer.toHexString(this.mIconResId));
            builder.append(" ");
        }
        if (this.mTitleResId != 0) {
            builder.append("mTitleResId = 0x");
            builder.append(Integer.toHexString(this.mTitleResId));
            builder.append(" ");
        } else if (this.mTitle != null) {
            builder.append("mTitle = \"");
            builder.append(this.mTitle);
            builder.append("\"");
        }
        if (this.mNeutralButtonTextResId != 0) {
            builder.append("mNeutralButtonTextResId = 0x");
            builder.append(Integer.toHexString(this.mNeutralButtonTextResId));
            builder.append(" ");
        } else if (this.mNeutralButtonText != null) {
            builder.append("mNeutralButtonText = \"");
            builder.append(this.mNeutralButtonText);
            builder.append("\"");
        }
        if (this.mDialogMessageResId != 0) {
            builder.append("mDialogMessageResId = 0x");
            builder.append(Integer.toHexString(this.mDialogMessageResId));
            builder.append(" ");
        } else if (this.mDialogMessage != null) {
            builder.append("mDialogMessage = \"");
            builder.append(this.mDialogMessage);
            builder.append("\" ");
        }
        builder.append("mNeutralButtonAction = ");
        builder.append(this.mNeutralButtonAction);
        builder.append("}");
        return builder.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.mIconResId);
        dest.writeInt(this.mTitleResId);
        dest.writeString(this.mTitle);
        dest.writeInt(this.mDialogMessageResId);
        dest.writeString(this.mDialogMessage);
        dest.writeInt(this.mNeutralButtonTextResId);
        dest.writeString(this.mNeutralButtonText);
        dest.writeInt(this.mNeutralButtonAction);
    }

    private SuspendDialogInfo(Parcel source) {
        this.mIconResId = source.readInt();
        this.mTitleResId = source.readInt();
        this.mTitle = source.readString();
        this.mDialogMessageResId = source.readInt();
        this.mDialogMessage = source.readString();
        this.mNeutralButtonTextResId = source.readInt();
        this.mNeutralButtonText = source.readString();
        this.mNeutralButtonAction = source.readInt();
    }

    SuspendDialogInfo(Builder b) {
        this.mIconResId = b.mIconResId;
        int i = b.mTitleResId;
        this.mTitleResId = i;
        this.mTitle = i == 0 ? b.mTitle : null;
        int i2 = b.mDialogMessageResId;
        this.mDialogMessageResId = i2;
        this.mDialogMessage = i2 == 0 ? b.mDialogMessage : null;
        int i3 = b.mNeutralButtonTextResId;
        this.mNeutralButtonTextResId = i3;
        this.mNeutralButtonText = i3 == 0 ? b.mNeutralButtonText : null;
        this.mNeutralButtonAction = b.mNeutralButtonAction;
    }

    /* renamed from: android.content.pm.SuspendDialogInfo$Builder */
    /* loaded from: classes.dex */
    public static final class Builder {
        private String mDialogMessage;
        private String mNeutralButtonText;
        private String mTitle;
        private int mDialogMessageResId = 0;
        private int mTitleResId = 0;
        private int mIconResId = 0;
        private int mNeutralButtonTextResId = 0;
        private int mNeutralButtonAction = 0;

        public Builder setIcon(int resId) {
            Preconditions.checkArgument(ResourceId.isValid(resId), "Invalid resource id provided");
            this.mIconResId = resId;
            return this;
        }

        public Builder setTitle(int resId) {
            Preconditions.checkArgument(ResourceId.isValid(resId), "Invalid resource id provided");
            this.mTitleResId = resId;
            return this;
        }

        public Builder setTitle(String title) {
            Preconditions.checkStringNotEmpty(title, "Title cannot be null or empty");
            this.mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            Preconditions.checkStringNotEmpty(message, "Message cannot be null or empty");
            this.mDialogMessage = message;
            return this;
        }

        public Builder setMessage(int resId) {
            Preconditions.checkArgument(ResourceId.isValid(resId), "Invalid resource id provided");
            this.mDialogMessageResId = resId;
            return this;
        }

        public Builder setNeutralButtonText(int resId) {
            Preconditions.checkArgument(ResourceId.isValid(resId), "Invalid resource id provided");
            this.mNeutralButtonTextResId = resId;
            return this;
        }

        public Builder setNeutralButtonText(String neutralButtonText) {
            Preconditions.checkStringNotEmpty(neutralButtonText, "Button text cannot be null or empty");
            this.mNeutralButtonText = neutralButtonText;
            return this;
        }

        public Builder setNeutralButtonAction(int buttonAction) {
            boolean z = true;
            if (buttonAction != 0 && buttonAction != 1) {
                z = false;
            }
            Preconditions.checkArgument(z, "Invalid button action");
            this.mNeutralButtonAction = buttonAction;
            return this;
        }

        public SuspendDialogInfo build() {
            return new SuspendDialogInfo(this);
        }
    }
}
