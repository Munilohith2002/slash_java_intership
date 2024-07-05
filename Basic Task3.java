
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int TILE_SIZE = 20;

    private List<Point> snake;
    private Point food;
    private Direction direction = Direction.RIGHT;

    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        initializeGame();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP && direction != Direction.DOWN) {
                direction = Direction.UP;
            } else if (code == KeyCode.DOWN && direction != Direction.UP) {
                direction = Direction.DOWN;
            } else if (code == KeyCode.LEFT && direction != Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (code == KeyCode.RIGHT && direction != Direction.LEFT) {
                direction = Direction.RIGHT;
            }
        });

        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) { // Update every 100 milliseconds
                    if (!gameOver) {
                        update();
                        render(gc);
                    } else {
                        stop();
                    }
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private void initializeGame() {
        snake = new ArrayList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        generateFood();
        gameOver = false;
    }

    private void update() {
        // Move the snake
        moveSnake();

        // Check for collisions
        checkCollisions();
    }

    private void moveSnake() {
        // Move the snake by adding a new head in the current direction
        Point head = snake.get(0);
        Point newHead = new Point(head.getX(), head.getY());
        switch (direction) {
            case UP:
                newHead.setY(newHead.getY() - TILE_SIZE);
                break;
            case DOWN:
                newHead.setY(newHead.getY() + TILE_SIZE);
                break;
            case LEFT:
                newHead.setX(newHead.getX() - TILE_SIZE);
                break;
            case RIGHT:
                newHead.setX(newHead.getX() + TILE_SIZE);
                break;
        }
        snake.add(0, newHead);

        // Remove the tail
        if (!snake.contains(food)) {
            snake.remove(snake.size() - 1);
        } else {
            generateFood();
        }
    }

    private void checkCollisions() {
        // Check if snake collides with itself
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                break;
            }
        }

        // Check if snake collides with walls
        if (head.getX() < 0 || head.getX() >= WIDTH || head.getY() < 0 || head.getY() >= HEIGHT) {
            gameOver = true;
        }
    }

    private void generateFood() {
        Random random = new Random();
        int x = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        int y = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
        food = new Point(x, y);

        // Ensure food is not generated on the snake
        while (snake.contains(food)) {
            x = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
            y = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
            food = new Point(x, y);
        }
    }

    private void render(GraphicsContext gc) {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw snake
        gc.setFill(Color.GREEN);
        for (Point point : snake) {
            gc.fillRect(point.getX(), point.getY(), TILE_SIZE, TILE_SIZE);
        }

        // Draw food
        gc.setFill(Color.RED);
        gc.fillRect(food.getX(), food.getY(), TILE_SIZE, TILE_SIZE);

        // Game Over text
        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.fillText("Game Over! Press R to restart.", WIDTH / 2 - 80, HEIGHT / 2);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
