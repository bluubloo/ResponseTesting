//
//  QuestionnaireResultsModel.swift
//  Fit to Perform
//
//  Created by mja37 on 4/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import CoreData

class QuestionnaireResultsModel: NSManagedObject {
    @NSManaged var day: Int
    @NSManaged var month: Int
    @NSManaged var year: Int
    @NSManaged var hour: Int
    @NSManaged var minute: Int
    @NSManaged var userId: String?
    @NSManaged var ratings: String?
    @NSManaged var sent: Bool
    @NSManaged var totalSleep: Double
    @NSManaged var lightSleep: Double
    @NSManaged var soundSleep: Double
    @NSManaged var heartRate: Int
    
    
    class func createdInManagedObjectContext(moc: NSManagedObjectContext, total: Double, light: Double, sound: Double, id: String, hr: Int, ratings: String, time: NSCalendar) -> QuestionnaireResultsModel{
        let item = NSEntityDescription.insertNewObjectForEntityForName("QuestionnaireResults", inManagedObjectContext: moc) as QuestionnaireResultsModel
        item.totalSleep = total
        item.lightSleep = light
        item.soundSleep = sound
        item.userId = id
        item.sent = false
        item.heartRate = hr
        item.ratings = ratings
        var parts: NSDateComponents = time.components(NSCalendarUnit.CalendarUnitHour | NSCalendarUnit.CalendarUnitMinute | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitYear, fromDate: NSDate())
        item.day = parts.day
        item.month = parts.month
        item.year = parts.year
        item.hour = parts.hour
        item.minute = parts.minute
        println(item)
        return item
    }
    
}