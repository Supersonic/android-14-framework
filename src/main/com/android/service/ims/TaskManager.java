package com.android.service.ims;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import com.android.ims.internal.Logger;
import com.android.service.ims.presence.ContactCapabilityResponse;
import com.android.service.ims.presence.PresenceAvailabilityTask;
import com.android.service.ims.presence.PresenceCapabilityTask;
import com.android.service.ims.presence.PresenceTask;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class TaskManager {
    private static final int TASK_MANAGER_ON_TERMINATED = 1;
    private static final int TASK_MANAGER_ON_TIMEOUT = 2;
    public static final int TASK_TYPE_GET_AVAILABILITY = 2;
    public static final int TASK_TYPE_GET_CAPABILITY = 1;
    public static final int TASK_TYPE_PUBLISH = 3;
    private static MessageHandler sMsgHandler;
    private static TaskManager sTaskManager = null;
    private Map<String, Task> mTaskMap;
    private Logger logger = Logger.getLogger(getClass().getName());
    private int mTaskId = 0;
    private final Object mSyncObj = new Object();

    public TaskManager() {
        this.logger.debug("TaskManager created.");
        this.mTaskMap = new HashMap();
        HandlerThread messageHandlerThread = new HandlerThread("MessageHandler", 10);
        messageHandlerThread.start();
        Looper messageHandlerLooper = messageHandlerThread.getLooper();
        sMsgHandler = new MessageHandler(messageHandlerLooper);
    }

    public static synchronized TaskManager getDefault() {
        TaskManager taskManager;
        synchronized (TaskManager.class) {
            if (sTaskManager == null) {
                sTaskManager = new TaskManager();
            }
            taskManager = sTaskManager;
        }
        return taskManager;
    }

    public synchronized int generateTaskId() {
        int i;
        i = this.mTaskId;
        this.mTaskId = i + 1;
        return i;
    }

    public void putTask(int taskId, Task task) {
        synchronized (this.mSyncObj) {
            putTaskInternal(taskId, task);
        }
    }

    private synchronized void putTaskInternal(int taskId, Task task) {
        Task sameKeyTask = this.mTaskMap.put(String.valueOf(taskId), task);
        this.logger.debug("Added Task: " + task + "Original same key task:" + sameKeyTask);
    }

    public int addCapabilityTask(Context context, String[] contacts, ContactCapabilityResponse listener, long timeout) {
        int taskId = getDefault().generateTaskId();
        synchronized (this.mSyncObj) {
            Task task = new PresenceCapabilityTask(context, taskId, 1, listener, contacts, timeout);
            putTaskInternal(taskId, task);
        }
        return taskId;
    }

    public int addAvailabilityTask(String contact, ContactCapabilityResponse listener) {
        int taskId = getDefault().generateTaskId();
        synchronized (this.mSyncObj) {
            String[] contacts = {contact};
            Task task = new PresenceAvailabilityTask(taskId, 2, listener, contacts);
            putTaskInternal(taskId, task);
        }
        return taskId;
    }

    public int addPublishTask(String contact) {
        int taskId = getDefault().generateTaskId();
        synchronized (this.mSyncObj) {
            String[] contacts = {contact};
            Task task = new PresenceTask(taskId, 3, null, contacts);
            putTaskInternal(taskId, task);
        }
        return taskId;
    }

    public Task getTask(int taskId) {
        Task task;
        synchronized (this.mSyncObj) {
            task = this.mTaskMap.get(String.valueOf(taskId));
        }
        return task;
    }

    public void removeTask(int taskId) {
        synchronized (this.mSyncObj) {
            Task task = this.mTaskMap.remove(String.valueOf(taskId));
            if (task instanceof PresenceCapabilityTask) {
                ((PresenceCapabilityTask) task).cancelTimer();
            }
            this.logger.debug("Removed Task: " + task);
        }
    }

    public Task getTaskForSingleContactQuery(String contact) {
        synchronized (this.mSyncObj) {
            Set<String> keys = this.mTaskMap.keySet();
            if (keys == null) {
                this.logger.debug("getTaskByContact keys=null");
                return null;
            }
            for (String key : keys) {
                Task task = this.mTaskMap.get(key);
                if (task != null) {
                    if (task instanceof PresenceTask) {
                        PresenceTask presenceTask = (PresenceTask) task;
                        if (presenceTask.mContacts.length == 1 && PhoneNumberUtils.compare(contact, presenceTask.mContacts[0])) {
                            return task;
                        }
                    }
                }
            }
            return null;
        }
    }

    public Task getTaskByRequestId(int sipRequestId) {
        synchronized (this.mSyncObj) {
            Set<String> keys = this.mTaskMap.keySet();
            if (keys == null) {
                this.logger.debug("getTaskByRequestId keys=null");
                return null;
            }
            for (String key : keys) {
                if (this.mTaskMap.get(key).mSipRequestId == sipRequestId) {
                    this.logger.debug("getTaskByRequestId, sipRequestId=" + sipRequestId + " task=" + this.mTaskMap.get(key));
                    return this.mTaskMap.get(key);
                }
            }
            this.logger.debug("getTaskByRequestId, sipRequestId=" + sipRequestId + " task=null");
            return null;
        }
    }

    public void onTerminated(String contact) {
        if (contact == null) {
            return;
        }
        synchronized (this.mSyncObj) {
            Set<String> keys = this.mTaskMap.keySet();
            if (keys == null) {
                this.logger.debug("onTerminated keys is null");
                return;
            }
            for (String key : keys) {
                Task task = this.mTaskMap.get(key);
                if (task != null) {
                    if (task instanceof PresenceCapabilityTask) {
                        PresenceCapabilityTask capabilityTask = (PresenceCapabilityTask) task;
                        if (capabilityTask.mContacts != null && capabilityTask.mContacts[0] != null && PhoneNumberUtils.compare(contact, capabilityTask.mContacts[0])) {
                            if (!capabilityTask.isWaitingForNotify()) {
                                this.logger.debug("onTerminated the tesk is not waiting for NOTIFY yet");
                            } else {
                                MessageData messageData = new MessageData();
                                messageData.mTask = capabilityTask;
                                messageData.mReason = null;
                                Message notifyMessage = sMsgHandler.obtainMessage(1, messageData);
                                sMsgHandler.sendMessage(notifyMessage);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onTerminated(int requestId, String reason) {
        this.logger.debug("onTerminated requestId=" + requestId + " reason=" + reason);
        Task task = getTaskByRequestId(requestId);
        if (task == null) {
            this.logger.debug("onTerminated Can't find request " + requestId);
            return;
        }
        synchronized (this.mSyncObj) {
            if (task instanceof PresenceCapabilityTask) {
                MessageData messageData = new MessageData();
                messageData.mTask = (PresenceCapabilityTask) task;
                messageData.mReason = reason;
                Message notifyMessage = sMsgHandler.obtainMessage(1, messageData);
                sMsgHandler.sendMessage(notifyMessage);
            }
        }
    }

    public void onTimeout(int taskId) {
        this.logger.debug("onTimeout taskId=" + taskId);
        Task task = getTask(taskId);
        if (task == null) {
            this.logger.debug("onTimeout task = null");
            return;
        }
        synchronized (this.mSyncObj) {
            if (task instanceof PresenceCapabilityTask) {
                MessageData messageData = new MessageData();
                messageData.mTask = (PresenceCapabilityTask) task;
                messageData.mReason = null;
                Message timeoutMessage = sMsgHandler.obtainMessage(2, messageData);
                sMsgHandler.sendMessage(timeoutMessage);
            } else {
                this.logger.debug("not PresenceCapabilityTask, taskId=" + taskId);
            }
        }
    }

    /* loaded from: classes.dex */
    public class MessageData {
        public String mReason;
        public PresenceCapabilityTask mTask;

        public MessageData() {
        }
    }

    /* loaded from: classes.dex */
    public class MessageHandler extends Handler {
        MessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TaskManager.this.logger.debug("Thread=" + Thread.currentThread().getName() + " received " + msg);
            if (msg == null) {
                TaskManager.this.logger.error("msg=null");
                return;
            }
            switch (msg.what) {
                case 1:
                    MessageData messageData = (MessageData) msg.obj;
                    if (messageData != null && messageData.mTask != null) {
                        messageData.mTask.onTerminated(messageData.mReason);
                        return;
                    }
                    return;
                case 2:
                    MessageData messageData2 = (MessageData) msg.obj;
                    if (messageData2 != null && messageData2.mTask != null) {
                        messageData2.mTask.onTimeout();
                        return;
                    }
                    return;
                default:
                    TaskManager.this.logger.debug("handleMessage unknown msg=" + msg.what);
                    return;
            }
        }
    }

    public void clearTimeoutAvailabilityTask(long availabilityExpire) {
        this.logger.debug("clearTimeoutAvailabilityTask");
        synchronized (this.mSyncObj) {
            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<String, Task>> iterator = this.mTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Task> entry = iterator.next();
                Task task = entry.getValue();
                this.logger.debug("Currently existing Availability task, key: " + entry.getKey() + ", Task: " + task);
                if (task != null && (task instanceof PresenceAvailabilityTask)) {
                    PresenceAvailabilityTask presenceTask = (PresenceAvailabilityTask) task;
                    long notifyTimestamp = presenceTask.getNotifyTimestamp();
                    long createTimestamp = presenceTask.getCreateTimestamp();
                    this.logger.debug("createTimestamp=" + createTimestamp + " notifyTimestamp=" + notifyTimestamp + " currentTime=" + currentTime);
                    if ((notifyTimestamp != 0 && notifyTimestamp + availabilityExpire < currentTime) || (notifyTimestamp == 0 && createTimestamp + availabilityExpire < currentTime)) {
                        this.logger.debug("remove expired availability task:" + presenceTask);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public PresenceAvailabilityTask getAvailabilityTaskByContact(String contact) {
        synchronized (this.mSyncObj) {
            Set<String> keys = this.mTaskMap.keySet();
            if (keys == null) {
                this.logger.debug("getTaskByContact keys=null");
                return null;
            }
            for (String key : keys) {
                Task task = this.mTaskMap.get(key);
                if (task != null) {
                    if (task instanceof PresenceAvailabilityTask) {
                        PresenceAvailabilityTask availabilityTask = (PresenceAvailabilityTask) task;
                        if (PhoneNumberUtils.compare(contact, availabilityTask.mContacts[0])) {
                            return availabilityTask;
                        }
                    }
                }
            }
            return null;
        }
    }
}
