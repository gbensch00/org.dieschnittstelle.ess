package org.dieschnittstelle.ess.jrs;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;

import static org.dieschnittstelle.ess.utils.Utils.show;

public class TouchpointCRUDServiceImpl implements ITouchpointCRUDService {

//    Annotation?
//    private ServletContext servletContext;
//    private HttpServletRequest request;
//    private GenericCRUDExecutor<AbstractTouchpoint> getCrudExecutor() {
//        show("getCRUDExecutor(): servletContext: %s", servletContext);
//        show("getCRUDExecutor(): request: %s", request);
//        return (GenericCRUDExecutor<AbstractTouchpoint>) servletContext.getAttribute("touchpointCRUD");
//    };

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(TouchpointCRUDServiceImpl.class);

    /**
     * this accessor will be provided by the ServletContext, to which it is written by the TouchpointServletContextListener
     */
    private GenericCRUDExecutor<AbstractTouchpoint> touchpointCRUD;

    /**
     * here we will be passed the context parameters by the resteasy framework. Alternatively @Context
     * can  be declared on the respective instance attributes. note that the request context is only
     * declared for illustration purposes, but will not be further used here
     *
     * @param servletContext
     */
    public TouchpointCRUDServiceImpl(@Context ServletContext servletContext, @Context HttpServletRequest request) {
        logger.info("<constructor>: " + servletContext + "/" + request);
        // read out the dataAccessor
        this.touchpointCRUD = (GenericCRUDExecutor<AbstractTouchpoint>) servletContext.getAttribute("touchpointCRUD");

        logger.debug("read out the touchpointCRUD from the servlet context: " + this.touchpointCRUD);
    }


    @Override
    public List<AbstractTouchpoint> readAllTouchpoints() {

        return (List) this.touchpointCRUD.readAllObjects();
        //return (List)getCrudExecutor().readAllObjects();
    }

    @Override
    public AbstractTouchpoint createTouchpoint(AbstractTouchpoint touchpoint) {
        return (AbstractTouchpoint) this.touchpointCRUD.createObject(touchpoint);

    }

    @Override
    public boolean deleteTouchpoint(long id) {
        return this.touchpointCRUD.deleteObject(id);
    }

    @Override
    public AbstractTouchpoint readTouchpoint(long id) {
        AbstractTouchpoint tp = (AbstractTouchpoint) this.touchpointCRUD.readObject(id);

        // this shows how JAX-RS WebApplicationException can be used to return HTTP error status codes
        // NOTE, HOWEVER, THAT FOR THE JRS EXERCISES null needs to be turned in case of non existence in order for the jUnit testcases
        // to work properly
        if (tp != null) {
            return tp;
        } else {
            throw new NotFoundException("The touchpoint with id " + id + " does not exist!");
        }
    }

    @Override
    public AbstractTouchpoint updateTouchpoint(long id, AbstractTouchpoint touchpoint) {
        touchpoint.setId(id);
        return this.touchpointCRUD.updateObject(touchpoint);
    }

    /*
     * UE JRS1: implement the method for updating touchpoints
     */

}