//
//  BridgeSessionManager.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

@interface BridgeSession : NSObject {
    NSString* _key;
    BOOL _javaScriptEvaluationEnabled;
    BOOL _initialized;
}

@property (nonatomic, retain, readwrite) NSString* key;
@property (nonatomic, assign, readwrite) BOOL javaScriptEvaluationEnabled;
@property (nonatomic, assign, readwrite) BOOL initialized;

-(id)initWithSessionKey:(NSString*)key;

@end


@interface BridgeSessionManager : NSObject

+(BridgeSession*)lookupWithSessionKey:(NSString*)key;

@end
