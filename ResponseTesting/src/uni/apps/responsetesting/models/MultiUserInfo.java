package uni.apps.responsetesting.models;

public class MultiUserInfo {

	private String group;
	private String name;
	private String id;
	
	public MultiUserInfo(String group, String name, String id){
		this.group = group;
		this.name = name;
		this.id = id;	
	}

	public String getName(){
		return name;
	}
	
	public String getGroup(){
		return group;
	}
	
	public void setGroup(String group){
		this.group = group;
	}
	
	public String getId(){
		return id;
	}
	


}
