package com.thegames.therightnumber.model;

import android.content.Context;

/**
 * Created by gyljean-lambert on 20/03/2014.
 */
public class Question extends AbstractModel {

    private String question;
    private String answer;
    private int level;
    private String jokermini;
    private String jokermaxi;
    private String unit;
    private int format;

    private boolean played;
    private String userId;
    private int tries;

    public Question(Context ctx){
        super(ctx);
    }

    public Question(Context ctx, String question, String answer, int level, String jokermini,
                    String jokermaxi, String unit, int format, boolean played, String userId){
        super(ctx);
        this.question = question;
        this.answer = answer;
        this.level = level;
        this.jokermini = jokermini;
        this.jokermaxi = jokermaxi;
        this.unit = unit;
        this.format = format;
        this.played = played;
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String mQuestion) {
        this.question = mQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String mAnswer) {
        this.answer = mAnswer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int mLevel) {
        this.level = mLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String mUnit) {
        this.unit = mUnit;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int mFormat) {
        this.format = mFormat;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String mUserId) {
        this.userId = mUserId;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public String getJokermini() {
        return jokermini;
    }

    public void setJokermini(String jokermini) {
        this.jokermini = jokermini;
    }

    public String getJokermaxi() {
        return jokermaxi;
    }

    public void setJokermaxi(String jokermaxi) {
        this.jokermaxi = jokermaxi;
    }
}
