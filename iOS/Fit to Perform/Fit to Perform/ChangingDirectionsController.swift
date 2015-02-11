//
//  ChangingDirectionsController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class ChangingDirectionsController: UIViewController {
    
    var eventName: String?
    
    let maxTurns: Int = 10
    var turn: Int = 0
    
    var results: [Double] = []
    var correct: Int = 0
    var time: NSDate = NSDate()
    
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var topLeft: UIButton!
    @IBOutlet weak var bottomLeft: UIButton!
    @IBOutlet weak var bottomRight: UIButton!
    @IBOutlet weak var topRight: UIButton!
    @IBOutlet weak var center: UIImageView!
    
    let outerArrows = ["arrow_down", "arrow_right", "arrow_left", "arrow_up"]
    let centerArrows = ["green_arrow_down","green_arrow_right","green_arrow_left", "green_arrow_up"]
    var order = [0,1,2,3]
    var centerIndex = 0
    
    var timer: NSTimer = NSTimer()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        
    }
    
    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        showAll()
        time = NSDate()
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "Click on the outer arrow pointing in the same direction as the center arrow."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func arrowClicked(sender: UIButton){
        timeTaken()
        if sender.tag == centerIndex {
            ++correct
        }
        next()
    }

    func showAll(){
        shuffle()
        showImages()
    }
    
    func show(){
        if toShuffle() {
            shuffle()
        }
        showImages()
        enableImages()
        time = NSDate()
    }
    
    func showImages(){
        centerIndex = randomNumber()
        center.image = UIImage(named: centerArrows[centerIndex])
        center.hidden = false
        
        if toShuffle() {
            topLeft.setImage(UIImage(named: outerArrows[order[0]]), forState: UIControlState.Normal)
            topLeft.hidden = false
            topLeft.tag = order[0]
        
            topRight.setImage(UIImage(named: outerArrows[order[1]]), forState: UIControlState.Normal)
            topRight.hidden = false
            topRight.tag = order[1]
        
            bottomLeft.setImage(UIImage(named: outerArrows[order[2]]), forState: UIControlState.Normal)
            bottomLeft.hidden = false
            bottomLeft.tag = order[2]
        
            bottomRight.setImage(UIImage(named: outerArrows[order[3]]), forState: UIControlState.Normal)
            bottomRight.hidden = false
            bottomRight.tag = order[3]
        } else {
            bottomRight.hidden = false
            bottomLeft.hidden = false
            topRight.hidden = false
            topLeft.hidden = false
        }
    }
    
    func hideImages(){
        center.hidden = true
    }
    
    func disableImages(){
        bottomRight.enabled = false
        bottomLeft.enabled = false
        topRight.enabled = false
        topLeft.enabled = false
    }
    
    func enableImages(){
        bottomRight.enabled = true
        bottomLeft.enabled = true
        topRight.enabled = true
        topLeft.enabled = true
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(4))
    }

    func toShuffle() -> Bool{
        let shuffle = [0,3,5,6,8,9]
        for i: Int in shuffle {
            if i == turn {
                return true
            }
        }
        return false
    }
    
    func shuffle(){
        for var index = order.count - 1; index > 0; index-- {
            var j = Int(arc4random_uniform(UInt32(index-1)))
            let tmp1 = order[index]
            let tmp2 = order[j]
            order[index] = tmp2
            order[j] = tmp1
        }
    }

    func endTest(){
        timer.invalidate()
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
    
    func timeTaken(){
        let timeSince = NSDate().timeIntervalSinceDate(time)
        results += [Double(timeSince)]
    }
    
    func next(){
        ++turn
        if turn < maxTurns {
            hideImages()
            disableImages()
            timer = NSTimer.scheduledTimerWithTimeInterval(0.75, target: self, selector: Selector("show"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }

}