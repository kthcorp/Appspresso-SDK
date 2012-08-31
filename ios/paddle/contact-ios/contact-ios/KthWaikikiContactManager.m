//
//  KthWaikikiContactManager.m
//  contact-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiContactManager.h"
#import "ABContact.h"
#import "ABContactsHelper.h"
#import "KthWaikikiContact.h"
#import "ContactPhotoFileSystem.h"

#import "AxRuntimeContext.h"
#import "AxPluginContext.h"
#import "AxFileSystemManager.h"
#import "AxLog.h"
#import "AxError.h"

// TODO: localize
#define kDefaultAddressBook @"Contacts"
#define kDefaultAddressBookHandle 1

#define kContactPrefix @"contact-photo"

#define WAC_CONTACT @"http://wacapps.net/api/pim.contact"
#define WAC_CONTACT_READ @"http://wacapps.net/api/pim.contact.read"
#define WAC_CONTACT_WRITE @"http://wacapps.net/api/pim.contact.write"

static NSMutableDictionary *_addressBooks;

@implementation KthWaikikiContactManager

- (BOOL)_isActivatedFeatureContactRead {
    return [[self runtimeContext]isActivatedFeature:WAC_CONTACT_READ];
}

- (BOOL)_isActivatedFeatureContactWrite {
    return [[self runtimeContext]isActivatedFeature:WAC_CONTACT_WRITE];
}

- (BOOL)_isActivatedFeatureContact {
    return [[self runtimeContext]isActivatedFeature:WAC_CONTACT];
}

- (void)activate:(id<AxRuntimeContext>)runtimeContext {
    [super activate:runtimeContext];
    
    [runtimeContext requirePlugin:@"deviceapis.pim"];

	_addressBooks = [[NSMutableDictionary alloc] init];
	NSDictionary *abook = [NSDictionary dictionaryWithObjectsAndKeys:
						   //1=device abook, 0=sim abook 
						   [NSDecimalNumber numberWithInt:1], @"_type",
						   [NSNumber numberWithInt:kDefaultAddressBookHandle], @"_handle",
						   kDefaultAddressBook, @"_name",
						   nil];
	
	[_addressBooks setObject:abook forKey:[NSNumber numberWithInt:kDefaultAddressBookHandle]];
	
    ContactPhotoFileSystem *contactPhotoFileSystem = [[ContactPhotoFileSystem alloc] init];
    [[runtimeContext getFileSystemManager] mount:kContactPrefix fileSystem:contactPhotoFileSystem option:nil];
    [contactPhotoFileSystem release];
}

- (void)deactivate:(id<AxRuntimeContext>)runtimeContext {
    [_addressBooks release];
    [[runtimeContext getFileSystemManager] unmount:kContactPrefix];
    [super deactivate:runtimeContext];
}

/***********************************************************************************************************************************/
/* ContactManager   PendingOperation getAddressBooks(AddressBookArraySuccessCallback successCallback, ErrorCallback errorCallback) */
/* AddressBook      Contact createContact(ContactProperties contactProperties)                                                     */
/* PendingOperation addContact(AddContactSuccessCallback successCallback, ErrorCallback? errorCallback, Contact contact)           */
/* PendingOperation updateContact(SuccessCallback successCallback, ErrorCallback? errorCallback, Contact contact)                  */
/* PendingOperation deleteContact(SuccessCallback successCallback, ErrorCallback? errorCallback, DOMString id)                     */
/* PendingOperation findContacts(ContactArraySuccessCallback successCallback, ErrorCallback? errorCallback, ContactFilter filter)  */
/***********************************************************************************************************************************/

- (void)getAddressBooks:(id<AxPluginContext>)context {	
    [context sendResult:[NSArray arrayWithArray:[_addressBooks allValues]]];
}

