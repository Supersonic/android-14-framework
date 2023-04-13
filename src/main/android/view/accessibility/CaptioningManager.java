package android.view.accessibility;

import android.annotation.SystemApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.p008os.Handler;
import android.provider.Settings;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes4.dex */
public class CaptioningManager {
    private static final int DEFAULT_ENABLED = 0;
    private static final float DEFAULT_FONT_SCALE = 1.0f;
    private static final int DEFAULT_PRESET = 0;
    private static final boolean SYSTEM_AUDIO_CAPTIONING_DEFAULT_ENABLED = false;
    private final AccessibilityManager mAccessibilityManager;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final Resources mResources;
    private final ArrayList<CaptioningChangeListener> mListeners = new ArrayList<>();
    private final Runnable mStyleChangedRunnable = new Runnable() { // from class: android.view.accessibility.CaptioningManager.1
        @Override // java.lang.Runnable
        public void run() {
            CaptioningManager.this.notifyUserStyleChanged();
        }
    };

    /* loaded from: classes4.dex */
    public interface SystemAudioCaptioningAccessing {
        boolean isSystemAudioCaptioningUiEnabled(int i);

        void setSystemAudioCaptioningEnabled(boolean z, int i);

        void setSystemAudioCaptioningUiEnabled(boolean z, int i);
    }

    public CaptioningManager(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        Handler handler = new Handler(context.getMainLooper());
        this.mContentObserver = new MyContentObserver(handler);
        this.mResources = context.getResources();
    }

