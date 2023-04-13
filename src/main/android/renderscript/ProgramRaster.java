package android.renderscript;
@Deprecated
/* loaded from: classes3.dex */
public class ProgramRaster extends BaseObj {
    CullMode mCullMode;
    boolean mPointSprite;

    /* loaded from: classes3.dex */
    public enum CullMode {
        BACK(0),
        FRONT(1),
        NONE(2);
        
        int mID;

        CullMode(int id) {
            this.mID = id;
        }
    }

    ProgramRaster(long id, RenderScript rs) {
        super(id, rs);
        this.mPointSprite = false;
        this.mCullMode = CullMode.BACK;
    }

    public boolean isPointSpriteEnabled() {
        return this.mPointSprite;
    }

    public CullMode getCullMode() {
        return this.mCullMode;
    }

    public static ProgramRaster CULL_BACK(RenderScript rs) {
        if (rs.mProgramRaster_CULL_BACK == null) {
            Builder builder = new Builder(rs);
            builder.setCullMode(CullMode.BACK);
            rs.mProgramRaster_CULL_BACK = builder.create();
        }
        return rs.mProgramRaster_CULL_BACK;
    }

    public static ProgramRaster CULL_FRONT(RenderScript rs) {
        if (rs.mProgramRaster_CULL_FRONT == null) {
            Builder builder = new Builder(rs);
            builder.setCullMode(CullMode.FRONT);
            rs.mProgramRaster_CULL_FRONT = builder.create();
        }
        return rs.mProgramRaster_CULL_FRONT;
    }

    public static ProgramRaster CULL_NONE(RenderScript rs) {
        if (rs.mProgramRaster_CULL_NONE == null) {
            Builder builder = new Builder(rs);
            builder.setCullMode(CullMode.NONE);
            rs.mProgramRaster_CULL_NONE = builder.create();
        }
        return rs.mProgramRaster_CULL_NONE;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        RenderScript mRS;
        boolean mPointSprite = false;
        CullMode mCullMode = CullMode.BACK;

        public Builder(RenderScript rs) {
            this.mRS = rs;
        }

        public Builder setPointSpriteEnabled(boolean enable) {
            this.mPointSprite = enable;
            return this;
        }

        public Builder setCullMode(CullMode m) {
            this.mCullMode = m;
            return this;
        }

        public ProgramRaster create() {
            this.mRS.validate();
            long id = this.mRS.nProgramRasterCreate(this.mPointSprite, this.mCullMode.mID);
            ProgramRaster programRaster = new ProgramRaster(id, this.mRS);
            programRaster.mPointSprite = this.mPointSprite;
            programRaster.mCullMode = this.mCullMode;
            return programRaster;
        }
    }
}
