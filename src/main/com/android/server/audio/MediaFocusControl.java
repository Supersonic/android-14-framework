package com.android.server.audio;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.AudioManager;
import android.media.IAudioFocusDispatcher;
import android.media.MediaMetrics;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
/* loaded from: classes.dex */
public class MediaFocusControl implements PlayerFocusEnforcer {
    public final AppOpsManager mAppOps;
    public final Context mContext;
    @GuardedBy({"mExtFocusChangeLock"})
    public long mExtFocusChangeCounter;
    public PlayerFocusEnforcer mFocusEnforcer;
    public Handler mFocusHandler;
    public HandlerThread mFocusThread;
    public boolean mMultiAudioFocusEnabled;
    public static final Object mAudioFocusLock = new Object();
    public static final EventLogger mEventLogger = new EventLogger(50, "focus commands as seen by MediaFocusControl");
    public static final int[] USAGES_TO_MUTE_IN_RING_OR_CALL = {1, 14};
    public boolean mRingOrCallActive = false;
    public final Object mExtFocusChangeLock = new Object();
    public final Stack<FocusRequester> mFocusStack = new Stack<>();
    public ArrayList<FocusRequester> mMultiAudioFocusList = new ArrayList<>();
    public boolean mNotifyFocusOwnerOnDuck = true;
    public ArrayList<IAudioPolicyCallback> mFocusFollowers = new ArrayList<>();
    @GuardedBy({"mAudioFocusLock"})
    public IAudioPolicyCallback mFocusPolicy = null;
    @GuardedBy({"mAudioFocusLock"})
    public IAudioPolicyCallback mPreviousFocusPolicy = null;
    public HashMap<String, FocusRequester> mFocusOwnersForFocusPolicy = new HashMap<>();

