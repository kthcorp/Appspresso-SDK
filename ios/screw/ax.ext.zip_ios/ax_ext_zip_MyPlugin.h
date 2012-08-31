//
//  ax_ext_zip_MyPlugin.h
//  ax.ext.zip
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"

@interface ax_ext_zip_MyPlugin : DefaultAxPlugin {
}

-(void)unzip:(NSObject<AxPluginContext>*)context;

@end
