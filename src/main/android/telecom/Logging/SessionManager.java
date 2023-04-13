package android.telecom.Logging;

import android.content.ContentResolver;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Process;
import android.provider.Settings;
import android.telecom.Log;
import android.telecom.Logging.Session;
import android.util.Base64;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes3.dex */
public class SessionManager {
    private static final long DEFAULT_SESSION_TIMEOUT_MS = 30000;
    private static final String LOGGING_TAG = "Logging";
    private static final long SESSION_ID_ROLLOVER_THRESHOLD = 262144;
    private static final String TIMEOUTS_PREFIX = "telecom.";
    private Context mContext;
    private int sCodeEntryCounter = 0;
    public ConcurrentHashMap<Integer, Session> mSessionMapper = new ConcurrentHashMap<>(100);
    public java.lang.Runnable mCleanStaleSessions = new java.lang.Runnable() { // from class: android.telecom.Logging.SessionManager$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            SessionManager.this.lambda$new$0();
        }
    };
    private Handler mSessionCleanupHandler = new Handler(Looper.getMainLooper());
    public ICurrentThreadId mCurrentThreadId = new ICurrentThreadId() { // from class: android.telecom.Logging.SessionManager$$ExternalSyntheticLambda1
        @Override // android.telecom.Logging.SessionManager.ICurrentThreadId
        public final int get() {
            return Process.myTid();
        }
    };
    private ISessionCleanupTimeoutMs mSessionCleanupTimeoutMs = new ISessionCleanupTimeoutMs() { // from class: android.telecom.Logging.SessionManager$$ExternalSyntheticLambda2
        @Override // android.telecom.Logging.SessionManager.ISessionCleanupTimeoutMs
        public final long get() {
            long lambda$new$1;
            lambda$new$1 = SessionManager.this.lambda$new$1();
            return lambda$new$1;
        }
    };
    private List<ISessionListener> mSessionListeners = new ArrayList();

    /* loaded from: classes3.dex */
    public interface ICurrentThreadId {
        int get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface ISessionCleanupTimeoutMs {
        long get();
    }

    /* loaded from: classes3.dex */
    public interface ISessionIdQueryHandler {
        String getSessionId();
    }

    /* loaded from: classes3.dex */
    public interface ISessionListener {
        void sessionComplete(String str, long j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        cleanupStaleSessions(getSessionCleanupTimeoutMs());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ long lambda$new$1() {
        Context context = this.mContext;
        if (context == null) {
            return 30000L;
        }
        return getCleanupTimeout(context);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private long getSessionCleanupTimeoutMs() {
        return this.mSessionCleanupTimeoutMs.get();
    }

    private synchronized void resetStaleSessionTimer() {
        this.mSessionCleanupHandler.removeCallbacksAndMessages(null);
        java.lang.Runnable runnable = this.mCleanStaleSessions;
        if (runnable != null) {
            this.mSessionCleanupHandler.postDelayed(runnable, getSessionCleanupTimeoutMs());
        }
    }

    public synchronized void startSession(Session.Info info, String shortMethodName, String callerIdentification) {
        if (info == null) {
            startSession(shortMethodName, callerIdentification);
        } else {
            startExternalSession(info, shortMethodName);
        }
    }

    public synchronized void startSession(String shortMethodName, String callerIdentification) {
        resetStaleSessionTimer();
        int threadId = getCallingThreadId();
        Session activeSession = this.mSessionMapper.get(Integer.valueOf(threadId));
        if (activeSession != null) {
            Session childSession = createSubsession(true);
            continueSession(childSession, shortMethodName);
            return;
        }
        Log.m138d(LOGGING_TAG, Session.START_SESSION, new Object[0]);
        Session newSession = new Session(getNextSessionID(), shortMethodName, System.currentTimeMillis(), false, callerIdentification);
        this.mSessionMapper.put(Integer.valueOf(threadId), newSession);
    }

    public synchronized void startExternalSession(Session.Info sessionInfo, String shortMethodName) {
        if (sessionInfo == null) {
            return;
        }
        int threadId = getCallingThreadId();
        Session threadSession = this.mSessionMapper.get(Integer.valueOf(threadId));
        if (threadSession != null) {
            Log.m130w(LOGGING_TAG, "trying to start an external session with a session already active.", new Object[0]);
            return;
        }
        Log.m138d(LOGGING_TAG, Session.START_EXTERNAL_SESSION, new Object[0]);
        Session externalSession = new Session(Session.EXTERNAL_INDICATOR + sessionInfo.sessionId, sessionInfo.methodPath, System.currentTimeMillis(), false, sessionInfo.ownerInfo);
        externalSession.setIsExternal(true);
        externalSession.markSessionCompleted(-1L);
        this.mSessionMapper.put(Integer.valueOf(threadId), externalSession);
        Session childSession = createSubsession();
        continueSession(childSession, shortMethodName);
    }

    public Session createSubsession() {
        return createSubsession(false);
    }

    public synchronized Session createSubsession(boolean isStartedFromActiveSession) {
        int threadId = getCallingThreadId();
        Session threadSession = this.mSessionMapper.get(Integer.valueOf(threadId));
        if (threadSession == null) {
            Log.m138d(LOGGING_TAG, "Log.createSubsession was called with no session active.", new Object[0]);
            return null;
        }
        Session newSubsession = new Session(threadSession.getNextChildId(), threadSession.getShortMethodName(), System.currentTimeMillis(), isStartedFromActiveSession, threadSession.getOwnerInfo());
        threadSession.addChild(newSubsession);
        newSubsession.setParentSession(threadSession);
        if (!isStartedFromActiveSession) {
            Log.m132v(LOGGING_TAG, "CREATE_SUBSESSION " + newSubsession.toString(), new Object[0]);
        } else {
            Log.m132v(LOGGING_TAG, "CREATE_SUBSESSION (Invisible subsession)", new Object[0]);
        }
        return newSubsession;
    }

    public synchronized Session.Info getExternalSession() {
        return getExternalSession(null);
    }

    public synchronized Session.Info getExternalSession(String ownerInfo) {
        int threadId = getCallingThreadId();
        Session threadSession = this.mSessionMapper.get(Integer.valueOf(threadId));
        if (threadSession == null) {
            Log.m138d(LOGGING_TAG, "Log.getExternalSession was called with no session active.", new Object[0]);
            return null;
        }
        return threadSession.getExternalInfo(ownerInfo);
    }

    public synchronized void cancelSubsession(Session subsession) {
        if (subsession == null) {
            return;
        }
        subsession.markSessionCompleted(-1L);
        endParentSessions(subsession);
    }

    public synchronized void continueSession(Session subsession, String shortMethodName) {
        if (subsession == null) {
            return;
        }
        resetStaleSessionTimer();
        subsession.setShortMethodName(shortMethodName);
        subsession.setExecutionStartTimeMs(System.currentTimeMillis());
        Session parentSession = subsession.getParentSession();
        if (parentSession == null) {
            Log.m134i(LOGGING_TAG, "Log.continueSession was called with no session active for method " + shortMethodName, new Object[0]);
            return;
        }
        this.mSessionMapper.put(Integer.valueOf(getCallingThreadId()), subsession);
        if (!subsession.isStartedFromActiveSession()) {
            Log.m132v(LOGGING_TAG, Session.CONTINUE_SUBSESSION, new Object[0]);
        } else {
            Log.m132v(LOGGING_TAG, "CONTINUE_SUBSESSION (Invisible Subsession) with Method " + shortMethodName, new Object[0]);
        }
    }

    public synchronized void endSession() {
        int threadId = getCallingThreadId();
        Session completedSession = this.mSessionMapper.get(Integer.valueOf(threadId));
        if (completedSession == null) {
            Log.m130w(LOGGING_TAG, "Log.endSession was called with no session active.", new Object[0]);
            return;
        }
        completedSession.markSessionCompleted(System.currentTimeMillis());
        if (!completedSession.isStartedFromActiveSession()) {
            Log.m132v(LOGGING_TAG, "END_SUBSESSION (dur: " + completedSession.getLocalExecutionTime() + " mS)", new Object[0]);
        } else {
            Log.m132v(LOGGING_TAG, "END_SUBSESSION (Invisible Subsession) (dur: " + completedSession.getLocalExecutionTime() + " ms)", new Object[0]);
        }
        Session parentSession = completedSession.getParentSession();
        this.mSessionMapper.remove(Integer.valueOf(threadId));
        endParentSessions(completedSession);
        if (parentSession != null && !parentSession.isSessionCompleted() && completedSession.isStartedFromActiveSession()) {
            this.mSessionMapper.put(Integer.valueOf(threadId), parentSession);
        }
    }

    private void endParentSessions(Session subsession) {
        if (!subsession.isSessionCompleted() || subsession.getChildSessions().size() != 0) {
            return;
        }
        Session parentSession = subsession.getParentSession();
        if (parentSession != null) {
            subsession.setParentSession(null);
            parentSession.removeChild(subsession);
            if (parentSession.isExternal()) {
                notifySessionCompleteListeners(subsession.getShortMethodName(), System.currentTimeMillis() - subsession.getExecutionStartTimeMilliseconds());
            }
            endParentSessions(parentSession);
            return;
        }
        long fullSessionTimeMs = System.currentTimeMillis() - subsession.getExecutionStartTimeMilliseconds();
        Log.m138d(LOGGING_TAG, "END_SESSION (dur: " + fullSessionTimeMs + " ms): " + subsession.toString(), new Object[0]);
        if (!subsession.isExternal()) {
            notifySessionCompleteListeners(subsession.getShortMethodName(), fullSessionTimeMs);
        }
    }

    private void notifySessionCompleteListeners(String methodName, long sessionTimeMs) {
        for (ISessionListener l : this.mSessionListeners) {
            l.sessionComplete(methodName, sessionTimeMs);
        }
    }

    public String getSessionId() {
        Session currentSession = this.mSessionMapper.get(Integer.valueOf(getCallingThreadId()));
        return currentSession != null ? currentSession.toString() : "";
    }

    public synchronized void registerSessionListener(ISessionListener l) {
        if (l != null) {
            this.mSessionListeners.add(l);
        }
    }

    private synchronized String getNextSessionID() {
        Integer nextId;
        int i = this.sCodeEntryCounter;
        this.sCodeEntryCounter = i + 1;
        nextId = Integer.valueOf(i);
        if (nextId.intValue() >= 262144) {
            restartSessionCounter();
            int i2 = this.sCodeEntryCounter;
            this.sCodeEntryCounter = i2 + 1;
            nextId = Integer.valueOf(i2);
        }
        return getBase64Encoding(nextId.intValue());
    }

    private synchronized void restartSessionCounter() {
        this.sCodeEntryCounter = 0;
    }

    private String getBase64Encoding(int number) {
        byte[] idByteArray = ByteBuffer.allocate(4).putInt(number).array();
        return Base64.encodeToString(Arrays.copyOfRange(idByteArray, 2, 4), 3);
    }

    private int getCallingThreadId() {
        return this.mCurrentThreadId.get();
    }

    public synchronized String printActiveSessions() {
        StringBuilder message;
        message = new StringBuilder();
        for (Map.Entry<Integer, Session> entry : this.mSessionMapper.entrySet()) {
            message.append(entry.getValue().printFullSessionTree());
            message.append("\n");
        }
        return message.toString();
    }

    public synchronized void cleanupStaleSessions(long timeoutMs) {
        String logMessage = "Stale Sessions Cleaned:\n";
        boolean isSessionsStale = false;
        long currentTimeMs = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, Session>> it = this.mSessionMapper.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Session> entry = it.next();
            Session session = entry.getValue();
            if (currentTimeMs - session.getExecutionStartTimeMilliseconds() > timeoutMs) {
                it.remove();
                logMessage = logMessage + session.printFullSessionTree() + "\n";
                isSessionsStale = true;
            }
        }
        if (isSessionsStale) {
            Log.m130w(LOGGING_TAG, logMessage, new Object[0]);
        } else {
            Log.m132v(LOGGING_TAG, "No stale logging sessions needed to be cleaned...", new Object[0]);
        }
    }

    private long getCleanupTimeout(Context context) {
        ContentResolver cr = context.getContentResolver();
        return Settings.Secure.getLongForUser(cr, "telecom.stale_session_cleanup_timeout_millis", 30000L, cr.getUserId());
    }
}