- (ABContact *)updateContactWithId:(NSInteger)recId contact:(NSDictionary *)contact {
	ABContact *nc = (recId < 0) ? [ABContact contact] : [ABContact contactWithRecordID:recId];;
	
	// TODO : photoURI	
	
	// firstName
	NSString *firstName = [contact objectForKey:@"firstName"];
	if (![firstName isKindOfClass:[NSNull class]] && [firstName length] != 0) {
		[nc setFirstname:firstName];
	}
	
	// middleName (extension)
	NSString *middleName = [contact objectForKey:@"middleName"];
	if (![middleName isKindOfClass:[NSNull class]] && [middleName length] != 0) {
		[nc setMiddlename:middleName];
	}	

	// lastName
	NSString *lastName = [contact objectForKey:@"lastName"];
	if (![lastName isKindOfClass:[NSNull class]] && [lastName length] != 0) {
		[nc setLastname:lastName];
	}	

	// nickNames
	NSArray *nicknames = [contact objectForKey:@"nicknames"];
	if (![nicknames isKindOfClass:[NSNull class]] && [nicknames count] != 0) {
		// TODO: check. use only 1 nickname
		[nc setNickname:[nicknames objectAtIndex:0]];
	}
	
	// phoneticName (CAUTION: phoneticName mapped to first phonetic name)
	NSString *phoneticName = [contact objectForKey:@"phoneticName"];
	if (![phoneticName isKindOfClass:[NSNull class]] && [phoneticName length] != 0) {
		[nc setFirstnamephonetic:phoneticName];
	}
	
	// address
	NSArray *addresses = [contact objectForKey:@"addresses"];
	if (![addresses isKindOfClass:[NSNull class]] && [addresses count] != 0) {
		NSMutableArray *aa = [[NSMutableArray alloc] init];
		for (NSDictionary *ad in addresses) {						
			if ([ad isKindOfClass:[NSNull class]] || [ad count] == 0)
				break;
				
			// TODO: ISO 3166-1 (mU.Um)
			NSString *country = [ad objectForKey:@"country"];
			if ([country isKindOfClass:[NSNull class]] || [country length] == 0) country = nil;
			
			NSString *city = [ad objectForKey:@"city"];
			if ([city isKindOfClass:[NSNull class]] && [city length] == 0) city = nil;
			
			NSString *street = [ad objectForKey:@"streetAddress"];
			if ([street isKindOfClass:[NSNull class]] && [street length] == 0) street = nil;
			
			NSString *region = [ad objectForKey:@"region"];
			if ([region isKindOfClass:[NSNull class]] && [region length] == 0) region = nil;
			
			NSString *postalCode = [ad objectForKey:@"postalCode"];
			if ([postalCode isKindOfClass:[NSNull class]] && [postalCode length] == 0) postalCode = nil;
			
			NSArray *types = [ad objectForKey:@"types"];
			NSString *typeLabel = nil;
			if (![types isKindOfClass:[NSNull class]] && [types count] != 0) {
				typeLabel = [KthWaikikiContact typeCommonFromWaikikiToIOS:[types objectAtIndex:0]];
			} else {
				typeLabel = [KthWaikikiContact typeCommonFromWaikikiToIOS:kDefaultAddressType];
			}
			NSDictionary *whaddy = [ABContact addressWithStreet:street withCity:city withState:region withZip:postalCode withCountry:country withCode:nil];
			[aa addObject:[ABContact dictionaryWithValue:whaddy andLabel:(CFStringRef)typeLabel]];
		}
		[nc setAddressDictionaries:aa];
		[aa release];
	}
	
	// phoneNumbers
	NSArray *phoneNumbers = [contact objectForKey:@"phoneNumbers"];
	if (![phoneNumbers isKindOfClass:[NSNull class]] && [phoneNumbers count] != 0) {
		NSMutableArray *pa = [[NSMutableArray alloc] init];
		for (NSDictionary *pn in phoneNumbers) {
			NSString *number = [pn objectForKey:@"number"];
			if ([number isKindOfClass:[NSNull class]] || [number length] == 0)
				continue;
			
			NSArray *types = [pn objectForKey:@"types"];
			NSString *typeLabel = nil;
			if (![types isKindOfClass:[NSNull class]] && [types count] != 0) {
				typeLabel = [KthWaikikiContact typePhonenumberFromWaikikiToIOS:[types objectAtIndex:0]];
			} else {
				typeLabel = [KthWaikikiContact typePhonenumberFromWaikikiToIOS:kDefaultPhoneNumberType];
			}
			
			[pa addObject:[ABContact dictionaryWithValue:number andLabel:(CFStringRef)typeLabel]];
		}
		[nc setPhoneDictionaries:pa];
		[pa release];
	}
	
	// emails
	NSArray *emails = [contact objectForKey:@"emails"];
	if (![emails isKindOfClass:[NSNull class]] && [emails count] != 0) {
		NSMutableArray *ea = [[NSMutableArray alloc] init];
		for (NSDictionary *e in emails) {
			NSString *email = [e objectForKey:@"email"];
			if ([email isKindOfClass:[NSNull class]] || [email length] == 0)
				continue;
			
			NSArray *types = [e objectForKey:@"types"];
			NSString *typeLabel = nil;
			if (![types isKindOfClass:[NSNull class]] && [types count] != 0) {
				typeLabel = [KthWaikikiContact typeCommonFromWaikikiToIOS:[types objectAtIndex:0]];
			} else {
				typeLabel = [KthWaikikiContact typeCommonFromWaikikiToIOS:kDefaultEmailType];
			}
			
			[ea addObject:[ABContact dictionaryWithValue:email andLabel:(CFStringRef)typeLabel]];
		}
		[nc setEmailDictionaries:ea];
		[ea release];
	}
	
	return nc;
}

