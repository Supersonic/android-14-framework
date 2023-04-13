package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Byte2 {

    /* renamed from: x */
    public byte f349x;

    /* renamed from: y */
    public byte f350y;

    public Byte2() {
    }

    public Byte2(byte initX, byte initY) {
        this.f349x = initX;
        this.f350y = initY;
    }

    public Byte2(Byte2 source) {
        this.f349x = source.f349x;
        this.f350y = source.f350y;
    }

    public void add(Byte2 a) {
        this.f349x = (byte) (this.f349x + a.f349x);
        this.f350y = (byte) (this.f350y + a.f350y);
    }

    public static Byte2 add(Byte2 a, Byte2 b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x + b.f349x);
        result.f350y = (byte) (a.f350y + b.f350y);
        return result;
    }

    public void add(byte value) {
        this.f349x = (byte) (this.f349x + value);
        this.f350y = (byte) (this.f350y + value);
    }

    public static Byte2 add(Byte2 a, byte b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x + b);
        result.f350y = (byte) (a.f350y + b);
        return result;
    }

    public void sub(Byte2 a) {
        this.f349x = (byte) (this.f349x - a.f349x);
        this.f350y = (byte) (this.f350y - a.f350y);
    }

    public static Byte2 sub(Byte2 a, Byte2 b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x - b.f349x);
        result.f350y = (byte) (a.f350y - b.f350y);
        return result;
    }

    public void sub(byte value) {
        this.f349x = (byte) (this.f349x - value);
        this.f350y = (byte) (this.f350y - value);
    }

    public static Byte2 sub(Byte2 a, byte b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x - b);
        result.f350y = (byte) (a.f350y - b);
        return result;
    }

    public void mul(Byte2 a) {
        this.f349x = (byte) (this.f349x * a.f349x);
        this.f350y = (byte) (this.f350y * a.f350y);
    }

    public static Byte2 mul(Byte2 a, Byte2 b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x * b.f349x);
        result.f350y = (byte) (a.f350y * b.f350y);
        return result;
    }

    public void mul(byte value) {
        this.f349x = (byte) (this.f349x * value);
        this.f350y = (byte) (this.f350y * value);
    }

    public static Byte2 mul(Byte2 a, byte b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x * b);
        result.f350y = (byte) (a.f350y * b);
        return result;
    }

    public void div(Byte2 a) {
        this.f349x = (byte) (this.f349x / a.f349x);
        this.f350y = (byte) (this.f350y / a.f350y);
    }

    public static Byte2 div(Byte2 a, Byte2 b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x / b.f349x);
        result.f350y = (byte) (a.f350y / b.f350y);
        return result;
    }

    public void div(byte value) {
        this.f349x = (byte) (this.f349x / value);
        this.f350y = (byte) (this.f350y / value);
    }

    public static Byte2 div(Byte2 a, byte b) {
        Byte2 result = new Byte2();
        result.f349x = (byte) (a.f349x / b);
        result.f350y = (byte) (a.f350y / b);
        return result;
    }

    public byte length() {
        return (byte) 2;
    }

    public void negate() {
        this.f349x = (byte) (-this.f349x);
        this.f350y = (byte) (-this.f350y);
    }

    public byte dotProduct(Byte2 a) {
        return (byte) ((this.f349x * a.f349x) + (this.f350y * a.f350y));
    }

    public static byte dotProduct(Byte2 a, Byte2 b) {
        return (byte) ((b.f349x * a.f349x) + (b.f350y * a.f350y));
    }

    public void addMultiple(Byte2 a, byte factor) {
        this.f349x = (byte) (this.f349x + (a.f349x * factor));
        this.f350y = (byte) (this.f350y + (a.f350y * factor));
    }

    public void set(Byte2 a) {
        this.f349x = a.f349x;
        this.f350y = a.f350y;
    }

    public void setValues(byte a, byte b) {
        this.f349x = a;
        this.f350y = b;
    }

    public byte elementSum() {
        return (byte) (this.f349x + this.f350y);
    }

    public byte get(int i) {
        switch (i) {
            case 0:
                return this.f349x;
            case 1:
                return this.f350y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f349x = value;
                return;
            case 1:
                this.f350y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f349x = (byte) (this.f349x + value);
                return;
            case 1:
                this.f350y = (byte) (this.f350y + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(byte[] data, int offset) {
        data[offset] = this.f349x;
        data[offset + 1] = this.f350y;
    }
}
