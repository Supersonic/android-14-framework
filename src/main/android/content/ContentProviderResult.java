package android.content;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ParcelableException;
import java.util.Objects;
/* loaded from: classes.dex */
public class ContentProviderResult implements Parcelable {
    public static final Parcelable.Creator<ContentProviderResult> CREATOR = new Parcelable.Creator<ContentProviderResult>() { // from class: android.content.ContentProviderResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentProviderResult createFromParcel(Parcel source) {
            return new ContentProviderResult(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentProviderResult[] newArray(int size) {
            return new ContentProviderResult[size];
        }
    };
    public final Integer count;
    public final Throwable exception;
    public final Bundle extras;
    public final Uri uri;

    public ContentProviderResult(Uri uri) {
        this((Uri) Objects.requireNonNull(uri), null, null, null);
    }

    public ContentProviderResult(int count) {
        this(null, Integer.valueOf(count), null, null);
    }

    public ContentProviderResult(Bundle extras) {
        this(null, null, (Bundle) Objects.requireNonNull(extras), null);
    }

    public ContentProviderResult(Throwable exception) {
        this(null, null, null, exception);
    }

    public ContentProviderResult(Uri uri, Integer count, Bundle extras, Throwable exception) {
        this.uri = uri;
        this.count = count;
        this.extras = extras;
        this.exception = exception;
    }

    public ContentProviderResult(Parcel source) {
        if (source.readInt() != 0) {
            this.uri = Uri.CREATOR.createFromParcel(source);
        } else {
            this.uri = null;
        }
        if (source.readInt() != 0) {
            this.count = Integer.valueOf(source.readInt());
        } else {
            this.count = null;
        }
        if (source.readInt() != 0) {
            this.extras = source.readBundle();
        } else {
            this.extras = null;
        }
        if (source.readInt() != 0) {
            this.exception = ParcelableException.readFromParcel(source);
        } else {
            this.exception = null;
        }
    }

    public ContentProviderResult(ContentProviderResult cpr, int userId) {
        this.uri = ContentProvider.maybeAddUserId(cpr.uri, userId);
        this.count = cpr.count;
        this.extras = cpr.extras;
        this.exception = cpr.exception;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.uri != null) {
            dest.writeInt(1);
            this.uri.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        if (this.count != null) {
            dest.writeInt(1);
            dest.writeInt(this.count.intValue());
        } else {
            dest.writeInt(0);
        }
        if (this.extras != null) {
            dest.writeInt(1);
            dest.writeBundle(this.extras);
        } else {
            dest.writeInt(0);
        }
        if (this.exception != null) {
            dest.writeInt(1);
            ParcelableException.writeToParcel(dest, this.exception);
            return;
        }
        dest.writeInt(0);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContentProviderResult(");
        if (this.uri != null) {
            sb.append("uri=").append(this.uri).append(' ');
        }
        if (this.count != null) {
            sb.append("count=").append(this.count).append(' ');
        }
        if (this.extras != null) {
            sb.append("extras=").append(this.extras).append(' ');
        }
        if (this.exception != null) {
            sb.append("exception=").append(this.exception).append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }
}
