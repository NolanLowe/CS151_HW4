/**
 * the GUI of the program. t
 * @author Nolan
 *
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.swing.*;

/**
 * Planner class 
 * handles GUI and input/output
 * @author nolan
 * last updated: 3/10/2017
 */
public class PlannerView implements ActionListener{
	private ArrayList<Event> events;
	private JFrame frame;
	private JPanel monthView, header, dayView;
	
	private PlannerModel model;

	/**
	 * creates a new planner object
	 * planner creates and starts default view (month view)
	 * no events loaded
	 * prompts user with main menu (uses console for all input, and SOME output)
	 */
	public PlannerView()
	{
		frame = new JFrame();
		monthView = new JPanel();
		dayView = new JPanel();
		header = new JPanel();

	}
	
	public void attach(PlannerModel m)
	{
		model = m;
	}
	
	public void start()
	{
		monthPanel();
		yearPanel();
		frame.setLayout(new BorderLayout());
	
		frame.add(monthView, BorderLayout.EAST);
		frame.add(dayView, BorderLayout.CENTER);
		
		// set defaultOp and visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	

	

	
	/**
	 * constructs the 'look' of the monthView panel
	 * creates a 'calendar' look.
	 * uses the date of the date object to pick the month
	 * highlights any events with the color green
	 * makes the current day a larger font
	 */
	public void monthPanel()
	{
		// make the grid of numbered days that will make up monthView of calendar display
		monthView.setLayout(new GridLayout(7, 7));
		
		// add in row saying days of week, MON, Tues, etc
		for(String day : PlannerModel.days){
			JTextArea dText = new JTextArea(day);
			dText.setEditable(false);
			monthView.add(dText);
		}
	
		// fill in first blank spaces of calendar
		for(int i = 0; i < model.firstDayOfMonth - 1; i++)
		{
			JTextArea blank = new JTextArea();
			blank.setEditable(false);
			monthView.add(blank);
		}
		
		
		ArrayList<Event> eventDays = model.getEventsForMonth();
		
		
		// add in numbered days
		for(int i = 1; i < model.lengthOfMonth + 1; i++)
		{
			JButton numDay = new JButton();
			
			// attach appropriate action listener (for the clickz)
			ActionListener l = getListener(i);
			numDay.addActionListener(l);
			
			// if it's the current day, outline the box
			if(model.currentDaySelected(i))
			{
				numDay.setBackground(Color.BLUE);
			}
				
				
			// if it's an event day, change background color
			for( Event e : eventDays)
			{
				if(e.day == i)
				{
					
				}
			}
			
			// add the text, add to monthView panel
			numDay.setText(Integer.toString(i));
			monthView.add(numDay);
			
		}
		// add in remaining days
		for(int i = 0; i < 42 - model.lengthOfMonth - model.firstDayOfMonth + 1; i++)
			monthView.add(new JTextArea());

		monthView.repaint();
		frame.validate();
	}
	
	
	/**
	 * actionlistener class used by the buttons on the calendar monthly view
	 * each day gets one, changes the selected day
	 * @param day
	 * @return
	 */
	public ActionListener getListener(int day)
	{
		return new ActionListener() {
			private final int d = day;
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDate(-1, -1, d);
			}
		};
	}


	public void update()
	{
		monthView.removeAll();
		dayView.removeAll();
		monthPanel();
		dayPanel();
		frame.repaint();
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
		model.changeDate(0, 1, 0); // here
		words.setText(model.getMonth() + " " + model.getYear());
		model.changeDate(0, -1, 0); // here
		
		words.setEditable(false);
		header.add(words);
		
		header.repaint();
		frame.validate();
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
		dayView.removeAll();
		
		// add the day and number sub-header
		String wordDay = model.getDay();
		JTextArea header = new JTextArea();
		changeFontSize(header, 15);
		header.setText(wordDay + " " + model.sDay);
		header.setEditable(false);
		
		// add header to panel
		dayView.add(header, BorderLayout.NORTH);
		
		
		// make event list sub-panel
		JPanel list = new JPanel();

		// add events that occur on that day
		int numEvents = 0;
		for(Event e : events)
		{
			int eventYear = model.sYear;
			int eventDay = model.sDay;
			int eventMonth = model.sMonth;
			if(eventYear == model.sYear && eventDay == model.sDay && eventMonth == model.sMonth + 1)
			{
				JTextArea j = new JTextArea();
				changeFontSize(j, 5);
				j.setText(e.toString_onlyEvent());
				list.add(j);
				numEvents++;
			}
		}
		list.setLayout(new GridLayout(numEvents, 1));
		
		dayView.add(list, BorderLayout.CENTER);
		dayView.repaint();
		frame.validate();
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

	}
	


	
	/**
	 * exits the running program.
	 * saves all the events scheduled in a text file called "events.txt" before closing 
	 * saves events in the order of starting date and starting time. 
	 * @throws FileNotFoundException 
	 */
	public void quit() 
	{
		
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





	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}

