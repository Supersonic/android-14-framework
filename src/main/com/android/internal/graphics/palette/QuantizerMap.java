package com.android.internal.graphics.palette;

import android.app.backup.BackupRestoreEventLogger$$ExternalSyntheticLambda0;
import com.android.internal.graphics.palette.Palette;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes4.dex */
public final class QuantizerMap implements Quantizer {
    private HashMap<Integer, Integer> mColorToCount;
    private Palette mPalette;

    @Override // com.android.internal.graphics.palette.Quantizer
    public void quantize(int[] pixels, int colorCount) {
        HashMap<Integer, Integer> colorToCount = new HashMap<>();
        for (int pixel : pixels) {
            colorToCount.merge(Integer.valueOf(pixel), 1, new BackupRestoreEventLogger$$ExternalSyntheticLambda0());
        }
        this.mColorToCount = colorToCount;
        List<Palette.Swatch> swatches = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : colorToCount.entrySet()) {
            swatches.add(new Palette.Swatch(entry.getKey().intValue(), entry.getValue().intValue()));
        }
        this.mPalette = Palette.from(swatches);
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public List<Palette.Swatch> getQuantizedColors() {
        return this.mPalette.getSwatches();
    }

    public Map<Integer, Integer> getColorToCount() {
        return this.mColorToCount;
    }
}
