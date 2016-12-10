package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Size {
	NORMAL("0"), DOUBLE("1"), FOLLOW("2");
	private String str;
	
	private Size(String str) {
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
