//
//  ax_ext_contact_MyPlugin.h
//  ax.ext.contact
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>
#import "AxPlugin.h"

@protocol AxContext;
@protocol AxPluginContext;

@interface ax_ext_contact_MyPlugin : NSObject<AxPlugin, ABPeoplePickerNavigationControllerDelegate> {
@private
    NSObject<AxRuntimeContext> *_runtimeContext;
    id<AxPluginContext> _runningContext;
}

- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext;
- (void)deactivate:(NSObject<AxRuntimeContext>*)runtimeContext;
- (void)execute:(NSObject<AxPluginContext>*)context;

- (IBAction)presentABPeoplePickerNavigationController;

// ABPeoplePickerNavigationControllerDelegate method
- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person;

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person 
    property:(ABPropertyID)property 
    identifier:(ABMultiValueIdentifier)identifier;

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker;

 @end
