# wLastmanStanding
**Last Man Standing**

### **Introduction**
[Requires Citizens]

Last Man Standing is a minigame where you will have to fight others to be the last man standing.
At the beginning of the game, you can choose a kit to help you out in battle.
Arenas and kits are very configurable.

### **How to set it up?**

Setting it up is extremely simple. Make sure you have a "general world spawn" where players will teleport to after the game is over/if they leave a game. 
You can do that by typing the command: [/lms setworldspawn].

If you have done that you can go ahead and create an arena by typing [/lms createarena arenaName]. This command will create a new world, and a new configuration file. 
In this config file, you can change settings such as the required players before the game starts, the maximum amount of players a game can hold, 
game duration, etc. After the world has been created, you can build/paste in a map and lobby waiting room to your likings. 

After you've done that, you will have to set a waiting room spawn location, which is done by typing [/lms setlobby arenaName]. 
Now the lobby spawn is created, there is only one thing left to do. Setting game spawns. You can create as many as you like by typing
[/lms setspawn arenaName]. That's it! Now you can either join a specific arena by typing [/lms join arenaName],
or you can look for a random free arena by creating a game NPC by typing [/lms spawnnpc] and clicking it.

### **Commands**
`/lms createarena <arenaName>` Creates a new arena\
`/lms deletearena <arenaName>` Deletes an existing arena\
`/lms join <arenaName>` Join an arena\
`/lms leave` Leaves an arena\
`/lms spawnnpc` Spawns an NPC you can click to join a game\
`/lms setlobby <arenaName>` Sets the lobby spawn point for an arena\
`/lms setspawn <arenaName>` Sets a game spawn point for an arena\
`/lms setworldspawn` Sets the world spawn, the spawn where players go to after the game ends, or they leave\
`/lms help` Displays the help message\
`/lms admin` Displays the admin mesage 
 

