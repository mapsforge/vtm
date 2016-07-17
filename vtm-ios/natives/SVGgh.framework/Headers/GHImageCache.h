//
//  SVGgh.h
//  GHImageCache.h
// The MIT License (MIT)

//  Copyright (c) 2011-2014 Glenn R. Howes

//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//
//  Created by Glenn Howes on 10/5/13.


#if defined(__has_feature) && __has_feature(modules)
@import Foundation;
@import CoreGraphics;
@import UIKit;
#else
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <UIKit/UIKit.h>
#endif

NS_ASSUME_NONNULL_BEGIN

/*! @brief definition of a block callback to handle the retrieval of an object
* @param anImage resulting image
* @param location where image was located
*/
typedef void (^handleRetrievedImage_t)(UIImage* __nullable  anImage, NSURL* __nullable  location);

/*! @brief  definition of a block callback after a list of faces (and locations) were extracted from a parent image
 * @param error if failed this will be non-nil
 * @param images resulting images each with a face
 * @param locations where images are now located
 
 */
typedef void (^handleExtractedFaces_t)( NSError* __nullable  error,  NSArray* __nullable  images, NSArray* __nullable  locations);

/*! @brief  instance-less class which caches images
* @note uses NSCache to handle low memory warnings.
 * @see NSCache
*/
@interface GHImageCache : NSObject

/*! @brief  method to store an image which can savely thrown away under low memory
 * @param anImage image to cache
 * @param aName unique name to associate with this image
 
*/
+(void) cacheImage:(UIImage*)anImage forName:(NSString*)aName;

/*! @brief method to remove an image from the cache
 * @param aName unique name of the image to be removed
*/

+(void) invalidateImageWithName:(NSString*)aName;

/*! @brief  retrieve the image of the given name
* @warning May return nil if there was a low memory situation
* @param uniqueName the unique name of the image to retrieve
* @return retrieved UIImage
 
*/
+(nullable UIImage*) uncacheImageForName:(NSString*)uniqueName;

/*! @brief  if you have an image in an NSData this will create an image, store it and return it
* @param imageData data in some standard format like PNG or JPEG
* @param preferredName what you want to call it.
* @param callback block to get called to when the operation is completed
 
*/
+(void) saveImageData:(NSData*)imageData withName:(NSString*)preferredName withCallback:(handleRetrievedImage_t)callback;

/*! @brief  store an image with a URL
* @param anImage to be stored, maybe be nil
* @param aFileURL to allow reload, cannot be nil
*/
+(void) setCachedImage:(nullable UIImage*)anImage forURL:(NSURL*) aFileURL;

/*! @brief  makes a filename that is unigue (via a GUID) with a given extension
* @param extension extension like "jpg"
* @return fileName
*/
+(NSString*) uniqueFilenameWithExtension:(NSString*)extension;

/*! @brief  return an image either from the memory cache, or from the provided URL
* @attention synchronous even though it takes a callback
* @param aURL file based URL which references an image
* @param retrievalCallback callback to accept the resulting image
*/
+(void) retrieveCachedImageFromURL:(NSURL*)aURL intoCallback:(handleRetrievedImage_t)retrievalCallback;

/*! @brief  using a separate operation queue to grab the image either from the cache or the url
* @param aURL file URL to grab the image from if need be and to use as cache key
* @param retrievalCallback to accetp the resulting image
*/
+(void) aSyncRetrieveCachedImageFromURL:(NSURL*)aURL intoCallback:(handleRetrievedImage_t)retrievalCallback;

/*! @brief  this will take an image, and extract faces from it, put them in the cache and store them to disk
* @param anImage image which may have human faces in it.
* @param callback returns a possible error or the faces and their locations
*/
+(void) extractFaceImageFromPickedImage:(UIImage*) anImage withCallback:(handleExtractedFaces_t)callback;
@end

/*! @brief  If you want to be notified when an image is added to the cache use NSNotificationCenter to register this string
 @memberof GHImageCache
 */
extern NSString*  const kImageAddedToCacheNotificationName; // does not include faces being added via the picker.
    extern NSString*  const kImageAddedKey;
    extern NSString*  const kImageURLAddedKey;

/*! @brief  If you want to be notified when faces (plural) is added to the cache use NSNotificationCenter to register this string
  @memberof GHImageCache
 */
extern  NSString*  const kFacesAddedToCacheNotificationName;
    extern  NSString*  const kFacesAddedKey;
    extern  NSString*  const kFacesURLsAddedKey;


extern const CGColorRenderingIntent	kColoringRenderingIntent;

NS_ASSUME_NONNULL_END
