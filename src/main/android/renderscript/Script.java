package android.renderscript;

import android.content.Context;
import android.util.SparseArray;
import java.io.UnsupportedEncodingException;
@Deprecated
/* loaded from: classes3.dex */
public class Script extends BaseObj {
    private final SparseArray<FieldID> mFIDs;
    private final SparseArray<InvokeID> mIIDs;
    long[] mInIdsBuffer;
    private final SparseArray<KernelID> mKIDs;

    /* loaded from: classes3.dex */
    public static final class KernelID extends BaseObj {
        Script mScript;
        int mSig;
        int mSlot;

        KernelID(long id, RenderScript rs, Script s, int slot, int sig) {
            super(id, rs);
            this.mScript = s;
            this.mSlot = slot;
            this.mSig = sig;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KernelID createKernelID(int slot, int sig, Element ein, Element eout) {
        KernelID k = this.mKIDs.get(slot);
        if (k != null) {
            return k;
        }
        long id = this.mRS.nScriptKernelIDCreate(getID(this.mRS), slot, sig);
        if (id == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        KernelID k2 = new KernelID(id, this.mRS, this, slot, sig);
        this.mKIDs.put(slot, k2);
        return k2;
    }

    /* loaded from: classes3.dex */
    public static final class InvokeID extends BaseObj {
        Script mScript;
        int mSlot;

        InvokeID(long id, RenderScript rs, Script s, int slot) {
            super(id, rs);
            this.mScript = s;
            this.mSlot = slot;
        }
    }

    protected InvokeID createInvokeID(int slot) {
        InvokeID i = this.mIIDs.get(slot);
        if (i != null) {
            return i;
        }
        long id = this.mRS.nScriptInvokeIDCreate(getID(this.mRS), slot);
        if (id == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        InvokeID i2 = new InvokeID(id, this.mRS, this, slot);
        this.mIIDs.put(slot, i2);
        return i2;
    }

    /* loaded from: classes3.dex */
    public static final class FieldID extends BaseObj {
        Script mScript;
        int mSlot;

        FieldID(long id, RenderScript rs, Script s, int slot) {
            super(id, rs);
            this.mScript = s;
            this.mSlot = slot;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FieldID createFieldID(int slot, Element e) {
        FieldID f = this.mFIDs.get(slot);
        if (f != null) {
            return f;
        }
        long id = this.mRS.nScriptFieldIDCreate(getID(this.mRS), slot);
        if (id == 0) {
            throw new RSDriverException("Failed to create FieldID");
        }
        FieldID f2 = new FieldID(id, this.mRS, this, slot);
        this.mFIDs.put(slot, f2);
        return f2;
    }

    protected void invoke(int slot) {
        this.mRS.nScriptInvoke(getID(this.mRS), slot);
    }

    protected void invoke(int slot, FieldPacker v) {
        if (v != null) {
            this.mRS.nScriptInvokeV(getID(this.mRS), slot, v.getData());
        } else {
            this.mRS.nScriptInvoke(getID(this.mRS), slot);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void forEach(int slot, Allocation ain, Allocation aout, FieldPacker v) {
        forEach(slot, ain, aout, v, (LaunchOptions) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void forEach(int slot, Allocation ain, Allocation aout, FieldPacker v, LaunchOptions sc) {
        byte[] params;
        int[] limits;
        this.mRS.validate();
        this.mRS.validateObject(ain);
        this.mRS.validateObject(aout);
        if (ain == null && aout == null && sc == null) {
            throw new RSIllegalArgumentException("At least one of input allocation, output allocation, or LaunchOptions is required to be non-null.");
        }
        long[] in_ids = null;
        if (ain != null) {
            in_ids = this.mInIdsBuffer;
            in_ids[0] = ain.getID(this.mRS);
        }
        long out_id = 0;
        if (aout != null) {
            out_id = aout.getID(this.mRS);
        }
        if (v == null) {
            params = null;
        } else {
            byte[] params2 = v.getData();
            params = params2;
        }
        if (sc != null) {
            int[] limits2 = {sc.xstart, sc.xend, sc.ystart, sc.yend, sc.zstart, sc.zend};
            limits = limits2;
        } else {
            limits = null;
        }
        this.mRS.nScriptForEach(getID(this.mRS), slot, in_ids, out_id, params, limits);
    }

    protected void forEach(int slot, Allocation[] ains, Allocation aout, FieldPacker v) {
        forEach(slot, ains, aout, v, (LaunchOptions) null);
    }

    protected void forEach(int slot, Allocation[] ains, Allocation aout, FieldPacker v, LaunchOptions sc) {
        long[] in_ids;
        long out_id;
        byte[] params;
        int[] limits;
        this.mRS.validate();
        if (ains != null) {
            for (Allocation ain : ains) {
                this.mRS.validateObject(ain);
            }
        }
        this.mRS.validateObject(aout);
        if (ains == null && aout == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        if (ains != null) {
            in_ids = new long[ains.length];
            for (int index = 0; index < ains.length; index++) {
                in_ids[index] = ains[index].getID(this.mRS);
            }
        } else {
            in_ids = null;
        }
        if (aout == null) {
            out_id = 0;
        } else {
            long out_id2 = aout.getID(this.mRS);
            out_id = out_id2;
        }
        if (v == null) {
            params = null;
        } else {
            byte[] params2 = v.getData();
            params = params2;
        }
        if (sc != null) {
            int[] limits2 = {sc.xstart, sc.xend, sc.ystart, sc.yend, sc.zstart, sc.zend};
            limits = limits2;
        } else {
            limits = null;
        }
        this.mRS.nScriptForEach(getID(this.mRS), slot, in_ids, out_id, params, limits);
    }

    protected void reduce(int slot, Allocation[] ains, Allocation aout, LaunchOptions sc) {
        int[] limits;
        this.mRS.validate();
        if (ains == null || ains.length < 1) {
            throw new RSIllegalArgumentException("At least one input is required.");
        }
        if (aout == null) {
            throw new RSIllegalArgumentException("aout is required to be non-null.");
        }
        for (Allocation ain : ains) {
            this.mRS.validateObject(ain);
        }
        long[] in_ids = new long[ains.length];
        for (int index = 0; index < ains.length; index++) {
            in_ids[index] = ains[index].getID(this.mRS);
        }
        long out_id = aout.getID(this.mRS);
        if (sc != null) {
            int[] limits2 = {sc.xstart, sc.xend, sc.ystart, sc.yend, sc.zstart, sc.zend};
            limits = limits2;
        } else {
            limits = null;
        }
        this.mRS.nScriptReduce(getID(this.mRS), slot, in_ids, out_id, limits);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Script(long id, RenderScript rs) {
        super(id, rs);
        this.mKIDs = new SparseArray<>();
        this.mIIDs = new SparseArray<>();
        this.mFIDs = new SparseArray<>();
        this.mInIdsBuffer = new long[1];
        this.guard.open("destroy");
    }

    public void bindAllocation(Allocation va, int slot) {
        this.mRS.validate();
        this.mRS.validateObject(va);
        if (va == null) {
            this.mRS.nScriptBindAllocation(getID(this.mRS), 0L, slot);
            return;
        }
        Context context = this.mRS.getApplicationContext();
        if (context.getApplicationInfo().targetSdkVersion >= 20) {
            Type t = va.mType;
            if (t.hasMipmaps() || t.hasFaces() || t.getY() != 0 || t.getZ() != 0) {
                throw new RSIllegalArgumentException("API 20+ only allows simple 1D allocations to be used with bind.");
            }
        }
        this.mRS.nScriptBindAllocation(getID(this.mRS), va.getID(this.mRS), slot);
    }

    public void setVar(int index, float v) {
        this.mRS.nScriptSetVarF(getID(this.mRS), index, v);
    }

    public float getVarF(int index) {
        return this.mRS.nScriptGetVarF(getID(this.mRS), index);
    }

    public void setVar(int index, double v) {
        this.mRS.nScriptSetVarD(getID(this.mRS), index, v);
    }

    public double getVarD(int index) {
        return this.mRS.nScriptGetVarD(getID(this.mRS), index);
    }

    public void setVar(int index, int v) {
        this.mRS.nScriptSetVarI(getID(this.mRS), index, v);
    }

    public int getVarI(int index) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), index);
    }

    public void setVar(int index, long v) {
        this.mRS.nScriptSetVarJ(getID(this.mRS), index, v);
    }

    public long getVarJ(int index) {
        return this.mRS.nScriptGetVarJ(getID(this.mRS), index);
    }

    public void setVar(int index, boolean v) {
        this.mRS.nScriptSetVarI(getID(this.mRS), index, v ? 1 : 0);
    }

    public boolean getVarB(int index) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), index) > 0;
    }

    public void setVar(int index, BaseObj o) {
        this.mRS.validate();
        this.mRS.validateObject(o);
        this.mRS.nScriptSetVarObj(getID(this.mRS), index, o == null ? 0L : o.getID(this.mRS));
    }

    public void setVar(int index, FieldPacker v) {
        this.mRS.nScriptSetVarV(getID(this.mRS), index, v.getData());
    }

    public void setVar(int index, FieldPacker v, Element e, int[] dims) {
        this.mRS.nScriptSetVarVE(getID(this.mRS), index, v.getData(), e.getID(this.mRS), dims);
    }

    public void getVarV(int index, FieldPacker v) {
        this.mRS.nScriptGetVarV(getID(this.mRS), index, v.getData());
    }

    public void setTimeZone(String timeZone) {
        this.mRS.validate();
        try {
            this.mRS.nScriptSetTimeZone(getID(this.mRS), timeZone.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        RenderScript mRS;

        Builder(RenderScript rs) {
            this.mRS = rs;
        }
    }

    /* loaded from: classes3.dex */
    public static class FieldBase {
        protected Allocation mAllocation;
        protected Element mElement;

        protected void init(RenderScript rs, int dimx) {
            this.mAllocation = Allocation.createSized(rs, this.mElement, dimx, 1);
        }

        protected void init(RenderScript rs, int dimx, int usages) {
            this.mAllocation = Allocation.createSized(rs, this.mElement, dimx, usages | 1);
        }

        protected FieldBase() {
        }

        public Element getElement() {
            return this.mElement;
        }

        public Type getType() {
            return this.mAllocation.getType();
        }

        public Allocation getAllocation() {
            return this.mAllocation;
        }

        public void updateAllocation() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class LaunchOptions {
        private int strategy;
        private int xstart = 0;
        private int ystart = 0;
        private int xend = 0;
        private int yend = 0;
        private int zstart = 0;
        private int zend = 0;

        public LaunchOptions setX(int xstartArg, int xendArg) {
            if (xstartArg < 0 || xendArg <= xstartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.xstart = xstartArg;
            this.xend = xendArg;
            return this;
        }

        public LaunchOptions setY(int ystartArg, int yendArg) {
            if (ystartArg < 0 || yendArg <= ystartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.ystart = ystartArg;
            this.yend = yendArg;
            return this;
        }

        public LaunchOptions setZ(int zstartArg, int zendArg) {
            if (zstartArg < 0 || zendArg <= zstartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.zstart = zstartArg;
            this.zend = zendArg;
            return this;
        }

        public int getXStart() {
            return this.xstart;
        }

        public int getXEnd() {
            return this.xend;
        }

        public int getYStart() {
            return this.ystart;
        }

        public int getYEnd() {
            return this.yend;
        }

        public int getZStart() {
            return this.zstart;
        }

        public int getZEnd() {
            return this.zend;
        }
    }
}