    public MediaFocusControl(Context context, PlayerFocusEnforcer playerFocusEnforcer) {
        this.mMultiAudioFocusEnabled = false;
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mFocusEnforcer = playerFocusEnforcer;
        ContentResolver contentResolver = context.getContentResolver();
        this.mMultiAudioFocusEnabled = Settings.System.getIntForUser(contentResolver, "multi_audio_focus_enabled", 0, contentResolver.getUserId()) != 0;
        initFocusThreading();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("\nMediaFocusControl dump time: " + DateFormat.getTimeInstance().format(new Date()));
        dumpFocusStack(printWriter);
        printWriter.println("\n");
        mEventLogger.dump(printWriter);
        dumpMultiAudioFocus(printWriter);
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public boolean duckPlayers(FocusRequester focusRequester, FocusRequester focusRequester2, boolean z) {
        return this.mFocusEnforcer.duckPlayers(focusRequester, focusRequester2, z);
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void restoreVShapedPlayers(FocusRequester focusRequester) {
        this.mFocusEnforcer.restoreVShapedPlayers(focusRequester);
        this.mFocusHandler.removeEqualMessages(2, new ForgetFadeUidInfo(focusRequester.getClientUid()));
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void mutePlayersForCall(int[] iArr) {
        this.mFocusEnforcer.mutePlayersForCall(iArr);
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void unmutePlayersForCall() {
        this.mFocusEnforcer.unmutePlayersForCall();
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public boolean fadeOutPlayers(FocusRequester focusRequester, FocusRequester focusRequester2) {
        return this.mFocusEnforcer.fadeOutPlayers(focusRequester, focusRequester2);
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void forgetUid(int i) {
        this.mFocusEnforcer.forgetUid(i);
    }

    public void noFocusForSuspendedApp(String str, int i) {
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            ArrayList<String> arrayList = new ArrayList();
            while (it.hasNext()) {
                FocusRequester next = it.next();
                if (next.hasSameUid(i) && next.hasSamePackage(str)) {
                    arrayList.add(next.getClientId());
                    EventLogger eventLogger = mEventLogger;
                    eventLogger.enqueue(new EventLogger.StringEvent("focus owner:" + next.getClientId() + " in uid:" + i + " pack: " + str + " getting AUDIOFOCUS_LOSS due to app suspension").printLog("MediaFocusControl"));
                    next.dispatchFocusChange(-1);
                }
            }
            for (String str2 : arrayList) {
                removeFocusStackEntry(str2, false, true);
            }
        }
    }

    public boolean hasAudioFocusUsers() {
        boolean z;
        synchronized (mAudioFocusLock) {
            z = !this.mFocusStack.empty();
        }
        return z;
    }

    public void discardAudioFocusOwner() {
        synchronized (mAudioFocusLock) {
            if (!this.mFocusStack.empty()) {
                FocusRequester pop = this.mFocusStack.pop();
                pop.handleFocusLoss(-1, null, false);
                pop.release();
            }
        }
    }

    public List<AudioFocusInfo> getFocusStack() {
        ArrayList arrayList;
        synchronized (mAudioFocusLock) {
            arrayList = new ArrayList(this.mFocusStack.size());
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().toAudioFocusInfo());
            }
        }
        return arrayList;
    }

    public boolean sendFocusLoss(AudioFocusInfo audioFocusInfo) {
        FocusRequester focusRequester;
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (true) {
                focusRequester = null;
                if (!it.hasNext()) {
                    break;
                }
                FocusRequester next = it.next();
                if (next.getClientId().equals(audioFocusInfo.getClientId())) {
                    next.handleFocusLoss(-1, null, false);
                    focusRequester = next;
                    break;
                }
            }
            if (focusRequester != null) {
                this.mFocusStack.remove(focusRequester);
                focusRequester.release();
                return true;
            }
            return false;
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    public final void notifyTopOfAudioFocusStack() {
        if (!this.mFocusStack.empty() && canReassignAudioFocus()) {
            this.mFocusStack.peek().handleFocusGain(1);
        }
        if (!this.mMultiAudioFocusEnabled || this.mMultiAudioFocusList.isEmpty()) {
            return;
        }
        Iterator<FocusRequester> it = this.mMultiAudioFocusList.iterator();
        while (it.hasNext()) {
            FocusRequester next = it.next();
            if (isLockedFocusOwner(next)) {
                next.handleFocusGain(1);
            }
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    public final void propagateFocusLossFromGain_syncAf(int i, FocusRequester focusRequester, boolean z) {
        LinkedList<String> linkedList = new LinkedList();
        if (!this.mFocusStack.empty()) {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (it.hasNext()) {
                FocusRequester next = it.next();
                if (next.handleFocusLossFromGain(i, focusRequester, z)) {
                    linkedList.add(next.getClientId());
                }
            }
        }
        if (this.mMultiAudioFocusEnabled && !this.mMultiAudioFocusList.isEmpty()) {
            Iterator<FocusRequester> it2 = this.mMultiAudioFocusList.iterator();
            while (it2.hasNext()) {
                FocusRequester next2 = it2.next();
                if (next2.handleFocusLossFromGain(i, focusRequester, z)) {
                    linkedList.add(next2.getClientId());
                }
            }
        }
        for (String str : linkedList) {
            removeFocusStackEntry(str, false, true);
        }
    }

    public final void dumpFocusStack(PrintWriter printWriter) {
        printWriter.println("\nAudio Focus stack entries (last is top of stack):");
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (it.hasNext()) {
                it.next().dump(printWriter);
            }
            printWriter.println("\n");
            if (this.mFocusPolicy == null) {
                printWriter.println("No external focus policy\n");
            } else {
                printWriter.println("External focus policy: " + this.mFocusPolicy + ", focus owners:\n");
                dumpExtFocusPolicyFocusOwners(printWriter);
            }
        }
        printWriter.println("\n");
        printWriter.println(" Notify on duck:  " + this.mNotifyFocusOwnerOnDuck + "\n");
        printWriter.println(" In ring or call: " + this.mRingOrCallActive + "\n");
    }

    @GuardedBy({"mAudioFocusLock"})
    public final void removeFocusStackEntry(String str, boolean z, boolean z2) {
        if (!this.mFocusStack.empty() && this.mFocusStack.peek().hasSameClient(str)) {
            FocusRequester pop = this.mFocusStack.pop();
            pop.maybeRelease();
            r1 = z2 ? pop.toAudioFocusInfo() : null;
            if (z) {
                notifyTopOfAudioFocusStack();
            }
        } else {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (it.hasNext()) {
                FocusRequester next = it.next();
                if (next.hasSameClient(str)) {
                    Log.i("MediaFocusControl", "AudioFocus  removeFocusStackEntry(): removing entry for " + str);
                    it.remove();
                    if (z2) {
                        r1 = next.toAudioFocusInfo();
                    }
                    next.maybeRelease();
                }
            }
        }
        if (r1 != null) {
            r1.clearLossReceived();
            notifyExtPolicyFocusLoss_syncAf(r1, false);
        }
        if (!this.mMultiAudioFocusEnabled || this.mMultiAudioFocusList.isEmpty()) {
            return;
        }
        Iterator<FocusRequester> it2 = this.mMultiAudioFocusList.iterator();
        while (it2.hasNext()) {
            FocusRequester next2 = it2.next();
            if (next2.hasSameClient(str)) {
                it2.remove();
                next2.release();
            }
        }
        if (z) {
            notifyTopOfAudioFocusStack();
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    public final void removeFocusStackEntryOnDeath(IBinder iBinder) {
        boolean z = !this.mFocusStack.isEmpty() && this.mFocusStack.peek().hasSameBinder(iBinder);
        Iterator<FocusRequester> it = this.mFocusStack.iterator();
        while (it.hasNext()) {
            FocusRequester next = it.next();
            if (next.hasSameBinder(iBinder)) {
                Log.i("MediaFocusControl", "AudioFocus  removeFocusStackEntryOnDeath(): removing entry for " + iBinder);
                EventLogger eventLogger = mEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("focus requester:" + next.getClientId() + " in uid:" + next.getClientUid() + " pack:" + next.getPackageName() + " died"));
                notifyExtPolicyFocusLoss_syncAf(next.toAudioFocusInfo(), false);
                it.remove();
                next.release();
            }
        }
        if (z) {
            notifyTopOfAudioFocusStack();
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    public final void removeFocusEntryForExtPolicyOnDeath(IBinder iBinder) {
        if (this.mFocusOwnersForFocusPolicy.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<String, FocusRequester>> it = this.mFocusOwnersForFocusPolicy.entrySet().iterator();
        while (it.hasNext()) {
            FocusRequester value = it.next().getValue();
            if (value.hasSameBinder(iBinder)) {
                it.remove();
                EventLogger eventLogger = mEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("focus requester:" + value.getClientId() + " in uid:" + value.getClientUid() + " pack:" + value.getPackageName() + " died"));
                value.release();
                notifyExtFocusPolicyFocusAbandon_syncAf(value.toAudioFocusInfo());
                return;
            }
        }
    }

    public final boolean canReassignAudioFocus() {
        return this.mFocusStack.isEmpty() || !isLockedFocusOwner(this.mFocusStack.peek());
    }

    public final boolean isLockedFocusOwner(FocusRequester focusRequester) {
        return focusRequester.hasSameClient("AudioFocus_For_Phone_Ring_And_Calls") || focusRequester.isLockedFocusOwner();
    }

    @GuardedBy({"mAudioFocusLock"})
    public final int pushBelowLockedFocusOwnersAndPropagate(FocusRequester focusRequester) {
        int size = this.mFocusStack.size();
        for (int size2 = this.mFocusStack.size() - 1; size2 >= 0; size2--) {
            if (isLockedFocusOwner(this.mFocusStack.elementAt(size2))) {
                size = size2;
            }
        }
        if (size == this.mFocusStack.size()) {
            Log.e("MediaFocusControl", "No exclusive focus owner found in propagateFocusLossFromGain_syncAf()", new Exception());
            propagateFocusLossFromGain_syncAf(focusRequester.getGainRequest(), focusRequester, false);
            this.mFocusStack.push(focusRequester);
            return 1;
        }
        this.mFocusStack.insertElementAt(focusRequester, size);
        LinkedList<String> linkedList = new LinkedList();
        for (int i = size - 1; i >= 0; i--) {
            if (this.mFocusStack.elementAt(i).handleFocusLossFromGain(focusRequester.getGainRequest(), focusRequester, false)) {
                linkedList.add(this.mFocusStack.elementAt(i).getClientId());
            }
        }
        for (String str : linkedList) {
            removeFocusStackEntry(str, false, true);
        }
        return 2;
    }

    /* loaded from: classes.dex */
    public class AudioFocusDeathHandler implements IBinder.DeathRecipient {
        public IBinder mCb;

        public AudioFocusDeathHandler(IBinder iBinder) {
            this.mCb = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MediaFocusControl.mAudioFocusLock) {
                if (MediaFocusControl.this.mFocusPolicy != null) {
                    MediaFocusControl.this.removeFocusEntryForExtPolicyOnDeath(this.mCb);
                } else {
                    MediaFocusControl.this.removeFocusStackEntryOnDeath(this.mCb);
                    if (MediaFocusControl.this.mMultiAudioFocusEnabled && !MediaFocusControl.this.mMultiAudioFocusList.isEmpty()) {
                        Iterator<FocusRequester> it = MediaFocusControl.this.mMultiAudioFocusList.iterator();
                        while (it.hasNext()) {
                            FocusRequester next = it.next();
                            if (next.hasSameBinder(this.mCb)) {
                                it.remove();
                                next.release();
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDuckingInExtPolicyAvailable(boolean z) {
        this.mNotifyFocusOwnerOnDuck = !z;
    }

    public boolean mustNotifyFocusOwnerOnDuck() {
        return this.mNotifyFocusOwnerOnDuck;
    }

    public void addFocusFollower(IAudioPolicyCallback iAudioPolicyCallback) {
        boolean z;
        if (iAudioPolicyCallback == null) {
            return;
        }
        synchronized (mAudioFocusLock) {
            Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (it.next().asBinder().equals(iAudioPolicyCallback.asBinder())) {
                    z = true;
                    break;
                }
            }
            if (z) {
                return;
            }
            this.mFocusFollowers.add(iAudioPolicyCallback);
            notifyExtPolicyCurrentFocusAsync(iAudioPolicyCallback);
        }
    }

    public void removeFocusFollower(IAudioPolicyCallback iAudioPolicyCallback) {
        if (iAudioPolicyCallback == null) {
            return;
        }
        synchronized (mAudioFocusLock) {
            Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                IAudioPolicyCallback next = it.next();
                if (next.asBinder().equals(iAudioPolicyCallback.asBinder())) {
                    this.mFocusFollowers.remove(next);
                    break;
                }
            }
        }
    }

    public void setFocusPolicy(IAudioPolicyCallback iAudioPolicyCallback, boolean z) {
        if (iAudioPolicyCallback == null) {
            return;
        }
        synchronized (mAudioFocusLock) {
            if (z) {
                this.mPreviousFocusPolicy = this.mFocusPolicy;
            }
            this.mFocusPolicy = iAudioPolicyCallback;
        }
    }

    public void unsetFocusPolicy(IAudioPolicyCallback iAudioPolicyCallback, boolean z) {
        if (iAudioPolicyCallback == null) {
            return;
        }
        synchronized (mAudioFocusLock) {
            if (this.mFocusPolicy == iAudioPolicyCallback) {
                if (z) {
                    this.mFocusPolicy = this.mPreviousFocusPolicy;
                } else {
                    this.mFocusPolicy = null;
                }
            }
        }
    }

    public void notifyExtPolicyCurrentFocusAsync(final IAudioPolicyCallback iAudioPolicyCallback) {
        new Thread() { // from class: com.android.server.audio.MediaFocusControl.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                synchronized (MediaFocusControl.mAudioFocusLock) {
                    if (MediaFocusControl.this.mFocusStack.isEmpty()) {
                        return;
                    }
                    try {
                        iAudioPolicyCallback.notifyAudioFocusGrant(((FocusRequester) MediaFocusControl.this.mFocusStack.peek()).toAudioFocusInfo(), 1);
                    } catch (RemoteException e) {
                        Log.e("MediaFocusControl", "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + iAudioPolicyCallback.asBinder(), e);
                    }
                }
            }
        }.start();
    }

    public void notifyExtPolicyFocusGrant_syncAf(AudioFocusInfo audioFocusInfo, int i) {
        Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
        while (it.hasNext()) {
            IAudioPolicyCallback next = it.next();
            try {
                next.notifyAudioFocusGrant(audioFocusInfo, i);
            } catch (RemoteException e) {
                Log.e("MediaFocusControl", "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + next.asBinder(), e);
            }
        }
    }

    public void notifyExtPolicyFocusLoss_syncAf(AudioFocusInfo audioFocusInfo, boolean z) {
        Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
        while (it.hasNext()) {
            IAudioPolicyCallback next = it.next();
            try {
                next.notifyAudioFocusLoss(audioFocusInfo, z);
            } catch (RemoteException e) {
                Log.e("MediaFocusControl", "Can't call notifyAudioFocusLoss() on IAudioPolicyCallback " + next.asBinder(), e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x002d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean notifyExtFocusPolicyFocusRequest_syncAf(AudioFocusInfo audioFocusInfo, IAudioFocusDispatcher iAudioFocusDispatcher, IBinder iBinder) {
        boolean z;
        synchronized (this.mExtFocusChangeLock) {
            long j = this.mExtFocusChangeCounter;
            this.mExtFocusChangeCounter = 1 + j;
            audioFocusInfo.setGen(j);
        }
        FocusRequester focusRequester = this.mFocusOwnersForFocusPolicy.get(audioFocusInfo.getClientId());
        try {
            if (focusRequester != null) {
                if (!focusRequester.hasSameDispatcher(iAudioFocusDispatcher)) {
                    focusRequester.release();
                } else {
                    z = false;
                    if (z) {
                        AudioFocusDeathHandler audioFocusDeathHandler = new AudioFocusDeathHandler(iBinder);
                        try {
                            iBinder.linkToDeath(audioFocusDeathHandler, 0);
                            this.mFocusOwnersForFocusPolicy.put(audioFocusInfo.getClientId(), new FocusRequester(audioFocusInfo, iAudioFocusDispatcher, iBinder, audioFocusDeathHandler, this));
                        } catch (RemoteException unused) {
                            return false;
                        }
                    }
                    this.mFocusPolicy.notifyAudioFocusRequest(audioFocusInfo, 1);
                    return true;
                }
            }
            this.mFocusPolicy.notifyAudioFocusRequest(audioFocusInfo, 1);
            return true;
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "Can't call notifyAudioFocusRequest() on IAudioPolicyCallback " + this.mFocusPolicy.asBinder(), e);
            return false;
        }
        z = true;
        if (z) {
        }
    }

    public void setFocusRequestResultFromExtPolicy(AudioFocusInfo audioFocusInfo, int i) {
        FocusRequester focusRequester;
        synchronized (this.mExtFocusChangeLock) {
            if (audioFocusInfo.getGen() > this.mExtFocusChangeCounter) {
                return;
            }
            if (i == 0) {
                focusRequester = this.mFocusOwnersForFocusPolicy.remove(audioFocusInfo.getClientId());
            } else {
                focusRequester = this.mFocusOwnersForFocusPolicy.get(audioFocusInfo.getClientId());
            }
            if (focusRequester != null) {
                focusRequester.dispatchFocusResultFromExtPolicy(i);
            }
        }
    }

    public boolean notifyExtFocusPolicyFocusAbandon_syncAf(AudioFocusInfo audioFocusInfo) {
        if (this.mFocusPolicy == null) {
            return false;
        }
        FocusRequester remove = this.mFocusOwnersForFocusPolicy.remove(audioFocusInfo.getClientId());
        if (remove != null) {
            remove.release();
        }
        try {
            this.mFocusPolicy.notifyAudioFocusAbandon(audioFocusInfo);
            return true;
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "Can't call notifyAudioFocusAbandon() on IAudioPolicyCallback " + this.mFocusPolicy.asBinder(), e);
            return true;
        }
    }

    public int dispatchFocusChange(AudioFocusInfo audioFocusInfo, int i) {
        FocusRequester focusRequester;
        synchronized (mAudioFocusLock) {
            if (this.mFocusPolicy == null) {
                return 0;
            }
            if (i == -1) {
                focusRequester = this.mFocusOwnersForFocusPolicy.remove(audioFocusInfo.getClientId());
            } else {
                focusRequester = this.mFocusOwnersForFocusPolicy.get(audioFocusInfo.getClientId());
            }
            if (focusRequester == null) {
                return 0;
            }
            return focusRequester.dispatchFocusChange(i);
        }
    }

    public final void dumpExtFocusPolicyFocusOwners(PrintWriter printWriter) {
        for (Map.Entry<String, FocusRequester> entry : this.mFocusOwnersForFocusPolicy.entrySet()) {
            entry.getValue().dump(printWriter);
        }
    }

    public int getCurrentAudioFocus() {
        synchronized (mAudioFocusLock) {
            if (this.mFocusStack.empty()) {
                return 0;
            }
            return this.mFocusStack.peek().getGainRequest();
        }
    }

    public static int getFocusRampTimeMs(int i, AudioAttributes audioAttributes) {
        int usage = audioAttributes.getUsage();
        if (usage != 16) {
            if (usage != 1002) {
                if (usage != 1003) {
                    switch (usage) {
                        case 1:
                        case 14:
                            return 1000;
                        case 2:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 13:
                            return 500;
                        case 4:
                        case 6:
                        case 11:
                        case 12:
                            return 700;
                        default:
                            return 0;
                    }
                }
                return 700;
            }
            return 500;
        }
        return 700;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v2, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r15v4 */
    public int requestAudioFocus(AudioAttributes audioAttributes, int i, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2, String str3, int i2, int i3, boolean z, int i4) {
        int i5;
        ?? r15;
        int i6;
        AudioFocusInfo audioFocusInfo;
        boolean z2;
        boolean z3;
        Object[] objArr;
        new MediaMetrics.Item("audio.focus").setUid(Binder.getCallingUid()).set(MediaMetrics.Property.CALLING_PACKAGE, str2).set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.EVENT, "requestAudioFocus").set(MediaMetrics.Property.FLAGS, Integer.valueOf(i2)).set(MediaMetrics.Property.FOCUS_CHANGE_HINT, AudioManager.audioFocusToString(i)).record();
        int callingUid = i2 == 8 ? i4 : Binder.getCallingUid();
        mEventLogger.enqueue(new EventLogger.StringEvent("requestAudioFocus() from uid/pid " + callingUid + "/" + Binder.getCallingPid() + " AA=" + audioAttributes.usageToString() + "/" + audioAttributes.contentTypeToString() + " clientId=" + str + " callingPack=" + str2 + " req=" + i + " flags=0x" + Integer.toHexString(i2) + " sdk=" + i3).printLog("MediaFocusControl"));
        if (!iBinder.pingBinder()) {
            Log.e("MediaFocusControl", " AudioFocus DOA client for requestAudioFocus(), aborting.");
            return 0;
        } else if (i2 == 8 || this.mAppOps.noteOp(32, Binder.getCallingUid(), str2, str3, (String) null) == 0) {
            synchronized (mAudioFocusLock) {
                if (this.mFocusStack.size() > 100) {
                    Log.e("MediaFocusControl", "Max AudioFocus stack size reached, failing requestAudioFocus()");
                    return 0;
                }
                boolean z4 = (!this.mRingOrCallActive ? (char) 1 : (char) 0) & ("AudioFocus_For_Phone_Ring_And_Calls".compareTo(str) == 0 ? (char) 1 : (char) 0);
                if (z4) {
                    this.mRingOrCallActive = true;
                }
                if (this.mFocusPolicy != null) {
                    i5 = 100;
                    r15 = 0;
                    i6 = callingUid;
                    audioFocusInfo = new AudioFocusInfo(audioAttributes, callingUid, str, str2, i, 0, i2, i3);
                } else {
                    i5 = 100;
                    r15 = 0;
                    i6 = callingUid;
                    audioFocusInfo = null;
                }
                AudioFocusInfo audioFocusInfo2 = audioFocusInfo;
                if (canReassignAudioFocus()) {
                    z2 = r15;
                } else if ((i2 & 1) == 0) {
                    return r15;
                } else {
                    z2 = true;
                }
                if (this.mFocusPolicy != null) {
                    return notifyExtFocusPolicyFocusRequest_syncAf(audioFocusInfo2, iAudioFocusDispatcher, iBinder) ? i5 : r15;
                }
                AudioFocusDeathHandler audioFocusDeathHandler = new AudioFocusDeathHandler(iBinder);
                try {
                    iBinder.linkToDeath(audioFocusDeathHandler, r15);
                    if (this.mFocusStack.empty() || !this.mFocusStack.peek().hasSameClient(str)) {
                        z3 = true;
                    } else {
                        FocusRequester peek = this.mFocusStack.peek();
                        if (peek.getGainRequest() == i && peek.getGrantFlags() == i2) {
                            iBinder.unlinkToDeath(audioFocusDeathHandler, r15);
                            notifyExtPolicyFocusGrant_syncAf(peek.toAudioFocusInfo(), 1);
                            return 1;
                        }
                        z3 = true;
                        if (!z2) {
                            this.mFocusStack.pop();
                            peek.release();
                        }
                    }
                    removeFocusStackEntry(str, r15, r15);
                    boolean z5 = z3;
                    FocusRequester focusRequester = new FocusRequester(audioAttributes, i, i2, iAudioFocusDispatcher, iBinder, str, audioFocusDeathHandler, str2, i6, this, i3);
                    if (this.mMultiAudioFocusEnabled && i == z5) {
                        if (z4) {
                            if (!this.mMultiAudioFocusList.isEmpty()) {
                                Iterator<FocusRequester> it = this.mMultiAudioFocusList.iterator();
                                while (it.hasNext()) {
                                    it.next().handleFocusLossFromGain(i, focusRequester, z);
                                }
                            }
                        } else {
                            if (!this.mMultiAudioFocusList.isEmpty()) {
                                Iterator<FocusRequester> it2 = this.mMultiAudioFocusList.iterator();
                                while (it2.hasNext()) {
                                    if (it2.next().getClientUid() == Binder.getCallingUid()) {
                                        objArr = null;
                                        break;
                                    }
                                }
                            }
                            objArr = z5 ? 1 : 0;
                            if (objArr != null) {
                                this.mMultiAudioFocusList.add(focusRequester);
                            }
                            focusRequester.handleFocusGainFromRequest(z5 ? 1 : 0);
                            notifyExtPolicyFocusGrant_syncAf(focusRequester.toAudioFocusInfo(), z5 ? 1 : 0);
                            return z5 ? 1 : 0;
                        }
                    }
                    if (z2) {
                        int pushBelowLockedFocusOwnersAndPropagate = pushBelowLockedFocusOwnersAndPropagate(focusRequester);
                        if (pushBelowLockedFocusOwnersAndPropagate != 0) {
                            notifyExtPolicyFocusGrant_syncAf(focusRequester.toAudioFocusInfo(), pushBelowLockedFocusOwnersAndPropagate);
                        }
                        return pushBelowLockedFocusOwnersAndPropagate;
                    }
                    propagateFocusLossFromGain_syncAf(i, focusRequester, z);
                    this.mFocusStack.push(focusRequester);
                    focusRequester.handleFocusGainFromRequest(z5 ? 1 : 0);
                    notifyExtPolicyFocusGrant_syncAf(focusRequester.toAudioFocusInfo(), z5 ? 1 : 0);
                    if (z4 & true) {
                        runAudioCheckerForRingOrCallAsync(z5);
                    }
                    return z5 ? 1 : 0;
                } catch (RemoteException unused) {
                    int i7 = r15;
                    Log.w("MediaFocusControl", "AudioFocus  requestAudioFocus() could not link to " + iBinder + " binder death");
                    return i7;
                }
            }
        } else {
            return 0;
        }
    }

    public int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str, AudioAttributes audioAttributes, String str2) {
        new MediaMetrics.Item("audio.focus").setUid(Binder.getCallingUid()).set(MediaMetrics.Property.CALLING_PACKAGE, str2).set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.EVENT, "abandonAudioFocus").record();
        EventLogger eventLogger = mEventLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("abandonAudioFocus() from uid/pid " + Binder.getCallingUid() + "/" + Binder.getCallingPid() + " clientId=" + str).printLog("MediaFocusControl"));
        try {
        } catch (ConcurrentModificationException e) {
            Log.e("MediaFocusControl", "FATAL EXCEPTION AudioFocus  abandonAudioFocus() caused " + e);
            e.printStackTrace();
        }
        synchronized (mAudioFocusLock) {
            if (this.mFocusPolicy == null || !notifyExtFocusPolicyFocusAbandon_syncAf(new AudioFocusInfo(audioAttributes, Binder.getCallingUid(), str, str2, 0, 0, 0, 0))) {
                boolean z = this.mRingOrCallActive & ("AudioFocus_For_Phone_Ring_And_Calls".compareTo(str) == 0);
                if (z) {
                    this.mRingOrCallActive = false;
                }
                removeFocusStackEntry(str, true, true);
                if (z & true) {
                    runAudioCheckerForRingOrCallAsync(false);
                }
                return 1;
            }
            return 1;
        }
    }

    public void unregisterAudioFocusClient(String str) {
        synchronized (mAudioFocusLock) {
            removeFocusStackEntry(str, false, true);
        }
    }

    public final void runAudioCheckerForRingOrCallAsync(final boolean z) {
        new Thread() { // from class: com.android.server.audio.MediaFocusControl.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (z) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (MediaFocusControl.mAudioFocusLock) {
                    if (MediaFocusControl.this.mRingOrCallActive) {
                        MediaFocusControl.this.mFocusEnforcer.mutePlayersForCall(MediaFocusControl.USAGES_TO_MUTE_IN_RING_OR_CALL);
                    } else {
                        MediaFocusControl.this.mFocusEnforcer.unmutePlayersForCall();
                    }
                }
            }
        }.start();
    }

    public void updateMultiAudioFocus(boolean z) {
        Log.d("MediaFocusControl", "updateMultiAudioFocus( " + z + " )");
        this.mMultiAudioFocusEnabled = z;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings.System.putIntForUser(contentResolver, "multi_audio_focus_enabled", z ? 1 : 0, contentResolver.getUserId());
        if (!this.mFocusStack.isEmpty()) {
            this.mFocusStack.peek().handleFocusLoss(-1, null, false);
        }
        if (z || this.mMultiAudioFocusList.isEmpty()) {
            return;
        }
        Iterator<FocusRequester> it = this.mMultiAudioFocusList.iterator();
        while (it.hasNext()) {
            it.next().handleFocusLoss(-1, null, false);
        }
        this.mMultiAudioFocusList.clear();
    }

    public boolean getMultiAudioFocusEnabled() {
        return this.mMultiAudioFocusEnabled;
    }

    public long getFadeOutDurationOnFocusLossMillis(AudioAttributes audioAttributes) {
        return FadeOutManager.getFadeOutDurationOnFocusLossMillis(audioAttributes);
    }

    public final void dumpMultiAudioFocus(PrintWriter printWriter) {
        printWriter.println("Multi Audio Focus enabled :" + this.mMultiAudioFocusEnabled);
        if (this.mMultiAudioFocusList.isEmpty()) {
            return;
        }
        printWriter.println("Multi Audio Focus List:");
        printWriter.println("------------------------------");
        Iterator<FocusRequester> it = this.mMultiAudioFocusList.iterator();
        while (it.hasNext()) {
            it.next().dump(printWriter);
        }
        printWriter.println("------------------------------");
    }

    public void postDelayedLossAfterFade(FocusRequester focusRequester, long j) {
        Handler handler = this.mFocusHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1, focusRequester), 2000L);
    }

    public final void postForgetUidLater(int i) {
        Handler handler = this.mFocusHandler;
        handler.sendMessageDelayed(handler.obtainMessage(2, new ForgetFadeUidInfo(i)), 2000L);
    }

    public final void initFocusThreading() {
        HandlerThread handlerThread = new HandlerThread("MediaFocusControl");
        this.mFocusThread = handlerThread;
        handlerThread.start();
        this.mFocusHandler = new Handler(this.mFocusThread.getLooper()) { // from class: com.android.server.audio.MediaFocusControl.3
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 1) {
                    if (i != 2) {
                        return;
                    }
                    MediaFocusControl.this.mFocusEnforcer.forgetUid(((ForgetFadeUidInfo) message.obj).mUid);
                    return;
                }
                synchronized (MediaFocusControl.mAudioFocusLock) {
                    FocusRequester focusRequester = (FocusRequester) message.obj;
                    if (focusRequester.isInFocusLossLimbo()) {
                        focusRequester.dispatchFocusChange(-1);
                        focusRequester.release();
                        MediaFocusControl.this.postForgetUidLater(focusRequester.getClientUid());
                    }
                }
            }
        };
    }

    /* loaded from: classes.dex */
    public static final class ForgetFadeUidInfo {
        public final int mUid;

        public ForgetFadeUidInfo(int i) {
            this.mUid = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj != null && ForgetFadeUidInfo.class == obj.getClass() && ((ForgetFadeUidInfo) obj).mUid == this.mUid;
        }

        public int hashCode() {
            return this.mUid;
        }
    }
}
