import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * 
 * @author nolan
 *
 */
public class PlannerController{
	PlannerModel model;
	PlannerView view;

	public PlannerController()
	{
		model = new PlannerModel();
		view = new PlannerView();
	}
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
	 * 
	 */
	public void attachMonthListeners()
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
					System.out.println("Button: " + day);
					view.standardView();
				}
			});
		}
	}
	


	/**
	 * 
	 */
	public void attachDayListeners()
	{
		JPanel dayView = view.getDayView();
		JScrollPane p = (JScrollPane) dayView.getComponent(0);
		JTable t = (JTable) p.getComponent(0);

		t.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String s = (String) t.getValueAt(t.getSelectedRow(), 0);
				view.setDeleteHour( Integer.valueOf(s.substring(0, 2)) );
				System.out.println(s);
			}
		});

	}
	
	/**
	 * 
	 */
	public void attachHeaderListeners()
	{
		// MAKE ALL THE  BUTTONS
		JPanel header = view.getHeader();
		
		// make the back & forward buttons
		JButton back = (JButton) header.getComponent(1);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, -1);
				view.update();
			}
		});
		
		JButton forward = (JButton) header.getComponent(2);
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeDate(0, 0, 1);
				view.update();
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
	 * 
	 */
	public void attachDeleteListeners()
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
	 * 
	 */
	public void attachCreateListeners()
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
				else view.standardView();
				
			}
		});

	
	}

}
