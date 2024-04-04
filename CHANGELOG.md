
# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).


## [[2.0.6](https://github.com/0ctave/SMPEssentials/releases/tag/v2.0.6)] - 2024-04-04

SMP Essentials 2.0.6 patch release that includes the following changes:

### Changed

- The radius of the lava bucket/block placement protection has been slightly increased to 4 and 2 units of distance respectively.

### Fixed

- Team members can't leave a team at War anymore.
- Fixed block breaking under a player protection, you won't be able to break the block under a player that has pvp off.
- Fixed bucket pvp protection, bucket uses don't trigger the 'in combat' status anymore on every players.


## [[2.0.5](https://github.com/0ctave/SMPEssentials/releases/tag/v2.0.5)] - 2024-03-19

SMP Essentials 2.0.5 is a patch release that includes the following changes:

### Added

- Support for [Hardcore Revival](https://github.com/TwelveIterationMods/HardcoreRevival), the kill/death count is now correctly updated when the mod is present

### Changed

- Changed wars winning conditions. Now, to end a war you have to kill $2 * (NbTeam + NbAllies)$, the goal is then different depending on the team sizes to equilibrate the wars. 

### Fixed

- Fixed war mechanics. Wars backend has been completely reworked, and now it's impossible to send invite to players/teams during a war.

## [[2.0.4](https://github.com/0ctave/SMPEssentials/releases/tag/v2.0.4)] - 2024-03-12

SMP Essentials 2.0.4 is a patch release that includes the following changes:

### Changed

- Damages that have been dealt to a player with pvp off are now logged.

### Fixed

- Fixed war mechanics. Teams at war can now attack each other and invites have been fixed. Alliance now work properly in wars, they don't have to declare war to attack the enemy of an ally.

## [Unreleased] - yyyy-mm-dd

Here we write upgrading notes for brands. It's a team effort to make them as
straightforward as possible.

### Added
- [LMR-ZZZZ]()
  Template line.

### Changed

### Fixed