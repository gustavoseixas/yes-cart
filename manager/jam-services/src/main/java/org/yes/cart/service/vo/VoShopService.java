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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public interface VoShopService {

    /**
     * Get all manageable shops.
     *
     * @return list of all manageble shops.
     * @throws Exception
     */
    List<VoShop> getAll() throws Exception;

    /**
     * Get shop by id.
     *
     * @param id
     * @return shop vo
     * @throws Exception
     */
    VoShop getById(long id) throws Exception;

    /**
     * Update given shop.
     *
     * @param vo shop to update
     * @return updated instance
     * @throws Exception
     */
    VoShop update(VoShop vo) throws Exception;

    /**
     * Create new shop
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoShop create(VoShop vo) throws Exception;

    /**
     * Get shop by id.
     *
     * @param id
     * @return shop vo
     * @throws Exception
     */
    void remove(long id) throws Exception;

    /**
     * Fet localization information for given shop.
     *
     * @param shopId given shop
     * @return localization information
     * @throws Exception
     */
    VoShopLocale getShopLocale(long shopId) throws Exception;

    /**
     * Update localization information.
     *
     * @param vo vo
     * @return localization information
     * @throws Exception
     */
    VoShopLocale update(final VoShopLocale vo) throws Exception;

    /**
     * Get urls for shop.
     *
     * @param shopId pk
     * @return urls
     * @throws Exception
     */
    VoShopUrl getShopUrls(long shopId) throws Exception;

    /**
     * Update shop urls, that are belong to some shop
     *
     * @param vo urls
     * @return updated version of shop urls
     * @throws Exception
     */
    VoShopUrl update(VoShopUrl vo) throws Exception;

    /**
     * GEt supprted currencies.
     *
     * @param shopId given shop id
     * @return supported currencies
     * @throws Exception
     */
    VoShopSupportedCurrencies getShopCurrencies(long shopId) throws Exception;


    /**
     * Update supported currencies.
     *
     * @param vo currencies
     * @return updated version of supported currencies.
     * @throws Exception
     */
    VoShopSupportedCurrencies update(VoShopSupportedCurrencies vo) throws Exception;

    /**
     * Get supported languages by given shop
     * @param shopId guven shop id
     * @return langs
     * @throws Exception
     */
    VoShopLanguages getShopLanguages(long shopId) throws Exception;

    /**
     * Update supported languages.
     * @param vo languages
     * @return updated version of supported langs.
     * @throws Exception
     */
    VoShopLanguages update(VoShopLanguages vo) throws Exception;

    /**
     * Update the shop disabled flag.
     *
     * @param shopId shop PK
     * @param disabled true if shop is disabled
     * @return shop dto if found otherwise null.
     * @throws Exception
     */
    VoShop updateDisabledFlag(long shopId, boolean disabled) throws Exception;

}