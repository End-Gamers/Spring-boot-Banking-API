package com.microfinanceBank.Employee.controller;

import com.microfinanceBank.Employee.dto.BranchDto;
import com.microfinanceBank.Employee.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 은행 지점(Branch) 관리 REST 컨트롤러.
 * 지점 생성 및 전체 지점 조회 API를 제공한다.
 */
@RestController
@RequestMapping(value = "api",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    /** 새 은행 지점을 생성한다. */
    @PostMapping("branch")
    public ResponseEntity<BranchDto> createBranch(@Valid @RequestBody BranchDto branchDto){
        var response=branchService.createBranch(branchDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** 모든 은행 지점 목록을 조회한다. */
    @GetMapping("all-branch")
    public ResponseEntity<List<BranchDto>> getAllBranch(){
        var response=branchService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
