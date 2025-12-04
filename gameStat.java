/**
* A class to store the current game stats, including the framework of keeping track of the time throughout the game
* @author (Melody Lyu)
* @date (Dec.4, 2025)
**/
public int year;
public int month;
public int currentEnergy;
private final int totalEnergy = 3;
pStats = playerStat();

/**
* Constructor for a new game
*/
public gameStat(){
  this.year = 1;
  this.month = 1;
  this.currentEnergy = 3;
}

/**
* check if there is more energy to perform action in this month
* @return boolean, true if energy is used up and should increment to the next month
**/
private boolean isEnergyZero(){
  return (currentEnergy == 0);
}

/**
* Helper method to check if the energy of the month is used up
* if the energy is used up then increment to the next month
**/
private void newMonth(){
  if (isEnergyZero()){
    month++;
  }
}

/**
* Helper method to check if there has been more than 8 month in the academic year, increment to next year
**/
private void newYear(){
  if (this.month > 8){
    year++;
    month = 1;
  }
}

/** 
* constantly checking if the game reaches the end
* @return boolean, true if the game should end
**/
public boolean isEnding(){
  boolean end? = false;
  if (year > 4 || !pStats.isValidStat()){
    end? = true;
  }
}
