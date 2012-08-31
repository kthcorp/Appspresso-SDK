//
//  KthWaikikiDocumentsFileStream.h
//  filesystem-ios
//
//  Copyright (c) 2012 KTH Corp.
//


#import <Foundation/Foundation.h>
#import "KthWaikikiRealPathTypeFileStream.h"


@interface KthWaikikiDocumentsFileStream : KthWaikikiRealPathTypeFileStream {
}
- (Class)fileClass;
@end
