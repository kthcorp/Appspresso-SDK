//
//  DefaultW3Preferences.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "AxError.h"
#import "DefaultW3Preferences.h"
#import "AxLog.h"

#define NO_MODIFICATION_ALLOWED_ERR 7

#define PERSISTENT_DOMAIN_NAME @"ax.w3.widget.preference"
#define KEY_READONLY_ITEMS @"ax.w3.widget.preference.readonly"
#define KEY_CURRENT_VERSION @"ax.app.version"


@interface DefaultW3Preferences (hidden)

-(void) setValueToPersistent:(NSString *)value forKey:(NSString *)key;
-(void) removeFromPersistent:(NSString *)key;
-(void) commit;
@end

@implementation DefaultW3Preferences

-(id)init {
    if ((self = [super init])) {
        _userDefaults = [[NSUserDefaults standardUserDefaults] retain];
    
        _fromPersistent = [_userDefaults stringForKey:KEY_CURRENT_VERSION] != nil;
        
        NSDictionary *persistent = [_userDefaults persistentDomainForName:PERSISTENT_DOMAIN_NAME];
        if (persistent == nil) {
            persistent = [[NSDictionary alloc] init];
            [_userDefaults setPersistentDomain :persistent forName:PERSISTENT_DOMAIN_NAME];
        }
        _storageArea = [[NSMutableDictionary alloc] initWithDictionary:persistent];
        
        NSArray * readonlyItems = [_userDefaults arrayForKey:KEY_READONLY_ITEMS];
        if (readonlyItems == nil) {
            readonlyItems = [[NSArray alloc] init];
            [_userDefaults setValue:readonlyItems forKey:KEY_READONLY_ITEMS];
        }
        _readonlyItems = [[NSMutableArray alloc] initWithArray:readonlyItems];
    }
    
    return self;
}

-(void)dealloc {
    [self commit];
    [_userDefaults synchronize];
    
    if (_storageArea != nil) {
        [_storageArea release];
    }
    
    [_readonlyItems release];
    [_userDefaults release];
    
    [super dealloc];
}

#pragma mark W3Storage

-(long)length {
    return [_storageArea count];
}

-(NSString *)key:(long)index {
    if ([self length] <= index) {
        return nil;
    }
    
    return [[_storageArea allKeys] objectAtIndex:index];
}

-(NSString*)getItem:(NSString *)key {
    return [_storageArea objectForKey:key];
}

-(void)setItem:(NSString *)key :(NSString*)value {
    if([_readonlyItems containsObject:key]) {
        @throw [AxError errorWithCode:NO_MODIFICATION_ALLOWED_ERR 
                              message:[key stringByAppendingString:@" property is readonly"] 
                                cause:nil];
    }
 
    [self setValueToPersistent:value forKey:key];
}

-(void)removeItem:(NSString *)key {
    if ([_readonlyItems containsObject:key]) {
        @throw [AxError errorWithCode:NO_MODIFICATION_ALLOWED_ERR 
                              message:[key stringByAppendingString:@" property is readonly"] 
                                cause:nil];
    }

    [self removeFromPersistent:key];
}

-(void)clear {
    for (NSString *key in [_storageArea allKeys]){
        if (![_readonlyItems containsObject:key]) {
            [_storageArea removeObjectForKey:key];
        }
    }
    
    [self commit];
}

//////////////////////////////////////////////////////////////////////
-(void) writeApplicationVersion:(NSString *)version {
    [_userDefaults setValue:version forKey:KEY_CURRENT_VERSION];
}

-(void) setItem:(NSString *)key :(NSString *)value :(Boolean)readonly {
    if (_fromPersistent) {
        return;
    }
    
    [self setValueToPersistent:value forKey:key];
    
    if (readonly) {
        [_readonlyItems addObject:key];
        [_userDefaults setValue:_readonlyItems forKey:KEY_READONLY_ITEMS];
    }
}

-(void) setValueToPersistent:(NSString *)value forKey:(NSString *)key {
    [_storageArea setValue:value forKey:key];
    [self commit];
}

-(void) removeFromPersistent:(NSString *)key {
    [_storageArea removeObjectForKey:key];
    [self commit];
}

-(void) commit {
    [_userDefaults setPersistentDomain:_storageArea forName:PERSISTENT_DOMAIN_NAME];
}

@end