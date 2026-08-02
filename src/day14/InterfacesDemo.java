public class InterfacesDemo {

    public static void main(String[] args) {
        Playable track = new AudioTrack("Lofi Beats");
        track.play();

        System.out.println("Highway limit: " + SpeedLimits.HIGHWAY_KMH + " km/h");
    }
}

interface Playable {
    void play(); // implicitly public abstract
}

class AudioTrack implements Playable {
    String title;

    AudioTrack(String title) {
        this.title = title;
    }

    @Override
    public void play() {
        System.out.println("Playing audio: " + title);
    }
}

interface SpeedLimits {
    int HIGHWAY_KMH = 120; // implicitly public static final
}
