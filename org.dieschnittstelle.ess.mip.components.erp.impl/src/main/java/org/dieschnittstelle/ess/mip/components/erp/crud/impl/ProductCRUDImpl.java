package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional   // Alle Datenzugriffe können gemeinsam gemanaged werden
@Logged          // Bindet einen Interceptor bei Methodenaufrufe dieser Klasse ein
@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
public class ProductCRUDImpl implements ProductCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        em.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        Query qu = em.createQuery("SELECT DISTINCT prod FROM AbstractProduct prod");
        return qu.getResultList();
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        return em.merge(update);
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return em.find(AbstractProduct.class, productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        AbstractProduct product = em.find(AbstractProduct.class, productID);
        if (product != null) {
            em.remove(product);
            return true;
        }
        return false;
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        Query qu = em.createQuery("SELECT c FROM Campaign c JOIN c.bundles bundle WHERE bundle.product.id" +
                " = " + productID);
        return qu.getResultList();
    }
}
