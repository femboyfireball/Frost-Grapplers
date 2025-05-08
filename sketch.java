import java.lang.reflect.Constructor;
import java.util.*;

class Level {
    ArrayList<Platform> platforms;
    void Constructor(int platformCount) {
        platforms = new ArrayList<Platform>(platformCount);
    }

    /**
     * Adds a new platform to the level
     * @param platform Index of the platform to add
     * @param coordinates Coordinates for the platform (Left Edge X, Top Edge Y, Width, Height)
     * @param type Type of the platform (Normal, Sticky, ect)
     * @param color Color of the platform (R, G, B)
     */
    void addPlatform(int platform, int[] coordinates, String type, int[] color) {

        Platform newPlatform = new Platform( coordinates, "Normal", color );
        platforms.add(platform, newPlatform);
    }
}

class Platform {
    public int[]  platformCoordinates;
    public String platformType;
    public int[]  platformColor;

    Platform(int[] coordinates, String type, int[] color) {
        this.platformCoordinates = coordinates;
        this.platformType = type;
        this.platformColor = color;
    }
}
