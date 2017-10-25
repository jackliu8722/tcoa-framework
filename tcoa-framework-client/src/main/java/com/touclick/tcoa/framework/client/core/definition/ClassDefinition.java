package com.touclick.tcoa.framework.client.core.definition;

import com.touclick.tcoa.framework.commons.annotation.TcoaService;
import org.apache.thrift.protocol.TProtocol;

import java.lang.reflect.Constructor;

/**
 * tcoa service definition, include serviceId,version and thrift client
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class ClassDefinition {

    private String serviceId;

    private String version;

    private Class<?> serviceClientClass;

    private Constructor<?> serviceClientConstructor;

    public ClassDefinition(Class<?> serviceInterface) throws ClassNotFoundException,
            SecurityException,NoSuchMethodException{
        String clientClassName = resolveClientClassName(serviceInterface);
        this.serviceClientClass = Class.forName(clientClassName);
        resolveServiceId(serviceInterface);
        this.serviceClientConstructor = serviceClientClass.getConstructor(TProtocol.class);
    }

    /**
     * Resolve thrift client
     * @param serviceClass
     * @return
     */
    private String resolveClientClassName(Class<?> serviceClass){
        String packageName = serviceClass.getPackage().getName();
        String simpleClassName = serviceClass.getSimpleName();
        simpleClassName = simpleClassName.substring(1); //remove 'I'
        return packageName + "." + simpleClassName + "$Client";
    }

    /**
     * Resolve the serviceid and version
     * @param serviceClass
     */
    private void resolveServiceId(Class<?> serviceClass){
        TcoaService tcoaService = serviceClass.getAnnotation(TcoaService.class);
        this.serviceId = tcoaService != null ? tcoaService.serviceId().trim() : "";
        this.version = tcoaService != null ? tcoaService.version().trim() : "";
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getVersion() {
        return version;
    }

    public Class<?> getServiceClientClass() {
        return serviceClientClass;
    }

    public Constructor<?> getServiceClientConstructor() {
        return serviceClientConstructor;
    }
}
