import java.awt.*;
import java.awt.event.*;
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
		
		// make the back & forward buttons
		JButton back = (JButton) header.getComponent(1);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, -1);
			}
		});
		
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
		JButton delete = (JButton) view.getDeleteView().getComponent(0);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.delete(view.getDeleteHour());
				view.standardView();
			}
		});
		
		
		JTable list = view.getDayList();
		list.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = list.rowAtPoint(e.getPoint());
				view.setDeleteHour(row);
				view.deleteView();
		    }
		});
	}
	
	
	/**
	 * assigns functionality to the 'create' button, and the following create menu. 
	 */
	private void attachCreateListeners()
	{
		JPanel header = view.getCreateView();

		JTextField text = (JTextField) header.getComponent(0);
		text.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(text.getText().equals("")) {
					text.setText("Untitled Event");
					text.setForeground(Color.GRAY);
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				text.setText(""); text.setForeground(Color.BLACK);	
			}
		});

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
					text.setText("ERROR: overlap");
					start.setText("00:00");
					end.setText("00:00");
				}
				// otherwise the add was successful, update the view to reflect change
				else 
				{
					view.standardView();
				}
				
			}
		});

	
	}

}
