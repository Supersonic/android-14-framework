package com.google.android.util;

import android.app.backup.FullBackup;
import android.app.blob.XmlTags;
import android.provider.Telephony;
import android.view.ThreadedRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes5.dex */
public abstract class AbstractMessageParser {
    public static final String musicNote = "♫ ";
    private HashMap<Character, Format> formatStart;
    private int nextChar;
    private int nextClass;
    private boolean parseAcronyms;
    private boolean parseFormatting;
    private boolean parseMeText;
    private boolean parseMusic;
    private boolean parseSmilies;
    private boolean parseUrls;
    private ArrayList<Part> parts;
    private String text;
    private ArrayList<Token> tokens;

    /* loaded from: classes5.dex */
    public interface Resources {
        TrieNode getAcronyms();

        TrieNode getDomainSuffixes();

        Set<String> getSchemes();

        TrieNode getSmileys();
    }

    protected abstract Resources getResources();

    public AbstractMessageParser(String text) {
        this(text, true, true, true, true, true, true);
    }

    public AbstractMessageParser(String text, boolean parseSmilies, boolean parseAcronyms, boolean parseFormatting, boolean parseUrls, boolean parseMusic, boolean parseMeText) {
        this.text = text;
        this.nextChar = 0;
        this.nextClass = 10;
        this.parts = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.formatStart = new HashMap<>();
        this.parseSmilies = parseSmilies;
        this.parseAcronyms = parseAcronyms;
        this.parseFormatting = parseFormatting;
        this.parseUrls = parseUrls;
        this.parseMusic = parseMusic;
        this.parseMeText = parseMeText;
    }

    public final String getRawText() {
        return this.text;
    }

    public final int getPartCount() {
        return this.parts.size();
    }

    public final Part getPart(int index) {
        return this.parts.get(index);
    }

    public final List<Part> getParts() {
        return this.parts;
    }

    public void parse() {
        if (parseMusicTrack()) {
            buildParts(null);
            return;
        }
        String meText = null;
        if (this.parseMeText && this.text.startsWith("/me") && this.text.length() > 3 && Character.isWhitespace(this.text.charAt(3))) {
            meText = this.text.substring(0, 4);
            this.text = this.text.substring(4);
        }
        boolean wasSmiley = false;
        while (this.nextChar < this.text.length()) {
            if (!isWordBreak(this.nextChar) && (!wasSmiley || !isSmileyBreak(this.nextChar))) {
                throw new AssertionError("last chunk did not end at word break");
            }
            if (parseSmiley()) {
                wasSmiley = true;
            } else {
                wasSmiley = false;
                if (!parseAcronym() && !parseURL() && !parseFormatting()) {
                    parseText();
                }
            }
        }
        for (int i = 0; i < this.tokens.size(); i++) {
            if (this.tokens.get(i).isMedia()) {
                if (i > 0 && (this.tokens.get(i - 1) instanceof Html)) {
                    ((Html) this.tokens.get(i - 1)).trimLeadingWhitespace();
                }
                if (i + 1 < this.tokens.size() && (this.tokens.get(i + 1) instanceof Html)) {
                    ((Html) this.tokens.get(i + 1)).trimTrailingWhitespace();
                }
            }
        }
        int i2 = 0;
        while (i2 < this.tokens.size()) {
            if (this.tokens.get(i2).isHtml() && this.tokens.get(i2).toHtml(true).length() == 0) {
                this.tokens.remove(i2);
                i2--;
            }
            i2++;
        }
        buildParts(meText);
    }

    public static Token tokenForUrl(String url, String text) {
        if (url == null) {
            return null;
        }
        Video video = Video.matchURL(url, text);
        if (video != null) {
            return video;
        }
        YouTubeVideo ytVideo = YouTubeVideo.matchURL(url, text);
        if (ytVideo != null) {
            return ytVideo;
        }
        Photo photo = Photo.matchURL(url, text);
        if (photo != null) {
            return photo;
        }
        FlickrPhoto flickrPhoto = FlickrPhoto.matchURL(url, text);
        if (flickrPhoto != null) {
            return flickrPhoto;
        }
        return new Link(url, text);
    }

