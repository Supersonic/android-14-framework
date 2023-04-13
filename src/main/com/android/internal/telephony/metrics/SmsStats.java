package com.android.internal.telephony.metrics;

import android.telephony.ServiceState;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.nano.PersistAtomsProto$IncomingSms;
import com.android.internal.telephony.nano.PersistAtomsProto$OutgoingShortCodeSms;
import com.android.internal.telephony.nano.PersistAtomsProto$OutgoingSms;
import java.util.Objects;
import java.util.Random;
/* loaded from: classes.dex */
public class SmsStats {
    private static final Random RANDOM = new Random();
    private final PersistAtomsStorage mAtomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
    private final Phone mPhone;

    private static int getIncomingSmsError(int i) {
        if (i == -1 || i == 1) {
            return 0;
        }
        if (i != 3) {
            return i != 4 ? 1 : 3;
        }
        return 2;
    }

    private static int getIncomingSmsError(boolean z) {
        return z ? 0 : 1;
    }

    private static int getSmsFormat(boolean z) {
        return z ? 2 : 1;
    }

    private int getSmsTech(boolean z, boolean z2) {
        if (z) {
            return 3;
        }
        return z2 ? 2 : 1;
    }

    public SmsStats(Phone phone) {
        this.mPhone = phone;
    }

    public void onDroppedIncomingMultipartSms(boolean z, int i, int i2) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(z, 0);
        incomingDefaultProto.smsTech = 0;
        incomingDefaultProto.rat = 0;
        incomingDefaultProto.error = 1;
        incomingDefaultProto.totalParts = i2;
        incomingDefaultProto.receivedParts = i;
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsVoicemail(boolean z, int i) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(z, i);
        incomingDefaultProto.smsType = 2;
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsTypeZero(int i) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(false, i);
        incomingDefaultProto.smsType = 3;
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsPP(int i, boolean z) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(false, i);
        incomingDefaultProto.smsType = 1;
        incomingDefaultProto.error = getIncomingSmsError(z);
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsSuccess(boolean z, int i, int i2, boolean z2, long j) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(z, i);
        incomingDefaultProto.totalParts = i2;
        incomingDefaultProto.receivedParts = i2;
        incomingDefaultProto.blocked = z2;
        incomingDefaultProto.messageId = j;
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsError(boolean z, int i, int i2) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(z, i);
        incomingDefaultProto.error = getIncomingSmsError(i2);
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onIncomingSmsWapPush(int i, int i2, int i3, long j) {
        PersistAtomsProto$IncomingSms incomingDefaultProto = getIncomingDefaultProto(false, i);
        incomingDefaultProto.smsType = 4;
        incomingDefaultProto.totalParts = i2;
        incomingDefaultProto.receivedParts = i2;
        incomingDefaultProto.error = getIncomingSmsError(i3);
        incomingDefaultProto.messageId = j;
        this.mAtomsStorage.addIncomingSms(incomingDefaultProto);
    }

    public void onOutgoingSms(boolean z, boolean z2, boolean z3, int i, long j, boolean z4, long j2) {
        onOutgoingSms(z, z2, z3, i, -1, j, z4, j2);
    }

    public void onOutgoingSms(boolean z, boolean z2, boolean z3, int i, int i2, long j, boolean z4, long j2) {
        PersistAtomsProto$OutgoingSms outgoingDefaultProto = getOutgoingDefaultProto(z2, z, j, z4, j2);
        if (z) {
            outgoingDefaultProto.errorCode = i;
            if (z3) {
                outgoingDefaultProto.sendResult = 4;
            } else if (i == 101) {
                outgoingDefaultProto.sendResult = 3;
            } else if (i != 0) {
                outgoingDefaultProto.sendResult = 2;
            }
        } else {
            if (i == 101) {
                outgoingDefaultProto.sendResult = 3;
            } else if (i != 0) {
                outgoingDefaultProto.sendResult = 2;
            }
            outgoingDefaultProto.errorCode = i2;
            if (i == 100 && i2 == -1) {
                outgoingDefaultProto.errorCode = z2 ? 66 : 331;
            }
        }
        outgoingDefaultProto.sendErrorCode = i;
        outgoingDefaultProto.networkErrorCode = i2;
        this.mAtomsStorage.addOutgoingSms(outgoingDefaultProto);
    }

    public void onOutgoingShortCodeSms(int i, int i2) {
        PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms = new PersistAtomsProto$OutgoingShortCodeSms();
        persistAtomsProto$OutgoingShortCodeSms.category = i;
        persistAtomsProto$OutgoingShortCodeSms.xmlVersion = i2;
        persistAtomsProto$OutgoingShortCodeSms.shortCodeSmsCount = 1;
        this.mAtomsStorage.addOutgoingShortCodeSms(persistAtomsProto$OutgoingShortCodeSms);
    }

    private PersistAtomsProto$IncomingSms getIncomingDefaultProto(boolean z, int i) {
        PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms = new PersistAtomsProto$IncomingSms();
        persistAtomsProto$IncomingSms.smsFormat = getSmsFormat(z);
        persistAtomsProto$IncomingSms.smsTech = getSmsTech(i, z);
        persistAtomsProto$IncomingSms.rat = getRat(i);
        persistAtomsProto$IncomingSms.smsType = 0;
        persistAtomsProto$IncomingSms.totalParts = 1;
        persistAtomsProto$IncomingSms.receivedParts = 1;
        persistAtomsProto$IncomingSms.blocked = false;
        persistAtomsProto$IncomingSms.error = 0;
        persistAtomsProto$IncomingSms.isRoaming = getIsRoaming();
        persistAtomsProto$IncomingSms.simSlotIndex = getPhoneId();
        persistAtomsProto$IncomingSms.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$IncomingSms.isEsim = SimSlotState.isEsim(getPhoneId());
        persistAtomsProto$IncomingSms.carrierId = getCarrierId();
        persistAtomsProto$IncomingSms.messageId = RANDOM.nextLong();
        persistAtomsProto$IncomingSms.count = 1;
        persistAtomsProto$IncomingSms.isManagedProfile = this.mPhone.isManagedProfile();
        return persistAtomsProto$IncomingSms;
    }

