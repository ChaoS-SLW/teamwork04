package aiwa.ulti;

import java.time.LocalTime;
import java.time.ZoneId;

public class Ulti {

	public static String showGreeting() {

		int hour = LocalTime.now(ZoneId.of("Asia/Tokyo")).getHour();

		if (hour >= 5 && hour < 11) {
			return "おはようございます！☀️";

		} else if (hour >= 11 && hour < 18) {
			return "こんにちは！🌤️";

		} else {
			return "こんばんは！🌙";
		}
	}

}
