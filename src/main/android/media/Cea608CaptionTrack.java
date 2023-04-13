package android.media;

import android.media.SubtitleTrack;
import java.util.Vector;
/* compiled from: ClosedCaptionRenderer.java */
/* loaded from: classes2.dex */
class Cea608CaptionTrack extends SubtitleTrack {
    private final Cea608CCParser mCCParser;
    private final Cea608CCWidget mRenderingWidget;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cea608CaptionTrack(Cea608CCWidget renderingWidget, MediaFormat format) {
        super(format);
        this.mRenderingWidget = renderingWidget;
        this.mCCParser = new Cea608CCParser(renderingWidget);
    }

    @Override // android.media.SubtitleTrack
    public void onData(byte[] data, boolean eos, long runID) {
        this.mCCParser.parse(data);
    }

    @Override // android.media.SubtitleTrack
    public SubtitleTrack.RenderingWidget getRenderingWidget() {
        return this.mRenderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public void updateView(Vector<SubtitleTrack.Cue> activeCues) {
    }
}
