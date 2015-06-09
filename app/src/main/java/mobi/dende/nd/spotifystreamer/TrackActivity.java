package mobi.dende.nd.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class TrackActivity extends ActionBarActivity {
    public static final String EXTRA_TRACK = "extra_track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
    }

}
