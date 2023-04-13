package android.renderscript;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.renderscript.RenderScript;
import android.view.Surface;
import android.view.SurfaceHolder;
@Deprecated
/* loaded from: classes3.dex */
public class RenderScriptGL extends RenderScript {
    int mHeight;
    SurfaceConfig mSurfaceConfig;
    int mWidth;

    /* loaded from: classes3.dex */
    public static class SurfaceConfig {
        int mAlphaMin;
        int mAlphaPref;
        int mColorMin;
        int mColorPref;
        int mDepthMin;
        int mDepthPref;
        int mSamplesMin;
        int mSamplesPref;
        float mSamplesQ;
        int mStencilMin;
        int mStencilPref;

        public SurfaceConfig() {
            this.mDepthMin = 0;
            this.mDepthPref = 0;
            this.mStencilMin = 0;
            this.mStencilPref = 0;
            this.mColorMin = 8;
            this.mColorPref = 8;
            this.mAlphaMin = 0;
            this.mAlphaPref = 0;
            this.mSamplesMin = 1;
            this.mSamplesPref = 1;
            this.mSamplesQ = 1.0f;
        }

        public SurfaceConfig(SurfaceConfig sc) {
            this.mDepthMin = 0;
            this.mDepthPref = 0;
            this.mStencilMin = 0;
            this.mStencilPref = 0;
            this.mColorMin = 8;
            this.mColorPref = 8;
            this.mAlphaMin = 0;
            this.mAlphaPref = 0;
            this.mSamplesMin = 1;
            this.mSamplesPref = 1;
            this.mSamplesQ = 1.0f;
            this.mDepthMin = sc.mDepthMin;
            this.mDepthPref = sc.mDepthPref;
            this.mStencilMin = sc.mStencilMin;
            this.mStencilPref = sc.mStencilPref;
            this.mColorMin = sc.mColorMin;
            this.mColorPref = sc.mColorPref;
            this.mAlphaMin = sc.mAlphaMin;
            this.mAlphaPref = sc.mAlphaPref;
            this.mSamplesMin = sc.mSamplesMin;
            this.mSamplesPref = sc.mSamplesPref;
            this.mSamplesQ = sc.mSamplesQ;
        }

        private void validateRange(int umin, int upref, int rmin, int rmax) {
            if (umin < rmin || umin > rmax) {
                throw new RSIllegalArgumentException("Minimum value provided out of range.");
            }
            if (upref < umin) {
                throw new RSIllegalArgumentException("preferred must be >= Minimum.");
            }
        }

        public void setColor(int minimum, int preferred) {
            validateRange(minimum, preferred, 5, 8);
            this.mColorMin = minimum;
            this.mColorPref = preferred;
        }

        public void setAlpha(int minimum, int preferred) {
            validateRange(minimum, preferred, 0, 8);
            this.mAlphaMin = minimum;
            this.mAlphaPref = preferred;
        }

        public void setDepth(int minimum, int preferred) {
            validateRange(minimum, preferred, 0, 24);
            this.mDepthMin = minimum;
            this.mDepthPref = preferred;
        }

        public void setSamples(int minimum, int preferred, float Q) {
            validateRange(minimum, preferred, 1, 32);
            if (Q < 0.0f || Q > 1.0f) {
                throw new RSIllegalArgumentException("Quality out of 0-1 range.");
            }
            this.mSamplesMin = minimum;
            this.mSamplesPref = preferred;
            this.mSamplesQ = Q;
        }
    }

    public RenderScriptGL(Context ctx, SurfaceConfig sc) {
        super(ctx);
        this.mSurfaceConfig = new SurfaceConfig(sc);
        int sdkVersion = ctx.getApplicationInfo().targetSdkVersion;
        this.mWidth = 0;
        this.mHeight = 0;
        long device = nDeviceCreate();
        int dpi = ctx.getResources().getDisplayMetrics().densityDpi;
        this.mContext = nContextCreateGL(device, 0, sdkVersion, this.mSurfaceConfig.mColorMin, this.mSurfaceConfig.mColorPref, this.mSurfaceConfig.mAlphaMin, this.mSurfaceConfig.mAlphaPref, this.mSurfaceConfig.mDepthMin, this.mSurfaceConfig.mDepthPref, this.mSurfaceConfig.mStencilMin, this.mSurfaceConfig.mStencilPref, this.mSurfaceConfig.mSamplesMin, this.mSurfaceConfig.mSamplesPref, this.mSurfaceConfig.mSamplesQ, dpi);
        if (this.mContext == 0) {
            throw new RSDriverException("Failed to create RS context.");
        }
        this.mMessageThread = new RenderScript.MessageThread(this);
        this.mMessageThread.start();
    }

    public void setSurface(SurfaceHolder sur, int w, int h) {
        validate();
        Surface s = null;
        if (sur != null) {
            s = sur.getSurface();
        }
        this.mWidth = w;
        this.mHeight = h;
        nContextSetSurface(w, h, s);
    }

    public void setSurfaceTexture(SurfaceTexture sur, int w, int h) {
        validate();
        Surface s = null;
        if (sur != null) {
            s = new Surface(sur);
        }
        this.mWidth = w;
        this.mHeight = h;
        nContextSetSurface(w, h, s);
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void pause() {
        validate();
        nContextPause();
    }

    public void resume() {
        validate();
        nContextResume();
    }

    public void bindRootScript(Script s) {
        validate();
        nContextBindRootScript((int) safeID(s));
    }

    public void bindProgramStore(ProgramStore p) {
        validate();
        nContextBindProgramStore((int) safeID(p));
    }

    public void bindProgramFragment(ProgramFragment p) {
        validate();
        nContextBindProgramFragment((int) safeID(p));
    }

    public void bindProgramRaster(ProgramRaster p) {
        validate();
        nContextBindProgramRaster((int) safeID(p));
    }

    public void bindProgramVertex(ProgramVertex p) {
        validate();
        nContextBindProgramVertex((int) safeID(p));
    }
}
