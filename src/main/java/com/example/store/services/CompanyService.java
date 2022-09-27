package com.example.store.services;

import com.example.store.components.TreeBuilder;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.CompanyMapper;
import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Company;
import com.example.store.repositories.CompanyRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    protected CompanyRepository companyRepository;
    @Autowired
    private TreeBuilder<Company> treeBuilder;
    @Autowired
    private CompanyMapper companyMapper;

    @Value("${our.company.id}")
    private int ourCompanyId;

    public Company getById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_COMPANY_MESSAGE));
    }

    public Company getByName(String name) {
        return companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_COMPANY_MESSAGE, name)));
    }

    public Company getByInn(long inn) {
        return companyRepository.findByInn(inn)
                .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_COMPANY_MESSAGE, inn)));
    }

    // todo add tests
    public Optional<Company> findByCode(int code) {
        return companyRepository.findByCode(code);
    }

    public List<CompanyDTO> getCompanyDTOList() {
        return companyRepository.findAll().stream()
                .map(companyMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public Company getOurCompany() {
        return companyRepository.getById(ourCompanyId);
    }

    // todo add tests
    public List<ItemDTOForTree> getCompanyDTOTree() {
        List<Company> companies = companyRepository.findAll(Sort.by("id"));
        return treeBuilder.getItemTree(companies);
    }

    // todo add tests
    public CompanyDTO getCompanyById(int id) {
        Company company = companyRepository.getById(id);
        return companyMapper.mapToDTO(company);
    }

    // todo add tests
    public void setCompany(CompanyDTO dto) {
        Company company = companyMapper.mapToItem(dto);
        companyRepository.save(company);
        if(company.getParent() == null) {
            companyRepository.setParentIdNotNull(company.getId());
        }
    }

    // todo add tests
    public void updateCompany(CompanyDTO dto) {
        Company company = companyRepository.getById(dto.getId());
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setInn(dto.getInn());
        company.setKpp(dto.getKpp());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        companyRepository.save(company);
    }

    public Company getByCode(int code) {
        return companyRepository.findByCode(code)
            .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_COMPANY_MESSAGE, code)));
    }
}
