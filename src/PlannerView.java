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
		headerPanel();
		dayPanel();
		
		frame.setLayout(new BorderLayout());
	
		frame.add(header, BorderLayout.NORTH);
		frame.add(monthView, BorderLayout.WEST);
		frame.add(dayView, BorderLayout.CENTER);

		
		// set defaultOp and visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	

	

	
	/**
	 * 
	 */
	public void monthPanel()
	{
		// make the grid of numbered days that will make up monthView of calendar display
		monthView.setLayout(new GridLayout(7, 7));
		monthView.setBackground(Color.WHITE);
		
		// add in row saying days of week, MON, Tues, etc
		for(String day : PlannerModel.days){
			JTextArea dText = new JTextArea(day);
			changeFontSize(dText, 5);
			dText.setForeground(Color.WHITE);
			dText.setBackground(Color.BLACK);
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
		
		ArrayList<Event> eventDays = model.getEventsForSelectedMonth();
		
		
		// add in numbered days
		for(int i = 1; i < model.lengthOfMonth + 1; i++)
		{
			JButton numDay = new JButton();
			formatHeaderButton(numDay);
			
			// attach appropriate action listener (for the clickz)
			ActionListener l = getListener(i);
			numDay.addActionListener(l);
			
			// if it's the currently selected day, outline the box
			// (selected day defaults to current day ON LAUNCH)
			if(model.currentDay(i))
			{
				invertButton(numDay);
				changeFontSize(numDay, 5);
			}
			
			// if day is current actual date, increase the font size
			if(model.selectedDay(i))
			{
				numDay.setBackground(Color.CYAN);
				numDay.setBorderPainted(true);
			}
				
				
			// if it's an event day, change background color
			for( Event e : eventDays)
			{
				if(e.day == i)
				{
					numDay.setBackground(Color.GRAY);
				}
			}
			
			// finally add the text and add to monthView panel
			numDay.setText(Integer.toString(i));
			monthView.add(numDay);
			
		}
		// add in remaining days
		for(int i = 0; i < 42 - model.lengthOfMonth - model.firstDayOfMonth + 1; i++)
			monthView.add(new JTextArea());

		frame.validate();
	}
	
	/**
	 * actionlistener class used by the buttons on the calendar monthly view
	 * each day gets one, changes the selected day
	 * @param day
	 * @return
	 */
	private ActionListener getListener(int day)
	{
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setDate(-1, -1, day);
				System.out.println("Button: " + day);
				update();
			}
		};
	}


	public void update()
	{
		monthView.removeAll();
		dayView.removeAll();
		header.removeAll();
		monthPanel();
		dayPanel();
		headerPanel();
		frame.repaint();
	}

	/**
	 * 
	 */
	public void createPanel()
	{
		// clear out the existing display, replace it with the create panel
		header.removeAll();
		header.setBackground(Color.WHITE);
		header.setLayout(new GridLayout(1, 5));
		header.setPreferredSize(new Dimension(1200, 50));
		
		// the jtextfield
		JTextField title = new JTextField("Untitled Event");
		changeFontSize(title, 10);
		
		// the date field, time start field, and time end field
		JButton date;
		date = new JButton(model.getSelectedDate());
		changeFontSize(date, 10);
		formatHeaderButton(date);
		date.setEnabled(false);

		JTextField start, end;
		start = new JTextField("00:00");
		end = new JTextField("00:00");
		changeFontSize(start, 10);
		changeFontSize(end, 10);
		
		
		// the save button
		JButton save = new JButton("SAVE");
		formatHeaderButton(save);
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String t = title.getText();
				String st = start.getText();
				String en = end.getText();
				model.addEvent(t, st, en);
				
				header.removeAll();
				headerPanel();
			}
		});

		header.add(title);
		header.add(date);
		header.add(start);
		header.add(end);
		header.add(save);
		
		frame.validate();
	}

	/**
	 * makes the look of the 'header' instance variable into a banner
	 * banner includes the current year and month
	 */
	public void headerPanel()
	{
		header.setBackground(Color.WHITE);
		header.setLayout(new GridLayout(1, 5));
		header.setPreferredSize(new Dimension(1200, 50));
		
		// make the MONTH + YEAR text area
		JButton words = new JButton();
		formatHeaderButton(words);
		changeFontSize(words, 20);

		words.setText(model.getMonth() + " " + model.getYear());
		header.add(words);
		
		// MAKE ALL THE  BUTTONS
		
		// make the back & forward buttons
		JButton back = new JButton("<");
		formatHeaderButton(back);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, -1);
				update();
			}
		});
		
		JButton forward = new JButton(">");
		formatHeaderButton(forward);
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, 1);
				update();
			}
		});
		
		// make the create button
		JButton create = new JButton("CREATE");
		formatHeaderButton(create);
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPanel();
			}
		});
		
		// make the quit button
		JButton quit = new JButton("QUIT");
		formatHeaderButton(quit);
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.saveEvents();
				System.exit(0);
			}
		});
		
		// ADD ALL THE BUTTONS
		
		header.add(back);
		header.add(forward);
		header.add(create);
		header.add(quit);

		frame.validate();
	}
	private void formatHeaderButton(JButton b)
	{
		b.setBackground(Color.WHITE);
		b.setFocusPainted(false);
		b.setBorderPainted(true);
	}
	private void invertButton(JComponent j)
	{
		j.setBackground(Color.BLACK);
		j.setForeground(Color.WHITE);
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
		dayView.setBackground(Color.WHITE);
		
		// add the day and number sub-header
		String wordDay = model.getDay();
		JTextArea header = new JTextArea();
		changeFontSize(header, 15);
		header.setText(wordDay + " " + model.sDay);
		header.setEditable(false);
		
		// add header to panel
		dayView.add(header, BorderLayout.NORTH);
		
		
		// make the list with hours : title of event + time
		JPanel list = new JPanel();
		
		ArrayList<Event> events = model.getEventsForSelectedDay();
		
		for(Event e : events)
		{
			JTextArea j = new JTextArea();
			changeFontSize(j, 5);
			j.setText(e.toString_onlyEvent());
			list.add(j);
		}

		
		dayView.add(list, BorderLayout.CENTER);

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
	public void changeFontSize(JComponent j, int ammount)
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

