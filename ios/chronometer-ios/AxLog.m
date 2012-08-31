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

#import "AxLog.h"
#import "AxConfig.h"

typedef enum {
    LOG_LEVEL_ALL=0,
    LOG_LEVEL_TRACE=0,
    LOG_LEVEL_DEBUG=1,
    LOG_LEVEL_INFO=2,
    LOG_LEVEL_WARN=3,
    LOG_LEVEL_ERROR=4,
    LOG_LEVEL_NONE=5
} AxLogLevel;

static const char* LOG_LEVELS[] = {
    "TRACE",
    "DEBUG",
    "INFO",
    "WARN",
    "ERROR"
};

static NSString* const DEF_LOG_CATEGORY = @"AxLog";

#ifdef DEBUG
static const int DEF_LOG_LEVEL = LOG_LEVEL_ALL;
#else
static const int DEF_LOG_LEVEL = LOG_LEVEL_INFO;
#endif

// TODO: send log to NSLog(device console) or on-the-fly server(eclipse console)
#define _AX_CONSOLE_SEND_LOG(category,level,message) \
    NSLog(@"(%@) [%s] %@", category, LOG_LEVELS[level], message); \

// the root logger
static AxLog* _g_ax_log = nil;

// log instances by category
static NSMutableDictionary *_g_ax_logs = nil;

@implementation AxLog

#pragma mark -

-(id)initWithCategory:(NSString*)category level:(int)level {
    if((self = [super init])) {
        _category = [category retain];
        _level = level;
    }
    return self;
}

-(void)dealloc {
    [_category release];
    [super dealloc];
}

#pragma mark -

-(void)trace:(NSString*) format, ... {
    if(_level <= LOG_LEVEL_TRACE) {
        va_list args;
        va_start(args, format);
        NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
        _AX_CONSOLE_SEND_LOG(_category, LOG_LEVEL_TRACE, message);
        [message release];
        va_end(args);
    }
}

-(void)debug:(NSString*) format, ... {
    if(_level <= LOG_LEVEL_DEBUG) {
        va_list args;
        va_start(args, format);
        NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
        _AX_CONSOLE_SEND_LOG(_category, LOG_LEVEL_DEBUG, message);
        [message release];
        va_end(args);
    }    
}

-(void)info:(NSString*) format, ... {
    if(_level <= LOG_LEVEL_INFO) {
        va_list args;
        va_start(args, format);
        NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
        _AX_CONSOLE_SEND_LOG(_category, LOG_LEVEL_INFO, message);
        [message release];
        va_end(args);
    }        
}

-(void)warn:(NSString*) format, ... {
    if(_level <= LOG_LEVEL_WARN) {
        va_list args;
        va_start(args, format);
        NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
        _AX_CONSOLE_SEND_LOG(_category, LOG_LEVEL_WARN, message);
        [message release];
        va_end(args);
    }        
}

-(void)error:(NSString*) format, ... {
    if(_level <= LOG_LEVEL_ERROR) {
        va_list args;
        va_start(args, format);
        NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
        _AX_CONSOLE_SEND_LOG(_category, LOG_LEVEL_ERROR, message);
        [message release];
        va_end(args);
    }        
}

-(BOOL)isTraceEnabled {
    return (_level <= LOG_LEVEL_TRACE);
}

-(BOOL)isDebugEnabled {
    return (_level <= LOG_LEVEL_DEBUG);
}

-(BOOL)isInfoEnabled {
    return (_level <= LOG_LEVEL_INFO);
}

-(BOOL)isWarnEnabled {
    return (_level <= LOG_LEVEL_WARN);
}

-(BOOL)isErrorEnabled {
    return (_level <= LOG_LEVEL_ERROR);
}

#pragma mark -

+(AxLog*)log {
    if(_g_ax_log == nil) {
        _g_ax_log = [AxLog log:DEF_LOG_CATEGORY];
    }
    return _g_ax_log;
}

+(AxLog*)log:(NSString*)category {
    AxLog* log = [_g_ax_logs objectForKey:category];
    if(log == nil) {
        int level = [AxConfig getAttributeAsInteger:@"ax.log.level" defaultValue:DEF_LOG_LEVEL];
        log = [AxLog log:category level:level];
        [_g_ax_logs setObject:log forKey:category];
    }
    return log;
}

+(AxLog*)log:(NSString*)category level:(int)level {
    AxLog* log = [_g_ax_logs objectForKey:category];
    if(log == nil) {
        log = [[AxLog alloc] initWithCategory:category level:level];
        [_g_ax_logs setObject:log forKey:category];
    }
    return log;
}
@end
