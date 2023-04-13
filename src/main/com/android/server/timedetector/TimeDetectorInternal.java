package com.android.server.timedetector;
/* loaded from: classes2.dex */
public interface TimeDetectorInternal {
    void suggestGnssTime(GnssTimeSuggestion gnssTimeSuggestion);

    void suggestNetworkTime(NetworkTimeSuggestion networkTimeSuggestion);
}
