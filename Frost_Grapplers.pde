int[][][] levelFloorHitboxes;
int level;

int playerX;
int playerY;
int pVelocityX;
int pVelocityY;
int playerWidth;
int playerHeight;

int jumpStrength = -15;
float Gravity = 5;
float playerSpeed = 5;
boolean isJumping = false;
boolean isAirborne;
boolean direction;
boolean moveLeft = false;
boolean moveRight = false;

//platform
void setup() {
  fullScreen();
  
  playerX = width / 2 - playerWidth / 2;
  playerY = height - height / 5;
  pVelocityX = 0;
  pVelocityY = 0;
  playerWidth = 60;
  playerHeight = 120;

  isAirborne = false;
  direction = false; // False = Right, True = Left
  
  
  level = 0;
  levelFloorHitboxes = new int[][][] {
    { // Level 1
      { 0,    900,  1920, 180 }, // Floor
      
      { 240,  720,  360,  36  },
      { 720,  450,  360,  36  },
      { 1200, 630,  480,  36  },
      { 120,  180,  240,  36  }
    },
    { // Level 2
      { 0,   600, 800, 50  },
      { 50,  450, 150, 20  },
      { 50,  300, 20,  150 },
      { 150, 300, 100, 20  },
      { 300, 400, 200, 20  },
    }
  };
  
  
}

// Physics and movement
void draw() {
  background(150);
  updatePlayer();
  drawPlayer();
  drawplatform();
}

void updatePlayer() {
  
  // Horizontal movment
  playerX += pVelocityX;

  // Gravity
  pVelocityY += Gravity;
  playerY += pVelocityY;

  // collision with platforms
  boolean onGround = false;
  int[][] currentLevelplatform = levelFloorHitboxes[level];
  for (int[] platform : currentLevelplatform ) {
    int platformX = platform[0];
    int platformY = platform[1];
    int platformWidth = platform[2];
    int platformHeight = platform[3];
    
    //check for horizontal over lap
    if (playerX + playerWidth > platformX && playerX < platformX + platformWidth) {
      //check for virtical collision from above
      if (playerY + playerHeight > platformY && playerX < platformX + platformWidth) {
        playerY = platformY - playerHeight;
        pVelocityY = 0;
        isJumping = false;
        onGround = true;
      }
    }
  }
  // sipmple ground collison (fallback if no plat forms)
  if (!onGround && playerY > height - playerHeight){
    playerY = 0;
    pVelocityY = 0;
    isJumping = false;
  }
  //keep player within horizontal bounds
  playerX = constrain(playerX, 0, width - playerWidth);
}

void drawplayer() {
  fill(0,0, 255);
  rect(playerX, playerY, playerWidth, playerHeight);
}


void keyPressed() {
  switch (key) {
    case ESC:
      exit();
      break;
    case 'd':
      pVelocityX += 10;
      break;
    case 'a':
      pVelocityX -= 10;
      break;
    case 'w':
      pVelocityY -= 10;
      break;
    case 's':
      pVelocityY += 10;
      break;
  }
}

void drawplatform() {
  switch (level) {
    case 0:
      int[][] levelOne = levelFloorHitboxes[0];
      for (int[] platform : levelOne) {
        rect(platform[0], platform[1], platform[2], platform[3]);
      }
      break;
    case 1:
      int[][] levelTwo = levelFloorHitboxes[1];
      for (int[] platform : levelTwo) {
        rect(platform[0], platform[1], platform[2], platform[3]);
      }
  }
}

void drawPlayer() {
  //println(playerWidth);
  //println(playerX);
  rect(playerX - playerWidth, playerY - playerHeight, playerWidth, playerHeight);
}
