int[][][] levelFloorHitboxes;
int level;

int playerX;
int playerY;
int pVelX;
int pVelY;
int playerWidth;
int playerHeight;
int jumpStrength = -15;
float playerSpeed = 5;
boolean isJumping = false;
boolean moveLeft = false;
boolean moveRight = false;

//Platforms
void setup() {
  fullScreen();
  
  playerX = width / 2 - playerWidth / 2;
  playerY = height - height / 5;
  pVelX = 0;
  pVelY = 0;
  playerWidth = 60;
  playerHeight = 120;

  isAirborne = false;
  direction = false; // False = Right, True = Left
  
  
  level = 0;
  levelFloorHitboxes = new int[][][] {
    { // Level 1
      { 0,    1000, 1920, 200 }, // Floor
      
      { 240,  800,  360,  40 },
      { 720,  500,  360,  40 },
      { 1200, 700,  480,  40 },
      { 120,  200,  240,  40 }
    },
    { // Level 2
      {  }
    }
  };
  
  
}

// Physics and movement
void draw() {
  background(150);
  updatePlayer();
  drawPlayer();
  drawPlatforms();
}

void updatePlayer() {
  
  // Horizontal movment
  if (moveLeft) {
    playerX -= pVelX;
  }
  if (moveRight) {
    playerX += pVelX;
  }

  // Gravity
  pVelY += Gravity;
  playerY += pVelY;

  // collision with platforms
  boolean onGround = false;
  int[][] currentLevelPlatforms = levelFloorHitboxes[level];
  for (int[] platforms : currentLevelPlatforms ) {
    
  }

}

void keyPressed() {
  switch (key) {
    case ESC:
      exit();
      break;
    case 'd':
      pVelX += 10;
      break;
    case 'a':
      pVelX -= 10;
      break;
    case 'w':
      pVelY -= 10;
      break;
    case 's':
      pVelY += 10;
      break;
  }
}
