//
//  Utils.swift
//  Fit to Perform
//
//  Created by mja37 on 9/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import CoreData
import UIKit

class Utils {
    
    func checkPlayable(list: [String], appDel: AppDelegate) -> [Bool] {
        var tmp: [Bool] = []
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let userId: String = getUserId(context)
        let userMode = getUserMode(context)
        let theme = getTheme(context)
        for var i = 0; i < list.count; ++i {
            if i == 0 {
                if userId != "-1" {
                    tmp += [checkQuestionnaire(userId, context: context)]
                } else {
                    tmp += [false]
                }
            } else {
                if  userMode == "0" {
                    let settings = getSingleModeSettings(context)
                    tmp += [checkSingleModeSettings(settings, item: list[i], theme: theme, context: context)]
                } else {
                    if userId == "-1" {
                        tmp += [false]
                    } else {
                        tmp += [checkMultiUserSettings(userId, item:list[i], theme: theme, context: context)]
                    }
                }
            }
        }
        return tmp
    }
    
    func checkQuestionnaire(userId: String, context: NSManagedObjectContext) -> Bool {
        var request = NSFetchRequest(entityName: "QuestionnaireResults")
        request.predicate = NSPredicate(format: "userId = %@", userId)
        if let results = context.executeFetchRequest(request, error: nil) as? [QuestionnaireResultsModel] {
            if results.count != 0 {
                let i = results.count - 1
                return !isSameDay(results[i])
            }
        }
        return true
    }
    
    func checkSingleModeSettings(settings: String, item: String, theme: String , context: NSManagedObjectContext) -> Bool {
        let parts: [String] = settings.componentsSeparatedByString("|")
        let i = settingPosition(item)
        if countElements(parts[i]) == 1 {
            return parts[i] == "0" && checkTime(item, theme: theme, context: context)
        }
        return true
    }
    
    func checkMultiUserSettings(userId: String, item: String, theme: String, context: NSManagedObjectContext) -> Bool {
        var settings = "0|0|0|0|0|0|0|0|0|0|0|"
        var request = NSFetchRequest(entityName: "MultiUserSettings")
        request.predicate = NSPredicate(format: "userId = %@", userId)
        if let results = context.executeFetchRequest(request, error: nil) as? [MultiUserSettingsModel] {
            if results.count == 1 {
                settings = results[0].userSettings!
            }
        }
        let parts: [String] = settings.componentsSeparatedByString("|")
        let i = settingPosition(item)
        if countElements(parts[i]) == 1 {
            return parts[i] == "0" && checkTime(userId, item: item, theme: theme, context: context)
        }
        return true
        
    }
    
