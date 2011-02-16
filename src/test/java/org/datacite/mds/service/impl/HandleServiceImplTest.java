package org.datacite.mds.service.impl;

import static org.easymock.EasyMock.*;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractRequest;
import net.handle.hdllib.GenericResponse;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

import org.datacite.mds.service.HandleException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class HandleServiceImplTest {

    @Autowired
    private HandleServiceImpl service;

    @Before
    public void setup() {
        service.dummyMode = false;
        service.resolver = createMock(HandleResolver.class);
    }
    
    @After
    public void tearDown() {
        service.dummyMode = true;
    }

    private static final String doi = "10.5072/test";
    private static final String url = "http://example.com";

    @Test
    public void testCreate() throws Exception {
        mockResponseCode(AbstractMessage.RC_SUCCESS);
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testCreateError() throws Exception {
        mockResponseCode(AbstractMessage.RC_ERROR);
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testCreateException() throws Exception {
        mockResponseException();
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegalArgument1() throws HandleException {
        service.create(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegalArgument2() throws HandleException {
        service.create("", "");
    }

    @Test
    public void testUpdate() throws Exception {
        mockExistingHandle();
        mockResponseCode(AbstractMessage.RC_SUCCESS);
        replay(service.resolver);
        service.update(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testUpdateNonExistingHandle() throws Exception {
        mockNonExistingHandle();
        replay(service.resolver);
        service.update(doi, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateIllegalArgument1() throws HandleException {
        service.update(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateIllegalArgument2() throws HandleException {
        service.update("", "");
    }

    void mockResponseCode(int respondeCode) throws net.handle.hdllib.HandleException {
        GenericResponse response = new GenericResponse(0, respondeCode);
        expect(service.resolver.processRequest(anyObject(AbstractRequest.class))).andReturn(response);
    }

    void mockResponseException() throws net.handle.hdllib.HandleException {
        net.handle.hdllib.HandleException ex = new net.handle.hdllib.HandleException(0);
        expect(service.resolver.processRequest(anyObject(AbstractRequest.class))).andThrow(ex);
    }

    void mockExistingHandle() throws net.handle.hdllib.HandleException {
        HandleValue[] values = { new HandleValue(1, "URL".getBytes(), url.getBytes()) };
        expect(service.resolver.resolveHandle(eq(doi), anyObject(String[].class), anyObject(int[].class)))//
                .andReturn(values);
    }

    void mockNonExistingHandle() throws net.handle.hdllib.HandleException {
        expect(service.resolver.resolveHandle(eq(doi), anyObject(String[].class), anyObject(int[].class)))//
                .andThrow(
                        new net.handle.hdllib.HandleException(net.handle.hdllib.HandleException.HANDLE_DOES_NOT_EXIST));
    }

}
