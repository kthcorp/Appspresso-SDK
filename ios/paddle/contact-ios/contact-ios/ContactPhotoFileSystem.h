//
//  ContactPhotoFileSystem.h
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

#import "AxFileSystem.h"

@class ContactPhotoFile;

@interface ContactPhotoFileSystem : NSObject<AxFileSystem> {
@private
    ContactPhotoFile* _root;
}

@end

