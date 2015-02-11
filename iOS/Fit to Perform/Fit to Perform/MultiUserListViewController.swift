//
//  MultiUserListViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class MultiUserListViewController: UIViewController, UITableViewDataSource, UIPickerViewDataSource, UIPickerViewDelegate{
    
    var names: [String] = []
    var ids: [Int] = []
    var groupName: String!
    var currentRow: Int = 0
    var selectedGroup: String = ""
    var groupNames: [String] = []
    
    
    @IBOutlet weak var userTable: UITableView!
    @IBOutlet weak var groupNameLabel: UILabel!
    
    @IBAction func deleteUser(sender: UIButton) {
        currentRow = sender.tag
        deleteAt()
    }
    
    func deleteAt(){
        let name = names[currentRow]
        let msg = "Are You sure you wish to delete user " + name + "?"
        let title = "Delete User"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        self.presentViewController(alert, animated: true, completion: nil)
        
        alert.addAction(UIAlertAction(title: "No", style: UIAlertActionStyle.Default, handler: nil))
        alert.addAction(UIAlertAction(title: "Yes", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let id = self.ids[self.currentRow]
            self.deleteUser(id)
            self.names.removeAtIndex(self.currentRow)
            self.ids.removeAtIndex(self.currentRow)
            self.userTable.deleteRowsAtIndexPaths([NSIndexPath(forRow: self.currentRow, inSection: 0)], withRowAnimation: UITableViewRowAnimation.Automatic)
        }))
    }
    
    @IBAction func moveGroup(sender: UIButton) {
        currentRow = sender.tag
        moveTo()
    }
    
    func moveTo(){
        let name = names[currentRow]
        
        let msg = "Move user " + name + " to?"
        let title = "Move User"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        alert.view.addConstraint(NSLayoutConstraint(item: alert.view, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 1, constant: 250))
        
        groupNames = getGroupNames()
        
        var frame: CGRect = CGRectMake(10, 60, 240, 100)
        var spinner: UIPickerView = UIPickerView(frame: frame)

        spinner.dataSource = self
        spinner.delegate = self
        alert.view.addSubview(spinner)
        
        
        self.presentViewController(alert, animated: true, completion: nil)
        
    
        alert.addAction(UIAlertAction(title: "No", style: UIAlertActionStyle.Default, handler: nil))
        alert.addAction(UIAlertAction(title: "Yes", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            
            self.selectedGroup = self.groupNames[spinner.selectedRowInComponent(0)]
            if(self.selectedGroup != "" && self.selectedGroup != self.groupName){
                println(self.selectedGroup)
                let id = self.ids[self.currentRow]
                self.save(id, value: self.selectedGroup)
                self.names.removeAtIndex(self.currentRow)
                self.ids.removeAtIndex(self.currentRow)
                self.userTable.deleteRowsAtIndexPaths([NSIndexPath(forRow: self.currentRow, inSection: 0)], withRowAnimation: UITableViewRowAnimation.Automatic)
            }else {
                println("Error or same group selected")
            }
        }))

        
    }
    
    func getGroupNames() -> [String]{
        var groups: [String] = ["Unassigned"]
        
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var groupsRequest = NSFetchRequest(entityName: "MultiUserSettings")
        groupsRequest.returnsObjectsAsFaults = false
        let results : [MultiUserSettingsModel] = context.executeFetchRequest(groupsRequest, error: nil) as [MultiUserSettingsModel]
        if results.count != 0 {
            for r: MultiUserSettingsModel in results {
                if doesntContain(r.userGroup!, groups: groups) {
                    groups += [r.userGroup!]
                }
            }
        }

        
        return groups
    }
    
    func doesntContain(group: String, groups: [String]) -> Bool {
        for s: String in groups{
            if s == group{
                return false
            }
        }
        return true
    }
    
    
    @IBAction func changeSettings(sender: UIButton) {
        let tag = sender.tag
        let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MultiUserSettingsView")
        let vc2 : MultiUserSettingsViewController = vc as MultiUserSettingsViewController
        vc2.userName = names[tag]
        vc2.userID = ids[tag]
        vc2.groupName = groupName
        self.showViewController(vc2, sender: vc)
    }
    
    @IBAction func addToList(sender: UIBarButtonItem) {
        let msg = "New User Name"
        let title = "Add User"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        self.presentViewController(alert, animated: true, completion: nil)
        
        alert.addTextFieldWithConfigurationHandler(nil)
        
        alert.addAction(UIAlertAction(title: "No", style: UIAlertActionStyle.Default, handler: nil))
        alert.addAction(UIAlertAction(title: "Yes", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let textfield = alert.textFields![0] as UITextField
            let name: String = textfield.text
            let id: Int = self.getNewId()
            self.addUser(name, id: id)
            self.names += [name]
            self.ids += [id]
            let row = self.names.count - 1
            self.userTable.insertRowsAtIndexPaths([NSIndexPath(forRow: row, inSection: 0)], withRowAnimation: UITableViewRowAnimation.Automatic)
        }))
    }
    
    func getNewId() -> Int{
        var max = 0
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "MultiUserSettings")
        if let results: [MultiUserSettingsModel] = context.executeFetchRequest(request, error: nil) as? [MultiUserSettingsModel]{
            if results.count != 0{
                for r: MultiUserSettingsModel in results {
                    if r.userId?.toInt() > max {
                        max = r.userId!.toInt()!
                    }
                }
            }
        }
        return max + 1
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        groupNameLabel?.text = groupName
        loadData()
    }
    
    func loadData(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var groupsRequest = NSFetchRequest(entityName: "MultiUserSettings")
        groupsRequest.returnsObjectsAsFaults = false
        groupsRequest.predicate = NSPredicate(format: "userGroup = %@", groupName)
        let results : [MultiUserSettingsModel] = context.executeFetchRequest(groupsRequest, error: nil) as [MultiUserSettingsModel]
        if results.count != 0 {
            for r: MultiUserSettingsModel in results {
                names += [r.userName!]
                let id: Int = r.userId!.toInt()!
                ids += [id]
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return names.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell: MultiUserCell = tableView.dequeueReusableCellWithIdentifier("MultiUserListCell") as MultiUserCell
        let tag : Int = indexPath.row
        
        cell.userNameLabel?.text = names[indexPath.row]
        cell.settingsButton?.tag = tag
        cell.moveButton?.tag = tag
        cell.deleteButton?.tag = tag
        
        return cell
        
    }
    
    func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
       return groupNames.count
    }
    
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String! {
        return groupNames[row]
    }
    
    func save(id: Int, value: String){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "MultiUserSettings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "userId = %@", String(id))
        if let modeResults = context.executeFetchRequest(settings, error: nil) as? [MultiUserSettingsModel] {
            if modeResults.count != 0 {
                var object = modeResults[0]
                object.setValue(value, forKey: "userGroup")
                context.save(nil)
            }
        }
    }
    
    func deleteUser(id: Int){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "MultiUserSettings")
        request.predicate = NSPredicate(format: "userId = %@", String(id))
        if let results = context.executeFetchRequest(request, error: nil) as? [MultiUserSettingsModel] {
            if results.count == 1{
                context.deleteObject(results[0])
            }
        }
    }
    
    func addUser(name: String, id: Int){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let newUser = MultiUserSettingsModel.createdInManagedObjectContext(context, group: groupName, name: name, settings: "0|0|0|0|0|0|0|0|0|0|0|", id: String(id))
        context.save(nil)
    }
    
    
}