    public final boolean isEnabled() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED, 0) == 1;
    }

    public final String getRawLocale() {
        return Settings.Secure.getString(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
    }

    public final Locale getLocale() {
        String rawLocale = getRawLocale();
        if (!TextUtils.isEmpty(rawLocale)) {
            String[] splitLocale = rawLocale.split(Session.SESSION_SEPARATION_CHAR_CHILD);
            switch (splitLocale.length) {
                case 1:
                    return new Locale(splitLocale[0]);
                case 2:
                    return new Locale(splitLocale[0], splitLocale[1]);
                case 3:
                    return new Locale(splitLocale[0], splitLocale[1], splitLocale[2]);
                default:
                    return null;
            }
        }
        return null;
    }

    public final float getFontScale() {
        return Settings.Secure.getFloat(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE, 1.0f);
    }

    public int getRawUserStyle() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_PRESET, 0);
    }

    public CaptionStyle getUserStyle() {
        int preset = getRawUserStyle();
        if (preset == -1) {
            return CaptionStyle.getCustomStyle(this.mContentResolver);
        }
        return CaptionStyle.PRESETS[preset];
    }

    public final boolean isSystemAudioCaptioningEnabled() {
        return Settings.Secure.getIntForUser(this.mContentResolver, Settings.Secure.ODI_CAPTIONS_ENABLED, 0, this.mContext.getUserId()) == 1;
    }

    @SystemApi
    public final void setSystemAudioCaptioningEnabled(boolean isEnabled) {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        if (accessibilityManager != null) {
            accessibilityManager.setSystemAudioCaptioningEnabled(isEnabled, this.mContext.getUserId());
        }
    }

    public final boolean isSystemAudioCaptioningUiEnabled() {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        return accessibilityManager != null && accessibilityManager.isSystemAudioCaptioningUiEnabled(this.mContext.getUserId());
    }

    @SystemApi
    public final void setSystemAudioCaptioningUiEnabled(boolean isEnabled) {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        if (accessibilityManager != null) {
            accessibilityManager.setSystemAudioCaptioningUiEnabled(isEnabled, this.mContext.getUserId());
        }
    }

    public void addCaptioningChangeListener(CaptioningChangeListener listener) {
        synchronized (this.mListeners) {
            if (this.mListeners.isEmpty()) {
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_WINDOW_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_PRESET);
                registerObserver(Settings.Secure.ODI_CAPTIONS_ENABLED);
                registerObserver(Settings.Secure.ODI_CAPTIONS_VOLUME_UI_ENABLED);
            }
            this.mListeners.add(listener);
        }
    }

    private void registerObserver(String key) {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(key), false, this.mContentObserver);
    }

    public void removeCaptioningChangeListener(CaptioningChangeListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
            if (this.mListeners.isEmpty()) {
                this.mContentResolver.unregisterContentObserver(this.mContentObserver);
            }
        }
    }

    public boolean isCallCaptioningEnabled() {
        try {
            return this.mResources.getBoolean(C4057R.bool.config_systemCaptionsServiceCallsEnabled);
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyEnabledChanged() {
        boolean enabled = isEnabled();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onEnabledChanged(enabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserStyleChanged() {
        CaptionStyle userStyle = getUserStyle();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onUserStyleChanged(userStyle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLocaleChanged() {
        Locale locale = getLocale();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onLocaleChanged(locale);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyFontScaleChanged() {
        float fontScale = getFontScale();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onFontScaleChanged(fontScale);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifySystemAudioCaptionChanged() {
        boolean enabled = isSystemAudioCaptioningEnabled();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onSystemAudioCaptioningChanged(enabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifySystemAudioCaptionUiChanged() {
        boolean enabled = isSystemAudioCaptioningUiEnabled();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                CaptioningChangeListener listener = it.next();
                listener.onSystemAudioCaptioningUiChanged(enabled);
            }
        }
    }

    /* loaded from: classes4.dex */
    private class MyContentObserver extends ContentObserver {
        private final Handler mHandler;

        public MyContentObserver(Handler handler) {
            super(handler);
            this.mHandler = handler;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            String uriPath = uri.getPath();
            String name = uriPath.substring(uriPath.lastIndexOf(47) + 1);
            if (Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED.equals(name)) {
                CaptioningManager.this.notifyEnabledChanged();
            } else if (Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE.equals(name)) {
                CaptioningManager.this.notifyLocaleChanged();
            } else if (Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE.equals(name)) {
                CaptioningManager.this.notifyFontScaleChanged();
            } else if (Settings.Secure.ODI_CAPTIONS_ENABLED.equals(name)) {
                CaptioningManager.this.notifySystemAudioCaptionChanged();
            } else if (Settings.Secure.ODI_CAPTIONS_VOLUME_UI_ENABLED.equals(name)) {
                CaptioningManager.this.notifySystemAudioCaptionUiChanged();
            } else {
                this.mHandler.removeCallbacks(CaptioningManager.this.mStyleChangedRunnable);
                this.mHandler.post(CaptioningManager.this.mStyleChangedRunnable);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class CaptionStyle {
        private static final CaptionStyle BLACK_ON_WHITE;
        private static final int COLOR_NONE_OPAQUE = 255;
        public static final int COLOR_UNSPECIFIED = 16777215;
        public static final CaptionStyle DEFAULT;
        private static final CaptionStyle DEFAULT_CUSTOM;
        public static final int EDGE_TYPE_DEPRESSED = 4;
        public static final int EDGE_TYPE_DROP_SHADOW = 2;
        public static final int EDGE_TYPE_NONE = 0;
        public static final int EDGE_TYPE_OUTLINE = 1;
        public static final int EDGE_TYPE_RAISED = 3;
        public static final int EDGE_TYPE_UNSPECIFIED = -1;
        public static final CaptionStyle[] PRESETS;
        public static final int PRESET_CUSTOM = -1;
        private static final CaptionStyle UNSPECIFIED;
        private static final CaptionStyle WHITE_ON_BLACK;
        private static final CaptionStyle YELLOW_ON_BLACK;
        private static final CaptionStyle YELLOW_ON_BLUE;
        public final int backgroundColor;
        public final int edgeColor;
        public final int edgeType;
        public final int foregroundColor;
        private final boolean mHasBackgroundColor;
        private final boolean mHasEdgeColor;
        private final boolean mHasEdgeType;
        private final boolean mHasForegroundColor;
        private final boolean mHasWindowColor;
        private Typeface mParsedTypeface;
        public final String mRawTypeface;
        public final int windowColor;

        private CaptionStyle(int foregroundColor, int backgroundColor, int edgeType, int edgeColor, int windowColor, String rawTypeface) {
            boolean hasColor = hasColor(foregroundColor);
            this.mHasForegroundColor = hasColor;
            boolean hasColor2 = hasColor(backgroundColor);
            this.mHasBackgroundColor = hasColor2;
            boolean z = edgeType != -1;
            this.mHasEdgeType = z;
            boolean hasColor3 = hasColor(edgeColor);
            this.mHasEdgeColor = hasColor3;
            boolean hasColor4 = hasColor(windowColor);
            this.mHasWindowColor = hasColor4;
            this.foregroundColor = hasColor ? foregroundColor : -1;
            this.backgroundColor = hasColor2 ? backgroundColor : -16777216;
            this.edgeType = z ? edgeType : 0;
            this.edgeColor = hasColor3 ? edgeColor : -16777216;
            this.windowColor = hasColor4 ? windowColor : 255;
            this.mRawTypeface = rawTypeface;
        }

        public static boolean hasColor(int packedColor) {
            return (packedColor >>> 24) != 0 || (16776960 & packedColor) == 0;
        }

        public CaptionStyle applyStyle(CaptionStyle overlay) {
            int newForegroundColor = overlay.hasForegroundColor() ? overlay.foregroundColor : this.foregroundColor;
            int newBackgroundColor = overlay.hasBackgroundColor() ? overlay.backgroundColor : this.backgroundColor;
            int newEdgeType = overlay.hasEdgeType() ? overlay.edgeType : this.edgeType;
            int newEdgeColor = overlay.hasEdgeColor() ? overlay.edgeColor : this.edgeColor;
            int newWindowColor = overlay.hasWindowColor() ? overlay.windowColor : this.windowColor;
            String str = overlay.mRawTypeface;
            if (str == null) {
                str = this.mRawTypeface;
            }
            String newRawTypeface = str;
            return new CaptionStyle(newForegroundColor, newBackgroundColor, newEdgeType, newEdgeColor, newWindowColor, newRawTypeface);
        }

        public boolean hasBackgroundColor() {
            return this.mHasBackgroundColor;
        }

        public boolean hasForegroundColor() {
            return this.mHasForegroundColor;
        }

        public boolean hasEdgeType() {
            return this.mHasEdgeType;
        }

        public boolean hasEdgeColor() {
            return this.mHasEdgeColor;
        }

        public boolean hasWindowColor() {
            return this.mHasWindowColor;
        }

        public Typeface getTypeface() {
            if (this.mParsedTypeface == null && !TextUtils.isEmpty(this.mRawTypeface)) {
                this.mParsedTypeface = Typeface.create(this.mRawTypeface, 0);
            }
            return this.mParsedTypeface;
        }

        public static CaptionStyle getCustomStyle(ContentResolver cr) {
            String rawTypeface;
            CaptionStyle defStyle = DEFAULT_CUSTOM;
            int foregroundColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, defStyle.foregroundColor);
            int backgroundColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, defStyle.backgroundColor);
            int edgeType = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE, defStyle.edgeType);
            int edgeColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR, defStyle.edgeColor);
            int windowColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_WINDOW_COLOR, defStyle.windowColor);
            String rawTypeface2 = Settings.Secure.getString(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
            if (rawTypeface2 != null) {
                rawTypeface = rawTypeface2;
            } else {
                rawTypeface = defStyle.mRawTypeface;
            }
            return new CaptionStyle(foregroundColor, backgroundColor, edgeType, edgeColor, windowColor, rawTypeface);
        }

        static {
            CaptionStyle captionStyle = new CaptionStyle(-1, -16777216, 0, -16777216, 255, null);
            WHITE_ON_BLACK = captionStyle;
            CaptionStyle captionStyle2 = new CaptionStyle(-16777216, -1, 0, -16777216, 255, null);
            BLACK_ON_WHITE = captionStyle2;
            CaptionStyle captionStyle3 = new CaptionStyle(-256, -16777216, 0, -16777216, 255, null);
            YELLOW_ON_BLACK = captionStyle3;
            CaptionStyle captionStyle4 = new CaptionStyle(-256, -16776961, 0, -16777216, 255, null);
            YELLOW_ON_BLUE = captionStyle4;
            CaptionStyle captionStyle5 = new CaptionStyle(16777215, 16777215, -1, 16777215, 16777215, null);
            UNSPECIFIED = captionStyle5;
            PRESETS = new CaptionStyle[]{captionStyle, captionStyle2, captionStyle3, captionStyle4, captionStyle5};
            DEFAULT_CUSTOM = captionStyle;
            DEFAULT = captionStyle;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class CaptioningChangeListener {
        public void onEnabledChanged(boolean enabled) {
        }

        public void onUserStyleChanged(CaptionStyle userStyle) {
        }

        public void onLocaleChanged(Locale locale) {
        }

        public void onFontScaleChanged(float fontScale) {
        }

        public void onSystemAudioCaptioningChanged(boolean enabled) {
        }

        public void onSystemAudioCaptioningUiChanged(boolean enabled) {
        }
    }
}
