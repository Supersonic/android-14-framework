package com.android.internal.telephony.metrics;

import android.os.Binder;
import android.telephony.SubscriptionManager;
import android.telephony.ims.FeatureTagState;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.aidl.IRcsConfigCallback;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.metrics.RcsStats;
import com.android.internal.telephony.nano.PersistAtomsProto$GbaEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsDedicatedBearerEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsDedicatedBearerListenerEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationFeatureTagStats;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationServiceDescStats;
import com.android.internal.telephony.nano.PersistAtomsProto$PersistAtoms;
import com.android.internal.telephony.nano.PersistAtomsProto$PresenceNotifyEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsAcsProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsClientProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipDelegateStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipMessageResponse;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportFeatureTagStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportSession;
import com.android.internal.telephony.nano.PersistAtomsProto$UceEventStats;
import com.android.telephony.Rlog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class RcsStats {
    private static final Map<String, Integer> FEATURE_TAGS;
    private static final Map<String, Integer> MESSAGE_TYPE;
    private static final HashSet<String> MMTEL_SERVICE_ID_SET;
    public static final int NONE = -1;
    private static final Map<String, Integer> NOTIFY_REASONS;
    private static final Random RANDOM = new Random();
    private static final HashSet<String> RCS_SERVICE_ID_SET;
    private static final Map<String, Integer> SERVICE_IDS;
    public static final int STATE_DENIED = 2;
    public static final int STATE_DEREGISTERED = 1;
    public static final int STATE_REGISTERED = 0;
    private static final String TAG = "RcsStats";
    private static RcsStats sInstance;
    private static final Map<Long, Integer> sSubscribeTaskIds;
    private SipMessageArray mSipMessage;
    private SipTransportSessionArray mSipTransportSession;
    private final PersistAtomsStorage mAtomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
    @VisibleForTesting
    protected final Map<Integer, PersistAtomsProto$ImsDedicatedBearerListenerEvent> mDedicatedBearerListenerEventMap = new HashMap();
    @VisibleForTesting
    protected final List<PersistAtomsProto$RcsAcsProvisioningStats> mRcsAcsProvisioningStatsList = new ArrayList();
    @VisibleForTesting
    protected final HashMap<Integer, RcsProvisioningCallback> mRcsProvisioningCallbackMap = new HashMap<>();
    private final List<PersistAtomsProto$ImsRegistrationFeatureTagStats> mImsRegistrationFeatureTagStatsList = new ArrayList();
    @VisibleForTesting
    protected final List<PersistAtomsProto$ImsRegistrationServiceDescStats> mImsRegistrationServiceDescStatsList = new ArrayList();
    private List<LastSipDelegateStat> mLastSipDelegateStatList = new ArrayList();
    private HashMap<Integer, SipTransportFeatureTags> mLastFeatureTagStatMap = new HashMap<>();
    private ArrayList<SipMessageArray> mSipMessageArray = new ArrayList<>();
    private ArrayList<SipTransportSessionArray> mSipTransportSessionArray = new ArrayList<>();
    private UceStatsWriterCallback mCallback = null;

    @VisibleForTesting
    protected boolean isValidCarrierId(int i) {
        return i > -1;
    }

    static {
        HashMap hashMap = new HashMap();
        FEATURE_TAGS = hashMap;
        Locale locale = Locale.ROOT;
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.msg,urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.largemsg,urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.deferred\";+g.gsma.rcs.cpm.pager-large".toLowerCase(locale), 2);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.im\"".toLowerCase(locale), 3);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.session\"".toLowerCase(locale), 4);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.fthttp\"".toLowerCase(locale), 5);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.ftsms\"".toLowerCase(locale), 6);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.callcomposer\"".toLowerCase(locale), 7);
        hashMap.put("+g.gsma.callcomposer".toLowerCase(locale), 8);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.callunanswered\"".toLowerCase(locale), 9);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.sharedmap\"".toLowerCase(locale), 10);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.sharedsketch\"".toLowerCase(locale), 11);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geopush\"".toLowerCase(locale), 12);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geosms\"".toLowerCase(locale), 13);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.chatbot\"".toLowerCase(locale), 14);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.chatbot.sa\"".toLowerCase(locale), 15);
        hashMap.put("+g.gsma.rcs.botversion=\"#=1\"".toLowerCase(locale), 16);
        hashMap.put("+g.gsma.rcs.isbot".toLowerCase(locale), 17);
        hashMap.put("+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"".toLowerCase(locale), 18);
        hashMap.put("video".toLowerCase(locale), 19);
        hashMap.put("+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.dp\"".toLowerCase(locale), 20);
        HashMap hashMap2 = new HashMap();
        SERVICE_IDS = hashMap2;
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.mmtel".toLowerCase(locale), 2);
        hashMap2.put("org.openmobilealliance:IM-session".toLowerCase(locale), 3);
        hashMap2.put("org.openmobilealliance:ChatSession".toLowerCase(locale), 4);
        hashMap2.put("org.openmobilealliance:File-Transfer-HTTP".toLowerCase(locale), 5);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.ftsms".toLowerCase(locale), 6);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geopush".toLowerCase(locale), 7);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geosms".toLowerCase(locale), 8);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer".toLowerCase(locale), 9);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callunanswered".toLowerCase(locale), 10);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedmap".toLowerCase(locale), 11);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedsketch".toLowerCase(locale), 12);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot".toLowerCase(locale), 13);
        hashMap2.put("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa".toLowerCase(locale), 14);
        hashMap2.put("org.gsma.rcs.isbot".toLowerCase(locale), 15);
        HashMap hashMap3 = new HashMap();
        MESSAGE_TYPE = hashMap3;
        hashMap3.put("INVITE".toLowerCase(locale), 2);
        hashMap3.put("ACK".toLowerCase(locale), 3);
        hashMap3.put("OPTIONS".toLowerCase(locale), 4);
        hashMap3.put("BYE".toLowerCase(locale), 5);
        hashMap3.put("CANCEL".toLowerCase(locale), 6);
        hashMap3.put("REGISTER".toLowerCase(locale), 7);
        hashMap3.put("PRACK".toLowerCase(locale), 8);
        hashMap3.put("SUBSCRIBE".toLowerCase(locale), 9);
        hashMap3.put("NOTIFY".toLowerCase(locale), 10);
        hashMap3.put("PUBLISH".toLowerCase(locale), 11);
        hashMap3.put("INFO".toLowerCase(locale), 12);
        hashMap3.put("REFER".toLowerCase(locale), 13);
        hashMap3.put("MESSAGE".toLowerCase(locale), 14);
        hashMap3.put("UPDATE".toLowerCase(locale), 15);
        HashMap hashMap4 = new HashMap();
        NOTIFY_REASONS = hashMap4;
        hashMap4.put("deactivated", 2);
        hashMap4.put("probation", 3);
        hashMap4.put("rejected", 4);
        hashMap4.put("timeout", 5);
        hashMap4.put("giveup", 6);
        hashMap4.put("noresource", 7);
        HashSet<String> hashSet = new HashSet<>();
        RCS_SERVICE_ID_SET = hashSet;
        hashSet.add("org.openmobilealliance:IM-session");
        hashSet.add("org.openmobilealliance:ChatSession");
        hashSet.add("org.openmobilealliance:File-Transfer-HTTP");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.ftsms");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geopush");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geosms");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedmap");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedsketch");
        hashSet.add("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot");
        hashSet.add(" org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa");
        hashSet.add("org.gsma.rcs.isbot");
        HashSet<String> hashSet2 = new HashSet<>();
        MMTEL_SERVICE_ID_SET = hashSet2;
        hashSet2.add("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.mmtel");
        hashSet2.add("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer");
        hashSet2.add("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callunanswered");
        sSubscribeTaskIds = new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LastSipDelegateStat {
        public PersistAtomsProto$SipDelegateStats mLastStat;
        public int mSubId;
        private Set<String> mSupportedTags;

        LastSipDelegateStat(int i, Set<String> set) {
            this.mSubId = i;
            this.mSupportedTags = set;
        }

        public void createSipDelegateStat(int i) {
            PersistAtomsProto$SipDelegateStats defaultSipDelegateStat = getDefaultSipDelegateStat(i);
            this.mLastStat = defaultSipDelegateStat;
            defaultSipDelegateStat.uptimeMillis = RcsStats.this.getWallTimeMillis();
            this.mLastStat.destroyReason = -1;
        }

        public void setSipDelegateDestroyReason(int i) {
            this.mLastStat.destroyReason = i;
        }

        public boolean isDestroyed() {
            return this.mLastStat.destroyReason > -1;
        }

        public void conclude(long j) {
            PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats = this.mLastStat;
            long j2 = j - persistAtomsProto$SipDelegateStats.uptimeMillis;
            if (j2 < 1000) {
                RcsStats rcsStats = RcsStats.this;
                rcsStats.logd("concludeSipDelegateStat: discarding transient stats, duration= " + j2);
            } else {
                persistAtomsProto$SipDelegateStats.uptimeMillis = j2;
                RcsStats.this.mAtomsStorage.addSipDelegateStats(RcsStats.copyOf(this.mLastStat));
            }
            this.mLastStat.uptimeMillis = j;
        }

        public boolean compare(int i, Set<String> set) {
            if (i != this.mSubId || set == null || set.isEmpty()) {
                return false;
            }
            for (String str : set) {
                if (!this.mSupportedTags.contains(str)) {
                    return false;
                }
            }
            return true;
        }

        private PersistAtomsProto$SipDelegateStats getDefaultSipDelegateStat(int i) {
            PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats = new PersistAtomsProto$SipDelegateStats();
            persistAtomsProto$SipDelegateStats.dimension = RcsStats.RANDOM.nextInt();
            persistAtomsProto$SipDelegateStats.carrierId = RcsStats.this.getCarrierId(i);
            persistAtomsProto$SipDelegateStats.slotId = RcsStats.this.getSlotId(i);
            return persistAtomsProto$SipDelegateStats;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PersistAtomsProto$SipDelegateStats copyOf(PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats) {
        PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats2 = new PersistAtomsProto$SipDelegateStats();
        persistAtomsProto$SipDelegateStats2.dimension = persistAtomsProto$SipDelegateStats.dimension;
        persistAtomsProto$SipDelegateStats2.slotId = persistAtomsProto$SipDelegateStats.slotId;
        persistAtomsProto$SipDelegateStats2.carrierId = persistAtomsProto$SipDelegateStats.carrierId;
        persistAtomsProto$SipDelegateStats2.destroyReason = persistAtomsProto$SipDelegateStats.destroyReason;
        persistAtomsProto$SipDelegateStats2.uptimeMillis = persistAtomsProto$SipDelegateStats.uptimeMillis;
        return persistAtomsProto$SipDelegateStats2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipTransportFeatureTags {
        private HashMap<String, LastFeatureTagState> mFeatureTagMap = new HashMap<>();
        private int mSubId;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class LastFeatureTagState {
            public int carrierId;
            public int reason;
            public int slotId;
            public int state;
            public long timeStamp;

            LastFeatureTagState(int i, int i2, int i3, int i4, long j) {
                this.carrierId = i;
                this.slotId = i2;
                this.state = i3;
                this.reason = i4;
                this.timeStamp = j;
            }

            public void update(int i, int i2, long j) {
                this.state = i;
                this.reason = i2;
                this.timeStamp = j;
            }

            public void update(long j) {
                this.timeStamp = j;
            }
        }

        SipTransportFeatureTags(int i) {
            this.mSubId = i;
        }

        public HashMap<String, LastFeatureTagState> getLastTagStates() {
            return this.mFeatureTagMap;
        }

        public synchronized void updateLastFeatureTagState(String str, int i, int i2, long j) {
            int carrierId = RcsStats.this.getCarrierId(this.mSubId);
            int slotId = RcsStats.this.getSlotId(this.mSubId);
            if (this.mFeatureTagMap.containsKey(str)) {
                LastFeatureTagState lastFeatureTagState = this.mFeatureTagMap.get(str);
                if (lastFeatureTagState != null) {
                    addFeatureTagStat(str, lastFeatureTagState, j);
                    lastFeatureTagState.update(i, i2, j);
                } else {
                    create(str, carrierId, slotId, i, i2, j);
                }
            } else {
                create(str, carrierId, slotId, i, i2, j);
            }
        }

        public synchronized void conclude(long j) {
            HashMap hashMap = new HashMap();
            hashMap.putAll(this.mFeatureTagMap);
            for (Map.Entry entry : hashMap.entrySet()) {
                String str = (String) entry.getKey();
                addFeatureTagStat(str, (LastFeatureTagState) entry.getValue(), j);
                updateTimeStamp(this.mSubId, str, j);
            }
        }

        private synchronized boolean addFeatureTagStat(String str, LastFeatureTagState lastFeatureTagState, long j) {
            long j2 = j - lastFeatureTagState.timeStamp;
            if (j2 >= 1000 && RcsStats.this.isValidCarrierId(lastFeatureTagState.carrierId)) {
                PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats = new PersistAtomsProto$SipTransportFeatureTagStats();
                int i = lastFeatureTagState.state;
                if (i == 1) {
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason = -1;
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason = lastFeatureTagState.reason;
                } else if (i == 2) {
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason = lastFeatureTagState.reason;
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason = -1;
                } else {
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason = -1;
                    persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason = -1;
                }
                persistAtomsProto$SipTransportFeatureTagStats.carrierId = lastFeatureTagState.carrierId;
                persistAtomsProto$SipTransportFeatureTagStats.slotId = lastFeatureTagState.slotId;
                persistAtomsProto$SipTransportFeatureTagStats.associatedMillis = j2;
                persistAtomsProto$SipTransportFeatureTagStats.featureTagName = RcsStats.this.convertTagNameToValue(str);
                RcsStats.this.mAtomsStorage.addSipTransportFeatureTagStats(persistAtomsProto$SipTransportFeatureTagStats);
                return true;
            }
            RcsStats rcsStats = RcsStats.this;
            rcsStats.logd("conclude: discarding transient stats, duration= " + j2 + ", carrierId = " + lastFeatureTagState.carrierId);
            return false;
        }

        private void updateTimeStamp(int i, String str, long j) {
            HashMap<String, LastFeatureTagState> lastTagStates;
            LastFeatureTagState lastFeatureTagState;
            SipTransportFeatureTags sipTransportFeatureTags = (SipTransportFeatureTags) RcsStats.this.mLastFeatureTagStatMap.get(Integer.valueOf(i));
            if (sipTransportFeatureTags == null || (lastTagStates = sipTransportFeatureTags.getLastTagStates()) == null || !lastTagStates.containsKey(str) || (lastFeatureTagState = lastTagStates.get(str)) == null) {
                return;
            }
            lastFeatureTagState.update(j);
        }

        private LastFeatureTagState create(String str, int i, int i2, int i3, int i4, long j) {
            LastFeatureTagState lastFeatureTagState = new LastFeatureTagState(i, i2, i3, i4, j);
            this.mFeatureTagMap.put(str, lastFeatureTagState);
            return lastFeatureTagState;
        }
    }

    /* loaded from: classes.dex */
    class UceStatsWriterCallback implements UceStatsWriter.UceStatsCallback {
        private RcsStats mRcsStats;

        UceStatsWriterCallback(RcsStats rcsStats) {
            RcsStats.this.logd("created Callback");
            this.mRcsStats = rcsStats;
        }

        public void onImsRegistrationFeatureTagStats(int i, List<String> list, int i2) {
            this.mRcsStats.onImsRegistrationFeatureTagStats(i, list, i2);
        }

        public void onStoreCompleteImsRegistrationFeatureTagStats(int i) {
            this.mRcsStats.onStoreCompleteImsRegistrationFeatureTagStats(i);
        }

        public void onImsRegistrationServiceDescStats(int i, List<String> list, List<String> list2, int i2) {
            this.mRcsStats.onImsRegistrationServiceDescStats(i, list, list2, i2);
        }

        public void onSubscribeResponse(int i, long j, int i2) {
            if (i2 >= 200 && i2 <= 299 && !RcsStats.sSubscribeTaskIds.containsKey(Long.valueOf(j))) {
                RcsStats.sSubscribeTaskIds.put(Long.valueOf(j), 1);
            }
            this.mRcsStats.onUceEventStats(i, 2, true, 0, i2);
        }

        public void onUceEvent(int i, int i2, boolean z, int i3, int i4) {
            int i5;
            int i6 = 1;
            if (i2 != 0) {
                if (i2 == 1) {
                    i5 = 2;
                    this.mRcsStats.onUceEventStats(i, i5, z, i3, i4);
                }
                i6 = 3;
                if (i2 != 2) {
                    if (i2 != 3) {
                        return;
                    }
                    i6 = 4;
                }
            }
            i5 = i6;
            this.mRcsStats.onUceEventStats(i, i5, z, i3, i4);
        }

        public void onSubscribeTerminated(int i, long j, String str) {
            if (RcsStats.sSubscribeTaskIds.containsKey(Long.valueOf(j))) {
                int intValue = ((Integer) RcsStats.sSubscribeTaskIds.get(Long.valueOf(j))).intValue();
                RcsStats.sSubscribeTaskIds.remove(Long.valueOf(j));
                if (intValue == 1) {
                    this.mRcsStats.onPresenceNotifyEvent(i, str, false, false, false, false);
                }
            }
        }

        public void onPresenceNotifyEvent(int i, long j, List<RcsContactUceCapability> list) {
            if (list == null || list.isEmpty()) {
                return;
            }
            if (RcsStats.sSubscribeTaskIds.containsKey(Long.valueOf(j))) {
                RcsStats.sSubscribeTaskIds.replace(Long.valueOf(j), 2);
            }
            for (RcsContactUceCapability rcsContactUceCapability : list) {
                List<RcsContactPresenceTuple> capabilityTuples = rcsContactUceCapability.getCapabilityTuples();
                if (capabilityTuples.isEmpty()) {
                    this.mRcsStats.onPresenceNotifyEvent(i, PhoneConfigurationManager.SSSS, true, false, false, true);
                } else {
                    boolean z = true;
                    boolean z2 = false;
                    boolean z3 = false;
                    for (RcsContactPresenceTuple rcsContactPresenceTuple : capabilityTuples) {
                        String serviceId = rcsContactPresenceTuple.getServiceId();
                        if (!RcsStats.RCS_SERVICE_ID_SET.contains(serviceId)) {
                            if (RcsStats.MMTEL_SERVICE_ID_SET.contains(serviceId)) {
                                if (!serviceId.equals("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer") || !"1.0".equals(rcsContactPresenceTuple.getServiceVersion())) {
                                    z3 = true;
                                    z = false;
                                }
                            }
                        }
                        z2 = true;
                        z = false;
                    }
                    this.mRcsStats.onPresenceNotifyEvent(i, PhoneConfigurationManager.SSSS, true, z2, z3, z);
                }
            }
        }

        public void onStoreCompleteImsRegistrationServiceDescStats(int i) {
            this.mRcsStats.onStoreCompleteImsRegistrationServiceDescStats(i);
        }
    }

    /* loaded from: classes.dex */
    public class RcsProvisioningCallback extends IRcsConfigCallback.Stub {
        private boolean mEnableSingleRegistration;
        private RcsStats mRcsStats;
        private boolean mRegistered;
        private int mSubId;

        public void onConfigurationChanged(byte[] bArr) {
        }

        public void onConfigurationReset() {
        }

        RcsProvisioningCallback(RcsStats rcsStats, int i, boolean z) {
            RcsStats.this.logd("created RcsProvisioningCallback");
            this.mRcsStats = rcsStats;
            this.mSubId = i;
            this.mEnableSingleRegistration = z;
            this.mRegistered = false;
        }

        public synchronized void setEnableSingleRegistration(boolean z) {
            this.mEnableSingleRegistration = z;
        }

        public boolean getRegistered() {
            return this.mRegistered;
        }

        public void setRegistered(boolean z) {
            this.mRegistered = z;
        }

        public void onAutoConfigurationErrorReceived(int i, String str) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mRcsStats.onRcsAcsProvisioningStats(this.mSubId, i, 1, this.mEnableSingleRegistration);
            } finally {
                IRcsConfigCallback.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onRemoved() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mRcsStats.onStoreCompleteRcsAcsProvisioningStats(this.mSubId);
                this.mRcsStats.removeRcsProvisioningCallback(this.mSubId);
            } finally {
                IRcsConfigCallback.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onPreProvisioningReceived(byte[] bArr) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mRcsStats.onRcsAcsProvisioningStats(this.mSubId, ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS, 3, this.mEnableSingleRegistration);
            } finally {
                IRcsConfigCallback.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipMessageArray {
        private String mCallId;
        private int mDirection;
        private String mMethod;

        SipMessageArray(String str, int i, String str2) {
            this.mMethod = str;
            this.mCallId = str2;
            this.mDirection = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void addSipMessageStat(int i, String str, int i2, int i3, int i4) {
            int carrierId = RcsStats.this.getCarrierId(i);
            if (RcsStats.this.isValidCarrierId(carrierId)) {
                PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse = new PersistAtomsProto$SipMessageResponse();
                persistAtomsProto$SipMessageResponse.carrierId = carrierId;
                persistAtomsProto$SipMessageResponse.slotId = RcsStats.this.getSlotId(i);
                persistAtomsProto$SipMessageResponse.sipMessageMethod = RcsStats.this.convertMessageTypeToValue(str);
                persistAtomsProto$SipMessageResponse.sipMessageResponse = i2;
                persistAtomsProto$SipMessageResponse.sipMessageDirection = i3;
                persistAtomsProto$SipMessageResponse.messageError = i4;
                persistAtomsProto$SipMessageResponse.count = 1;
                RcsStats.this.mAtomsStorage.addSipMessageResponse(persistAtomsProto$SipMessageResponse);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SipTransportSessionArray {
        private String mCallId;
        private int mDirection;
        private String mMethod;
        private int mSipResponse = 0;

        SipTransportSessionArray(String str, int i, String str2) {
            this.mMethod = str;
            this.mCallId = str2;
            this.mDirection = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void addSipTransportSessionStat(int i, String str, int i2, int i3, boolean z) {
            int carrierId = RcsStats.this.getCarrierId(i);
            if (RcsStats.this.isValidCarrierId(carrierId)) {
                PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession = new PersistAtomsProto$SipTransportSession();
                persistAtomsProto$SipTransportSession.carrierId = carrierId;
                persistAtomsProto$SipTransportSession.slotId = RcsStats.this.getSlotId(i);
                persistAtomsProto$SipTransportSession.sessionMethod = RcsStats.this.convertMessageTypeToValue(str);
                persistAtomsProto$SipTransportSession.sipMessageDirection = i2;
                persistAtomsProto$SipTransportSession.sipResponse = i3;
                persistAtomsProto$SipTransportSession.sessionCount = 1;
                persistAtomsProto$SipTransportSession.endedGracefullyCount = 1;
                persistAtomsProto$SipTransportSession.isEndedGracefully = z;
                RcsStats.this.mAtomsStorage.addCompleteSipTransportSession(persistAtomsProto$SipTransportSession);
            }
        }
    }

    @VisibleForTesting
    protected RcsStats() {
    }

    public static RcsStats getInstance() {
        RcsStats rcsStats;
        synchronized (RcsStats.class) {
            if (sInstance == null) {
                Rlog.d(TAG, "RcsStats created.");
                sInstance = new RcsStats();
            }
            rcsStats = sInstance;
        }
        return rcsStats;
    }

    public void registerUceCallback() {
        if (this.mCallback == null) {
            this.mCallback = new UceStatsWriterCallback(sInstance);
            Rlog.d(TAG, "UceStatsWriterCallback created.");
            UceStatsWriter.init(this.mCallback);
        }
    }

    public void onImsRegistrationFeatureTagStats(int i, List<String> list, int i2) {
        synchronized (this.mImsRegistrationFeatureTagStatsList) {
            int carrierId = getCarrierId(i);
            if (!isValidCarrierId(carrierId)) {
                flushImsRegistrationFeatureTagStatsInvalid();
                return;
            }
            onStoreCompleteImsRegistrationFeatureTagStats(i);
            if (list == null) {
                Rlog.d(TAG, "featureTagNames is null or empty");
                return;
            }
            for (String str : list) {
                PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats = new PersistAtomsProto$ImsRegistrationFeatureTagStats();
                persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId = carrierId;
                persistAtomsProto$ImsRegistrationFeatureTagStats.slotId = getSlotId(i);
                persistAtomsProto$ImsRegistrationFeatureTagStats.featureTagName = convertTagNameToValue(str);
                persistAtomsProto$ImsRegistrationFeatureTagStats.registrationTech = i2;
                persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis = getWallTimeMillis();
                this.mImsRegistrationFeatureTagStatsList.add(persistAtomsProto$ImsRegistrationFeatureTagStats);
            }
        }
    }

    public void onStoreCompleteImsRegistrationFeatureTagStats(int i) {
        synchronized (this.mImsRegistrationFeatureTagStatsList) {
            int carrierId = getCarrierId(i);
            ArrayList<PersistAtomsProto$ImsRegistrationFeatureTagStats> arrayList = new ArrayList();
            long wallTimeMillis = getWallTimeMillis();
            for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats : this.mImsRegistrationFeatureTagStatsList) {
                if (persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId == carrierId) {
                    persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis = wallTimeMillis - persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis;
                    this.mAtomsStorage.addImsRegistrationFeatureTagStats(persistAtomsProto$ImsRegistrationFeatureTagStats);
                    arrayList.add(persistAtomsProto$ImsRegistrationFeatureTagStats);
                }
            }
            for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats2 : arrayList) {
                this.mImsRegistrationFeatureTagStatsList.remove(persistAtomsProto$ImsRegistrationFeatureTagStats2);
            }
        }
    }

    public void onFlushIncompleteImsRegistrationFeatureTagStats() {
        synchronized (this.mImsRegistrationFeatureTagStatsList) {
            long wallTimeMillis = getWallTimeMillis();
            for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats : this.mImsRegistrationFeatureTagStatsList) {
                PersistAtomsProto$ImsRegistrationFeatureTagStats copyImsRegistrationFeatureTagStats = copyImsRegistrationFeatureTagStats(persistAtomsProto$ImsRegistrationFeatureTagStats);
                copyImsRegistrationFeatureTagStats.registeredMillis = wallTimeMillis - persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis;
                this.mAtomsStorage.addImsRegistrationFeatureTagStats(copyImsRegistrationFeatureTagStats);
                persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis = wallTimeMillis;
            }
        }
    }

    public synchronized void onRcsClientProvisioningStats(int i, int i2) {
        int carrierId = getCarrierId(i);
        if (isValidCarrierId(carrierId)) {
            PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats = new PersistAtomsProto$RcsClientProvisioningStats();
            persistAtomsProto$RcsClientProvisioningStats.carrierId = carrierId;
            persistAtomsProto$RcsClientProvisioningStats.slotId = getSlotId(i);
            persistAtomsProto$RcsClientProvisioningStats.event = i2;
            persistAtomsProto$RcsClientProvisioningStats.count = 1;
            this.mAtomsStorage.addRcsClientProvisioningStats(persistAtomsProto$RcsClientProvisioningStats);
        }
    }

    public void onRcsAcsProvisioningStats(int i, int i2, int i3, boolean z) {
        synchronized (this.mRcsAcsProvisioningStatsList) {
            int carrierId = getCarrierId(i);
            if (!isValidCarrierId(carrierId)) {
                flushRcsAcsProvisioningStatsInvalid();
                return;
            }
            onStoreCompleteRcsAcsProvisioningStats(i);
            PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats = new PersistAtomsProto$RcsAcsProvisioningStats();
            persistAtomsProto$RcsAcsProvisioningStats.carrierId = carrierId;
            persistAtomsProto$RcsAcsProvisioningStats.slotId = getSlotId(i);
            persistAtomsProto$RcsAcsProvisioningStats.responseCode = i2;
            persistAtomsProto$RcsAcsProvisioningStats.responseType = i3;
            persistAtomsProto$RcsAcsProvisioningStats.isSingleRegistrationEnabled = z;
            persistAtomsProto$RcsAcsProvisioningStats.count = 1;
            persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis = getWallTimeMillis();
            this.mRcsAcsProvisioningStatsList.add(persistAtomsProto$RcsAcsProvisioningStats);
        }
    }

    public void onStoreCompleteRcsAcsProvisioningStats(int i) {
        synchronized (this.mRcsAcsProvisioningStatsList) {
            PersistAtomsProto$RcsAcsProvisioningStats rcsAcsProvisioningStats = getRcsAcsProvisioningStats(i);
            if (rcsAcsProvisioningStats != null) {
                rcsAcsProvisioningStats.stateTimerMillis = getWallTimeMillis() - rcsAcsProvisioningStats.stateTimerMillis;
                this.mAtomsStorage.addRcsAcsProvisioningStats(rcsAcsProvisioningStats);
                this.mRcsAcsProvisioningStatsList.remove(rcsAcsProvisioningStats);
            }
        }
    }

    public void onFlushIncompleteRcsAcsProvisioningStats() {
        synchronized (this.mRcsAcsProvisioningStatsList) {
            long wallTimeMillis = getWallTimeMillis();
            for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats : this.mRcsAcsProvisioningStatsList) {
                PersistAtomsProto$RcsAcsProvisioningStats copyRcsAcsProvisioningStats = copyRcsAcsProvisioningStats(persistAtomsProto$RcsAcsProvisioningStats);
                copyRcsAcsProvisioningStats.stateTimerMillis = wallTimeMillis - copyRcsAcsProvisioningStats.stateTimerMillis;
                this.mAtomsStorage.addRcsAcsProvisioningStats(copyRcsAcsProvisioningStats);
                persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis = wallTimeMillis;
            }
        }
    }

    public synchronized void createSipDelegateStats(int i, Set<String> set) {
        if (set != null) {
            if (!set.isEmpty()) {
                getLastSipDelegateStat(i, set).createSipDelegateStat(i);
            }
        }
    }

    public synchronized void onSipDelegateStats(int i, Set<String> set, int i2) {
        if (set != null) {
            if (!set.isEmpty()) {
                getLastSipDelegateStat(i, set).setSipDelegateDestroyReason(i2);
                concludeSipDelegateStat();
            }
        }
    }

    public synchronized void onSipTransportFeatureTagStats(int i, Set<FeatureTagState> set, Set<FeatureTagState> set2, Set<String> set3) {
        long wallTimeMillis = getWallTimeMillis();
        SipTransportFeatureTags lastFeatureTags = getLastFeatureTags(i);
        if (set3 != null && !set3.isEmpty()) {
            for (String str : set3) {
                lastFeatureTags.updateLastFeatureTagState(str, 0, -1, wallTimeMillis);
            }
        }
        if (set != null && !set.isEmpty()) {
            for (FeatureTagState featureTagState : set) {
                lastFeatureTags.updateLastFeatureTagState(featureTagState.getFeatureTag(), 2, featureTagState.getState(), wallTimeMillis);
            }
        }
        if (set2 != null && !set2.isEmpty()) {
            for (FeatureTagState featureTagState2 : set2) {
                lastFeatureTags.updateLastFeatureTagState(featureTagState2.getFeatureTag(), 1, featureTagState2.getState(), wallTimeMillis);
            }
        }
    }

    public synchronized void concludeSipTransportFeatureTagsStat() {
        if (this.mLastFeatureTagStatMap.isEmpty()) {
            return;
        }
        long wallTimeMillis = getWallTimeMillis();
        HashMap hashMap = new HashMap();
        hashMap.putAll(this.mLastFeatureTagStatMap);
        for (SipTransportFeatureTags sipTransportFeatureTags : hashMap.values()) {
            if (sipTransportFeatureTags != null) {
                sipTransportFeatureTags.conclude(wallTimeMillis);
            }
        }
    }

    public synchronized void onSipMessageRequest(String str, String str2, int i) {
        SipMessageArray sipMessageArray = new SipMessageArray(str2, i, str);
        this.mSipMessage = sipMessageArray;
        this.mSipMessageArray.add(sipMessageArray);
    }

    public synchronized void invalidatedMessageResult(int i, String str, int i2, int i3) {
        this.mSipMessage.addSipMessageStat(i, str, 0, i2, i3);
    }

    public synchronized void onSipMessageResponse(int i, final String str, int i2, int i3) {
        SipMessageArray sipMessageArray = (SipMessageArray) this.mSipMessageArray.stream().filter(new Predicate() { // from class: com.android.internal.telephony.metrics.RcsStats$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onSipMessageResponse$0;
                lambda$onSipMessageResponse$0 = RcsStats.lambda$onSipMessageResponse$0(str, (RcsStats.SipMessageArray) obj);
                return lambda$onSipMessageResponse$0;
            }
        }).findFirst().orElse(null);
        if (sipMessageArray != null) {
            this.mSipMessage.addSipMessageStat(i, sipMessageArray.mMethod, i2, sipMessageArray.mDirection, i3);
            this.mSipMessageArray.removeIf(new Predicate() { // from class: com.android.internal.telephony.metrics.RcsStats$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onSipMessageResponse$1;
                    lambda$onSipMessageResponse$1 = RcsStats.lambda$onSipMessageResponse$1(str, (RcsStats.SipMessageArray) obj);
                    return lambda$onSipMessageResponse$1;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onSipMessageResponse$0(String str, SipMessageArray sipMessageArray) {
        return sipMessageArray.mCallId.equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onSipMessageResponse$1(String str, SipMessageArray sipMessageArray) {
        return sipMessageArray.mCallId.equals(str);
    }

    public synchronized void earlySipTransportSession(String str, String str2, int i) {
        SipTransportSessionArray sipTransportSessionArray = new SipTransportSessionArray(str, i, str2);
        this.mSipTransportSession = sipTransportSessionArray;
        this.mSipTransportSessionArray.add(sipTransportSessionArray);
    }

    public synchronized void confirmedSipTransportSession(final String str, int i) {
        SipTransportSessionArray sipTransportSessionArray = (SipTransportSessionArray) this.mSipTransportSessionArray.stream().filter(new Predicate() { // from class: com.android.internal.telephony.metrics.RcsStats$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$confirmedSipTransportSession$2;
                lambda$confirmedSipTransportSession$2 = RcsStats.lambda$confirmedSipTransportSession$2(str, (RcsStats.SipTransportSessionArray) obj);
                return lambda$confirmedSipTransportSession$2;
            }
        }).findFirst().orElse(null);
        if (sipTransportSessionArray != null) {
            sipTransportSessionArray.mSipResponse = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$confirmedSipTransportSession$2(String str, SipTransportSessionArray sipTransportSessionArray) {
        return sipTransportSessionArray.mCallId.equals(str);
    }

    public synchronized void onSipTransportSessionClosed(int i, final String str, int i2, boolean z) {
        SipTransportSessionArray sipTransportSessionArray = (SipTransportSessionArray) this.mSipTransportSessionArray.stream().filter(new Predicate() { // from class: com.android.internal.telephony.metrics.RcsStats$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onSipTransportSessionClosed$3;
                lambda$onSipTransportSessionClosed$3 = RcsStats.lambda$onSipTransportSessionClosed$3(str, (RcsStats.SipTransportSessionArray) obj);
                return lambda$onSipTransportSessionClosed$3;
            }
        }).findFirst().orElse(null);
        if (sipTransportSessionArray != null) {
            if (i2 != 0) {
                sipTransportSessionArray.mSipResponse = i2;
            }
            this.mSipTransportSession.addSipTransportSessionStat(i, sipTransportSessionArray.mMethod, sipTransportSessionArray.mDirection, i2, z);
            this.mSipTransportSessionArray.removeIf(new Predicate() { // from class: com.android.internal.telephony.metrics.RcsStats$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onSipTransportSessionClosed$4;
                    lambda$onSipTransportSessionClosed$4 = RcsStats.lambda$onSipTransportSessionClosed$4(str, (RcsStats.SipTransportSessionArray) obj);
                    return lambda$onSipTransportSessionClosed$4;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onSipTransportSessionClosed$3(String str, SipTransportSessionArray sipTransportSessionArray) {
        return sipTransportSessionArray.mCallId.equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onSipTransportSessionClosed$4(String str, SipTransportSessionArray sipTransportSessionArray) {
        return sipTransportSessionArray.mCallId.equals(str);
    }

    public synchronized void onImsDedicatedBearerListenerAdded(int i, int i2, int i3, int i4) {
        int subId = getSubId(i2);
        int carrierId = getCarrierId(subId);
        if (subId != -1 && isValidCarrierId(carrierId)) {
            if (this.mDedicatedBearerListenerEventMap.containsKey(Integer.valueOf(i))) {
                return;
            }
            PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent = new PersistAtomsProto$ImsDedicatedBearerListenerEvent();
            persistAtomsProto$ImsDedicatedBearerListenerEvent.carrierId = carrierId;
            persistAtomsProto$ImsDedicatedBearerListenerEvent.slotId = i2;
            persistAtomsProto$ImsDedicatedBearerListenerEvent.ratAtEnd = i3;
            persistAtomsProto$ImsDedicatedBearerListenerEvent.qci = i4;
            persistAtomsProto$ImsDedicatedBearerListenerEvent.dedicatedBearerEstablished = false;
            persistAtomsProto$ImsDedicatedBearerListenerEvent.eventCount = 1;
            this.mDedicatedBearerListenerEventMap.put(Integer.valueOf(i), persistAtomsProto$ImsDedicatedBearerListenerEvent);
        }
    }

    public synchronized void onImsDedicatedBearerListenerUpdateSession(int i, int i2, int i3, int i4, boolean z) {
        int subId = getSubId(i2);
        int carrierId = getCarrierId(subId);
        if (subId != -1 && isValidCarrierId(carrierId)) {
            if (this.mDedicatedBearerListenerEventMap.containsKey(Integer.valueOf(i))) {
                PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent = this.mDedicatedBearerListenerEventMap.get(Integer.valueOf(i));
                persistAtomsProto$ImsDedicatedBearerListenerEvent.ratAtEnd = i3;
                persistAtomsProto$ImsDedicatedBearerListenerEvent.qci = i4;
                persistAtomsProto$ImsDedicatedBearerListenerEvent.dedicatedBearerEstablished = z;
                this.mDedicatedBearerListenerEventMap.replace(Integer.valueOf(i), persistAtomsProto$ImsDedicatedBearerListenerEvent);
            } else {
                PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent2 = new PersistAtomsProto$ImsDedicatedBearerListenerEvent();
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.carrierId = carrierId;
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.slotId = i2;
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.ratAtEnd = i3;
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.qci = i4;
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.dedicatedBearerEstablished = z;
                persistAtomsProto$ImsDedicatedBearerListenerEvent2.eventCount = 1;
                this.mDedicatedBearerListenerEventMap.put(Integer.valueOf(i), persistAtomsProto$ImsDedicatedBearerListenerEvent2);
            }
        }
    }

    public synchronized void onImsDedicatedBearerListenerRemoved(int i) {
        if (this.mDedicatedBearerListenerEventMap.containsKey(Integer.valueOf(i))) {
            this.mAtomsStorage.addImsDedicatedBearerListenerEvent(this.mDedicatedBearerListenerEventMap.get(Integer.valueOf(i)));
            this.mDedicatedBearerListenerEventMap.remove(Integer.valueOf(i));
        }
    }

    public synchronized void onImsDedicatedBearerEvent(int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        int subId = getSubId(i);
        if (subId == -1) {
            return;
        }
        PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent = new PersistAtomsProto$ImsDedicatedBearerEvent();
        persistAtomsProto$ImsDedicatedBearerEvent.carrierId = getCarrierId(subId);
        persistAtomsProto$ImsDedicatedBearerEvent.slotId = getSlotId(subId);
        persistAtomsProto$ImsDedicatedBearerEvent.ratAtEnd = i2;
        persistAtomsProto$ImsDedicatedBearerEvent.qci = i3;
        persistAtomsProto$ImsDedicatedBearerEvent.bearerState = i4;
        persistAtomsProto$ImsDedicatedBearerEvent.localConnectionInfoReceived = z;
        persistAtomsProto$ImsDedicatedBearerEvent.remoteConnectionInfoReceived = z2;
        persistAtomsProto$ImsDedicatedBearerEvent.hasListeners = z3;
        persistAtomsProto$ImsDedicatedBearerEvent.count = 1;
        this.mAtomsStorage.addImsDedicatedBearerEvent(persistAtomsProto$ImsDedicatedBearerEvent);
    }

    public void onImsRegistrationServiceDescStats(int i, List<String> list, List<String> list2, int i2) {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            int carrierId = getCarrierId(i);
            if (!isValidCarrierId(carrierId)) {
                handleImsRegistrationServiceDescStats();
                return;
            }
            onStoreCompleteImsRegistrationServiceDescStats(i);
            if (list == null) {
                Rlog.d(TAG, "serviceIds is null or empty");
                return;
            }
            int i3 = 0;
            for (String str : list) {
                PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats = new PersistAtomsProto$ImsRegistrationServiceDescStats();
                persistAtomsProto$ImsRegistrationServiceDescStats.carrierId = carrierId;
                persistAtomsProto$ImsRegistrationServiceDescStats.slotId = getSlotId(i);
                persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdName = convertServiceIdToValue(str);
                persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdVersion = Float.parseFloat(list2.get(i3));
                persistAtomsProto$ImsRegistrationServiceDescStats.registrationTech = i2;
                this.mImsRegistrationServiceDescStatsList.add(persistAtomsProto$ImsRegistrationServiceDescStats);
                i3++;
            }
        }
    }

    public void onFlushIncompleteImsRegistrationServiceDescStats() {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : this.mImsRegistrationServiceDescStatsList) {
                PersistAtomsProto$ImsRegistrationServiceDescStats copyImsRegistrationServiceDescStats = copyImsRegistrationServiceDescStats(persistAtomsProto$ImsRegistrationServiceDescStats);
                long wallTimeMillis = getWallTimeMillis();
                copyImsRegistrationServiceDescStats.publishedMillis = wallTimeMillis - persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis;
                this.mAtomsStorage.addImsRegistrationServiceDescStats(copyImsRegistrationServiceDescStats);
                persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis = wallTimeMillis;
            }
        }
    }

    public synchronized void onUceEventStats(int i, int i2, boolean z, int i3, int i4) {
        PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats = new PersistAtomsProto$UceEventStats();
        int carrierId = getCarrierId(i);
        if (!isValidCarrierId(carrierId)) {
            handleImsRegistrationServiceDescStats();
            return;
        }
        persistAtomsProto$UceEventStats.carrierId = carrierId;
        persistAtomsProto$UceEventStats.slotId = getSlotId(i);
        persistAtomsProto$UceEventStats.type = i2;
        persistAtomsProto$UceEventStats.successful = z;
        persistAtomsProto$UceEventStats.commandCode = i3;
        persistAtomsProto$UceEventStats.networkResponse = i4;
        persistAtomsProto$UceEventStats.count = 1;
        this.mAtomsStorage.addUceEventStats(persistAtomsProto$UceEventStats);
        if (i2 == 1) {
            if (z) {
                setImsRegistrationServiceDescStatsTime(persistAtomsProto$UceEventStats.carrierId);
            } else {
                deleteImsRegistrationServiceDescStats(persistAtomsProto$UceEventStats.carrierId);
            }
        }
    }

    public synchronized void onPresenceNotifyEvent(int i, String str, boolean z, boolean z2, boolean z3, boolean z4) {
        PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent = new PersistAtomsProto$PresenceNotifyEvent();
        int carrierId = getCarrierId(i);
        if (!isValidCarrierId(carrierId)) {
            handleImsRegistrationServiceDescStats();
            return;
        }
        persistAtomsProto$PresenceNotifyEvent.carrierId = carrierId;
        persistAtomsProto$PresenceNotifyEvent.slotId = getSlotId(i);
        persistAtomsProto$PresenceNotifyEvent.reason = convertPresenceNotifyReason(str);
        persistAtomsProto$PresenceNotifyEvent.contentBodyReceived = z;
        persistAtomsProto$PresenceNotifyEvent.rcsCapsCount = z2 ? 1 : 0;
        persistAtomsProto$PresenceNotifyEvent.mmtelCapsCount = z3 ? 1 : 0;
        persistAtomsProto$PresenceNotifyEvent.noCapsCount = z4 ? 1 : 0;
        persistAtomsProto$PresenceNotifyEvent.count = 1;
        this.mAtomsStorage.addPresenceNotifyEvent(persistAtomsProto$PresenceNotifyEvent);
    }

    public void onStoreCompleteImsRegistrationServiceDescStats(int i) {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            int carrierId = getCarrierId(i);
            ArrayList<PersistAtomsProto$ImsRegistrationServiceDescStats> arrayList = new ArrayList();
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : this.mImsRegistrationServiceDescStatsList) {
                if (persistAtomsProto$ImsRegistrationServiceDescStats.carrierId == carrierId) {
                    persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis = getWallTimeMillis() - persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis;
                    this.mAtomsStorage.addImsRegistrationServiceDescStats(persistAtomsProto$ImsRegistrationServiceDescStats);
                    arrayList.add(persistAtomsProto$ImsRegistrationServiceDescStats);
                }
            }
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 : arrayList) {
                this.mImsRegistrationServiceDescStatsList.remove(persistAtomsProto$ImsRegistrationServiceDescStats2);
            }
        }
    }

    public synchronized void onGbaSuccessEvent(int i) {
        int carrierId = getCarrierId(i);
        if (isValidCarrierId(carrierId)) {
            PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent = new PersistAtomsProto$GbaEvent();
            persistAtomsProto$GbaEvent.carrierId = carrierId;
            persistAtomsProto$GbaEvent.slotId = getSlotId(i);
            persistAtomsProto$GbaEvent.successful = true;
            persistAtomsProto$GbaEvent.failedReason = -1;
            persistAtomsProto$GbaEvent.count = 1;
            this.mAtomsStorage.addGbaEvent(persistAtomsProto$GbaEvent);
        }
    }

    public synchronized void onGbaFailureEvent(int i, int i2) {
        int carrierId = getCarrierId(i);
        if (isValidCarrierId(carrierId)) {
            PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent = new PersistAtomsProto$GbaEvent();
            persistAtomsProto$GbaEvent.carrierId = carrierId;
            persistAtomsProto$GbaEvent.slotId = getSlotId(i);
            persistAtomsProto$GbaEvent.successful = false;
            persistAtomsProto$GbaEvent.failedReason = i2;
            persistAtomsProto$GbaEvent.count = 1;
            this.mAtomsStorage.addGbaEvent(persistAtomsProto$GbaEvent);
        }
    }

    public synchronized RcsProvisioningCallback getRcsProvisioningCallback(int i, boolean z) {
        RcsProvisioningCallback rcsProvisioningCallback = this.mRcsProvisioningCallbackMap.get(Integer.valueOf(i));
        if (rcsProvisioningCallback != null) {
            return rcsProvisioningCallback;
        }
        RcsProvisioningCallback rcsProvisioningCallback2 = new RcsProvisioningCallback(this, i, z);
        this.mRcsProvisioningCallbackMap.put(Integer.valueOf(i), rcsProvisioningCallback2);
        return rcsProvisioningCallback2;
    }

    public synchronized void setEnableSingleRegistration(int i, boolean z) {
        RcsProvisioningCallback rcsProvisioningCallback = this.mRcsProvisioningCallbackMap.get(Integer.valueOf(i));
        if (rcsProvisioningCallback != null) {
            rcsProvisioningCallback.setEnableSingleRegistration(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void removeRcsProvisioningCallback(int i) {
        this.mRcsProvisioningCallbackMap.remove(Integer.valueOf(i));
    }

    private PersistAtomsProto$ImsRegistrationFeatureTagStats copyImsRegistrationFeatureTagStats(PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats) {
        PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats2 = new PersistAtomsProto$ImsRegistrationFeatureTagStats();
        persistAtomsProto$ImsRegistrationFeatureTagStats2.carrierId = persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId;
        persistAtomsProto$ImsRegistrationFeatureTagStats2.slotId = persistAtomsProto$ImsRegistrationFeatureTagStats.slotId;
        persistAtomsProto$ImsRegistrationFeatureTagStats2.featureTagName = persistAtomsProto$ImsRegistrationFeatureTagStats.featureTagName;
        persistAtomsProto$ImsRegistrationFeatureTagStats2.registrationTech = persistAtomsProto$ImsRegistrationFeatureTagStats.registrationTech;
        persistAtomsProto$ImsRegistrationFeatureTagStats2.registeredMillis = persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis;
        return persistAtomsProto$ImsRegistrationFeatureTagStats2;
    }

    private PersistAtomsProto$RcsAcsProvisioningStats copyRcsAcsProvisioningStats(PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats) {
        PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats2 = new PersistAtomsProto$RcsAcsProvisioningStats();
        persistAtomsProto$RcsAcsProvisioningStats2.carrierId = persistAtomsProto$RcsAcsProvisioningStats.carrierId;
        persistAtomsProto$RcsAcsProvisioningStats2.slotId = persistAtomsProto$RcsAcsProvisioningStats.slotId;
        persistAtomsProto$RcsAcsProvisioningStats2.responseCode = persistAtomsProto$RcsAcsProvisioningStats.responseCode;
        persistAtomsProto$RcsAcsProvisioningStats2.responseType = persistAtomsProto$RcsAcsProvisioningStats.responseType;
        persistAtomsProto$RcsAcsProvisioningStats2.isSingleRegistrationEnabled = persistAtomsProto$RcsAcsProvisioningStats.isSingleRegistrationEnabled;
        persistAtomsProto$RcsAcsProvisioningStats2.count = persistAtomsProto$RcsAcsProvisioningStats.count;
        persistAtomsProto$RcsAcsProvisioningStats2.stateTimerMillis = persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis;
        return persistAtomsProto$RcsAcsProvisioningStats2;
    }

    private PersistAtomsProto$ImsRegistrationServiceDescStats copyImsRegistrationServiceDescStats(PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats) {
        PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 = new PersistAtomsProto$ImsRegistrationServiceDescStats();
        persistAtomsProto$ImsRegistrationServiceDescStats2.carrierId = persistAtomsProto$ImsRegistrationServiceDescStats.carrierId;
        persistAtomsProto$ImsRegistrationServiceDescStats2.slotId = persistAtomsProto$ImsRegistrationServiceDescStats.slotId;
        persistAtomsProto$ImsRegistrationServiceDescStats2.serviceIdName = persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdName;
        persistAtomsProto$ImsRegistrationServiceDescStats2.serviceIdVersion = persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdVersion;
        persistAtomsProto$ImsRegistrationServiceDescStats2.registrationTech = persistAtomsProto$ImsRegistrationServiceDescStats.registrationTech;
        return persistAtomsProto$ImsRegistrationServiceDescStats2;
    }

    private void setImsRegistrationServiceDescStatsTime(int i) {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : this.mImsRegistrationServiceDescStatsList) {
                if (persistAtomsProto$ImsRegistrationServiceDescStats.carrierId == i) {
                    persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis = getWallTimeMillis();
                }
            }
        }
    }

    private void deleteImsRegistrationServiceDescStats(int i) {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            ArrayList<PersistAtomsProto$ImsRegistrationServiceDescStats> arrayList = new ArrayList();
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : this.mImsRegistrationServiceDescStatsList) {
                if (persistAtomsProto$ImsRegistrationServiceDescStats.carrierId == i) {
                    arrayList.add(persistAtomsProto$ImsRegistrationServiceDescStats);
                }
            }
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 : arrayList) {
                this.mImsRegistrationServiceDescStatsList.remove(persistAtomsProto$ImsRegistrationServiceDescStats2);
            }
        }
    }

    private void handleImsRegistrationServiceDescStats() {
        synchronized (this.mImsRegistrationServiceDescStatsList) {
            ArrayList<PersistAtomsProto$ImsRegistrationServiceDescStats> arrayList = new ArrayList();
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : this.mImsRegistrationServiceDescStatsList) {
                if (persistAtomsProto$ImsRegistrationServiceDescStats.carrierId != getCarrierId(getSubId(persistAtomsProto$ImsRegistrationServiceDescStats.slotId))) {
                    arrayList.add(persistAtomsProto$ImsRegistrationServiceDescStats);
                    if (persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis != 0) {
                        persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis = getWallTimeMillis() - persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis;
                        this.mAtomsStorage.addImsRegistrationServiceDescStats(persistAtomsProto$ImsRegistrationServiceDescStats);
                    }
                }
            }
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 : arrayList) {
                this.mImsRegistrationServiceDescStatsList.remove(persistAtomsProto$ImsRegistrationServiceDescStats2);
            }
        }
    }

    private PersistAtomsProto$RcsAcsProvisioningStats getRcsAcsProvisioningStats(int i) {
        int carrierId = getCarrierId(i);
        int slotId = getSlotId(i);
        for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats : this.mRcsAcsProvisioningStatsList) {
            if (persistAtomsProto$RcsAcsProvisioningStats != null && persistAtomsProto$RcsAcsProvisioningStats.carrierId == carrierId && persistAtomsProto$RcsAcsProvisioningStats.slotId == slotId) {
                return persistAtomsProto$RcsAcsProvisioningStats;
            }
        }
        return null;
    }

    private void flushRcsAcsProvisioningStatsInvalid() {
        ArrayList<PersistAtomsProto$RcsAcsProvisioningStats> arrayList = new ArrayList();
        for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats : this.mRcsAcsProvisioningStatsList) {
            if (persistAtomsProto$RcsAcsProvisioningStats.carrierId != getCarrierId(getSubId(persistAtomsProto$RcsAcsProvisioningStats.slotId))) {
                arrayList.add(persistAtomsProto$RcsAcsProvisioningStats);
            }
        }
        for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats2 : arrayList) {
            persistAtomsProto$RcsAcsProvisioningStats2.stateTimerMillis = getWallTimeMillis() - persistAtomsProto$RcsAcsProvisioningStats2.stateTimerMillis;
            this.mAtomsStorage.addRcsAcsProvisioningStats(persistAtomsProto$RcsAcsProvisioningStats2);
            this.mRcsAcsProvisioningStatsList.remove(persistAtomsProto$RcsAcsProvisioningStats2);
        }
        arrayList.clear();
    }

    private void flushImsRegistrationFeatureTagStatsInvalid() {
        ArrayList<PersistAtomsProto$ImsRegistrationFeatureTagStats> arrayList = new ArrayList();
        for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats : this.mImsRegistrationFeatureTagStatsList) {
            if (persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId != getCarrierId(getSubId(persistAtomsProto$ImsRegistrationFeatureTagStats.slotId))) {
                arrayList.add(persistAtomsProto$ImsRegistrationFeatureTagStats);
            }
        }
        for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats2 : arrayList) {
            persistAtomsProto$ImsRegistrationFeatureTagStats2.registeredMillis = getWallTimeMillis() - persistAtomsProto$ImsRegistrationFeatureTagStats2.registeredMillis;
            this.mAtomsStorage.addImsRegistrationFeatureTagStats(persistAtomsProto$ImsRegistrationFeatureTagStats2);
            this.mImsRegistrationFeatureTagStatsList.remove(persistAtomsProto$ImsRegistrationFeatureTagStats2);
        }
        arrayList.clear();
    }

    private LastSipDelegateStat getLastSipDelegateStat(int i, Set<String> set) {
        LastSipDelegateStat lastSipDelegateStat;
        Iterator<LastSipDelegateStat> it = this.mLastSipDelegateStatList.iterator();
        while (true) {
            if (!it.hasNext()) {
                lastSipDelegateStat = null;
                break;
            }
            lastSipDelegateStat = it.next();
            if (lastSipDelegateStat.compare(i, set)) {
                break;
            }
        }
        if (lastSipDelegateStat == null) {
            LastSipDelegateStat lastSipDelegateStat2 = new LastSipDelegateStat(i, set);
            this.mLastSipDelegateStatList.add(lastSipDelegateStat2);
            return lastSipDelegateStat2;
        }
        return lastSipDelegateStat;
    }

    private void concludeSipDelegateStat() {
        if (this.mLastSipDelegateStatList.isEmpty()) {
            return;
        }
        long wallTimeMillis = getWallTimeMillis();
        for (LastSipDelegateStat lastSipDelegateStat : new ArrayList(this.mLastSipDelegateStatList)) {
            if (lastSipDelegateStat.isDestroyed()) {
                lastSipDelegateStat.conclude(wallTimeMillis);
                this.mLastSipDelegateStatList.remove(lastSipDelegateStat);
            }
        }
    }

    private SipTransportFeatureTags getLastFeatureTags(int i) {
        if (this.mLastFeatureTagStatMap.containsKey(Integer.valueOf(i))) {
            return this.mLastFeatureTagStatMap.get(Integer.valueOf(i));
        }
        SipTransportFeatureTags sipTransportFeatureTags = new SipTransportFeatureTags(i);
        this.mLastFeatureTagStatMap.put(Integer.valueOf(i), sipTransportFeatureTags);
        return sipTransportFeatureTags;
    }

    @VisibleForTesting
    protected int getSlotId(int i) {
        return SubscriptionManager.getPhoneId(i);
    }

    @VisibleForTesting
    protected int getCarrierId(int i) {
        Phone phone = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(i));
        if (phone != null) {
            return phone.getCarrierId();
        }
        return -1;
    }

    @VisibleForTesting
    protected long getWallTimeMillis() {
        return System.currentTimeMillis();
    }

    @VisibleForTesting
    protected void logd(String str) {
        Rlog.d(TAG, str);
    }

    @VisibleForTesting
    protected int getSubId(int i) {
        return SubscriptionManager.getSubscriptionId(i);
    }

    @VisibleForTesting
    public int convertTagNameToValue(String str) {
        return FEATURE_TAGS.getOrDefault(str.trim().toLowerCase(Locale.ROOT), 1).intValue();
    }

    @VisibleForTesting
    public int convertServiceIdToValue(String str) {
        return SERVICE_IDS.getOrDefault(str.trim().toLowerCase(Locale.ROOT), 1).intValue();
    }

    @VisibleForTesting
    public int convertMessageTypeToValue(String str) {
        return MESSAGE_TYPE.getOrDefault(str.trim().toLowerCase(Locale.ROOT), 1).intValue();
    }

    @VisibleForTesting
    public int convertPresenceNotifyReason(String str) {
        return NOTIFY_REASONS.getOrDefault(str.trim().toLowerCase(Locale.ROOT), 1).intValue();
    }

    public synchronized void printAllMetrics(PrintWriter printWriter) {
        PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr;
        PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr;
        PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr;
        PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr;
        PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr;
        PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr;
        PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr;
        PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr;
        PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr;
        PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr;
        PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr;
        PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr;
        PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr;
        PersistAtomsStorage persistAtomsStorage = this.mAtomsStorage;
        if (persistAtomsStorage != null && persistAtomsStorage.mAtoms != null) {
            AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtomsStorage.mAtoms;
            androidUtilIndentingPrintWriter.println("RcsStats Metrics Proto: ");
            androidUtilIndentingPrintWriter.println("------------------------------------------");
            androidUtilIndentingPrintWriter.println("ImsRegistrationFeatureTagStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats : persistAtomsProto$PersistAtoms.imsRegistrationFeatureTagStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId + "] [" + persistAtomsProto$ImsRegistrationFeatureTagStats.slotId + "] Feature Tag Name = " + persistAtomsProto$ImsRegistrationFeatureTagStats.featureTagName + ", Registration Tech = " + persistAtomsProto$ImsRegistrationFeatureTagStats.registrationTech + ", Registered Duration (ms) = " + persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("RcsClientProvisioningStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats : persistAtomsProto$PersistAtoms.rcsClientProvisioningStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$RcsClientProvisioningStats.carrierId + "] [" + persistAtomsProto$RcsClientProvisioningStats.slotId + "] Event = " + persistAtomsProto$RcsClientProvisioningStats.event + ", Count = " + persistAtomsProto$RcsClientProvisioningStats.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("RcsAcsProvisioningStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats : persistAtomsProto$PersistAtoms.rcsAcsProvisioningStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$RcsAcsProvisioningStats.carrierId + "] [" + persistAtomsProto$RcsAcsProvisioningStats.slotId + "] Response Code = " + persistAtomsProto$RcsAcsProvisioningStats.responseCode + ", Response Type = " + persistAtomsProto$RcsAcsProvisioningStats.responseType + ", Single Registration Enabled = " + persistAtomsProto$RcsAcsProvisioningStats.isSingleRegistrationEnabled + ", Count = " + persistAtomsProto$RcsAcsProvisioningStats.count + ", State Timer (ms) = " + persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("SipDelegateStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats : persistAtomsProto$PersistAtoms.sipDelegateStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$SipDelegateStats.carrierId + "] [" + persistAtomsProto$SipDelegateStats.slotId + "] [" + persistAtomsProto$SipDelegateStats.dimension + "] Destroy Reason = " + persistAtomsProto$SipDelegateStats.destroyReason + ", Uptime (ms) = " + persistAtomsProto$SipDelegateStats.uptimeMillis);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("SipTransportFeatureTagStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats : persistAtomsProto$PersistAtoms.sipTransportFeatureTagStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$SipTransportFeatureTagStats.carrierId + "] [" + persistAtomsProto$SipTransportFeatureTagStats.slotId + "] Feature Tag Name = " + persistAtomsProto$SipTransportFeatureTagStats.featureTagName + ", Denied Reason = " + persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason + ", Deregistered Reason = " + persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason + ", Associated Time (ms) = " + persistAtomsProto$SipTransportFeatureTagStats.associatedMillis);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("SipMessageResponse:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse : persistAtomsProto$PersistAtoms.sipMessageResponse) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$SipMessageResponse.carrierId + "] [" + persistAtomsProto$SipMessageResponse.slotId + "] Message Method = " + persistAtomsProto$SipMessageResponse.sipMessageMethod + ", Response = " + persistAtomsProto$SipMessageResponse.sipMessageResponse + ", Direction = " + persistAtomsProto$SipMessageResponse.sipMessageDirection + ", Error = " + persistAtomsProto$SipMessageResponse.messageError + ", Count = " + persistAtomsProto$SipMessageResponse.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("SipTransportSession:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession : persistAtomsProto$PersistAtoms.sipTransportSession) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$SipTransportSession.carrierId + "] [" + persistAtomsProto$SipTransportSession.slotId + "] Session Method = " + persistAtomsProto$SipTransportSession.sessionMethod + ", Direction = " + persistAtomsProto$SipTransportSession.sipMessageDirection + ", Response = " + persistAtomsProto$SipTransportSession.sipResponse + ", Count = " + persistAtomsProto$SipTransportSession.sessionCount + ", GraceFully Count = " + persistAtomsProto$SipTransportSession.endedGracefullyCount);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("ImsDedicatedBearerListenerEvent:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent : persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEvent) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$ImsDedicatedBearerListenerEvent.carrierId + "] [" + persistAtomsProto$ImsDedicatedBearerListenerEvent.slotId + "] RAT = " + persistAtomsProto$ImsDedicatedBearerListenerEvent.ratAtEnd + ", QCI = " + persistAtomsProto$ImsDedicatedBearerListenerEvent.qci + ", Dedicated Bearer Established = " + persistAtomsProto$ImsDedicatedBearerListenerEvent.dedicatedBearerEstablished + ", Count = " + persistAtomsProto$ImsDedicatedBearerListenerEvent.eventCount);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("ImsDedicatedBearerEvent:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent : persistAtomsProto$PersistAtoms.imsDedicatedBearerEvent) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$ImsDedicatedBearerEvent.carrierId + "] [" + persistAtomsProto$ImsDedicatedBearerEvent.slotId + "] RAT = " + persistAtomsProto$ImsDedicatedBearerEvent.ratAtEnd + ", QCI = " + persistAtomsProto$ImsDedicatedBearerEvent.qci + ", Bearer State = " + persistAtomsProto$ImsDedicatedBearerEvent.bearerState + ", Local Connection Info = " + persistAtomsProto$ImsDedicatedBearerEvent.localConnectionInfoReceived + ", Remote Connection Info = " + persistAtomsProto$ImsDedicatedBearerEvent.remoteConnectionInfoReceived + ", Listener Existence = " + persistAtomsProto$ImsDedicatedBearerEvent.hasListeners + ", Count = " + persistAtomsProto$ImsDedicatedBearerEvent.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("ImsRegistrationServiceDescStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$ImsRegistrationServiceDescStats.carrierId + "] [" + persistAtomsProto$ImsRegistrationServiceDescStats.slotId + "] Name = " + persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdName + ", Version = " + persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdVersion + ", Registration Tech = " + persistAtomsProto$ImsRegistrationServiceDescStats.registrationTech + ", Published Time (ms) = " + persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("UceEventStats:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats : persistAtomsProto$PersistAtoms.uceEventStats) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$UceEventStats.carrierId + "] [" + persistAtomsProto$UceEventStats.slotId + "] Type = " + persistAtomsProto$UceEventStats.type + ", Successful = " + persistAtomsProto$UceEventStats.successful + ", Code = " + persistAtomsProto$UceEventStats.commandCode + ", Response = " + persistAtomsProto$UceEventStats.networkResponse + ", Count = " + persistAtomsProto$UceEventStats.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("PresenceNotifyEvent:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent : persistAtomsProto$PersistAtoms.presenceNotifyEvent) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$PresenceNotifyEvent.carrierId + "] [" + persistAtomsProto$PresenceNotifyEvent.slotId + "] Reason = " + persistAtomsProto$PresenceNotifyEvent.reason + ", Body = " + persistAtomsProto$PresenceNotifyEvent.contentBodyReceived + ", RCS Count = " + persistAtomsProto$PresenceNotifyEvent.rcsCapsCount + ", MMTEL Count = " + persistAtomsProto$PresenceNotifyEvent.mmtelCapsCount + ", NoCaps Count = " + persistAtomsProto$PresenceNotifyEvent.noCapsCount + ", Count = " + persistAtomsProto$PresenceNotifyEvent.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println("GbaEvent:");
            androidUtilIndentingPrintWriter.increaseIndent();
            for (PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent : persistAtomsProto$PersistAtoms.gbaEvent) {
                androidUtilIndentingPrintWriter.println("[" + persistAtomsProto$GbaEvent.carrierId + "] [" + persistAtomsProto$GbaEvent.slotId + "] Successful = " + persistAtomsProto$GbaEvent.successful + ", Fail Reason = " + persistAtomsProto$GbaEvent.failedReason + ", Count = " + persistAtomsProto$GbaEvent.count);
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
        }
    }
}
