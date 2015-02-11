//
//  FirstViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 27/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import UIKit

class AttentionViewController: UIViewController, UITableViewDataSource {
    

    let attentionData = ["Questionnaire", "Arrow Ignoring Test", "Changing Directions", "Even or Vowel", "Stroop Test"]
    var data: [String] = []
    
    @IBOutlet weak var attentionTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        setUpList()
        setUpReminders()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("MainMenu") as UITableViewCell
        
        cell.textLabel?.text = data[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let utils = Utils()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        if utils.checkInstructions(appDel) {
            let vc : EventViewController = self.storyboard?.instantiateViewControllerWithIdentifier("InstructionsView") as EventViewController
            vc.eventName = data[indexPath.row]
            self.showDetailViewController(vc as UIViewController, sender: vc)
        } else {
            utils.goToTest(data[indexPath.row], view: self, appDel: appDel)
        }
    }
    
    func setUpList() {
        data = []
        let utils = Utils()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        let playable = utils.checkPlayable(attentionData, appDel: appDel)
        for var i = 0; i < playable.count; ++i {
            if playable[i] {
                data += [attentionData[i]]
            }
        }
         attentionTableView.reloadData()
    }

    func setUpReminders(){
        let utils = Utils()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        if utils.checkReminder(appDel) {
            let time = utils.getReminderTime(appDel)
            if time.count == 2 {
                let date = utils.getDate(time)
                var localNotification: UILocalNotification = UILocalNotification()
                localNotification.alertAction = "Reminder"
                localNotification.alertBody = "Remember to do the tests in this app"
                localNotification.fireDate = date
                localNotification.alertLaunchImage = "uni_logo"
                UIApplication.sharedApplication().scheduleLocalNotification(localNotification)
            }
        }
    }

}

