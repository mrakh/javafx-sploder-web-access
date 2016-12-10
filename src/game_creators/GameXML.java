package game_creators;

import static utils.ClientUtils.defaultPostHeaders;
import static utils.ClientUtils.generateRequest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import log_on.SploderClient;
import utils.PNGEncoder;

public abstract class GameXML {
	public static final Random RANDOM = new Random();
	protected int gameXML_startTime = 0;
	protected String gameXML_projectID = "0";
	
	public abstract HttpPost saveRequest(SploderClient client) throws Exception;
	public abstract void parseSaveResponse(CloseableHttpResponse response) throws Exception;
	public abstract HttpPost publishRequest(SploderClient client) throws Exception;
	public abstract void parsePublishResponse(CloseableHttpResponse response) throws Exception;
	public abstract GameSettings settings();
	public abstract String creatorVersion();
	
	public HttpPost thumbRequest(SploderClient client, boolean small, BufferedImage bi) throws Exception {
		int w = bi.getWidth();
		int h = bi.getHeight();
		if(gameXML_projectID.equals("0") || gameXML_projectID.isEmpty())
			throw new Exception("Project has not been saved yet.");
		if(small && (w != 80 || h != 80))
			throw new Exception("Invalid thumbnail dimension.");
		if(!small && (w != 220 || h != 220))
			throw new Exception("Invalid thumbnail dimension.");
		
		Map<String, String> headers = defaultPostHeaders(null, null, creatorVersion() + ".swf", true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PNGEncoder encoder = new PNGEncoder(baos, PNGEncoder.COLOR_MODE);
		encoder.encode(bi);
		HttpEntity payload = new ByteArrayEntity(baos.toByteArray());
		
		HttpPost post = (HttpPost) generateRequest(HttpPost.class,
				"php/savethumb.php?PHPSESSID=" + client.getPHPSessionID()
				+ "&projid=" + gameXML_projectID
				+ "&size=" + (small ? "small" : "big")
				+ "&nocache=" + (gameXML_startTime += RANDOM.nextInt(3000)),
				payload,
				headers);
		
		return post;
	}
	
	public String getProjectID() {
		return gameXML_projectID;
	}
	
	public void resetStartTime() {
		gameXML_startTime = 0;
	}
}
