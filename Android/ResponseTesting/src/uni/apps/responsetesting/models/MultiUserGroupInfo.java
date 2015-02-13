package uni.apps.responsetesting.models;

import java.util.ArrayList;

import uni.apps.responsetesting.R;

/**
 * This is the data structure for the multi user mode group list
 * 
 * @author Mathew Andea
 *
 */
public class MultiUserGroupInfo {

	private String group;
	private ArrayList<MultiUserInfo> users;
	private int iconId;
	
	public MultiUserGroupInfo(String group){
		this.group = group;
		users = new ArrayList<MultiUserInfo>();
		setIcon(R.drawable.uni_logo);
	}
	
	public MultiUserGroupInfo(String group, int icon){
		this.group = group;
		users = new ArrayList<MultiUserInfo>();
		setIcon(icon);
	}
	
	public void addUser(MultiUserInfo user){
		user.setGroup(group);
		user.setIcon(iconId);
		users.add(user);
	}
	
	public void setUsers(ArrayList<MultiUserInfo> users){
		users.clear();
		this.users.addAll(users);
	}
	
	public MultiUserInfo getUser(int index){
		return users.get(index);
	}
	
	public ArrayList<MultiUserInfo> getAllUsers(){
		return users;
	}
	
	public void removeUser(int index){
		users.remove(index);
	}
	
	public int getUserCount(){
		return users.size();
	}
	
	private void setIcon(int id){
		iconId = id;
		for(MultiUserInfo i: users){
			i.setIcon(iconId);
		}
	}
	
	public int getIconId(){
		return iconId;
	}
	
	public String getGroup(){
		return group;
	}
}
