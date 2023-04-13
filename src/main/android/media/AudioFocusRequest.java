package android.media;

import android.annotation.SystemApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.p008os.Bundle;
import android.p008os.Handler;
/* loaded from: classes2.dex */
public final class AudioFocusRequest {
    private static final AudioAttributes FOCUS_DEFAULT_ATTR = new AudioAttributes.Builder().setUsage(1).build();
    public static final String KEY_ACCESSIBILITY_FORCE_FOCUS_DUCKING = "a11y_force_ducking";
    private final AudioAttributes mAttr;
    private final int mFlags;
    private final int mFocusGain;
    private final AudioManager.OnAudioFocusChangeListener mFocusListener;
    private final Handler mListenerHandler;

    private AudioFocusRequest(AudioManager.OnAudioFocusChangeListener listener, Handler handler, AudioAttributes attr, int focusGain, int flags) {
        this.mFocusListener = listener;
        this.mListenerHandler = handler;
        this.mFocusGain = focusGain;
        this.mAttr = attr;
        this.mFlags = flags;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isValidFocusGain(int focusGain) {
        switch (focusGain) {
            case 1:
            case 2:
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    public AudioManager.OnAudioFocusChangeListener getOnAudioFocusChangeListener() {
        return this.mFocusListener;
    }

    public Handler getOnAudioFocusChangeListenerHandler() {
        return this.mListenerHandler;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAttr;
    }

    public int getFocusGain() {
        return this.mFocusGain;
    }

    public boolean willPauseWhenDucked() {
        return (this.mFlags & 2) == 2;
    }

    public boolean acceptsDelayedFocusGain() {
        return (this.mFlags & 1) == 1;
    }

    @SystemApi
    public boolean locksFocus() {
        return (this.mFlags & 4) == 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFlags() {
        return this.mFlags;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mA11yForceDucking;
        private AudioAttributes mAttr;
        private boolean mDelayedFocus;
        private int mFocusGain;
        private AudioManager.OnAudioFocusChangeListener mFocusListener;
        private boolean mFocusLocked;
        private Handler mListenerHandler;
        private boolean mPausesOnDuck;

        public Builder(int focusGain) {
            this.mAttr = AudioFocusRequest.FOCUS_DEFAULT_ATTR;
            this.mPausesOnDuck = false;
            this.mDelayedFocus = false;
            this.mFocusLocked = false;
            this.mA11yForceDucking = false;
            setFocusGain(focusGain);
        }

        public Builder(AudioFocusRequest requestToCopy) {
            this.mAttr = AudioFocusRequest.FOCUS_DEFAULT_ATTR;
            this.mPausesOnDuck = false;
            this.mDelayedFocus = false;
            this.mFocusLocked = false;
            this.mA11yForceDucking = false;
            if (requestToCopy == null) {
                throw new IllegalArgumentException("Illegal null AudioFocusRequest");
            }
            this.mAttr = requestToCopy.mAttr;
            this.mFocusListener = requestToCopy.mFocusListener;
            this.mListenerHandler = requestToCopy.mListenerHandler;
            this.mFocusGain = requestToCopy.mFocusGain;
            this.mPausesOnDuck = requestToCopy.willPauseWhenDucked();
            this.mDelayedFocus = requestToCopy.acceptsDelayedFocusGain();
        }

        public Builder setFocusGain(int focusGain) {
            if (!AudioFocusRequest.isValidFocusGain(focusGain)) {
                throw new IllegalArgumentException("Illegal audio focus gain type " + focusGain);
            }
            this.mFocusGain = focusGain;
            return this;
        }

        public Builder setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener listener) {
            if (listener == null) {
                throw new NullPointerException("Illegal null focus listener");
            }
            this.mFocusListener = listener;
            this.mListenerHandler = null;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setOnAudioFocusChangeListenerInt(AudioManager.OnAudioFocusChangeListener listener, Handler handler) {
            this.mFocusListener = listener;
            this.mListenerHandler = handler;
            return this;
        }

        public Builder setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener listener, Handler handler) {
            if (listener == null || handler == null) {
                throw new NullPointerException("Illegal null focus listener or handler");
            }
            this.mFocusListener = listener;
            this.mListenerHandler = handler;
            return this;
        }

        public Builder setAudioAttributes(AudioAttributes attributes) {
            if (attributes == null) {
                throw new NullPointerException("Illegal null AudioAttributes");
            }
            this.mAttr = attributes;
            return this;
        }

        public Builder setWillPauseWhenDucked(boolean pauseOnDuck) {
            this.mPausesOnDuck = pauseOnDuck;
            return this;
        }

        public Builder setAcceptsDelayedFocusGain(boolean acceptsDelayedFocusGain) {
            this.mDelayedFocus = acceptsDelayedFocusGain;
            return this;
        }

        @SystemApi
        public Builder setLocksFocus(boolean focusLocked) {
            this.mFocusLocked = focusLocked;
            return this;
        }

        public Builder setForceDucking(boolean forceDucking) {
            this.mA11yForceDucking = forceDucking;
            return this;
        }

        public AudioFocusRequest build() {
            Bundle extraInfo;
            if ((this.mDelayedFocus || this.mPausesOnDuck) && this.mFocusListener == null) {
                throw new IllegalStateException("Can't use delayed focus or pause on duck without a listener");
            }
            if (this.mA11yForceDucking) {
                if (this.mAttr.getBundle() == null) {
                    extraInfo = new Bundle();
                } else {
                    extraInfo = this.mAttr.getBundle();
                }
                extraInfo.putBoolean(AudioFocusRequest.KEY_ACCESSIBILITY_FORCE_FOCUS_DUCKING, true);
                this.mAttr = new AudioAttributes.Builder(this.mAttr).addBundle(extraInfo).build();
            }
            int flags = (this.mDelayedFocus ? 1 : 0) | 0 | (this.mPausesOnDuck ? 2 : 0) | (this.mFocusLocked ? 4 : 0);
            return new AudioFocusRequest(this.mFocusListener, this.mListenerHandler, this.mAttr, this.mFocusGain, flags);
        }
    }
}
