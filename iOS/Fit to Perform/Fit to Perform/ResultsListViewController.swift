//
//  ResultsListViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class ResultsListViewController: UIViewController, UITableViewDataSource {
    
    let data = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring", "Changing Directions", "Chase", "Even or Vowel", "Finger Tap", "Monkey Ladder", "One Card Learning", "Pattern Recreation", "Resting HR", "Sleep Duration", "Stroop"]
    
    @IBOutlet weak var resultsListTable: UITableView!
    
    @IBAction func deleteResults(sender: UIBarButtonItem) {
        
        let msg = "Are You sure you wish to delete all results?"
        let title = "Delete Results"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        self.presentViewController(alert, animated: true, completion: nil)
        
        alert.addAction(UIAlertAction(title: "All Results", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            self.deleteAllResults()
        }))
        alert.addAction(UIAlertAction(title: "Current User", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    func deleteAllResults(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "EventResults")
        if let results = context.executeFetchRequest(request, error: nil) as? [EventResultsModel] {
            if results.count != 0 {
                for item in results {
                    context.deleteObject(item)
                }
            }
        }
        var qrequest = NSFetchRequest(entityName: "QuestionnaireResults")
        if let qresults = context.executeFetchRequest(qrequest, error: nil) as? [QuestionnaireResultsModel] {
            if qresults.count != 0 {
                for item in qresults {
                    context.deleteObject(item)
                }
            }
        }
    }
    
    func deleteCurrentUserResults(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let userId = getCurrentUser(context)
        var request = NSFetchRequest(entityName: "EventResults")
        request.predicate = NSPredicate(format: "userId = %@", userId)
        if let results = context.executeFetchRequest(request, error: nil) as? [EventResultsModel] {
            if results.count != 0 {
                for item in results {
                    context.deleteObject(item)
                }
            }
        }
        var qrequest = NSFetchRequest(entityName: "QuestionnaireResults")
        qrequest.predicate = NSPredicate(format: "userId = %@", userId)
        if let qresults = context.executeFetchRequest(qrequest, error: nil) as? [QuestionnaireResultsModel] {
            if qresults.count != 0 {
                for item in qresults {
                    context.deleteObject(item)
                }
            }
        }

    }
    
    func getCurrentUser(context: NSManagedObjectContext) -> String {
        let utils = Utils()
        return utils.getUserId(context)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("ResultsListCell") as UITableViewCell
        
        cell.textLabel?.text = data[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let value = data[indexPath.row]
        let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("ResultsGraphView")
        let vc2: ResultsGraphViewController = vc as ResultsGraphViewController
        vc2.eventName = value
        self.showViewController(vc2, sender: vc)
    }
}