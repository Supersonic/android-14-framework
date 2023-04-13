package com.android.internal.telephony;

import android.os.SystemClock;
import android.telephony.ClientRequestStats;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ClientWakelockTracker {
    public static final String LOG_TAG = "ClientWakelockTracker";
    @VisibleForTesting
    public HashMap<String, ClientWakelockAccountant> mClients = new HashMap<>();
    @VisibleForTesting
    public ArrayList<ClientWakelockAccountant> mActiveClients = new ArrayList<>();

    @VisibleForTesting
    public void startTracking(String str, int i, int i2, int i3) {
        ClientWakelockAccountant clientWakelockAccountant = getClientWakelockAccountant(str);
        long uptimeMillis = SystemClock.uptimeMillis();
        clientWakelockAccountant.startAttributingWakelock(i, i2, i3, uptimeMillis);
        updateConcurrentRequests(i3, uptimeMillis);
        synchronized (this.mActiveClients) {
            if (!this.mActiveClients.contains(clientWakelockAccountant)) {
                this.mActiveClients.add(clientWakelockAccountant);
            }
        }
    }

    @VisibleForTesting
    public void stopTracking(String str, int i, int i2, int i3) {
        ClientWakelockAccountant clientWakelockAccountant = getClientWakelockAccountant(str);
        long uptimeMillis = SystemClock.uptimeMillis();
        clientWakelockAccountant.stopAttributingWakelock(i, i2, uptimeMillis);
        if (clientWakelockAccountant.getPendingRequestCount() == 0) {
            synchronized (this.mActiveClients) {
                this.mActiveClients.remove(clientWakelockAccountant);
            }
        }
        updateConcurrentRequests(i3, uptimeMillis);
    }

    @VisibleForTesting
    public void stopTrackingAll() {
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this.mActiveClients) {
            Iterator<ClientWakelockAccountant> it = this.mActiveClients.iterator();
            while (it.hasNext()) {
                it.next().stopAllPendingRequests(uptimeMillis);
            }
            this.mActiveClients.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ClientRequestStats> getClientRequestStats() {
        ArrayList arrayList;
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this.mClients) {
            arrayList = new ArrayList(this.mClients.size());
            for (String str : this.mClients.keySet()) {
                ClientWakelockAccountant clientWakelockAccountant = this.mClients.get(str);
                clientWakelockAccountant.updatePendingRequestWakelockTime(uptimeMillis);
                arrayList.add(new ClientRequestStats(clientWakelockAccountant.mRequestStats));
            }
        }
        return arrayList;
    }

    private ClientWakelockAccountant getClientWakelockAccountant(String str) {
        ClientWakelockAccountant clientWakelockAccountant;
        synchronized (this.mClients) {
            if (this.mClients.containsKey(str)) {
                clientWakelockAccountant = this.mClients.get(str);
            } else {
                ClientWakelockAccountant clientWakelockAccountant2 = new ClientWakelockAccountant(str);
                this.mClients.put(str, clientWakelockAccountant2);
                clientWakelockAccountant = clientWakelockAccountant2;
            }
        }
        return clientWakelockAccountant;
    }

    private void updateConcurrentRequests(int i, long j) {
        if (i != 0) {
            synchronized (this.mActiveClients) {
                Iterator<ClientWakelockAccountant> it = this.mActiveClients.iterator();
                while (it.hasNext()) {
                    it.next().changeConcurrentRequests(i, j);
                }
            }
        }
    }

    public boolean isClientActive(String str) {
        ClientWakelockAccountant clientWakelockAccountant = getClientWakelockAccountant(str);
        synchronized (this.mActiveClients) {
            return this.mActiveClients.contains(clientWakelockAccountant);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpClientRequestTracker(PrintWriter printWriter) {
        printWriter.println("-------mClients---------------");
        synchronized (this.mClients) {
            for (String str : this.mClients.keySet()) {
                printWriter.println("Client : " + str);
                printWriter.println(this.mClients.get(str).toString());
            }
        }
    }
}
