//
//  AppearingObjectController.swift
//  Fit to Perform
//
//  Created by mja37 on 4/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class AppearingObjectController: UIViewController {

    var eventName: String?
    var results: [Double] = []
    var time: NSDate = NSDate()
    var count = 1
    let maxTurns = 5
    var fixed = false
    var timer: NSTimer = NSTimer()
    
    @IBOutlet weak var infoView: UIView!
    @IBOutlet weak var titlelabels: UILabel!
    @IBOutlet weak var topLeft: UIButton!
    @IBOutlet weak var bottomLeft: UIButton!
    @IBOutlet weak var bottomRight: UIButton!
    @IBOutlet weak var center: UIButton!
    @IBOutlet weak var topRight: UIButton!
    
   override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titlelabels.text = eventName
        if eventName == "Appearing Object - Fixed Point" {
            fixed = true
        }
        hideButtons()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func instructClicked(sender: UIButton) {
        let title = eventName! + " Instructions"
        let msg = "Tap on the object as soon as it appears"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func startClicked(sender: UIButton) {
        infoView.hidden = true
        timer = NSTimer.scheduledTimerWithTimeInterval(randomNumberDouble(), target: self, selector: Selector("show"), userInfo: nil, repeats: false)
    }
    
    @IBAction func clicked(sender: UIButton) {
        let timeSince = NSDate().timeIntervalSinceDate(time)
        results += [Double(timeSince)]
        hideButtons()
        if count < maxTurns{
            ++count
            timer = NSTimer.scheduledTimerWithTimeInterval(randomNumberDouble(), target: self, selector: Selector("show"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }
    
    func show(){
        if !fixed {
            let i = randomNumber()
            showButton(i)
        } else {
            center.hidden = false
        }
        time = NSDate()
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(5))
    }
    
    func randomNumberDouble() -> Double {
        return Double(randomNumber()) + 0.5
    }
    
    func showButton(i: Int){
        switch i {
        case 0:
            topLeft.hidden = false
        case 1:
            topRight.hidden = false
        case 2:
            center.hidden = false
        case 3:
            bottomLeft.hidden = false
        case 4:
            bottomRight.hidden = false
        default:
            println("Error Occured")
        }
    }
    
    func hideButtons(){
        topLeft.hidden = true
        topRight.hidden = true
        center.hidden = true
        bottomLeft.hidden = true
        bottomRight.hidden = true
    }
    
    func endTest(){
        timer.invalidate()
        let average = getAverage()
        let value = formatScore()
        let r = Results()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        r.insertResults(eventName!, eventScore: value, appDel: appDel)
        
        let title = "Test Completed"
        let msg = "Your Score was: " + String(format: "%.3f", average) + "s average"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    func getAverage() -> Double {
        var tmp: Double = 0
        for r: Double in results{
            tmp += r
        }
        return (tmp / Double(results.count))
    }
    
    func formatScore() -> String {
        var s = ""
        for r: Double in results{
            let tmp = String(format: "%.3f", r)
            s += tmp + "|"
        }
        return s
    }
}
