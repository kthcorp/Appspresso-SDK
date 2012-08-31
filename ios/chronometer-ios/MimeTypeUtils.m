/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */


#import "MimeTypeUtils.h"

@implementation MimeTypeUtils

static NSDictionary *_extensionToMimeTypes;

+(NSDictionary*)extensionToMimeTypes {
    if(_extensionToMimeTypes == nil) {
        _extensionToMimeTypes = [NSDictionary dictionaryWithObjectsAndKeys:
                                 MIME_TYPE_HTML, EXTENSION_HTML, 
                                 MIME_TYPE_HTML, EXTENSION_HTM,
                                 MIME_TYPE_XHTML, EXTENSION_XHTML,
                                 MIME_TYPE_XHTML, EXTENSION_XHTM,
                                 MIME_TYPE_XHTML, EXTENSION_XHT, 
                                 MIME_TYPE_CSS, EXTENSION_CSS,
                                 MIME_TYPE_JS, EXTENSION_JS,
                                 MIME_TYPE_XML, EXTENSION_XML, 
                                 MIME_TYPE_TEXT, EXTENSION_TEXT, 
                                 MIME_TYPE_TEXT, EXTENSION_TXT, 
                                 MIME_TYPE_JSON, EXTENSION_JSON, 
                                 MIME_TYPE_PNG, EXTENSION_PNG, 
                                 MIME_TYPE_JPEG, EXTENSION_JPEG, 
                                 MIME_TYPE_JPEG, EXTENSION_JPG, 
                                 MIME_TYPE_GIF, EXTENSION_GIF, 
                                 MIME_TYPE_SVG, EXTENSION_SVG, 
                                 // TODO: add more mappings here...
                                 nil];
    }
    return [_extensionToMimeTypes retain];
}

+(NSString*)extractExtension:(NSString*)path {
    NSRange r = [path rangeOfString:@"." options:NSBackwardsSearch];
    if(r.location == NSNotFound) {
        return path;
    }
    return [path substringFromIndex:r.location + 1];
}

+(NSString*)getMimeTypeForExtension:(NSString*)extension {
    NSString *mimeType = [[MimeTypeUtils extensionToMimeTypes] objectForKey:[extension lowercaseString]];
    return (mimeType != nil) ? mimeType : MIME_TYPE_BINARY;
}

+(NSString*)getMimeType:(NSString*)path {
    return [MimeTypeUtils getMimeTypeForExtension:[MimeTypeUtils extractExtension:path]];
}


@end
