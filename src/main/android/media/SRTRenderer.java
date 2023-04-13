package android.media;

import android.content.Context;
import android.media.SubtitleController;
import android.p008os.Handler;
/* loaded from: classes2.dex */
public class SRTRenderer extends SubtitleController.Renderer {
    private final Context mContext;
    private final Handler mEventHandler;
    private final boolean mRender;
    private WebVttRenderingWidget mRenderingWidget;

    public SRTRenderer(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SRTRenderer(Context mContext, Handler mEventHandler) {
        this.mContext = mContext;
        this.mRender = mEventHandler == null;
        this.mEventHandler = mEventHandler;
    }

    @Override // android.media.SubtitleController.Renderer
    public boolean supports(MediaFormat format) {
        if (format.containsKey(MediaFormat.KEY_MIME) && format.getString(MediaFormat.KEY_MIME).equals("application/x-subrip")) {
            return this.mRender == (format.getInteger(MediaFormat.KEY_IS_TIMED_TEXT, 0) == 0);
        }
        return false;
    }

    @Override // android.media.SubtitleController.Renderer
    public SubtitleTrack createTrack(MediaFormat format) {
        if (this.mRender && this.mRenderingWidget == null) {
            this.mRenderingWidget = new WebVttRenderingWidget(this.mContext);
        }
        if (this.mRender) {
            return new SRTTrack(this.mRenderingWidget, format);
        }
        return new SRTTrack(this.mEventHandler, format);
    }
}
