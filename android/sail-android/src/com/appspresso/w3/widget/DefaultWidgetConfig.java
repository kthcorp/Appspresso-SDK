package com.appspresso.w3.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.appspresso.w3.Feature;
import com.appspresso.w3.Storage;
import com.appspresso.w3.Widget;

// TODO Localizable string : author, version, short name, name, description
public class DefaultWidgetConfig implements Widget {

    // attribute
    private String author;
    private String authorEmail;
    private String authorHref;
    private String description;

    private String id;
    private String defaultlocale;
    private String name;
    private String shortName;

    private String version;

    private long height;
    private long width;

    private Preferences preferences;

    private List<Feature> features;

    // FIXME default start file. http://www.w3.org/TR/widgets/#step-8
    private String contentSrc = "index.html";
    private String contentType;
    private String contentEncoding;

    DefaultWidgetConfig(Activity activity) {
        preferences = new Preferences(activity, this);
        features = new ArrayList<Feature>();
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getAuthorEmail() {
        return this.authorEmail;
    }

    @Override
    public String getAuthorHref() {
        return this.authorHref;
    }

    @Override
    public long getWidth() {
        return this.width;
    }

    @Override
    public long getHeight() {
        return this.height;
    }

    @Override
    public Storage getPreferences() {
        return preferences;
    }

    void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    void setAuthorHref(String authorHref) {
        this.authorHref = authorHref;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setId(String id) {
        this.id = id;
    }

    void setName(String name) {
        this.name = name;
    }

    void setShortName(String shortName) {
        this.shortName = shortName;
    }

    void setVersion(String version) {
        this.version = version;

        preferences.writeApplicationVersion(version);
    }

    void setHeight(long height) {
        this.height = height;
    }

    void setWidth(long width) {
        this.width = width;
    }

    void addPreference(String name, String value, boolean readonly) {
        this.preferences.setItem(name, value, readonly);
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    void addFeatues(Feature feature) {
        this.features.add(feature);
    }

    void setContent(String src, String type, String encoding) {
        this.contentSrc = src;
        this.contentType = type;
        this.contentEncoding = encoding;
    }

    void setDefaultLocale(String defaultlocale) {
        this.defaultlocale = defaultlocale;
    }

    String getContentType() {
        return contentType;
    }

    String getContentEncoding() {
        return contentEncoding;
    }

    public String getDefaultLocale() {
        return this.defaultlocale;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public String getContentSrc() {
        return this.contentSrc;
    }

}
