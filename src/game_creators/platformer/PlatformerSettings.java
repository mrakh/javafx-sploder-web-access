package game_creators.platformer;

import game_creators.GameSettings;

import static game_creators.Flag.*;
import static game_creators.platformer.PlatFlag.*;

public class PlatformerSettings extends GameSettings { 
	
	private boolean uniformLighting = false;
	private boolean eightBit = false;
	private boolean containsGraphics = false;
	
	public PlatformerSettings(String title, String author, int flags) {
		super(title, author, (flags & COMMENTS) == COMMENTS, (flags & PRIVATE) == PRIVATE);
		
		uniformLighting =	(flags & FAST) == FAST;
		eightBit =			(flags & BITVIEW) == BITVIEW;
		containsGraphics =	(flags & GRAPHICS) == GRAPHICS;
	}

	public void set(boolean uniformLighting, boolean eightBit, boolean containsGraphics) {
		this.uniformLighting = uniformLighting;
		this.eightBit = eightBit;
		this.containsGraphics = containsGraphics;
	}
	
	public boolean isUniformLighting() {
		return uniformLighting;
	}
	
	public boolean is8Bit() {
		return eightBit;
	}
	
	public boolean containsGraphics() {
		return containsGraphics;
	}
}
