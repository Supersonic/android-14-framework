package com.android.service.ims.presence;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.telephony.ims.RcsContactUceCapability;
import android.text.TextUtils;
import com.android.ims.internal.ContactNumberUtils;
import com.android.ims.internal.Logger;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.service.ims.RcsSettingUtils;
import com.android.service.ims.Task;
import com.android.service.ims.TaskManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PresenceSubscriber extends PresenceBase {
    private Logger logger;
    private int mAssociatedSubscription;
    private String mAvailabilityRetryNumber;
    private final String[] mConfigRcsProvisionErrorOnSubscribeResponse;
    private final String[] mConfigVolteProvisionErrorOnSubscribeResponse;
    private SubscribePublisher mSubscriber;
    private final Object mSubscriberLock;

    public PresenceSubscriber(SubscribePublisher subscriber, Context context, String[] configVolteProvisionErrorOnSubscribeResponse, String[] configRcsProvisionErrorOnSubscribeResponse) {
        super(context);
        this.logger = Logger.getLogger(getClass().getName());
        Object obj = new Object();
        this.mSubscriberLock = obj;
        this.mAvailabilityRetryNumber = null;
        this.mAssociatedSubscription = -1;
        synchronized (obj) {
            this.mSubscriber = subscriber;
        }
        this.mConfigVolteProvisionErrorOnSubscribeResponse = configVolteProvisionErrorOnSubscribeResponse;
        this.mConfigRcsProvisionErrorOnSubscribeResponse = configRcsProvisionErrorOnSubscribeResponse;
    }

    public void updatePresenceSubscriber(SubscribePublisher subscriber) {
        synchronized (this.mSubscriberLock) {
            this.logger.print("Update PresencePublisher");
            this.mSubscriber = subscriber;
        }
    }

    public void removePresenceSubscriber() {
        synchronized (this.mSubscriberLock) {
            this.logger.print("Remove PresenceSubscriber");
            this.mSubscriber = null;
        }
    }

    public void handleAssociatedSubscriptionChanged(int newSubId) {
        if (this.mAssociatedSubscription == newSubId) {
            return;
        }
        this.mAssociatedSubscription = newSubId;
    }

    private String numberToUriString(String number) {
        String formattedContact = number;
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (tm != null && !formattedContact.startsWith("sip:") && !formattedContact.startsWith("tel:")) {
            String domain = tm.getIsimDomain();
            this.logger.debug("domain=" + domain);
            formattedContact = (domain == null || domain.length() == 0) ? "tel:" + formattedContact : "sip:" + formattedContact + "@" + domain;
        }
        this.logger.print("numberToUriString formattedContact=" + formattedContact);
        return formattedContact;
    }

    private String numberToTelString(String number) {
        String formatedContact = number;
        if (!formatedContact.startsWith("sip:") && !formatedContact.startsWith("tel:")) {
            formatedContact = "tel:" + formatedContact;
        }
        this.logger.print("numberToTelString formatedContact=" + formatedContact);
        return formatedContact;
    }

    public int requestCapability(List<String> contactsNumber, ContactCapabilityResponse listener) {
        SubscribePublisher subscriber;
        synchronized (this.mSubscriberLock) {
            subscriber = this.mSubscriber;
        }
        if (subscriber == null) {
            this.logger.error("requestCapability Subscribe not registered");
            return -5;
        } else if (!RcsSettingUtils.hasUserEnabledContactDiscovery(this.mContext, this.mAssociatedSubscription)) {
            this.logger.warn("requestCapability request has been denied due to contact discovery being disabled by the user");
            return -1;
        } else {
            int ret = subscriber.getStackStatusForCapabilityRequest();
            if (ret < 0) {
                this.logger.error("requestCapability ret=" + ret);
                return ret;
            } else if (contactsNumber == null || contactsNumber.size() == 0) {
                return -11;
            } else {
                this.logger.debug("check contact size ...");
                if (contactsNumber.size() > RcsSettingUtils.getMaxNumbersInRCL(this.mAssociatedSubscription)) {
                    this.logger.error("requestCapability contctNumber size=" + contactsNumber.size());
                    return -9;
                }
                String[] formatedNumbers = ContactNumberUtils.getDefault().format(contactsNumber);
                int formatResult = ContactNumberUtils.getDefault().validate(formatedNumbers);
                if (formatResult != ContactNumberUtils.NUMBER_VALID) {
                    this.logger.error("requestCapability formatResult=" + formatResult);
                    return -11;
                }
                String[] formatedContacts = new String[formatedNumbers.length];
                for (int i = 0; i < formatedContacts.length; i++) {
                    formatedContacts[i] = numberToTelString(formatedNumbers[i]);
                }
                int i2 = this.mAssociatedSubscription;
                long timeout = RcsSettingUtils.getCapabPollListSubExp(i2) * 1000;
                this.logger.print("add to task manager, formatedNumbers=" + PresenceUtils.toContactString(formatedNumbers));
                int taskId = TaskManager.getDefault().addCapabilityTask(this.mContext, formatedNumbers, listener, timeout + RcsSettingUtils.getSIPT1Timer(this.mAssociatedSubscription) + 3000);
                this.logger.print("taskId=" + taskId);
                int ret2 = subscriber.requestCapability(formatedContacts, taskId);
                if (ret2 < 0) {
                    this.logger.error("requestCapability ret=" + ret2 + " remove taskId=" + taskId);
                    TaskManager.getDefault().removeTask(taskId);
                }
                return taskId;
            }
        }
    }

    public int requestAvailability(String contactNumber, ContactCapabilityResponse listener, boolean forceToNetwork) {
        SubscribePublisher subscriber;
        String formatedContact = ContactNumberUtils.getDefault().format(contactNumber);
        int ret = ContactNumberUtils.getDefault().validate(formatedContact);
        if (ret != ContactNumberUtils.NUMBER_VALID) {
            return ret;
        }
        if (!RcsSettingUtils.hasUserEnabledContactDiscovery(this.mContext, this.mAssociatedSubscription)) {
            this.logger.warn("requestCapability request has been denied due to contact discovery being disabled by the user");
            return -1;
        }
        if (!forceToNetwork) {
            this.logger.debug("check if we can use the value in cache");
            int availabilityExpire = RcsSettingUtils.getAvailabilityCacheExpiration(this.mAssociatedSubscription);
            int availabilityExpire2 = availabilityExpire > 0 ? availabilityExpire * 1000 : 60000;
            this.logger.print("requestAvailability availabilityExpire=" + availabilityExpire2);
            TaskManager.getDefault().clearTimeoutAvailabilityTask(availabilityExpire2);
            Task task = TaskManager.getDefault().getAvailabilityTaskByContact(formatedContact);
            if (task != null && (task instanceof PresenceAvailabilityTask)) {
                PresenceAvailabilityTask availabilityTask = (PresenceAvailabilityTask) task;
                if (availabilityTask.getNotifyTimestamp() == 0) {
                    this.logger.print("requestAvailability: the request is pending in queue");
                    return -19;
                }
                this.logger.print("requestAvailability: the prevous valuedoesn't be expired yet");
                return -20;
            }
        }
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (tm == null || tm.getDataNetworkType() != 13) {
            this.logger.error("requestAvailability return ERROR_SERVICE_NOT_AVAILABLE for it is not LTE network");
            return -3;
        }
        synchronized (this.mSubscriberLock) {
            subscriber = this.mSubscriber;
        }
        if (subscriber == null) {
            this.logger.error("requestAvailability Subscribe not registered");
            return -5;
        }
        int ret2 = subscriber.getStackStatusForCapabilityRequest();
        if (ret2 < 0) {
            this.logger.error("requestAvailability=" + ret2);
            return ret2;
        }
        int taskId = TaskManager.getDefault().addAvailabilityTask(formatedContact, listener);
        String formatedContact2 = numberToUriString(formatedContact);
        this.logger.print("addAvailabilityTask formatedContact=" + formatedContact2);
        int ret3 = subscriber.requestAvailability(formatedContact2, taskId);
        if (ret3 < 0) {
            this.logger.error("requestAvailability ret=" + ret3 + " remove taskId=" + taskId);
            TaskManager.getDefault().removeTask(taskId);
        }
        return taskId;
    }

    private int translateResponse403(String reasonPhrase) {
        if (reasonPhrase == null) {
            return -10;
        }
        String reasonPhrase2 = reasonPhrase.toLowerCase();
        if (reasonPhrase2.contains("user not registered")) {
            return -5;
        }
        if (reasonPhrase2.contains(NetworkSipCode.SIP_NOT_AUTHORIZED_FOR_PRESENCE)) {
            return -6;
        }
        return -7;
    }

    private int translateResponseCode(int responseCode, String reasonPhrase) {
        int ret;
        this.logger.debug("translateResponseCode getSipResponseCode=" + responseCode);
        if (responseCode < 100 || responseCode > 699) {
            this.logger.debug("internal error code sipCode=" + responseCode);
            return -4;
        }
        switch (responseCode) {
            case NetworkSipCode.SIP_CODE_OK /* 200 */:
                ret = 0;
                break;
            case NetworkSipCode.SIP_CODE_FORBIDDEN /* 403 */:
                ret = translateResponse403(reasonPhrase);
                break;
            case NetworkSipCode.SIP_CODE_NOT_FOUND /* 404 */:
                ret = -8;
                break;
            case NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT /* 408 */:
                ret = -4;
                break;
            case NetworkSipCode.SIP_CODE_REQUEST_ENTITY_TOO_LARGE /* 413 */:
                ret = -9;
                break;
            case NetworkSipCode.SIP_CODE_INTERVAL_TOO_BRIEF /* 423 */:
                ret = -4;
                break;
            case NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR /* 500 */:
                ret = -4;
                break;
            case NetworkSipCode.SIP_CODE_SERVICE_UNAVAILABLE /* 503 */:
                ret = -4;
                break;
            case NetworkSipCode.SIP_CODE_DECLINE /* 603 */:
                ret = -4;
                break;
            default:
                ret = -10;
                break;
        }
        this.logger.debug("translateResponseCode ret=" + ret);
        return ret;
    }

    public void onSipResponse(int requestId, int responseCode, String reasonPhrase) {
        SubscribePublisher subscriber;
        synchronized (this.mSubscriberLock) {
            subscriber = this.mSubscriber;
        }
        if (isInConfigList(responseCode, reasonPhrase, this.mConfigVolteProvisionErrorOnSubscribeResponse)) {
            this.logger.print("volte provision sipCode=" + responseCode + " phrase=" + reasonPhrase);
            if (subscriber != null) {
                subscriber.updatePublisherState(2);
            }
            notifyDm();
        } else if (isInConfigList(responseCode, reasonPhrase, this.mConfigRcsProvisionErrorOnSubscribeResponse)) {
            this.logger.print("rcs proRcsPresence.vision sipCode=" + responseCode + " phrase=" + reasonPhrase);
            if (subscriber != null) {
                subscriber.updatePublisherState(3);
            }
        }
        int errorCode = translateResponseCode(responseCode, reasonPhrase);
        this.logger.print("handleSipResponse errorCode=" + errorCode);
        if (errorCode == -5) {
            this.logger.debug("setPublishState to unknown for subscribe error 403 not registered");
            if (subscriber != null) {
                subscriber.updatePublisherState(5);
            }
        }
        if (errorCode == -6) {
            this.logger.debug("ResultCode.SUBSCRIBE_NOT_AUTHORIZED_FOR_PRESENCE");
        }
        if (errorCode == -7) {
            this.logger.debug("ResultCode.SUBSCRIBE_FORBIDDEN");
        }
        Task task = TaskManager.getDefault().getTaskByRequestId(requestId);
        this.logger.debug("handleSipResponse task=" + task);
        if (task != null) {
            task.mSipResponseCode = responseCode;
            task.mSipReasonPhrase = reasonPhrase;
            TaskManager.getDefault().putTask(task.mTaskId, task);
        }
        if (errorCode == -5 && task != null && task.mCmdId == 2) {
            String[] contacts = ((PresenceTask) task).mContacts;
            if (contacts != null && contacts.length > 0) {
                this.mAvailabilityRetryNumber = contacts[0];
            }
            this.logger.debug("retry to get availability for " + this.mAvailabilityRetryNumber);
        }
        if (errorCode == -8 && task != null && ((PresenceTask) task).mContacts != null) {
            String[] contacts2 = ((PresenceTask) task).mContacts;
            ArrayList<RcsContactUceCapability> contactCapabilities = new ArrayList<>();
            for (int i = 0; i < contacts2.length; i++) {
                if (!TextUtils.isEmpty(contacts2[i])) {
                    this.logger.debug("onSipResponse: contact= " + contacts2[i] + ", not found.");
                    contactCapabilities.add(buildContactWithNoCapabilities(PresenceUtils.convertContactNumber(contacts2[i])));
                }
            }
            handleCapabilityUpdate(task, contactCapabilities, true);
        } else if (errorCode == -10) {
            updateAvailabilityToUnknown(task);
        }
        handleCallback(task, errorCode, false);
    }

    private RcsContactUceCapability buildContactWithNoCapabilities(Uri contactUri) {
        RcsContactUceCapability.PresenceBuilder presenceBuilder = new RcsContactUceCapability.PresenceBuilder(contactUri, 1, 3);
        return presenceBuilder.build();
    }

    private void handleCapabilityUpdate(Task task, List<RcsContactUceCapability> capabilities, boolean updateLastTimestamp) {
        if (task == null || task.mListener == null) {
            this.logger.warn("handleCapabilityUpdate, invalid listener!");
        } else {
            task.mListener.onCapabilitiesUpdated(task.mTaskId, capabilities, updateLastTimestamp);
        }
    }

    public void retryToGetAvailability() {
        String str = this.mAvailabilityRetryNumber;
        if (str == null) {
            return;
        }
        requestAvailability(str, null, true);
        this.mAvailabilityRetryNumber = null;
    }

    public void updatePresence(RcsContactUceCapability capabilities) {
        if (this.mContext == null) {
            this.logger.error("updatePresence mContext == null");
            return;
        }
        ArrayList<RcsContactUceCapability> presenceInfos = new ArrayList<>();
        presenceInfos.add(capabilities);
        String contactNumber = capabilities.getContactUri().getSchemeSpecificPart();
        TaskManager.getDefault().onTerminated(contactNumber);
        PresenceAvailabilityTask availabilityTask = TaskManager.getDefault().getAvailabilityTaskByContact(contactNumber);
        if (availabilityTask != null) {
            availabilityTask.updateNotifyTimestamp();
        }
        Task task = TaskManager.getDefault().getTaskForSingleContactQuery(contactNumber);
        handleCapabilityUpdate(task, presenceInfos, true);
    }

    public void updatePresences(int requestId, List<RcsContactUceCapability> contactsCapabilities, boolean isTerminated, String terminatedReason) {
        if (this.mContext == null) {
            this.logger.error("updatePresences: mContext == null");
            return;
        }
        if (isTerminated) {
            TaskManager.getDefault().onTerminated(requestId, terminatedReason);
        }
        Task task = TaskManager.getDefault().getTaskByRequestId(requestId);
        if (contactsCapabilities.size() > 0 || task != null) {
            handleCapabilityUpdate(task, contactsCapabilities, true);
        }
    }

    @Override // com.android.service.ims.presence.PresenceBase
    public void onCommandStatusUpdated(int taskId, int requestId, int resultCode) {
        Task taskTmp = TaskManager.getDefault().getTask(taskId);
        this.logger.print("handleCmdStatus resultCode=" + resultCode);
        PresenceTask task = null;
        if (taskTmp != null && (taskTmp instanceof PresenceTask)) {
            task = (PresenceTask) taskTmp;
            task.mSipRequestId = requestId;
            task.mCmdStatus = resultCode;
            TaskManager.getDefault().putTask(task.mTaskId, task);
            if (resultCode != 0 && task.mContacts != null) {
                updateAvailabilityToUnknown(task);
            }
        }
        handleCallback(task, resultCode, true);
    }

    private void updateAvailabilityToUnknown(Task inTask) {
        if (this.mContext == null) {
            this.logger.error("updateAvailabilityToUnknown mContext=null");
        } else if (inTask == null) {
            this.logger.error("updateAvailabilityToUnknown task=null");
        } else if (!(inTask instanceof PresenceTask)) {
            this.logger.error("updateAvailabilityToUnknown not PresencTask");
        } else {
            PresenceTask task = (PresenceTask) inTask;
            if (task.mContacts == null || task.mContacts.length == 0) {
                this.logger.error("updateAvailabilityToUnknown no contacts");
                return;
            }
            ArrayList<RcsContactUceCapability> presenceInfoList = new ArrayList<>();
            for (int i = 0; i < task.mContacts.length; i++) {
                if (!TextUtils.isEmpty(task.mContacts[i])) {
                    Uri uri = PresenceUtils.convertContactNumber(task.mContacts[i]);
                    RcsContactUceCapability.PresenceBuilder presenceBuilder = new RcsContactUceCapability.PresenceBuilder(uri, 1, 3);
                    presenceInfoList.add(presenceBuilder.build());
                }
            }
            int i2 = presenceInfoList.size();
            if (i2 > 0) {
                handleCapabilityUpdate(task, presenceInfoList, false);
            }
        }
    }
}
