package log_on;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpPost;

import utils.ClientUtils;

public class Booster {
	
	private static ScheduledExecutorService gameBooster = Executors.newSingleThreadScheduledExecutor();
	private static LocalDateTime nextAttempt = LocalDateTime.now();
	
	public static void boostGame() {
//		HttpPost boostRequest = (HttpPost) ClientUtils.generateRequest(HttpPost.class,
//			"",
//			payload,
//			parameters
//		);
		moveToNextAuctionEnding();
		armTimer();
	}
	
	public static void armTimer() {
		gameBooster.schedule(
			Booster::boostGame,
			LocalDateTime.now().until(nextAttempt, ChronoUnit.SECONDS),
			TimeUnit.SECONDS
		);
		System.out.println("Timer armed!");
	}
	
	private static void moveToNextAuctionEnding() {
		LocalDateTime now = LocalDateTime.now();
		int hour = now.getHour();
		if(hour >= 0 && hour < 7)
			now = now.withHour(7).withMinute(0).withSecond(0);
		else if(hour >= 7 && hour < 19)
			now = now.withHour(19).withMinute(0).withSecond(0);
		else if(hour >= 19 && hour < 24)
			now = now.plusDays(1).withHour(7).withMinute(0).withSecond(0);
		
		nextAttempt = now.minusSeconds(5);
	}
}
