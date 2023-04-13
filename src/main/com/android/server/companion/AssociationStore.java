package com.android.server.companion;

import android.companion.AssociationInfo;
import java.util.Collection;
import java.util.List;
/* loaded from: classes.dex */
public interface AssociationStore {
    AssociationInfo getAssociationById(int i);

    Collection<AssociationInfo> getAssociations();

    List<AssociationInfo> getAssociationsByAddress(String str);

    List<AssociationInfo> getAssociationsForPackage(int i, String str);

    List<AssociationInfo> getAssociationsForUser(int i);

    void registerListener(OnChangeListener onChangeListener);

    /* loaded from: classes.dex */
    public interface OnChangeListener {
        default void onAssociationAdded(AssociationInfo associationInfo) {
        }

        default void onAssociationRemoved(AssociationInfo associationInfo) {
        }

        default void onAssociationUpdated(AssociationInfo associationInfo, boolean z) {
        }

        default void onAssociationChanged(int i, AssociationInfo associationInfo) {
            if (i == 0) {
                onAssociationAdded(associationInfo);
            } else if (i == 1) {
                onAssociationRemoved(associationInfo);
            } else if (i == 2) {
                onAssociationUpdated(associationInfo, true);
            } else if (i != 3) {
            } else {
                onAssociationUpdated(associationInfo, false);
            }
        }
    }
}
