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
package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopEntity implements org.yes.cart.domain.entity.Shop, java.io.Serializable {

    private long shopId;
    private long version;

    private String code;
    private String name;
    private String description;
    private String fspointer;
    private boolean disabled;
    private Set<ShopUrl> shopUrl = new HashSet<ShopUrl>(0);
    private Collection<ShopAdvPlace> advertisingPlaces = new ArrayList<ShopAdvPlace>(0);
    private Collection<AttrValueShop> attributes = new ArrayList<AttrValueShop>(0);
    private SeoEntity seoInternal;
    private Collection<ShopCategory> shopCategory = new ArrayList<ShopCategory>(0);
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    // Below are internal caches as Shop object is long lived.

    private Map<String, AttrValueShop> attributesMap = null;
    private Map<String, List<AttrValueShop>> attributesMapMulti = null;

    private List<String> supportedCurrenciesAsList;
    private List<String> supportedShippingCountriesAsList;
    private List<String> supportedBillingCountriesAsList;
    private List<String> supportedLanguagesAsList;

    private Map<String, List<String>> supportedRegistrationFormAttributesByType = new HashMap<String, List<String>>();
    private Map<String, List<String>> supportedProfileFormAttributesByType = new HashMap<String, List<String>>();
    private Map<String, List<String>> supportedProfileFormReadOnlyAttributesByType = new HashMap<String, List<String>>();

    private Map<String, String> addressFormattingByTypeByLanguageByCountryCodeByCustomerType = new HashMap<String, String>();

    private Set<String> sfRequireCustomerRegistrationApprovalTypes;
    private Set<String> sfRequireCustomerRegistrationNotificationTypes;

    public ShopEntity() {
    }



    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFspointer() {
        return this.fspointer;
    }

    public void setFspointer(String fspointer) {
        this.fspointer = fspointer;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public Set<ShopUrl> getShopUrl() {
        return this.shopUrl;
    }

    public void setShopUrl(Set<ShopUrl> shopUrl) {
        this.shopUrl = shopUrl;
    }

    public Collection<ShopAdvPlace> getAdvertisingPlaces() {
        return this.advertisingPlaces;
    }

    public void setAdvertisingPlaces(Collection<ShopAdvPlace> advertisingPlaces) {
        this.advertisingPlaces = advertisingPlaces;
    }

    public Collection<AttrValueShop> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueShop> attributes) {
        this.attributes = attributes;
    }

    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<AttrValue>(attributes != null ? attributes : Collections.<AttrValue>emptyList());
    }

    private void resetAttributesMapsInternal() {
        final Map<String, AttrValueShop> rez = new HashMap<String, AttrValueShop>(this.attributes != null ? this.attributes.size() * 2 : 50);
        final Map<String, List<AttrValueShop>> rezMulti = new HashMap<String, List<AttrValueShop>>(this.attributes != null ? this.attributes.size() * 2 : 50);
        if (this.attributes != null) {
            for (AttrValueShop attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    final String code = attrValue.getAttribute().getCode();
                    rez.put(code, attrValue);
                    List<AttrValueShop> attrs = rezMulti.get(code);
                    if (attrs == null) {
                        attrs = new ArrayList<AttrValueShop>(1); // Most values are single, preserve memory
                        rezMulti.put(code, attrs);
                    }
                    attrs.add(attrValue);
                }
            }
        }
        this.attributesMap = rez;
        this.attributesMapMulti = rezMulti;
    }

    private Map<String, AttrValueShop> getAttributesMapInternal(final boolean reset) {
        if (this.attributesMap == null || reset) {
            this.resetAttributesMapsInternal();
        }
        return this.attributesMap;
    }

    private Map<String, List<AttrValueShop>> getAttributesMapMultiInternal(final boolean reset) {
        if (this.attributesMapMulti == null || reset) {
            this.resetAttributesMapsInternal();
        }
        return this.attributesMapMulti;
    }

    public Map<String, AttrValue> getAllAttributesAsMap() {
        return (Map) this.getAttributesMapInternal(false);
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    public Collection<ShopCategory> getShopCategory() {
        return this.shopCategory;
    }

    public void setShopCategory(Collection<ShopCategory> shopCategory) {
        this.shopCategory = shopCategory;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getShopId() {
        return this.shopId;
    }

    public long getId() {
        return this.shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public String getSupportedCurrencies() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES);
    }

    public String getDefaultCurrency() {
        List<String> currencies = getSupportedCurrenciesAsList();
        if (currencies != null && !currencies.isEmpty()) {
            return currencies.get(0);
        }
        return null;
    }

    public List<String> getSupportedCurrenciesAsList() {
        if (supportedCurrenciesAsList == null) {
            final String currencies = getSupportedCurrencies();
            if (currencies != null) {
                supportedCurrenciesAsList = Arrays.asList(StringUtils.split(currencies, ','));
            } else {
                supportedCurrenciesAsList = Collections.emptyList();
            }
        }
        return supportedCurrenciesAsList;
    }


    public String getSupportedShippingCountries() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_COUNTRY_SHIP);
    }

    public List<String> getSupportedShippingCountriesAsList() {
        if (supportedShippingCountriesAsList == null) {
            final String countries = getSupportedShippingCountries();
            if (countries != null) {
                supportedShippingCountriesAsList = Arrays.asList(StringUtils.split(countries, ','));
            } else {
                supportedShippingCountriesAsList = Collections.emptyList();
            }

        }
        return supportedShippingCountriesAsList;
    }

    public String getSupportedBillingCountries() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_COUNTRY_BILL);
    }

    public List<String> getSupportedBillingCountriesAsList() {
        if (supportedBillingCountriesAsList == null) {
            final String countries = getSupportedBillingCountries();
            if (countries != null) {
                supportedBillingCountriesAsList = Arrays.asList(StringUtils.split(countries, ','));
            } else {
                supportedBillingCountriesAsList = Collections.emptyList();
            }
        }
        return supportedBillingCountriesAsList;
    }

    public String getSupportedLanguages() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_LANGUAGES);
    }

    public List<String> getSupportedLanguagesAsList() {
        if (supportedLanguagesAsList == null) {
            final String languages = getSupportedLanguages();
            if (languages != null) {
                supportedLanguagesAsList = Arrays.asList(StringUtils.split(languages, ','));
            } else {
                supportedLanguagesAsList = Collections.emptyList();
            }
        }
        return supportedLanguagesAsList;
    }

    public String getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType(final String countryCode, final String locale, final String customerType, final String addressType) {

        final String countryKey = StringUtils.isNotBlank(customerType) ? countryCode + "_" + customerType : countryCode;
        final String formatKey = addressType + "#" + locale + "#" + countryKey;

        String format = addressFormattingByTypeByLanguageByCountryCodeByCustomerType.get(formatKey);

        if (format == null) {

            final String addressTypePrefix = "_" + addressType;

            if (StringUtils.isNotBlank(customerType)) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + locale + "_" + customerType);
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + customerType);
                }
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX);
            }
            addressFormattingByTypeByLanguageByCountryCodeByCustomerType.put(formatKey, format);
        }
        return format;
    }

    public String getSupportedRegistrationFormAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX + '_' + customerType);
    }

    public List<String> getSupportedRegistrationFormAttributesAsList(final String customerType) {
        List<String> supportedRegistrationFormAttributesAsList = this.supportedRegistrationFormAttributesByType.get(customerType);
        if (supportedRegistrationFormAttributesAsList == null) {
            final String attrs = getSupportedRegistrationFormAttributes(customerType);
            if (attrs != null) {
                supportedRegistrationFormAttributesAsList = Arrays.asList(StringUtils.split(attrs, ','));
            } else {
                supportedRegistrationFormAttributesAsList = Collections.emptyList();
            }
            this.supportedRegistrationFormAttributesByType.put(customerType, supportedRegistrationFormAttributesAsList);
        }
        return supportedRegistrationFormAttributesAsList;
    }

    public String getSupportedProfileFormAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX + '_' + customerType);
    }

    public List<String> getSupportedProfileFormAttributesAsList(final String customerType) {
        List<String> supportedProfileFormAttributesAsList = this.supportedProfileFormAttributesByType.get(customerType);
        if (supportedProfileFormAttributesAsList == null) {
            final String attrs = getSupportedProfileFormAttributes(customerType);
            if (attrs != null) {
                supportedProfileFormAttributesAsList = Arrays.asList(StringUtils.split(attrs, ','));
            } else {
                supportedProfileFormAttributesAsList = Collections.emptyList();
            }
            this.supportedProfileFormAttributesByType.put(customerType, supportedProfileFormAttributesAsList);
        }
        return supportedProfileFormAttributesAsList;
    }

    public String getSupportedProfileFormReadOnlyAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX + '_' + customerType);
    }

    public List<String> getSupportedProfileFormReadOnlyAttributesAsList(final String customerType) {
        List<String> supportedProfileFormReadOnlyAttributesAsList = this.supportedProfileFormReadOnlyAttributesByType.get(customerType);
        if (supportedProfileFormReadOnlyAttributesAsList == null) {
            final String attrs = getSupportedProfileFormReadOnlyAttributes(customerType);
            if (attrs != null) {
                supportedProfileFormReadOnlyAttributesAsList = Arrays.asList(StringUtils.split(attrs, ','));
            } else {
                supportedProfileFormReadOnlyAttributesAsList = Collections.emptyList();
            }
            this.supportedProfileFormReadOnlyAttributesByType.put(customerType, supportedProfileFormReadOnlyAttributesAsList);
        }
        return supportedProfileFormReadOnlyAttributesAsList;
    }

    public Collection<AttrValueShop> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueShop> result = new ArrayList<AttrValueShop>();
        if (attributeCode != null) {
            final List<AttrValueShop> list = this.getAttributesMapMultiInternal(false).get(attributeCode); // build maps
            if (list != null) {
                result.addAll(list);
            }
        }
        return result;
    }

    public AttrValueShop getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        return this.getAttributesMapInternal(false).get(attributeCode);
    }

    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValueShop val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }

    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValueShop val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }

    public boolean isB2BProfileActive() {
        return isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B);
    }

    public boolean isSfRequireCustomerLogin() {
        return isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_LOGIN);
    }

    private Set<String> getSfRequireCustomerRegistrationApprovalTypes() {
        if (this.sfRequireCustomerRegistrationApprovalTypes == null) {
            final String attrs = getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_APPROVE);
            if (attrs != null) {
                this.sfRequireCustomerRegistrationApprovalTypes = new HashSet<String>(Arrays.asList(StringUtils.split(attrs, ',')));
            } else {
                this.sfRequireCustomerRegistrationApprovalTypes = Collections.emptySet();
            }
        }
        return this.sfRequireCustomerRegistrationApprovalTypes;
    }

    public boolean isSfRequireCustomerRegistrationApproval(final String customerType) {
        return getSfRequireCustomerRegistrationApprovalTypes().contains(customerType);
    }


    private Set<String> getSfRequireCustomerRegistrationNotificationTypes() {
        if (this.sfRequireCustomerRegistrationNotificationTypes == null) {
            final String attrs = getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_NOFIICATION);
            if (attrs != null) {
                this.sfRequireCustomerRegistrationNotificationTypes = new HashSet<String>(Arrays.asList(StringUtils.split(attrs, ',')));
            } else {
                this.sfRequireCustomerRegistrationNotificationTypes = Collections.emptySet();
            }
        }
        return this.sfRequireCustomerRegistrationNotificationTypes;
    }

    public boolean isSfRequireCustomerRegistrationNotification(final String customerType) {
        return getSfRequireCustomerRegistrationNotificationTypes().contains(customerType);
    }


    public String getDefaultShopUrl() {
        return getDefaultShopUrlWithProtocol("http://");
    }

    public String getDefaultShopSecureUrl() {
        return getDefaultShopUrlWithProtocol("https://");
    }

    protected String getDefaultShopUrlWithProtocol(final String protocol) {
        for (ShopUrl shopUrl : getShopUrl()) {
            if (shopUrl.isPrimary()) {
                return protocol + shopUrl.getUrl();
            }
        }
        for (ShopUrl shopUrl : getShopUrl()) {
            if (shopUrl.getUrl().endsWith("localhost") || shopUrl.getUrl().contains("127.0.0.1")) {
                continue;
            }
            return protocol + shopUrl.getUrl();
        }
        return "";
    }


    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }


    /** {@inheritDoc} */
    public int hashCode() {
        return (int) (shopId ^ (shopId >>> 32));
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getShopId();
    }

}


