package android.media.effect;

import android.filterfw.core.Frame;
/* loaded from: classes2.dex */
public class SizeChangeEffect extends SingleFilterEffect {
    public SizeChangeEffect(EffectContext context, String name, Class filterClass, String inputName, String outputName, Object... finalParameters) {
        super(context, name, filterClass, inputName, outputName, finalParameters);
    }

    @Override // android.media.effect.SingleFilterEffect, android.media.effect.Effect
    public void apply(int inputTexId, int width, int height, int outputTexId) {
        beginGLEffect();
        Frame inputFrame = frameFromTexture(inputTexId, width, height);
        Frame resultFrame = this.mFunction.executeWithArgList(this.mInputName, inputFrame);
        int outputWidth = resultFrame.getFormat().getWidth();
        int outputHeight = resultFrame.getFormat().getHeight();
        Frame outputFrame = frameFromTexture(outputTexId, outputWidth, outputHeight);
        outputFrame.setDataFromFrame(resultFrame);
        inputFrame.release();
        outputFrame.release();
        resultFrame.release();
        endGLEffect();
    }
}
