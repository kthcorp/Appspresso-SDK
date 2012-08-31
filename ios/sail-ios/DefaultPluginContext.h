//
//  DefaultPluginContext.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <UIKit/UIKit.h>
#import "AxPluginContext.h"

@class BridgeSession;

@interface DefaultPluginContext : NSObject <AxPluginContext>
{
@private
	NSNumber *_identifier;
    NSString *_prefix;
    NSString *_method;
	NSArray *_params;
    NSMutableDictionary* _attrs;
    NSDictionary *_result;
    BridgeSession *_session;
    BOOL _malformed;
}
@property (nonatomic, retain, readwrite) NSNumber *identifier;
@property (nonatomic, retain, readwrite) NSString *method;
@property (nonatomic, retain, readwrite) NSString *prefix;
@property (nonatomic, retain, readwrite) NSArray *params;
@property (nonatomic, assign, readwrite) NSDictionary *result;
@property (nonatomic, retain, readwrite) BridgeSession *session;

- (id)initWithRequestJson:(NSDictionary*)json session:(BridgeSession*)session;
- (void)makeSuccessResult:(id)result;
- (void)makeErrorResult:(NSInteger)code message:(NSString *)message;
- (BOOL)isMalformedRequest;

@end
