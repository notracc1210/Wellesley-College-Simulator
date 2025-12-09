/**
* A class to store the current game stats, including the framework of keeping track of the time throughout the game
* @author (Melody Lyu)
* @date (Dec.4, 2025)
**/

public class gameStat{

    public int year; //1-4
    public int month; //1-8
    public int currentEnergy; //0-3
    private final int totalEnergy = 3;
    private PlayerStat pStats = new PlayerStat();
    private int consecutiveLowSocialMonths = 0; // Track months with social < 10
    
    /**
    * Constructor for a new game
    */
    public gameStat(){
      this.year = 1;
      this.month = 1;
      this.currentEnergy = 3;
    }
    
    /**
    * Deduct one energy when player performs an action
    * Check if the energy is zero, then advance month
    **/
    public void useEnergy(){
      if (currentEnergy > 0){
        currentEnergy--;
      }
      if (isEnergyZero()){
        advanceMonth();
      }
    }
    
    /**
    * check if there is more energy to perform action in this month
    * @return boolean, true if energy is used up and should increment to the next month
    **/
    private boolean isEnergyZero(){
      return (currentEnergy == 0);
    }
    
    /**
    * Reset energy at the start of each new month
    **/
    private void resetEnergy(){
      currentEnergy = totalEnergy;
    }
    
    /**
    * If the energy is used up then increment to the next month
    * check if the all month in the academic year has passed, then increment year
    **/
    private void advanceMonth(){
      // Check for social isolation before advancing
      checkSocialIsolation();
      
      month++;
      resetEnergy();
      if (month > 8){
        advanceYear();
      }
    }
    
    /**
    * Check if social connection has been below 10 for 3+ months
    * If so, apply isolation penalty (large happiness decrease)
    **/
    private void checkSocialIsolation(){
      if (pStats.getSocial() < 10) {
        consecutiveLowSocialMonths++;
        if (consecutiveLowSocialMonths >= 3) {
          // Apply isolation penalty: large happiness decrease
          // We'll do this by creating a temporary consequence
          // Actually, we need to modify happiness directly
          // Let's add a method to PlayerStat for this
          pStats.applyIsolationPenalty();
        }
      } else {
        consecutiveLowSocialMonths = 0; // Reset counter if social recovers
      }
    }
    
    /**
    * Get the number of consecutive months with social < 10
    **/
    public int getConsecutiveLowSocialMonths() {
      return consecutiveLowSocialMonths;
    }
    
    /**
    * Helper method to increment to next academic year and reset month count
    **/
    private void advanceYear(){
      year++;
      month = 1;
    }
    
    /** 
    * constantly checking if the game reaches the end
    * @return boolean, true if the game should end
    **/
    public boolean isEnding(){
      return (year > 4) || !pStats.isValidStat();
    }
    
    /**
     * Returns the season or finals period based on the game's month.
     * Month mapping:
     * 1 = First Month of the Fall Semester, 2 = Second Month of the Fall Semester, 3 = Third Month of the Fall Semester, 4 = Fourth Month of the Fall Semester
     * 5 = First Month of the Spring Semester, 6 = Second Month of the Spring Semester, 7 = Third Month of the Spring Semester, 8 = Fourth Month of the Spring Semester
     * @return String, text representation of the month
     */
    public String getSeason() {
        // Finals months: Fourth Month of the Fall Semester (4) and Fourth Month of the Spring Semester (8)
        if (month == 4 || month == 8) {
            return "Finals";
        }
        // Winter months: First Month of the Fall Semester (1) and Second Month of the Fall Semester (2)
        else if (month == 1 || month == 2) {
            return "Fall";
        }
        // Spring month: Third Month of the Fall Semester (3)
        else if (month == 3) {
            return "Winter";
        }
        // Fall months: First Month of the Spring Semester (5), Second Month of the Spring Semester (6), Third Month of the Spring Semester (7)
        else if (month == 5 || month == 6 || month == 7) {
            return "Spring";
        }
        else {
            return "Unknown";
        }
    }
    
    /**
     * Converts year number (1-4) to year label string.
     * @return String representing the academic year (Freshman, Sophomore, Junior, Senior)
     */
    public String getYearLabel() {
        switch (year) {
            case 1: return "Freshman";
            case 2: return "Sophomore";
            case 3: return "Junior";
            case 4: return "Senior";
            default: return "Unknown";
        }
    }
    
    /**
     * Calculates and returns the day number within the academic career.
     * Each month has 3 energy points = 3 days, so day = (year-1)*24 + (month-1)*3 + (3-currentEnergy)
     * @return int representing the day number
     */
    public int getDayNumber() {
        int daysFromYears = (year - 1) * 24; // 8 months * 3 days per month
        int daysFromMonths = (month - 1) * 3; // 3 days per month
        int daysInCurrentMonth = 3 - currentEnergy; // Days already passed this month
        return daysFromYears + daysFromMonths + daysInCurrentMonth + 1; // +1 for 1-indexed
    }
    
    /**
     * Formats the day/year display for the header.
     * @return String in format "Day X of [YearLabel]"
     */
    public String getDayLabel() {
        return "Day " + getDayNumber() + " of " + getYearLabel();
    }
    
    /**
    * Getters for the instance variables
    **/
    public int getYear() {return year;}
    public int getMonth() {return month;}
    public int getCurrentEnergy() {return currentEnergy;}
    public PlayerStat getPlayerStats() {return pStats;}
}
