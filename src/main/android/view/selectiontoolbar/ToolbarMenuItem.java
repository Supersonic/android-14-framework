package android.view.selectiontoolbar;

import android.annotation.NonNull;
import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.MenuItem;
import com.android.internal.util.AnnotationValidations;
import com.android.net.module.util.NetworkStackConstants;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ToolbarMenuItem implements Parcelable {
    public static final Parcelable.Creator<ToolbarMenuItem> CREATOR = new Parcelable.Creator<ToolbarMenuItem>() { // from class: android.view.selectiontoolbar.ToolbarMenuItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ToolbarMenuItem[] newArray(int size) {
            return new ToolbarMenuItem[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ToolbarMenuItem createFromParcel(Parcel in) {
            return new ToolbarMenuItem(in);
        }
    };
    public static final int PRIORITY_OVERFLOW = 2;
    public static final int PRIORITY_PRIMARY = 1;
    public static final int PRIORITY_UNKNOWN = 0;
    private final CharSequence mContentDescription;
    private final int mGroupId;
    private final Icon mIcon;
    private final int mItemId;
    private final int mPriority;
    private final CharSequence mTitle;
    private final CharSequence mTooltipText;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Priority {
    }

    public static int getPriorityFromMenuItem(MenuItem menuItem) {
        if (menuItem.requiresActionButton()) {
            return 1;
        }
        if (menuItem.requiresOverflow()) {
            return 2;
        }
        return 0;
    }

    public static String priorityToString(int value) {
        switch (value) {
            case 0:
                return "PRIORITY_UNKNOWN";
            case 1:
                return "PRIORITY_PRIMARY";
            case 2:
                return "PRIORITY_OVERFLOW";
            default:
                return Integer.toHexString(value);
        }
    }

    ToolbarMenuItem(int itemId, CharSequence title, CharSequence contentDescription, int groupId, Icon icon, CharSequence tooltipText, int priority) {
        this.mItemId = itemId;
        this.mTitle = title;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) title);
        this.mContentDescription = contentDescription;
        this.mGroupId = groupId;
        this.mIcon = icon;
        this.mTooltipText = tooltipText;
        this.mPriority = priority;
    }

    public int getItemId() {
        return this.mItemId;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public CharSequence getTooltipText() {
        return this.mTooltipText;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public String toString() {
        return "ToolbarMenuItem { itemId = " + this.mItemId + ", title = " + ((Object) this.mTitle) + ", contentDescription = " + ((Object) this.mContentDescription) + ", groupId = " + this.mGroupId + ", icon = " + this.mIcon + ", tooltipText = " + ((Object) this.mTooltipText) + ", priority = " + this.mPriority + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToolbarMenuItem that = (ToolbarMenuItem) o;
        if (this.mItemId == that.mItemId && Objects.equals(this.mTitle, that.mTitle) && Objects.equals(this.mContentDescription, that.mContentDescription) && this.mGroupId == that.mGroupId && Objects.equals(this.mIcon, that.mIcon) && Objects.equals(this.mTooltipText, that.mTooltipText) && this.mPriority == that.mPriority) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mItemId;
        return (((((((((((_hash * 31) + Objects.hashCode(this.mTitle)) * 31) + Objects.hashCode(this.mContentDescription)) * 31) + this.mGroupId) * 31) + Objects.hashCode(this.mIcon)) * 31) + Objects.hashCode(this.mTooltipText)) * 31) + this.mPriority;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mContentDescription != null ? (byte) (0 | 4) : (byte) 0;
        if (this.mIcon != null) {
            flg = (byte) (flg | 16);
        }
        if (this.mTooltipText != null) {
            flg = (byte) (flg | NetworkStackConstants.TCPHDR_URG);
        }
        dest.writeByte(flg);
        dest.writeInt(this.mItemId);
        dest.writeCharSequence(this.mTitle);
        CharSequence charSequence = this.mContentDescription;
        if (charSequence != null) {
            dest.writeCharSequence(charSequence);
        }
        dest.writeInt(this.mGroupId);
        Icon icon = this.mIcon;
        if (icon != null) {
            dest.writeTypedObject(icon, flags);
        }
        CharSequence charSequence2 = this.mTooltipText;
        if (charSequence2 != null) {
            dest.writeCharSequence(charSequence2);
        }
        dest.writeInt(this.mPriority);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    ToolbarMenuItem(Parcel in) {
        byte flg = in.readByte();
        int itemId = in.readInt();
        CharSequence title = in.readCharSequence();
        CharSequence contentDescription = (flg & 4) == 0 ? null : in.readCharSequence();
        int groupId = in.readInt();
        Icon icon = (flg & 16) == 0 ? null : (Icon) in.readTypedObject(Icon.CREATOR);
        CharSequence tooltipText = (flg & NetworkStackConstants.TCPHDR_URG) == 0 ? null : in.readCharSequence();
        int priority = in.readInt();
        this.mItemId = itemId;
        this.mTitle = title;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) title);
        this.mContentDescription = contentDescription;
        this.mGroupId = groupId;
        this.mIcon = icon;
        this.mTooltipText = tooltipText;
        this.mPriority = priority;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private CharSequence mContentDescription;
        private int mGroupId;
        private Icon mIcon;
        private int mItemId;
        private int mPriority;
        private CharSequence mTitle;
        private CharSequence mTooltipText;

        public Builder(int itemId, CharSequence title, CharSequence contentDescription, int groupId, Icon icon, CharSequence tooltipText, int priority) {
            this.mItemId = itemId;
            this.mTitle = title;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) title);
            this.mContentDescription = contentDescription;
            this.mGroupId = groupId;
            this.mIcon = icon;
            this.mTooltipText = tooltipText;
            this.mPriority = priority;
        }

        public Builder setItemId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mItemId = value;
            return this;
        }

        public Builder setTitle(CharSequence value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mTitle = value;
            return this;
        }

        public Builder setContentDescription(CharSequence value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mContentDescription = value;
            return this;
        }

        public Builder setGroupId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mGroupId = value;
            return this;
        }

        public Builder setIcon(Icon value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mIcon = value;
            return this;
        }

        public Builder setTooltipText(CharSequence value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mTooltipText = value;
            return this;
        }

        public Builder setPriority(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mPriority = value;
            return this;
        }

        public ToolbarMenuItem build() {
            checkNotUsed();
            this.mBuilderFieldsSet |= 128;
            ToolbarMenuItem o = new ToolbarMenuItem(this.mItemId, this.mTitle, this.mContentDescription, this.mGroupId, this.mIcon, this.mTooltipText, this.mPriority);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 128) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