    private PersistAtomsProto$OutgoingSms getOutgoingDefaultProto(boolean z, boolean z2, long j, boolean z3, long j2) {
        PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms = new PersistAtomsProto$OutgoingSms();
        persistAtomsProto$OutgoingSms.smsFormat = getSmsFormat(z);
        persistAtomsProto$OutgoingSms.smsTech = getSmsTech(z2, z);
        persistAtomsProto$OutgoingSms.rat = getRat(z2);
        persistAtomsProto$OutgoingSms.sendResult = 1;
        persistAtomsProto$OutgoingSms.errorCode = z2 ? 0 : -1;
        persistAtomsProto$OutgoingSms.isRoaming = getIsRoaming();
        persistAtomsProto$OutgoingSms.isFromDefaultApp = z3;
        persistAtomsProto$OutgoingSms.simSlotIndex = getPhoneId();
        persistAtomsProto$OutgoingSms.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$OutgoingSms.isEsim = SimSlotState.isEsim(getPhoneId());
        persistAtomsProto$OutgoingSms.carrierId = getCarrierId();
        if (j == 0) {
            j = RANDOM.nextLong();
        }
        persistAtomsProto$OutgoingSms.messageId = j;
        persistAtomsProto$OutgoingSms.retryId = 0;
        persistAtomsProto$OutgoingSms.intervalMillis = j2;
        persistAtomsProto$OutgoingSms.count = 1;
        persistAtomsProto$OutgoingSms.isManagedProfile = this.mPhone.isManagedProfile();
        return persistAtomsProto$OutgoingSms;
    }

    private int getSmsTech(int i, boolean z) {
        if (i == 2) {
            return 0;
        }
        return getSmsTech(i == 1, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSmsHashCode(PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms) {
        return Objects.hash(Integer.valueOf(persistAtomsProto$OutgoingSms.smsFormat), Integer.valueOf(persistAtomsProto$OutgoingSms.smsTech), Integer.valueOf(persistAtomsProto$OutgoingSms.rat), Integer.valueOf(persistAtomsProto$OutgoingSms.sendResult), Integer.valueOf(persistAtomsProto$OutgoingSms.errorCode), Boolean.valueOf(persistAtomsProto$OutgoingSms.isRoaming), Boolean.valueOf(persistAtomsProto$OutgoingSms.isFromDefaultApp), Integer.valueOf(persistAtomsProto$OutgoingSms.simSlotIndex), Boolean.valueOf(persistAtomsProto$OutgoingSms.isMultiSim), Boolean.valueOf(persistAtomsProto$OutgoingSms.isEsim), Integer.valueOf(persistAtomsProto$OutgoingSms.carrierId));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSmsHashCode(PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms) {
        return Objects.hash(Integer.valueOf(persistAtomsProto$IncomingSms.smsFormat), Integer.valueOf(persistAtomsProto$IncomingSms.smsTech), Integer.valueOf(persistAtomsProto$IncomingSms.rat), Integer.valueOf(persistAtomsProto$IncomingSms.smsType), Integer.valueOf(persistAtomsProto$IncomingSms.totalParts), Integer.valueOf(persistAtomsProto$IncomingSms.receivedParts), Boolean.valueOf(persistAtomsProto$IncomingSms.blocked), Integer.valueOf(persistAtomsProto$IncomingSms.error), Boolean.valueOf(persistAtomsProto$IncomingSms.isRoaming), Integer.valueOf(persistAtomsProto$IncomingSms.simSlotIndex), Boolean.valueOf(persistAtomsProto$IncomingSms.isMultiSim), Boolean.valueOf(persistAtomsProto$IncomingSms.isEsim), Integer.valueOf(persistAtomsProto$IncomingSms.carrierId));
    }

    private int getPhoneId() {
        Phone phone = this.mPhone;
        if (phone.getPhoneType() == 5) {
            phone = this.mPhone.getDefaultPhone();
        }
        return phone.getPhoneId();
    }

    private ServiceState getServiceState() {
        Phone phone = this.mPhone;
        if (phone.getPhoneType() == 5) {
            phone = this.mPhone.getDefaultPhone();
        }
        ServiceStateTracker serviceStateTracker = phone.getServiceStateTracker();
        if (serviceStateTracker != null) {
            return serviceStateTracker.getServiceState();
        }
        return null;
    }

    private int getRat(int i) {
        if (i == 2) {
            return 0;
        }
        return getRat(i == 1);
    }

    private int getRat(boolean z) {
        if (z && this.mPhone.getImsRegistrationTech() == 1) {
            return 18;
        }
        ServiceState serviceState = getServiceState();
        if (serviceState != null) {
            return serviceState.getVoiceNetworkType();
        }
        return 0;
    }

    private boolean getIsRoaming() {
        ServiceState serviceState = getServiceState();
        if (serviceState != null) {
            return serviceState.getRoaming();
        }
        return false;
    }

    private int getCarrierId() {
        Phone phone = this.mPhone;
        if (phone.getPhoneType() == 5) {
            phone = this.mPhone.getDefaultPhone();
        }
        return phone.getCarrierId();
    }
}
