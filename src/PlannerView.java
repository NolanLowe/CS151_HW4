import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 * Planner class 
 * handles GUI and input/output
 * A PORTION OF CODE REUSED FROM MY HW2 SUBMISSION
 * @author nolan
 * last updated: 3/10/2017
 */
public class PlannerView{
	private JFrame frame;
	private JPanel monthView, header, dayView, deleteView, createView;
	
	private JTable dayList;
	private JLabel dayListHeader;
	private JButton[] days;
	
	private PlannerModel model;
	private int deleteHour = 0;

	/**
	 * creates a new planner object
	 * planner creates and starts default view (month view)
	 * no events loaded
	 * prompts user with main menu (uses console for all input, and SOME output)
	 */
	public PlannerView()
	{
		frame = new JFrame();
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
		createPanel();
		deletePanel();
		
		frame.setLayout(new BorderLayout());
		frame.add(header, BorderLayout.NORTH);
		frame.add(monthView, BorderLayout.WEST);
		frame.add(dayView, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	

	/**
	 * 
	 */
	public void monthPanel()
	{
		monthView = new JPanel();
		days = new JButton[42];
		
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
		// add in the rest of the buttons
		for(int i = 0; i < 42; i++)
		{
			JButton b = new JButton();
			monthView.add(b);
			days[i] = b;
			
		}
		
		updateMonthView();
	}

	/**
	 * a different day has been selected
	 * change the day view to reflect that day's events
	 */
	public void update()
	{
		updateMonthView();
		updateDayView();
		updateHeader();
		frame.validate();
	}
	
	/**
	 * 
	 */
	public void updateMonthView()
	{
		// declarations
		int offset = 0;
		int runCount = 0;
		ArrayList<Event> eventDays = model.getEventsForSelectedMonth();
		
		// fill in first blank spaces of calendar
		for(int i = 0; i < model.firstDayOfMonth - 1; i++, offset++)
			blankButton(days[i]);


		// add in numbered days
		offset--;
		for(int i = 1; i < model.lengthOfMonth + 1; i++)
		{
			// format the button, make sure it's enabled
			formatButton(days[i + offset]);
			(days[i + offset]).setEnabled(true);
			
			// if it's the currently selected day, outline the box
			// (selected day defaults to current day ON LAUNCH)
			if(model.currentDay(i)) invertButton((days[i + offset]));

			
			// if day is current actual date, increase the font size
			if(model.selectedDay(i))
			{
				(days[i + offset]).setBackground(Color.CYAN);
				(days[i + offset]).setBorderPainted(true);
			}

			// if it's an event day (and not currently selected), change background color
			for( Event e : eventDays)
			{
				if(e.day == i && !model.selectedDay(i)) 
					(days[i + offset]).setBackground(Color.GRAY);
			}

			// finally add the text to the button, and add to monthView panel
			(days[i + offset]).setText(Integer.toString(i));
			
			runCount++;
		}
		offset += runCount;
		
		// add in remaining days
		for(int i = 0; i < 42 - model.lengthOfMonth - model.firstDayOfMonth + 1; i++)
			blankButton(days[i + offset + 1]);

	}

	/**
	 * 
	 */
	public void updateDayView()
	{
		// initializations
		ArrayList<Event> events = model.getEventsForSelectedDay();
		
		// here is the top banner, with current day of week + #month /#day
		// add the day and number sub-header
		dayListHeader.setText(model.getDay() + " " + (model.sMonth + 1) + "/" + model.sDay);
		dayListHeader.setHorizontalAlignment(SwingConstants.CENTER);
		
		int rowHeight = dayList.getRowHeight();
		for(int i = 0; i < 24; i++)
		{
			// blank the fields to erase existing data
			dayList.setValueAt("", i, 1);
			dayList.setValueAt("", i, 2);
			dayList.setValueAt("", i, 3);

			// go through the events, appending their values to the existing blanks (if they occur on said hour)
			// if there are multiple, keep track of how many, increase that row's height to reflect this.
			int numEvents = 0;
			String title = "<html>", sTime = "<html>", eTime = "<html>";
			for(Event e : events)
			{
				int eventStart = Integer.valueOf(e.startTime.substring(0, 2));
				if(eventStart == i)
				{
					numEvents++;
					dayList.setRowHeight(i, numEvents * rowHeight);
					
					title += e.title + "<br>";
					sTime += e.startTime + "<br>";
					eTime += e.endTime + "<br>";
				}
			}	
			title += "</html>";
			sTime += "</html>";
			eTime += "</html>";
			
			dayList.setValueAt(title, i, 1);
			dayList.setValueAt(sTime, i, 2);
			dayList.setValueAt(eTime, i, 3);
			
			title = ""; sTime = ""; eTime = "";

		}
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
		// init.
		dayView = new JPanel();
		dayListHeader = new JLabel();
		dayList = new JTable();
		String[] names = {"Hour", "Event title", "Event start", "Event end"};
		String[][] content = new String[24][4];
		for(int i = 0; i < 24; i++)
		{
			content[i][0] = (i < 10 ? "0" : "") + Integer.toString(i);
			content[i][0] += (i < 12 ? "AM" : "PM");
		}
		dayList = new JTable(content, names);
		
		
		// invariable formatting
		changeFontSize(dayListHeader, 10);
		dayView.setBackground(Color.WHITE);
		dayView.setLayout(new BorderLayout());
		JScrollPane pane = new JScrollPane(dayList);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setPreferredSize(new Dimension(300, 200));
		
		
		// finally add the (blank at this time) pieces
		dayView.add(pane, BorderLayout.CENTER);
		dayView.add(dayListHeader, BorderLayout.NORTH);
		
		updateDayView();
	}
	
	/**
	 * creates the 'look' of the create-new-event panel, paints it over the previous 'header' panel.
	 */
	public void createPanel()
	{
		createView = new JPanel();
		// clear out the existing display, replace it with the create panel
		createView.setBackground(Color.WHITE);
		createView.setLayout(new GridLayout(1, 5));
		createView.setPreferredSize(new Dimension(1200, 50));
		
		// the jtextfield
		JTextField title = new JTextField("Untitled Event");
		changeFontSize(title, 10);
		title.setForeground(Color.GRAY);

		
		// the date field
		JButton date;
		date = new JButton(model.getSelectedDate());
		changeFontSize(date, 10);
		formatButton(date);

		
		// the start and end fields
		JTextField start, end;
		start = new JTextField("00:00");
		end = new JTextField("00:00");
		changeFontSize(start, 10);
		changeFontSize(end, 10);
		
		
		// the save button
		JButton save = new JButton("SAVE");
		formatButton(save);

		// finally add in all the pieces
		createView.add(title);
		createView.add(date);
		createView.add(start);
		createView.add(end);
		createView.add(save);
	}
	
	

	
	/**
	 * makes the look of the 'header' instance variable into a banner
	 * banner includes the current year and month
	 */
	public void headerPanel()
	{
		header = new JPanel();
		
		header.setBackground(Color.WHITE);
		header.setLayout(new GridLayout(1, 5));
		header.setPreferredSize(new Dimension(1200, 50));
		
		// make the MONTH + YEAR text area
		JLabel words = new JLabel();
		changeFontSize(words, 26);
		header.add(words);
		
		// MAKE ALL THE  BUTTONS
		
		// make the back & forward buttons
		JButton back = new JButton("<");
		formatButton(back);
		
		JButton forward = new JButton(">");
		formatButton(forward);
		
		// make the create button
		JButton create = new JButton("CREATE");
		formatButton(create);
		
		// make the quit button
		JButton quit = new JButton("QUIT");
		formatButton(quit);
		
		// ADD ALL THE BUTTONS
		
		header.add(back);
		header.add(forward);
		header.add(create);
		header.add(quit);
		
		updateHeader();
	}
	
	/**
	 * 
	 */
	public void updateHeader()
	{
		JLabel words = (JLabel) header.getComponent(0);
		words.setBackground(Color.WHITE);
		words.setFocusable(false);
		words.setText(model.getMonth() + " " + model.getYear());
	}
	
	
	private void formatButton(JButton b)
	{
		b.setEnabled(true);
		b.setBorderPainted(true);
		b.setBackground(Color.WHITE);
		b.setFocusable(false);
	}
	private void blankButton(JButton b)
	{
		b.setEnabled(false);
		b.setFocusable(false);
		b.setText("");
		b.setBorderPainted(false);
		b.setBackground(Color.LIGHT_GRAY);
	}
	private void invertButton(JComponent j)
	{
		j.setBackground(Color.BLACK);
		j.setForeground(Color.WHITE);
	}
	
	
	/**
	 * only called by dayPanel when an hour w/ events is selected on view
	 * allows user to clear events starting on that hour (and scheduled for currently selected day)
	 * @param sHour the starting hour for events to be deleted
	 */
	private void deletePanel()
	{
		deleteView = new JPanel();
		
		JButton delete = new JButton("Delete Events For Selected Hour?");
		formatButton(delete);
		deleteView.add(delete, BorderLayout.CENTER);
		
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
	
	public JPanel getMonthView() {
		return monthView;
	}
	public JPanel getHeader() {
		return header;
	}
	public JPanel getDayView() {
		return dayView;
	}
	public JPanel getDeleteView() {
		return deleteView;
	}
	public JPanel getCreateView() {
		return createView;
	}
	public void setDeleteHour(int deleteHour) {
		this.deleteHour = deleteHour;
	}
	public int getDeleteHour() {
		return deleteHour;
	}
	


}

