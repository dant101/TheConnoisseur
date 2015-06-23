package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.theconnoisseur.R;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.ExerciseScore;
import com.theconnoisseur.android.Model.GlobalPreferenceString;
import com.theconnoisseur.android.Model.InternalDbContract;
import com.theconnoisseur.android.Model.SessionSummaryContent;

import java.io.File;
import java.io.IOException;

import Database.ConnoisseurDatabase;
import Util.ContentDownloadHelper;
import Voice.VoiceRecogniser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment implements VoiceRecogniser.VoiceCallback {
    private static final String TAG = ExerciseFragment.class.getSimpleName();
    private static final String ATTEMPT = "attempt";

    private static final int TRANSITION_TIME = 300;
    private static final int REVERSE_TRANSITION = 600;

    private LinearLayout mBackground;
    private ImageView mLanguageImage;
    private TextView mLanguage;
    private TextView mProgress;
    private ImageView mWordIllustration;
    private TextView mWord;
    //private TextView mPhoneticSpelling;
    private TextView mWordDescription;
    private ScrollView mWordDescriptionView;
    private ImageView mProceed;
    private LinearLayout mLivesBig;
    private LinearLayout mLivesSmall;
    private FrameLayout mRecordLayout;
    private ImageView mRecord;
    private ImageView mRecordAnim;
    private FrameLayout mListenLayout;
    private ImageView mListen;
    private ImageView mListenAnim;
    private ImageView mBigLife1g;
    private ImageView mBigLife1b;
    private ImageView mBigLife2g;
    private ImageView mBigLife2b;
    private ImageView mBigLife3g;
    private ImageView mBigLife3b;
    private ImageView mSmallLife1;
    private ImageView mSmallLife2;
    private ImageView mSmallLife3;
    private ImageView mSideLogo;
    private ImageView mCommentsIcon;
    private ProgressBar mSpinner;

    private MediaPlayer mMediaPlayer;
    private boolean played = false; //TESTING, Illegal state exception on second playback...
    private boolean mClicked = false;
    private boolean firstAttempt = true;
    private boolean mBackgroundIsTransitioned= false;
    private boolean mRandomSession = false;

    private int mCursorPosition = -1;
    private int mSessionWord = 0;
    private int mCurrentWordBestScore;
    private int mSessionCumulativeScore = 0;

    private int mSessionWordPasses = 0;
    private int mSessionWorstScore = 0;
    private int mSessionBestScore = ExerciseContent.MAXIMUM_LIVES;
    private String mSessionWorstWord = "";
    private String mSessionBestWord = "";
    private int mSessionWorstWordId;
    private int mSessionBestWordId;

    private int mSessionBestAttempts = ExerciseContent.MAXIMUM_LIVES;
    private int mSessionWorstAttempts = 0;

    private String mCurrentWord;
    private int mCurrentWordId;
    private String mLanguageString;
    private String mWordIllustrationUri;
    private String mLanguageFlagUri;
    private String mLanguagePlaceholderConnoisseurUri;
    private String mLanguagePlaceholderTouristUri;
    private String mLanguagePlaceholderBarbarianUri;
    private String mLanguageHex;
    private String mSoundLocation;
    private int mLanguageId;
    private int mThreshold;

    private boolean mPassedWord = false;
    private int mAttemptsRemaining = ExerciseContent.MAXIMUM_LIVES;
    private int mAttempts = 0;
    private int mSessionAttempts = 0;

    private OnFragmentInteractionListener mListener;
    private VoiceRecogniser mVoiceRecogniser;

    Typeface plantin; Typeface roboto;

    public static ExerciseFragment newInstance() {
        ExerciseFragment fragment = new ExerciseFragment();
        return fragment;
    }

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        mBackground = (LinearLayout) view.findViewById(R.id.background);
        mLanguageImage = (ImageView) view.findViewById(R.id.language_image);
        mLanguage = (TextView) view.findViewById(R.id.language);
        mProgress = (TextView) view.findViewById(R.id.progess);
        mWordIllustration = (ImageView) view.findViewById(R.id.word_illustration);
        mWord = (TextView) view.findViewById(R.id.word);
        //mPhoneticSpelling = (TextView) view.findViewById(R.id.phonetic_spelling);
        mWordDescription = (TextView) view.findViewById(R.id.word_description);
        mWordDescriptionView = (ScrollView) view.findViewById(R.id.word_description_view);
        mProceed = (ImageView) view.findViewById(R.id.proceed);
        mLivesBig = (LinearLayout) view.findViewById(R.id.lives_section);
        mLivesSmall = (LinearLayout) view.findViewById(R.id.lives_section_small);

        mRecord = (ImageView) view.findViewById(R.id.record_icon);
        mRecordLayout = (FrameLayout) view.findViewById(R.id.record_layout);
        mRecordAnim = (ImageView) view.findViewById(R.id.record_icon_anim);
        mListen = (ImageView) view.findViewById(R.id.listen_icon);
        mListenLayout = (FrameLayout) view.findViewById(R.id.listen_layout);
        mListenAnim = (ImageView) view.findViewById(R.id.listen_icon_anim);
        mBigLife1g = (ImageView) view.findViewById(R.id.heart_big_1_green);
        mBigLife1b = (ImageView) view.findViewById(R.id.heart_big_1_black);
        mBigLife2g = (ImageView) view.findViewById(R.id.heart_big_2_green);
        mBigLife2b = (ImageView) view.findViewById(R.id.heart_big_2_black);
        mBigLife3g = (ImageView) view.findViewById(R.id.heart_big_3_green);
        mBigLife3b = (ImageView) view.findViewById(R.id.heart_big_3_black);
        mSmallLife1 = (ImageView) view.findViewById(R.id.heart_small_1);
        mSmallLife2 = (ImageView) view.findViewById(R.id.heart_small_2);
        mSmallLife3 = (ImageView) view.findViewById(R.id.heart_small_3);
        mSideLogo = (ImageView) view.findViewById(R.id.connoisseur_side);
        mCommentsIcon = (ImageView) view.findViewById(R.id.comments_icon);
        mSpinner = (ProgressBar) view.findViewById(R.id.loading_spinner);

        //Background is invisible until first exercise is ready - via callback
        mBackground.setVisibility(View.GONE);
        plantin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/PlantinMTStd_Semibold.ttf");
        roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        mWord.setTypeface(plantin); mWordDescription.setTypeface(roboto);

        setListeners();
        setInitialView();

        return view;
    }

    private void setListeners() {
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                concludeWordAttempt();
                mListener.nextExercise();
            }
        });

        final Animation grow = AnimationUtils.loadAnimation(getActivity(), R.anim.grow);
        final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);

        mRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (mBackgroundIsTransitioned) {
                    ((TransitionDrawable) mBackground.getBackground()).reverseTransition(REVERSE_TRANSITION);
                    mBackgroundIsTransitioned = false;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mVoiceRecogniser.startListening();
                    mRecordAnim.startAnimation(grow);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mVoiceRecogniser.stopListening();
                    mRecordAnim.startAnimation(shrink);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Shifts screen to next exercise by loading the next word from the cursor
     * @param c
     */
    public void nextExercise(Cursor c, boolean resuming) {
        mBackground.setVisibility(View.VISIBLE);
        mSpinner.setVisibility(View.GONE);

        if (c == null) { return; }
        if (!resuming) {
            mCursorPosition += 1;
            mSessionWord += 1;
        }

        mCurrentWordBestScore = 0;

        mAttemptsRemaining = ExerciseContent.MAXIMUM_LIVES;
        firstAttempt = true;

        //CursorHelper.toString(c); //Testing

        Log.d(TAG, "Exercise cursor position: " + String.valueOf(mCursorPosition));

        c.moveToPosition(mCursorPosition);
        if (c.isAfterLast()) { concludeSession(); getActivity().finish(); return; }

        setInitialView();

        mProgress.setText(String.valueOf(mSessionWord) + "/" + String.valueOf(ExerciseActivity.EXERCISES_PER_SESSION));

        mLanguageString = c.getString(c.getColumnIndex(ExerciseContent.LANGUAGE));
        mLanguage.setText(mLanguageString);

        mWordDescription.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD_DESCRIPTION)));

        mWord.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD)));
        mCurrentWord = c.getString(c.getColumnIndex(ExerciseContent.WORD));
        mCurrentWordId = c.getInt(c.getColumnIndex(ExerciseContent.WORD_ID));
        //mPhoneticSpelling.setText(c.getString(c.getColumnIndex(ExerciseContent.PHONETIC)));

        mWordIllustrationUri = c.getString(c.getColumnIndex(ExerciseContent.IMAGE_URL));

        //Set language specific UI
        mLanguageId = c.getInt(c.getColumnIndex(ExerciseContent.LANGUAGE_ID));
        ((ExerciseActivity)getActivity()).prepareLanguageSpecifics(mLanguageId);

        mThreshold = c.getInt(c.getColumnIndex(ExerciseContent.THRESHOLD));
        mSoundLocation = c.getString(c.getColumnIndex(ExerciseContent.SOUND_RECORDING));

        String word = c.getString(c.getColumnIndex(ExerciseContent.WORD));
        String locale = c.getString(c.getColumnIndex(ExerciseContent.LOCALE));
        locale = locale == null ? "en-GB" : locale;

        Log.d(TAG, "Locale: " + locale);

        if (mVoiceRecogniser != null) {
            mVoiceRecogniser.destroyVoiceRecogniser();
        }
        mVoiceRecogniser = new VoiceRecogniser(getActivity(),  word, locale, this);

        loadWordIllustration();
    }

    /**
     * Change the text colour of the language to given hex value stored in db
     * @param hex hex value of language text colour
     */
    public void setLanguageSpecifics(String hex, String image_path, String path_connoisseur, String path_tourist, String path_barbarian) {
        mLanguageFlagUri = image_path;
        mLanguageHex = hex;
        mLanguagePlaceholderConnoisseurUri = path_connoisseur;
        mLanguagePlaceholderTouristUri = path_tourist;
        mLanguagePlaceholderBarbarianUri = path_barbarian;

        try {
            mLanguage.setTextColor(Color.parseColor(hex));
            mProgress.setTextColor(Color.parseColor(hex));
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Hex was provided for language - check database value!");
            e.printStackTrace();
        }

        ContentDownloadHelper.loadImage(getActivity(), mLanguageImage, image_path);
    }

    /**
     * Prepares and Plays the exercise sound recording
     */
    public void playRecording() {
        if (mSoundLocation != null) {

            String path = mSoundLocation.replace(File.separator, "");

            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(getActivity().getFilesDir()+ "/" + path);
                mMediaPlayer.prepare();
                mMediaPlayer.start();

                Log.d(TAG, "mediaPlayer set with source: " + getActivity().getFilesDir() + path);

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {mp.release(); Log.d(TAG, "MediaPlayer onCompletion!");
                    }
                });

            } catch (IOException e) {
                Log.d(TAG, "Unable to prepare sound recording");
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "SoundPath is null"); return;
        }

        final Animation grow = AnimationUtils.loadAnimation(getActivity(), R.anim.grow);
        final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);

        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mListenAnim.startAnimation(shrink);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mListenAnim.startAnimation(grow);
    }

    public void setRandomSession() {
        mRandomSession = true;
    }

    private void loadWordIllustration() {
        Log.d(TAG, "loadWordIllustration - mWordIllustrationUri: "+ mWordIllustrationUri);
        if (mWordIllustrationUri == null) {
            ContentDownloadHelper.loadImage(getActivity(), mWordIllustration, mLanguagePlaceholderConnoisseurUri);
        } else {
            ContentDownloadHelper.loadImage(getActivity(), mWordIllustration, mWordIllustrationUri);
        }
    }

    //UI change as a result of a successful recording attempt
    private void onSuccessfulAttempt() {
        Log.d(TAG, "Exercise Fragment: onSuccessfulAttempt");
        mLivesBig.setVisibility(View.GONE);
        mLivesSmall.setVisibility(View.VISIBLE);
        mWordDescriptionView.setVisibility(View.VISIBLE);
        mLanguageImage.setImageResource(R.drawable.tick_correct);
        mRecordLayout.setVisibility(View.GONE);
        mListenLayout.setVisibility(View.GONE);
        mProceed.setVisibility(View.VISIBLE);

        setBackgroundResourceAndAnimate(R.drawable.transition_green);
    }

    // UI changes as a result of an unsuccessful recording attempt
    private void onUnSuccessfulAttempt() {
        Log.d(TAG, "Exercise Fragment: onUnSuccessfulAttempt");
        mLanguageImage.setImageResource(R.drawable.cross_wrong);
        setBackgroundResourceAndAnimate(R.drawable.transition_red);

        mAttemptsRemaining -= 1;
    }

    // Sets the background and animates, (user feedback)
    private void setBackgroundResourceAndAnimate(int resource) {
        mBackground.setBackgroundResource(resource);
        int pad = (int) getResources().getDimension(R.dimen.normal_padding);
        mBackground.setPadding(pad, pad, pad, pad);
        ((TransitionDrawable) mBackground.getBackground()).startTransition(TRANSITION_TIME);
        mBackgroundIsTransitioned = true;
    }

    //UI alterations after first recording attempt by user
    private void afterFirstAttempt() {
        Log.d(TAG, "afterFirstAttempt");
    }

    //Initial UI settings (see story board)
    private void setInitialView() {
        mLivesBig.setVisibility(View.VISIBLE);
        mLivesSmall.setVisibility(View.GONE);
        mWordDescriptionView.setVisibility(View.GONE);
        mProceed.setVisibility(View.GONE);
        mListenLayout.setVisibility(View.VISIBLE);
        //mRecord.setVisibility(View.VISIBLE);
        mRecordLayout.setVisibility(View.VISIBLE);

        if(mBackgroundIsTransitioned) {
            ((TransitionDrawable)mBackground.getBackground()).reverseTransition(REVERSE_TRANSITION);
            mBackgroundIsTransitioned = false;
        }

        initLives();
    }

    private void initLives() {
        mBigLife3g.clearAnimation();
        mBigLife3g.setVisibility(View.VISIBLE);
        mBigLife3b.setVisibility(View.GONE);
        mSmallLife3.setImageResource(R.drawable.heart_green_small);

        mBigLife2g.clearAnimation();
        mBigLife2g.setVisibility(View.VISIBLE);
        mBigLife2b.setVisibility(View.GONE);
        mSmallLife2.setImageResource(R.drawable.heart_green_small);
        
        mBigLife1g.clearAnimation();
        mBigLife1g.setVisibility(View.VISIBLE);
        mBigLife1b.setVisibility(View.GONE);
        mSmallLife1.setImageResource(R.drawable.heart_green_small);
    }

    private void updateLives() {

        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);

        switch(mAttemptsRemaining) {
            case 2:
                mBigLife3b.setVisibility(View.VISIBLE);
                mBigLife3g.startAnimation(anim);
                mSmallLife3.setImageResource(R.drawable.heart_green_black);
                break;
            case 1:
                mBigLife2b.setVisibility(View.VISIBLE);
                mBigLife2g.startAnimation(anim);
                mSmallLife2.setImageResource(R.drawable.heart_green_black);
                break;
            case 0:
                mBigLife1b.setVisibility(View.VISIBLE);
                mBigLife1g.startAnimation(anim);
                mSmallLife1.setImageResource(R.drawable.heart_green_black);

                mRecordLayout.setVisibility(View.GONE);
                mListenLayout.setVisibility(View.GONE);
                mProceed.setVisibility(View.VISIBLE);
                break;
        }

        if (mWordIllustrationUri == null) {
            switch(mAttemptsRemaining) {
                case 2:
                    ContentDownloadHelper.loadImage(getActivity(), mWordIllustration, mLanguagePlaceholderTouristUri);
                    break;
                case 1:
                    ContentDownloadHelper.loadImage(getActivity(), mWordIllustration, mLanguagePlaceholderBarbarianUri);
                    break;
            }
        }

        mAttempts += 1;
    }

    // House keeping as we move onto the next word in a session
    private void concludeWordAttempt() {
        //Maintain cumulative score for average calculation
        mSessionCumulativeScore += mCurrentWordBestScore;

        //Set attempts at this pronunciation
        mAttempts = ExerciseContent.MAXIMUM_LIVES - mAttemptsRemaining;
        mSessionAttempts += mAttempts;

        Log.d(TAG, "mAttemptsRemaining: " + String.valueOf(mAttemptsRemaining) + ". mAttempts: " + String.valueOf(mAttempts));

        //Track best scores/words
        if (mAttempts <= mSessionBestScore) {
            mSessionBestScore = mAttempts;
            mSessionBestWord = mCurrentWord;
            mSessionBestWordId = mCurrentWordId;
        }

        //Track best scores/words
        if (mAttempts > mSessionWorstScore) {
            mSessionWorstScore = mAttempts;
            mSessionWorstWord = mCurrentWord;
            mSessionWorstWordId = mCurrentWordId;
        }

        //Main number of words passed
        if (mCurrentWordBestScore >= mThreshold) {
            mSessionWordPasses += 1;
            Log.d(TAG, "mSessionword passes now: " + String.valueOf(mSessionWordPasses));
        }

        Log.d(TAG, "mCurrentWordBestScore: " + String.valueOf(mCurrentWordBestScore) + ". mThreshold: " + String.valueOf(mThreshold));
        Log.d(TAG, "mAttempts = " +String.valueOf(mAttempts));
        Log.d(TAG, "mSessionAttempts now: " + String.valueOf(mSessionAttempts));

        //Save word performance in internal db
        saveWordPerformance();

        // Reset flag (UI reset)
        ContentDownloadHelper.loadImage(getActivity(), mLanguageImage, mLanguageFlagUri);

    }

    private void concludeSession() {

        int session_number = getSessionNumber(mLanguageString);
        mSessionWord -= 1; //Since we increment before we realise we have none left, correct here
        Intent intent = new Intent(getActivity(), SessionSummary.class);

        intent.putExtra(SessionSummaryContent.AVERAGE_SCORE, mSessionCumulativeScore / mSessionWord);
        intent.putExtra(SessionSummaryContent.BEST_SCORE, mSessionBestScore);
        intent.putExtra(SessionSummaryContent.BEST_WORD, mSessionBestWord);
        intent.putExtra(SessionSummaryContent.BEST_WORD_ID, mSessionBestWordId);
        intent.putExtra(SessionSummaryContent.WORST_SCORE, mSessionWorstScore);
        intent.putExtra(SessionSummaryContent.WORST_WORD, mSessionWorstWord);
        intent.putExtra(SessionSummaryContent.WORST_WORD_ID, mSessionWorstWordId);
        intent.putExtra(SessionSummaryContent.LANGUAGE_FLAG_URI, mLanguageFlagUri);
        intent.putExtra(SessionSummaryContent.WORDS_PASSED, mSessionWordPasses);
        intent.putExtra(SessionSummaryContent.TOTAL_WORDS, mSessionWord);
        intent.putExtra(SessionSummaryContent.LANGUAGE, mLanguageString);
        intent.putExtra(SessionSummaryContent.LANGUAGE_HEX, mLanguageHex);
        intent.putExtra(SessionSummaryContent.SESSION_ATTEMPTS, mSessionAttempts);


        if (mRandomSession) {
            intent.putExtra(SessionSummaryContent.SESSION_NUMBER, getSessionNumber("random"));
            intent.putExtra(SessionSummaryContent.LANGUAGE_ID, ExerciseActivity.RANDOM_ID);
            intent.putExtra(SessionSummaryContent.RANDOM_SESSION, true);
        } else {
            intent.putExtra(SessionSummaryContent.SESSION_NUMBER, session_number);
            intent.putExtra(SessionSummaryContent.LANGUAGE_ID, mLanguageId);
            intent.putExtra(SessionSummaryContent.RANDOM_SESSION, false);
        }

        mVoiceRecogniser.destroyVoiceRecogniser();

        startActivity(intent);
    }

    @Override
    public void updateScore(float a) {
        if(firstAttempt) { afterFirstAttempt(); firstAttempt = false; }

        int score = Math.round(a * 100);
        Log.d(TAG, "Pronunciation Score: " + String.valueOf(score));

        //Updates current word best score
        mCurrentWordBestScore = score > mCurrentWordBestScore ? score : mCurrentWordBestScore;

        if (score < mThreshold) {
            mPassedWord = true;
            onUnSuccessfulAttempt();
        } else {
            mPassedWord = true;
            onSuccessfulAttempt();
        }
        updateLives();

        Log.d(TAG, "Cumulative score: " + String.valueOf(mSessionCumulativeScore) + ". BestCurrent: " + String.valueOf(mCurrentWordBestScore));
    }

    public void viewComments() {
        Intent i = new Intent(getActivity(), CommentActivity.class);
        i.putExtra(ExerciseContent.WORD_ID, mCurrentWordId);
        i.putExtra(ExerciseContent.WORD, mCurrentWord);
        i.putExtra(ExerciseContent.IMAGE_URL, mWordIllustrationUri);

        Log.d(TAG, "View comments onClick (from exercise fragment)");

        startActivity(i);
    }

    /**
     * Checks whether round performance is new best for attempts/best percentage
     * Where appropriate, updates internal and online database values
     */
    private void saveWordPerformance() {
        Cursor score = getActivity().getContentResolver().query(InternalDbContract.queryForExerciseScore(mCurrentWordId), null, null, null, null);
        String username = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(GlobalPreferenceString.USERNAME_PREF, "Guest");
        ContentValues v = new ContentValues();
        v.put(ExerciseScore.USER_ID, username);
        v.put(ExerciseScore.WORD_ID, mCurrentWordId);

        if (score.moveToFirst()) {
            boolean shouldUpdate = false;
            if (score.getInt(score.getColumnIndex(ExerciseScore.PERCENTAGE_SCORE)) < mCurrentWordBestScore) {
                v.put(ExerciseScore.PERCENTAGE_SCORE, mCurrentWordBestScore);
                shouldUpdate = true;
            }

            if (score.getInt(score.getColumnIndex(ExerciseScore.ATTEMPTS_SCORE)) > mAttempts) {
                v.put(ExerciseScore.ATTEMPTS_SCORE, mAttempts);
                shouldUpdate = true;
            }

            if (shouldUpdate) {
                getActivity().getContentResolver().update(InternalDbContract.updateExerciseScore(mCurrentWordId), v, null, null);

                //Initiates online database update
                Log.d(TAG, "ExerciseFragment: updating scores for word_id = " + String.valueOf(mCurrentWordId));
                new Thread(new ScoreUpdate(username, mCurrentWordId, mAttempts, mCurrentWordBestScore)).start();
            }

        } else {
            //Insert new row
            v.put(ExerciseScore.PERCENTAGE_SCORE, mCurrentWordBestScore);
            v.put(ExerciseScore.ATTEMPTS_SCORE, mAttempts);
            Log.d(TAG, "ExerciseFragment: inserting scores for word_id = " + String.valueOf(mCurrentWordId));
            getActivity().getContentResolver().insert(InternalDbContract.insertExerciseScoreUri(), v);

            //Initiates online database update
            new Thread(new ScoreUpdate(username, mCurrentWordId, mAttempts, mCurrentWordBestScore)).start();
        }
    }

    /**
     * Gets the number of the sessions attempt
     */
    private int getSessionNumber(String language_string) {
        int session_number = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(language_string + ATTEMPT, 0) + 1;

        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        e.putInt(language_string + ATTEMPT, session_number);
        e.commit();

        return session_number;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public void nextExercise();
    }

    /**
     * Responsible for initiating a online database score update off main thread
     */
    private class ScoreUpdate implements Runnable {
        String username; int word_id; int attempts; int percentage;

        public ScoreUpdate(String username, int word_id, int attempts, int percentage) {
            this.username = username; this.word_id = word_id; this.attempts = attempts; this.percentage = percentage;
            Log.d(TAG, "Online database score update: word_id(" + String.valueOf(word_id)+"), attempts(" + String.valueOf(attempts) + ")");
        }

        @Override
        public void run() {
            ConnoisseurDatabase.getInstance().getScoreTable().updateScoreAndAttempts(username, word_id, percentage, attempts);
        }
    }


}
