//
//  DefaultW3Wiget.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "DefaultW3Widget.h"
#import "DefaultW3Feature.h"
#import "DefaultW3Preferences.h"
#import "AxLog.h"

//
// literals for W3C widget config.xml
// http://www.w3.org/TR/widgets/
//

typedef enum { W3_E_UNKNOWN, W3_E_WIDGET, W3_E_NAME, W3_E_AUTHOR, W3_E_DESCRIPTION, W3_E_LICENSE, W3_E_CONTENT, W3_E_ICON, W3_E_FEATURE, W3_E_PREFERENCE } W3CurrentElement;

#define W3_ELEMENT_WIDGET @"widget"
#define W3_ATTR_WIDGET_ID @"id"
#define W3_ATTR_WIDGET_VERSION @"version"
#define W3_ATTR_WIDGET_WIDTH @"widgth"
#define W3_ATTR_WIDGET_HEIGHT @"height"
#define W3_ATTR_WIDGET_VIEWMODES @"viewmodes"

#define W3_ELEMENT_NAME @"name"
#define W3_ATTR_NAME_SHORT @"short"

#define W3_ELEMENT_AUTHOR @"author"
#define W3_ATTR_AUTHOR_HREF @"authorHref"
#define W3_ATTR_AUTHOR_EMAIL @"authorEmail"

#define W3_ELEMENT_DESCRIPTION @"description"

#define W3_ELEMENT_LICENSE @"license"

#define W3_ELEMENT_CONTENT @"content"
#define W3_ATTR_CONTENT_SRC @"src"

#define W3_ELEMENT_ICON @"icon"
#define W3_ATTR_ICON_SRC @"src"

#define W3_ELEMENT_FEATURE @"feature"
#define W3_ATTR_FEATURE_NAME @"name"
#define W3_ATTR_FEATURE_REQUIRED @"required"

#define W3_ELEMENT_FEATURE_PARAM @"param"
#define W3_ATTR_PARAM_NAME @"name"
#define W3_ATTR_PARAM_VALUE @"value"

#define W3_ELEMENT_PREFERENCE @"preference"
#define W3_ATTR_PREFERENCE_NAME @"name"
#define W3_ATTR_PREFERENCE_VALUE @"value"
#define W3_ATTR_PREFERENCE_READONLY @"readonly"

#define W3_FILENAME_CONFIG_XML @"config.xml"
#define W3_FILENAME_ICON_PNG @"icon.png"
#define W3_FILENAME_ICON_GIF @"icon.gif"
#define W3_FILENAME_ICON_JPG @"icon.jpg"
#define W3_FILENAME_ICON_ICO @"icon.ico"
#define W3_FILENAME_ICON_SVG @"icon.svg"
#define W3_FILENAME_INDEX_HTML @"index.html"
#define W3_FILENAME_INDEX_HTM @"index.htm"
#define W3_FILENAME_INDEX_XHTML @"index.xhtml"
#define W3_FILENAME_INDEX_XHT @"index.xht"
#define W3_FILENAME_LOCALES @"locales"

@implementation DefaultW3Widget

@synthesize contentSrc = _contentSrc;
@synthesize iconSrc = _iconSrc;
@synthesize features = _features;
@synthesize preferences = _preferences;

-(id)initWithContentOfFile:(NSString*)path {
    if((self = [super init])) {
		_features = [[NSMutableDictionary alloc] init];
        _featureParam = [[NSMutableDictionary alloc] init];
		_preferences = [[DefaultW3Preferences alloc] init];
		_currentElement = W3_E_UNKNOWN;
		_parsed = NO;
		NSXMLParser *parser = nil;
		NSURL *url = nil;
		@try {
			//url = [[NSURL alloc] initFileURLWithPath:path];
            //NSURL *path = [NSURL URLWithString:kConfigXmlPath relativeToURL:[[NSBundle mainBundle] bundleURL]];
			url = [NSURL URLWithString:path relativeToURL:[[NSBundle mainBundle] bundleURL]];
            AX_LOG_TRACE(@"parse widget config: url=%@", url);
			parser = [[NSXMLParser alloc] initWithContentsOfURL:url];
			[parser setDelegate:self];
			//[addressParser setShouldResolveExternalEntities:YES];
			_parsed = [parser parse];
		} @finally {
			[parser release];
			//[url release];
		}        
    }
    return self;
}

-(void)dealloc {
	[_id release];
	[_version release];
	//[_width release];
	//[_height release];
	[_viewmodes release];
	[_name release];
	[_shortName release];
	[_author release];
	[_authorHref release];
	[_authorEmail release];
	[_contentSrc release];
	[_iconSrc release];
	[_features release];
	[_preferences release];
    [_featureParam release];
    [super dealloc];
}

#pragma W3Widget

-(NSString *)getId {
    return _id;
}

-(NSString *)getDescription {
    return _description;
}

-(NSString *)getName {
    return _name;
}

-(NSString *)getShortName {
    return _shortName;
}

-(NSString *)getVersion {
    return _version;
}

-(NSString *)getAuthor {
    return _author;
}

-(NSString *)getAuthorEmail {
    return _authorEmail;
}

-(NSString *)getAuthorHref {
    return _authorHref;
}

-(id<W3Storage>)getPreferences {
    return _preferences;
}

-(long)getWidth {
    return _width;
}

-(long)getHeight {
    return _height;
}

