package de.storagesystem.api.properties;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


/**
 * @author Simon Brebeck on 24.11.2022
 */

@Validated
@Component
public class ServerProperty {

    @NotBlank
    private String prefix;

    @NotBlank
    private String name;

    @NotBlank
    private String host;

    @Min(1024)
    @Max(65535)
    private int port;

    @NotBlank
    private String protocol;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
