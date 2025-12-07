
/**
 * 在这里给出对类 DecisionTree 的描述。
 * 
 * @author Tracy
 * @version 2025.12.7
 */
public class DecisionTree 
{
    
    Node deansList            = new AchievementNode("Dean’s List Legend", "Finish with a perfect academic year while staying mentally, physically, and socially strong. You didn’t just survive—you optimized.");
    Node perfect400           = new AchievementNode("Perfectly Imperfect 4.00","You reached a perfect GPA even though life was clearly on fire in at least one department. Academic goblin mode activated.");
    Node noThoughts           = new AchievementNode("No Thoughts, Just Vibes","You came to college for the experience, not the syllabus. Somehow, it still worked out.");
    Node campusGhost          = new AchievementNode("Campus Ghost","You mastered the material but barely existed in the social ecosystem. Professors knew you well. No one else did.");
    Node wellnessWarrior      = new AchievementNode("Wellness Warrior","You protected your health like it was your primary major. Sleep, food, and sanity all secured.");
    Node burnoutSpeedrun      = new AchievementNode("Burnout Speedrun","You held your GPA together while your body and happiness completely collapsed. Technically impressive. Deeply concerning.");
    Node balancedMonarch      = new AchievementNode("Balanced Monarch","Nothing hit perfection, nothing crashed. You walked the middle path with rare consistency.");
    Node socialButterflyLab   = new AchievementNode("Social Butterfly in a Lab Coat","You somehow maintained near-elite social life while still handling heavy academic pressure.");
    Node luluPhilosopher      = new AchievementNode("Lulu Philosopher","Your real education happened over campus food and late-night conversations. Grades existed, but meaning mattered more.");
    Node starryScholar        = new AchievementNode("Starry-Eyed Scholar","You kept both curiosity and performance alive. Learning still felt magical by the end.");
    Node botanicalRecharge    = new AchievementNode("Botanical Recharge","You chose recovery over constant grind, and your body and mood both thanked you for it.");
    Node officeHourHero       = new AchievementNode("Office Hour Hero","Through pure persistence and asking for help, you built a surprisingly strong academic finish.");
    Node chaosButCharming     = new AchievementNode("Chaos But Charming","Academics were mid, health was questionable, but socially you were unstoppable and genuinely happy.");
    Node chronicOvercommitter = new AchievementNode("Chronic Overcommitter","You said yes to everything. Social life boomed, health and grades quietly suffered.");
    Node researchGoblin       = new AchievementNode("Research Goblin","You lived in academic isolation, trading social life for intellectual obsession and results.");
    Node quietComeback        = new AchievementNode("Quiet Comeback","Your ending stats told a recovery story. The early struggle didn’t define your final form.");
    Node permaProcrastinator  = new AchievementNode("Perma-Procrastinator","Nothing fully collapsed, but nothing truly flourished either. Everything stayed in a permanent “later.”");
    Node healthBarZero        = new AchievementNode("Health Bar at Zero","Your body forced the game to pause. No stat mattered more than this one anymore.");
    Node socialReset          = new AchievementNode("Social Reset","You rebuilt your entire social world after almost losing it completely. A rare second beginning.");
    Node invisibleGraduate    = new AchievementNode("Invisible Graduate","You earned the degree with quiet consistency, but left very little emotional footprint on campus.");
    
    Node root;

