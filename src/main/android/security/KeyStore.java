package android.security;

import android.p008os.UserHandle;
/* loaded from: classes3.dex */
public class KeyStore {
    private static final KeyStore KEY_STORE = new KeyStore();
    public static final int NO_ERROR = 1;
    private static final String TAG = "KeyStore";
    public static final int UID_SELF = -1;

    /* loaded from: classes3.dex */
    public enum State {
        UNLOCKED,
        LOCKED,
        UNINITIALIZED
    }

    public static KeyStore getInstance() {
        return KEY_STORE;
    }

    public State state(int userId) {
        int userState = AndroidKeyStoreMaintenance.getState(userId);
        switch (userState) {
            case 0:
                return State.UNINITIALIZED;
            case 1:
                return State.UNLOCKED;
            case 2:
                return State.LOCKED;
            default:
                throw new AssertionError(userState);
        }
    }

    public State state() {
        return state(UserHandle.myUserId());
    }

    public byte[] get(String key) {
        return null;
    }

    public boolean delete(String key) {
        return false;
    }

    public int[] listUidsOfAuthBoundKeys() {
        return null;
    }

    public boolean unlock(String password) {
        return false;
    }

    public boolean isEmpty() {
        return true;
    }

    public int addAuthToken(byte[] authToken) {
        return Authorization.addAuthToken(authToken);
    }

    public void onDeviceOffBody() {
        AndroidKeyStoreMaintenance.onDeviceOffBody();
    }

    public static KeyStoreException getKeyStoreException(int errorCode) {
        return new KeyStoreException(-10000, "Should not be called.");
    }
}
