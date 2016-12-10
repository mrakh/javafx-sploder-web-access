package log_on;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class EventDriver {
//	public static void main(String[] args) throws Exception {
//		HttpClientBuilder hcp = HttpClientBuilder.create();
//		CookieStore cs = new BasicCookieStore();
//		hcp.setDefaultCookieStore(cs);
//		hcp.setRedirectStrategy(new LaxRedirectStrategy());
//		CloseableHttpClient chc = hcp.build();
//		
//		HttpGet get = new HttpGet("http://www.sploder.com/cache/events.xml");
//		get.addHeader("Host", "www.sploder.com");
//		get.addHeader("Connection", "keep-alive");
//		get.addHeader("Cache-Control", "max-age=0");
//		get.addHeader("X-Requested-With", "ShockwaveFlash/20.0.0.286");
//		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
//		get.addHeader("Accept", "*/*");
//		get.addHeader("Referer", "http://www.sploder.com/games/members/cryptik/play/crossfire-3/");
//		get.addHeader("Accept-Encoding", "gzip, deflate, sdch");
//		get.addHeader("Accept-Language", "en-US,en;q=0.8");
//		
//		CloseableHttpResponse chr = chc.execute(get);
//		HttpEntity he = chr.getEntity();
//		System.out.println(chr.getStatusLine());
//		System.out.println("Size (Bytes): " + he.getContentLength());
//		BufferedReader br = new BufferedReader(new InputStreamReader(he.getContent()), 65728);
//		StringBuilder sb = new StringBuilder();
//		String line = null;
//		
//		while((line = br.readLine()) != null)
//			sb.append(line + "\n");
//		
//		System.out.println("Response:");
//		System.out.println(sb.toString());
//		for(int i = 0; i < 100; i++) System.out.print("-"); System.out.println();
//		Document doc = Jsoup.parse(sb.toString());
//
//		for(Element e : doc.getElementsByAttribute("u")) {
//			System.out.println("--------UPDATE--------");
//			System.out.println("User: " + e.attr("u"));
//			System.out.println("Event: " + e.attr("e"));
//			System.out.println("Title: " + e.attr("g").replace("+", " "));
//			System.out.println("Pubkey: " + e.attr("s"));
//			String date = e.attr("d");
//			String year = date.substring(0, 4);
//			String month = date.substring(4, 6);
//			String day = date.substring(6, 8);
//			String hour = date.substring(8, 10);
//			String minute = date.substring(10, 12);
//			String second = date.substring(12);
//			System.out.println("Date: " + month + "/" + day + "/" + year + ", " + hour + ":" + minute + ":" + second);
//			System.out.println("----------------------");
//			System.out.println();
//		}
//	}
}
