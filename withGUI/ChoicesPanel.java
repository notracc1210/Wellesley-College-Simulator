import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Panel displaying game choice buttons.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class ChoicesPanel {
    
    private GameManager gameManager;
    private VBox container;
    private VBox buttonsContainer;
    private ScrollPane scrollPane;
    private Runnable refreshCallback;
    private Runnable restartCallback;
    
    public ChoicesPanel(GameManager gameManager, Runnable refreshCallback, Runnable restartCallback) {
        this.gameManager = gameManager;
        this.refreshCallback = refreshCallback;
        this.restartCallback = restartCallback;
        createView();
    }
    
    private void createView() {
        container = new VBox();
        container.setPadding(new Insets(0, 30, 0, 30));  // Match NarrativePanel container padding
        container.setStyle("-fx-background-color: #f5f5f5;");
        
        buttonsContainer = new VBox(15);  // Increased spacing between buttons
        buttonsContainer.setPadding(new Insets(25, 25, 25, 25));  // Match NarrativePanel label padding
        
        scrollPane = new ScrollPane(buttonsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f5f5f5; " +
                           "-fx-border-color: #cccccc; " +
                           "-fx-border-width: 1px;");
        
        container.getChildren().add(scrollPane);
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        if (gameManager == null) return;
        
        buttonsContainer.getChildren().clear();
        
        List<Option> options = gameManager.getCurrentOptions();
        
        if (options.isEmpty()) {
            // Game ended - show ending message and restart button
            Label endLabel = new Label("Game Over");
            endLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
            endLabel.setAlignment(Pos.CENTER);
            endLabel.setPadding(new Insets(20, 0, 20, 0));
            
            Button restartButton = new Button("Restart Game");
            restartButton.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
            restartButton.setPrefWidth(Double.MAX_VALUE);
            restartButton.setPrefHeight(60);
            restartButton.setAlignment(Pos.CENTER);
            restartButton.setStyle(
                "-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #357abd; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 15px; " +
                "-fx-cursor: hand;"
            );
            
            // Hover effect for restart button
            restartButton.setOnMouseEntered(e -> {
                restartButton.setStyle(
                    "-fx-background-color: #357abd; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #2a5f8f; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-padding: 15px; " +
                    "-fx-cursor: hand;"
                );
            });
            
            restartButton.setOnMouseExited(e -> {
                restartButton.setStyle(
                    "-fx-background-color: #4a90e2; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #357abd; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-padding: 15px; " +
                    "-fx-cursor: hand;"
                );
            });
            
            // Click handler for restart button
            restartButton.setOnAction(e -> {
                if (restartCallback != null) {
                    restartCallback.run();
                }
            });
            
            buttonsContainer.getChildren().addAll(endLabel, restartButton);
            return;
        }
        
        for (int i = 0; i < options.size(); i++) {
            final int choiceIndex = i;
            Option option = options.get(i);
            
            Button button = new Button((i + 1) + ". " + option.description);
            button.setFont(Font.font("Georgia", 18));
            button.setWrapText(true);
            button.setPrefWidth(Double.MAX_VALUE);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-padding: 18px; " +
                "-fx-cursor: hand;"
            );
            
            // Hover effect
            button.setOnMouseEntered(e -> {
                button.setStyle(
                    "-fx-background-color: #e8e8e8; " +
                    "-fx-border-color: #4a90e2; " +
                    "-fx-border-width: 2px; " +
                    "-fx-padding: 18px; " +
                    "-fx-cursor: hand;"
                );
            });
            
            button.setOnMouseExited(e -> {
                button.setStyle(
                    "-fx-background-color: #ffffff; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-width: 1px; " +
                    "-fx-padding: 18px; " +
                    "-fx-cursor: hand;"
                );
            });
            
            // Click handler
            button.setOnAction(e -> {
                gameManager.processChoice(choiceIndex);
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            });
            
            buttonsContainer.getChildren().add(button);
        }
        
        // Scroll to top when options update
        scrollPane.setVvalue(0);
    }
}