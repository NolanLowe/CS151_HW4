import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Planner class 
 * handles GUI and input/output
 * @author nolan
 * last updated: 3/10/2017
 */
public class Planner {
	private ArrayList<Event> events;
	private JFrame frame;
	private final JPanel header;
	private final JPanel body;
	private Date d;
	private Scanner in;
	
	

	/**
	 * creates a new planner object
	 * planner creates and starts default view (month view)
	 * no events loaded
	 * prompts user with main menu (uses console for all input, and SOME output)
	 */
	public Planner()
	{
		
		// handle initializations
		events = new ArrayList<Event>();
		frame = new JFrame();
		d = new Date();
		header = new JPanel();
		body = new JPanel();
		in = new Scanner(System.in);
		// initialize the body and header panels, setting up default appearance
		monthPanel();
		yearPanel();
		
		// construct the default appearance
		frame.add(body, BorderLayout.CENTER);
		frame.add(header, BorderLayout.NORTH);
		
		
		// set defaultOp and visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		// pass off to mainMenu function to handle IO
		mainMenu();
		
	}
	

	

	/**
	 * The system loads events.txt to populate the calendar. 
	 * If there is no such file because it is the first run,
	 * the load function prompts a message to the user indicating this is the first run. 
	 */
	public void load()
	{
		events.clear();
		File f = new File("events.txt");
		if(f.exists())
		{
			try {
				Scanner fileReader = new Scanner(f);
				String title, date, start, end;
				
				while(fileReader.hasNextLine())
				{
					title = fileReader.nextLine();
					date = fileReader.nextLine();
					start = fileReader.nextLine();
					end = fileReader.nextLine();
					
					Event e = new Event(title, date, start, end);
					events.add(e);
					
				}
				fileReader.close();
		
			} catch (FileNotFoundException e) {e.printStackTrace();}
		}
		else
		{
			System.out.println("No events file found, new events file created.");
		}
		
		// refreshes to default view, including any now loaded events
		monthPanel();
	}
	
	

	/**
	 * prompts user with the main menu selections:
	 * [L]oad   [V]iew by  [C]reate [G]o to [E]vent list [D]elete  [Q]uit
	 * 
	 * passes off functionality to respective functions upon selection

	 * 
	 * 
	 * The previous and next options allow the user to navigate the calendar back and forth by day if the calendar is in a day view 
	 * or by month if it is in a month view. 
	 * If the user selects m, navigation is done, and the user gets to access the main menu again.
	 */
	public void mainMenu()
	{
		// output message, get user input
		System.out.println("[L]oad   [V]iew by  [C]reate [G]o to [E]vent list [D]elete  [Q]uit ? ");
		String userInput = in.nextLine();
		
		if(userInput.toLowerCase().equals("q"))
		{
			quit();
		}
		else if(userInput.toLowerCase().equals("c"))
		{
			create();
		}
		else if(userInput.toLowerCase().equals("g"))
		{
			goTo();
		}
		else if(userInput.toLowerCase().equals("l"))
		{
			load();
		}
		else if(userInput.toLowerCase().equals("e"))
		{
			eventList();
		}
		else if (userInput.toLowerCase().equals("d"))
		{
			delete();
		}
		else if(userInput.toLowerCase().equals("v"))
		{
			// see if user wants day view or month view
			System.out.println("[D]ay view or [M]onth view ?");
			userInput = in.nextLine();
			
			if(userInput.toLowerCase().equals("d"))
			{
				dayPanel();
				dayMenu();
			}
			else if(userInput.toLowerCase().equals("m"))
			{
				monthPanel();
				monthMenu();
			}
		}
		else System.out.println("Invalid input in mainMenu\n");
		
		// cycles through main menu infinitely until user selects quit
		mainMenu();
	}
	
	

	/**
	 * handles 'month view' functionality.
	 * displays the current month and highlights day(s) if any event scheduled on that day. 
	 * After a view is displayed, the calendar gives the user three options: P, N, and M, 
	 * previous, next, and main menu, respectively. 
	 * asks the user if they want to move backward or forward a month, or return to the main menu
	 */
	public void monthMenu()
	{
		yearPanel();
		monthPanel();

		// get user input
		System.out.println("[P]revious or [N]ext or [M]ain menu ? ");
		String userInput = in.nextLine();
		
		int offset = 0;
		
		if(userInput.equals("P") || userInput.equals("p"))
			offset = -1;
		else if(userInput.equals("N") || userInput.equals("n"))
			offset = 1;
		else if(userInput.equals("M") || userInput.equals("m"))
		{
			return;
		}
		else
		{
			System.out.println("Invalid input in monthMenu");
			monthMenu();
		}
		
		// user has altered current date, change it
		d.changeDate(0, offset, 0);
		
		monthMenu();	
	}

