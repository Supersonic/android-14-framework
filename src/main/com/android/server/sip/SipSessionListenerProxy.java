package com.android.server.sip;

import android.net.sip.ISipSession;
import android.net.sip.ISipSessionListener;
import android.net.sip.SipProfile;
import android.os.DeadObjectException;
import android.telephony.Rlog;
/* loaded from: classes.dex */
class SipSessionListenerProxy extends ISipSessionListener.Stub {
    private static final String TAG = "SipSessionListnerProxy";
    private ISipSessionListener mListener;

    public void setListener(ISipSessionListener listener) {
        this.mListener = listener;
    }

    public ISipSessionListener getListener() {
        return this.mListener;
    }

    private void proxy(Runnable runnable) {
        new Thread(runnable, "SipSessionCallbackThread").start();
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCalling(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCalling(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCalling()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRinging(final ISipSession session, final SipProfile caller, final String sessionDescription) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRinging(session, caller, sessionDescription);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRinging()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRingingBack(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRingingBack(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRingingBack()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallEstablished(final ISipSession session, final String sessionDescription) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCallEstablished(session, sessionDescription);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCallEstablished()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallEnded(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCallEnded(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCallEnded()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallTransferring(final ISipSession newSession, final String sessionDescription) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCallTransferring(newSession, sessionDescription);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCallTransferring()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallBusy(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCallBusy(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCallBusy()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallChangeFailed(final ISipSession session, final int errorCode, final String message) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onCallChangeFailed(session, errorCode, message);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onCallChangeFailed()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onError(final ISipSession session, final int errorCode, final String message) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.9
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onError(session, errorCode, message);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onError()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRegistering(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.10
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRegistering(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRegistering()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRegistrationDone(final ISipSession session, final int duration) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.11
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRegistrationDone(session, duration);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRegistrationDone()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRegistrationFailed(final ISipSession session, final int errorCode, final String message) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.12
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRegistrationFailed(session, errorCode, message);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRegistrationFailed()");
                }
            }
        });
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRegistrationTimeout(final ISipSession session) {
        if (this.mListener == null) {
            return;
        }
        proxy(new Runnable() { // from class: com.android.server.sip.SipSessionListenerProxy.13
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SipSessionListenerProxy.this.mListener.onRegistrationTimeout(session);
                } catch (Throwable t) {
                    SipSessionListenerProxy.this.handle(t, "onRegistrationTimeout()");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handle(Throwable t, String message) {
        if (t instanceof DeadObjectException) {
            this.mListener = null;
        } else if (this.mListener != null) {
            loge(message, t);
        }
    }

    private void log(String s) {
        Rlog.d(TAG, s);
    }

    private void loge(String s, Throwable t) {
        Rlog.e(TAG, s, t);
    }
}
