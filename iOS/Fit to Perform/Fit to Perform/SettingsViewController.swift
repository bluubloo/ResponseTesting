//
//  SettingsViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class SettingsViewController: UIViewController {
    
    var switchSettings: [Int] = [0,0,1,0,1]
    let themes = ["Default", "Magic", "Forestry"]
    var themeIndex: Int = 0
    var timeHour: Int = 9
    var timeMin: Int = 0
    
    @IBOutlet weak var timePicker: UIDatePicker!
    @IBOutlet weak var themePicker: UIPickerView!
    @IBOutlet weak var userMode: UISegmentedControl!
    @IBOutlet weak var reminderSwitch: UISegmentedControl!
    @IBOutlet weak var resultsSwitch: UISegmentedControl!
    @IBOutlet weak var sleepSwitch: UISegmentedControl!
    @IBOutlet weak var instructionsSwitch: UISegmentedControl!
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBAction func saveClicked(sender: UIButton) {
        saveData()
        let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("OptionsView")
        self.showViewController(vc as UIViewController, sender: vc)
    }
    
    func saveData(){
        
        var parts: NSDateComponents = timePicker.calendar.components(NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute, fromDate: timePicker.date)
        let hour = parts.hour
        let min = parts.minute
        let time = String(hour) + ":" + String(min)
        
        save("Mode", value: String(switchSettings[0]))
        save("Instruct", value: String(switchSettings[1]))
        save("Sleep", value: String(switchSettings[2]))
        save("Results", value: String(switchSettings[3]))
        save("Remind", value: String(switchSettings[4]))
        save("Theme", value: themes[themePicker.selectedRowInComponent(0)])
        save("RemindTime", value: time)
        
        if switchSettings[0] == 0 {
            save("userId", value: "single")
        } else {
            save("userId", value: "-1")
        }
        
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
    
    
    @IBAction func indexChangedReminder(sender: UISegmentedControl) {
     //   reminder = reminderSwitch.selectedSegmentIndex
        switchSettings[4] = reminderSwitch.selectedSegmentIndex
    }
    
    @IBAction func indexChangedResults(sender: UISegmentedControl) {
       // results = resultsSwitch.selectedSegmentIndex
        switchSettings[3] = resultsSwitch.selectedSegmentIndex
    }
    
    @IBAction func indexChangedSleep(sender: UISegmentedControl) {
      //  sleep = sleepSwitch.selectedSegmentIndex
        switchSettings[2] = sleepSwitch.selectedSegmentIndex
    }
    
    @IBAction func indexChangedInstructions(sender: UISegmentedControl) {
    //    instruct = instructionsSwitch.selectedSegmentIndex
        switchSettings[1] = instructionsSwitch.selectedSegmentIndex
    }
    
    @IBAction func indexChanged(sender: UISegmentedControl) {
      //  mode = userMode.selectedSegmentIndex
        switchSettings[0] = userMode.selectedSegmentIndex
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        loadData()
        userMode.selectedSegmentIndex = switchSettings[0]
        instructionsSwitch.selectedSegmentIndex = switchSettings[1]
        resultsSwitch.selectedSegmentIndex = switchSettings[3]
        reminderSwitch.selectedSegmentIndex = switchSettings[4]
        sleepSwitch.selectedSegmentIndex = switchSettings[2]
        themePicker.selectRow(themeIndex, inComponent: 0, animated: false)
        let calendar = NSCalendar(calendarIdentifier: NSGregorianCalendar)
        let date = calendar?.dateWithEra(0, year: 2015, month: 1, day: 1, hour: timeHour, minute: timeMin, second: 0, nanosecond: 0)
        timePicker.setDate(date!, animated: false)
        self.scrollView.contentSize = CGSizeMake(320, 720)
        self.scrollView.scrollEnabled = true
    }
    
    func loadData(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        
        settings.predicate = NSPredicate(format: "settingsName = %@", "Mode")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 0, name: "Mode")
        settings.predicate = NSPredicate(format: "settingsName = %@", "Instruct")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 1, name: "Instruct")
        settings.predicate = NSPredicate(format: "settingsName = %@", "Sleep")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 2, name: "Sleep")
        settings.predicate = NSPredicate(format: "settingsName = %@", "Results")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 3, name: "Results")
        settings.predicate = NSPredicate(format: "settingsName = %@", "Remind")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 4, name: "Remind")
        settings.predicate = NSPredicate(format: "settingsName = %@", "Theme")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 5, name: "Theme")
        settings.predicate = NSPredicate(format: "settingsName = %@", "RemindTime")
        setValue(context.executeFetchRequest(settings, error: nil)!, i: 6, name: "RemindTime")
        
    }
    
    func setValue(results: NSArray, i: Int, name: String){
        if(results.count > 0 && results.count == 1){
            var res = results[0] as NSManagedObject
            let tmp = res.valueForKey("settingsValue") as String
            if(i >= 0 && i <= 4){
                switchSettings[i] = tmp.toInt()!
            } else if(i == 5){
                var j = 0
                for s: String in themes{
                    if(s == tmp){
                        break
                    }
                    ++j
                }
                themeIndex = j
            } else if (i == 6){
                let time = tmp.componentsSeparatedByString(":")
                if(time.count == 2){
                    timeHour = time[0].toInt()!
                    timeMin = time[1].toInt()!
                } else{
                    timeHour = 9
                    timeMin = 0
                }
            
            }
        } else{
            println("0 Results or potential error in search for " + name)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent: Int) -> Int{
        return themes.count
    }
    
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String {
        return themes[row]
    }
}