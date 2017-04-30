import java.io.*;
import java.util.*;


/**
 * The Model for the planner. Keeps track of the size and extend of months, user's saved events. 
 * Doesn't "know" about any of the View or Model's functionality. Can be used with either of them. 
 * [A PORTION OF CODE WAS REUSED FROM MY HW2 SUBMISSION]
 * @author Nolan
 *
 */
public class PlannerModel implements Serializable {

	private static final long serialVersionUID = -8105054484660211031L;
	
	public static final String[] days = {"S", "M", "T", "W", "T", "F", "S"};
	public static final String[] months = {"January","February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	// keeps track of a specified date, changes as the user navigates through the planner
	public int sDay, sMonth, sYear;
	// used to properly draw the current month in the View
	public int lengthOfMonth, firstDayOfMonth;

	// one used to track user's position in the calendar, other used to remember the current day (is never changed after)
	public GregorianCalendar selectedDay;
	public final GregorianCalendar currentDay;
	
	// the events data structure
	private ArrayList<Event> events;
	
	private ArrayList<PlannerView> views;
	
	
	

	/**
	 * Constructs a new planner model object. attempts to load events from a file upon construction
	 */
	public PlannerModel()
	{
		selectedDay = new GregorianCalendar();
		currentDay = new GregorianCalendar();
		views = new ArrayList<PlannerView>();
		update();
		
		tryLoad();
	}
	
	/**
	 * attaches the provided planner view object to the model. 
	 * the view will then be notified when any changes that occur to the model
	 * @param v
	 */
	public void addListener(PlannerView v)
	{
		views.add(v);
	}
	
	/**
	 * notifies all the currently attached listeners
	 */
	private void notifyListeners()
	{
		for(PlannerView v : views) v.update();
	}
	
	/**
	 * tries to load an events arraylist from the bin file selectedDayled data.txt
	 */
	@SuppressWarnings("unchecked")
	public void tryLoad()
	{
		try {	
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("events.data"));
			events = (ArrayList<Event>) in.readObject();
			in.close();
		} 
		catch (Exception e){
		// no existing file found, no events loaded	
			events = new ArrayList<Event>();
		}
	}
	
	/**
	 * saves events to the bin file selectedDayled data.txt
	 */
	public void saveEvents()
	{
		try {
			File f = new File("events.data");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(events);
			out.close();
		} 
		catch (FileNotFoundException e) {

		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Events not saved");
		}
	}
	

	/**
	 * sets the values for the current day, month, year, first day of the month, and length of the month
	 * selectedDayled when user changes through the months, to then get the information about the new month, and
	 * make the corresponding month-view panel.
	 */
	private void update()
	{
		// get current month, day and year
		this.sMonth = selectedDay.get(Calendar.MONTH);
		this.sDay = selectedDay.get(Calendar.DAY_OF_MONTH);
		this.sYear = selectedDay.get(Calendar.YEAR);
		
		// get first day of current month
		GregorianCalendar temp = new GregorianCalendar(selectedDay.get(Calendar.YEAR), selectedDay.get(Calendar.MONTH), 1);
		this.firstDayOfMonth = temp.get(Calendar.DAY_OF_WEEK);
		
		// get last day of current month
		temp.add(Calendar.MONTH, 1);
		temp.add(Calendar.DAY_OF_MONTH, -1);
		this.lengthOfMonth = temp.get(Calendar.DAY_OF_MONTH);
		
		notifyListeners();
	}
	
	/**
	 * attempt to add a new event to the planner model. If there is overlap with an existing event, returns false.
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @return false if overlap, true otherwise
	 */
	public boolean addEvent(String title, String startTime, String endTime)
	{
		// properly format the date to include leading zeros if necessary.
		String d = "";
		if(selectedDay.get(Calendar.MONTH) < 10) d += "0";
		d += selectedDay.get(Calendar.MONTH);
		
		if(selectedDay.get(Calendar.DAY_OF_MONTH) < 10) d += "0";
		d += selectedDay.get(Calendar.DAY_OF_MONTH);
	
		d += selectedDay.get(Calendar.YEAR);
		
		// make the new event with the now formatted data
		Event e = new Event(title, d, startTime, endTime);
		
		
		// now make sure that your newly formatted date doesn't occur in another event
		ArrayList<Event> today = getEventsForSelectedDay();
		for(Event event : today)
		{
			if(event.overlap(e)) return false;
		}
		
		// there was no overlap, go ahead and add new event to list
		events.add(e);
		sortEvents();
	
		notifyListeners();
		return true;
	}

	/**
	 * deletes events occurring on selected date (selected date is stored and monitored by the model) 
	 * @param sTime the starting time for events in single-integer-hour format( 1AM, 2PM, etc...)
	 */
	public void delete(int sTime)
	{
		// convert sTime to minutes
		sTime *= 60;
		
		// check events on selected day for matches
		ArrayList<Event> eventsForDay = getEventsForSelectedDay();
		for(Event e : eventsForDay)
		{
			if(e.getStartTimeInMinutes() >= sTime && e.getStartTimeInMinutes() < sTime + 60) this.events.remove(e);
		}
		notifyListeners();
	}

