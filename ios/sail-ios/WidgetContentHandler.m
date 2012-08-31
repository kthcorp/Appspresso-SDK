//
//  WidgetContentHandler.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "AxLog.h"
#import "WidgetContentHandler.h"

#import "MimeTypeUtils.h"

#define DEF_BASE_DIR @"/assets/ax_www/"

@implementation WidgetContentHandler

- (id)initWithWidgetAgent:(DefaultWidgetAgent *)widgetAgent connection:(HydraConnection *)connection {
    self = [super initWithWidgetAgent:widgetAgent connection:connection];
    if(self != nil){
        userAgentLocales = [[NSMutableArray alloc] init];
        [self deriveUserAgentLocales];
    }
    return self;
}

- (void)dealloc {
    [userAgentLocales release];
    [super dealloc];
}

- (void)specificHandlerForApi:(NSString *)name value:(NSString *)value {
    NSString *uri = [value stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSString *path = [[NSURL URLWithString:uri]path];
    
    NSData *data = [self findWidgetContents:(path)];
    
    if(data == nil) {
        [self setStatus:404];
        [self setContentType:MIME_TYPE_TEXT];
        [self replyResponse:[@"404 NOT FOUND" dataUsingEncoding:NSUTF8StringEncoding]];
        return;
    }
    [self setContentType:[MimeTypeUtils getMimeType:value]];
    [self setCacheHeader];
    [self replyResponse:data];
}

- (NSData*)findWidgetContents:(NSString *)path {
    //NSURL *url = [NSURL URLWithString:path relativeToURL:[[NSBundle mainBundle] bundleURL]];
    //NSData *data = [NSData dataWithContentsOfURL:url];
    //if NSURL value has a Korean char, it needs euc-kr encoding. and some one uses korean name html, NSdata returns nil value. 
    
    NSString *base = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:DEF_BASE_DIR];
    NSString *localeDir = [base stringByAppendingPathComponent:@"locales"];
    
    // locale content
    for (NSString* locale in userAgentLocales) {
        NSString *nativePath = [[localeDir stringByAppendingPathComponent:locale] stringByAppendingPathComponent:path];
        NSData *data = [NSData dataWithContentsOfFile:nativePath];
        
        AX_LOG_TRACE(@"nativePath %@", nativePath);
        if (data != nil){
            AX_LOG_TRACE(@"find widget content (in locales) : %@", nativePath);
            return data;
        }
    }
    
    // default locale content
    NSString *nativePath = [base stringByAppendingPathComponent:path];
    NSData *data = [NSData dataWithContentsOfFile:nativePath];
    
    if ([[AxLog log] isDebugEnabled] && data != nil){
        AX_LOG_TRACE(@"find widget content : %@", nativePath);
    }
    
    return data;
}

// see also http://www.w3.org/TR/widgets/#rule-for-deriving-the-user-agent-locales
- (void) deriveUserAgentLocales {
    
    NSLocale* locale = [NSLocale currentLocale];
    // NSString* language = [locale objectForKey:(NSLocaleLanguageCode)];
    NSString* language = [[NSLocale preferredLanguages] objectAtIndex:0];
    NSString* country = [locale objectForKey:(NSLocaleCountryCode)];
    
    if(!(language == nil || [language isEqualToString:(@"")])){
        if(!(country == nil || [country isEqualToString:(@"")])){
            NSString *str = [[language stringByAppendingString:@"-"] stringByAppendingString:country];
            [userAgentLocales addObject:[str lowercaseString]];
        }
        [userAgentLocales addObject:[language lowercaseString]];
    }
}

@end
