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
		model.tryLoad();
		
		view = new PlannerView();
		view.attach(model);
		view.start();
		
	}

}
