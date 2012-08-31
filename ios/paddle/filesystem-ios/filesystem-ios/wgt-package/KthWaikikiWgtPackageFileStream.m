//
//  KthWaikikiWgtPackageFileStream.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import "KthWaikikiDocumentsFile.h"

#import "KthWaikikiFileStream.h"
#import "KthWaikikiWgtPackageFileStream.h"
#import "KthWaikikiWgtPackageFile.h"
#import "Base64Codec.h"


@interface KthWaikikiWgtPackageFileStream ()
@end


@implementation KthWaikikiWgtPackageFileStream
- (Class)fileClass {
	return [KthWaikikiWgtPackageFile class];
}
@end
