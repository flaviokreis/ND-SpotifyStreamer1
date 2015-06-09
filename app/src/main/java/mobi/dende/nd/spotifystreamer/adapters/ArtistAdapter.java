package mobi.dende.nd.spotifystreamer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import mobi.dende.nd.spotifystreamer.R;
import mobi.dende.nd.spotifystreamer.models.SimpleArtist;

/**
 * Adapter of artists, necessary to show the list on listview.
 */
public class ArtistAdapter extends BaseAdapter {
    private static final String TAG = "ArtistAdapter";

    private List<SimpleArtist> mList;

    private final Context mContext;
    private final LayoutInflater inflater;

    public ArtistAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setArtists( List<SimpleArtist> artists ){
        if( artists != null ){
            mList = new ArrayList<>();
            mList.addAll(artists);
        }
        else{
            mList = null;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ( mList != null ) ? mList.size() : 0;
    }

    @Override
    public SimpleArtist getItem(int position) {
        return ( mList != null ) ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_artist, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView)convertView.findViewById(R.id.artist_image);
            viewHolder.name  = (TextView)convertView.findViewById(R.id.artist_name);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.clear();

        SimpleArtist artist = getItem(position);

        viewHolder.name.setText(artist.getName());

        Log.d( TAG, "Artist: " + artist.getName() );

        if( artist.getImageUrl() != null ){
            Picasso.with(mContext)
                    .load(artist.getImageUrl())
                    .into(viewHolder.image);
        }
        else{
            viewHolder.image.setImageResource(R.mipmap.notes);
        }

        return convertView;
    }

    // Reference http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    static class ViewHolder {
        ImageView image;
        TextView name;

        public void clear(){
            image.setImageBitmap(null);
            name.setText("");
        }
    }

}
