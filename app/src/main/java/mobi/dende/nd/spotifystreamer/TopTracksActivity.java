package mobi.dende.nd.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import java.util.ArrayList;

import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


public class TopTracksActivity extends ActionBarActivity implements TopTracksFragment.OnTopTrackListener{

    public static Intent getIntent(Activity activity, SimpleArtist artist){
        Intent intent = new Intent( activity, TopTracksActivity.class );
        //Artist reference, in this case artist is parcelable object
        intent.putExtra(TopTracksFragment.EXTRA_ARTIST, artist);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleArtist artist = getIntent().getExtras().getParcelable(TopTracksFragment.EXTRA_ARTIST);

        //Set subtitle, artist name
        if( ( artist != null ) && ( ! TextUtils.isEmpty(artist.getName()) ) ){
            getSupportActionBar().setSubtitle(artist.getName());
        }

        //Create top tracks Fragment
        if(savedInstanceState == null){
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, TopTracksFragment.getInstance(artist))
                    .commit();
        }
    }

    // http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public void onTrackSelected(int position, ArrayList<SimpleTrack> tracks) {
        startActivity(TrackActivity.getIntent(TopTracksActivity.this, position, tracks));
    }
}