    private void buildParts(String meText) {
        for (int i = 0; i < this.tokens.size(); i++) {
            Token token = this.tokens.get(i);
            if (token.isMedia() || this.parts.size() == 0 || lastPart().isMedia()) {
                this.parts.add(new Part());
            }
            lastPart().add(token);
        }
        if (this.parts.size() > 0) {
            this.parts.get(0).setMeText(meText);
        }
    }

    private Part lastPart() {
        ArrayList<Part> arrayList = this.parts;
        return arrayList.get(arrayList.size() - 1);
    }

    private boolean parseMusicTrack() {
        if (this.parseMusic && this.text.startsWith(musicNote)) {
            addToken(new MusicTrack(this.text.substring(musicNote.length())));
            this.nextChar = this.text.length();
            return true;
        }
        return false;
    }

    private void parseText() {
        StringBuilder buf = new StringBuilder();
        int start = this.nextChar;
        do {
            String str = this.text;
            int i = this.nextChar;
            this.nextChar = i + 1;
            char ch = str.charAt(i);
            switch (ch) {
                case '\n':
                    buf.append("<br>");
                    break;
                case '\"':
                    buf.append("&quot;");
                    break;
                case '&':
                    buf.append("&amp;");
                    break;
                case '\'':
                    buf.append("&apos;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        } while (!isWordBreak(this.nextChar));
        addToken(new Html(this.text.substring(start, this.nextChar), buf.toString()));
    }

    private boolean parseSmiley() {
        TrieNode match;
        if (this.parseSmilies && (match = longestMatch(getResources().getSmileys(), this, this.nextChar, true)) != null) {
            int previousCharClass = getCharClass(this.nextChar - 1);
            int nextCharClass = getCharClass(this.nextChar + match.getText().length());
            if ((previousCharClass == 2 || previousCharClass == 3) && (nextCharClass == 2 || nextCharClass == 3)) {
                return false;
            }
            addToken(new Smiley(match.getText()));
            this.nextChar += match.getText().length();
            return true;
        }
        return false;
    }

    private boolean parseAcronym() {
        TrieNode match;
        if (this.parseAcronyms && (match = longestMatch(getResources().getAcronyms(), this, this.nextChar)) != null) {
            addToken(new Acronym(match.getText(), match.getValue()));
            this.nextChar += match.getText().length();
            return true;
        }
        return false;
    }

    private boolean isDomainChar(char c) {
        return c == '-' || Character.isLetter(c) || Character.isDigit(c);
    }

    private boolean isValidDomain(String domain) {
        if (matches(getResources().getDomainSuffixes(), reverse(domain))) {
            return true;
        }
        return false;
    }

    private boolean parseURL() {
        char ch;
        if (this.parseUrls && isURLBreak(this.nextChar)) {
            int start = this.nextChar;
            int index = start;
            while (index < this.text.length() && isDomainChar(this.text.charAt(index))) {
                index++;
            }
            String url = "";
            boolean done = false;
            if (index == this.text.length()) {
                return false;
            }
            if (this.text.charAt(index) == ':') {
                String scheme = this.text.substring(this.nextChar, index);
                if (!getResources().getSchemes().contains(scheme)) {
                    return false;
                }
            } else if (this.text.charAt(index) != '.') {
                return false;
            } else {
                while (index < this.text.length() && ((ch = this.text.charAt(index)) == '.' || isDomainChar(ch))) {
                    index++;
                }
                String domain = this.text.substring(this.nextChar, index);
                if (!isValidDomain(domain)) {
                    return false;
                }
                if (index + 1 < this.text.length() && this.text.charAt(index) == ':' && Character.isDigit(this.text.charAt(index + 1))) {
                    while (true) {
                        index++;
                        if (index >= this.text.length() || !Character.isDigit(this.text.charAt(index))) {
                            break;
                        }
                    }
                }
                if (index == this.text.length()) {
                    done = true;
                } else {
                    char ch2 = this.text.charAt(index);
                    if (ch2 == '?') {
                        if (index + 1 == this.text.length()) {
                            done = true;
                        } else {
                            char ch22 = this.text.charAt(index + 1);
                            if (Character.isWhitespace(ch22) || isPunctuation(ch22)) {
                                done = true;
                            }
                        }
                    } else if (isPunctuation(ch2)) {
                        done = true;
                    } else if (Character.isWhitespace(ch2)) {
                        done = true;
                    } else if (ch2 != '/' && ch2 != '#') {
                        return false;
                    }
                }
                url = "http://";
            }
            if (!done) {
                while (index < this.text.length() && !Character.isWhitespace(this.text.charAt(index))) {
                    index++;
                }
            }
            String urlText = this.text.substring(start, index);
            addURLToken(url + urlText, urlText);
            this.nextChar = index;
            return true;
        }
        return false;
    }

    private void addURLToken(String url, String text) {
        addToken(tokenForUrl(url, text));
    }

    private boolean parseFormatting() {
        if (this.parseFormatting) {
            int endChar = this.nextChar;
            while (endChar < this.text.length() && isFormatChar(this.text.charAt(endChar))) {
                endChar++;
            }
            if (endChar == this.nextChar || !isWordBreak(endChar)) {
                return false;
            }
            LinkedHashMap<Character, Boolean> seenCharacters = new LinkedHashMap<>();
            for (int index = this.nextChar; index < endChar; index++) {
                char ch = this.text.charAt(index);
                Character key = Character.valueOf(ch);
                if (seenCharacters.containsKey(key)) {
                    addToken(new Format(ch, false));
                } else {
                    Format start = this.formatStart.get(key);
                    if (start != null) {
                        start.setMatched(true);
                        this.formatStart.remove(key);
                        seenCharacters.put(key, Boolean.TRUE);
                    } else {
                        Format start2 = new Format(ch, true);
                        this.formatStart.put(key, start2);
                        addToken(start2);
                        seenCharacters.put(key, Boolean.FALSE);
                    }
                }
            }
            for (Character key2 : seenCharacters.keySet()) {
                if (seenCharacters.get(key2) == Boolean.TRUE) {
                    Format end = new Format(key2.charValue(), false);
                    end.setMatched(true);
                    addToken(end);
                }
            }
            this.nextChar = endChar;
            return true;
        }
        return false;
    }

    private boolean isWordBreak(int index) {
        return getCharClass(index + (-1)) != getCharClass(index);
    }

    private boolean isSmileyBreak(int index) {
        if (index > 0 && index < this.text.length() && isSmileyBreak(this.text.charAt(index - 1), this.text.charAt(index))) {
            return true;
        }
        return false;
    }

    private boolean isURLBreak(int index) {
        switch (getCharClass(index - 1)) {
            case 2:
            case 3:
            case 4:
                return false;
            default:
                return true;
        }
    }

    private int getCharClass(int index) {
        if (index < 0 || this.text.length() <= index) {
            return 0;
        }
        char ch = this.text.charAt(index);
        if (Character.isWhitespace(ch)) {
            return 1;
        }
        if (Character.isLetter(ch)) {
            return 2;
        }
        if (Character.isDigit(ch)) {
            return 3;
        }
        if (isPunctuation(ch)) {
            int i = this.nextClass + 1;
            this.nextClass = i;
            return i;
        }
        return 4;
    }

    private static boolean isSmileyBreak(char c1, char c2) {
        switch (c1) {
            case '$':
            case '&':
            case '*':
            case '+':
            case '-':
            case '/':
            case '<':
            case '=':
            case '>':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '|':
            case '}':
            case '~':
                switch (c2) {
                    case '#':
                    case '$':
                    case '%':
                    case '*':
                    case '/':
                    case '<':
                    case '=':
                    case '>':
                    case '@':
                    case '[':
                    case '\\':
                    case '^':
                    case '~':
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    private static boolean isPunctuation(char ch) {
        switch (ch) {
            case '!':
            case '\"':
            case '(':
            case ')':
            case ',':
            case '.':
            case ':':
            case ';':
            case '?':
                return true;
            default:
                return false;
        }
    }

    private static boolean isFormatChar(char ch) {
        switch (ch) {
            case '*':
            case '^':
            case '_':
                return true;
            default:
                return false;
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Token {
        protected String text;
        protected Type type;

        public abstract boolean isHtml();

        /* loaded from: classes5.dex */
        public enum Type {
            HTML("html"),
            FORMAT(Telephony.CellBroadcasts.MESSAGE_FORMAT),
            LINK(XmlTags.TAG_LEASEE),
            SMILEY("e"),
            ACRONYM(FullBackup.APK_TREE_TOKEN),
            MUSIC("m"),
            GOOGLE_VIDEO("v"),
            YOUTUBE_VIDEO("yt"),
            PHOTO("p"),
            FLICKR(FullBackup.FILES_TREE_TOKEN);
            
            private String stringRep;

            Type(String stringRep) {
                this.stringRep = stringRep;
            }

            @Override // java.lang.Enum
            public String toString() {
                return this.stringRep;
            }
        }

        protected Token(Type type, String text) {
            this.type = type;
            this.text = text;
        }

        public Type getType() {
            return this.type;
        }

        public List<String> getInfo() {
            List<String> info = new ArrayList<>();
            info.add(getType().toString());
            return info;
        }

        public String getRawText() {
            return this.text;
        }

        public boolean isMedia() {
            return false;
        }

        public boolean isArray() {
            return !isHtml();
        }

        public String toHtml(boolean caps) {
            throw new AssertionError("not html");
        }

        public boolean controlCaps() {
            return false;
        }

        public boolean setCaps() {
            return false;
        }
    }

    /* loaded from: classes5.dex */
    public static class Html extends Token {
        private String html;

        public Html(String text, String html) {
            super(Token.Type.HTML, text);
            this.html = html;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return true;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public String toHtml(boolean caps) {
            String str = this.html;
            return caps ? str.toUpperCase() : str;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        public void trimLeadingWhitespace() {
            this.text = trimLeadingWhitespace(this.text);
            this.html = trimLeadingWhitespace(this.html);
        }

        public void trimTrailingWhitespace() {
            this.text = trimTrailingWhitespace(this.text);
            this.html = trimTrailingWhitespace(this.html);
        }

        private static String trimLeadingWhitespace(String text) {
            int index = 0;
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            return text.substring(index);
        }

        public static String trimTrailingWhitespace(String text) {
            int index = text.length();
            while (index > 0 && Character.isWhitespace(text.charAt(index - 1))) {
                index--;
            }
            return text.substring(0, index);
        }
    }

    /* loaded from: classes5.dex */
    public static class MusicTrack extends Token {
        private String track;

        public MusicTrack(String track) {
            super(Token.Type.MUSIC, track);
            this.track = track;
        }

        public String getTrack() {
            return this.track;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getTrack());
            return info;
        }
    }

    /* loaded from: classes5.dex */
    public static class Link extends Token {
        private String url;

        public Link(String url, String text) {
            super(Token.Type.LINK, text);
            this.url = url;
        }

        public String getURL() {
            return this.url;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getURL());
            info.add(getRawText());
            return info;
        }
    }

    /* loaded from: classes5.dex */
    public static class Video extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://video\\.google\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/videoplay\\?.*?\\bdocid=(-?\\d+).*");
        private String docid;

        public Video(String docid, String text) {
            super(Token.Type.GOOGLE_VIDEO, text);
            this.docid = docid;
        }

        public String getDocID() {
            return this.docid;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public static Video matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new Video(m.group(1), text);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String docid) {
            return "http://video.google.com/videofeed?type=docid&output=rss&sourceid=gtalk&docid=" + docid;
        }

        public static String getURL(String docid) {
            return getURL(docid, null);
        }

        public static String getURL(String docid, String extraParams) {
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                extraParams = extraParams + "&";
            }
            return "http://video.google.com/videoplay?" + extraParams + "docid=" + docid;
        }
    }

    /* loaded from: classes5.dex */
    public static class YouTubeVideo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://(?:[a-z0-9]+\\.)?youtube\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/watch\\?.*\\bv=([-_a-zA-Z0-9=]+).*");
        private String docid;

        public YouTubeVideo(String docid, String text) {
            super(Token.Type.YOUTUBE_VIDEO, text);
            this.docid = docid;
        }

        public String getDocID() {
            return this.docid;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public static YouTubeVideo matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new YouTubeVideo(m.group(1), text);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String docid) {
            return "http://youtube.com/watch?v=" + docid;
        }

        public static String getURL(String docid) {
            return getURL(docid, null);
        }

        public static String getURL(String docid, String extraParams) {
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                extraParams = extraParams + "&";
            }
            return "http://youtube.com/watch?" + extraParams + "v=" + docid;
        }

        public static String getPrefixedURL(boolean http, String prefix, String docid, String extraParams) {
            String protocol = "";
            if (http) {
                protocol = "http://";
            }
            if (prefix == null) {
                prefix = "";
            }
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                extraParams = extraParams + "&";
            }
            return protocol + prefix + "youtube.com/watch?" + extraParams + "v=" + docid;
        }
    }

    /* loaded from: classes5.dex */
    public static class Photo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("http://picasaweb.google.com/([^/?#&]+)/+((?!searchbrowse)[^/?#&]+)(?:/|/photo)?(?:\\?[^#]*)?(?:#(.*))?");
        private String album;
        private String photo;
        private String user;

        public Photo(String user, String album, String photo, String text) {
            super(Token.Type.PHOTO, text);
            this.user = user;
            this.album = album;
            this.photo = photo;
        }

        public String getUser() {
            return this.user;
        }

        public String getAlbum() {
            return this.album;
        }

        public String getPhoto() {
            return this.photo;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public static Photo matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new Photo(m.group(1), m.group(2), m.group(3), text);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(getUser()));
            info.add(getAlbumURL(getUser(), getAlbum()));
            if (getPhoto() != null) {
                info.add(getPhotoURL(getUser(), getAlbum(), getPhoto()));
            } else {
                info.add(null);
            }
            return info;
        }

        public static String getRssUrl(String user) {
            return "http://picasaweb.google.com/data/feed/api/user/" + user + "?category=album&alt=rss";
        }

        public static String getAlbumURL(String user, String album) {
            return "http://picasaweb.google.com/" + user + "/" + album;
        }

        public static String getPhotoURL(String user, String album, String photo) {
            return "http://picasaweb.google.com/" + user + "/" + album + "/photo#" + photo;
        }
    }

    /* loaded from: classes5.dex */
    public static class FlickrPhoto extends Token {
        private static final String SETS = "sets";
        private static final String TAGS = "tags";
        private String grouping;
        private String groupingId;
        private String photo;
        private String user;
        private static final Pattern URL_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/?([^/?#&]+)?/?.*");
        private static final Pattern GROUPING_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/(tags|sets)/([^/?#&]+)/?");

        public FlickrPhoto(String user, String photo, String grouping, String groupingId, String text) {
            super(Token.Type.FLICKR, text);
            if (!TAGS.equals(user)) {
                this.user = user;
                this.photo = ThreadedRenderer.OVERDRAW_PROPERTY_SHOW.equals(photo) ? null : photo;
                this.grouping = grouping;
                this.groupingId = groupingId;
                return;
            }
            this.user = null;
            this.photo = null;
            this.grouping = TAGS;
            this.groupingId = photo;
        }

        public String getUser() {
            return this.user;
        }

        public String getPhoto() {
            return this.photo;
        }

        public String getGrouping() {
            return this.grouping;
        }

        public String getGroupingId() {
            return this.groupingId;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public static FlickrPhoto matchURL(String url, String text) {
            Matcher m = GROUPING_PATTERN.matcher(url);
            if (m.matches()) {
                return new FlickrPhoto(m.group(1), null, m.group(2), m.group(3), text);
            }
            Matcher m2 = URL_PATTERN.matcher(url);
            if (m2.matches()) {
                return new FlickrPhoto(m2.group(1), m2.group(2), null, null, text);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getUrl());
            info.add(getUser() != null ? getUser() : "");
            info.add(getPhoto() != null ? getPhoto() : "");
            info.add(getGrouping() != null ? getGrouping() : "");
            info.add(getGroupingId() != null ? getGroupingId() : "");
            return info;
        }

        public String getUrl() {
            if (SETS.equals(this.grouping)) {
                return getUserSetsURL(this.user, this.groupingId);
            }
            if (TAGS.equals(this.grouping)) {
                String str = this.user;
                if (str != null) {
                    return getUserTagsURL(str, this.groupingId);
                }
                return getTagsURL(this.groupingId);
            }
            String str2 = this.photo;
            if (str2 != null) {
                return getPhotoURL(this.user, str2);
            }
            return getUserURL(this.user);
        }

        public static String getRssUrl(String user) {
            return null;
        }

        public static String getTagsURL(String tag) {
            return "http://flickr.com/photos/tags/" + tag;
        }

        public static String getUserURL(String user) {
            return "http://flickr.com/photos/" + user;
        }

        public static String getPhotoURL(String user, String photo) {
            return "http://flickr.com/photos/" + user + "/" + photo;
        }

        public static String getUserTagsURL(String user, String tagId) {
            return "http://flickr.com/photos/" + user + "/tags/" + tagId;
        }

        public static String getUserSetsURL(String user, String setId) {
            return "http://flickr.com/photos/" + user + "/sets/" + setId;
        }
    }

    /* loaded from: classes5.dex */
    public static class Smiley extends Token {
        public Smiley(String text) {
            super(Token.Type.SMILEY, text);
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            return info;
        }
    }

    /* loaded from: classes5.dex */
    public static class Acronym extends Token {
        private String value;

        public Acronym(String text, String value) {
            super(Token.Type.ACRONYM, text);
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            info.add(getValue());
            return info;
        }
    }

    /* loaded from: classes5.dex */
    public static class Format extends Token {

        /* renamed from: ch */
        private char f2229ch;
        private boolean matched;
        private boolean start;

        public Format(char ch, boolean start) {
            super(Token.Type.FORMAT, String.valueOf(ch));
            this.f2229ch = ch;
            this.start = start;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return true;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public String toHtml(boolean caps) {
            if (this.matched) {
                return this.start ? getFormatStart(this.f2229ch) : getFormatEnd(this.f2229ch);
            }
            char c = this.f2229ch;
            return c == '\"' ? "&quot;" : String.valueOf(c);
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean controlCaps() {
            return this.f2229ch == '^';
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean setCaps() {
            return this.start;
        }

        private String getFormatStart(char ch) {
            switch (ch) {
                case '\"':
                    return "<font color=\"#999999\">“";
                case '*':
                    return "<b>";
                case '^':
                    return "<b><font color=\"#005FFF\">";
                case '_':
                    return "<i>";
                default:
                    throw new AssertionError("unknown format '" + ch + "'");
            }
        }

        private String getFormatEnd(char ch) {
            switch (ch) {
                case '\"':
                    return "”</font>";
                case '*':
                    return "</b>";
                case '^':
                    return "</font></b>";
                case '_':
                    return "</i>";
                default:
                    throw new AssertionError("unknown format '" + ch + "'");
            }
        }
    }

    private void addToken(Token token) {
        this.tokens.add(token);
    }

    public String toHtml() {
        StringBuilder html = new StringBuilder();
        Iterator<Part> it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = it.next();
            boolean caps = false;
            html.append("<p>");
            Iterator<Token> it2 = part.getTokens().iterator();
            while (it2.hasNext()) {
                Token token = it2.next();
                if (token.isHtml()) {
                    html.append(token.toHtml(caps));
                } else {
                    switch (C45021.f2228x418776ea[token.getType().ordinal()]) {
                        case 1:
                            html.append("<a href=\"");
                            html.append(((Link) token).getURL());
                            html.append("\">");
                            html.append(token.getRawText());
                            html.append("</a>");
                            break;
                        case 2:
                            html.append(token.getRawText());
                            break;
                        case 3:
                            html.append(token.getRawText());
                            break;
                        case 4:
                            html.append(((MusicTrack) token).getTrack());
                            break;
                        case 5:
                            html.append("<a href=\"");
                            Video video = (Video) token;
                            html.append(Video.getURL(((Video) token).getDocID()));
                            html.append("\">");
                            html.append(token.getRawText());
                            html.append("</a>");
                            break;
                        case 6:
                            html.append("<a href=\"");
                            YouTubeVideo youTubeVideo = (YouTubeVideo) token;
                            html.append(YouTubeVideo.getURL(((YouTubeVideo) token).getDocID()));
                            html.append("\">");
                            html.append(token.getRawText());
                            html.append("</a>");
                            break;
                        case 7:
                            html.append("<a href=\"");
                            html.append(Photo.getAlbumURL(((Photo) token).getUser(), ((Photo) token).getAlbum()));
                            html.append("\">");
                            html.append(token.getRawText());
                            html.append("</a>");
                            break;
                        case 8:
                            Photo photo = (Photo) token;
                            html.append("<a href=\"");
                            html.append(((FlickrPhoto) token).getUrl());
                            html.append("\">");
                            html.append(token.getRawText());
                            html.append("</a>");
                            break;
                        default:
                            throw new AssertionError("unknown token type: " + token.getType());
                    }
                }
                if (token.controlCaps()) {
                    caps = token.setCaps();
                }
            }
            html.append("</p>\n");
        }
        return html.toString();
    }

    /* renamed from: com.google.android.util.AbstractMessageParser$1 */
    /* loaded from: classes5.dex */
    static /* synthetic */ class C45021 {

        /* renamed from: $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type */
        static final /* synthetic */ int[] f2228x418776ea;

        static {
            int[] iArr = new int[Token.Type.values().length];
            f2228x418776ea = iArr;
            try {
                iArr[Token.Type.LINK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f2228x418776ea[Token.Type.SMILEY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f2228x418776ea[Token.Type.ACRONYM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f2228x418776ea[Token.Type.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f2228x418776ea[Token.Type.GOOGLE_VIDEO.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f2228x418776ea[Token.Type.YOUTUBE_VIDEO.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f2228x418776ea[Token.Type.PHOTO.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f2228x418776ea[Token.Type.FLICKR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    protected static String reverse(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            buf.append(str.charAt(i));
        }
        return buf.toString();
    }

    /* loaded from: classes5.dex */
    public static class TrieNode {
        private final HashMap<Character, TrieNode> children;
        private String text;
        private String value;

        public TrieNode() {
            this("");
        }

        public TrieNode(String text) {
            this.children = new HashMap<>();
            this.text = text;
        }

        public final boolean exists() {
            return this.value != null;
        }

        public final String getText() {
            return this.text;
        }

        public final String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public TrieNode getChild(char ch) {
            return this.children.get(Character.valueOf(ch));
        }

        public TrieNode getOrCreateChild(char ch) {
            Character key = Character.valueOf(ch);
            TrieNode node = this.children.get(key);
            if (node == null) {
                TrieNode node2 = new TrieNode(this.text + String.valueOf(ch));
                this.children.put(key, node2);
                return node2;
            }
            return node;
        }

        public static void addToTrie(TrieNode root, String str, String value) {
            for (int index = 0; index < str.length(); index++) {
                root = root.getOrCreateChild(str.charAt(index));
            }
            root.setValue(value);
        }
    }

    private static boolean matches(TrieNode root, String str) {
        int index = 0;
        while (index < str.length()) {
            int index2 = index + 1;
            root = root.getChild(str.charAt(index));
            if (root != null) {
                if (!root.exists()) {
                    index = index2;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private static TrieNode longestMatch(TrieNode root, AbstractMessageParser p, int start) {
        return longestMatch(root, p, start, false);
    }

    private static TrieNode longestMatch(TrieNode root, AbstractMessageParser p, int start, boolean smiley) {
        int index = start;
        TrieNode bestMatch = null;
        while (index < p.getRawText().length()) {
            int index2 = index + 1;
            root = root.getChild(p.getRawText().charAt(index));
            if (root == null) {
                break;
            }
            if (root.exists()) {
                if (p.isWordBreak(index2)) {
                    bestMatch = root;
                    index = index2;
                } else if (smiley && p.isSmileyBreak(index2)) {
                    bestMatch = root;
                    index = index2;
                }
            }
            index = index2;
        }
        return bestMatch;
    }

    /* loaded from: classes5.dex */
    public static class Part {
        private String meText;
        private ArrayList<Token> tokens = new ArrayList<>();

        public String getType(boolean isSend) {
            return (isSend ? XmlTags.TAG_SESSION : "r") + getPartType();
        }

        private String getPartType() {
            if (isMedia()) {
                return XmlTags.ATTR_DESCRIPTION;
            }
            if (this.meText != null) {
                return "m";
            }
            return "";
        }

        public boolean isMedia() {
            return this.tokens.size() == 1 && this.tokens.get(0).isMedia();
        }

        public Token getMediaToken() {
            if (isMedia()) {
                return this.tokens.get(0);
            }
            return null;
        }

        public void add(Token token) {
            if (isMedia()) {
                throw new AssertionError("media ");
            }
            this.tokens.add(token);
        }

        public void setMeText(String meText) {
            this.meText = meText;
        }

        public String getRawText() {
            StringBuilder buf = new StringBuilder();
            String str = this.meText;
            if (str != null) {
                buf.append(str);
            }
            for (int i = 0; i < this.tokens.size(); i++) {
                buf.append(this.tokens.get(i).getRawText());
            }
            return buf.toString();
        }

        public ArrayList<Token> getTokens() {
            return this.tokens;
        }
    }
}
