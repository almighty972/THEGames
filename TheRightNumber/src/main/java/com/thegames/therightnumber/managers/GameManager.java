package com.thegames.therightnumber.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.helpers.FileHelper;
import com.thegames.therightnumber.helpers.QuestionsDBHelper;
import com.thegames.therightnumber.interfaces.GameplayListener;
import com.thegames.therightnumber.model.Question;
import com.thegames.therightnumber.popups.Popup;
import com.thegames.therightnumber.uigame.GameOver;
import com.thegames.therightnumber.uigame.Joker;
import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class GameManager {

    public static final String TAG = "GameManager";

    private static GameManager INSTANCE;

    private QuestionsDBHelper mQuestionDbHelper;
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;
    private WeakReference<GameplayListener> mWeakGameplayListener = new WeakReference<GameplayListener>(null);

    private List<Question> mQuestions;
    private List<String> mAnswersHistory = new ArrayList<String>();;
    private Question mCurrentQuestion;
    private int mScore = Constants.INITIAL_SCORE;


    private GameManager() {
        mQuestionDbHelper = new QuestionsDBHelper(Question.class);
    }

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
        getSharedPrefs();
        initUserScore();
    }

    public static GameManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GameManager();
        }
        return INSTANCE;
    }

    public void setGameplayListener(final GameplayListener listener) {
        if(listener != null) {
            mWeakGameplayListener = new WeakReference<GameplayListener>(listener);
        }
    }

    /*
     * SHARED PREFERENCES METHODS
     */

    public SharedPreferences getSharedPrefs() {
        if(mAppContext != null) {
           return mSharedPreferences = mAppContext.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return null;
    }

    public void saveIntPref(String pref, int value) {
        mSharedPreferences.edit().putInt(pref, value).commit();
    }

    public int getIntPref(String pref) {
        return mSharedPreferences.getInt(pref, -1);
    }

    public void saveLongPref(String pref, long value) {
        mSharedPreferences.edit().putLong(pref, value).commit();
    }

    public long getLongPref(String pref) {
        return mSharedPreferences.getLong(pref, -1);
    }


    /*
     * GAMEPLAY METHODS
     */


    public void processAnswer(String answer) {
        if(mCurrentQuestion != null &&
                !TextUtils.isEmpty(answer) &&
                !TextUtils.isEmpty(mCurrentQuestion.getAnswer())) {

            // check if the question has a percentage of tolerance for the good answer
//            if(mCurrentQuestion.getTolerance() > 0) {
//                boolean answerTolerated = isUserAnswerToloerated(answer);
//                if(answerTolerated) { // if answer is tolerated
//                    Log.i(TAG, "GOOD ANSWER WITH TOLERANCE (" +answer+ ")");
//                    triggerGoodAnswer();
//                } else {
//                    triggerBadAnswer(answer);
//                }
//            }
            if(answer.equals(mCurrentQuestion.getAnswer())) { // else check if the user gave the exact good answer
                Log.i(TAG, "GOOD ANSWER (" +answer+ ")");
                triggerGoodAnswer();
            } else { // otherwise, the user gave a bad answer
                triggerBadAnswer(answer);
            }
        }
    }


    private void triggerGoodAnswer() {
        mCurrentQuestion.setPlayed(true);
        mCurrentQuestion.save(); // update the question record. set it as played
        if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
            mWeakGameplayListener.get().onGoodAnswer();
        }
        emptyHistory();
    }


    private void triggerBadAnswer(String answer) {
        Log.e(TAG, "BAD ANSWER (" + answer + ")");
        updateQuestionTries();

        if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
            mWeakGameplayListener.get().onBadAnswer();
            if(mScore > 0 ) {
                mScore--;
                saveIntPref(Constants.PREFS_SCORE, mScore);
                mWeakGameplayListener.get().onUpdateLivesScore(mScore);
            } else { // no more lives, GAME OVER
                saveIntPref(Constants.PREFS_SCORE, 0);
                mWeakGameplayListener.get().onGameOver(GameOver.NO_MORE_lIVES);
            }
        }
        updateAnswerHistory(answer , mCurrentQuestion.getAnswer());
    }


    private void updateQuestionTries() {
        if(mCurrentQuestion != null) {
            int currentTries = mCurrentQuestion.getTries();
            mCurrentQuestion.setTries(++currentTries);
            mCurrentQuestion.save();
        }
    }

    public int getUserScore() {
        return getIntPref(Constants.PREFS_SCORE);
    }

    private void initUserScore() {
        mScore = getIntPref(Constants.PREFS_SCORE);
        if(mScore == -1) { // if this is the first application launch, init the user score to INITIAL_SCORE
            mScore = Constants.INITIAL_SCORE;
            saveIntPref(Constants.PREFS_SCORE, mScore);
        }
    }


    private void resetScore() {
        mScore = Constants.INITIAL_SCORE;
        saveIntPref(Constants.PREFS_SCORE, mScore);
        if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
            mWeakGameplayListener.get().onUpdateLivesScore(mScore);
        }
    }


    public Question getNextQuestion() {
        mQuestions = GameManager.getInstance().getUnplayedQuestions();
        if(mQuestions != null && !mQuestions.isEmpty()) {
            Log.e(TAG, "Nb questions fetched: " + mQuestions.size());
            mCurrentQuestion = mQuestions.get(0);
            Log.e(TAG, "Current question: " + mQuestions.get(0).getQuestion());
        } else { // No more questions: game over
            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                mWeakGameplayListener.get().onGameOver(GameOver.NO_MORE_QUESTIONS);
            }
        }
        return mCurrentQuestion;
    }


    private List<String> updateAnswerHistory(String userAnswer, String goodAnswer) {
        if(TextUtils.isEmpty(userAnswer) || TextUtils.isEmpty(goodAnswer)){
            return mAnswersHistory;
        }

        long uAnswer = Long.valueOf(userAnswer);
        long gAnswer = Long.valueOf(goodAnswer);
        String formattedStr = null;

        if(uAnswer < gAnswer) {
            formattedStr = mAppContext.getString(R.string.its_more_than, uAnswer);
        } else if(uAnswer > gAnswer) {
            formattedStr = mAppContext.getString(R.string.its_less_than, uAnswer);
        }

        if(mAnswersHistory.size() == 0) {
            mAnswersHistory.add(formattedStr);
            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                mWeakGameplayListener.get().onUpdateHistory(formattedStr, "");
            }
        } else if (mAnswersHistory.size() == 1) {
            mAnswersHistory.add(formattedStr);
            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                mWeakGameplayListener.get().onUpdateHistory(formattedStr, mAnswersHistory.get(0));
            }
        } else {
            String prev = mAnswersHistory.get(0);
            mAnswersHistory.set(0, formattedStr);
            mAnswersHistory.set(1, prev);

            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                mWeakGameplayListener.get().onUpdateHistory(mAnswersHistory.get(0), mAnswersHistory.get(1));
            }
        }

        Log.w(TAG, formattedStr);
        return mAnswersHistory;
    }


    private void emptyHistory() {
        if(mAnswersHistory.size() == 2) {
            mAnswersHistory.remove(1);
            mAnswersHistory.remove(0);
        } else if(mAnswersHistory.size() == 1){
            mAnswersHistory.remove(0);
        }

        if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
            mWeakGameplayListener.get().onEmptyHistory();
        }
    }

    public String generateBingoTitle() {
        String title = null;
        if(mCurrentQuestion != null) {
           title = mAppContext.getString(R.string.popup_bingo_title, String.valueOf(mCurrentQuestion.getLevel()));
        }
        return title;
    }

    public String generateBingoTranslatedText() {
        String text = null;
        String itsTheRightNumber = mAppContext.getString(R.string.it_is_the_right_number);
        String in = mAppContext.getString(R.string.in);
        if(mCurrentQuestion != null) {
            String unit = !TextUtils.isEmpty(mCurrentQuestion.getUnit()) ? " (" + in + " " + mCurrentQuestion.getUnit() + ")" : "";
            StringBuilder sbBingoTextFR = new StringBuilder("<h3>" + itsTheRightNumber + "</h3></br></br> ");
            sbBingoTextFR.append(mCurrentQuestion.getQuestion() + unit);
            sbBingoTextFR.append("</br>");
            sbBingoTextFR.append("<h2><font color=\"#045FB4\">" + mCurrentQuestion.getAnswer() + "</font></h2>");
            text = sbBingoTextFR.toString();
        }
        return text;
    }


