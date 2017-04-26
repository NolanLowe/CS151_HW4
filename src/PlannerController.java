import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PlannerController implements ActionListener {

	public static void main(String[] arg)
	{
		PlannerModel model = new PlannerModel();
		model.tryLoad();
		
		PlannerView view = new PlannerView();
		view.attach(model);
		view.start();
		
	}
// creates the proper views for User, handles IO, coordinating the view and updating the model 

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
