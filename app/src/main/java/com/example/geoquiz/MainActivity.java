package com.example.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    //private boolean mIsCheater;

    private Questions[] mQuestionBank = new Questions[]{
            new Questions(R.string.question_australia, true),
            new Questions(R.string.question_oceans, true),
            new Questions(R.string.question_mideast, false),
            new Questions(R.string.question_africa, false),
            new Questions(R.string.question_americas, true),
            new Questions(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int correctAnswer = 0;
    private int percentAnswer = 0;

    private boolean[] resState = new boolean[mQuestionBank.length];
    private boolean[] result = new boolean[mQuestionBank.length];
    private boolean[] mIsCheaterArray = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            resState = (savedInstanceState.getBooleanArray("Bool"));
            mIsCheaterArray = savedInstanceState.getBooleanArray("CHEAT");
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
               // mIsCheater = false;
               // mIsCheaterArray[mCurrentIndex] = false;
                updateQuestion();
            }
        });
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                    //mIsCheater = false;
                    updateQuestion();
                } else {
                    mCurrentIndex = mCurrentIndex - 1;
                   // mIsCheater = false;
                    updateQuestion();
                }

            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            //mIsCheater = CheatActivity.wasAnswerShown(data);
            mIsCheaterArray[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray("Bool", resState);
        savedInstanceState.putBooleanArray("CHEAT", mIsCheaterArray);
    }

    private void updateQuestion() {

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (resState[mCurrentIndex] == true) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else if (resState[mCurrentIndex] == false) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheaterArray[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                resState[mCurrentIndex] = true;
                result[mCurrentIndex] = true;

                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                correctAnswer++;
            } else {
                messageResId = R.string.incorrect_toast;
                resState[mCurrentIndex] = true;
                result[mCurrentIndex] = false;

                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
            }
        }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

            for (boolean b : resState) {
                if (b) {
                    percentAnswer = (correctAnswer * 100) / 6;

                    Toast.makeText(this, percentAnswer + "%" + " правильных ответов", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
            }
        }
    }

