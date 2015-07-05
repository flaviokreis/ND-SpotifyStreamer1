package mobi.dende.nd.spotifystreamer;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


public class MainActivity extends ActionBarActivity implements SearchArtistFragment.OnSearchListener,
        TopTracksFragment.OnTopTrackListener{

    private TopTracksFragment topTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        topTracksFragment = (TopTracksFragment)getFragmentManager().findFragmentById(R.id.top_tracks);
    }

    @Override
    public void onInitSearch() {
        if( topTracksFragment != null ){
            getSupportActionBar().setSubtitle(null);
            topTracksFragment.clear();
        }
    }

    @Override
    public void onSelectedArtist(SimpleArtist artist) {
        if( topTracksFragment != null ){
            getSupportActionBar().setSubtitle(artist.getName());
            topTracksFragment.setArtist(artist);
        }
        else{
            //Go to next screen, show the top tracks
            startActivity(TopTracksActivity.getIntent(MainActivity.this, artist));
        }

    }

    //Call this method only on tablet
    @Override
    public void onTrackSelected(int position, ArrayList<SimpleTrack> tracks) {
        FragmentManager fragmentManager = getFragmentManager();
        TrackFragment trackFragment = TrackFragment.getInstance(position, tracks);
        // The device is using a large layout, so show the fragment as a dialog
        trackFragment.show(fragmentManager, "dialog");
    }
}
