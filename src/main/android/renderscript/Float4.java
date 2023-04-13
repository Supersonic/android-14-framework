package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Float4 {

    /* renamed from: w */
    public float f372w;

    /* renamed from: x */
    public float f373x;

    /* renamed from: y */
    public float f374y;

    /* renamed from: z */
    public float f375z;

    public Float4() {
    }

    public Float4(Float4 data) {
        this.f373x = data.f373x;
        this.f374y = data.f374y;
        this.f375z = data.f375z;
        this.f372w = data.f372w;
    }

    public Float4(float x, float y, float z, float w) {
        this.f373x = x;
        this.f374y = y;
        this.f375z = z;
        this.f372w = w;
    }

    public static Float4 add(Float4 a, Float4 b) {
        Float4 res = new Float4();
        res.f373x = a.f373x + b.f373x;
        res.f374y = a.f374y + b.f374y;
        res.f375z = a.f375z + b.f375z;
        res.f372w = a.f372w + b.f372w;
        return res;
    }

    public void add(Float4 value) {
        this.f373x += value.f373x;
        this.f374y += value.f374y;
        this.f375z += value.f375z;
        this.f372w += value.f372w;
    }

    public void add(float value) {
        this.f373x += value;
        this.f374y += value;
        this.f375z += value;
        this.f372w += value;
    }

    public static Float4 add(Float4 a, float b) {
        Float4 res = new Float4();
        res.f373x = a.f373x + b;
        res.f374y = a.f374y + b;
        res.f375z = a.f375z + b;
        res.f372w = a.f372w + b;
        return res;
    }

    public void sub(Float4 value) {
        this.f373x -= value.f373x;
        this.f374y -= value.f374y;
        this.f375z -= value.f375z;
        this.f372w -= value.f372w;
    }

    public void sub(float value) {
        this.f373x -= value;
        this.f374y -= value;
        this.f375z -= value;
        this.f372w -= value;
    }

    public static Float4 sub(Float4 a, float b) {
        Float4 res = new Float4();
        res.f373x = a.f373x - b;
        res.f374y = a.f374y - b;
        res.f375z = a.f375z - b;
        res.f372w = a.f372w - b;
        return res;
    }

    public static Float4 sub(Float4 a, Float4 b) {
        Float4 res = new Float4();
        res.f373x = a.f373x - b.f373x;
        res.f374y = a.f374y - b.f374y;
        res.f375z = a.f375z - b.f375z;
        res.f372w = a.f372w - b.f372w;
        return res;
    }

    public void mul(Float4 value) {
        this.f373x *= value.f373x;
        this.f374y *= value.f374y;
        this.f375z *= value.f375z;
        this.f372w *= value.f372w;
    }

    public void mul(float value) {
        this.f373x *= value;
        this.f374y *= value;
        this.f375z *= value;
        this.f372w *= value;
    }

    public static Float4 mul(Float4 a, Float4 b) {
        Float4 res = new Float4();
        res.f373x = a.f373x * b.f373x;
        res.f374y = a.f374y * b.f374y;
        res.f375z = a.f375z * b.f375z;
        res.f372w = a.f372w * b.f372w;
        return res;
    }

    public static Float4 mul(Float4 a, float b) {
        Float4 res = new Float4();
        res.f373x = a.f373x * b;
        res.f374y = a.f374y * b;
        res.f375z = a.f375z * b;
        res.f372w = a.f372w * b;
        return res;
    }

    public void div(Float4 value) {
        this.f373x /= value.f373x;
        this.f374y /= value.f374y;
        this.f375z /= value.f375z;
        this.f372w /= value.f372w;
    }

    public void div(float value) {
        this.f373x /= value;
        this.f374y /= value;
        this.f375z /= value;
        this.f372w /= value;
    }

    public static Float4 div(Float4 a, float b) {
        Float4 res = new Float4();
        res.f373x = a.f373x / b;
        res.f374y = a.f374y / b;
        res.f375z = a.f375z / b;
        res.f372w = a.f372w / b;
        return res;
    }

    public static Float4 div(Float4 a, Float4 b) {
        Float4 res = new Float4();
        res.f373x = a.f373x / b.f373x;
        res.f374y = a.f374y / b.f374y;
        res.f375z = a.f375z / b.f375z;
        res.f372w = a.f372w / b.f372w;
        return res;
    }

    public float dotProduct(Float4 a) {
        return (this.f373x * a.f373x) + (this.f374y * a.f374y) + (this.f375z * a.f375z) + (this.f372w * a.f372w);
    }

    public static float dotProduct(Float4 a, Float4 b) {
        return (b.f373x * a.f373x) + (b.f374y * a.f374y) + (b.f375z * a.f375z) + (b.f372w * a.f372w);
    }

    public void addMultiple(Float4 a, float factor) {
        this.f373x += a.f373x * factor;
        this.f374y += a.f374y * factor;
        this.f375z += a.f375z * factor;
        this.f372w += a.f372w * factor;
    }

    public void set(Float4 a) {
        this.f373x = a.f373x;
        this.f374y = a.f374y;
        this.f375z = a.f375z;
        this.f372w = a.f372w;
    }

    public void negate() {
        this.f373x = -this.f373x;
        this.f374y = -this.f374y;
        this.f375z = -this.f375z;
        this.f372w = -this.f372w;
    }

    public int length() {
        return 4;
    }

    public float elementSum() {
        return this.f373x + this.f374y + this.f375z + this.f372w;
    }

    public float get(int i) {
        switch (i) {
            case 0:
                return this.f373x;
            case 1:
                return this.f374y;
            case 2:
                return this.f375z;
            case 3:
                return this.f372w;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, float value) {
        switch (i) {
            case 0:
                this.f373x = value;
                return;
            case 1:
                this.f374y = value;
                return;
            case 2:
                this.f375z = value;
                return;
            case 3:
                this.f372w = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, float value) {
        switch (i) {
            case 0:
                this.f373x += value;
                return;
            case 1:
                this.f374y += value;
                return;
            case 2:
                this.f375z += value;
                return;
            case 3:
                this.f372w += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(float x, float y, float z, float w) {
        this.f373x = x;
        this.f374y = y;
        this.f375z = z;
        this.f372w = w;
    }

    public void copyTo(float[] data, int offset) {
        data[offset] = this.f373x;
        data[offset + 1] = this.f374y;
        data[offset + 2] = this.f375z;
        data[offset + 3] = this.f372w;
    }
}
