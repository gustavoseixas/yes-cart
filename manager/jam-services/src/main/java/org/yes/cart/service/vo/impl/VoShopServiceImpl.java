/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoShopUrlService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.misc.CurrencyService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoShopService;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

    private final DtoShopService dtoShopService;
    private final DtoShopUrlService dtoShopUrlService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private final LanguageService languageService;
    private final CurrencyService currencyService;
    private final CountryService countryService;
    private final SystemService systemService;
    private final ThemeService themeService;

    private final VoAttributesCRUDTemplate<VoAttrValueShop, AttrValueShopDTO> voAttributesCRUDTemplate;

    private Set<String> skipAttributesInView = Collections.emptySet();

    /**
     * Construct service.
     * @param languageService languages
     * @param currencyService currencies
     * @param countryService  locations
     * @param dtoShopUrlService underlying service to work with shop urls.
     * @param dtoShopService    underlying service to use.
     * @param dtoAttributeService attribute service
     * @param federationFacade  access.
     * @param voAssemblySupport vo support
     * @param voIOSupport vo support
     * @param systemService system service
     * @param themeService
     */
    public VoShopServiceImpl(final LanguageService languageService,
                             final CurrencyService currencyService,
                             final CountryService countryService,
                             final DtoShopUrlService dtoShopUrlService,
                             final DtoShopService dtoShopService,
                             final DtoAttributeService dtoAttributeService,
                             final FederationFacade federationFacade,
                             final VoAssemblySupport voAssemblySupport,
                             final VoIOSupport voIOSupport,
                             final SystemService systemService,
                             final ThemeService themeService) {
        this.currencyService = currencyService;
        this.countryService = countryService;
        this.dtoShopUrlService = dtoShopUrlService;
        this.dtoShopService = dtoShopService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.languageService = languageService;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;
        this.systemService = systemService;
        this.themeService = themeService;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueShop, AttrValueShopDTO>(
                        VoAttrValueShop.class,
                        AttrValueShopDTO.class,
                        Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN,
                        this.dtoShopService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
        {
            @Override
            protected boolean skipAttributesInView(final String code) {
                return skipAttributesInView.contains(code);
            }

            @Override
            protected long determineObjectId(final VoAttrValueShop vo) {
                return vo.getShopId();
            }

            @Override
            protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId) throws Exception {
                boolean accessible = federationFacade.isShopAccessibleByCurrentManager(objectId);
                if (!accessible) {
                    return new Pair<>(false, null);
                }
                final ShopDTO shop = dtoShopService.getById(objectId);
                return new Pair<>(true, shop.getCode());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public List<VoShop> getAll() throws Exception {
        final List<ShopDTO> all = dtoShopService.getAll();
        federationFacade.applyFederationFilter(all, ShopDTO.class);
        return voAssemblySupport.assembleVos(VoShop.class, ShopDTO.class, all);
    }

    /**
     * {@inheritDoc}
     */
    public VoShop getById(long id) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(id);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            return voAssemblySupport.assembleVo(VoShop.class, ShopDTO.class, new VoShop(), shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShop update(VoShop vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.update(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShop.class, shopDTO, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getById(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    public VoShop create(VoShop vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            ShopDTO shopDTO = dtoShopService.getNew();
            shopDTO = dtoShopService.create(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShop.class, shopDTO, vo)
            );
            return getById(shopDTO.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoShopService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fillShopSummaryDetails(final VoShopSummary summary, final long shopId, final String lang) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {

            addMainInfo(summary, shopId, shopDTO);

            addShopLocales(summary, shopId);

            addShopCurrencies(summary, shopId);

            addShopLocations(summary, shopId);

            addShopUrls(summary, shopId);

            final Map<String, VoAttrValueShop> attrsMap = getStringVoAttrValueShopMap(shopId);

            addCheckoutConfig(summary, lang, attrsMap);

            addTaxConfig(summary, lang, attrsMap);

            addCustomerConfig(summary, lang, attrsMap);

            addEmailTemplatesBasicSettings(summary, lang, attrsMap);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    protected void addMainInfo(final VoShopSummary summary, final long shopId, final ShopDTO shopDTO) {
        summary.setShopId(shopId);
        summary.setDisabled(shopDTO.isDisabled());
        summary.setCode(shopDTO.getCode());
        summary.setName(shopDTO.getName());
        summary.setThemeChain(StringUtils.join(themeService.getThemeChainByShopId(shopId, null), " > "));
    }

    protected void addCustomerConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setCookiePolicy(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_COOKIE_POLICY_ENABLE, lang, false));
        summary.setAnonymousBrowsing(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_LOGIN, lang, true));

        final MutablePair<String, String> sessionExpiry = getShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS, lang, "21600");
        int sessionExpirySeconds = NumberUtils.toInt(sessionExpiry.getFirst());
        String time = "6h";
        if (sessionExpirySeconds > 3600) { // more than hour
            time = new BigDecimal(sessionExpirySeconds).divide(new BigDecimal(60 * 60), 1, BigDecimal.ROUND_HALF_EVEN).toPlainString() + "h";
        } else if (sessionExpirySeconds > 60) {  // more than minute
            time = new BigDecimal(sessionExpirySeconds).divide(new BigDecimal(60), 1, BigDecimal.ROUND_HALF_EVEN).toPlainString() + "m";
        }
        summary.setCustomerSession(MutablePair.of(sessionExpiry.getFirst(), time));

        final Set<String> knownCustomerTypes = new HashSet<String>();
        final VoAttrValueShop registrationTypesCsv = attrsMap.get(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES);
        if (registrationTypesCsv != null && StringUtils.isNotBlank(registrationTypesCsv.getVal())) {

            final String[] registrationTypes = StringUtils.split(registrationTypesCsv.getVal(), ',');
            final String[] registrationTypesNames = StringUtils.split(
                    getDisplayName(registrationTypesCsv.getDisplayVals(), registrationTypesCsv.getVal(), lang), ',');

            for (int i = 0; i < registrationTypes.length; i++) {
                final MutablePair<String, String> typeAndName = MutablePair.of(
                        registrationTypes[i],
                        registrationTypesNames.length > i ? registrationTypesNames[i] : registrationTypes[i]
                );
                knownCustomerTypes.add(typeAndName.getFirst());
                summary.getCustomerTypes().add(typeAndName);
            }
            if (!knownCustomerTypes.contains("B2G")) {
                knownCustomerTypes.add("B2G");
                summary.getCustomerTypes().add(MutablePair.of("B2G", "-"));
            }

        }

        final MutablePair<String, List<String>> ableToRegister =
                getCsvShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES, lang);
        final MutablePair<String, List<String>> approveRegister =
                getCsvShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_APPROVE, lang);
        final MutablePair<String, List<String>> notifyRegister =
                getCsvShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_NOFIICATION, lang);
        final MutablePair<String, List<String>> seeTax =
                getCsvShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CUSTOMER_TYPES, lang);
        final MutablePair<String, List<String>> changeTax =
                getCsvShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES, lang);

        final Set<String> additionalTypes = new HashSet<String>();
        additionalTypes.addAll(ableToRegister.getSecond());
        additionalTypes.addAll(approveRegister.getSecond());
        additionalTypes.addAll(notifyRegister.getSecond());
        additionalTypes.addAll(seeTax.getSecond());
        additionalTypes.addAll(changeTax.getSecond());
        if (CollectionUtils.isNotEmpty(additionalTypes)) {
            additionalTypes.removeAll(knownCustomerTypes);
            if (!additionalTypes.isEmpty()) {
                for (final String newType : additionalTypes) {
                    knownCustomerTypes.add(newType);
                    summary.getCustomerTypes().add(MutablePair.of(newType, newType));
                }
            }
        }

        summary.setCustomerTypesAbleToRegister(ableToRegister);
        summary.setCustomerTypesRequireRegistrationApproval(approveRegister);
        summary.setCustomerTypesRequireRegistrationNotification(notifyRegister);
        summary.setCustomerTypesSeeTax(seeTax);
        summary.setCustomerTypesChangeTaxView(changeTax);
    }

    protected void addTaxConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setTaxEnableShow(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO, lang, false));
        summary.setTaxEnableShowNet(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET, lang, false));
        summary.setTaxEnableShowAmount(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT, lang, false));
    }

    protected void addCheckoutConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setCheckoutEnableGuest(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_CHECKOUT_ENABLE_GUEST, lang, false));
        summary.setCheckoutEnableCoupons(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS, lang, false));
        summary.setCheckoutEnableMessage(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG, lang, false));
        summary.setCheckoutEnableQuanityPicker(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_ADD_ENABLE_QTY_PICKER, lang, false));
    }

    protected Map<String, VoAttrValueShop> getStringVoAttrValueShopMap(final long shopId) throws Exception {
        final List<VoAttrValueShop> attrs = getShopAttributes(shopId);
        final Map<String, VoAttrValueShop> attrsMap = new HashMap<String, VoAttrValueShop>(attrs.size() * 2);
        for (final VoAttrValueShop attr : attrs) {
            attrsMap.put(attr.getAttribute().getCode(), attr);
        }
        return attrsMap;
    }

    protected void addShopUrls(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopUrl urls = getShopUrls(shopId);
        summary.setPreviewUrl(urls.getPreviewUrl());
        summary.setPreviewCss(urls.getPreviewCss());
        for (final VoShopUrlDetail url : urls.getUrls()) {
            if (url.isPrimary()) {
                summary.setPrimaryUrlAndThemeChain(
                        MutablePair.of(
                                url.getUrl(),
                                StringUtils.join(themeService.getThemeChainByShopId(shopId, url.getUrl()), " > ")
                        )
                );
            } else {
                summary.getAliasUrlAndThemeChain().add(
                        MutablePair.of(
                                url.getUrl(),
                                StringUtils.join(themeService.getThemeChainByShopId(shopId, url.getUrl()), " > ")
                        )
                );
            }
        }
    }

    protected void addShopLocations(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopLocations loc = getShopLocations(shopId);
        for (final MutablePair<String, String> codeAndName : loc.getAll()) {
            if (loc.getSupportedBilling().contains(codeAndName.getFirst())) {
                summary.getBillingLocations().add(codeAndName);
            }
            if (loc.getSupportedShipping().contains(codeAndName.getFirst())) {
                summary.getShippingLocations().add(codeAndName);
            }
        }
    }

    protected void addShopCurrencies(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopSupportedCurrencies curr = getShopCurrencies(shopId);
        for (final String code : curr.getSupported()) {
            summary.getCurrencies().add(MutablePair.of(code, code));
        }
    }

    protected void addShopLocales(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopLanguages langs = getShopLanguages(shopId);
        for (final String code : langs.getSupported()) {
            for (final MutablePair langAndName : langs.getAll()) {
                if (langAndName.getFirst().equals(code)) {
                    summary.getLocales().add(langAndName);
                }
            }
        }
    }

    protected void addEmailTemplatesBasicSettings(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {

        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-cant-allocate-product-qty");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-contactform-request");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-customer-registered");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-newsletter-request");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-canceled");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-confirmed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-allocated");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-inprogress");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-inprogress-wait");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-packing");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-ready");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-ready-wait");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-new");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-payment-confirmed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-returned");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-shipping-completed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-wait-confirmation");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-failed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-shipped");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-shipped-failed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-refund");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-refund-failed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-activation");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-change-password");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-deactivation");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-registered");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-canceled");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-confirmed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-delivery-readytoshipping");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-delivery-shipped");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-new");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-returned");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-shipping-completed");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "payment");
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "shipment-complete");

    }

    protected void addEmailTemplateBasicSettings(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap, final String template) {

        summary.getEmailTemplates().add(MutablePair.of(template, Boolean.FALSE));
        final MutablePair<String, String> shopAdmin = getShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL, lang, "");
        summary.getEmailTemplatesFrom().add(MutablePair.of(template, shopAdmin.getSecond()));
        summary.getEmailTemplatesTo().add(MutablePair.of(template, template.startsWith("adm-") ? shopAdmin.getSecond() : "-"));
        summary.getEmailTemplatesShop().add(MutablePair.of(template, Boolean.FALSE));

    }

    private String getDisplayName(final List<MutablePair<String, String>> names, final String defName, final String lang) {
        if (CollectionUtils.isNotEmpty(names)) {
            for (final MutablePair<String, String> name : names) {
                if (lang.equals(name.getFirst())) {
                    return name.getSecond();
                }
            }
        }
        return defName;
    }

    private MutablePair<String, Boolean> getBooleanShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang,  final boolean inverse) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(attr, !inverse);
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, Boolean.valueOf(attr.getVal()) ? !inverse : inverse);
    }

    private MutablePair<String, String> getShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang, final String def) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(attr, def);
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, StringUtils.isNotBlank(attr.getVal()) ? attr.getVal() : def);
    }

    private MutablePair<String, List<String>> getCsvShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(attr, Collections.emptyList());
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, StringUtils.isNotBlank(attr.getVal()) ? Arrays.asList(StringUtils.split(attr.getVal(), ',')) : Collections.emptyList());
    }


    /**
     * {@inheritDoc}
     */
    public VoShopSeo getShopLocale(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            return voAssemblySupport.assembleVo(VoShopSeo.class, ShopDTO.class, new VoShopSeo(), shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopSeo update(final VoShopSeo vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.update(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShopSeo.class, shopDTO, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getShopLocale(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    public VoShopUrl getShopUrls(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final List<ShopUrlDTO> shopUrlDTO = dtoShopUrlService.getAllByShopId(shopId);
            final VoShopUrl voShopUrl = new VoShopUrl();
            voShopUrl.setUrls(voAssemblySupport.assembleVos(VoShopUrlDetail.class, ShopUrlDTO.class, shopUrlDTO));
            voShopUrl.setShopId(shopId);

            String previewURLTemplate = systemService.getPreviewShopURLTemplate();
            if (previewURLTemplate == null) {
                previewURLTemplate = "http://{primaryShopURL}:8080/";
                ShopCodeContext.getLog(this).error("Preview shop URL template is not configured, using '{}'", previewURLTemplate);
            }

            String primary = null;
            if (voShopUrl.getUrls() == null || voShopUrl.getUrls().isEmpty()) {
                primary = "localhost";
            } else {
                for (final VoShopUrlDetail url : voShopUrl.getUrls()) {
                    if (primary == null || url.isPrimary()) {
                        primary = url.getUrl();
                    }
                }
            }

            voShopUrl.setPreviewUrl(previewURLTemplate.replace("{primaryShopURL}", primary));

            String previewURICss = systemService.getPreviewShopURICss();
            if (previewURICss == null) {
                previewURICss = "yes-shop/wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css";
                ShopCodeContext.getLog(this).error("Preview shop URI CSS is not configured, using '{}'", previewURICss);
            }
            voShopUrl.setPreviewCss(voShopUrl.getPreviewUrl() + previewURICss);


            return voShopUrl;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopUrl update(VoShopUrl vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            final List<ShopUrlDTO> originalShopUrlDTOs = dtoShopUrlService.getAllByShopId(vo.getShopId());
            final Set<Long> updated = new HashSet<>();
            for (VoShopUrlDetail urlDetail : vo.getUrls()) {
                ShopUrlDTO shopUrlDTO =
                        voAssemblySupport.assembleDto(ShopUrlDTO.class, VoShopUrlDetail.class, new ShopUrlDTOImpl(), urlDetail);
                shopUrlDTO.setShopId(vo.getShopId());
                if (urlDetail.getUrlId() == 0) {  //new one insert
                    dtoShopUrlService.create(shopUrlDTO);
                } else { //update
                    dtoShopUrlService.update(shopUrlDTO);
                    updated.add(shopUrlDTO.getStoreUrlId());
                }
            }
            for (ShopUrlDTO dto : originalShopUrlDTOs) {
                if (!updated.contains(dto.getId())) {
                    dtoShopUrlService.remove(dto.getId());
                }
            }
            return getShopUrls(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopSupportedCurrencies getShopCurrencies(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            VoShopSupportedCurrencies ssc = new VoShopSupportedCurrencies();
            ssc.setShopId(shopId);
            ssc.setAll(currencyService.getSupportedCurrencies());
            String curr = dtoShopService.getSupportedCurrencies(shopId);
            ssc.setSupported(
                    curr == null ? Collections.<String>emptyList() : Arrays.asList(curr.split(","))
            );
            return ssc;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopSupportedCurrencies update(VoShopSupportedCurrencies vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            dtoShopService.updateSupportedCurrencies(
                    vo.getShopId(),
                    StringUtils.join(vo.getSupported().toArray(), ",")
            );
            return getShopCurrencies(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLanguages getShopLanguages(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final VoShopLanguages voShopLanguages = new VoShopLanguages();
            String lng = dtoShopService.getSupportedLanguages(shopId);
            voShopLanguages.setSupported(lng == null ? Collections.<String>emptyList() : Arrays.asList(lng.split(",")));
            voShopLanguages.setAll(VoUtils.adaptMapToPairs(languageService.getLanguageName()));
            voShopLanguages.setShopId(shopId);
            return voShopLanguages;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLanguages update(VoShopLanguages vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            dtoShopService.updateSupportedLanguages(vo.getShopId(),
                    StringUtils.join(vo.getSupported().toArray(), ","));
            return getShopLanguages(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocations getShopLocations(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final VoShopLocations shopLocations = new VoShopLocations();

            String billing = dtoShopService.getSupportedBillingCountries(shopId);
            String shipping = dtoShopService.getSupportedShippingCountries(shopId);
            shopLocations.setSupportedBilling(billing == null ? Collections.<String>emptyList() : Arrays.asList(billing.split(",")));
            shopLocations.setSupportedShipping(shipping == null ? Collections.<String>emptyList() : Arrays.asList(shipping.split(",")));

            final List<Country> countries = countryService.findAll();
            final List<MutablePair<String, String>> all = new ArrayList<>();
            for (final Country country : countries) {
                all.add(MutablePair.of(
                        country.getCountryCode(),
                        country.getName() + (StringUtils.isNotBlank(country.getDisplayName()) ? " (" + country.getDisplayName() + ")" : "")));
            }

            shopLocations.setAll(all);
            shopLocations.setShopId(shopId);
            return shopLocations;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocations update(VoShopLocations vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            dtoShopService.updateSupportedBillingCountries(vo.getShopId(),
                    StringUtils.join(vo.getSupportedBilling().toArray(), ","));
            dtoShopService.updateSupportedShippingCountries(vo.getShopId(),
                    StringUtils.join(vo.getSupportedShipping().toArray(), ","));
            return getShopLocations(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShop updateDisabledFlag(final long shopId, final boolean disabled) throws Exception {
        final ShopDTO dto = dtoShopService.updateDisabledFlag(shopId, disabled);
        if (dto != null) {
            return getById(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }



    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueShop> getShopAttributes(final long shopId) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(shopId);

    }


    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueShop> update(final List<MutablePair<VoAttrValueShop, Boolean>> vo) throws Exception {

        final long shopId = this.voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo);

        return getShopAttributes(shopId);

    }

    /**
     * Spring IoC
     *
     * @param attributes attributes to skip
     */
    public void setSkipAttributesInView(List<String> attributes) {
        this.skipAttributesInView = new HashSet<>(attributes);
    }

}
