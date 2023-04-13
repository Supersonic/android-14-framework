package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Double3 {

    /* renamed from: x */
    public double f360x;

    /* renamed from: y */
    public double f361y;

    /* renamed from: z */
    public double f362z;

    public Double3() {
    }

    public Double3(Double3 data) {
        this.f360x = data.f360x;
        this.f361y = data.f361y;
        this.f362z = data.f362z;
    }

    public Double3(double x, double y, double z) {
        this.f360x = x;
        this.f361y = y;
        this.f362z = z;
    }

    public static Double3 add(Double3 a, Double3 b) {
        Double3 res = new Double3();
        res.f360x = a.f360x + b.f360x;
        res.f361y = a.f361y + b.f361y;
        res.f362z = a.f362z + b.f362z;
        return res;
    }

    public void add(Double3 value) {
        this.f360x += value.f360x;
        this.f361y += value.f361y;
        this.f362z += value.f362z;
    }

    public void add(double value) {
        this.f360x += value;
        this.f361y += value;
        this.f362z += value;
    }

    public static Double3 add(Double3 a, double b) {
        Double3 res = new Double3();
        res.f360x = a.f360x + b;
        res.f361y = a.f361y + b;
        res.f362z = a.f362z + b;
        return res;
    }

    public void sub(Double3 value) {
        this.f360x -= value.f360x;
        this.f361y -= value.f361y;
        this.f362z -= value.f362z;
    }

    public static Double3 sub(Double3 a, Double3 b) {
        Double3 res = new Double3();
        res.f360x = a.f360x - b.f360x;
        res.f361y = a.f361y - b.f361y;
        res.f362z = a.f362z - b.f362z;
        return res;
    }

    public void sub(double value) {
        this.f360x -= value;
        this.f361y -= value;
        this.f362z -= value;
    }

    public static Double3 sub(Double3 a, double b) {
        Double3 res = new Double3();
        res.f360x = a.f360x - b;
        res.f361y = a.f361y - b;
        res.f362z = a.f362z - b;
        return res;
    }

    public void mul(Double3 value) {
        this.f360x *= value.f360x;
        this.f361y *= value.f361y;
        this.f362z *= value.f362z;
    }

    public static Double3 mul(Double3 a, Double3 b) {
        Double3 res = new Double3();
        res.f360x = a.f360x * b.f360x;
        res.f361y = a.f361y * b.f361y;
        res.f362z = a.f362z * b.f362z;
        return res;
    }

    public void mul(double value) {
        this.f360x *= value;
        this.f361y *= value;
        this.f362z *= value;
    }

    public static Double3 mul(Double3 a, double b) {
        Double3 res = new Double3();
        res.f360x = a.f360x * b;
        res.f361y = a.f361y * b;
        res.f362z = a.f362z * b;
        return res;
    }

    public void div(Double3 value) {
        this.f360x /= value.f360x;
        this.f361y /= value.f361y;
        this.f362z /= value.f362z;
    }

    public static Double3 div(Double3 a, Double3 b) {
        Double3 res = new Double3();
        res.f360x = a.f360x / b.f360x;
        res.f361y = a.f361y / b.f361y;
        res.f362z = a.f362z / b.f362z;
        return res;
    }

    public void div(double value) {
        this.f360x /= value;
        this.f361y /= value;
        this.f362z /= value;
    }

    public static Double3 div(Double3 a, double b) {
        Double3 res = new Double3();
        res.f360x = a.f360x / b;
        res.f361y = a.f361y / b;
        res.f362z = a.f362z / b;
        return res;
    }

    public double dotProduct(Double3 a) {
        return (this.f360x * a.f360x) + (this.f361y * a.f361y) + (this.f362z * a.f362z);
    }

    public static double dotProduct(Double3 a, Double3 b) {
        return (b.f360x * a.f360x) + (b.f361y * a.f361y) + (b.f362z * a.f362z);
    }

    public void addMultiple(Double3 a, double factor) {
        this.f360x += a.f360x * factor;
        this.f361y += a.f361y * factor;
        this.f362z += a.f362z * factor;
    }

    public void set(Double3 a) {
        this.f360x = a.f360x;
        this.f361y = a.f361y;
        this.f362z = a.f362z;
    }

    public void negate() {
        this.f360x = -this.f360x;
        this.f361y = -this.f361y;
        this.f362z = -this.f362z;
    }

    public int length() {
        return 3;
    }

    public double elementSum() {
        return this.f360x + this.f361y + this.f362z;
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return this.f360x;
            case 1:
                return this.f361y;
            case 2:
                return this.f362z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, double value) {
        switch (i) {
            case 0:
                this.f360x = value;
                return;
            case 1:
                this.f361y = value;
                return;
            case 2:
                this.f362z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, double value) {
        switch (i) {
            case 0:
                this.f360x += value;
                return;
            case 1:
                this.f361y += value;
                return;
            case 2:
                this.f362z += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(double x, double y, double z) {
        this.f360x = x;
        this.f361y = y;
        this.f362z = z;
    }

    public void copyTo(double[] data, int offset) {
        data[offset] = this.f360x;
        data[offset + 1] = this.f361y;
        data[offset + 2] = this.f362z;
    }
}
