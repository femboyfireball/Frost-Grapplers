int[][][] levels;
int level;
int previousLevel; // Added to store the previous level
//color(173, 216, 230)
int playerX;
int playerY;
float pVelocityX; // Changed to float for smoother acceleration
int pVelocityXMax;
float pVelocityY; // Changed to float for smoother gravity
int pVelocityYMax;
int playerWidth;
int playerHeight;
int standingHeight;

int jumpStrength = -20;
float Gravity = 1;
float playerAcceleration = 1.5; // Adjust this value for acceleration speed
float playerDeceleration = 0.9; // Adjust this value for deceleration
float iceFriction = 0.95; // Added ice friction
boolean isJumping = false;
boolean isCrouching = false;
boolean isAirborne;
boolean direction;
boolean isSticking = false; // New variable to track sticking
boolean isWallClinging = false; // New variable for wall clinging
int wallClingTimer = 0;
int maxWallClingTime = 30; // Adjust the duration of wall cling in frames
float wallClingJumpStrength = -18.5; // Increased the upward impulse for a higher wall jump
boolean canWallCling = true; // New variable to control if wall clinging is allowed after landing
boolean releasedCling = false; // Add this line
boolean onIce = false;

color stickyColor = color(0, 0, 79); // The specific blue color
color iceColor = color(0, 255, 255);

boolean[] keys = new boolean[256]; // Array to track pressed keys

// Enemy variables
float enemyX;
float enemyY;
int enemyWidth = 40;
int enemyHeight = 80;
boolean enemyAlive = true;
float enemyVelocityX = 1.0; // Basic horizontal movement speed
int enemyDirection = 1; // 1 for right, -1 for left
float enemyGravity = Gravity;
float enemyVelocityY = 0;
int enemyViewRange = 300; // How far the enemy can see the player
float enemyFollowSpeed = 2.2f; // Increased follow speed (was 1.5)
float enemyBaseSpeed = 1.0f;

// Attack variables
boolean isAttacking = false;
int attackTimer = 0;
int attackDuration = 10; // Number of frames the attack lasts
int attackRange = 60;

void setup() {
  fullScreen();

  standingHeight = 120;
  playerWidth = 60;
  playerHeight = 120;
  playerX = width / 2 - playerWidth / 2;
  playerY = height - height / 3;
  pVelocityX = 0;
  pVelocityXMax = 15;
  pVelocityY = 0;
  pVelocityYMax = 30;

  isAirborne = false;
  direction = false; // False = Right, True = Left

  // Initialize enemy position
  enemyX = 300;
  enemyY = 820;
  enemyAlive = true;

  level = 0;
  previousLevel = 0; // Initialize previousLevel
  levels = new int[][][] {
    { // Level 1
      { 0,     900,    1920,    180 }, // Floor
      { 240,    720,    350,     36  },
      { 720,    450,    150,     360 },
      { 1200,   630,    480,     36  },
      { 120,    180,    240,     36  }
    },
    { // Level 2
      { 1000,     600, 800, 50  },
      { 50,     450, 150, 50  },
      { 50,     300, 20,     150 },
      { 1200,    500, 100, 50  },
      { 1000,    425, 50, 100  },
      { 1000,    600, 50, 50  }, // Added a blue platform for testing
      { 900, 850, 100, 20} //Added Ice Platform, made it longer
    }
  };
}

// Physics and movement
void draw() {
  background(0, 0, 0);
  processInput();
  updateCrouch(); // Call updateCrouch() here!
  drawPlayer();
  fill(0, 0, 79);
  drawplatform();
  updatePlayer();
  updateEnemy(); // Update enemy movement and behavior
  drawEnemy();
  updateAttack();
}

void processInput() {
  if (keys['w']) {
    if (isWallClinging) {
      pVelocityY += wallClingJumpStrength; // Use the updated wallClingJumpStrength
      isJumping = true;
      isWallClinging = false;
      wallClingTimer = 0;
      isAirborne = true;
      canWallCling = false; // Disable wall clinging after a wall jump until landing
      releasedCling = true;
      pVelocityX = direction ? 10 : -10; // Give a horizontal impulse away from the wall
    } else if (!isJumping) {
      pVelocityY += jumpStrength;
      isJumping = true;
      isSticking = false;
      isAirborne = true;
    }
  }
  if (keys['a']) {
    pVelocityX -= playerAcceleration;
    direction = true;
  }
  if (keys['s']) isCrouching = true;
  if (keys['d']) {
    pVelocityX += playerAcceleration;
    direction = false;
  }
  if (keys['f'] && !isAttacking) {
    isAttacking = true;
    attackTimer = 0;
    checkAttackCollision();
  }

  // Reset wall cling if no longer pressing against a wall
  if (isWallClinging && ((keys['a'] && !direction) || (keys['d'] && direction) || !(keys['a'] || keys['d']))) { // Modified condition
    isWallClinging = false;
    wallClingTimer = 0;
    releasedCling = true;
  }
}

