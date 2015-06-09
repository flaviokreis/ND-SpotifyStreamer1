package mobi.dende.nd.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Track simplified obejct. Need implement Parcelable to save and restore list on Activity and Fragment.
 */
public class SimpleTrack implements Parcelable {
    private String artistId;
    private String artistName;

    private String albumId;
    private String albumName;
    private String albumImageUrl;

    private String id;
    private String name;
    private String previewUrl;

    public SimpleTrack(Track track){
        id = track.id;
        name = track.name;
        previewUrl = track.preview_url;

        albumId = track.album.id;
        albumName = track.album.name;
        albumImageUrl = track.album.images.get(0).url;

        artistId = track.artists.get(0).id;
        artistName = track.artists.get(0).name;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
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

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    protected SimpleTrack(Parcel in) {
        artistId = in.readString();
        artistName = in.readString();
        albumId = in.readString();
        albumName = in.readString();
        albumImageUrl = in.readString();
        id = in.readString();
        name = in.readString();
        previewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistId);
        dest.writeString(artistName);
        dest.writeString(albumId);
        dest.writeString(albumName);
        dest.writeString(albumImageUrl);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(previewUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SimpleTrack> CREATOR = new Parcelable.Creator<SimpleTrack>() {
        @Override
        public SimpleTrack createFromParcel(Parcel in) {
            return new SimpleTrack(in);
        }

        @Override
        public SimpleTrack[] newArray(int size) {
            return new SimpleTrack[size];
        }
    };
}
