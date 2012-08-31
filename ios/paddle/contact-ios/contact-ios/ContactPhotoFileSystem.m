//
//  ContactPhotoFileSystem.m
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

#import "ContactPhotoFileSystem.h"
#import "ContactPhotoFile.h"

@implementation ContactPhotoFileSystem

- (id)init
{
    if ((self = [super init])) {
        _root = [[ContactPhotoFile alloc] initRootWithFileSystem:self];
    }
    
    return self;
}

- (void)dealloc {
    [_root release];
    [super dealloc];
}

-(BOOL)onMount:(NSString*)prefix option:(NSDictionary*)option {
    return YES;
}

-(void)onUnmount {
    
}

-(id<AxFile>)getRoot {
    return [_root retain];
}

-(id<AxFile>)getFile:(NSString*)path {
    return [[ContactPhotoFile alloc] initWithPersonId:[path integerValue] fileSystem:self];
}

-(BOOL)canRead {
    return YES;
}

-(BOOL)canWrite {
    return YES;
}

@end
