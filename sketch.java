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
        this.positionX = 0;
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

        if (playerY > this.platformCoordinates[1] && playerY < this.platformCoordinates[2]) edgesColliding[1] = true;
        if (playerX + playerWidth > this.platformCoordinates[0] && playerX + playerWidth < this.platformCoordinates[3]) edgesColliding[2] = true;
        if (playerY + playerHeight > this.platformCoordinates[1] && playerY + playerHeight < this.platformCoordinates[2]) edgesColliding[3] = true;
        if (playerX > this.platformCoordinates[0] && playerX < this.platformCoordinates[3]) edgesColliding[4] = true;

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

    // Render the current level (Index 0 is the current level)
    game.levels.get(0).renderLevel();
    // Render the player
    game.player.renderEntity();
    
    // Apply the velocity limitis to the player
    if (Math.abs(game.player.velocityY) > game.player.maxVelocityY) {
        if (game.player.velocityY < 0) game.player.velocityY = game.player.maxVelocityY * -1;
        else game.player.velocityY = game.player.maxVelocityY;
    }
    if (Math.abs(game.player.velocityX) > game.player.maxVelocityX) {
        if (game.player.velocityX < 0) game.player.velocityX = game.player.maxVelocityX * -1;
        else game.player.velocityX = game.player.maxVelocityX;
    }

    // Update player vertical position and velocity
    game.player.positionY = constrain(game.player.positionY, 0, height - game.player.boundBoxHeight);
    game.player.velocityY += 2;
    game.player.positionY += game.player.velocityY;
    // Why did I use terneries? WHYYYYYYYYYYYYYYYYYYYYYYYYYY-
    game.player.velocityY = game.player.positionY == 0 ? 0 : game.player.positionY >= height - game.player.boundBoxHeight ? 0 : game.player.velocityY;

    // Update player horizontal position and velocity
    game.player.positionX = constrain(game.player.positionX, 0, width - game.player.boundBodWidth);
    game.player.positionX += game.player.velocityX;
    game.player.velocityX = game.player.positionX == 0 ? 0 : game.player.positionX >= width - game.player.boundBoxHeight ? 0 : game.player.velocityX;

    // Uses Collections.rotate to ensure the current level is updated to index 0
    if (game.player.positionY <= 0) {
        Collections.rotate(game.levels, 1);
        game.player.velocityY = 0;
        game.player.positionY = height / 2;
    }

    // Uses Collections.rotate to ensure the current level is updated to index 0
    // Also makes sure you're not at the bottom already
    if (game.player.positionY >= height - game.player.boundBoxHeight && game.levels.get(0).levelID > 0) {
        Collections.rotate(game.levels, -1);
        game.player.velocityY = 0;
        game.player.positionY = height / 2;
    }
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
        levels.add(new Level(5, 0));
        levels.add(new Level(7, 1));

        int[] greyPlatform = { 100, 100, 100 };
        int[] icePlatform =  { 10,  200, 240 };

        // Certainly one of the ways of all time to create levels
        levels.get(0).addPlatform(new int[] { 0,     900,   1920,  180 }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 240,   720,   360,   36  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 720,   450,   150,   360 }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 1200,  630,   480,   36  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 120,   180,   240,   36  }, "Normal", greyPlatform);

        levels.get(1).addPlatform(new int[] { 0,     600,   800,   50  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 50,    450,   150,   20  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 50,    300,   20,    150 }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 150,   300,   100,   20  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 300,   400,   200,   20  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 600,   600,   100,   50  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 0,     800,   200,   20  }, "Ice",    icePlatform );

    }
}