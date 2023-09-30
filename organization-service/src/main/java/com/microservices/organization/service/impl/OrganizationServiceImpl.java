package com.microservices.organization.service.impl;

import com.microservices.organization.dto.OrganizationDto;
import com.microservices.organization.entity.Organization;
import com.microservices.organization.exception.OrganizationNotFoundException;
import com.microservices.organization.repository.OrganizationRepository;
import com.microservices.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;


@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public OrganizationDto saveOrganization(OrganizationDto organizationDto) {
        // dto to entity
        Organization organization = mapToEntity(organizationDto);

        // save
        Organization savedOrganization = organizationRepository.save(organization);

        // entity to dto
        OrganizationDto savedDto = mapToDto(savedOrganization);

        // return dto
        return savedDto;
    }

    @Override
    public OrganizationDto getOrganizationByCode(String organizationCode) {

        // find department by code
        Organization organization = organizationRepository.findByOrganizationCode(organizationCode);

        if (organization == null) {
            throw new OrganizationNotFoundException("Organization","organizationCode",organizationCode);
        }

        // convert entity to dto
        OrganizationDto newOrganizationDto = mapToDto(organization);

        // return dto
        return newOrganizationDto;
    }

    // modelMapper entity to dto
    private OrganizationDto mapToDto(Organization organization){
        OrganizationDto organizationDto = modelMapper.map(organization, OrganizationDto.class);
        return organizationDto;
    }

    // modelMapper dto to Entity
    private Organization mapToEntity(OrganizationDto organizationDto){
        Organization organization = modelMapper.map(organizationDto, Organization.class);
        return organization;
    }
}