#pragma mark NSXMLParserDelegate

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict {
	//AX_LOG_TRACE(@"parser:didStartElement: element=%@ ns=%@ qname=%@ attrs=%@", elementName, namespaceURI, qName, attributeDict);
    if ([W3_ELEMENT_WIDGET isEqualToString:elementName]) {
		_currentElement = W3_E_WIDGET;
		_id = [[attributeDict objectForKey:W3_ATTR_WIDGET_ID] retain];
		_version = [[attributeDict objectForKey:W3_ATTR_WIDGET_VERSION] retain];
		_width = [[attributeDict objectForKey:W3_ATTR_WIDGET_WIDTH] intValue];
		_height = [[attributeDict objectForKey:W3_ATTR_WIDGET_HEIGHT] intValue];
		_viewmodes = [[attributeDict objectForKey:W3_ATTR_WIDGET_VIEWMODES] retain];
        
        [_preferences writeApplicationVersion:_version];
		AX_LOG_TRACE(@"widget: id=%@ version=%@ width=%d height=%d viewmodes=%@", _id, _version, _width, _height, _viewmodes);
	} else if ([W3_ELEMENT_NAME isEqualToString:elementName]) {
		_currentElement = W3_E_NAME;
		_shortName = [[attributeDict objectForKey:W3_ATTR_NAME_SHORT] retain];
		AX_LOG_TRACE(@"name: short=%@", _shortName);
	} else if ([W3_ELEMENT_AUTHOR isEqualToString:elementName]) {
		_currentElement = W3_E_AUTHOR;
		_authorHref = [[attributeDict objectForKey:W3_ATTR_AUTHOR_HREF] retain];
		_authorEmail = [[attributeDict objectForKey:W3_ATTR_AUTHOR_EMAIL] retain];
		AX_LOG_TRACE(@"author: href=%@ email=%@", _authorHref, _authorEmail);
	} else if ([W3_ELEMENT_DESCRIPTION isEqualToString:elementName]) {
		_currentElement = W3_E_DESCRIPTION;
		AX_LOG_TRACE(@"description");
	} else if ([W3_ELEMENT_LICENSE isEqualToString:elementName]) {
		_currentElement = W3_E_LICENSE;
		AX_LOG_TRACE(@"license");
	} else if ([W3_ELEMENT_CONTENT isEqualToString:elementName]) {
		_currentElement = W3_E_CONTENT;
		_contentSrc = [[attributeDict objectForKey:W3_ATTR_CONTENT_SRC] retain];
		AX_LOG_TRACE(@"content: src=%@", _contentSrc);
	} else if ([W3_ELEMENT_ICON isEqualToString:elementName]) {
		_currentElement = W3_E_ICON;
		_iconSrc = [[attributeDict objectForKey:W3_ATTR_ICON_SRC] retain];
		AX_LOG_TRACE(@"icon: src=%@", _iconSrc);
	} else if ([W3_ELEMENT_FEATURE isEqualToString:elementName]) {
		_currentElement = W3_E_FEATURE;
        _featureName = [NSString stringWithString:[attributeDict objectForKey:W3_ATTR_FEATURE_NAME]];
        _featureRequired = [[attributeDict objectForKey:W3_ATTR_FEATURE_REQUIRED] boolValue];
	} else if ([W3_ELEMENT_FEATURE_PARAM isEqualToString:elementName]) {
        [_featureParam setValue:[attributeDict objectForKey:W3_ATTR_PARAM_VALUE] forKey:[attributeDict objectForKey:W3_ATTR_PARAM_NAME]];
	}  else if ([W3_ELEMENT_PREFERENCE isEqualToString:elementName]) {
		_currentElement = W3_E_PREFERENCE;
        NSString* preferenceName = [attributeDict objectForKey:W3_ATTR_PREFERENCE_NAME];
        NSString* preferenceValue = [attributeDict objectForKey:W3_ATTR_PREFERENCE_VALUE];
        BOOL preferenceReadonly = [[attributeDict objectForKey:W3_ATTR_PREFERENCE_READONLY] boolValue];

		// AX_LOG_TRACE(@"preference: %@", preference);
        [_preferences setItem:preferenceName :preferenceValue :preferenceReadonly];
	} else {
		//AX_LOG_TRACE(@"ignore element!");
	}
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	//AX_LOG_TRACE(@"parser:didEndElement: element=%@ ns=%@ qname=%@", elementName, namespaceURI, qName);
    if (![_featureName isEqual:nil] && [elementName isEqualToString:W3_ELEMENT_FEATURE]) {
        _feature = [[DefaultW3Feature alloc] initWithName:_featureName
                                                 required:_featureRequired
                                                   params:(_featureParam.count > 0)? [[NSDictionary alloc]initWithDictionary:_featureParam] : nil];
        AX_LOG_TRACE(@"feature: %@", _feature);
		[_features setObject:_feature forKey:_featureName];
		_feature = nil;
        [_featureParam removeAllObjects];
    }
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
	//AX_LOG_TRACE(@"parse:foundCharacters: %@", string);
	switch (_currentElement) {
        case W3_E_NAME:
            AX_LOG_TRACE(@"name: %@", _name);
            _name = [string retain];
            break;
        case W3_E_AUTHOR:
            AX_LOG_TRACE(@"author: %@", _author);
            _author = [string retain];
            break;
        default:
            //AX_LOG_TRACE(@"ignore node text!");
            break;
	}
}

- (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError {
	AX_LOG_TRACE(@"parse error! %@", parseError);
}

#pragma mark -

+(DefaultW3Widget*)createWithContentOfFile:(NSString *)path {
    DefaultW3Widget *result = [[DefaultW3Widget alloc] initWithContentOfFile:path];
    return [result autorelease];
}

@end