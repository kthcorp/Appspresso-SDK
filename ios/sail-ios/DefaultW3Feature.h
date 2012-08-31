//
//  DefaultW3Feature.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>

#import "W3Feature.h"

@interface DefaultW3Feature : NSObject<W3Feature> {
@private
    NSString *_name;
    BOOL _required;
    NSDictionary *_params;
}

@property (readonly,retain) NSString *name;
@property (readonly,assign) BOOL required;
@property (readonly,assign) NSDictionary *params;

- (id) initWithName:(NSString*)name required:(BOOL)required params:(NSDictionary*)params;

@end
