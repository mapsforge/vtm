//
//  SVGTabBarItem.h
//  SVGgh
//
//  Created by Glenn Howes on 7/9/15.
//  Copyright © 2015 Generally Helpful. All rights reserved.
//

#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import UIKit;
#else
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#endif


NS_ASSUME_NONNULL_BEGIN

@interface SVGTabBarItem : UITabBarItem
/*! @property artworkPath
 * @brief optional subpath to an svg file inside the resources folder, svg is displayed inside the button at appropriate size
 * @attention do not append '.svg'
 */
@property(nonatomic, strong) IBInspectable NSString*           artworkPath;
/*! @property selectedArtworkPath
 * @brief optional subpath to an svg file inside the resources folder, svg is displayed inside the button at appropriate size. When button is in selected state.
 * @attention do not append '.svg'
 */
@property(nonatomic, strong) IBInspectable NSString*     __nullable    selectedArtworkPath;
/*! @property baseColor
 * @brief currentColor used when the tab is not selected
 */
@property(nonatomic, strong) IBInspectable UIColor*      __nullable    baseColor;
/*! @property baseColor
 * @brief currentColor used when the tab is selected
 */
@property(nonatomic, strong) IBInspectable UIColor*      __nullable   selectedColor;


+(void)makeSureLoaded;
@end


NS_ASSUME_NONNULL_END