package com.jcod3r.entity;

import java.util.Date;

public class CookieEntity {
    private String refererUrl;
    private String cookie;
    private Date createTime;

    public String getRefererUrl() {
        return this.refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getCookie() {
        return this.cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}