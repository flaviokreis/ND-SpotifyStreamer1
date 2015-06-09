package mobi.dende.nd.spotifystreamer.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import mobi.dende.nd.spotifystreamer.R;
import mobi.dende.nd.spotifystreamer.models.SimpleTrack;

/**
 * Adapter of tracks, necessary to show the list on listview.
 */
public class TracksAdapter extends BaseAdapter {
    private ArrayList<SimpleTrack> mList;

    private final Context mContext;
    private final LayoutInflater inflater;

    public TracksAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setArtists( ArrayList<SimpleTrack> tracks ){
        if( tracks != null ){
            mList = new ArrayList<>();
            mList.addAll(tracks);
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
    public SimpleTrack getItem(int position) {
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
            convertView = inflater.inflate(R.layout.item_track, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView)convertView.findViewById(R.id.track_image);
            viewHolder.name  = (TextView)convertView.findViewById(R.id.track_name);
            viewHolder.album = (TextView)convertView.findViewById(R.id.album_name);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.clear();

        SimpleTrack track = getItem(position);

        viewHolder.name.setText(track.getName());
        viewHolder.album.setText(track.getAlbumName());

        if( ! TextUtils.isEmpty(track.getAlbumImageUrl())  ){
            //Get the image smaller image
            Picasso.with(mContext)
                    .load(track.getAlbumSmallImageUrl())
                    .into(viewHolder.image);
        }

        return convertView;
    }

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    static class ViewHolder {
        ImageView image;
        TextView  name;
        TextView  album;

        public void clear(){
            image.setImageBitmap(null);
            name.setText("");
            album.setText("");
        }
    }

}
