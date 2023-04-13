package android.animation;

import java.util.List;
/* loaded from: classes.dex */
public interface Keyframes extends Cloneable {

    /* loaded from: classes.dex */
    public interface FloatKeyframes extends Keyframes {
        float getFloatValue(float f);
    }

    /* loaded from: classes.dex */
    public interface IntKeyframes extends Keyframes {
        int getIntValue(float f);
    }

    Keyframes clone();

    List<Keyframe> getKeyframes();

    Class getType();

    Object getValue(float f);

    void setEvaluator(TypeEvaluator typeEvaluator);
}
