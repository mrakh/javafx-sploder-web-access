package game_creators.physics;

import static game_creators.Flag.*;
import static game_creators.physics.PhysicsFlag.*;

import game_creators.GameSettings;

public class PhysicsSettings extends GameSettings {

	private boolean allowCopy = false;
	private boolean turbo = false;
	
	public PhysicsSettings(String title, String author, int flags) {
		super(title, author, (flags & COMMENTS) == COMMENTS, (flags & PRIVATE) == PRIVATE);
		pubkey = "null";
		allowCopy =		(flags & ALLOW_COPY) == ALLOW_COPY;
		turbo =			(flags & TURBO) == TURBO;
	}

	public void set(boolean allowCopy, boolean turbo) {
		this.allowCopy = allowCopy;
		this.turbo = turbo;
	}
	
	public boolean copyingAllowed() {
		return allowCopy;
	}
	
	public boolean isTurbo() {
		return turbo;
	}
}
