package utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import static log_on.SploderClient.*;

public class ClientUtils {
	public static void saveGraphicAsHeaders(HttpPost post) {
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("X-Requested-With", "ShockwaveFlash/20.0.0.286");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/octet-stream");
		post.addHeader("Accept", "*/*");
		post.addHeader("Referer", HOMEPAGE + "pixeleditor05s.swf");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
	}
	
	public static void loginRequestHeaders(HttpPost post) {
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Cache-Control", "max-age=0");
		post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("Upgrade-Insecure-Requests", "1");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Referer", HOMEPAGE + "index.php?logout=1&good_message=You+have+been+logged+out.");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
	}

	public static void saveThumbnailAsHeaders(HttpPost post) {
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("X-Requested-With", "ShockwaveFlash/20.0.0.286");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/octet-stream");
		post.addHeader("Accept", "*/*");
		post.addHeader("Referer", HOMEPAGE + "pixeleditor05s.swf");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
	}

	public static void publishHeaders(HttpPost post) {
		post.addHeader("Host", "www.sploder.com");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Origin", "http://www.sploder.com");
		post.addHeader("X-Requested-With", "ShockwaveFlash/20.0.0.286");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		post.addHeader("Content-Type", "application/octet-stream");
		post.addHeader("Accept", "*/*");
		post.addHeader("Referer", HOMEPAGE + "pixeleditor05s.swf");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
	}
	
	public static Map<String, String> defaultPostHeaders(String acceptMime, String contentType, String referrerRelativeURL, boolean fromFlashApp) {
		Map<String, String> headerParams = new HashMap<>();
		headerParams.put("Host", "www.sploder.com");
		headerParams.put("Connection", "keep-alive");
		headerParams.put("Origin", "http://www.sploder.com");
		if(fromFlashApp)
			headerParams.put("X-Requested-With", "ShockwaveFlash/20.0.0.286");
		headerParams.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		headerParams.put("Content-Type", (contentType != null) ? contentType : "application/octet-stream");
		headerParams.put("Accept", (acceptMime != null) ? acceptMime : "*/*");
		headerParams.put("Referer", HOMEPAGE + (referrerRelativeURL != null ? referrerRelativeURL : ""));
		headerParams.put("Accept-Encoding", "gzip, deflate, sdch");
		headerParams.put("Accept-Language", "en-US,en;q=0.8");
		return headerParams;
	}
	
	public static HttpRequestBase generateRequest(Class<? extends HttpRequestBase> requestClass, String relativeDestURL, HttpEntity payload, Map<String, String> parameters) {
		HttpRequestBase requestTemplate = null;
		try {
			requestTemplate = requestClass.getConstructor(String.class).newInstance(HOMEPAGE + relativeDestURL);
		} catch (InstantiationException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RequestException(e.getClass().getSimpleName() + " in SploderClient.generateRequest(Class<?>, HttpEntity, Map<String, String>)");
		}
		final HttpRequestBase request = requestTemplate;
		parameters.entrySet().forEach(pair -> request.addHeader(pair.getKey(), pair.getValue()));
		if(HttpPost.class.isAssignableFrom(requestClass))
			((HttpPost) request).setEntity(payload);
		return request;
	}
	
	public HttpRequestBase generateRequest(Class<? extends HttpRequestBase> requestClass, String destURL, HttpEntity payload, String... parameters) {
		if(parameters.length % 2 != 1)
			throw new IllegalArgumentException("SploderClient.generateRequest(Class<?>, HttpEntity, String...) must have an even number of String parameters");
		Map<String, String> paramMap = new HashMap<>();
		for(int i = 0; i < parameters.length; i += 2)
			paramMap.put(parameters[i], parameters[i+1]);
		return generateRequest(requestClass, destURL, payload, paramMap);	
	}
}

@SuppressWarnings("serial")
class RequestException extends RuntimeException {
	public RequestException(String message) {
		super(message);
	}
}
