package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Short3 {

    /* renamed from: x */
    public short f405x;

    /* renamed from: y */
    public short f406y;

    /* renamed from: z */
    public short f407z;

    public Short3() {
    }

    public Short3(short i) {
        this.f407z = i;
        this.f406y = i;
        this.f405x = i;
    }

    public Short3(short x, short y, short z) {
        this.f405x = x;
        this.f406y = y;
        this.f407z = z;
    }

    public Short3(Short3 source) {
        this.f405x = source.f405x;
        this.f406y = source.f406y;
        this.f407z = source.f407z;
    }

    public void add(Short3 a) {
        this.f405x = (short) (this.f405x + a.f405x);
        this.f406y = (short) (this.f406y + a.f406y);
        this.f407z = (short) (this.f407z + a.f407z);
    }

    public static Short3 add(Short3 a, Short3 b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x + b.f405x);
        result.f406y = (short) (a.f406y + b.f406y);
        result.f407z = (short) (a.f407z + b.f407z);
        return result;
    }

    public void add(short value) {
        this.f405x = (short) (this.f405x + value);
        this.f406y = (short) (this.f406y + value);
        this.f407z = (short) (this.f407z + value);
    }

    public static Short3 add(Short3 a, short b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x + b);
        result.f406y = (short) (a.f406y + b);
        result.f407z = (short) (a.f407z + b);
        return result;
    }

    public void sub(Short3 a) {
        this.f405x = (short) (this.f405x - a.f405x);
        this.f406y = (short) (this.f406y - a.f406y);
        this.f407z = (short) (this.f407z - a.f407z);
    }

    public static Short3 sub(Short3 a, Short3 b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x - b.f405x);
        result.f406y = (short) (a.f406y - b.f406y);
        result.f407z = (short) (a.f407z - b.f407z);
        return result;
    }

    public void sub(short value) {
        this.f405x = (short) (this.f405x - value);
        this.f406y = (short) (this.f406y - value);
        this.f407z = (short) (this.f407z - value);
    }

    public static Short3 sub(Short3 a, short b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x - b);
        result.f406y = (short) (a.f406y - b);
        result.f407z = (short) (a.f407z - b);
        return result;
    }

    public void mul(Short3 a) {
        this.f405x = (short) (this.f405x * a.f405x);
        this.f406y = (short) (this.f406y * a.f406y);
        this.f407z = (short) (this.f407z * a.f407z);
    }

    public static Short3 mul(Short3 a, Short3 b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x * b.f405x);
        result.f406y = (short) (a.f406y * b.f406y);
        result.f407z = (short) (a.f407z * b.f407z);
        return result;
    }

    public void mul(short value) {
        this.f405x = (short) (this.f405x * value);
        this.f406y = (short) (this.f406y * value);
        this.f407z = (short) (this.f407z * value);
    }

    public static Short3 mul(Short3 a, short b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x * b);
        result.f406y = (short) (a.f406y * b);
        result.f407z = (short) (a.f407z * b);
        return result;
    }

    public void div(Short3 a) {
        this.f405x = (short) (this.f405x / a.f405x);
        this.f406y = (short) (this.f406y / a.f406y);
        this.f407z = (short) (this.f407z / a.f407z);
    }

    public static Short3 div(Short3 a, Short3 b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x / b.f405x);
        result.f406y = (short) (a.f406y / b.f406y);
        result.f407z = (short) (a.f407z / b.f407z);
        return result;
    }

    public void div(short value) {
        this.f405x = (short) (this.f405x / value);
        this.f406y = (short) (this.f406y / value);
        this.f407z = (short) (this.f407z / value);
    }

    public static Short3 div(Short3 a, short b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x / b);
        result.f406y = (short) (a.f406y / b);
        result.f407z = (short) (a.f407z / b);
        return result;
    }

    public void mod(Short3 a) {
        this.f405x = (short) (this.f405x % a.f405x);
        this.f406y = (short) (this.f406y % a.f406y);
        this.f407z = (short) (this.f407z % a.f407z);
    }

    public static Short3 mod(Short3 a, Short3 b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x % b.f405x);
        result.f406y = (short) (a.f406y % b.f406y);
        result.f407z = (short) (a.f407z % b.f407z);
        return result;
    }

    public void mod(short value) {
        this.f405x = (short) (this.f405x % value);
        this.f406y = (short) (this.f406y % value);
        this.f407z = (short) (this.f407z % value);
    }

    public static Short3 mod(Short3 a, short b) {
        Short3 result = new Short3();
        result.f405x = (short) (a.f405x % b);
        result.f406y = (short) (a.f406y % b);
        result.f407z = (short) (a.f407z % b);
        return result;
    }

    public short length() {
        return (short) 3;
    }

    public void negate() {
        this.f405x = (short) (-this.f405x);
        this.f406y = (short) (-this.f406y);
        this.f407z = (short) (-this.f407z);
    }

    public short dotProduct(Short3 a) {
        return (short) ((this.f405x * a.f405x) + (this.f406y * a.f406y) + (this.f407z * a.f407z));
    }

    public static short dotProduct(Short3 a, Short3 b) {
        return (short) ((b.f405x * a.f405x) + (b.f406y * a.f406y) + (b.f407z * a.f407z));
    }

    public void addMultiple(Short3 a, short factor) {
        this.f405x = (short) (this.f405x + (a.f405x * factor));
        this.f406y = (short) (this.f406y + (a.f406y * factor));
        this.f407z = (short) (this.f407z + (a.f407z * factor));
    }

    public void set(Short3 a) {
        this.f405x = a.f405x;
        this.f406y = a.f406y;
        this.f407z = a.f407z;
    }

    public void setValues(short a, short b, short c) {
        this.f405x = a;
        this.f406y = b;
        this.f407z = c;
    }

    public short elementSum() {
        return (short) (this.f405x + this.f406y + this.f407z);
    }

    public short get(int i) {
        switch (i) {
            case 0:
                return this.f405x;
            case 1:
                return this.f406y;
            case 2:
                return this.f407z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, short value) {
        switch (i) {
            case 0:
                this.f405x = value;
                return;
            case 1:
                this.f406y = value;
                return;
            case 2:
                this.f407z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, short value) {
        switch (i) {
            case 0:
                this.f405x = (short) (this.f405x + value);
                return;
            case 1:
                this.f406y = (short) (this.f406y + value);
                return;
            case 2:
                this.f407z = (short) (this.f407z + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(short[] data, int offset) {
        data[offset] = this.f405x;
        data[offset + 1] = this.f406y;
        data[offset + 2] = this.f407z;
    }
}
