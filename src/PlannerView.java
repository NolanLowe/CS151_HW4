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
			formatButton(numDay);
			
			// attach appropriate action listener (for the clickz)
			ActionListener l = getListener(i);
			numDay.addActionListener(l);
			
			// if it's the currently selected day, outline the box
			// (selected day defaults to current day ON LAUNCH)
			if(model.currentDay(i))
			{
				invertButton(numDay);
			}
			
			// if day is current actual date, increase the font size
			if(model.selectedDay(i))
			{
				numDay.setBackground(Color.CYAN);
				numDay.setBorderPainted(true);
			}
				
				
			// if it's an event day (and not currently selected), change background color
			for( Event e : eventDays)
			{
				if(e.day == i && !model.selectedDay(i))
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
	 * each day gets one; when pressed changes selected day of the model
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


	/**
	 * functionally a brute force 'repaint' -- used after View pushes changes to model to then reflect the new data
	 */
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
	 * creates the 'look' of the create-new-event panel, paints it over the previous 'header' panel.
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
		title.setForeground(Color.GRAY);
		title.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e){}
			@Override
			public void mousePressed(MouseEvent e){}
			@Override
			public void mouseEntered(MouseEvent e){}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(title.getText().equals("")) {
					title.setText("Untitled Event");
					title.setForeground(Color.GRAY);
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				title.setText(""); title.setForeground(Color.BLACK);	
			}
		});

		
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
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String t = title.getText();
				String st = start.getText();
				String en = end.getText();
				// attempt to add the event, if was overlap prompt the user to change time
				if(! model.addEvent(t, st, en))	
				{
					title.setText("ERROR: overlap");
					start.setText("00:00");
					end.setText("00:00");
				}
				// otherwise the add was successful, update the view to reflect change
				else update();

			}
		});

		// finally add in all the pieces
		header.add(title);
		header.add(date);
		header.add(start);
		header.add(end);
		header.add(save);
		
		frame.add(header, BorderLayout.NORTH);
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
		JLabel words = new JLabel();
		formatButton(words);
		changeFontSize(words, 26);

		words.setText(model.getMonth() + " " + model.getYear());
		header.add(words);
		
		// MAKE ALL THE  BUTTONS
		
		// make the back & forward buttons
		JButton back = new JButton("<");
		formatButton(back);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, -1);
				update();
			}
		});
		
		JButton forward = new JButton(">");
		formatButton(forward);
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, 1);
				update();
			}
		});
		
		// make the create button
		JButton create = new JButton("CREATE");
		formatButton(create);
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPanel();
			}
		});
		
		// make the quit button
		JButton quit = new JButton("QUIT");
		formatButton(quit);
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
	private void formatButton(JComponent b)
	{
		b.setBackground(Color.WHITE);
		b.setFocusable(false);
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
		// the main JPanel, will collect pieces 
		dayView.setBackground(Color.WHITE);
		dayView.setLayout(new BorderLayout());
		
		// here is the top banner, with current day of week + #month /#day
		// add the day and number sub-header
		JLabel header = new JLabel(model.getDay() + " " + (model.sMonth + 1) + "/" + model.sDay, SwingConstants.CENTER);
		changeFontSize(header, 10);
		
		// add header to panel
		dayView.add(header, BorderLayout.NORTH);
		

		// I'll be using a jtable
		// construct the arrays for the column names & the times / event titles
		ArrayList<Event> events = model.getEventsForSelectedDay();

		// create the array of text / hours to place into a JTable
		String[][] content = new String[24][4];
		String[] names = {"Hour", "Event title", "Event start", "Event end"};
		
		// construct the table and set some default behaviors
		JTable t = new JTable(content, names);
		//t.setEnabled(false);
		
		int rowHeight = t.getRowHeight();
		for(int i = 0; i < 24; i++)
		{
			// set the value for the 'hour' collumn
			content[i][0] = (i < 10 ? "0" : "") + Integer.toString(i);
			content[i][0] += (i < 12 ? "AM" : "PM");
			
			// initalize the other values to start of HTML tag used to properly format the table
			for(int y = 1; y < 4; y++) content[i][y] = "<html>";

			// go through the events, appending their values to the existing blanks (if they occur on said hour)
			// if there are multiple, keep track of how many, increase that row's height to reflect this.
			int numEvents = 0;
			for(Event e : events)
			{
				int eventStart = Integer.valueOf(e.startTime.substring(0, 2));
				if(eventStart == i)
				{
					numEvents++;
					t.setRowHeight(i, numEvents * rowHeight);

					content[i][1] += e.title + "<br>";
					content[i][2] += e.startTime + "<br>";
					content[i][3] += e.endTime + "<br>";
				}
			}	
			for(int y = 1; y < 4; y++) content[i][y] += "</html>";
			
		}
		
		// add the proper and logic to the table to allow deletion of events
		t.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String s = (String) t.getValueAt(t.getSelectedRow(), 0);
				int sHour = Integer.valueOf(s.substring(0, 2));
				System.out.println(s);
				deletePanel(sHour);
				
			}
		});
		
		
		// dayview panel is now complete, format it for the scrollbar and add to the frame
		JScrollPane pane = new JScrollPane(t);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setPreferredSize(new Dimension(300, 200));
		
		dayView.add(pane, BorderLayout.CENTER);
		frame.validate();
	}
	
	
	/**
	 * only called by dayPanel when an hour w/ events is selected on view
	 * allows user to clear events starting on that hour (and scheduled for currently selected day)
	 * @param sHour the starting hour for events to be deleted
	 */
	private void deletePanel(int sHour)
	{
		header.removeAll();
		JButton delete = new JButton("Delete Events For Selected Hour?");
		formatButton(delete);
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.delete(sHour);
				update();
			}
		});
		
		header.add(delete);
		frame.validate();
		
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


}

