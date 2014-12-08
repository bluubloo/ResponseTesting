package uni.apps.responsetesting.fragment.settings;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.GroupIconSpinnerAdapter;
import uni.apps.responsetesting.adapter.MultiUserGroupListAdapter;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MultiUserGroupListener;
import uni.apps.responsetesting.interfaces.listener.MultiUserSettingsListener;
import uni.apps.responsetesting.models.MultiUserGroupInfo;
import uni.apps.responsetesting.models.MultiUserInfo;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This fragment handles the management of groups and users in multi user mode
 * 
 * 
 * @author Mathew Andela
 *
 */
public class MultiUserGroupFragment extends Fragment implements MultiUserGroupListener {

	//variables
	private static final String TAG = "MultiUserGroupFragment";

	private ExpandableListView list;
	private ArrayList<MultiUserGroupInfo> groups;
	private MultiUserGroupListAdapter adapter;
	private MultiUserGroupListener listener;
	private MultiUserSettingsListener listListener;
	private TextView email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//set subtitle
		getActivity().getActionBar().setSubtitle(null);
	}

	//attachs listener to fragment
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (MultiUserGroupListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement MultiUserGroupListener");
		}
		try {
			listListener = (MultiUserSettingsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement MultiUserSettingsListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.multi_user_group_fragment, container, false);
		//set views
		list = (ExpandableListView) view.findViewById(R.id.multi_expand_list);
		email = (TextView) view.findViewById(R.id.multi_email);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		//get email
		String tmp = prefs.getString(getResources().getString(R.string.pref_key_multi_email),
				getResources().getString(R.string.setup_mode_default_email));
		email.setText(tmp);
		//set adapter
		setUpAdapter();
		setUpButtons(view);
		return view;
	}

	private void setUpButtons(View view) {
		Button setEmail = (Button) view.findViewById(R.id.multi_email_set);
		//set email button click
		setEmail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				updateEmail();
			}

		});
	}

	private void updateEmail(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Enter New Email");

		final EditText text = new EditText(getActivity());
		text.setInputType(InputType.TYPE_CLASS_TEXT);
		text.setText(email.getText());
		builder.setView(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//update email
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String tmp = text.getText().toString();
				email.setText(tmp);
				Editor editor = prefs.edit();
				editor.putString(getResources().getString(R.string.pref_key_multi_email), tmp);
				editor.commit();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		builder.show();
	}

	private void setUpAdapter() {		
		Cursor cursor = DatabaseHelper.getInstance(getActivity(), getResources()).getMultiUsers();
		//get groups
		groups = getGroups(cursor);
		//get users
		getNames(cursor);
		//set adpater
		resetAdapter();
		//set list click listener
		list.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				listListener.onGroupChildClick(groups.get(groupPosition).getUser(childPosition));
				return false;
			}

		});
	}

	//resets adapter
	//only way to update and expandableistview currently
	private void resetAdapter() {
		adapter = new MultiUserGroupListAdapter(groups, getActivity(), listener, listListener);
		list.setAdapter(adapter);
	}

	private void getNames(Cursor cursor) {
		//checks if groups and cursor is empty
		if(!groups.isEmpty()){
			if(cursor.moveToFirst()){
				do{
					//get user info
					MultiUserInfo tmp = new MultiUserInfo(cursor.getString(0), cursor.getString(1),
							cursor.getString(2));
					int index = getGroupIndex(tmp.getGroup());
					if(index == -1)
						index = groups.size() - 1;
					//add user
					groups.get(index).addUser(tmp);
				} while(cursor.moveToNext());				
			}
		}
	}

	//get group index value
	private int getGroupIndex(String group){
		for(int i = 0; i < groups.size(); i ++)
			if(groups.get(i).getGroup().equals(group))
				return i;
		return -1;
	}

	//gets group list
	private ArrayList<MultiUserGroupInfo> getGroups(Cursor cursor) {
		ArrayList<MultiUserGroupInfo> tmp = new ArrayList<MultiUserGroupInfo>();
		if(cursor.moveToFirst()){			
			do{
				//check if group exists
				//i f not add to list
				String s = cursor.getString(0);
				if(!containsGroup(s, tmp))
					tmp.add(new MultiUserGroupInfo(s, cursor.getInt(3)));
			} while(cursor.moveToNext());
		}		
		//check if unassigned group exists
		//add if not
		int j = -1;
		for(int i = 0; i < tmp.size(); i++)
			if(tmp.get(i).getGroup().equals("Unassigned"))
				j = i;
		if(j == -1)
			tmp.add(new MultiUserGroupInfo ("Unassigned"));
		return tmp;
	}
	
	//checks if list conatins group values
	private boolean containsGroup(String name, ArrayList<MultiUserGroupInfo> tmp){
		for(MultiUserGroupInfo i : tmp)
			if(i.getGroup().equals(name))
				return true;
		return false;
	}

	@Override
	public void onAddUserClick(int position) {
		//sets up dialog
		final int pos = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Enter Name");

		final EditText text = new EditText(getActivity());
		text.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//updates the notes
				DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
				String name = text.getText().toString();
				//checks if name exists
				if(!name.equals("single")){
					int id = db.checkUserName(name);
					if(id == -1){
						//gets new id
						id = db.getNewUserId();
						//creats new user 
						//adds to db and group list
						MultiUserInfo tmp = new MultiUserInfo(groups.get(pos).getGroup(), 
								name, Integer.toString(id), groups.get(pos).getIconId());
						add(tmp, db);
						groups.get(pos).addUser(tmp);
						//updates adapter
						resetAdapter();
					}
					else{
						ActivityUtilities.displayMessage(getActivity(), "Name Exists", 
								"The name " + name + " already exists. Please try again");
					}
				}else
					ActivityUtilities.displayMessage(getActivity(), "Name Exists", 
							"Use of the name 'single' is not allowed");
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//closes dialog
				dialog.cancel();

			}
		});
		builder.show();
	}

	private void add(MultiUserInfo tmp, DatabaseHelper db) {
		//adds user info to db
		ContentValues values = new ContentValues();
		Resources r = getResources();
		values.put(r.getString(R.string.user_id), tmp.getId());
		Log.d(TAG, tmp.getId());
		values.put(r.getString(R.string.user_group), tmp.getGroup());
		Log.d(TAG, tmp.getGroup());
		values.put(r.getString(R.string.user_name), tmp.getName());
		Log.d(TAG, tmp.getName());
		values.put(r.getString(R.string.user_settings), getDefaultSettings(r));
		Log.d(TAG, getDefaultSettings(r));
		values.put(r.getString(R.string.group_icon), tmp.getGroupIcon());
		Log.d(TAG, Integer.toString(tmp.getGroupIcon()));
		db.insertMultiSettings(values);

	}

	//gets default settings
	private String getDefaultSettings(Resources r) {
		int length = r.getStringArray(R.array.event_name_array).length - 1;
		String tmp = "";
		for(int i = 0; i < length - 1; i ++)
			tmp += "1|";
		return tmp + "1";
	}

	@Override
	public void onRemoveUsersClick(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteUser(int groupPosition, int childPosition) {
		//sets up dialog
		final int pos1 = groupPosition;
		final int pos2 = childPosition;
		new AlertDialog.Builder(getActivity())
		.setTitle("Delete User")
		.setMessage("Are you sure you want to delete - " + 
				groups.get(groupPosition).getUser(childPosition).getName() + "?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						//deletes user
						DatabaseHelper.getInstance(getActivity(), getResources()).
						removeMulitUser(groups.get(pos1).getUser(pos2).getId());
						groups.get(pos1).removeUser(pos2);
						resetAdapter();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
					}
				})
				.setIcon(android.R.drawable.ic_menu_info_details)
				.show();
	}

	@Override
	public void onMoveUser(int groupPosition, int childPosition) {
		final int pos1 = groupPosition;
		final int pos2 = childPosition;
		//sets up dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Enter Name");
		//set child view
		final Spinner chooser = new Spinner(getActivity());
		String[] groupNames = getGroupNames();

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), 
				android.R.layout.simple_spinner_item, groupNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooser.setAdapter(adapter);
		builder.setView(chooser);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//checks if group is not the same
				if(!chooser.getSelectedItem().equals(groups.get(pos1).getGroup())){
					//gets user
					MultiUserInfo tmp = groups.get(pos1).getUser(pos2);
					groups.get(pos1).removeUser(pos2);
					//moves to new group
					int index = getNewGroupIndex((String) chooser.getSelectedItem());
					Log.d(TAG, Integer.toString(index));
					if(index == -1)
						index = groups.size() - 1;
					groups.get(index).addUser(tmp);
					tmp = groups.get(index).getUser(groups.get(index).getAllUsers().size() - 1);
					//updates adapter and db
					updateDB(tmp, index);
					resetAdapter();
				} else
					dialog.cancel();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//closes dialog
				dialog.cancel();

			}
		});
		builder.show();
	}

