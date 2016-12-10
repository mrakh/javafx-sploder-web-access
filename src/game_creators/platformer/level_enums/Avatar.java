package game_creators.platformer.level_enums;

public enum Avatar {
	DEFAULT("0"),
	AVATAR_1("1"),
	AVATAR_2("2"),
	AVATAR_3("3"),
	AVATAR_4("4"),
	AVATAR_5("5"),
	AVATAR_6("6"),
	AVATAR_7("7"),
	AVATAR_8("8"),
	AVATAR_9("9");
	
	private String num;
	
	private Avatar(String num) {
		this.num = num;
	}
	
	public String get() {
		return num;
	}
}