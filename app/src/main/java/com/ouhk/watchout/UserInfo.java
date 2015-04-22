package com.ouhk.watchout;

public class UserInfo {
	private String datetime;
	private String content;
	private String alerttime;
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String date) {
		this.datetime = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAlerttime() {
		return alerttime;
	}
	public void setAlerttime(String alerttime) {
		this.alerttime = alerttime;
	}
    public String getLogDateTime() {return datetime; }
    public void setLogDatetime(String date) {
        this.datetime = date;
    }
    public String getLogContent() {
        return content;
    }
    public void setLogContent(String content) {
        this.content = content;
    }


}