//gets group names
	private String[] getGroupNames() {
		String[] tmp = new String[groups.size()];
		for(int i = 0; i < groups.size(); i ++)
			tmp[i] = groups.get(i).getGroup();
		return tmp;
	}

	//updates user info
	protected void updateDB(MultiUserInfo tmp, int index) {
		ContentValues values = new ContentValues();
		Resources r = getResources();
		values.put(r.getString(R.string.user_group), tmp.getGroup());
		values.put(r.getString(R.string.group_icon), tmp.getGroupIcon());
		DatabaseHelper.getInstance(getActivity(), r).updateMultiSettings(tmp.getId(), values);
	}

	//gets new group index
	private int getNewGroupIndex(String selectedItem) {
		for(int i = 0; i < groups.size(); i ++)
			if(groups.get(i).getGroup().equals(selectedItem))
				return i;

		return -1;
	}

	
	public void addNewGroup() {
		//sets up dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Enter Group Name");
		//creates view
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parent = inflater.inflate(R.layout.new_group_parent, null);
		final EditText text = (EditText) parent.findViewById(R.id.group_name);
		final Spinner icons = (Spinner) parent.findViewById(R.id.group_icons);
		icons.setAdapter(new GroupIconSpinnerAdapter(getActivity()));
		text.setInputType(InputType.TYPE_CLASS_TEXT);
		//sets view
		builder.setView(parent);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(getGroupIndex(text.getText().toString()) == -1){
					//gets icon id updates list
					int iconId = (int) icons.getSelectedItem();
					groups.add(new MultiUserGroupInfo(text.getText().toString(), iconId));
					resetAdapter();
				} else
					dialog.cancel();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//closes dialog
				dialog.cancel();

			}
		});
		builder.show();
	}
}
