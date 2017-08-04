package com.teammerge.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CORSFilter implements ContainerResponseFilter {
  @Override
  public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
    // Removing if there is any other domain attached
    response.getHttpHeaders().remove("Access-Control-Allow-Origin");

    // specifying the domain that is allowed access
    response.getHttpHeaders().add("Access-Control-Allow-Origin", "http://localhost");
    response.getHttpHeaders().add("Access-Control-Allow-Headers",
        "origin, content-type, accept, authorization");
    response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
    response.getHttpHeaders().add("Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, OPTIONS, HEAD");

    return response;
  }
}
