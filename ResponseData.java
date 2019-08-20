package com.quantrium.verifydoc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseData {

    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("doc_id")
    @Expose
    private String docId;
    @SerializedName("doc_type")
    @Expose
    private String docType;
    @SerializedName("father")
    @Expose
    private String father;
    @SerializedName("issue_date")
    @Expose
    private String issueDate;
    @SerializedName("name")
    @Expose
    private String name;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}