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
import java.util.ArrayList;

public class Maze extends Application {

    private Navigator player = new Navigator();
    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private Hedge[] hedges;
    
    // Centralized collection for handling elements safely inside the loop
    private ArrayList<Fireball> fireballs = new ArrayList<>();
    private Attackers[] enemies = new Attackers[10];
    private int enemiesDefeated = 0;
    private boolean gameEnded = false;
    private AnimationTimer gameLoop;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
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

        // When player presses 1, 2, or 3, choose difficulty
        scene.setOnKeyPressed(e -> {
            String difficulty = "";
            if (e.getCode() == KeyCode.DIGIT1) difficulty = "Easy";
            else if (e.getCode() == KeyCode.DIGIT2) difficulty = "Medium";
            else if (e.getCode() == KeyCode.DIGIT3) difficulty = "Hard";
            else return; // ignores other keys

            // Clear the screen after difficulty is chosen
            root.getChildren().clear();
            // Now actually start the game
            startGame(root, scene, difficulty);
        });
    }

    private void startGame(Group root, Scene scene, String difficulty) {
        //----------------------
        // Set Difficulty level
        //----------------------
        double playerStrength = 10;
        double enemyStrength = 3;
        if (difficulty.equals("Medium")) {
            playerStrength = 7;
            enemyStrength = 6;
        } else if (difficulty.equals("Hard")) {
            playerStrength = 5;
            enemyStrength = 9;
        }

        // Text display for navigator health!
        Text healthText = new Text(10, 20, "Health: 100");
        healthText.setFont(Font.font(20));
        root.getChildren().add(healthText);

        // Text setup for timer
        Text timerText = new Text(700, 20, "Time: 0");
        timerText.setFont(Font.font(20));
        root.getChildren().add(timerText);

        // Start timer
        long startTime = System.currentTimeMillis();

        // Calling createMazeHedges to actually add the maze to root
        hedges = createMazeHedges();
        root.getChildren().addAll(hedges);

        // Sets strength based on difficulty
        player.setStrength(playerStrength);
        
        // Robust Image Loading Check to prevent silent pipeline crashes
        Image luigi = new Image("luigi.png", false);
        if (luigi.isError()) {
            System.out.println("Luigi image asset missing. Defaulting to safe classic blue shape layout.");
            player.setFill(Color.BLUE);
        } else {
            player.setFill(new ImagePattern(luigi));
        }
        root.getChildren().add(player);

        // Spawning attackers randomly outside hedge boundaries
        for (int i = 0; i < enemies.length; i++) {
            double randX, randY;
            do {
                randX = 50 + Math.random() * 700;
                randY = 50 + Math.random() * 500;
            } while (isOnHedge(randX, randY, hedges));
            
            enemies[i] = new Attackers(randX, randY);
            enemies[i].setStrength(enemyStrength);
            root.getChildren().add(enemies[i]);
        }

        // Key listeners for navigator movement
        scene.setOnKeyPressed(this::moveNavigator);
        scene.setOnKeyReleased(this::stopMovingNavigator);

        // Animation for smooth key press action via custom Animator layout wrapper
        Animator.start(this::animate);

        // Fireball click casting implementation
        scene.setOnMousePressed(e -> {
            if (gameEnded) return;
            double startX = player.getX() + player.getWidth() / 2;
            double startY = player.getY() + player.getHeight() / 2;
            double dx = e.getX() - startX;
            double dy = e.getY() - startY;
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length > 0) {
                dx = (dx / length) * 6; // Fireball velocity speed vector
                dy = (dy / length) * 6;
                Fireball ball = new Fireball(startX, startY, dx, dy, player.getStrength());
                fireballs.add(ball);
                root.getChildren().add(ball);
            }
        });

        // Moving the health and timer to front (above hedges)
        healthText.toFront();
        timerText.toFront();

        //-----------------------------------------
        // Single Centralized Clock Loop Engine
        //-----------------------------------------
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameEnded) return;

                // 1. Process and update alive enemies
                for (Attackers enemy : enemies) {
                    if (enemy != null) {
                        enemy.update(player, hedges, Maze.this);
                    }
                }

                // 2. Process Fireballs and safe cleanup to prevent concurrent modifications
                ArrayList<Fireball> toRemove = new ArrayList<>();
                for (Fireball ball : fireballs) {
                    if (ball.getParent() == null) {
                        toRemove.add(ball);
                        continue;
                    }

                    ball.move();
                    if (fireballHitsHedge(ball, hedges) || fireballOutOfBounds(ball)) {
                        toRemove.add(ball);
                        root.getChildren().remove(ball);
                        continue;
                    }
                    
                    // Collision check loops mapping to array index entries
                    for (int i = 0; i < enemies.length; i++) {
                        if (enemies[i] != null && ball.getBoundsInParent().intersects(enemies[i].getBody().getBoundsInParent())) {
                            enemies[i].takeDamage(ball.getDamage());
                            toRemove.add(ball);
                            root.getChildren().remove(ball);
                            
                            if (enemies[i].getHealth() <= 0) {
                                root.getChildren().remove(enemies[i]);
                                enemies[i] = null;
                                enemiesDefeated++;
                            }
                            break;
                        }
                    }
                }
                fireballs.removeAll(toRemove);

                // 3. Update health text each frame 
                healthText.setText("Health: " + (int) player.getHealth());

                // 4. Update timer 
                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                timerText.setText("Time: " + elapsed);

                // 5. Check lose state
                if (player.getHealth() <= 0) {
                    endGame("You lost! Try again!", Color.RED, root);
                } 
                // 6. Check win state (reach exit coordinates)
                else if (player.getX() > 720 && player.getY() < 80) {
                    double score = player.getHealth() * 10 + enemiesDefeated * 100 - elapsed * 5;
                    endGame("You Escaped! Score: " + (int) score, Color.GOLD, root);
                }
            }
        };
        gameLoop.start();
    }

    private void endGame(String message, Color color, Group root) {
        gameEnded = true;
        gameLoop.stop();
        Text endText = new Text(150, 300, message);
        endText.setFont(Font.font(35));
        endText.setFill(color);
        root.getChildren().add(endText);
    }

    // Methods called by custom loop wrapper to smoothly animate the navigator
    public void animate(long time) {
        if (gameEnded) return;
        if (moveUp) player.moveUp(hedges, this);
        if (moveDown) player.moveDown(hedges, this);
        if (moveLeft) player.moveLeft(hedges, this);
        if (moveRight) player.moveRight(hedges, this);
    }

    // Movement method inputs
    public void moveNavigator(KeyEvent e) {
        if (e.getCode() == KeyCode.W) moveUp = true;
        if (e.getCode() == KeyCode.A) moveLeft = true;
        if (e.getCode() == KeyCode.S) moveDown = true;
        if (e.getCode() == KeyCode.D) moveRight = true;
    }

    // Stopping movement method inputs
    public void stopMovingNavigator(KeyEvent e) {
        if (e.getCode() == KeyCode.W) moveUp = false;
        if (e.getCode() == KeyCode.A) moveLeft = false;
        if (e.getCode() == KeyCode.S) moveDown = false;
        if (e.getCode() == KeyCode.D) moveRight = false;
    }

    //-----------------------------
    // Helper methods (collisions)
    //-----------------------------
    private Hedge[] createMazeHedges() {
        return new Hedge[] {
            // Outer border
            new Hedge(0, 0, 740, 20), // Top
            new Hedge(0, 0, 20, 530), // Left (w/ gap for entry)
            new Hedge(720, 70, 20, 600), // Right (w/ gap for exit)
            new Hedge(0, 580, 720, 20), // Bottom
            // Internal maze walls (rows & columns)
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
            // Maze segment two
            new Hedge(220, 510, 220, 20),
            new Hedge(440, 480, 20, 50),
            new Hedge(460, 480, 100, 20),
            new Hedge(560, 410, 20, 120),
            new Hedge(560, 510, 90, 20),
            new Hedge(650, 510, 20, 90),
            new Hedge(500, 540, 20, 60),
            // Maze segment three
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
            new Hedge(510, 220, 20, 70)
        };
    }

    // testing for collisions for spawn checks
    private boolean isOnHedge(double x, double y, Hedge[] hedges) {
        Circle test = new Circle(x, y, 10);
        for (Hedge hedge : hedges) {
            if (test.getBoundsInParent().intersects(hedge.getMainRect().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    // testing for collisions for navigator
    public boolean isNavigatorBlocked(double nextX, double nextY, Hedge[] hedges) {
        Rectangle temp = new Rectangle(nextX, nextY, player.getWidth(), player.getHeight());
        for (Hedge hedge : hedges) {
            if (temp.getBoundsInParent().intersects(hedge.getMainRect().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    // testing for collisions for attackers
    public boolean isAttackerBlocked(double nextX, double nextY, Hedge[] hedges) {
        Circle temp = new Circle(nextX, nextY, 10);
        for (Hedge hedge : hedges) {
            if (temp.getBoundsInParent().intersects(hedge.getMainRect().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    // testing for fireball collisions
    public boolean fireballHitsHedge(Fireball ball, Hedge[] hedges) {
        for (Hedge hedge : hedges) {
            if (ball.getBoundsInParent().intersects(hedge.getMainRect().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    // fireball running outside of the scene check
    public boolean fireballOutOfBounds(Fireball ball) {
        return ball.getX() < 0 || ball.getX() > 800 || ball.getY() < 0 || ball.getY() > 600;
    }

    // optimized line-of-sight collision checking layout
    public boolean canSeePlayer(double startX, double startY, double endX, double endY, Hedge[] hedges) {
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double checkX = startX + t * (endX - startX);
            double checkY = startY + t * (endY - startY);
            
            for (Hedge hedge : hedges) {
                if (hedge.getMainRect().getBoundsInParent().contains(checkX, checkY)) {
                    return false; // Vision is blocked by a hedge
                }
            }
        }
        return true; 
    }
}