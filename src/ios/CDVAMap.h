#import <Cordova/CDVPlugin.h>
#import <AMapNaviKit/AMapNaviKit.h>
#import <AMapLocationKit/AMapLocationKit.h>

@interface CDVAMap : CDVPlugin
- (void)startNavi:(CDVInvokedUrlCommand*) command;

@property (nonatomic,strong) AMapNaviCompositeManager *compositeManager;
@property (nonatomic,strong) AMapLocationManager *locationManager;
@end
