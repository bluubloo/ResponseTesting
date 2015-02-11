//
//  StroopTestController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class StroopTestController: UIViewController {
    
    var eventName: String?
    
    let maxTurns: Int = 10
    var turn: Int = 0
    
    var results: [Double] = []
    var correct: Int = 0
    var time: NSDate = NSDate()
    
    let names = ["Black", "Red", "Blue", "Green", "Yellow", "Orange", "Purple"]
    let colours = [UIColor.blackColor(), UIColor.redColor(), UIColor.blueColor(), UIColor.greenColor(), UIColor.yellowColor(), UIColor.orangeColor(), UIColor.purpleColor()]
    var nameIndex = 0
    var colourIndex = 0
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var colourLabel: UILabel!
    @IBOutlet weak var wordLabel: UILabel!
    @IBOutlet weak var infoView: UIView!
    @IBOutlet weak var startView: UIView!
    
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
        let msg = "Click on Match if:\n - The text colour of the top word matches the text meaning below.\nOtherwise click on No Match."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func yesClicked(sender: UIButton){
       timeTaken()
        if nameIndex == colourIndex {
            ++correct
        }
        next()
    }
    
    @IBAction func noClicked(sender: UIButton){
        timeTaken()
        if nameIndex != colourIndex {
            ++correct
        }
        next()
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(7))
    }
    
    func changeText(){
        nameIndex = randomNumber()
        colourIndex = randomNumber()
        let i = randomNumber()
        let out = String(nameIndex) + " " + String(colourIndex) + " " + String(i)
        println(out)
        show(i)
        time = NSDate()
    }
    
    func show(i: Int){
        wordLabel.text = names[nameIndex]
        colourLabel.textColor = colours[colourIndex]
        colourLabel.text = names[i]
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