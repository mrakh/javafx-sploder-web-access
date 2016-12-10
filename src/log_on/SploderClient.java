package log_on;

import static utils.ClientUtils.loginRequestHeaders;
import static utils.ClientUtils.publishHeaders;
import static utils.ClientUtils.saveGraphicAsHeaders;
import static utils.ClientUtils.saveThumbnailAsHeaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import game_creators.GameXML;
import utils.StringUtils;

public class SploderClient {
	public static final String HOMEPAGE = "http://www.sploder.com/";
	public static final String XML = "application/x-www-form-urlencoded";
	
	private String username = null;
	private String password = null;
	private String cfduid = null;
	private String phpSessionID = null;
	private boolean loggedOn = false;
	private Document currentPageDoc = null;
	private int nocachevalue = 0;
	
	private HttpClientBuilder sploderClientBuilder = HttpClientBuilder.create();
	private CookieStore sploderCookieStore = new BasicCookieStore();
	private CloseableHttpClient sploderClient;
	
	public CloseableHttpClient getHttpClient() {
		return sploderClient;
	}
	
	public SploderClient(String username, String password) {
		if(username == null || password == null)
			throw new IllegalArgumentException("Username and password cannot be null values.");
		
		this.username = username;
		this.password = password;
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10*1000)
				.setConnectionRequestTimeout(10*1000)
				.setSocketTimeout(10*1000)
				.build();
		
