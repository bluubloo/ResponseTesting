package uni.apps.responsetesting.interfaces.listener;

/**
 * Interface for multi user mode group clicks
 * 
 * 
 * @author Mathew Andela
 *
 */
public interface MultiUserGroupListener {
	public void onAddUserClick(int position);
	public void onRemoveUsersClick(int position);
	public void onDeleteUser(int groupPosition, int childPosition);
	public void onMoveUser(int groupPosition, int childPosition);
}
