//
//  EventViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 28/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import UIKit
import CoreData

class EventViewController: UIViewController {
    
    var eventName: String?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var instructionsLabel: UILabel!
    @IBOutlet weak var webView: UIWebView!
    @IBOutlet weak var swipeLabel: UILabel!
    

    let instructions = ["Tap on the object as soon as it appears", "Click on the arrow pointing in the same direction as the center arrow.\nIgnore all the others.", "Click on the outer arrow pointing in the same direction as the center arrow.",
        "You are the blue square.\n\nYou can move 1 square vertically or horiontally. (You cannot move diagonally)\n\nThe goal is to chase after the red square for 30s or till you cath them.",
        "Click on Match if:\n - The top boxes number is even.\n - The bottom boxes letter is a vowel.\nOtherwise click on No Match.", "Tap on the square as many time as possible in 5s",
        "You will see a 5x5 grid containing several green squares.\nEach square contains a number, memorise these.\nAfter a short amount of time the numbers will disappear.\nClick on the green squares in sequential order.\n\n" +
        "Getting a error will set you back one square, 3 errors will end the test", "Click on Match if:\n - The card has been shown before.\n Otherwise click No Match.",
        "Recreate the shown pattern as fast as you can.\nFor each pattern you can make 3 errors.", "Click on Match if:\n - The text colour of the top word matches the text meaning below.\nOtherwise click on No Match.",
        "Answer the questions as truthfully as possible.\n\nPlease enter your sleep durations and resting heart rate (If Known)\nFor the remaining questions give a rating from 0 to 5."]
    
    var images: [String] = []
    var image = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel.text = eventName
        setInstructions()
        setImages()
        var leftSwipe = UISwipeGestureRecognizer(target: self, action: Selector("handleSwipes:"))
        var rightSwipe = UISwipeGestureRecognizer(target: self, action: Selector("handleSwipes:"))
        leftSwipe.direction = UISwipeGestureRecognizerDirection.Left
        rightSwipe.direction = UISwipeGestureRecognizerDirection.Right
        self.view.addGestureRecognizer(leftSwipe)
        self.view.addGestureRecognizer(rightSwipe)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func nextClicked(sender: UIButton) {
        let utils = Utils()
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        utils.goToTest(eventName!, view: self, appDel: appDel)
    }
    
    func setInstructions(){
        var index = 0
        switch eventName! {
        case "Appearing Object", "Appearing Object - Fixed Point":
            index = 0
            break
        case "Arrow Ignoring Test":
            index = 1
            break
        case "Changing Directions":
            index = 2
            break
        case "Chase Test":
            index = 3
            break
        case "Even or Vowel":
            index = 4
            break
        case "Finger Tap Test":
            index = 5
            break
        case "Monkey Ladder":
            index = 6
            break
        case "One Card Learning Test":
            index = 7
            break
        case "Pattern Recreation":
            index = 8
            break
        case "Stroop Test":
            index = 9
            break
        case "Questionnaire":
            index = 10
            break
        default:
            break
        }
        instructionsLabel.text = instructions[index]
    }

    func setImages(){
        images = []
        image = 0
        switch eventName! {
        case "Appearing Object", "Appearing Object - Fixed Point":
            var filepath: String = NSBundle.mainBundle().pathForResource("appearingobject1", ofType: "gif")!
            images += [filepath]
            break
        case "Arrow Ignoring Test":
            var filepath: String = NSBundle.mainBundle().pathForResource("arrowignoringtest1", ofType: "gif")!
            images += [filepath]
            break
        case "Changing Directions":
            var filepath: String = NSBundle.mainBundle().pathForResource("changingdirections1", ofType: "gif")!
            var filepath2: String = NSBundle.mainBundle().pathForResource("changingdirections2", ofType: "gif")!
            images += [filepath, filepath2]
            break
        case "Chase Test":
            var filepath: String = NSBundle.mainBundle().pathForResource("chasetest1", ofType: "gif")!
            images += [filepath]
            break
        case "Even or Vowel":
            var filepath: String = NSBundle.mainBundle().pathForResource("evenorvowel1", ofType: "gif")!
            var filepath2: String = NSBundle.mainBundle().pathForResource("evenorvowel2", ofType: "gif")!
            images += [filepath, filepath2]
            break
        case "Finger Tap Test":
            var filepath: String = NSBundle.mainBundle().pathForResource("fingertaptest1", ofType: "gif")!
            images += [filepath]
            break
        case "Monkey Ladder":
            var filepath: String = NSBundle.mainBundle().pathForResource("monkeyladder1", ofType: "gif")!
            images += [filepath]
            break
        case "One Card Learning Test":
            var filepath: String = NSBundle.mainBundle().pathForResource("onecardlearningtest1", ofType: "gif")!
            images += [filepath]
            break
        case "Pattern Recreation":
            var filepath: String = NSBundle.mainBundle().pathForResource("patternrecreation1", ofType: "gif")!
            images += [filepath]
            break
        case "Stroop Test":
            var filepath: String = NSBundle.mainBundle().pathForResource("strooptest2", ofType: "gif")!
            var filepath2: String = NSBundle.mainBundle().pathForResource("strooptest1", ofType: "gif")!
            images += [filepath, filepath2]
            break
        case "Questionnaire":
            images += [""]
            break
        default:
            break
        }
        loadWebView()
        if images.count > 1 {
            swipeLabel.hidden = false
        }
    }
    
    func handleSwipes(sender: UISwipeGestureRecognizer) {
        if sender.direction == UISwipeGestureRecognizerDirection.Left {
            if images.count > 1 && image != images.count - 1{
                ++image
                loadWebView()
            }
        } else if sender.direction == UISwipeGestureRecognizerDirection.Right {
            if images.count > 1 && image != 0 {
                image--
                loadWebView()
            }
        }
    }
    
    func loadWebView(){
        var gif = NSData(contentsOfFile: images[image])
        if gif != nil {
            self.webView.reload()
            self.webView.loadData(gif, MIMEType: "image/gif", textEncodingName: nil, baseURL: nil)
            self.webView.userInteractionEnabled = false
        }
    }
    
}
