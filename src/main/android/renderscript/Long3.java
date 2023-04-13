package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Long3 {

    /* renamed from: x */
    public long f387x;

    /* renamed from: y */
    public long f388y;

    /* renamed from: z */
    public long f389z;

    public Long3() {
    }

    public Long3(long i) {
        this.f389z = i;
        this.f388y = i;
        this.f387x = i;
    }

    public Long3(long x, long y, long z) {
        this.f387x = x;
        this.f388y = y;
        this.f389z = z;
    }

    public Long3(Long3 source) {
        this.f387x = source.f387x;
        this.f388y = source.f388y;
        this.f389z = source.f389z;
    }

    public void add(Long3 a) {
        this.f387x += a.f387x;
        this.f388y += a.f388y;
        this.f389z += a.f389z;
    }

    public static Long3 add(Long3 a, Long3 b) {
        Long3 result = new Long3();
        result.f387x = a.f387x + b.f387x;
        result.f388y = a.f388y + b.f388y;
        result.f389z = a.f389z + b.f389z;
        return result;
    }

    public void add(long value) {
        this.f387x += value;
        this.f388y += value;
        this.f389z += value;
    }

    public static Long3 add(Long3 a, long b) {
        Long3 result = new Long3();
        result.f387x = a.f387x + b;
        result.f388y = a.f388y + b;
        result.f389z = a.f389z + b;
        return result;
    }

    public void sub(Long3 a) {
        this.f387x -= a.f387x;
        this.f388y -= a.f388y;
        this.f389z -= a.f389z;
    }

    public static Long3 sub(Long3 a, Long3 b) {
        Long3 result = new Long3();
        result.f387x = a.f387x - b.f387x;
        result.f388y = a.f388y - b.f388y;
        result.f389z = a.f389z - b.f389z;
        return result;
    }

    public void sub(long value) {
        this.f387x -= value;
        this.f388y -= value;
        this.f389z -= value;
    }

    public static Long3 sub(Long3 a, long b) {
        Long3 result = new Long3();
        result.f387x = a.f387x - b;
        result.f388y = a.f388y - b;
        result.f389z = a.f389z - b;
        return result;
    }

    public void mul(Long3 a) {
        this.f387x *= a.f387x;
        this.f388y *= a.f388y;
        this.f389z *= a.f389z;
    }

    public static Long3 mul(Long3 a, Long3 b) {
        Long3 result = new Long3();
        result.f387x = a.f387x * b.f387x;
        result.f388y = a.f388y * b.f388y;
        result.f389z = a.f389z * b.f389z;
        return result;
    }

    public void mul(long value) {
        this.f387x *= value;
        this.f388y *= value;
        this.f389z *= value;
    }

    public static Long3 mul(Long3 a, long b) {
        Long3 result = new Long3();
        result.f387x = a.f387x * b;
        result.f388y = a.f388y * b;
        result.f389z = a.f389z * b;
        return result;
    }

    public void div(Long3 a) {
        this.f387x /= a.f387x;
        this.f388y /= a.f388y;
        this.f389z /= a.f389z;
    }

    public static Long3 div(Long3 a, Long3 b) {
        Long3 result = new Long3();
        result.f387x = a.f387x / b.f387x;
        result.f388y = a.f388y / b.f388y;
        result.f389z = a.f389z / b.f389z;
        return result;
    }

    public void div(long value) {
        this.f387x /= value;
        this.f388y /= value;
        this.f389z /= value;
    }

    public static Long3 div(Long3 a, long b) {
        Long3 result = new Long3();
        result.f387x = a.f387x / b;
        result.f388y = a.f388y / b;
        result.f389z = a.f389z / b;
        return result;
    }

    public void mod(Long3 a) {
        this.f387x %= a.f387x;
        this.f388y %= a.f388y;
        this.f389z %= a.f389z;
    }

    public static Long3 mod(Long3 a, Long3 b) {
        Long3 result = new Long3();
        result.f387x = a.f387x % b.f387x;
        result.f388y = a.f388y % b.f388y;
        result.f389z = a.f389z % b.f389z;
        return result;
    }

    public void mod(long value) {
        this.f387x %= value;
        this.f388y %= value;
        this.f389z %= value;
    }

    public static Long3 mod(Long3 a, long b) {
        Long3 result = new Long3();
        result.f387x = a.f387x % b;
        result.f388y = a.f388y % b;
        result.f389z = a.f389z % b;
        return result;
    }

    public long length() {
        return 3L;
    }

    public void negate() {
        this.f387x = -this.f387x;
        this.f388y = -this.f388y;
        this.f389z = -this.f389z;
    }

    public long dotProduct(Long3 a) {
        return (this.f387x * a.f387x) + (this.f388y * a.f388y) + (this.f389z * a.f389z);
    }

    public static long dotProduct(Long3 a, Long3 b) {
        return (b.f387x * a.f387x) + (b.f388y * a.f388y) + (b.f389z * a.f389z);
    }

    public void addMultiple(Long3 a, long factor) {
        this.f387x += a.f387x * factor;
        this.f388y += a.f388y * factor;
        this.f389z += a.f389z * factor;
    }

    public void set(Long3 a) {
        this.f387x = a.f387x;
        this.f388y = a.f388y;
        this.f389z = a.f389z;
    }

    public void setValues(long a, long b, long c) {
        this.f387x = a;
        this.f388y = b;
        this.f389z = c;
    }

    public long elementSum() {
        return this.f387x + this.f388y + this.f389z;
    }

    public long get(int i) {
        switch (i) {
            case 0:
                return this.f387x;
            case 1:
                return this.f388y;
            case 2:
                return this.f389z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, long value) {
        switch (i) {
            case 0:
                this.f387x = value;
                return;
            case 1:
                this.f388y = value;
                return;
            case 2:
                this.f389z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, long value) {
        switch (i) {
            case 0:
                this.f387x += value;
                return;
            case 1:
                this.f388y += value;
                return;
            case 2:
                this.f389z += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(long[] data, int offset) {
        data[offset] = this.f387x;
        data[offset + 1] = this.f388y;
        data[offset + 2] = this.f389z;
    }
}
