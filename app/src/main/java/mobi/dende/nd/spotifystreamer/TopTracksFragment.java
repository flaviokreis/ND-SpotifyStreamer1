package mobi.dende.nd.spotifystreamer;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import mobi.dende.nd.spotifystreamer.adapters.TracksAdapter;
import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;
import mobi.dende.nd.spotifystreamer.utils.NetworkUtils;
import retrofit.RetrofitError;


/**
 * This fragment show the list of to tracks.
 * Get this list on Spotify API {see https://developer.spotify.com/web-api/get-artists-top-tracks/}
 *
 * Parser json from Spotify API using Spotify Web Api Android {see https://github.com/kaaes/spotify-web-api-android}
 */
public class TopTracksFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String TAG = SearchArtistFragment.class.getSimpleName();

    public static final String EXTRA_ARTIST = "extra_artist";

    private static final String EXTRA_TRACKS = "extra_tracks";

    private SimpleArtist mArtist;

    private ListView mListView;
    private TextView mEmptyList;
    private ProgressBar mLoading;

    private TracksAdapter mAdapter;

    private ArrayList<SimpleTrack> mTracks;

    private OnTopTrackListener mListener;

    public interface OnTopTrackListener {
        void onTrackSelected(int position, ArrayList<SimpleTrack> tracks);
    }

    public static TopTracksFragment getInstance(SimpleArtist artist){
        TopTracksFragment topTracksFragment = new TopTracksFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ARTIST, artist);

        topTracksFragment.setArguments(bundle);

        return topTracksFragment;
    }

    public TopTracksFragment() {/* no code */}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnTopTrackListener) {
            mListener = (OnTopTrackListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet SearchArtistFragment.OnSelectedArtist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            mArtist = getArguments().getParcelable(EXTRA_ARTIST);
        }

        View layout = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        mListView   = (ListView) layout.findViewById(R.id.list_tracks);
        mEmptyList  = (TextView) layout.findViewById(R.id.empty_list);
        mLoading    = (ProgressBar)layout.findViewById(R.id.loading);

        mAdapter = new TracksAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(TopTracksFragment.this);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save searched top tracks to show on list if the screen rotate
        if( mTracks != null ){
            outState.putParcelableArrayList(EXTRA_TRACKS, mTracks);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        //Restore the searched top tracks, if possible.
        if( savedInstanceState != null ){
            mTracks = savedInstanceState.getParcelableArrayList(EXTRA_TRACKS);
            showList(mTracks);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    private void showList(ArrayList<SimpleTrack> tracks){
        mAdapter.setArtists(tracks);
        mLoading.setVisibility(View.GONE);
        if( tracks == null || tracks.isEmpty() ){
            mEmptyList.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), R.string.no_tracks_found_message,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(( mTracks == null) && (mArtist != null)){
            if(NetworkUtils.isNetworkAvailable(getActivity())){
                new TopTracksTask().execute(mArtist.getId());
            }
            else{
                Toast.makeText(getActivity(), R.string.no_internet_available, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void clear(){
        mArtist = null;
        mTracks = null;
        mAdapter.setArtists(mTracks);
    }

    public void setArtist(SimpleArtist artist){
        mArtist = artist;
        new TopTracksTask().execute(mArtist.getId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onTrackSelected(position, mTracks);
    }

    private class TopTracksTask extends AsyncTask<String, Void, ArrayList<SimpleTrack>> {
        @Override
        protected void onPreExecute() {
            //New search, clear list, hide empty message and show the loading
            mAdapter.setArtists(null);
            mEmptyList.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<SimpleTrack> doInBackground(String... params) {
            Map<String, Object> options = new HashMap<>();
            options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
            Tracks tracks = null;
            try{
                tracks = new SpotifyApi().getService().getArtistTopTrack(params[0], options);
            }
            catch (RetrofitError rex){
                Log.e(TAG, "Error on try get top tracks.", rex);
            }
            catch (Exception ex){
                Log.e(TAG, "Error on try get top tracks, verify connection.", ex);
            }
            if( ( tracks != null ) && ( ! tracks.tracks.isEmpty() ) ){
                ArrayList<SimpleTrack> list = new ArrayList<>();
                //Convert Tracks to SimpleTracks(Parcelable object)
                for(Track track : tracks.tracks){
                    list.add(new SimpleTrack(track));
                }
                return list;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SimpleTrack> tracks) {
            mTracks = new ArrayList<>();
            if( tracks != null ){
                mTracks.addAll(tracks);
            }
            showList(mTracks);
        }
    }
}
