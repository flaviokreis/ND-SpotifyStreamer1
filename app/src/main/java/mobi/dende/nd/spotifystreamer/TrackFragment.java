package mobi.dende.nd.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


/**
 * This fragment show the track infos and play a track music.
 * For now, only show and play, not change music and not show or change progress of music.
 */
public class TrackFragment extends DialogFragment implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {

    public static final String EXTRA_TRACK = "extra_track";

    private TextView  mArtistName;
    private TextView  mAlbumName;
    private ImageView mAlbumImage;
    private TextView  mTrackName;

    private SeekBar mTrackProgress;
    private TextView mTrackActualTime;
    private TextView mTrackTime;

    private ImageButton mTrackPrevious;
    private ImageButton mTrackPlay;
    private ImageButton mTrackNext;

    private double timeElapsed = 0;
    private double finalTime   = 0;

    private MediaPlayer mMediaPlayer;

    private SimpleTrack mTrack;

    private Handler durationHandler = new Handler();


    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mMediaPlayer == null){
            mMediaPlayer  = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(TrackFragment.this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_track, container, false);

        mArtistName = (TextView) layout.findViewById(R.id.artist_name);
        mAlbumName  = (TextView) layout.findViewById(R.id.album_name);
        mAlbumImage = (ImageView)layout.findViewById(R.id.album_image);
        mTrackName  = (TextView)layout.findViewById(R.id.track_name);

        mTrackProgress = (SeekBar)layout.findViewById(R.id.track_progress);
        mTrackActualTime = (TextView)layout.findViewById(R.id.track_actual_time);
        mTrackTime = (TextView)layout.findViewById(R.id.track_time);

        mTrackPrevious = (ImageButton)layout.findViewById(R.id.track_previous);
        mTrackPlay = (ImageButton)layout.findViewById(R.id.track_play);
        mTrackNext = (ImageButton)layout.findViewById(R.id.track_next);

        mTrackPrevious.setEnabled(false);
        mTrackPlay.setEnabled(false);
        mTrackNext.setEnabled(false);

        mTrackPrevious.setOnClickListener(this);
        mTrackPlay.setOnClickListener(this);
        mTrackNext.setOnClickListener(this);

        mTrackProgress.setOnSeekBarChangeListener(this);

        if(getArguments() != null){
            setTrack((SimpleTrack)getArguments().getParcelable(EXTRA_TRACK));
        }
        else if(getActivity().getIntent().getExtras() != null){
            setTrack((SimpleTrack)getActivity().getIntent().getExtras().getParcelable(EXTRA_TRACK));
        }

        return layout;
    }

    public void setTrack(SimpleTrack track){
        mTrack = track;

        prepareMusic(mTrack.getPreviewUrl());

        mArtistName.setText(mTrack.getArtistName());
        mAlbumName.setText(mTrack.getAlbumName());
        if( !TextUtils.isEmpty(mTrack.getAlbumImageUrl()) ){
            Picasso.with(getActivity())
                    .load(mTrack.getAlbumImageUrl())
                    .into(mAlbumImage);
        }
        mTrackName.setText(mTrack.getName());

        mTrackActualTime.setText("00:00");
        mTrackTime.setText("00:30");
    }

    private void prepareMusic(String previewUrl){
        try {
            mMediaPlayer.setDataSource(previewUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.track_previous:
                break;
            case R.id.track_next:
                break;
            case R.id.track_play:
                playAndPause();
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        finalTime = mMediaPlayer.getDuration();
        mTrackProgress.setMax((int)finalTime);
        mTrackTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
        mTrackPlay.setEnabled(true);
        playAndPause();
    }

    private void playAndPause(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mTrackPlay.setImageResource(android.R.drawable.ic_media_play);
        }
        else{
            mMediaPlayer.start();
            mTrackPlay.setImageResource(android.R.drawable.ic_media_pause);
            timeElapsed = mMediaPlayer.getCurrentPosition();
            mTrackProgress.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);
        }
    }

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            timeElapsed = mMediaPlayer.getCurrentPosition();
            mTrackProgress.setProgress((int) timeElapsed);
            mTrackActualTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
            if(mMediaPlayer.isPlaying()){
                durationHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            timeElapsed = progress;
            mMediaPlayer.seekTo((int)timeElapsed);
            mTrackActualTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { /* no code */ }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { /* no code */ }
}
