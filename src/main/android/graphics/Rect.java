package android.graphics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import android.util.proto.WireTypeMismatchException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class Rect implements Parcelable {
    public static final Parcelable.Creator<Rect> CREATOR = new Parcelable.Creator<Rect>() { // from class: android.graphics.Rect.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Rect createFromParcel(Parcel in) {
            Rect r = new Rect();
            r.readFromParcel(in);
            return r;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Rect[] newArray(int size) {
            return new Rect[size];
        }
    };
    public int bottom;
    public int left;
    public int right;
    public int top;

    /* loaded from: classes.dex */
    private static final class UnflattenHelper {
        private static final Pattern FLATTENED_PATTERN = Pattern.compile("(-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)");

        private UnflattenHelper() {
        }

        static Matcher getMatcher(String str) {
            return FLATTENED_PATTERN.matcher(str);
        }
    }

    public Rect() {
    }

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Rect(Rect r) {
        if (r == null) {
            this.bottom = 0;
            this.right = 0;
            this.top = 0;
            this.left = 0;
            return;
        }
        this.left = r.left;
        this.top = r.top;
        this.right = r.right;
        this.bottom = r.bottom;
    }

    public Rect(Insets r) {
        if (r == null) {
            this.bottom = 0;
            this.right = 0;
            this.top = 0;
            this.left = 0;
            return;
        }
        this.left = r.left;
        this.top = r.top;
        this.right = r.right;
        this.bottom = r.bottom;
    }

    public static Rect copyOrNull(Rect r) {
        if (r == null) {
            return null;
        }
        return new Rect(r);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rect r = (Rect) o;
        if (this.left == r.left && this.top == r.top && this.right == r.right && this.bottom == r.bottom) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.left;
        return (((((result * 31) + this.top) * 31) + this.right) * 31) + this.bottom;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Rect(");
        sb.append(this.left);
        sb.append(", ");
        sb.append(this.top);
        sb.append(" - ");
        sb.append(this.right);
        sb.append(", ");
        sb.append(this.bottom);
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }

    public String toShortString() {
        return toShortString(new StringBuilder(32));
    }

    public String toShortString(StringBuilder sb) {
        sb.setLength(0);
        sb.append('[');
        sb.append(this.left);
        sb.append(',');
        sb.append(this.top);
        sb.append("][");
        sb.append(this.right);
        sb.append(',');
        sb.append(this.bottom);
        sb.append(']');
        return sb.toString();
    }

    public String flattenToString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append(this.left);
        sb.append(' ');
        sb.append(this.top);
        sb.append(' ');
        sb.append(this.right);
        sb.append(' ');
        sb.append(this.bottom);
        return sb.toString();
    }

    public static Rect unflattenFromString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Matcher matcher = UnflattenHelper.getMatcher(str);
        if (matcher.matches()) {
            return new Rect(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        }
        return null;
    }

    public void printShortString(PrintWriter pw) {
        pw.print('[');
        pw.print(this.left);
        pw.print(',');
        pw.print(this.top);
        pw.print("][");
        pw.print(this.right);
        pw.print(',');
        pw.print(this.bottom);
        pw.print(']');
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long fieldId) {
        long token = protoOutputStream.start(fieldId);
        protoOutputStream.write(1120986464257L, this.left);
        protoOutputStream.write(1120986464258L, this.top);
        protoOutputStream.write(1120986464259L, this.right);
        protoOutputStream.write(1120986464260L, this.bottom);
        protoOutputStream.end(token);
    }

    public void readFromProto(ProtoInputStream proto, long fieldId) throws IOException, WireTypeMismatchException {
        long token = proto.start(fieldId);
        while (proto.nextField() != -1) {
            try {
                switch (proto.getFieldNumber()) {
                    case 1:
                        this.left = proto.readInt(1120986464257L);
                        break;
                    case 2:
                        this.top = proto.readInt(1120986464258L);
                        break;
                    case 3:
                        this.right = proto.readInt(1120986464259L);
                        break;
                    case 4:
                        this.bottom = proto.readInt(1120986464260L);
                        break;
                }
            } finally {
                proto.end(token);
            }
        }
    }

    public final boolean isEmpty() {
        return this.left >= this.right || this.top >= this.bottom;
    }

    public boolean isValid() {
        return this.left <= this.right && this.top <= this.bottom;
    }

    public final int width() {
        return this.right - this.left;
    }

    public final int height() {
        return this.bottom - this.top;
    }

    public final int centerX() {
        return (this.left + this.right) >> 1;
    }

    public final int centerY() {
        return (this.top + this.bottom) >> 1;
    }

    public final float exactCenterX() {
        return (this.left + this.right) * 0.5f;
    }

    public final float exactCenterY() {
        return (this.top + this.bottom) * 0.5f;
    }

    public void setEmpty() {
        this.bottom = 0;
        this.top = 0;
        this.right = 0;
        this.left = 0;
    }

    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(Rect src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    public void offset(int dx, int dy) {
        this.left += dx;
        this.top += dy;
        this.right += dx;
        this.bottom += dy;
    }

    public void offsetTo(int newLeft, int newTop) {
        this.right += newLeft - this.left;
        this.bottom += newTop - this.top;
        this.left = newLeft;
        this.top = newTop;
    }

    public void inset(int dx, int dy) {
        this.left += dx;
        this.top += dy;
        this.right -= dx;
        this.bottom -= dy;
    }

    public void inset(Rect insets) {
        this.left += insets.left;
        this.top += insets.top;
        this.right -= insets.right;
        this.bottom -= insets.bottom;
    }

    public void inset(Insets insets) {
        this.left += insets.left;
        this.top += insets.top;
        this.right -= insets.right;
        this.bottom -= insets.bottom;
    }

    public void inset(int left, int top, int right, int bottom) {
        this.left += left;
        this.top += top;
        this.right -= right;
        this.bottom -= bottom;
    }

    public boolean contains(int x, int y) {
        int i;
        int i2;
        int i3 = this.left;
        int i4 = this.right;
        return i3 < i4 && (i = this.top) < (i2 = this.bottom) && x >= i3 && x < i4 && y >= i && y < i2;
    }

    public boolean contains(int left, int top, int right, int bottom) {
        int i;
        int i2;
        int i3 = this.left;
        int i4 = this.right;
        return i3 < i4 && (i = this.top) < (i2 = this.bottom) && i3 <= left && i <= top && i4 >= right && i2 >= bottom;
    }

    public boolean contains(Rect r) {
        int i;
        int i2;
        int i3 = this.left;
        int i4 = this.right;
        return i3 < i4 && (i = this.top) < (i2 = this.bottom) && i3 <= r.left && i <= r.top && i4 >= r.right && i2 >= r.bottom;
    }

    public boolean intersect(int left, int top, int right, int bottom) {
        int i;
        int i2;
        int i3;
        int i4 = this.left;
        if (i4 < right && left < (i = this.right) && (i2 = this.top) < bottom && top < (i3 = this.bottom)) {
            if (i4 < left) {
                this.left = left;
            }
            if (i2 < top) {
                this.top = top;
            }
            if (i > right) {
                this.right = right;
            }
            if (i3 > bottom) {
                this.bottom = bottom;
                return true;
            }
            return true;
        }
        return false;
    }

    public boolean intersect(Rect r) {
        return intersect(r.left, r.top, r.right, r.bottom);
    }

    public void intersectUnchecked(Rect other) {
        this.left = Math.max(this.left, other.left);
        this.top = Math.max(this.top, other.top);
        this.right = Math.min(this.right, other.right);
        this.bottom = Math.min(this.bottom, other.bottom);
    }

    public boolean setIntersect(Rect a, Rect b) {
        int i;
        int i2 = a.left;
        if (i2 < b.right && (i = b.left) < a.right && a.top < b.bottom && b.top < a.bottom) {
            this.left = Math.max(i2, i);
            this.top = Math.max(a.top, b.top);
            this.right = Math.min(a.right, b.right);
            this.bottom = Math.min(a.bottom, b.bottom);
            return true;
        }
        return false;
    }

    public boolean intersects(int left, int top, int right, int bottom) {
        return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
    }

    public static boolean intersects(Rect a, Rect b) {
        return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
    }

    public void union(int left, int top, int right, int bottom) {
        int i;
        int i2;
        if (left < right && top < bottom) {
            int i3 = this.left;
            int i4 = this.right;
            if (i3 < i4 && (i = this.top) < (i2 = this.bottom)) {
                if (i3 > left) {
                    this.left = left;
                }
                if (i > top) {
                    this.top = top;
                }
                if (i4 < right) {
                    this.right = right;
                }
                if (i2 < bottom) {
                    this.bottom = bottom;
                    return;
                }
                return;
            }
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public void union(Rect r) {
        union(r.left, r.top, r.right, r.bottom);
    }

    public void union(int x, int y) {
        if (x < this.left) {
            this.left = x;
        } else if (x > this.right) {
            this.right = x;
        }
        if (y < this.top) {
            this.top = y;
        } else if (y > this.bottom) {
            this.bottom = y;
        }
    }

    public void sort() {
        int i = this.left;
        int i2 = this.right;
        if (i > i2) {
            int temp = this.left;
            this.left = i2;
            this.right = temp;
        }
        int temp2 = this.top;
        int i3 = this.bottom;
        if (temp2 > i3) {
            int temp3 = this.top;
            this.top = i3;
            this.bottom = temp3;
        }
    }

    public void splitVertically(Rect... splits) {
        int count = splits.length;
        int splitWidth = width() / count;
        for (int i = 0; i < count; i++) {
            Rect split = splits[i];
            int i2 = this.left + (splitWidth * i);
            split.left = i2;
            split.top = this.top;
            split.right = i2 + splitWidth;
            split.bottom = this.bottom;
        }
    }

    public void splitHorizontally(Rect... outSplits) {
        int count = outSplits.length;
        int splitHeight = height() / count;
        for (int i = 0; i < count; i++) {
            Rect split = outSplits[i];
            split.left = this.left;
            int i2 = this.top + (splitHeight * i);
            split.top = i2;
            split.right = this.right;
            split.bottom = i2 + splitHeight;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.left);
        out.writeInt(this.top);
        out.writeInt(this.right);
        out.writeInt(this.bottom);
    }

    public void readFromParcel(Parcel in) {
        this.left = in.readInt();
        this.top = in.readInt();
        this.right = in.readInt();
        this.bottom = in.readInt();
    }

    public void scale(float scale) {
        if (scale != 1.0f) {
            this.left = (int) ((this.left * scale) + 0.5f);
            this.top = (int) ((this.top * scale) + 0.5f);
            this.right = (int) ((this.right * scale) + 0.5f);
            this.bottom = (int) ((this.bottom * scale) + 0.5f);
        }
    }
}
