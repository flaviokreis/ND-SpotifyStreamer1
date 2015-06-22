package mobi.dende.nd.spotifystreamer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


public class MainActivity extends ActionBarActivity implements SearchArtistFragment.OnSearchListener,
        TopTracksFragment.OnTopTrackListener{

    private TopTracksFragment topTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topTracksFragment = (TopTracksFragment)getSupportFragmentManager().findFragmentById(R.id.top_tracks);
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
            Intent intent = new Intent( MainActivity.this, TopTracksActivity.class );
            //Artist reference, in this case artist is parcelable object
            intent.putExtra(TopTracksActivity.EXTRA_ARTIST, artist);
            startActivity(intent);
        }

    }

    //Call this method only on tablet
    @Override
    public void onTrackSelected(SimpleTrack track) {
        if(track != null){
            FragmentManager fragmentManager = getFragmentManager();
            TrackFragment trackFragment = new TrackFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(TrackFragment.EXTRA_TRACK, track);
            trackFragment.setArguments(bundle);

            // The device is using a large layout, so show the fragment as a dialog
            trackFragment.show(fragmentManager, "dialog");
        }
    }
}
