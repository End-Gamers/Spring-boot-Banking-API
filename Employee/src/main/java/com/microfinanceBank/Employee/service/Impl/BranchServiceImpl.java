package com.microfinanceBank.Employee.service.Impl;

import com.microfinanceBank.Employee.dto.AddressDto;
import com.microfinanceBank.Employee.dto.BranchDto;
import com.microfinanceBank.Employee.entity.Address;
import com.microfinanceBank.Employee.entity.Branch;
import com.microfinanceBank.Employee.repository.BranchRepository;
import com.microfinanceBank.Employee.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BranchService 구현체.
 * 지점 생성 및 전체 조회 로직을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    /** 새 은행 지점을 생성하고 주소를 함께 저장한다. */
    @Override
    public BranchDto createBranch(BranchDto branchDto) {
        log.trace("entering createBranch method");
        log.debug("Creating bank branch");
        var branch=convertBranchDtoToEntity(branchDto);
        branch.addAddress(convertAddressDtoToEntity(branchDto.getAddress()));
        var saved=branchRepository.save(branch);
        log.info("Succesfully created bank branch with address {}",saved.getAddress());
        return convertBranchEntityToDto(saved);
    }

    /** 모든 은행 지점 목록을 DTO로 변환하여 반환한다. */
    @Override
    @Transactional(readOnly = true)
    public List<BranchDto> getAll() {
        return branchRepository.findAll().stream().map(this::convertBranchEntityToDto)
                .collect(Collectors.toList());
    }


    /** BranchDto를 Branch 엔티티로 변환한다. */
    private Branch convertBranchDtoToEntity(BranchDto branchDto){
        var branch=new Branch();
        branch=modelMapper.map(branchDto,Branch.class);
        return branch;
    }

    /** AddressDto를 Address 엔티티로 변환한다. */
    private Address convertAddressDtoToEntity(AddressDto addressDto){
        var address=new Address();
        address=modelMapper.map(addressDto,Address.class);
        return address;
    }

    /** Branch 엔티티를 BranchDto로 변환한다. */
    private BranchDto convertBranchEntityToDto(Branch branch){
        var branchDto=new BranchDto();
        branchDto=modelMapper.map(branch,BranchDto.class);
        return branchDto;
    }
}
