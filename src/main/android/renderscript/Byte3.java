package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Byte3 {

    /* renamed from: x */
    public byte f351x;

    /* renamed from: y */
    public byte f352y;

    /* renamed from: z */
    public byte f353z;

    public Byte3() {
    }

    public Byte3(byte initX, byte initY, byte initZ) {
        this.f351x = initX;
        this.f352y = initY;
        this.f353z = initZ;
    }

    public Byte3(Byte3 source) {
        this.f351x = source.f351x;
        this.f352y = source.f352y;
        this.f353z = source.f353z;
    }

    public void add(Byte3 a) {
        this.f351x = (byte) (this.f351x + a.f351x);
        this.f352y = (byte) (this.f352y + a.f352y);
        this.f353z = (byte) (this.f353z + a.f353z);
    }

    public static Byte3 add(Byte3 a, Byte3 b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x + b.f351x);
        result.f352y = (byte) (a.f352y + b.f352y);
        result.f353z = (byte) (a.f353z + b.f353z);
        return result;
    }

    public void add(byte value) {
        this.f351x = (byte) (this.f351x + value);
        this.f352y = (byte) (this.f352y + value);
        this.f353z = (byte) (this.f353z + value);
    }

    public static Byte3 add(Byte3 a, byte b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x + b);
        result.f352y = (byte) (a.f352y + b);
        result.f353z = (byte) (a.f353z + b);
        return result;
    }

    public void sub(Byte3 a) {
        this.f351x = (byte) (this.f351x - a.f351x);
        this.f352y = (byte) (this.f352y - a.f352y);
        this.f353z = (byte) (this.f353z - a.f353z);
    }

    public static Byte3 sub(Byte3 a, Byte3 b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x - b.f351x);
        result.f352y = (byte) (a.f352y - b.f352y);
        result.f353z = (byte) (a.f353z - b.f353z);
        return result;
    }

    public void sub(byte value) {
        this.f351x = (byte) (this.f351x - value);
        this.f352y = (byte) (this.f352y - value);
        this.f353z = (byte) (this.f353z - value);
    }

    public static Byte3 sub(Byte3 a, byte b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x - b);
        result.f352y = (byte) (a.f352y - b);
        result.f353z = (byte) (a.f353z - b);
        return result;
    }

    public void mul(Byte3 a) {
        this.f351x = (byte) (this.f351x * a.f351x);
        this.f352y = (byte) (this.f352y * a.f352y);
        this.f353z = (byte) (this.f353z * a.f353z);
    }

    public static Byte3 mul(Byte3 a, Byte3 b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x * b.f351x);
        result.f352y = (byte) (a.f352y * b.f352y);
        result.f353z = (byte) (a.f353z * b.f353z);
        return result;
    }

    public void mul(byte value) {
        this.f351x = (byte) (this.f351x * value);
        this.f352y = (byte) (this.f352y * value);
        this.f353z = (byte) (this.f353z * value);
    }

    public static Byte3 mul(Byte3 a, byte b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x * b);
        result.f352y = (byte) (a.f352y * b);
        result.f353z = (byte) (a.f353z * b);
        return result;
    }

    public void div(Byte3 a) {
        this.f351x = (byte) (this.f351x / a.f351x);
        this.f352y = (byte) (this.f352y / a.f352y);
        this.f353z = (byte) (this.f353z / a.f353z);
    }

    public static Byte3 div(Byte3 a, Byte3 b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x / b.f351x);
        result.f352y = (byte) (a.f352y / b.f352y);
        result.f353z = (byte) (a.f353z / b.f353z);
        return result;
    }

    public void div(byte value) {
        this.f351x = (byte) (this.f351x / value);
        this.f352y = (byte) (this.f352y / value);
        this.f353z = (byte) (this.f353z / value);
    }

    public static Byte3 div(Byte3 a, byte b) {
        Byte3 result = new Byte3();
        result.f351x = (byte) (a.f351x / b);
        result.f352y = (byte) (a.f352y / b);
        result.f353z = (byte) (a.f353z / b);
        return result;
    }

    public byte length() {
        return (byte) 3;
    }

    public void negate() {
        this.f351x = (byte) (-this.f351x);
        this.f352y = (byte) (-this.f352y);
        this.f353z = (byte) (-this.f353z);
    }

    public byte dotProduct(Byte3 a) {
        return (byte) (((byte) (((byte) (this.f351x * a.f351x)) + ((byte) (this.f352y * a.f352y)))) + ((byte) (this.f353z * a.f353z)));
    }

    public static byte dotProduct(Byte3 a, Byte3 b) {
        return (byte) (((byte) (((byte) (b.f351x * a.f351x)) + ((byte) (b.f352y * a.f352y)))) + ((byte) (b.f353z * a.f353z)));
    }

    public void addMultiple(Byte3 a, byte factor) {
        this.f351x = (byte) (this.f351x + (a.f351x * factor));
        this.f352y = (byte) (this.f352y + (a.f352y * factor));
        this.f353z = (byte) (this.f353z + (a.f353z * factor));
    }

    public void set(Byte3 a) {
        this.f351x = a.f351x;
        this.f352y = a.f352y;
        this.f353z = a.f353z;
    }

    public void setValues(byte a, byte b, byte c) {
        this.f351x = a;
        this.f352y = b;
        this.f353z = c;
    }

    public byte elementSum() {
        return (byte) (this.f351x + this.f352y + this.f353z);
    }

    public byte get(int i) {
        switch (i) {
            case 0:
                return this.f351x;
            case 1:
                return this.f352y;
            case 2:
                return this.f353z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f351x = value;
                return;
            case 1:
                this.f352y = value;
                return;
            case 2:
                this.f353z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, byte value) {
        switch (i) {
            case 0:
                this.f351x = (byte) (this.f351x + value);
                return;
            case 1:
                this.f352y = (byte) (this.f352y + value);
                return;
            case 2:
                this.f353z = (byte) (this.f353z + value);
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(byte[] data, int offset) {
        data[offset] = this.f351x;
        data[offset + 1] = this.f352y;
        data[offset + 2] = this.f353z;
    }
}
