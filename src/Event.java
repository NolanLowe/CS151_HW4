/**
 * Event class
 * used to keep event titles and starting / ending times
 * @author nolan
 * last updated: 3/10/2017
 */
public class Event{
	public String title;
	public int day, month, year;
	public String startTime; // HH/MM, 24 hour format
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
	 * @return a string containing the contents of the event in format:
	 * [start time] - [end time] [event title]
	 */
	public String toString_onlyEvent() {
		return " " + startTime + "-" + endTime + " " + title;
	}

	
	
	
}
