//
//  ChaseController.swift
//  Fit to Perform
//
//  Created by mja37 on 9/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class ChaseController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    var eventName: String?

    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var grid: UICollectionView!
    
    var timer: NSTimer = NSTimer()
    let maxTime = 30
    var timeTaken = 0
    var counter = 0
    
    var targetPos = 30
    var userPos = 5
    var results: [Int] = []
    var clickable = false

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        
    }
    

    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        grid.hidden = false
        clickable = true
        timer = NSTimer.scheduledTimerWithTimeInterval(1, target: self, selector: Selector("timerTick"), userInfo: nil, repeats: false)
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "You are the blue square.\n\nYou can move 1 square vertically or horiontally. (You cannot move diagonally)\n\nThe goal is to chase after the red square for 30s or till you cath them."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }

    func endTest(){
        timer.invalidate()
        let value = getAverage()
        let r = Results()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        r.insertResults(eventName!, eventScore: value, appDel: appDel)
        
        let title = "Test Completed"
        let msg = "Your Score was: " + value + " taps per second"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    func getAverage() -> String {
        var tmp: Double = 0
        for i: Int in results {
            tmp += Double(i)
        }
        tmp /= Double(results.count)
        return String(format: "%.3f", tmp)
    }

    func randomNumber(i: Int) -> Int {
        return Int(arc4random_uniform(UInt32(i)))
    }
    
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 6
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 6
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = grid.dequeueReusableCellWithReuseIdentifier("ChaseCell", forIndexPath: indexPath) as UICollectionViewCell
        let pos = indexPath.row + (indexPath.section * 6)
        if pos == userPos {
            cell.backgroundColor = UIColor.blueColor()
        } else if pos == targetPos {
            cell.backgroundColor = UIColor.redColor()
        } else {
            cell.backgroundColor = UIColor.grayColor()
        }
        
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        if clickable {
            let pos = indexPath.row + (indexPath.section * 6)
            if pos == targetPos {
                endTest()
            } else {
                if moveable(pos) {
                    ++counter
                    userPos = pos
                    moveToNext()
                }
            }
        }
    }
    
    func moveable(pos: Int) -> Bool {
        return pos == userPos - 1 || pos == userPos + 1 || pos == userPos - 6 || pos == userPos + 6
    }
    
    func moveToNext(){
        let newPos = [targetPos + 1, targetPos - 1, targetPos + 6, targetPos - 6]
        var moveable: [Bool] = []
        for i: Int in newPos {
            moveable += [moveableTarget(i)]
        }
        var moves: [Int] = []
        for var j = 0; j < moveable.count; ++j {
            if moveable[j] {
                moves += [newPos[j]]
            }
        }
        let k = randomNumber(moves.count)
        targetPos = moves[k]
        grid.reloadData()
    }
    
    func moveableTarget(i: Int) -> Bool {
        if i == userPos {
            return false
        }
        
        if targetPos == 0 {
            return i == 1 || i == 6
        } else if targetPos == 5 {
            return i == 5 || i == 11
        } else if targetPos == 30 {
            return i == 31 || i == 24
        } else if targetPos == 35 {
            return i == 34 || i == 29
        }
        
        if targetPos > 0 && targetPos < 5 {
            return i == targetPos + 1 || i == targetPos - 1 || i == targetPos + 6
        } else if targetPos > 30 && targetPos < 35 {
            return i == targetPos + 1 || i == targetPos - 1 || i == targetPos - 6
        }
        
        if targetPos % 6 == 0 {
            return i == targetPos + 6 || i == targetPos - 6 || i == targetPos + 1
        } else if targetPos % 6 == 5 {
            return i == targetPos + 6 || i == targetPos - 6 || i == targetPos - 1
        }
        
        return true
    }
    
    func timerTick() {
        results += [counter]
        counter = 0
        ++timeTaken
        if maxTime - timeTaken <= 0 {
            clickable = false
            endTest()
        } else {
            timer = NSTimer.scheduledTimerWithTimeInterval(1, target: self, selector: Selector("timerTick"), userInfo: nil, repeats: false)
        }
    }
    
}