- (void)addContact:(id<AxPluginContext>)context {	
	if ([self _isActivatedFeatureContactRead] && ![self _isActivatedFeatureContact] && ![self _isActivatedFeatureContactWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *contact = [context getParamAsDictionary:1];
	ABContact *nc = [self updateContactWithId:-1 contact:contact];
	
	NSError *error;
	if ([ABContactsHelper addContact:nc withError:&error]) {
		ABAddressBookRef ab = ABAddressBookCreate();
		KthWaikikiContact *contact = [[[KthWaikikiContact alloc] initWithAbAddressBook:ab withRecordId:[nc recordID]] autorelease];
		CFRelease(ab);
		[context sendResult:[contact returnData]];
	}
	else {
		// TODO: appropriate error
		[context sendError:AX_UNKNOWN_ERR message:AX_UNKNOWN_ERR_MSG];
		return;
	}
}	

- (void)updateContact:(id<AxPluginContext> )context {	
	if ([self _isActivatedFeatureContactRead] && ![self _isActivatedFeatureContact] && ![self _isActivatedFeatureContactWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *contact = [context getParamAsDictionary:1];
	id im;
	
	NSInteger recId = !!(im = [contact objectForKey:@"id"]) ? [im intValue] : -1;
    if (recId < 0) {
        [context sendError:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG];
        return;
    }
	ABAddressBookRef ab = ABAddressBookCreate();
	if (ABAddressBookGetPersonWithRecordID(ab, recId) == NULL) {
		CFRelease(ab);
		[context sendError:AX_NOT_FOUND_ERR message:AX_NOT_FOUND_ERR_MSG];
		return;
	}
	ABContact *nc = [self updateContactWithId:recId contact:contact];

	NSError *error;
	if ([ABContactsHelper addContact:nc withError:&error]) {
		KthWaikikiContact *c = [[[KthWaikikiContact alloc] initWithAbAddressBook:ab withRecordId:[nc recordID]]autorelease];
		CFRelease(ab);
		//[context sendResult:[c autorelease]];
        [context sendResult:[c returnData]];
	}
	else {
		// TODO: appropriate error
		CFRelease(ab);
		[context sendError:AX_UNKNOWN_ERR message:AX_UNKNOWN_ERR_MSG];
		return;
	}
}

- (void)deleteContact:(id<AxPluginContext> )context {
	if ([self _isActivatedFeatureContactRead] && ![self _isActivatedFeatureContact] && ![self _isActivatedFeatureContactWrite]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSInteger recordID = [context getParamAsInteger:1];
    if (recordID < 0) {
        [context sendError:AX_INVALID_VALUES_ERR message:AX_INVALID_VALUES_ERR_MSG];
        return;
    }
	
	ABAddressBookRef ab = ABAddressBookCreate();
	ABRecordRef record = ABAddressBookGetPersonWithRecordID(ab, recordID);
	
	if (NULL == record) {
		CFRelease(ab);
		[context sendError:AX_NOT_FOUND_ERR message:AX_NOT_FOUND_ERR_MSG];
		return;
	}
	
	if (!ABAddressBookRemoveRecord(ab, record, NULL)) {
		// TODO: appropriate error
		CFRelease(ab);
		[context sendError:AX_UNKNOWN_ERR message:AX_UNKNOWN_ERR_MSG];
		return;
	}
	
	ABAddressBookSave(ab, NULL);
	CFRelease(ab);
	[context sendResult];
}

- (void)findContacts:(id<AxPluginContext>)context {
	if ([self _isActivatedFeatureContactWrite] && ![self _isActivatedFeatureContact] && ![self _isActivatedFeatureContactRead]) {
        [context sendError:AX_SECURITY_ERR message:AX_SECURITY_ERR_MSG];
        return;
    }
    NSDictionary *filter = [context getParamAsDictionary:1];
	AX_LOG_TRACE(@"findContacts: filter=%@", filter);
	
	id im;
	ABAddressBookRef ab = ABAddressBookCreate();
	
	// find from id
	NSInteger recID = !!(im = [filter objectForKey:@"id"]) ? [im intValue] : -1;
	if (recID >= 0) {
		ABRecordRef record = ABAddressBookGetPersonWithRecordID(ab, recID);
		if (NULL == record) {
			[context sendResult:[[[NSArray alloc] init] autorelease]];
		} else  {
			KthWaikikiContact *contact = [[KthWaikikiContact alloc] initWithRecord:record];
            if(ABPersonHasImageData(record)) {
                NSString *photoVirtualPath = [NSString stringWithFormat:@"%@/%d", kContactPrefix, recID];
                [contact setPhotoURI:[[self.runtimeContext getFileSystemManager] toUri:photoVirtualPath]];
            }
            [context sendResult:[NSArray arrayWithObject:[contact returnData]]];
            [contact release];
		}
		CFRelease(ab);
		return;
	}
	
	NSArray *contacts = [ABContactsHelper contacts];
	// filtering by firstname
	if (!!(im = [filter objectForKey:@"firstName"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingFirstName:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"] 
													 withArray:contacts];
	}
	
	// filtering by lastname
	if (!!(im = [filter objectForKey:@"lastName"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingLastName:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
													withArray:contacts];
	}
	
	// filtering by phoneticName(firstPhoneticName)
	if (!!(im = [filter objectForKey:@"phoneticName"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingPhoneticFirstName:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
															 withArray:contacts];
	}

	// filtering by nickname
	if (!!(im = [filter objectForKey:@"nickName"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingNickName:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
													withArray:contacts];
	}
	
	// filtering by phone number
	if (!!(im = [filter objectForKey:@"phoneNumber"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingPhoneNumber:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
													   withArray:contacts];
	}

	// filtering by email
	if (!!(im = [filter objectForKey:@"emails"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingEmails:[[im stringByReplacingOccurrencesOfString:@"@" withString:@"\\@"] stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
												  withArray:contacts];
	}

	// filtering by address
	if (!!(im = [filter objectForKey:@"address"]) && ![im isKindOfClass:[NSNull class]]) {
		contacts = [ABContactsHelper contactsMatchingAddress:[im stringByReplacingOccurrencesOfString:@"%" withString:@"*"]
												   withArray:contacts];
	}
	
	NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:[contacts count]];
	for (ABContact *abContact in contacts) {
		KthWaikikiContact *c = [[KthWaikikiContact alloc] initWithAbAddressBook:ab withRecordId:[abContact recordID]];
		if (nil != c) {
            if([abContact hasImage]) {
                NSString *photoVirtualPath = [NSString stringWithFormat:@"%@/%d", kContactPrefix, [abContact recordID]];
                [c setPhotoURI:[[self.runtimeContext getFileSystemManager] toUri:photoVirtualPath]];
            }
            [result addObject:[c returnData]];
            [c release];
		}
	}
	
	CFRelease(ab);
	[context sendResult:result];
}
@end
