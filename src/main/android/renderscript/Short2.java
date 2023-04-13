package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Short2 {

    /* renamed from: x */
    public short f403x;

    /* renamed from: y */
    public short f404y;

    public Short2() {
    }

    public Short2(short i) {
        this.f404y = i;
        this.f403x = i;
    }

    public Short2(short x, short y) {
        this.f403x = x;
        this.f404y = y;
    }

    public Short2(Short2 source) {
        this.f403x = source.f403x;
        this.f404y = source.f404y;
    }

    public void add(Short2 a) {
        this.f403x = (short) (this.f403x + a.f403x);
        this.f404y = (short) (this.f404y + a.f404y);
    }

    public static Short2 add(Short2 a, Short2 b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x + b.f403x);
        result.f404y = (short) (a.f404y + b.f404y);
        return result;
    }

    public void add(short value) {
        this.f403x = (short) (this.f403x + value);
        this.f404y = (short) (this.f404y + value);
    }

    public static Short2 add(Short2 a, short b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x + b);
        result.f404y = (short) (a.f404y + b);
        return result;
    }

    public void sub(Short2 a) {
        this.f403x = (short) (this.f403x - a.f403x);
        this.f404y = (short) (this.f404y - a.f404y);
    }

    public static Short2 sub(Short2 a, Short2 b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x - b.f403x);
        result.f404y = (short) (a.f404y - b.f404y);
        return result;
    }

    public void sub(short value) {
        this.f403x = (short) (this.f403x - value);
        this.f404y = (short) (this.f404y - value);
    }

    public static Short2 sub(Short2 a, short b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x - b);
        result.f404y = (short) (a.f404y - b);
        return result;
    }

    public void mul(Short2 a) {
        this.f403x = (short) (this.f403x * a.f403x);
        this.f404y = (short) (this.f404y * a.f404y);
    }

    public static Short2 mul(Short2 a, Short2 b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x * b.f403x);
        result.f404y = (short) (a.f404y * b.f404y);
        return result;
    }

    public void mul(short value) {
        this.f403x = (short) (this.f403x * value);
        this.f404y = (short) (this.f404y * value);
    }

    public static Short2 mul(Short2 a, short b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x * b);
        result.f404y = (short) (a.f404y * b);
        return result;
    }

    public void div(Short2 a) {
        this.f403x = (short) (this.f403x / a.f403x);
        this.f404y = (short) (this.f404y / a.f404y);
    }

    public static Short2 div(Short2 a, Short2 b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x / b.f403x);
        result.f404y = (short) (a.f404y / b.f404y);
        return result;
    }

    public void div(short value) {
        this.f403x = (short) (this.f403x / value);
        this.f404y = (short) (this.f404y / value);
    }

    public static Short2 div(Short2 a, short b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x / b);
        result.f404y = (short) (a.f404y / b);
        return result;
    }

    public void mod(Short2 a) {
        this.f403x = (short) (this.f403x % a.f403x);
        this.f404y = (short) (this.f404y % a.f404y);
    }

    public static Short2 mod(Short2 a, Short2 b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x % b.f403x);
        result.f404y = (short) (a.f404y % b.f404y);
        return result;
    }

    public void mod(short value) {
        this.f403x = (short) (this.f403x % value);
        this.f404y = (short) (this.f404y % value);
    }

    public static Short2 mod(Short2 a, short b) {
        Short2 result = new Short2();
        result.f403x = (short) (a.f403x % b);
        result.f404y = (short) (a.f404y % b);
        return result;
    }

    public short length() {
        return (short) 2;
    }

    public void negate() {
        this.f403x = (short) (-this.f403x);
        this.f404y = (short) (-this.f404y);
    }

    public short dotProduct(Short2 a) {
        return (short) ((this.f403x * a.f403x) + (this.f404y * a.f404y));
    }

    public static short dotProduct(Short2 a, Short2 b) {
        return (short) ((b.f403x * a.f403x) + (b.f404y * a.f404y));
    }

    public void addMultiple(Short2 a, short factor) {
        this.f403x = (short) (this.f403x + (a.f403x * factor));
        this.f404y = (short) (this.f404y + (a.f404y * factor));
    }

    public void set(Short2 a) {
        this.f403x = a.f403x;
        this.f404y = a.f404y;
    }

    public void setValues(short a, short b) {
        this.f403x = a;
        this.f404y = b;
    }

    public short elementSum() {
        return (short) (this.f403x + this.f404y);
    }

    public short get(int i) {
        switch (i) {
            case 0:
                return this.f403x;
            case 1:
                return this.f404y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, short value) {
        switch (i) {
            case 0:
                this.f403x = value;
                return;
            case 1:
                this.f404y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, short value) {
        switch (i) {
            case 0:
                this.f403x = (short) (this.f403x + value);
                return;
            case 1:
                this.f404y = (short) (this.f404y + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(short[] data, int offset) {
        data[offset] = this.f403x;
        data[offset + 1] = this.f404y;
    }
}
