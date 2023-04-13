package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Double2 {

    /* renamed from: x */
    public double f358x;

    /* renamed from: y */
    public double f359y;

    public Double2() {
    }

    public Double2(Double2 data) {
        this.f358x = data.f358x;
        this.f359y = data.f359y;
    }

    public Double2(double x, double y) {
        this.f358x = x;
        this.f359y = y;
    }

    public static Double2 add(Double2 a, Double2 b) {
        Double2 res = new Double2();
        res.f358x = a.f358x + b.f358x;
        res.f359y = a.f359y + b.f359y;
        return res;
    }

    public void add(Double2 value) {
        this.f358x += value.f358x;
        this.f359y += value.f359y;
    }

    public void add(double value) {
        this.f358x += value;
        this.f359y += value;
    }

    public static Double2 add(Double2 a, double b) {
        Double2 res = new Double2();
        res.f358x = a.f358x + b;
        res.f359y = a.f359y + b;
        return res;
    }

    public void sub(Double2 value) {
        this.f358x -= value.f358x;
        this.f359y -= value.f359y;
    }

    public static Double2 sub(Double2 a, Double2 b) {
        Double2 res = new Double2();
        res.f358x = a.f358x - b.f358x;
        res.f359y = a.f359y - b.f359y;
        return res;
    }

    public void sub(double value) {
        this.f358x -= value;
        this.f359y -= value;
    }

    public static Double2 sub(Double2 a, double b) {
        Double2 res = new Double2();
        res.f358x = a.f358x - b;
        res.f359y = a.f359y - b;
        return res;
    }

    public void mul(Double2 value) {
        this.f358x *= value.f358x;
        this.f359y *= value.f359y;
    }

    public static Double2 mul(Double2 a, Double2 b) {
        Double2 res = new Double2();
        res.f358x = a.f358x * b.f358x;
        res.f359y = a.f359y * b.f359y;
        return res;
    }

    public void mul(double value) {
        this.f358x *= value;
        this.f359y *= value;
    }

    public static Double2 mul(Double2 a, double b) {
        Double2 res = new Double2();
        res.f358x = a.f358x * b;
        res.f359y = a.f359y * b;
        return res;
    }

    public void div(Double2 value) {
        this.f358x /= value.f358x;
        this.f359y /= value.f359y;
    }

    public static Double2 div(Double2 a, Double2 b) {
        Double2 res = new Double2();
        res.f358x = a.f358x / b.f358x;
        res.f359y = a.f359y / b.f359y;
        return res;
    }

    public void div(double value) {
        this.f358x /= value;
        this.f359y /= value;
    }

    public static Double2 div(Double2 a, double b) {
        Double2 res = new Double2();
        res.f358x = a.f358x / b;
        res.f359y = a.f359y / b;
        return res;
    }

    public double dotProduct(Double2 a) {
        return (this.f358x * a.f358x) + (this.f359y * a.f359y);
    }

    public static Double dotProduct(Double2 a, Double2 b) {
        return Double.valueOf((b.f358x * a.f358x) + (b.f359y * a.f359y));
    }

    public void addMultiple(Double2 a, double factor) {
        this.f358x += a.f358x * factor;
        this.f359y += a.f359y * factor;
    }

    public void set(Double2 a) {
        this.f358x = a.f358x;
        this.f359y = a.f359y;
    }

    public void negate() {
        this.f358x = -this.f358x;
        this.f359y = -this.f359y;
    }

    public int length() {
        return 2;
    }

    public double elementSum() {
        return this.f358x + this.f359y;
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return this.f358x;
            case 1:
                return this.f359y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, double value) {
        switch (i) {
            case 0:
                this.f358x = value;
                return;
            case 1:
                this.f359y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, double value) {
        switch (i) {
            case 0:
                this.f358x += value;
                return;
            case 1:
                this.f359y += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(double x, double y) {
        this.f358x = x;
        this.f359y = y;
    }

    public void copyTo(double[] data, int offset) {
        data[offset] = this.f358x;
        data[offset + 1] = this.f359y;
    }
}
