package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Short4 {

    /* renamed from: w */
    public short f408w;

    /* renamed from: x */
    public short f409x;

    /* renamed from: y */
    public short f410y;

    /* renamed from: z */
    public short f411z;

    public Short4() {
    }

    public Short4(short i) {
        this.f408w = i;
        this.f411z = i;
        this.f410y = i;
        this.f409x = i;
    }

    public Short4(short x, short y, short z, short w) {
        this.f409x = x;
        this.f410y = y;
        this.f411z = z;
        this.f408w = w;
    }

    public Short4(Short4 source) {
        this.f409x = source.f409x;
        this.f410y = source.f410y;
        this.f411z = source.f411z;
        this.f408w = source.f408w;
    }

    public void add(Short4 a) {
        this.f409x = (short) (this.f409x + a.f409x);
        this.f410y = (short) (this.f410y + a.f410y);
        this.f411z = (short) (this.f411z + a.f411z);
        this.f408w = (short) (this.f408w + a.f408w);
    }

    public static Short4 add(Short4 a, Short4 b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x + b.f409x);
        result.f410y = (short) (a.f410y + b.f410y);
        result.f411z = (short) (a.f411z + b.f411z);
        result.f408w = (short) (a.f408w + b.f408w);
        return result;
    }

    public void add(short value) {
        this.f409x = (short) (this.f409x + value);
        this.f410y = (short) (this.f410y + value);
        this.f411z = (short) (this.f411z + value);
        this.f408w = (short) (this.f408w + value);
    }

    public static Short4 add(Short4 a, short b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x + b);
        result.f410y = (short) (a.f410y + b);
        result.f411z = (short) (a.f411z + b);
        result.f408w = (short) (a.f408w + b);
        return result;
    }

    public void sub(Short4 a) {
        this.f409x = (short) (this.f409x - a.f409x);
        this.f410y = (short) (this.f410y - a.f410y);
        this.f411z = (short) (this.f411z - a.f411z);
        this.f408w = (short) (this.f408w - a.f408w);
    }

    public static Short4 sub(Short4 a, Short4 b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x - b.f409x);
        result.f410y = (short) (a.f410y - b.f410y);
        result.f411z = (short) (a.f411z - b.f411z);
        result.f408w = (short) (a.f408w - b.f408w);
        return result;
    }

    public void sub(short value) {
        this.f409x = (short) (this.f409x - value);
        this.f410y = (short) (this.f410y - value);
        this.f411z = (short) (this.f411z - value);
        this.f408w = (short) (this.f408w - value);
    }

    public static Short4 sub(Short4 a, short b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x - b);
        result.f410y = (short) (a.f410y - b);
        result.f411z = (short) (a.f411z - b);
        result.f408w = (short) (a.f408w - b);
        return result;
    }

    public void mul(Short4 a) {
        this.f409x = (short) (this.f409x * a.f409x);
        this.f410y = (short) (this.f410y * a.f410y);
        this.f411z = (short) (this.f411z * a.f411z);
        this.f408w = (short) (this.f408w * a.f408w);
    }

    public static Short4 mul(Short4 a, Short4 b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x * b.f409x);
        result.f410y = (short) (a.f410y * b.f410y);
        result.f411z = (short) (a.f411z * b.f411z);
        result.f408w = (short) (a.f408w * b.f408w);
        return result;
    }

    public void mul(short value) {
        this.f409x = (short) (this.f409x * value);
        this.f410y = (short) (this.f410y * value);
        this.f411z = (short) (this.f411z * value);
        this.f408w = (short) (this.f408w * value);
    }

    public static Short4 mul(Short4 a, short b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x * b);
        result.f410y = (short) (a.f410y * b);
        result.f411z = (short) (a.f411z * b);
        result.f408w = (short) (a.f408w * b);
        return result;
    }

    public void div(Short4 a) {
        this.f409x = (short) (this.f409x / a.f409x);
        this.f410y = (short) (this.f410y / a.f410y);
        this.f411z = (short) (this.f411z / a.f411z);
        this.f408w = (short) (this.f408w / a.f408w);
    }

    public static Short4 div(Short4 a, Short4 b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x / b.f409x);
        result.f410y = (short) (a.f410y / b.f410y);
        result.f411z = (short) (a.f411z / b.f411z);
        result.f408w = (short) (a.f408w / b.f408w);
        return result;
    }

    public void div(short value) {
        this.f409x = (short) (this.f409x / value);
        this.f410y = (short) (this.f410y / value);
        this.f411z = (short) (this.f411z / value);
        this.f408w = (short) (this.f408w / value);
    }

    public static Short4 div(Short4 a, short b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x / b);
        result.f410y = (short) (a.f410y / b);
        result.f411z = (short) (a.f411z / b);
        result.f408w = (short) (a.f408w / b);
        return result;
    }

    public void mod(Short4 a) {
        this.f409x = (short) (this.f409x % a.f409x);
        this.f410y = (short) (this.f410y % a.f410y);
        this.f411z = (short) (this.f411z % a.f411z);
        this.f408w = (short) (this.f408w % a.f408w);
    }

    public static Short4 mod(Short4 a, Short4 b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x % b.f409x);
        result.f410y = (short) (a.f410y % b.f410y);
        result.f411z = (short) (a.f411z % b.f411z);
        result.f408w = (short) (a.f408w % b.f408w);
        return result;
    }

    public void mod(short value) {
        this.f409x = (short) (this.f409x % value);
        this.f410y = (short) (this.f410y % value);
        this.f411z = (short) (this.f411z % value);
        this.f408w = (short) (this.f408w % value);
    }

    public static Short4 mod(Short4 a, short b) {
        Short4 result = new Short4();
        result.f409x = (short) (a.f409x % b);
        result.f410y = (short) (a.f410y % b);
        result.f411z = (short) (a.f411z % b);
        result.f408w = (short) (a.f408w % b);
        return result;
    }

    public short length() {
        return (short) 4;
    }

    public void negate() {
        this.f409x = (short) (-this.f409x);
        this.f410y = (short) (-this.f410y);
        this.f411z = (short) (-this.f411z);
        this.f408w = (short) (-this.f408w);
    }

    public short dotProduct(Short4 a) {
        return (short) ((this.f409x * a.f409x) + (this.f410y * a.f410y) + (this.f411z * a.f411z) + (this.f408w * a.f408w));
    }

    public static short dotProduct(Short4 a, Short4 b) {
        return (short) ((b.f409x * a.f409x) + (b.f410y * a.f410y) + (b.f411z * a.f411z) + (b.f408w * a.f408w));
    }

    public void addMultiple(Short4 a, short factor) {
        this.f409x = (short) (this.f409x + (a.f409x * factor));
        this.f410y = (short) (this.f410y + (a.f410y * factor));
        this.f411z = (short) (this.f411z + (a.f411z * factor));
        this.f408w = (short) (this.f408w + (a.f408w * factor));
    }

    public void set(Short4 a) {
        this.f409x = a.f409x;
        this.f410y = a.f410y;
        this.f411z = a.f411z;
        this.f408w = a.f408w;
    }

    public void setValues(short a, short b, short c, short d) {
        this.f409x = a;
        this.f410y = b;
        this.f411z = c;
        this.f408w = d;
    }

    public short elementSum() {
        return (short) (this.f409x + this.f410y + this.f411z + this.f408w);
    }

    public short get(int i) {
        switch (i) {
            case 0:
                return this.f409x;
            case 1:
                return this.f410y;
            case 2:
                return this.f411z;
            case 3:
                return this.f408w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, short value) {
        switch (i) {
            case 0:
                this.f409x = value;
                return;
            case 1:
                this.f410y = value;
                return;
            case 2:
                this.f411z = value;
                return;
            case 3:
                this.f408w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, short value) {
        switch (i) {
            case 0:
                this.f409x = (short) (this.f409x + value);
                return;
            case 1:
                this.f410y = (short) (this.f410y + value);
                return;
            case 2:
                this.f411z = (short) (this.f411z + value);
                return;
            case 3:
                this.f408w = (short) (this.f408w + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(short[] data, int offset) {
        data[offset] = this.f409x;
        data[offset + 1] = this.f410y;
        data[offset + 2] = this.f411z;
        data[offset + 3] = this.f408w;
    }
}
