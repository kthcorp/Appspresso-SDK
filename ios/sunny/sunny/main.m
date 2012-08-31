//
//  main.m
//  sunny
//
//  Created by Dongsu Jang on 11. 6. 19..
//  Copyright 2011 KTH Corp.
//

#import <UIKit/UIKit.h>

int main(int argc, char *argv[])
{
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    int retVal = UIApplicationMain(argc, argv, @"UIApplication", @"AxApplicationDelegate");
    [pool release];
    return retVal;
}
