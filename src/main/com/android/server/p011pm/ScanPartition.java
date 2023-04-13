package com.android.server.p011pm;

import android.content.pm.PackagePartitions;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p011pm.ApexManager;
import java.io.File;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.ScanPartition */
/* loaded from: classes2.dex */
public class ScanPartition extends PackagePartitions.SystemPartition {
    public final ApexManager.ActiveApexInfo apexInfo;
    public final int scanFlag;

    public ScanPartition(PackagePartitions.SystemPartition systemPartition) {
        super(systemPartition);
        this.scanFlag = scanFlagForPartition(systemPartition);
        this.apexInfo = null;
    }

    public ScanPartition(File file, ScanPartition scanPartition, ApexManager.ActiveApexInfo activeApexInfo) {
        super(file, scanPartition);
        int i = scanPartition.scanFlag;
        this.apexInfo = activeApexInfo;
        if (activeApexInfo != null) {
            i |= 8388608;
            i = activeApexInfo.isFactory ? i | 33554432 : i;
            if (activeApexInfo.activeApexChanged) {
                i |= 16777216;
            }
        }
        this.scanFlag = i;
    }

    public static int scanFlagForPartition(PackagePartitions.SystemPartition systemPartition) {
        int i = systemPartition.type;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return 2097152;
                            }
                            throw new IllegalStateException("Unable to determine scan flag for " + systemPartition.getFolder());
                        }
                        return 1048576;
                    }
                    return 262144;
                }
                return 4194304;
            }
            return 524288;
        }
        return 0;
    }

    public String toString() {
        return getFolder().getAbsolutePath() + XmlUtils.STRING_ARRAY_SEPARATOR + this.scanFlag;
    }
}
