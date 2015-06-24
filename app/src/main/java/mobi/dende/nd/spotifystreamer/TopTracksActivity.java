package mobi.dende.nd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import java.util.ArrayList;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


public class TopTracksActivity extends ActionBarActivity implements TopTracksFragment.OnTopTrackListener{

    public static final String EXTRA_ARTIST = "extra_artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        //Set subtitle, artist name
        SimpleArtist artist = getIntent().getExtras().getParcelable(EXTRA_ARTIST);
        if( ( artist != null ) && ( ! TextUtils.isEmpty(artist.getName()) ) ){
            getSupportActionBar().setSubtitle(artist.getName());
        }
    }

    // http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public void onTrackSelected(int position, ArrayList<SimpleTrack> tracks) {
        Intent intent = new Intent(TopTracksActivity.this, TrackActivity.class);
        intent.putExtra(TrackFragment.EXTRA_SELECTED_POSITION, position);
        intent.putExtra(TrackFragment.EXTRA_TRACKS, tracks);
        startActivity(intent);
    }
}
