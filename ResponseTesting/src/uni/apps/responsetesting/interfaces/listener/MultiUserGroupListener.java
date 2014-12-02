package uni.apps.responsetesting.interfaces.listener;

public interface MultiUserGroupListener {
	public void onAddUserClick(int position);
	public void onRemoveUsersClick(int position);
	public void onDeleteUser(int groupPosition, int childPosition);
	public void onMoveUser(int groupPosition, int childPosition);
}
