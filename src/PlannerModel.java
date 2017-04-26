import java.io.*;
import java.util.*;


/**
 * underlying data structure
 * keeps track of events, current date, and extent of currently selected month (first day, last day, day of week)
 * (SOME CODE REUSED FROM MY HW2 SUBMISSION)
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

	// one used to track user, other used to remember actual current date
	public GregorianCalendar selectedDay, currentDay;
	
	// the events data structure
	private ArrayList<Event> events;
	
	

	/**
	 * 
	 */
	public PlannerModel()
	{
		selectedDay = new GregorianCalendar();
		currentDay = new GregorianCalendar();
		update();
		
		tryLoad();
	}
	
	/**
	 * checks if the passed params match the current date
	 * @param day
	 * @return true if a match, false otherwise
	 */
	public boolean currentDaySelected(int day)
	{
		if(day == currentDay.get(Calendar.DAY_OF_MONTH))
			if(sMonth == currentDay.get(Calendar.MONTH))
				if(sYear == currentDay.get(Calendar.YEAR))
					return true;
				
		return false;
	}
	
	/**
	 * trys to load an events arraylist from the bin file selectedDayled data.txt
	 * if one does not exist, creates one, leaving it blank
	 */
	@SuppressWarnings("unchecked")
	public void tryLoad()
	{
		try {	
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("events.data"));
			events = (ArrayList<Event>) in.readObject();
		} 
		catch (Exception e){
		// no existing file found, no events loaded	
			e.printStackTrace();
			events = new ArrayList<Event>();
			System.out.println("Exited");
			saveEvents();
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
	}
	
	/**
	 * changes the date the selectedDayendar object is currently set to.
	 * increments year, month and day by provided amounts, then updates its own instance variables to reflect the change
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
	 * sets the current date to the params provided.
	 * a value of -1 will leave that field with its existing value
	 * @param year the year to set the selectedDayendar object to
	 * @param month the month ''
	 * @param day the day ''
	 */
	public void setDate(int year, int month, int day)
	{
		if(day != -1) selectedDay.set(Calendar.DAY_OF_MONTH, day);
		if(month != -1)selectedDay.set(Calendar.MONTH, month);
		if(year != -1)selectedDay.set(Calendar.YEAR, year);
		
		update();
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
		return months[sMonth- 1];
	}
	/**
	 * @return the selected year of the date object (format YYYY - ex: 2017)
	 */
	public String getYear()
	{
		return Integer.toString(sYear);
	}
	

	/**
	 * returns an arraylist containing all the events occuring in the existing selected year and month
	 * @param month
	 * @param year
	 * @return
	 */
	public ArrayList<Event> getEventsForMonth()
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
	 * sorts all the events currently loaded into the selectedDayendar into sequential order
	 * does not worry about ending times when sorting, only starting times. 
	 * (REUSED FROM HW2)
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
	        				int aStart = Integer.valueOf(a.startTime.substring(0, 2) + a.startTime.substring(3, 5));
	        				int bStart = Integer.valueOf(b.startTime.substring(0, 2) + b.startTime.substring(3, 5));
	        				
	        				if(aStart < bStart)
	        					return -1;
	        			}		
	        		}
	        	}
	        	// b is before a
	        	return 1;	
	        } 
		});
	}
	
	
	
}
