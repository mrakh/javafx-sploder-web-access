package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Strength {
	PERM("21"),
	STRONG("22"),
	MEDIUM("23"),
	WEAK("24");
	
	private String no;
	
	private Strength(String no) {
		this.no = no;
	}
	
	public String num() {
		return no;
	}
	
	@Override
	public String toString() {
		return WordUtils.capitalizeFully(super.toString());
	}
}