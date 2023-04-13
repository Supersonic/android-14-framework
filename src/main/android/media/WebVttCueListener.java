package android.media;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: classes2.dex */
public interface WebVttCueListener {
    void onCueParsed(TextTrackCue textTrackCue);

    void onRegionParsed(TextTrackRegion textTrackRegion);
}
