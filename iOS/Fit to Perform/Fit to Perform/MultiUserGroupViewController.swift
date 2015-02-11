//
//  MultiUserGroupViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class MultiUserGroupViewController: UIViewController, UITableViewDataSource {
    
    var data: [String] = ["Unassigned"]
    var email = "activitytrackers@gmail.com"
    
    @IBOutlet weak var groupTable: UITableView!
    @IBOutlet weak var emailAddress: UITextField!
    
    @IBAction func saveEmailAddress(sender: UIButton) {
        email = emailAddress.text
        save("multiEmailAddress", value: email)
        let msg = "Email Address Saved"
        let title = "Setting Saved"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func addToList(sender: UIBarButtonItem) {
        let msg = "New Group Name"
        let title = "Add Group"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        self.presentViewController(alert, animated: true, completion: nil)
        
        alert.addTextFieldWithConfigurationHandler(nil)
        
        alert.addAction(UIAlertAction(title: "No", style: UIAlertActionStyle.Default, handler: nil))
        alert.addAction(UIAlertAction(title: "Yes", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let textfield = alert.textFields![0] as UITextField
            self.data += [textfield.text]
            let row = self.data.count - 1
            let path = NSIndexPath(forRow: row, inSection: 0)
            self.groupTable.insertRowsAtIndexPaths([path], withRowAnimation: UITableViewRowAnimation.Automatic)
            //self.groupTable.reloadData()
        }))
    }

    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        loadEmail()
        emailAddress.text = email
        loadGroups()
    }
    
    func loadEmail(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "settingsName = %@", "multiEmailAddress")
        let results : [SettingsModel] = context.executeFetchRequest(settings, error: nil) as [SettingsModel]
        if results.count == 1 {
           email = results[0].settingsValue!
        }
    }
    
    func loadGroups(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var groups = NSFetchRequest(entityName: "MultiUserSettings")
        groups.returnsObjectsAsFaults = false
        let results : [MultiUserSettingsModel] = context.executeFetchRequest(groups, error: nil) as [MultiUserSettingsModel]
        if results.count != 0 {
            for r: MultiUserSettingsModel in results {
                if doesntContain(r.userGroup!) {
                    data += [r.userGroup!]
                }
            }
        }

    }
    
    func doesntContain(group: String) -> Bool {
        for s: String in data{
            if s == group{
                return false
            }
        }
        return true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("MultiUserGroupCell") as UITableViewCell
        
        cell.textLabel?.text = data[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let value = data[indexPath.row]
        let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MultiUserListView")
        let vc2: MultiUserListViewController = vc as MultiUserListViewController
        vc2.groupName = value
        self.showViewController(vc2, sender: vc)
        
    }
    
    func save(name: String, value: String){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "settingsName = %@", name)
        if let modeResults = context.executeFetchRequest(settings, error: nil) as? [SettingsModel] {
            if modeResults.count != 0 {
                var object = modeResults[0]
                object.setValue(value, forKey: "settingsValue")
                context.save(nil)
            } else{
                var settings = SettingsModel.createdInManagedObjectContext(context, name: name, value: value)
                context.save(nil)
            }
        }
    }

    
}