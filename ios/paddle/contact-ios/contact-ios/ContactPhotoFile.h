//
//  ContactPhotoFile.h
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

#import "AxFile.h"

@class ContactPhotoFileSystem;

@interface ContactPhotoFile : NSObject<AxFile> {
@private
    ABRecordID _personId;
    ContactPhotoFileSystem *_fileSystem;
    ABRecordRef _record;
    BOOL _isRoot;
    NSData *_data;
    int _position;
}

- (id)initRootWithFileSystem:(ContactPhotoFileSystem*)fileSystem;
- (id)initWithPersonId:(ABRecordID)personId fileSystem:(ContactPhotoFileSystem*)fileSystem;

@end
