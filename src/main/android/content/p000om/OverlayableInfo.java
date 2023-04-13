package android.content.p000om;

import android.annotation.NonNull;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* renamed from: android.content.om.OverlayableInfo */
/* loaded from: classes.dex */
public final class OverlayableInfo {
    public final String actor;
    public final String name;

    public OverlayableInfo(String name, String actor) {
        this.name = name;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) name);
        this.actor = actor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OverlayableInfo that = (OverlayableInfo) o;
        if (Objects.equals(this.name, that.name) && Objects.equals(this.actor, that.actor)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.name);
        return (_hash * 31) + Objects.hashCode(this.actor);
    }

    @Deprecated
    private void __metadata() {
    }
}
