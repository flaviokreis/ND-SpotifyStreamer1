package mobi.dende.nd.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import mobi.dende.nd.spotifystreamer.adapters.ArtistAdapter;
import mobi.dende.nd.spotifystreamer.models.SimpleArtist;
import mobi.dende.nd.spotifystreamer.utils.NetworkUtils;


/**
 * This fragment search the artist and show on list.
 * Get this list on Spotify API {see https://developer.spotify.com/web-api/search-item/}
 *
 * Parser json from Spotify API using Spotify Web Api Android {see https://github.com/kaaes/spotify-web-api-android}
 */
public class SearchArtistFragment extends Fragment implements SearchView.OnQueryTextListener,
        OnItemClickListener{

    private static final String TAG = SearchArtistFragment.class.getSimpleName();

    private SearchView  mSearchView;
    private ListView    mListView;
    private TextView    mEmptyList;
    private ProgressBar mLoading;

    private ArtistAdapter mAdapter;

    private InputMethodManager imm;

    private ArrayList<SimpleArtist> mArtists;

    public SearchArtistFragment() {/* no code */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment
        // Reference: http://developer.android.com/guide/topics/resources/runtime-changes.html
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save searched artists to show on list if the screen rotate
        if( mArtists != null ){
            outState.putParcelableArrayList("artists", mArtists);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        //Restore the searched artists, if possible.
        if( savedInstanceState != null ){
            mArtists = savedInstanceState.getParcelableArrayList("artists");
            showList();
        }
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Show the artists. If the list is null or empty, show the empty message
     */
    private void showList(){
        if(mArtists != null){
            mAdapter.setArtists(mArtists);
            mLoading.setVisibility(View.GONE);
            if( mArtists == null || mArtists.isEmpty() ){
                mEmptyList.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.no_artist_found_message,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_seach_artist, container, false);

        mSearchView     = (SearchView)layout.findViewById(R.id.search_artist);
        mListView       = (ListView) layout.findViewById(R.id.list_artist);
        mEmptyList      = (TextView) layout.findViewById(R.id.empty_list);
        mLoading        = (ProgressBar)layout.findViewById(R.id.loading);

        mAdapter = new ArtistAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(SearchArtistFragment.this);

        mSearchView.setOnQueryTextListener(SearchArtistFragment.this);

        imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleArtist artist = mAdapter.getItem(position);
        if( artist != null ){
            //Go to next screen, show the top tracks
            Intent intent = new Intent( getActivity(), TopTracksActivity.class );
            //Artist reference, in this case artist is parcelable object
            intent.putExtra(TopTracksActivity.EXTRA_ARTIST, artist);
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String search) {
        if( !TextUtils.isEmpty(search) ){
            if(NetworkUtils.isNetworkAvailable(getActivity())){
                new ArtistsTask().execute(search);
                // On click search, hide keyboard
                // Reference: http://stackoverflow.com/questions/7409288/how-to-dismiss-keyboard-in-android-searchview
                mSearchView.clearFocus();
            }
            else{
                Toast.makeText(getActivity(), R.string.no_internet_available, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private class ArtistsTask extends AsyncTask<String, Void, ArrayList<SimpleArtist>>{
        private ArtistsPager mArtistsPager;

        @Override
        protected void onPreExecute() {
            //New search, clear list, hide empty message and show the loading
            mAdapter.setArtists(null);
            mEmptyList.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<SimpleArtist> doInBackground(String... params) {
            try{
                mArtistsPager = new SpotifyApi().getService().searchArtists(params[0]);
            }
            catch (Exception ex){
                Log.e(TAG, "Error on try get artists, verify connection.", ex);
            }

            if( ( mArtistsPager != null ) && ( ! mArtistsPager.artists.items.isEmpty() ) ){
                ArrayList<SimpleArtist> list = new ArrayList<>();
                //Convert Artist to SimpleArtist(Parcelable object)
                for(Artist artist : mArtistsPager.artists.items){
                    list.add(new SimpleArtist(artist));
                }
                return list;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SimpleArtist> artists) {
            mArtists = new ArrayList<>();
            if(artists != null){
                mArtists.addAll(artists);
            }
            showList();
        }
    }
}
