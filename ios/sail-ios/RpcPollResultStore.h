//
//  RpcPollStore.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

@interface RpcPollResultStore : NSObject {
    NSMutableDictionary* _sessionResults;
    NSMutableDictionary* _sessionConditions;
}

+ (RpcPollResultStore*)instance;

- (BOOL)waitForQueue:(NSString*)session untilTimeout:(NSTimeInterval)timeout;
- (NSArray*)drainQueue:(NSString*)session;
- (void)putResult:(NSObject*)result toQueue:(NSString*)session;

@end
