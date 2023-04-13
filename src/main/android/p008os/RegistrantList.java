package android.p008os;

import java.util.ArrayList;
/* renamed from: android.os.RegistrantList */
/* loaded from: classes3.dex */
public class RegistrantList {
    ArrayList registrants = new ArrayList();

    public synchronized void add(Handler h, int what, Object obj) {
        add(new Registrant(h, what, obj));
    }

    public synchronized void addUnique(Handler h, int what, Object obj) {
        remove(h);
        add(new Registrant(h, what, obj));
    }

    public synchronized void add(Registrant r) {
        removeCleared();
        this.registrants.add(r);
    }

    public synchronized void removeCleared() {
        for (int i = this.registrants.size() - 1; i >= 0; i--) {
            Registrant r = (Registrant) this.registrants.get(i);
            if (r.refH == null) {
                this.registrants.remove(i);
            }
        }
    }

    public synchronized void removeAll() {
        this.registrants.clear();
    }

    public synchronized int size() {
        return this.registrants.size();
    }

    public synchronized Object get(int index) {
        return this.registrants.get(index);
    }

    private synchronized void internalNotifyRegistrants(Object result, Throwable exception) {
        int s = this.registrants.size();
        for (int i = 0; i < s; i++) {
            Registrant r = (Registrant) this.registrants.get(i);
            r.internalNotifyRegistrant(result, exception);
        }
    }

    public void notifyRegistrants() {
        internalNotifyRegistrants(null, null);
    }

    public void notifyException(Throwable exception) {
        internalNotifyRegistrants(null, exception);
    }

    public void notifyResult(Object result) {
        internalNotifyRegistrants(result, null);
    }

    public void notifyRegistrants(AsyncResult ar) {
        internalNotifyRegistrants(ar.result, ar.exception);
    }

    public synchronized void remove(Handler h) {
        int s = this.registrants.size();
        for (int i = 0; i < s; i++) {
            Registrant r = (Registrant) this.registrants.get(i);
            Handler rh = r.getHandler();
            if (rh == null || rh == h) {
                r.clear();
            }
        }
        removeCleared();
    }
}
