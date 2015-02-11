//
//  OneCardLearningController.swift
//  Fit to Perform
//
//  Created by mja37 on 5/02/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class OneCardLearningController: UIViewController {

    var eventName: String?
    
    let maxTurns: Int = 20
    var turn: Int = 0
    
    var correct: Int = 0

    @IBOutlet weak var infoView: UIView!
    @IBOutlet weak var startView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var card: UIImageView!
    
    let allCards = ["card_club_1", "card_club_2", "card_club_3", "card_club_4", "card_club_5", "card_club_6", "card_club_7", "card_club_8", "card_club_9", "card_club_10", "card_club_j", "card_club_q", "card_club_k",
                    "card_diam_1", "card_diam_2", "card_diam_3", "card_diam_4", "card_diam_5", "card_diam_6", "card_diam_7", "card_diam_8", "card_diam_9", "card_diam_10", "card_diam_j", "card_diam_q", "card_diam_k",
                    "card_hear_1", "card_hear_2", "card_hear_3", "card_hear_4", "card_hear_5", "card_hear_6", "card_hear_7", "card_hear_8", "card_hear_9", "card_hear_10", "card_hear_j", "card_hear_q", "card_hear_k",
                    "card_spad_1", "card_spad_2", "card_spad_3", "card_spad_4", "card_spad_5", "card_spad_6", "card_spad_7", "card_spad_8", "card_spad_9", "card_spad_10", "card_spad_j", "card_spad_q", "card_spad_k",
                    "card_joke_b", "card_joke_r"]
    var testCards: [Int] = []
    
    var timer: NSTimer = NSTimer()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        
    }
    
    @IBAction func startClicked(sender: UIButton){
        startView.hidden = true
        infoView.hidden = false
        setupCards()
        showImage()
    }
    
    @IBAction func instructClicked(sender: UIButton){
        let title = eventName! + " Instructions"
        let msg = "Click on Match if:\n - The card has been shown before.\n Otherwise click No Match."
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
    }
    
    @IBAction func yesClicked(sender: UIButton){
        if seenCard() {
            ++correct
        }
        next()
    }
    
    @IBAction func noClicked(sender: UIButton){
        if !seenCard() {
            ++correct
        }
        next()
    }

    func endTest(){
        timer.invalidate()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        let r = Results()
        r.insertResults(eventName!, eventScore: String(correct), appDel: appDel)
        
        let title = "Test Completed"
        let msg = "Your Score was: " + String(correct) + " correct"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabs")
            self.showDetailViewController(vc as UIViewController, sender: vc)
        }))
    }
    
    func randomNumber() -> Int {
        return Int(arc4random_uniform(UInt32(allCards.count)))
    }
    
    func setupCards(){
        for var i: Int = 0; i < maxTurns; ++i {
            testCards += [randomNumber()]
        }
    }

    func seenCard() -> Bool {
        for var i: Int = turn - 1; i >= 0; i-- {
            if testCards[turn] == testCards[i] {
                return true
            }
        }
        return false
    }
    
    func next(){
        ++turn
        if turn < maxTurns {
            card.hidden = true
            timer = NSTimer.scheduledTimerWithTimeInterval(0.75, target: self, selector: Selector("showImage"), userInfo: nil, repeats: false)
        } else {
            endTest()
        }
    }
    
    func showImage(){
        card.image = UIImage(named: allCards[testCards[turn]])
        card.hidden = false
    }
}