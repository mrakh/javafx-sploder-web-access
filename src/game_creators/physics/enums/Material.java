package game_creators.physics.enums;

import org.apache.commons.lang3.text.WordUtils;

public enum Material {
	TIRE("12"),
	GLASS("13"),
	RUBBER("14"),
	ICE("15"),
	STEEL("16"),
	WOOD("17"),
	AIR_BALLOON("18"),
	HELIUM_BALLOON("19"),
	MAGNET("20"),
	SUPERBALL("51");
	
	private String no;
	
	private Material(String no) {
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