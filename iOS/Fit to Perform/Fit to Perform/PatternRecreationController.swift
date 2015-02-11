//
//  PatternRecreationController.swift
//  Fit to Perform
//
//  Created by mja37 on 9/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class PatternRecreationController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {
    var eventName: String?
    
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var grid: UICollectionView!
    
    var clickable = false
    var results: [Double] = []
    var time = NSDate()
    var timer = NSTimer()
    
    let maxTurns = 10
    var turn = 0
    let maxErrors = 3
    var errors = 0
    let minSquares = 3
    var maxSquares = 3
    var totalMax = 3
    var currentSquares = 0
    let maxRowsCols = 7
    var rows = 3
    var columns = 3
    
    var tiles: [Int] = []
    var tileVisible = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        initalizeTiles()
    }
    
    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        grid.hidden = false
        clickable = true
        show()
        let delay: Double = Double(maxSquares) * 0.5
        timer = NSTimer.scheduledTimerWithTimeInterval(delay, target: self, selector: Selector("tilesDisappear"), userInfo: nil, repeats: false)
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "Recreate the shown pattern as fast as you can.\nFor each pattern you can make 3 errors."
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
        let msg = "Your Score was: " + max + " tiles reached. " + average + "s average."
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
    
    func randomNumber(i: Int) -> Int {
        return Int(arc4random_uniform(UInt32(i)))
    }
    
    func initalizeTiles(){
        tiles = []
        let total = rows * columns
        for var i = 0; i < total; ++i {
            tiles += [0]
        }
    }
    
    func show(){
        initalizeTiles()
        tileVisible = true
        let positions = getPostions()
        for i: Int in positions {
            tiles[i] = 1
        }
        grid.reloadData()
    }
    
    func getPostions() -> [Int] {
        var tmp: [Int] = []
        let total = rows * columns
        for var i: Int = 0; i < maxSquares; ++i {
            var j = randomNumber(total)
            while !notIn(j, list: tmp) {
                j = randomNumber(total)
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
    
    func tilesDisappear(){
        tileVisible = false
        grid.reloadData()
        time = NSDate()
        clickable = true
    }
    
    func showNewGridData(){
        show()
        currentSquares = 0
        errors = 0 
        grid.hidden = false
        let delay: Double = Double(maxSquares) * 0.5
        timer = NSTimer.scheduledTimerWithTimeInterval(delay, target: self, selector: Selector("tilesDisappear"), userInfo: nil, repeats: false)
    }

    func error(i: Int){
        if errors < maxErrors - 1 {
            ++errors
            tiles[i] = 3
            grid.reloadData()
        } else {
            clickable = false
            timeTaken()
            if turn == maxTurns - 1 {
                endTest()
            } else {
                if maxSquares > minSquares {
                    maxSquares--
                }
                ++turn
                grid.hidden = true
                timer = NSTimer.scheduledTimerWithTimeInterval(0.5, target: self, selector: Selector("showNewGridData"), userInfo: nil, repeats: false)
            }
        }
    }
    
    func alterTile(i: Int){
        tiles[i] = 2
        ++currentSquares
        grid.reloadData()
    }
    
    func moveToNext(){
        clickable = false
        timeTaken()
        if turn < maxTurns - 1 {
            ++maxSquares
            if maxSquares > totalMax {
                totalMax = maxSquares
            }
            alterRowsCols()
            ++turn
            grid.hidden = true
            timer = NSTimer.scheduledTimerWithTimeInterval(0.5, target: self, selector: Selector("showNewGridData"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }

    func timeTaken(){
        let timeSince = NSDate().timeIntervalSinceDate(time)
        results += [Double(timeSince)]
    }

    func alterRowsCols() {
        if rows > columns {
            if columns != maxRowsCols{
                ++columns
            }
        } else {
            if rows != maxRowsCols {
                ++rows
            }
        }
    }
    
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return rows
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return columns
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = grid.dequeueReusableCellWithReuseIdentifier("PatternRecreationCell", forIndexPath: indexPath) as UICollectionViewCell
        let pos = indexPath.row + (indexPath.section * columns)
        cell.backgroundColor = UIColor.grayColor()
        let i = tiles[pos]
        cell.tag = i
        if (i == 1 && tileVisible) || i == 2 {
            cell.backgroundColor = UIColor.greenColor()
        } else if i == 3 {
            cell.backgroundColor = UIColor.redColor()
        }
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        if clickable {
            let pos = indexPath.row + (indexPath.section * columns)
            let tag = collectionView.cellForItemAtIndexPath(indexPath)?.tag
            if tag == 0 {
                error(pos)
            } else if tag == 1 {
                if currentSquares + 1 == maxSquares {
                    moveToNext()
                } else {
                    alterTile(pos)
                }
            }
        }
    }
}