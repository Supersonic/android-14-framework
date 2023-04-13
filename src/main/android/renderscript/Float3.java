package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Float3 {

    /* renamed from: x */
    public float f369x;

    /* renamed from: y */
    public float f370y;

    /* renamed from: z */
    public float f371z;

    public Float3() {
    }

    public Float3(Float3 data) {
        this.f369x = data.f369x;
        this.f370y = data.f370y;
        this.f371z = data.f371z;
    }

    public Float3(float x, float y, float z) {
        this.f369x = x;
        this.f370y = y;
        this.f371z = z;
    }

    public static Float3 add(Float3 a, Float3 b) {
        Float3 res = new Float3();
        res.f369x = a.f369x + b.f369x;
        res.f370y = a.f370y + b.f370y;
        res.f371z = a.f371z + b.f371z;
        return res;
    }

    public void add(Float3 value) {
        this.f369x += value.f369x;
        this.f370y += value.f370y;
        this.f371z += value.f371z;
    }

    public void add(float value) {
        this.f369x += value;
        this.f370y += value;
        this.f371z += value;
    }

    public static Float3 add(Float3 a, float b) {
        Float3 res = new Float3();
        res.f369x = a.f369x + b;
        res.f370y = a.f370y + b;
        res.f371z = a.f371z + b;
        return res;
    }

    public void sub(Float3 value) {
        this.f369x -= value.f369x;
        this.f370y -= value.f370y;
        this.f371z -= value.f371z;
    }

    public static Float3 sub(Float3 a, Float3 b) {
        Float3 res = new Float3();
        res.f369x = a.f369x - b.f369x;
        res.f370y = a.f370y - b.f370y;
        res.f371z = a.f371z - b.f371z;
        return res;
    }

    public void sub(float value) {
        this.f369x -= value;
        this.f370y -= value;
        this.f371z -= value;
    }

    public static Float3 sub(Float3 a, float b) {
        Float3 res = new Float3();
        res.f369x = a.f369x - b;
        res.f370y = a.f370y - b;
        res.f371z = a.f371z - b;
        return res;
    }

    public void mul(Float3 value) {
        this.f369x *= value.f369x;
        this.f370y *= value.f370y;
        this.f371z *= value.f371z;
    }

    public static Float3 mul(Float3 a, Float3 b) {
        Float3 res = new Float3();
        res.f369x = a.f369x * b.f369x;
        res.f370y = a.f370y * b.f370y;
        res.f371z = a.f371z * b.f371z;
        return res;
    }

    public void mul(float value) {
        this.f369x *= value;
        this.f370y *= value;
        this.f371z *= value;
    }

    public static Float3 mul(Float3 a, float b) {
        Float3 res = new Float3();
        res.f369x = a.f369x * b;
        res.f370y = a.f370y * b;
        res.f371z = a.f371z * b;
        return res;
    }

    public void div(Float3 value) {
        this.f369x /= value.f369x;
        this.f370y /= value.f370y;
        this.f371z /= value.f371z;
    }

    public static Float3 div(Float3 a, Float3 b) {
        Float3 res = new Float3();
        res.f369x = a.f369x / b.f369x;
        res.f370y = a.f370y / b.f370y;
        res.f371z = a.f371z / b.f371z;
        return res;
    }

    public void div(float value) {
        this.f369x /= value;
        this.f370y /= value;
        this.f371z /= value;
    }

    public static Float3 div(Float3 a, float b) {
        Float3 res = new Float3();
        res.f369x = a.f369x / b;
        res.f370y = a.f370y / b;
        res.f371z = a.f371z / b;
        return res;
    }

    public Float dotProduct(Float3 a) {
        return new Float((this.f369x * a.f369x) + (this.f370y * a.f370y) + (this.f371z * a.f371z));
    }

    public static Float dotProduct(Float3 a, Float3 b) {
        return new Float((b.f369x * a.f369x) + (b.f370y * a.f370y) + (b.f371z * a.f371z));
    }

    public void addMultiple(Float3 a, float factor) {
        this.f369x += a.f369x * factor;
        this.f370y += a.f370y * factor;
        this.f371z += a.f371z * factor;
    }

    public void set(Float3 a) {
        this.f369x = a.f369x;
        this.f370y = a.f370y;
        this.f371z = a.f371z;
    }

    public void negate() {
        this.f369x = -this.f369x;
        this.f370y = -this.f370y;
        this.f371z = -this.f371z;
    }

    public int length() {
        return 3;
    }

    public Float elementSum() {
        return new Float(this.f369x + this.f370y + this.f371z);
    }

    public float get(int i) {
        switch (i) {
            case 0:
                return this.f369x;
            case 1:
                return this.f370y;
            case 2:
                return this.f371z;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setAt(int i, float value) {
        switch (i) {
            case 0:
                this.f369x = value;
                return;
            case 1:
                this.f370y = value;
                return;
            case 2:
                this.f371z = value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, float value) {
        switch (i) {
            case 0:
                this.f369x += value;
                return;
            case 1:
                this.f370y += value;
                return;
            case 2:
                this.f371z += value;
                return;
            default:
                throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(float x, float y, float z) {
        this.f369x = x;
        this.f370y = y;
        this.f371z = z;
    }

    public void copyTo(float[] data, int offset) {
        data[offset] = this.f369x;
        data[offset + 1] = this.f370y;
        data[offset + 2] = this.f371z;
    }
}
