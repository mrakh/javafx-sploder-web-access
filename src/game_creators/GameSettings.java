package game_creators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class GameSettings {
	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu");
	
	protected String date = LocalDateTime.now().format(DATE_FORMAT);
	protected String author = null;
	protected String title = null;
	protected String id = "noid-unsaved-project";
	protected String mode = "2";
	protected String pubkey = "";
	protected boolean comments = true;
	protected boolean isPrivate = false;
	
	public GameSettings(String title, String author, boolean comments, boolean isPrivate) {
		if(title == null || author == null || title.isEmpty() || author.isEmpty())
			throw new IllegalArgumentException("Title and author must be specified.");
		
		this.title = title.replace(" ", "%20");
		this.author = author;
		this.comments = comments;
		this.isPrivate = isPrivate;
	}
	
	public void updateDate() {
		date = LocalDateTime.now().format(DATE_FORMAT);
	}
	
	public String getDate() {
		return date;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getMode() {
		return mode;
	}
	
	public String getPubkey() {
		return pubkey;
	}
	
	public boolean canComment() {
		return comments;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
}