	/**
	 * constructs the 'look' of the instance variable 'body'
	 * creates a 'calendar' look.
	 * uses the date of the date object to pick the month
	 * highlights any events with the color green
	 * makes the current day a larger font
	 */
	public void monthPanel()
	{
		// make the grid of numbered days that will make up body of calendar display
		body.removeAll();
		body.setLayout(new GridLayout(7, 7));
		
		// add in row saying days of week, MON, Tues, etc
		for(String day : Date.days){
			JTextArea dText = new JTextArea(day);
			dText.setEditable(false);
			body.add(dText);
		}
	
		// fill in first blank spaces of calendar
		for(int i = 0; i < d.firstDayOfMonth - 1; i++)
		{
			JTextArea blank = new JTextArea();
			blank.setEditable(false);
			body.add(blank);
		}
		
		// add in numbered days
		for(int i = 1; i < d.lengthOfMonth + 1; i++)
		{
			JTextArea numDay = new JTextArea();
			// if it's the current day, increase size
			Date temp = new Date();
			if(i == temp.cDay && d.cMonth == temp.cMonth && d.cYear == temp.cYear) 
				changeFontSize(numDay, 15);
	
			// if it's an event day, change background color
			for(Event e : events)
			{
				if(e.date.cDay == i && e.date.cMonth == d.cMonth + 1 && e.date.cYear == d.cYear)
				{
					numDay.setBackground(Color.GREEN);
					break;
				}	
			}
			
			// add the text, add to body panel
			numDay.setText(Integer.toString(i));
			numDay.setEditable(false);
			body.add(numDay);
			
		}
		// add in remaining days
		for(int i = 0; i < 42 - d.lengthOfMonth - d.firstDayOfMonth + 1; i++)
			body.add(new JTextArea());

		body.repaint();
		frame.validate();
	}




	/**
	 * makes the look of the 'header' instance variable into a banner
	 * banner includes the current year and month
	 */
	public void yearPanel()
	{
		// the returning panel value
		header.removeAll();
		
		// make the MONTH + YEAR text area
		JTextArea words = new JTextArea();
		changeFontSize(words, 15);
		
		// was a problem with it being a month ahead, I have no idea why, it drove me insane
		// here's a bad but working fix
		d.changeDate(0, 1, 0); // here
		words.setText(d.getMonth() + " " + d.getYear());
		d.changeDate(0, -1, 0); // here
		
		words.setEditable(false);
		header.add(words);
		
		header.repaint();
		frame.validate();
	}



	/**
	 * If a Day view is chosen, the calendar displays the current date. 
	 * If there is an event(s) scheduled on that day, displays them in the order of start time of the event. 
	 */
	public void dayMenu()
	{
		// set up dayMenu display
		dayPanel();
		yearPanel();
		
		// get user input
		System.out.println("[P]revious or [N]ext or [M]ain menu ? ");
		String userInput = in.nextLine();
		
		int offset = 0;
		
		// move forward, back, or return to main menu
		if(userInput.equals("P") || userInput.equals("p")) 		offset = -1;
		else if(userInput.equals("N") || userInput.equals("n")) offset = 1;
		else if(userInput.equals("M") || userInput.equals("m"))	return;
		else
		{
			System.out.println("Invalid input");
			dayMenu();
		}

		// change the date to reflect user input
		d.changeDate(0, 0, offset);
		
		// cycle infinitely until returned to main menu
		dayMenu();
	}

	/**
	 * makes the look of the 'body' instance variable into the day view
	 * (day view 'look' is described below)
	 * 
	 * Tuesday, Feb 27, 2017 
	 * 
	 * Dr. Kim's office hour 9:15 - 10:15 
	 * ... 
	 */
	public void dayPanel()
	{
		// initialize superpanel
		body.removeAll();
		
		// add the day and number sub-header
		String wordDay = d.getDay();
		JTextArea header = new JTextArea();
		changeFontSize(header, 15);
		header.setText(wordDay + " " + d.cDay);
		header.setEditable(false);
		
		// add header to panel
		body.add(header, BorderLayout.NORTH);
		
		
		// make event list sub-panel
		JPanel list = new JPanel();

		// add events that occur on that day
		int numEvents = 0;
		for(Event e : events)
		{
			int eventYear = e.date.cYear;
			int eventDay = e.date.cDay;
			int eventMonth = e.date.cMonth;
			if(eventYear == d.cYear && eventDay == d.cDay && eventMonth == d.cMonth + 1)
			{
				JTextArea j = new JTextArea();
				changeFontSize(j, 5);
				j.setText(e.toString_onlyEvent());
				list.add(j);
				numEvents++;
			}
		}
		list.setLayout(new GridLayout(numEvents, 1));
		
		body.add(list, BorderLayout.CENTER);
		body.repaint();
		frame.validate();
	}
	
