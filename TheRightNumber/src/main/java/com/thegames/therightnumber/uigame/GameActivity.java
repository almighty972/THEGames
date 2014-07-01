package com.thegames.therightnumber.uigame;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.AbstractFragment;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.interfaces.GameplayListener;
import com.thegames.therightnumber.interfaces.NumericPadListener;
import com.thegames.therightnumber.interfaces.PopupDialogListener;
import com.thegames.therightnumber.managers.GameManager;
import com.thegames.therightnumber.model.Question;
import com.thegames.therightnumber.popups.LevelsDonePopup;
import com.thegames.therightnumber.popups.NoMoreLivesPopup;
import com.thegames.therightnumber.popups.OutchPopup;
import com.thegames.therightnumber.popups.Popup;
import com.thegames.therightnumber.views.CustomNumericPad;

public class GameActivity extends AbstractActivity {

    private String TAG = "GameActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GameFragment())
                    .commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view and the game logic.
     */
    public static class GameFragment extends AbstractFragment implements NumericPadListener, GameplayListener,
            View.OnClickListener, PopupDialogListener {

        public static final String TAG = "GameFragment";

        private CustomNumericPad mCustomNumericPad;
        private EditText mAnswerFieldEditText;
        private ImageButton mClearButton, mBackImageButton, mHelpImageButton;
        private ImageView mLivesImageView;
        private TextView mLivesCountTextView, mLevelTextView, mQuestionTextView, mHistory1TextView, mHistory2TextView, mJokerRangeAnswerTextView;
        private RelativeLayout mHelpFriendsRelativeLayout;
        private Button mCancelHelpButton;

        private Popup mPopup;

        private StringBuilder mAnswerBuilder = new StringBuilder();

        private NumberTextWatcher mNumberTextWatcher;

        public GameFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_game, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mQuestionTextView = (TextView) view.findViewById(R.id.questionTextView);
            mBackImageButton = (ImageButton) view.findViewById(R.id.backImageButton);
            mHelpImageButton = (ImageButton) view.findViewById(R.id.helpImageButton);
            mLivesImageView = (ImageView) view.findViewById(R.id.livesImageView);
            mLivesCountTextView = (TextView) view.findViewById(R.id.livesCountTextView);
            mLevelTextView = (TextView) view.findViewById(R.id.levelTextView);
            mCustomNumericPad = (CustomNumericPad) view.findViewById(R.id.numericPad);
            mClearButton = (ImageButton) view.findViewById(R.id.clearButton);
            mHistory1TextView = (TextView) view.findViewById(R.id.history1TextView);
            mHistory2TextView = (TextView) view.findViewById(R.id.history2TextView);
            mHelpFriendsRelativeLayout = (RelativeLayout) view.findViewById(R.id.helpFriendsRelativeLayout);
            mCancelHelpButton = (Button) view.findViewById(R.id.cancelHelpButton);
            mJokerRangeAnswerTextView = (TextView) view.findViewById(R.id.jokerRangeAnswerTextView);

            mAnswerFieldEditText = (EditText) view.findViewById(R.id.answerFieldEditText);
            mAnswerFieldEditText.setInputType(InputType.TYPE_NULL);
            mNumberTextWatcher = new NumberTextWatcher(mAnswerFieldEditText); // adding a watcher

            setListeners();
        }

        private void setListeners() { // for more clarity
            mBackImageButton.setOnClickListener(this);
            mHelpImageButton.setOnClickListener(this);
            mClearButton.setOnClickListener(this);
            mCancelHelpButton.setOnClickListener(this);
            mCustomNumericPad.setWeakNumericPadListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();

            GameManager.getInstance().setGameplayListener(this); // attach the listener to the GameManager

            // update user score
            mLivesCountTextView.setText(String.valueOf(GameManager.getInstance().getUserScore()));

            // load next unplayed question
            Question q = GameManager.getInstance().getNextQuestion();
            showQuestion(q);
        }

        private void showQuestion(Question question) {
            if(question != null) {
                mQuestionTextView.setText(question.getQuestion());
                mAnswerFieldEditText.setHint(question.getUnit());
                mLevelTextView.setText(getString(R.string.level_num, question.getLevel()));
            }
        }

        private void clear() {
            mAnswerBuilder.setLength(0); // clear the buffer
            mAnswerFieldEditText.setText("");
        }

        private void displayUserInput(String text) {
            mAnswerFieldEditText.setText(text);
        }

        private void toggleHelpFromFriendsModule() {
            if(mHelpFriendsRelativeLayout.getVisibility() == View.GONE) {
                mHelpFriendsRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                mHelpFriendsRelativeLayout.setVisibility(View.GONE);
            }
        }




