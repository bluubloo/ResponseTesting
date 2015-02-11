//
//  ArrowIgnoringController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class ArrowIgnoringController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    
    var eventName: String?
    
    let maxTurns: Int = 10
    var turn: Int = 0
    
    var results: [Double] = []
    var correct: Int = 0
    var time: NSDate = NSDate()
    
    @IBOutlet weak var buttonView: UIView!
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var grid: UICollectionView!
    
    let arrows = ["green_arrow_down","green_arrow_right","green_arrow_left", "green_arrow_up", "blue_arrow_down","blue_arrow_right","blue_arrow_left",
        "blue_arrow_up", "orange_arrow_down","orange_arrow_right","orange_arrow_left", "orange_arrow_up"]
    let tags = [0,2,3,1]
    var order: [Int] = []
    var centerIndex = 0
    
    var timer: NSTimer = NSTimer()
    var clickable = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        initalizeImages()
    }
    
    @IBAction func arrowClicked(sender: UIButton) {
        if clickable {
            clickable = false
            timeTaken()
            if isCorrect(sender.tag) {
                ++correct
            }
            next()
        }
    }
    
    @IBAction func instructClicked(sender: UIButton) {
        let title = eventName! + " Instructions"
        let msg = "Click on the arrow pointing in the same direction as the center arrow.\nIgnore all the others."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func startClicked(sender: UIButton) {
        startView.hidden = true
        buttonView.hidden = false
        show()
        grid.reloadData()
    }

    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(UInt32(arrows.count)))
    }

    func randomNumberPos() -> Int {
        return Int(arc4random_uniform(25))
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
            timer = NSTimer.scheduledTimerWithTimeInterval(0.75, target: self, selector: Selector("show"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }
    
    func show(){
        initalizeImages()
        showImages()
        clickable = true
        time = NSDate()
    }

    func initalizeImages(){
        order = []
        for var i: Int = 0; i < 25; ++i {
            order += [-1]
        }
    }
    
    func showImages(){
        let pos: [Int] = getPositions()
        for i: Int in pos {
            order[i] = randomNumber()
        }
        centerIndex = randomNumber()
        order[12] = centerIndex
        grid.hidden = false
        grid.reloadData()
    }

    func getPositions() -> [Int] {
        var tmp: [Int] = []
        for var i: Int = 0; i < 4; ++i {
            var j = randomNumberPos()
            while !notIn(j, list: tmp) {
                j = randomNumberPos()
            }
            tmp += [j]
        }
        return tmp
    }

    func notIn(j: Int, list: [Int]) -> Bool{
        if j == 12 {
            return false
        }

        for i: Int in list {
            if i == j {
                return false
            }
        }
        return true
    }
    
    func hideImages(){
        grid.hidden = true
    }
    
    func isCorrect(arrow: Int) -> Bool{
        let j = centerIndex % 4
        return arrow == tags[j]
    }

    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 5
    }

    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 5
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        //let cell = grid.dequeueReusableCellWithIdentifier("ArrowIgnoringCell", forIndexPath: indexPath) as UICollectionViewCell
        let cell: ArrowIgnoringCell = grid.dequeueReusableCellWithReuseIdentifier("ArrowIgnoringCell", forIndexPath: indexPath) as ArrowIgnoringCell
        let pos = indexPath.row + (indexPath.section * 5)
        let j = order[pos]
        if j != -1 {
            cell.imageView.image = UIImage(named: arrows[j])
        } else {
            cell.imageView.image = nil
        }
        return cell
    }
}