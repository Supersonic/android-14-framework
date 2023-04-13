package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Float2 {

    /* renamed from: x */
    public float f367x;

    /* renamed from: y */
    public float f368y;

    public Float2() {
    }

    public Float2(Float2 data) {
        this.f367x = data.f367x;
        this.f368y = data.f368y;
    }

    public Float2(float x, float y) {
        this.f367x = x;
        this.f368y = y;
    }

    public static Float2 add(Float2 a, Float2 b) {
        Float2 res = new Float2();
        res.f367x = a.f367x + b.f367x;
        res.f368y = a.f368y + b.f368y;
        return res;
    }

    public void add(Float2 value) {
        this.f367x += value.f367x;
        this.f368y += value.f368y;
    }

    public void add(float value) {
        this.f367x += value;
        this.f368y += value;
    }

    public static Float2 add(Float2 a, float b) {
        Float2 res = new Float2();
        res.f367x = a.f367x + b;
        res.f368y = a.f368y + b;
        return res;
    }

    public void sub(Float2 value) {
        this.f367x -= value.f367x;
        this.f368y -= value.f368y;
    }

    public static Float2 sub(Float2 a, Float2 b) {
        Float2 res = new Float2();
        res.f367x = a.f367x - b.f367x;
        res.f368y = a.f368y - b.f368y;
        return res;
    }

    public void sub(float value) {
        this.f367x -= value;
        this.f368y -= value;
    }

    public static Float2 sub(Float2 a, float b) {
        Float2 res = new Float2();
        res.f367x = a.f367x - b;
        res.f368y = a.f368y - b;
        return res;
    }

    public void mul(Float2 value) {
        this.f367x *= value.f367x;
        this.f368y *= value.f368y;
    }

    public static Float2 mul(Float2 a, Float2 b) {
        Float2 res = new Float2();
        res.f367x = a.f367x * b.f367x;
        res.f368y = a.f368y * b.f368y;
        return res;
    }

    public void mul(float value) {
        this.f367x *= value;
        this.f368y *= value;
    }

    public static Float2 mul(Float2 a, float b) {
        Float2 res = new Float2();
        res.f367x = a.f367x * b;
        res.f368y = a.f368y * b;
        return res;
    }

    public void div(Float2 value) {
        this.f367x /= value.f367x;
        this.f368y /= value.f368y;
    }

    public static Float2 div(Float2 a, Float2 b) {
        Float2 res = new Float2();
        res.f367x = a.f367x / b.f367x;
        res.f368y = a.f368y / b.f368y;
        return res;
    }

    public void div(float value) {
        this.f367x /= value;
        this.f368y /= value;
    }

    public static Float2 div(Float2 a, float b) {
        Float2 res = new Float2();
        res.f367x = a.f367x / b;
        res.f368y = a.f368y / b;
        return res;
    }

    public float dotProduct(Float2 a) {
        return (this.f367x * a.f367x) + (this.f368y * a.f368y);
    }

    public static float dotProduct(Float2 a, Float2 b) {
        return (b.f367x * a.f367x) + (b.f368y * a.f368y);
    }

    public void addMultiple(Float2 a, float factor) {
        this.f367x += a.f367x * factor;
        this.f368y += a.f368y * factor;
    }

    public void set(Float2 a) {
        this.f367x = a.f367x;
        this.f368y = a.f368y;
    }

    public void negate() {
        this.f367x = -this.f367x;
        this.f368y = -this.f368y;
    }

    public int length() {
        return 2;
    }

    public float elementSum() {
        return this.f367x + this.f368y;
    }

    public float get(int i) {
        switch (i) {
            case 0:
                return this.f367x;
            case 1:
                return this.f368y;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, float value) {
        switch (i) {
            case 0:
                this.f367x = value;
                return;
            case 1:
                this.f368y = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, float value) {
        switch (i) {
            case 0:
                this.f367x += value;
                return;
            case 1:
                this.f368y += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(float x, float y) {
        this.f367x = x;
        this.f368y = y;
    }

    public void copyTo(float[] data, int offset) {
        data[offset] = this.f367x;
        data[offset + 1] = this.f368y;
    }
}
