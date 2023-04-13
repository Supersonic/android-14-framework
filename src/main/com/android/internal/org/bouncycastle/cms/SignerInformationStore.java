package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.util.Iterable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes4.dex */
public class SignerInformationStore implements Iterable<SignerInformation> {
    private List all;
    private Map table = new HashMap();

    public SignerInformationStore(SignerInformation signerInfo) {
        this.all = new ArrayList();
        ArrayList arrayList = new ArrayList(1);
        this.all = arrayList;
        arrayList.add(signerInfo);
        SignerId sid = signerInfo.getSID();
        this.table.put(sid, this.all);
    }

    public SignerInformationStore(Collection<SignerInformation> signerInfos) {
        this.all = new ArrayList();
        for (SignerInformation signer : signerInfos) {
            SignerId sid = signer.getSID();
            ArrayList arrayList = (ArrayList) this.table.get(sid);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.table.put(sid, arrayList);
            }
            arrayList.add(signer);
        }
        this.all = new ArrayList(signerInfos);
    }

    public SignerInformation get(SignerId selector) {
        Collection list = getSigners(selector);
        if (list.size() == 0) {
            return null;
        }
        return list.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<SignerInformation> getSigners() {
        return new ArrayList(this.all);
    }

    public Collection<SignerInformation> getSigners(SignerId selector) {
        if (selector.getIssuer() != null && selector.getSubjectKeyIdentifier() != null) {
            List results = new ArrayList();
            Collection match1 = getSigners(new SignerId(selector.getIssuer(), selector.getSerialNumber()));
            if (match1 != null) {
                results.addAll(match1);
            }
            Collection match2 = getSigners(new SignerId(selector.getSubjectKeyIdentifier()));
            if (match2 != null) {
                results.addAll(match2);
            }
            return results;
        }
        List list = (ArrayList) this.table.get(selector);
        return list == null ? new ArrayList() : new ArrayList(list);
    }

    @Override // com.android.internal.org.bouncycastle.util.Iterable, java.lang.Iterable
    public Iterator<SignerInformation> iterator() {
        return getSigners().iterator();
    }
}
