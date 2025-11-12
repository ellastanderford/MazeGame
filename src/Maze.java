//Name: Ella Standerford
//References: TODO

//Class purpose: main class that runs the Maze game in JavaFX

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.application.*;
import javafx.animation.AnimationTimer;
import javafx.event.*;

public class Maze extends Application {

  //Declaring these variables so they have scope throughout
  Navigator player = new Navigator();
  boolean moveUp = false;
  boolean moveDown = false;
  boolean moveLeft = false;
  boolean moveRight = false;
  Hedge[] hedges;

  public static void main (String[] args) {
    launch(args);
  }
  //------------------------------------
  //MAIN START METHOD (difficulty menu)
  //------------------------------------
  public void start (Stage stage) {
    //Creating the window
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle("Maze Game!");
    stage.show();

   //------Difficulty Selection screen------
    Text diffText = new Text(180, 250, "Select Difficulty: \n\n[1] Easy\n[2] Medium\n[3] Hard");
    diffText.setFont(Font.font(40));
    diffText.setFill(Color.DARKBLUE);
    root.getChildren().add(diffText);

    //When player presses 1, 2, or 3, choose difficulty
    EventHandler<KeyEvent> difficultyHandler = new EventHandler<KeyEvent>() {
      public void handle(KeyEvent e) {
        String difficulty = ""; //default empty string
        if (e.getCode() == KeyCode.DIGIT1) {
          difficulty = "Easy";
        }
        else if (e.getCode() == KeyCode.DIGIT2) {
          difficulty = "Medium";
        }
        else if (e.getCode() == KeyCode.DIGIT3) {
          difficulty = "Hard";
        }
        else {
          return; //ignores other keys
        }
        //clear the screen after difficulty is chosen
        root.getChildren().clear();
        //Now actually start the game
        startGame(root, scene, difficulty, stage);
      }
    };
    scene.setOnKeyPressed(difficultyHandler);
  } 

