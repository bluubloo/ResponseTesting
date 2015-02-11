//
//  SingleSetupViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class SingleSetupViewController: UIViewController {
    
    let data = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring", "Changing Directions", "Chase", "Even or Vowel", "Finger Tap", "Monkey Ladder", "One Card Learning", "Pattern Recreation", "Stroop"]
    var tests: [Int] = [0,0,0,0,0,0,0,0,0,0,0]
    var user = "Single"
    var email = "activitytrackers@gmail.com"
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var testTableView: UITableView!
    @IBOutlet weak var emailAddress: UITextField!
    @IBOutlet weak var userName: UITextField!

    
    @IBAction func changedIndex(sender: UISegmentedControl) {
        let row = sender.tag
        tests[row] = sender.selectedSegmentIndex
    }
    
    @IBAction func saveClicked(sender: UIButton) {
        let name = userName.text
        let email = emailAddress.text
        var test: String = getValues()
        storeValues(name, email: email,test: test)
        let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("OptionsView")
        self.showViewController(vc as UIViewController, sender: vc)
    }
    
    func storeValues(name: String, email: String, test: String){
        //TODO
        save("singleUserName", value: name)
        save("singleEmailAddress", value: email)
        save("singleTests", value: test)
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
    
    func getValues() -> String{
        var tmp: String = ""
        for var i = 0; i < tests.count; ++i {
           tmp += String(tests[i]) + "|"
        }
        return tmp
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.scrollView.scrollEnabled = true
        self.scrollView.contentSize = CGSizeMake(320, 1000)
        loadData()
        userName.text = user
        emailAddress.text = email
    }
    
    func loadData(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        
        settings.predicate = NSPredicate(format: "settingsName = %@", "singleUserName")
        setValues(context.executeFetchRequest(settings, error: nil)!, i: 0, name: "userName")
        settings.predicate = NSPredicate(format: "settingsName = %@", "singleEmailAddress")
        setValues(context.executeFetchRequest(settings, error: nil) as [SettingsModel], i: 1, name: "singleEmailAddress")
        settings.predicate = NSPredicate(format: "settingsName = %@", "singleTests")
        setValues(context.executeFetchRequest(settings, error: nil) as [SettingsModel], i: 2, name: "singleTests")
        
    }
    
    
   func setValues(results: NSArray, i: Int, name: String){
        if results.count == 1{
            let res = results[0] as NSManagedObject
            let tmp: String = res.valueForKey("settingsValue") as String
            switch i {
            case 0:
                user = tmp
            case 1:
                email = tmp
            case 2:
                formatSettings(tmp)
            default:
                println("Error occured for: " + name)
            }
        }
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell: TestCell = tableView.dequeueReusableCellWithIdentifier("SingleSetupCell", forIndexPath: indexPath) as TestCell
        
        cell.titleLabel?.text = data[indexPath.row]
        let test = tests[indexPath.row]
        cell.playableSwitch.selectedSegmentIndex = test
        cell.playableSwitch.tag = indexPath.row
        return cell
        
    }
}
