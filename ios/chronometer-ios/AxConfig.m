/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
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
