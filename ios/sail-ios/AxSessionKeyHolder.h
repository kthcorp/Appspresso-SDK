//
//  AxSessionKeyHolder.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

@interface AxSessionKeyHolder : NSObject {
    NSString *_key;
}

+ (AxSessionKeyHolder*)instance;

- (NSString*)key;
- (NSString*)generate;

@end