	/**
	 * sorts all the events currently loaded into the selectedDayendar into sequential order
	 * (events occurring on same day are sorted by starting time as well)
	 */
	public void sortEvents()
	{
		Collections.sort(events, new Comparator<Event>() 
		{
	        @Override public int compare(Event a, Event b) 
	        {  	
	        	// is a year before b year?
	        	if(a.year < b.year)
	        		return -1;
	        	// are years equal? check months
	        	else if(a.year == b.year )
	        	{
	        		if(a.month < b.month)
	        			return -1;
	        		else if(a.month == b.month)
	        		{
	        			// months are equal? check days
	        			if(a.day < b.day)
	        				return -1;
	        			// days are equal, check the times
	        			else if (a.day == b.day)
	        			{
	        				if(a.getStartTimeInMinutes() < b.getStartTimeInMinutes())
	        					return -1;
	        			}		
	        		}
	        	}
	        	// b is before a
	        	return 1;	
	        } 
		});
	}
	
	
	
	// -------------------------------------------------------------------------------- //
	//                 GETTERS AND SETTERS                                           //
	// --------------------------------------------------------------------------------- //

	/**
	 * INCREMENTS the currently selected date by the provided integer values, then updates selected day values to reflect change 
	 * EX: (-1, -1, 1) will change 04/29/17 to 04/30/19
	 * @param year amnt. to add to year field
	 * @param month amnt to add to month field
	 * @param day amnt to add to day field
	 */
	public void changeDate(int year, int month, int day)
	{
		selectedDay.add(Calendar.DAY_OF_MONTH, day);
		selectedDay.add(Calendar.MONTH, month);
		selectedDay.add(Calendar.YEAR, year);
		update();
	}

	/**
	 * SETS the current date to the params provided. a value of -1 will leave that field with its existing value
	 * @param year 
	 * @param month
	 * @param day 
	 */
	public void setDate(int year, int month, int day)
	{
		if(day != -1) selectedDay.set(Calendar.DAY_OF_MONTH, day);
		if(month != -1)selectedDay.set(Calendar.MONTH, month);
		if(year != -1)selectedDay.set(Calendar.YEAR, year);
		update();
	}
	
	/**
	 * checks if the passed day matches the current selected date
	 * EX: 
	 * @param day
	 * @return true if the passed day is the selected day of month, false otherwise
	 */
	public boolean selectedDay(int day)
	{
		if(day == selectedDay.get(Calendar.DAY_OF_MONTH))	
		{
			if(sMonth == selectedDay.get(Calendar.MONTH))
			{
				if(sYear == selectedDay.get(Calendar.YEAR))
					return true;
			}
		}		
		return false;
	}

	/**
	 * checks if the passed day matches the current ACTUAL date
	 * uses currently selected month / year values from 'selected date'
	 * the current date is set on launch and never modified subsequently
	 * @param day the integer value for the current day, starts at 1 for the 1st, not 0
	 * @return true if a match, false otherwise
	 */
	public boolean currentDay(int day)
	{
		if(day == currentDay.get(Calendar.DAY_OF_MONTH))
			if(sMonth == currentDay.get(Calendar.MONTH))
				if(sYear == currentDay.get(Calendar.YEAR))
					return true;
				
		return false;
	}

	/**
	 * @return the selected day of the week for the date object in string format (monday, tuesday, etc...)
	 */
	public String getDay()
	{
		return selectedDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
	}
	/**
	 * @return the selected month for the date object (january, may...)
	 */
	public String getMonth()
	{
		return months[sMonth];
	}
	/**
	 * @return the selected year of the date object (format YYYY - ex: 2017)
	 */
	public String getYear()
	{
		return Integer.toString(sYear);
	}
	

	/**
	 * @return an array containing all the events occuring on the currently stored selected MONTH
	 * used to draw the monthly calendar view
	 * the selected date is changed when a user clicks on any of the days of the month view, or the back/forward button.
	 */
	public ArrayList<Event> getEventsForSelectedMonth()
	{
		sortEvents();
		ArrayList<Event> forMonth = new ArrayList<Event>();
		
		for(Event e : events)
		{
			if(e.month == sMonth && e.year == sYear)
				forMonth.add(e);
		}
		
		return forMonth;
	}
	
	/**
	 * @return an array containing all the events occuring on the currently stored selected DAY
	 * used to draw the daily calendar view
	 * the selected date is changed when a user clicks on any of the days of the month view, or the back/forward button.
	 */
	public ArrayList<Event> getEventsForSelectedDay()
	{
		sortEvents();
		ArrayList<Event> forDay = new ArrayList<Event>();
		
		for(Event e : events)
		{
			if(e.day == sDay && e.month == sMonth && e.year == sYear)
				forDay.add(e);
		}
		
		return forDay;
	}
	
	/**
	 * @return the String values for the currently selected month and day, in format " MONTH/DAYOFMONTH"
	 * EX: if selected date is May 20th, will return 5/20 in a string.
	 */
	public String getSelectedDate()
	{
		return selectedDay.get(Calendar.MONTH) + "/" + selectedDay.get(Calendar.DAY_OF_MONTH) + "/" + getYear();
	}
	
	
	
}
