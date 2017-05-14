package com.example.hau.mynote;

import java.io.Serializable;

/**
 * Created by Hau on 2017/5/6.
 */

public class Note implements Serializable{

    private int mId;//便签ID
    private String mContent;//便签内容
    private int mInportant;//便签是否为星，0代表不为星，1代表为星
    private String mDateTime;

    public Note(int id, String content, int inportant,String dateTime) {
        mId = id;
        mContent = content;
        mInportant = inportant;
        mDateTime = dateTime;
    }

    public Note(int id, String content, int inportant) {
        mId = id;
        mContent = content;
        mInportant = inportant;
    }
    public Note( String content, int inportant) {

        mContent = content;
        mInportant = inportant;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getInportant() {
        return mInportant;
    }

    public void setInportant(int inportant) {
        mInportant = inportant;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }
}
