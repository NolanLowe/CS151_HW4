import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Date class
 * used to construct calendar 
 * @author nolan
 * last updated: 3/10/2017
 */
public class Date {
	public static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
	public static final String[] months = {"January","February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	// keeps track of only where the current date is set, current date is not necessarily actual date
	public int cDay, cMonth, cYear;
	// keeps track of the actual current date
	public int firstDayOfMonth, lengthOfMonth;
	public GregorianCalendar cal;
	


	/**
	 * creates a new date object. A date object contains:
	 * a calendar object
	 * integer values for the current day, month, year, first day of the month, and length of the month
	 * Date objects are used by the planner class to construct the month-view panel, and by events to keep 
	 * track of when they are.
	 */
	public Date()
	{
		cal = new GregorianCalendar();
		update();
	}
	

	/**
	 * sets the values for the current day, month, year, first day of the month, and length of the month
	 * called when user changes through the months, to then get the information about the new month, and
	 * make the corresponding month-view panel.
	 */
	public void update()
	{
		// get current month, day and year
		this.cMonth = cal.get(Calendar.MONTH);
		this.cDay = cal.get(Calendar.DAY_OF_MONTH);
		this.cYear = cal.get(Calendar.YEAR);
		
		// get first day of current month
		GregorianCalendar temp = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		this.firstDayOfMonth = temp.get(Calendar.DAY_OF_WEEK);
		
		// get last day of current month
		temp.add(Calendar.MONTH, 1);
		temp.add(Calendar.DAY_OF_MONTH, -1);
		this.lengthOfMonth = temp.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * changes the date the calendar object is currently set to.
	 * increments year, month and day by provided amounts, then updates its own instance variables to reflect the change
	 * @param year amnt. to add to year field
	 * @param month amnt to add to month field
	 * @param day amnt to add to day field
	 */
	public void changeDate(int year, int month, int day)
	{
		cal.add(Calendar.DAY_OF_MONTH, day);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.YEAR, year);
		
		update();

	}
	
	/**
	 * sets the date of the greg.calendar object owned by date to the one provided, then updates the current day, month, year variables
	 * [used by Planner class to change a panel's view to a user-selected time]
	 * @param year the year to set the calendar object to
	 * @param month the month ''
	 * @param day the day ''
	 */
	public void setDate(int year, int month, int day)
	{
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		
		update();
	}
	
	/**
	 * @return the current day of the week for the date object (monday, tuesday, etc...)
	 */
	public String getDay()
	{
		return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
	}
	/**
	 * @return the current month for the date object (january, may...)
	 */
	public String getMonth()
	{
		return months[cMonth- 1];
	}
	/**
	 * @return the year of the date object (format YYYY - ex: 2017)
	 */
	public String getYear()
	{
		return Integer.toString(cYear);
	}
	
	
	
}










