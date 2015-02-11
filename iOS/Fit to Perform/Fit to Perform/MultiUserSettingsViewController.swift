//
//  MultiUserSettingsViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 30/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class MultiUserSettingsViewController: UIViewController, UITableViewDataSource {
    
    let data = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring", "Changing Directions", "Chase", "Even or Vowel", "Finger Tap", "Monkey Ladder", "One Card Learning", "Pattern Recreation", "Stroop"]
    var tests: [Int] = [0,0,0,0,0,0,0,0,0,0,0]
    
    var userID: Int?
    var userName: String?
    var groupName: String?
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var testTable: UITableView!
    
    @IBAction func saveClicked(sender: UIButton) {
        let settings = getSettings()
        save(settings)
        goBack()
    }
    
    @IBAction func backClicked(sender: UIBarButtonItem) {
        goBack()
    }
    
    @IBAction func indexChanged(sender: UISegmentedControl) {
        let row = sender.tag
        tests[row] = sender.selectedSegmentIndex
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.titleLabel?.text = userName! + " - Tests"
        loadData()
    }
    
    func loadData(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "MultiUserSettings")
        request.predicate = NSPredicate(format: "userId = %@", String(userID!))
        if let results = context.executeFetchRequest(request, error: nil) as? [MultiUserSettingsModel] {
            if results.count == 1{
                formatSettings(results[0].userSettings!)
            }
        }

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell: MultiTestCell = tableView.dequeueReusableCellWithIdentifier("MultiUserSettingsCell", forIndexPath: indexPath) as MultiTestCell
        let tag : Int = indexPath.row
        
        cell.titleLabel?.text = data[tag]
        cell.playableSwitch.selectedSegmentIndex = tests[tag]
        cell.playableSwitch.tag = tag
        return cell
        
    }
    
    func formatSettings(value: String){
        var array: [String] = value.componentsSeparatedByString("|")
        if array.count != 0{
            var i = 0
            for s: String in array {
                if countElements(s) == 1 {
                    tests[i] = s.toInt()!
                    ++i
                }
            }
        }
    }

    func getSettings() -> String{
        var tmp: String = ""
        for var i = 0; i < tests.count; ++i {
            tmp += String(tests[i]) + "|"
        }
        return tmp
    }
    
    func save(value: String){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "MultiUserSettings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "userId = %@", String(userID!))
        if let modeResults = context.executeFetchRequest(settings, error: nil) as? [MultiUserSettingsModel] {
            if modeResults.count != 0 {
                var object = modeResults[0]
                object.setValue(value, forKey: "userSettings")
                context.save(nil)
            }
        }
    }
    
    func goBack(){
        let vc : MultiUserListViewController = self.storyboard?.instantiateViewControllerWithIdentifier("MultiUserListView") as MultiUserListViewController
        vc.groupName = groupName
        self.showViewController(vc, sender: vc)
    }
}