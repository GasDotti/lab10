package it.unibo.oop.lab.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
        .sorted((a, b) -> {
            return a.getSongName().compareTo(b.getSongName());
        })
        .map(s -> s.songName);
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final List<String> s = new ArrayList<>();
        this.albums.forEach((k, v) -> {
            if(v.equals((Object) year)) {
                s.add(k);
            }
        });
        return s.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs
        .stream()
        .filter(s -> albumName.equals(s.getAlbumName().orElse("")))
        .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs
        .stream()
        .filter(s -> s.getAlbumName().isEmpty())
        .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return OptionalDouble.of(this.songs
            .stream()
            .filter(s -> albumName.equals(s.getAlbumName().orElse("")))
            .map(s -> s.getDuration())
            .reduce((a, b) -> a + b)
            .get() / this.countSongs(albumName));
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(this.songs
        .stream()
        .max((Song s1, Song s2) -> Double.compare(s1.getDuration(), s2.getDuration()))
        .get()
        .getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return Optional.empty();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
