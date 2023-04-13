package android.renderscript;

import android.renderscript.Program;
@Deprecated
/* loaded from: classes3.dex */
public class ProgramFragment extends Program {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ProgramFragment(long id, RenderScript rs) {
        super(id, rs);
    }

    /* loaded from: classes3.dex */
    public static class Builder extends Program.BaseProgramBuilder {
        public Builder(RenderScript rs) {
            super(rs);
        }

        public ProgramFragment create() {
            this.mRS.validate();
            long[] tmp = new long[(this.mInputCount + this.mOutputCount + this.mConstantCount + this.mTextureCount) * 2];
            String[] texNames = new String[this.mTextureCount];
            int idx = 0;
            for (int i = 0; i < this.mInputCount; i++) {
                int idx2 = idx + 1;
                tmp[idx] = Program.ProgramParam.INPUT.mID;
                idx = idx2 + 1;
                tmp[idx2] = this.mInputs[i].getID(this.mRS);
            }
            for (int i2 = 0; i2 < this.mOutputCount; i2++) {
                int idx3 = idx + 1;
                tmp[idx] = Program.ProgramParam.OUTPUT.mID;
                idx = idx3 + 1;
                tmp[idx3] = this.mOutputs[i2].getID(this.mRS);
            }
            for (int i3 = 0; i3 < this.mConstantCount; i3++) {
                int idx4 = idx + 1;
                tmp[idx] = Program.ProgramParam.CONSTANT.mID;
                idx = idx4 + 1;
                tmp[idx4] = this.mConstants[i3].getID(this.mRS);
            }
            for (int i4 = 0; i4 < this.mTextureCount; i4++) {
                int idx5 = idx + 1;
                tmp[idx] = Program.ProgramParam.TEXTURE_TYPE.mID;
                idx = idx5 + 1;
                tmp[idx5] = this.mTextureTypes[i4].mID;
                texNames[i4] = this.mTextureNames[i4];
            }
            long id = this.mRS.nProgramFragmentCreate(this.mShader, texNames, tmp);
            ProgramFragment pf = new ProgramFragment(id, this.mRS);
            initProgram(pf);
            return pf;
        }
    }
}
