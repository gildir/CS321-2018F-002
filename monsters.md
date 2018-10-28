# Users

## Ghosts

## Ghouls

The purpose of Ghouls to provide dynamic events and interactions to the world. Ghouls are a type
of NPC (non-playable character) that any player can interact with. At the start of the game, several ghouls will spawn into the world, "wandering around"
different rooms in given time intervals.

The player can interact with the ghoul with the "pokeGhoul" command. This allows the player to poke the Ghoul, causing the Ghoul's aggravation to rise. The player and the ghoul must be in the same room for this interaction to happen. The ghoul keeps track of the player's current aggravation until the player leaves the server. If the player pokes the ghoul too many times, the ghoul will "drag" the player back to the the starting room, removing a random item from the player's inventory.

The player can also give an item to the ghoul with the "giftGhoul" command. This causes the Ghoul's current anger with the player to decrease. The player must be in the same room with the ghoul and have at least one item in their inventory to perform this command.


## Spirits

The purpose of Spirits is to search for them throughout the map, and once found, the Spirits can be captured. They are a collectible for the world to give the player more activities to do. There are twenty unique Spirit types for the player to collect. Different Spirit types spawn randomly throughout the world and move around, occasionally shifting in and out of reality. The Spirits are supposed to be a hard collectible to catch, so it becomes a challenge capturing them all.

The player may capture the Spirits, using the “capture” command. The player has a checklist of Spirits. If a new Spirit type is captured, and the type is not crossed off on the list, the list will automatically update when the new Spirit type is captured.

The Spirit checklist may be displayed with the command, “spirit log”. Any type already captured will end each line with a “-C”.

# Developers

## Ghouls

## Ghoul Aggro System

Ghouls are phantom like beings that roam around in the game. The ghouls class in Java extends the Non-Playable Character (NPC) class, this means that the ghouls class inherits all the variables, constructors, and methods from the NPC class since ghouls are a type of a NPC.

Ghouls have an aggression system set in the ghoul object. The ghouls anger level can increase, maximum anger level is five and decrease, minimum anger level is zero, based on a player’s actions towards the ghoul. If a player pokes a ghoul, then the ghoul’s anger level increases; and if a player gifts an item to a ghoul, then the item is transferred out of their inventory (as if it was going to the ghoul) and the ghoul’s anger level decreases. Keep in mind, a player must be in the same room as the ghoul to poke or gift an item to the ghoul, the player also must have at least one object to gift the ghoul or else the player does not have anything to gift. There are already methods set in place for the ghoul’s class’s aggression system.


## NPC System

