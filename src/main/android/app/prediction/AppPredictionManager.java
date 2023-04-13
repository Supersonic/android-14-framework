package android.app.prediction;

import android.annotation.SystemApi;
import android.content.Context;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class AppPredictionManager {
    private final Context mContext;

    public AppPredictionManager(Context context) {
        this.mContext = (Context) Objects.requireNonNull(context);
    }

    public AppPredictor createAppPredictionSession(AppPredictionContext predictionContext) {
        return new AppPredictor(this.mContext, predictionContext);
    }
}
