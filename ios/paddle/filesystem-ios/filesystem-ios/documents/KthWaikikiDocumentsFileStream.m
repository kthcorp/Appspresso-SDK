//
//  KthWaikikiDocumentsFileStream.m
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import "KthWaikikiDocumentsFile.h"

#import "KthWaikikiFileStream.h"
#import "KthWaikikiDocumentsFileStream.h"
#import "KthWaikikiDocumentsFile.h"
#import "Base64Codec.h"


@interface KthWaikikiDocumentsFileStream ()
@end


@implementation KthWaikikiDocumentsFileStream
- (Class)fileClass {
	return [KthWaikikiDocumentsFile class];
}
@end
