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


#import <Foundation/Foundation.h>


/**
 * fallback mime type
 */
#define MIME_TYPE_BINARY @"application/octet-stream"

//
// well-known mimetypes
//

#define MIME_TYPE_HTML @"text/html"
#define MIME_TYPE_XHTML @"application/xhtml+xml"
#define MIME_TYPE_CSS @"text/css"
#define MIME_TYPE_JS @"application/javascript"
#define MIME_TYPE_XML @"text/xml"
#define MIME_TYPE_TEXT @"text/plain"
#define MIME_TYPE_JSON @"application/json"
#define MIME_TYPE_PNG @"image/png"
#define MIME_TYPE_JPEG @"image/jpeg"
#define MIME_TYPE_GIF @"image/gif"
#define MIME_TYPE_SVG @"image/svg+xml"

// TODO: add more mime types here...
#define MIME_TYPE_WGT @"application/widget"

//
// well-known extensions
//

#define EXTENSION_HTML @"html"
#define EXTENSION_HTM @"htm"
#define EXTENSION_XHTML @"xhtml"
#define EXTENSION_XHTM @"xhtm"
#define EXTENSION_XHT @"xht"
#define EXTENSION_CSS @"css"
#define EXTENSION_JS @"js"
#define EXTENSION_XML @"xml"
#define EXTENSION_TEXT @"text"
#define EXTENSION_TXT @"txt"
#define EXTENSION_JSON @"json"
#define EXTENSION_PNG @"png"
#define EXTENSION_JPEG @"jpeg"
#define EXTENSION_JPG @"jpg"
#define EXTENSION_GIF @"gif"
#define EXTENSION_SVG @"svg"

// TODO: add more extensions here...
#define EXTENSION_WGT @"wgt"

/*!
 * This class provides a simple mime-type database.
 * <p/>
 * NOTE: supports well-known(essential) mime types only!
 *
 */
@interface MimeTypeUtils : NSObject

/*!
 * extract an extension from the given path.
 *
 * @param path
 *            a whole path string with an extension
 * @return the extension part if available, otherwise the given path itself
 */
+(NSString*)extractExtension:(NSString*)path;

/*!
 * get mime type for the given extension.
 *
 * @param extension
 *            an extension
 * @return the matching mime type, otherwise, 'application/octet-stream'
 */
+(NSString*)getMimeTypeForExtension:(NSString*)extension;

/*!
 * get mime type for the given path(or extension).
 *
 * @param path
 *            a whole path string or an extension itself
 * @return the matching mime type, otheriwse 'application/octet-stream'
 */
+(NSString*)getMimeType:(NSString*)path;

@end
