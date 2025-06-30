package com.puentenet.domain.exceptions;

/**
 * Excepción personalizada para cuando un servicio externo no está disponible
 */
public class ServiceUnavailableException extends RuntimeException {
    
    private final String serviceName;
    private final String endpoint;
    
    public ServiceUnavailableException(String message) {
        super(message);
        this.serviceName = null;
        this.endpoint = null;
    }
    
    public ServiceUnavailableException(String message, String serviceName) {
        super(message);
        this.serviceName = serviceName;
        this.endpoint = null;
    }
    
    public ServiceUnavailableException(String message, String serviceName, String endpoint) {
        super(message);
        this.serviceName = serviceName;
        this.endpoint = endpoint;
    }
    
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = null;
        this.endpoint = null;
    }
    
    public ServiceUnavailableException(String message, String serviceName, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.endpoint = null;
    }
    
    public ServiceUnavailableException(String message, String serviceName, String endpoint, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.endpoint = endpoint;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
} 