//
//  SVGghLoader.h
//  SVGgh
//
//  Created by Glenn Howes on 4/27/16.
//  Copyright © 2016 Generally Helpful. All rights reserved.
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@class SVGRenderer;

@protocol SVGghLoader
/*! @brief method to retrieve an SVGRenderer
 * @param identifier (a subPath or an XCAsset name for instance
 * @param bundle usually nil
 * @return an SVGRender if one can be found
 */
-(nullable SVGRenderer*) loadRenderForSVGIdentifier:(NSString*)identifier inBundle:(nullable NSBundle*)bundle;
@end

typedef NS_ENUM(NSInteger, SVGghLoaderType)
{
    SVGghLoaderTypeDefault,
    SVGghLoaderTypePath,
    SVGghLoaderTypeDataXCAsset // only available on iOS 9 or above
};


@interface SVGghLoaderManager: NSObject
/*! @brief method to retrieve the loader used by the UI elements
 * @return the loader
 */
+(id<SVGghLoader>) loader;


/*! @brief method to retrieve an SVGRenderer
 * @param loader the loader to use by the widget classes.
 */
+(void) setLoader:(nullable id<SVGghLoader>)loader; // call only once, passing null will return to th

+(void) setLoaderToType:(SVGghLoaderType)type;

@end


NS_ASSUME_NONNULL_END