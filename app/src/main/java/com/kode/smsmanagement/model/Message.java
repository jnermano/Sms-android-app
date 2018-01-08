package com.kode.smsmanagement.model;

import com.orm.SugarRecord;

import java.util.Locale;

/**
 * Created by Ermano
 * on 1/7/2018.
 */

public class Message extends SugarRecord {

    private String sender;
    private String receiver;
    private String message;
    private String operator;
    private String datecreated;
    private int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return String.format(Locale.FRENCH, "sender : %s\nreceiver : %s\ndate : %s\noperator : %s\nMsg : %s", sender, receiver, datecreated,operator, message);
    }
}