		sploderClientBuilder.setDefaultCookieStore(sploderCookieStore);
		sploderClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		sploderClientBuilder.setDefaultRequestConfig(requestConfig);
		sploderClient = sploderClientBuilder.build();
	}
	
	public void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Attempts to log in to Sploder. Returns true if user has successfully logged in,
	 * or false if attempted log-in is unsuccessful.
	 * @return	<b>true</b> - If log in is successful, or if user is already logged in
	 * @return 	<b>false</b> - If log in is unsuccessful
	 */
	public boolean logIn() {
		if(loggedOn)
			return true;
		
		HttpPost sploderLoginRequest = new HttpPost(HOMEPAGE + "login.php");
		loginRequestHeaders(sploderLoginRequest);
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("Submit", "LOG IN"));
		postData.add(new BasicNameValuePair("username", username));
		postData.add(new BasicNameValuePair("password", password));
		
		try {
			sploderLoginRequest.setEntity(new UrlEncodedFormEntity(postData));
		} catch (UnsupportedEncodingException e) {
			System.out.println("(LOGIN) UnsupportedEncodingException");
			return false;
		}
		
		CloseableHttpResponse sploderLoginResponse = null;
		try {
			sploderLoginResponse = sploderClient.execute(sploderLoginRequest);
		} catch (IOException e) {
			System.out.println("(LOGIN) IOException - Cannot retrieve response");
			return false;
		}

		try {
			currentPageDoc = Jsoup.parse(EntityUtils.toString(sploderLoginResponse.getEntity()));
			Element e = currentPageDoc.select(".alert").first();
			if(e != null && e.text().contains("do not match those on file"))
				return false;
		} catch(ParseException | IOException e) {
			System.out.println("(LOGIN) - Cannot parse document");
			return false;
		}
		
		sploderCookieStore.getCookies().forEach(c -> {
			if(c.getName().contains("cfduid"))
				cfduid = c.getValue();
			else if(c.getName().contains("PHPSESSID"))
				phpSessionID = c.getValue();
		});
		
		try {
			sploderLoginResponse.close();
		} catch(IOException e) {
			System.out.println("(LOGIN) IOException - Attempt to close stream was blocked");
			EntityUtils.consumeQuietly(sploderLoginResponse.getEntity());
		}
		
		loggedOn = true;
		return true;
	}
	
	/**
	 * Attempts to log out of Sploder. Returns true if user has successfully logged out,
	 * or false if attempted log out is unsuccessful.
	 * @return	<b>true</b> - If log out is successful, or if user is already logged out
	 * @return 	<b>false</b> - If log out is unsuccessful
	 */
	public boolean logOut() {
		if(!loggedOn)
			return true;
		HttpGet logout = new HttpGet(HOMEPAGE + "logout.php");
		logout.addHeader("Host", "www.sploder.com");
		logout.addHeader("Connection", "keep-alive");
		logout.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		logout.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		logout.addHeader("Referer", HOMEPAGE);
		logout.addHeader("Accept-Encoding", "gzip, deflate, sdch");
		logout.addHeader("Accept-Language", "en-US,en;q=0.8");
		
		try {
			CloseableHttpResponse response = sploderClient.execute(logout);
			System.out.println("(LOGOUT) " + response.getStatusLine());
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Saves a graphic to your account.
	 * @param	publish Determines whether the graphic is published.
	 * @param	isPrivate Determines whether the graphic is published publicly or privately. If set to <b>true</b>, the graphic is published publically. Otherwise, the graphic is published privately.
	 * @param	image The image to convert to a graphic.
	 * @return	The project ID of the image, or "failed" if the save fails.
	 */
	public String saveGraphicAs(boolean publish, boolean isPrivate, javafx.scene.image.Image image) {
		if(!loggedOn)
			return "failed";
		try {
			Random rand = new Random();
			nocachevalue = 5000 + rand.nextInt(5000);
			long paceKeeper = System.currentTimeMillis();
			Graphics g = new Graphics(image);
			StringJoiner uriParams;
			
			/* ~~~~~~~~~~~~~ BEGIN: CREATE AND SEND GRAPHIC ~~~~~~~~~~~~~ */
			uriParams = new StringJoiner("&", "graphics/put.php?", "");
			uriParams.add("PHPSESSID=" + phpSessionID)
			.add("projid=0")
			.add("objtype=project")
			.add("type=texture")
			.add("isprivate=1")
			.add("nocache=" + nocachevalue);
			
			HttpPost saveAsGraphicRequest = new HttpPost(HOMEPAGE + uriParams.toString());
			saveGraphicAsHeaders(saveAsGraphicRequest);
			
			saveAsGraphicRequest.setEntity(new ByteArrayEntity(g.octetStream()));
			CloseableHttpResponse saveAsGraphicResponse = sploderClient.execute(saveAsGraphicRequest);
			String xmlResponse = EntityUtils.toString(saveAsGraphicResponse.getEntity());
			String projectID = xmlResponse.split("\"")[1];
			/* ~~~~~~~~~~~~~ END: CREATE AND SEND GRAPHIC ~~~~~~~~~~~~~ */
			System.out.println("Response (Graphic): " + saveAsGraphicResponse.getStatusLine() + " [ID: " + projectID + "]");
			saveAsGraphicResponse.close();
			
			/* ~~~~~~~~~~~~~ BEGIN: CREATE AND SEND THUMBNAIL ~~~~~~~~~~~~~ */
			nocachevalue += (System.currentTimeMillis() - paceKeeper);
			uriParams = new StringJoiner("&", "graphics/put.php?", "");
			uriParams.add("PHPSESSID=" + phpSessionID)
			.add("projid=" + projectID)
			.add("objtype=thumbnail")
			.add("type=texture")
			.add("isprivate=1")
			.add("nocache=" + nocachevalue);
			HttpPost saveAsThumbnailRequest = new HttpPost(HOMEPAGE + uriParams.toString());
			saveThumbnailAsHeaders(saveAsThumbnailRequest);
			saveAsThumbnailRequest.setEntity(new ByteArrayEntity(g.thumbnailStream()));
			CloseableHttpResponse saveAsThumbnailResponse = sploderClient.execute(saveAsThumbnailRequest);
			/* ~~~~~~~~~~~~~ END: CREATE AND SEND THUMBNAIL ~~~~~~~~~~~~~ */
			System.out.println("Response (Thumbnail): " + saveAsThumbnailResponse.getStatusLine());
			saveAsThumbnailResponse.close();
			
			if(publish) {
				nocachevalue += (System.currentTimeMillis() - paceKeeper);
				uriParams = new StringJoiner("&", "graphics/put.php?", "");
				uriParams.add("PHPSESSID=" + phpSessionID)
				.add("projid=" + projectID)
				.add("objtype=sprite")
				.add("type=texture")
				.add("isprivate=" + (isPrivate ? "1" : "0"))
				.add("nocache=" + nocachevalue);
				HttpPost publishRequest = new HttpPost(HOMEPAGE + uriParams.toString());
				publishHeaders(publishRequest);
				publishRequest.setEntity(new ByteArrayEntity(g.pngStream()));
				CloseableHttpResponse publishResponse = sploderClient.execute(publishRequest);
				System.out.println("Response (Publish): " + publishResponse.getStatusLine());
				publishResponse.close();
			}
			
			return projectID;
		} catch(Exception e) {
			return "failed";
		}
	}
		
	public void winGame(String pubkey, String time) throws Exception {
		HttpPost post = new HttpPost(HOMEPAGE + "php/gameresults.php?ax=" + StringUtils.sign(pubkey + "true" + time));
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("X-Requested-With", "ShockwaveFlash/21.0.0.216");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Accept", "*/*");
		post.addHeader("Referer", HOMEPAGE + "[[IMPORT]]/sploder.s3.amazonaws.com/fullgame2_b20.swf");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("pubkey", pubkey));
		postData.add(new BasicNameValuePair("gtm", time));
		postData.add(new BasicNameValuePair("w", "true"));
		
		post.setEntity(new UrlEncodedFormEntity(postData));
		CloseableHttpResponse response = sploderClient.execute(post);
		String successResponse = EntityUtils.toString(response.getEntity());
		if(successResponse.trim().endsWith("true"))
			System.out.println("Successfully won game!");
		else
			System.out.println("Did not win game.");
		response.close();
	}
		
	public void deleteGraphics(final int fromId, final int toId) throws Exception {
		List<Integer> graphicsToDelete = new ArrayList<Integer>();
		for(int counter = 0;; counter += 12) {
			HttpGet getGraphicsPage = new HttpGet(HOMEPAGE + "my-graphics.php?start=" + counter);
			getGraphicsPage.addHeader("Host", "www.sploder.com");
			getGraphicsPage.addHeader("Connection", "keep-alive");
			getGraphicsPage.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
			getGraphicsPage.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			getGraphicsPage.addHeader("Upgrade-Insecure-Requests", "1");
			getGraphicsPage.addHeader("Referer", HOMEPAGE + "/my-games.php");
			getGraphicsPage.addHeader("Accept-Encoding", "gzip, deflate, sdch");
			getGraphicsPage.addHeader("Accept-Language", "en-US,en;q=0.8");
			
			Document doc = Jsoup.parse(EntityUtils.toString(sploderClient.execute(getGraphicsPage).getEntity()), HOMEPAGE);
			if(!doc.select("p[class=\"prompt\"]").isEmpty()) // If page doesn't contain any more graphics
				break;
			Elements graphics = doc.select("img[alt^=\"created by \"]");
			if(!graphics.isEmpty()) {
				graphics.stream()
				.mapToInt(SploderClient::getIdFromGraphic)
				.filter(i -> i <= toId && i >= fromId)
				.forEachOrdered(graphicsToDelete::add);
				if(graphicsToDelete.get(graphicsToDelete.size() - 1) == fromId)
					break;
			}
		}
		
		for(int id : graphicsToDelete) {
			HttpPost post = new HttpPost(HOMEPAGE + "my-graphics.php?start=0");
			post.addHeader("Host", "www.sploder.com");
			post.addHeader("Connection", "keep-alive");
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			post.addHeader("Origin", "http://www.sploder.com");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Referer", HOMEPAGE + "my-graphics.php?start=0");
			post.addHeader("Accept-Encoding", "gzip, deflate");
			post.addHeader("Accept-Language", "en-US,en;q=0.8");
			
			List<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("graphic_id", Integer.toString(id)));
			postData.add(new BasicNameValuePair("post_action", "really_delete"));
			postData.add(new BasicNameValuePair(Integer.toString(id), "Really Delete"));
			post.setEntity(new UrlEncodedFormEntity(postData));
			
			System.out.println("Sending request to delete graphic no. " + id + "!");
			CloseableHttpResponse response = sploderClient.execute(post);
			response.close();
		}
	}
	
	private static int getIdFromGraphic(Element e) {
		String urlString = e.attr("src");
		String id = urlString.substring(urlString.lastIndexOf("/")+1, urlString.lastIndexOf("."));
		return Integer.parseInt(id);
	}
	
	public String saveGame(boolean publish, BufferedImage smallThumb, BufferedImage largeThumb, GameXML xml) {
		double sw = smallThumb.getWidth();
		double sh = smallThumb.getHeight();
		double lw = largeThumb.getWidth();
		double lh = largeThumb.getHeight();
		if(sw != 80 || sh != 80 || lw != 220 || lh != 220)
			return "failed";
		
		try {
			CloseableHttpResponse xmlResponse = sploderClient.execute(xml.saveRequest(this));
			xml.parseSaveResponse(xmlResponse);
			
			CloseableHttpResponse smallThumbResponse = sploderClient.execute(xml.thumbRequest(this, true, smallThumb));
			System.out.println("Response (Small Platformer Thumbnail): " + smallThumbResponse.getStatusLine());
			smallThumbResponse.close();
			
			CloseableHttpResponse largeThumbResponse = sploderClient.execute(xml.thumbRequest(this, false, largeThumb));
			System.out.println("Response (Large Platformer Thumbnail): " + largeThumbResponse.getStatusLine());
			largeThumbResponse.close();
			
			if(publish) {
				CloseableHttpResponse publishResponse = sploderClient.execute(xml.publishRequest(this));
				xml.parsePublishResponse(publishResponse);
			}
			
			return xml.getProjectID();
		} catch(Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	
	/*
	1 = playing
	2 = win
	3 = lose
	4 = low health
	5 = close to winning
	6 = got a key
	7 = got 10 more crystals
	8 = give thumbs up
	9 = give thumbs down
	10 = making a game
	11 = ?
	12 = ?
	13 = ?
	14 = ?
	15 = ?
	*/
	public boolean sendGameEvent(String pubkey, String gameTitle, int status) {
		HttpPost post = new HttpPost(HOMEPAGE + "php/gameevent.php");
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("X-Requested-With", "ShockwaveFlash/20.0.0.286");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Accept", "*/*");
		post.addHeader("Referer", HOMEPAGE + "events7.swf");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("e", Integer.toString(status)));
		postData.add(new BasicNameValuePair("s", pubkey));
		postData.add(new BasicNameValuePair("g", gameTitle));
		postData.add(new BasicNameValuePair("PHPSESSID", phpSessionID));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(postData));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		
		CloseableHttpResponse response = null;
		try {
			response = sploderClient.execute(post);
		} catch (IOException e) {
			return false;
		}
		
		System.out.println("(GAME EVENT) " + response.getStatusLine());
		try {
			response.close();
		} catch(IOException e) {
			EntityUtils.consumeQuietly(response.getEntity());
			return false;
		}
		
		return true;
		
	}
	
	public Document getCurrentPage() {
		return currentPageDoc;
	}
	
	public String getPHPSessionID() {
		return phpSessionID;
	}
	
	public String getCloudFlareID() {
		return cfduid;
	}
	
	public void close() {
		try {
			sploderClient.close();
		} catch (IOException e) {
			System.out.println("Unable to close the Sploder client for some reason.");
		}
	}
}

/* HttpClientBuilder sploderClientBuilder = HttpClientBuilder.create();
CookieStore sploderCookieStore = new BasicCookieStore();

sploderClientBuilder.setDefaultCookieStore(sploderCookieStore);
sploderClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
CloseableHttpClient sploderClient = sploderClientBuilder.build();

HttpPost sploderLoginRequest = new HttpPost(LOGIN_POST_DEST);
addHeadersTo(sploderLoginRequest);

List<NameValuePair> postData = new ArrayList<NameValuePair>();

postData.add(new BasicNameValuePair("Submit", "LOG IN"));
postData.add(new BasicNameValuePair("username", ""));
postData.add(new BasicNameValuePair("password", ""));

sploderLoginRequest.setEntity(new UrlEncodedFormEntity(postData));

CloseableHttpResponse sploderLoginResponse = sploderClient.execute(sploderLoginRequest);
System.out.println("\nLogin request get: " + sploderLoginResponse.getStatusLine());
HttpEntity sploderLoginEntity = sploderLoginResponse.getEntity();
if(sploderLoginEntity != null) {
	String htmlPage = EntityUtils.toString(sploderLoginEntity);
	System.out.println("--------------------HTML START--------------------");
	System.out.println(htmlPage);
	System.out.println("--------------------HTML END--------------------");
}

System.out.println("\nPOST logon cookies:");
List<Cookie> cookies = sploderCookieStore.getCookies();
if(cookies.isEmpty())
	System.out.println("None");
else
 	cookies.forEach(c -> System.out.println(c.getName() + " - " + c.getValue()));

sploderClient.close();
*/
