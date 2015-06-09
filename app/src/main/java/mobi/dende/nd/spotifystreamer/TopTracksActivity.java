package mobi.dende.nd.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;


public class TopTracksActivity extends ActionBarActivity {

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
}
