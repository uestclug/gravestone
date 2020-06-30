# Gravestone mod

[README](README.md) | [中文文档](README_zh.md)

A server side gravestone mod for fabric.

Support minecraft 1.16.x.

CurseForge: https://www.curseforge.com/minecraft/mc-mods/gravestone


## Description

After the player dies, a gravestone with the skin of the player will be placed.

![player_dead](./doc/player_dead.gif)

Play Skin:

![skin](./doc/skin.png)

The shape of gravestone:

![skin](./doc/gravestone.png)

Player's items will be saved in the gravestone.Items with "Curse of Vanishing" will disappear after the player dies.

The xp of player will be divided by 2 and placed in the tombstone.

After the gravestone is broke, the items and xp in the gravestone will drop to the world.

![player_dead](./doc/break_gravestone.gif)


## Install

Only need copy gravestone mod to mods dir.

When the mod is installed on the server, the client can use it without installing this mod.

The vanilla client and the fabric client without this mod can directly connect to the server which installed this mod.


## Thanks

yet-another-gravestone-mod: https://github.com/Nuclearfarts/yet-another-gravestone-mod

ServerAdditionsUtil: https://github.com/ExtraCrafTX/ServerAdditionsUtil