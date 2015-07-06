package mobi.dende.nd.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


/**
 * This fragment show the track infos and play a track music.
 * For now, only show and play, not change music and not show or change progress of music.
 */
public class TrackFragment extends DialogFragment implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    public static final String EXTRA_TRACKS             = "extra_tracks";
    public static final String EXTRA_SELECTED_POSITION  = "actual_position";

    private static final int TRACK_NOTIFICATION_ID = 501;

    private static final String ACTION_PREVIOUS = "mobi.dende.nd.spotifystreamer.ACTION_PREVIOUS";
    private static final String ACTION_PLAY     = "mobi.dende.nd.spotifystreamer.ACTION_PLAY";
    private static final String ACTION_PAUSE    = "mobi.dende.nd.spotifystreamer.ACTION_STOP";
    private static final String ACTION_NEXT     = "mobi.dende.nd.spotifystreamer.ACTION_NEXT";

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

    private ShareActionProvider mShareActionProvider;

    private double timeElapsed = 0;
    private double finalTime   = 0;

    private MediaPlayer mMediaPlayer;

    private List<SimpleTrack> mTracks;
    private int mActualPosition;
    private SimpleTrack mTrack;

    private Handler durationHandler = new Handler();

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_PREVIOUS:
                    playPrevious();
                    break;
                case ACTION_PLAY:
                    playAndPause();
                    break;
                case ACTION_PAUSE:
                    playAndPause();
                    break;
                case ACTION_NEXT:
                    playNext();
                    break;
            }
        }
    };

    public static TrackFragment getInstance(int position, ArrayList<SimpleTrack> tracks){
        TrackFragment trackFragment = new TrackFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SELECTED_POSITION, position);
        bundle.putParcelableArrayList(EXTRA_TRACKS, tracks);

        trackFragment.setArguments(bundle);

        return trackFragment;
    }

    public TrackFragment() { /* no code */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().registerReceiver(mReceiver, new IntentFilter(ACTION_PREVIOUS));
        getActivity().registerReceiver(mReceiver, new IntentFilter(ACTION_PLAY));
        getActivity().registerReceiver(mReceiver, new IntentFilter(ACTION_PAUSE));
        getActivity().registerReceiver(mReceiver, new IntentFilter(ACTION_NEXT));
        if(mMediaPlayer == null){
            mMediaPlayer  = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(TrackFragment.this);
            mMediaPlayer.setOnCompletionListener(TrackFragment.this);
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
            mTracks         = getArguments().getParcelableArrayList(EXTRA_TRACKS);
            mActualPosition = getArguments().getInt(EXTRA_SELECTED_POSITION, 0);
        }
        else if(getActivity().getIntent().getExtras() != null){
            mTracks = getActivity().getIntent().getExtras().getParcelableArrayList(EXTRA_TRACKS);
            mActualPosition = getActivity().getIntent().getExtras().getInt(EXTRA_SELECTED_POSITION);
        }

        changeLayout();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_track, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Now get the ShareActionProvider from the item

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if(mTrack != null){
            setShareIntent();
        }
    }

    public void changeLayout(){
        mTrack = mTracks.get(mActualPosition);

        mTrackPrevious.setEnabled(mActualPosition != 0);
        mTrackPlay.setEnabled(false);
        mTrackNext.setEnabled(mActualPosition != ( mTracks.size() - 1 ));
        mTrackProgress.setEnabled(false);

        mTrackActualTime.setText("");
        mTrackTime.setText("");

        mArtistName.setText(mTrack.getArtistName());
        mAlbumName.setText(mTrack.getAlbumName());
        if( !TextUtils.isEmpty(mTrack.getAlbumImageUrl()) ){
            Picasso.with(getActivity())
                    .load(mTrack.getAlbumImageUrl())
                    .into(mAlbumImage);
        }
        mTrackName.setText(mTrack.getName());

        prepareMusic(mTrack.getPreviewUrl());

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            setShareIntent();
        }
    }

    private void prepareMusic(String previewUrl){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }

        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(previewUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Reference: http://developer.android.com/guide/topics/ui/dialogs.html
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
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(TRACK_NOTIFICATION_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.track_previous:
                playPrevious();
                break;
            case R.id.track_next:
                playNext();
                break;
            case R.id.track_play:
                playAndPause();
                break;
        }
    }

    //Reference: http://www.tutorialspoint.com/android/android_mediaplayer.htm
    @Override
    public void onPrepared(MediaPlayer mp) {
        finalTime = mMediaPlayer.getDuration();
        mTrackProgress.setMax((int)finalTime);
        mTrackTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
        mTrackPlay.setEnabled(true);
        mTrackProgress.setEnabled(true);
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
        createNotification();
    }

    private void playPrevious(){
        if(mActualPosition > 0){
            mActualPosition--;
            changeLayout();
        }
    }

    private void playNext(){
        if(mActualPosition < (mTracks.size() - 1)){
            mActualPosition++;
            changeLayout();
        }
    }

    //Reference http://www.tutorialspoint.com/android/android_mediaplayer.htm
    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            if((mMediaPlayer != null) && mMediaPlayer.isPlaying()){
                timeElapsed = mMediaPlayer.getCurrentPosition();
                mTrackProgress.setProgress((int) timeElapsed);
                mTrackActualTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            sendIntent.setType("text/plain");

            String url = mTrack.getArtistUrl();
            if(TextUtils.isEmpty(url)){
                url = mTrack.getAlbumImageUrl();
            }

            sendIntent.putExtra(Intent.EXTRA_TEXT, "Playing: " + mTrack.getName() +
                    " - " + mTrack.getArtistName() + "\n" + url);

            // Now update the ShareActionProvider with the new share intent
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    public void createNotification(){
        NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle();
        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setStyle(mediaStyle)
                .addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        if(mMediaPlayer.isPlaying()){
            mBuilder.addAction(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        } else {
            mBuilder.addAction(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
        }
        mBuilder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT))
                .setContentTitle(mTrack.getName())
                .setContentText(mTrack.getArtistName() + "\n" + mTrack.getAlbumName());

        Picasso.with(getActivity()).load(mTrack.getAlbumImageUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBuilder.setLargeIcon(bitmap);
                NotificationManager mNotificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(TRACK_NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                NotificationManager mNotificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(TRACK_NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { /* no code */ }
        });
    }

    private NotificationCompat.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( intentAction );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                201, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }
}
