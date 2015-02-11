//
//  OptionsTableViewController.swift
//  Fit to Perform
//
//  Created by mja37 on 28/01/15.
//  Copyright (c) 2015 mja37. All rights reserved.
//

import Foundation
import UIKit
import CoreData
import MessageUI

class OptionsTableViewController: UIViewController, MFMailComposeViewControllerDelegate  {
    
    var data: [String] = ["Settings", "Setup Mode", "Send All Results", "Send Recent Results", "About"]
    var error = false
    @IBOutlet weak var optionsTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        loadString("Results", condition: "0", value: "Results")
        loadStringMode("Mode", condition: "1", value: "Select User")
    }
    
    func loadString(predicate: String, condition: String, value: String){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "settingsName = %@", predicate)
        if let results: [SettingsModel] = context.executeFetchRequest(settings, error: nil) as? [SettingsModel]{
            if results.count != 1 || results[0].settingsValue == condition {
                data += [value]
            }
        }
    }
    
    func loadStringMode(predicate: String, condition: String, value: String){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        settings.predicate = NSPredicate(format: "settingsName = %@", predicate)
        if let results: [SettingsModel] = context.executeFetchRequest(settings, error: nil) as? [SettingsModel]{
            if results.count != 0 && results[0].settingsValue == condition {
                data += [value]
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection: Int) -> Int {
        return data.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("OptionsCell") as UITableViewCell
        
        cell.textLabel?.text = data[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        //CODE TO BE RUN ON CELL TOUCH
        let index: Int = tableView.indexPathForSelectedRow()!.row
        let tmp: String = data[index]
        switch (tmp){
        case "Results":
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("ResultsListView")
            self.showViewController(vc as UIViewController, sender: vc)
        case "Settings":
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("SettingsView")
            self.showViewController(vc as UIViewController, sender: vc)
        case "Setup Mode":
            setupModePassword()
        case "Send All Results":
            sendResults(true)
           // let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabView")
           // self.showDetailViewController(vc as UIViewController, sender: nil)
        case "Send Recent Results":
            sendResults(false)
           // let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("MainMenuTabView")
           // self.showDetailViewController(vc as UIViewController, sender: nil)
        case "About":
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("AboutView")
            self.showViewController(vc as UIViewController, sender: vc)
        case "Select User":
            let vc : AnyObject! = self.storyboard?.instantiateViewControllerWithIdentifier("UserSelectorView")
            self.showViewController(vc as UIViewController, sender: vc)
        default:
            print("No Option Selected")
        }
    }
    
    func setupModePassword(){
        let msg = "Input Setup Mode Password"
        let title = "Password Required"
        var alert = UIAlertController(title: title , message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        
        self.presentViewController(alert, animated: true, completion: nil)
        
        
        alert.addTextFieldWithConfigurationHandler({textField in  textField.secureTextEntry = true})

        
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Default, handler: nil))
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: {(alertAction:UIAlertAction!) in
            let textfield = alert.textFields![0] as UITextField
            let value: String = textfield.text
            if value == "123" {
                self.goToSetupMode()
            } else {
                let errorAlert = UIAlertView(title: "Password Incorrect", message: "Password was incorrect. Please try again.", delegate: self, cancelButtonTitle: "OK")
                errorAlert.show()
            }
        }))
    }
    
    func goToSetupMode(){
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        var context: NSManagedObjectContext = appDel.managedObjectContext!
        var settings = NSFetchRequest(entityName: "Settings")
        settings.returnsObjectsAsFaults = false
        
        settings.predicate = NSPredicate(format: "settingsName = %@", "Mode")
        let results: [SettingsModel] = context.executeFetchRequest(settings, error: nil) as [SettingsModel]
        var vc: AnyObject! = nil
        if(results.count != 1){
            vc = self.storyboard?.instantiateViewControllerWithIdentifier("SingleSetupView")
        } else {
            if(results[0].settingsValue == "0"){
                vc = self.storyboard?.instantiateViewControllerWithIdentifier("SingleSetupView")
            } else {
                vc = self.storyboard?.instantiateViewControllerWithIdentifier("MultiUserGroupView")
            }
        }
        
        self.showViewController(vc as UIViewController, sender: vc)
    }

    func sendResults(all: Bool) {
        
        var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
        sendMail(appDel, all: all, view: self)
    }
    
    func sendMail(appDel: AppDelegate, all: Bool, view: UIViewController){
        let mailComposeViewController = configuredMailComposeViewController(appDel, all: all)
        if !error {
            if MFMailComposeViewController.canSendMail() {
                view.presentViewController(mailComposeViewController, animated: true, completion: nil)
            } else {
                self.showSendMailErrorAlert()
            }
        }
    }
    
    func configuredMailComposeViewController(appDel: AppDelegate, all: Bool) -> MFMailComposeViewController {
        let mailComposeVC = MFMailComposeViewController()
        mailComposeVC.mailComposeDelegate = self
        let mail = Mail()
        mailComposeVC.setToRecipients(mail.getMailTo(appDel))
        var subject = ""
        if all {
            subject = "All Test Results"
        } else {
            subject = "Recent Test Results"
        }
        mailComposeVC.setSubject(subject)
        mailComposeVC.setMessageBody("Hi\n\nAttatched is the test results.\n\nCheers\nFit To Perform", isHTML: true)
        let name = mail.getFileName(appDel)
        let path = mail.getFilePath(appDel, fileName: name)
        if path == "" {
            showSendMailErrorAlertNoFile()
        } else {
            let csvString = mail.getResultFileString(appDel, all: all)
            if csvString == "" {
                error = true
                return mailComposeVC
            }
            csvString.writeToFile(path, atomically: true, encoding: NSUTF8StringEncoding, error: nil)
            var fileData: NSData = NSData(contentsOfFile: path)!
            mailComposeVC.addAttachmentData(fileData, mimeType: "text/csv", fileName: name)
        }
        return mailComposeVC
    }
    
    func showSendMailErrorAlert(){
        let mailErrorAlert = UIAlertView(title: "Could Not Send Email", message: "Check connection and try again.", delegate: self, cancelButtonTitle: "OK")
        mailErrorAlert.show()
    }
    
    func showSendMailErrorAlertNoFile(){
        let mailErrorAlert = UIAlertView(title: "Could Not Send Email", message: "Your device cannot attach files to this email.", delegate: self, cancelButtonTitle: "OK")
        mailErrorAlert.show()
    }
    
    func showSendMailSuccessfulAlert(){
        let mailErrorAlert = UIAlertView(title: "Email Successful", message: "Your results were successfully sent.", delegate: self, cancelButtonTitle: "OK")
        mailErrorAlert.show()
    }
    
    func  mailComposeController(controller: MFMailComposeViewController!, didFinishWithResult result: MFMailComposeResult, error: NSError!) {
        
        if result.value == MFMailComposeResultSent.value{
            let utils = Utils()
            var appDel: AppDelegate = (UIApplication.sharedApplication().delegate as AppDelegate)
            utils.updateSent(appDel)
            showSendMailSuccessfulAlert()
        } else if result.value == MFMailComposeResultFailed.value{
            showSendMailErrorAlert()
        }
        self.error = false
        controller.dismissViewControllerAnimated(true, completion: nil)
    }

    
}