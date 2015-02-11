//
//  MultiUserSettingsModel.swift
//  Fit to Perform
//
//  Created by mja37 on 2/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import CoreData

class MultiUserSettingsModel: NSManagedObject {

    @NSManaged var userGroup: String?
    @NSManaged var userName: String?
    @NSManaged var userSettings: String?
    @NSManaged var userId: String?
    
    
    class func createdInManagedObjectContext(moc: NSManagedObjectContext, group: String, name: String, settings: String, id: String) -> MultiUserSettingsModel{
        let item = NSEntityDescription.insertNewObjectForEntityForName("MultiUserSettings", inManagedObjectContext: moc) as MultiUserSettingsModel
        item.userGroup = group
        item.userName = name
        item.userId = id
        item.userSettings = settings
        return item
    }
}