	/**
	 * This option allows the user to schedule an event. 
	 * The calendar asks the user to enter the title, date, starting time, and ending time of an event. 
	 * For simplicity, we consider one day event only. 
	 * Also, let's assume there is no conflict between events that user entered, 
	 * and therefore your program doesn't have to check if a new event is conflict with existing events. 
	 * 
	 * Please stick to the following format to enter data: 
	 * 
	 * Title: a string (doesn't have to be one word) 
	 * date: MM/DD/YYYY 
	 * Starting time and ending time: 24 hour clock such as 06:00 for 6 AM and 15:30 for 3:30 PM. 
	 * 
	 * The user may not enter ending time if an ending time doesn't make sense for the event 
	 * (e.g. leaving for Korea event may have a starting time but no ending time.)
	 */
	public void create()
	{
		String title, date = null, start = null, end = null;
		
		
		// NOT GONNA CHECK THE TITLE
		System.out.println("Title of event:");
		title = in.nextLine();
		
		
		
		// CHECK THE DATE
		boolean badFormat = true;
		while(badFormat)
		{
			System.out.println("Date of event: \n(Format MMDDYYYY with no slashes)");
			date = in.nextLine();
			
			// check the proper length
			if(date.length() == 8)
			{
				// check valid day (only that it's in range (0, 31)
				int d = Integer.valueOf(date.substring(2,  4));
				if( ! (d < 0 || d > 31) )
				{
					// check valid month
					int m = Integer.valueOf(date.substring(0, 2));
					if(! (m < 0 || m > 12))
					{
						// check valid year
						int y = Integer.valueOf(date.substring(4, 8));
						if(! (y < 0))
						{
							// year is good, can break
							badFormat = false;
						}			
					}			
				}
			}
			
			if(badFormat) System.out.println("Bad formatting, please try again.\n");
		}

		
		
		// CHECK THE START TIME
		badFormat = true;
		while(badFormat)
		{
			System.out.println("Starting time:\n(Format HH:MM, 24 hour clock, include [:])");
			start = in.nextLine();
			
			// check for proper length
			if(start.length() == 5)
			{
				// check for colon
				if( start.substring(2,  3).equals(":") )
				{
					// check for legal hour / minutes
					int h = Integer.valueOf(start.substring(0, 2));
					int m = Integer.valueOf(start.substring(3, 5));
					if( h < 24 && h >= 0 && m < 60 && m >= 0 )
					{
						// input is good, can break
						badFormat = false;
					}
				}	
			}
			
			if(badFormat) System.out.println("Bad formatting, please try again.\n");
		}

		
		
		
		// CHECK THE END TIME
		badFormat = true;
		while(badFormat)
		{
			System.out.println("Ending time, or [NA] if there is no ending time:\n(Format HH:MM, include :)");
			end = in.nextLine();
			
			// check if NA
			if(end.equals("NA"))badFormat = false;
			
			// check for proper length
			else if(end.length() == 5)
			{
				// check for colon
				if( end.substring(2,  3).equals(":") )
				{
					// check for legal hour / minutes
					int h = Integer.valueOf(end.substring(0, 2));
					int m = Integer.valueOf(end.substring(3, 5));
					if( h < 24 && h >= 0 && m < 60 && m >= 0 )
					{
						// input is good, can break
						// NOT CONCERNED ABOUT END TIME BEING BEFORE START TIME
						badFormat = false;
					}
				}	
			}
			
			if(badFormat) System.out.println("Bad formatting, please try again.\n");
		}

		
		
		// INPUT IS GOOD, MAKE THE EVENT
		Event e = new Event(title, date, start, end);
		events.add(e);
		
		
		mainMenu();

	}
	
	
	
	
	
	/**
	 * With this option, the user is asked to enter a date in the form of MM/DD/YYYY 
	 * and then the calendar displays the Day view of the requested date 
	 * including any event scheduled on that day in the order of starting time
	 */
	public void goTo()
	{
		System.out.println("enter a date in the form of MM/DD/YYYY: \n(no slashes)");
		String date = in.nextLine();
		// month value includes (-1) because of how calendars handle the month field (1 - 12 not 0 - 11)
		int month = Integer.valueOf(date.substring(0, 2)) - 1;
		int day = Integer.valueOf(date.substring(2, 4));
		int year = Integer.valueOf(date.substring(4, 8));
		
		d.setDate(year, month, day);
		
		// date has been changed to user selected
		// now go to their selected day
		dayMenu();
	}
	
