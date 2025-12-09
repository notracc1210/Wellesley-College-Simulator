import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {


    private Map<String, List<Context>> locationEvents; // Stores all loaded events by location
    private Context currentEvent;
    private gameStat gameTracker; 
    private Random random;
    private DecisionTree decisionTree;

    // State flags
    private boolean isInNavigationMode; // True = Picking a location, False = In an event
    private boolean isForcedRandomEvent; // True if we are in the end-of-day random event
    private String currentLocation; // Tracks the player's current location

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
        currentLocation = "DORM"; // Initial location
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
        Context navContext = new Context("It is " + gameTracker.getSeason() + ". You have " + gameTracker.getCurrentEnergy() + " energy left this month.\nWhere do you want to go?");
        
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
            currentLocation = locationKey; // Update current location
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

            // 2. Check if game should end (e.g., dropout condition met)
            if (gameTracker.isEnding()) {
                // Game over - logic handled in UI via getEndingText()
                return;
            }

            // 3. Logic for "Next Step"
            
            // If we just finished the Forced Random Event, the day is over.
            if (isForcedRandomEvent) {
                // Since energy was 0, useEnergy() inside GameStat class should have already
                // triggered advanceMonth() when we clicked the Navigation button previously.
                // However, per your prompt: "Day ends after 3 actions AND the random event."
                
                // We need to manually reset energy or allow the calendar to advance here.
                // Assuming gameStat handles the advancement automatically when hitting 0,
                // we just go back to navigation for the new month.
                setNavigationState();
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
        
        // Check if game ended due to invalid stats (dropout) vs. graduation (year > 4)
        // If year > 4, use normal decision tree. Otherwise, check which stat failed.
        if (gameTracker.getYear() > 4) {
            // Graduated - use normal decision tree
            return decisionTree.getRoot().getAchievement(p);
        }
        
        // Game ended due to stat failure - determine which stat failed FIRST
        // IMPORTANT: Check stats BEFORE calling DecisionTree, which has fallback "Drop Out Hero"
        // Check in order of priority to see which one triggered the ending
        
        // Check for social isolation first (special case - 3+ months)
        if (gameTracker.getConsecutiveLowSocialMonths() >= 3 && p.getSocial() < 10) {
            return "SOCIAL ISOLATION: The Hermit Life Destroyed You\n\n" +
                   "Your social connections have been below 10 for over 3 months, and the isolation has finally broken you completely. " +
                   "You've become a campus cryptid—people whisper about 'that person who never talks to anyone' but no one actually knows your name. " +
                   "You're not a person anymore; you're a ghost that occasionally appears in the library or Lulu, always alone, always silent.\n\n" +
                   "You eat alone. You study alone. You exist alone. The loneliness has triggered a massive happiness crash that you'll never recover from. " +
                   "You're trapped in a spiral of isolation and despair. You try to talk to someone, but your voice comes out as a croak because you haven't spoken in weeks. " +
                   "They look at you like you're a strange creature and quickly walk away.\n\n" +
                   "You'll graduate (maybe, if you can even make it that far), but you'll leave with zero friends, zero memories, zero connections. " +
                   "You'll have no idea how to function in a world that requires human interaction. Job interviews? You'll fail them all because you've forgotten how to talk to people. " +
                   "Dating? Forget it. You don't know how to have a conversation anymore.\n\n" +
                   "You'll spend your life as a professional recluse, working from home (if you can even get a job), ordering groceries online, " +
                   "and having your only human interactions be with delivery drivers who avoid eye contact. " +
                   "Your neighbors will wonder if you're dead because they never see you. Eventually, you will be dead, and no one will notice for weeks.\n\n" +
                   "You'll die alone in your apartment, and your body will be discovered by a landlord who's finally come to collect rent. " +
                   "No one will come to your funeral because there's no one left to invite. You'll be buried in an unmarked grave, forgotten, as if you never existed at all.";
        }
        
        // Check which stat failed - check DIRECTLY with proper thresholds
        // Use <= for stats that can be exactly 0, and < for GPA which needs to be below 2.0
        // Check GPA first since it's the most common failure
        if (p.getGPA() < 2.0) {
            // GPA failed - Academic Dismissal
            return "ACADEMIC DISMISSAL: Kicked Out by the University\n\n" +
                   "Your GPA dropped below 2.0, and the administration has officially had ENOUGH of your academic failure. " +
                   "This wasn't your choice to drop out—you got KICKED OUT. Expelled. Booted. Gone.\n\n" +
                   "You receive a formal letter in the mail (yes, actual mail, because email is too good for failures like you) " +
                   "explaining that you are no longer welcome at Wellesley College. No diploma. No degree. No refund. " +
                   "Just a permanent mark on your transcript that screams 'ACADEMIC PROBATION → DISMISSAL' in bold letters.\n\n" +
                   "Your parents are beyond disappointed—they're actively avoiding telling relatives about you at family gatherings. " +
                   "Your friends are confused and slowly stop inviting you to things because 'college dropout' is awkward to explain. " +
                   "Your high school teachers who wrote your recommendation letters are questioning their judgment.\n\n" +
                   "You'll spend the next decade working minimum wage retail jobs, living in your childhood bedroom at age 30, " +
                   "and explaining to every single date why you don't have a college degree. Spoiler: most of them stop responding after that. " +
                   "You'll scroll through LinkedIn seeing all your former classmates with successful careers, nice apartments, and actual lives, " +
                   "while you're still trying to figure out if you can afford rent on a studio apartment with your $12/hour salary.\n\n" +
                   "You'll die alone, surrounded by nothing but regret and the knowledge that you had every opportunity and squandered it all. " +
                   "At least you'll have plenty of time to reflect on your choices... in your parents' basement... forever.";
        }
        
        if (p.getHealth() <= 0) {
            // Health failed - Hospitalized
            return "HOSPITALIZED: Your Body Completely Gave Up\n\n" +
                   "Your health hit absolute zero, and your body finally said 'I'M DONE WITH THIS BULLSHIT' and shut down completely. " +
                   "You're now in the hospital, hooked up to machines that beep ominously, with doctors shaking their heads at how you " +
                   "managed to destroy yourself this thoroughly. The nurse keeps asking if you have anyone to call, and you realize you don't.\n\n" +
                   "Your friends visit once out of obligation, take awkward photos for Instagram with the caption 'visiting our friend! get well soon! ❤️', " +
                   "and then never come back. Your professors send 'get well soon' emails that feel more like 'please don't die, we don't want the paperwork' " +
                   "and immediately assign your work to other students. You're replaceable, and everyone knows it.\n\n" +
                   "The medical bills start arriving. $50,000. $75,000. $100,000. Your parents' insurance won't cover it all. " +
                   "You'll spend the rest of your life paying off debt from a hospital stay that could have been prevented if you'd just... " +
                   "slept? Eaten? Taken care of yourself? But no, you chose to grind yourself into dust.\n\n" +
                   "You'll recover eventually (maybe), but you'll be permanently damaged. Chronic fatigue. Weakened immune system. " +
                   "The doctors say you'll never fully recover. Your body is broken, and it's your fault. " +
                   "At least the hospital food is better than Lulu... wait, no it's not. It's worse. Much worse.\n\n" +
                   "You'll spend your remaining years fragile, sickly, and alone, because who wants to date someone who's always in and out of hospitals? " +
                   "You'll die early, and your obituary will say 'died of complications from poor self-care' because that's the polite way of saying you killed yourself through neglect.";
        }
        
        if (p.getHappiness() <= 0) {
            // Happiness failed - Clinical Depression
            return "CLINICAL DEPRESSION: The Void Consumed You\n\n" +
                   "Your happiness hit absolute zero, and you've officially entered the void. You can't get out of bed. " +
                   "You can't remember why you came to college. You can't remember why you do anything. " +
                   "You can't even cry in the Lulu bathroom anymore because that requires too much energy, and you have none left.\n\n" +
                   "Your therapist has given up on you. After the 47th session where you just stared at the wall, they suggested you 'try harder' " +
                   "and then stopped returning your calls. Your friends have given up—they tried to help, but you're too much work. " +
                   "Your professors have given up—you've missed too many classes, failed too many assignments. You've given up. Everyone has given up.\n\n" +
                   "You're not dropping out—you're being consumed by a black hole of despair that makes every day feel like an eternity of nothingness. " +
                   "The only thing you're good at now is existing in a state of perpetual sadness. You've achieved peak misery. Congratulations.\n\n" +
                   "You'll spend the next several years in your parents' house, unable to function, unable to work, unable to do anything except exist. " +
                   "Your parents will eventually get tired of supporting you and start making passive-aggressive comments about 'getting your life together.' " +
                   "You won't. You can't. The depression has won, and you've lost.\n\n" +
                   "You'll die alone, having never experienced joy again, having never recovered, having never lived. " +
                   "Your tombstone will read 'Here lies someone who used to be happy, once, maybe, a long time ago.' " +
                   "But honestly? No one will visit your grave anyway.";
        }
        
        if (p.getSocial() <= 0) {
            // Social failed - Isolation
            return "SOCIAL ISOLATION: The Hermit Life Destroyed You\n\n" +
                   "Your social connections hit zero, and you've become completely isolated. " +
                   "You've become a campus cryptid—people whisper about 'that person who never talks to anyone' but no one actually knows your name. " +
                   "You're not a person anymore; you're a ghost that occasionally appears in the library or Lulu, always alone, always silent.\n\n" +
                   "You eat alone. You study alone. You exist alone. The loneliness has triggered a massive happiness crash that you'll never recover from. " +
                   "You're trapped in a spiral of isolation and despair. You try to talk to someone, but your voice comes out as a croak because you haven't spoken in weeks. " +
                   "They look at you like you're a strange creature and quickly walk away.\n\n" +
                   "You'll graduate (maybe, if you can even make it that far), but you'll leave with zero friends, zero memories, zero connections. " +
                   "You'll have no idea how to function in a world that requires human interaction. Job interviews? You'll fail them all because you've forgotten how to talk to people. " +
                   "Dating? Forget it. You don't know how to have a conversation anymore.\n\n" +
                   "You'll spend your life as a professional recluse, working from home (if you can even get a job), ordering groceries online, " +
                   "and having your only human interactions be with delivery drivers who avoid eye contact. " +
                   "Your neighbors will wonder if you're dead because they never see you. Eventually, you will be dead, and no one will notice for weeks.\n\n" +
                   "You'll die alone in your apartment, and your body will be discovered by a landlord who's finally come to collect rent. " +
                   "No one will come to your funeral because there's no one left to invite. You'll be buried in an unmarked grave, forgotten, as if you never existed at all.";
        }
        
        // If we get here, something unexpected happened
        // This should NOT happen if a stat failed - all failures should be caught above
        // But if it does, we need to check DecisionTree, but it will return "Drop Out Hero"
        // So let's add a more specific message
        String decisionTreeResult = decisionTree.getRoot().getAchievement(p);
        
        // If DecisionTree returns "Drop Out Hero", it means a stat failed but we didn't catch it
        // In that case, check stats one more time with debug info
        if (decisionTreeResult.contains("Drop Out Hero")) {
            // Double-check which stat actually failed
            if (p.getGPA() < 2.0) {
                return "ACADEMIC DISMISSAL: Kicked Out by the University\n\n" +
                       "Your GPA dropped below 2.0, and the administration has officially had ENOUGH of your academic failure. " +
                       "This wasn't your choice to drop out—you got KICKED OUT. Expelled. Booted. Gone.\n\n" +
                       "You receive a formal letter in the mail (yes, actual mail, because email is too good for failures like you) " +
                       "explaining that you are no longer welcome at Wellesley College. No diploma. No degree. No refund. " +
                       "Just a permanent mark on your transcript that screams 'ACADEMIC PROBATION → DISMISSAL' in bold letters.\n\n" +
                       "Your parents are beyond disappointed—they're actively avoiding telling relatives about you at family gatherings. " +
                       "Your friends are confused and slowly stop inviting you to things because 'college dropout' is awkward to explain. " +
                       "Your high school teachers who wrote your recommendation letters are questioning their judgment.\n\n" +
                       "You'll spend the next decade working minimum wage retail jobs, living in your childhood bedroom at age 30, " +
                       "and explaining to every single date why you don't have a college degree. Spoiler: most of them stop responding after that. " +
                       "You'll scroll through LinkedIn seeing all your former classmates with successful careers, nice apartments, and actual lives, " +
                       "while you're still trying to figure out if you can afford rent on a studio apartment with your $12/hour salary.\n\n" +
                       "You'll die alone, surrounded by nothing but regret and the knowledge that you had every opportunity and squandered it all. " +
                       "At least you'll have plenty of time to reflect on your choices... in your parents' basement... forever.";
            }
            if (p.getHealth() <= 0) {
                return "HOSPITALIZED: Your Body Completely Gave Up\n\n" +
                       "Your health hit absolute zero, and your body finally said 'I'M DONE WITH THIS BULLSHIT' and shut down completely. " +
                       "You're now in the hospital, hooked up to machines that beep ominously, with doctors shaking their heads at how you " +
                       "managed to destroy yourself this thoroughly. The nurse keeps asking if you have anyone to call, and you realize you don't.\n\n" +
                       "Your friends visit once out of obligation, take awkward photos for Instagram with the caption 'visiting our friend! get well soon! ❤️', " +
                       "and then never come back. Your professors send 'get well soon' emails that feel more like 'please don't die, we don't want the paperwork' " +
                       "and immediately assign your work to other students. You're replaceable, and everyone knows it.\n\n" +
                       "The medical bills start arriving. $50,000. $75,000. $100,000. Your parents' insurance won't cover it all. " +
                       "You'll spend the rest of your life paying off debt from a hospital stay that could have been prevented if you'd just... " +
                       "slept? Eaten? Taken care of yourself? But no, you chose to grind yourself into dust.\n\n" +
                       "You'll recover eventually (maybe), but you'll be permanently damaged. Chronic fatigue. Weakened immune system. " +
                       "The doctors say you'll never fully recover. Your body is broken, and it's your fault. " +
                       "At least the hospital food is better than Lulu... wait, no it's not. It's worse. Much worse.\n\n" +
                       "You'll spend your remaining years fragile, sickly, and alone, because who wants to date someone who's always in and out of hospitals? " +
                       "You'll die early, and your obituary will say 'died of complications from poor self-care' because that's the polite way of saying you killed yourself through neglect.";
            }
            if (p.getHappiness() <= 0) {
                return "CLINICAL DEPRESSION: The Void Consumed You\n\n" +
                       "Your happiness hit absolute zero, and you've officially entered the void. You can't get out of bed. " +
                       "You can't remember why you came to college. You can't remember why you do anything. " +
                       "You can't even cry in the Lulu bathroom anymore because that requires too much energy, and you have none left.\n\n" +
                       "Your therapist has given up on you. After the 47th session where you just stared at the wall, they suggested you 'try harder' " +
                       "and then stopped returning your calls. Your friends have given up—they tried to help, but you're too much work. " +
                       "Your professors have given up—you've missed too many classes, failed too many assignments. You've given up. Everyone has given up.\n\n" +
                       "You're not dropping out—you're being consumed by a black hole of despair that makes every day feel like an eternity of nothingness. " +
                       "The only thing you're good at now is existing in a state of perpetual sadness. You've achieved peak misery. Congratulations.\n\n" +
                       "You'll spend the next several years in your parents' house, unable to function, unable to work, unable to do anything except exist. " +
                       "Your parents will eventually get tired of supporting you and start making passive-aggressive comments about 'getting your life together.' " +
                       "You won't. You can't. The depression has won, and you've lost.\n\n" +
                       "You'll die alone, having never experienced joy again, having never recovered, having never lived. " +
                       "Your tombstone will read 'Here lies someone who used to be happy, once, maybe, a long time ago.' " +
                       "But honestly? No one will visit your grave anyway.";
            }
            if (p.getSocial() <= 0) {
                return "SOCIAL ISOLATION: The Hermit Life Destroyed You\n\n" +
                       "Your social connections hit zero, and you've become completely isolated. " +
                       "You've become a campus cryptid—people whisper about 'that person who never talks to anyone' but no one actually knows your name. " +
                       "You're not a person anymore; you're a ghost that occasionally appears in the library or Lulu, always alone, always silent.\n\n" +
                       "You eat alone. You study alone. You exist alone. The loneliness has triggered a massive happiness crash that you'll never recover from. " +
                       "You're trapped in a spiral of isolation and despair. You try to talk to someone, but your voice comes out as a croak because you haven't spoken in weeks. " +
                       "They look at you like you're a strange creature and quickly walk away.\n\n" +
                       "You'll graduate (maybe, if you can even make it that far), but you'll leave with zero friends, zero memories, zero connections. " +
                       "You'll have no idea how to function in a world that requires human interaction. Job interviews? You'll fail them all because you've forgotten how to talk to people. " +
                       "Dating? Forget it. You don't know how to have a conversation anymore.\n\n" +
                       "You'll spend your life as a professional recluse, working from home (if you can even get a job), ordering groceries online, " +
                       "and having your only human interactions be with delivery drivers who avoid eye contact. " +
                       "Your neighbors will wonder if you're dead because they never see you. Eventually, you will be dead, and no one will notice for weeks.\n\n" +
                       "You'll die alone in your apartment, and your body will be discovered by a landlord who's finally come to collect rent. " +
                       "No one will come to your funeral because there's no one left to invite. You'll be buried in an unmarked grave, forgotten, as if you never existed at all.";
            }
        }
        
        // If DecisionTree didn't return "Drop Out Hero", it's a normal graduation ending
        return decisionTreeResult;
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
    
    /**
     * Get the current location name.
     * @return String representing the current location (e.g., "DORM", "LULU", etc.)
     */
    public String getCurrentLocation() {
        return currentLocation != null ? currentLocation : "DORM";
    }
    
    /**
     * Get the game tracker for accessing year, month, energy info.
     * @return gameStat instance
     */
    public gameStat getGameTracker() {
        return gameTracker;
    }
    
    /**
     * Check if the game is currently in navigation mode (map selection).
     * @return true if player is selecting a location, false if in an event
     */
    public boolean isInNavigationMode() {
        return isInNavigationMode;
    }
}