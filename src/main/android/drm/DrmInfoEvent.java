package android.drm;

import java.util.HashMap;
@Deprecated
/* loaded from: classes.dex */
public class DrmInfoEvent extends DrmEvent {
    public static final int TYPE_ACCOUNT_ALREADY_REGISTERED = 5;
    public static final int TYPE_ALREADY_REGISTERED_BY_ANOTHER_ACCOUNT = 1;
    public static final int TYPE_REMOVE_RIGHTS = 2;
    public static final int TYPE_RIGHTS_INSTALLED = 3;
    public static final int TYPE_RIGHTS_REMOVED = 6;
    public static final int TYPE_WAIT_FOR_RIGHTS = 4;

    public DrmInfoEvent(int uniqueId, int type, String message) {
        super(uniqueId, type, message);
        checkTypeValidity(type);
    }

    public DrmInfoEvent(int uniqueId, int type, String message, HashMap<String, Object> attributes) {
        super(uniqueId, type, message, attributes);
        checkTypeValidity(type);
    }

    private void checkTypeValidity(int type) {
        if ((type < 1 || type > 6) && type != 1001 && type != 1002) {
            String msg = "Unsupported type: " + type;
            throw new IllegalArgumentException(msg);
        }
    }
}
