package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Long2 {

    /* renamed from: x */
    public long f385x;

    /* renamed from: y */
    public long f386y;

    public Long2() {
    }

    public Long2(long i) {
        this.f386y = i;
        this.f385x = i;
    }

    public Long2(long x, long y) {
        this.f385x = x;
        this.f386y = y;
    }

    public Long2(Long2 source) {
        this.f385x = source.f385x;
        this.f386y = source.f386y;
    }

    public void add(Long2 a) {
        this.f385x += a.f385x;
        this.f386y += a.f386y;
    }

    public static Long2 add(Long2 a, Long2 b) {
        Long2 result = new Long2();
        result.f385x = a.f385x + b.f385x;
        result.f386y = a.f386y + b.f386y;
        return result;
    }

    public void add(long value) {
        this.f385x += value;
        this.f386y += value;
    }

    public static Long2 add(Long2 a, long b) {
        Long2 result = new Long2();
        result.f385x = a.f385x + b;
        result.f386y = a.f386y + b;
        return result;
    }

    public void sub(Long2 a) {
        this.f385x -= a.f385x;
        this.f386y -= a.f386y;
    }

    public static Long2 sub(Long2 a, Long2 b) {
        Long2 result = new Long2();
        result.f385x = a.f385x - b.f385x;
        result.f386y = a.f386y - b.f386y;
        return result;
    }

    public void sub(long value) {
        this.f385x -= value;
        this.f386y -= value;
    }

    public static Long2 sub(Long2 a, long b) {
        Long2 result = new Long2();
        result.f385x = a.f385x - b;
        result.f386y = a.f386y - b;
        return result;
    }

    public void mul(Long2 a) {
        this.f385x *= a.f385x;
        this.f386y *= a.f386y;
    }

    public static Long2 mul(Long2 a, Long2 b) {
        Long2 result = new Long2();
        result.f385x = a.f385x * b.f385x;
        result.f386y = a.f386y * b.f386y;
        return result;
    }

    public void mul(long value) {
        this.f385x *= value;
        this.f386y *= value;
    }

    public static Long2 mul(Long2 a, long b) {
        Long2 result = new Long2();
        result.f385x = a.f385x * b;
        result.f386y = a.f386y * b;
        return result;
    }

    public void div(Long2 a) {
        this.f385x /= a.f385x;
        this.f386y /= a.f386y;
    }

    public static Long2 div(Long2 a, Long2 b) {
        Long2 result = new Long2();
        result.f385x = a.f385x / b.f385x;
        result.f386y = a.f386y / b.f386y;
        return result;
    }

    public void div(long value) {
        this.f385x /= value;
        this.f386y /= value;
    }

    public static Long2 div(Long2 a, long b) {
        Long2 result = new Long2();
        result.f385x = a.f385x / b;
        result.f386y = a.f386y / b;
        return result;
    }

    public void mod(Long2 a) {
        this.f385x %= a.f385x;
        this.f386y %= a.f386y;
    }

    public static Long2 mod(Long2 a, Long2 b) {
        Long2 result = new Long2();
        result.f385x = a.f385x % b.f385x;
        result.f386y = a.f386y % b.f386y;
        return result;
    }

    public void mod(long value) {
        this.f385x %= value;
        this.f386y %= value;
    }

    public static Long2 mod(Long2 a, long b) {
        Long2 result = new Long2();
        result.f385x = a.f385x % b;
        result.f386y = a.f386y % b;
        return result;
    }

    public long length() {
        return 2L;
    }

    public void negate() {
        this.f385x = -this.f385x;
        this.f386y = -this.f386y;
    }

    public long dotProduct(Long2 a) {
        return (this.f385x * a.f385x) + (this.f386y * a.f386y);
    }

    public static long dotProduct(Long2 a, Long2 b) {
        return (b.f385x * a.f385x) + (b.f386y * a.f386y);
    }

    public void addMultiple(Long2 a, long factor) {
        this.f385x += a.f385x * factor;
        this.f386y += a.f386y * factor;
    }

    public void set(Long2 a) {
        this.f385x = a.f385x;
        this.f386y = a.f386y;
    }

    public void setValues(long a, long b) {
        this.f385x = a;
        this.f386y = b;
    }

    public long elementSum() {
        return this.f385x + this.f386y;
    }

    public long get(int i) {
        switch (i) {
            case 0:
                return this.f385x;
            case 1:
                return this.f386y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, long value) {
        switch (i) {
            case 0:
                this.f385x = value;
                return;
            case 1:
                this.f386y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, long value) {
        switch (i) {
            case 0:
                this.f385x += value;
                return;
            case 1:
                this.f386y += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(long[] data, int offset) {
        data[offset] = this.f385x;
        data[offset + 1] = this.f386y;
    }
}
