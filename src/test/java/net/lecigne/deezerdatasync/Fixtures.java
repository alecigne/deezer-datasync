package net.lecigne.deezerdatasync;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.album.AlbumId;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.track.Track;

public final class Fixtures {

  private Fixtures() {
  }

  public static DeezerData getTestData() {
    return DeezerData.builder()
        .albums(albums())
        .artists(artists())
        .playlistInfos(playlistInfos())
        .playlists(List.of(ambientPlaylist()))
        .build();
  }

  public static DeezerData getServiceTestData() {
    return DeezerData.builder()
        .albums(albums())
        .artists(artists())
        .playlistInfos(List.of(ambientPlaylistInfo()))
        .playlists(List.of(ambientPlaylist()))
        .build();
  }

  public static List<Album> albums() {
    var transcend = Album.builder()
        .albumId(AlbumId.of(11301648))
        .artist("Benedictine Nuns of St. Cecilia's Abbey")
        .title("Transcend")
        .creationTimeUtc(Instant.parse("2023-10-28T10:50:05Z"))
        .build();
    var templeOfTheMeltingDawn = Album.builder()
        .albumId(AlbumId.of(210071572L))
        .artist("Steve Roach")
        .title("Temple Of The Melting Dawn")
        .creationTimeUtc(Instant.parse("2023-10-21T04:52:14Z"))
        .build();
    var neogene = Album.builder()
        .albumId(AlbumId.of(286558852L))
        .artist("Jarguna")
        .title("Neogene")
        .creationTimeUtc(Instant.parse("2023-10-10T13:29:34Z"))
        .build();
    var ifMemoryServesMe = Album.builder()
        .albumId(AlbumId.of(448896205L))
        .artist("Coral Sea")
        .title("If Memory Serves Me")
        .creationTimeUtc(Instant.parse("2023-10-09T20:01:16Z"))
        .build();
    var sakura = Album.builder()
        .albumId(AlbumId.of(307484127L))
        .artist("Susumu Yokota")
        .title("Sakura")
        .creationTimeUtc(Instant.parse("2023-09-23T21:09:09Z"))
        .build();
    var soundtrackForImaginarySpaces = Album.builder()
        .albumId(AlbumId.of(47563122L))
        .artist("Tonepoet")
        .title("Soundtrack for Imaginary Spaces")
        .creationTimeUtc(Instant.parse("2023-09-21T19:48:40Z"))
        .build();
    var replica = Album.builder()
        .albumId(AlbumId.of(14393696L))
        .artist("Oneohtrix Point Never")
        .title("Replica")
        .creationTimeUtc(Instant.parse("2023-09-11T20:16:19Z"))
        .build();
    var chapter3 = Album.builder()
        .albumId(AlbumId.of(462719925L))
        .artist("Harbor Tea Rooms")
        .title("Chapter 3")
        .creationTimeUtc(Instant.parse("2023-08-11T00:49:37Z"))
        .build();
    var cityInTheClouds = Album.builder()
        .albumId(AlbumId.of(276610982L))
        .artist("Astropilot")
        .title("City in the Clouds")
        .creationTimeUtc(Instant.parse("2023-07-04T21:22:21Z"))
        .build();
    var lunaticHarness = Album.builder()
        .albumId(AlbumId.of(302017L))
        .artist("µ-Ziq")
        .title("Lunatic Harness")
        .creationTimeUtc(Instant.parse("2023-06-13T19:51:49Z"))
        .build();
    return List.of(
        transcend,
        templeOfTheMeltingDawn,
        neogene,
        ifMemoryServesMe,
        sakura,
        soundtrackForImaginarySpaces,
        replica,
        chapter3,
        cityInTheClouds,
        lunaticHarness
    );
  }

