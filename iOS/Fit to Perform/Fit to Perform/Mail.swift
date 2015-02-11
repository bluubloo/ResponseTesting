//
//  Mail.swift
//  Fit to Perform
//
//  Created by mja37 on 10/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import MessageUI

class Mail: NSObject{
        
    func getMailTo(appDel: AppDelegate) -> [String]{
        let utils = Utils()
        return utils.getMailAddress(appDel)
    }
    
    func getFileName(appDel: AppDelegate) -> String {
        let utils = Utils()
        let userName = utils.getUserName(appDel)
        return userName + "_Results.csv"
    }
    
    func getFilePath(appDel: AppDelegate, fileName: String) -> String {
        let FileManager = NSFileManager.defaultManager()
        let directories: [String]? = NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory.DocumentDirectory, NSSearchPathDomainMask.AllDomainsMask, true) as? [String]
        if directories != nil {
            let dirs: [String] = directories!
            let dictionary = dirs[0]
            let path = dictionary.stringByAppendingPathComponent(fileName)
            return path
        }
        return ""
    }
    
    func getResultFileString(appDel: AppDelegate, all: Bool) -> NSMutableString {
        let utils = Utils()
        let results = Results()
        let theme = utils.getTheme(appDel)
        let eventResults: [EventResultsModel] = results.getEventResults(appDel, all: all)
        let questResults: [QuestionnaireResultsModel] = results.getQuestionnaireResults(appDel, all: all)
        if eventResults.count == 0 && questResults.count == 0 {
            showSendMailErrorAlertAllSent()
            return ""
        }
        let userNames: [String: String] = utils.getUserNameMap(appDel)
        if theme != "Magic" {
            return formatResults(eventResults, questions: questResults, nameMap: userNames)
        } else {
            return formatMagicResults(eventResults, questions: questResults, nameMap: userNames)
        }
    }
    
    func showSendMailErrorAlertAllSent(){
        let mailErrorAlert = UIAlertView(title: "Could Not Send Email", message: "All results have been sent before.", delegate: self, cancelButtonTitle: "OK")
        mailErrorAlert.show()
    }
    
    func formatResults(events: [EventResultsModel], questions: [QuestionnaireResultsModel], nameMap: [String: String]) -> NSMutableString {
        var s = NSMutableString()
        let utils = Utils()
        s.appendString("User Name,User Id,Event Name,Event Score,Tries,All Scores,Time\n")
        for e in events {
            let user = nameMap[e.userId!]
            let tries = String(e.tries)
            let time = utils.getStringDate(e.year, month: e.month, day: e.day, hour: e.hour, min: e.minute)
            let s1 = user! + "," + e.userId! + ","
            let s2 = e.eventName! + "," + e.eventScore! + ","
            let s3 = tries + "," + e.allScores! + "," + time + "\n"
            s.appendString(s1 + s2 + s3)
        }
        s.appendString("\n\nUser Name,User Id,Total Sleep,Light Sleep,Sound Sleep,Resting HR,Ratings,Time\n")
        for q in questions {
            let user = nameMap[q.userId!]
            let time = utils.getStringDate(q.year, month: q.month, day: q.day, hour: q.hour, min: q.minute)
            let sound = String(format: "%.2f", q.soundSleep)
            let light = String(format: "%.2f", q.lightSleep)
            let total = String(format: "%.2f", q.totalSleep)
            let hr = String(q.heartRate)
            let s1 = user! + "," + q.userId! + ","
            let s2 =  total + "," + light + "," + sound + ","
            let s3 = hr + "," + q.ratings! + "," + time + "\n"
            s.appendString(s1 + s2 + s3)
        }
        return s
    }
    
    func formatMagicResults(events: [EventResultsModel], questions: [QuestionnaireResultsModel], nameMap: [String: String]) -> NSMutableString {
        var s = NSMutableString()
        s.appendString("User Name,Time,")
        let eventNames = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring Test", "Changing Directions", "Chase Test", "Even or Vowel", "Finger Tap Test", "Monkey Ladder", "One Card Learning Test", "Pattern Recreation", "Stroop Test"]
        for tmp in eventNames {
            let s1 = tmp + ","
            s.appendString(s1)
        }
        s.appendString("Total Sleep,Light Sleep,Sound Sleep,Resting HR,Fatigue,Muscle Soreness,Mood,Sleep Quality\n")
        var results: [ResultsInfoModel] = []
        for e in events {
            let name = nameMap[e.userId!]
            let index = exists(name!, event: e, results: results)
            if index != -1 {
                results[index].setEventValues(e)
            } else {
                let resultInfo = ResultsInfoModel()
                resultInfo.setEventValues(name!, event: e)
                results += [resultInfo]
            }
        }
        
        for q in questions {
            let name = nameMap[q.userId!]
            let index = exists(name!, quest: q, results: results)
            if index != -1 {
                results[index].setQuestionnaireValues(q)
            } else {
                let resultInfo = ResultsInfoModel()
                resultInfo.setQuestionnaireValues(name!, question: q)
                results += [resultInfo]
            }
        }
        
        for r in results {
            s.appendString(r.getString())
        }

        return s
    }
    
    func exists(name: String, event: EventResultsModel, results: [ResultsInfoModel]) -> Int {
        var i = 0
        for r in results {
            if r.checkUserName(name) {
                if event.year == r.year && event.month == r.month && event.day == r.day {
                    return i
                }
            }
            ++i
        }
        return -1
    }
    
    func exists(name: String, quest: QuestionnaireResultsModel, results: [ResultsInfoModel]) -> Int {
        var i = 0
        for r in results {
            if r.checkUserName(name) {
                if quest.year == r.year && quest.month == r.month && quest.day == r.day {
                    return i
                }
            }
            ++i
        }
        return -1
    }
}