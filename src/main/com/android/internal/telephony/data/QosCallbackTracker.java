package com.android.internal.telephony.data;

import android.net.LinkAddress;
import android.net.QosFilter;
import android.os.Handler;
import android.telephony.data.EpsBearerQosSessionAttributes;
import android.telephony.data.EpsQos;
import android.telephony.data.NrQos;
import android.telephony.data.NrQosSessionAttributes;
import android.telephony.data.QosBearerFilter;
import android.telephony.data.QosBearerSession;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.TelephonyNetworkAgent;
import com.android.internal.telephony.metrics.RcsStats;
import com.android.telephony.Rlog;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class QosCallbackTracker extends Handler {
    private final String mLogTag;
    private final TelephonyNetworkAgent mNetworkAgent;
    private final int mPhoneId;
    private final Map<Integer, QosBearerSession> mQosBearerSessions = new HashMap();
    private final Map<Integer, IFilter> mCallbacksToFilter = new HashMap();
    private final RcsStats mRcsStats = RcsStats.getInstance();

    /* loaded from: classes.dex */
    public interface IFilter {
        boolean matchesLocalAddress(InetAddress inetAddress, int i, int i2);

        boolean matchesProtocol(int i);

        boolean matchesRemoteAddress(InetAddress inetAddress, int i, int i2);
    }

    public QosCallbackTracker(TelephonyNetworkAgent telephonyNetworkAgent, Phone phone) {
        this.mNetworkAgent = telephonyNetworkAgent;
        this.mPhoneId = phone.getPhoneId();
        this.mLogTag = "QOSCT-" + telephonyNetworkAgent.getNetwork().getNetId();
        telephonyNetworkAgent.registerCallback(new TelephonyNetworkAgent.TelephonyNetworkAgentCallback(new Executor() { // from class: com.android.internal.telephony.data.QosCallbackTracker$$ExternalSyntheticLambda3
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                QosCallbackTracker.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.QosCallbackTracker.1
            @Override // com.android.internal.telephony.data.TelephonyNetworkAgent.TelephonyNetworkAgentCallback
            public void onQosCallbackUnregistered(int i) {
            }

            @Override // com.android.internal.telephony.data.TelephonyNetworkAgent.TelephonyNetworkAgentCallback
            public void onQosCallbackRegistered(int i, final QosFilter qosFilter) {
                QosCallbackTracker.this.addFilter(i, new IFilter() { // from class: com.android.internal.telephony.data.QosCallbackTracker.1.1
                    @Override // com.android.internal.telephony.data.QosCallbackTracker.IFilter
                    public boolean matchesLocalAddress(InetAddress inetAddress, int i2, int i3) {
                        return qosFilter.matchesLocalAddress(inetAddress, i2, i3);
                    }

                    @Override // com.android.internal.telephony.data.QosCallbackTracker.IFilter
                    public boolean matchesRemoteAddress(InetAddress inetAddress, int i2, int i3) {
                        return qosFilter.matchesRemoteAddress(inetAddress, i2, i3);
                    }

                    @Override // com.android.internal.telephony.data.QosCallbackTracker.IFilter
                    public boolean matchesProtocol(int i2) {
                        return qosFilter.matchesProtocol(i2);
                    }
                });
            }
        });
    }

    public void addFilter(final int i, final IFilter iFilter) {
        post(new Runnable() { // from class: com.android.internal.telephony.data.QosCallbackTracker$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                QosCallbackTracker.this.lambda$addFilter$0(i, iFilter);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addFilter$0(int i, IFilter iFilter) {
        log("addFilter: callbackId=" + i);
        this.mCallbacksToFilter.put(Integer.valueOf(i), iFilter);
        for (QosBearerSession qosBearerSession : this.mQosBearerSessions.values()) {
            if (doFiltersMatch(qosBearerSession, iFilter)) {
                sendSessionAvailable(i, qosBearerSession, iFilter);
                notifyMetricDedicatedBearerListenerAdded(i, qosBearerSession);
            }
        }
    }

    public void removeFilter(final int i) {
        post(new Runnable() { // from class: com.android.internal.telephony.data.QosCallbackTracker$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                QosCallbackTracker.this.lambda$removeFilter$1(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeFilter$1(int i) {
        log("removeFilter: callbackId=" + i);
        this.mCallbacksToFilter.remove(Integer.valueOf(i));
        notifyMetricDedicatedBearerListenerRemoved(i);
    }

    public void updateSessions(final List<QosBearerSession> list) {
        post(new Runnable() { // from class: com.android.internal.telephony.data.QosCallbackTracker$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                QosCallbackTracker.this.lambda$updateSessions$2(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSessions$2(List list) {
        log("updateSessions: sessions size=" + list.size());
        ArrayList<QosBearerSession> arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        HashSet hashSet = new HashSet();
        Iterator it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            QosBearerSession qosBearerSession = (QosBearerSession) it.next();
            int qosBearerSessionId = qosBearerSession.getQosBearerSessionId();
            hashMap.put(Integer.valueOf(qosBearerSessionId), qosBearerSession);
            QosBearerSession qosBearerSession2 = this.mQosBearerSessions.get(Integer.valueOf(qosBearerSessionId));
            for (Integer num : this.mCallbacksToFilter.keySet()) {
                int intValue = num.intValue();
                IFilter iFilter = this.mCallbacksToFilter.get(Integer.valueOf(intValue));
                boolean doFiltersMatch = doFiltersMatch(qosBearerSession, iFilter);
                boolean z = qosBearerSession2 != null && doFiltersMatch(qosBearerSession2, iFilter);
                if (!z && doFiltersMatch) {
                    sendSessionAvailable(intValue, qosBearerSession, iFilter);
                    i = 1;
                }
                if (z && doFiltersMatch && !qosBearerSession.getQos().equals(qosBearerSession2.getQos())) {
                    sendSessionAvailable(intValue, qosBearerSession, iFilter);
                    i = 2;
                }
                if (!hashSet.contains(Integer.valueOf(qosBearerSessionId)) && doFiltersMatch) {
                    notifyMetricDedicatedBearerEvent(qosBearerSession, i, true);
                    hashSet.add(Integer.valueOf(qosBearerSessionId));
                }
            }
            if (!hashSet.contains(Integer.valueOf(qosBearerSessionId))) {
                notifyMetricDedicatedBearerEvent(qosBearerSession, 1, false);
                hashSet.add(Integer.valueOf(qosBearerSessionId));
                i = 1;
            }
            arrayList.add(qosBearerSession);
        }
        ArrayList<Integer> arrayList2 = new ArrayList();
        hashSet.clear();
        for (QosBearerSession qosBearerSession3 : this.mQosBearerSessions.values()) {
            int qosBearerSessionId2 = qosBearerSession3.getQosBearerSessionId();
            if (!hashMap.containsKey(Integer.valueOf(qosBearerSessionId2))) {
                for (Integer num2 : this.mCallbacksToFilter.keySet()) {
                    int intValue2 = num2.intValue();
                    if (doFiltersMatch(qosBearerSession3, this.mCallbacksToFilter.get(Integer.valueOf(intValue2)))) {
                        sendSessionLost(intValue2, qosBearerSession3);
                        notifyMetricDedicatedBearerEvent(qosBearerSession3, 3, true);
                        hashSet.add(Integer.valueOf(qosBearerSessionId2));
                    }
                }
                arrayList2.add(Integer.valueOf(qosBearerSessionId2));
                if (!hashSet.contains(Integer.valueOf(qosBearerSessionId2))) {
                    notifyMetricDedicatedBearerEvent(qosBearerSession3, 3, false);
                    hashSet.add(Integer.valueOf(qosBearerSessionId2));
                }
            }
        }
        for (QosBearerSession qosBearerSession4 : arrayList) {
            this.mQosBearerSessions.put(Integer.valueOf(qosBearerSession4.getQosBearerSessionId()), qosBearerSession4);
        }
        for (Integer num3 : arrayList2) {
            this.mQosBearerSessions.remove(Integer.valueOf(num3.intValue()));
        }
    }

    private boolean doFiltersMatch(QosBearerSession qosBearerSession, IFilter iFilter) {
        return getMatchingQosBearerFilter(qosBearerSession, iFilter) != null;
    }

    private boolean matchesByLocalAddress(QosBearerFilter qosBearerFilter, IFilter iFilter) {
        int start;
        int end;
        if (qosBearerFilter.getLocalPortRange() != null) {
            if (qosBearerFilter.getLocalPortRange().isValid()) {
                start = qosBearerFilter.getLocalPortRange().getStart();
                end = qosBearerFilter.getLocalPortRange().getEnd();
            }
            return false;
        }
        start = 20;
        end = 65535;
        if (qosBearerFilter.getLocalAddresses().isEmpty()) {
            try {
                return iFilter.matchesLocalAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}), start, end);
            } catch (UnknownHostException unused) {
                return false;
            }
        }
        Iterator it = qosBearerFilter.getLocalAddresses().iterator();
        if (it.hasNext()) {
            return iFilter.matchesLocalAddress(((LinkAddress) it.next()).getAddress(), start, end);
        }
        return false;
    }

    private boolean matchesByRemoteAddress(QosBearerFilter qosBearerFilter, IFilter iFilter) {
        int start;
        int end;
        boolean z = false;
        if (qosBearerFilter.getRemotePortRange() == null) {
            start = 20;
            end = 65535;
        } else if (!qosBearerFilter.getRemotePortRange().isValid()) {
            return false;
        } else {
            start = qosBearerFilter.getRemotePortRange().getStart();
            end = qosBearerFilter.getRemotePortRange().getEnd();
        }
        if (qosBearerFilter.getRemoteAddresses().isEmpty()) {
            try {
                return iFilter.matchesRemoteAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}), start, end);
            } catch (UnknownHostException unused) {
                return false;
            }
        }
        for (LinkAddress linkAddress : qosBearerFilter.getRemoteAddresses()) {
            z = iFilter.matchesRemoteAddress(linkAddress.getAddress(), start, end);
        }
        return z;
    }

    private boolean matchesByProtocol(QosBearerFilter qosBearerFilter, IFilter iFilter, boolean z) {
        int protocol = qosBearerFilter.getProtocol();
        return (protocol == 6 || protocol == 17) ? iFilter.matchesProtocol(protocol) : z;
    }

    private QosBearerFilter getFilterByPrecedence(QosBearerFilter qosBearerFilter, QosBearerFilter qosBearerFilter2) {
        return (qosBearerFilter == null || qosBearerFilter2.getPrecedence() < qosBearerFilter.getPrecedence()) ? qosBearerFilter2 : qosBearerFilter;
    }

    private QosBearerFilter getMatchingQosBearerFilter(QosBearerSession qosBearerSession, IFilter iFilter) {
        boolean z;
        QosBearerFilter qosBearerFilter = null;
        for (QosBearerFilter qosBearerFilter2 : qosBearerSession.getQosBearerFilterList()) {
            boolean z2 = false;
            boolean z3 = true;
            if (qosBearerFilter2.getLocalAddresses().isEmpty() && qosBearerFilter2.getLocalPortRange() == null) {
                z = false;
            } else if (matchesByLocalAddress(qosBearerFilter2, iFilter)) {
                z = false;
                z2 = true;
            } else {
                z = true;
            }
            if (!qosBearerFilter2.getRemoteAddresses().isEmpty() || qosBearerFilter2.getRemotePortRange() != null) {
                if (matchesByRemoteAddress(qosBearerFilter2, iFilter)) {
                    z2 = true;
                } else {
                    z = true;
                }
            }
            if (qosBearerFilter2.getProtocol() != -1) {
                if (matchesByProtocol(qosBearerFilter2, iFilter, z2)) {
                    z2 = true;
                }
                if (!z3 && z2) {
                    qosBearerFilter = getFilterByPrecedence(qosBearerFilter, qosBearerFilter2);
                }
            }
            z3 = z;
            if (!z3) {
                qosBearerFilter = getFilterByPrecedence(qosBearerFilter, qosBearerFilter2);
            }
        }
        return qosBearerFilter;
    }

    private void sendSessionAvailable(int i, QosBearerSession qosBearerSession, IFilter iFilter) {
        QosBearerFilter matchingQosBearerFilter = getMatchingQosBearerFilter(qosBearerSession, iFilter);
        ArrayList arrayList = new ArrayList();
        if (matchingQosBearerFilter.getRemoteAddresses().size() > 0 && matchingQosBearerFilter.getRemotePortRange() != null) {
            arrayList.add(new InetSocketAddress(((LinkAddress) matchingQosBearerFilter.getRemoteAddresses().get(0)).getAddress(), matchingQosBearerFilter.getRemotePortRange().getStart()));
        }
        if (qosBearerSession.getQos() instanceof EpsQos) {
            EpsQos qos = qosBearerSession.getQos();
            this.mNetworkAgent.sendQosSessionAvailable(i, qosBearerSession.getQosBearerSessionId(), new EpsBearerQosSessionAttributes(qos.getQci(), qos.getUplinkBandwidth().getMaxBitrateKbps(), qos.getDownlinkBandwidth().getMaxBitrateKbps(), qos.getDownlinkBandwidth().getGuaranteedBitrateKbps(), qos.getUplinkBandwidth().getGuaranteedBitrateKbps(), arrayList));
        } else {
            NrQos qos2 = qosBearerSession.getQos();
            this.mNetworkAgent.sendQosSessionAvailable(i, qosBearerSession.getQosBearerSessionId(), new NrQosSessionAttributes(qos2.get5Qi(), qos2.getQfi(), qos2.getUplinkBandwidth().getMaxBitrateKbps(), qos2.getDownlinkBandwidth().getMaxBitrateKbps(), qos2.getDownlinkBandwidth().getGuaranteedBitrateKbps(), qos2.getUplinkBandwidth().getGuaranteedBitrateKbps(), qos2.getAveragingWindow(), arrayList));
        }
        notifyMetricDedicatedBearerListenerBearerUpdateSession(i, qosBearerSession);
        log("sendSessionAvailable, callbackId=" + i);
    }

    private void sendSessionLost(int i, QosBearerSession qosBearerSession) {
        this.mNetworkAgent.sendQosSessionLost(i, qosBearerSession.getQosBearerSessionId(), qosBearerSession.getQos() instanceof EpsQos ? 1 : 2);
        log("sendSessionLost, callbackId=" + i);
    }

    private void notifyMetricDedicatedBearerListenerAdded(int i, QosBearerSession qosBearerSession) {
        this.mRcsStats.onImsDedicatedBearerListenerAdded(i, this.mPhoneId, getRatInfoFromSessionInfo(qosBearerSession), getQCIFromSessionInfo(qosBearerSession));
    }

    private void notifyMetricDedicatedBearerListenerBearerUpdateSession(int i, QosBearerSession qosBearerSession) {
        this.mRcsStats.onImsDedicatedBearerListenerUpdateSession(i, this.mPhoneId, getRatInfoFromSessionInfo(qosBearerSession), getQCIFromSessionInfo(qosBearerSession), true);
    }

    private void notifyMetricDedicatedBearerListenerRemoved(int i) {
        this.mRcsStats.onImsDedicatedBearerListenerRemoved(i);
    }

    private int getQCIFromSessionInfo(QosBearerSession qosBearerSession) {
        if (qosBearerSession.getQos() instanceof EpsQos) {
            return qosBearerSession.getQos().getQci();
        }
        if (qosBearerSession.getQos() instanceof NrQos) {
            return qosBearerSession.getQos().get5Qi();
        }
        return 0;
    }

    private int getRatInfoFromSessionInfo(QosBearerSession qosBearerSession) {
        if (qosBearerSession.getQos() instanceof EpsQos) {
            return 13;
        }
        return qosBearerSession.getQos() instanceof NrQos ? 20 : 0;
    }

    private boolean doesLocalConnectionInfoExist(QosBearerSession qosBearerSession) {
        for (QosBearerFilter qosBearerFilter : qosBearerSession.getQosBearerFilterList()) {
            if (!qosBearerFilter.getLocalAddresses().isEmpty() && qosBearerFilter.getLocalPortRange() != null && qosBearerFilter.getLocalPortRange().isValid()) {
                return true;
            }
        }
        return false;
    }

    private boolean doesRemoteConnectionInfoExist(QosBearerSession qosBearerSession) {
        for (QosBearerFilter qosBearerFilter : qosBearerSession.getQosBearerFilterList()) {
            if (!qosBearerFilter.getRemoteAddresses().isEmpty() && qosBearerFilter.getRemotePortRange() != null && qosBearerFilter.getRemotePortRange().isValid()) {
                return true;
            }
        }
        return false;
    }

    private void notifyMetricDedicatedBearerEvent(QosBearerSession qosBearerSession, int i, boolean z) {
        this.mRcsStats.onImsDedicatedBearerEvent(this.mPhoneId, getRatInfoFromSessionInfo(qosBearerSession), getQCIFromSessionInfo(qosBearerSession), i, doesLocalConnectionInfoExist(qosBearerSession), doesRemoteConnectionInfoExist(qosBearerSession), z);
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }
}
