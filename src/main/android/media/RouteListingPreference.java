package android.media;

import android.content.ComponentName;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class RouteListingPreference implements Parcelable {
    public static final String ACTION_TRANSFER_MEDIA = "android.media.action.TRANSFER_MEDIA";
    public static final Parcelable.Creator<RouteListingPreference> CREATOR = new Parcelable.Creator<RouteListingPreference>() { // from class: android.media.RouteListingPreference.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RouteListingPreference createFromParcel(Parcel in) {
            return new RouteListingPreference(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RouteListingPreference[] newArray(int size) {
            return new RouteListingPreference[size];
        }
    };
    public static final String EXTRA_ROUTE_ID = "android.media.extra.ROUTE_ID";
    private final List<Item> mItems;
    private final ComponentName mLinkedItemComponentName;
    private final boolean mUseSystemOrdering;

    private RouteListingPreference(Builder builder) {
        this.mItems = builder.mItems;
        this.mUseSystemOrdering = builder.mUseSystemOrdering;
        this.mLinkedItemComponentName = builder.mLinkedItemComponentName;
    }

    private RouteListingPreference(Parcel in) {
        List<Item> items = in.readParcelableList(new ArrayList(), Item.class.getClassLoader(), Item.class);
        this.mItems = List.copyOf(items);
        this.mUseSystemOrdering = in.readBoolean();
        this.mLinkedItemComponentName = ComponentName.readFromParcel(in);
    }

    public List<Item> getItems() {
        return this.mItems;
    }

    public boolean getUseSystemOrdering() {
        return this.mUseSystemOrdering;
    }

    public ComponentName getLinkedItemComponentName() {
        return this.mLinkedItemComponentName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableList(this.mItems, flags);
        dest.writeBoolean(this.mUseSystemOrdering);
        ComponentName.writeToParcel(this.mLinkedItemComponentName, dest);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RouteListingPreference) {
            RouteListingPreference that = (RouteListingPreference) other;
            return this.mItems.equals(that.mItems) && this.mUseSystemOrdering == that.mUseSystemOrdering && Objects.equals(this.mLinkedItemComponentName, that.mLinkedItemComponentName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mItems, Boolean.valueOf(this.mUseSystemOrdering), this.mLinkedItemComponentName);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private ComponentName mLinkedItemComponentName;
        private List<Item> mItems = List.of();
        private boolean mUseSystemOrdering = true;

        public Builder setItems(List<Item> items) {
            this.mItems = List.copyOf((Collection) Objects.requireNonNull(items));
            return this;
        }

        public Builder setUseSystemOrdering(boolean useSystemOrdering) {
            this.mUseSystemOrdering = useSystemOrdering;
            return this;
        }

        public Builder setLinkedItemComponentName(ComponentName linkedItemComponentName) {
            this.mLinkedItemComponentName = linkedItemComponentName;
            return this;
        }

        public RouteListingPreference build() {
            return new RouteListingPreference(this);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Item implements Parcelable {
        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() { // from class: android.media.RouteListingPreference.Item.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
        public static final int FLAG_ONGOING_SESSION = 1;
        public static final int FLAG_ONGOING_SESSION_MANAGED = 2;
        public static final int FLAG_SUGGESTED = 4;
        public static final int SELECTION_BEHAVIOR_GO_TO_APP = 2;
        public static final int SELECTION_BEHAVIOR_NONE = 0;
        public static final int SELECTION_BEHAVIOR_TRANSFER = 1;
        public static final int SUBTEXT_AD_ROUTING_DISALLOWED = 4;
        public static final int SUBTEXT_CUSTOM = 10000;
        public static final int SUBTEXT_DEVICE_LOW_POWER = 5;
        public static final int SUBTEXT_DOWNLOADED_CONTENT_ROUTING_DISALLOWED = 3;
        public static final int SUBTEXT_ERROR_UNKNOWN = 1;
        public static final int SUBTEXT_NONE = 0;
        public static final int SUBTEXT_SUBSCRIPTION_REQUIRED = 2;
        public static final int SUBTEXT_TRACK_UNSUPPORTED = 7;
        public static final int SUBTEXT_UNAUTHORIZED = 6;
        private final CharSequence mCustomSubtextMessage;
        private final int mFlags;
        private final String mRouteId;
        private final int mSelectionBehavior;
        private final int mSubText;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface Flags {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface SelectionBehavior {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface SubText {
        }

        private Item(Builder builder) {
            this.mRouteId = builder.mRouteId;
            this.mSelectionBehavior = builder.mSelectionBehavior;
            this.mFlags = builder.mFlags;
            this.mSubText = builder.mSubText;
            this.mCustomSubtextMessage = builder.mCustomSubtextMessage;
            validateCustomMessageSubtext();
        }

        private Item(Parcel in) {
            String readString = in.readString();
            this.mRouteId = readString;
            Preconditions.checkArgument(!TextUtils.isEmpty(readString));
            this.mSelectionBehavior = in.readInt();
            this.mFlags = in.readInt();
            this.mSubText = in.readInt();
            this.mCustomSubtextMessage = in.readCharSequence();
            validateCustomMessageSubtext();
        }

        public String getRouteId() {
            return this.mRouteId;
        }

        public int getSelectionBehavior() {
            return this.mSelectionBehavior;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public int getSubText() {
            return this.mSubText;
        }

        public CharSequence getCustomSubtextMessage() {
            return this.mCustomSubtextMessage;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mRouteId);
            dest.writeInt(this.mSelectionBehavior);
            dest.writeInt(this.mFlags);
            dest.writeInt(this.mSubText);
            dest.writeCharSequence(this.mCustomSubtextMessage);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof Item) {
                Item item = (Item) other;
                return this.mRouteId.equals(item.mRouteId) && this.mSelectionBehavior == item.mSelectionBehavior && this.mFlags == item.mFlags && this.mSubText == item.mSubText && TextUtils.equals(this.mCustomSubtextMessage, item.mCustomSubtextMessage);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mRouteId, Integer.valueOf(this.mSelectionBehavior), Integer.valueOf(this.mFlags), Integer.valueOf(this.mSubText), this.mCustomSubtextMessage);
        }

        private void validateCustomMessageSubtext() {
            Preconditions.checkArgument((this.mSubText == 10000 && this.mCustomSubtextMessage == null) ? false : true, "The custom subtext message cannot be null if subtext is SUBTEXT_CUSTOM.");
        }

        /* loaded from: classes2.dex */
        public static final class Builder {
            private CharSequence mCustomSubtextMessage;
            private int mFlags;
            private final String mRouteId;
            private int mSelectionBehavior;
            private int mSubText;

            public Builder(String routeId) {
                Preconditions.checkArgument(!TextUtils.isEmpty(routeId));
                this.mRouteId = routeId;
                this.mSelectionBehavior = 1;
                this.mSubText = 0;
            }

            public Builder setSelectionBehavior(int selectionBehavior) {
                this.mSelectionBehavior = selectionBehavior;
                return this;
            }

            public Builder setFlags(int flags) {
                this.mFlags = flags;
                return this;
            }

            public Builder setSubText(int subText) {
                this.mSubText = subText;
                return this;
            }

            public Builder setCustomSubtextMessage(CharSequence customSubtextMessage) {
                this.mCustomSubtextMessage = customSubtextMessage;
                return this;
            }

            public Item build() {
                return new Item(this);
            }
        }
    }
}
