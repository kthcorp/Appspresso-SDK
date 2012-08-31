//
//  DefaultW3Feature.m
//  sail-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "DefaultW3Feature.h"

@implementation DefaultW3Feature

@synthesize name = _name, required = _required, params = _params;

- (id) initWithName:(NSString*)name required:(BOOL)required params:(NSDictionary*)params {
	if (self = [super init]) {
		_name = [name retain];
		_required = required;
        _params = [params retain];
	}
	return self;
}

- (void) dealloc {
	[_name release];
    [_params release];
	[super dealloc];
}

- (NSString*) description { 
	return [NSString stringWithFormat:@"Feature:name=%@ required=%d param=%@", _name, _required, _params];
}

#pragma mark W3Feature

-(NSString*)getName {
    return _name;
}

-(BOOL)isRequired {
    return _required;
}

-(NSDictionary*)getParams {
    return _params;
}

@end
