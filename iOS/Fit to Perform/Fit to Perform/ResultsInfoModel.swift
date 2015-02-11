//
//  ResultsInfoModel.swift
//  Fit to Perform
//
//  Created by mja37 on 10/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation

class ResultsInfoModel {
    
    var userName: String = ""
    var year: Int = 2015
    var month: Int = 1
    var day: Int = 1
    var date: String = ""
    var scores: [String] = ["","","","","","","","","","",""]
    var ratings: [String] = []
    var totalSleep: String = "0"
    var lightSleep: String = "0"
    var soundSleep: String = "0"
    var hr: String = "0"
    let eventNames = ["Appearing Object", "Appearing Object - Fixed Point", "Arrow Ignoring Test", "Changing Directions", "Chase Test", "Even or Vowel", "Finger Tap Test", "Monkey Ladder", "One Card Learning Test", "Pattern Recreation", "Stroop Test"]

    func setEventValues(name: String, event: EventResultsModel){
        userName = name
        year = event.year
        month = event.month
        day = event.day
        formatDate()
        setEventValues(event)
    }
    
    func setEventValues(event: EventResultsModel) {
        let i = indexOf(event.eventName!)
        scores[i] = event.eventScore!
    }

    func indexOf(event: String) -> Int {
        for var i = 0; i < eventNames.count; ++i {
            if eventNames[i] == event {
                return i
            }
        }
        return 0
    }
    
    func setQuestionnaireValues(name: String, question: QuestionnaireResultsModel) {
        userName = name
        year = question.year
        month = question.month
        day = question.day
        formatDate()
        setQuestionnaireValues(question)
    }
    
    func setQuestionnaireValues(question: QuestionnaireResultsModel) {
        hr = String(question.heartRate)
        totalSleep = String(format: "%.2f", question.totalSleep)
        soundSleep = String(format: "%.2f", question.soundSleep)
        lightSleep = String(format: "%.2f", question.lightSleep)
        ratings = formatRatings(question.ratings!)
    }

    func formatRatings(ratings: String) -> [String] {
        let array: [String] = ratings.componentsSeparatedByString("|")
        var tmp: [String] = []
        for s in array {
            if countElements(s) == 1 {
                tmp += [s]
            }
        }
        return tmp
    }
    
    func formatDate(){
        let utils = Utils()
        date = utils.getStringDate(year, month: month, day: day, hour: 0, min: 0)
    }
    
    func getString() -> String {
        var s: String = self.userName + "," + date + ","
        for tmp in scores {
                s += tmp + ","
        }
        s += self.totalSleep + "," + self.lightSleep + "," + self.soundSleep + "," + self.hr + ","
        for tmp in ratings {
            s += tmp + ","
        }
        return s + "\n"
    }
    
    func checkUserName(name: String) -> Bool {
        return self.userName == name
    }
    
    func getDate() -> [Int] {
        return [year, month, day]
    }

}