package bouncingballs;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    Pane canvas = new Pane();

    public void handleTimeline(Circle ball, double dx, double dy){
        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            double deltaX = dx;
            double deltaY = dy;

            @Override
            public void handle(final ActionEvent t) {
                ball.setLayoutX(ball.getLayoutX() + deltaX);
                ball.setLayoutY(ball.getLayoutY() + deltaY);

                final Bounds bounds = canvas.getBoundsInLocal();
                final boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
                final boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
                final boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius());
                final boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius());

                if (atRightBorder || atLeftBorder) {
                    deltaX *= -1;
                }
                if (atBottomBorder || atTopBorder) {
                    deltaY *= -1;
                }
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    public double calculateDelta(Circle ball){
        return (1 / ball.getRadius()) * 10 ;
    }

    @Override
    public void start(Stage stage) {
    	Scene scene = new Scene(canvas, 800, 400);
    	Circle ball = new Circle(10, Color.RED);
        Circle ball2 = new Circle(20, Color.BLUE);

        ball.relocate(0, 10);
        ball2.relocate(100, 100);

        canvas.getChildren().add(ball);
        canvas.getChildren().add(ball2);
        
        stage.setTitle("Moving Ball");
        stage.setScene(scene);
        stage.show();
        
        handleTimeline(ball, calculateDelta(ball), calculateDelta(ball));
        handleTimeline(ball2, calculateDelta(ball2), calculateDelta(ball2));
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {         
            @Override         
            public void handle(MouseEvent event) {  
                if (event.getButton() == MouseButton.PRIMARY) {
                    handleTimeline(ball, -calculateDelta(ball), -calculateDelta(ball));
                    handleTimeline(ball2, calculateDelta(ball2), calculateDelta(ball2));
                }
            }     
        }); 
    }
    
    public static void main(String[] args) {
        launch();
    }

}