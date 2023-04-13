package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Long4 {

    /* renamed from: w */
    public long f390w;

    /* renamed from: x */
    public long f391x;

    /* renamed from: y */
    public long f392y;

    /* renamed from: z */
    public long f393z;

    public Long4() {
    }

    public Long4(long i) {
        this.f390w = i;
        this.f393z = i;
        this.f392y = i;
        this.f391x = i;
    }

    public Long4(long x, long y, long z, long w) {
        this.f391x = x;
        this.f392y = y;
        this.f393z = z;
        this.f390w = w;
    }

    public Long4(Long4 source) {
        this.f391x = source.f391x;
        this.f392y = source.f392y;
        this.f393z = source.f393z;
        this.f390w = source.f390w;
    }

    public void add(Long4 a) {
        this.f391x += a.f391x;
        this.f392y += a.f392y;
        this.f393z += a.f393z;
        this.f390w += a.f390w;
    }

    public static Long4 add(Long4 a, Long4 b) {
        Long4 result = new Long4();
        result.f391x = a.f391x + b.f391x;
        result.f392y = a.f392y + b.f392y;
        result.f393z = a.f393z + b.f393z;
        result.f390w = a.f390w + b.f390w;
        return result;
    }

    public void add(long value) {
        this.f391x += value;
        this.f392y += value;
        this.f393z += value;
        this.f390w += value;
    }

    public static Long4 add(Long4 a, long b) {
        Long4 result = new Long4();
        result.f391x = a.f391x + b;
        result.f392y = a.f392y + b;
        result.f393z = a.f393z + b;
        result.f390w = a.f390w + b;
        return result;
    }

    public void sub(Long4 a) {
        this.f391x -= a.f391x;
        this.f392y -= a.f392y;
        this.f393z -= a.f393z;
        this.f390w -= a.f390w;
    }

    public static Long4 sub(Long4 a, Long4 b) {
        Long4 result = new Long4();
        result.f391x = a.f391x - b.f391x;
        result.f392y = a.f392y - b.f392y;
        result.f393z = a.f393z - b.f393z;
        result.f390w = a.f390w - b.f390w;
        return result;
    }

    public void sub(long value) {
        this.f391x -= value;
        this.f392y -= value;
        this.f393z -= value;
        this.f390w -= value;
    }

    public static Long4 sub(Long4 a, long b) {
        Long4 result = new Long4();
        result.f391x = a.f391x - b;
        result.f392y = a.f392y - b;
        result.f393z = a.f393z - b;
        result.f390w = a.f390w - b;
        return result;
    }

    public void mul(Long4 a) {
        this.f391x *= a.f391x;
        this.f392y *= a.f392y;
        this.f393z *= a.f393z;
        this.f390w *= a.f390w;
    }

    public static Long4 mul(Long4 a, Long4 b) {
        Long4 result = new Long4();
        result.f391x = a.f391x * b.f391x;
        result.f392y = a.f392y * b.f392y;
        result.f393z = a.f393z * b.f393z;
        result.f390w = a.f390w * b.f390w;
        return result;
    }

    public void mul(long value) {
        this.f391x *= value;
        this.f392y *= value;
        this.f393z *= value;
        this.f390w *= value;
    }

    public static Long4 mul(Long4 a, long b) {
        Long4 result = new Long4();
        result.f391x = a.f391x * b;
        result.f392y = a.f392y * b;
        result.f393z = a.f393z * b;
        result.f390w = a.f390w * b;
        return result;
    }

    public void div(Long4 a) {
        this.f391x /= a.f391x;
        this.f392y /= a.f392y;
        this.f393z /= a.f393z;
        this.f390w /= a.f390w;
    }

    public static Long4 div(Long4 a, Long4 b) {
        Long4 result = new Long4();
        result.f391x = a.f391x / b.f391x;
        result.f392y = a.f392y / b.f392y;
        result.f393z = a.f393z / b.f393z;
        result.f390w = a.f390w / b.f390w;
        return result;
    }

    public void div(long value) {
        this.f391x /= value;
        this.f392y /= value;
        this.f393z /= value;
        this.f390w /= value;
    }

    public static Long4 div(Long4 a, long b) {
        Long4 result = new Long4();
        result.f391x = a.f391x / b;
        result.f392y = a.f392y / b;
        result.f393z = a.f393z / b;
        result.f390w = a.f390w / b;
        return result;
    }

    public void mod(Long4 a) {
        this.f391x %= a.f391x;
        this.f392y %= a.f392y;
        this.f393z %= a.f393z;
        this.f390w %= a.f390w;
    }

    public static Long4 mod(Long4 a, Long4 b) {
        Long4 result = new Long4();
        result.f391x = a.f391x % b.f391x;
        result.f392y = a.f392y % b.f392y;
        result.f393z = a.f393z % b.f393z;
        result.f390w = a.f390w % b.f390w;
        return result;
    }

    public void mod(long value) {
        this.f391x %= value;
        this.f392y %= value;
        this.f393z %= value;
        this.f390w %= value;
    }

    public static Long4 mod(Long4 a, long b) {
        Long4 result = new Long4();
        result.f391x = a.f391x % b;
        result.f392y = a.f392y % b;
        result.f393z = a.f393z % b;
        result.f390w = a.f390w % b;
        return result;
    }

    public long length() {
        return 4L;
    }

    public void negate() {
        this.f391x = -this.f391x;
        this.f392y = -this.f392y;
        this.f393z = -this.f393z;
        this.f390w = -this.f390w;
    }

    public long dotProduct(Long4 a) {
        return (this.f391x * a.f391x) + (this.f392y * a.f392y) + (this.f393z * a.f393z) + (this.f390w * a.f390w);
    }

    public static long dotProduct(Long4 a, Long4 b) {
        return (b.f391x * a.f391x) + (b.f392y * a.f392y) + (b.f393z * a.f393z) + (b.f390w * a.f390w);
    }

    public void addMultiple(Long4 a, long factor) {
        this.f391x += a.f391x * factor;
        this.f392y += a.f392y * factor;
        this.f393z += a.f393z * factor;
        this.f390w += a.f390w * factor;
    }

    public void set(Long4 a) {
        this.f391x = a.f391x;
        this.f392y = a.f392y;
        this.f393z = a.f393z;
        this.f390w = a.f390w;
    }

    public void setValues(long a, long b, long c, long d) {
        this.f391x = a;
        this.f392y = b;
        this.f393z = c;
        this.f390w = d;
    }

    public long elementSum() {
        return this.f391x + this.f392y + this.f393z + this.f390w;
    }

    public long get(int i) {
        switch (i) {
            case 0:
                return this.f391x;
            case 1:
                return this.f392y;
            case 2:
                return this.f393z;
            case 3:
                return this.f390w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, long value) {
        switch (i) {
            case 0:
                this.f391x = value;
                return;
            case 1:
                this.f392y = value;
                return;
            case 2:
                this.f393z = value;
                return;
            case 3:
                this.f390w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, long value) {
        switch (i) {
            case 0:
                this.f391x += value;
                return;
            case 1:
                this.f392y += value;
                return;
            case 2:
                this.f393z += value;
                return;
            case 3:
                this.f390w += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(long[] data, int offset) {
        data[offset] = this.f391x;
        data[offset + 1] = this.f392y;
        data[offset + 2] = this.f393z;
        data[offset + 3] = this.f390w;
    }
}
