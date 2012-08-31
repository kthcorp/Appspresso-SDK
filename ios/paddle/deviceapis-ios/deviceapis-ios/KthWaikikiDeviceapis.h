//
//  deviceapis.h
//  deviceapis-ios
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"

#define WAC_DEVICEAPIS @"http://wacapps.net/api/deviceapis"
#define WAC_ACCELERMETER @"http://wacapps.net/api/accelerometer"
#define WAC_ORIENTATION @"http://wacapps.net/api/orientation"
#define WAC_CAMERA @"http://wacapps.net/api/camera"
#define WAC_CAMERA_SHOW @"http://wacapps.net/api/camera.show"
#define WAC_CAMERA_CAPTURE @"http://wacapps.net/api/camera.capture"
#define WAC_DEVICESTATUS @"http://wacapps.net/api/devicestatus"
#define WAC_DEVICESTATUS_DEVICEINFO @"http://wacapps.net/api/devicestatus.deviceinfo"
#define WAC_DEVICESTATUS_NETWORKINFO @"http://wacapps.net/api/devicestatus.networkinfo"
#define WAC_FILESYSTEM @"http://wacapps.net/api/filesystem"
#define WAC_FILESYSTEM_READ @"http://wacapps.net/api/filesystem.read"
#define WAC_FILESYSTEM_WRITE @"http://wacapps.net/api/filesystem.write"
#define WAC_MESSAGE @"http://wacapps.net/api/messaging"
#define WAC_MESSAGE_SEND @"http://wacapps.net/api/messaging.send"
#define WAC_MESSAGE_FIND @"http://wacapps.net/api/messaging.find"
#define WAC_MESSAGE_SUBSCRIBE @"http://wacapps.net/api/messaging.subscribe"
#define WAC_MESSAGE_WRITE @"http://wacapps.net/api/messaging.write"
#define WAC_GEOLOCATION @"http://www.w3.org/TR/geolocation-API/"
#define WAC_CONTACT @"http://wacapps.net/api/pim.contact"
#define WAC_CONTACT_READ @"http://wacapps.net/api/pim.contact.read"
#define WAC_CONTACT_WRITE @"http://wacapps.net/api/pim.contact.write"
#define WAC_DEVICEINTERACTION @"http://wacapps.net/api/deviceinteraction"
#define WAC_TASK @"http://wacapps.net/api/pim.task"
#define WAC_TASK_READ @"http://wacapps.net/api/pim.task.read"
#define WAC_TASK_WRITE @"http://wacapps.net/api/pim.task.write"
#define WAC_CALENDAR @"http://wacapps.net/api/pim.calendar"
#define WAC_CALENDAR_READ @"http://wacapps.net/api/pim.calendar.read"
#define WAC_CALENDAR_WRITE @"http://wacapps.net/api/pim.calendar.write"

@interface KthWaikikiDeviceapis : DefaultAxPlugin

@end
