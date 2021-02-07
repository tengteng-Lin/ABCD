package com.ltt.Model;

public class Metadata {
    private String local_id;
    private String title;
    private String notes;
    private String name;
    private String created_time;
    private String modified_time;
    private String author;
    private String author_email;
    private String maintainer;
    private String maintainer_email;
    private String org_title;
    private String license;
    private String state;
    private String version;
    private String creator_user_id;
    private String license_id;
    private String revision_id;
    private String org_description;
    private String org_created;
    private String org_state;
    private String org_image_url;
    private String org_revision_id;
    private String org_approval_status;
    private String num_tags;



//    public Metadata(String local_id,String title, String notes, String name, String created_time, String modified_time, String author, String author_email, String maintainer, String maintainer_email, String org_title, String license, String state, String url) {
//        this.local_id = local_id;
//        this.title = title;
//        this.notes = notes;
//        this.name = name;
//        this.created_time = created_time;
//        this.modified_time = modified_time;
//        this.author = author;
//        this.author_email = author_email;
//        this.maintainer = maintainer;
//        this.maintainer_email = maintainer_email;
//        this.org_title = org_title;
//        this.license = license;
//        this.state = state;
//        this.url = url;
//    }


    public Metadata() {
    }

    public Metadata(String title, String notes, String name, String created_time, String modified_time, String author, String author_email, String maintainer, String maintainer_email, String org_title, String license, String state, String version, String creator_user_id, String license_id, String revision_id, String org_description, String org_created, String org_state, String org_image_url, String org_revision_id, String org_approval_status, String num_tags) {
        this.title = title;
        this.notes = notes;
        this.name = name;
        this.created_time = created_time;
        this.modified_time = modified_time;
        this.author = author;
        this.author_email = author_email;
        this.maintainer = maintainer;
        this.maintainer_email = maintainer_email;
        this.org_title = org_title;
        this.license = license;
        this.state = state;
        this.version = version;
        this.creator_user_id = creator_user_id;
        this.license_id = license_id;
        this.revision_id = revision_id;
        this.org_description = org_description;
        this.org_created = org_created;
        this.org_state = org_state;
        this.org_image_url = org_image_url;
        this.org_revision_id = org_revision_id;
        this.org_approval_status = org_approval_status;
        this.num_tags = num_tags;
    }

    public Metadata(String local_id, String title, String notes, String name, String created_time, String modified_time, String author, String author_email, String maintainer, String maintainer_email, String org_title, String license, String state, String version, String creator_user_id, String license_id, String revision_id, String org_description, String org_created, String org_state, String org_image_url, String org_revision_id, String org_approval_status, String num_tags) {
        this.local_id = local_id;
        this.title = title;
        this.notes = notes;
        this.name = name;
        this.created_time = created_time;
        this.modified_time = modified_time;
        this.author = author;
        this.author_email = author_email;
        this.maintainer = maintainer;
        this.maintainer_email = maintainer_email;
        this.org_title = org_title;
        this.license = license;
        this.state = state;
        this.version = version;
        this.creator_user_id = creator_user_id;
        this.license_id = license_id;
        this.revision_id = revision_id;
        this.org_description = org_description;
        this.org_created = org_created;
        this.org_state = org_state;
        this.org_image_url = org_image_url;
        this.org_revision_id = org_revision_id;
        this.org_approval_status = org_approval_status;
        this.num_tags = num_tags;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getModified_time() {
        return modified_time;
    }

    public void setModified_time(String modified_time) {
        this.modified_time = modified_time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_email() {
        return author_email;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getMaintainer_email() {
        return maintainer_email;
    }

    public void setMaintainer_email(String maintainer_email) {
        this.maintainer_email = maintainer_email;
    }

    public String getOrg_title() {
        return org_title;
    }

    public void setOrg_title(String org_title) {
        this.org_title = org_title;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreator_user_id() {
        return creator_user_id;
    }

    public void setCreator_user_id(String creator_user_id) {
        this.creator_user_id = creator_user_id;
    }

    public String getLicense_id() {
        return license_id;
    }

    public void setLicense_id(String license_id) {
        this.license_id = license_id;
    }

    public String getRevision_id() {
        return revision_id;
    }

    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    public String getOrg_description() {
        return org_description;
    }

    public void setOrg_description(String org_description) {
        this.org_description = org_description;
    }

    public String getOrg_created() {
        return org_created;
    }

    public void setOrg_created(String org_created) {
        this.org_created = org_created;
    }

    public String getOrg_state() {
        return org_state;
    }

    public void setOrg_state(String org_state) {
        this.org_state = org_state;
    }

    public String getOrg_image_url() {
        return org_image_url;
    }

    public void setOrg_image_url(String org_image_url) {
        this.org_image_url = org_image_url;
    }

    public String getOrg_revision_id() {
        return org_revision_id;
    }

    public void setOrg_revision_id(String org_revision_id) {
        this.org_revision_id = org_revision_id;
    }

    public String getOrg_approval_status() {
        return org_approval_status;
    }

    public void setOrg_approval_status(String org_approval_status) {
        this.org_approval_status = org_approval_status;
    }

    public String getNum_tags() {
        return num_tags;
    }

    public void setNum_tags(String num_tags) {
        this.num_tags = num_tags;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String toString(){
        return "title:"+this.title+",maintainer:"+this.maintainer;
    }
}


