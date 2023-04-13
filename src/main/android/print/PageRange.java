package android.print;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class PageRange implements Parcelable {
    public static final PageRange ALL_PAGES;
    public static final PageRange[] ALL_PAGES_ARRAY;
    public static final Parcelable.Creator<PageRange> CREATOR;
    private final int mEnd;
    private final int mStart;

    static {
        PageRange pageRange = new PageRange(0, Integer.MAX_VALUE);
        ALL_PAGES = pageRange;
        ALL_PAGES_ARRAY = new PageRange[]{pageRange};
        CREATOR = new Parcelable.Creator<PageRange>() { // from class: android.print.PageRange.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PageRange createFromParcel(Parcel parcel) {
                return new PageRange(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PageRange[] newArray(int size) {
                return new PageRange[size];
            }
        };
    }

    public PageRange(int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("start cannot be less than zero.");
        }
        if (end < 0) {
            throw new IllegalArgumentException("end cannot be less than zero.");
        }
        if (start > end) {
            throw new IllegalArgumentException("start must be lesser than end.");
        }
        this.mStart = start;
        this.mEnd = end;
    }

    private PageRange(Parcel parcel) {
        this(parcel.readInt(), parcel.readInt());
    }

    public int getStart() {
        return this.mStart;
    }

    public int getEnd() {
        return this.mEnd;
    }

    public boolean contains(int pageIndex) {
        return pageIndex >= this.mStart && pageIndex <= this.mEnd;
    }

    public int getSize() {
        return (this.mEnd - this.mStart) + 1;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mStart);
        parcel.writeInt(this.mEnd);
    }

    public int hashCode() {
        int result = (1 * 31) + this.mEnd;
        return (result * 31) + this.mStart;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PageRange other = (PageRange) obj;
        if (this.mEnd == other.mEnd && this.mStart == other.mStart) {
            return true;
        }
        return false;
    }

    public String toString() {
        if (this.mStart == 0 && this.mEnd == Integer.MAX_VALUE) {
            return "PageRange[<all pages>]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("PageRange[").append(this.mStart).append(" - ").append(this.mEnd).append(NavigationBarInflaterView.SIZE_MOD_END);
        return builder.toString();
    }
}
