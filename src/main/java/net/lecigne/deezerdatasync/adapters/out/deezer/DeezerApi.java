package net.lecigne.deezerdatasync.adapters.out.deezer;

import static net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig.OBJECT_MAPPER;

import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig.DeezerConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface DeezerApi {

  @GET("/user/{userId}/albums")
  Call<DeezerWrapper<AlbumDto>> getAlbums(@Path("userId") long userId, @Query("index") int index);

  @GET("/user/{userId}/artists")
  Call<DeezerWrapper<ArtistDto>> getArtists(@Path("userId") long userId, @Query("index") int index);

  @GET("/user/{userId}/playlists")
  Call<DeezerWrapper<PlaylistInfoDto>> getPlaylists(@Path("userId") long userId, @Query("index") int index);

  @GET("/playlist/{playlistId}")
  Call<PlaylistDto> getPlaylist(@Path("playlistId") long playlistId, @Query("index") int index);

  static DeezerApi init(DeezerConfig deezerConfig) {
    return new Retrofit.Builder()
        .baseUrl(deezerConfig.getUrl())
        .client(new OkHttpClient().newBuilder()
            .addInterceptor(chain -> {
              Request original = chain.request();
              HttpUrl url = original.url().newBuilder()
                  .addQueryParameter("access_token", deezerConfig.getToken())
                  .addQueryParameter("limit", String.valueOf(deezerConfig.getMaxResults()))
                  .build();
              Request request = original.newBuilder().url(url).build();
              return chain.proceed(request);
            })
            .addInterceptor(new RateLimitInterceptor(deezerConfig.getRateLimit()))
            .build())
        .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
        .build()
        .create(DeezerApi.class);
  }

}
