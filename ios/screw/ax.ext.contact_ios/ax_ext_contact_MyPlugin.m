//
//  ax_ext_contact_MyPlugin.m
//  ax.ext.contact
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"
#import "AxLog.h"
#import "ax_ext_contact_MyPlugin.h"

@implementation ax_ext_contact_MyPlugin

- (void)activate:(NSObject<AxRuntimeContext>*)runtimeContext {
    _runtimeContext = [runtimeContext retain];
}

- (void)deactivate:(NSObject<AxRuntimeContext>*)runtimeContext {
    [_runtimeContext release];
    _runtimeContext = nil;
}

- (void)execute:(id<AxPluginContext>)context {
    NSString* method = [context getMethod];

    AX_LOG_TRACE(@"ContactView_ios_method : %s", method);
    if ([method isEqualToString:@"pickContact"]) {
        if (_runningContext != nil) {
            [context sendError:AX_INVALID_ACCESS_ERR message:@"pickContact is already running"];
            return;
        }
        _runningContext = [context retain];
        [self presentABPeoplePickerNavigationController];
    }
    else {
        [context sendError:(AX_NOT_AVAILABLE_ERR)];
    }
}

- (IBAction)presentABPeoplePickerNavigationController {
    dispatch_async(dispatch_get_main_queue(), ^{
        ABPeoplePickerNavigationController *picker = [[[ABPeoplePickerNavigationController alloc] init] autorelease];
        picker.peoplePickerDelegate = self;
        [[_runtimeContext getViewController] presentModalViewController:picker animated:YES];
    });
}

- (void)clearSpentContext {
    [_runningContext release];
    _runningContext = nil;
}

#pragma mark ABPeoplePickerNavigationControllerDelegate

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person {
    // hide contact view
    [[_runtimeContext getViewController] dismissModalViewControllerAnimated:YES];

    if (_runningContext == nil) {
        AX_LOG_WARN(@"%@ missing context", __PRETTY_FUNCTION__);
        return NO;
    }

    NSString* firstName = (NSString *)ABRecordCopyValue(person, kABPersonFirstNameProperty);
    NSString* lastName = (NSString *)ABRecordCopyValue(person, kABPersonLastNameProperty);

    CFTypeRef theProperty = ABRecordCopyValue(person, kABPersonPhoneProperty);
    NSArray* phoneNumbers = (NSArray *)ABMultiValueCopyArrayOfAllValues(theProperty);
    CFRelease(theProperty);

    NSDictionary* result = [NSDictionary dictionaryWithObjectsAndKeys:
                            firstName, @"firstName",
                            lastName, @"lastName",
                            phoneNumbers, @"phoneNumbers", nil];

    [_runningContext sendResult:result];
    [self clearSpentContext];

    AX_LOG_INFO(@"picked contact: %@", [result description]);
    return NO;
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {

    // never reached..
    return NO;
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker {
    [[_runtimeContext getViewController] dismissModalViewControllerAnimated:YES];

    if (_runningContext == nil) {
        AX_LOG_WARN(@"%@ missing context", __PRETTY_FUNCTION__);
        return;
    }

    [_runningContext sendError:AX_UNKNOWN_ERR message:@"user canceled contact pick"];
    [self clearSpentContext];
}

@end