  public static List<Artist> artists() {
    var jarguna = Artist.builder()
        .deezerId(1652024)
        .name("Jarguna")
        .creationTimeUtc(Instant.parse("2023-09-23T06:45:48Z"))
        .build();
    var oneohtrixPointNever = Artist.builder()
        .deezerId(406857)
        .name("Oneohtrix Point Never")
        .creationTimeUtc(Instant.parse("2023-09-11T20:28:59Z"))
        .build();
    var anaRoxanne = Artist.builder()
        .deezerId(48925411)
        .name("Ana Roxanne")
        .creationTimeUtc(Instant.parse("2023-09-11T19:49:54Z"))
        .build();
    var chrisCoco = Artist.builder()
        .deezerId(297034)
        .name("Chris Coco")
        .creationTimeUtc(Instant.parse("2023-09-05T06:28:50Z"))
        .build();
    var symphoCat = Artist.builder()
        .deezerId(1653842)
        .name("SymphoCat")
        .creationTimeUtc(Instant.parse("2023-07-01T21:37:28Z"))
        .build();
    var hammock = Artist.builder()
        .deezerId(60267)
        .name("Hammock")
        .creationTimeUtc(Instant.parse("2023-05-07T16:48:28Z"))
        .build();
    var spyroGyra = Artist.builder()
        .deezerId(14567)
        .name("Spyro Gyra")
        .creationTimeUtc(Instant.parse("2023-04-30T21:37:45Z"))
        .build();
    var louigiVerona = Artist.builder()
        .deezerId(369630)
        .name("Louigi Verona")
        .creationTimeUtc(Instant.parse("2023-04-03T20:24:38Z"))
        .build();
    var markusGuentner = Artist.builder()
        .deezerId(131012)
        .name("Markus Guentner")
        .creationTimeUtc(Instant.parse("2023-04-03T20:14:36Z"))
        .build();
    var heatheredPearls = Artist.builder()
        .deezerId(4092148)
        .name("Heathered Pearls")
        .creationTimeUtc(Instant.parse("2023-03-29T21:27:50Z"))
        .build();
    return List.of(
        jarguna,
        oneohtrixPointNever,
        anaRoxanne,
        chrisCoco,
        symphoCat,
        hammock,
        spyroGyra,
        louigiVerona,
        markusGuentner,
        heatheredPearls
    );
  }

