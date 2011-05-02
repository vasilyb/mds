package org.datacite.mds.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

/**
 * This is a wrapper around HttpServletResponse. By default the sendError
 * methods result in a html page generated by tomcat. This wrapper prints only
 * the error message to the response body (and sets the given response code)
 */
public class ApiResponseWrapper extends HttpServletResponseWrapper {

    Logger log = Logger.getLogger(ApiResponseWrapper.class);

    public ApiResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        log.debug("sendError " + sc + ": " + msg);
        setStatus(sc);
        getWriter().println(msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        log.debug("sendError " + sc);
        sendError(sc, "");
    }

}
