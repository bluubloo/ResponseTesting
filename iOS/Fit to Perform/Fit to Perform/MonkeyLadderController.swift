//
//  MonkeyLadderController.swift
//  Fit to Perform
//
//  Created by mja37 on 9/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class MonkeyLadderController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    var eventName: String?
    
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var grid: UICollectionView!

    var clickable = false
    var results: [Double] = []
    let maxTurns = 10
    var turn = 0
    let maxErrors = 3
    var errors = 0
    let minNumbers = 3
    var maxNumbers = 3
    var totalMax = 3
    var currentNumber = 0 
    
    var time = NSDate()
    var timer = NSTimer()
    
    var tiles: [Int] = []
    var numberVisible = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        initalizeTiles()
    }
    
    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        grid.hidden = false
        show()
        let delay: Double = Double(maxNumbers) * 0.5
        timer = NSTimer.scheduledTimerWithTimeInterval(delay, target: self, selector: Selector("textDisappear"), userInfo: nil, repeats: false)
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "You will see a 5x5 grid containing several green squares.\nEach square contains a number, memorise these.\n" +
            "After a short amount of time the numbers will disappear.\nClick on the green squares in sequential order.\n\n" +
            "Getting a error will set you back one square, 3 errors will end the test"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    func endTest(){
        let average = getAverage()
        let max = String(totalMax)
        let value = max + "|" + average
        let r = Results()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        r.insertResults(eventName!, eventScore: value, appDel: appDel)
        
        let title = "Test Completed"
        let msg = "Your Score was: " + max + " tiles reached. " + average + "s average"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    func getAverage() -> String {
        var tmp: Double = 0
        for i: Double in results {
            tmp += i
        }
        tmp /= Double(results.count)
        return String(format: "%.3f", tmp)
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(25))
    }
    
    func initalizeTiles(){
        tiles = []
        for var i = 0; i < 25; ++i {
            tiles += [-1]
        }
    }
    
    func show() {
        initalizeTiles()
        numberVisible = true
        let positions  = getPostions()
        var counter = 1
        for i: Int in positions {
            tiles[i] = counter
            ++counter
        }
        grid.reloadData()
    }
    
    func getPostions() -> [Int] {
        var tmp: [Int] = []
        for var i: Int = 0; i < maxNumbers; ++i {
            var j = randomNumber()
            while !notIn(j, list: tmp) {
                j = randomNumber()
            }
            tmp += [j]
        }
        return tmp
    }
    
    func notIn(j: Int, list: [Int]) -> Bool{
        for i: Int in list {
            if i == j {
                return false
            }
        }
        return true
    }

    func textDisappear() {
        numberVisible = false
        grid.reloadData()
        time = NSDate()
        clickable = true
    }
    
    func showNewGridData(){
        show()
        currentNumber = 0
        grid.hidden = false
        let delay: Double = Double(maxNumbers) * 0.5
        timer = NSTimer.scheduledTimerWithTimeInterval(delay, target: self, selector: Selector("textDisappear"), userInfo: nil, repeats: false)
    }
    
    func error() {
        clickable = false
        timeTaken()
        if errors == maxErrors - 1 || turn == maxTurns - 1 {
            endTest()
        } else {
            ++turn
            ++errors
            if maxNumbers > minNumbers {
                maxNumbers--
            }
            grid.hidden = true
            timer = NSTimer.scheduledTimerWithTimeInterval(0.5, target: self, selector: Selector("showNewGridData"), userInfo: nil, repeats: false)
        }
    }
    
    func moveToNext() {
        clickable = false
        timeTaken()
        if turn < maxTurns - 1 {
            ++maxNumbers
            if maxNumbers > totalMax {
                totalMax = maxNumbers
            }
            ++turn
            grid.hidden = true
            timer = NSTimer.scheduledTimerWithTimeInterval(0.5, target: self, selector: Selector("showNewGridData"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }
    
    func reloadGrid() {
        ++currentNumber
        grid.reloadData()
    }
    
    func timeTaken(){
        let timeSince = NSDate().timeIntervalSinceDate(time)
        results += [Double(timeSince)]
    }
    
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 5
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 5
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = grid.dequeueReusableCellWithReuseIdentifier("MonkeyLadderCell", forIndexPath: indexPath) as MonkeyLadderCell
        let pos = indexPath.row + (indexPath.section * 5)
        cell.numberLabel.text = ""
        cell.backgroundColor = UIColor.grayColor()
        cell.tag = tiles[pos]
        if tiles[pos] != -1 {
            if currentNumber < tiles[pos] {
                cell.backgroundColor = UIColor.greenColor()
            }
            if numberVisible {
                cell.numberLabel.text = String(tiles[pos])
            }
        }
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        if clickable {
            let pos = indexPath.row + (indexPath.section * 5)
            let tag = collectionView.cellForItemAtIndexPath(indexPath)?.tag
            if tag == -1 {
                error()
            } else {
                if tag == maxNumbers && maxNumbers == currentNumber + 1 {
                    moveToNext()
                } else if tag == currentNumber + 1 {
                    reloadGrid()
                } else {
                    error()
                }
            }
        }
    }
}