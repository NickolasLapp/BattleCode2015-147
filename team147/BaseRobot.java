package team147;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public abstract class BaseRobot {
	public RobotController rc;
	public Messenger messaging;
	public Random rand;
	public Direction currentDirection;
	public MapLocation enemyHQLoc;

	public BaseRobot(RobotController rc) {
		this.rc = rc;
		rand = new Random(rc.getID());
	}

	public void attackEnemyTowerZero() throws GameActionException {
		MapLocation enemyTowers[] = rc.senseEnemyTowerLocations();
		if (rc.getLocation().distanceSquaredTo(enemyTowers[0]) <= rc.getType().attackRadiusSquared) {
			rc.attackLocation(enemyTowers[0]);
		} else {
			moveTowardDestination(enemyTowers[0]);
		}
	}

	public MapLocation getAndSetRallyPoint(MapLocation attackLocation)
			throws GameActionException {
		MapLocation ourHQ = rc.senseHQLocation();

		MapLocation rallyPoint = ourHQ.add(ourHQ.directionTo(attackLocation),
				(int) (Math.sqrt(ourHQ.distanceSquaredTo(attackLocation)) / 4));

		messaging.setRallyPoint1x(rallyPoint.x);
		messaging.setRallyPoint1y(rallyPoint.y);
		return rallyPoint;
	} // end of getRallyPoint method

	public Direction randomDirection() {
		return Direction.values()[rand.nextInt(8)];
	} // end of randomDirection method

	public void moveAround() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = currentDirection.rotateRight();
					if (rc.canMove(currentDirection)) {
						rc.move(currentDirection);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				moveAround();
			}
		}
	} // end of moveAround method

	public void followEconUnit() throws GameActionException {
		RobotInfo allies[] = rc.senseNearbyRobots(
				rc.getType().attackRadiusSquared, rc.getTeam());
		RobotInfo allyToFollow = null;

		for (RobotInfo ally : allies) {
			if (ally.type == RobotType.MINER || ally.type == RobotType.BEAVER) {
				allyToFollow = ally;
				break;
			}
		}

		if (allyToFollow != null)
			safeMoveTowardDestination(allyToFollow.location.add(
					allyToFollow.location.directionTo(getEnemyHQLoc()),
					(int) ((double) rc.getType().attackRadiusSquared / 2 * rand
							.nextDouble())));
	}

	public void safeMoveAround() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection) && directionSafeFromTowers()) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = currentDirection.rotateRight();
					if (rc.canMove(currentDirection)
							&& directionSafeFromTowers()) {
						rc.move(currentDirection);
					} else {
						currentDirection = currentDirection.rotateRight();
						safeMoveAround();
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				safeMoveAround();
			}
		}
	}

	public RobotType getNeededBuilding() throws GameActionException {
		if (messaging.getNumMinerfactoriesSpawned() < 2)
			return RobotType.MINERFACTORY;

		else if (messaging.getNumHelipadsSpawned() < 4)
			return RobotType.HELIPAD;

		else if (messaging.getNumSupplydepotsSpawned() < Clock.getRoundNum() / 100)
			return RobotType.SUPPLYDEPOT;

		else
			return RobotType.AEROSPACELAB;
	}

	public boolean directionSafeFromTowers() {
		MapLocation target = rc.getLocation().add(currentDirection);
		MapLocation towerLocs[] = rc.senseEnemyTowerLocations();

		for (MapLocation towerLoc : towerLocs) {
			if (target.distanceSquaredTo(towerLoc) <= RobotType.TOWER.attackRadiusSquared)
				return false;
		}
		return true;
	}

	public boolean directionSafeFromTowers(Direction facing) {
		MapLocation target = rc.getLocation().add(facing);
		MapLocation towerLocs[] = rc.senseEnemyTowerLocations();

		for (MapLocation towerLoc : towerLocs) {
			if (target.distanceSquaredTo(towerLoc) <= RobotType.TOWER.attackRadiusSquared)
				return false;
		}
		return true;
	}

	public boolean directionSafeFromHQ() {
		MapLocation target = rc.getLocation().add(currentDirection);
		MapLocation hqLoc = getEnemyHQLoc();
		if (target.distanceSquaredTo(hqLoc) <= RobotType.HQ.attackRadiusSquared)
			return false;
		return true;
	}

	public boolean directionSafeFromHQ(Direction d) {
		MapLocation target = rc.getLocation().add(d);
		MapLocation hqLoc = getEnemyHQLoc();
		if (target.distanceSquaredTo(hqLoc) <= RobotType.HQ.attackRadiusSquared)
			return false;
		return true;
	}

	public void moveTowardsHQ() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10) {
					rc.move(currentDirection);
				} else {
					currentDirection = rc.getLocation().directionTo(
							getEnemyHQLoc());
					if (rc.canMove(currentDirection)) {
						rc.move(currentDirection);
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
			}
		}
	} // end of moveTowardsHQ method

	public MapLocation getEnemyHQLoc() {
		if (enemyHQLoc == null)
			enemyHQLoc = rc.senseEnemyHQLocation();
		return enemyHQLoc;
	}

	public void safeMoveTowardsHQ() throws GameActionException {
		if (rc.isCoreReady()) {
			if (rc.canMove(currentDirection)) {
				if (rand.nextInt(100) > 10 && directionSafeFromTowers()
						&& directionSafeFromHQ()) {
					rc.move(currentDirection);
				} else {
					currentDirection = rc.getLocation().directionTo(
							getEnemyHQLoc());
					if (rc.canMove(currentDirection)
							&& directionSafeFromTowers()) {
						rc.move(currentDirection);
					} else {
						currentDirection = currentDirection.rotateRight();
						// safeMoveTowardsHQ();
					}
				}
			} else {
				currentDirection = currentDirection.rotateRight();
				// safeMoveTowardsHQ();
			}
		}
	} // end of moveTowardsHQ method

	// we probably shouldn't use this method anymore since it makes more sense
	// to attack the least healthy enemy
	public void attackEnemyZero() throws GameActionException {
		if (rc.isWeaponReady()) {
			RobotInfo[] enemies = rc.senseNearbyRobots(
					rc.getType().attackRadiusSquared, rc.getTeam().opponent());
			if (1 <= enemies.length) {
				rc.attackLocation(enemies[0].location);
			}
		}
	} // end of attackEnemyZero method

	public void attackLeastHealthyEnemy() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
				rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		double leastHealth = 999999;
		MapLocation loc = rc.getLocation().add(1, 1);
		for (RobotInfo robot : nearbyEnemies) {
			if (robot.health < leastHealth) {
				leastHealth = robot.health;
				loc = robot.location;
			}
		}
		if (nearbyEnemies.length >= 1) {
			if (rc.isCoreReady() && rc.isWeaponReady()) {
				rc.attackLocation(loc);
			}
		}
	} // end of attackLeastHealthyEnemy method

	public MapLocation getClosestTowerLocation() {
		MapLocation enemyTowers[] = rc.senseEnemyTowerLocations();

		int closestDistance = Integer.MAX_VALUE;
		MapLocation closestTower = null;
		for (MapLocation tower : enemyTowers) {
			int distanceSquaredTo = tower.distanceSquaredTo(rc
					.senseHQLocation());
			if (distanceSquaredTo < closestDistance) {
				closestTower = tower;
				closestDistance = distanceSquaredTo;
			}
		}

		if (closestTower != null)
			return closestTower;
		else
			return getEnemyHQLoc();
	}

	public void mine() throws GameActionException {
		int mineMax = (rc.getType() == RobotType.MINER ? GameConstants.MINER_MINE_MAX
				: GameConstants.BEAVER_MINE_MAX);

		if (rc.isCoreReady() && rc.senseOre(rc.getLocation()) > mineMax) {
			rc.mine();
		}
	} // end of mine method

	public void spawnRobot(RobotType type) throws GameActionException {
		if (rc.hasSpawnRequirements(type) && rc.isCoreReady()) {
			for (Direction d : Direction.values()) {
				if (rc.canSpawn(d, type)) {
					rc.spawn(d, type);
					break;
				}
			}
		}
	} // end of spawnRobot method

	public void build(RobotType building) throws GameActionException {
		if (rc.hasBuildRequirements(building) && rc.isCoreReady()) {
			for (int i = 0; i < 8; i++) {
				if (rc.canBuild(currentDirection, building)) {
					rc.build(currentDirection, building);
					break;
				} else
					currentDirection = currentDirection.rotateRight();
			}
		}
	} // end of build method

	public void buildSupplyDepotNearHQ() throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int distanceFromHQ = currentLoc.distanceSquaredTo(rc.senseHQLocation());
		if (Clock.getRoundNum() < 1500) {
			if (rand.nextInt(100) < 10) {
				if (distanceFromHQ < 60 && distanceFromHQ > 10) {
					build(RobotType.SUPPLYDEPOT);
				}
			}
		}
	} // end of buildSupplyDepotNearHQ method

	// checks to see how many nearby allies have zero supply
	public int checkSupplyLevels() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
				rc.getType().sensorRadiusSquared, rc.getTeam());
		int zeroSupplyCounter = 0;
		for (RobotInfo robot : nearbyAllies) {
			if (robot.supplyLevel == 0) {
				zeroSupplyCounter++;
			}
		}
		return zeroSupplyCounter;
	} // end of checkSupplyLevels method

	// transfer supply to other robots that have less supply
	public void transferSupply() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
				GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation transferDestination = null;
		if (nearbyAllies.length > 0) {
			for (RobotInfo robot : nearbyAllies) {
				if (robot.supplyLevel < lowestSupply) {
					lowestSupply = robot.supplyLevel;
					transferAmount = ((rc.getSupplyLevel() - robot.supplyLevel) / 2);
					transferDestination = robot.location;
				}
			}
		}
		if (transferDestination != null) {
			int transferDistance = transferDestination.distanceSquaredTo(rc
					.getLocation());
			if (transferDistance <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
				rc.transferSupplies((int) transferAmount, transferDestination);
			}
		}
	} // end of transferSupply method

	public void moveTowardDestination(MapLocation dest)
			throws GameActionException {
		Direction toDest = rc.getLocation().directionTo(dest);
		Direction[] directions = { toDest, toDest.rotateLeft(),
				toDest.rotateLeft().rotateLeft(), toDest.rotateRight(),
				toDest.rotateRight().rotateRight() };
		for (Direction d : directions) {
			if (rc.canMove(d) && rc.isCoreReady()) {
				rc.move(d);
				break;
			}
		}
	} // end of moveTowardDestination method

	public void safeMoveTowardDestination(MapLocation dest)
			throws GameActionException {
		if (rc.isCoreReady()) {
			Direction toDest = rc.getLocation().directionTo(dest);
			Direction[] directions = { toDest, toDest.rotateLeft(),
					toDest.rotateLeft().rotateLeft(), toDest.rotateRight(),
					toDest.rotateRight().rotateRight() };
			for (Direction d : directions) {
				if (rc.canMove(d) && directionSafeFromTowers(d)
						&& directionSafeFromHQ(d)) {
					rc.move(d);
					break;
				}
			}
		}
	} // end of moveTowardDestination method

	public abstract void defaultMove();

	public abstract void defaultAttack();

	public abstract void sendSpawnMessages();

	// this method isn't being used, but could be used for efficient direction
	// changing
	public int directionNum(Direction d) {
		switch (d) {
		case NORTH:
			return 0;
		case NORTH_WEST:
			return 1;
		case WEST:
			return 2;
		case SOUTH_WEST:
			return 3;
		case SOUTH:
			return 4;
		case SOUTH_EAST:
			return 5;
		case EAST:
			return 6;
		case NORTH_EAST:
			return 7;
		default:
			return -1;
		}
	} // end of directionNum method
}