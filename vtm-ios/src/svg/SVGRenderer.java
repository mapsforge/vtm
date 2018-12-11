package svg;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.ptr.Ptr;

@Library(Library.INTERNAL)
@NativeClass("SVGRenderer")
public class SVGRenderer extends SVGParser implements SVGContext, GHRenderable {
    public static class SVGRendererPtr extends Ptr<SVGRenderer, SVGRendererPtr> {
    }

    static {
        ObjCRuntime.bind(SVGRenderer.class);
    }/*</bind>*/

    public SVGRenderer() {
    }

    ;

    protected SVGRenderer(long handle) {
        super(handle);
    }

    protected SVGRenderer(SkipInit skipInit) {
        super(skipInit);
    }

    public SVGRenderer(String utf8String) {
        super((SkipInit) null);
        initObject(init(utf8String));
    }

    @Override
    @Method(selector = "initWithString:")
    protected native
    @Pointer
    long init(String utf8String);

    @Property(selector = "viewRect")
    public native
    @ByVal
    CGRect getViewRect();

    @Override
    @Method(selector = "colorForSVGColorString:")
    public native UIColor colorForSVGColorString(String svgColorString);

    @Override
    @Method(selector = "objectAtURL:")
    public native NSObject objectAtURL(String aLocation);

    @Override
    @Property(selector = "transform")
    public native CGAffineTransform getTransform();

    @Override
    @Property(selector = "hidden")
    public native boolean isHidden();

    @Method(selector = "asImageWithSize:andScale:")
    public native UIImage asImageWithSize(@ByVal CGSize maximumSize, @MachineSizedFloat double scale);
}
