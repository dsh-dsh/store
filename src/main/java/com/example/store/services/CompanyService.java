package com.example.store.services;

import com.example.store.components.SystemSettingsCash;
import com.example.store.components.TreeBuilder;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.CompanyMapper;
import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Company;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.CompanyRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected CompanyMapper companyMapper;
    @Autowired
    private SystemSettingsCash systemSettingsCash;

    public Company getById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_COMPANY_MESSAGE,
                        this.getClass().getName() + " - getById(int id)"));
    }

    // todo add tests
    public Company findById(int id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Company getByName(String name) {
        return companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_COMPANY_MESSAGE, name),
                        this.getClass().getName() + " - getByName(String name)"));
    }

    public Company getByInn(String inn) {
        return companyRepository.findByInn(inn)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_COMPANY_MESSAGE, inn),
                        this.getClass().getName() + " - getByInn(String inn)"));
    }

    public Optional<Company> findByCode(int code) {
        return companyRepository.findByCode(code);
    }

    public List<CompanyDTO> getCompanyDTOList() {
        return companyRepository.findAll().stream()
                .filter(company -> !company.isNode())
                .map(companyMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public Company getOurCompany() {
        return companyRepository.getById(systemSettingsCash.getProperty(SettingType.OUR_COMPANY_ID));
    }

    public List<ItemDTOForTree> getCompanyDTOTree() {
        List<Company> companies = companyRepository.findAll(Sort.by("id"));
        return treeBuilder.getItemTree(companies);
    }

    public CompanyDTO getCompanyById(int id) {
        Company company = companyRepository.getById(id);
        return companyMapper.mapToDTO(company);
    }

    public void setCompany(CompanyDTO dto) {
        Company company = companyMapper.mapToCompany(dto);
        companyRepository.save(company);
        if(company.getParent() == null) {
            companyRepository.setParentIdNotNull(company.getId());
        }
    }

    public void updateCompany(CompanyDTO dto) {
        Company company = getById(dto.getId());
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setInn(dto.getInn());
        company.setKpp(dto.getKpp());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        companyRepository.save(company);
        if(company.getParent() == null) {
            companyRepository.setParentIdNotNull(company.getId());
        }
    }

    public Company getByCode(int code) {
        if(code == 0) return null;
        return companyRepository.findByCode(code)
            .orElseThrow(() -> new BadRequestException(
                    String.format(Constants.NO_SUCH_COMPANY_MESSAGE, code),
                    this.getClass().getName() + " - getByCode(int code)"));
    }
}
