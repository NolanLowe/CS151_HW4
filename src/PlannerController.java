import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


/**
 * The controller for the Planner.
 * Takes input from the user, uses it to update the model. The model then notifies the view of the change
 * @author nolan
 *
 */
public class PlannerController{
	PlannerModel model;
	PlannerView view;

	/**
	 * creates a new plannercontroller object.
	 * creates its own instances of the model / view
	 */
	public PlannerController()
	{
		model = new PlannerModel();
		view = new PlannerView();
	}
	
	/**
	 * assigns the variables and starts up the View
	 * "presents the planner object"
	 */
	public void run()
	{
		model.tryLoad();
		view.attach(model);
		view.start();
		attachMonthListeners();
		attachHeaderListeners();
		attachCreateListeners();
		attachDeleteListeners();
	}
	
	

	/**
	 * attaches the 'click on a day on the calendar' functionality to the view
	 */
	private void attachMonthListeners()
	{
		JPanel monthView = view.getMonthView();
		int count = monthView.getComponentCount();
		for(int i = 7; i < count; i ++)
		{
			JButton b = (JButton) monthView.getComponent(i);
			b.addActionListener(new ActionListener() {
				
				// tied to each of the buttons on the monthly view section of the planner
				// when pressed, change the date to that day
				@Override
				public void actionPerformed(ActionEvent e) {
					int day = Integer.valueOf(b.getText());
					model.setDate(-1, -1, day);
					view.standardView();
				}
			});
		}
	}
	

	/**
	 * attaches functionality to the default top-button-bar (back, forward, create, exit)
	 */
	private void attachHeaderListeners()
	{
		// MAKE ALL THE  BUTTONS
		JPanel header = view.getHeader();
		
		// make the back button
		JButton back = (JButton) header.getComponent(1);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, -1);
			}
		});
		
		// the forward button
		JButton forward = (JButton) header.getComponent(2);
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, 1);
			}
		});
		
		// make the create button
		JButton create = (JButton) header.getComponent(3);
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.createView();
			}
		});
		
		// make the quit button
		JButton quit = (JButton) header.getComponent(4);
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.saveEvents();
				System.exit(0);
			}
		});
		
	}


	/**
	 * assigns delete functionality for clicking on the list of daily events & the subsequent delete button showing up.
	 */
	private void attachDeleteListeners()
	{
		// the delete button
		JButton delete = (JButton) view.getDeleteView().getComponent(0);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.delete(view.getDeleteHour());
				view.standardView();
			}
		});
		
		// the JTable functionality
		JTable list = view.getDayList();
		list.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = list.rowAtPoint(e.getPoint());
				// check if events occur during selected hour & day
				ArrayList<Event> events = model.getEventsForSelectedDay();
				if(events.size() != 0)
				{
					for(Event toCheck : events)
					{
						if(toCheck.occurs(row * 60))
						{
							view.setDeleteHour(row);
							view.deleteView();
							return;
						}
					}
					// no events occur during that hour, default to standard view
					view.standardView();
				}
		    }
		});
	}
	
	
	/**
	 * assigns functionality to the 'create' button, and the following create menu, as well as the overlap warning
	 */
	private void attachCreateListeners()
	{
		JPanel header = view.getCreateView();

		// the text and functionality of the create new event banner that displays atop the planner
		JTextField text = (JTextField) header.getComponent(0);
		text.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(text.getText().equals("")) {
					text.setText("Event Title");
					text.setForeground(Color.GRAY);
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				text.setText(""); text.setForeground(Color.BLACK);	
			}
		});

		// the save button functionality 
		JButton save = (JButton) header.getComponent(4);
		JTextField start = (JTextField) header.getComponent(2), end = (JTextField) header.getComponent(3);
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String t = text.getText();
				String st = start.getText();
				String en = end.getText();
				// attempt to add the event, if was overlap prompt the user to change time
				if(! model.addEvent(t, st, en))	
				{
					view.overlapView();
					view.update();
				}
				// otherwise the add was successful, update the view to reflect change
				else 
				{
					view.standardView();
				}
				
			}
		});
		
		// the overlap button functionality
		JButton b = (JButton) view.getOverlapWarning().getComponent(1);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.createView();
			}
		});

	
	}

}
