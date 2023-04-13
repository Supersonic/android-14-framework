package android.content;

import java.util.List;
/* loaded from: classes.dex */
public interface Attributable {
    void setAttributionSource(AttributionSource attributionSource);

    static <T extends Attributable> T setAttributionSource(T attributable, AttributionSource attributionSource) {
        if (attributable != null) {
            attributable.setAttributionSource(attributionSource);
        }
        return attributable;
    }

    static <T extends Attributable> List<T> setAttributionSource(List<T> attributableList, AttributionSource attributionSource) {
        if (attributableList != null) {
            int size = attributableList.size();
            for (int i = 0; i < size; i++) {
                setAttributionSource(attributableList.get(i), attributionSource);
            }
        }
        return attributableList;
    }
}
