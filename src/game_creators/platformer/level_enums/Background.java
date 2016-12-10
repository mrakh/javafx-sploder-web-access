package game_creators.platformer.level_enums;

public enum Background {
	DEFAULT("5"),
	BACKGROUND_1("1"),
	BACKGROUND_2("2"),
	BACKGROUND_3("3"),
	BACKGROUND_4("4"),
	BACKGROUND_5("5"),
	BACKGROUND_6("6"),
	BACKGROUND_7("7"),
	BACKGROUND_8("8"),
	BACKGROUND_9("9");
	
	private String num;
	
	private Background(String num) {
		this.num = num;
	}
	
	public String get() {
		return num;
	}
}