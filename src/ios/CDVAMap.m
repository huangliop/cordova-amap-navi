#import "CDVAMap.h"
#import <Cordova/CDVPlugin.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapNaviKit/AMapNaviKit.h>

@implementation CDVAMap

-(void)startNavi:(CDVInvokedUrlCommand *)command
{
    self.compositeManager=[[AMapNaviCompositeManager alloc]init];
//    self.compositeManager.delegate=self;
    [self.compositeManager presentRoutePlanViewControllerWithOptions:nil];
}
-(void)pluginInitialize
{
    [AMapServices sharedServices].apiKey=[self getAMapApiKey];
}

-(NSString *)getAMapApiKey{
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"AMapKey"];
}

@end
