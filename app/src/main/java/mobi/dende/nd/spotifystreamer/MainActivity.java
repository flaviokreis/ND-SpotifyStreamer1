package mobi.dende.nd.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;


public class MainActivity extends ActionBarActivity implements SearchArtistFragment.OnSearchListener {

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
}
