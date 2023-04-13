package android.graphics;

import android.text.format.DateFormat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class MeshSpecification {
    public static final int ALPHA_TYPE_OPAQUE = 1;
    public static final int ALPHA_TYPE_PREMULTIPLIED = 2;
    public static final int ALPHA_TYPE_UNKNOWN = 0;
    public static final int ALPHA_TYPE_UNPREMULTIPLIED = 3;
    public static final int TYPE_FLOAT = 0;
    public static final int TYPE_FLOAT2 = 1;
    public static final int TYPE_FLOAT3 = 2;
    public static final int TYPE_FLOAT4 = 3;
    public static final int TYPE_UBYTE4 = 4;
    long mNativeMeshSpec;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface AlphaType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface Type {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    private static native long nativeMake(Attribute[] attributeArr, int i, Varying[] varyingArr, String str, String str2);

    private static native long nativeMakeWithAlpha(Attribute[] attributeArr, int i, Varying[] varyingArr, String str, String str2, long j, int i2);

    private static native long nativeMakeWithCS(Attribute[] attributeArr, int i, Varying[] varyingArr, String str, String str2, long j);

    /* loaded from: classes.dex */
    public static class Attribute {
        private final String mName;
        private final int mOffset;
        private final int mType;

        public Attribute(int type, int offset, String name) {
            this.mType = type;
            this.mOffset = offset;
            this.mName = name;
        }

        public int getType() {
            return this.mType;
        }

        public int getOffset() {
            return this.mOffset;
        }

        public String getName() {
            return this.mName;
        }

        public String toString() {
            return "Attribute{mType=" + this.mType + ", mOffset=" + this.mOffset + ", mName='" + this.mName + DateFormat.QUOTE + '}';
        }
    }

    /* loaded from: classes.dex */
    public static class Varying {
        private final String mName;
        private final int mType;

        public Varying(int type, String name) {
            this.mType = type;
            this.mName = name;
        }

        public int getType() {
            return this.mType;
        }

        public String getName() {
            return this.mName;
        }

        public String toString() {
            return "Varying{mType=" + this.mType + ", mName='" + this.mName + DateFormat.QUOTE + '}';
        }
    }

    /* loaded from: classes.dex */
    private static class MeshSpecificationHolder {
        public static final NativeAllocationRegistry MESH_SPECIFICATION_REGISTRY = NativeAllocationRegistry.createMalloced(MeshSpecification.class.getClassLoader(), MeshSpecification.nativeGetFinalizer());

        private MeshSpecificationHolder() {
        }
    }

    public static MeshSpecification make(Attribute[] attributes, int vertexStride, Varying[] varyings, String vertexShader, String fragmentShader) {
        long nativeMeshSpec = nativeMake(attributes, vertexStride, varyings, vertexShader, fragmentShader);
        if (nativeMeshSpec == 0) {
            throw new IllegalArgumentException("MeshSpecification construction failed");
        }
        return new MeshSpecification(nativeMeshSpec);
    }

    public static MeshSpecification make(Attribute[] attributes, int vertexStride, Varying[] varyings, String vertexShader, String fragmentShader, ColorSpace colorSpace) {
        long nativeMeshSpec = nativeMakeWithCS(attributes, vertexStride, varyings, vertexShader, fragmentShader, colorSpace.getNativeInstance());
        if (nativeMeshSpec == 0) {
            throw new IllegalArgumentException("MeshSpecification construction failed");
        }
        return new MeshSpecification(nativeMeshSpec);
    }

    public static MeshSpecification make(Attribute[] attributes, int vertexStride, Varying[] varyings, String vertexShader, String fragmentShader, ColorSpace colorSpace, int alphaType) {
        long nativeMeshSpec = nativeMakeWithAlpha(attributes, vertexStride, varyings, vertexShader, fragmentShader, colorSpace.getNativeInstance(), alphaType);
        if (nativeMeshSpec == 0) {
            throw new IllegalArgumentException("MeshSpecification construction failed");
        }
        return new MeshSpecification(nativeMeshSpec);
    }

    private MeshSpecification(long meshSpec) {
        this.mNativeMeshSpec = meshSpec;
        MeshSpecificationHolder.MESH_SPECIFICATION_REGISTRY.registerNativeAllocation(this, meshSpec);
    }
}
