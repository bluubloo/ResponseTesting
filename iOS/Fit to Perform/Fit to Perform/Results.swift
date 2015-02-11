//
//  Results.swift
//  Fit to Perform
//
//  Created by mja37 on 4/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import CoreData

class Results {
    
    func insertResults(eventName: String, eventScore: String, appDel: AppDelegate){
       
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let userId: String = getUserId(context)
        
        var request = NSFetchRequest(entityName: "EventResults")
        let p1: NSPredicate = NSPredicate(format: "userId = %@" , userId)!
        let p2: NSPredicate = NSPredicate(format: "eventName = %@", eventName)!
        let compound = NSCompoundPredicate.andPredicateWithSubpredicates([p1, p2])
        request.predicate = compound
        
        if let results: [EventResultsModel] = context.executeFetchRequest(request, error: nil) as? [EventResultsModel] {
            println("Results Count: " + String(results.count))
            if results.count != 0 {
                let i = results.count - 1
                let theme = getTheme(context)
                println(theme)
                if theme == "Magic" {
                    if sameDay(results[i]) {
                        if results[i].tries < 3 {
                            println("updating")
                            results[i].tries += 1
                            results[i].eventScore! = getBestScore(eventScore, oldScore: results[i].eventScore!)
                            results[i].allScores! += eventScore + "|"
                            context.save(nil)
                        }
                    } else {
                        insertResultsEvent(context, userId: userId, eventName: eventName, eventScore: eventScore)
                    }
                } else {
                    if sameDay(results[i]) {
                        if isNewResult(results[i]){
                             insertResultsEvent(context, userId: userId, eventName: eventName, eventScore: eventScore)
                        } else {
                            if results[i].tries < 3 {
                                println("updating")
                                results[i].tries += 1
                                results[i].eventScore! = getBestScore(eventScore, oldScore: results[i].eventScore!)
                                results[i].allScores! += eventScore + "|"
                                context.save(nil)
                            }
                        }
                    } else {
                        insertResultsEvent(context, userId: userId, eventName: eventName, eventScore: eventScore)
                    }
                }
            } else {
                insertResultsEvent(context, userId: userId, eventName: eventName, eventScore: eventScore)
            }
        }
    }
    
    func insertResultsEvent(context: NSManagedObjectContext, userId: String, eventName: String, eventScore: String){
        println("inserting")
        let allS = eventScore + "|"
        let results = EventResultsModel.createdInManagedObjectContext(context, name: eventName, score: eventScore, all: allS, id: userId, time: NSCalendar.currentCalendar())
        context.save(nil)
    }
    
    func insertQuestionnaire(totalSleep: Double, lightSleep: Double, soundSleep: Double, hr: Int, ratings: String, appDel: AppDelegate){
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        let userId: String = getUserId(context)
        
        var request = NSFetchRequest(entityName: "QuestionnaireResults")
        request.predicate = NSPredicate(format: "userId = %@" , userId)
        
        if let results: [QuestionnaireResultsModel] = context.executeFetchRequest(request, error: nil) as? [QuestionnaireResultsModel] {
            println("Results Count: " + String(results.count))
            if results.count != 0 {
                let i = results.count - 1
                if sameDay(results[i]) {
                    return
                }
            }
            insertQuestionnaireResults(context, totalSleep: totalSleep, lightSleep: lightSleep, soundSleep: soundSleep, hr: hr, ratings: ratings, userId : userId)
        }
        
    }
    
    func insertQuestionnaireResults(context: NSManagedObjectContext, totalSleep: Double, lightSleep: Double, soundSleep: Double, hr: Int, ratings: String, userId: String){
        println("inserting")
        let results = QuestionnaireResultsModel.createdInManagedObjectContext(context, total: totalSleep, light: lightSleep, sound: soundSleep, id: userId, hr: hr, ratings: ratings, time: NSCalendar.currentCalendar())
        context.save(nil)
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
    
    func getBestScore(eventScore: String, oldScore: String) -> String {
        if eventScore > oldScore{
            return eventScore
        }
        return oldScore
    }

    func sameDay(result: EventResultsModel) -> Bool {
        let time: NSCalendar = NSCalendar.currentCalendar()
        let parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitYear | NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay, fromDate: NSDate())
        return result.day == parts.day && result.month == parts.month && result.year == parts.year
    }
    
    func sameDay(result: QuestionnaireResultsModel) -> Bool {
        let time: NSCalendar = NSCalendar.currentCalendar()
        let parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitYear | NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay, fromDate: NSDate())
        return result.day == parts.day && result.month == parts.month && result.year == parts.year
    }
    
    func isMorning(hour: Int) -> Bool {
        return hour < 12
    }
    
    func isAfternoon(hour: Int) -> Bool{
        return hour >= 12
    }
    
    func isNewResult(result: EventResultsModel) -> Bool{
        let time: NSCalendar = NSCalendar.currentCalendar()
        let parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour, fromDate: NSDate())
        return isMorning(result.hour) && isAfternoon(parts.hour)
    }
    
    func getEventResults(appDel: AppDelegate, all: Bool) -> [EventResultsModel] {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "EventResults")
        if !all {
            request.predicate = NSPredicate(format: "sent = NO", "")
        }
        return context.executeFetchRequest(request, error: nil) as [EventResultsModel]
    }
    
    func getQuestionnaireResults(appDel: AppDelegate, all: Bool) -> [QuestionnaireResultsModel] {
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var request = NSFetchRequest(entityName: "QuestionnaireResults")
        if !all {
            request.predicate = NSPredicate(format: "sent = NO", "")
        }
        return context.executeFetchRequest(request, error: nil) as [QuestionnaireResultsModel]
    }

}