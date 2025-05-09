import java.lang.reflect.Constructor;
import java.util.*;


class Player {

    public int xPosition;
    public int yPosition;
    public int xVelocity;
    public int yVelocity;

    public int bBoxWidth;
    public int bBoxHeight;
    
    
}

/**
 * Stores information about a platform
 * @author Daniel K
 */
class Platform {
    public int[]  platformCoordinates;
    public String platformType;
    public int[]  platformColor;

    /**
     * Creates the platform
     * @param coordinates Coordinates (Top left and bottom right corners)
     * @param type Type of the platform (Normal, Sticky, ect)
     * @param color Color of the platform (R, G, B)
     */
    Platform(int[] coordinates, String type, int[] color) {
        this.platformCoordinates = coordinates;
        this.platformType = type;
        this.platformColor = color;
    }

    /**
     * Detects if the player is colliding with a platform
     * @param playerX
     * @param playerY
     * @param playerWidth
     * @param playerHeight
     * @return A boolean[] representing which edge(s) are being collidded with. 0 (None), 1 (Top), 2 (Right), 3 (Bottom), 4 (Left)
     */
    boolean[] playerColliding(int playerX, int playerY, int playerWidth, int playerHeight) {

        boolean[] edgesColliding = new boolean[5];
        edgesColliding[0] = true;
        Arrays.fill(edgesColliding, false);

        if (playerY > this.platformCoordinates[1] && playerY < this.platformCoordinates[2]) {
            edgesColliding[0] = false;
            edgesColliding[1] = true;
        }
        if (playerX + playerWidth > this.platformCoordinates[0] && playerX + playerWidth < this.platformCoordinates[3]) {
            edgesColliding[0] = false;
            edgesColliding[2] = true;
        }
        if (playerY + playerHeight > this.platformCoordinates[1] && playerY + playerHeight < this.platformCoordinates[2]) {
            edgesColliding[0] = false;
            edgesColliding[3] = true;
        }
        if (playerX > this.platformCoordinates[0] && playerX < this.platformCoordinates[3]) {
            edgesColliding[0] = false;
            edgesColliding[4] = true;
        }

        return edgesColliding;
    }

}

/**
 * Stores information about the current level
 * @author Daniel K
 */
class Level {

    /**
     * Array of the platforms in this level
     */
    public ArrayList<Platform> platforms;

    /**
     * Create the level
     * @param platformCount Number of platforms in the level
     */
    Level(int platformCount) {
        this.platforms = new ArrayList<Platform>(platformCount);
    }

    /**
     * Adds a new platform to the level
     * @param platform Index of the platform to add
     * @param coordinates Coordinates for the platform (Top Left X, Top Left Y, Bottom Left X, Bottom Left Y)
     * @param type Type of the platform (Normal, Sticky, ect)
     * @param color Color of the platform (R, G, B)
     * @author Daniel K
     */
    void addPlatform(int platform, int[] coordinates, String type, int[] color) {

        Platform newPlatform = new Platform( coordinates, "Normal", color );
        platforms.add(platform, newPlatform);
    }

    void renderLevel() {

    }
}



class Main {
    public void start() {
        ArrayList<Level> levels = new ArrayList<Level>();
        levels.add(new Level(5));
        levels.add(new Level(6));

        for (Level level : levels) {
            for (int platform : level.platforms )
        }
    }
}

Main game = new Main();
game.start();
