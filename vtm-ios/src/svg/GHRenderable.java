package svg;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Property;

public interface GHRenderable extends NSObjectProtocol{
    @Property(selector = "transform")
    public CGAffineTransform getTransform();
    
    @Property(selector = "hidden")
    public boolean isHidden();
    

}
