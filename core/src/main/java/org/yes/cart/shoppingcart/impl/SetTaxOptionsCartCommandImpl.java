/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.util.Arrays;
import java.util.Map;

/**
 * User: denis
 */
public class SetTaxOptionsCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20170109L;

    private final CustomerService customerService;
    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerService customer service
     * @param shopService shop service
     */
    public SetTaxOptionsCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                        final CustomerService customerService,
                                        final ShopService shopService) {
        super(registry);
        this.shopService = shopService;
        this.customerService = customerService;
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SETTAXOPTIONS;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final Boolean show = Boolean.valueOf(String.valueOf(parameters.get(getCmdKey())));
            final Boolean net = parameters.containsKey(ShoppingCartCommand.CMD_SETTAXOPTIONS_P_NET) ?
                    Boolean.valueOf(String.valueOf(parameters.get(ShoppingCartCommand.CMD_SETTAXOPTIONS_P_NET))) : null;
            final Boolean amount = parameters.containsKey(ShoppingCartCommand.CMD_SETTAXOPTIONS_P_AMOUNT) ?
                    Boolean.valueOf(String.valueOf(parameters.get(ShoppingCartCommand.CMD_SETTAXOPTIONS_P_AMOUNT))) : null;
            if ((show != null && !show.equals(shoppingCart.getShoppingContext().isTaxInfoEnabled())) ||
                    (net != null && !net.equals(shoppingCart.getShoppingContext().isTaxInfoUseNet())) ||
                    (amount != null && !amount.equals(shoppingCart.getShoppingContext().isTaxInfoShowAmount()))) {

                final Shop shop = shopService.getById(shoppingCart.getShoppingContext().getShopId());

                Customer customer = null;
                if (shoppingCart.getLogonState() == ShoppingCart.LOGGED_IN) {
                    customer = customerService.getCustomerByEmail(shoppingCart.getCustomerEmail(), shop);
                }

                setTaxOptions(shop, customer, show, net, amount, shoppingCart.getShoppingContext());
                markDirty(shoppingCart);
            }
        }
    }


    protected void setTaxOptions(final Shop shop,
                                 final Customer customer,
                                 final boolean showTaxOption,
                                 final Boolean showNetOption,
                                 final Boolean showAmountOption,
                                 final MutableShoppingContext ctx) {

        // Resolve type. Anonymous type is B2G, blank is B2C
        final String customerType = customer == null ? "B2G" : (StringUtils.isBlank(customer.getCustomerType()) ? "B2C" : customer.getCustomerType());
        boolean showTax = Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO));
        if (showTax) {
            // If types limit is set then only enable showTax option for given types. Anonymous type is B2G, blank is B2C
            final String types = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CUSTOMER_TYPES);
            showTax = StringUtils.isBlank(types) || Arrays.asList(StringUtils.split(types, ',')).contains(customerType);
        }
        boolean showTaxNet = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET));
        boolean showTaxAmount = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT));

        ctx.setTaxInfoChangeViewEnabled(false);
        final String typesThatCanChangeView = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES);
        // Ensure change view is allowed for anonymous
        if (StringUtils.isNotBlank(typesThatCanChangeView)) {
            final String[] customerTypesThatCanChangeView = StringUtils.split(typesThatCanChangeView, ',');
            if (Arrays.asList(customerTypesThatCanChangeView).contains(customerType)) {
                ctx.setTaxInfoChangeViewEnabled(true);
            }
        }

        if (ctx.isTaxInfoChangeViewEnabled()) {
            showTax = showTaxOption;
            showTaxNet = showNetOption == null ? showTax && showTaxNet : showTax && showNetOption;
            showTaxAmount = showAmountOption == null ? showTax && showTaxAmount : showTax && showAmountOption;
        }

        ctx.setTaxInfoEnabled(showTax);
        ctx.setTaxInfoUseNet(showTaxNet);
        ctx.setTaxInfoShowAmount(showTaxAmount);

    }


}
