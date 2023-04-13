package android.renderscript;

import android.graphics.FontListParser;
import android.provider.BrowserContract;
import android.renderscript.Element;
import android.renderscript.Type;
import java.util.Vector;
@Deprecated
/* loaded from: classes3.dex */
public class Mesh extends BaseObj {
    Allocation[] mIndexBuffers;
    Primitive[] mPrimitives;
    Allocation[] mVertexBuffers;

    /* loaded from: classes3.dex */
    public enum Primitive {
        POINT(0),
        LINE(1),
        LINE_STRIP(2),
        TRIANGLE(3),
        TRIANGLE_STRIP(4),
        TRIANGLE_FAN(5);
        
        int mID;

        Primitive(int id) {
            this.mID = id;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Mesh(long id, RenderScript rs) {
        super(id, rs);
        this.guard.open("destroy");
    }

    public int getVertexAllocationCount() {
        Allocation[] allocationArr = this.mVertexBuffers;
        if (allocationArr == null) {
            return 0;
        }
        return allocationArr.length;
    }

    public Allocation getVertexAllocation(int slot) {
        return this.mVertexBuffers[slot];
    }

    public int getPrimitiveCount() {
        Allocation[] allocationArr = this.mIndexBuffers;
        if (allocationArr == null) {
            return 0;
        }
        return allocationArr.length;
    }

    public Allocation getIndexSetAllocation(int slot) {
        return this.mIndexBuffers[slot];
    }

    public Primitive getPrimitive(int slot) {
        return this.mPrimitives[slot];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.renderscript.BaseObj
    public void updateFromNative() {
        super.updateFromNative();
        int vtxCount = this.mRS.nMeshGetVertexBufferCount(getID(this.mRS));
        int idxCount = this.mRS.nMeshGetIndexCount(getID(this.mRS));
        long[] vtxIDs = new long[vtxCount];
        long[] idxIDs = new long[idxCount];
        int[] primitives = new int[idxCount];
        this.mRS.nMeshGetVertices(getID(this.mRS), vtxIDs, vtxCount);
        this.mRS.nMeshGetIndices(getID(this.mRS), idxIDs, primitives, idxCount);
        this.mVertexBuffers = new Allocation[vtxCount];
        this.mIndexBuffers = new Allocation[idxCount];
        this.mPrimitives = new Primitive[idxCount];
        for (int i = 0; i < vtxCount; i++) {
            if (vtxIDs[i] != 0) {
                this.mVertexBuffers[i] = new Allocation(vtxIDs[i], this.mRS, null, 1);
                this.mVertexBuffers[i].updateFromNative();
            }
        }
        for (int i2 = 0; i2 < idxCount; i2++) {
            if (idxIDs[i2] != 0) {
                this.mIndexBuffers[i2] = new Allocation(idxIDs[i2], this.mRS, null, 1);
                this.mIndexBuffers[i2].updateFromNative();
            }
            this.mPrimitives[i2] = Primitive.values()[primitives[i2]];
        }
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        RenderScript mRS;
        int mUsage;
        int mVertexTypeCount = 0;
        Entry[] mVertexTypes = new Entry[16];
        Vector mIndexTypes = new Vector();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes3.dex */
        public class Entry {

            /* renamed from: e */
            Element f395e;
            Primitive prim;
            int size;

            /* renamed from: t */
            Type f396t;
            int usage;

            Entry() {
            }
        }

        public Builder(RenderScript rs, int usage) {
            this.mRS = rs;
            this.mUsage = usage;
        }

        public int getCurrentVertexTypeIndex() {
            return this.mVertexTypeCount - 1;
        }

        public int getCurrentIndexSetIndex() {
            return this.mIndexTypes.size() - 1;
        }

        public Builder addVertexType(Type t) throws IllegalStateException {
            int i = this.mVertexTypeCount;
            Entry[] entryArr = this.mVertexTypes;
            if (i >= entryArr.length) {
                throw new IllegalStateException("Max vertex types exceeded.");
            }
            entryArr[i] = new Entry();
            this.mVertexTypes[this.mVertexTypeCount].f396t = t;
            this.mVertexTypes[this.mVertexTypeCount].f395e = null;
            this.mVertexTypeCount++;
            return this;
        }

        public Builder addVertexType(Element e, int size) throws IllegalStateException {
            int i = this.mVertexTypeCount;
            Entry[] entryArr = this.mVertexTypes;
            if (i >= entryArr.length) {
                throw new IllegalStateException("Max vertex types exceeded.");
            }
            entryArr[i] = new Entry();
            this.mVertexTypes[this.mVertexTypeCount].f396t = null;
            this.mVertexTypes[this.mVertexTypeCount].f395e = e;
            this.mVertexTypes[this.mVertexTypeCount].size = size;
            this.mVertexTypeCount++;
            return this;
        }

        public Builder addIndexSetType(Type t, Primitive p) {
            Entry indexType = new Entry();
            indexType.f396t = t;
            indexType.f395e = null;
            indexType.size = 0;
            indexType.prim = p;
            this.mIndexTypes.addElement(indexType);
            return this;
        }

        public Builder addIndexSetType(Primitive p) {
            Entry indexType = new Entry();
            indexType.f396t = null;
            indexType.f395e = null;
            indexType.size = 0;
            indexType.prim = p;
            this.mIndexTypes.addElement(indexType);
            return this;
        }

        public Builder addIndexSetType(Element e, int size, Primitive p) {
            Entry indexType = new Entry();
            indexType.f396t = null;
            indexType.f395e = e;
            indexType.size = size;
            indexType.prim = p;
            this.mIndexTypes.addElement(indexType);
            return this;
        }

        Type newType(Element e, int size) {
            Type.Builder tb = new Type.Builder(this.mRS, e);
            tb.setX(size);
            return tb.create();
        }

        public Mesh create() {
            Allocation alloc;
            Allocation alloc2;
            this.mRS.validate();
            long[] vtx = new long[this.mVertexTypeCount];
            long[] idx = new long[this.mIndexTypes.size()];
            int[] prim = new int[this.mIndexTypes.size()];
            Allocation[] vertexBuffers = new Allocation[this.mVertexTypeCount];
            Allocation[] indexBuffers = new Allocation[this.mIndexTypes.size()];
            Primitive[] primitives = new Primitive[this.mIndexTypes.size()];
            for (int ct = 0; ct < this.mVertexTypeCount; ct++) {
                Entry entry = this.mVertexTypes[ct];
                if (entry.f396t != null) {
                    alloc2 = Allocation.createTyped(this.mRS, entry.f396t, this.mUsage);
                } else if (entry.f395e != null) {
                    alloc2 = Allocation.createSized(this.mRS, entry.f395e, entry.size, this.mUsage);
                } else {
                    throw new IllegalStateException("Builder corrupt, no valid element in entry.");
                }
                vertexBuffers[ct] = alloc2;
                vtx[ct] = alloc2.getID(this.mRS);
            }
            for (int ct2 = 0; ct2 < this.mIndexTypes.size(); ct2++) {
                Entry entry2 = (Entry) this.mIndexTypes.elementAt(ct2);
                if (entry2.f396t != null) {
                    alloc = Allocation.createTyped(this.mRS, entry2.f396t, this.mUsage);
                } else if (entry2.f395e != null) {
                    alloc = Allocation.createSized(this.mRS, entry2.f395e, entry2.size, this.mUsage);
                } else {
                    throw new IllegalStateException("Builder corrupt, no valid element in entry.");
                }
                long allocID = alloc == null ? 0L : alloc.getID(this.mRS);
                indexBuffers[ct2] = alloc;
                primitives[ct2] = entry2.prim;
                idx[ct2] = allocID;
                prim[ct2] = entry2.prim.mID;
            }
            long id = this.mRS.nMeshCreate(vtx, idx, prim);
            Mesh newMesh = new Mesh(id, this.mRS);
            newMesh.mVertexBuffers = vertexBuffers;
            newMesh.mIndexBuffers = indexBuffers;
            newMesh.mPrimitives = primitives;
            return newMesh;
        }
    }

    /* loaded from: classes3.dex */
    public static class AllocationBuilder {
        RenderScript mRS;
        int mVertexTypeCount = 0;
        Entry[] mVertexTypes = new Entry[16];
        Vector mIndexTypes = new Vector();

        /* loaded from: classes3.dex */
        class Entry {

            /* renamed from: a */
            Allocation f394a;
            Primitive prim;

            Entry() {
            }
        }

        public AllocationBuilder(RenderScript rs) {
            this.mRS = rs;
        }

        public int getCurrentVertexTypeIndex() {
            return this.mVertexTypeCount - 1;
        }

        public int getCurrentIndexSetIndex() {
            return this.mIndexTypes.size() - 1;
        }

        public AllocationBuilder addVertexAllocation(Allocation a) throws IllegalStateException {
            int i = this.mVertexTypeCount;
            Entry[] entryArr = this.mVertexTypes;
            if (i >= entryArr.length) {
                throw new IllegalStateException("Max vertex types exceeded.");
            }
            entryArr[i] = new Entry();
            this.mVertexTypes[this.mVertexTypeCount].f394a = a;
            this.mVertexTypeCount++;
            return this;
        }

        public AllocationBuilder addIndexSetAllocation(Allocation a, Primitive p) {
            Entry indexType = new Entry();
            indexType.f394a = a;
            indexType.prim = p;
            this.mIndexTypes.addElement(indexType);
            return this;
        }

        public AllocationBuilder addIndexSetType(Primitive p) {
            Entry indexType = new Entry();
            indexType.f394a = null;
            indexType.prim = p;
            this.mIndexTypes.addElement(indexType);
            return this;
        }

        public Mesh create() {
            this.mRS.validate();
            long[] vtx = new long[this.mVertexTypeCount];
            long[] idx = new long[this.mIndexTypes.size()];
            int[] prim = new int[this.mIndexTypes.size()];
            Allocation[] indexBuffers = new Allocation[this.mIndexTypes.size()];
            Primitive[] primitives = new Primitive[this.mIndexTypes.size()];
            Allocation[] vertexBuffers = new Allocation[this.mVertexTypeCount];
            for (int ct = 0; ct < this.mVertexTypeCount; ct++) {
                Entry entry = this.mVertexTypes[ct];
                vertexBuffers[ct] = entry.f394a;
                vtx[ct] = entry.f394a.getID(this.mRS);
            }
            for (int ct2 = 0; ct2 < this.mIndexTypes.size(); ct2++) {
                Entry entry2 = (Entry) this.mIndexTypes.elementAt(ct2);
                long allocID = entry2.f394a == null ? 0L : entry2.f394a.getID(this.mRS);
                indexBuffers[ct2] = entry2.f394a;
                primitives[ct2] = entry2.prim;
                idx[ct2] = allocID;
                prim[ct2] = entry2.prim.mID;
            }
            long id = this.mRS.nMeshCreate(vtx, idx, prim);
            Mesh newMesh = new Mesh(id, this.mRS);
            newMesh.mVertexBuffers = vertexBuffers;
            newMesh.mIndexBuffers = indexBuffers;
            newMesh.mPrimitives = primitives;
            return newMesh;
        }
    }

    /* loaded from: classes3.dex */
    public static class TriangleMeshBuilder {
        public static final int COLOR = 1;
        public static final int NORMAL = 2;
        public static final int TEXTURE_0 = 256;
        Element mElement;
        int mFlags;
        RenderScript mRS;
        int mVtxSize;
        float mNX = 0.0f;
        float mNY = 0.0f;
        float mNZ = -1.0f;
        float mS0 = 0.0f;
        float mT0 = 0.0f;

        /* renamed from: mR */
        float f400mR = 1.0f;

        /* renamed from: mG */
        float f399mG = 1.0f;

        /* renamed from: mB */
        float f398mB = 1.0f;

        /* renamed from: mA */
        float f397mA = 1.0f;
        int mVtxCount = 0;
        int mMaxIndex = 0;
        int mIndexCount = 0;
        float[] mVtxData = new float[128];
        short[] mIndexData = new short[128];

        public TriangleMeshBuilder(RenderScript rs, int vtxSize, int flags) {
            this.mRS = rs;
            this.mVtxSize = vtxSize;
            this.mFlags = flags;
            if (vtxSize < 2 || vtxSize > 3) {
                throw new IllegalArgumentException("Vertex size out of range.");
            }
        }

        private void makeSpace(int count) {
            int i = this.mVtxCount + count;
            float[] fArr = this.mVtxData;
            if (i >= fArr.length) {
                float[] t = new float[fArr.length * 2];
                System.arraycopy(fArr, 0, t, 0, fArr.length);
                this.mVtxData = t;
            }
        }

        private void latch() {
            if ((this.mFlags & 1) != 0) {
                makeSpace(4);
                float[] fArr = this.mVtxData;
                int i = this.mVtxCount;
                int i2 = i + 1;
                this.mVtxCount = i2;
                fArr[i] = this.f400mR;
                int i3 = i2 + 1;
                this.mVtxCount = i3;
                fArr[i2] = this.f399mG;
                int i4 = i3 + 1;
                this.mVtxCount = i4;
                fArr[i3] = this.f398mB;
                this.mVtxCount = i4 + 1;
                fArr[i4] = this.f397mA;
            }
            if ((this.mFlags & 256) != 0) {
                makeSpace(2);
                float[] fArr2 = this.mVtxData;
                int i5 = this.mVtxCount;
                int i6 = i5 + 1;
                this.mVtxCount = i6;
                fArr2[i5] = this.mS0;
                this.mVtxCount = i6 + 1;
                fArr2[i6] = this.mT0;
            }
            if ((this.mFlags & 2) != 0) {
                makeSpace(4);
                float[] fArr3 = this.mVtxData;
                int i7 = this.mVtxCount;
                int i8 = i7 + 1;
                this.mVtxCount = i8;
                fArr3[i7] = this.mNX;
                int i9 = i8 + 1;
                this.mVtxCount = i9;
                fArr3[i8] = this.mNY;
                int i10 = i9 + 1;
                this.mVtxCount = i10;
                fArr3[i9] = this.mNZ;
                this.mVtxCount = i10 + 1;
                fArr3[i10] = 0.0f;
            }
            this.mMaxIndex++;
        }

        public TriangleMeshBuilder addVertex(float x, float y) {
            if (this.mVtxSize != 2) {
                throw new IllegalStateException("add mistmatch with declared components.");
            }
            makeSpace(2);
            float[] fArr = this.mVtxData;
            int i = this.mVtxCount;
            int i2 = i + 1;
            this.mVtxCount = i2;
            fArr[i] = x;
            this.mVtxCount = i2 + 1;
            fArr[i2] = y;
            latch();
            return this;
        }

        public TriangleMeshBuilder addVertex(float x, float y, float z) {
            if (this.mVtxSize != 3) {
                throw new IllegalStateException("add mistmatch with declared components.");
            }
            makeSpace(4);
            float[] fArr = this.mVtxData;
            int i = this.mVtxCount;
            int i2 = i + 1;
            this.mVtxCount = i2;
            fArr[i] = x;
            int i3 = i2 + 1;
            this.mVtxCount = i3;
            fArr[i2] = y;
            int i4 = i3 + 1;
            this.mVtxCount = i4;
            fArr[i3] = z;
            this.mVtxCount = i4 + 1;
            fArr[i4] = 1.0f;
            latch();
            return this;
        }

        public TriangleMeshBuilder setTexture(float s, float t) {
            if ((this.mFlags & 256) == 0) {
                throw new IllegalStateException("add mistmatch with declared components.");
            }
            this.mS0 = s;
            this.mT0 = t;
            return this;
        }

        public TriangleMeshBuilder setNormal(float x, float y, float z) {
            if ((this.mFlags & 2) == 0) {
                throw new IllegalStateException("add mistmatch with declared components.");
            }
            this.mNX = x;
            this.mNY = y;
            this.mNZ = z;
            return this;
        }

        public TriangleMeshBuilder setColor(float r, float g, float b, float a) {
            if ((this.mFlags & 1) == 0) {
                throw new IllegalStateException("add mistmatch with declared components.");
            }
            this.f400mR = r;
            this.f399mG = g;
            this.f398mB = b;
            this.f397mA = a;
            return this;
        }

        public TriangleMeshBuilder addTriangle(int idx1, int idx2, int idx3) {
            int i = this.mMaxIndex;
            if (idx1 >= i || idx1 < 0 || idx2 >= i || idx2 < 0 || idx3 >= i || idx3 < 0) {
                throw new IllegalStateException("Index provided greater than vertex count.");
            }
            int i2 = this.mIndexCount + 3;
            short[] sArr = this.mIndexData;
            if (i2 >= sArr.length) {
                short[] t = new short[sArr.length * 2];
                System.arraycopy(sArr, 0, t, 0, sArr.length);
                this.mIndexData = t;
            }
            short[] t2 = this.mIndexData;
            int i3 = this.mIndexCount;
            int i4 = i3 + 1;
            this.mIndexCount = i4;
            t2[i3] = (short) idx1;
            int i5 = i4 + 1;
            this.mIndexCount = i5;
            t2[i4] = (short) idx2;
            this.mIndexCount = i5 + 1;
            t2[i5] = (short) idx3;
            return this;
        }

        public Mesh create(boolean uploadToBufferObject) {
            Element.Builder b = new Element.Builder(this.mRS);
            b.add(Element.createVector(this.mRS, Element.DataType.FLOAT_32, this.mVtxSize), BrowserContract.Bookmarks.POSITION);
            if ((this.mFlags & 1) != 0) {
                b.add(Element.F32_4(this.mRS), "color");
            }
            if ((this.mFlags & 256) != 0) {
                b.add(Element.F32_2(this.mRS), "texture0");
            }
            if ((this.mFlags & 2) != 0) {
                b.add(Element.F32_3(this.mRS), FontListParser.STYLE_NORMAL);
            }
            this.mElement = b.create();
            int usage = 1;
            if (uploadToBufferObject) {
                usage = 1 | 4;
            }
            Builder smb = new Builder(this.mRS, usage);
            smb.addVertexType(this.mElement, this.mMaxIndex);
            smb.addIndexSetType(Element.U16(this.mRS), this.mIndexCount, Primitive.TRIANGLE);
            Mesh sm = smb.create();
            sm.getVertexAllocation(0).copy1DRangeFromUnchecked(0, this.mMaxIndex, this.mVtxData);
            if (uploadToBufferObject) {
                sm.getVertexAllocation(0).syncAll(1);
            }
            sm.getIndexSetAllocation(0).copy1DRangeFromUnchecked(0, this.mIndexCount, this.mIndexData);
            if (uploadToBufferObject) {
                sm.getIndexSetAllocation(0).syncAll(1);
            }
            return sm;
        }
    }
}
