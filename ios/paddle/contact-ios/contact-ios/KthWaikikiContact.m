//
//  KthWaikikiContact.m
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiContact.h"
#import "ContactPhotoFileSystem.h"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark Address

@implementation KthWaikikiContactAddress
@synthesize types = _types;
@synthesize country = _country;
@synthesize region = _region;
@synthesize county = _county;
@synthesize city = _city;
@synthesize streetAddress = _streetAddress;
@synthesize additionalInformation = _additionalInformation;
@synthesize postalCode = _postalCode;

- (id)initWithDictionary:(NSDictionary *)dict withType:(NSString *)type {
	if (nil == (self = [super init])) {
		return nil;
	}
	
	[self setCounty:[dict objectForKey:(NSString *)kABPersonAddressCountryKey]];
	[self setStreetAddress:[dict objectForKey:(NSString *)kABPersonAddressStreetKey]];
	[self setRegion:[dict objectForKey:(NSString *)kABPersonAddressStateKey]];
	[self setCity:[dict objectForKey:(NSString *)kABPersonAddressCityKey]];
	[self setPostalCode:[dict objectForKey:(NSString *)kABPersonAddressZIPKey]];
	
	// Types
	[self setTypes:[NSArray arrayWithObject:type]];

	return self;
}

- (void)dealloc {
	[self setTypes:nil];
	[self setCountry:nil];
	[self setRegion:nil];
	[self setCounty:nil];
	[self setCity:nil];
	[self setStreetAddress:nil];
	[self setAdditionalInformation:nil];
	[self setPostalCode:nil];
	
	[super dealloc];
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
								_types, @"types", nil];
	
	if (!!_country)			{ [res setObject:_county forKey:@"country"]; }
	if (!!_region)			{ [res setObject:_region forKey:@"region"]; }
	if (!!_county)			{ [res setObject:_county forKey:@"county"]; }
	if (!!_city)			{ [res setObject:_city forKey:@"city"]; }
	if (!!_streetAddress)	{ [res setObject:_streetAddress forKey:@"streetAddress"]; }
	if (!!_postalCode)		{ [res setObject:_postalCode forKey:@"postalCode"]; }
	
	return res;
}
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PhoneNumber

@implementation KthWaikikiPhoneNumber
@synthesize types = _types;
@synthesize number = _number;
- (id)initWithNumber:(NSString *)number type:(NSString *)type {
	self = [super init];
	if (!!self) {
		[self setNumber:number];
		[self setTypes:[NSArray arrayWithObject:type]];
	}
	return self;
}

- (void)dealloc {
	[self setTypes:nil];
	[self setNumber:nil];
	
	[super dealloc];
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
						 _types, @"types", nil];
	
	if (!!_number) { [res setObject:_number forKey:@"number"]; }
	
	return res;
}
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark Email

@implementation KthWaikikiEmailAddress
@synthesize types = _types;
@synthesize email = _email;
- (id)initWithEmail:(NSString *)email type:(NSString *)type {
	self = [super init];
	if (!!self) {
		[self setEmail:email];
		[self setTypes:[NSArray arrayWithObject:type]];
	}
	return self;
}

- (void)dealloc {
	[self setTypes:nil];
	[self setEmail:nil];
	
	[super dealloc];
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
						 _types, @"types", nil];
	
	if (!!_email) { [res setObject:_email forKey:@"email"]; }
	
	return res;
}

@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark Contact

@implementation KthWaikikiContact
@synthesize identifier = _identifier;
@synthesize firstName = _firstName;
@synthesize middleName = _middleName; // Extension
@synthesize lastName = _lastName;
@synthesize nicknames = _nicknames;
@synthesize phoneticName = _phoneticName;
@synthesize addresses = _addresses;
@synthesize photoURI = _photoURI;
@synthesize phoneNumbers = _phoneNumbers;
@synthesize emails = _emails;

