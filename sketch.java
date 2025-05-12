import java.util.*;

class Entity {

    int positionX;
    int positionY;

    int velocityX;
    int velocityY;

    int maxVelocityX;
    int maxVelocityY;

    int boundBodWidth;
    int boundBoxHeight;

    int[] Color;

    void renderEntity() {
        fill(Color[0], Color[1], Color[2]);
        rect(positionX, positionY, boundBodWidth, boundBoxHeight);
    }
}

class Player extends Entity {
    int level;
    int health;

    boolean isAirborne;
    boolean isAttacking;

    Player() {
        this.level = 0;
        this.health = 100;
        this.isAirborne = false;
        this.isAttacking = false;
        
        this.velocityX = 0;
        this.velocityY = 0;
        this.positionX = 0;
        this.positionY = 0;
        this.maxVelocityX = 100;
        this.maxVelocityY = 100;
        this.boundBodWidth = 50;
        this.boundBoxHeight = 100;

        this.Color = new int[] { 20, 140, 200 };
    }
}

class Enemy extends Entity {
    String target;
}

class Platform {
    public int[]  platformCoordinates;
    public String platformType;
    public int[]  platformColor;

    Platform(int[] coordinates, String type, int[] platformColor) {
        this.platformCoordinates = coordinates;
        this.platformType = type;
        this.platformColor = platformColor;
    }

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

class Level {
    public ArrayList<Platform> platforms;

    Level(int platformCount) {
        this.platforms = new ArrayList<Platform>(platformCount);
    }

    void addPlatform(int[] coordinates, String type, int[] platformColor) {
        platforms.add(new Platform( coordinates, type, platformColor ));
    }

    void renderLevel() {
        for (Platform platform : platforms) {
            fill(platform.platformColor[0], platform.platformColor[1], platform.platformColor[2]);
            rect(platform.platformCoordinates[0],
                    platform.platformCoordinates[1],
                    platform.platformCoordinates[2] - platform.platformCoordinates[0], // Width
                    platform.platformCoordinates[2] - platform.platformCoordinates[1]  // Height
            );
        }
    }
}

Main game;
int level = 0;

void setup() {
    size(1920, 1080); // Or your desired size
    fullScreen();
    game = new Main();
    game.start();
    
}

void draw() {
    background(0); // Clear the background each frame
    switch (level) {
      case 0:
        game.levels.get(0).renderLevel();
    }
    game.player.renderEntity();
    
}

class Main {

    public ArrayList<Level> levels = new ArrayList<Level>();
    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public Player player = new Player();

    public void start() {
        levels.add(new Level(5));
        levels.add(new Level(7));

        int[] greyPlatform = { 100, 100, 100 };
        int[] icePlatform =  { 10,  200, 240 };

        levels.get(0).addPlatform(new int[] { 0,     900,   1920,  180 }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 240,   720,   360,   36  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 720,   450,   150,   360 }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 1200,  630,   480,   36  }, "Normal", greyPlatform);
        levels.get(0).addPlatform(new int[] { 120,   180,   240,   36  }, "Normal", greyPlatform);

        levels.get(1).addPlatform(new int[] { 0,     600,   800,   650  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 50,    450,   200,   470  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 50,    300,   70,    450  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 150,   300,   250,   320  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 300,   400,   500,   420  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 600,   600,   700,   650  }, "Normal", greyPlatform);
        levels.get(1).addPlatform(new int[] { 0,     800,   200,   820  }, "Ice",    icePlatform );
        
    }
}