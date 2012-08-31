//
//  DefaultPluginManager.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultPluginManager.h"
#import "AppspressoResponse.h"
#import "AxViewController.h"
#import "JSONKit.h"
#import "DefaultWidgetAgent.h"
#import "AxLog.h"

typedef enum {PLGUIN_E_UNKNOWN, PLUGIN_E_AXPLUGIN, PLUGIN_E_DESCRIPTION, PLUGIN_E_URL, PLUGIN_E_AUTHOR, PLUGIN_E_LICENSE, PLUGIN_E_FEATURE, PLUGIN_E_MODULE, PLUGIN_E_PROPERTY} PluginCurrentElement;

#define PLUGIN_ELEMENT_AXPLUGIN @"axplugin"
#define PLUGIN_ATTR_AXPLUGIN_ID @"id"
#define PLUGIN_ATTR_AXPLUGIN_VERSION @"version"

#define PLUGIN_ELEMENT_DESCRIPTION @"description"
#define PLUGIN_ELEMENT_URL @"url"
#define PLUGIN_ELEMENT_AUTHOR @"author"
#define PLUGIN_ELEMENT_LICENSE @"license"

#define PLUGIN_ELEMENT_FEATURE @"feature"
#define PLUGIN_ATTR_FEATURE_ID @"id"
#define PLUGIN_ATTR_FEATURE_CATEGORY @"category"

#define PLUGIN_ELEMENT_MODULE @"module"
#define PLUGIN_ATTR_MODULE_PLATFORM @"platform"
#define PLUGIN_ATTR_MODULE_PLATFORM_VERSION @"platform-version"
#define PLUGIN_ATTR_MODULE_MIN_PLATFORM_VERSION @"min-platform-version"
#define PLUGIN_ATTR_MODULE_MAX_PLATFORM_VERSION @"max-platform-version"
#define PLUGIN_ATTR_MODULE_CLASS @"class"

#define PLUGIN_ELEMENT_PROPERTY @"property"
#define PLUGIN_ATTR_PROPERTY_NAME @"name"
#define PLUGIN_ATTR_PROPERTY_VALUE @"value"



@implementation DefaultPluginManager

# pragma mark -

- (id)initWithWidgetAgent:(DefaultWidgetAgent*)widgetAgent
{
    if((self = [super init])) {
        _widgetAgent = [widgetAgent retain];

        _pluginClassName = [[NSMutableDictionary alloc] init];
        _pluginInstances = [[NSMutableDictionary alloc] init];
        _pluginForFeature = [[NSMutableDictionary alloc] init];
        _pluginLoadedOrder = [[NSMutableArray alloc] init];
    }
    return self;
}

-(void)dealloc {
    [_pluginClassName release];
    [_pluginInstances release];
    [_pluginForFeature release];
    [_pluginLoadedOrder release];
    [_widgetAgent release];
    [super dealloc];
}

#pragma mark PluginManager

-(id<AxPlugin>)requirePlugin:(NSString*)pluginId {
    NSObject<AxPlugin> *plugin = [_pluginInstances objectForKey:pluginId];
    if(plugin == nil) {
        AX_LOG_INFO(@"load appspresso plugin... id=%@", pluginId);

        NSString *className = [_pluginClassName objectForKey:pluginId];
        if(className == nil) {
            AX_LOG_WARN(@"failed to load appspresso plugin... bad or missing class. id=%@", pluginId, className);
            return nil;
        }
        
        Class class = NSClassFromString(className);
        if(class == nil) {
            AX_LOG_WARN(@"failed to load appspresso plugin... class not found. id=%@,class=%@", pluginId, className);
            return nil;
        }
        
        plugin = [[class alloc] init];
        if(plugin == nil) {
            AX_LOG_WARN(@"failed to load appspresso plugin... instanciation failed. id=%@,class=%@", pluginId, className);
            return nil;
        }
        
        SEL activateSelector = @selector(activate:);
        if ([plugin respondsToSelector:activateSelector]) {
            // NOTE: 각 플러그인의 activate에서 requirePlugin을 호출해서 의존하는 플러그인을 로드. 일종의 재귀호출...
            [plugin performSelector:activateSelector withObject:_widgetAgent.runtimeContext];
        }
        [_pluginInstances setObject:plugin forKey:pluginId];
        [_pluginLoadedOrder addObject:pluginId];
    } else {
        AX_LOG_TRACE(@"skip to load appspresso plugin... already loaded. id=%@", pluginId);        
    }
    return plugin;
}

-(id<AxPlugin>)requirePluginWithFeature:(NSString*)featureId {
    return [self requirePlugin:[_pluginForFeature objectForKey:featureId]];
}

-(NSArray*)getPluginLoadedOrder {
    return _pluginLoadedOrder;
}

#pragma mark UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSError *error = nil;
    NSString *pluginPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"assets/ax_plugins"];
    
    // build-in plugin
    [_pluginClassName setObject:@"W3WidgetPreferencesPlugin" forKey:[NSString stringWithString:@"ax.w3.widget.preferences"]];
    [_pluginClassName setObject:@"BuiltinDeviceStatusPlugin" forKey:[NSString stringWithString:@"ax.builtin.devicestatus"]];
    
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:pluginPath error:&error];
    if(files == nil) {
        // no plugins! really?
        return YES;
    }
    
    for(NSString* file in files) {
        _currentPluginId = nil;
        //parse axplugin descriptor
        NSXMLParser *parser = [[NSXMLParser alloc] initWithContentsOfURL:[NSURL fileURLWithPath:[pluginPath stringByAppendingPathComponent:file]]];
        parser.delegate = self;
        if([parser parse]) {
            // parse ok
        }
        [parser release];
    }
    
    // load all plugins
    for(NSString *pluginId in [_pluginClassName allKeys]) {
        [self requirePlugin:pluginId];
    }
    
    return YES;
}

#pragma mark NSXMLParserDelegate

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict {
	//NSLog(@"parser:didStartElement: element=%@ ns=%@ qname=%@ attrs=%@", elementName, namespaceURI, qName, attributeDict);
	if ([PLUGIN_ELEMENT_AXPLUGIN isEqualToString:elementName]) {
        _currentPluginId = [attributeDict objectForKey:PLUGIN_ATTR_AXPLUGIN_ID];
	} else if (_currentPluginId != nil && [PLUGIN_ELEMENT_FEATURE isEqualToString:elementName]) {
        NSString *featureId = [attributeDict objectForKey:PLUGIN_ATTR_FEATURE_ID];
        if (featureId != nil) {
            [_pluginForFeature setObject:_currentPluginId forKey:featureId];
        }
	} else if (_currentPluginId != nil && [PLUGIN_ELEMENT_MODULE isEqualToString:elementName]) {
        NSString* platform = [attributeDict objectForKey:PLUGIN_ATTR_MODULE_PLATFORM];
        if([@"ios" isEqualToString:platform]) {
            NSString *class = [attributeDict objectForKey:PLUGIN_ATTR_MODULE_CLASS];
            if (class != nil) {
                [_pluginClassName setObject:class forKey:_currentPluginId];
            }
        }
	} else {
		//NSLog(@"ignore element!");
	}
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	//NSLog(@"parser:didEndElement: element=%@ ns=%@ qname=%@", elementName, namespaceURI, qName);
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
	//NSLog(@"parse:foundCharacters: %@", string);
}

- (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError {
	AX_LOG_TRACE(@"parse error! %@", parseError);
}

@end