- (id)initWithRecord:(CFTypeRef)record {
	if (!(self = [super init])) {
		return nil;
	}
	
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	
	// First, Middle, Last name
	[self setIdentifier:ABRecordGetRecordID(record)];
	[self setFirstName:[(NSString*)ABRecordCopyValue(record, kABPersonFirstNameProperty) autorelease]];
	[self setMiddleName:[(NSString*)ABRecordCopyValue(record, kABPersonMiddleNameProperty) autorelease]]; // Extension
	[self setLastName:[(NSString*)ABRecordCopyValue(record, kABPersonLastNameProperty) autorelease]];
	
	// Nick name
	NSString* nickname = [(NSString*)ABRecordCopyValue(record, kABPersonNicknameProperty) autorelease];
	[self setNicknames:[NSArray arrayWithObjects:nickname, nil]];
	
	// PhotoURI
    [self setPhotoURI:nil];
	
	// Phonetic name (CAUTION: Waikiki phonetic name is mapped to first phonetic name)
	[self setPhoneticName:[(NSString*)ABRecordCopyValue(record, kABPersonFirstNamePhoneticProperty) autorelease]];

	// Address
	ABMultiValueRef multiAddressValue = ABRecordCopyValue(record, kABPersonAddressProperty);
	NSArray *addressArray = [(id)ABMultiValueCopyArrayOfAllValues(multiAddressValue) autorelease];
	NSUInteger adCount = ABMultiValueGetCount(multiAddressValue);
	NSMutableArray *addresses = [[NSMutableArray alloc] init];
	for (NSUInteger i=0; i < adCount; ++i) {
		NSDictionary *theDict = [addressArray objectAtIndex:i];
		NSString *dType = (NSString*)ABMultiValueCopyLabelAtIndex(multiAddressValue, i);
		NSString *type = [KthWaikikiContact typeCommonFromIOSToWaikiki:dType];
		[dType release];
		
		// Cannot be happend...maybe...
		if (!type) { type = kDefaultAddressType; }

		KthWaikikiContactAddress *addr = [[KthWaikikiContactAddress alloc] initWithDictionary:theDict withType:type];
		[addresses addObject:[addr returnData]];
		[addr release];
	}
	[self setAddresses:addresses];
	[addresses release];
	CFRelease(multiAddressValue);
		
		
	// Phone numbers
	CFTypeRef phoneNumbers_mv = ABRecordCopyValue(record, kABPersonPhoneProperty);
	NSUInteger pnCount = ABMultiValueGetCount(phoneNumbers_mv);
	NSMutableArray *numbers = [[NSMutableArray alloc] initWithCapacity:pnCount];
	for (int i = 0; i < pnCount; i++) {
		NSString *number = (NSString*)ABMultiValueCopyValueAtIndex(phoneNumbers_mv, i);
		NSString *type = (NSString*)ABMultiValueCopyLabelAtIndex(phoneNumbers_mv, i);
		
		// Cannot be happend...maybe...
		if (!type) type = kDefaultPhoneNumberType;

		KthWaikikiPhoneNumber *pn = [[KthWaikikiPhoneNumber alloc] initWithNumber:number type:[KthWaikikiContact typePhonenumberFromIOSToWaikiki:type]];
		[number release];
		[type release];
		[numbers addObject:[pn returnData]];
		[pn release];
	}
	[self setPhoneNumbers:numbers];
	[numbers release];
	CFRelease(phoneNumbers_mv);
	

	// Emails
	CFTypeRef emails_mv = ABRecordCopyValue(record, kABPersonEmailProperty);
	NSUInteger emCount = ABMultiValueGetCount(emails_mv);
	NSMutableArray *emails = [[NSMutableArray alloc] initWithCapacity:emCount];
	for (int i = 0; i < emCount; i++) {
		NSString *email = (NSString*)ABMultiValueCopyValueAtIndex(emails_mv, i);
		NSString *type = (NSString*)ABMultiValueCopyLabelAtIndex(emails_mv, i);

		// Cannot be happend...maybe...
		if (!type) { type = kDefaultEmailType; }
		
		KthWaikikiEmailAddress *e = [[KthWaikikiEmailAddress alloc] initWithEmail:email type:[KthWaikikiContact typeCommonFromIOSToWaikiki:type]];
		[email release];
		[type release];		  
		[emails addObject:[e returnData]];
		[e release];
	}
	[self setEmails:emails];
	[emails release];
	CFRelease(emails_mv);
	
	[pool release];
	return self;
}

- (id)initWithAbAddressBook:(ABAddressBookRef)abAddressBook withRecordId:(ABRecordID)recordId {
	ABRecordRef record = ABAddressBookGetPersonWithRecordID(abAddressBook, recordId);
	if (NULL == record) {
		[self release];
		return nil;
	}
	
	self = [self initWithRecord:record];
	return self;
}

