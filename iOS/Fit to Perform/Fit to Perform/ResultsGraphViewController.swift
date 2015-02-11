//
//  ResultsGraphViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class ResultsGraphViewController: UIViewController {
    
    var eventName: String?
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var graphView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        titleLabel?.text = eventName
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}