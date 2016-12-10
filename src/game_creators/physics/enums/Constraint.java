package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Constraint {
	STATIC("8"),
	SLIDE("9"),
	PIN("10"),
	FREE("11");
	
	private String no;
	
	private Constraint(String no) {
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