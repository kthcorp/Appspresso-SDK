//
//  KthWaikikiWgtPrivateTmpFileStream.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiDocumentsFile.h"

#import "KthWaikikiFileStream.h"
#import "KthWaikikiWgtPrivateTmpFileStream.h"
#import "KthWaikikiWgtPrivateTmpFile.h"
#import "Base64Codec.h"


@interface KthWaikikiWgtPrivateTmpFileStream ()
@end


@implementation KthWaikikiWgtPrivateTmpFileStream
- (Class)fileClass {
	return [KthWaikikiWgtPrivateTmpFile class];
}
@end
