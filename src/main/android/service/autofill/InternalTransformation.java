package android.service.autofill;

import android.p008os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.autofill.Helper;
import android.widget.RemoteViews;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public abstract class InternalTransformation implements Transformation, Parcelable {
    private static final String TAG = "InternalTransformation";

    abstract void apply(ValueFinder valueFinder, RemoteViews remoteViews, int i) throws Exception;

    public static boolean batchApply(ValueFinder finder, RemoteViews template, ArrayList<Pair<Integer, InternalTransformation>> transformations) {
        int size = transformations.size();
        if (Helper.sDebug) {
            Log.m112d(TAG, "getPresentation(): applying " + size + " transformations");
        }
        for (int i = 0; i < size; i++) {
            Pair<Integer, InternalTransformation> pair = transformations.get(i);
            int id = pair.first.intValue();
            InternalTransformation transformation = pair.second;
            if (Helper.sDebug) {
                Log.m112d(TAG, "#" + i + ": " + transformation);
            }
            try {
                transformation.apply(finder, template, id);
            } catch (Exception e) {
                Log.m110e(TAG, "Could not apply transformation " + transformation + ": " + e.getClass());
                return false;
            }
        }
        return true;
    }
}