void updatePlayer() {
  // Apply deceleration
  pVelocityX *= iceFriction; //always ice friction

  // Limit horizontal velocity
  if (pVelocityX > pVelocityXMax) pVelocityX = pVelocityXMax;
  if (pVelocityX < -pVelocityXMax) pVelocityX = -pVelocityXMax;

  // **Horizontal Collision Check BEFORE Horizontal Movement**
  float nextPlayerX = playerX + pVelocityX;
  boolean horizontalCollision = false;
  boolean leftCollision = false;
  boolean rightCollision = false;
  int currentLevel = level % levels.length;
  for (int[] platform : levels[currentLevel]) {
    if (nextPlayerX < platform[0] + platform[2] &&
        nextPlayerX + playerWidth > platform[0] &&
        playerY < platform[1] + platform[3] &&
        playerY + playerHeight > platform[1]) {
      horizontalCollision = true;
      if (pVelocityX > 0) { // Moving right, collide with left side
        rightCollision = true;
        nextPlayerX = platform[0] - playerWidth;
      } else if (pVelocityX < 0) { // Moving left, collide with right side
        leftCollision = true;
        nextPlayerX = platform[0] + platform[2];
      }
      pVelocityX = 0;
      break; // Stop checking other platforms if a horizontal collision occurs
    }
  }
  if (!horizontalCollision) {
    playerX = (int)nextPlayerX; // Cast float to int here
  }

  // Apply vertical velocity and gravity
  if (!isSticking && !isWallClinging) {
    if (pVelocityY > pVelocityYMax) pVelocityY = pVelocityYMax;
    pVelocityY += Gravity;
    playerY += pVelocityY;
  } else if (isSticking) {
    pVelocityY = 0;
  } else if (isWallClinging && !keys['w']) {
    pVelocityY = min(pVelocityY + Gravity * 0.5, 5); // Slow down fall when clinging
  }

  // **Vertical Collision Check AFTER Vertical Movement**
  boolean onGround = false;
  boolean onStickyPlatform = false;
  onIce = true; //all platforms are ice
  for (int[] platform : levels[currentLevel]) {
    if (playerY + playerHeight > platform[1] &&
        playerY < platform[1] + platform[3] &&
        playerX + playerWidth > platform[0] &&
        playerX < platform[0] + platform[2]) {
      if (pVelocityY >= 0) { // Collision from top (landing)
        playerY = platform[1] - playerHeight;
        pVelocityY = 0;
        isJumping = false;
        isAirborne = false;
        onGround = true;
        isWallClinging = false; // Stop wall clinging on landing
        wallClingTimer = 0;
        canWallCling = true; // Re-enable wall clinging after landing
        releasedCling = false; // Reset here
        if (getPlatformColor(platform) == stickyColor) {
          isSticking = true;
        } else {
          isSticking = false;
        }
      } else if (pVelocityY < 0) { // Collision from bottom (ceiling)
        playerY = platform[1] + platform[3];
        pVelocityY = 0;
      }
    }
  }

  // Wall Cling Logic
  if (!onGround && canWallCling && !releasedCling && (leftCollision || rightCollision) && (keys['a'] || keys['d'])) {
    isWallClinging = true;
    pVelocityY = 0; // Stop vertical movement when clinging
    wallClingTimer++;
    if (wallClingTimer > maxWallClingTime) {
      // Force off cling after timer expires
      isWallClinging = false;
      wallClingTimer = 0;
      canWallCling = false; // Player can't immediately re-cling
      releasedCling = true;
      pVelocityY += wallClingJumpStrength / 2; // Apply a small downward impulse to simulate falling off
      isAirborne = true;
    }
  } else if (!keys['a'] && !keys['d']) {
    isWallClinging = false;
    wallClingTimer = 0;
  }

  // If not colliding with any platform from the top, then the player is airborne and not sticking
  if (!onGround && !isJumping && playerY < height - playerHeight) {
    isAirborne = true;
    isSticking = false;
    onIce = false;
  }



  // Screen constraints (apply after potential collision adjustments) - REMOVE CONSTRAINTS
  playerX = constrain(playerX, 0, width - playerWidth);
  if (playerX == 0 || playerX == width - playerWidth) pVelocityX = 0;
  //playerY = constrain(playerY, 0, height - playerHeight);  <-- REMOVE THIS LINE
  if (playerY <= 0) { // Changed to check if playerY is less than or equal to 0
    level = (level + 1) % levels.length; // Move to the next level (wrap around)
    previousLevel = level - 1;
    resetPlayerPosition();
    resetEnemyPosition();
  }
  if (playerY == 0) {
    pVelocityY = 0;
    isJumping = false;
    isSticking = false;
    isWallClinging = false;
    wallClingTimer = 0;
    canWallCling = true; // Re-enable wall clinging after landing
    releasedCling = false; // Reset here
    onIce = false;
  }
  //if player falls off screen
  if (playerY > height){
    level = previousLevel;
    resetPlayerPosition();
    resetEnemyPosition();
  }
  if (playerY == height - playerHeight && !isJumping) isSticking = false;
}

