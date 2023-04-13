package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Int2 {

    /* renamed from: x */
    public int f376x;

    /* renamed from: y */
    public int f377y;

    public Int2() {
    }

    public Int2(int i) {
        this.f377y = i;
        this.f376x = i;
    }

    public Int2(int x, int y) {
        this.f376x = x;
        this.f377y = y;
    }

    public Int2(Int2 source) {
        this.f376x = source.f376x;
        this.f377y = source.f377y;
    }

    public void add(Int2 a) {
        this.f376x += a.f376x;
        this.f377y += a.f377y;
    }

    public static Int2 add(Int2 a, Int2 b) {
        Int2 result = new Int2();
        result.f376x = a.f376x + b.f376x;
        result.f377y = a.f377y + b.f377y;
        return result;
    }

    public void add(int value) {
        this.f376x += value;
        this.f377y += value;
    }

    public static Int2 add(Int2 a, int b) {
        Int2 result = new Int2();
        result.f376x = a.f376x + b;
        result.f377y = a.f377y + b;
        return result;
    }

    public void sub(Int2 a) {
        this.f376x -= a.f376x;
        this.f377y -= a.f377y;
    }

    public static Int2 sub(Int2 a, Int2 b) {
        Int2 result = new Int2();
        result.f376x = a.f376x - b.f376x;
        result.f377y = a.f377y - b.f377y;
        return result;
    }

    public void sub(int value) {
        this.f376x -= value;
        this.f377y -= value;
    }

    public static Int2 sub(Int2 a, int b) {
        Int2 result = new Int2();
        result.f376x = a.f376x - b;
        result.f377y = a.f377y - b;
        return result;
    }

    public void mul(Int2 a) {
        this.f376x *= a.f376x;
        this.f377y *= a.f377y;
    }

    public static Int2 mul(Int2 a, Int2 b) {
        Int2 result = new Int2();
        result.f376x = a.f376x * b.f376x;
        result.f377y = a.f377y * b.f377y;
        return result;
    }

    public void mul(int value) {
        this.f376x *= value;
        this.f377y *= value;
    }

    public static Int2 mul(Int2 a, int b) {
        Int2 result = new Int2();
        result.f376x = a.f376x * b;
        result.f377y = a.f377y * b;
        return result;
    }

    public void div(Int2 a) {
        this.f376x /= a.f376x;
        this.f377y /= a.f377y;
    }

    public static Int2 div(Int2 a, Int2 b) {
        Int2 result = new Int2();
        result.f376x = a.f376x / b.f376x;
        result.f377y = a.f377y / b.f377y;
        return result;
    }

    public void div(int value) {
        this.f376x /= value;
        this.f377y /= value;
    }

    public static Int2 div(Int2 a, int b) {
        Int2 result = new Int2();
        result.f376x = a.f376x / b;
        result.f377y = a.f377y / b;
        return result;
    }

    public void mod(Int2 a) {
        this.f376x %= a.f376x;
        this.f377y %= a.f377y;
    }

    public static Int2 mod(Int2 a, Int2 b) {
        Int2 result = new Int2();
        result.f376x = a.f376x % b.f376x;
        result.f377y = a.f377y % b.f377y;
        return result;
    }

    public void mod(int value) {
        this.f376x %= value;
        this.f377y %= value;
    }

    public static Int2 mod(Int2 a, int b) {
        Int2 result = new Int2();
        result.f376x = a.f376x % b;
        result.f377y = a.f377y % b;
        return result;
    }

    public int length() {
        return 2;
    }

    public void negate() {
        this.f376x = -this.f376x;
        this.f377y = -this.f377y;
    }

    public int dotProduct(Int2 a) {
        return (this.f376x * a.f376x) + (this.f377y * a.f377y);
    }

    public static int dotProduct(Int2 a, Int2 b) {
        return (b.f376x * a.f376x) + (b.f377y * a.f377y);
    }

    public void addMultiple(Int2 a, int factor) {
        this.f376x += a.f376x * factor;
        this.f377y += a.f377y * factor;
    }

    public void set(Int2 a) {
        this.f376x = a.f376x;
        this.f377y = a.f377y;
    }

    public void setValues(int a, int b) {
        this.f376x = a;
        this.f377y = b;
    }

    public int elementSum() {
        return this.f376x + this.f377y;
    }

    public int get(int i) {
        switch (i) {
            case 0:
                return this.f376x;
            case 1:
                return this.f377y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, int value) {
        switch (i) {
            case 0:
                this.f376x = value;
                return;
            case 1:
                this.f377y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, int value) {
        switch (i) {
            case 0:
                this.f376x += value;
                return;
            case 1:
                this.f377y += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(int[] data, int offset) {
        data[offset] = this.f376x;
        data[offset + 1] = this.f377y;
    }
}
