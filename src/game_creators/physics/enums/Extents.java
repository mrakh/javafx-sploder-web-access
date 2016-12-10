package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Extents {
	ENCLOSED("0"), GROUND("1"), OPEN("2");
	private String str;
	
	private Extents(String str) {
		this.str = str;
	}
	
	public String str() {
		return str;
	}
	
	@Override
	public String toString() {
		return WordUtils.capitalizeFully(super.toString());
	}
}
