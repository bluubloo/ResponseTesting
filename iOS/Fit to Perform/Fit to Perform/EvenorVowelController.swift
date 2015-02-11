//
//  EvenorVowelController.swift
//  Fit to Perform
//
//  Created by mja37 on 4/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class EvenorVowelController: UIViewController {
    
    var eventName: String?
    let maxTurns: Int = 10
    var turn: Int = 0
    var num: Int = 0
    var letter: String = ""
    var visibleLabel: Int = 0
    let vowels = ["A","E","I","O","U"]
    var results: [Double] = []
    var correct: Int = 0
    var time: NSDate = NSDate()

    @IBOutlet weak var infoView: UIView!
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var evenLabel: UILabel!
    @IBOutlet weak var vowelLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        
    }
    
    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        infoView.hidden = false
        changeText()
        
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "Click on Match if:\n - The top boxes number is even.\n - The bottom boxes letter is a vowel.\nOtherwise click on No Match."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func yesClicked(sender: UIButton){
        timeTaken()
        if visibleLabel == 0 {
            if isEven() {
                ++correct
            }
        } else {
            if isVowel() {
                ++correct
            }
        }
        next()
    }
    
    @IBAction func noClicked(sender: UIButton){
        timeTaken()
        if visibleLabel == 0 {
            if !isEven() {
                ++correct
            }
        } else {
            if !isVowel() {
                ++correct
            }
        }
        next()
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(9)) + 1
    }
    
    func randomLetter() -> String {
        let i = Int(arc4random_uniform(90 - 65)) + 65
        return String(UnicodeScalar(i))
    }

    func changeText(){
        num = randomNumber()
        letter = randomLetter()
        let text = String(num) + letter
        showText(text)
        time = NSDate()
    }
    
    func showText(text: String){
        visibleLabel = Int(arc4random_uniform(20)) % 2
        if visibleLabel == 0 {
            evenLabel.text = text
            vowelLabel.text = ""
        } else {
            evenLabel.text = ""
            vowelLabel.text = text
        }
    }

    func isEven() -> Bool {
        return (num % 2) == 0
    }
    
    func isVowel() -> Bool{
        for s: String in vowels{
            if s == letter {
                return true
            }
        }
        return false
    }

    func timeTaken(){
        let timeSince = NSDate().timeIntervalSinceDate(time)
        results += [Double(timeSince)]
    }
    
    func next(){
        ++turn
        if turn < maxTurns {
            changeText()
        } else {
            endTest()
        }
    }

    func endTest(){
        let average = getAverage()
        let correctString = String(correct)
        let value = correctString + "|" + average
        let r = Results()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        r.insertResults(eventName!, eventScore: value, appDel: appDel)
        
        let title = "Test Completed"
        let msg = "Your Score was: " + correctString + " correct.\nWith a average time of: " + average + "s"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    func getAverage() -> String {
        var tmp: Double = 0
        for r: Double in results{
            tmp += r
        }
        let average = tmp / Double(results.count)
        return String(format: "%.3f", average)
    }
}