//
//  W3WidgetPreferencesPlugin.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "W3WidgetPreferencesPlugin.h"

#import "W3Storage.h"
#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxError.h"

@implementation W3WidgetPreferencesPlugin 

-(void) activate:(id<AxRuntimeContext>)runtimeContext {
    _widget = [runtimeContext getWidget]; 
}

-(void) deactivate:(id<AxRuntimeContext>)runtimeContext {    
    _widget = nil;
}

-(void) execute:(id<AxPluginContext>)context {
    NSString *method = [context getMethod];
    NSObject *result = nil;
    
    @try {
        if ([@"length" isEqualToString:method]) {
            result = [NSNumber numberWithLong:[[_widget getPreferences] length]];
        }
        else if ([@"key" isEqualToString:method]) {
            long index = [[context getParamAsNumber:0 defaultValue:0] longValue];
            result = [[_widget getPreferences] key:index];
        }
        else if ([@"getItem" isEqualToString:method]) {
            NSString *key = [context getParamAsString:0];
            result = (NSString *)[[_widget getPreferences] getItem:key];
        }
        else if ([@"setItem" isEqualToString:method]) {
            NSString *key = [context getParamAsString:0];
            NSString *value = [context getParamAsString:1];
            [[_widget getPreferences] setItem:key :value];
        }
        else if ([@"removeItem" isEqualToString:method]) {
            NSString *key = [context getParamAsString:0];
            [[_widget getPreferences] removeItem:key];
        }
        else if ([@"clear" isEqualToString:method]) {
            [[_widget getPreferences] clear];
        }
    }
    @catch (AxError *e) {
        [context sendError:e.code message:e.message];
        return;
    }
    @catch (NSException *e) {
        [context sendError:AX_UNKNOWN_ERR message:[e reason]];
        return;
    }
    
    if (result == nil){
        result = [NSNull null];
    }
    
    [context sendResult:result];
}

@end
