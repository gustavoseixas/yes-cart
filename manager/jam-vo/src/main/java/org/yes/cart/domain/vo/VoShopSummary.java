/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/01/2017
 * Time: 10:36
 */
public class VoShopSummary {

    private long shopId;

    private boolean disabled;

    private String code;
    private String name;
    private String themeChain;

    private List<MutablePair<String, Boolean>> categories = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> carriers = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> fulfilmentCentres = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> paymentGateways = new ArrayList<MutablePair<String, Boolean>>();
    private String paymentGatewaysIPsRegEx;

    private List<MutablePair<String, String>> locales = new ArrayList<MutablePair<String, String>>();

    private List<MutablePair<String, String>> currencies = new ArrayList<MutablePair<String, String>>();

    private List<MutablePair<String, String>> billingLocations = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, String>> shippingLocations = new ArrayList<MutablePair<String, String>>();

    private String previewUrl;
    private String previewCss;

    private MutablePair<String, String> primaryUrlAndThemeChain;
    private List<MutablePair<String, String>> aliasUrlAndThemeChain = new ArrayList<MutablePair<String, String>>();

    private MutablePair<String, Boolean> checkoutEnableGuest;
    private MutablePair<String, Boolean> checkoutEnableCoupons;
    private MutablePair<String, Boolean> checkoutEnableMessage;
    private MutablePair<String, Boolean> checkoutEnableQuanityPicker;

    private MutablePair<String, Boolean> taxEnableShow;
    private MutablePair<String, Boolean> taxEnableShowNet;
    private MutablePair<String, Boolean> taxEnableShowAmount;

    private MutablePair<String, Boolean> cookiePolicy;
    private MutablePair<String, Boolean> anonymousBrowsing;
    private MutablePair<String, String> customerSession;
    private List<MutablePair<String, String>> customerTypes = new ArrayList<MutablePair<String, String>>();
    private MutablePair<String, List<String>> customerTypesAbleToRegister;
    private MutablePair<String, List<String>> customerTypesRequireRegistrationApproval;
    private MutablePair<String, List<String>> customerTypesRequireRegistrationNotification;
    private MutablePair<String, List<String>> customerTypesSeeTax;
    private MutablePair<String, List<String>> customerTypesChangeTaxView;

    private List<MutablePair<String, Boolean>> emailTemplates = new ArrayList<MutablePair<String, Boolean>>();
    private List<MutablePair<String, String>> emailTemplatesFrom = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, String>> emailTemplatesTo = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, Boolean>> emailTemplatesShop = new ArrayList<MutablePair<String, Boolean>>();

    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getThemeChain() {
        return themeChain;
    }

    public void setThemeChain(final String themeChain) {
        this.themeChain = themeChain;
    }

    public List<MutablePair<String, Boolean>> getCategories() {
        return categories;
    }

    public void setCategories(final List<MutablePair<String, Boolean>> categories) {
        this.categories = categories;
    }

    public List<MutablePair<String, Boolean>> getCarriers() {
        return carriers;
    }

    public void setCarriers(final List<MutablePair<String, Boolean>> carriers) {
        this.carriers = carriers;
    }

    public List<MutablePair<String, Boolean>> getFulfilmentCentres() {
        return fulfilmentCentres;
    }

    public void setFulfilmentCentres(final List<MutablePair<String, Boolean>> fulfilmentCentres) {
        this.fulfilmentCentres = fulfilmentCentres;
    }

    public List<MutablePair<String, Boolean>> getPaymentGateways() {
        return paymentGateways;
    }

    public void setPaymentGateways(final List<MutablePair<String, Boolean>> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }

    public String getPaymentGatewaysIPsRegEx() {
        return paymentGatewaysIPsRegEx;
    }

    public void setPaymentGatewaysIPsRegEx(final String paymentGatewaysIPsRegEx) {
        this.paymentGatewaysIPsRegEx = paymentGatewaysIPsRegEx;
    }

    public List<MutablePair<String, String>> getLocales() {
        return locales;
    }

    public void setLocales(final List<MutablePair<String, String>> locales) {
        this.locales = locales;
    }

    public List<MutablePair<String, String>> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(final List<MutablePair<String, String>> currencies) {
        this.currencies = currencies;
    }

    public List<MutablePair<String, String>> getBillingLocations() {
        return billingLocations;
    }

    public void setBillingLocations(final List<MutablePair<String, String>> billingLocations) {
        this.billingLocations = billingLocations;
    }

    public List<MutablePair<String, String>> getShippingLocations() {
        return shippingLocations;
    }