- (NSDictionary*)returnData {
	NSMutableDictionary *res = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                [NSString stringWithFormat:@"%d",(int)_identifier], @"_id", nil ];
	
	if (!!_firstName)		{ [res setObject:_firstName forKey:@"firstName"]; }
	if (!!_middleName)		{ [res setObject:_middleName forKey:@"middleName"]; } // Extension
	if (!!_lastName)		{ [res setObject:_lastName forKey:@"lastName"]; }
	if (!!_phoneNumbers)	{ [res setObject:_phoneNumbers forKey:@"phoneNumbers"]; }
	if (!!_emails)			{ [res setObject:_emails forKey:@"emails"]; }
	if (!!_nicknames)		{ [res setObject:_nicknames forKey:@"nicknames"]; }
	if (!!_phoneticName)	{ [res setObject:_phoneticName forKey:@"phoneticName"]; }
	if (!!_addresses)		{ [res setObject:_addresses forKey:@"addresses"]; }
	if (!!_photoURI)		{ [res setObject:_photoURI forKey:@"photoURI"]; }
	
	return res;
}

- (void)dealloc {
	[self setFirstName:nil];
	[self setMiddleName:nil]; // Extension
	[self setLastName:nil];
	[self setNicknames:nil];
	[self setPhoneticName:nil];
	[self setAddresses:nil];
	[self setPhotoURI:nil];
	[self setPhoneNumbers:nil];
	[self setEmails:nil];
	
	[super dealloc];
}

static NSDictionary *_pnWtoI, *_pnItoW, *_cmWtoI, *_cmItoW;
+ (void)initTypes {
	// check one.
	if (_pnWtoI != nil) return;
	
	// Phonenumber
	NSUInteger count;
	_pnWtoI = [[NSDictionary dictionaryWithObjectsAndKeys:
				(NSString *)kABWorkLabel, @"WORK",
				(NSString *)kABHomeLabel, @"HOME",
				(NSString *)kABOtherLabel, @"OTHER",
				(NSString *)kABPersonPhoneMobileLabel, @"CELL",
				(NSString *)kABPersonPhoneIPhoneLabel, @"IPHONE",
				(NSString *)kABPersonPhoneMainLabel, @"MAIN", 
				(NSString *)kABPersonPhoneHomeFAXLabel, @"HOMEFAX", 
				(NSString *)kABPersonPhoneWorkFAXLabel, @"WORKFAX", 
				(NSString *)kABPersonPhonePagerLabel, @"PAGER",				
				nil] retain];
	
	count = [_pnWtoI count];
	NSString *pnKeys[count];
	NSString *pnValues[count];
	[_pnWtoI getObjects:pnValues andKeys:pnKeys];
	_pnItoW = [[NSDictionary dictionaryWithObjects:pnKeys forKeys:pnValues count:count] retain];
	
	// Common
	_cmWtoI = [[NSDictionary dictionaryWithObjectsAndKeys:
				(NSString *)kABWorkLabel, @"WORK",
				(NSString *)kABHomeLabel, @"HOME",
				(NSString *)kABOtherLabel, @"OTHER",
				nil] retain];
	
	count = [_cmWtoI count];
	NSString *cmKeys[count];
	NSString *cmValues[count];
	[_cmWtoI getObjects:cmValues andKeys:cmKeys];
	_cmItoW = [[NSDictionary dictionaryWithObjects:cmKeys forKeys:cmValues count:count] retain];
}

+ (NSString *)typePhonenumberFromWaikikiToIOS:(NSString *)type {
	[KthWaikikiContact initTypes];
	NSString *label = [_pnWtoI objectForKey:type];
	return (!!label) ? label : type;
}
+ (NSString *)typePhonenumberFromIOSToWaikiki:(NSString *)type {	
	[KthWaikikiContact initTypes];
	NSString *label = [_pnItoW objectForKey:type];
	return (!!label) ? label : type;
}
+ (NSString *)typeCommonFromWaikikiToIOS:(NSString *)type {
	[KthWaikikiContact initTypes];
	NSString *label = [_cmWtoI objectForKey:type];
	return (!!label) ? label : type;
}
+ (NSString *)typeCommonFromIOSToWaikiki:(NSString *)type {	
	[KthWaikikiContact initTypes];
	NSString *label = [_cmItoW objectForKey:type];
	return (!!label) ? label : type;
}

@end
