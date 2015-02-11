//
//  QuestionnaireSleepController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class QuestionnaireSleepController: UIViewController {
    
    var eventName: String?
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var SoundView: UIView!
    @IBOutlet weak var lightView: UIView!
    
    @IBOutlet weak var restingHR: UITextField!
    @IBOutlet weak var soundHour: UITextField!
    @IBOutlet weak var soundMin: UITextField!
    @IBOutlet weak var lightMin: UITextField!
    @IBOutlet weak var lightHour: UITextField!
    @IBOutlet weak var totalMin: UITextField!
    @IBOutlet weak var totalHour: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        setupView()
    }
    
    func textField(textField: UITextField, shouldChangeCharactersInRange: NSRange, replacementString string: String) -> Bool {
        let inverseSet = NSCharacterSet(charactersInString: "0123456789").invertedSet
        let components = string.componentsSeparatedByCharactersInSet(inverseSet)
        let filtered = join("", components)
        return string == filtered
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "ToRatings" {
            let vc: QuestionnaireRatingsController = segue.destinationViewController as QuestionnaireRatingsController
            vc.eventName = eventName
            vc.soundSleep = getSoundSleep()
            vc.totalSleep = getTotalSleep()
            vc.lightSleep = getLightSleep()
            vc.restingHR = restingHR.text.toInt()!
        }
    }
    
    func getSoundSleep() -> Double{
        let hour = Double(soundHour.text.toInt()!)
        let min = Double(soundMin.text.toInt()!) / 60
        return hour + min
    }
    
    func getTotalSleep() -> Double{
        let hour = Double(totalHour.text.toInt()!)
        let min = Double(totalMin.text.toInt()!) / 60
        return hour + min
    }
    
    func getLightSleep() -> Double{
        let hour = Double(lightHour.text.toInt()!)
        let min = Double(lightMin.text.toInt()!) / 60
        return hour + min
    }
    
    func setupView(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let sleep = getSleep(context)
        if sleep == "1" {
            SoundView.hidden = true
            lightView.hidden = true
        }
    }
    
    func getSleep(context: NSManagedObjectContext) -> String {
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "Sleep")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel]{
            if results.count == 1 {
                return results[0].settingsValue!
            }
        }
        return "1"
    }
}