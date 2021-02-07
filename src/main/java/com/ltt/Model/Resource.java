package com.ltt.Model;

public class Resource {
    private String mimetype;
    private String cache_url;
    private String hash;
    private String description;
    private String name;
    private String format;
    private String url;
    private String cache_last_updated;
    private String package_id;
    private String created;
    private String state;
    private String mimetype_inner;
    private String last_modified;
    private String position;
    private String revision_id;
    private String url_type;
    private String resource_type;
    private String downloaded;
    private String data_source;

//    public Resource() {
//    }

    public Resource(String mimetype, String cache_url, String hash, String description, String name, String format, String url, String cache_last_updated, String package_id, String created, String state, String mimetype_inner, String last_modified, String position, String revision_id, String url_type, String resource_type, String downloaded, String data_source) {
        this.mimetype = mimetype;
        this.cache_url = cache_url;
        this.hash = hash;
        this.description = description;
        this.name = name;
        this.format = format;
        this.url = url;
        this.cache_last_updated = cache_last_updated;
        this.package_id = package_id;
        this.created = created;
        this.state = state;
        this.mimetype_inner = mimetype_inner;
        this.last_modified = last_modified;
        this.position = position;
        this.revision_id = revision_id;
        this.url_type = url_type;
        this.resource_type = resource_type;
        this.downloaded = downloaded;
        this.data_source = data_source;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getCache_url() {
        return cache_url;
    }

    public void setCache_url(String cache_url) {
        this.cache_url = cache_url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCache_last_updated() {
        return cache_last_updated;
    }

    public void setCache_last_updated(String cache_last_updated) {
        this.cache_last_updated = cache_last_updated;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMimetype_inner() {
        return mimetype_inner;
    }

    public void setMimetype_inner(String mimetype_inner) {
        this.mimetype_inner = mimetype_inner;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    public String getUrl_type() {
        return url_type;
    }

    public void setUrl_type(String url_type) {
        this.url_type = url_type;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }
}
