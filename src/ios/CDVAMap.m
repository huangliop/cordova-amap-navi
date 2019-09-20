#import "CDVAMap.h"
#import <Cordova/CDVPlugin.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapNaviKit/AMapNaviKit.h>

@interface CDVAMap ()<AMapNaviCompositeManagerDelegate,AMapLocationManagerDelegate>

@property (nonatomic,strong) NSString *naviCallbackId;

@property (nonatomic,strong) NSString *locationCallbackId;

@property (nonatomic, copy) AMapLocatingCompletionBlock completionBlock;

@end

@implementation CDVAMap

-(void)startNavi:(CDVInvokedUrlCommand *)command
{
    self.naviCallbackId=command.callbackId;
    self.compositeManager=[[AMapNaviCompositeManager alloc]init];
    self.compositeManager.delegate=self;
    
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
-(void)getLocation:(CDVInvokedUrlCommand*) command
{
    [self initLocationManage];
    NSDictionary* obj=command.arguments[0];
    CLLocationAccuracy c=kCLLocationAccuracyHundredMeters;
    int timeout=2;
    if([[obj valueForKey:@"mode"] isEqual:[NSNumber numberWithInt:2]]){
        c=kCLLocationAccuracyBest;
        timeout=10;
    }
    [self.locationManager setDesiredAccuracy:c];
    self.locationManager.locationTimeout =timeout;
    self.locationManager.reGeocodeTimeout = timeout;
    BOOL needAdd=NO;
    if([obj valueForKey:@"address"]){
        needAdd=YES;
    }
    self.locationCallbackId=command.callbackId;
    [self.locationManager requestLocationWithReGeocode:needAdd completionBlock:self.completionBlock];
}

-(void)startLocation:(CDVInvokedUrlCommand*)command
{
    self.locationCallbackId=command.callbackId;
    [self initLocationManage];
    [self.locationManager setDelegate:self];
    BOOL needAdd=NO;
    NSDictionary* obj=command.arguments[0];
    if([obj valueForKey:@"address"]){
        needAdd=YES;
    }
    //设置允许连续定位逆地理
    [self.locationManager setLocatingWithReGeocode:needAdd];
    [self.locationManager startUpdatingLocation];
}

-(void)stopLocation:(CDVInvokedUrlCommand*)command
{
    if(self.locationManager){
        [self.locationManager stopUpdatingLocation];
        
    }
}
-(void)pluginInitialize
{
    [AMapServices sharedServices].apiKey=[self getAMapApiKey];
    [self initCompleteBlock];
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

-(void) updateInfo:(NSDictionary*) obj keep:(BOOL)keep callbackId:(NSString*)callbackId
{
    if(callbackId){
        CDVPluginResult* result=[CDVPluginResult resultWithStatus:SWIFT_CDVCommandStatus_OK messageAsDictionary:obj];
        [result setKeepCallbackAsBool:keep];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    }
}

-(void) initLocationManage
{
    if(nil==self.locationManager){
        self.locationManager=[[AMapLocationManager alloc] init];
        //设置不允许系统暂停定位
        [self.locationManager setPausesLocationUpdatesAutomatically:NO];
        //设置允许在后台定位
        [self.locationManager setAllowsBackgroundLocationUpdates:YES];
    }
}
-(NSMutableDictionary*) locationToDictionary:(CLLocation *)location regeocode:(AMapLocationReGeocode*) regeocode
{
    NSMutableDictionary *d=[NSMutableDictionary dictionary];
    [d setValue:[NSNumber numberWithDouble:location.coordinate.latitude] forKey:@"lat"];
    [d setValue:[NSNumber numberWithDouble:location.coordinate.longitude] forKey:@"lng"];
    if(regeocode){
        [d setValue:regeocode.citycode forKey:@"citCode"];
        [d setValue:regeocode.city forKey:@"cit"];
        [d setValue:regeocode.formattedAddress  forKey:@"add"];
        [d setValue:regeocode.country forKey:@"cou"];
        [d setValue:regeocode.province forKey:@"pro"];
        [d setValue:regeocode.district forKey:@"dis"];
        [d setValue:regeocode.street forKey:@"str"];
        [d setValue:regeocode.adcode forKey:@"adc"];
        [d setValue:regeocode.POIName forKey:@"poi"];
        [d setValue:regeocode.AOIName forKey:@"aoi"];
        
    }
    return d;
}
- (void)stopLocation
{
    //停止定位
    [self.locationManager stopUpdatingLocation];
    
    [self.locationManager setDelegate:nil];
    
}
#pragma mark - AMapNaviCompositeManagerDelegate

- (void)compositeManager:(AMapNaviCompositeManager *_Nonnull)compositeManager didBackwardAction:(AMapNaviCompositeVCBackwardActionType)backwardActionType {
    
    NSMutableDictionary* obj=[NSMutableDictionary dictionary];
    [obj setValue:@"exitPage" forKey:@"eventType"];
    NSInteger t=backwardActionType;
    [obj setValue:[NSNumber numberWithInteger:t]  forKey:@"data"];
    [self updateInfo:obj keep:YES callbackId:self.naviCallbackId];
}

- (void)compositeManager:(AMapNaviCompositeManager *)compositeManager onArrivedWayPoint:(int)wayPointIndex {
    NSLog(@"途径点：%d",wayPointIndex);
}

- (void)compositeManager:(AMapNaviCompositeManager *)compositeManager error:(NSError *)error {
    NSLog(@"error:{%ld - %@}", (long)error.code, error.localizedDescription);
}

- (void)compositeManagerOnCalculateRouteSuccess:(AMapNaviCompositeManager *)compositeManager {
    NSLog(@"onCalculateRouteSuccess,%ld",(long)compositeManager.naviRouteID);
}

- (void)compositeManager:(AMapNaviCompositeManager *)compositeManager onCalculateRouteSuccessWithType:(AMapNaviRoutePlanType)type {
    NSLog(@"=====  算路成功 %ld",type);
}

- (void)compositeManager:(AMapNaviCompositeManager *)compositeManager onCalculateRouteFailure:(NSError *)error {
    NSLog(@"onCalculateRouteFailure error:{%ld - %@}", (long)error.code, error.localizedDescription);
    
}

- (void)compositeManager:(AMapNaviCompositeManager *)compositeManager didStartNavi:(AMapNaviMode)naviMode {
    NSMutableDictionary* obj=[NSMutableDictionary dictionary];
    [obj setValue:@"startNavi" forKey:@"eventType"];
    NSInteger mode=naviMode;
    [obj setValue:[NSNumber numberWithInteger:mode]  forKey:@"data"];
    [self updateInfo:obj keep:YES callbackId:self.naviCallbackId ];
}

-(void)compositeManager:(AMapNaviCompositeManager *)compositeManager updateNaviLocation:(AMapNaviLocation *)naviLocation
{
    NSMutableDictionary* obj=[NSMutableDictionary dictionary];
    [obj setValue:@"locationChange" forKey:@"eventType"];
    NSMutableDictionary *l=[NSMutableDictionary dictionary];
    [l setValue:[NSNumber numberWithDouble:naviLocation.coordinate.latitude] forKey:@"lat"];
    [l setValue:[NSNumber numberWithDouble:naviLocation.coordinate.longitude]  forKey:@"lng"];
    [obj setValue:l  forKey:@"data"];
    [self updateInfo:obj keep:YES callbackId:self.naviCallbackId ];
}

#pragma mark - AMapLocationManager Delegate

- (void)amapLocationManager:(AMapLocationManager *)manager didUpdateLocation:(CLLocation *)location reGeocode:(AMapLocationReGeocode *)reGeocode
{
    NSLog(@"location:{lat:%f; lon:%f; accuracy:%f; reGeocode:%@}", location.coordinate.latitude, location.coordinate.longitude, location.horizontalAccuracy, reGeocode.formattedAddress);
    NSMutableDictionary *d=[self locationToDictionary:location regeocode:reGeocode];
    [self updateInfo:d keep:YES callbackId:self.locationCallbackId ];
}
- (void)initCompleteBlock
{
    __weak CDVAMap *weakSelf = self;
    self.completionBlock = ^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error)
    {
        [weakSelf stopLocation];
        if (error != nil && error.code == AMapLocationErrorLocateFailed)
        {
            //定位错误：此时location和regeocode没有返回值，不进行annotation的添加
            NSLog(@"定位错误:{%ld - %@};", (long)error.code, error.userInfo);
            NSMutableDictionary *d=[NSMutableDictionary dictionary];
            [d setValue:[NSNumber numberWithLong:error.code] forKey:@"ecode"];
            [d setValue:error.userInfo forKey:@"einfo"];
            [weakSelf updateInfo:d keep:NO callbackId:weakSelf.locationCallbackId];
            return;
        }
        else if (error != nil
                 && (error.code == AMapLocationErrorReGeocodeFailed
                     || error.code == AMapLocationErrorTimeOut
                     || error.code == AMapLocationErrorCannotFindHost
                     || error.code == AMapLocationErrorBadURL
                     || error.code == AMapLocationErrorNotConnectedToInternet
                     || error.code == AMapLocationErrorCannotConnectToHost))
        {
            //逆地理错误：在带逆地理的单次定位中，逆地理过程可能发生错误，此时location有返回值，regeocode无返回值，进行annotation的添加
            NSLog(@"逆地理错误:{%ld - %@};", (long)error.code, error.userInfo);
        }
        else if (error != nil && error.code == AMapLocationErrorRiskOfFakeLocation)
        {
            //存在虚拟定位的风险：此时location和regeocode没有返回值，不进行annotation的添加
            NSLog(@"存在虚拟定位的风险:{%ld - %@};", (long)error.code, error.userInfo);
            
            //存在虚拟定位的风险的定位结果
            __unused CLLocation *riskyLocateResult = [error.userInfo objectForKey:@"AMapLocationRiskyLocateResult"];
            //存在外接的辅助定位设备
            __unused NSDictionary *externalAccressory = [error.userInfo objectForKey:@"AMapLocationAccessoryInfo"];
            
            NSMutableDictionary *d=[NSMutableDictionary dictionary];
            [d setValue:[NSNumber numberWithLong:error.code] forKey:@"ecode"];
            [d setValue:error.userInfo forKey:@"einfo"];
            [weakSelf updateInfo:d keep:NO callbackId:weakSelf.locationCallbackId];
            return;
        }
        else
        {
            //没有错误：location有返回值，regeocode是否有返回值取决于是否进行逆地理操作，进行annotation的添加
        }
        
        NSMutableDictionary *d=[weakSelf locationToDictionary:location regeocode:regeocode];
        [weakSelf updateInfo:d keep:NO callbackId:weakSelf.locationCallbackId ];
    };
}

@end

