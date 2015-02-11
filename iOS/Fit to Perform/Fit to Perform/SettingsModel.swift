//
//  SettingsModel.swift
//  Fit to Perform
//
//  Created by mja37 on 30/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//
import Foundation
import CoreData

class SettingsModel: NSManagedObject {

    @NSManaged var settingsName: String?
    @NSManaged var settingsValue: String?
    
    class func createdInManagedObjectContext(moc: NSManagedObjectContext, name: String, value: String) -> SettingsModel{
        let item = NSEntityDescription.insertNewObjectForEntityForName("Settings", inManagedObjectContext: moc) as SettingsModel
        item.settingsName = name
        item.settingsValue = value
        return item
    }
    
}
