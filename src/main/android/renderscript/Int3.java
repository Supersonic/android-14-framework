package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Int3 {

    /* renamed from: x */
    public int f378x;

    /* renamed from: y */
    public int f379y;

    /* renamed from: z */
    public int f380z;

    public Int3() {
    }

    public Int3(int i) {
        this.f380z = i;
        this.f379y = i;
        this.f378x = i;
    }

    public Int3(int x, int y, int z) {
        this.f378x = x;
        this.f379y = y;
        this.f380z = z;
    }

    public Int3(Int3 source) {
        this.f378x = source.f378x;
        this.f379y = source.f379y;
        this.f380z = source.f380z;
    }

    public void add(Int3 a) {
        this.f378x += a.f378x;
        this.f379y += a.f379y;
        this.f380z += a.f380z;
    }

    public static Int3 add(Int3 a, Int3 b) {
        Int3 result = new Int3();
        result.f378x = a.f378x + b.f378x;
        result.f379y = a.f379y + b.f379y;
        result.f380z = a.f380z + b.f380z;
        return result;
    }

    public void add(int value) {
        this.f378x += value;
        this.f379y += value;
        this.f380z += value;
    }

    public static Int3 add(Int3 a, int b) {
        Int3 result = new Int3();
        result.f378x = a.f378x + b;
        result.f379y = a.f379y + b;
        result.f380z = a.f380z + b;
        return result;
    }

    public void sub(Int3 a) {
        this.f378x -= a.f378x;
        this.f379y -= a.f379y;
        this.f380z -= a.f380z;
    }

    public static Int3 sub(Int3 a, Int3 b) {
        Int3 result = new Int3();
        result.f378x = a.f378x - b.f378x;
        result.f379y = a.f379y - b.f379y;
        result.f380z = a.f380z - b.f380z;
        return result;
    }

    public void sub(int value) {
        this.f378x -= value;
        this.f379y -= value;
        this.f380z -= value;
    }

    public static Int3 sub(Int3 a, int b) {
        Int3 result = new Int3();
        result.f378x = a.f378x - b;
        result.f379y = a.f379y - b;
        result.f380z = a.f380z - b;
        return result;
    }

    public void mul(Int3 a) {
        this.f378x *= a.f378x;
        this.f379y *= a.f379y;
        this.f380z *= a.f380z;
    }

    public static Int3 mul(Int3 a, Int3 b) {
        Int3 result = new Int3();
        result.f378x = a.f378x * b.f378x;
        result.f379y = a.f379y * b.f379y;
        result.f380z = a.f380z * b.f380z;
        return result;
    }

    public void mul(int value) {
        this.f378x *= value;
        this.f379y *= value;
        this.f380z *= value;
    }

    public static Int3 mul(Int3 a, int b) {
        Int3 result = new Int3();
        result.f378x = a.f378x * b;
        result.f379y = a.f379y * b;
        result.f380z = a.f380z * b;
        return result;
    }

    public void div(Int3 a) {
        this.f378x /= a.f378x;
        this.f379y /= a.f379y;
        this.f380z /= a.f380z;
    }

    public static Int3 div(Int3 a, Int3 b) {
        Int3 result = new Int3();
        result.f378x = a.f378x / b.f378x;
        result.f379y = a.f379y / b.f379y;
        result.f380z = a.f380z / b.f380z;
        return result;
    }

    public void div(int value) {
        this.f378x /= value;
        this.f379y /= value;
        this.f380z /= value;
    }

    public static Int3 div(Int3 a, int b) {
        Int3 result = new Int3();
        result.f378x = a.f378x / b;
        result.f379y = a.f379y / b;
        result.f380z = a.f380z / b;
        return result;
    }

    public void mod(Int3 a) {
        this.f378x %= a.f378x;
        this.f379y %= a.f379y;
        this.f380z %= a.f380z;
    }

    public static Int3 mod(Int3 a, Int3 b) {
        Int3 result = new Int3();
        result.f378x = a.f378x % b.f378x;
        result.f379y = a.f379y % b.f379y;
        result.f380z = a.f380z % b.f380z;
        return result;
    }

    public void mod(int value) {
        this.f378x %= value;
        this.f379y %= value;
        this.f380z %= value;
    }

    public static Int3 mod(Int3 a, int b) {
        Int3 result = new Int3();
        result.f378x = a.f378x % b;
        result.f379y = a.f379y % b;
        result.f380z = a.f380z % b;
        return result;
    }

    public int length() {
        return 3;
    }

    public void negate() {
        this.f378x = -this.f378x;
        this.f379y = -this.f379y;
        this.f380z = -this.f380z;
    }

    public int dotProduct(Int3 a) {
        return (this.f378x * a.f378x) + (this.f379y * a.f379y) + (this.f380z * a.f380z);
    }

    public static int dotProduct(Int3 a, Int3 b) {
        return (b.f378x * a.f378x) + (b.f379y * a.f379y) + (b.f380z * a.f380z);
    }

    public void addMultiple(Int3 a, int factor) {
        this.f378x += a.f378x * factor;
        this.f379y += a.f379y * factor;
        this.f380z += a.f380z * factor;
    }

    public void set(Int3 a) {
        this.f378x = a.f378x;
        this.f379y = a.f379y;
        this.f380z = a.f380z;
    }

    public void setValues(int a, int b, int c) {
        this.f378x = a;
        this.f379y = b;
        this.f380z = c;
    }

    public int elementSum() {
        return this.f378x + this.f379y + this.f380z;
    }

    public int get(int i) {
        switch (i) {
            case 0:
                return this.f378x;
            case 1:
                return this.f379y;
            case 2:
                return this.f380z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, int value) {
        switch (i) {
            case 0:
                this.f378x = value;
                return;
            case 1:
                this.f379y = value;
                return;
            case 2:
                this.f380z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, int value) {
        switch (i) {
            case 0:
                this.f378x += value;
                return;
            case 1:
                this.f379y += value;
                return;
            case 2:
                this.f380z += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(int[] data, int offset) {
        data[offset] = this.f378x;
        data[offset + 1] = this.f379y;
        data[offset + 2] = this.f380z;
    }
}
