package model;

import hibernate.Product;
import hibernate.Stock;
import java.util.Comparator;
import java.util.Iterator;

public class ProductComparator implements Comparator<Product> {

    private static ProductComparator pc;

    public final static int NEWLY_ADDED = 0;
    public final static int PRICE_LOW_TO_HIGH = 1;
    public final static int PRICE_HIGH_TO_LOW = 2;
    public final static int LOW_STOCKS = 3;
    private static int SORT_METHOD = NEWLY_ADDED;

    public static Comparator getComparator(int sortMethod) {
        SORT_METHOD = sortMethod;
        if (pc == null) {
            pc = new ProductComparator();
        }
        return pc;
    }

    @Override
    public int compare(Product p1, Product p2) {
        Stock stk1 = null;
        for (Iterator iterator = p1.getStocks().iterator(); iterator.hasNext();) {
            Stock st = (Stock) iterator.next();
            if (stk1 == null) {
                stk1 = st;
            } else if ((st.getSellingPrice()* (100 - st.getDiscount()) / 100) < (stk1.getSellingPrice() * (100 - stk1.getDiscount()) / 100)) {
                stk1 = st;
            }

        }
        Stock stk2 = null;
        for (Iterator iterator = p2.getStocks().iterator(); iterator.hasNext();) {
            Stock st = (Stock) iterator.next();
            if (stk2 == null) {
                stk2 = st;
            } else if ((st.getSellingPrice()* (100 - st.getDiscount()) / 100) < (stk2.getSellingPrice() * (100 - stk2.getDiscount()) / 100)) {
                stk2 = st;
            }

        }

        int val = 0;
        double sellPrice1 = stk1.getSellingPrice() * (100 - stk1.getDiscount()) / 100;
        double sellPrice2 = stk2.getSellingPrice() * (100 - stk2.getDiscount()) / 100;
        switch (SORT_METHOD) {
            case NEWLY_ADDED:
                val = stk2.getId() - stk1.getId();
                break;
            case PRICE_LOW_TO_HIGH:
                if (sellPrice1 > sellPrice2) {
                    val = 1;
                } else {
                    val = -1;
                }
                break;
            case PRICE_HIGH_TO_LOW:
                if (sellPrice1 > sellPrice2) {
                    val = -1;
                } else {
                    val = 1;
                }
                break;
            case LOW_STOCKS:
                val = stk1.getQty() - stk2.getQty();
                break;
        }
        return val;

    }
}
