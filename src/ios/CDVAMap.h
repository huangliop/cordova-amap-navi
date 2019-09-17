#import <Cordova/CDVPlugin.h>
#import <AMapNaviKit/AMapNaviKit.h>

@interface CDVAMap : CDVPlugin
- (void)startNavi:(CDVInvokedUrlCommand*) command;

@property (nonatomic,strong) AMapNaviCompositeManager *compositeManager;
@end
