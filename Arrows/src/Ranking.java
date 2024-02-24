// Purpose: to rank highscores

import java.util.Calendar;

public class Ranking implements Comparable <Ranking> {
	private String name;
	private long time;
	private Calendar date = Calendar.getInstance();
	
	public Ranking(String s, long t) {
		name = s;
		time = t;
	}
	
	public Ranking(String s, long t, long ms) {
		name = s;
		time = t;
		date.setTimeInMillis(ms);
	}
	
	// returns the Ranking how it would be displayed on the leaderboard
	// @return The Ranking as a string
	public String toString() {
		return String.format("%-16s     %02d:%05.2f     %s", name, time / 60000, time / 1000.0 % 60, date.getTime().toString().substring(4));
	}
	
	// returns the Ranking how it is stored in the leaderboard file
	// @return The Ranking to be stored in the leaderboard file
	public String fileString() {
		return String.format("%-16s %d %d", name, time, date.getTimeInMillis());
	}

	// compares two Rankings
	// @returns if this Ranking is faster that another Ranking. If they have the same speed down to the millisecond it will return which Ranking was achieved earlier
	public int compareTo(Ranking r) {
		if (time != r.time) {
			return (int) (time - r.time);
		}
		return date.compareTo(r.date);
	}
}
