package mobi.dende.nd.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


/**
 * This fragment show the track infos and play a track music.
 * For now, only show and play, not change music and not show or change progress of music.
 */
public class TrackFragment extends DialogFragment implements View.OnClickListener {

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

    private MediaPlayer mMediaPlayer;

    private SimpleTrack mTrack;

    public TrackFragment() {
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

        mTrackPrevious.setOnClickListener(this);
        mTrackPlay.setOnClickListener(this);
        mTrackNext.setOnClickListener(this);

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

//    http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    //http://stackoverflow.com/questions/1965784/streaming-audio-from-a-url-in-android-using-mediaplayer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
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
                if( mMediaPlayer == null ){
                    mMediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(mTrack.getPreviewUrl()));
                }

                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    mTrackPlay.setImageResource(android.R.drawable.ic_media_play);
                }
                else{
                    mMediaPlayer.start();
                    mTrackPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
                break;
        }
    }
}