    func settingPosition(item: String) -> Int {
        let data = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring Test", "Changing Directions", "Chase Test", "Even or Vowel",
            "Finger Tap Test", "Monkey Ladder", "One Card Learning Test", "Pattern Recreation", "Stroop Test"]
        for var i = 0; i < data.count; ++i {
            if data[i] == item {
                return i
            }
        }
        return 0
    }
    
    func checkTime(item: String, theme: String, context: NSManagedObjectContext) -> Bool {
        return checkTime("single", item: item, theme: theme, context: context)
    }
    
    func checkTime(userId: String, item: String, theme: String, context: NSManagedObjectContext) -> Bool {
        var request = NSFetchRequest(entityName: "EventResults")
        var pred1: NSPredicate = NSPredicate(format: "userId = %@", userId)!
        var pred2: NSPredicate = NSPredicate(format: "eventName = %@", item)!
        let compound: NSCompoundPredicate = NSCompoundPredicate.andPredicateWithSubpredicates([pred1, pred2])
        request.predicate = compound
        if let results = context.executeFetchRequest(request, error: nil) as? [EventResultsModel] {
            if results.count != 0 {
                let i = results.count - 1
                if isSameDay(results[i]) {
                    if theme != "Magic" {
                        if isAfternoon(results[i]) {
                            return true
                        }
                    }
                    
                    if isWithinTime(results[i]) {
                        if results[i].tries != 3 {
                            return true
                        }
                    }
                    return false
                }
            }
        }
         return true
    }

    func isWithinTime(result: EventResultsModel) -> Bool {
        let time = NSCalendar.currentCalendar()
        var parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute, fromDate: NSDate())
        let minR: Double = Double(result.minute) / 60
        let minC: Double = Double(parts.minute) / 60
        let totalR = Double(result.hour) + minR
        let totalC = Double(parts.hour) + minC
        let five: Double = 5 / 60
        return totalC - totalR < five
    }

    func isAfternoon(result: EventResultsModel) -> Bool {
        let time = NSCalendar.currentCalendar()
        var parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour, fromDate: NSDate())
        return result.hour < 12 && parts.hour >= 12
    }

    func isSameDay(result: EventResultsModel) -> Bool {
        let time = NSCalendar.currentCalendar()
        var parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitYear, fromDate: NSDate())
        return result.year == parts.year && result.month == parts.month && result.day == parts.day
    }
    
    func isSameDay(result: QuestionnaireResultsModel) -> Bool {
        let time = NSCalendar.currentCalendar()
        var parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitYear, fromDate: NSDate())
        return result.year == parts.year && result.month == parts.month && result.day == parts.day
    }
    
    func getUserId(appDel: AppDelegate) -> String {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        return getUserId(context)
    }
    
    func getUserId(context: NSManagedObjectContext) -> String {
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "userId")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel]{
            if results.count == 1 {
                return results[0].settingsValue!
            }
        }
        return "single"
    }
    
    func getUserMode(appDel: AppDelegate) -> String {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        return getUserMode(context)
    }
    
    func getUserMode(context: NSManagedObjectContext) -> String {
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "Mode")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel]{
            if results.count == 1 {
                return results[0].settingsValue!
            }
        }
        return "0"
    }
    
    func getTheme(appDel: AppDelegate) -> String {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        return getTheme(context)
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
    
    func getSingleModeSettings(appDel: AppDelegate) -> String {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        return getSingleModeSettings(context)
    }
    
    func getSingleModeSettings(context: NSManagedObjectContext) -> String {
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "singleTests")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel]{
            if results.count == 1 {
                return results[0].settingsValue!
            }
        }
        return "0|0|0|0|0|0|0|0|0|0|0|"
    }
    
    func getMailAddress(appDel: AppDelegate) -> [String] {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var tmp = ""
        let mode = getUserMode(context)
        var pred: NSPredicate
        if mode == "0" {
            pred = NSPredicate(format: "settingsName = %@", "singleEmailAddress")!
        } else {
             pred = NSPredicate(format: "settingsName = %@", "multiEmailAddress")!
        }
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = pred
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                tmp = results[0].settingsValue!
            } else {
                tmp = "activitytrackers@gmail.com"
            }
        } else {
             tmp = "activitytrackers@gmail.com"
        }
        return [tmp]
    }
    
    func getUserName(appDel:AppDelegate) -> String {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var tmp = ""
        let mode = getUserMode(context)
        var pred: NSPredicate
        if mode == "0" {
            pred = NSPredicate(format: "settingsName = %@", "singleUserName")!
        } else {
            pred = NSPredicate(format: "settingsName = %@", "userName")!
        }
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = pred
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                tmp = results[0].settingsValue!
            } else {
                tmp = getDefaultUserName(mode)
            }
        } else {
             tmp = getDefaultUserName(mode)
        }
        return tmp
    }
    
    func getSingleUserName(context: NSManagedObjectContext) -> String {
        var tmp = ""
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate = NSPredicate(format: "settingsName = %@", "singleUserName")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                tmp = results[0].settingsValue!
            }
        }
        if tmp == "" {
            tmp = getDefaultUserName("0")
        }
        return tmp
    }

    func getDefaultUserName(mode: String) -> String {
        if mode == "0" {
            return "Single"
        }
        return "Unknown"
    }
    
    func getUserNameMap(appDel: AppDelegate) -> [String: String] {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var tmp: [String: String] = ["single": getSingleUserName(context)]
        var request = NSFetchRequest(entityName: "MultiUserSettings")
        if let results: [MultiUserSettingsModel] = context.executeFetchRequest(request, error: nil) as? [MultiUserSettingsModel] {
            if results.count > 0 {
                for r in results {
                    tmp[r.userId!] = r.userName
                }
            }
        }
        return tmp
    }

    func updateSent(appDel: AppDelegate){
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "EventResults")
        let pred = NSPredicate(format: "sent = NO", "")
        request.predicate = pred
        if let results: [EventResultsModel] = context.executeFetchRequest(request, error: nil) as? [EventResultsModel] {
            if results.count > 0 {
                for r in results {
                    r.sent = true
                }
                context.save(nil)
            }
        }
        var qrequest = NSFetchRequest(entityName: "QuestionnaireResults")
        qrequest.predicate = pred
        if let results: [QuestionnaireResultsModel] = context.executeFetchRequest(qrequest, error: nil) as? [QuestionnaireResultsModel] {
            if results.count > 0 {
                for r in results {
                    r.sent = true
                }
                context.save(nil)
            }
        }
    }
    
    func checkReminder(appDel: AppDelegate) -> Bool {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate  = NSPredicate(format: "settingsName = %@", "Remind")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                return results[0].settingsValue! == "0"
            }
        }
        return false
    }

    func getReminderTime(appDel: AppDelegate) -> [Int] {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate  = NSPredicate(format: "settingsName = %@", "RemindTime")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                let time = results[0].settingsValue!
                let array = time.componentsSeparatedByString(":")
                if array.count == 2 {
                    return [array[0].toInt()!, array[1].toInt()!]
                }
            }
        }
        return [9,0]
    }
    
    func checkInstructions(appDel: AppDelegate) -> Bool {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "Settings")
        request.predicate  = NSPredicate(format: "settingsName = %@", "Instruct")
        if let results: [SettingsModel] = context.executeFetchRequest(request, error: nil) as? [SettingsModel] {
            if results.count == 1 {
                return results[0].settingsValue! == "0"
            }
        }
        return true
    }

    func getDate(time: [Int]) -> NSDate {
        let calendar = NSCalendar(calendarIdentifier: NSGregorianCalendar)
        calendar?.timeZone = NSTimeZone(abbreviation: "NZDT")!
        var parts: NSDateComponents = calendar!.components(NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitYear, fromDate: NSDate())
        return getDate(parts.year, month: parts.month, day: (parts.day + 1), hour: time[0], min: time[1])
    }
    
    func getDate(year: Int, month: Int, day: Int, hour: Int, min: Int) -> NSDate {
        var tmp: NSDateComponents = NSDateComponents()
        tmp.year = year
        tmp.month = month
        tmp.day = day
        tmp.minute = min
        tmp.hour = hour
        let seconds = 12 * 60 * 60
        tmp.timeZone = NSTimeZone(forSecondsFromGMT: seconds)
        let tmpDate = NSCalendar.currentCalendar().dateFromComponents(tmp)
        return tmpDate!
    }
    
    func getStringDate(parts: NSDateComponents) -> String {
        return getStringDate(parts.year, month: parts.month, day: parts.day, hour: parts.hour, min: parts.minute)
    }
    
    func getStringDate(year: Int, month: Int, day: Int, hour: Int, min: Int) -> String {
        var s = String(day) + "/" + String(month) + "/" + String(year)
        var min = String(min)
        if countElements(min) == 1 {
            min = "0" + min
        }
        s += " " + String(hour) + ":" + min
        return s
    }

    func goToTest(eventName: String, view: UIViewController, appDel: AppDelegate) {
        switch eventName {
        case "Appearing Object", "Appearing Object - Fixed Point":
                let vc : AppearingObjectController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewAppear") as AppearingObjectController
                vc.eventName = eventName
                view.showDetailViewController(vc, sender: vc)
        case "Finger Tap Test":
            let vc : FingerTapTestController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewFinger") as FingerTapTestController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        case "Even or Vowel":
                let vc : EvenorVowelController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewEvenVowel") as EvenorVowelController
                vc.eventName = eventName
                view.showDetailViewController(vc, sender: vc)
        case "Stroop Test":
                let vc : StroopTestController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewStroop") as StroopTestController
                vc.eventName = eventName
                view.showDetailViewController(vc, sender: vc)
        case "Changing Directions":
                let vc : ChangingDirectionsController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewDirections") as ChangingDirectionsController
                vc.eventName = eventName
                view.showDetailViewController(vc, sender: vc)
        case "One Card Learning Test":
            let vc : OneCardLearningController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewCardLearning") as OneCardLearningController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        case "Questionnaire":
            goToQuestionnaire(appDel, view: view, eventName: eventName)
        case "Arrow Ignoring Test":
            let vc : ArrowIgnoringController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewIgnoring") as ArrowIgnoringController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        case "Chase Test":
            let vc : ChaseController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewChase") as ChaseController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        case "Monkey Ladder":
            let vc : MonkeyLadderController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewMonkey") as MonkeyLadderController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        case "Pattern Recreation":
            let vc : PatternRecreationController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewPattern") as PatternRecreationController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        default:
            println(eventName)
        }
    }

    func goToQuestionnaire(appDel: AppDelegate, view: UIViewController, eventName: String){
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let theme = getTheme(context)
        if theme == "Magic" {
            let vc : QuestionnaireSleepController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewQuestionnaire") as QuestionnaireSleepController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        } else {
            let vc : QuestionnaireRatingsController = view.storyboard?.instantiateViewControllerWithIdentifier("EventViewRatings") as QuestionnaireRatingsController
            vc.eventName = eventName
            view.showDetailViewController(vc, sender: vc)
        }
    }
}