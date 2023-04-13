package android.telephony;

import android.telephony.DomainSelectionService;
/* loaded from: classes3.dex */
public interface DomainSelector {
    void cancelSelection();

    void finishSelection();

    void reselectDomain(DomainSelectionService.SelectionAttributes selectionAttributes);
}
