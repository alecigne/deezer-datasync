package net.lecigne.deezerdatasync.repository.deezer;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.OBJECT_MAPPER;

import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.DeezerProfile;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface DeezerClient {

  @GET("/user/{userId}/albums")
  Call<DeezerWrapper<AlbumDto>> getAlbums(@Path("userId") String userId, @Query("index") int index);

  @GET("/user/{userId}/artists")
  Call<DeezerWrapper<ArtistDto>> getArtists(@Path("userId") String userId, @Query("index") int index);

  @GET("/user/{userId}/playlists")
  Call<DeezerWrapper<PlaylistInfoDto>> getPlaylists(@Path("userId") String userId, @Query("index") int index);

  @GET("/playlist/{playlistId}")
  Call<PlaylistDto> getPlaylist(@Path("playlistId") String playlistId, @Query("index") int index);

  static DeezerClient init(DeezerProfile deezerProfile) {
    return new Retrofit.Builder()
        .baseUrl(deezerProfile.getUrl())
        .client(new OkHttpClient().newBuilder()
            .addInterceptor(chain -> {
              Request original = chain.request();
              HttpUrl url = original.url().newBuilder()
                  .addQueryParameter("access_token", deezerProfile.getToken())
                  .addQueryParameter("limit", String.valueOf(Math.max(deezerProfile.getLimit(), 100)))
                  .build();
              Request request = original.newBuilder().url(url).build();
              return chain.proceed(request);
            })
            .build())
        .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
        .build()
        .create(DeezerClient.class);
  }

}
