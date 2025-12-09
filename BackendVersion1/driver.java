import java.util.List;
import java.util.Scanner;

/**
 * Driver class to test the game in pure text mode
 * 
 * @author AI Generated Driver Class for Testing the Game
 * @version 2025.12.7
 */
public class driver {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("   Wellesley College Life Simulator");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize the game
        System.out.println("Loading game data...");
        hashForLocation loader = new hashForLocation();
        loader.importFile("text for locations");
        
        GameManager game = new GameManager(loader);
        System.out.println("Game loaded successfully!");
        System.out.println();
        
        // Main game loop
        while (true) {
            // Display current game state
            displayGameState(game);
            
            // Get current options
            List<Option> options = game.getCurrentOptions();
            
            // Check if game is ending (no options available)
            if (options.isEmpty()) {
                // Game is ending - display ending text
                String endingText = game.getEndingText();
                if (!endingText.isEmpty()) {
                    System.out.println("\n========================================");
                    System.out.println("           GAME OVER");
                    System.out.println("========================================");
                    System.out.println();
                    System.out.println(endingText);
                    System.out.println();
                }
                break;
            }
            
            // Display options
            displayOptions(options);
            
            // Get user input
            System.out.print("\nEnter your choice (0-" + (options.size() - 1) + "): ");
            int choice = -1;
            
            try {
                String input = scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                
                if (choice < 0 || choice >= options.size()) {
                    System.out.println("Invalid choice! Please enter a number between 0 and " + (options.size() - 1));
                    System.out.println();
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                System.out.println();
                continue;
            }
            
            // Get the selected option to display consequence
            Option selectedOption = options.get(choice);
            
            // Process the choice
            game.processChoice(choice);
            
            // Display the consequence description
            if (selectedOption.consequence != null && 
                selectedOption.consequence.description != null && 
                !selectedOption.consequence.description.isEmpty()) {
                System.out.println();
                System.out.println(">>> " + selectedOption.consequence.description);
            }
            
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println();
        }
        
        scanner.close();
    }
    
    /**
     * Display the current game state (stats and current text)
     */
    private static void displayGameState(GameManager game) {
        System.out.println("----------------------------------------");
        System.out.println(game.getStatsDisplay());
        System.out.println("----------------------------------------");
        System.out.println();
        System.out.println(game.getCurrentText());
        System.out.println();
    }
    
    /**
     * Display all available options
     */
    private static void displayOptions(List<Option> options) {
        System.out.println("Options:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  [" + i + "] " + options.get(i).description);
        }
    }
}
