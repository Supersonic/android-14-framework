package android.view;

import android.app.slice.Slice;
import android.content.ClipData;
import android.content.ClipDescription;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Pair;
import android.view.inputmethod.InputContentInfo;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public final class ContentInfo implements Parcelable {
    public static final Parcelable.Creator<ContentInfo> CREATOR = new Parcelable.Creator<ContentInfo>() { // from class: android.view.ContentInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentInfo createFromParcel(Parcel parcel) {
            ClipData clip = ClipData.CREATOR.createFromParcel(parcel);
            int source = parcel.readInt();
            int flags = parcel.readInt();
            Uri linkUri = Uri.CREATOR.createFromParcel(parcel);
            Bundle extras = parcel.readBundle();
            InputContentInfo inputContentInfo = null;
            if (parcel.readInt() != 0) {
                InputContentInfo inputContentInfo2 = InputContentInfo.CREATOR.createFromParcel(parcel);
                inputContentInfo = inputContentInfo2;
            }
            DragAndDropPermissions dragAndDropPermissions = null;
            if (parcel.readInt() != 0) {
                DragAndDropPermissions dragAndDropPermissions2 = DragAndDropPermissions.CREATOR.createFromParcel(parcel);
                dragAndDropPermissions = dragAndDropPermissions2;
            }
            return new Builder(clip, source).setFlags(flags).setLinkUri(linkUri).setExtras(extras).setInputContentInfo(inputContentInfo).setDragAndDropPermissions(dragAndDropPermissions).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentInfo[] newArray(int size) {
            return new ContentInfo[size];
        }
    };
    public static final int FLAG_CONVERT_TO_PLAIN_TEXT = 1;
    public static final int SOURCE_APP = 0;
    public static final int SOURCE_AUTOFILL = 4;
    public static final int SOURCE_CLIPBOARD = 1;
    public static final int SOURCE_DRAG_AND_DROP = 3;
    public static final int SOURCE_INPUT_METHOD = 2;
    public static final int SOURCE_PROCESS_TEXT = 5;
    private final ClipData mClip;
    private final DragAndDropPermissions mDragAndDropPermissions;
    private final Bundle mExtras;
    private final int mFlags;
    private final InputContentInfo mInputContentInfo;
    private final Uri mLinkUri;
    private final int mSource;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Flags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Source {
    }

    static String sourceToString(int source) {
        switch (source) {
            case 0:
                return "SOURCE_APP";
            case 1:
                return "SOURCE_CLIPBOARD";
            case 2:
                return "SOURCE_INPUT_METHOD";
            case 3:
                return "SOURCE_DRAG_AND_DROP";
            case 4:
                return "SOURCE_AUTOFILL";
            case 5:
                return "SOURCE_PROCESS_TEXT";
            default:
                return String.valueOf(source);
        }
    }

    static String flagsToString(int flags) {
        if ((flags & 1) != 0) {
            return "FLAG_CONVERT_TO_PLAIN_TEXT";
        }
        return String.valueOf(flags);
    }

    private ContentInfo(Builder b) {
        this.mClip = (ClipData) Objects.requireNonNull(b.mClip);
        this.mSource = Preconditions.checkArgumentInRange(b.mSource, 0, 5, Slice.SUBTYPE_SOURCE);
        this.mFlags = Preconditions.checkFlagsArgument(b.mFlags, 1);
        this.mLinkUri = b.mLinkUri;
        this.mExtras = b.mExtras;
        this.mInputContentInfo = b.mInputContentInfo;
        this.mDragAndDropPermissions = b.mDragAndDropPermissions;
    }

    public void releasePermissions() {
        InputContentInfo inputContentInfo = this.mInputContentInfo;
        if (inputContentInfo != null) {
            inputContentInfo.releasePermission();
        }
        DragAndDropPermissions dragAndDropPermissions = this.mDragAndDropPermissions;
        if (dragAndDropPermissions != null) {
            dragAndDropPermissions.release();
        }
    }

    public String toString() {
        return "ContentInfo{clip=" + this.mClip + ", source=" + sourceToString(this.mSource) + ", flags=" + flagsToString(this.mFlags) + ", linkUri=" + this.mLinkUri + ", extras=" + this.mExtras + "}";
    }

    public ClipData getClip() {
        return this.mClip;
    }

    public int getSource() {
        return this.mSource;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public Uri getLinkUri() {
        return this.mLinkUri;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Pair<ContentInfo, ContentInfo> partition(Predicate<ClipData.Item> itemPredicate) {
        if (this.mClip.getItemCount() == 1) {
            boolean matched = itemPredicate.test(this.mClip.getItemAt(0));
            return Pair.create(matched ? this : null, matched ? null : this);
        }
        ArrayList<ClipData.Item> acceptedItems = new ArrayList<>();
        ArrayList<ClipData.Item> remainingItems = new ArrayList<>();
        for (int i = 0; i < this.mClip.getItemCount(); i++) {
            ClipData.Item item = this.mClip.getItemAt(i);
            if (itemPredicate.test(item)) {
                acceptedItems.add(item);
            } else {
                remainingItems.add(item);
            }
        }
        if (acceptedItems.isEmpty()) {
            return Pair.create(null, this);
        }
        if (remainingItems.isEmpty()) {
            return Pair.create(this, null);
        }
        ContentInfo accepted = new Builder(this).setClip(new ClipData(new ClipDescription(this.mClip.getDescription()), acceptedItems)).build();
        ContentInfo remaining = new Builder(this).setClip(new ClipData(new ClipDescription(this.mClip.getDescription()), remainingItems)).build();
        return Pair.create(accepted, remaining);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private ClipData mClip;
        private DragAndDropPermissions mDragAndDropPermissions;
        private Bundle mExtras;
        private int mFlags;
        private InputContentInfo mInputContentInfo;
        private Uri mLinkUri;
        private int mSource;

        public Builder(ContentInfo other) {
            this.mClip = other.mClip;
            this.mSource = other.mSource;
            this.mFlags = other.mFlags;
            this.mLinkUri = other.mLinkUri;
            this.mExtras = other.mExtras;
            this.mInputContentInfo = other.mInputContentInfo;
            this.mDragAndDropPermissions = other.mDragAndDropPermissions;
        }

        public Builder(ClipData clip, int source) {
            this.mClip = clip;
            this.mSource = source;
        }

        public Builder setClip(ClipData clip) {
            this.mClip = clip;
            return this;
        }

        public Builder setSource(int source) {
            this.mSource = source;
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public Builder setLinkUri(Uri linkUri) {
            this.mLinkUri = linkUri;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setInputContentInfo(InputContentInfo inputContentInfo) {
            this.mInputContentInfo = inputContentInfo;
            return this;
        }

        public Builder setDragAndDropPermissions(DragAndDropPermissions permissions) {
            this.mDragAndDropPermissions = permissions;
            return this;
        }

        public ContentInfo build() {
            return new ContentInfo(this);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mClip.writeToParcel(dest, flags);
        dest.writeInt(this.mSource);
        dest.writeInt(this.mFlags);
        Uri.writeToParcel(dest, this.mLinkUri);
        dest.writeBundle(this.mExtras);
        if (this.mInputContentInfo == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            this.mInputContentInfo.writeToParcel(dest, flags);
        }
        if (this.mDragAndDropPermissions == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(1);
        this.mDragAndDropPermissions.writeToParcel(dest, flags);
    }
}
