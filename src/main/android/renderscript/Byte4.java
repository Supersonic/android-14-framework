package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Byte4 {

    /* renamed from: w */
    public byte f354w;

    /* renamed from: x */
    public byte f355x;

    /* renamed from: y */
    public byte f356y;

    /* renamed from: z */
    public byte f357z;

    public Byte4() {
    }

    public Byte4(byte initX, byte initY, byte initZ, byte initW) {
        this.f355x = initX;
        this.f356y = initY;
        this.f357z = initZ;
        this.f354w = initW;
    }

    public Byte4(Byte4 source) {
        this.f355x = source.f355x;
        this.f356y = source.f356y;
        this.f357z = source.f357z;
        this.f354w = source.f354w;
    }

    public void add(Byte4 a) {
        this.f355x = (byte) (this.f355x + a.f355x);
        this.f356y = (byte) (this.f356y + a.f356y);
        this.f357z = (byte) (this.f357z + a.f357z);
        this.f354w = (byte) (this.f354w + a.f354w);
    }

    public static Byte4 add(Byte4 a, Byte4 b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x + b.f355x);
        result.f356y = (byte) (a.f356y + b.f356y);
        result.f357z = (byte) (a.f357z + b.f357z);
        result.f354w = (byte) (a.f354w + b.f354w);
        return result;
    }

    public void add(byte value) {
        this.f355x = (byte) (this.f355x + value);
        this.f356y = (byte) (this.f356y + value);
        this.f357z = (byte) (this.f357z + value);
        this.f354w = (byte) (this.f354w + value);
    }

    public static Byte4 add(Byte4 a, byte b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x + b);
        result.f356y = (byte) (a.f356y + b);
        result.f357z = (byte) (a.f357z + b);
        result.f354w = (byte) (a.f354w + b);
        return result;
    }

    public void sub(Byte4 a) {
        this.f355x = (byte) (this.f355x - a.f355x);
        this.f356y = (byte) (this.f356y - a.f356y);
        this.f357z = (byte) (this.f357z - a.f357z);
        this.f354w = (byte) (this.f354w - a.f354w);
    }

    public static Byte4 sub(Byte4 a, Byte4 b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x - b.f355x);
        result.f356y = (byte) (a.f356y - b.f356y);
        result.f357z = (byte) (a.f357z - b.f357z);
        result.f354w = (byte) (a.f354w - b.f354w);
        return result;
    }

    public void sub(byte value) {
        this.f355x = (byte) (this.f355x - value);
        this.f356y = (byte) (this.f356y - value);
        this.f357z = (byte) (this.f357z - value);
        this.f354w = (byte) (this.f354w - value);
    }

    public static Byte4 sub(Byte4 a, byte b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x - b);
        result.f356y = (byte) (a.f356y - b);
        result.f357z = (byte) (a.f357z - b);
        result.f354w = (byte) (a.f354w - b);
        return result;
    }

    public void mul(Byte4 a) {
        this.f355x = (byte) (this.f355x * a.f355x);
        this.f356y = (byte) (this.f356y * a.f356y);
        this.f357z = (byte) (this.f357z * a.f357z);
        this.f354w = (byte) (this.f354w * a.f354w);
    }

    public static Byte4 mul(Byte4 a, Byte4 b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x * b.f355x);
        result.f356y = (byte) (a.f356y * b.f356y);
        result.f357z = (byte) (a.f357z * b.f357z);
        result.f354w = (byte) (a.f354w * b.f354w);
        return result;
    }

    public void mul(byte value) {
        this.f355x = (byte) (this.f355x * value);
        this.f356y = (byte) (this.f356y * value);
        this.f357z = (byte) (this.f357z * value);
        this.f354w = (byte) (this.f354w * value);
    }

    public static Byte4 mul(Byte4 a, byte b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x * b);
        result.f356y = (byte) (a.f356y * b);
        result.f357z = (byte) (a.f357z * b);
        result.f354w = (byte) (a.f354w * b);
        return result;
    }

    public void div(Byte4 a) {
        this.f355x = (byte) (this.f355x / a.f355x);
        this.f356y = (byte) (this.f356y / a.f356y);
        this.f357z = (byte) (this.f357z / a.f357z);
        this.f354w = (byte) (this.f354w / a.f354w);
    }

    public static Byte4 div(Byte4 a, Byte4 b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x / b.f355x);
        result.f356y = (byte) (a.f356y / b.f356y);
        result.f357z = (byte) (a.f357z / b.f357z);
        result.f354w = (byte) (a.f354w / b.f354w);
        return result;
    }

    public void div(byte value) {
        this.f355x = (byte) (this.f355x / value);
        this.f356y = (byte) (this.f356y / value);
        this.f357z = (byte) (this.f357z / value);
        this.f354w = (byte) (this.f354w / value);
    }

    public static Byte4 div(Byte4 a, byte b) {
        Byte4 result = new Byte4();
        result.f355x = (byte) (a.f355x / b);
        result.f356y = (byte) (a.f356y / b);
        result.f357z = (byte) (a.f357z / b);
        result.f354w = (byte) (a.f354w / b);
        return result;
    }

    public byte length() {
        return (byte) 4;
    }

    public void negate() {
        this.f355x = (byte) (-this.f355x);
        this.f356y = (byte) (-this.f356y);
        this.f357z = (byte) (-this.f357z);
        this.f354w = (byte) (-this.f354w);
    }

    public byte dotProduct(Byte4 a) {
        return (byte) ((this.f355x * a.f355x) + (this.f356y * a.f356y) + (this.f357z * a.f357z) + (this.f354w * a.f354w));
    }

    public static byte dotProduct(Byte4 a, Byte4 b) {
        return (byte) ((b.f355x * a.f355x) + (b.f356y * a.f356y) + (b.f357z * a.f357z) + (b.f354w * a.f354w));
    }

    public void addMultiple(Byte4 a, byte factor) {
        this.f355x = (byte) (this.f355x + (a.f355x * factor));
        this.f356y = (byte) (this.f356y + (a.f356y * factor));
        this.f357z = (byte) (this.f357z + (a.f357z * factor));
        this.f354w = (byte) (this.f354w + (a.f354w * factor));
    }

    public void set(Byte4 a) {
        this.f355x = a.f355x;
        this.f356y = a.f356y;
        this.f357z = a.f357z;
        this.f354w = a.f354w;
    }

    public void setValues(byte a, byte b, byte c, byte d) {
        this.f355x = a;
        this.f356y = b;
        this.f357z = c;
        this.f354w = d;
    }

    public byte elementSum() {
        return (byte) (this.f355x + this.f356y + this.f357z + this.f354w);
    }

    public byte get(int i) {
        switch (i) {
            case 0:
                return this.f355x;
            case 1:
                return this.f356y;
            case 2:
                return this.f357z;
            case 3:
                return this.f354w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f355x = value;
                return;
            case 1:
                this.f356y = value;
                return;
            case 2:
                this.f357z = value;
                return;
            case 3:
                this.f354w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f355x = (byte) (this.f355x + value);
                return;
            case 1:
                this.f356y = (byte) (this.f356y + value);
                return;
            case 2:
                this.f357z = (byte) (this.f357z + value);
                return;
            case 3:
                this.f354w = (byte) (this.f354w + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(byte[] data, int offset) {
        data[offset] = this.f355x;
        data[offset + 1] = this.f356y;
        data[offset + 2] = this.f357z;
        data[offset + 3] = this.f354w;
    }
}