void updateEnemy() {
  if (enemyAlive) {
    // Basic horizontal movement
    enemyX += enemyVelocityX * enemyDirection;

    // Apply gravity to the enemy
    enemyVelocityY += enemyGravity;
    enemyY += enemyVelocityY;

    // Enemy collision detection with platforms
    boolean onGround = false;
    int currentLevel = level % levels.length;
    for (int[] platform : levels[currentLevel]) {
      if (enemyY + enemyHeight > platform[1] &&
          enemyY < platform[1] + platform[3] &&
          enemyX + enemyWidth > platform[0] &&
          enemyX < platform[0] + platform[2]) {
        // Collision occurred
        if (enemyVelocityY > 0) { // Landing on a platform
          enemyY = platform[1] - enemyHeight;
          enemyVelocityY = 0;
          onGround = true;
        } else if (enemyVelocityY < 0) { // Hitting the ceiling
          enemyY = platform[1] + platform[3];
          enemyVelocityY = 0;
        }
        // Horizontal collision
        if (enemyX < platform[0] && enemyVelocityX * enemyDirection > 0) {
          enemyX = platform[0] - enemyWidth;
          enemyDirection *= -1;
        } else if (enemyX + enemyWidth > platform[0] + platform[2] && enemyVelocityX * enemyDirection < 0) {
          enemyX = platform[0] + platform[2] - enemyWidth; // Corrected line
          enemyDirection *= -1;
        }
      }
    }
    if (!onGround) {
      // If not on ground, continue to apply gravity
    }

    // Player detection and following
    float distanceToPlayer = dist(enemyX + enemyWidth / 2, enemyY + enemyHeight / 2,
                                    playerX + playerWidth / 2, playerY + playerHeight / 2);

    boolean canSeePlayer = true; // Declare canSeePlayer here, default to true
    if (distanceToPlayer < enemyViewRange) {
      // Check for walls between enemy and player
      int startX = (int)enemyX + enemyWidth / 2;
      int startY = (int)enemyY + enemyHeight / 2;
      int endX = (int)playerX + playerWidth / 2;
      int endY = (int)playerY + playerHeight / 2;

      // Simplified line intersection check (can be improved for more accuracy)
      for (int[] platform : levels[currentLevel]) {
        if (lineIntersectsRect(startX, startY, endX, endY, platform[0], platform[1], platform[0] + platform[2], platform[1] + platform[3])) {
          canSeePlayer = false;
          break;
        }
      }
    }
    if (distanceToPlayer < enemyWidth && enemyAlive) {
      resetGame();
    }

    if (canSeePlayer) {
      // Player is in view, follow the player
      if (playerX > enemyX) {
        enemyX += enemyFollowSpeed;
        enemyDirection = 1;
      } else if (playerX < enemyX) {
        enemyX -= enemyFollowSpeed;
        enemyDirection = -1;
      }
      // Basic vertical follow (can be improved with jumping logic)
      if (playerY < enemyY - 10 && onGround) {
        enemyVelocityY = jumpStrength / 1.5f; // Small jump to try and follow
      }
    } else {
      // Player is not visible, revert to base speed
      enemyVelocityX = enemyBaseSpeed;
    }
  }
}

// Function to check if a line intersects a rectangle
boolean lineIntersectsRect(int x1, int y1, int x2, int y2, int rx, int ry, int rw, int rh) {
  // Check if either endpoint is inside the rectangle
  if (x1 >= rx && x1 <= rx + rw && y1 >= ry && y1 <= ry + rh ||
      x2 >= rx && x2 <= rx + rw && y2 >= ry && y2 <= ry + rh) {
    return true;
  }

  // Check for intersection with each of the four sides of the rectangle
  boolean left = lineIntersectsLine(x1, y1, x2, y2, rx, ry, rx, ry + rh);
  boolean right = lineIntersectsLine(x1, y1, x2, y2, rx + rw, ry, rx + rw, ry + rh);
  boolean top = lineIntersectsLine(x1, y1, x2, y2, rx, ry, rx + rw, ry);
  boolean bottom = lineIntersectsLine(x1, y1, x2, y2, rx, ry + rh, rx + rw, ry + rh);

  return left || right || top || bottom;
}

