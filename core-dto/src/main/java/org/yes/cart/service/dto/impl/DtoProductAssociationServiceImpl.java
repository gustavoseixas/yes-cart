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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductAssociationDTOImpl;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductAssociationServiceImpl
        extends AbstractDtoServiceImpl<ProductAssociationDTO, ProductAssociationDTOImpl, ProductAssociation>
        implements DtoProductAssociationService {

    private final ProductAssociationService productAssociationService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productAssociationGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoProductAssociationServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<ProductAssociation> productAssociationGenericService,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, productAssociationGenericService, adaptersRepository);
        productAssociationService = (ProductAssociationService) productAssociationGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO create(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (instance.getProductId() != instance.getAssociatedProductId()) {
            return super.create(instance);
        }
        throw new UnableToCreateInstanceException("Cannot associate product with itself", null);


    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO update(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (instance.getProductId() != instance.getAssociatedProductId()) {
            return super.update(instance);
        }
        throw new UnableToCreateInstanceException("Cannot associate product with itself", null);
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductAssociationDTO> getDtoIFace() {
        return ProductAssociationDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductAssociationDTOImpl> getDtoImpl() {
        return ProductAssociationDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductAssociation> getEntityIFace() {
        return ProductAssociation.class;
    }

    /**
     * Get all product associations.
     *
     * @param productId product primary key
     * @return list of product assotiations
     */
    public List<ProductAssociationDTO> getProductAssociations(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<ProductAssociation> list = productAssociationService.findProductAssociations(productId);
        List<ProductAssociationDTO> result = new ArrayList<ProductAssociationDTO>(list.size());
        fillDTOs(list, result);
        return result;
    }

    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param associationCode accosiation code [up, cross, etc]
     * @return list of product assotiations
     */
    public List<ProductAssociationDTO> getProductAssociationsByProductAssociationType(final long productId, final String associationCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<ProductAssociation> list = productAssociationService.findProductAssociations(productId, associationCode);
        List<ProductAssociationDTO> result = new ArrayList<ProductAssociationDTO>(list.size());
        fillDTOs(list, result);
        return result;

    }
}