    public DecisionTree(){
        DecisionNode deanOrPerfectCheck = new DecisionNode("GPA", "==", 4.0, 
                                    new DecisionNode("HAPPINESS", ">=", 80, 
                                        new DecisionNode("HEALTH", ">=",80,
                                            new DecisionNode("SOCIAL", ">=", 80, 
                                                deansList, 
                                                perfect400),
                                            perfect400),
                                        perfect400),
                                    null);
        
        Node burnoutChek = new DecisionNode("HAPPINESS", "<=", 10, 
                             new DecisionNode("HEALTH", "<=", 10,
                                 burnoutSpeedrun,
                                 null),
                             null);
                             
        Node campusGhostCheck = new DecisionNode("SOCIAL", "<=", 10, campusGhost, null);
        
        Node invisibleCheck = new DecisionNode("SOCIAL", "<=", 20,
                                new DecisionNode("HAPPINESS", "<=", 50,
                                    invisibleGraduate,
                                    null),
                                null);
                                
        Node researchGoblinCheck = new DecisionNode("GPA", ">=", 3.85,
                                     new DecisionNode("SOCIAL", "<=", 40,
                                        new DecisionNode("HAPPINESS", ">=", 60,
                                            researchGoblin,
                                            null),
                                        null),
                                    null);
                                    
        Node officeHourCheck = new DecisionNode("GPA", ">=", 3.85,
                                new DecisionNode("SOCIAL", "<=", 60,
                                    officeHourHero,
                                    null),
                                null);
        
 
        Node balancedCheck = new DecisionNode("GPA", ">=", 3.70,
                                new DecisionNode("HAPPINESS", ">=", 65,
                                    new DecisionNode("HEALTH", ">=", 65,
                                        new DecisionNode("SOCIAL", ">=", 65,
                                            balancedMonarch,
                                            null),
                                        null),
                                    null),
                                null);

        Node starryCheck = new DecisionNode("GPA", ">=", 3.80,
                            new DecisionNode("HAPPINESS", ">=", 80,
                                starryScholar,
                                null),
                            null);

        Node socialButterflyCheck = new DecisionNode("SOCIAL", ">=", 95,
                                        new DecisionNode("GPA", ">=", 3.50,
                                            socialButterflyLab,
                                            null),
                                        null);

        Node wellnessCheck = new DecisionNode("HEALTH", ">=", 95,
                                wellnessWarrior,
                                null);

        Node quietComebackCheck = new DecisionNode("GPA", ">=", 3.50,
                                    new DecisionNode("HAPPINESS", ">=", 70,
                                        new DecisionNode("HEALTH", ">=", 65,
                                            quietComeback,
                                            null),
                                        null),
                                    null);

        Node highGpaFallback = permaProcrastinator;

        Node highGpaSubtree =
            new DecisionNode("HAPPINESS", "<=", 10,
                new DecisionNode("HEALTH", "<=", 10,
                    burnoutSpeedrun,
                    new DecisionNode("SOCIAL", "<=", 10,
                        campusGhost,
                        new DecisionNode("SOCIAL", "<=", 20,
                            new DecisionNode("HAPPINESS", "<=", 50,
                                invisibleGraduate,
                                new DecisionNode("GPA", "==", 4.0,
                                    deanOrPerfectCheck.getYesChild(),
                                    new DecisionNode("GPA", ">=", 3.85,
                                        new DecisionNode("SOCIAL", "<=", 40,
                                            new DecisionNode("HAPPINESS", ">=", 60,
                                                researchGoblin,
                                                officeHourHero
                                            ),
                                            officeHourHero
                                        ),
                                        new DecisionNode("GPA", ">=", 3.80,
                                            new DecisionNode("HAPPINESS", ">=", 80,
                                                starryScholar,
                                                new DecisionNode("HAPPINESS", ">=", 65,
                                                    new DecisionNode("HEALTH", ">=", 65,
                                                        new DecisionNode("SOCIAL", ">=", 65,
                                                            balancedMonarch,
                                                            socialButterflyLab
                                                        ),
                                                        socialButterflyLab
                                                    ),
                                                    socialButterflyLab
                                                )
                                            ),
                                            new DecisionNode("GPA", ">=", 3.70,
                                                new DecisionNode("HAPPINESS", ">=", 65,
                                                    new DecisionNode("HEALTH", ">=", 65,
                                                        new DecisionNode("SOCIAL", ">=", 65,
                                                            balancedMonarch,
                                                            socialButterflyLab
                                                        ),
                                                        socialButterflyLab
                                                    ),
                                                    socialButterflyLab
                                                ),
                                                new DecisionNode("HEALTH", ">=", 95,
                                                    wellnessWarrior,
                                                    new DecisionNode("HAPPINESS", ">=", 70,
                                                        quietComeback,
                                                        highGpaFallback
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            ),
                            new DecisionNode("GPA", "==", 4.0,
                                deanOrPerfectCheck.yesChild,
                                new DecisionNode("GPA", ">=", 3.85,
                                    new DecisionNode("SOCIAL", "<=", 40,
                                        new DecisionNode("HAPPINESS", ">=", 60,
                                            researchGoblin,
                                            officeHourHero
                                        ),
                                        officeHourHero
                                    ),
                                    new DecisionNode("GPA", ">=", 3.80,
                                        new DecisionNode("HAPPINESS", ">=", 80,
                                            starryScholar,
                                            new DecisionNode("HAPPINESS", ">=", 65,
                                                new DecisionNode("HEALTH", ">=", 65,
                                                    new DecisionNode("SOCIAL", ">=", 65,
                                                        balancedMonarch,
                                                        socialButterflyLab
                                                    ),
                                                    socialButterflyLab
                                                ),
                                                socialButterflyLab
                                            )
                                        ),
                                        new DecisionNode("HEALTH", ">=", 95,
                                            wellnessWarrior,
                                            new DecisionNode("HAPPINESS", ">=", 70,
                                                quietComeback,
                                                highGpaFallback
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                new DecisionNode("SOCIAL", "<=", 10,
                    campusGhost,
                    new DecisionNode("GPA", "==", 4.0,
                        deanOrPerfectCheck.yesChild,
                        new DecisionNode("GPA", ">=", 3.85,
                            new DecisionNode("SOCIAL", "<=", 40,
                                new DecisionNode("HAPPINESS", ">=", 60,
                                    researchGoblin,
                                    officeHourHero
                                ),
                                officeHourHero
                            ),
                            new DecisionNode("GPA", ">=", 3.80,
                                new DecisionNode("HAPPINESS", ">=", 80,
                                    starryScholar,
                                    new DecisionNode("HAPPINESS", ">=", 65,
                                        new DecisionNode("HEALTH", ">=", 65,
                                            new DecisionNode("SOCIAL", ">=", 65,
                                                balancedMonarch,
                                                socialButterflyLab
                                            ),
                                            socialButterflyLab
                                        ),
                                        socialButterflyLab
                                    )
                                ),
                                new DecisionNode("HEALTH", ">=", 95,
                                    wellnessWarrior,
                                    new DecisionNode("HAPPINESS", ">=", 70,
                                        quietComeback,
                                        highGpaFallback
                                    )
                                )
                            )
                        )
                    )
                )
            );
    
    
        Node midHighGpaSubtree =
            new DecisionNode("SOCIAL", "<=", 20,
                new DecisionNode("HAPPINESS", "<=", 50,
                    invisibleGraduate,
    
                    new DecisionNode("GPA", ">=", 3.70,
                        new DecisionNode("HAPPINESS", ">=", 65,
                            new DecisionNode("HEALTH", ">=", 65,
                                new DecisionNode("SOCIAL", ">=", 65,
                                    balancedMonarch,
                                    starryScholar
                                ),
                                starryScholar
                            ),
                            starryScholar
                        ),
      
                        new DecisionNode("HAPPINESS", ">=", 70,
                            new DecisionNode("HEALTH", ">=", 65,
                                quietComeback,
                                wellnessWarrior
                            ),
                            permaProcrastinator
                        )
                    )
                ),
     
                new DecisionNode("SOCIAL", ">=", 95,
                    socialButterflyLab,
                    new DecisionNode("GPA", ">=", 3.70,
                        new DecisionNode("HAPPINESS", ">=", 65,
                            new DecisionNode("HEALTH", ">=", 65,
                                new DecisionNode("SOCIAL", ">=", 65,
                                    balancedMonarch,
                                    starryScholar
                                ),
                                starryScholar
                            ),
                            new DecisionNode("HEALTH", ">=", 95,
                                wellnessWarrior,
                                quietComeback
                            )
                        ),
                        new DecisionNode("HEALTH", ">=", 95,
                            wellnessWarrior,
                            new DecisionNode("SOCIAL", ">=", 80,
                                chronicOvercommitter,
                                permaProcrastinator
                            )
                        )
                    )
                )
            );
    
        Node midGpaSubtree =
            new DecisionNode("GPA", "<=", 3.20,
                new DecisionNode("HEALTH", "<=", 50,
                    new DecisionNode("SOCIAL", ">=", 90,
                        new DecisionNode("HAPPINESS", ">=", 90,
                            chaosButCharming,
                            chronicOvercommitter
                        ),
                        chronicOvercommitter
                    ),
                    null
                ),
                null
            );
    
      
        midGpaSubtree =
            new DecisionNode("GPA", "<=", 3.20,
     
                new DecisionNode("HEALTH", "<=", 50,
                    new DecisionNode("SOCIAL", ">=", 90,
                        new DecisionNode("HAPPINESS", ">=", 90,
                            chaosButCharming,
                            chronicOvercommitter
                        ),
                        chronicOvercommitter
                    ),
    
                    new DecisionNode("SOCIAL", ">=", 65,
                        new DecisionNode("SOCIAL", "<=", 75,
                            new DecisionNode("HAPPINESS", ">=", 60,
                                socialReset,
                                permaProcrastinator
                            ),
                            permaProcrastinator
                        ),
                        permaProcrastinator
                    )
                ),
    
                new DecisionNode("HAPPINESS", ">=", 85,
                    new DecisionNode("SOCIAL", ">=", 85,
                        luluPhilosopher,
                        new DecisionNode("HEALTH", ">=", 70,
                            botanicalRecharge,
                            permaProcrastinator
                        )
                    ),
                    new DecisionNode("HEALTH", ">=", 70,
                        new DecisionNode("HAPPINESS", ">=", 70,
                            botanicalRecharge,
                            permaProcrastinator
                        ),
                        permaProcrastinator
                    )
                )
            );
    
        Node lowerMidGpaSubtree =
            new DecisionNode("HEALTH", "<=", 50,
                new DecisionNode("SOCIAL", ">=", 90,
                    new DecisionNode("HAPPINESS", ">=", 90,
                        chaosButCharming,
                        chronicOvercommitter
                    ),
                    chronicOvercommitter
                ),
                new DecisionNode("SOCIAL", ">=", 65,
                    new DecisionNode("SOCIAL", "<=", 75,
                        new DecisionNode("HAPPINESS", ">=", 60,
                            socialReset,
                            permaProcrastinator
                        ),
                        new DecisionNode("HEALTH", ">=", 70,
                            botanicalRecharge,
                            permaProcrastinator
                        )
                    ),
                    new DecisionNode("HEALTH", ">=", 70,
                        botanicalRecharge,
                        permaProcrastinator
                    )
                )
            );
    
    
    
        Node lowGpaSubtree =
            new DecisionNode("SOCIAL", "<=", 20,
                new DecisionNode("HAPPINESS", "<=", 50,
                    invisibleGraduate,
                    permaProcrastinator
                ),
                new DecisionNode("SOCIAL", ">=", 80,
                    chronicOvercommitter,
                    permaProcrastinator
                )
            );
    
     
        Node gpaBandTree =
            new DecisionNode("GPA", ">=", 3.9,
                highGpaSubtree,
                new DecisionNode("GPA", ">=", 3.5,
                    midHighGpaSubtree,
                    new DecisionNode("GPA", ">=", 3.0,
                        midGpaSubtree,
                        new DecisionNode("GPA", ">=", 2.8,
                            lowerMidGpaSubtree,
                            lowGpaSubtree
                        )
                    )
                )
            );
    
    
    
        Node vibesCheck =
            new DecisionNode("GPA", ">=", 3.0,
                new DecisionNode("GPA", "<=", 3.10,
                    new DecisionNode("HAPPINESS", "==", 100,
                        noThoughts,
                        gpaBandTree
                    ),
                    gpaBandTree
                ),
                gpaBandTree
            );
    
    
        root =
            new DecisionNode("HEALTH", "==", 0,
                healthBarZero,
                vibesCheck
            );
    
        
    }
    public Node getRoot(){
        return root;
    }

    public static void main(String[] args){
        DecisionTree tree = new DecisionTree();
        Node root = tree.getRoot();
        System.out.println(root.getAchievement(new PlayerStat()));
    }
}