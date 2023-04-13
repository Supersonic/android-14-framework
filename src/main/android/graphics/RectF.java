package android.graphics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.FastMath;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class RectF implements Parcelable {
    public static final Parcelable.Creator<RectF> CREATOR = new Parcelable.Creator<RectF>() { // from class: android.graphics.RectF.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RectF createFromParcel(Parcel in) {
            RectF r = new RectF();
            r.readFromParcel(in);
            return r;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RectF[] newArray(int size) {
            return new RectF[size];
        }
    };
    public float bottom;
    public float left;
    public float right;
    public float top;

    public RectF() {
    }

    public RectF(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public RectF(RectF r) {
        if (r == null) {
            this.bottom = 0.0f;
            this.right = 0.0f;
            this.top = 0.0f;
            this.left = 0.0f;
            return;
        }
        this.left = r.left;
        this.top = r.top;
        this.right = r.right;
        this.bottom = r.bottom;
    }

    public RectF(Rect r) {
        if (r == null) {
            this.bottom = 0.0f;
            this.right = 0.0f;
            this.top = 0.0f;
            this.left = 0.0f;
            return;
        }
        this.left = r.left;
        this.top = r.top;
        this.right = r.right;
        this.bottom = r.bottom;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RectF r = (RectF) o;
        if (this.left == r.left && this.top == r.top && this.right == r.right && this.bottom == r.bottom) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        float f = this.left;
        int result = f != 0.0f ? Float.floatToIntBits(f) : 0;
        int i = result * 31;
        float f2 = this.top;
        int result2 = i + (f2 != 0.0f ? Float.floatToIntBits(f2) : 0);
        int result3 = result2 * 31;
        float f3 = this.right;
        int result4 = (result3 + (f3 != 0.0f ? Float.floatToIntBits(f3) : 0)) * 31;
        float f4 = this.bottom;
        return result4 + (f4 != 0.0f ? Float.floatToIntBits(f4) : 0);
    }

    public String toString() {
        return "RectF(" + this.left + ", " + this.top + ", " + this.right + ", " + this.bottom + NavigationBarInflaterView.KEY_CODE_END;
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

    public final boolean isEmpty() {
        return this.left >= this.right || this.top >= this.bottom;
    }

    public final float width() {
        return this.right - this.left;
    }

    public final float height() {
        return this.bottom - this.top;
    }

    public final float centerX() {
        return (this.left + this.right) * 0.5f;
    }

    public final float centerY() {
        return (this.top + this.bottom) * 0.5f;
    }

    public void setEmpty() {
        this.bottom = 0.0f;
        this.top = 0.0f;
        this.right = 0.0f;
        this.left = 0.0f;
    }

    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(RectF src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    public void set(Rect src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    public void offset(float dx, float dy) {
        this.left += dx;
        this.top += dy;
        this.right += dx;
        this.bottom += dy;
    }

    public void offsetTo(float newLeft, float newTop) {
        this.right += newLeft - this.left;
        this.bottom += newTop - this.top;
        this.left = newLeft;
        this.top = newTop;
    }

    public void inset(float dx, float dy) {
        this.left += dx;
        this.top += dy;
        this.right -= dx;
        this.bottom -= dy;
    }

    public boolean contains(float x, float y) {
        float f = this.left;
        float f2 = this.right;
        if (f < f2) {
            float f3 = this.top;
            float f4 = this.bottom;
            if (f3 < f4 && x >= f && x < f2 && y >= f3 && y < f4) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(float left, float top, float right, float bottom) {
        float f = this.left;
        float f2 = this.right;
        if (f < f2) {
            float f3 = this.top;
            float f4 = this.bottom;
            if (f3 < f4 && f <= left && f3 <= top && f2 >= right && f4 >= bottom) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(RectF r) {
        float f = this.left;
        float f2 = this.right;
        if (f < f2) {
            float f3 = this.top;
            float f4 = this.bottom;
            if (f3 < f4 && f <= r.left && f3 <= r.top && f2 >= r.right && f4 >= r.bottom) {
                return true;
            }
        }
        return false;
    }

    public boolean intersect(float left, float top, float right, float bottom) {
        float f = this.left;
        if (f < right) {
            float f2 = this.right;
            if (left < f2) {
                float f3 = this.top;
                if (f3 < bottom) {
                    float f4 = this.bottom;
                    if (top < f4) {
                        if (f < left) {
                            this.left = left;
                        }
                        if (f3 < top) {
                            this.top = top;
                        }
                        if (f2 > right) {
                            this.right = right;
                        }
                        if (f4 > bottom) {
                            this.bottom = bottom;
                            return true;
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean intersect(RectF r) {
        return intersect(r.left, r.top, r.right, r.bottom);
    }

    public boolean setIntersect(RectF a, RectF b) {
        float f = a.left;
        if (f < b.right) {
            float f2 = b.left;
            if (f2 < a.right && a.top < b.bottom && b.top < a.bottom) {
                this.left = Math.max(f, f2);
                this.top = Math.max(a.top, b.top);
                this.right = Math.min(a.right, b.right);
                this.bottom = Math.min(a.bottom, b.bottom);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean intersects(float left, float top, float right, float bottom) {
        return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
    }

    public static boolean intersects(RectF a, RectF b) {
        return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
    }

    public void round(Rect dst) {
        dst.set(FastMath.round(this.left), FastMath.round(this.top), FastMath.round(this.right), FastMath.round(this.bottom));
    }

    public void roundOut(Rect dst) {
        dst.set((int) Math.floor(this.left), (int) Math.floor(this.top), (int) Math.ceil(this.right), (int) Math.ceil(this.bottom));
    }

    public void union(float left, float top, float right, float bottom) {
        if (left < right && top < bottom) {
            float f = this.left;
            float f2 = this.right;
            if (f < f2) {
                float f3 = this.top;
                float f4 = this.bottom;
                if (f3 < f4) {
                    if (f > left) {
                        this.left = left;
                    }
                    if (f3 > top) {
                        this.top = top;
                    }
                    if (f2 < right) {
                        this.right = right;
                    }
                    if (f4 < bottom) {
                        this.bottom = bottom;
                        return;
                    }
                    return;
                }
            }
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public void union(RectF r) {
        union(r.left, r.top, r.right, r.bottom);
    }

    public void union(float x, float y) {
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
        float f = this.left;
        float f2 = this.right;
        if (f > f2) {
            float temp = this.left;
            this.left = f2;
            this.right = temp;
        }
        float temp2 = this.top;
        float f3 = this.bottom;
        if (temp2 > f3) {
            float temp3 = this.top;
            this.top = f3;
            this.bottom = temp3;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.left);
        out.writeFloat(this.top);
        out.writeFloat(this.right);
        out.writeFloat(this.bottom);
    }

    public void readFromParcel(Parcel in) {
        this.left = in.readFloat();
        this.top = in.readFloat();
        this.right = in.readFloat();
        this.bottom = in.readFloat();
    }

    public void scale(float scale) {
        if (scale != 1.0f) {
            this.left *= scale;
            this.top *= scale;
            this.right *= scale;
            this.bottom *= scale;
        }
    }
}
