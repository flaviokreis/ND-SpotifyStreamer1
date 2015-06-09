package mobi.dende.nd.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Artist simplified obejct. Need implement Parcelable to save and restore list on Activity and Fragment.
 */
public class SimpleArtist implements Parcelable {
    private String id;
    private String name;
    private String imageUrl;

    public SimpleArtist(Artist artist){
        this.id = artist.id;
        this.name = artist.name;
        if( ( artist.images != null ) && ( ! artist.images.isEmpty() ) ){
            for(Image image : artist.images){
                if( (image.width >= 150) && (image.width <= 250) ){ //200px +- 50px tolerance
                    this.imageUrl = image.url;
                    break;
                }
            }
            //If not get image between 150px and 250px, get image with any size
            if(TextUtils.isEmpty(this.imageUrl)){
                this.imageUrl = artist.images.get(0).url;
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected SimpleArtist(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    // Create parcelable using: http://www.parcelabler.com/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SimpleArtist> CREATOR = new Parcelable.Creator<SimpleArtist>() {
        @Override
        public SimpleArtist createFromParcel(Parcel in) {
            return new SimpleArtist(in);
        }

        @Override
        public SimpleArtist[] newArray(int size) {
            return new SimpleArtist[size];
        }
    };
}
