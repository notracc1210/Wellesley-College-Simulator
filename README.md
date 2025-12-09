# Wellesley College Life Simulator

## Project Name
Wellesley College Life Simulator - A text-based decision-making game simulating college life at Wellesley College

## Project Goals
This project is an interactive text-based simulation game where players navigate through their college experience at Wellesley College. Players make choices at various campus locations that affect their academic performance (GPA), happiness, health, and social connections. The game tracks progress over 4 academic years (32 months total) and determines the player's ending achievement based on their final statistics using a decision tree system.

## Version/Date
- Version: 2.0 (GUI + Console)
- Date: December 2025
- Last Updated: December 2025
- **Note**: This project now includes both a GUI version (recommended) and a console/text-based version. The GUI version provides an enhanced visual experience with interactive campus map, stat displays, and narrative panels.

## How to Start the Project

### Prerequisites
- Java Development Kit (JDK) installed
- A text file named "text for locations" containing game event data (must be in the same directory as the compiled classes)

### Running the Game (Version 1.0 - Console Only)
**Current Version**: The game is currently only playable through the command-line interface using the driver class. A GUI version is planned for future releases.

1. Compile all Java files:
   ```bash
   javac *.java
   ```

2. Run the driver class:
   ```bash
   java driver
   ```

3. Follow the on-screen prompts to make choices and play the game.

## Authors
- **Tracy**
- **Praslin**
- **Melody**

## Usage Guide

### Game Mechanics

#### Player Statistics
Players have four main statistics that are tracked throughout the game:
- **GPA** (0.0 - 4.0): Academic performance
- **Happiness** (0-100): Emotional well-being
- **Health** (0-100): Physical condition
- **Social Connection** (0-100): Social relationships

#### Time System
- The game spans **4 academic years**
- Each year has **8 months** (4 months per semester)
- Each month, players have **3 energy points** to spend on actions
- After using all energy, a mandatory end-of-day random event occurs
- Time advances automatically when energy reaches zero

#### Gameplay Flow
1. **Navigation Phase**: Choose a location to visit (costs 1 energy)
   - Available locations: Lulu, Clapp (Library), Jewett (Arts), Science Center, Founders (Liberal Arts), Tower (Dorm), Chapel/Shuttle, Club/Lake

2. **Event Phase**: Encounter a random event at the chosen location
   - Read the event description
   - Choose from available options
   - Each choice affects your statistics differently

3. **End of Day**: When energy reaches 0, a forced random event occurs before the month advances

4. **Game End**: The game ends when:
   - All 4 years are completed, OR
   - Any critical stat reaches an invalid state (happiness ≤ 0, health ≤ 0, social ≤ 0, or GPA ≤ 1.0)

#### Ending System
The game uses a **Decision Tree** to determine your ending achievement based on your final statistics. There are 20+ unique endings, including:
- Dean's List Legend
- Perfectly Imperfect 4.00
- Campus Ghost
- Wellness Warrior
- Burnout Speedrun
- Balanced Monarch
- Social Butterfly in a Lab Coat
- And many more!

### Input Format

**GUI Version:**
- Click on location nodes in the campus map to navigate
- Click on choice buttons to make decisions
- All interactions are mouse-based

**Console Version:**
- When prompted, enter a number (0, 1, 2, etc.) corresponding to your desired choice
- Invalid inputs will prompt you to try again

## Class Structure

### Core Game Classes

#### `GameManager`
- Main game controller that manages game state, events, and player progression
- Handles navigation between locations and event resolution
- Manages energy consumption and time advancement
- Provides interface for UI to display game state

#### `gameStat`
- Tracks game time (year, month) and energy system
- Manages month/year advancement when energy is depleted
- Provides season information (Fall, Winter, Spring, Finals)
- Checks if game should end

#### `PlayerStat`
- Stores and manages player statistics (GPA, Happiness, Health, Social Connection)
- Applies consequences from player choices
- Normalizes stats to stay within valid ranges
- Provides validation checks for game ending conditions

### Event System Classes

#### `hashForLocation`
- Loads game events from external text file
- Parses location-based event data
- Stores events in a HashMap organized by location
- File format: "text for locations"

#### `Context`
- Represents a game event or situation
- Contains a description and list of available options
- Used for both navigation menus and location events

