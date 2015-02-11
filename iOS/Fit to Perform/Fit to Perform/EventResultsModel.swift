//
//  EventResultsModel.swift
//  Fit to Perform
//
//  Created by mja37 on 2/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import CoreData

class EventResultsModel: NSManagedObject {

    @NSManaged var eventName: String?
    @NSManaged var eventScore: String?
    @NSManaged var allScores: String?
    @NSManaged var userId: String?
    @NSManaged var sent: Bool
    @NSManaged var tries: Int
    @NSManaged var day: Int
    @NSManaged var month: Int
    @NSManaged var year: Int
    @NSManaged var hour: Int
    @NSManaged var minute: Int

    class func createdInManagedObjectContext(moc: NSManagedObjectContext, name: String, score: String, all: String, id: String, time: NSCalendar) -> EventResultsModel{
        let item = NSEntityDescription.insertNewObjectForEntityForName("EventResults", inManagedObjectContext: moc) as EventResultsModel
        item.eventName = name
        item.eventScore = score
        item.allScores = all
        item.userId = id
        item.sent = false
        item.tries = 1
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