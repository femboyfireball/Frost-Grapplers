import java.util.*;

// The *only* point of this block of code is to make this easier to work with in VSC
// I did find a "processing language support" plugin, but it's very shit
// It just gives syntax highlighting, zero docs, zero autocomplete...
// Oh, make sure to comment out this chunk of code before running this. Processing doesn't like it
// vvv---------- IGNORE ----------vvv
int height = 1080;
int width = 1920;
char key;

void fill(int one, int two, int three) { }
void rect(int one, int two, int three, int four) { }
int constrain(int one, int two, int three) { }
void size(int one, int two) { }
void background(int one) { }
void fullScreen() { }
// ^^^---------- IGNORE ----------^^^

/**
 * Mostly a template class, stores information about entities
 */
class Entity {

    // Position of the entity
    int positionX;
    int positionY;
    // Velocity of the entity (Positive = right/down, negative = left/up)
    int velocityX;
    int velocityY;
    // Maximum velocity of the entity
    int maxVelocityX;
    int maxVelocityY;
    // Bounding box of the entity (Currently the render box too)
    int boundBodWidth;
    int boundBoxHeight;
    // Color of the entity's box
    int[] Color;

    void renderEntity() {
        fill(Color[0], Color[1], Color[2]);
        rect(positionX, positionY, boundBodWidth, boundBoxHeight);
    }
}

/**
 * Stores information about the player
 */
class Player extends Entity {

    // Health of the player
    int health;

    // isAirborne will prevent double jumping and allow wall gripping
    // isAttacking will help prevent attacking multiple times at once
    boolean isAirborne;
    boolean isAttacking;

    // Constructor, sets the defaults
    Player() {
        this.health = 100;
        this.isAirborne = false;
        this.isAttacking = false;
        
        // Initalize entity variables. Position, velocity, bounding box, ect
        this.velocityX = 0;
        this.velocityY = 0;
        this.positionX = width / 2;
        this.positionY =  height / 2;
        this.maxVelocityX = 100;
        this.maxVelocityY = 100;
        this.boundBodWidth = 50;
        this.boundBoxHeight = 100;

        // Sky blue
        this.Color = new int[] { 20, 140, 200 };
    }
}

/**
 * Stores information about enemies
 */
class Enemy extends Entity {
    String target;
}

/**
 * Stores information about a platform
 */
class Platform {
    public int[]  platformCoordinates;
    public String platformType;
    public int[]  platformColor;

    /**
     * Constructs a platform
     * @param coordinates Coordinates (Top Left X, Top Left Y, Width, Height) for the platform
     * @param type Gives the platform a type identifier
     * @param platformColor Color for the platform
     */
    Platform(int[] coordinates, String type, int[] platformColor) {
        this.platformCoordinates = coordinates;
        this.platformType = type;
        this.platformColor = platformColor;
    }

    /**
     * Check if a given entity is colliding with this platform
     * @param playerX Entity X position
     * @param playerY Entity Y position
     * @param playerWidth Entity width
     * @param playerHeight Entity height
     * @return Boolean[] representing which edge(s) are colliding
     */
    boolean[] playerColliding(int playerX, int playerY, int playerWidth, int playerHeight) {
        boolean[] edgesColliding = new boolean[5];
        Arrays.fill(edgesColliding, false);

        if (playerY > this.platformCoordinates[1] && playerY < this.platformCoordinates[1] + this.platformCoordinates[3]) {
            edgesColliding[1] = true;
            println("True");
        }
        if (playerX + playerWidth > this.platformCoordinates[0] && playerX + playerWidth < this.platformCoordinates[0] + this.platformCoordinates[2]) edgesColliding[2] = true;
        if (playerY + playerHeight > this.platformCoordinates[1] && playerY + playerHeight < this.platformCoordinates[1] + this.platformCoordinates[3]) edgesColliding[3] = true;
        if (playerX > this.platformCoordinates[0] && playerX < this.platformCoordinates[0] + this.platformCoordinates[2]) edgesColliding[4] = true;

        if (!edgesColliding[1] && !edgesColliding[2] && !edgesColliding[3] && !edgesColliding[4]) {
            edgesColliding[0] = true; // No collision
        }

        return edgesColliding;
    }
}

/**
 * Stores information about a level
 * @see Platform Platform data
 */
class Level {
    /**
     * ArrayList of platforms in this level
     */
    public ArrayList<Platform> platforms;
    /**
     * ID of the level
     */
    public int levelID;

    /**
     * Level constructor
     * @param platformCount Number of platforms in the level
     * @param newLevelID LevelID for this level
     */
    Level(int platformCount, int newLevelID) {
        this.platforms = new ArrayList<Platform>(platformCount);
        this.levelID = newLevelID;
    }

