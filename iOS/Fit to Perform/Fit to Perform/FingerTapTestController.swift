//
//  FingerTapTestController.swift
//  Fit to Perform
//
//  Created by mja37 on 2/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class FingerTapTestController: UIViewController {
    var eventName: String?
    var count: Int = 0
    var running = false
    let countString = "Tap Count: "
    let timeString = "Time Left: "

    var timer: NSTimer = NSTimer()
    var timeDone: Double = 0
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var startButton: UIButton!
    @IBOutlet weak var instuctButton: UIButton!
    @IBOutlet weak var clickableButton: UIButton!
    @IBOutlet weak var countLabel: UILabel!
    @IBOutlet weak var infoView: UIView!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
    }
    
    @IBAction func clicked(sender: UIButton) {
        if clickableButton.enabled && running{
            if count == 0{
                clickableButton.setTitle("Keep on Tapping", forState: UIControlState.Normal)
                timer = NSTimer.scheduledTimerWithTimeInterval(0.1, target: self, selector: Selector("updateTime"), userInfo: nil, repeats: false)
            }
            ++count
            countLabel.text = countString + String(count)
        }
        
    }

    func updateTime() {
        timeDone += 0.1
        let timeLeft: Double = 5 - timeDone
        if timeLeft > 0 {
            var s = timeString + String(format: "%.1f:", timeLeft) + "s"
            timeLabel.text = s
            timer = NSTimer.scheduledTimerWithTimeInterval(0.1, target: self, selector: Selector("updateTime"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }

    }
    
    @IBAction func startClicked(sender: UIButton) {
        count = 0
        instuctButton.hidden = true
        startButton.hidden = true
        clickableButton.enabled = true
        clickableButton.setTitle("Tap Square to Start", forState: UIControlState.Normal)
        infoView.hidden = false
        running = true
    }
    
    @IBAction func instructClicked(sender: UIButton) {
        let title = eventName! + " Instructions"
        let msg = "Tap on the square as many time as possible in 5s"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    func endTest(){
        clickableButton.setTitle("", forState: UIControlState.Normal)
        clickableButton.enabled = false
        timer.invalidate()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        let r = Results()
        r.insertResults(eventName!, eventScore: String(count), appDel: appDel)

        let title = "Test Completed"
        let msg = "Your Score was: " + String(count) + " taps"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    



}