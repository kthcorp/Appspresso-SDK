/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */

#import "AxConfig.h"


@implementation AxConfig

static NSDictionary *_ax_config;

+(NSDictionary*)getConfig {
    if(!_ax_config) {        
        NSString *path = [[NSBundle mainBundle] pathForResource:@"appspresso_config" ofType:@"plist"];
        _ax_config = [[NSDictionary alloc] initWithContentsOfFile:path];
    }
    return _ax_config;
}

#pragma mark -

+(NSString*)getAttribute:(NSString*)name{
    return [[AxConfig getConfig] objectForKey:name];
}

+(NSString*)getAttribute:(NSString*)name defaultValue:(NSString*)defaultValue{
    NSString *result = [AxConfig getAttribute:name];
    if(result) {
        return result;
    }
    return defaultValue;
}

+(int)getAttributeAsInteger:(NSString*)name defaultValue:(int)defaultValue{
    NSString *result = [AxConfig getAttribute:name];
    if(result) {
        return [result intValue];
    }
    return defaultValue;
}

+(BOOL)getAttributeAsBoolean:(NSString*)name defaultValue:(BOOL)defaultValue{
    NSString *result = [AxConfig getAttribute:name];
    if(result) {
        return [result boolValue];
    }
    return defaultValue;
}

@end
