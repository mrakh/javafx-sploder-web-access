package game_creators.physics;

import static log_on.SploderClient.XML;
import static utils.ClientUtils.defaultPostHeaders;
import static utils.ClientUtils.generateRequest;
import static utils.StringUtils.str;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.StringJoiner;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import game_creators.GameSettings;
import game_creators.GameXML;
import game_creators.XMLNode;
import log_on.SploderClient;
import utils.ClientUtils;
import utils.ImageUtils;
import utils.PNGEncoder;

public class PhysicsXML extends GameXML {
	private boolean obeysGraphicsURL = true;
	
	private XMLNode root = new XMLNode("project");
		private XMLNode levels = new XMLNode("levels").setAttr("id", "levels");
		private XMLNode graphics = new XMLNode("graphics");
		
	private PhysicsSettings settings = null;
	
	public PhysicsXML(PhysicsSettings ps) {
		settings = ps;
		root.setAttr("date",			ps.getDate())
			.setAttr("author",			ps.getAuthor())
			.setAttr("comments",		ps.canComment() ? "1" : "0")
			.setAttr("title",			ps.getTitle())
			.setAttr("isprivate",		ps.isPrivate() ? "1" : "0")
			.setAttr("id",				ps.getId())
			.setAttr("allowcopying",	ps.copyingAllowed() ? "1" : "0")
			.setAttr("turbo",			ps.isTurbo() ? "1" : "0")
			.setAttr("mode",			ps.getMode())
			.setAttr("pubkey",			ps.getPubkey());
		root.add(levels, graphics);
	}
	
	public void addLevel(PhysicsLevel ls, PhysicsObjectPool objPool) {
		XMLNode newLevel = new XMLNode("level").setAttr("env", ls.toString());
		newLevel.setText(objPool.toString());
		levels.add(newLevel);
	}
	
	public void addGraphics(int... graphicIds) {
		if(!obeysGraphicsURL)
			throw new RuntimeException("Cannot use the addGraphics() method if URL is not obeyed.");
		XMLNode[] newGraphics = new XMLNode[graphicIds.length];
		for(int i = 0; i < newGraphics.length; i++) {
			String content = "";
			boolean success = true;
			try(InputStream is = new URL("http://sploder.s3.amazonaws.com/gfx/png/" + graphicIds[i] + "_1.png").openStream()) {
				content = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
			} catch(Exception e) {
				System.err.println("Couldn't encode graphic in base-64");
				success = false;
			}
			if(success) {
				newGraphics[i] = new XMLNode("graphic").setAttr("name", str(graphicIds[i]) + "_1");
				newGraphics[i].setText(content);
			}
		}
		graphics.add(newGraphics);
	}
	
	public void addGraphics(int[] ids, BufferedImage... images) {
		if(ids.length != images.length)
			throw new IllegalArgumentException("The number of identifiers should be the same as the number of images");
		obeysGraphicsURL = false;
		final int argLength = ids.length < images.length ? ids.length : images.length;
		XMLNode[] newGraphics = new XMLNode[argLength];
		
		for(int i = 0; i < argLength; i++) {
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				PNGEncoder encoder = new PNGEncoder(baos, PNGEncoder.COLOR_MODE);
				newGraphics[i] = new XMLNode("graphic").setAttr("name", str(ids[i]) + "_1");
				encoder.encode(images[i]);
				newGraphics[i].setText(Base64.getEncoder().encodeToString(baos.toByteArray()));
			} catch(Exception e) {
				System.err.println("Could not encode image no. " + i + " in method addGraphics().");
			}
		}
		
		graphics.add(newGraphics);
	}
	
	public void addGraphics(int[] ids, String... urlStrings) {
		if(ids.length != urlStrings.length)
			throw new IllegalArgumentException("The number of identifiers should be the same as the number of URLs");
		BufferedImage[] images = Arrays.stream(urlStrings).map(ImageUtils::toImage).toArray(BufferedImage[]::new);
		addGraphics(ids, images);
	}
	
	public void addGraphics(int[] ids, File... files) {
		if(ids.length != files.length)
			throw new IllegalArgumentException("The number of identifiers should be the same as the number of files");
		BufferedImage[] images = Arrays.stream(files).map(f -> {
			try {
				return ImageIO.read(f);
			} catch(IOException e) {
				return null;
			}
		}).toArray(BufferedImage[]::new);
		addGraphics(ids, images);
	}

	@Override
	public String toString() {
		return root.toString();
	}


	@Override
	public HttpPost saveRequest(SploderClient client) throws Exception {
		Map<String, String> headers = ClientUtils.defaultPostHeaders(null, SploderClient.XML, creatorVersion() + ".swf", true);
		HttpEntity payload = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("xml", toString())));
		gameXML_startTime = RANDOM.nextInt(7000);
		
		HttpPost post = (HttpPost) ClientUtils.generateRequest(HttpPost.class,
				"php/saveproject5.php?PHPSESSID=" + client.getPHPSessionID() + "&version=5&nocache=" + gameXML_startTime,
				payload,
				headers);
		
		return post;
	}
	
	@Override
	public void parseSaveResponse(CloseableHttpResponse response) throws Exception {
		String xmlResponse = EntityUtils.toString(response.getEntity());
		response.close();
		gameXML_projectID = xmlResponse.split("\"")[1];
	}

	@Override
	public HttpPost publishRequest(SploderClient client) throws Exception {
		if(gameXML_projectID.equals("0") || gameXML_projectID.isEmpty())
			throw new Exception("Invalid project ID. Cannot publish physics game.");
		
		Map<String, String> headers = defaultPostHeaders(null, XML, creatorVersion() + ".swf", true);
		HttpEntity payload = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("xml", toString())));
		StringJoiner url = new StringJoiner("&", "php/savegamedata5.php?PHPSESSID=", "");
		url.add(client.getPHPSessionID())
			.add("projid=" + gameXML_projectID)
			.add("comments=" + settings().canComment())
			.add("private=" + settings().isPrivate())
			.add("nocache=" + (gameXML_startTime += RANDOM.nextInt(3000)));
		
		HttpPost post = (HttpPost) generateRequest(HttpPost.class,
				url.toString(),
				payload,
				headers);
		
		return post;
	}
	
	@Override
	public void parsePublishResponse(CloseableHttpResponse response) throws Exception {
		System.out.println("Response (Publish Physics): " + response.getStatusLine());
		response.close();
	}
	

	@Override
	public GameSettings settings() {
		return settings;
	}
	
	@Override
	public String creatorVersion() {
		return "creator5_b21";
	}
}