//    public boolean isUserAnswerToloerated(String answer) {
//        boolean isTolerated = false;
//        if(mCurrentQuestion != null &&
//                !TextUtils.isEmpty(answer) &&
//                !TextUtils.isEmpty(mCurrentQuestion.getAnswer())) {
//
//            long tolerance = mCurrentQuestion.getTolerance();
//            long userAnswer = Long.valueOf(answer);
//            long goodAnswer = Long.valueOf(mCurrentQuestion.getAnswer());
//
//            long result = (goodAnswer * tolerance) / 100;
//            if(userAnswer >= (goodAnswer - result) &&
//                    userAnswer <= (goodAnswer + result)) {
//                isTolerated = true; // bingo, lucky user...
//            }
//        }
//        return isTolerated;
//    }


    public <T extends Popup> void handlePopupAction(T popup, PopupAction popupAction) {
        if(popup != null && popupAction != null) {
            switch (popupAction) {
                case OK:
                    popup.dismiss();
                    break;
                case CONTINUE:
                    popup.dismiss();
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        mWeakGameplayListener.get().onShowNextQuestion(getNextQuestion());
                    }
                    break;
                case FRIEND_HELP:
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        mWeakGameplayListener.get().onAskForFriendsHelp(true);
                    }
                    break;
                case SHARE:
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        mWeakGameplayListener.get().onRequestShareFoundAnswer(mCurrentQuestion);
                    }
                    break;
                case SHARE_ALL_LEVELS_DONE:
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        ///TODO share text to add
                        mWeakGameplayListener.get().onShareAllLevelsDone("TODO");
                    }
                    break;

                case VISIT_FACEBOOK_PAGE:
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        mWeakGameplayListener.get().onClickVisitFacebookPage();
                    }
                    break;

                case VISITE_TWITTER_PAGE:
                    if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                        mWeakGameplayListener.get().onClickVisitTwitterPage();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handles the joker logic
     * @param joker the joker the user clicked on
     */
    public void handleJokerRequest(Joker joker) {
        if(joker != null) {
            switch (joker) {
                case RANGE:
                    if(mCurrentQuestion != null &&
                            !TextUtils.isEmpty(mCurrentQuestion.getJokermini()) &&
                            !TextUtils.isEmpty(mCurrentQuestion.getJokermaxi())) {

                        // remove 10 lives when using the joker
                        if(mScore - Constants.JOKER_RANGE_MALUS > 0) { // if the user has more than "Constants.JOKER_RANGE_MALUS" lives

                            mScore -= Constants.JOKER_RANGE_MALUS;
                            saveIntPref(Constants.PREFS_SCORE, mScore);

                            // notify listener to update the lives count
                            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                                mWeakGameplayListener.get().onUpdateLivesScore(mScore);
                            }

                            // notify listener to show the joker range
                            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                                mWeakGameplayListener.get().onRequestRangeJoker(mCurrentQuestion.getJokermini(),
                                        mCurrentQuestion.getJokermaxi());
                            }
                        } else { // no more lives: game over
                            if(mWeakGameplayListener != null && mWeakGameplayListener.get() != null) {
                                mWeakGameplayListener.get().onGameOver(GameOver.NO_MORE_lIVES);
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }


    /*
     * DATABASE METHODS
     */

    public void populateDatabase() {
        new InsertDataToDBTask().execute();
    }

    public List<Question> getAllQuestions() {
        return mQuestionDbHelper.fetchAll();
    }

    public List<Question> getUnplayedQuestions() {
        return mQuestionDbHelper.fetchUnplayedOnly();
    }


    /**
     * Populates the database with entries
     */
    private class InsertDataToDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String jsonString = FileHelper.getFileContentFromAssets(mAppContext, Constants.JSON_FILE);
            Log.d(TAG, "json string =====> " + jsonString);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Question>>(){}.getType();
                List<Question> questions = gson.fromJson(jsonString, listType);
                if (questions != null && questions.size() > 0) {
                    Log.e(TAG, "INSERTING QUESTIONS INTO DB (" + questions.size() + ")");
                    for (Question q : questions) {
                        Question qDB = mQuestionDbHelper.find("level = ?", new String[]{ String.valueOf(q.getLevel()) } );
                        if(qDB == null) { // entry does not exists yet, so we can insert it
                            /// TODO add timestamp and lang column
                            qDB = new Question(mAppContext, q.getQuestion(), q.getAnswer(), q.getLevel(), q.getJokermini(), q.getJokermaxi(),
                                    q.getUnit(), q.getFormat(), q.isPlayed(), q.getUserId());
                            mQuestionDbHelper.insertOrUpdate(qDB);
                            Log.e(TAG, "> INSERT QUESTION (" + qDB.getQuestion()+ ")");
                        } else { // else question already exists
                            // update the question only is it not already played
//                            if(!qDB.isPlayed()) {
//                                Log.w(TAG, "> UPDATE EXISTING AND UNPLAYED QUESTION (" + qDB.getQuestion() + ")");
//                                qDB = new Question(mAppContext, q.getQuestion(), q.getAnswer(), q.getLevel(), q.getJokermini(), q.getJokermaxi(),
//                                        q.getUnit(), q.getFormat(), q.isPlayed(), q.getUserId());
//                                mQuestionDbHelper.insertOrUpdate(qDB);
//                            }
                        }
                    }
                }

            return null;
        }
    }

}
