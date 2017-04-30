import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


/**
 * The view for the Planner. Takes data from the Model and displays it, then updates according to the Controller's logic. 
 * A PORTION OF CODE REUSED FROM MY HW2 SUBMISSION
 * @author nolan
 */
public class PlannerView{
	private JFrame frame;
	private JPanel monthView, header, dayView, deleteView, createView, overlapWarning;
	
	private JTable dayList;
	private int std_rowHeight;
	private JLabel dayListHeader;
	private JButton[] days;
	
	private PlannerModel model;
	private int deleteHour = 0;

	/**
	 * creates a new planner object
	 * planner creates and starts default view (month view)
	 * attempts to load events from a serialized file called "events.data"
	 */
	public PlannerView()
	{
		frame = new JFrame();
	}
	
	/**
	 * loads up the data from the Model and displays it.
	 */
	public void start()
	{
		monthPanel();
		headerPanel();
		dayPanel();
		createPanel();
		deletePanel();
		overlapPanel();
		
		frame.setLayout(new BorderLayout());
		frame.add(header, BorderLayout.NORTH);
		frame.add(monthView, BorderLayout.WEST);
		frame.add(dayView, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * plugs in the supplied data model, also plugging the view into the model to be notifed upon changes
	 * @param m
	 */
	public void attach(PlannerModel m)
	{
		model = m;
		m.addListener(this);
	}
	

	
	
	
	// -------------------------------------------------------------------------------- //
	//               METHODS THAT CREATE THE 'LOOK' AND DRAW FROM THE MODEL              //
	// --------------------------------------------------------------------------------- //
	
	/**
	 * initializes all the components used by the Month View
	 * to then be decorated by updateMonthView
	 */
	private void monthPanel()
	{
		monthView = new JPanel();
		days = new JButton[42];
		
		// make the grid of numbered days that will make up monthView of calendar display
		monthView.setLayout(new GridLayout(7, 7));
		monthView.setBackground(Color.WHITE);
		Border border = BorderFactory.createEmptyBorder(monthView.getHeight() + 10,
				monthView.getWidth() + 10,
				monthView.getWidth() + 10,
				monthView.getHeight() + 10);
		monthView.setBorder(border);
		
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
	 * fills in the components of the month view JPanel
	 */
	private void updateMonthView()
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
	
			// if it's an event day (and not the current day), change background color
			for( Event e : eventDays)
			{
				if(e.day == i && !model.selectedDay(i) && !model.currentDay(i)) 
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
	 * creates the components used by the DayView panel (has the list of events for selected day)
	 * to then be filled out by updateDayView
	 */
	private void dayPanel()
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
		std_rowHeight = dayList.getRowHeight();
		
		
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
	 * fills out the DayView panel with data from the model
	 */
	private void updateDayView()
	{
		// initializations
		ArrayList<Event> events = model.getEventsForSelectedDay();
		
		// here is the top banner, with current day of week + #month /#day
		// add the day and number sub-header
		dayListHeader.setText(model.getDay() + " " + (model.sMonth + 1) + "/" + model.sDay);
		dayListHeader.setHorizontalAlignment(SwingConstants.CENTER);
		
		for(int i = 0; i < 24; i++)
		{
			// reset the rowheight
			dayList.setRowHeight(i, std_rowHeight);
			
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
					dayList.setRowHeight(i, numEvents * std_rowHeight);
					
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
	 * creates the components used by the header
	 */
	private void headerPanel()
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
	 * updates the headerView to reflect the data in the model
	 */
	private void updateHeader()
	{
		JLabel words = (JLabel) header.getComponent(0);
		words.setBackground(Color.WHITE);
		words.setFocusable(false);
		words.setText(model.getMonth() + " " + model.getYear());
	}
	/**
	 * creates the components used by the createPanel (the options panel on top of the planner)
	 */
	private void createPanel()
	{
		createView = new JPanel();
		// clear out the existing display, replace it with the create panel
		createView.setBackground(Color.WHITE);
		createView.setLayout(new GridLayout(1, 5));
		createView.setPreferredSize(new Dimension(1200, 50));
		
		// the jtextfield
		JTextField title = new JTextField();
		changeFontSize(title, 10);
		title.setForeground(Color.GRAY);
	
		
		// the date field
		JButton date;
		date = new JButton();
		changeFontSize(date, 10);
		formatButton(date);
		date.setEnabled(false);
	
		
		// the start and end fields
		JTextField start, end;
		start = new JTextField();
		end = new JTextField();
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
		
		updateCreateView();
	}
	
	/**
	 * fills out the createView panel with the proper default values
	 */
	private void updateCreateView()
	{
		JTextField text = (JTextField) createView.getComponent(0),
				startTime = (JTextField) createView.getComponent(2), 
				endTime = (JTextField) createView.getComponent(3);
		JButton date = (JButton) createView.getComponent(1);
		text.setText("Event Title");
		startTime.setText("00:00");
		endTime.setText("00:00");
		date.setText(model.getSelectedDate());
		
	}
	
	/**
	 * fills out the overlapwarning panel
	 */
	private void overlapPanel()
	{
		overlapWarning = new JPanel(new FlowLayout());
		overlapWarning.setBackground(Color.WHITE);
		
		JTextArea a = new JTextArea("Overlap with existing event detected.");
		changeFontSize(a,  8);
		overlapWarning.add(a);
		

		JButton j = new JButton("CLICK HERE");
		formatButton(j);
		changeFontSize(j, 8);
		overlapWarning.add(j);
		
		
		JTextArea b = new JTextArea("to Re-enter.");
		changeFontSize(b,  8);
		overlapWarning.add(b);
		
		
	}
	
	/**
	 * creates the components used by the deletePanel. Display never changes, so no update needed. 
	 */
	private void deletePanel()
	{
		deleteView = new JPanel();
		deleteView.setBackground(Color.WHITE);
		
		JButton delete = new JButton("Delete Events Occuring During Selected Hour?");
		changeFontSize(delete, 10);
		formatButton(delete);
		deleteView.add(delete, BorderLayout.NORTH);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	// -------------------------------------------------------------------------------- //
	//                 METHODS USED BY THE CONTROLLER (UPDATE / CHANGE THE VIEW)         //
	// --------------------------------------------------------------------------------- //
	
	/**
	 * changes the view of the planner to the CreateView
	 * CreateView displays the month calendar, the daily calendar, and a banner with the options ( back, forward, create, exit)
	 */
	public void createView()
	{
		clearUpHeader();
		frame.add(createView, BorderLayout.NORTH);
		update();
	}
	
	/**
	 * changes the view of the planner to the DeleteView
	 * DeleteView displays the month calendar, the daily calendar, and a banner with the delete option.
	 * (if somehow reached before selecting an hour from the daily events panel, will delete the zero'th hour of the currently selected day)
	 */
	public void deleteView()
	{
		clearUpHeader();
		frame.add(deleteView, BorderLayout.NORTH);
		update();
	}
	
	/**
	 * changes the view of the planner to the Standard View. This is the view given on launch.
	 */
	public void standardView()
	{
		clearUpHeader();
		frame.add(header, BorderLayout.NORTH);
		update();
	}
	
	/**
	 * changes the header view to the overlap detected view. Naturally this is only called if an overlap is detected.
	 */
	public void overlapView()
	{
		clearUpHeader();
		frame.add(overlapWarning);
		update();
	}
	
	/**
	 * blunt-force removes all panels from the header. 
	 */
	private void clearUpHeader()
	{
		frame.remove(header);
		frame.remove(overlapWarning);
		frame.remove(createView);
		frame.remove(deleteView);
	}

	/**
	 * updates all the views to reflect data in the model. 
	 */
	public void update()
	{
		updateMonthView();
		updateDayView();
		updateHeader();
		updateCreateView();
		frame.validate();
		frame.repaint();
	}
	
	
	
	
	
	
	// -------------------------------------------------------------------------------- //
	//                 GETTERS AND SIMPLE "DECORATORS"                                  //
	// --------------------------------------------------------------------------------- //
	
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
	public JTable getDayList() {
		return dayList;
	}
	public JPanel getOverlapWarning() {
		return overlapWarning;
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
}

