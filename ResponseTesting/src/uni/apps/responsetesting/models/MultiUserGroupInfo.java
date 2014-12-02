package uni.apps.responsetesting.models;

import java.util.ArrayList;

import uni.apps.responsetesting.R;

public class MultiUserGroupInfo {

	private String group;
	private ArrayList<MultiUserInfo> users;
	private int iconId;
	
	public MultiUserGroupInfo(String group){
		this.group = group;
		users = new ArrayList<MultiUserInfo>();
		setIcon();
	}
	
	public void addUser(MultiUserInfo user){
		user.setGroup(group);
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
	
	private void setIcon(){
		switch(group){
		default:
			iconId = R.drawable.uni_logo;
			break;
		}
	}
	
	public int getIconId(){
		return iconId;
	}
	
	public String getGroup(){
		return group;
	}
}
