

public class InputParam {
	
	String user;
	String design;
	boolean graph;
	
	
	public InputParam(String user, String design, boolean graph) {
		super();
		this.user = user;
		this.design = design;
		this.graph = graph;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDesign() {
		return design;
	}
	public void setDesign(String design) {
		this.design = design;
	}
	public boolean isGraph() {
		return graph;
	}
	public void setGraph(boolean graph) {
		this.graph = graph;
	}
	@Override
	public String toString() {
		return "InputParam [user=" + user + ", design=" + design + ", graph="
				+ graph + "]";
	}
	
	
	
	
	

}
