package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Effect {
	NONE, SNOW, RAIN, CLOUDS, STARS, SILK, LEAFY, SMOKE, GRID;
	
	@Override
	public String toString() {
		return WordUtils.capitalizeFully(super.toString());
	}
}