	/**
	 * The user can browse scheduled events. 
	 * The calendar displays all the events scheduled in the calendar in the order of starting date and starting time. 
	 * An example presentation of events is as follows:
	 * 2017  
	 * Friday March 17 13:15 - 14:00 Dentist   
	 * Tuesday April 25 15:00 - 16:00 Job Interview 2017   
	 * Friday June 2 17:00 Leave for Korea
	 */
	public void eventList()
	{
		if(events.size() == 0) mainMenu();
		
		sortEvents();

		int year = events.get(0).date.cYear;
		System.out.println(year);
		
		for(Event e : events)
		{
			if(e.date.cYear != year)
			{
				year = e.date.cYear;
				System.out.println(year);
			}
				
			System.out.println("   " + e.toString());
		}
		System.out.println();
		mainMenu();
	}
	
	
	/**
	 * User can delete an event from the Calendar. 
	 * There are two different ways to delete an event: Selected and All. 
	 * [S]elected: all the events scheduled on the selected date will be deleted. 
	 * [A]ll: all the events scheduled on this calendar will be deleted.
	 * 
	 * [S]elected or [A]ll ? 
	 * 
	 * If the user enters s, then the calendar asks for the date as shown below.
	 * Enter the date. 
	 * 06/03/2016
	 */
	public void delete()
	{
		
		System.out.println("[S]elected or [A]ll or [M]ain Menu");
		String uinput = in.nextLine();
		
		if(uinput.toLowerCase().equals("s"))
		{
			System.out.println("Please enter the date clear of events:\n(format MMDDYYYY with no slashes or spaces)");
			uinput = in.nextLine();
			
			// check for matching events, get their references, then remove them
			ArrayList<Event> toRemove = new ArrayList<Event>();
			for(Event e : events)
			{
				if(e.d.equals(uinput))
				{
					toRemove.add(e);
				}
			}
			System.out.println("  Events deleted:");
			for(Event e : toRemove)
			{
				System.out.println(e.toString());
				events.remove(e);
			}
			
			return;
			
		}
		// just reset the events list
		else if(uinput.toLowerCase().equals("a"))
		{
			events.clear();
			System.out.println("all events removed");
			
			return;
		}
		else if(uinput.toLowerCase().equals("m"))
		{
			
			return;
		}
		else
		{
			System.out.println("Invalid input in delete");
			delete();
		}
	}
	

	
	/**
	 * exits the running program.
	 * saves all the events scheduled in a text file called "events.txt" before closing 
	 * saves events in the order of starting date and starting time. 
	 * @throws FileNotFoundException 
	 */
	public void quit() 
	{
		// try to find file
		File f = new File("events.txt");
		if(!f.exists()) System.out.println("events file not found \nevents.txt was created for you");

		// if there are events, save them (will override existing file)
		if((events.size() != 0))
		{
			PrintWriter p;
			try {
				p = new PrintWriter(f);
				for( Event e : events)
				{
					p.println(e.title);
					p.println(e.d);
					p.println(e.startTime);
					p.println(e.endTime);
				}
				System.out.println("loaded events were saved to events.txt");
					
				p.close();
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		// exit the system
		System.out.println("system exited successfully");
		in.close();
		System.exit(0);
	}
	
	/**
	 * changes the font size of the passed JTextArea object
	 * @param j the JTextArea object
	 * @param ammount will be added to the font size, negative values will decrese font size
	 */
	public void changeFontSize(JTextArea j, int ammount)
	{
		Font f = j.getFont();
		Font biggerF = new Font(f.getFontName(), f.getStyle(), f.getSize() + ammount);
		j.setFont(biggerF);
	}
	
	
	/**
	 * sorts all the events currently loaded into the calendar into sequential order
	 * does not worry about ending times when sorting, only starting times. 
	 */
	public void sortEvents()
	{
		Collections.sort(events, new Comparator<Event>() 
		{
	        @Override public int compare(Event a, Event b) 
	        {  	
	        	// is a year before b year?
	        	if(a.date.cYear < b.date.cYear)
	        		return -1;
	        	// are years equal? check months
	        	else if(a.date.cYear == b.date.cYear )
	        	{
	        		if(a.date.cMonth < b.date.cMonth)
	        			return -1;
	        		else if(a.date.cMonth == b.date.cMonth)
	        		{
	        			// months are equal? check days
	        			if(a.date.cDay < b.date.cDay)
	        				return -1;
	        			// days are equal, check the times
	        			else if (a.date.cDay == b.date.cDay)
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

