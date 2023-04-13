package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Handler;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RegistrantList {
    ArrayList registrants = new ArrayList();

    public synchronized void add(Handler handler, int i, Object obj) {
        add(new Registrant(handler, i, obj));
    }

    public synchronized void addUnique(Handler handler, int i, Object obj) {
        remove(handler);
        add(new Registrant(handler, i, obj));
    }

    public synchronized void add(Registrant registrant) {
        removeCleared();
        this.registrants.add(registrant);
    }

    public synchronized void removeCleared() {
        for (int size = this.registrants.size() - 1; size >= 0; size--) {
            if (((Registrant) this.registrants.get(size)).refH == null) {
                this.registrants.remove(size);
            }
        }
    }

    public synchronized void removeAll() {
        this.registrants.clear();
    }

    public synchronized int size() {
        return this.registrants.size();
    }

    public synchronized Object get(int i) {
        return this.registrants.get(i);
    }

    private synchronized void internalNotifyRegistrants(Object obj, Throwable th) {
        int size = this.registrants.size();
        for (int i = 0; i < size; i++) {
            ((Registrant) this.registrants.get(i)).internalNotifyRegistrant(obj, th);
        }
    }

    public void notifyRegistrants() {
        internalNotifyRegistrants(null, null);
    }

    public void notifyException(Throwable th) {
        internalNotifyRegistrants(null, th);
    }

    public void notifyResult(Object obj) {
        internalNotifyRegistrants(obj, null);
    }

    public void notifyRegistrants(AsyncResult asyncResult) {
        internalNotifyRegistrants(asyncResult.result, asyncResult.exception);
    }

    public synchronized void remove(Handler handler) {
        int size = this.registrants.size();
        for (int i = 0; i < size; i++) {
            Registrant registrant = (Registrant) this.registrants.get(i);
            Handler handler2 = registrant.getHandler();
            if (handler2 == null || handler2 == handler) {
                registrant.clear();
            }
        }
        removeCleared();
    }
}
