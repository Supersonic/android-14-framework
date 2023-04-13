package android.media;

import android.media.AudioMetadata;
import java.util.Set;
/* loaded from: classes2.dex */
public interface AudioMetadataReadMap {
    <T> boolean containsKey(AudioMetadata.Key<T> key);

    AudioMetadataMap dup();

    <T> T get(AudioMetadata.Key<T> key);

    Set<AudioMetadata.Key<?>> keySet();

    int size();
}
