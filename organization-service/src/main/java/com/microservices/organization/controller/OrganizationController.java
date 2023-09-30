package com.microservices.organization.controller;

import com.microservices.organization.dto.OrganizationDto;
import com.microservices.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    @Autowired
    private OrganizationService organizationService;

    // Post REST API
    @PostMapping
    public ResponseEntity<OrganizationDto> saveOrganization(@RequestBody OrganizationDto organizationDto){
        OrganizationDto savedOrganization = organizationService.saveOrganization(organizationDto);
        return new ResponseEntity<OrganizationDto>(savedOrganization, HttpStatus.CREATED);
    }

    // Get REST API
    @GetMapping("/{organization-code}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("organization-code") String organizationCode){
        OrganizationDto organizationDto = organizationService.getOrganizationByCode(organizationCode);
        return new ResponseEntity<OrganizationDto>(organizationDto, HttpStatus.OK);
    }
}