        /*****************************
         * LISTENERS CALLBACK METHODS
         *****************************/



        @Override
        public void onPressNumericKey(int keyNum) {
            mAnswerBuilder.append(keyNum);
            displayUserInput(mAnswerBuilder.toString());
        }

        @Override
        public void onPressOk() {
            Log.d(TAG, "user pressed num key OK");
            String userAnswer = mNumberTextWatcher.getRawInput();
            GameManager.getInstance().processAnswer(userAnswer);
        }

        @Override
        public void onPressJokerRangeKey() {
            GameManager.getInstance().handleJokerRequest(Joker.RANGE);
        }

        @Override
        public void onClick(View v) {

            if(v == mBackImageButton) { getActivity().finish(); }

            if(v == mClearButton) { clear(); }

            if(v == mHelpImageButton || v == mCancelHelpButton) { toggleHelpFromFriendsModule(); }

        }

        @Override
        public void onShowNextQuestion(Question question) {
            if(question != null) {
                mCustomNumericPad.enableJokerKey();
                showQuestion(question);
            } else {
                ///TODO no more questions. All levels done
            }
            clear();
        }

        @Override
        public void onGoodAnswer() {
           /* String bingoTitle = GameManager.getInstance().generateBingoTitle();
            String bingoBody = GameManager.getInstance().generateBingoTranslatedText();
            BingoPopup bingoPopup = BingoPopup.newInstance(bingoTitle, bingoBody);
            bingoPopup.setPopupListener(this);
            showPopupDialog(bingoPopup); */

            /* LevelsDonePopup levelsDonePopup = LevelsDonePopup.newInstance(getString(R.string.popup_levels_done_title),
                    getString(R.string.popup_levels_done_body));
            levelsDonePopup.setPopupListener(this);
            showPopupDialog(levelsDonePopup); */

            NoMoreLivesPopup noMoreLivesPopup = NoMoreLivesPopup.newInstance(getString(R.string.popup_no_more_lives_title),
                    getString(R.string.popup_no_more_lives_body, String.valueOf(Constants.LIVES_GIFT)));
            noMoreLivesPopup.setPopupListener(this);
            showPopupDialog(noMoreLivesPopup);
        }

        @Override
        public void onBadAnswer() {
            clear();
        }

        @Override
        public void onRequestRangeJoker(String minRange, String maxRange) {
            mCustomNumericPad.disableJokerKey();
            mJokerRangeAnswerTextView.setText(getString(R.string.popup_joker_range_body, minRange, maxRange));
        }

        @Override
        public void onEmptyHistory() {
            mHistory1TextView.setText("");
            mHistory2TextView.setText("");
            mJokerRangeAnswerTextView.setText("");
        }

        @Override
        public void onUpdateHistory(String firstRecord, String secondRecord) {
            mHistory1TextView.setText(secondRecord);
            mHistory2TextView.setText(firstRecord);
        }

        @Override
        public void onSecondWrongAnswer() {
            OutchPopup outchPopup = OutchPopup.newInstance(getString(R.string.popup_outch_title),
                    getString(R.string.popup_outch_body));
            outchPopup.setPopupListener(this);
            showPopupDialog(outchPopup);
        }

        @Override
        public void onAskForFriendsHelp(boolean withDelay) {
            ///TODO show friends module
        }

        @Override
        public void onRequestShareFoundAnswer(Question question) {
            /// TODO show share answer module
        }

        @Override
        public void onUpdateLivesScore(int score) {
            mLivesCountTextView.setText(String.valueOf(score));
        }

        @Override
        public void onUpdateLevel(int level) {
            mLevelTextView.setText(getString(R.string.level_num, level));
        }

        @Override
        public void onGameOver(GameOver gameOverReason) {
            switch (gameOverReason) {
                case NO_MORE_lIVES:
                    break;

                case NO_MORE_QUESTIONS:
                    LevelsDonePopup levelsDonePopup = LevelsDonePopup.newInstance(getString(R.string.popup_levels_done_title),
                            getString(R.string.popup_levels_done_body));
                    levelsDonePopup.setPopupListener(this);
                    showPopupDialog(levelsDonePopup);
                    break;
            }
        }

        @Override
        public void onShareAllLevelsDone(String text) {

        }

        @Override
        public void onClickVisitFacebookPage() {
            openUrl(getString(R.string.facebook_page_url));
        }

        @Override
        public void onClickVisitTwitterPage() {
            openUrl(getString(R.string.twitter_page_url));
        }

        @Override
        public <T extends Popup> void onClickPopupButton(T popup, PopupAction popupAction) {
            GameManager.getInstance().handlePopupAction(popup, popupAction);
        }

        @Override
        public void onCountdownFinished() {
            /// TODO give 50 lives free
        }
    }
}
