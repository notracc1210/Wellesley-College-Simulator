import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Narrative text display panel.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class NarrativePanel {
    
    private GameManager gameManager;
    private VBox container;
    private ScrollPane scrollPane;
    private VBox contentContainer;
    private Label titleLabel;
    private Label narrativeLabel;
    
    public NarrativePanel(GameManager gameManager) {
        this.gameManager = gameManager;
        createView();
    }
    
    private void createView() {
        container = new VBox();
        container.setPadding(new Insets(0, 30, 0, 30));  // Keep top/bottom 0 for tight fit, increase left/right for text
        container.setStyle("-fx-background-color: white;");
        VBox.setVgrow(container, javafx.scene.layout.Priority.ALWAYS);
        
        // Content container for title and description
        contentContainer = new VBox(15);
        contentContainer.setPadding(new Insets(25, 25, 25, 25));
        contentContainer.setMaxWidth(Double.MAX_VALUE);
        
        // Title label for achievement (initially hidden)
        titleLabel = new Label();
        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#FFD700")); // Golden color
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(20, 0, 10, 0)); // Top padding: 20, Bottom padding: 10
        titleLabel.setStyle("-fx-line-spacing: 8px;"); // Increase line spacing
        titleLabel.setVisible(false);
        
        // Narrative/description label
        narrativeLabel = new Label();
        narrativeLabel.setFont(Font.font("Georgia", 18));
        narrativeLabel.setWrapText(true);
        narrativeLabel.setMaxWidth(Double.MAX_VALUE);
        narrativeLabel.setAlignment(Pos.TOP_LEFT);
        narrativeLabel.setMinHeight(javafx.scene.control.Label.USE_PREF_SIZE);
        narrativeLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        narrativeLabel.setStyle("-fx-line-spacing: 8px;"); // Increase line spacing
        
        contentContainer.getChildren().addAll(titleLabel, narrativeLabel);
        
        scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: white; " +
                           "-fx-border-color: #cccccc; " +
                           "-fx-border-width: 1px;");
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        
        container.getChildren().add(scrollPane);
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        if (gameManager != null) {
            String text = gameManager.getCurrentText();
            
            // Check if game is ending - if so, split title and description
            if (gameManager.getGameTracker().isEnding() && text != null && !text.isEmpty()) {
                String[] parts = text.split("\n", 2);
                if (parts.length >= 2) {
                    // Show title and description separately
                    titleLabel.setText(parts[0]);
                    titleLabel.setVisible(true);
                    narrativeLabel.setText(parts[1]);
                } else {
                    // Fallback if no newline found
                    titleLabel.setVisible(false);
                    narrativeLabel.setText(text);
                }
            } else {
                // Normal game text - hide title, show full text
                titleLabel.setVisible(false);
                narrativeLabel.setText(text != null ? text : "");
            }
            
            // Calculate preferred height based on text content
            // Force layout calculation to ensure text wrapping is complete
            narrativeLabel.applyCss();
            narrativeLabel.layout();
            if (titleLabel.isVisible()) {
                titleLabel.applyCss();
                titleLabel.layout();
            }
            
            // Scroll to top when text updates
            scrollPane.setVvalue(0);
        }
    }
}