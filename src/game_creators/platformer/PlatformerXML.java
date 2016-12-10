package game_creators.platformer;

import static log_on.SploderClient.XML;
import static utils.ClientUtils.defaultPostHeaders;
import static utils.ClientUtils.generateRequest;
import static utils.StringUtils.str;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.StringJoiner;

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

public class PlatformerXML extends GameXML {
	
	private int artificialGraphicsId = -1;
	private String mode = creatorVersion().substring("creator".length(), creatorVersion().indexOf('_'));
	
	private XMLNode root = new XMLNode("project");
		private XMLNode levels = new XMLNode("levels").setAttr("id", "levels");
		private XMLNode graphics = new XMLNode("graphics");
		private XMLNode textures = new XMLNode("textures").setAttr("lastid", "0");
	
	private PlatformerSettings settings = null;
	
	public PlatformerXML(PlatformerSettings ppc) {
		if(ppc != null) {
			settings = ppc;
			root.setAttr("date",		ppc.getDate())
				.setAttr("author",		ppc.getAuthor())
				.setAttr("comments",	ppc.canComment() ? "1" : "0")
				.setAttr("title",		ppc.getTitle())
				.setAttr("isprivate",	ppc.isPrivate() ? "1" : "0")
				.setAttr("id",			ppc.getId())
				.setAttr("fast",		ppc.isUniformLighting() ? "1" : "0")
				.setAttr("bitview",		ppc.is8Bit() ? "1" : "0")
				.setAttr("mode",		ppc.getMode())
				.setAttr("pubkey",		ppc.getPubkey())
				.setAttr("g",			ppc.containsGraphics() ? "1" : "0");
		}
		root.add(levels, graphics, textures);
	}
	
	public void addLevel(PlatformerLevel ls) {
		ls.childOf(this);
		XMLNode newLevel = new XMLNode("level")
				.setAttr("name", ls.getTitle())
				.setAttr("avatar", ls.getAvatar().get())
				.setAttr("env", new StringJoiner(",")
						.add(ls.getBackground().get())
						.add(ls.getSky())
						.add(ls.getGround())
						.add(str(ls.getLight()))
						.toString())
				.setAttr("music", ls.getMusic());

		StringJoiner sj = new StringJoiner("|");
		ls.getObjects().forEach(obj -> sj.add(obj.toString()));
		newLevel.setText(sj.toString());
		levels.add(newLevel);
	}
	
	public void addGraphics(int... graphicIds) {
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
	
	public void addGraphics(Path... paths) {
		XMLNode[] newGraphics = new XMLNode[paths.length];
		for(int i = 0; i < newGraphics.length; i++) {
			String content = "";
			boolean success = true;
			try(InputStream is = Files.newInputStream(paths[i])) {
				content = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
			} catch(Exception e) {
				System.err.println("Couldn't encode graphic in base-64");
				success = false;
			}
			if(success) {
				newGraphics[i] = new XMLNode("graphic").setAttr("name", artificialGraphicsId-- + "_1");
				newGraphics[i].setText(content);
			}
		}
		graphics.add(newGraphics);
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
		
		HttpPost xmlPost = (HttpPost) ClientUtils.generateRequest(HttpPost.class,
				"php/saveproject" + mode + ".php?PHPSESSID=" + client.getPHPSessionID() + "&version=" + mode + "&nocache=" + gameXML_startTime,
				payload,
				headers);
		
		return xmlPost;
	}

	
	@Override
	public void parseSaveResponse(CloseableHttpResponse response) throws Exception {
		String xmlResponse = EntityUtils.toString(response.getEntity());
		response.close();
		gameXML_projectID = xmlResponse.split("\"")[1];
		System.out.println("PLATFORMER SAVE RESPONSE:");
		System.out.println(xmlResponse);
	}
	
	@Override
	public HttpPost publishRequest(SploderClient client) throws Exception {
		if(gameXML_projectID.equals("0") || gameXML_projectID.isEmpty())
			throw new Exception("Invalid project ID. Cannot publish platformer.");
		Map<String, String> headers = defaultPostHeaders(null, XML, creatorVersion() + ".swf", true);
		HttpEntity payload = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("xml", toString())));
		StringJoiner url = new StringJoiner("&", "php/savegamedata" + mode + ".php?PHPSESSID=", "");
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
		String xmlResponse = EntityUtils.toString(response.getEntity());
		System.out.println("Response (Publish Platformer): " + response.getStatusLine());
		response.close();
		System.out.println("PLATFORMER SAVE RESPONSE:");
		System.out.println(xmlResponse);
	}
	
	@Override
	public GameSettings settings() {
		return settings;
	}
	
	@Override
	public String creatorVersion() {
		return "creator2_b17";
	}
}