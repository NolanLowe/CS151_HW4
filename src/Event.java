/**
 * Event class
 * used to keep event titles and starting / ending times
 * @author nolan
 * last updated: 3/10/2017
 */
public class Event{
	public String title;
	public String d;
	public String startTime; // HH/MM, 24 hour format
	public String endTime;
	public Date date;
	
	
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
		this.d = date;
		
		// date is in format MMDDYYYY
		// set up the date instance variable with the proper values 
		int month = Integer.valueOf( date.substring(0, 2));
		int day = Integer.valueOf( date.substring(2, 4) );
		int year = Integer.valueOf( date.substring(4, 8));
		
		this.date = new Date();
		this.date.setDate(year, month, day);
	}

	

	/**
	 * @return a string containing the contents of the event in format:
	 * [day of week] [month] [day] [start time] - [end time] [event title]
	 */
	@Override
	public String toString() {
		
		// get the written day of the week
		String wordDay = date.getDay();
		
		// assemble the returning string
		String rv = "";
		
		// add STRINGDAYOFWEEK MONTH DAYNUMBER
		rv += wordDay + " " + date.getMonth();
		
		// add DAYNUMBER STARTTIME : ENDTIME TITLE
		rv +=  " " + date.cDay + " " + startTime + "-" + endTime + " " + title;

		return rv;	
	}

	/**
	 * @return a string containing the contents of the event in format:
	 * [start time] - [end time] [event title]
	 */
	public String toString_onlyEvent() {
		return " " + startTime + "-" + endTime + " " + title;
	}

	
	
	
}
