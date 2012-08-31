//
//  DefaultW3Wiget.h
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <Foundation/Foundation.h>

#import "W3Widget.h"
#import "W3Storage.h"

@protocol W3Storage;
@class DefaultW3Preferences;
@class DefaultW3Feature;
/**
 * TODO: support localized config
 */
@interface DefaultW3Widget : NSObject<W3Widget,NSXMLParserDelegate> {
@private
	BOOL _parsed;
	int _currentElement;
    
	NSString *_id;
	NSString *_version;
	int _width;
	int _height;
	NSString *_viewmodes;
	NSString *_name;
	NSString *_shortName;
	NSString *_author;
	NSString *_authorHref;
	NSString *_authorEmail;
	NSString *_description;
	NSString *_license;
	NSString *_contentSrc;
	NSString *_iconSrc;
	NSMutableDictionary *_features;
    NSString * _featureName;
    BOOL _featureRequired;
    NSMutableDictionary *_featureParam;
    DefaultW3Feature *_feature;
    DefaultW3Preferences* _preferences;
}

@property (nonatomic,readonly,retain) NSString* contentSrc;
@property (nonatomic,readonly,retain) NSString* iconSrc;
@property (nonatomic,readonly,retain) NSDictionary* features;
@property (nonatomic,readonly,retain) NSObject<W3Storage>* preferences;

-(id)initWithContentOfFile:(NSString*)path;

+(DefaultW3Widget*)createWithContentOfFile:(NSString*)path;

@end