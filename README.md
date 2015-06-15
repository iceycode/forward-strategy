Forward Strategy
==

This will update after major changes or fixes pushed to branch1 & merged with master.
 
The TODO list is [here](TODO.md).

Info on game Artificial Intelligence component is [here](FS_AI.md).

####*Change Log*:####
--

*06-14-2015*

- Everything is working like it was before recent change to code structure. Still a ton of bugs to take care of.

*06-11-2015*

- updated libgdx version from 1.5.0 to 1.6.2 

*05-27-2015*

- some major changes to project code structure and some of the game design
- created a UML for Unit-State behaviors during player turn cycle
- GAME MAP CHANGES:
	- larger grid created to test out how scrollable larger map & minimap could enhance game play
	- Panels now use a "state" enum for changing texture, similar to how Units change animations
	- Got rid of MapActor & TiledMapRenderer, all terrain tile texture are put into Panels & it does drawing
	- Terrain textures moveable panels are animated!
- UNIT CONTROL:
	- changed entire structure of Unit control by using a UnitController singleton which takes data
	directly from Panel & Pathfinder and sends to Unit. Unit Actor only listens for states now. 
	All the actions are handled by UnitController now! This makes updating any of the game mechanics 
	much easier in the future. 
	- Everything Unit does is in UnitState. See source code for details.


*05-13-2015*

- GAME MECHANICS CHANGE: instead of automatic attack, players need to click on Unit to attack it
	- also, moves can be made after or before an attack occurs 
- MORE CHANGES TO COME:
	- tiles with special abilities on them 
		1. increase range of unit
		2. heals a unit
		3. increases unit's damage (to all units)
		4. tile teleports unit (that fits within it)
		5. kill zones and natural disasters 
			- unit that steps on a spot, will die 
			- at certain time, something will occur on board at spot constantly
			(this last special ability would come in more advanced maps)


*05-06-2015*

- Began work on the game AI using gdx-ai StateMachine interface. See [AI markdown](FS_AI.md) for details.

*1-13-14*

- began work on the game AI for single player mode
- see todo for more info

*12-30-14*

- first working jar in build/lib/jar created (see configuration for packaging details)

*12-14-14*

- got online multiplayer movements working
- created a [TODO](TODO.md) markdown file

*11-07-14*

- fixed unit health/attack sequence
- started working on the pause menu
- cleaned up & organized some code
- sorted & began adding some missing animations

*11-02-14*

- created a music track & added to LevelScreen
- method for testing 2 units only added to TestUtils
- added a new TiledMap for testing
- cleaned up unit attack behavior a bit 

*10-29-14*

- made a 16x12 board to change game mechanics
- fixed path finding methods, still a bit buggy
- unit movement work decently


--
<small>
#####*About this project*
Currently a work in progress & mainly for self-learning purposes. Code is not very pretty at the moment, game runs but doesn't properly work only on desktop, and there still exist a lot of deprecated classes. I only recently started working on it after taking a small hiatus to pursue other unrelated projects. But I'm back and on top of it now, so more updates will come soon. </small>
##Configuration Info
####*Deploying Desktop*
- DESKTOP:
	- need to add all jars in core under libs (including libs/lib) to desktop/lib as libararies
- make sure to add the depenencies to main project build.gradle

####*Configuration*
- Current project is setup in IntelliJ 14 but also can be built in properly setup eclipse environment
- For github tasks in Eclipse, egit is used
- see libgdx wiki on github for more detailed info on setup

####*Dependencies & Database config*
- libgdx engine, gradle, Android SDK, RoboVM
- JSON and XML for storing game data & assets
- AppWarp (not currently using, but soon will be integrated)

####Deployment, Testing **
- use JUnit4 tests and gdx-tests within libgdx library 
- varies based on platform 
- currently desktop is working

---
##Game Info
###*Summary*

As in the military concept of forward strategy, this game is all about
charging the enemy quickly but with strategic placement of units. 
So, the environment is fast-paced Players choose a faction and always engage
units that they encounter (or attack). 

###*Story*
This sci-fi RTS is a fast paced game that takes place on a planet that is
being fought over by 4 factions: humans, arthroids, reptoids & the mysterious light
beings. There are many rumors as to why this planet is being fought over - resources,
territory expansions, strategy outpost, etc.

###*Basic Gameplay*
Here are the game specs and rules. More will be coming (ie, minigames when units encounter each other)

- RTS turn-based game
- occurs on a 12x12 board 
- players choose factions, units & a map
	- 3 classes of units: small, medium & large
	- 2 types of units: land & air
- Unit properties: 
	- move distance (max moves is 5)
	- all units have health of 4
	- certain units have advantages over others in terms of damage (ie, a small unit can kill a medium unit)
	- going over certain obstacles
	- only being able to attack certain units

For in-game play

- units placed on left & right sides of board
- each player has 45 seconds to move all their units (this is set to 30 seconds currently for testing purposes, though that value is not set in stone atm)
- can only move forward
- once enemy units are next to each other, they attack once each turn and are locked in combat till one dies
- if a player's unit can get past the enemies side of the board, they return back to their side & another unit is added to that player's side of the board
- the game ends when a player has defeated all the units of another player

Future additions:
- special abilities for units or player
- minigames for certain unit encounters
- map tiles that, when reached by a unit, contain power-ups, such as damage-boost 



#####*That is all for now! More to come...*


---
####Contributions

- My cousin, David, for creating all the animations used so far & helping with the game design & mechanics. 

- Also, all the online tutorials and free tools I have used so far; many available through <https://github.com/libgdx/libgdx/wiki/External-tutorials>

####Contact Info
Contact me at <thirdeyemind@gmail.com> via email. 