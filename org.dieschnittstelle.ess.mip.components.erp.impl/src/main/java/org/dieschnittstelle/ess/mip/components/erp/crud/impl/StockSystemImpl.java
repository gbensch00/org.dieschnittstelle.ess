package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.PointOfSaleCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.dieschnittstelle.ess.utils.Utils.show;

@ApplicationScoped
@Logged
@Transactional
public class StockSystemImpl implements StockSystem {

    @Inject
    private PointOfSaleCRUD pointOfSaleCRUD;

    @Inject
    private StockItemCRUD stockItemCRUD;

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pos = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);

        StockItem stockItem = stockItemCRUD.readStockItem(product, pos);
        if(stockItem != null) {
            stockItem.setUnits(stockItem.getUnits() + units);
            stockItemCRUD.updateStockItem(stockItem);
        }
        else if(units > 0){
            stockItem = new StockItem(product, pos, units);
            stockItemCRUD.createStockItem(stockItem);
        }
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        addToStock(product, pointOfSaleId, -units);

    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        PointOfSale pos = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        List<StockItem> stockItemList = stockItemCRUD.readStockItemsForPointOfSale(pos);
        List<IndividualisedProductItem> products = new ArrayList<>();
        for (StockItem currentStockItem : stockItemList) {
            products.add(currentStockItem.getProduct());
        }
        return products;
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        List<PointOfSale> posList = pointOfSaleCRUD.readAllPointsOfSale();
        Set<IndividualisedProductItem> setOfProducts = new HashSet<IndividualisedProductItem>();
        posList.forEach(pos -> {
            setOfProducts.addAll(getProductsOnStock(pos.getId()));
        });
        return new ArrayList<>(setOfProducts);
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        PointOfSale pos = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        StockItem stockItem = stockItemCRUD.readStockItem(product, pos);
        return stockItem != null ? stockItem.getUnits() : 0;
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        List<StockItem> stockItemList = stockItemCRUD.readStockItemsForProduct(product);
        AtomicInteger totalUnits = new AtomicInteger();
        stockItemList.forEach(item -> {
            totalUnits.addAndGet(item.getUnits());
        });

        return totalUnits.intValue();
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        List<StockItem> stockItems = stockItemCRUD.readStockItemsForProduct(product);


        List<Long> pointsOfSaleIds = new ArrayList<>();
        
        for (StockItem stockItem : stockItems) {
            pointsOfSaleIds.add(stockItem.getPos().getId());
        }

        // Return the list of point of sale IDs
        return pointsOfSaleIds;
    }
}
