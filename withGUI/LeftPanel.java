import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Left panel containing campus map and student stats.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class LeftPanel {
    
    private GameManager gameManager;
    private VBox container;
    private CampusMapView campusMapView;
    private StudentStatsPanel statsPanel;
    private Label currentLocationLabel;
    private Runnable refreshCallback;
    
    public LeftPanel(GameManager gameManager, Runnable refreshCallback) {
        this.gameManager = gameManager;
        this.refreshCallback = refreshCallback;
        createView();
    }
    
    private void createView() {
        container = new VBox(0);
        container.setMinWidth(400);  // Minimum width
        container.setPrefWidth(600);  // Preferred width
        container.setMaxWidth(Double.MAX_VALUE);  // Allow growth
        
        // Campus map view with refresh callback
        campusMapView = new CampusMapView(gameManager, refreshCallback);
        
        // Current location label
        currentLocationLabel = new Label("Current: DORM");
        currentLocationLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        currentLocationLabel.setAlignment(Pos.CENTER);
        currentLocationLabel.setPrefWidth(Double.MAX_VALUE);
        currentLocationLabel.setPadding(new Insets(10, 0, 10, 0));
        
        // Add label to map view container
        campusMapView.getView().getChildren().add(currentLocationLabel);
        
        // Student stats panel
        statsPanel = new StudentStatsPanel(gameManager);
        
        container.getChildren().addAll(campusMapView.getView(), statsPanel.getView());
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        if (gameManager != null) {
            // Update current location
            String location = gameManager.getCurrentLocation();
            // Convert location key to display format
            String displayName = CampusMap.getDisplayName(location);
            // If DORM, show DORM; otherwise show the display name
            if ("DORM".equals(location)) {
                currentLocationLabel.setText("Current: DORM");
            } else {
                currentLocationLabel.setText("Current: " + displayName.toUpperCase());
            }
            
            campusMapView.update();
            statsPanel.update();
        }
    }
}