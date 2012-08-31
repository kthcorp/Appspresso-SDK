//
//  KthWaikikiContact.h
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import <AddressBook/ABAddressBook.h>
#import <AddressBookUI/AddressBookUI.h>
#import "ABContact.h"

#define kDefaultAddressType @"HOME"
#define kDefaultPhoneNumberType @"VOICE"
#define kDefaultEmailType @"HOME"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Address
@interface KthWaikikiContactAddress : NSObject {
@private
	NSArray *_types;
	NSString *_country;
	NSString *_region;
	NSString *_county;					// Not used
	NSString *_city;
	NSString *_streetAddress;
	NSString *_additionalInformation;	// Not used
	NSString *_postalCode;
}
@property (nonatomic, retain) NSArray *types;
@property (nonatomic, retain) NSString *country;
@property (nonatomic, retain) NSString *region;
@property (nonatomic, retain) NSString *county;
@property (nonatomic, retain) NSString *city;
@property (nonatomic, retain) NSString *streetAddress;
@property (nonatomic, retain) NSString *additionalInformation;
@property (nonatomic, retain) NSString *postalCode;

- (id)initWithDictionary:(NSDictionary *)dict withType:(NSString *)type;
- (NSDictionary*)returnData;
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark PhoneNumber
@interface KthWaikikiPhoneNumber : NSObject {
@private
	NSArray *_types;
	NSString *_number;
}
@property (nonatomic, retain) NSArray *types;
@property (nonatomic, retain) NSString *number;

- (id)initWithNumber:(NSString *)number type:(NSString *)type;
- (NSDictionary*)returnData;
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Email
@interface KthWaikikiEmailAddress : NSObject {
@private
	NSArray *_types;
	NSString *_email;
}
@property (nonatomic, retain) NSArray *types;
@property (nonatomic, retain) NSString *email;

- (id)initWithEmail:(NSString *)email type:(NSString *)type;
- (NSDictionary*)returnData;
@end


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark Contact
@interface KthWaikikiContact : NSObject {
@private
	ABRecordID _identifier;
	NSString *_firstName;
	NSString *_middleName; // Extension
	NSString *_lastName;
	NSArray *_nicknames;
	NSString *_phoneticName;
	NSArray *_addresses;
	NSString *_photoURI;
	NSArray *_phoneNumbers;
	NSArray *_emails;
}
@property (nonatomic, assign) ABRecordID identifier;
@property (nonatomic, retain) NSString *firstName;
@property (nonatomic, retain) NSString *middleName; // Extension
@property (nonatomic, retain) NSString *lastName;
@property (nonatomic, retain) NSArray *nicknames;
@property (nonatomic, retain) NSString *phoneticName;
@property (nonatomic, retain) NSArray *addresses;
@property (nonatomic, retain) NSString *photoURI;
@property (nonatomic, retain) NSArray *phoneNumbers;
@property (nonatomic, retain) NSArray *emails;

- (id)initWithRecord:(CFTypeRef)record;
- (id)initWithAbAddressBook:(ABAddressBookRef)abAddressBook withRecordId:(ABRecordID)recordId;
- (NSDictionary*)returnData;

+ (NSString *)typePhonenumberFromWaikikiToIOS:(NSString *)type;
+ (NSString *)typePhonenumberFromIOSToWaikiki:(NSString *)type;
+ (NSString *)typeCommonFromWaikikiToIOS:(NSString *)type;
+ (NSString *)typeCommonFromIOSToWaikiki:(NSString *)type;

//+ (NSString *)loadPhotoUriWithId:(NSInteger)recordID;
//+ (BOOL)savePhotoUriWithId:(NSInteger)recordID uri:(NSString *)uri;
@end
