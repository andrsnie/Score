/*
 * Created by andrSnie on 3.03.20 1:17
 * Copyright (c) 2020. All rights reserved.
 */

package angel.andrsnie.score;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //the score for Team A
    private int mScoreTeamA = 0;

    //the score for Team B
    private int mScoreTeamB = 0;

    private static final String SCORE_A = "scoreA";
    private static final String SCORE_B = "scoreB";

	/** Handles playback of all the sound files */
    private MediaPlayer mMediaPlayer;

    /** Handles audio focus when playing a sound file */
    private AudioManager mAudioManager;

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                releaseMediaPlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mScoreTeamA = savedInstanceState.getInt(SCORE_A);
            mScoreTeamB = savedInstanceState.getInt(SCORE_B);
        }
		
        setContentView(R.layout.activity_main);

        displayForTeamA(mScoreTeamA);
        displayForTeamB(mScoreTeamB);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * Increase the score for Team A by 1 point & show.
     */
    public void addOneForTeamA(View v) {
        soundOfButton(R.raw.goal);
        mScoreTeamA += 1;
        displayForTeamA(mScoreTeamA);
    }

	/**
     * Increase the score for Team B by 1 point & show.
     */
    public void addOneForTeamB(View v) {
        soundOfButton(R.raw.goal);
        mScoreTeamB += 1;
        displayForTeamB(mScoreTeamB);
    }

    /**
     * Resets the score for both teams back to 0 & show.
     */
    public void resetScore(View v) {
        soundOfButton(R.raw.anthem);
        mScoreTeamA = 0;
        mScoreTeamB = 0;
        displayForTeamA(mScoreTeamA);
        displayForTeamB(mScoreTeamB);
    }

    public void soundOfButton(int track)
    {
        // Release the media player if it currently exists because we are about to
        // play a different sound file
        releaseMediaPlayer();

        // Request audio focus so in order to play the audio file.
        int outcome = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (outcome == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // We have audio focus now.

            mMediaPlayer = MediaPlayer.create(this, track);

            // Start the audio file
            mMediaPlayer.start();

            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sound has finished playing.
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
        }
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayForTeamA(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(score));
    }
	
	/**
	* Clean up the media player by releasing its resources.
	*/
	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
			mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
		}
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCORE_A, mScoreTeamA);
        outState.putInt(SCORE_B, mScoreTeamB);
    }
}