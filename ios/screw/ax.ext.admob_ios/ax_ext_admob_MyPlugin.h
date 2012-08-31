//
//  ax_ext_admob_MyPlugin.h
//  ax.ext.admob
//
//  Copyright (c) 2012 KTH Corp.
//

#import <Foundation/Foundation.h>
#import "DefaultAxPlugin.h"

@interface ax_ext_admob_MyPlugin : DefaultAxPlugin {
@private
    NSMutableDictionary *_bannerViews;
}

- (void)showAdmob:(NSObject<AxPluginContext>*)context;
- (void)hideAdmob:(NSObject<AxPluginContext>*)context;
- (void)refreshAdmob:(NSObject<AxPluginContext>*)context;

@end