// Function to check if two lines intersect
boolean lineIntersectsLine(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
  // Calculate the determinants
  float det1 = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
  if (det1 == 0) {
    return false; // Lines are parallel
  }

  float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / det1;
  float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / det1;

  return t > 0 && t < 1 && u > 0 && u < 1;
}


//Crouching logic
void updateCrouch() {
  if (isCrouching) {
    playerHeight = standingHeight / 2;
  } else {
    // Prevent standing up into a platform
    boolean canStand = true;
    int currentLevel = level % levels.length;
    int originalPlayerY = playerY;
    playerY -= standingHeight / 2; // Temporarily move player up to check for collision
    for (int[] platform : levels[currentLevel]) {
      if (playerX < platform[0] + platform[2] &&
          playerX + playerWidth > platform[0] &&
          playerY < platform[1] + platform[3] &&
          playerY + standingHeight > platform[1]) {
        canStand = false;
        break;
      }
    }
    playerY = originalPlayerY; // Move player back
    if (canStand) {
      playerHeight = standingHeight;
    }
  }
}

void drawPlayer() {
  if (isWallClinging) {
    fill(100, 100, 255); // Indicate wall clinging with a different color
  } else {
    fill(255, 0, 79);
  }
  rect(playerX, playerY, playerWidth, playerHeight);

  // Draw attack indicator
  if (isAttacking) {
    fill(255, 255, 0, map(attackTimer, 0, attackDuration, 200, 0)); // Fading yellow
    if (!direction) { // Facing right
      rect(playerX + playerWidth, playerY + playerHeight / 3, attackRange, playerHeight / 3);
    } else { // Facing left
      rect(playerX - attackRange, playerY + playerHeight / 3, attackRange, playerHeight / 3);
    }
  }
}

void drawEnemy() {
  if (enemyAlive) {
    fill(255, 0, 0); // Red enemy
    rect(enemyX, enemyY, enemyWidth, enemyHeight);
  }
}

void updateAttack() {
  if (isAttacking) {
    attackTimer++;
    if (attackTimer > attackDuration) {
      isAttacking = false;
    }
  }
}

void checkAttackCollision() {
  if (enemyAlive) {
    int attackStartX;
    if (!direction) { // Facing right
      attackStartX = playerX + playerWidth;
    } else { // Facing left
      attackStartX = playerX - attackRange;
    }
    int attackEndX = attackStartX + (direction ? -attackRange : attackRange);

    // Check if the enemy's horizontal position overlaps with the attack range
    if (min(attackStartX, attackEndX) < enemyX + enemyWidth &&
        max(attackStartX, attackEndX) > enemyX &&
        playerY < enemyY + enemyHeight &&
        playerY + playerHeight > enemyY) {
      enemyAlive = false;
      println("Enemy hit!");
    }
  }
}


void keyPressed() {
  if (key == ESC) exit();
  if (key < 256) { // Check if the key is within the array bounds
    keys[key] = true;
    println("Key Pressed: " + key + ", KeyCode: " + keyCode);
  }
}

void keyReleased() {
  if (key < 256) { // Check if the key is within the array bounds
    keys[key] = false;
    println("Key Released: " + key + ", KeyCode: " + keyCode);
  }
  if (key == 's') isCrouching = false;
}

void drawplatform() {
  switch (level) {
    case 0:
      int[][] levelOne = levels[0];
      for (int[] platform : levelOne) {
        fill(iceColor); // Default platform color is ice
        rect(platform[0], platform[1], platform[2], platform[3]);
      }
      break;
    case 1:
      int[][] levelTwo = levels[1];
      for (int[] platform : levelTwo) {
        fill(iceColor); // Default platform color is ice
        rect(platform[0], platform[1], platform[2], platform[3]);
      }
      break;
  }
}

color getPlatformColor(int[] platform) {
  return iceColor; // Default platform color is ice
}

void resetGame() {
  playerX = width / 2 - playerWidth / 2;
  playerY = height - height / 3;
  pVelocityX = 0;
  pVelocityY = 0;
  isJumping = false;
  isCrouching = false;
  isAirborne = false;
  direction = false;
  isSticking = false;
  isWallClinging = false;
  wallClingTimer = 0;
  releasedCling = false;
  onIce = true; //start on ice
  level = 0;
  previousLevel = 0;

  enemyX = 300;
  enemyY = 820;
  enemyAlive = true;
}

void resetPlayerPosition() {
  playerX = width / 2 - playerWidth / 2;
  playerY = height - height / 3;
  pVelocityX = 0;
  pVelocityY = 0;
  isJumping = false;
  isCrouching = false;
  isAirborne = false;
  direction = false;
  isSticking = false;
  isWallClinging = false;
  wallClingTimer = 0;
  releasedCling = false;
  onIce = true;
}

void resetEnemyPosition() {
  enemyX = 300;
  enemyY = 820;
  enemyAlive = true;
}
