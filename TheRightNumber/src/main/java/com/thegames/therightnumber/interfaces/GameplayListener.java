package com.thegames.therightnumber.interfaces;

import com.thegames.therightnumber.model.Question;
import com.thegames.therightnumber.uigame.GameOver;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public interface GameplayListener {

    void onShowNextQuestion(Question question);
    void onGoodAnswer();
    void onBadAnswer();
    void onRequestRangeJoker(String minRange, String maxRange);
    void onEmptyHistory();
    void onUpdateHistory(String firstRecord, String secondRecord);
    void onSecondWrongAnswer();
    void onAskForFriendsHelp(boolean withDelay);
    void onRequestShareFoundAnswer(Question question);
    void onUpdateLivesScore(int score);
    void onUpdateLevel(int level);
    void onGameOver(GameOver gameOverReason);
    void onShareAllLevelsDone(String text);
    void onClickVisitFacebookPage();
    void onClickVisitTwitterPage();
}