  public static List<PlaylistInfo> playlistInfos() {
    var acidHouseAcidTechno = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1327063625L))
        .title("Acid House / Acid Techno")
        .duration(Duration.ofSeconds(3807))
        .nbTracks(11)
        .fans(1)
        .creationTimeUtc(Instant.parse("2015-08-04T04:37:15Z"))
        .build();
    var ambient = ambientPlaylistInfo();
    var braindanceIllbientDrillAndBass = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1327042525L))
        .title("Braindance / Illbient / Drill and Bass")
        .duration(Duration.ofSeconds(8355))
        .nbTracks(28)
        .fans(2)
        .creationTimeUtc(Instant.parse("2015-08-04T03:46:00Z"))
        .build();
    var breakbeatHardcore = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1350754875L))
        .title("Breakbeat Hardcore")
        .duration(Duration.ofSeconds(1466))
        .nbTracks(5)
        .fans(0)
        .creationTimeUtc(Instant.parse("2015-08-24T12:26:22Z"))
        .build();
    var chansons = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(10937682782L))
        .title("Chansons")
        .duration(Duration.ofSeconds(479))
        .nbTracks(2)
        .fans(0)
        .creationTimeUtc(Instant.parse("2022-12-07T00:21:00Z"))
        .build();
    var chicagoHouse = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1349861855L))
        .title("Chicago House")
        .duration(Duration.ofSeconds(2398))
        .nbTracks(6)
        .fans(0)
        .creationTimeUtc(Instant.parse("2015-08-23T17:21:34Z"))
        .build();
    var classical = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1260222531L))
        .title("Classical")
        .duration(Duration.ofSeconds(98786))
        .nbTracks(411)
        .fans(0)
        .creationTimeUtc(Instant.parse("2015-06-05T22:21:05Z"))
        .build();
    var deepHouseSoulfulHouse = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1260225631L))
        .title("Deep House/Soulful House")
        .duration(Duration.ofSeconds(37981))
        .nbTracks(92)
        .fans(3)
        .creationTimeUtc(Instant.parse("2015-06-05T22:25:00Z"))
        .build();
    var disco = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1327051755L))
        .title("Disco")
        .duration(Duration.ofSeconds(791))
        .nbTracks(2)
        .fans(0)
        .creationTimeUtc(Instant.parse("2015-08-04T04:07:24Z"))
        .build();
    var discoHouseFunkyHouse = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1284839775L))
        .title("Disco House/Funky House")
        .duration(Duration.ofSeconds(8144))
        .nbTracks(24)
        .fans(0)
        .creationTimeUtc(Instant.parse("2015-06-27T17:48:59Z"))
        .build();

    return List.of(
        acidHouseAcidTechno,
        ambient,
        braindanceIllbientDrillAndBass,
        breakbeatHardcore,
        chansons,
        chicagoHouse,
        classical,
        deepHouseSoulfulHouse,
        disco,
        discoHouseFunkyHouse
    );

  }

  public static PlaylistInfo ambientPlaylistInfo() {
    return PlaylistInfo.builder()
        .playlistId(PlaylistId.of(10616324822L))
        .title("Ambient")
        .duration(Duration.ofSeconds(296472))
        .nbTracks(527)
        .fans(1)
        .creationTimeUtc(Instant.parse("2022-08-14T12:14:58Z"))
        .build();
  }

  public static Playlist ambientPlaylist() {
    var filamentAndPlace = Track.builder()
        .deezerId(1324663722)
        .artist("Innesti")
        .title("Insouciant")
        .album("Filament and Place")
        .creationTimeUtc(Instant.parse("2022-08-14T12:16:03Z"))
        .build();
    var lapiz = Track.builder()
        .deezerId(1308346072)
        .artist("Igneous Flame")
        .title("Regenerative Shifts")
        .album("Lapiz")
        .creationTimeUtc(Instant.parse("2022-08-14T12:17:40Z"))
        .build();
    var ki = Track.builder()
        .deezerId(999070082)
        .artist("Igneous Flame")
        .title("Tomorrow with You")
        .album("Ki")
        .creationTimeUtc(Instant.parse("2022-08-14T12:18:31Z"))
        .build();
    var healingDreams = Track.builder()
        .deezerId(1770002677)
        .artist("charlie dreaming")
        .title("Dream #1 [Arrival]")
        .album("Healing Dreams")
        .creationTimeUtc(Instant.parse("2022-08-14T12:20:31Z"))
        .build();
    var sleepTones = Track.builder()
        .deezerId(1770002827)
        .artist("charlie dreaming")
        .title("Sweet Everything")
        .album("Sleep Tones")
        .creationTimeUtc(Instant.parse("2022-08-14T12:21:05Z"))
        .build();
    var noyaux = Track.builder()
        .deezerId(108604026)
        .artist("Benoit Pioulard")
        .title("Noyaux")
        .album("Noyaux")
        .creationTimeUtc(Instant.parse("2022-08-14T12:27:48Z"))
        .build();
    var nighttide = Track.builder()
        .deezerId(1065454402)
        .artist("Swartz et")
        .title("Night Ships (Sea Mist)")
        .album("Nighttide (10 Year Deluxe Edition)")
        .creationTimeUtc(Instant.parse("2022-08-14T12:30:38Z"))
        .build();
    var amidstTheCirclingSpires = Track.builder()
        .deezerId(72860896)
        .artist("Alio Die & Sylvi Alli")
        .title("The Inner Sea (Silver Sea)")
        .album("Amidst The Circling Spires")
        .creationTimeUtc(Instant.parse("2022-08-14T12:32:23Z"))
        .build();
    var deconsecratedAndPure = Track.builder()
        .deezerId(82715706)
        .artist("Alio Die")
        .title("Obliterated Alcove")
        .album("Deconsecrated and Pure")
        .creationTimeUtc(Instant.parse("2022-08-14T12:33:53Z"))
        .build();
    var interstice = Track.builder()
        .deezerId(1657130562)
        .artist("Aes Dana")
        .title("Period 10 _ Evocatory (Live)")
        .album("Interstice (Live Set)")
        .creationTimeUtc(Instant.parse("2022-08-14T12:35:41Z"))
        .build();
    List<Track> tracks = List.of(
        filamentAndPlace,
        lapiz,
        ki,
        healingDreams,
        sleepTones,
        noyaux,
        nighttide,
        amidstTheCirclingSpires,
        deconsecratedAndPure,
        interstice
    );
    return Playlist.builder()
        .description("Mostly drone & dark ambient.")
        .playlistId(PlaylistId.of(10616324822L))
        .title("Ambient")
        .duration(Duration.ofSeconds(296472))
        .nbTracks(10)
        .fans(1)
        .creationTimeUtc(Instant.parse("2022-08-14T12:14:58Z"))
        .tracks(tracks)
        .build();
  }


}
