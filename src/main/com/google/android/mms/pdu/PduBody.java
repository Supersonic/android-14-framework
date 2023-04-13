package com.google.android.mms.pdu;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
/* loaded from: classes5.dex */
public class PduBody {
    private Map<String, PduPart> mPartMapByContentId;
    private Map<String, PduPart> mPartMapByContentLocation;
    private Map<String, PduPart> mPartMapByFileName;
    private Map<String, PduPart> mPartMapByName;
    private Vector<PduPart> mParts;

    public PduBody() {
        this.mParts = null;
        this.mPartMapByContentId = null;
        this.mPartMapByContentLocation = null;
        this.mPartMapByName = null;
        this.mPartMapByFileName = null;
        this.mParts = new Vector<>();
        this.mPartMapByContentId = new HashMap();
        this.mPartMapByContentLocation = new HashMap();
        this.mPartMapByName = new HashMap();
        this.mPartMapByFileName = new HashMap();
    }

    private void putPartToMaps(PduPart part) {
        byte[] contentId = part.getContentId();
        if (contentId != null) {
            this.mPartMapByContentId.put(new String(contentId), part);
        }
        byte[] contentLocation = part.getContentLocation();
        if (contentLocation != null) {
            String clc = new String(contentLocation);
            this.mPartMapByContentLocation.put(clc, part);
        }
        byte[] name = part.getName();
        if (name != null) {
            String clc2 = new String(name);
            this.mPartMapByName.put(clc2, part);
        }
        byte[] fileName = part.getFilename();
        if (fileName != null) {
            String clc3 = new String(fileName);
            this.mPartMapByFileName.put(clc3, part);
        }
    }

    public boolean addPart(PduPart part) {
        if (part == null) {
            throw new NullPointerException();
        }
        putPartToMaps(part);
        return this.mParts.add(part);
    }

    public void addPart(int index, PduPart part) {
        if (part == null) {
            throw new NullPointerException();
        }
        putPartToMaps(part);
        this.mParts.add(index, part);
    }

    public PduPart removePart(int index) {
        return this.mParts.remove(index);
    }

    public void removeAll() {
        this.mParts.clear();
    }

    public PduPart getPart(int index) {
        return this.mParts.get(index);
    }

    public int getPartIndex(PduPart part) {
        return this.mParts.indexOf(part);
    }

    public int getPartsNum() {
        return this.mParts.size();
    }

    public PduPart getPartByContentId(String cid) {
        return this.mPartMapByContentId.get(cid);
    }

    public PduPart getPartByContentLocation(String contentLocation) {
        return this.mPartMapByContentLocation.get(contentLocation);
    }

    public PduPart getPartByName(String name) {
        return this.mPartMapByName.get(name);
    }

    public PduPart getPartByFileName(String filename) {
        return this.mPartMapByFileName.get(filename);
    }
}
