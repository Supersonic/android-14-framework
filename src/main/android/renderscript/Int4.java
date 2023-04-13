package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Int4 {

    /* renamed from: w */
    public int f381w;

    /* renamed from: x */
    public int f382x;

    /* renamed from: y */
    public int f383y;

    /* renamed from: z */
    public int f384z;

    public Int4() {
    }

    public Int4(int i) {
        this.f381w = i;
        this.f384z = i;
        this.f383y = i;
        this.f382x = i;
    }

    public Int4(int x, int y, int z, int w) {
        this.f382x = x;
        this.f383y = y;
        this.f384z = z;
        this.f381w = w;
    }

    public Int4(Int4 source) {
        this.f382x = source.f382x;
        this.f383y = source.f383y;
        this.f384z = source.f384z;
        this.f381w = source.f381w;
    }

    public void add(Int4 a) {
        this.f382x += a.f382x;
        this.f383y += a.f383y;
        this.f384z += a.f384z;
        this.f381w += a.f381w;
    }

    public static Int4 add(Int4 a, Int4 b) {
        Int4 result = new Int4();
        result.f382x = a.f382x + b.f382x;
        result.f383y = a.f383y + b.f383y;
        result.f384z = a.f384z + b.f384z;
        result.f381w = a.f381w + b.f381w;
        return result;
    }

    public void add(int value) {
        this.f382x += value;
        this.f383y += value;
        this.f384z += value;
        this.f381w += value;
    }

    public static Int4 add(Int4 a, int b) {
        Int4 result = new Int4();
        result.f382x = a.f382x + b;
        result.f383y = a.f383y + b;
        result.f384z = a.f384z + b;
        result.f381w = a.f381w + b;
        return result;
    }

    public void sub(Int4 a) {
        this.f382x -= a.f382x;
        this.f383y -= a.f383y;
        this.f384z -= a.f384z;
        this.f381w -= a.f381w;
    }

    public static Int4 sub(Int4 a, Int4 b) {
        Int4 result = new Int4();
        result.f382x = a.f382x - b.f382x;
        result.f383y = a.f383y - b.f383y;
        result.f384z = a.f384z - b.f384z;
        result.f381w = a.f381w - b.f381w;
        return result;
    }

    public void sub(int value) {
        this.f382x -= value;
        this.f383y -= value;
        this.f384z -= value;
        this.f381w -= value;
    }

    public static Int4 sub(Int4 a, int b) {
        Int4 result = new Int4();
        result.f382x = a.f382x - b;
        result.f383y = a.f383y - b;
        result.f384z = a.f384z - b;
        result.f381w = a.f381w - b;
        return result;
    }

    public void mul(Int4 a) {
        this.f382x *= a.f382x;
        this.f383y *= a.f383y;
        this.f384z *= a.f384z;
        this.f381w *= a.f381w;
    }

    public static Int4 mul(Int4 a, Int4 b) {
        Int4 result = new Int4();
        result.f382x = a.f382x * b.f382x;
        result.f383y = a.f383y * b.f383y;
        result.f384z = a.f384z * b.f384z;
        result.f381w = a.f381w * b.f381w;
        return result;
    }

    public void mul(int value) {
        this.f382x *= value;
        this.f383y *= value;
        this.f384z *= value;
        this.f381w *= value;
    }

    public static Int4 mul(Int4 a, int b) {
        Int4 result = new Int4();
        result.f382x = a.f382x * b;
        result.f383y = a.f383y * b;
        result.f384z = a.f384z * b;
        result.f381w = a.f381w * b;
        return result;
    }

    public void div(Int4 a) {
        this.f382x /= a.f382x;
        this.f383y /= a.f383y;
        this.f384z /= a.f384z;
        this.f381w /= a.f381w;
    }

    public static Int4 div(Int4 a, Int4 b) {
        Int4 result = new Int4();
        result.f382x = a.f382x / b.f382x;
        result.f383y = a.f383y / b.f383y;
        result.f384z = a.f384z / b.f384z;
        result.f381w = a.f381w / b.f381w;
        return result;
    }

    public void div(int value) {
        this.f382x /= value;
        this.f383y /= value;
        this.f384z /= value;
        this.f381w /= value;
    }

    public static Int4 div(Int4 a, int b) {
        Int4 result = new Int4();
        result.f382x = a.f382x / b;
        result.f383y = a.f383y / b;
        result.f384z = a.f384z / b;
        result.f381w = a.f381w / b;
        return result;
    }

    public void mod(Int4 a) {
        this.f382x %= a.f382x;
        this.f383y %= a.f383y;
        this.f384z %= a.f384z;
        this.f381w %= a.f381w;
    }

    public static Int4 mod(Int4 a, Int4 b) {
        Int4 result = new Int4();
        result.f382x = a.f382x % b.f382x;
        result.f383y = a.f383y % b.f383y;
        result.f384z = a.f384z % b.f384z;
        result.f381w = a.f381w % b.f381w;
        return result;
    }

    public void mod(int value) {
        this.f382x %= value;
        this.f383y %= value;
        this.f384z %= value;
        this.f381w %= value;
    }

    public static Int4 mod(Int4 a, int b) {
        Int4 result = new Int4();
        result.f382x = a.f382x % b;
        result.f383y = a.f383y % b;
        result.f384z = a.f384z % b;
        result.f381w = a.f381w % b;
        return result;
    }

    public int length() {
        return 4;
    }

    public void negate() {
        this.f382x = -this.f382x;
        this.f383y = -this.f383y;
        this.f384z = -this.f384z;
        this.f381w = -this.f381w;
    }

    public int dotProduct(Int4 a) {
        return (this.f382x * a.f382x) + (this.f383y * a.f383y) + (this.f384z * a.f384z) + (this.f381w * a.f381w);
    }

    public static int dotProduct(Int4 a, Int4 b) {
        return (b.f382x * a.f382x) + (b.f383y * a.f383y) + (b.f384z * a.f384z) + (b.f381w * a.f381w);
    }

    public void addMultiple(Int4 a, int factor) {
        this.f382x += a.f382x * factor;
        this.f383y += a.f383y * factor;
        this.f384z += a.f384z * factor;
        this.f381w += a.f381w * factor;
    }

    public void set(Int4 a) {
        this.f382x = a.f382x;
        this.f383y = a.f383y;
        this.f384z = a.f384z;
        this.f381w = a.f381w;
    }

    public void setValues(int a, int b, int c, int d) {
        this.f382x = a;
        this.f383y = b;
        this.f384z = c;
        this.f381w = d;
    }

    public int elementSum() {
        return this.f382x + this.f383y + this.f384z + this.f381w;
    }

    public int get(int i) {
        switch (i) {
            case 0:
                return this.f382x;
            case 1:
                return this.f383y;
            case 2:
                return this.f384z;
            case 3:
                return this.f381w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, int value) {
        switch (i) {
            case 0:
                this.f382x = value;
                return;
            case 1:
                this.f383y = value;
                return;
            case 2:
                this.f384z = value;
                return;
            case 3:
                this.f381w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, int value) {
        switch (i) {
            case 0:
                this.f382x += value;
                return;
            case 1:
                this.f383y += value;
                return;
            case 2:
                this.f384z += value;
                return;
            case 3:
                this.f381w += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(int[] data, int offset) {
        data[offset] = this.f382x;
        data[offset + 1] = this.f383y;
        data[offset + 2] = this.f384z;
        data[offset + 3] = this.f381w;
    }
}
