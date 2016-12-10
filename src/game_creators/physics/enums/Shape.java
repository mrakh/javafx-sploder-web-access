package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Shape {
	NONE("0"),
	POLYGON("1"),
	HEXAGON("2"),
	PENTAGON("3"),
	RECTANGLE("4"),
	RAMP("5"),
	CIRCLE("6"),
	SQUARE("7");
	
	private String no;
	
	private Shape(String no) {
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