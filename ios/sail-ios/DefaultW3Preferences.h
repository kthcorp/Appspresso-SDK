//
//  DefaultW3Prefernces.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <Foundation/Foundation.h>

#import "W3Storage.h"
#import "W3Widget.h"

@protocol W3Storage;

@interface DefaultW3Preferences : NSObject<W3Storage> {
@private
    Boolean _fromPersistent;
    NSUserDefaults *_userDefaults;
    NSMutableDictionary *_storageArea;
    NSMutableArray *_readonlyItems;
}

-(void) setItem:(NSString *)key :(NSString *)value :(Boolean) readonly;
-(void) writeApplicationVersion:(NSString *) version;

@end