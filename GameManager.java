import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
* A class to manage game flow: navigation between locations, event processing, and player choice handling.
* @author (Praslin Hayes)
* @date (Dec.7, 2025)
**/

public class GameManager {


    private Map<String, List<Context>> locationEvents; // Stores all loaded events by location
    private Context currentEvent;
    private gameStat gameTracker; 
    private Random random;
    private DecisionTree decisionTree;

    // State flags
    private boolean isInNavigationMode; // True = Picking a location, False = In an event
    private boolean isForcedRandomEvent; // True if we are in the end-of-day random event

    // --- CONSTRUCTOR ---

    public GameManager(hashForLocation loader) {
        locationEvents = new HashMap<>();
        gameTracker = new gameStat(); 
        random = new Random();
        decisionTree = new DecisionTree();
        
        // 1. Load the text file content
        HashMap<String, ArrayList<Context>> loadedMap = loader.getMap();
        for (String location : loadedMap.keySet()) {
            locationEvents.put(location, new ArrayList<>(loadedMap.get(location)));
        }
        
        // 2. Start the game in Navigation Mode
        setNavigationState();
    }


    // --- GAME LOGIC ---

    /**
     * Sets the game state to the main menu where the user picks a location.
     */
    private void setNavigationState() {
        isInNavigationMode = true;
        isForcedRandomEvent = false;

        // Create a dummy node representing the map
        Context navContext = new Context("It is " + gameTracker.getSeason() + ". You have " + gameTracker.getCurrentEnergy() + " energy left today.\nWhere do you want to go?");
        
        // Add navigation options (these don't change stats, they just move you)
        // 0s for all stats because the cost is applied when the event starts
        navContext.addOption(new Option("Go to Lulu", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Clapp (Library)", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Jewett (Arts)", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Science Center", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Founders (Liberal Arts)", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Tower (Dorm)", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Chapel/Shuttle", new Consequence("", 0, 0, 0, 0.0)));
        navContext.addOption(new Option("Go to Club/Lake", new Consequence("", 0, 0, 0, 0.0)));

        currentEvent = navContext;
    }

    /**
     * Pulls a random event from the loaded file for a specific location.
     */
    private void triggerLocationEvent(String locationKey) {
        if (locationEvents.containsKey(locationKey)) {
            List<Context> events = locationEvents.get(locationKey);
            // Pick a random event
            Context next = events.get(random.nextInt(events.size()));
            currentEvent = next;
            isInNavigationMode = false;
        } else {
            System.out.println("Error: No events found for " + locationKey);
            setNavigationState(); // Fallback
        }
    }

    /**
     * Triggers the mandatory random event at the end of the day (Energy = 0).
     */
    private void triggerEndOfDayEvent() {
        isForcedRandomEvent = true;
        isInNavigationMode = false;

        // Pull a random event from ANY location to represent chaos/life
        // Or specifically from TOWER/DORM if preferred. Let's do random from all keys.
        List<String> keys = new ArrayList<>(locationEvents.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        
        List<Context> events = locationEvents.get(randomKey);
        Context next = events.get(random.nextInt(events.size()));
        

        currentEvent = next;

    }

    /**
     * Main method called by the UI when a button is clicked.
     */
    public void processChoice(int choiceIndex) {
        if (gameTracker.isEnding()) return; // Game over check

        // SCENARIO 1: We are in Navigation Mode (Map)
        if (isInNavigationMode) {
            String targetLocation = "";
            switch (choiceIndex) {
                case 0: targetLocation = "LULU"; break;
                case 1: targetLocation = "CLAPP"; break;
                case 2: targetLocation = "JEWETT"; break;
                case 3: targetLocation = "SCIENCE"; break;
                case 4: targetLocation = "FOUNDERS"; break;
                case 5: targetLocation = "TOWER"; break;
                case 6: targetLocation = "CHAPEL"; break;
                case 7: targetLocation = "CLUB"; break;
            }
            // Consume Energy for the action
            gameTracker.useEnergy(); 
            triggerLocationEvent(targetLocation);
            return;
        }

        // SCENARIO 2: We are inside an Event (Resolving a choice)
        if (choiceIndex < currentEvent.options.size()) {
            Option selected = currentEvent.options.get(choiceIndex);
            
            // 1. Apply Stats
            gameTracker.getPlayerStats().performConsequence(selected);

            // 2. Logic for "Next Step"
            
            // If we just finished the Forced Random Event, the day is over.
            if (isForcedRandomEvent) {
                // Since energy was 0, useEnergy() inside GameStat class should have already
                // triggered advanceMonth() when we clicked the Navigation button previously.
                
                
                // We need to manually reset energy or allow the calendar to advance here.
                // Assuming gameStat handles the advancement automatically when hitting 0,
                // we just go back to navigation for the new month.
                
                if (gameTracker.isEnding()) {
                   // Logic handled in UI via getEndingText()
                } else {
                   setNavigationState();
                }
            } 
            // If we just finished a normal Action
            else {
                // Check if we are out of energy. 
                // Note: gameTracker.currentEnergy is decremented immediately when choosing location.
                
                if (gameTracker.getCurrentEnergy() == 0) {
                    // Trigger the forced random event
                    triggerEndOfDayEvent();
                } else {
                    // Go back to map for next action
                    setNavigationState();
                }
            }
        }
    }

    // --- ENDINGS ---

    public String getEndingText() {
        if (!gameTracker.isEnding()) return "";

        PlayerStat p = gameTracker.getPlayerStats();
        
        return decisionTree.getRoot().getAchievement(p);
    }

    // --- GETTERS FOR UI ---
    
    public String getCurrentText() { 
        if (gameTracker.isEnding()) return getEndingText();
        
        String prefix = "";
        if (isForcedRandomEvent) {
            prefix = "[END OF DAY EVENT] \n";
        }
        return prefix + currentEvent.description; 
    }
    
    public List<Option> getCurrentOptions() { 
        if (gameTracker.isEnding()) return new ArrayList<>(); // No options if game over
        return currentEvent.options; 
    }
    
    public String getStatsDisplay() {
        PlayerStat p = gameTracker.getPlayerStats();
        return String.format("Year: %d | Month: %d | Energy: %d/3\nGPA: %.2f | Happy: %d | Social: %d | Health: %d", 
               gameTracker.getYear(), gameTracker.getMonth(), gameTracker.getCurrentEnergy(),
               p.getGPA(), p.getHappiness(), p.getSocial(), p.getHealth());
    }
}
