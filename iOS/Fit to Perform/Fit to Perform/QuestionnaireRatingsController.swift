//
//  QuestionnaireRatingsController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class QuestionnaireRatingsController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    var eventName: String?
    var totalSleep: Double = 0.0
    var lightSleep: Double = 0.0
    var soundSleep: Double = 0.0
    var restingHR: Int = 0
    var ratings = [0,0,0,0]
    
    @IBOutlet weak var backButton: UIBarButtonItem!
    @IBOutlet weak var titleLabel: UILabel!

    var theme = "Default"
    let magicQuestions = ["How would you rate your current fatigue level?", "How would you rate your current level of muscle soreness?", "How would you rate your overall mood?", "How would you rate your sleep quality last night?"]
    let otherQuestions = ["How would you rate your current fatigue level?", "How would you rate your current level of alertness?", "How would you rate your overall mood?", "How would you rate your sleep quality last night?"]

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        setupNavigation()
    }
    @IBAction func backClicked(sender: UIBarButtonItem) {
        let tag = sender.tag
        if tag == 0 {
            let vc : QuestionnaireSleepController = self.storyboard?.instantiateViewControllerWithIdentifier("EventViewQuestionnaire") as QuestionnaireSleepController
            vc.eventName = eventName
            self.showDetailViewController(vc as UIViewController, sender: vc)
        } else {
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }
    }
    
    @IBAction func saveClicked(sender: UIButton) {
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        let ratingsString = formatRatings()
        let r = Results()
        r.insertQuestionnaire(totalSleep, lightSleep: lightSleep, soundSleep: soundSleep, hr: restingHR, ratings: ratingsString, appDel: appDel)

        let title = "Questionnaire Completed"
        let msg = "You will now be returned to the Main Menu."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    @IBAction func indexChanged(sender: UISegmentedControl) {
        let tag = sender.tag
        ratings[tag] = sender.selectedSegmentIndex
    }
    
    func setupNavigation(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        theme = getTheme(context)
        if theme == "Magic" {
            backButton.tag = 0
        } else {
            backButton.tag = 1
        }
    }
    
    func getTheme(context: NSManagedObjectContext) -> String {
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "Theme")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel]{
            if results.count == 1 {
                return results[0].settingsValue!
            }
        }
        return "Default"
    }
    
    func formatRatings() -> String{
        var tmp = ""
        for i: Int in ratings {
            tmp += String(i) + "|"
        }
        return tmp
    }

    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return magicQuestions.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell: QuestionnaireRatingsCell = tableView.dequeueReusableCellWithIdentifier("RatingsCell") as QuestionnaireRatingsCell
        let row = indexPath.row
        var question = ""
        if theme == "Magic" {
            question = magicQuestions[row]
        } else {
            question = otherQuestions[row]
        }
        
        cell.questionLabel?.text = question
        cell.ratingSwitch.tag = row
        
        if row > 1 {
            cell.leftLabel?.text = "Very Poor"
            cell.rightLabel?.text = "Very Good"
        }
        
        return cell
        
    }

}