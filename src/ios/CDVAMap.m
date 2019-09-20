#import "CDVAMap.h"
#import <Cordova/CDVPlugin.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapNaviKit/AMapNaviKit.h>

@implementation CDVAMap

-(void)startNavi:(CDVInvokedUrlCommand *)command
{
    self.compositeManager=[[AMapNaviCompositeManager alloc]init];
//    self.compositeManager.delegate=self;
    
    //导航组件配置类 since 5.2.0
    AMapNaviCompositeUserConfig *config = [[AMapNaviCompositeUserConfig alloc] init];
    NSDictionary *start=command.arguments[0];
    NSArray *waylist=command.arguments[1];
    NSDictionary *end=command.arguments[2];
    NSDictionary *info=command.arguments[3];
    //传入起点，并且带高德POIId
    if(start!=[NSNull null]){
        [config setRoutePlanPOIType:AMapNaviRoutePlanPOITypeStart location:[self jsonToPoint:start] name:[start objectForKey:@"name"] POIId:[start objectForKey:@"poiid"]];
    }
    //传入途径点，并且带高德POIId
    if(waylist!=[NSNull null]){
        for (NSDictionary* d in waylist) {
            [config setRoutePlanPOIType:AMapNaviRoutePlanPOITypeWay location:[self jsonToPoint:d] name:[d objectForKey:@"name"] POIId:[d objectForKey:@"poiid"]];
        }
    }
    //传入终点，并且带高德POIId
    if(end!=[NSNull null]){
            [config setRoutePlanPOIType:AMapNaviRoutePlanPOITypeEnd location:[self jsonToPoint:end] name:[end objectForKey:@"name"] POIId:[end objectForKey:@"poiid"]];
    }
    if(info!=[NSNull null]){
        [config setVehicleInfo:[self jsonToInfo:info]];
    }
    //启动
    [self.compositeManager presentRoutePlanViewControllerWithOptions:config];
}
-(void)pluginInitialize
{
    [AMapServices sharedServices].apiKey=[self getAMapApiKey];
}

-(NSString *)getAMapApiKey{
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"AMapKey"];
}
-(AMapNaviCompositeUserConfig *) jsonToConfig:(NSDictionary*) json
{
    AMapNaviCompositeUserConfig *config=[[AMapNaviCompositeUserConfig alloc] init];
    
}
-(AMapNaviPoint*) jsonToPoint:(NSDictionary *)json
{
    NSNumber *lat=[json objectForKey:@"lat"];
    NSNumber *lng=[json objectForKey:@"lng"];
    return [AMapNaviPoint locationWithLatitude:[lat floatValue] longitude:[lng floatValue]];
}
-(AMapNaviVehicleInfo*) jsonToInfo:(NSDictionary*) json
{
    //设置车辆信息
    AMapNaviVehicleInfo *info = [[AMapNaviVehicleInfo alloc] init];
    info.vehicleId = [json objectForKey:@"carNumber"]; //设置车牌号
    info.type = 1;                                                      //设置车辆类型,0:小车; 1:货车. 默认0(小车).
    info.size = [[json objectForKey:@"size"] integerValue];              //设置货车的类型(大小)
    info.width = [[json objectForKey:@"width"] floatValue];             //设置货车的宽度,范围:(0,5],单位：米
    info.height = [[json objectForKey:@"height"] floatValue];           //设置货车的高度,范围:(0,10],单位：米
    info.length = [[json objectForKey:@"length"] floatValue];           //设置货车的长度,范围:(0,25],单位：米
    info.weight = [[json objectForKey:@"weight"] floatValue];            //设置货车的总重量,范围:(0,100]
    info.load = [[json objectForKey:@"load"] floatValue];              //设置货车的核定载重,范围:(0,100],核定载重应小于总重量
    info.axisNums = [[json objectForKey:@"axis"] integerValue];           //设置货车的轴数（用来计算过路费及限重）
    return  info;
}
@end
