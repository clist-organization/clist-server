package com.clist.domain.md.controller;

import com.clist.domain.md.dto.MdDto;
import com.clist.domain.md.service.MdService;
import com.clist.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/md")
@RequiredArgsConstructor
public class MdController {

    private final MdService mdService;

    @PostMapping
    public ResponseEntity<ApiResponse<MdDto.Response>> create(@RequestBody MdDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(mdService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MdDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(mdService.getAll()));
    }

    @GetMapping("/{title}")
    public ResponseEntity<ApiResponse<MdDto.Response>> getByTitle(@PathVariable String title) {
        return ResponseEntity.ok(ApiResponse.success(mdService.getByTitle(title)));
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String title) {
        mdService.deleteByTitle(title);
        return ResponseEntity.ok(ApiResponse.success());
    }
}