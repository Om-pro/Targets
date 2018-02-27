package ru.ompro.targets.app;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dn on 18.07.2016.
 */
public class Target {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        this.mSuspect = suspect;
    }

    private String mSuspect;

    public Target() {
        this(UUID.randomUUID());
    }

    public Target(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }
}
