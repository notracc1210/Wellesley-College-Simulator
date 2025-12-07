
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    // --- INNER CLASSES ---

    // 1. Option Class (Formerly Choice)
    // Renamed to 'Option' because PlayerStat.performConsequence expects an 'Option' object
    public class Option {
        String text;             // What the button says
        String nextEventId;      // The ID of the node this leads to
        
        // Stats consequences
        double gpaEffect;       
        int happinessEffect;
        int socialEffect;
        int healthEffect;        // Added Health to match PlayerStat

        public Option(String text, String nextId, double gpa, int happy, int social, int health) {
            this.text = text;
            this.nextEventId = nextId;
            this.gpaEffect = gpa;
            this.happinessEffect = happy;
            this.socialEffect = social;
            this.healthEffect = health;
        }

        // Getters required by PlayerStat.performConsequence
        public double getGPAChange() { return gpaEffect; }
        public int getHappinessChange() { return happinessEffect; }
        public int getSocialChange() { return socialEffect; }
        public int getHealthChange() { return healthEffect; }
    }

    // 2. EventNode Class
    public class EventNode {
        String eventId;
        String speaker; 
        String mainText;    
        List<Option> options;

        // Constructor for Narrator
        public EventNode(String id, String text) {
            this.eventId = id;
            this.speaker = "Narrator";
            this.mainText = text;
            this.options = new ArrayList<>();
        }

        // Constructor for NPC
        public EventNode(String id, String speaker, String text) {
            this.eventId = id;
            this.speaker = speaker; 
            this.mainText = text;
            this.options = new ArrayList<>();
        }

        public void addOption(Option c) {
            options.add(c);
        }
    }

    // --- GAME MANAGER FIELDS ---

    private Map<String, EventNode> storyMap; 
    private EventNode currentEvent;
    
    // REPLACED StudentStats with your GameStat class
    private GameStat gameTracker; 
    
    private String[] raNames = {"Tara", "Ellie", "Paige", "Maya", "Carmen"};
    private String currentRA; 

    // --- CONSTRUCTOR ---

    public GameManager() {
        storyMap = new HashMap<>();
        
        // Initialize the new GameStat system
        // Note: Ensure your GameStat constructor initializes its internal PlayerStat!
        gameTracker = new GameStat(); 
        
        currentRA = pickRandomRA(); 
        loadStory(); 
    }

    // --- CORE LOGIC ---

    private String pickRandomRA() {
        int index = (int) (Math.random() * raNames.length); 
        return raNames[index];
    }

    private void loadStory() {
        // --- LEVEL 1: THE ROOT ---
        EventNode start = new EventNode("root", "It is a beautiful day at Wellesley. Where do you go?");
        
        // --- LEVEL 2: MAIN LOCATIONS ---
        EventNode library = new EventNode("library", "You enter Clapp Library. It smells like old books.");
        EventNode lulu = new EventNode("lulu", "You are at Lulu. It's chaotic during lunch rush.");
        EventNode ksc = new EventNode("KSC", "You are at the KSC (Gym). Varsity athletes are training.");
        EventNode dorm = new EventNode("dorm", "You are back in your dorm room.");
        EventNode lake = new EventNode("lake", "You are walking around Lake Waban.");

        // ROOT CONNECTIONS 
        // Note: Added 0 as the last parameter (Health) for most options
        start.addOption(new Option("Go to Library", "library", 0.0, -2, 0, 0));
        start.addOption(new Option("Go to Lulu", "lulu", 0.0, 5, 5, -2)); // Unhealthy food?
        start.addOption(new Option("Go to KSC", "KSC", 0.0, 5, 0, 10));   // Health boost
        start.addOption(new Option("Go to Dorm", "dorm", 0.0, 5, -5, 5)); // Rest
        start.addOption(new Option("Walk the Lake", "lake", 0.0, 10, 0, 5));

        // --- LEVEL 3: DEEP DIVES ---

        // == LIBRARY BRANCH ==
        EventNode libQuiet = new EventNode("lib_quiet", "You find a desk in the stacks. Absolute silence.");
        EventNode libGroup = new EventNode("lib_group", "You join a study group on the main floor.");

        library.addOption(new Option("Go to quiet stacks", "lib_quiet", 0.05, -5, -5, 0));
        library.addOption(new Option("Join a group table", "lib_group", 0.0, 5, 5, 0));
        library.addOption(new Option("Leave", "root", 0.0, 0, 0, 0));

        // Library Outcomes
        libQuiet.addOption(new Option("Focus intently", "root", 0.2, -10, 0, -5)); // High GPA, Low Happy, Low Health
        libQuiet.addOption(new Option("Scroll on phone", "root", -0.1, 5, 0, 0)); 

        libGroup.addOption(new Option("Review notes together", "root", 0.1, 0, 5, 0));
        libGroup.addOption(new Option("Just gossip", "root", -0.1, 10, 15, 0)); 

        // == LULU BRANCH ==
        EventNode luluPizza = new EventNode("lulu_pizza", "You wait in the long line for pizza.");
        EventNode luluSalad = new EventNode("lulu_salad", "You grab a salad quickly.");

        lulu.addOption(new Option("Get Pizza (Long line)", "lulu_pizza", 0.0, 5, 5, -5));
        lulu.addOption(new Option("Get Salad (Fast)", "lulu_salad", 0.0, 0, 0, 5));

        luluPizza.addOption(new Option("Chat with person in line", "root", 0.0, 5, 10, 0)); 
        luluSalad.addOption(new Option("Eat while studying", "root", 0.1, -5, 0, 0)); 

        // == KSC BRANCH ==
        ksc.addOption(new Option("Heavy lifting", "root", 0.0, 10, 2, 10));
        ksc.addOption(new Option("Yoga class", "root", 0.0, 15, 5, 5));
        ksc.addOption(new Option("Too tired, go back", "root", 0.0, -5, 0, 0));

        // == LAKE BRANCH ==
        lake.addOption(new Option("Listen to a podcast", "root", 0.0, 15, -5, 5)); 
        lake.addOption(new Option("Call a friend", "root", 0.0, 10, 10, 0)); 

        // == DORM & RA BRANCH ==
        dorm.addOption(new Option("Take a nap", "root", 0.0, 10, 0, 10));
        dorm.addOption(new Option("Knock on RA's door", "RA_start", 0.0, 0, 5, 0));
        dorm.addOption(new Option("Go outside", "root", 0.0, 0, 0, 0));

        // NPC: RA Interaction
        EventNode raStart = new EventNode("RA_start", currentRA, 
            "Hey there! I'm " + currentRA + ". Everything okay?");
        
        EventNode raAcademic = new EventNode("RA_academic", currentRA, 
            "For classes, I really recommend the PLTC tutors. Don't be afraid to ask for help!");
        EventNode raSocial = new EventNode("RA_social", currentRA, 
            "Join a specialized org! Or just leave your door open when you're in.");
        EventNode raFunny = new EventNode("RA_funny", currentRA, 
            "Honestly? I'm just trying to survive finals too.");

        // RA Tree Connections
        raStart.addOption(new Option("I'm stressed about grades.", "RA_academic", 0.0, -2, 2, -2));
        raStart.addOption(new Option("I feel lonely.", "RA_social", 0.0, 2, 5, 0));
        raStart.addOption(new Option("Just saying hi!", "RA_funny", 0.0, 5, 5, 0));
        raStart.addOption(new Option("Nevermind.", "dorm", 0.0, 0, 0, 0));

        // RA Outcomes
        raAcademic.addOption(new Option("Thanks, I'll go study.", "library", 0.1, 0, 0, 0));
        raAcademic.addOption(new Option("Can I ask something else?", "RA_start", 0.0, 0, 0, 0));

        raSocial.addOption(new Option("I'll go to Lulu.", "lulu", 0.0, 5, 5, 0));
        raSocial.addOption(new Option("Can I ask something else?", "RA_start", 0.0, 0, 0, 0));

        raFunny.addOption(new Option("Good luck!", "dorm", 0.0, 2, 2, 0));

        // --- REGISTER NODES TO MAP ---
        storyMap.put("root", start);
        storyMap.put("library", library);
        storyMap.put("lulu", lulu);
        storyMap.put("KSC", ksc);
        storyMap.put("dorm", dorm);
        storyMap.put("lake", lake);

        storyMap.put("lib_quiet", libQuiet);
        storyMap.put("lib_group", libGroup);
        storyMap.put("lulu_pizza", luluPizza);
        storyMap.put("lulu_salad", luluSalad);

        storyMap.put("RA_start", raStart);
        storyMap.put("RA_academic", raAcademic);
        storyMap.put("RA_social", raSocial);
        storyMap.put("RA_funny", raFunny);

        currentEvent = start;
    }

    // --- INTERACTION METHODS ---

    public void processChoice(int choiceIndex) {
        if (choiceIndex >= currentEvent.options.size()) return;

        Option selected = currentEvent.options.get(choiceIndex);
        
        // 1. Update stats using PlayerStat logic
        // We get the player stats from the game tracker
        PlayerStat pStat = gameTracker.getPlayerStats();
        if (pStat != null) {
            pStat.performConsequence(selected);
        }

        // 2. Consume Time/Energy using GameStat logic
        gameTracker.useEnergy();
        
        // 3. Check for End Game conditions
        if (gameTracker.isEnding()) {
            System.out.println("GAME OVER");
            
            return;
        }

        // 4. Navigate
        if (storyMap.containsKey(selected.nextEventId)) {
            currentEvent = storyMap.get(selected.nextEventId);
        } else {
            System.out.println("ERROR: Event ID " + selected.nextEventId + " not found!");
        }
    }

    // --- GETTERS ---
    
    public String getCurrentText() { return currentEvent.mainText; }
    public String getCurrentSpeaker() { return currentEvent.speaker; }
    public List<Option> getCurrentOptions() { return currentEvent.options; }
    
    // New Getters for UI to display Time and Stats
    public String getTimeDisplay() {
        return "Year " + gameTracker.getYear() + " | " + gameTracker.getSeason() + 
               " (Energy: " + gameTracker.getCurrentEnergy() + "/3)";
    }
    
    public String getStatsDisplay() {
        PlayerStat p = gameTracker.getPlayerStats();
        if (p == null) return "Stats Loading...";
        return String.format("GPA: %.2f | Happy: %d | Social: %d | Health: %d | Mood: %s", 
               p.getGPA(), p.getHappiness(), p.getSocial(), p.getHealth(), p.getMood());
    }
}
