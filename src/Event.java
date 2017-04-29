import java.io.Serializable;

/**
 * Event class
 * used to keep event titles and starting / ending times
 * @author nolan
 * last updated: 3/10/2017
 */
public class Event implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7042137329929006807L;
	public String title;
	public int day, month, year;
	public String startTime; // stored as HH:MM including [:] , 24 hour format
	public String endTime;
	
	
	/**
	 * Constructs a new event using the passed parameters
	 * @param title the title of the event
	 * @param date the date of the event in format MM/DD/YYYY
	 * @param startTime the starting time
	 * @param endTime the ending time (or NA if an ending time does not apply)
	 */
	public Event(String title, String date, String startTime, String endTime) {
		// set up the strings
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		
		// date is in format MMDDYYYY
		// set up the date instance variable with the proper values 
		month = Integer.valueOf( date.substring(0, 2));
	    day = Integer.valueOf( date.substring(2, 4) );
		year = Integer.valueOf( date.substring(4, 8));
	}
	
	/**
	 * @return the start time of the event converted into minutes 
	 */
	public int getStartTimeInMinutes()
	{
		return Integer.valueOf(startTime.substring(0, 2)) * 60  + Integer.valueOf(startTime.substring(3, 5));
	}
	
	/**
	 * @return the end time of the event converted into minutes
	 */
	public int getEndTimeInMinutes()
	{
		return Integer.valueOf(endTime.substring(0, 2)) * 60 + Integer.valueOf(endTime.substring(3, 5));
	}

	/**
	 * @return a string containing the contents of the event in format:
	 * [start time] - [end time] [event title]
	 */
	public String toString_onlyEvent() {
		return " " + startTime + "-" + endTime + " " + title;
	}

	
	/**
	 * tests implicit and param to see if they occur in the same time slot.
	 * @param e
	 * @return false if they do not occur in the same time slot, true otherwise
	 */
	public boolean overlap(Event e)
	{
		// easy case
		if(day != e.day || month != e.month || year != e.year) return false;
		
		// time slot overlap; check the ranges
		// convert the start and end values into minutes for easier comparison
		int aStart = getStartTimeInMinutes();
		int bStart = e.getStartTimeInMinutes();
		int aFin = getEndTimeInMinutes();
		int bFin = e.getEndTimeInMinutes();
		
		if(aStart >= bStart && aStart < bFin) return true;
		if(bStart >= aStart && bStart < aFin) return true;

		return false;
	}

	
	
	
}
