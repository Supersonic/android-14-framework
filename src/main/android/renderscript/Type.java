package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class Type extends BaseObj {
    static final int mMaxArrays = 4;
    int[] mArrays;
    boolean mDimFaces;
    boolean mDimMipmaps;
    int mDimX;
    int mDimY;
    int mDimYuv;
    int mDimZ;
    Element mElement;
    int mElementCount;

    /* loaded from: classes3.dex */
    public enum CubemapFace {
        POSITIVE_X(0),
        NEGATIVE_X(1),
        POSITIVE_Y(2),
        NEGATIVE_Y(3),
        POSITIVE_Z(4),
        NEGATIVE_Z(5),
        POSITVE_X(0),
        POSITVE_Y(2),
        POSITVE_Z(4);
        
        int mID;

        CubemapFace(int id) {
            this.mID = id;
        }
    }

    public Element getElement() {
        return this.mElement;
    }

    public int getX() {
        return this.mDimX;
    }

    public int getY() {
        return this.mDimY;
    }

    public int getZ() {
        return this.mDimZ;
    }

    public int getYuv() {
        return this.mDimYuv;
    }

    public boolean hasMipmaps() {
        return this.mDimMipmaps;
    }

    public boolean hasFaces() {
        return this.mDimFaces;
    }

    public int getCount() {
        return this.mElementCount;
    }

    public int getArray(int arrayNum) {
        if (arrayNum < 0 || arrayNum >= 4) {
            throw new RSIllegalArgumentException("Array dimension out of range.");
        }
        int[] iArr = this.mArrays;
        if (iArr == null || arrayNum >= iArr.length) {
            return 0;
        }
        return iArr[arrayNum];
    }

    public int getArrayCount() {
        int[] iArr = this.mArrays;
        if (iArr != null) {
            return iArr.length;
        }
        return 0;
    }

    void calcElementCount() {
        boolean hasLod = hasMipmaps();
        int x = getX();
        int y = getY();
        int z = getZ();
        int faces = 1;
        if (hasFaces()) {
            faces = 6;
        }
        if (x == 0) {
            x = 1;
        }
        if (y == 0) {
            y = 1;
        }
        if (z == 0) {
            z = 1;
        }
        int count = x * y * z * faces;
        while (hasLod && (x > 1 || y > 1 || z > 1)) {
            if (x > 1) {
                x >>= 1;
            }
            if (y > 1) {
                y >>= 1;
            }
            if (z > 1) {
                z >>= 1;
            }
            count += x * y * z * faces;
        }
        if (this.mArrays != null) {
            int ct = 0;
            while (true) {
                int[] iArr = this.mArrays;
                if (ct >= iArr.length) {
                    break;
                }
                count *= iArr[ct];
                ct++;
            }
        }
        this.mElementCount = count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Type(long id, RenderScript rs) {
        super(id, rs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.renderscript.BaseObj
    public void updateFromNative() {
        boolean z;
        long[] dataBuffer = new long[6];
        this.mRS.nTypeGetNativeData(getID(this.mRS), dataBuffer);
        boolean z2 = false;
        this.mDimX = (int) dataBuffer[0];
        this.mDimY = (int) dataBuffer[1];
        this.mDimZ = (int) dataBuffer[2];
        if (dataBuffer[3] != 1) {
            z = false;
        } else {
            z = true;
        }
        this.mDimMipmaps = z;
        if (dataBuffer[4] == 1) {
            z2 = true;
        }
        this.mDimFaces = z2;
        long elementID = dataBuffer[5];
        if (elementID != 0) {
            Element element = new Element(elementID, this.mRS);
            this.mElement = element;
            element.updateFromNative();
        }
        calcElementCount();
    }

    public static Type createX(RenderScript rs, Element e, int dimX) {
        if (dimX < 1) {
            throw new RSInvalidStateException("Dimension must be >= 1.");
        }
        long id = rs.nTypeCreate(e.getID(rs), dimX, 0, 0, false, false, 0);
        Type t = new Type(id, rs);
        t.mElement = e;
        t.mDimX = dimX;
        t.calcElementCount();
        return t;
    }

    public static Type createXY(RenderScript rs, Element e, int dimX, int dimY) {
        if (dimX < 1 || dimY < 1) {
            throw new RSInvalidStateException("Dimension must be >= 1.");
        }
        long id = rs.nTypeCreate(e.getID(rs), dimX, dimY, 0, false, false, 0);
        Type t = new Type(id, rs);
        t.mElement = e;
        t.mDimX = dimX;
        t.mDimY = dimY;
        t.calcElementCount();
        return t;
    }

    public static Type createXYZ(RenderScript rs, Element e, int dimX, int dimY, int dimZ) {
        if (dimX < 1 || dimY < 1 || dimZ < 1) {
            throw new RSInvalidStateException("Dimension must be >= 1.");
        }
        long id = rs.nTypeCreate(e.getID(rs), dimX, dimY, dimZ, false, false, 0);
        Type t = new Type(id, rs);
        t.mElement = e;
        t.mDimX = dimX;
        t.mDimY = dimY;
        t.mDimZ = dimZ;
        t.calcElementCount();
        return t;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        boolean mDimFaces;
        boolean mDimMipmaps;
        int mDimY;
        int mDimZ;
        Element mElement;
        RenderScript mRS;
        int mYuv;
        int mDimX = 1;
        int[] mArray = new int[4];

        public Builder(RenderScript rs, Element e) {
            e.checkValid();
            this.mRS = rs;
            this.mElement = e;
        }

        public Builder setX(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension X are not valid.");
            }
            this.mDimX = value;
            return this;
        }

        public Builder setY(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension Y are not valid.");
            }
            this.mDimY = value;
            return this;
        }

        public Builder setZ(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension Z are not valid.");
            }
            this.mDimZ = value;
            return this;
        }

        public Builder setArray(int dim, int value) {
            if (dim < 0 || dim >= 4) {
                throw new RSIllegalArgumentException("Array dimension out of range.");
            }
            this.mArray[dim] = value;
            return this;
        }

        public Builder setMipmaps(boolean value) {
            this.mDimMipmaps = value;
            return this;
        }

        public Builder setFaces(boolean value) {
            this.mDimFaces = value;
            return this;
        }

        public Builder setYuvFormat(int yuvFormat) {
            switch (yuvFormat) {
                case 17:
                case 35:
                case 842094169:
                    this.mYuv = yuvFormat;
                    return this;
                default:
                    throw new RSIllegalArgumentException("Only ImageFormat.NV21, .YV12, and .YUV_420_888 are supported..");
            }
        }

        public Type create() {
            int i = this.mDimZ;
            if (i > 0) {
                if (this.mDimX < 1 || this.mDimY < 1) {
                    throw new RSInvalidStateException("Both X and Y dimension required when Z is present.");
                }
                if (this.mDimFaces) {
                    throw new RSInvalidStateException("Cube maps not supported with 3D types.");
                }
            }
            int i2 = this.mDimY;
            if (i2 > 0 && this.mDimX < 1) {
                throw new RSInvalidStateException("X dimension required when Y is present.");
            }
            boolean z = this.mDimFaces;
            if (z && i2 < 1) {
                throw new RSInvalidStateException("Cube maps require 2D Types.");
            }
            if (this.mYuv != 0 && (i != 0 || z || this.mDimMipmaps)) {
                throw new RSInvalidStateException("YUV only supports basic 2D.");
            }
            int[] arrays = null;
            for (int ct = 3; ct >= 0; ct--) {
                int i3 = this.mArray[ct];
                if (i3 != 0 && arrays == null) {
                    arrays = new int[ct];
                }
                if (i3 == 0 && arrays != null) {
                    throw new RSInvalidStateException("Array dimensions must be contigous from 0.");
                }
            }
            RenderScript renderScript = this.mRS;
            long id = renderScript.nTypeCreate(this.mElement.getID(renderScript), this.mDimX, this.mDimY, this.mDimZ, this.mDimMipmaps, this.mDimFaces, this.mYuv);
            Type t = new Type(id, this.mRS);
            t.mElement = this.mElement;
            t.mDimX = this.mDimX;
            t.mDimY = this.mDimY;
            t.mDimZ = this.mDimZ;
            t.mDimMipmaps = this.mDimMipmaps;
            t.mDimFaces = this.mDimFaces;
            t.mDimYuv = this.mYuv;
            t.mArrays = arrays;
            t.calcElementCount();
            return t;
        }
    }
}
