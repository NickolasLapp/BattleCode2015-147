package team147.util;

import team147.BaseRobot;

public abstract class StateMachine {
	protected BaseRobot br;

	public StateMachine(BaseRobot br) {
		this.br = br;
	}

	public enum State {
		/** Attack least healthy enemy in weapon range */
		ATTACK,
		/** Defend our HQ/Towers */
		DEFEND,
		/** Building structures */
		ECON,
		/** Move around the map to new locations */
		EXPLORE,
		/** Retreat from enemies in vicinity*/
		PANIC,
		/** Have all robots gather at a specified point */
		RALLY
	}

	public abstract void updateState();

	public abstract void sendStateMessages();
}
