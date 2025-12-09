import javafx.scene.layout.VBox;

/**
 * Right panel containing narrative and choices.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class RightPanel {
    
    private GameManager gameManager;
    private VBox container;
    private NarrativePanel narrativePanel;
    private ChoicesPanel choicesPanel;
    private Runnable refreshCallback;
    private Runnable restartCallback;
    
    public RightPanel(GameManager gameManager, Runnable refreshCallback, Runnable restartCallback) {
        this.gameManager = gameManager;
        this.refreshCallback = refreshCallback;
        this.restartCallback = restartCallback;
        createView();
    }
    
    private void createView() {
        container = new VBox(0);
        container.setMinWidth(500);  // Minimum width
        container.setPrefWidth(800);  // Preferred width
        container.setMaxWidth(Double.MAX_VALUE);  // Allow growth
        
        narrativePanel = new NarrativePanel(gameManager);
        choicesPanel = new ChoicesPanel(gameManager, refreshCallback != null ? refreshCallback : () -> {}, restartCallback);
        
        // Use VBox with proper growth priority - give more space to choices panel
        // Give narrative 40% of space, choices 60%
        VBox.setVgrow(narrativePanel.getView(), javafx.scene.layout.Priority.ALWAYS);
        narrativePanel.getView().setMinHeight(300);  // Minimum height for narrative
        narrativePanel.getView().setPrefHeight(350); // Preferred height - reduced to give more space to choices
        
        VBox.setVgrow(choicesPanel.getView(), javafx.scene.layout.Priority.ALWAYS);
        choicesPanel.getView().setMinHeight(350);    // Minimum height for choices - increased
        choicesPanel.getView().setPrefHeight(500);   // Preferred height - significantly larger
        
        container.getChildren().addAll(narrativePanel.getView(), choicesPanel.getView());
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        narrativePanel.update();
        choicesPanel.update();
    }
    
    /**
     * Update the game manager reference (used when restarting the game).
     */
    public void setGameManager(GameManager newGameManager) {
        this.gameManager = newGameManager;
        narrativePanel = new NarrativePanel(gameManager);
        choicesPanel = new ChoicesPanel(gameManager, refreshCallback != null ? refreshCallback : () -> {}, restartCallback);
        
        // Rebuild view with new panels
        container.getChildren().clear();
        VBox.setVgrow(narrativePanel.getView(), javafx.scene.layout.Priority.ALWAYS);
        narrativePanel.getView().setMinHeight(300);
        narrativePanel.getView().setPrefHeight(350);
        VBox.setVgrow(choicesPanel.getView(), javafx.scene.layout.Priority.ALWAYS);
        choicesPanel.getView().setMinHeight(350);
        choicesPanel.getView().setPrefHeight(500);
        container.getChildren().addAll(narrativePanel.getView(), choicesPanel.getView());
    }
}