package android.media;

import android.media.SubtitleTrack;
import java.util.Vector;
/* compiled from: Cea708CaptionRenderer.java */
/* loaded from: classes2.dex */
class Cea708CaptionTrack extends SubtitleTrack {
    private final Cea708CCParser mCCParser;
    private final Cea708CCWidget mRenderingWidget;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cea708CaptionTrack(Cea708CCWidget renderingWidget, MediaFormat format) {
        super(format);
        this.mRenderingWidget = renderingWidget;
        this.mCCParser = new Cea708CCParser(renderingWidget);
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
