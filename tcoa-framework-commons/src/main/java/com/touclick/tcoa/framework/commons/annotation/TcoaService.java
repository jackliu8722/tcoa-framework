package com.touclick.tcoa.framework.commons.annotation;

import java.lang.annotation.*;

/**
 * Annotation for service
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TcoaService {

    /**
     * Service id
     * @return
     */
    public String serviceId();

    /**
     * Version
     * @return
     */
    public String version() default "1.0.0";
}
