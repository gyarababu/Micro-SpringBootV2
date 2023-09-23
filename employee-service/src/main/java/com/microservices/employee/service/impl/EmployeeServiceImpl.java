package com.microservices.employee.service.impl;

import com.microservices.employee.dto.APIResponseDto;
import com.microservices.employee.dto.DepartmentDto;
import com.microservices.employee.dto.EmployeeDto;
import com.microservices.employee.entity.Employee;
import com.microservices.employee.exception.DepartmentNotFoundException;
import com.microservices.employee.exception.ResourceNotFoundException;
import com.microservices.employee.repository.EmployeeRepository;
import com.microservices.employee.service.EmployeeService;
import com.microservices.employee.service.FeignAPI;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

//    @Autowired
//    private FeignAPI feignAPI;

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {

        Employee employee = mapToEntity(employeeDto);

        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeDto savedDto = mapToDto(employee);

        return savedDto;
    }

//    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Retry(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Override
    public APIResponseDto getEmployeeById(long employeeId) {
        logger.info("started getEmployeeById info log level ");
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException("Employee","id",employeeId));

        String departmentCode = employee.getDepartmentCode();
        if (departmentCode == null || departmentCode.isEmpty()) {
            throw new DepartmentNotFoundException("Department","departmentCode",departmentCode);
        }

        //resttemplate
//        ResponseEntity<DepartmentDto> responseEntity = restTemplate
//                .getForEntity("http://localhost:8081/api/departments/"
//                        + employee.getDepartmentCode(), DepartmentDto.class);

        // getting department details
//        DepartmentDto departmentDto = responseEntity.getBody();

        // webclient
        DepartmentDto departmentDto = webClient.get()
                .uri("http://localhost:8081/api/departments/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .block();

        // synchronous communication using feignClient and getting department details
//        DepartmentDto departmentDto = feignAPI.getDepartment(employee.getDepartmentCode());

        EmployeeDto employeeDto = mapToDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);
        logger.info("ended getEmployeeById info log level ");

        // returning both details
        return apiResponseDto;
    }

    // fallback method
    public APIResponseDto getDefaultDepartment(long employeeId, Exception exception) {
        logger.info("started getDefaultDepartment info log level ");

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException("Employee","id",employeeId));

        String departmentCode = employee.getDepartmentCode();
        if (departmentCode == null || departmentCode.isEmpty()) {
            throw new DepartmentNotFoundException("Department","departmentCode",departmentCode);
        }

        // default department
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentCode("RD001");
        departmentDto.setDepartmentName("R&D Department");
        departmentDto.setDepartmentDescription("Research and Development Department");

        EmployeeDto employeeDto = mapToDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);
        logger.info("ended getDefaultDepartment info log level ");

        // returning both details
        return apiResponseDto;
    }

        // modelMapper entity to dto
    private EmployeeDto mapToDto(Employee employee){
        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
        return employeeDto;
    }

    // modelMapper dto to entity
    private Employee mapToEntity(EmployeeDto employeeDto){
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        return employee;
    }
}
