package com.android.server.vibrator;

import android.os.VibrationEffect;
import android.os.vibrator.VibrationEffectSegment;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class VibrationEffectAdapters {

    /* loaded from: classes2.dex */
    public interface SegmentsAdapter<T> {
        int apply(List<VibrationEffectSegment> list, int i, T t);
    }

    public static <T> VibrationEffect apply(VibrationEffect vibrationEffect, List<SegmentsAdapter<T>> list, T t) {
        if (vibrationEffect instanceof VibrationEffect.Composed) {
            VibrationEffect.Composed composed = (VibrationEffect.Composed) vibrationEffect;
            ArrayList arrayList = new ArrayList(composed.getSegments());
            int repeatIndex = composed.getRepeatIndex();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                repeatIndex = list.get(i).apply(arrayList, repeatIndex, t);
            }
            return new VibrationEffect.Composed(arrayList, repeatIndex);
        }
        return vibrationEffect;
    }
}
