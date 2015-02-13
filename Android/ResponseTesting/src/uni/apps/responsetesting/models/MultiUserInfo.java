package uni.apps.responsetesting.models;

/**
 * This is the data structure for the multi user mode user info
 * 
 * @author Mathew Andea
 *
 */
public class MultiUserInfo {

	private String group;
	private String name;
	private String id;
	private int group_icon;
	
	public MultiUserInfo(String group, String name, String id){
		this.group = group;
		this.name = name;
		this.id = id;	
	}
	
	public MultiUserInfo(String group, String name, String id, int icon){
		this.group = group;
		this.name = name;
		this.id = id;	
		this.group_icon = icon;
	}
	
	public void setIcon(int icon){
		this.group_icon = icon;
	}
	
	public int getGroupIcon(){
		return group_icon;	
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
