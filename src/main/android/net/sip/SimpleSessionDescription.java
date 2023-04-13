package android.net.sip;

import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes.dex */
public class SimpleSessionDescription {
    private final Fields mFields;
    private final ArrayList<Media> mMedia;

    public SimpleSessionDescription(long sessionId, String address) {
        Fields fields = new Fields("voscbtka");
        this.mFields = fields;
        this.mMedia = new ArrayList<>();
        String address2 = (address.indexOf(58) < 0 ? "IN IP4 " : "IN IP6 ") + address;
        fields.parse("v=0");
        fields.parse(String.format(Locale.US, "o=- %d %d %s", Long.valueOf(sessionId), Long.valueOf(System.currentTimeMillis()), address2));
        fields.parse("s=-");
        fields.parse("t=0 0");
        fields.parse("c=" + address2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public SimpleSessionDescription(String message) {
        String[] split;
        this.mFields = new Fields("voscbtka");
        this.mMedia = new ArrayList<>();
        String[] lines = message.trim().replaceAll(" +", Separators.f12SP).split("[\r\n]+");
        Fields fields = this.mFields;
        Fields fields2 = fields;
        for (String line : lines) {
            try {
                if (line.charAt(1) != '=') {
                    throw new IllegalArgumentException();
                }
                if (line.charAt(0) == 'm') {
                    String[] parts = line.substring(2).split(Separators.f12SP, 4);
                    String[] ports = parts[1].split(Separators.SLASH, 2);
                    Media media = newMedia(parts[0], Integer.parseInt(ports[0]), ports.length >= 2 ? Integer.parseInt(ports[1]) : 1, parts[2]);
                    for (String format : parts[3].split(Separators.f12SP)) {
                        media.setFormat(format, null);
                    }
                    fields2 = media;
                } else {
                    fields2.parse(line);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid SDP: " + line);
            }
        }
    }

    public Media newMedia(String type, int port, int portCount, String protocol) {
        Media media = new Media(type, port, portCount, protocol);
        this.mMedia.add(media);
        return media;
    }

    public Media[] getMedia() {
        ArrayList<Media> arrayList = this.mMedia;
        return (Media[]) arrayList.toArray(new Media[arrayList.size()]);
    }

    public String encode() {
        StringBuilder buffer = new StringBuilder();
        this.mFields.write(buffer);
        Iterator<Media> it = this.mMedia.iterator();
        while (it.hasNext()) {
            Media media = it.next();
            media.write(buffer);
        }
        return buffer.toString();
    }

    public String getAddress() {
        return this.mFields.getAddress();
    }

    public void setAddress(String address) {
        this.mFields.setAddress(address);
    }

    public String getEncryptionMethod() {
        return this.mFields.getEncryptionMethod();
    }

    public String getEncryptionKey() {
        return this.mFields.getEncryptionKey();
    }

    public void setEncryption(String method, String key) {
        this.mFields.setEncryption(method, key);
    }

    public String[] getBandwidthTypes() {
        return this.mFields.getBandwidthTypes();
    }

    public int getBandwidth(String type) {
        return this.mFields.getBandwidth(type);
    }

    public void setBandwidth(String type, int value) {
        this.mFields.setBandwidth(type, value);
    }

    public String[] getAttributeNames() {
        return this.mFields.getAttributeNames();
    }

    public String getAttribute(String name) {
        return this.mFields.getAttribute(name);
    }

    public void setAttribute(String name, String value) {
        this.mFields.setAttribute(name, value);
    }

    /* loaded from: classes.dex */
    public static class Media extends Fields {
        private ArrayList<String> mFormats;
        private final int mPort;
        private final int mPortCount;
        private final String mProtocol;
        private final String mType;

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String getAddress() {
            return super.getAddress();
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String getAttribute(String str) {
            return super.getAttribute(str);
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String[] getAttributeNames() {
            return super.getAttributeNames();
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ int getBandwidth(String str) {
            return super.getBandwidth(str);
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String[] getBandwidthTypes() {
            return super.getBandwidthTypes();
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String getEncryptionKey() {
            return super.getEncryptionKey();
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ String getEncryptionMethod() {
            return super.getEncryptionMethod();
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ void setAddress(String str) {
            super.setAddress(str);
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ void setAttribute(String str, String str2) {
            super.setAttribute(str, str2);
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ void setBandwidth(String str, int i) {
            super.setBandwidth(str, i);
        }

        @Override // android.net.sip.SimpleSessionDescription.Fields
        public /* bridge */ /* synthetic */ void setEncryption(String str, String str2) {
            super.setEncryption(str, str2);
        }

        private Media(String type, int port, int portCount, String protocol) {
            super("icbka");
            this.mFormats = new ArrayList<>();
            this.mType = type;
            this.mPort = port;
            this.mPortCount = portCount;
            this.mProtocol = protocol;
        }

        public String getType() {
            return this.mType;
        }

        public int getPort() {
            return this.mPort;
        }

        public int getPortCount() {
            return this.mPortCount;
        }

        public String getProtocol() {
            return this.mProtocol;
        }

        public String[] getFormats() {
            ArrayList<String> arrayList = this.mFormats;
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }

        public String getFmtp(String format) {
            return get("a=fmtp:" + format, ' ');
        }

        public void setFormat(String format, String fmtp) {
            this.mFormats.remove(format);
            this.mFormats.add(format);
            set("a=rtpmap:" + format, ' ', null);
            set("a=fmtp:" + format, ' ', fmtp);
        }

        public void removeFormat(String format) {
            this.mFormats.remove(format);
            set("a=rtpmap:" + format, ' ', null);
            set("a=fmtp:" + format, ' ', null);
        }

        public int[] getRtpPayloadTypes() {
            int[] types = new int[this.mFormats.size()];
            int length = 0;
            Iterator<String> it = this.mFormats.iterator();
            while (it.hasNext()) {
                String format = it.next();
                try {
                    types[length] = Integer.parseInt(format);
                    length++;
                } catch (NumberFormatException e) {
                }
            }
            return Arrays.copyOf(types, length);
        }

        public String getRtpmap(int type) {
            return get("a=rtpmap:" + type, ' ');
        }

        public String getFmtp(int type) {
            return get("a=fmtp:" + type, ' ');
        }

        public void setRtpPayload(int type, String rtpmap, String fmtp) {
            String format = String.valueOf(type);
            this.mFormats.remove(format);
            this.mFormats.add(format);
            set("a=rtpmap:" + format, ' ', rtpmap);
            set("a=fmtp:" + format, ' ', fmtp);
        }

        public void removeRtpPayload(int type) {
            removeFormat(String.valueOf(type));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void write(StringBuilder buffer) {
            buffer.append("m=").append(this.mType).append(' ').append(this.mPort);
            if (this.mPortCount != 1) {
                buffer.append('/').append(this.mPortCount);
            }
            buffer.append(' ').append(this.mProtocol);
            Iterator<String> it = this.mFormats.iterator();
            while (it.hasNext()) {
                String format = it.next();
                buffer.append(' ').append(format);
            }
            buffer.append(Separators.NEWLINE);
            write(buffer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Fields {
        private final ArrayList<String> mLines = new ArrayList<>();
        private final String mOrder;

        Fields(String order) {
            this.mOrder = order;
        }

        public String getAddress() {
            String address = get("c", '=');
            if (address == null) {
                return null;
            }
            String[] parts = address.split(Separators.f12SP);
            if (parts.length != 3) {
                return null;
            }
            int slash = parts[2].indexOf(47);
            return slash < 0 ? parts[2] : parts[2].substring(0, slash);
        }

        public void setAddress(String address) {
            if (address != null) {
                address = (address.indexOf(58) < 0 ? "IN IP4 " : "IN IP6 ") + address;
            }
            set("c", '=', address);
        }

        public String getEncryptionMethod() {
            String encryption = get("k", '=');
            if (encryption == null) {
                return null;
            }
            int colon = encryption.indexOf(58);
            return colon == -1 ? encryption : encryption.substring(0, colon);
        }

        public String getEncryptionKey() {
            int colon;
            String encryption = get("k", '=');
            if (encryption == null || (colon = encryption.indexOf(58)) == -1) {
                return null;
            }
            return encryption.substring(0, colon + 1);
        }

        public void setEncryption(String method, String key) {
            set("k", '=', (method == null || key == null) ? method : method + ':' + key);
        }

        public String[] getBandwidthTypes() {
            return cut("b=", ':');
        }

        public int getBandwidth(String type) {
            String value = get("b=" + type, ':');
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    setBandwidth(type, -1);
                }
            }
            return -1;
        }

        public void setBandwidth(String type, int value) {
            set("b=" + type, ':', value < 0 ? null : String.valueOf(value));
        }

        public String[] getAttributeNames() {
            return cut("a=", ':');
        }

        public String getAttribute(String name) {
            return get("a=" + name, ':');
        }

        public void setAttribute(String name, String value) {
            set("a=" + name, ':', value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void write(StringBuilder buffer) {
            for (int i = 0; i < this.mOrder.length(); i++) {
                char type = this.mOrder.charAt(i);
                Iterator<String> it = this.mLines.iterator();
                while (it.hasNext()) {
                    String line = it.next();
                    if (line.charAt(0) == type) {
                        buffer.append(line).append(Separators.NEWLINE);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void parse(String line) {
            char type = line.charAt(0);
            if (this.mOrder.indexOf(type) == -1) {
                return;
            }
            char delimiter = '=';
            if (line.startsWith("a=rtpmap:") || line.startsWith("a=fmtp:")) {
                delimiter = ' ';
            } else if (type == 'b' || type == 'a') {
                delimiter = ':';
            }
            int i = line.indexOf(delimiter);
            if (i == -1) {
                set(line, delimiter, "");
            } else {
                set(line.substring(0, i), delimiter, line.substring(i + 1));
            }
        }

        private String[] cut(String prefix, char delimiter) {
            String[] names = new String[this.mLines.size()];
            int length = 0;
            Iterator<String> it = this.mLines.iterator();
            while (it.hasNext()) {
                String line = it.next();
                if (line.startsWith(prefix)) {
                    int i = line.indexOf(delimiter);
                    if (i == -1) {
                        i = line.length();
                    }
                    names[length] = line.substring(prefix.length(), i);
                    length++;
                }
            }
            return (String[]) Arrays.copyOf(names, length);
        }

        private int find(String key, char delimiter) {
            int length = key.length();
            for (int i = this.mLines.size() - 1; i >= 0; i--) {
                String line = this.mLines.get(i);
                if (line.startsWith(key) && (line.length() == length || line.charAt(length) == delimiter)) {
                    return i;
                }
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void set(String key, char delimiter, String value) {
            int index = find(key, delimiter);
            if (value != null) {
                if (value.length() != 0) {
                    key = key + delimiter + value;
                }
                if (index == -1) {
                    this.mLines.add(key);
                } else {
                    this.mLines.set(index, key);
                }
            } else if (index != -1) {
                this.mLines.remove(index);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String get(String key, char delimiter) {
            int index = find(key, delimiter);
            if (index == -1) {
                return null;
            }
            String line = this.mLines.get(index);
            int length = key.length();
            return line.length() == length ? "" : line.substring(length + 1);
        }
    }
}
