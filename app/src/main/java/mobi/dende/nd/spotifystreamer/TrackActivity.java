package mobi.dende.nd.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

import mobi.dende.nd.spotifystreamer.models.SimpleTrack;


public class TrackActivity extends ActionBarActivity {

    public static Intent getIntent(Activity activity, int position, ArrayList<SimpleTrack> tracks){
        Intent intent = new Intent(activity, TrackActivity.class);
        intent.putExtra(TrackFragment.EXTRA_SELECTED_POSITION, position);
        intent.putExtra(TrackFragment.EXTRA_TRACKS, tracks);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
    }
}