#### `Option`
- Represents a choice available to the player
- Contains a description and associated consequence
- Provides getters for stat changes

#### `Consequence`
- Defines the impact of a player's choice
- Contains description text and stat modifications (happiness, health, social, GPA)

### Decision Tree System

#### `DecisionTree`
- Builds and manages the complex decision tree for ending determination
- Contains 20+ achievement nodes
- Evaluates player stats through decision nodes to determine final achievement

#### `Node` (Abstract)
- Base class for decision tree nodes
- Defines interface for achievement evaluation

#### `DecisionNode`
- Internal node in decision tree
- Contains condition (stat category, operator, threshold)
- Traverses tree based on player stat evaluation

#### `AchievementNode`
- Leaf node in decision tree
- Contains achievement name and description
- Returns final ending text when reached

### GUI Classes

#### `WellesleySimulatorGUI`
- Main JavaFX application entry point
- Initializes game components and GUI panels
- Manages application lifecycle and window setup
- Coordinates between game logic and UI components

#### `HeaderBar`
- Displays game title, current day/energy information
- Shows Wellesley "W" logo
- Updates in real-time as game progresses

#### `LeftPanel`
- Contains the interactive campus map
- Displays available locations for navigation
- Shows narrative/event descriptions
- Handles location selection and choice making

#### `RightPanel`
- Displays player statistics (GPA, Happiness, Health, Social)
- Shows available choices for current event
- Provides game control buttons (restart, etc.)
- Updates dynamically based on game state

#### `CampusMap` & `CampusMapView`
- Interactive visual representation of Wellesley campus
- Clickable location nodes for navigation
- Visual feedback for available/selected locations

#### `NarrativePanel`
- Displays event descriptions and context
- Shows narrative text in a readable format

#### `ChoicesPanel`
- Displays available choices for current event
- Interactive buttons for player decisions

#### `StudentStatsPanel`
- Visual display of player statistics
- Real-time updates as stats change

### Driver Class

#### `driver`
- Text-based interface for playing the game
- Handles user input and displays game state
- Main entry point for console version

## File Format

The game requires a text file named "text for locations" with the following format:

```
A NEW CONTEXT BEGINS
LOCATION: [Location Name]
CONTEXT: [Event Description]
OPTION: [Option Text]|[Consequence Text]|[Happiness,Health,Social,GPA]
OPTION: [Option Text]|[Consequence Text]|[Happiness,Health,Social,GPA]
END
A NEW CONTEXT BEGINS
...
```

Example:
```
A NEW CONTEXT BEGINS
LOCATION: LULU
CONTEXT: You're at Lulu and see your favorite food!
OPTION: Get the food|You feel happy!|5,0,0,0.0
OPTION: Skip it|You feel a bit sad.|-2,0,0,0.0
END
```

## Game Features
- **Multiple Campus Locations**: 8 different locations to explore
- **Interactive Campus Map**: Visual GUI with clickable location nodes
- **Dynamic Event System**: Random events based on location
- **Stat Management**: Balance academics, health, happiness, and social life
- **Real-Time Stat Display**: Visual indicators for all player statistics
- **Time Progression**: Realistic academic calendar with seasons
- **Multiple Endings**: 20+ unique achievements based on playstyle
- **Energy System**: Strategic resource management with visual energy counter
- **End-of-Day Events**: Mandatory random events add unpredictability
- **Dual Interface**: Both GUI and console versions available

## Notes
- Ensure the "text for locations" file exists in the same directory as the compiled classes
- Stats are automatically normalized to stay within valid ranges
- The game automatically ends if critical stats reach dangerous levels
- Energy resets to 3 at the start of each new month
- The game tracks 4 years × 8 months = 32 months total gameplay
- JavaFX SDK is included in the project (javafx-sdk-21/) for easy setup
- GUI version requires JavaFX modules; console version does not
- Both versions use the same game logic and event system

## Development Environment
- Language: Java
- IDE: BlueJ (based on .ctxt files)
- GUI Framework: JavaFX 21
- Project Type: GUI application with console fallback (Version 2.0)
- Build Tools: Shell scripts for compilation and execution

---

*This project was created for CS 230X at Wellesley College.*
