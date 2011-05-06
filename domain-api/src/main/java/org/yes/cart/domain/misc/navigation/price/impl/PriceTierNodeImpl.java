package org.yes.cart.domain.misc.navigation.price.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class PriceTierNodeImpl implements PriceTierNode {

    private Pair<BigDecimal, BigDecimal> priceRange;

    private List<PriceTierNode> priceSubRange;

    public Pair<BigDecimal, BigDecimal> getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(Pair<BigDecimal, BigDecimal> priceRange) {
        this.priceRange = priceRange;
    }

    public List<PriceTierNode> getPriceSubRange() {
        return priceSubRange;
    }

    public void setPriceSubRange(List<PriceTierNode> priceSubRange) {
        this.priceSubRange = priceSubRange;
    }

    public PriceTierNodeImpl(Pair<BigDecimal, BigDecimal> priceRange) {
        this.priceRange = priceRange;
    }

    public PriceTierNodeImpl(BigDecimal bottomBorder, BigDecimal topBorder) {
        this.priceRange = new Pair<BigDecimal, BigDecimal>(bottomBorder, topBorder);
    }
}