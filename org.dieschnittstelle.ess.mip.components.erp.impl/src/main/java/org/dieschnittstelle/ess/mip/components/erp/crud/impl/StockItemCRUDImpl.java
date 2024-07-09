package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@ApplicationScoped
@Transactional
@Logged
@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
public class StockItemCRUDImpl implements StockItemCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;

    @Override
    public StockItem createStockItem(StockItem item) {
        em.merge(item);
        return item;
    }

    @Override
    public StockItem readStockItem(IndividualisedProductItem prod, PointOfSale pos) {
        Query qu = em.createQuery("SELECT si FROM StockItem si WHERE si.product.id = " + prod.getId() + "AND si.pos.id =" + pos.getId());
        List<StockItem> stockItems = qu.getResultList();
        return !stockItems.isEmpty() ? stockItems.get(0) : null;
    }

    @Override
    public StockItem updateStockItem(StockItem item) {
        return em.merge(item);
    }

    @Override
    public List<StockItem> readStockItemsForProduct(IndividualisedProductItem prod) {
        Query qu = em.createQuery("SELECT si FROM StockItem si WHERE si.product.id = " + prod.getId());
        List<StockItem> stockItems = qu.getResultList();
        return stockItems;
    }

    @Override
    public List<StockItem> readStockItemsForPointOfSale(PointOfSale pos) {
        Query qu = em.createQuery("SELECT si FROM StockItem si WHERE si.pos.id = " + pos.getId());
        List<StockItem> stockItems = qu.getResultList();
        return stockItems;
    }
}
