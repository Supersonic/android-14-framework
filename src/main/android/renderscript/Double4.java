package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Double4 {

    /* renamed from: w */
    public double f363w;

    /* renamed from: x */
    public double f364x;

    /* renamed from: y */
    public double f365y;

    /* renamed from: z */
    public double f366z;

    public Double4() {
    }

    public Double4(Double4 data) {
        this.f364x = data.f364x;
        this.f365y = data.f365y;
        this.f366z = data.f366z;
        this.f363w = data.f363w;
    }

    public Double4(double x, double y, double z, double w) {
        this.f364x = x;
        this.f365y = y;
        this.f366z = z;
        this.f363w = w;
    }

    public static Double4 add(Double4 a, Double4 b) {
        Double4 res = new Double4();
        res.f364x = a.f364x + b.f364x;
        res.f365y = a.f365y + b.f365y;
        res.f366z = a.f366z + b.f366z;
        res.f363w = a.f363w + b.f363w;
        return res;
    }

    public void add(Double4 value) {
        this.f364x += value.f364x;
        this.f365y += value.f365y;
        this.f366z += value.f366z;
        this.f363w += value.f363w;
    }

    public void add(double value) {
        this.f364x += value;
        this.f365y += value;
        this.f366z += value;
        this.f363w += value;
    }

    public static Double4 add(Double4 a, double b) {
        Double4 res = new Double4();
        res.f364x = a.f364x + b;
        res.f365y = a.f365y + b;
        res.f366z = a.f366z + b;
        res.f363w = a.f363w + b;
        return res;
    }

    public void sub(Double4 value) {
        this.f364x -= value.f364x;
        this.f365y -= value.f365y;
        this.f366z -= value.f366z;
        this.f363w -= value.f363w;
    }

    public void sub(double value) {
        this.f364x -= value;
        this.f365y -= value;
        this.f366z -= value;
        this.f363w -= value;
    }

    public static Double4 sub(Double4 a, double b) {
        Double4 res = new Double4();
        res.f364x = a.f364x - b;
        res.f365y = a.f365y - b;
        res.f366z = a.f366z - b;
        res.f363w = a.f363w - b;
        return res;
    }

    public static Double4 sub(Double4 a, Double4 b) {
        Double4 res = new Double4();
        res.f364x = a.f364x - b.f364x;
        res.f365y = a.f365y - b.f365y;
        res.f366z = a.f366z - b.f366z;
        res.f363w = a.f363w - b.f363w;
        return res;
    }

    public void mul(Double4 value) {
        this.f364x *= value.f364x;
        this.f365y *= value.f365y;
        this.f366z *= value.f366z;
        this.f363w *= value.f363w;
    }

    public void mul(double value) {
        this.f364x *= value;
        this.f365y *= value;
        this.f366z *= value;
        this.f363w *= value;
    }

    public static Double4 mul(Double4 a, Double4 b) {
        Double4 res = new Double4();
        res.f364x = a.f364x * b.f364x;
        res.f365y = a.f365y * b.f365y;
        res.f366z = a.f366z * b.f366z;
        res.f363w = a.f363w * b.f363w;
        return res;
    }

    public static Double4 mul(Double4 a, double b) {
        Double4 res = new Double4();
        res.f364x = a.f364x * b;
        res.f365y = a.f365y * b;
        res.f366z = a.f366z * b;
        res.f363w = a.f363w * b;
        return res;
    }

    public void div(Double4 value) {
        this.f364x /= value.f364x;
        this.f365y /= value.f365y;
        this.f366z /= value.f366z;
        this.f363w /= value.f363w;
    }

    public void div(double value) {
        this.f364x /= value;
        this.f365y /= value;
        this.f366z /= value;
        this.f363w /= value;
    }

    public static Double4 div(Double4 a, double b) {
        Double4 res = new Double4();
        res.f364x = a.f364x / b;
        res.f365y = a.f365y / b;
        res.f366z = a.f366z / b;
        res.f363w = a.f363w / b;
        return res;
    }

    public static Double4 div(Double4 a, Double4 b) {
        Double4 res = new Double4();
        res.f364x = a.f364x / b.f364x;
        res.f365y = a.f365y / b.f365y;
        res.f366z = a.f366z / b.f366z;
        res.f363w = a.f363w / b.f363w;
        return res;
    }

    public double dotProduct(Double4 a) {
        return (this.f364x * a.f364x) + (this.f365y * a.f365y) + (this.f366z * a.f366z) + (this.f363w * a.f363w);
    }

    public static double dotProduct(Double4 a, Double4 b) {
        return (b.f364x * a.f364x) + (b.f365y * a.f365y) + (b.f366z * a.f366z) + (b.f363w * a.f363w);
    }

    public void addMultiple(Double4 a, double factor) {
        this.f364x += a.f364x * factor;
        this.f365y += a.f365y * factor;
        this.f366z += a.f366z * factor;
        this.f363w += a.f363w * factor;
    }

    public void set(Double4 a) {
        this.f364x = a.f364x;
        this.f365y = a.f365y;
        this.f366z = a.f366z;
        this.f363w = a.f363w;
    }

    public void negate() {
        this.f364x = -this.f364x;
        this.f365y = -this.f365y;
        this.f366z = -this.f366z;
        this.f363w = -this.f363w;
    }

    public int length() {
        return 4;
    }

    public double elementSum() {
        return this.f364x + this.f365y + this.f366z + this.f363w;
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return this.f364x;
            case 1:
                return this.f365y;
            case 2:
                return this.f366z;
            case 3:
                return this.f363w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, double value) {
        switch (i) {
            case 0:
                this.f364x = value;
                return;
            case 1:
                this.f365y = value;
                return;
            case 2:
                this.f366z = value;
                return;
            case 3:
                this.f363w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, double value) {
        switch (i) {
            case 0:
                this.f364x += value;
                return;
            case 1:
                this.f365y += value;
                return;
            case 2:
                this.f366z += value;
                return;
            case 3:
                this.f363w += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(double x, double y, double z, double w) {
        this.f364x = x;
        this.f365y = y;
        this.f366z = z;
        this.f363w = w;
    }

    public void copyTo(double[] data, int offset) {
        data[offset] = this.f364x;
        data[offset + 1] = this.f365y;
        data[offset + 2] = this.f366z;
        data[offset + 3] = this.f363w;
    }
}
