//
//  File.swift
//  Fit to Perform
//
//  Created by mja37 on 2/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class UserSelectionViewController: UITableViewController, UITableViewDataSource, UITableViewDelegate {
    
    var names: [String] = []
    var ids: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var groupsRequest = NSFetchRequest(entityName: "MultiUserSettings")
        groupsRequest.returnsObjectsAsFaults = false
        let results : [MultiUserSettingsModel] = context.executeFetchRequest(groupsRequest, error: nil) as [MultiUserSettingsModel]
        if results.count != 0 {
            for r: MultiUserSettingsModel in results {
                names += [r.userName!]
                ids += [r.userId!]
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return names.count
    }
    
   override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("UserSelectionCell") as UITableViewCell
        let tag : Int = indexPath.row
    
        cell.textLabel?.text = names[tag]

        return cell
        
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let name = names[indexPath.row]
        let id = ids[indexPath.row]
        save("userName", value: name)
        save("userId", value: id)
        let title = "User Selected"
        let msg = "User: " + name + " has been selected"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("OptionsView")
            self.showViewController(vc as UIViewController, sender: vc)
        }))
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
            }
        }
    }

}