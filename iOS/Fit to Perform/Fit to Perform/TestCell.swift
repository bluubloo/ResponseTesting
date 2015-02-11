//
//  File.swift
//  Fit to Perform
//
//  Created by mja37 on 29/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit

class TestCell: UITableViewCell {    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var playableSwitch: UISegmentedControl!
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!){
        super.init(style : style, reuseIdentifier: reuseIdentifier)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}