    /**
     * Add a new platform to this level
     * @param coordinates Coordinates (Top Left X, Top Left Y, Width, Height) for the platform
     * @param type Gives the platform a type identifier
     * @param platformColor Color for the platform
     */
    void addPlatform(int[] coordinates, String type, int[] platformColor) {
        platforms.add(new Platform( coordinates, type, platformColor ));
    }

    /**
     * Render this level
     */
    void renderLevel() {
        for (Platform platform : platforms) {
            fill(platform.platformColor[0], platform.platformColor[1], platform.platformColor[2]);
            rect(platform.platformCoordinates[0],
                    platform.platformCoordinates[1],
                    platform.platformCoordinates[2], // Width
                    platform.platformCoordinates[3]  // Height
            );
        }
    }
}

// Ok I'm ngl the setup function was made by Gemini
Main game;

void setup() {
    size(1920, 1080); // Or your desired size
    fullScreen();
    game = new Main();
    game.start();
    
}

// Why am I doing my main update loop here? Idk, it's easy ig
// oh god why am I doing collision logic here? I have a collision function above...
// I'm losing my mind
void draw() {
    background(0); // Clear the background each frame

    game.updateGame();
}

// There are better ways to handle key input ._.
void keyPressed() {
    if (key == 'w') {
        game.player.velocityY -= 25;
    }
    if (key == 's') {
        game.player.velocityY += 10;
    }
    if (key == 'a') {
        game.player.velocityX -= 10;
    }
    if (key == 'd') {
        game.player.velocityX += 10;
    }
}

// I originally wanted the main loop here, but moved it to draw() because of time crunch
class Main {

    public ArrayList<Level> levels = new ArrayList<Level>();
    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public Player player = new Player();

    public void start() {
        levels.add(new Level(7, 1));
        levels.add(new Level(5, 0));

        int[] greyPlatform = { 100, 100, 100 };
        int[] icePlatform =  { 10,  200, 240 };

        // Certainly one of the ways of all time to create levels
        levels.get(1).addPlatform(new int[] { 0,     900,   1920,  180 }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 240,   720,   360,   36  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 720,   450,   150,   360 }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 1200,  630,   480,   36  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 120,   180,   240,   36  }, "Normal", greyPlatform);

        levels.get(0).addPlatform(new int[] { 0,     600,   800,   50  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 50,    450,   150,   20  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 50,    300,   20,    150 }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 150,   300,   100,   20  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 300,   400,   200,   20  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 600,   600,   100,   50  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 0,     800,   200,   20  }, "Ice",    icePlatform );

    }

    public void updatePlayer() {

        player.velocityX *= 0.9;
        player.velocityY *= 1.05;
        player.velocityY -= 1;

        player.positionX = constrain(player.positionX + player.velocityX, 0, width);
        player.positionY = constrain(player.velocityY + player.velocityY, 0, height);

        /*// Platform collision logic
        for (Platform platform : levels.get(0).platforms) {
            if (platform.playerColliding(player.positionY, player.positionX, player.boundBodWidth, player.boundBoxHeight)[1]) {
                // Top edge of the player is colliding
                player.velocityY = 0;
                player.positionY = platform.platformCoordinates[1] + player.boundBoxHeight;
            }
            if (platform.playerColliding(player.positionY, player.positionX, player.boundBodWidth, player.boundBoxHeight)[2]) {
                // Right edge of the player is colliding
                player.velocityX = 0;
                player.positionX = platform.platformCoordinates[0] - player.boundBodWidth;
            }
            if (platform.playerColliding(player.positionY, player.positionX, player.boundBodWidth, player.boundBoxHeight)[3]) {
                // Bottom edge of the player is colliding
                player.velocityY = 0;
                player.positionY = platform.platformCoordinates[1] + platform.platformCoordinates[3];
            }
            if (platform.playerColliding(player.positionY, player.positionX, player.boundBodWidth, player.boundBoxHeight)[4]) {
                // Left edge of the player is colliding
                player.velocityX = 0;
                player.positionX = platform.platformCoordinates[0] + platform.platformCoordinates[2];
            }
        }
        */
        if (player.positionY <= 0 + player.boundBoxHeight) {
            Collections.rotate(levels, 1);
            player.positionY = height - 1;
        } else if (player.positionY >= height - player.boundBoxHeight) {
            Collections.rotate(levels, -1);
            player.positionY = 1;
        }
    }

    public void updateGame() {
        levels.get(0).renderLevel();
        updatePlayer();
        player.renderEntity();
    }

}
