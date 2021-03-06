package team147;

import team147.buildings.Aerospacelab;
import team147.buildings.Barracks;
import team147.buildings.Handwashstation;
import team147.buildings.Helipad;
import team147.buildings.Hq;
import team147.buildings.Minerfactory;
import team147.buildings.Supplydepot;
import team147.buildings.Tankfactory;
import team147.buildings.Technologyinstitute;
import team147.buildings.Tower;
import team147.buildings.Trainingfield;
import team147.units.Basher;
import team147.units.Beaver;
import team147.units.Commander;
import team147.units.Computer;
import team147.units.Drone;
import team147.units.Launcher;
import team147.units.Miner;
import team147.units.Missile;
import team147.units.Soldier;
import team147.units.Tank;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		try {
			switch (rc.getType()) {
			case AEROSPACELAB:
				new Aerospacelab(rc);
				break;
			case BARRACKS:
				new Barracks(rc);
				break;
			case BASHER:
				new Basher(rc);
				break;
			case BEAVER:
				new Beaver(rc);
				break;
			case COMMANDER:
				new Commander(rc);
				break;
			case COMPUTER:
				new Computer(rc);
				break;
			case DRONE:
				new Drone(rc);
				break;
			case HANDWASHSTATION:
				new Handwashstation(rc);
				break;
			case HELIPAD:
				new Helipad(rc);
				break;
			case HQ:
				new Hq(rc);
				break;
			case LAUNCHER:
				new Launcher(rc);
				break;
			case MINER:
				new Miner(rc);
				break;
			case MINERFACTORY:
				new Minerfactory(rc);
				break;
			case MISSILE:
				new Missile(rc);
				break;
			case SOLDIER:
				new Soldier(rc);
				break;
			case SUPPLYDEPOT:
				new Supplydepot(rc);
				break;
			case TANK:
				new Tank(rc);
				break;
			case TANKFACTORY:
				new Tankfactory(rc);
				break;
			case TECHNOLOGYINSTITUTE:
				new Technologyinstitute(rc);
				break;
			case TOWER:
				new Tower(rc);
				break;
			case TRAININGFIELD:
				new Trainingfield(rc);
				break;
			default:
				break;
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
}