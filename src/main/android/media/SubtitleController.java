package android.media;

import android.content.Context;
import android.media.SubtitleTrack;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.view.accessibility.CaptioningManager;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
/* loaded from: classes2.dex */
public class SubtitleController {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int WHAT_HIDE = 2;
    private static final int WHAT_SELECT_DEFAULT_TRACK = 4;
    private static final int WHAT_SELECT_TRACK = 3;
    private static final int WHAT_SHOW = 1;
    private Anchor mAnchor;
    private CaptioningManager mCaptioningManager;
    private Handler mHandler;
    private Listener mListener;
    private SubtitleTrack mSelectedTrack;
    private MediaTimeProvider mTimeProvider;
    private final Handler.Callback mCallback = new Handler.Callback() { // from class: android.media.SubtitleController.1
        @Override // android.p008os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SubtitleController.this.doShow();
                    return true;
                case 2:
                    SubtitleController.this.doHide();
                    return true;
                case 3:
                    SubtitleController.this.doSelectTrack((SubtitleTrack) msg.obj);
                    return true;
                case 4:
                    SubtitleController.this.doSelectDefaultTrack();
                    return true;
                default:
                    return false;
            }
        }
    };
    private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener() { // from class: android.media.SubtitleController.2
        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onEnabledChanged(boolean enabled) {
            SubtitleController.this.selectDefaultTrack();
        }

        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onLocaleChanged(Locale locale) {
            SubtitleController.this.selectDefaultTrack();
        }
    };
    private boolean mTrackIsExplicit = false;
    private boolean mVisibilityIsExplicit = false;
    private Vector<Renderer> mRenderers = new Vector<>();
    private boolean mShowing = false;
    private Vector<SubtitleTrack> mTracks = new Vector<>();

    /* loaded from: classes2.dex */
    public interface Anchor {
        Looper getSubtitleLooper();

        void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget);
    }

    /* loaded from: classes2.dex */
    public interface Listener {
        void onSubtitleTrackSelected(SubtitleTrack subtitleTrack);
    }

    /* loaded from: classes2.dex */
    public static abstract class Renderer {
        public abstract SubtitleTrack createTrack(MediaFormat mediaFormat);

        public abstract boolean supports(MediaFormat mediaFormat);
    }

    public SubtitleController(Context context, MediaTimeProvider timeProvider, Listener listener) {
        this.mTimeProvider = timeProvider;
        this.mListener = listener;
        this.mCaptioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    }

    protected void finalize() throws Throwable {
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
        super.finalize();
    }

    public SubtitleTrack[] getTracks() {
        SubtitleTrack[] tracks;
        synchronized (this.mTracks) {
            tracks = new SubtitleTrack[this.mTracks.size()];
            this.mTracks.toArray(tracks);
        }
        return tracks;
    }

    public SubtitleTrack getSelectedTrack() {
        return this.mSelectedTrack;
    }

    private SubtitleTrack.RenderingWidget getRenderingWidget() {
        SubtitleTrack subtitleTrack = this.mSelectedTrack;
        if (subtitleTrack == null) {
            return null;
        }
        return subtitleTrack.getRenderingWidget();
    }

    public boolean selectTrack(SubtitleTrack track) {
        if (track != null && !this.mTracks.contains(track)) {
            return false;
        }
        processOnAnchor(this.mHandler.obtainMessage(3, track));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectTrack(SubtitleTrack track) {
        this.mTrackIsExplicit = true;
        SubtitleTrack subtitleTrack = this.mSelectedTrack;
        if (subtitleTrack == track) {
            return;
        }
        if (subtitleTrack != null) {
            subtitleTrack.hide();
            this.mSelectedTrack.setTimeProvider(null);
        }
        this.mSelectedTrack = track;
        Anchor anchor = this.mAnchor;
        if (anchor != null) {
            anchor.setSubtitleWidget(getRenderingWidget());
        }
        SubtitleTrack subtitleTrack2 = this.mSelectedTrack;
        if (subtitleTrack2 != null) {
            subtitleTrack2.setTimeProvider(this.mTimeProvider);
            this.mSelectedTrack.show();
        }
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onSubtitleTrackSelected(track);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x009a  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00a3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public SubtitleTrack getDefaultTrack() {
        Locale locale;
        boolean languageMatches;
        int score;
        SubtitleTrack bestTrack = null;
        int bestScore = -1;
        Locale selectedLocale = this.mCaptioningManager.getLocale();
        if (selectedLocale != null) {
            locale = selectedLocale;
        } else {
            Locale locale2 = Locale.getDefault();
            locale = locale2;
        }
        int i = 1;
        boolean selectForced = !this.mCaptioningManager.isEnabled();
        synchronized (this.mTracks) {
            Iterator<SubtitleTrack> it = this.mTracks.iterator();
            while (it.hasNext()) {
                SubtitleTrack track = it.next();
                MediaFormat format = track.getFormat();
                String language = format.getString("language");
                int i2 = format.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) != 0 ? i : 0;
                int i3 = format.getInteger(MediaFormat.KEY_IS_AUTOSELECT, i) != 0 ? i : 0;
                int i4 = format.getInteger(MediaFormat.KEY_IS_DEFAULT, 0) != 0 ? i : 0;
                if (locale != null && !locale.getLanguage().equals("") && !locale.getISO3Language().equals(language) && !locale.getLanguage().equals(language)) {
                    languageMatches = false;
                    score = (i2 == 0 ? 0 : 8) + ((selectedLocale == null || i4 == 0) ? 0 : 4) + (i3 == 0 ? 0 : 2) + (!languageMatches ? 1 : 0);
                    if (!selectForced && i2 == 0) {
                        i = 1;
                    } else {
                        if (((selectedLocale == null && i4 != 0) || (languageMatches && (i3 != 0 || i2 != 0 || selectedLocale != null))) && score > bestScore) {
                            bestScore = score;
                            bestTrack = track;
                        }
                        i = 1;
                    }
                }
                languageMatches = true;
                score = (i2 == 0 ? 0 : 8) + ((selectedLocale == null || i4 == 0) ? 0 : 4) + (i3 == 0 ? 0 : 2) + (!languageMatches ? 1 : 0);
                if (!selectForced) {
                }
                if (selectedLocale == null) {
                    bestScore = score;
                    bestTrack = track;
                    i = 1;
                }
                bestScore = score;
                bestTrack = track;
                i = 1;
            }
        }
        return bestTrack;
    }

    public void selectDefaultTrack() {
        processOnAnchor(this.mHandler.obtainMessage(4));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectDefaultTrack() {
        SubtitleTrack subtitleTrack;
        if (this.mTrackIsExplicit) {
            if (!this.mVisibilityIsExplicit) {
                if (this.mCaptioningManager.isEnabled() || ((subtitleTrack = this.mSelectedTrack) != null && subtitleTrack.getFormat().getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) != 0)) {
                    show();
                } else {
                    SubtitleTrack subtitleTrack2 = this.mSelectedTrack;
                    if (subtitleTrack2 != null && subtitleTrack2.getTrackType() == 4) {
                        hide();
                    }
                }
                this.mVisibilityIsExplicit = false;
                return;
            }
            return;
        }
        SubtitleTrack track = getDefaultTrack();
        if (track != null) {
            selectTrack(track);
            this.mTrackIsExplicit = false;
            if (!this.mVisibilityIsExplicit) {
                show();
                this.mVisibilityIsExplicit = false;
            }
        }
    }

    public void reset() {
        checkAnchorLooper();
        hide();
        selectTrack(null);
        this.mTracks.clear();
        this.mTrackIsExplicit = false;
        this.mVisibilityIsExplicit = false;
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
    }

    public SubtitleTrack addTrack(MediaFormat format) {
        SubtitleTrack track;
        synchronized (this.mRenderers) {
            Iterator<Renderer> it = this.mRenderers.iterator();
            while (it.hasNext()) {
                Renderer renderer = it.next();
                if (renderer.supports(format) && (track = renderer.createTrack(format)) != null) {
                    synchronized (this.mTracks) {
                        if (this.mTracks.size() == 0) {
                            this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
                        }
                        this.mTracks.add(track);
                    }
                    return track;
                }
            }
            return null;
        }
    }

    public void show() {
        processOnAnchor(this.mHandler.obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow() {
        this.mShowing = true;
        this.mVisibilityIsExplicit = true;
        SubtitleTrack subtitleTrack = this.mSelectedTrack;
        if (subtitleTrack != null) {
            subtitleTrack.show();
        }
    }

    public void hide() {
        processOnAnchor(this.mHandler.obtainMessage(2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doHide() {
        this.mVisibilityIsExplicit = true;
        SubtitleTrack subtitleTrack = this.mSelectedTrack;
        if (subtitleTrack != null) {
            subtitleTrack.hide();
        }
        this.mShowing = false;
    }

    public void registerRenderer(Renderer renderer) {
        synchronized (this.mRenderers) {
            if (!this.mRenderers.contains(renderer)) {
                this.mRenderers.add(renderer);
            }
        }
    }

    public boolean hasRendererFor(MediaFormat format) {
        synchronized (this.mRenderers) {
            Iterator<Renderer> it = this.mRenderers.iterator();
            while (it.hasNext()) {
                Renderer renderer = it.next();
                if (renderer.supports(format)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setAnchor(Anchor anchor) {
        Anchor anchor2 = this.mAnchor;
        if (anchor2 == anchor) {
            return;
        }
        if (anchor2 != null) {
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(null);
        }
        this.mAnchor = anchor;
        this.mHandler = null;
        if (anchor != null) {
            this.mHandler = new Handler(this.mAnchor.getSubtitleLooper(), this.mCallback);
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(getRenderingWidget());
        }
    }

    private void checkAnchorLooper() {
    }

    private void processOnAnchor(Message m) {
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            this.mHandler.dispatchMessage(m);
        } else {
            this.mHandler.sendMessage(m);
        }
    }
}