    public void setShippingLocations(final List<MutablePair<String, String>> shippingLocations) {
        this.shippingLocations = shippingLocations;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(final String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPreviewCss() {
        return previewCss;
    }

    public void setPreviewCss(final String previewCss) {
        this.previewCss = previewCss;
    }

    public MutablePair<String, String> getPrimaryUrlAndThemeChain() {
        return primaryUrlAndThemeChain;
    }

    public void setPrimaryUrlAndThemeChain(final MutablePair<String, String> primaryUrlAndThemeChain) {
        this.primaryUrlAndThemeChain = primaryUrlAndThemeChain;
    }

    public List<MutablePair<String, String>> getAliasUrlAndThemeChain() {
        return aliasUrlAndThemeChain;
    }

    public void setAliasUrlAndThemeChain(final List<MutablePair<String, String>> aliasUrlAndThemeChain) {
        this.aliasUrlAndThemeChain = aliasUrlAndThemeChain;
    }

    public MutablePair<String, Boolean> getCheckoutEnableGuest() {
        return checkoutEnableGuest;
    }

    public void setCheckoutEnableGuest(final MutablePair<String, Boolean> checkoutEnableGuest) {
        this.checkoutEnableGuest = checkoutEnableGuest;
    }

    public MutablePair<String, Boolean> getCheckoutEnableCoupons() {
        return checkoutEnableCoupons;
    }

    public void setCheckoutEnableCoupons(final MutablePair<String, Boolean> checkoutEnableCoupons) {
        this.checkoutEnableCoupons = checkoutEnableCoupons;
    }

    public MutablePair<String, Boolean> getCheckoutEnableMessage() {
        return checkoutEnableMessage;
    }

    public void setCheckoutEnableMessage(final MutablePair<String, Boolean> checkoutEnableMessage) {
        this.checkoutEnableMessage = checkoutEnableMessage;
    }

    public MutablePair<String, Boolean> getCheckoutEnableQuanityPicker() {
        return checkoutEnableQuanityPicker;
    }

    public void setCheckoutEnableQuanityPicker(final MutablePair<String, Boolean> checkoutEnableQuanityPicker) {
        this.checkoutEnableQuanityPicker = checkoutEnableQuanityPicker;
    }

    public MutablePair<String, Boolean> getTaxEnableShow() {
        return taxEnableShow;
    }

    public void setTaxEnableShow(final MutablePair<String, Boolean> taxEnableShow) {
        this.taxEnableShow = taxEnableShow;
    }

    public MutablePair<String, Boolean> getTaxEnableShowNet() {
        return taxEnableShowNet;
    }

    public void setTaxEnableShowNet(final MutablePair<String, Boolean> taxEnableShowNet) {
        this.taxEnableShowNet = taxEnableShowNet;
    }

    public MutablePair<String, Boolean> getTaxEnableShowAmount() {
        return taxEnableShowAmount;
    }

    public void setTaxEnableShowAmount(final MutablePair<String, Boolean> taxEnableShowAmount) {
        this.taxEnableShowAmount = taxEnableShowAmount;
    }

    public MutablePair<String, Boolean> getCookiePolicy() {
        return cookiePolicy;
    }

    public void setCookiePolicy(final MutablePair<String, Boolean> cookiePolicy) {
        this.cookiePolicy = cookiePolicy;
    }

    public MutablePair<String, Boolean> getAnonymousBrowsing() {
        return anonymousBrowsing;
    }

    public void setAnonymousBrowsing(final MutablePair<String, Boolean> anonymousBrowsing) {
        this.anonymousBrowsing = anonymousBrowsing;
    }

    public MutablePair<String, String> getCustomerSession() {
        return customerSession;
    }

    public void setCustomerSession(final MutablePair<String, String> customerSession) {
        this.customerSession = customerSession;
    }

    public List<MutablePair<String, String>> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(final List<MutablePair<String, String>> customerTypes) {
        this.customerTypes = customerTypes;
    }

    public MutablePair<String, List<String>> getCustomerTypesAbleToRegister() {
        return customerTypesAbleToRegister;
    }

    public void setCustomerTypesAbleToRegister(final MutablePair<String, List<String>> customerTypesAbleToRegister) {
        this.customerTypesAbleToRegister = customerTypesAbleToRegister;
    }

    public MutablePair<String, List<String>> getCustomerTypesRequireRegistrationApproval() {
        return customerTypesRequireRegistrationApproval;
    }

    public void setCustomerTypesRequireRegistrationApproval(final MutablePair<String, List<String>> customerTypesRequireRegistrationApproval) {
        this.customerTypesRequireRegistrationApproval = customerTypesRequireRegistrationApproval;
    }

    public MutablePair<String, List<String>> getCustomerTypesRequireRegistrationNotification() {
        return customerTypesRequireRegistrationNotification;
    }

    public void setCustomerTypesRequireRegistrationNotification(final MutablePair<String, List<String>> customerTypesRequireRegistrationNotification) {
        this.customerTypesRequireRegistrationNotification = customerTypesRequireRegistrationNotification;
    }

    public MutablePair<String, List<String>> getCustomerTypesSeeTax() {
        return customerTypesSeeTax;
    }

    public void setCustomerTypesSeeTax(final MutablePair<String, List<String>> customerTypesSeeTax) {
        this.customerTypesSeeTax = customerTypesSeeTax;
    }

    public MutablePair<String, List<String>> getCustomerTypesChangeTaxView() {
        return customerTypesChangeTaxView;
    }

    public void setCustomerTypesChangeTaxView(final MutablePair<String, List<String>> customerTypesChangeTaxView) {
        this.customerTypesChangeTaxView = customerTypesChangeTaxView;
    }

    public List<MutablePair<String, Boolean>> getEmailTemplates() {
        return emailTemplates;
    }

    public void setEmailTemplates(final List<MutablePair<String, Boolean>> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    public List<MutablePair<String, String>> getEmailTemplatesFrom() {
        return emailTemplatesFrom;
    }

    public void setEmailTemplatesFrom(final List<MutablePair<String, String>> emailTemplatesFrom) {
        this.emailTemplatesFrom = emailTemplatesFrom;
    }

    public List<MutablePair<String, String>> getEmailTemplatesTo() {
        return emailTemplatesTo;
    }

    public void setEmailTemplatesTo(final List<MutablePair<String, String>> emailTemplatesTo) {
        this.emailTemplatesTo = emailTemplatesTo;
    }

    public List<MutablePair<String, Boolean>> getEmailTemplatesShop() {
        return emailTemplatesShop;
    }

    public void setEmailTemplatesShop(final List<MutablePair<String, Boolean>> emailTemplatesShop) {
        this.emailTemplatesShop = emailTemplatesShop;
    }
}