  private void startGame(Group root, Scene scene, String difficulty, Stage stage) {
    //----------------------
    // Set Difficulty level
    //----------------------
    double playerStrength = 10;
    double enemyStrength = 3;
    if (difficulty.equals("Medium")) {
      playerStrength = 7;
      enemyStrength = 6;
    }
    else if (difficulty.equals("Hard")) {
      playerStrength = 5;
      enemyStrength = 9;
    }

    //Text display for navigator health!
    Text healthText = new Text(10, 20, "Health: 100");
    healthText.setFill(Color.BLACK);
    healthText.setFont(Font.font(20));
    root.getChildren().add(healthText);

    //Text setup for timer
    Text timerText = new Text (700, 20, "Time: 0");
    timerText.setFill(Color.BLACK);
    timerText.setFont(Font.font(20));
    root.getChildren().add(timerText);

    //Start timer
    long startTime = System.currentTimeMillis();
    
    //Calling createMazeHedges to actually add the maze to root
    hedges = createMazeHedges();
    root.getChildren().addAll(hedges);

    //sets strength based on difficulty
    player.setStrength(playerStrength);
    Image luigi = new Image("file:images/luigi.png");
    ImagePattern luigiChar = new ImagePattern(luigi);
    player.setFill(luigiChar);
    root.getChildren().add(player);

    //spawning attackers randomly
    Attackers[] enemies = new Attackers[10];
    for (int i = 0; i < enemies.length; i++) {
      double randX = 50 + Math.random() * 700;
      double randY = 50 + Math.random() * 500;
      //test if the location is on a hedge
      while (isOnHedge(randX, randY, hedges)) {
        randX = 50 + Math.random() * 700;
        randY = 50 + Math.random() * 500;
      }
      enemies[i] = new Attackers(randX, randY);
      enemies[i].setStrength(enemyStrength);
      root.getChildren().add(enemies[i]);
    }

    //Key listeners for navigator movement
		scene.setOnKeyPressed(this::moveNavigator);
		scene.setOnKeyReleased(this::stopMovingNavigator);

		//Animation for smooth key press action (and so we can do multiple keys at once)
		Animator.start(this::animate);
    
    //Fireball handler for navigator
    EventHandler<MouseEvent> myHandlerTwo = new EventHandler<MouseEvent>() {
      public void handle (MouseEvent e) {
        double xClick = e.getX();
        double yClick = e.getY();
        player.shootFireballToward(xClick, yClick, root, hedges, enemies, Maze.this);
      }
    };
    scene.setOnMousePressed(myHandlerTwo);

    //Moving the health and timer to front (above hedges)
    healthText.toFront();
    timerText.toFront();

    //----------------------
    // Enemy + game updates
    //----------------------
    AnimationTimer enemyTimer = new AnimationTimer() {
      public void handle (long now) {
        //update all enemies
        for (int i = 0; i < enemies.length; i++) {
          if (enemies[i] != null) {
            enemies[i].update(player, hedges, Maze.this);
          }
        }

        //update health text each frame 
        healthText.setText("Health: " + (int)player.getHealth());

        //update timer 
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timerText.setText("Time: " + elapsed);

        //Check lose
        if (player.getHealth() <= 0) {
          this.stop();
          Text loseText = new Text(250, 300, "You lost! Try again!");
          loseText.setFont(Font.font(30));
          loseText.setFill(Color.RED);
          root.getChildren().add(loseText);
        }

        //Check win (reach exit)
        if (player.getX() > 750 && player.getY() < 80) {
          this.stop();
          int enemiesDefeated = 0;
          for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] == null) {
              enemiesDefeated++;
            }
          }
          long elapsedSecs = (System.currentTimeMillis() - startTime) / 1000;
          double score = player.getHealth() * 10 + enemiesDefeated * 100 - elapsedSecs * 5;
          Text winText = new Text(150, 300, "You Escaped! Score: " + (int)score);
          winText.setFont(Font.font(30));
          winText.setFill(Color.GOLD);
          root.getChildren().add(winText);
        }
      } 
    };
  enemyTimer.start();
  }

  //Methods to move the navigator
  public void animate(long time) {
		double x = player.getTranslateX();
		double y = player.getTranslateY();
		if (moveUp) {
      player.moveUp(hedges, this);
		}
		if (moveDown) {
			player.moveDown(hedges, this);
		}
		if (moveLeft) {
			player.moveLeft(hedges, this);
		}
		if (moveRight) {
			player.moveRight(hedges, this);
		}
	}

  //Movement method
  public void moveNavigator(KeyEvent e) {
    if (e.getCode() == KeyCode.W) {
      moveUp = true;
    }
    else if (e.getCode() == KeyCode.A) {
      moveLeft = true;
    }
    else if (e.getCode() == KeyCode.S) {
      moveDown = true;
    }
    else if (e.getCode() == KeyCode.D) {
      moveRight = true;
    }
   }
    //stopping movement method
    public void stopMovingNavigator(KeyEvent e) {
      if (e.getCode() == KeyCode.W) {
        moveUp = false;
      }
      else if (e.getCode() == KeyCode.A) {
        moveLeft = false;
      }
      else if (e.getCode() == KeyCode.S) {
        moveDown = false;
      }
      else if (e.getCode() == KeyCode.D) {
        moveRight = false;
      }
    }
  //-----------------------------
  // Helper methods (collisions)
  //-----------------------------
  private Hedge[] createMazeHedges() {
    return new Hedge[] {
      //Design based on hedge_reference.jpg
      // Outer border
      new Hedge(0, 0, 740, 20), // Top
      new Hedge(0, 0, 20, 530), // Left (w/ gap for entry)
      new Hedge(720, 70, 20, 600), // Right (w/ gap for exit)
      new Hedge(0, 580, 720, 20), // Bottom
      // Internal maze walls (rows & columns)
      //xPosition (/800), yPosition (/600), width, height
      //First segment
      new Hedge(0, 350, 100, 20), 
      new Hedge(80, 420, 20, 160), 
      new Hedge(80, 420, 90, 20), 
      new Hedge(150, 280, 20, 140), 
      new Hedge(70, 280, 100, 20), 
      new Hedge(70, 220, 20, 80), 
      new Hedge(70, 220, 170, 20), 
      new Hedge(220, 220, 20, 240), 
      new Hedge(150, 490, 20, 110), 
      new Hedge(240, 280, 50, 20),
      new Hedge(290, 280, 20, 80),
      new Hedge(290, 340, 100, 20),
      new Hedge(240, 410, 50, 20),
      new Hedge(290, 410, 20, 50),
      new Hedge(290, 440, 80, 20),
      new Hedge(370, 410, 20, 50), 
      new Hedge(370, 410, 140, 20),
      //Maze segment two
      new Hedge(220, 510, 220, 20),
      new Hedge(440, 480, 20, 50),
      new Hedge(460, 480, 100, 20),
      new Hedge(560, 410, 20, 120),
      new Hedge(560, 510, 90, 20),
      new Hedge(650, 510, 20, 90),
      new Hedge(500, 540, 20, 60),
      //Maze segment three
      new Hedge(650, 0, 20, 220),
      new Hedge(370, 200, 300, 20),
      new Hedge(650, 270, 20, 190), 
      new Hedge(440, 340, 210, 20), 
      new Hedge(510, 270, 160, 20),
      new Hedge(440, 270, 20, 70), 
      new Hedge(600, 130, 70, 20), 
      new Hedge(580, 70, 20, 80),
      new Hedge(460, 130, 70, 20), 
      new Hedge(460, 70, 120, 20),
      new Hedge(440, 70, 20, 80),
      new Hedge(140, 100, 300, 20),
      new Hedge(190, 120, 20, 40), 
      new Hedge(70, 150, 140, 20),
      new Hedge(70, 70, 20, 100),
      new Hedge(70, 50, 250, 20),
      new Hedge(370, 20, 20, 50),
      new Hedge(370, 155, 20, 130), 
      new Hedge(290, 155, 80, 20),
      new Hedge(290, 155, 20, 80), 
      new Hedge(510, 220, 20, 70),
    };
  }
  //testing for collisions for attackers
  private boolean isOnHedge(double x, double y, Hedge[] hedges) {
    Circle test = new Circle(x, y, 10);
    //Reference Node JavaDocs for information on BoundsInParent
    for (int i = 0; i < hedges.length; i++) {
      Rectangle rect = hedges[i].getMainRect();
      if (test.getBoundsInParent().intersects(rect.getBoundsInParent())) {
        return true;
      }
    }
    return false;
  }

  //testing for collisions for navigator
  public boolean isNavigatorBlocked(double nextX, double nextY, Navigator player, Hedge[] hedges) {
    Rectangle temp = new Rectangle(nextX, nextY, player.getWidth(), player.getHeight());
    for (int i = 0; i < hedges.length; i++) {
      Rectangle rect = hedges[i].getMainRect();
      if (temp.getBoundsInParent().intersects(rect.getBoundsInParent())) {
        return true; //collision!
      }
    }
    return false; //no collision, safe to move
  }

  //testing for collisions for attackers
  public boolean isAttackerBlocked(double nextX, double nextY, Attackers enemies, Hedge[] hedges) {
    Circle temp = new Circle(nextX, nextY, 10);
    for (int i = 0; i < hedges.length; i++) {
      Rectangle rect = hedges[i].getMainRect();
      if (temp.getBoundsInParent().intersects(rect.getBoundsInParent())) {
        return true; //cannot move there
      }
    }
    return false; //safe spot
  }

  //testing for fireball collisions
  public boolean fireballHitsHedge(Fireball ball, Hedge[] hedges) {
    for (int i = 0; i < hedges.length; i++) {
      Rectangle rect = hedges[i].getMainRect();
      if (ball.getBody().getBoundsInParent().intersects(rect.getBoundsInParent())) {
        return true;
      }
    }
    return false;
  }
  //fireball running outside of the scene check
  public boolean fireballOutOfBounds(Fireball ball) {
    if (ball.getX() < 0 || ball.getX() > 800) {
      return true;
    }
    else if (ball.getY() < 0 || ball.getY() > 600) {
      return true;
    }
    else {
      return false;
    }
  }

  //checks if a straight line between attacker and player hits a hedge
  public boolean canSeePlayer(double startX, double startY, double endX, double endY, Hedge[] hedges) {
    //check many points along the line
    int steps = 50;
    for (int i = 0; i <= steps; i++) {
      double t = (double) i / steps;
      double checkX = startX + t * (endX - startX);
      double checkY = startY + t * (endY - startY);
      //creating a tiny point circle to test for collisions
      Circle point = new Circle(checkX, checkY, 2);
      for (int j = 0; j < hedges.length; j++) {
        Rectangle rect = hedges[j].getMainRect();
        if (point.getBoundsInParent().intersects(rect.getBoundsInParent())) {
          return false; //blocked by a hedge
        }
      }
    }
    return true; //no hedge in the way
  }
}
